package com.yoho.gateway.controller.product.convert;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.yoho.gateway.model.product.LimitProductVo;
import com.yoho.gateway.model.product.ReminderVo;
import com.yoho.product.model.LimitProductAttachBo;
import com.yoho.product.model.LimitProductBo;
import com.yoho.product.model.ReminderBo;

@Service
public class LimitProductConvert {
	
	public List<LimitProductVo> convert(LimitProductBo[] limitProductBoList)
	{
		if(null==limitProductBoList||limitProductBoList.length<=0)
		{
			return Lists.newArrayList();
		}
		List<LimitProductVo> limitProductVoList=Lists.newArrayList();
		for (LimitProductBo limitProductBo : limitProductBoList) {
			LimitProductVo limitProductVo=this.convert(limitProductBo);
			//开售时间 不带年
			limitProductVo.setSaleTime(new DateAppender().appendMonth(limitProductBo.getSaleTime())
					.appendDay(limitProductBo.getSaleTime(),limitProductBo.getDayFlag()).toBuilder());
			limitProductVoList.add(limitProductVo);
		}
		return limitProductVoList;
	}
	
	
	public LimitProductVo convert(LimitProductBo limitProductBo)
	{
		if(null==limitProductBo)
		{
			return null;
		}
		LimitProductVo limitProductVo=new LimitProductVo();
		limitProductVo.setBatchNo(limitProductBo.getBatchNo());
		limitProductVo.setDefaultUrl(buildDefaultUrl(limitProductBo.getAttachment()));
		limitProductVo.setDescription(limitProductBo.getDescription());
		limitProductVo.setHotFlag(limitProductBo.getHotFlag());
		limitProductVo.setId(limitProductBo.getId());
		limitProductVo.setLimitProductCode(limitProductBo.getLimitProductCode());
		limitProductVo.setPrice(limitProductBo.getPrice());
		limitProductVo.setProductName(limitProductBo.getProductName());
		limitProductVo.setProductSkn(limitProductBo.getProductSkn());
		//开售时间 带年
		limitProductVo.setSaleTime(new DateAppender().appendYear(limitProductBo.getSaleTime())
				.appendMonth(limitProductBo.getSaleTime())
				.appendDay(limitProductBo.getSaleTime(),limitProductBo.getDayFlag()).toBuilder());
		
		limitProductVo.setOldSaleTime(limitProductBo.getSaleTime());
		limitProductVo.setShowFlag(limitProductBo.getShowFlag());
		limitProductVo.setActivityId(limitProductBo.getActivityId());
		limitProductVo.setLimitProductType(limitProductBo.getLimitProductType());
		limitProductVo.setOrderBy(limitProductBo.getOrderBy());
		limitProductVo.setAttachment(limitProductBo.getAttachment());
		return limitProductVo;
	}


	public List<ReminderVo> convert(ReminderBo[] reminderBoList)
	{
		if(null==reminderBoList||reminderBoList.length<=0)
		{
			return Lists.newArrayList();
		}
		List<ReminderVo> reminderVoList=Lists.newArrayList();
		ReminderVo reminderVo=null;
		for (ReminderBo reminderBo : reminderBoList) {
			reminderVo=new ReminderVo();
			reminderVo.setDefaultUrl(reminderBo.getDefaultImage());
			reminderVo.setLimitProductCode(reminderBo.getLimitProductCode());
			reminderVo.setPrice(reminderBo.getPrice());
			reminderVo.setProductName(reminderBo.getProductName());
			reminderVo.setReminderId(reminderBo.getReminderId());
			reminderVo.setProductSkn(reminderBo.getProductSkn());
			reminderVo.setOrder(getOrder(reminderBo.getSaleTime(), reminderBo.getDayFlag()));
			reminderVo.setSaleTime(new DateAppender()
					.appendMonth(reminderBo.getSaleTime())
					.appendDay(reminderBo.getSaleTime(),reminderBo.getDayFlag()).toBuilder());
			
			reminderVoList.add(reminderVo);
		}
		//进行排序：有日期的按正序排列，无日期只有月份排在有日期的后面，例如3月12日排在3月11日后面，3月排在3月12日后面
		sortReminderVoList(reminderVoList);
		return reminderVoList;
	}
	
