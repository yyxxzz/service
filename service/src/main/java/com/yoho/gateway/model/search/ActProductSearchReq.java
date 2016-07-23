package com.yoho.gateway.model.search;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

/**
 * 活动模板商品搜索信息
 * 
 * @author 蔡青青
 * @version [版本号, 2016年4月13日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ActProductSearchReq
{
    
    /**
     * actTemp 活动模板
     */
    private String actTemp;
    
    /**
     * order
     */
    private String order;
    
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
     * needSmallSort
     */
    private Integer needSmallSort;
    
    /**
     * 是否需要查询过滤条件
     */
    private String needFilter;
    
    /**
     * 来源于那个搜索词列表
     */
    private String from;
    
    /**
     * parameter
     */
    private String[] parameter;
    
    /**
     * 流量来源
     */
    private String searchFrom;
    
    public String getSearchFrom()
    {
        return searchFrom;
    }
    
    public ActProductSearchReq setSearchFrom(String searchFrom)
    {
        this.searchFrom = searchFrom;
        return this;
    }
    
    public String[] getParameter()
    {
        return parameter;
    }
    
    public String getFrom()
    {
        return from;
    }
    
    public ActProductSearchReq setParameter(String[] parameter)
    {
        this.parameter = parameter;
        return this;
    }
    
    public ActProductSearchReq setFrom(String from)
    {
        this.from = from;
        return this;
    }
    
    public String getOrder()
    {
        return order;
    }
    
    public ActProductSearchReq setOrder(String order)
    {
        this.order = order;
        return this;
    }
    
    public Integer getViewNum()
    {
        return viewNum;
    }
    
    public ActProductSearchReq setViewNum(Integer viewNum)
    {
        this.viewNum = viewNum;
        return this;
    }
    
    public Integer getStatus()
    {
        return status;
    }
    
    public ActProductSearchReq setStatus(Integer status)
    {
        this.status = status;
        return this;
    }
    
    public Integer getStocknumber()
    {
        return stocknumber;
    }
    
    public ActProductSearchReq setStocknumber(Integer stocknumber)
    {
        this.stocknumber = stocknumber;
        return this;
    }
    
    public Integer getNeedSmallSort()
    {
        return needSmallSort;
    }
    
    public ActProductSearchReq setNeedSmallSort(Integer needSmallSort)
    {
        this.needSmallSort = needSmallSort;
        return this;
    }
    
    public String getNeedFilter()
    {
        return needFilter;
    }
    
    public ActProductSearchReq setNeedFilter(String needFilter)
    {
        this.needFilter = needFilter;
        return this;
    }
    
    public ActProductSearchReq setActTemp(String actTemp)
    {
        this.actTemp = actTemp;
        return this;
    }
    
    public String getActTemp()
    {
        return actTemp;
    }

    /**
     * 重载方法
     * @return
     */
    @Override
    public String toString()
    {
        return "ActProductSearchReq [actTemp=" + actTemp + ", order=" + order + ", viewNum=" + viewNum + ", status="
            + status + ", stocknumber=" + stocknumber + ", needSmallSort=" + needSmallSort + ", needFilter="
            + needFilter + ", from=" + from + ", parameter=" + Arrays.toString(parameter) + ", searchFrom=" + searchFrom
            + "]";
    }
    
}
