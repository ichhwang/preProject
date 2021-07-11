package com.insurance.payment.dto;

import com.insurance.payment.helper.PaymentConstans;
import com.insurance.payment.helper.PaymentUtil;

public class PaymentDetailStrDto {
	private Item length;
	private Item type;
	private Item id;
	
	private Item cardNo;
	private Item installmentMonth;
	private Item expiryDate;
	private Item cvc;
	private Item paymentAmount;
	private Item vat;
	private Item orgId;
	private Item encCardInfo;
	private Item reserve;
	

	public PaymentDetailStrDto() {
		this.length = new Item("length", 4, "R", "java.lang.Integer", " ", "");
		this.type = new Item("type", 10, "L", "java.lang.String", " ", "");
		this.id = new Item("id", 20, "L", "java.lang.String", " ", "");
		this.cardNo = new Item("cardNo", 20, "L", "java.math.BigInteger", " ", "");
		this.installmentMonth = new Item("installmentMonth", 2, "R", "java.lang.Integer", "0", "");
		this.expiryDate = new Item("expiryDate", 4, "L", "java.lang.Integer", " ", "");
		this.cvc = new Item("cvc", 3, "L", "java.lang.Integer", " ", "");
		this.paymentAmount = new Item("paymentAmount", 10, "R", "java.lang.Long", " ", "");
		this.vat = new Item("vat", 10, "R", "java.lang.Long", "0", "");
		this.orgId = new Item("orgId", 20, "L", "java.lang.String", " ", "");
		this.encCardInfo = new Item("encCardInfo", 300, "L", "java.lang.String", " ", "");
		this.reserve = new Item("reserve", 47, "L", "java.lang.String", " ", "");
	}
	
	/**
	 * contents 를 입력받아, 결재/취소 데이터로 변환하여 응답객체로 변환
	 * @param contents
	 * @return PaymentDetailResponseDto
	 */
	public PaymentDetailResponseDto convertContentsToPaymentDetailResponse(String contents) {
		PaymentDetailResponseDto paymentDetailResponseDto = new PaymentDetailResponseDto();
		
		int startIndex = 0;
		startIndex = this.setItemValue(length, contents, startIndex);
		startIndex = this.setItemValue(type, contents, startIndex);
		startIndex = this.setItemValue(id, contents, startIndex);
		startIndex = this.setItemValue(cardNo, contents, startIndex);
		startIndex = this.setItemValue(installmentMonth, contents, startIndex);
		startIndex = this.setItemValue(expiryDate, contents, startIndex);
		startIndex = this.setItemValue(cvc, contents, startIndex);
		startIndex = this.setItemValue(paymentAmount, contents, startIndex);
		startIndex = this.setItemValue(vat, contents, startIndex);
		startIndex = this.setItemValue(orgId, contents, startIndex);
		startIndex = this.setItemValue(encCardInfo, contents, startIndex);
		startIndex = this.setItemValue(reserve, contents, startIndex);
		
		paymentDetailResponseDto.setId(id.getValue());
		paymentDetailResponseDto.setCardNo(cardNo.getValue());
		paymentDetailResponseDto.setExpiryDate(Integer.parseInt(expiryDate.getValue()));
		paymentDetailResponseDto.setCvc(Integer.parseInt(cvc.getValue()));
		paymentDetailResponseDto.setType(type.getValue());
		paymentDetailResponseDto.setAmount(Long.parseLong(paymentAmount.getValue()));
		paymentDetailResponseDto.setVat(Long.parseLong(vat.getValue()));
		
		return paymentDetailResponseDto;
	}
	
	/**
	 * contents 를 입력받아, 결재/취소 데이터로 변환하여 응답객체로 변환
	 * 카드번호 마스킹 처리
	 * @param contents
	 * @return PaymentDetailResponseDto
	 */
	public PaymentDetailResponseDto convertContentsToPaymentDetailResponseMasking(String contents) throws Exception {
		PaymentDetailResponseDto paymentDetailResponseDto = this.convertContentsToPaymentDetailResponse(contents);
		CardInfo cardInfo = new CardInfo(encCardInfo.getValue());
		paymentDetailResponseDto.setCardNo(cardInfo.getCardNoMasking());
		paymentDetailResponseDto.setExpiryDate(cardInfo.getExpiryDate());
		paymentDetailResponseDto.setCvc(cardInfo.getCvc());
		
		return paymentDetailResponseDto;
	}
	
	/**
	 * payment 요청을 string으로 변환
	 * @param paymentRequest
	 * @return
	 */
	public String convertPaymentDetailRequestToContents(String type, String id, PaymentRequestDto paymentRequest) {
		String contents = new String();
		
		this.type.setValue(type);
		contents += this.type.toString();
		
		this.id.setValue(id);
		contents += this.id.toString();
		
		this.cardNo.setValue(paymentRequest.getCardNo().toString());
		contents += this.cardNo.toString();
		
		this.installmentMonth.setValue(String.valueOf(paymentRequest.getInstallmentMonth()));
		contents += this.installmentMonth.toString();

		this.expiryDate.setValue(String.valueOf(paymentRequest.getExpiryDate()));
		contents += this.expiryDate.toString();
		
		this.cvc.setValue(String.valueOf(paymentRequest.getCvc()));
		contents += this.cvc.toString();
		
		this.paymentAmount.setValue(String.valueOf(paymentRequest.getPaymentAmount()));
		contents += this.paymentAmount.toString();
		
		this.vat.setValue(String.valueOf(paymentRequest.getVat()));
		contents += this.vat.toString();
		
		this.orgId.setValue("");
		contents += this.orgId.toString();
		
		String encStr = paymentRequest.getCardNo() + PaymentConstans.SPLIT
				      + paymentRequest.getExpiryDate() + PaymentConstans.SPLIT
				      + paymentRequest.getCvc();
		this.encCardInfo.setValue(PaymentUtil.encrypt(encStr));
		contents += this.encCardInfo.toString();
		
		this.reserve.setValue("");
		contents += this.reserve.toString();
				
		this.length.setValue(String.valueOf(contents.length()));
		contents = this.length.toString() + contents;
				
		return contents;
	}
	
	public int setItemValue(Item item, String str, int startIndex) {
		int endIndex = startIndex + item.getLength();
		item.setValue(str.substring(startIndex, endIndex)); 
		
		return endIndex;
	}
}
