package com.insurance.payment.dto;

public class PaymentResponseDto {
	private String id;
	private String contents;
	
	private int rsltCd;
	private String rsltMsg;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public int getRsltCd() {
		return rsltCd;
	}
	public void setRsltCd(int rsltCd) {
		this.rsltCd = rsltCd;
	}
	public String getRsltMsg() {
		return rsltMsg;
	}
	public void setRsltMsg(String rsltMsg) {
		this.rsltMsg = rsltMsg;
	}
	
}
