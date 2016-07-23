package com.yoho.gateway.model.search;

import org.apache.commons.lang3.StringUtils;

/**
 * 调用搜索接口的入参对象
 * 
 * @author mali
 *
 */
public class ProductSearchReq {
	private String clientType;
	private String type;
	private Integer limit;
	private String sort;
	private Integer page;
	private String yhChannel;
	private String order;
	private String userId;
	private String gender;
	private String vdt;
	private String udid;
	
	/**
	 * 年龄层
	 */
	private String ageLevel;

    /**
     * actTemp 活动模板
     */
    private String actTemp;


	/**
	 * 新品到着的标签标识  1   2   3
	 */
	private String dayLimit;
	
	/**
	 * 搜索的参数名称，每页展示条数
	*/
	private Integer viewNum;

	/**
	 * 产品状态
	 */
	private Integer status;

	/**
	 * 库存
	 */
	private Integer stocknumber;


	/**
	 * outlets=1 奥莱商品 outlets=2非奥莱商品
	 *
	 */
	private Integer outlets;

	private Integer attributeNot;

	private Integer needSmallSort;

	/**
	 * 是否需要查询过滤条件
	 */
	private String needFilter;

	/**
	 * 请求参数，品牌ID（多个以逗号分隔）
	 */
	private String brand;

	/**
	 * 请求参数，店铺ID（多个以逗号分隔）
	 */
	private String shop;

	/**
	 * 请求参数，颜色ID（多个以逗号分隔）
	 */
	private String color;

	/**
	 * 请求参数，尺寸ID（多个以逗号分隔）
	 */
	private String size;

	/**
	 * 请求参数，价格区间（以逗号分隔）
	 */
	private String price;

	/**
	 * 请求参数，打折区间（以逗号分隔）
	 */
	private String pd;

	/**
	 * 上架时间区间，如shelve_time =1420041600,1420473600
	 */
	private String shelveTime;

	/**
	 * 首次上架时间区间，如first_shelve_time =1420041600,1420473600
	 */
	private String firstShelveTime;
	
	/**
	 * 中分类Id
	 */
	private String msort;
	
	private String misort;
	
	/**
	 * 促销ID
	 */
	private String promotion;

	
	/**
	 * not_字段名，过滤字段如：not_productSkn=50019303多个值以逗号分隔
	 */
	private String notProductSkn;
	
	/**
	 * 关键词查询条件
	 */
	private String query;
	/**
	 * 来源于那个搜索词列表
	 */
	private String from;
	
	private String[] parameter;
	
	/**
	 * breaking=1断码商品
	 */
	private String breaking;
	
	/**
	 * filter_saleAction=1折扣专场的商品
	 */
	private String filterSaleAction;
	/**
	 * 商品池
	 */
	private String productPool;
	/**
	 * 打折商品(非正价)
	 */
	private String isdiscount;
	
	/**
	 * 流量来源
	 */
	private String searchFrom;
	
	/**
	 * 二级分类ID
	 */
	private String categoryId;
	
	/**
	 * 二级分类下的子ID
	 */
	private String subCategoryId;

    /**
     * app版本号
     */
    private String appVersion;

    /**
     * 是否包含全球购商品 Y：包含 N：不包含
     * 默认包含
     */
    private String includeGlobal = "Y";


    public String getSearchFrom() {
		return searchFrom;
	}

	private Integer storageNum;
	public ProductSearchReq setSearchFrom(String searchFrom) {
		this.searchFrom = searchFrom;
		return this;
	}

	public String getIsdiscount() {
		return isdiscount;
	}

	public ProductSearchReq setIsdiscount(String isdiscount) {
		this.isdiscount = isdiscount;
		return this;
	}

	public String getProductPool() {
		return productPool;
	}

	public ProductSearchReq setProductPool(String productPool) {
		this.productPool = productPool;
		return this;
	}

	public String getVdt() {
		return vdt;
	}

