package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.UserSessionService;
import cz.jansimerda.homebrewdash.model.UserSession;
import cz.jansimerda.homebrewdash.rest.dto.converter.UserSessionToCreatedDtoConverter;
import cz.jansimerda.homebrewdash.rest.dto.converter.UserSessionToDtoConverter;
import cz.jansimerda.homebrewdash.rest.dto.request.UserSessionRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.response.UserSessionCreatedResponseDto;
import cz.jansimerda.homebrewdash.rest.dto.response.UserSessionResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/v0/user-sessions")
public class UserSessionController {

    private final UserSessionService userSessionService;

    private final UserSessionToDtoConverter dtoConverter;

    private final UserSessionToCreatedDtoConverter createdDtoConverter;

    public UserSessionController(
            UserSessionService userSessionService,
            UserSessionToDtoConverter userSessionToDtoConverter,
            UserSessionToCreatedDtoConverter userSessionToCreatedDtoConverter
    ) {
        this.userSessionService = userSessionService;
        this.dtoConverter = userSessionToDtoConverter;
        this.createdDtoConverter = userSessionToCreatedDtoConverter;
    }

    @PostMapping
    public ResponseEntity<UserSessionCreatedResponseDto> create(@Valid @RequestBody UserSessionRequestDto request) {
        UserSessionCreatedResponseDto responseDto = createdDtoConverter.apply(
                userSessionService.create(request.getEmail(), request.getPassword())
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping
    public ResponseEntity<Collection<UserSessionResponseDto>> readAll() {
        return ResponseEntity.ok(
                StreamSupport.stream(userSessionService.readAllAccessible().spliterator(), false)
                        .map(dtoConverter).toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSessionResponseDto> readOne(@PathVariable UUID id) {
        UserSession session = userSessionService.readOneAccessible(id)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));

        return ResponseEntity.ok(dtoConverter.apply(session));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        userSessionService.expireById(id);
        return ResponseEntity.noContent().build();
    }
}
