package com.yoho.gateway.interceptor;

import com.yoho.gateway.model.browse.BrowseReqVO;
import com.yoho.gateway.service.favorite.IBrowseServiceNew;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 商品浏览记录统计
 *
 * @author xieyong
 */
public class ProductBrowserStatistic implements HandlerInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(ProductBrowserStatistic.class);

    /**
     * 商品详情页下半部分触发的方法名
     */
    private final static String PRODUCTINTRO_METHODNAME = "app.product.intro";

    //private static AsyncRestTemplate restTemplate;
    private final static String H5_PRODUCTINTRO_METHODNAME = "h5.product.intro";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String method = request.getParameter("method");
        if (PRODUCTINTRO_METHODNAME.equals(method) || H5_PRODUCTINTRO_METHODNAME.equals(method)) {
            int productSkn = 0;
            if (H5_PRODUCTINTRO_METHODNAME.equals(method)) {
                productSkn = NumberUtils.toInt(request.getParameter("productskn"), 0);
            } else {
                productSkn = NumberUtils.toInt(request.getParameter("product_skn"), 0);
            }
            Integer uid = StringUtils.isBlank(request.getParameter("uid")) ? 0 : NumberUtils.toInt(request.getParameter("uid"), 0);
            String udid = StringUtils.isBlank(request.getParameter("udid")) ? "" : request.getParameter("udid");
            String app_version = StringUtils.isBlank(request.getParameter("app_version")) ? "" : request.getParameter("app_version");
            int flag = 0;
            try {
                flag = StringUtils.isBlank(request.getParameter("app_version")) ? 0 : Integer.parseInt(request.getParameter("app_version"));
            } catch (Exception e) {
            }
            if (1 == flag) {
                return true;
            }

            final int skn = productSkn;
            BrowseReqVO browseReqVO = new BrowseReqVO();
            browseReqVO.setSkn(skn + "");
            browseReqVO.setUdid(udid);
            browseReqVO.setUid(uid);
            browseReqVO.setApp_version(app_version);
            try {
                IBrowseServiceNew.blockingQueue.offer(browseReqVO);
            } catch (Exception e) {
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        //dothing
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        //donoting
    }

}