	public ProductSearchReq setVdt(String vdt) {
		this.vdt = vdt;
		return this;
	}
	
	public String getCategoryId() {
		return categoryId;
	}

	public String getSubCategoryId() {
		return subCategoryId;
	}


	public String getBreaking() {
		return breaking;
	}

	public ProductSearchReq setBreaking(String breaking) {
		this.breaking = breaking;
		return this;
	}

	public String getFilterSaleAction() {
		return filterSaleAction;
	}

	public ProductSearchReq setFilterSaleAction(String filterSaleAction) {
		this.filterSaleAction = filterSaleAction;
		return this;
	}

	public String[] getParameter() {
		return parameter;
	}

	public String getFrom() {
		return from;
	}

	public ProductSearchReq setParameter(String[] parameter) {
		this.parameter = parameter;
		return this;
	}
	public ProductSearchReq setFrom(String from) {
		this.from = from;
		return this;
	}

	public String getPromotion() {
		return promotion;
	}

	public ProductSearchReq setPromotion(String promotion) {
		this.promotion = promotion;
		return this;
	}
	
	public String getClientType() {
		return clientType;
	}

	public ProductSearchReq setClientType(String clientType) {
		this.clientType = clientType;
		return this;
	}

	public String getType() {
		return type;
	}

	public ProductSearchReq setType(String type) {
		this.type = type;
		return this;
	}

	public Integer getLimit() {
		return limit;
	}

