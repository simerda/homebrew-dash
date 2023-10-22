package cz.jansimerda.homebrewdash.rest.dto.response;

public class UserSessionCreatedResponseDto extends UserSessionResponseDto {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
