package com.bca.pratima.service;


import com.bca.pratima.dto.AuthenticationRequest;
import com.bca.pratima.dto.AuthenticationResponse;
import com.bca.pratima.dto.RegistrationRequest;
import com.bca.pratima.appenum.AuthProvider;
import com.bca.pratima.entity.Token;
import com.bca.pratima.entity.User;
import com.bca.pratima.exception.InvalidCredentialsException;
import com.bca.pratima.repository.RoleRepository;
import com.bca.pratima.repository.TokenRepository;
import com.bca.pratima.repository.UserRepository;
import com.bca.pratima.security.JwtService;
import com.bca.pratima.utils.EmailService;
import com.bca.pratima.utils.EmailTemplateName;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenRepository tokenRepository;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Value("${application.security.oauth2.google.client-id:}")
    private String googleClientId;

    public String register(RegistrationRequest request) throws MessagingException {
        String newToken = "";
        var userRole = roleRepository.findByName("NORMAL")
                // todo - better exception handling
                .orElseThrow(() -> new IllegalStateException("ROLE NORMAL was not initiated"));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .provider(AuthProvider.LOCAL)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        try{
            newToken = sendValidationEmail(user);
        }catch (Exception e){
            log.info("Error while sending the email.");
            e.printStackTrace();
        }

        return newToken;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = ((User) auth.getPrincipal());
        return createAuthenticationResponse(user);
    }

    public AuthenticationResponse authenticateWithGoogle(String credential) {
        if (googleClientId.isBlank()) {
            throw new IllegalStateException("Google sign-in is not configured");
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
            GoogleIdToken idToken = verifier.verify(credential);
            if (idToken == null || !Boolean.TRUE.equals(idToken.getPayload().getEmailVerified())) {
                throw new InvalidCredentialsException("Invalid Google credential");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> createGoogleUser(payload));
            return createAuthenticationResponse(user);
        } catch (InvalidCredentialsException exception) {
            throw exception;
        } catch (Exception exception) {
            log.warn("Google sign-in failed", exception);
            throw new InvalidCredentialsException("Invalid Google credential");
        }
    }

    private User createGoogleUser(GoogleIdToken.Payload payload) {
        var userRole = roleRepository.findByName("NORMAL")
                .orElseThrow(() -> new IllegalStateException("ROLE NORMAL was not initiated"));
        String firstName = payload.get("given_name") instanceof String name ? name : "Google";
        String lastName = payload.get("family_name") instanceof String name ? name : "User";
        var user = User.builder()
                .firstname(firstName)
                .lastname(lastName)
                .email(payload.getEmail())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .accountLocked(false)
                .enabled(true)
                .provider(AuthProvider.GOOGLE)
                .roles(List.of(userRole))
                .build();
        return userRepository.save(user);
    }

    private AuthenticationResponse createAuthenticationResponse(User user) {
        var claims = new HashMap<String, Object>();
        claims.put("fullName", user.getFullName());

        var jwtToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .userEmail(user.getEmail())
                .userName(user.fullName())
                .build();
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                // todo exception has to be defined
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    private String generateAndSaveActivationToken(User user) {
        // Generate a token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }

    private String sendValidationEmail(User user) throws MessagingException {
        String newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
                );
        return newToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
}
