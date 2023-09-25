package cz.jansimerda.homebrewdash.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

@Validated
public class UserRequestDto {

    @Email
    @Size(max = 50)
    @NotBlank
    private String email;

    @Size(min = 5)
    @Pattern(regexp = "^\\S+$", message = "Password must not contain whitespace")
    @Pattern(regexp = "^.*[a-zA-Z].*$", message = "Password must contain at least one alphabet character")
    @Pattern(regexp = "^.*\\d.*$", message = "Password must contain at least one digit")
    @Pattern(regexp = "^.*\\W.*$", message = "Password must contain at least one special character")
    @NotBlank
    private String password;

    @Size(min = 3, max = 30)
    @Pattern(regexp = "^\\w[\\w\\d]*$")
    @NotBlank
    private String username;

    @Size(min = 1, max = 30)
    @Pattern(regexp = "^\\S+$", message = "First name must not contain whitespace")
    private String firstName;

    @Size(min = 1, max = 30)
    private String surname;

    public String getEmail() {
        return email.trim();
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username.trim();
    }

    public String getFirstName() {
        return firstName.trim();
    }

    public String getSurname() {
        return surname.trim();
    }
}
