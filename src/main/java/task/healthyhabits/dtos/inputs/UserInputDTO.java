package task.healthyhabits.dtos.inputs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UserInputDTO {

    @NotBlank(message = "Name is required.")
    @Size(max = 50, message = "Name cannot exceed 50 characters.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    @Size(max = 50, message = "Email cannot exceed 50 characters.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // solo entra, no sale en respuestas
    private String password;

    @NotNull(message = "Roles cannot be null.")
    private List<RoleInputDTO> roles;

    @NotNull(message = "Favorite habits cannot be null.")
    private List<HabitInputDTO> favoriteHabits;

    // --- Constructores ---
    public UserInputDTO() { }

    public UserInputDTO(String name, String email, String password,
                        List<RoleInputDTO> roles, List<HabitInputDTO> favoriteHabits) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.favoriteHabits = favoriteHabits;
    }

    // --- Getters (solicitados) ---
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<RoleInputDTO> getRoles() {
        return roles;
    }

    public List<HabitInputDTO> getFavoriteHabits() {
        return favoriteHabits;
    }

    // --- Setters (necesarios para binding de GraphQL/JSON) ---
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(List<RoleInputDTO> roles) {
        this.roles = roles;
    }

    public void setFavoriteHabits(List<HabitInputDTO> favoriteHabits) {
        this.favoriteHabits = favoriteHabits;
    }

    // --- toString sin exponer password ---
    @Override
    public String toString() {
        return "UserInputDTO{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='***hidden***'" +
                ", roles=" + roles +
                ", favoriteHabits=" + favoriteHabits +
                '}';
    }
}
