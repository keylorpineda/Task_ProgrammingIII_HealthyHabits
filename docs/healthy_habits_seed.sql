-- HealthyHabits heavy seed (≈500k+ filas según parámetros)
-- MariaDB 10.11+ | Deterministic + reproducible
-- Autor: ChatGPT (keylor project)
-- ------------------------------------------------------------
-- Tuning & seguridad
SET NAMES utf8mb4;
SET @rand_seed := 20250922;
SELECT RAND(@rand_seed);
SET @fixed_ts := 1735689600;          -- 2024-12-31 00:00:00 UTC
SET timestamp = @fixed_ts;

-- ===========================
-- Parámetros de volumen
-- ===========================
SET @N_USERS := 5000;
SET @ROUTINES_PER_USER := 2;
SET @LOGS_PER_ROUTINE := 5;
SET @COMPLETED_PER_LOG := 10;
SET @TAGS_PER_ROUTINE := 2;
SET @DAYS_PER_ROUTINE := 3;
SET @ACTIVITIES_PER_ROUTINE := 3;

-- ===========================
-- Limpieza inicial
-- ===========================
START TRANSACTION;
SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;

TRUNCATE TABLE completed_activities;
TRUNCATE TABLE progress_logs;
TRUNCATE TABLE reminders;
TRUNCATE TABLE routine_activities;
TRUNCATE TABLE routine_days_of_week;
TRUNCATE TABLE routine_tags;
TRUNCATE TABLE routines;
TRUNCATE TABLE guide_habit;
TRUNCATE TABLE guides;
TRUNCATE TABLE user_favorite_habits;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE habits;
TRUNCATE TABLE roles;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

-- -------------------------------------------------------------------------
-- 1) Roles
-- -------------------------------------------------------------------------
INSERT INTO roles (name, permission)
SELECT CONCAT('ROLE_', permission), permission
FROM (
    SELECT 'HABIT_READ'    AS permission UNION ALL
    SELECT 'HABIT_EDITOR'  UNION ALL
    SELECT 'ROUTINE_READ'  UNION ALL
    SELECT 'ROUTINE_EDITOR' UNION ALL
    SELECT 'REMINDER_READ' UNION ALL
    SELECT 'REMINDER_EDITOR' UNION ALL
    SELECT 'PROGRESS_READ' UNION ALL
    SELECT 'PROGRESS_EDITOR' UNION ALL
    SELECT 'GUIDE_READ'    UNION ALL
    SELECT 'GUIDE_EDITOR'  UNION ALL
    SELECT 'USER_READ'     UNION ALL
    SELECT 'USER_EDITOR'   UNION ALL
    SELECT 'USER_WRITE'    UNION ALL
    SELECT 'AUDITOR'
) AS permission_catalog
ORDER BY permission;

-- -------------------------------------------------------------------------
-- 2) Users (@N_USERS) con coaches (primeros 80)
-- -------------------------------------------------------------------------
INSERT INTO users (name, email, password, coach_id)
WITH
first_names(name) AS (
    SELECT 'Luna' UNION ALL SELECT 'Mateo' UNION ALL SELECT 'Sofía' UNION ALL
    SELECT 'Elías' UNION ALL SELECT 'Abril' UNION ALL SELECT 'Iker' UNION ALL
    SELECT 'Noa' UNION ALL SELECT 'Gael' UNION ALL SELECT 'Ivanna' UNION ALL
    SELECT 'Thiago' UNION ALL SELECT 'Aura' UNION ALL SELECT 'Bruno' UNION ALL
    SELECT 'Camila' UNION ALL SELECT 'Nicolás' UNION ALL SELECT 'Valentina' UNION ALL
    SELECT 'Samuel' UNION ALL SELECT 'Isabella' UNION ALL SELECT 'Benjamín' UNION ALL
    SELECT 'Emilia' UNION ALL SELECT 'Dylan'
),
last_names(name) AS (
    SELECT 'Rivera' UNION ALL SELECT 'Martínez' UNION ALL SELECT 'Lozano' UNION ALL
    SELECT 'Escamilla' UNION ALL SELECT 'Gaitán' UNION ALL SELECT 'Vera' UNION ALL
    SELECT 'Quintero' UNION ALL SELECT 'Herrera' UNION ALL SELECT 'Bustamante' UNION ALL
    SELECT 'Paredes' UNION ALL SELECT 'Ayala' UNION ALL SELECT 'Rosales' UNION ALL
    SELECT 'Soto' UNION ALL SELECT 'Molina' UNION ALL SELECT 'Vargas' UNION ALL
    SELECT 'Cordero' UNION ALL SELECT 'Araya' UNION ALL SELECT 'Monge' UNION ALL
    SELECT 'Rojas' UNION ALL SELECT 'Zúñiga'
),
fn AS (SELECT name, ROW_NUMBER() OVER () rn, COUNT(*) OVER () total FROM first_names),
ln AS (SELECT name, ROW_NUMBER() OVER () rn, COUNT(*) OVER () total FROM last_names)
SELECT
    CONCAT(fn.name, ' ', ln.name) AS full_name,
    LOWER(CONCAT(REPLACE(fn.name,' ',''), '.', REPLACE(ln.name,' ',''), LPAD(seq.seq, 4, '0'), '@healthy.test')) AS email,
    '$2a$12$kzhFS4BHdP3ACWv3lR2OHOVOsmVk2011LLefmm76eQ1ebJM0UNXfi' AS password,
    CASE WHEN seq.seq <= 80 THEN NULL ELSE 1 + MOD(seq.seq-1, 80) END AS coach_id
