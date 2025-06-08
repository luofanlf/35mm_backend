package com.luofan.backend_35mm.controller;

import com.luofan.backend_35mm.common.BaseResponse;
import com.luofan.backend_35mm.common.ResultUtils;
import com.luofan.backend_35mm.exception.ErrorCode;
import com.luofan.backend_35mm.exception.ThrowUtils;
import com.luofan.backend_35mm.model.dto.UserRegisterRequest;
import com.luofan.backend_35mm.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        ThrowUtils.throwIf(userRegisterRequest==null, ErrorCode.SYSTEM_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount,userPassword,checkPassword);
        return ResultUtils.success(result);
    }
}
