package com.yoho.gateway.controller.fileupload;

import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by CaoQi on 2015/10/23.
 */
@Controller
public class FileUploadController {

    @Value("${qiniu.domain}")
    private String qiniuDomain;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UploadService uploadService;

    /**
     * SNS 的文件上传接口
     *
     * @param file   spring 默认的上传文件对象
     * @param userId 用户userid
     * @return
     */
    @RequestMapping( params = "method=yoho.fileupload")
    @ResponseBody
    public ApiResponse fileUpload(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId,
                                   @RequestParam("bucket")String bucket)throws Exception{
    	logger.info("come in FileUploadController method=yoho.fileupload userId is:{}, bucket is:{}", userId, bucket);
        ApiResponse responseBean = new ApiResponse();
        String saveName = uploadService.upload(file,bucket , userId);
        responseBean.setCode(200);
        responseBean.setMessage("上传成功");
        responseBean.setData(saveName);
        logger.info("out FileUploadController method=yoho.fileupload userId is:{}, bucket is:{}", userId, bucket);
        return responseBean;
    }

}
