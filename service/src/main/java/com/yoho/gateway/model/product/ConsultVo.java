package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class ConsultVo implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 9069399826502724389L;

	private Integer id;

    private Integer productId;

    private String ask;

    private String answer;
    
    @JSONField(name="ask_time")
    private String askTime;

    @JSONField(name="answer_time")
    private String answerTime;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask == null ? null : ask.trim();
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer == null ? null : answer.trim();
    }

	public String getAskTime() {
		return askTime;
	}

	public void setAskTime(String askTime) {
		this.askTime = askTime;
	}

	public String getAnswerTime() {
		return answerTime;
	}

	public void setAnswerTime(String answerTime) {
		this.answerTime = answerTime;
	}
}