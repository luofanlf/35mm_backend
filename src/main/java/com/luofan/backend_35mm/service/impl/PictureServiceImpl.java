package com.luofan.backend_35mm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luofan.backend_35mm.exception.ErrorCode;
import com.luofan.backend_35mm.exception.ThrowUtils;
import com.luofan.backend_35mm.manager.FileManager;
import com.luofan.backend_35mm.mapper.PictureMapper;
import com.luofan.backend_35mm.model.dto.PictureUploadRequest;
import com.luofan.backend_35mm.model.dto.file.UploadPictureResult;
import com.luofan.backend_35mm.model.entity.Picture;
import com.luofan.backend_35mm.model.entity.User;
import com.luofan.backend_35mm.model.vo.PictureVO;
import com.luofan.backend_35mm.service.PictureService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;

/**
* @author luofan
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-06-15 13:58:25
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{


    @Resource
    FileManager fileManager;

    /**
     * 上传图片
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) {
        ThrowUtils.throwIf(loginUser==null, ErrorCode.NO_AUTH_ERROR);
        //判断是新增还是修改图片
        Long pictureId = null;
        if(pictureUploadRequest!=null){
            pictureId = pictureUploadRequest.getId();
        }
        //如果是更新图片，判断是否已经存在（因为如果是新增前端的表单中不带id）
        if(pictureId!=null){
            boolean exist = this.lambdaQuery().eq(Picture::getId,pictureId).exists();
            ThrowUtils.throwIf(!exist,ErrorCode.NOT_FOUND_ERROR,"图片不存在");
        }
        //上传照片，得到信息
        String uploadPathPrefix = String.format("public/%s",loginUser.getId());
        UploadPictureResult uploadPictureResult = fileManager.uploadPicture(multipartFile,uploadPathPrefix);

        //构造picture实体
        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());

        //如果pictureid不为空，则为更新，反之为新增
        if(pictureId !=null){
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR,"图片上传失败");
        return PictureVO.objToVo(picture);
    }
}




