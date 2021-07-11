package com.insurance.payment.helper;

import java.math.BigInteger;

import com.insurance.payment.dto.CancelRequestDto;
import com.insurance.payment.dto.PaymentRequestDto;

public class PaymentRequestChecker {
	/**
	 * 결제요청 필수체크
	 * 카드번호(10 ~ 16자리 숫자)
	 * 유효기간(4자리 숫자, mmyy)
     * cvc(3자리 숫자)
     * 할부개월수 : 0(일시불), 1 ~ 12
     * 결제금액(100원 이상, 10억원 이하, 숫자)
	 * @param request
	 * @return
	 */
	public static boolean checkPaymentRequest(PaymentRequestDto request) {
		//cardNo
		if(request.getCardNo().compareTo(new BigInteger("1000000000")) >= 0
				&& request.getCardNo().compareTo(new BigInteger("10000000000000000")) <= 0 ) {			
		}else {
			return false;
		}
		//expiryDate
		if(request.getExpiryDate() >= 0721 && request.getExpiryDate() <= 1299 ) {
			int mm = request.getExpiryDate() / 100;
			if(mm >= 01 && mm <= 12) {
			}else {
				return false;
			}
		}else {
			return false;
		}
		//cvc
		if(request.getCvc() >= 100 && request.getCvc() <= 999) {
		}else {
			return false;
		}
		//InstallmentMonth
		if(request.getInstallmentMonth() >= 0 && request.getInstallmentMonth() <= 12) {
		}else {
			return false;
		}
		//payment amount
		if(request.getPaymentAmount() >= 100 && request.getPaymentAmount() <= 1000000000 ) {
		}else {
			return false;
		}
		return true;
	}
	
	/**
	 * 취소요청 필수체크
	 * 관리번호(unique id, 20자리)
	 * 취소금액
	 * @param request
	 * @return
	 */
	public static boolean checkCancelRequest(CancelRequestDto request) {
		//id
		if(request.getId().length() == 20) {
		}else {
			System.out.println("*****"+request.getId().length());
			return false;
		}
		//cancel amount
		if(request.getCancelAmount() > 0) {
		}else {
			return false;
		}
		return true;
	}
	
	/**
	 * 조회요청 필수체크
	 * 관리번호(unique id, 20자리)
	 * @param id
	 * @return
	 */
	public static boolean checkInfoRequest(String id) {
		//id
		if(id.length() == 20) {
			return true;
		}else {
			return false;
		}
	}
}
