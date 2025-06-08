package com.luofan.backend_35mm.service;

import com.luofan.backend_35mm.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author luofan
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-06-08 11:08:37
*/
public interface UserService extends IService<User> {

    long userRegister(String userAccount, String userPassword, String checkPassword);

    String getEncryptPassword(String userPassword);
}
