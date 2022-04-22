package edu.lysak.ldap.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LdapRestController {
    @GetMapping(value = "/info")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getInfo() {
        return "Page is visible only for admins";
    }

    @GetMapping(value = "/info-user")
//    @PreAuthorize("hasRole('ROLE_USER')")
    public String getInfoForUsers() {
        return "Page is visible for all users";
    }
}
