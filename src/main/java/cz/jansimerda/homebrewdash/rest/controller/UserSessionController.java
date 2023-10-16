package cz.jansimerda.homebrewdash.rest.controller;

import cz.jansimerda.homebrewdash.business.UserSessionService;
import cz.jansimerda.homebrewdash.rest.dto.converter.UserSessionToDtoConverter;
import cz.jansimerda.homebrewdash.rest.dto.request.UserSessionRequestDto;
import cz.jansimerda.homebrewdash.rest.dto.response.UserSessionResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/v0/user-sessions")
public class UserSessionController {

    private final UserSessionService userSessionService;

    private final UserSessionToDtoConverter userSessionToDtoConverter;

    public UserSessionController(
            UserSessionService userSessionService,
            UserSessionToDtoConverter userSessionToDtoConverter
    ) {
        this.userSessionService = userSessionService;
        this.userSessionToDtoConverter = userSessionToDtoConverter;
    }

    @PostMapping
    public ResponseEntity<UserSessionResponseDto> create(@Valid @RequestBody UserSessionRequestDto request) {
        UserSessionResponseDto responseDto = userSessionToDtoConverter.apply(userSessionService.create(request));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping
    public ResponseEntity<Collection<UserSessionResponseDto>> readAll() {
        return ResponseEntity.ok(
                StreamSupport.stream(this.userSessionService.readAll().spliterator(), false)
                        .map(userSessionToDtoConverter).toList()
        );
    }
}
