package edu.lysak.recipes.controller;

import edu.lysak.recipes.model.user.BlockedUser;
import edu.lysak.recipes.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/blocked-users")
    public ResponseEntity<List<BlockedUser>> getAllBlockedUsers() {
        return ResponseEntity.ok(userService.getBlockedUsers());
    }
}
