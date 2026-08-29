package com.expensetracker.controller;

import com.expensetracker.dto.ChangePasswordRequest;
import com.expensetracker.dto.UpdateUserRequest;
import com.expensetracker.dto.UserResponse;
import com.expensetracker.service.UserService;
import com.expensetracker.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(userService.getCurrentUser(email));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(userService.updateUser(email, request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        userService.changePassword(email, request);
        return ResponseEntity.ok("Password changed successfully");
    }
}
