package com.luofan.backend_35mm.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.luofan.backend_35mm.config.CosClientConfig;
import com.luofan.backend_35mm.exception.BusinessException;
import com.luofan.backend_35mm.exception.ErrorCode;
import com.luofan.backend_35mm.exception.ThrowUtils;
import com.luofan.backend_35mm.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class FileManager {  
  
    @Resource
    private CosClientConfig cosClientConfig;
  
    @Resource  
    private CosManager cosManager;  
  
    public UploadPictureResult uploadPicture(MultipartFile multipartFile,String uploadPathPrefix){
        //校验图片
        validPicture(multipartFile);
        //图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originFilename = multipartFile.getOriginalFilename();
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,FileUtil.getSuffix(originFilename));
        String uploadPath = String.format("/%s/%s",uploadPathPrefix,uploadFilename);
        File file = null;
        try{
            //创建临时图片
            file = File.createTempFile(uploadPathPrefix,null);
            multipartFile.transferTo(file);
            //上传图片
            PutObjectResult putObjectResult = cosManager.putObject(uploadPath,file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            //返回封装结果
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth*1.0/picHeight,2).doubleValue();
            uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setUrl(cosClientConfig.getHost()+"/"+uploadPath);
            return uploadPictureResult;
        }catch(Exception e){
            log.error("图片上传到对象存储失败",e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"上传失败");
        }finally{
            this.deleteTempFile(file);
        }
    }

    /**
     * 校验文件
     * @param multipartFile
     */
    public void validPicture(MultipartFile multipartFile){
        ThrowUtils.throwIf(multipartFile.isEmpty(), ErrorCode.PARAMS_ERROR,"文件不能为空");
        //1.校验文件大小
        long fileSize = multipartFile.getSize();
        final long ONE_M = 1024*1024L;
        ThrowUtils.throwIf(fileSize>20*ONE_M,ErrorCode.PARAMS_ERROR,"文件不可以超过20M");
        //2.校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg","jpg","png","webp");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix),ErrorCode.PARAMS_ERROR,"文件后缀不符合");
    }

    public void deleteTempFile(File file){
        if (file == null){
            return;
        }
        //删除临时文件
        boolean deleteResult = file.delete();
        if (!deleteResult){
            log.error("file delete error,filepath = {}",file.getAbsolutePath());
        }
    }
}
