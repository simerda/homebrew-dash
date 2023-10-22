package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.model.UserSession;
import cz.jansimerda.homebrewdash.rest.dto.response.UserResponseDto;
import cz.jansimerda.homebrewdash.rest.dto.response.UserSessionCreatedResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserSessionToCreatedDtoConverter implements Function<UserSession, UserSessionCreatedResponseDto> {

    private final Function<User, UserResponseDto> userToDtoConverter;

    public UserSessionToCreatedDtoConverter(Function<User, UserResponseDto> userToDtoConverter) {
        this.userToDtoConverter = userToDtoConverter;
    }

    @Override
    public UserSessionCreatedResponseDto apply(UserSession userSession) {
        UserSessionCreatedResponseDto dto = new UserSessionCreatedResponseDto();

        dto.setId(userSession.getId());
        dto.setToken(userSession.getToken());
        dto.setUser(userToDtoConverter.apply(userSession.getUser()));
        dto.setExpiresAt(userSession.getExpiresAt());
        dto.setCreatedAt(userSession.getCreatedAt());

        return dto;
    }
}
