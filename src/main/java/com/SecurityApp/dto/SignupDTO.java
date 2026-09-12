package com.SecurityApp.dto;

import com.SecurityApp.entities.enums.Role;
import lombok.Data;

import java.util.List;

@Data
public class SignupDTO {

    private String email;
    private String password;
    private String  name;
    //not recommended in prod to give role at signup time im doing for test purpose later i will change
    private List<Role>roles;
}
