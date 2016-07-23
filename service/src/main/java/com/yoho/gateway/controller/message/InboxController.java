package com.yoho.gateway.controller.message;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.netflix.config.DynamicPropertyFactory;
import com.yoho.gateway.mqmessage.YhProducerTemplateCommon;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.core.common.restbean.ResponseBean;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.GatewayError;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.request.GetInBoxListVO;
import com.yoho.gateway.model.user.Inbox.InboxReqVO;
import com.yoho.service.model.inbox.InBoxModel;
import com.yoho.service.model.inbox.request.GetListReqBO;
import com.yoho.service.model.inbox.request.InboxReqBO;
import com.yoho.service.model.inbox.request.SetIsReadRequestBO;
import com.yoho.service.model.inbox.response.GetListRespBO;
import com.yoho.service.model.response.CommonRspBO;
import com.yoho.service.model.response.PageResponseBO;

/**
 * 消息中心统一管理
 *
 * @author DengXinfei modify by 2016-1-11
 */
@Controller
public class InboxController {

    //默认调用inbox微服务
    public static boolean flag = true;

    private static final String MESSAGE_SUCCESS_INFO = "inbox list";

    private static final String INBOX_TOPIC = "inbox.send_inbox_msg";

    static Logger logger = LoggerFactory.getLogger(InboxController.class);

    @Autowired
    private ServiceCaller serviceCaller;

    @Autowired
    private YhProducerTemplateCommon yhProducerTemplateCommon;

    //获取消息数量UID为空的返回的消息, 以及错误码
    private final static int INBOX_UID_NULL_CODE = 200;
    private final static String INBOX_UID_NULL_MSG = "uid is 0";

    private final static int INBOX_PARAM_ERROR_CODE = 400;

    private final static int CALL_MESSAGE_INBOX_SERVICE_FAIL = 500;

    @RequestMapping(params = "method=inbox.queryListById")
    @ResponseBody
    public ApiResponse queryListById(@RequestParam("uid") Integer uid) {

        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        Map<String, Object> result = null;
        logger.info("inbox switch is {}",flag);
        if (flag) {
            result = serviceCaller.post("brower.queryListById", DynamicPropertyFactory.getInstance().getStringProperty("browse.service.url", "http://localhost:8092/brower/").get() + "/InBoxRest/queryListById", params, Map.class, null).get();
        } else {
            result = serviceCaller.call("message.queryListById", params, Map.class);
        }
        ApiResponse responseBean = new ApiResponse();
        responseBean.setCode(GatewayError.CODE_SUCCESS.getCode());
        responseBean.setMessage(GatewayError.CODE_SUCCESS.getMessage());
        responseBean.setData(result);
        return responseBean;
    }

    @RequestMapping(params = "method=open.message.messageCount")
    @ResponseBody
    public ApiResponse messageCoun(String uid,
                                   String size,
                                   String page,
                                   String gender) throws GatewayException {
        logger.info("enter into method=open.message.messageCount,uid is {},size is{},page is {},gender is{}", uid, size, page, gender);
        if (null == size || "".equals(size)) {
            size = "10";
        }
        if (null == page || "".equals(page)) {
            page = "1";
        }
        if (!isNum(uid) || !isNum(size) || !isNum(page)) {
            logger.debug("params is error,uid {} size {}  page {} must be number ", uid, size, page, gender);
            throw new GatewayException(400, "params uid  size  page must be number");
        }
        if (null == gender || "".equals(gender)) {
            gender = "1,3";
        }
        if (Integer.parseInt(uid) < 1) {
            ApiResponse responseBean = new ApiResponse();
            responseBean.setCode(400);
            responseBean.setMessage("Message Count");
            JSONObject obj = new JSONObject();
            obj.put("count", 0);
            responseBean.setData(obj);
            return responseBean;
        }
        GetListReqBO req = new GetListReqBO();
        req.setSize(Integer.parseInt(size));
        req.setUid(Integer.parseInt(uid));
        req.setPage(Integer.parseInt(page));
        req.setGender(gender);
        ApiResponse result = null;
        logger.info("inbox switch is {}",flag);
        if (flag) {
            result = serviceCaller.post("brower.messageCount", DynamicPropertyFactory.getInstance().getStringProperty("browse.service.url", "http://localhost:8092/brower/").get() + "/InBoxRest/messageCount", req, ApiResponse.class, null).get();
        } else {
            result = serviceCaller.call("message.messageCount", req, ApiResponse.class);
        }
        ApiResponse responseBean = new ApiResponse();
        responseBean.setCode(GatewayError.CODE_SUCCESS.getCode());
        responseBean.setMessage(GatewayError.CODE_SUCCESS.getMessage());
        responseBean.setData(result.getData());
        logger.info("leave method=open.message.messageCount,uid is {},size is{},page is {},gender is{}", uid, size, page, gender);
        return responseBean;
    }

