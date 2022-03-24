package edu.lysak.recipes.actuator;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

@Component
public class H2DatabaseHealthIndicator implements HealthIndicator {

    private final String url;
    private final String username;
    private final String password;

    public H2DatabaseHealthIndicator(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @SneakyThrows
    @Override
    public Health health() {
        int errorCode = check();
        if (errorCode != 0) {
            return Health.down().withDetail("H2 database is down", errorCode).build();
        }
        return Health.up().build();
    }

    private int check() throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            return conn.isClosed() ? 1 : 0;
        } catch (Exception exception) {
            return 1;
        }
    }

}
