package com.kotlarz.configuration.security;

import com.kotlarz.backend.domain.system.User;
import com.kotlarz.backend.domain.system.UserType;
import com.kotlarz.backend.service.system.UserService;
import com.kotlarz.configuration.security.exception.UserIsNotLoggedInException;
import com.kotlarz.configuration.security.exception.UserNotFoundException;
import com.kotlarz.configuration.servlet.VaadinSessionListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
@SpringComponent
public class SecurityService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private VaadinSessionListener sessionListener;

    public boolean isLoggedIn() {
        return getAuthentication()
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }

    public void logIn(String username, String password) {
        Authentication token = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public void logOut() {
        SecurityContextHolder.clearContext();
    }

    public void logOut(Long userId) {
        sessionListener.getSessionsForUser(userId).forEach(VaadinSession::close);
    }

    public User getCurrentUser() {
        return getAuthentication()
                .map(Authentication::getPrincipal)
                .map(principal -> (User) principal)
                .map(user -> userService.getUser(user.getId()).orElseThrow(UserNotFoundException::new))
                .orElseThrow(UserIsNotLoggedInException::new);
    }

    public boolean isTypeOf(UserType type) {
        return getAuthentication()
                .map(Authentication::getAuthorities)
                .map(grantedAuthorities -> grantedAuthorities.stream()
                        .anyMatch(authority -> authority.getAuthority().equals(type.name())))
                .orElse(false);
    }

    private Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

}
