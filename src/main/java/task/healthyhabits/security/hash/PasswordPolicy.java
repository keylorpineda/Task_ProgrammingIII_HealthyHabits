package task.healthyhabits.security.hash;
public interface PasswordPolicy {
    void validate(String rawPassword);
}