FROM seq_1_to_5000 seq
JOIN fn ON ((seq.seq % fn.total) + 1) = fn.rn
JOIN ln ON ((seq.seq % ln.total) + 1) = ln.rn
ORDER BY seq.seq;

-- Roles por usuario
INSERT INTO user_roles (user_id, role_id)
WITH user_roles_src AS (
    SELECT u.user_id,
           ELT(1 + MOD(u.user_id, 5), 'HABIT_READ','ROUTINE_READ','GUIDE_READ','USER_READ','PROGRESS_READ') AS p1,
           ELT(1 + MOD(u.user_id+1, 5), 'HABIT_EDITOR','ROUTINE_EDITOR','GUIDE_EDITOR','USER_EDITOR','REMINDER_EDITOR') AS p2,
           ELT(1 + MOD(u.user_id+2, 4), 'USER_WRITE','AUDITOR','PROGRESS_EDITOR','REMINDER_READ') AS p3
    FROM users u
)
SELECT DISTINCT urs.user_id, r.role_id
FROM user_roles_src urs
JOIN roles r ON r.permission IN (urs.p1, urs.p2, urs.p3)
ORDER BY urs.user_id, r.role_id;

-- -------------------------------------------------------------------------
-- 3) Habits (~120) + favoritos
-- -------------------------------------------------------------------------
INSERT INTO habits (name, category, description)
WITH actions(a) AS (
    SELECT 'Respirar' UNION ALL SELECT 'Estirar' UNION ALL SELECT 'Caminar' UNION ALL
    SELECT 'Meditar' UNION ALL SELECT 'Registrar' UNION ALL SELECT 'Preparar' UNION ALL
    SELECT 'Planificar' UNION ALL SELECT 'Hidratar' UNION ALL SELECT 'Visualizar' UNION ALL
    SELECT 'Fortalecer'
),
adjs(a) AS (
    SELECT 'Vital' UNION ALL SELECT 'Rítmico' UNION ALL SELECT 'Consciente' UNION ALL
    SELECT 'Enfocado' UNION ALL SELECT 'Ligero' UNION ALL SELECT 'Energético' UNION ALL
    SELECT 'Calmado' UNION ALL SELECT 'Renovador' UNION ALL SELECT 'Dinámico' UNION ALL
    SELECT 'Sereno'
),
cats(c) AS (SELECT 'PHYSICAL' UNION ALL SELECT 'MENTAL' UNION ALL SELECT 'SLEEP' UNION ALL SELECT 'DIET'),
A AS (SELECT a, ROW_NUMBER() OVER () rn, COUNT(*) OVER () total FROM actions),
B AS (SELECT a, ROW_NUMBER() OVER () rn, COUNT(*) OVER () total FROM adjs),
C AS (SELECT c, ROW_NUMBER() OVER () rn, COUNT(*) OVER () total FROM cats)
SELECT
  CONCAT(b.a, ' ', a.a, ' #', LPAD(seq.seq, 3, '0')),
  c.c,
  CONCAT('Rutina ', LOWER(b.a), ' que potencia ',
         ELT(1 + MOD(seq.seq,8),'energía','claridad','descanso','nutrición','fuerza','postura','concentración','calma'), '.')
