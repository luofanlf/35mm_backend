package com.luofan.backend_35mm.service;

import com.luofan.backend_35mm.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luofan.backend_35mm.model.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;

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
}
