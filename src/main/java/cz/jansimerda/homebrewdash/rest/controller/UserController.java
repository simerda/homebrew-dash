package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.AbstractCrudService;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.rest.dto.request.UserRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.response.UserResponseDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping(value = "/api/v0/users")
public class UserController extends AbstractCrudController<User, UserRequestDto, UserResponseDto, UUID> {
    public UserController(
            AbstractCrudService<User, UUID> service,
            Function<User, UserResponseDto> toDtoConverter,
            Function<UserRequestDto, User> toEntityConverter
    ) {
        super(service, toDtoConverter, toEntityConverter);
    }
}
