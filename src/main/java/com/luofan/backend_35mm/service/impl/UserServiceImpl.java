package com.luofan.backend_35mm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luofan.backend_35mm.exception.BusinessException;
import com.luofan.backend_35mm.exception.ErrorCode;
import com.luofan.backend_35mm.mapper.UserMapper;
import com.luofan.backend_35mm.model.entity.User;
import com.luofan.backend_35mm.model.enums.UserRoleEnum;
import com.luofan.backend_35mm.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
* @author luofan
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-06-08 11:08:37
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword){
        // 1.校验
        if (StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if(userPassword.length()<8||checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入密码不一致");
        }

        // 2.检查账号是否重复
        QueryWrapper<User>  queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
        }

        // 3.加密密码
        String encryptPassword = getEncryptPassword(userPassword);

        // 4.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("默认用户");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean result = this.save(user);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据库错误，注册失败");
        }
        return user.getId();

    }

    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "luofan";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

}




