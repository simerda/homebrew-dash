package cz.jansimerda.homebrewdash.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

@Validated
public class UserSessionRequestDto {

    @Email
    @Size(max = 50)
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    public String getEmail() {
        return StringUtils.trim(email);
    }

    public String getPassword() {
        return password;
    }
}
