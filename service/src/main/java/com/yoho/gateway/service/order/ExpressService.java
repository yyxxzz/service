package com.yoho.gateway.service.order;


import com.yoho.gateway.model.order.ExpressCompanyVO;
import com.yoho.gateway.model.order.WaybillInfoVO;
import com.yoho.gateway.utils.CalendarUtils;
import com.yoho.service.model.order.model.ExpressCompanyBO;
import com.yoho.service.model.order.model.WaybillInfoBO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * qianjun
 * 2015/11/28
 */
@Service
public class ExpressService {

    /**
     * 根据给定的公司列表进行分组
     * 获得公司列表进行分组后VO
     *
     */
    public Map<String,List<ExpressCompanyVO>> getExpressCompanyVO(List<ExpressCompanyBO> expressCompanyBOList){
        Map<String,List<ExpressCompanyBO>> expressCompanyBOListMap=groupExpressCompany(expressCompanyBOList);
        Map<String,List<ExpressCompanyVO>> expressCompanyVOListMap=new LinkedHashMap<>();
        List<ExpressCompanyVO> expressCompanyVOList=new ArrayList<>();
        for(Map.Entry<String,List<ExpressCompanyBO>> expressCompanyBOListMapEntry:expressCompanyBOListMap.entrySet()){
            String companyAlif=expressCompanyBOListMapEntry.getKey();
            expressCompanyBOList=expressCompanyBOListMapEntry.getValue();
            int i=0;
            for(ExpressCompanyBO expressCompanyBO:expressCompanyBOList){
                String _companyNameSubstr= expressCompanyBO.getCompanyName().substring(1,2);
                String companyNameSubstr=expressCompanyBO.getCompanyName();
                int companyNameAlif=-1;
                try {
                    companyNameAlif=Integer.valueOf(_companyNameSubstr);
                }catch (Exception ex){
                    ex.getMessage();
                }

                if(companyNameAlif== 95 || (companyNameAlif>= 48 && companyNameAlif <= 57) || (companyNameAlif>= 97 && companyNameAlif<= 122) || (companyNameAlif>= 65 && companyNameAlif<= 90)){
                    companyNameSubstr=_companyNameSubstr;
                }
                ExpressCompanyVO expressCompanyVO=new ExpressCompanyVO();
                expressCompanyVO.setId(expressCompanyBO.getId());
                expressCompanyVO.setCompanyName(expressCompanyBO.getCompanyName());
                expressCompanyVO.setCompanyAlif(expressCompanyBO.getCompanyAlif());
                expressCompanyVO.setCompanyNameSubstr(companyNameSubstr);
                expressCompanyVO.setCompanyCode(expressCompanyBO.getCompanyCode());
                if(i==0){
                    expressCompanyVO.setIsFirst("Y");
                    i++;
                }else {
                    expressCompanyVO.setIsFirst("N");
                }
                String companyAlifLower=expressCompanyBO.getCompanyAlif().toLowerCase().trim();
                if(StringUtils.isEmpty(companyAlifLower) && Integer.valueOf(companyAlifLower)<0){
                    continue;
                }
                if(companyAlif.matches("\\d+")){
                    expressCompanyVOList.add(expressCompanyVO);
                    continue;
                }
                String companyAlifUpper=expressCompanyBO.getCompanyAlif().toUpperCase().trim();
                expressCompanyVOList.add(expressCompanyVO);
            }
            expressCompanyVOListMap.put(companyAlif,expressCompanyVOList);
            expressCompanyVOList=new ArrayList<>();
        }
      return  expressCompanyVOListMap;
    }

    /**
     * 根据给定的公司列表进行分组
     */
    public Map<String,List<ExpressCompanyBO>> groupExpressCompany(List<ExpressCompanyBO> expressCompanyBOList){
        Map<String,List<ExpressCompanyBO>> expressCompanyBOListMap=new LinkedHashMap<>();
        List<ExpressCompanyBO> groupExpressCompanyBOList=new ArrayList<>();
        String[] companyAlifTable=new String[]{ "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        for(String companyAlif:companyAlifTable){
            for(int i=0;i<expressCompanyBOList.size();i++){
                if(companyAlif.equalsIgnoreCase(expressCompanyBOList.get(i).getCompanyAlif())){
                    groupExpressCompanyBOList.add(expressCompanyBOList.get(i));
                }
            }
            if(CollectionUtils.isNotEmpty(groupExpressCompanyBOList))
                expressCompanyBOListMap.put(companyAlif,groupExpressCompanyBOList);
            groupExpressCompanyBOList=new ArrayList<>();
        }
        return expressCompanyBOListMap;
    }

    /**
     * 获得用户退货物流信息VO
     */
    public List<WaybillInfoVO> getRefundExpressVO(List<WaybillInfoBO> waybillInfoBOList){
        List<WaybillInfoVO> waybillInfoVOList=new ArrayList<>();
        WaybillInfoVO waybillInfoVO=null;
        if(CollectionUtils.isNotEmpty(waybillInfoBOList)){
            for(WaybillInfoBO waybillInfoBO:waybillInfoBOList){
                waybillInfoVO=new WaybillInfoVO();
                waybillInfoVO.setAcceptTime(CalendarUtils.parseformatSeconds(waybillInfoBO.getCreateTime(), CalendarUtils.LONG_FORMAT_LINE));
                waybillInfoVO.setAcceptAddress(waybillInfoBO.getAddressInfo());
                waybillInfoVO.setExpressId(waybillInfoBO.getLogisticsType());
                waybillInfoVO.setExpressNumber(waybillInfoBO.getWaybillCode());
                waybillInfoVO.setOrderCode(waybillInfoBO.getOrderCode());
                waybillInfoVOList.add(waybillInfoVO);
            }
        }
        return waybillInfoVOList;
    }
}















