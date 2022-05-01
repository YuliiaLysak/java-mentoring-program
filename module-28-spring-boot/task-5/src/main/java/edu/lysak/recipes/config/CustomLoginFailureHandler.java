package edu.lysak.recipes.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.lysak.recipes.model.user.User;
import edu.lysak.recipes.service.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final UserService userService;
    private final int maxFailedAttempts;
    private final long lockTimeDuration;

    public CustomLoginFailureHandler(
            UserService userService,
            @Value("${login.max-failed-attempts}") int maxFailedAttempts,
            @Value("${login.lock-time}") long lockTimeDuration
    ) {
        this.userService = userService;
        this.maxFailedAttempts = maxFailedAttempts;
        this.lockTimeDuration = lockTimeDuration;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        String email = request.getParameter("username"); // username = email
        User user = userService.getByEmail(email);

        if (user != null) {
            if (user.isAccountNonLocked()) {
                if (user.getFailedAttempt() < maxFailedAttempts - 1) {
                    userService.increaseFailedAttempts(user);
                } else {
                    userService.lock(user);
                    exception = new LockedException(
                            String.format("Your account has been locked due to %s failed attempts." +
                            " It will be unlocked after %s minutes.", maxFailedAttempts, (lockTimeDuration / 1000 / 60))
                    );
                }
            } else {
                if (userService.unlockWhenTimeExpired(user)) {
                    exception = new LockedException("Your account has been unlocked. Please try to login again.");
                }
            }
        }

        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }

}
