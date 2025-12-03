package com.cy.newpt.pojo.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class Admin implements Serializable {
    private int id;
    private String account;
    private String password;
    private String role;
}
