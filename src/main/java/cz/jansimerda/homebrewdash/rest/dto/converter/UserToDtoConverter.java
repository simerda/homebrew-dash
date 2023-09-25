package cz.jansimerda.homebrewdash.rest.dto.converter;

import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.rest.dto.response.UserResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserToDtoConverter implements Function<User, UserResponseDto> {

    @Override
    public UserResponseDto apply(User user) {
        UserResponseDto dto = new UserResponseDto();

        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        user.getUsername().ifPresent(dto::setUsername);
        user.getFirstName().ifPresent(dto::setFirstName);
        user.getSurname().ifPresent(dto::setSurname);

        return dto;
    }
}
