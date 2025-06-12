package com.luofan.backend_35mm.aop;

import com.luofan.backend_35mm.annotation.AuthCheck;
import com.luofan.backend_35mm.exception.BusinessException;
import com.luofan.backend_35mm.exception.ErrorCode;
import com.luofan.backend_35mm.model.entity.User;
import com.luofan.backend_35mm.model.enums.UserRoleEnum;
import com.luofan.backend_35mm.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthInterceptor {

    @Resource
    UserService userService;

    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck)throws Throwable{
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();

        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);

        //如果不需要校验权限，则放行
        if(mustRoleEnum==null){
            return joinPoint.proceed();
        }

        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        //用户没有权限，抛出异常
        if(userRoleEnum==null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //需要管理员权限而用户没有
        if(UserRoleEnum.ADMIN.equals(mustRoleEnum)&&!UserRoleEnum.ADMIN.equals(userRoleEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        //通过校验，放行
        return joinPoint.proceed();
    }

}
