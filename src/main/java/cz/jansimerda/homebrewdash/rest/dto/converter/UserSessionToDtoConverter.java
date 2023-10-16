package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.UserSession;
import cz.jansimerda.homebrewdash.rest.dto.response.UserResponseDto;
import cz.jansimerda.homebrewdash.rest.dto.response.UserSessionResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserSessionToDtoConverter implements Function<UserSession, UserSessionResponseDto> {

    private final Function<User, UserResponseDto> userToDtoConverter;

    public UserSessionToDtoConverter(Function<User, UserResponseDto> userToDtoConverter) {
        this.userToDtoConverter = userToDtoConverter;
    }

    @Override
    public UserSessionResponseDto apply(UserSession userSession) {
        UserSessionResponseDto dto = new UserSessionResponseDto();

        dto.setId(userSession.getId());
        dto.setToken(userSession.getToken());
        dto.setUser(userToDtoConverter.apply(userSession.getUser()));
        dto.setExpiresAt(userSession.getExpiresAt());
        dto.setCreatedAt(userSession.getCreatedAt());

        return dto;
    }
}
