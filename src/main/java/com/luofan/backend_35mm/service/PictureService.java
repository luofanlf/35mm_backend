package com.luofan.backend_35mm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.luofan.backend_35mm.model.dto.PictureUploadRequest;
import com.luofan.backend_35mm.model.entity.Picture;
import com.luofan.backend_35mm.model.entity.User;
import com.luofan.backend_35mm.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

/**
* @author luofan
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-06-15 13:58:25
*/
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

}
