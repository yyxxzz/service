package com.yoho.gateway.service.assist;

/**
 * Created by sailing on 2015/11/24.
 */
public final class SearchConstants {
	public interface NodeConstants {
		String FILTER_KEY_GENDER = "gender";
	    
	    /**
	     * brand
	     */
	    String FILTER_KEY_BRAND = "brand";
	    
	    /**
	     * shop
	     */
	    String FILTER_KEY_SHOP = "shop";
	    
	    String KEY_ORDER = "order";
	    String KEY_STOCKNUMBER = "stocknumber";
	    String KEY_NEEDFILTER = "needFilter";

	    Double VIP1_PRICE=0.95;
	    Double VIP2_PRICE=0.9;
	    Double VIP3_PRICE=0.88;
	    /**
	     * 过滤条件的根节点名称
	     * filter
	     */
	    String KEY_FILTER = "filter";
	    
	    /**
	     * size
	     */
	    String FILTER_KEY_SIZE = "size";
	    
	    /**
	     * color
	     */
	    String FILTER_KEY_COLOR = "color";
	    
	    /**
	     * 过滤条件的价格区间
	     * price
	     */
	    String FILTER_KEY_PRICE = "price";
	    
	    /**
	     * 过滤条件的价格区间
	     * priceRange
	     */
	    String FILTER_KEY_PRICERANGE = "priceRange";
	    
	    /**
	     * 过滤条件的年龄层
	     */
	    String FILTER_KEY_AGELEVEL = "ageLevel";
	    
	    /**
	     * product_list
	     */
	    String KEY_PRODUCT_LIST = "product_list";
	    
	    /**
	     * discount
	     */
	    String KEY_DISCOUNT = "discount";
	    
	    /**
	     * data
	     */
	    String KEY_DATA = "data";
	    /**
	     * 断码区节点
	     */
	    String KEY_BREAKING_SIZE = "breaking_size";
	    
	    /**
	     * group_sort
	     */
	    String KEY_GROUP_SORT = "group_sort";
	    
	    /**
	     * brand_ico
	     */
	    String KEY_BRAND_ICO = "brand_ico";
	    
	    /**
	     * 商品tag，新品
	     */
	    String PRODUCT_TAG_IS_NEW = "is_new";
	    
		/**
		 * 商品tag  折扣
		 */
	    String PRODUCT_TAG_IS_DISCOUNT = "is_discount";
	}
	
    
	
    public interface IndexNameConstant {
    	String SEPERATOR_COMMA = ",";
    	
    	//是否上架 1 或 2
    	int PRD_STATUS_USE = 1;
        //默认库存数
    	int DEFAULT_STOCKNUMBER = 1;

        // outlets=1 奥莱商品 outlets=2非奥莱商品
    	int DEFAULT_OUTLETS = 2;
        
    	int DEFAULT_ATTRIBUTE_NOT = 2;
        //默认记录数
    	int DEFAULT_VIEWNUM = 20;
    	//默认记录数
    	int DEFAULT_PAGE = 1;
    	//默认记录数
    	String DEFAULT_NEEDFILTER = "1";
    	//sale专区判断是否为断码，折扣的默认值
    	String DEFAULT_SALL_FILTER = "1";
    	/**
    	 * 品牌ID查多个品牌以逗号分隔
    	 */
    	String BRAND_ID="brand";
    	
    	/**
    	 * 性别1-男,2-女,3-通用
    	 */
    	String GENDER="gender";
    	
    	/**
    	 * 库存量如stocknumber=2，则过滤出库存量>=2的商品
    	 */
    	public final static String STORAGE_NUMBER="storage_num";
    	String STOCKNUMBER="stocknumber";
    	/**
    	 * 商品池 id
    	 */
    	String PRODUCT_POOL = "filter_poolId";
    	
