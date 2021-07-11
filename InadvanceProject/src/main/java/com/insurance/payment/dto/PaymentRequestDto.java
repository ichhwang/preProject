package com.insurance.payment.dto;

import java.math.BigInteger;

public class PaymentRequestDto {
	private BigInteger cardNo;
	private int expiryDate; //유효기간 mmyy
	private int cvc;
	private int installmentMonth; //할부개월수 
	private Long paymentAmount; //결재금액
	private Long vat; // 부가가치세
	
	public BigInteger getCardNo() {
		return cardNo;
	}
	public void setCardNo(BigInteger cardNo) {
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
	public int getInstallmentMonth() {
		return installmentMonth;
	}
	public void setInstallmentMonth(int installmentMonth) {
		this.installmentMonth = installmentMonth;
	}
	public Long getPaymentAmount() {
		return paymentAmount;
	}
	public void setPaymentAmount(Long paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	/**
	 * vat 값이 있으면, 리턴
	 * 없으면 paymentAmount / 11 반올
	 * @return
	 */
	public Long getVat() {		
		if(vat == null) {
			vat = Math.round((double)paymentAmount/11);
		}
				
		return vat;
	}
	public void setVat(Long vat) {
		this.vat = vat;
	}
	
		
}