	public ProductSearchReq setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}

	public String getSort() {
		return sort;
	}

	public ProductSearchReq setSort(String sort) {
		this.sort = sort;
		return this;
	}

	public Integer getPage() {
		return page;
	}
	
	public String getMisort() {
		return misort;
	}

	public ProductSearchReq setMisort(String misort) {
		this.misort = misort;
		return this;
	}
	
	public ProductSearchReq setCategoryId(String categoryId)
	{	
		this.categoryId=categoryId;
		return this;
	}
	
	public ProductSearchReq setSubCategoryId(String subCategoryId)
	{	
		this.subCategoryId=subCategoryId;
		return this;
	}
	
	public ProductSearchReq setPage(Integer page) {
		this.page = page;
		return this;
	}

	public String getYhChannel() {
		return yhChannel;
	}

	public ProductSearchReq setYhChannel(String yhChannel) {
		this.yhChannel = yhChannel;
		return this;
	}

	public String getOrder() {
		return order;
	}

	public ProductSearchReq setOrder(String order) {
		this.order = order;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public ProductSearchReq setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getGender() {
		return gender;
	}

	public ProductSearchReq setGender(String gender) {
		this.gender = gender;
		return this;
	}

	public Integer getViewNum() {
		return viewNum;
	}

	public ProductSearchReq setViewNum(Integer viewNum) {
		this.viewNum = viewNum;
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public ProductSearchReq setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public Integer getStocknumber() {
		return stocknumber;
	}

	public ProductSearchReq setStocknumber(Integer stocknumber) {
		this.stocknumber = stocknumber;
		return this;
	}

	public Integer getOutlets() {
		return outlets;
	}

	public ProductSearchReq setOutlets(Integer outlets) {
		this.outlets = outlets;
		return this;
	}

	public Integer getAttributeNot() {
		return attributeNot;
	}

	public ProductSearchReq setAttributeNot(Integer attributeNot) {
		this.attributeNot = attributeNot;
		return this;
	}

	public Integer getNeedSmallSort() {
		return needSmallSort;
	}

	public ProductSearchReq setNeedSmallSort(Integer needSmallSort) {
		this.needSmallSort = needSmallSort;
		return this;
	}

	public String getNeedFilter() {
		return needFilter;
	}

	public ProductSearchReq setNeedFilter(String needFilter) {
		this.needFilter = needFilter;
		return this;
	}

	public String getBrand() {
		return brand;
	}

	public ProductSearchReq setBrand(String brand) {
		this.brand = brand;
		return this;
	}

	public String getColor() {
		return color;
	}

	public ProductSearchReq setColor(String color) {
		this.color = color;
		return this;
	}

	public String getSize() {
		return size;
	}

	public ProductSearchReq setSize(String size) {
		this.size = size;
		return this;
	}

	public String getPrice() {
		return price;
	}

	public ProductSearchReq setPrice(String price) {
		this.price = price;
		return this;
	}

	public String getMsort() {
		return msort;
	}

	public ProductSearchReq setMsort(String msort) {
		this.msort = msort;
		return this;
	}

	public String getQuery() {
		return query;
	}

	public ProductSearchReq setQuery(String query) {
		this.query = query;
		return this;
	}

	@Override
	public String toString() {
		return "ProductSearchReq [clientType=" + clientType + ", type=" + type
				+ ", limit=" + limit + ", sort=" + sort + ", page=" + page
				+ ", yhChannel=" + yhChannel + ", order=" + order + ", userId="
				+ userId + ", gender=" + gender + ", viewNum=" + viewNum
				+ ", status=" + status + ", stocknumber=" + stocknumber
				+ ", outlets=" + outlets + ", attributeNot=" + attributeNot
				+ ", needSmallSort=" + needSmallSort + ", needFilter="
				+ needFilter + ", brand=" + brand + ", shop=" + shop +", color=" + color
				+ ", size=" + size + ", price=" + price + ", pd=" + pd
				+ ", shelveTime=" + shelveTime + ", firstShelveTime="
				+ firstShelveTime + ", msort=" + msort + ", misort=" + misort
				+ ", promotion=" + promotion + ", dayLimit="
				+ dayLimit  + ", notProductSkn="
				+ notProductSkn + ", query=" + query + ", actTemp="
	                + actTemp + "]";
	}

	public String getFirstShelveTime() {
		return firstShelveTime;
	}

	public ProductSearchReq setFirstShelveTime(String firstShelveTime) {
		this.firstShelveTime = firstShelveTime;
		return this;
	}

	public String getPd() {
		return pd;
	}

	public ProductSearchReq setPd(String pd) {
		this.pd = pd;
		return this;
	}

	public String getShelveTime() {
		return shelveTime;
	}

	public ProductSearchReq setShelveTime(String shelveTime) {
		this.shelveTime = shelveTime;
		return this;
	}

	public String getNotProductSkn() {
		return notProductSkn;
	}

	public ProductSearchReq setNotProductSkn(String notProductSkn) {
		this.notProductSkn = notProductSkn;
		return this;
	}

	public String getDayLimit() {
		return dayLimit;
	}

	public ProductSearchReq setDayLimit(String dayLimit) {
		this.dayLimit = dayLimit;
		return this;
	}

	public ProductSearchReq setQueryAndBrand(String query, String brand) {
		if (StringUtils.isNotEmpty(brand)) {
			
		}
		return null;
	}

	public Integer getStorageNum() {
		return storageNum;
	}

	public ProductSearchReq setStorageNum(Integer storageNum) {
		this.storageNum = storageNum;
		return this;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}
	public String getShop() {
		return shop;
	}

	public ProductSearchReq setShop(String shop) {
		this.shop = shop;
		return this;
	}

    public ProductSearchReq setActTemp(String actTemp)
    {
        this.actTemp = actTemp;
        return this;
    }

    public String getActTemp()
    {
        return actTemp;
    }
    
    public ProductSearchReq setAgeLevel(String ageLevel)
    {
        this.ageLevel = ageLevel;
        return this;
    }

    public String getAgeLevel()
    {
        return ageLevel;
    }

    public ProductSearchReq setAppVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getIncludeGlobal() {
        return includeGlobal;
    }

    public ProductSearchReq setIncludeGlobal(String includeGlobal) {
        this.includeGlobal = includeGlobal;
        return this;
    }
}
