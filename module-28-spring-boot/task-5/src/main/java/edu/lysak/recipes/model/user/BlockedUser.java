package edu.lysak.recipes.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class BlockedUser {

    private String email;
    private Date lockTime;
}
