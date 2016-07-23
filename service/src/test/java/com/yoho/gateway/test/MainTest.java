package com.yoho.gateway.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by zhaoqi on 2016/6/14.
 */
public class MainTest {
    public static void main(String[] args) {
//        String jsonStr = "{\"price\":{\"0,39\":\"￥0-39\",\"40,119\":\"￥40-119\",\"120,249\":\"￥120-249\",\"250,99999\":\"￥249以上\"},\"message\":\"Search List.\"}";
//        String jsonStr="{\"message\":\"Search List.\",\"price\":[{\"0,39\":\"￥0-39\"},{\"40,119\":\"￥40-119\"},{\"120,249\":\"￥120-249\"},{\"250,99999\":\"￥249以上\"}]}";

//        String jsonStr = "{\"price\":{\"0,79\":\"￥0-79\",\"80,179\":\"￥80-179\",\"180,99999\":\"￥179以上\"},\"message\":\"Search List.\"}";
        String jsonStr="{\"filter\":{\"color\":\"red\",\"price\":{\"0,79\":\"￥0-79\",\"80,179\":\"￥80-179\",\"180,99999\":\"￥179以上\"},\"message\":\"Search List.\"}}";

        JSONObject fiter=JSON.parseObject(jsonStr);
        JSONObject sortFilter = new JSONObject(true);
        for(String key : fiter.getJSONObject("filter").keySet()) {
            sortFilter.put(key,fiter.getJSONObject("filter").get(key));
        }

        JSONObject price = sortFilter.getJSONObject("price");

        JSONObject jsonObject = new JSONObject(true);

        List<String> priceRange = new ArrayList<>();
        priceRange.addAll(price.keySet());
        Collections.sort(priceRange, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.valueOf(o1.split(",")[0].trim()).compareTo(Integer.valueOf(o2.split(",")[0].trim()));
            }
        });
        for (String key: priceRange) {
            jsonObject.put(key,price.get(key));
        }
        sortFilter.put("price",jsonObject);

        System.out.println(sortFilter.toString());
//        System.out.println(JSON.toJSONString(jsonObject.getJSONObject("data").get("filter")));
//        System.out.println(JSON.toJSONString(jsonObject));
    }
}

