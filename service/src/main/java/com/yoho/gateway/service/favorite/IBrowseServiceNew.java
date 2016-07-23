package com.yoho.gateway.service.favorite;

import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.browse.BrowseReqVO;
import com.yoho.gateway.model.browse.ProductBrowseRespVO;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public interface IBrowseServiceNew {

    /**
     * 描述：获取用户浏览记录的商品列表,从java的redis缓存中获取<br>
     * 业务流程：<br>
     * &nbsp;&nbsp;1.校验uid：若为空，则抛GatewayException(500, "请先登录")<br>
     * &nbsp;&nbsp;2.异步请求商品服务，获取商品二级分类列表<br>
     * &nbsp;&nbsp;3.从java的redis缓存中获取product_skn列表<br>
     * &nbsp;&nbsp;4.异步请求商品服务，获取商品信息列表<br>
     * &nbsp;&nbsp;5.数据处理，返回<br>
     *
     * @param browseReqVO
     * @return ProductBrowseRespVO
     * @throws GatewayException
     */
    ProductBrowseRespVO listBrowse(BrowseReqVO browseReqVO) throws GatewayException;

    /**
     * 描述：删除/清空浏览记录，从java的redis缓存中删除/清空<br>
     * 业务流程：<br>
     * &nbsp;&nbsp;1.校验uid：若为空，则抛ServiceException(400, "用户ID错误")<br>
     * &nbsp;&nbsp;2.判断product_skn和category_id：若都为空，则清空该用户的java，redis缓存<br>
     * &nbsp;&nbsp;3.判断product_skn：若为空，则获取用户浏览记录列表并根据分类ID删除列表数据；若不为空，
     * 则获取用户浏览记录列表并根据商品SKN删除列表数据<br>
     * &nbsp;&nbsp;4.刷新该用户的java，redis缓存<br>
     *
     * @param browseReqVO
     * @return
     * @throws GatewayException
     */
    void deleteBrowse(BrowseReqVO browseReqVO) throws GatewayException;

    /**
     * 描述：批量新增浏览记录<br>
     * 业务流程：<br>
     * &nbsp;&nbsp;1.初始化参数：uid， browseList<br>
     * &nbsp;&nbsp;2.参数校验：uid， browseList不能为空<br>
     * &nbsp;&nbsp;3.获取用户浏览记录列表<br>
     * &nbsp;&nbsp;4.合并浏览记录<br>
     * &nbsp;&nbsp;5.清空用户浏览记录<br>
     * &nbsp;&nbsp;6.重新push用户浏览记录<br>
     * &nbsp;&nbsp;7.返回<br>
     *
     * @param browseReqVO
     * @return
     * @throws GatewayException
     */
    Long addBrowseBatch(BrowseReqVO browseReqVO) throws GatewayException;

    /**
     * 描述：获取用户浏览记录总数<br>
     * 业务流程：<br>
     * &nbsp;&nbsp;1.校验uid：若为空，则抛GatewayException(400, "请先登录")<br>
     * &nbsp;&nbsp;2.从老php接口同步用户浏览记录数据(每个用户(uid)只同步一次)到java的redis缓存中<br>
     * &nbsp;&nbsp;3.java的redis缓存，合并未登陆时的浏览记录(根据udid)到该用户下<br>
     * &nbsp;&nbsp;4.从java的redis缓存中获取用户的浏览记录数量并返回<br>
     *
     * @param browseReqVO
     * @return int
     * @throws GatewayException
     */
    int totalBrowse(BrowseReqVO browseReqVO) throws GatewayException;

    BlockingQueue<BrowseReqVO> blockingQueue = new ArrayBlockingQueue<>(20000);
}
