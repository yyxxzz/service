package com.yoho.gateway.controller.search.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.yoho.gateway.model.search.SalesCategoryRsp;
import com.yoho.gateway.model.search.SalesCategoryVo;
import com.yoho.product.model.SalesCategoryBo;

/**
 * 销售分类的BO转换成Vo对象接口
 * @author mali
 *
 */
@Service
public class DefaultSalesCategoryConvert implements SalesCategoryConvert{
	/**
	 * 销售分类的BO转换成Vo对象
	 * @param data BO
	 * @return Vo对象
	 */
	@Override
	public SalesCategoryRsp convert(Map<Integer, SalesCategoryBo> data) {
		SalesCategoryRsp rsp = new SalesCategoryRsp();
		if (null != data) {
			Map<String, List<SalesCategoryVo>> dataVo = new LinkedHashMap<String, List<SalesCategoryVo>>();
			for(Integer key :  data.keySet()) {
				SalesCategoryBo salesCategoryBo = data.get(key);
				List<SalesCategoryBo> subTow = salesCategoryBo.getSub();
				if (CollectionUtils.isNotEmpty(subTow)) {
					List<SalesCategoryVo> subTowVo = new ArrayList<SalesCategoryVo>(subTow.size());
					// 对二级分类的BO-->VO转换
					for (SalesCategoryBo salesCategoryBoSub : subTow) {
						SalesCategoryVo convertToVO = convertToVO(salesCategoryBoSub);
						List<SalesCategoryBo> subThree = salesCategoryBoSub.getSub();
						
						// 三级分类的BO-->VO转换
						if (CollectionUtils.isNotEmpty(subThree)) {
							Set<SalesCategoryVo> subThreeVo = new LinkedHashSet<SalesCategoryVo>(subThree.size());
							for (SalesCategoryBo salesCategoryBoLeaf : subThree) {
								subThreeVo.add(convertToVO(salesCategoryBoLeaf));
							}
							convertToVO.setVoSub(subThreeVo);
						}
						subTowVo.add(convertToVO);
					}
					
					String convertKey = convertKey(key);
					if (null != convertKey) {
						dataVo.put(convertKey(key), subTowVo);		// 构造出参的结构
					}
				}
			}
			
			rsp.setData(dataVo);
		}
		return rsp;
	}
	
	// 男孩、女孩等一级分类的名称转换成对应英文的标识
	private String convertKey(Integer key) {
		switch(key) {
			case 1: return "boy";
			case 2: return "girl";
			case 3: return "kids";
			case 4: return "lifestyle";
			default : return null;
		}
	}
	
	/**
	 * 单个转换
	 * @param //BO-->VO转换
	 * @return
	 */
	@Override
	public SalesCategoryVo convertToVO(SalesCategoryBo bo) {
		SalesCategoryVo vo = null;
		if (null != bo) {
			vo = new SalesCategoryVo();
			vo.setCategoryId(String.valueOf(bo.getCategoryId()));
			vo.setCategoryName(bo.getCategoryName());
			vo.setIcon(bo.getIcon());
			vo.setParentId(String.valueOf(bo.getParentId()));
			Map<String, String> relationParameterMap = new HashMap<String, String>();
			relationParameterMap.put("sort", bo.getRelationParameter());
			vo.setRelationParameterMap(relationParameterMap);
		}
		return vo;
	}
}
