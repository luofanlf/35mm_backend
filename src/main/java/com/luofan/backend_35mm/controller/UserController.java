package com.luofan.backend_35mm.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luofan.backend_35mm.annotation.AuthCheck;
import com.luofan.backend_35mm.common.BaseResponse;
import com.luofan.backend_35mm.common.ResultUtils;
import com.luofan.backend_35mm.common.UserConstant;
import com.luofan.backend_35mm.exception.ErrorCode;
import com.luofan.backend_35mm.exception.ThrowUtils;
import com.luofan.backend_35mm.model.dto.*;
import com.luofan.backend_35mm.model.entity.User;
import com.luofan.backend_35mm.model.vo.LoginUserVO;
import com.luofan.backend_35mm.model.vo.UserVO;
import com.luofan.backend_35mm.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        ThrowUtils.throwIf(userRegisterRequest==null, ErrorCode.PARAMS_ERROR);
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
        ThrowUtils.throwIf(userLoginRequest==null, ErrorCode.PARAMS_ERROR);
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
        ThrowUtils.throwIf(request==null,ErrorCode.PARAMS_ERROR);
        Boolean result = this.userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登陆用户
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }


    /**
     * 创建用户
     * @param userAddRequest
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(UserAddRequest userAddRequest){
        ThrowUtils.throwIf(userAddRequest==null,ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userAddRequest,user);
        //设置默认密码
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);

        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());

    }

    /**
     * 根据id获取用户
     * @param id
     * @return
     */
    @PostMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id){
        ThrowUtils.throwIf(id<=0,ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user==null,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user);
    }


    /**
     * 根据id获取用户VO
     * @param id
     * @return
     */
    @PostMapping("/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserVO> getUserVOById(long id){
        ThrowUtils.throwIf(id<=0,ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        userVO = userService.getUserVO(user);
        ThrowUtils.throwIf(user==null,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(userVO);
    }

    /**
     * 根据id删除用户
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserById(long id){
        ThrowUtils.throwIf(id<=0,ErrorCode.PARAMS_ERROR);
        boolean result = userService.removeById(id);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 更新用户
     * @param userUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(UserUpdateRequest userUpdateRequest){
        ThrowUtils.throwIf(userUpdateRequest==null,ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest,user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        return  ResultUtils.success(result);
    }

    @PostMapping("list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(UserQueryRequest userQueryRequest){
        ThrowUtils.throwIf(userQueryRequest==null,ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage= userService.page(new Page<>(current,pageSize),userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current,pageSize,userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
}

