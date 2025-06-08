package com.luofan.backend_35mm.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 20030808L;

    private String userAccount;

    private String userPassword;

}
