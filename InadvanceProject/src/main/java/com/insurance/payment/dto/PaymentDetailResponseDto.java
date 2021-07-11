package com.insurance.payment.dto;

public class PaymentDetailResponseDto {
	private String id;
	private String cardNo;
	private int expiryDate; //유효기간 mmyy
	private int cvc;
	private String type; //결제/취소 구분
	private long amount;  //결제/취소 금액
	private long vat;
	private int rsltCd;
	private String rsltMsg;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public int getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(int expiryDate) {
		this.expiryDate = expiryDate;
	}
	public int getCvc() {
		return cvc;
	}
	public void setCvc(int cvc) {
		this.cvc = cvc;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public long getVat() {
		return vat;
	}
	public void setVat(long vat) {
		this.vat = vat;
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
