package com.luofan.backend_35mm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luofan.backend_35mm.exception.BusinessException;
import com.luofan.backend_35mm.exception.ErrorCode;
import com.luofan.backend_35mm.mapper.UserMapper;
import com.luofan.backend_35mm.model.dto.UserQueryRequest;
import com.luofan.backend_35mm.model.entity.User;
import com.luofan.backend_35mm.model.enums.UserRoleEnum;
import com.luofan.backend_35mm.model.vo.LoginUserVO;
import com.luofan.backend_35mm.model.vo.UserVO;
import com.luofan.backend_35mm.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author luofan
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-06-08 11:08:37
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    public static final String USER_LOGIN_STATE = "USER_LOGIN_STATE";
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
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

    /**
     * 加密密码
     * @param userPassword
     * @return
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "luofan";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    /**
     * 用户登陆
     * @param userAccount
     * @param userPassword
     * @return
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if(StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }

        //2.加密密码
        String encryptPassword = getEncryptPassword(userPassword);

        //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);

        if (user == null){
            log.info("user account and password can not match,login failed");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户或密码错误");
        }

        //3.记录用户的登陆状态
        request.getSession().setAttribute(USER_LOGIN_STATE,user);
        return getLoginUserVO(user);

    }

    /**
     * 获取登陆用户vo
     * @param user
     * @return
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null){
            return null;
        }
       LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user,loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取当前登陆用户
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User)userObj;
        if(currentUser==null||currentUser.getId()==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;

    }

    /**
     * 用户登出
     * @param request
     * @return
     */
    public boolean userLogout(HttpServletRequest request){
        Object user = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"未登陆");
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 返回uservo
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        if (user==null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user,userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if(CollUtil.isEmpty(userList)){
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    /**
     * 获取用户查询querywarpper
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }



}




