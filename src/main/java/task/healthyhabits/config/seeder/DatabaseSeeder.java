package task.healthyhabits.config.seeder;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.CompletedActivity;
import task.healthyhabits.models.DaysOfWeek;
import task.healthyhabits.models.Frequency;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.ProgressLog;
import task.healthyhabits.models.Reminder;
import task.healthyhabits.models.Role;
import task.healthyhabits.models.Routine;
import task.healthyhabits.models.RoutineActivity;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.CompletedActivityRepository;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.repositories.ProgressLogRepository;
import task.healthyhabits.repositories.ReminderRepository;
import task.healthyhabits.repositories.RoleRepository;
import task.healthyhabits.repositories.RoutineActivityRepository;
import task.healthyhabits.repositories.RoutineRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.security.hash.PasswordHashService;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.seeder", name = "enabled", havingValue = "true")
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private static final int ROUTINES_PER_USER = 3;
    private static final int REMINDERS_PER_USER = 2;
    private static final int ACTIVITIES_PER_ROUTINE = 3;
    private static final int PROGRESS_LOGS_PER_ROUTINE = 5;
    private static final int COMPLETED_PER_LOG = 2;
    private static final int DEFAULT_USER_MINIMUM = 500;
    private static final int DEFAULT_HABIT_MINIMUM = 200;
    private static final String DEFAULT_PASSWORD = "SeederP4ss";
    private static final int MAX_HABIT_GENERATION_ATTEMPTS = 100;
    private static final int MIN_BATCH_SIZE = 100;
    private static final int MAX_BATCH_SIZE = 5000;
    private static final int MAX_TARGET_USERS = 10_000;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HabitRepository habitRepository;
    private final RoutineRepository routineRepository;
    private final RoutineActivityRepository routineActivityRepository;
    private final ReminderRepository reminderRepository;
    private final ProgressLogRepository progressLogRepository;
    private final CompletedActivityRepository completedActivityRepository;
    private final PasswordHashService passwordHashService;

    @Value("${app.seeder.total-records:500000}")
    private int targetTotalRecords;

    @Value("${app.seeder.batch-size:1000}")
    private int configuredBatchSize;

    @Value("${app.seeder.locale:es}")
    private String localeTag;

    @Override
    public void run(String... args) {

        int batchSize = Math.max(MIN_BATCH_SIZE, configuredBatchSize);
        if (batchSize > MAX_BATCH_SIZE) {
            log.warn(
                    "Configured batch size {} exceeds safe maximum {}. Using {} instead.",
                    batchSize, MAX_BATCH_SIZE, MAX_BATCH_SIZE);
            batchSize = MAX_BATCH_SIZE;
        }
        configuredBatchSize = batchSize;

        int requestedUsers = Math.max(DEFAULT_USER_MINIMUM, targetTotalRecords / 60);
        int targetUsers = Math.min(MAX_TARGET_USERS, requestedUsers);
        if (targetUsers < requestedUsers) {
            log.warn(
                    "Configured target users {} exceeds safe maximum {}. Using {} instead.",
                    requestedUsers, MAX_TARGET_USERS, MAX_TARGET_USERS);
        }

        int requestedHabits = Math.max(DEFAULT_HABIT_MINIMUM, requestedUsers / 5);
        int targetHabits = Math.min(MAX_TARGET_USERS, Math.max(DEFAULT_HABIT_MINIMUM, targetUsers / 5));
        if (targetHabits < requestedHabits) {
            log.warn(
                    "Configured target habits {} exceeds safe maximum {}. Using {} instead.",
                    requestedHabits, MAX_TARGET_USERS, targetHabits);
        }

        Locale locale = resolveLocale(localeTag);
        Faker faker = new Faker(locale);

        log.info(
                "Starting database seeding (incremental): usersTarget={}, habitsTarget={}, batchSize={}, locale={}",
                targetUsers, targetHabits, batchSize, locale);

        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            log.warn("No roles available. Seed aborted.");
            return;
        }

        List<Habit> habits = prepareHabits(faker, targetHabits, batchSize);
        if (habits.isEmpty()) {
            log.warn("No habits available after preparation. Seed aborted.");
            return;
        }

        seedUsersWithRelatedData(faker, roles, habits, targetUsers, batchSize);

        log.info("Database seeding completed successfully (incremental)");
    }

    private Locale resolveLocale(String tag) {
        Locale locale = Locale.forLanguageTag(Objects.requireNonNullElse(tag, "es"));
        if (locale == null || locale.toString().isEmpty()) {
            return new Locale("es");
        }
        return locale;
    }

    private List<Habit> prepareHabits(Faker faker, int targetHabits, int batchSize) {
        List<Habit> existingHabits = habitRepository.findAll();
        int currentHabitCount = existingHabits.size();
        int toCreate = Math.max(0, targetHabits - currentHabitCount);
        if (toCreate <= 0) {
            if (currentHabitCount > 0) {
                log.info("Target habits already satisfied ({}). Skipping habit generation.", currentHabitCount);
            }
            return existingHabits;
        }

        log.info("Generating {} new habits", toCreate);

        Set<String> usedHabitIdentifiers = new HashSet<>(Math.max(currentHabitCount, 16));
        for (Habit habit : existingHabits) {
            if (habit.getName() != null && habit.getCategory() != null) {
                usedHabitIdentifiers.add(buildHabitIdentifier(habit.getName(), habit.getCategory()));
            }
        }

        List<Habit> allHabits = new ArrayList<>(existingHabits);
        Category[] categories = Category.values();
        int created = 0;
        while (toCreate > 0) {
            int currentBatch = Math.min(batchSize, toCreate);
            List<Habit> habitBatch = new ArrayList<>(currentBatch);
            for (int i = 0; i < currentBatch; i++) {
                Habit habit = generateUniqueHabit(faker, categories, usedHabitIdentifiers);
                if (habit == null) {
                    log.warn(
                            "Unable to generate additional unique habits for this batch after {} attempts. Created {} habits instead of {}.",
                            MAX_HABIT_GENERATION_ATTEMPTS,
                            habitBatch.size(),
                            currentBatch);
                    break;
                }
                habitBatch.add(habit);
            }
            if (habitBatch.isEmpty()) {
                log.warn("Stopping habit generation early because no unique habits could be created.");
                break;
            }
            List<Habit> savedHabits = habitRepository.saveAll(habitBatch);
            allHabits.addAll(savedHabits);
            created += savedHabits.size();
            toCreate -= savedHabits.size();
        }
        if (created > 0) {
            log.info("{} habits created", created);
        }
        return allHabits;
    }

    private Habit generateUniqueHabit(Faker faker, Category[] categories, Set<String> usedHabitIdentifiers) {
        for (int attempt = 0; attempt < MAX_HABIT_GENERATION_ATTEMPTS; attempt++) {
            Habit habit = new Habit();
            habit.setName(truncate(faker.commerce().productName() + " " + faker.number().digits(4), 100));
            habit.setDescription(truncate(faker.lorem().sentence(12), 200));
            habit.setCategory(categories[faker.number().numberBetween(0, categories.length)]);

            String identifier = buildHabitIdentifier(habit.getName(), habit.getCategory());
            if (identifier == null) {
                continue;
            }
            if (usedHabitIdentifiers.add(identifier)) {
                return habit;
            }
        }
        return null;
    }

    private String buildHabitIdentifier(String name, Category category) {
        if (name == null || category == null) {
            return null;
        }
        return name + ":" + category.name();
    }

    private void seedUsersWithRelatedData(
            Faker faker,
            List<Role> roles,
            List<Habit> habits,
            int targetUsers,
            int batchSize) {
        long existingUsers = userRepository.count();
        int availableUsers = (int) Math.min(existingUsers, (long) Integer.MAX_VALUE);
        int toCreate = Math.max(0, targetUsers - availableUsers);
        if (toCreate <= 0) {
            log.info("Target users already satisfied ({}). Skipping user generation.", existingUsers);
            return;
        }

        String encodedPassword = passwordHashService.encode(DEFAULT_PASSWORD);
        int userBatchSize = Math.max(1, Math.min(batchSize, toCreate));
        long emailCounter = existingUsers + 1;

        List<User> userBatch = new ArrayList<>(userBatchSize);
        for (int i = 0; i < toCreate; i++) {
            User user = new User();
            user.setName(truncate(faker.name().fullName(), 50));
            user.setEmail("seed.user" + emailCounter + "@healthyhabits.local");
            emailCounter++;
            user.setPassword(encodedPassword);
            user.setRoles(
                    new ArrayList<>(pickRandomElements(roles, determineCount(faker, 1, Math.min(roles.size(), 4)))));
            user.setFavoriteHabits(
                    new ArrayList<>(pickRandomElements(habits, determineCount(faker, 2, Math.min(habits.size(), 6)))));
            userBatch.add(user);

            if (userBatch.size() >= userBatchSize) {
                List<User> savedUsers = userRepository.saveAll(userBatch);
                createRelatedDataForUsers(faker, savedUsers, habits, batchSize);
                userBatch.clear();
            }
        }
        if (!userBatch.isEmpty()) {
            List<User> savedUsers = userRepository.saveAll(userBatch);
            createRelatedDataForUsers(faker, savedUsers, habits, batchSize);
        }
    }

    private void createRelatedDataForUsers(Faker faker, List<User> users, List<Habit> habits, int batchSize) {
        if (users.isEmpty()) {
            return;
        }
        generateReminders(faker, users, habits, batchSize);
        List<Routine> routines = generateRoutines(faker, users, habits, batchSize);
        if (!routines.isEmpty()) {
            Map<Long, List<RoutineActivity>> activitiesByRoutine = generateRoutineActivities(faker, routines, habits,
                    batchSize);
            generateProgressLogs(faker, routines, activitiesByRoutine, batchSize);
        }
    }

    private void generateReminders(Faker faker, List<User> users, List<Habit> habits, int batchSize) {
        List<Reminder> reminders = new ArrayList<>(users.size() * REMINDERS_PER_USER);
        Frequency[] frequencies = Frequency.values();
        for (User user : users) {
            for (int i = 0; i < REMINDERS_PER_USER; i++) {
                Reminder reminder = new Reminder();
                reminder.setUser(user);
                reminder.setHabit(selectRandomHabit(habits));
                reminder.setTime(
                        LocalTime.of(faker.number().numberBetween(5, 22), faker.number().numberBetween(0, 60)));
                reminder.setFrequency(frequencies[faker.number().numberBetween(0, frequencies.length)]);
                reminders.add(reminder);
            }
        }
        if (!reminders.isEmpty()) {
            List<List<Reminder>> partitions = partition(
                    reminders,
                    Math.max(1, Math.min(batchSize, batchSize * REMINDERS_PER_USER)));
            for (List<Reminder> partition : partitions) {
                reminderRepository.saveAll(partition);
            }
        }
    }

    private List<Routine> generateRoutines(Faker faker, List<User> users, List<Habit> habits, int batchSize) {
        List<Routine> routines = new ArrayList<>(users.size() * ROUTINES_PER_USER);
        for (User user : users) {
            for (int i = 0; i < ROUTINES_PER_USER; i++) {
                Routine routine = new Routine();
                routine.setUser(user);
                routine.setTitle(truncate(faker.company().buzzword() + " " + faker.color().name(), 100));
                routine.setDescription(truncate(faker.lorem().sentence(20), 200));
                routine.setTags(generateTags(faker));
                routine.setDaysOfWeek(generateDaysOfWeek(faker));
                routines.add(routine);
            }
        }
        if (!routines.isEmpty()) {
            List<List<Routine>> partitions = partition(
                    routines,
                    Math.max(1, Math.min(batchSize, batchSize * ROUTINES_PER_USER)));
            List<Routine> persisted = new ArrayList<>(routines.size());
            for (List<Routine> partition : partitions) {
                persisted.addAll(routineRepository.saveAll(partition));
            }
            return persisted;
        }
        return routines;
    }

    private Map<Long, List<RoutineActivity>> generateRoutineActivities(
            Faker faker,
            List<Routine> routines,
            List<Habit> habits,
            int batchSize) {
        Map<Long, List<RoutineActivity>> activitiesByRoutine = new HashMap<>();
        List<RoutineActivity> activitiesToPersist = new ArrayList<>(routines.size() * ACTIVITIES_PER_ROUTINE);
        for (Routine routine : routines) {
            List<RoutineActivity> activities = new ArrayList<>(ACTIVITIES_PER_ROUTINE);
            for (int i = 0; i < ACTIVITIES_PER_ROUTINE; i++) {
                RoutineActivity activity = new RoutineActivity();
                activity.setRoutine(routine);
                activity.setHabit(selectRandomHabit(habits));
                activity.setDuration(faker.number().numberBetween(10, 90));
                activity.setTargetTime(faker.number().numberBetween(1, 5));
                activity.setNotes(truncate(faker.lorem().sentence(10), 255));
                activities.add(activity);
                activitiesToPersist.add(activity);
            }
            activitiesByRoutine.put(routine.getId(), activities);
        }
        if (!activitiesToPersist.isEmpty()) {
            List<List<RoutineActivity>> partitions = partition(
                    activitiesToPersist,
                    Math.max(1, Math.min(batchSize, batchSize * ACTIVITIES_PER_ROUTINE)));
            for (List<RoutineActivity> partition : partitions) {
                routineActivityRepository.saveAll(partition);
            }
        }
        return activitiesByRoutine;
    }

    private void generateProgressLogs(
            Faker faker,
            List<Routine> routines,
            Map<Long, List<RoutineActivity>> activitiesByRoutine,
            int batchSize) {
        List<ProgressLog> logBatch = new ArrayList<>();
        List<ProgressLogContext> contexts = new ArrayList<>();
        int logBatchSize = Math.max(1, Math.min(batchSize, batchSize * PROGRESS_LOGS_PER_ROUTINE));
        for (Routine routine : routines) {
            List<RoutineActivity> activities = activitiesByRoutine.getOrDefault(routine.getId(), List.of());
            for (int i = 0; i < PROGRESS_LOGS_PER_ROUTINE; i++) {
                ProgressLog log = new ProgressLog();
                log.setUser(routine.getUser());
                log.setRoutine(routine);
                log.setDate(LocalDate.now().minusDays(faker.number().numberBetween(0, 90)));
                logBatch.add(log);
                contexts.add(new ProgressLogContext(log, activities));
                if (logBatch.size() >= logBatchSize) {
                    persistProgressLogs(faker, logBatch, contexts, batchSize);
                }
            }
        }
        if (!logBatch.isEmpty()) {
            persistProgressLogs(faker, logBatch, contexts, batchSize);
        }
    }

    private void persistProgressLogs(
            Faker faker,
            List<ProgressLog> logBatch,
            List<ProgressLogContext> contexts,
            int batchSize) {
        progressLogRepository.saveAll(logBatch);
        List<CompletedActivity> completedActivities = new ArrayList<>(logBatch.size() * COMPLETED_PER_LOG);
        for (ProgressLogContext context : contexts) {
            if (context.activities().isEmpty()) {
                continue;
            }
            for (int i = 0; i < COMPLETED_PER_LOG; i++) {
                RoutineActivity activity = context.activities()
                        .get(ThreadLocalRandom.current().nextInt(context.activities().size()));
                CompletedActivity completed = new CompletedActivity();
                completed.setProgressLog(context.log());
                completed.setHabit(activity.getHabit());
                completed
                        .setCompletedAt(toOffsetDateTime(context.log().getDate(), faker.number().numberBetween(6, 22)));
                completed.setNotes(truncate(faker.lorem().sentence(8), 200));
                completedActivities.add(completed);
            }
        }
        if (!completedActivities.isEmpty()) {
            List<List<CompletedActivity>> partitions = partition(
                    completedActivities,
                    Math.max(1, Math.min(batchSize, completedActivities.size())));
            for (List<CompletedActivity> partition : partitions) {
                completedActivityRepository.saveAll(partition);
            }
        }
        logBatch.clear();
        contexts.clear();
    }

    private Habit selectRandomHabit(List<Habit> habits) {
        return habits.get(ThreadLocalRandom.current().nextInt(habits.size()));
    }

    private OffsetDateTime toOffsetDateTime(LocalDate date, int hour) {
        return date.atTime(hour, ThreadLocalRandom.current().nextInt(0, 60)).atOffset(ZoneOffset.UTC);
    }

    private List<String> generateTags(Faker faker) {
        int count = determineCount(faker, 2, 4);
        List<String> tags = new ArrayList<>(count);
        for (String word : faker.lorem().words(count)) {
            tags.add(truncate(word.replaceAll("[^\\p{L}\\p{N} ]", ""), 50));
        }
        return tags;
    }

    private List<DaysOfWeek> generateDaysOfWeek(Faker faker) {
        DaysOfWeek[] values = DaysOfWeek.values();
        int count = determineCount(faker, 3, values.length);
        EnumSet<DaysOfWeek> set = EnumSet.noneOf(DaysOfWeek.class);
        while (set.size() < count) {
            set.add(values[ThreadLocalRandom.current().nextInt(values.length)]);
        }
        return new ArrayList<>(set);
    }

    private <T> List<T> pickRandomElements(List<T> source, int count) {
        int limit = Math.min(count, source.size());
        if (limit <= 0) {
            return List.of();
        }
        Set<T> selection = new LinkedHashSet<>(limit);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        while (selection.size() < limit) {
            selection.add(source.get(random.nextInt(source.size())));
        }
        return new ArrayList<>(selection);
    }

    private int determineCount(Faker faker, int minInclusive, int maxInclusive) {
        if (maxInclusive < minInclusive) {
            return minInclusive;
        }
        return faker.number().numberBetween(minInclusive, maxInclusive + 1);
    }

    private <T> List<List<T>> partition(List<T> items, int size) {
        List<List<T>> partitions = new ArrayList<>();
        if (size <= 0) {
            size = items.size();
        }
        for (int i = 0; i < items.size(); i += size) {
            partitions.add(new ArrayList<>(items.subList(i, Math.min(i + size, items.size()))));
        }
        return partitions;
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength);
    }

    private record ProgressLogContext(ProgressLog log, List<RoutineActivity> activities) {
    }
}