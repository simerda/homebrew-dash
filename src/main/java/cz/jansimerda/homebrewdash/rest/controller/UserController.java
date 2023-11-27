package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.UserService;
import cz.jansimerda.homebrewdash.model.User;
import cz.jansimerda.homebrewdash.rest.dto.request.UserRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.response.UserResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController extends AbstractCrudController<User, UserRequestDto, UserResponseDto, UUID> {

    private final UserService service;

    public UserController(
            UserService service,
            Function<User, UserResponseDto> toDtoConverter,
            Function<UserRequestDto, User> toEntityConverter
    ) {
        super(service, toDtoConverter, toEntityConverter);
        this.service = service;
    }

    @Override
    @PostMapping
    @PreAuthorize("!isAuthenticated() || principal.isAdmin()")
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto request) {
        return super.create(request);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> readAll() {
        return ResponseEntity.ok(collectionToDto(service.readAllAccessible()));
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("principal.id == #id || principal.isAdmin()")
    public ResponseEntity<UserResponseDto> readOne(@PathVariable UUID id) {
        return super.readOne(id);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("principal.id == #id || principal.isAdmin()")
    public ResponseEntity<UserResponseDto> update(@Valid @RequestBody UserRequestDto request, @PathVariable UUID id) {
        return super.update(request, id);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("principal.id == #id || principal.isAdmin()")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return super.delete(id);
    }
}
