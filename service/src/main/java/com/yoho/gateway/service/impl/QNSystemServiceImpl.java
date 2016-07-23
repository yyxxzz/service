package com.yoho.gateway.service.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.yoho.gateway.service.StroSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * 类的描述
 *
 * @author CaoQi
 * @Time 2015/11/10
 */
@Service("qNSystemServiceImpl")
public class QNSystemServiceImpl implements StroSystemService {

    @Value("${qiniu.accesskey}")
    private String accessKey;

    @Value("${qiniu.secretkey}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    private Auth auth;

    private Logger logger= LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void init(){
        auth = Auth.create(accessKey, secretKey);
    }

    // 简单上传，使用默认策略
    private String getUpToken(String bucket){
        return auth.uploadToken(bucket);
    }

    public void upload(File file,String fileName, String bucket){
        UploadManager uploadManager = new UploadManager();
        try {
            Response res = uploadManager.put(file, fileName, getUpToken(bucket));

            if (res.isOK()){
                logger.info("七牛上传成功,文件名是:{}",fileName);
            }
        } catch (QiniuException e) {
            try {
                logger.error("上传文件到七牛出错，文件名"+file.getName()+";出错的原因："+e.response.bodyString());
            } catch (QiniuException e1) {
                logger.error("上传文件到七牛出错");
            }
        }
    }
}