    private static boolean isNum(String s) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(s);
        return isNum.matches();
    }

    /**
     * 功能描述: 给指定用户发送一条站内信
     *
     * @param verify_key
     * @param send_uid
     * @return ApiResponse
     * @throws GatewayException
     * @since 2016.3.15
     */
//    @RequestMapping( "/inbox/service/*/inbox")
    @RequestMapping(params = "method=web.inbox.setSingleMessage")
    @ResponseBody
    public ApiResponse setSingleMessage(InboxReqVO vo) throws GatewayException {
        logger.info("enter into InboxController.setSingleMessage param {} is ", vo);
        //组装请求参数
        InboxReqBO req = new InboxReqBO();
        if (StringUtils.isEmpty(vo.getUid()) || (!vo.getUid().matches("\\d+"))) {
            logger.debug("/inbox/service/v1/inbox uid {} error", vo.getUid());
            throw new GatewayException(INBOX_PARAM_ERROR_CODE, "uid is error");
        }
        int uid = Integer.valueOf(vo.getUid());
        if (isEmpty(vo.getTitle()) && isEmpty(vo.getContent())) {
            logger.debug("/inbox/service/v1/inbox uid {} error", vo.getTitle(), vo.getContent());
            throw new GatewayException(INBOX_PARAM_ERROR_CODE, "title and content can not be null either");
        }
        if (!StringUtils.isEmpty(vo.getVerify_key()) && vo.getVerify_key().matches("\\d+")) {
            req.setVerifyKey(Integer.valueOf(vo.getVerify_key()));
        }
        if (!StringUtils.isEmpty(vo.getSend_uid()) && !vo.getSend_uid().matches(vo.getSend_uid())) {
            logger.debug("/inbox/service/v1/inbox uid {} error", vo.getUid());
            throw new GatewayException(INBOX_PARAM_ERROR_CODE, "sendUid is error");
        }
        if (StringUtils.isEmpty(vo.getSend_uid())) {
            req.setSendUid(0);
        } else {
            req.setSendUid(Integer.valueOf(vo.getSend_uid()));
        }
        if (!StringUtils.isEmpty(vo.getType()) && !vo.getType().matches("\\d+")) {
            logger.debug("/inbox/service/v1/inbox uid {} error", vo.getType());
            throw new GatewayException(INBOX_PARAM_ERROR_CODE, "type is error");
        }
        //组装请求参数
        req.setContent(vo.getContent());
        req.setTitle(vo.getTitle());
        req.setType(vo.getType());
        req.setUid(uid);

        ApiResponse responseBean = new ApiResponse();
        logger.info("bagin call service message.saveInbox {}", req.toString());
        ResponseBean res = serviceCaller.call("message.saveInbox", req, ResponseBean.class);
        if (null == res) {
            logger.debug("call service message.saveInbox fail");
            throw new GatewayException(CALL_MESSAGE_INBOX_SERVICE_FAIL, "call service message.saveInbox fail");
        } else {
            responseBean.setCode(GatewayError.CODE_SUCCESS.getCode());
            responseBean.setMessage(GatewayError.CODE_SUCCESS.getMessage());
        }
        /*logger.info("inbox switch is {}",flag);
        if (flag) {
            //发送站内信mq
            logger.info("begin send inbox mq. topic {},message {}", INBOX_TOPIC, req);
            yhProducerTemplateCommon.send(INBOX_TOPIC, req);
        } else {
            ResponseBean res = serviceCaller.call("message.saveInbox", req, ResponseBean.class);
            if (null == res) {
                logger.debug("call service message.saveInbox fail");
                throw new GatewayException(CALL_MESSAGE_INBOX_SERVICE_FAIL, "call service message.saveInbox fail");
            } else {
                responseBean.setCode(GatewayError.CODE_SUCCESS.getCode());
                responseBean.setMessage(GatewayError.CODE_SUCCESS.getMessage());
            }
        }*/
        logger.info("leave  /inbox/service/v1/inbox,time is {}", System.currentTimeMillis());
        return responseBean;
    }

    @RequestMapping(params = "method=app.inbox.setSingleMessage")
    @ResponseBody
    public ApiResponse saveInbox(InboxReqVO vo) throws GatewayException {
        return setSingleMessage(vo);
    }

    private boolean isEmpty(String o) {
        if (null == o || "".equals(o)) {
            return true;
        }
        return false;
    }

    @RequestMapping(params = "method=inbox.updateInbox")
    @ResponseBody
    public ApiResponse updateInbox(@RequestBody InBoxModel inBox) {
        ResponseBean result = null;
        logger.info("inbox switch is {}",flag);
        if (flag) {
            result = serviceCaller.post("brower.updateInbox", DynamicPropertyFactory.getInstance().getStringProperty("browse.service.url", "http://localhost:8092/brower/").get() + "/InBoxRest/updateInbox", inBox, ResponseBean.class, null).get();
        } else {
            result = serviceCaller.call("message.updateInbox", inBox, ResponseBean.class);
        }
        ApiResponse responseBean = new ApiResponse();
        responseBean.setCode(Integer.parseInt(result.getCode()));
        responseBean.setMessage(result.getMessage());
        return responseBean;
    }

    /**
     * 根据用户id以及消息id列表批量删除消息
     *
     * @param uid
     * @param id
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.inbox.delmessage")
    @ResponseBody
    public ApiResponse delMessage(String uid,
                                  String id) throws GatewayException {
        logger.info("enter into method=app.inbox.delmessage,uid is {} id is {}", uid, id);
        if (null == uid || !isNum(uid)) {
            logger.debug("method=app.inbox.delmessage uid {} error", uid);
            throw new GatewayException(400, "uid is error");
        }
        InboxReqBO req = new InboxReqBO();
        req.setUid(Integer.parseInt(uid));
        req.setIds(id);
        ResponseBean result = null;
        logger.info("inbox switch is {}",flag);
        if (flag) {
            result = serviceCaller.post("brower.delByIdsAndUid", DynamicPropertyFactory.getInstance().getStringProperty("browse.service.url", "http://localhost:8092/brower/").get() + "/InBoxRest/delByIdsAndUid", req, ResponseBean.class, null).get();
        } else {
            result = serviceCaller.call("message.delByIdsAndUid", req, ResponseBean.class);
        }
        ApiResponse responseBean = new ApiResponse();
        responseBean.setCode(Integer.parseInt(result.getCode()));
        responseBean.setMessage(result.getMessage());
        responseBean.setData(result.getData());
        logger.info("leave method=app.inbox.delmessage,uid is {} id is {}", uid, id);
        return responseBean;
    }

    /**
     * 获取用户的消息列表
     *
     * @param vo
     * @return
     * @throws ServiceException
     */
    @RequestMapping(params = "method=app.inbox.getlist")
    @ResponseBody
    public ApiResponse getlist(GetInBoxListVO vo) throws ServiceException {
        logger.info("Begin call getlist gateway. with param is {}", vo);
        GetListReqBO bo = new GetListReqBO();
        BeanUtils.copyProperties(vo, bo);
        PageResponseBO<GetListRespBO> result = null;
        logger.info("inbox switch is {}",flag);
        if (flag) {
            result = serviceCaller.post("brower.getInBoxList", DynamicPropertyFactory.getInstance().getStringProperty("browse.service.url", "http://localhost:8092/brower/").get() + "/InBoxRest/getInBoxList", bo, PageResponseBO.class, null).get();
        } else {
            result = serviceCaller.call("message.getInBoxList", bo, PageResponseBO.class);
        }
        logger.info("call inbox.getlist with param is {}, with result is {}", vo, result);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", result.getList());
        map.put("page_total", result.getPage_total());
        map.put("page", result.getPage());
        map.put("total", result.getTotal());
        ApiResponse response = new ApiResponse.ApiResponseBuilder().message(MESSAGE_SUCCESS_INFO).data(map).build();
        return response;
    }

    /**
     * 获取用户的消息列表
     *
     * @param vo
     * @return
     * @throws ServiceException
     */
    @RequestMapping(params = "method=app.inbox.getlistnew")
    @ResponseBody
    public ApiResponse getlistnew(GetInBoxListVO vo) throws ServiceException {
        logger.info("Begin call getlistnew gateway. with param is {}", vo);
        GetListReqBO bo = new GetListReqBO();
        BeanUtils.copyProperties(vo, bo);
        PageResponseBO<GetListRespBO> result = null;
        logger.info("inbox switch is {}",flag);
        if (flag) {
            result = serviceCaller.post("brower.getInBoxListNew", DynamicPropertyFactory.getInstance().getStringProperty("browse.service.url", "http://localhost:8092/brower/").get() + "/InBoxRest/getInBoxListNew", bo, PageResponseBO.class, null).get();
        } else {
            result = serviceCaller.call("message.getInBoxListNew", bo, PageResponseBO.class);
        }
        logger.info("call inbox.getlistnew with param is {}, with result is {}", vo, result);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", result.getList());
        map.put("page_total", result.getPage_total());
        map.put("page", result.getPage());
        map.put("total", result.getTotal());
        ApiResponse response = new ApiResponse.ApiResponseBuilder().message(MESSAGE_SUCCESS_INFO).data(map).build();
        return response;
    }

    /**
     * 获取消息的数量, 根据is_read条件过滤.
     *
     * @param is_read Y: 代表已读消息, N:未读消息
     * @param uid     用户UID
     * @return
     */
    @RequestMapping(params = "method=app.inbox.getTotal")
    @ResponseBody
    public ApiResponse getTotal(@RequestParam("is_read") String is_read, @RequestParam("uid") int uid) throws GatewayException {
        logger.info("Begin call getTotal: is_read is {}, uid is {}", is_read, uid);
        //(1)判断用户的uid是否不存在, 或者值为0
        if (uid < 1) {
            logger.warn("getTotal: uid is {}. is_read is {}", uid, is_read);
            throw new GatewayException(INBOX_UID_NULL_CODE, INBOX_UID_NULL_MSG);
        }
        //(2)调用接口返回消息数量
        InboxReqBO inboxReqBO = new InboxReqBO(uid, is_read);
        int count = 0;
        logger.info("inbox switch is {}",flag);
        if (flag) {
            count = serviceCaller.post("brower.getInboxCount", DynamicPropertyFactory.getInstance().getStringProperty("browse.service.url", "http://localhost:8092/brower/").get() + "/InBoxRest/getInboxCount", inboxReqBO, Integer.class, null).get();
        } else {
            count = serviceCaller.call("message.getInboxCount", inboxReqBO, Integer.class);
        }

        //(3)组装消息返回
        Map<String, String> data = new HashMap<String, String>();
        data.put("total", String.valueOf(count));
        ApiResponse response = new ApiResponse.ApiResponseBuilder().code(200).message("total").data(data).build();
        return response;
    }

    /**
     * 批量设置消息为已读
     *
     * @param uid
     * @param ids
     * @return
     */
    @RequestMapping(params = "method=web.inbox.setread")
    @ResponseBody
    public ApiResponse setIsRead(String uid, String ids) {
        logger.info("setIsRead with uid={}, ids={}", uid, ids);
        if (StringUtils.isEmpty(uid) || !uid.matches("\\d+")) {
            logger.warn("setIsRead error with uid is error uid={}, ids={}", uid, ids);
            throw new ServiceException(ServiceError.BROWSE_DEL_UID_ISNULL);
        }
        SetIsReadRequestBO bo = new SetIsReadRequestBO();
        bo.setIds(ids);
        bo.setUid(Integer.parseInt(uid));
        CommonRspBO resp = null;
        logger.info("inbox switch is {}",flag);
        if(flag){
            resp = serviceCaller.post("brower.setIsRead", DynamicPropertyFactory.getInstance().getStringProperty("browse.service.url", "http://localhost:8092/brower/").get() + "/InBoxRest/setIsRead", bo, CommonRspBO.class, null).get();
        }else {
            resp = serviceCaller.call("message.setIsRead", bo, CommonRspBO.class);
        }
        return new ApiResponse.ApiResponseBuilder().data(resp).build();
    }

    @RequestMapping(params = "method=app.inbox.changeFlag")
    @ResponseBody
    public ApiResponse changeFlag() throws GatewayException {
        logger.debug("Enter changeFlag.");
        flag = !flag;
        // (2)返回
        return new ApiResponse.ApiResponseBuilder().code(200).message("changeFlag success").data(flag).build();
    }

}
