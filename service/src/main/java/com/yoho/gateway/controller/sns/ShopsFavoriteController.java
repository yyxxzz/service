package com.yoho.gateway.controller.sns;

import java.util.Arrays;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.product.model.ProductBo;
import com.yoho.product.request.BatchBaseRequest;
import com.yoho.product.request.FavoriteRequest;
import com.yoho.product.response.VoidResponse;

/**
 * 这个类是为了H5逛得重构（PS:H5要求重构的时候同参数同返回）
 * 
 * @author hugufei
 */

@Controller
@RequestMapping(value = "/shops/service/*/favorite")
public class ShopsFavoriteController {

	private static Logger logger = LoggerFactory.getLogger(ShopsFavoriteController.class);

	@Resource
	ServiceCaller serviceCaller;

	private static final String GET_PRODUCT_BY_SKN = "product.batchQueryProductBasicInfo";
	private static final String ISFAVORITE_URL = "product.isFavorite";
	private static final String CANCELFAVORITE_URL = "product.cancelFavorite";
	private static final String ADDFAVORTIE_URL = "product.addFavorite";

	@RequestMapping(value = "/getUidBrandFav")
	@ResponseBody
	public ApiResponse getUidBrandFav(@RequestParam(defaultValue = "0") int uid, @RequestParam(defaultValue = "0") int brandId) throws GatewayException {
		logger.info("Enter ShopsFavoriteController.getUidBrandFav. param uid is {}, brandId is {}", uid, brandId);
		if (uid < 1 || brandId < 1) {
			return new ApiResponse.ApiResponseBuilder().code(404).message("uid or brandId is null").build();
		}
		FavoriteRequest favoriteRequest = new FavoriteRequest();
		favoriteRequest.setUid(uid);
		favoriteRequest.setType("brand");
		favoriteRequest.setId(brandId);
		logger.info("getUidBrandFav: begin call product.isFavorite. request param is {}", favoriteRequest);
		Boolean result = serviceCaller.call(ISFAVORITE_URL, favoriteRequest, Boolean.class);
		logger.info("getUidBrandFav: End call product.isFavorite. request param is {}, result is {}", favoriteRequest, result);

		if (result != null && result.booleanValue() == true) {
			return new ApiResponse.ApiResponseBuilder().code(200).message("favorite").build();
		} else {
			return new ApiResponse.ApiResponseBuilder().code(404).message("not favorite").build();
		}
	}

	@RequestMapping(value = "/addUidProductFav")
	@ResponseBody
	public ApiResponse addUidProductFav(@RequestParam(defaultValue = "0") int uid, @RequestParam(defaultValue = "0") int productSkn) throws GatewayException {
		logger.info("Enter ShopsFavoriteController.addUidProductFav. param uid is {}, productSkn is {}", uid, productSkn);

		// 参数检测
		if (uid < 1 || productSkn < 1) {
			return new ApiResponse.ApiResponseBuilder().code(400).message("uid or productSkn is null").build();
		}

		// 查询商品
		ProductBo product = this.getProductBoByProductSkn(productSkn);
		if (product == null || product.getId() == null || product.getId() <= 0) {
			return new ApiResponse.ApiResponseBuilder().code(404).message("product is not exist").build();
		}
		// 调用服务检测是否已收藏
		FavoriteRequest isFavRequest = new FavoriteRequest();
		isFavRequest.setUid(uid);
		isFavRequest.setType("product");
		isFavRequest.setId(product.getId());
		logger.info("getUidBrandFav: begin call product.isFavorite. request param is {}", isFavRequest);
		Boolean result = serviceCaller.call(ISFAVORITE_URL, isFavRequest, Boolean.class);
		logger.info("getUidBrandFav: End call product.isFavorite. request param is {}, result is {}", isFavRequest, result);
		if (result != null && result) {
			return new ApiResponse.ApiResponseBuilder().code(400).message("Already the Favorite").build();
		}

		// 调用新增收藏的服务
		FavoriteRequest addRequest = new FavoriteRequest();
		addRequest.setUid(uid);
		addRequest.setType("product");
		addRequest.setId(product.getId());
		logger.info("getUidBrandFav: begin call product.addFavorite. request param is {}", addRequest);
		Integer fav_id = serviceCaller.call(ADDFAVORTIE_URL, addRequest, Integer.class);
		logger.info("getUidBrandFav: End call product.addFavorite. request param is {}, fav_id is {}", addRequest, fav_id);

		if (fav_id != null && fav_id > 0) {
			return new ApiResponse.ApiResponseBuilder().code(200).message("success").data(fav_id).build();
		} else {
			return new ApiResponse.ApiResponseBuilder().code(201).message("faild").build();
		}
	}

	@RequestMapping(value = "/delUidProductFav")
	@ResponseBody
	public ApiResponse delUidProductFav(@RequestParam(defaultValue = "0") int uid, @RequestParam(defaultValue = "0") int productSkn) throws GatewayException {
		logger.info("Enter ShopsFavoriteController.delUidProductFav. param uid is {}, productSkn is {}", uid, productSkn);

		// 参数检测
		if (uid < 1 || productSkn < 1) {
			return new ApiResponse.ApiResponseBuilder().code(400).message("uid or productSkn is null").build();
		}

		// 查询商品
		ProductBo product = this.getProductBoByProductSkn(productSkn);
		if (product == null || product.getId() == null || product.getId() <= 0) {
			return new ApiResponse.ApiResponseBuilder().code(404).message("product is not exist").build();
		}

		// 调用取消收藏的服务
		FavoriteRequest cancelRequest = new FavoriteRequest();
		cancelRequest.setUid(uid);
		cancelRequest.setType("product");
		cancelRequest.setFav_id(product.getId());
		logger.info("getUidBrandFav: begin call product.cancelFavorite. request param is {}", cancelRequest);
		VoidResponse cancelResponse = serviceCaller.call(CANCELFAVORITE_URL, cancelRequest, VoidResponse.class);
		logger.info("getUidBrandFav: End call product.cancelFavorite. request param is {}, cancelResponse is {}", cancelRequest, cancelResponse);
		if (cancelResponse != null && cancelResponse.getCode() == 200) {
			return new ApiResponse.ApiResponseBuilder().code(200).message("success").build();
		} else {
			return new ApiResponse.ApiResponseBuilder().code(201).message("faild").build();
		}
	}

	private ProductBo getProductBoByProductSkn(int productSkn) {
		// 根据productSkn获取product详情(捕获service异常直接返回商品不存在)
		BatchBaseRequest<Integer> request = new BatchBaseRequest<Integer>();
		request.setParams(Arrays.asList(productSkn));
		ProductBo[] products = null;
		try {
			products = serviceCaller.call(GET_PRODUCT_BY_SKN, request, ProductBo[].class);
			if (products == null || products.length == 0) {
				return null;
			}
			return products[0];
		} catch (Exception e) {
			return null;
		}
	}

}
