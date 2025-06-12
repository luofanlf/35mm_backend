package com.luofan.backend_35mm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luofan.backend_35mm.model.dto.UserQueryRequest;
import com.luofan.backend_35mm.model.entity.User;
import com.luofan.backend_35mm.model.vo.LoginUserVO;
import com.luofan.backend_35mm.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author luofan
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-06-08 11:08:37
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 加密密码
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    /**
     * 用户登陆
     * @param userAccount
     * @param userPassword
     * @return
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取user vo
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取当前登录user
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户登出
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取user vo
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取uservo列表
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取用户查询querywrapper
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);


}