FROM seq_1_to_120 seq
JOIN A a ON ((seq.seq % a.total) + 1) = a.rn
JOIN B b ON ((seq.seq % b.total) + 1) = b.rn
JOIN C c ON ((seq.seq % c.total) + 1) = c.rn
ORDER BY seq.seq;

-- Favoritos
INSERT INTO user_favorite_habits (user_id, habit_id)
WITH h_rank AS (
  SELECT habit_id, ROW_NUMBER() OVER (ORDER BY habit_id) rn, COUNT(*) OVER() total FROM habits
),
slots AS (
  SELECT u.user_id, s.slot
  FROM users u
  JOIN (SELECT 1 AS slot UNION ALL SELECT 2 UNION ALL SELECT 3) s
)
SELECT sl.user_id, hr.habit_id
FROM slots sl
JOIN h_rank hr ON hr.rn = (((sl.user_id-1)*3 + sl.slot - 1) % hr.total) + 1
ORDER BY sl.user_id, hr.habit_id;

-- -------------------------------------------------------------------------
-- 4) Guides (40) + guide_habit
-- -------------------------------------------------------------------------
INSERT INTO guides (title, content, category, objective)
WITH titles(t) AS (
  SELECT 'Impulso diario' UNION ALL SELECT 'Reseteo nocturno' UNION ALL SELECT 'Respira y avanza' UNION ALL
  SELECT 'Energía sostenible' UNION ALL SELECT 'Enfoque consciente' UNION ALL SELECT 'Movilidad inteligente'
),
cats2(c) AS (SELECT 'PHYSICAL' UNION ALL SELECT 'MENTAL' UNION ALL SELECT 'SLEEP' UNION ALL SELECT 'DIET'),
T AS (SELECT t, ROW_NUMBER() OVER () rn, COUNT(*) OVER () total FROM titles),
C2 AS (SELECT c, ROW_NUMBER() OVER () rn, COUNT(*) OVER () total FROM cats2)
SELECT
  CONCAT(t.t, ' ', ELT(1 + MOD(seq.seq,4),'360','Plus','Essentials','Flow')),
  CONCAT('Guía de ',
         LOWER(ELT(1 + MOD(seq.seq,6),'micro-hábitos','rituales','bloques de enfoque','activaciones','respiros guiados','refuerzos diarios')),
         ' para objetivos sostenibles.'),
  c2.c,
  CONCAT('Lograr ',
         ELT(1 + MOD(seq.seq,6),'energía estable','claridad constante','descanso reparador','alimentación equilibrada','fuerza funcional','calma activa'))
FROM seq_1_to_40 seq
JOIN T t ON ((seq.seq % t.total) + 1) = t.rn
JOIN C2 c2 ON ((seq.seq % c2.total) + 1) = c2.rn
ORDER BY seq.seq;

INSERT INTO guide_habit (guide_id, habit_id)
WITH g_rank AS (SELECT guide_id, ROW_NUMBER() OVER (ORDER BY guide_id) rn FROM guides),
     h_rank2 AS (SELECT habit_id, ROW_NUMBER() OVER (ORDER BY habit_id) rn, COUNT(*) OVER() total FROM habits),
     slots2 AS (
       SELECT g.guide_id, s.slot
       FROM g_rank g
       JOIN (SELECT 1 AS slot UNION ALL SELECT 2) s
     )
SELECT sl.guide_id, h.habit_id
FROM slots2 sl
JOIN h_rank2 h ON h.rn = (((sl.guide_id-1)*2 + sl.slot - 1) % h.total) + 1;

-- -------------------------------------------------------------------------
-- 5) Routines + tags + days + activities
-- -------------------------------------------------------------------------
INSERT INTO routines (user_id, title, description)
WITH slots_r AS (
  SELECT u.user_id, seq.seq AS slot
  FROM users u
  JOIN seq_1_to_2 seq
)
SELECT user_id,
       CONCAT(ELT(1 + MOD(user_id+slot,5),'Impulso','Balance','Foco','Recarga','Evolución'),
              ' ', ELT(1 + MOD(user_id+slot,4),'Activa','Serena','Dinámica','Integral'),
              ' ', LPAD(slot,2,'0')),
       CONCAT('Secuencia adaptable con énfasis en ',
              ELT(1 + MOD(user_id+slot,6),'energía','claridad','descanso','nutrición','fuerza','calma'), '.')
FROM slots_r
ORDER BY user_id, slot;

