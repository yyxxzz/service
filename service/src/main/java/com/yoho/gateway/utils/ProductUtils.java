package com.yoho.gateway.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.common.helpers.ImagesHelper;
import com.yoho.product.model.html5.YourPreferProduct;

/**
 * Created by sailing on 2015/12/11.
 */
public final class ProductUtils {
    /**
     * 格式化商品信息
     *
     * @param array productData 需要格式化的商品数据
     * @param bool showTag 控制是否显示标签
     * @param bool showNew 控制是否显示NEW图标
     * @param bool showSale 控制是否显示SALE图标
     * @param int width 图片的宽度
     * @param int height 图片的高度
     * @param bool isApp 判断是不是APP访问
     * @param bool showPoint 商品价格是否显示小数位，默认显示
     * @return array | false
     */
    public static YourPreferProduct formatProduct(JSONObject productData , Integer width ,Integer height)
    {
        if (width==null)
        {
        	width = 290;
        }
        if (height==null)
        {
        	height = 388;
        }
        // 商品信息有问题，则不显示
        Integer product_skn = productData.getInteger("product_skn");
        JSONArray goods_list = productData.getJSONArray("goods_list");
        if (product_skn==null || goods_list==null || goods_list.isEmpty()) {
            return new YourPreferProduct();
        }

        // 市场价和售价一样，则不显示市场价
        String market_price = productData.getString("market_price");
        String sales_price = productData.getString("sales_price");
        if (StringUtils.isNotBlank(market_price) && Double.valueOf(market_price).equals(Double.valueOf(sales_price))) {
            market_price = "";
        }

        // 如果productData[default_images]为空，就取productData[goods_list]中第一个，为空就不处理
        String defaultImg = productData.getString("default_images");
        if (StringUtils.isBlank(defaultImg)) {
            productData.replace("default_images",goods_list.getJSONObject(0).getString("images_url")) ;
        }

        YourPreferProduct yourPreferProduct = new YourPreferProduct();
        yourPreferProduct.setId(productData.getInteger("product_skn"));
        yourPreferProduct.setProduct_id(productData.getInteger("product_id"));

        yourPreferProduct.setThumb(StringUtils.isBlank(defaultImg) ? "" : ImagesHelper.getImageUrl(defaultImg, width, height));
        yourPreferProduct.setName(productData.getString("product_name"));
        //String market_price = productData.getString("market_price");
        //补全两位
        yourPreferProduct.setPrice(StringUtils.isBlank(market_price)? "":String.format("%.2f" ,Double.valueOf(market_price)));
        yourPreferProduct.setSalePrice(String.format("%.2f" ,Double.valueOf(productData.getString("sales_price"))));
        StringBuilder builder = new StringBuilder();
        builder.append("http://m.yohobuy.com")
                .append("/product/pro_").append(productData.getString("product_id")).append("_")
                .append(goods_list.getJSONObject(0).get("goods_id")).append("/")
                .append(productData.getString("cn_alphabet")).append(".html");;
        String url = builder.toString();
        // APP访问需要加附加的参数
        // 备注：如果以后APP的接口太多，可以把这边参数提取出来，变成一个公共的方法来生成，便于以后管理维护
        url += "?openby:yohobuy={\"action\":\"go.productDetail\",\"params\":{\"product_skn\":" + product_skn + "}}";
        yourPreferProduct.setUrl(url);

        return yourPreferProduct;
    }
    
}
