package com.bca.pratima.controller;

import com.bca.pratima.dto.*;
import com.bca.pratima.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
@Slf4j
public class AuthenticationController {

    @Autowired
    private AuthenticationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Response> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        String newToken = service.register(request);

        Status status = Status.builder().status(HttpStatus.ACCEPTED.value())
                .message("Since Render Free tiar blocks the smtp email service. Please use OPT: "+newToken+" to activate your account.").build();
        Response response = Response.builder().status(status).build();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/google")
    public ResponseEntity<AuthenticationResponse> authenticateWithGoogle(
            @RequestBody @Valid GoogleAuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticateWithGoogle(request.getCredential()));
    }

    @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        service.activateAccount(token);
    }


}