-- Tags
INSERT INTO routine_tags (routine_id, tag)
WITH tag_slots AS (
  SELECT r.routine_id, r.user_id, seq.seq AS slot
  FROM routines r
  JOIN seq_1_to_2 seq
)
SELECT routine_id,
       CASE MOD(user_id + slot + routine_id, 12)
         WHEN 0 THEN 'mañanas'
         WHEN 1 THEN 'energía'
         WHEN 2 THEN 'foco'
         WHEN 3 THEN 'recuperación'
         WHEN 4 THEN 'consistencia'
         WHEN 5 THEN 'ligero'
         WHEN 6 THEN 'respira'
         WHEN 7 THEN 'hidrata'
         WHEN 8 THEN 'planifica'
         WHEN 9 THEN 'descansa'
         WHEN 10 THEN 'registra'
         ELSE 'medita'
       END
FROM tag_slots
ORDER BY routine_id;

-- Days
INSERT INTO routine_days_of_week (routine_id, day_of_week)
WITH day_slots AS (
  SELECT r.routine_id, r.user_id, seq.seq AS slot
  FROM routines r
  JOIN seq_1_to_3 seq
)
SELECT routine_id,
       ELT(1 + MOD(user_id + slot + routine_id,7),
          'MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')
FROM day_slots;

-- Activities
INSERT INTO routine_activities (routine_id, habit_id, duration, target_time, notes)
WITH a_slots AS (
  SELECT r.routine_id, r.user_id, seq.seq AS slot
  FROM routines r
  JOIN seq_1_to_3 seq
),
h_rank3 AS (
  SELECT habit_id, ROW_NUMBER() OVER (ORDER BY habit_id) rn, COUNT(*) OVER() total FROM habits
)
SELECT a.routine_id,
       h.habit_id,
       15 + (a.slot * 5) + MOD(a.routine_id,4)*2,
       (6*60 + 10 + (a.slot * 7) + MOD(a.routine_id, 50)), -- minutos como entero
       CONCAT(ELT(1 + MOD(a.routine_id+a.slot,4),'Prepara respiraciones','Calienta músculos','Evalúa energía','Registra sensaciones'),
              ' | ref-', LPAD(CONV(UUID_SHORT(),10,32), 6, '0'))
FROM a_slots a
JOIN h_rank3 h ON h.rn = (((a.routine_id-1)*3 + a.slot - 1) % h.total) + 1
ORDER BY a.routine_id, a.slot;

-- -------------------------------------------------------------------------
-- 6) Progress logs
-- -------------------------------------------------------------------------
INSERT INTO progress_logs (user_id, routine_id, `date`)
WITH l_slots AS (
  SELECT r.routine_id, r.user_id, seq.seq AS slot
  FROM routines r
  JOIN seq_1_to_5 seq
)
SELECT user_id, routine_id,
       DATE_ADD('2024-11-01', INTERVAL (routine_id*2 + slot*3) DAY) AS log_date
FROM l_slots
ORDER BY routine_id, log_date;

-- -------------------------------------------------------------------------
-- 7) Completed activities
-- -------------------------------------------------------------------------
INSERT INTO completed_activities (habit_id, progress_log_id, completed_at, notes)
WITH slots_c AS (
  SELECT pl.progress_log_id, pl.routine_id, pl.user_id, pl.`date`, seq.seq AS slot
  FROM progress_logs pl
  JOIN seq_1_to_10 seq
),
h_rank4 AS (
  SELECT habit_id, ROW_NUMBER() OVER (ORDER BY habit_id) rn, COUNT(*) OVER() total FROM habits
)
SELECT
  h.habit_id,
  sc.progress_log_id,
  TIMESTAMPADD(MINUTE,
      30*sc.slot + MOD(sc.progress_log_id, 11)*3,
      CONCAT(sc.`date`, ' 06:10:00')),
  CONCAT(ELT(1 + MOD(sc.progress_log_id+sc.slot,4),'Sesión cumplida','Ajuste ligero','Buen ritmo','Aplicar respiraciones'),
         ' | seg-', ELT(1 + MOD(sc.slot,4),'prioritario','constante','refuerzo','ligero'),
         ' | serie-', LPAD(CONV(UUID_SHORT(),10,32), 6, '0'))
FROM slots_c sc
JOIN h_rank4 h ON h.rn = (((sc.progress_log_id-1)*10 + sc.slot - 1) % h.total) + 1
ORDER BY sc.progress_log_id, sc.slot;

-- ===========================
-- Cierre
-- ===========================
SET UNIQUE_CHECKS = 1;
COMMIT;
