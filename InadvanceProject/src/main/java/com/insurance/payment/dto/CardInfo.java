package com.insurance.payment.dto;

import com.insurance.payment.helper.PaymentConstans;
import com.insurance.payment.helper.PaymentUtil;

public class CardInfo {
	private String cardNoOrigin;
	private String cardNoMasking;
	private Integer expiryDate;
	private Integer cvc;
	
	public CardInfo(String decrypt) {
		super();
		String infoStr = PaymentUtil.decrypt(decrypt);
		String[] infoArray = infoStr.split(PaymentConstans.SPLIT);
		this.setCardNoOrigin(infoArray[0]);
		this.setExpiryDate(Integer.parseInt(infoArray[1]));
		this.setCvc(Integer.parseInt(infoArray[2]));
		
	}
	
	/**
	 * 카드번호 masking 적용
	 * @return
	 */
	public String getCardNoMasking() {
		cardNoMasking = cardNoOrigin.substring(6, cardNoOrigin.length()-4);
		cardNoMasking = "******"+cardNoMasking+"****";
		return cardNoMasking;
	}
	public void setCardNoMasking(String cardNoMasking) {
		this.cardNoMasking = cardNoMasking;
	}
	public Integer getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Integer expiryDate) {
		this.expiryDate = expiryDate;
	}
	public Integer getCvc() {
		return cvc;
	}
	public void setCvc(Integer cvc) {
		this.cvc = cvc;
	}

	public String getCardNoOrigin() {
		return cardNoOrigin;
	}

	public void setCardNoOrigin(String cardNoOrigin) {
		this.cardNoOrigin = cardNoOrigin;
	}
	
	
	
}
