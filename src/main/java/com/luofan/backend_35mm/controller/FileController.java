package com.luofan.backend_35mm.controller;

import com.luofan.backend_35mm.annotation.AuthCheck;
import com.luofan.backend_35mm.common.BaseResponse;
import com.luofan.backend_35mm.common.ResultUtils;
import com.luofan.backend_35mm.common.UserConstant;
import com.luofan.backend_35mm.model.dto.PictureUploadRequest;
import com.luofan.backend_35mm.model.entity.User;
import com.luofan.backend_35mm.model.vo.PictureVO;
import com.luofan.backend_35mm.service.PictureService;
import com.luofan.backend_35mm.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    UserService userService;
    @Resource
    PictureService pictureService;

    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PictureVO> uploadPicture(@RequestPart("file")MultipartFile multipartFile,
                                                 PictureUploadRequest pictureUploadRequest,
                                                 HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile,pictureUploadRequest,loginUser);
        return ResultUtils.success(pictureVO);
    }
}
