package com.luofan.backend_35mm.controller;

import com.luofan.backend_35mm.common.BaseResponse;
import com.luofan.backend_35mm.common.ResultUtils;
import com.luofan.backend_35mm.exception.ErrorCode;
import com.luofan.backend_35mm.exception.ThrowUtils;
import com.luofan.backend_35mm.model.dto.UserLoginRequest;
import com.luofan.backend_35mm.model.dto.UserRegisterRequest;
import com.luofan.backend_35mm.model.vo.LoginUserVO;
import com.luofan.backend_35mm.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

    /**
     * 用户登陆
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        ThrowUtils.throwIf(userLoginRequest==null, ErrorCode.SYSTEM_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount,userPassword,request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request){
        ThrowUtils.throwIf(request==null,ErrorCode.SYSTEM_ERROR);
        Boolean result = this.userService.userLogout(request);
        return ResultUtils.success(result);
    }
}

