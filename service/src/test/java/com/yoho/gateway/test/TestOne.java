package com.yoho.gateway.test;

import com.alibaba.fastjson.JSON;
import com.yoho.service.model.sns.model.CommentBo;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by chunhua.zhang@yoho.cn on 2015/12/16.
 */
public class TestOne {


    /**
     List<CommentBo> list = JSON.parseArray(pageResponse.getList().toString(), CommentBo.class);


    */
    @Test
    public void test(){
        List<CommentBo> bos = new LinkedList<>();


        CommentBo commentBo = new CommentBo();
        commentBo.setCreateTime(1000);
        commentBo.setProductName("1222");

        bos.add(commentBo);


        commentBo = new CommentBo();
        commentBo.setCreateTime(1002);
        commentBo.setProductName("1222");
        bos.add(commentBo);



        commentBo = new CommentBo();
        commentBo.setCreateTime(998);
        commentBo.setProductName("1222");
        bos.add(commentBo);


        List<CommentBo> list = JSON.parseArray(bos.toString(), CommentBo.class);


        System.out.println(list.get(0).getCreateTime());


    }

}