	/**
	 * 构造排序No,3月12日order是312,3月则为332
	 * @param saleTime
	 * @return
	 */
	private Integer getOrder(Integer saleTime, Integer dayFlag){
		String saleTimeStr = new DateAppender().appendYear(saleTime)
		.appendMonth(saleTime)
		.appendDay(saleTime,dayFlag).toBuilder();
		
		String orderY = saleTimeStr.split("年")[0];
		String orderM = (saleTimeStr.split("年")[1]).split("月")[0];
		orderM = orderM.length()==1 ? "0" + orderM : orderM;
		
		String orderD = "32";
		if(saleTimeStr.contains("日")){
			orderD = ((saleTimeStr.split("年")[1]).split("月")[1]).split("日")[0];
			orderD = orderD.length()==1 ? "0" + orderD : orderD;
			
		}
		
		String order = orderY + orderM + orderD;
		
		return Integer.parseInt(order);
	}
	
	private void sortReminderVoList(List<ReminderVo> reminderVoList){
		Collections.sort(reminderVoList, new Comparator<ReminderVo>() {
            public int compare(ReminderVo arg0, ReminderVo arg1) {
                return arg0.getOrder().compareTo(arg1.getOrder());
            }
        });
	}
	
	
	
	/**
	 * 
	 * @author xieyong
	 *
	 */
	public static class DateAppender
	{	
		private StringBuilder builder;
		
		public DateAppender()
		{
			this.builder=new StringBuilder();
		}
		
		public DateAppender appendYear(Integer saleTime)
		{	
			if(null==saleTime)
			{
				return this;
			}
			int year=getDateOfYear(saleTime.longValue());
			builder.append(year).append("年");
			return this;
		}
		
		public DateAppender appendMonth(Integer saleTime)
		{	
			if(null==saleTime)
			{
				return this;
			}
			int month=getDateOfMonth(saleTime.longValue());
			builder.append(month).append("月");
			return this;
		}
		
		public DateAppender appendDay(Integer saleTime,Integer dayFlag)
		{	
			if(null==saleTime)
			{
				return this;
			}
			if(dayFlag==null){
				dayFlag = 1;// 默认展示日
			}
			int day=getDateOfDay(saleTime.longValue());
			if(dayFlag==1){
				builder.append(day).append("日");
			}
			return this;
		}
		
		public String toBuilder()
		{
			return builder.toString();
		}
	}
	
	

	/**
	 * 构造默认封面图
	 * @param list 
	 * @return
	 */
	private String buildDefaultUrl(List<LimitProductAttachBo> limitProductAttachBoList) {
		if(CollectionUtils.isEmpty(limitProductAttachBoList))
		{
			return StringUtils.EMPTY;
		}
		for (LimitProductAttachBo limitProductAttachBo : limitProductAttachBoList) {
			if(1==limitProductAttachBo.getIsDefault())
			{
				return limitProductAttachBo.getAttachUrl();
			}
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * 不转UTC时间计算
	 * @param time  获取数据库中的UNIX_Time(该时间是距离1970年的秒数，在转换过程中先要换算成毫秒)中的月份信息
	 * @return
	 */
	private static int getDateOfMonth(long time)
	{	
		Calendar c=Calendar.getInstance();
		c.setTime(new Date (time*1000));
		return c.get(Calendar.MONTH)+1;
	}
	
	/**
	 * 不转UTC时间计算
	 * @param time  获取数据库中的UNIX_Time(该时间是距离1970年的秒数，在转换过程中先要换算成毫秒)中的年信息
	 * @return
	 */
	private static int getDateOfYear(long time)
	{	
		Calendar c=Calendar.getInstance();
		c.setTime(new Date (time*1000));
		return c.get(Calendar.YEAR);
	}
	
	/**
	 * 不转UTC时间计算
	 * @param time 获取数据库中的UNIX_Time(该时间是距离1970年的秒数，在转换过程中先要换算成毫秒)中的天信息 
	 * @return
	 */
	private static int getDateOfDay(long time)
	{	
		Calendar c=Calendar.getInstance();
		c.setTime(new Date (time*1000));
		return c.get(Calendar.DAY_OF_MONTH);
	}
}