    	/**
    	 * 
    	 */
    	String OUTLETS="outlets";
    	/**
    	 * 会员专区
    	 */
    	String VDT="vdt";
    	/**
    	 * sale专区默认值 1 
    	 */
    	String SALE_DEFAULT_VALUE="1";
    	/**
    	 * sale专区 会员专享非正价商品默认值 Y
    	 */
    	String SALE_IS_DISCOUNT="isdiscount";
    	/**
	     * 断码区
	     */
	    String KEY_BREACKING = "breaking";
	    /**
	     * 折扣专场
	     */
	    String KEY_FILTER_SALEACTION = "filter_saleAction";
    	String FROM="from";
    	
    	/**
    	 * 过滤商品属性，attribute_not=2过滤掉赠品
    	 */
    	String ATTRIBUTE_NOT="attribute_not";
    	
    	/**
    	 * 时间区间起始时间以逗号分隔，如查询20150101-20150106 “shelve_time =1420041600,1420473600”
    	 */
    	String SHELVE_TIME="shelve_time";
    	
    	/**
    	 * 每页记录数默认为10
    	 */
    	String VIEWNUM="viewNum";
    	
    	/**
    	 * 每页记录数默认为10
    	 */
    	String PAGE="page";
    	
    	/**
    	 * 产品大分类ID查多个大类以逗号分隔
    	 *
    	 */
    	String MSORT="msort";
    	
    	/**
    	 * 是否上架 1 或 2
    	 */
    	String STATUS="status";

    	/**
    	 * 查询关键字
    	 */
    	String QUERY="query";
    	
    	/**
    	 * 查询关键字
    	 */
    	String NEEDFILTER="needFilter";
    	
    	/**
    	 * 排序信息
    	 */
		String ORDER = "order";
		
		/**
		 * 年龄层
		 */
		String AGE_LEVEL = "ageLevel";
		
		/**
		 * sort
		 */
		String SORT = "sort";
		
		/**
		 * new
		 */
		String NEW = "new";
		
		/**
		 * 是新品
		 */
		String IS_NEW = "Y";

		/**
		 * brand
		 */
		String BRAND = "brand";

		/**
		 * shop
		 */
		String SHOP = "shop";
		
		/**
		 * size
		 */
		String SIZE = "size";
		
		/**
		 * color
		 */
		String COLOR = "color";
		
		/**
		 * price
		 */
		String PRICE = "price";

		/**
		 * promotion
		 */
		String PROMOTION = "promotion";
		
		/**
		 * pd 折扣区间
		 */
		String PD = "p_d";
		
		/**
		 * 产品中分类ID
		 */
		String MISORT = "misort";
		
		/**
		 * shelve_time 上架时间
		 */
		String SHELVETIME = "shelve_time";
		/**
		 * 查询参数列表
		 */
		String PARAMETER = "parameter_";
		
		/**
		 * 首次上架时间
		 */
		String FIRSTSHELVETIME = "first_shelve_time";
		
		/**
		 * 除去这些skn的商品列表查询
		 */
		String NOTPRODUCTSKN = "not_productSkn";
		
		/**
		 * needSmallSort
		 */
		String NEEDSMALLSORT = "needSmallSort";

		/**
		 * ACTTEMP
		 */
		String ACTTEMP = "act_temp";
		
		/**
		 * 需要小分类
		 */
		String NEEDSMALLSORT_YES = "1";
		
		
		/**
		 *  自主品牌的检索条件分页信息limit
		 */
		String LIMIT = "limit";
		
		/**
		 *  自主品牌的检索条件分页信息默认 8
		 */
		Integer DEFAULT_LIMIT = 8;

		/**
		 * 商品参数数量
		 */
		String PARAMNUM = "paramNum";
		/**
		 * ACT TEMP
		 */
		String ACT_TEMP = "act_temp";
        /**
         * 是否包含全球购商品
         */
        String CONTAIN_GLOBAL="contain_global";
        
        /**
    	 * 折扣比
    	 */
    	String FILTER_PROMOTIONDISCOUNT="filter_promotionDiscount";
    }
}
