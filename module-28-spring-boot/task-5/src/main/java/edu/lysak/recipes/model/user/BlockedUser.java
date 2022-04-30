package edu.lysak.recipes.model.user;

import lombok.Data;

import java.util.Date;

@Data
public class BlockedUser {

    private String email;
    private Date lockTime;
}
