package com.yoho.gateway.service;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传的一些业务
 *
 * @author CaoQi
 * @Time 2015/11/10
 */
@Service
public class UploadService {

    @Value("${file.saveDir}")
    private String saveDir;


    @Resource(name = "stroSysList")
    private List<StroSystemService> stroSysList;

    public String upload(MultipartFile file,String bucket,String uid) throws Exception{
        String fileMode = "";
        String fileName = "" ;

        String filePath;

        if (StringUtils.equals(bucket, "yhb-img01")) {
            fileMode = "01";
        } else if (StringUtils.equals(bucket, "yhb-img02")) {
            fileMode = "02";
        } else {
            fileMode = "0" + (new Random().nextInt(1) + 1);
        }

        String saveName = "/" + Calendar.getInstance().get(Calendar.YEAR) + "/"
                + (Calendar.getInstance().get(Calendar.MONTH)+1) + "/"
                + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/"
                + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "/";

        fileName =fileMode + DigestUtils.md5Hex(uid + "_" + new Date().getTime() + file.getOriginalFilename()) +
                "." + file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".") + 1);


        // 文件保存路径
        filePath = saveDir + File.separator + fileName;


        File file1 = new File(filePath);
        file.transferTo(file1);

//        所有的文件系统都上传一次，至于上传策略 以后再优化
        for (StroSystemService stroSystemService:stroSysList){
            stroSystemService.upload(file1,bucket+saveName+fileName, bucket);
        }

        file1.delete();
        return saveName+ fileName;
    }
}
