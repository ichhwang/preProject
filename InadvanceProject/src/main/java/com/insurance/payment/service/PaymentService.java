package com.insurance.payment.service;

import java.math.BigInteger;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.payment.dao.CancelRepository;
import com.insurance.payment.dao.PaymentDetailRepository;
import com.insurance.payment.dao.PaymentRepository;
import com.insurance.payment.dto.PaymentRequestDto;
import com.insurance.payment.dto.PaymentResponseDto;
import com.insurance.payment.entity.Cancel;
import com.insurance.payment.entity.Payment;
import com.insurance.payment.entity.PaymentDetail;
import com.insurance.payment.helper.PaymentConstans;
import com.insurance.payment.helper.PaymentRequestChecker;
import com.insurance.payment.helper.PaymentUtil;
import com.insurance.payment.dto.CancelRequestDto;
import com.insurance.payment.dto.CancelResponseDto;
import com.insurance.payment.dto.PaymentDetailResponseDto;
import com.insurance.payment.dto.PaymentDetailStrDto;

@Service
public class PaymentService{
	
	@Autowired
	private PaymentRepository paymentRepository;
	private CancelRepository cancelRepository;
	private PaymentDetailRepository paymentDetailRepository;

	public PaymentService(PaymentRepository paymentRepository, CancelRepository cancelRepository, PaymentDetailRepository paymentDetailRepository) {
		super();
		this.paymentRepository = paymentRepository;
		this.cancelRepository = cancelRepository;
		this.paymentDetailRepository = paymentDetailRepository;
	}
	
	/**
	 * 결재 request 를
	 * Payment 와 PaymentDetail에 저장하고 
	 * 관리번호와 문자열을 리턴
	 * @param request
	 * @return
	 */
	@Transactional
	public PaymentResponseDto payment(PaymentRequestDto request){
		PaymentResponseDto response = new PaymentResponseDto();
		response.setRsltCd(HttpStatus.OK.value());
		response.setRsltMsg(HttpStatus.OK.getReasonPhrase());
		
		PaymentDetailStrDto paymentDetailStrDto = new PaymentDetailStrDto();
		String id = PaymentUtil.makeKey();
		
		// 정합성 체크
		if(PaymentRequestChecker.checkPaymentRequest(request)) {		
			try {
				// PaymentDetail
				PaymentDetail paymentDetail = new PaymentDetail();
				paymentDetail.setId(id);
				paymentDetail.setContents(paymentDetailStrDto.convertPaymentDetailRequestToContents(PaymentConstans.PAYMENT, id, request));
				paymentDetailRepository.save(paymentDetail);
				
				// Payment
				Payment payment = new Payment();
				payment.setId(id);
				payment.setTotalAmount(request.getPaymentAmount());
				payment.setTotalVat(request.getVat());
				payment.setDetail(paymentDetail);
				paymentRepository.save(payment);
				
				response.setId(id);
				response.setContents(paymentDetail.getContents());
			}catch(NoSuchElementException nsee) {
				response.setRsltCd(HttpStatus.NO_CONTENT.value());
				response.setRsltMsg(HttpStatus.NO_CONTENT.getReasonPhrase());
			}catch(Exception e) {			
				response.setRsltCd(HttpStatus.INTERNAL_SERVER_ERROR.value());
				response.setRsltMsg(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
			}
		}else {
			response.setRsltCd(PaymentConstans.CD902);
			response.setRsltMsg(PaymentConstans.MSG902);
		}
		return response;
	}
	
	/**
	 * 취소 request를 Cancel과 PaymentDetail에 저장하고
	 * 취소금액이 차감된 최종결재금액과 최종부가세금액을 Payment에 업데이트한다.
	 * @param request
	 * @return
	 */
	@Transactional
	public CancelResponseDto cancel(CancelRequestDto request){
		CancelResponseDto cancelResponseDto = new CancelResponseDto();
		cancelResponseDto.setRsltCd(HttpStatus.OK.value());
		cancelResponseDto.setRsltMsg(HttpStatus.OK.getReasonPhrase());
		PaymentDetail paymentDetail = new PaymentDetail();
		Cancel cancel = new Cancel();
		String id = PaymentUtil.makeKey();
		
		if(PaymentRequestChecker.checkCancelRequest(request)){
			try {
				//원 결제 데이터 조회
				Payment payment = paymentRepository.findById(request.getId()).orElseThrow();
				
				// vat 보정
				if(request.getVat() == null) {
					long tmpVat = Math.round((double)request.getCancelAmount()/11);
					// 최종vat보다 크면 최종vat로설정
					if (payment.getTotalVat() < tmpVat) {
						request.setVat(payment.getTotalVat());
					}else {
						request.setVat(tmpVat);
					}
				}
				
				//취소 가능 체크
				boolean check = true;
				if ( payment.getTotalAmount() >= request.getCancelAmount() 
						&& payment.getTotalVat() >= request.getVat()  ) {
					if (request.getCancelAmount() > request.getVat()) {
						if ( payment.getTotalAmount() == request.getCancelAmount() ) {
							 if ( payment.getTotalVat() != request.getVat()) {
								 check = false;
							 }
						}
					} else {
						check = false;
					}
				} else {
					check = false;
				}
				
				if(check) {	
					payment.setTotalAmount((payment.getTotalAmount() - request.getCancelAmount()));
					payment.setTotalVat((payment.getTotalVat() - request.getVat()));
					
					// Payment
					// 원 결제 데이터 수정(결제금액, vat)
					paymentRepository.save(payment);
					
					PaymentDetailStrDto paymentDetailStrDtoForPayment = new PaymentDetailStrDto();
					PaymentDetailResponseDto paymentDetailResponseDto = paymentDetailStrDtoForPayment.convertContentsToPaymentDetailResponse(payment.getDetail().getContents());
					// PaymentRequestDto
					PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
					paymentRequestDto.setCardNo(new BigInteger(paymentDetailResponseDto.getCardNo()));
					paymentRequestDto.setCvc(paymentDetailResponseDto.getCvc());
					paymentRequestDto.setExpiryDate(paymentDetailResponseDto.getExpiryDate());
					paymentRequestDto.setInstallmentMonth(0);
					paymentRequestDto.setPaymentAmount(request.getCancelAmount());
					paymentRequestDto.setVat(request.getVat());
					PaymentDetailStrDto paymentDetailStrDtoForCancel = new PaymentDetailStrDto();
					;
					// PaymentDetail
					paymentDetail.setId(id);
					paymentDetail.setContents(paymentDetailStrDtoForCancel.convertPaymentDetailRequestToContents(PaymentConstans.CANCEL, id, paymentRequestDto));
					paymentDetailRepository.save(paymentDetail);
					
					// Cancel
					cancel.setDetail(paymentDetail);
					cancel.setId(id);
					cancel.setPayment(payment);
					cancelRepository.save(cancel);
					
				} else {
					cancelResponseDto.setRsltCd(PaymentConstans.CD901);
					cancelResponseDto.setRsltMsg(PaymentConstans.MSG901);
				}
							
				// CancelResponseDto
				cancelResponseDto.setId(id);
				cancelResponseDto.setContents(paymentDetail.getContents());
			
			}catch(NoSuchElementException nsee) {
				cancelResponseDto.setRsltCd(HttpStatus.NO_CONTENT.value());
				cancelResponseDto.setRsltMsg(HttpStatus.NO_CONTENT.getReasonPhrase());
			}catch(Exception e) {			
				cancelResponseDto.setRsltCd(HttpStatus.INTERNAL_SERVER_ERROR.value());
				cancelResponseDto.setRsltMsg(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
			}
		}else {
			cancelResponseDto.setRsltCd(PaymentConstans.CD902);
			cancelResponseDto.setRsltMsg(PaymentConstans.MSG902);
		}
		return cancelResponseDto;
	}
	
	/**
	 * 결재 상세정보 조회
	 * 카드번호는 마스킹처리
	 * @param id
	 * @return
	 */
	@Transactional(readOnly =  true)
	public PaymentDetailResponseDto getPaymentDetail(String id){
		PaymentDetailResponseDto paymentDetailResponseDto = new PaymentDetailResponseDto();
		paymentDetailResponseDto.setRsltCd(HttpStatus.OK.value());
		paymentDetailResponseDto.setRsltMsg(HttpStatus.OK.getReasonPhrase());
		
		if(PaymentRequestChecker.checkInfoRequest(id)) {
			try {
				PaymentDetail paymentDetail = paymentDetailRepository.findById(id).orElseThrow();
				PaymentDetailStrDto paymentDetailStr = new PaymentDetailStrDto();
				paymentDetailResponseDto = paymentDetailStr.convertContentsToPaymentDetailResponseMasking(paymentDetail.getContents());
				
				paymentDetailResponseDto.setRsltCd(HttpStatus.OK.value());
				paymentDetailResponseDto.setRsltMsg(HttpStatus.OK.getReasonPhrase());
			}catch(NoSuchElementException nsee) {
				
				paymentDetailResponseDto.setRsltCd(HttpStatus.NO_CONTENT.value());
				paymentDetailResponseDto.setRsltMsg(HttpStatus.NO_CONTENT.getReasonPhrase());
			}catch(Exception e) {
				
				paymentDetailResponseDto.setRsltCd(HttpStatus.INTERNAL_SERVER_ERROR.value());
				paymentDetailResponseDto.setRsltMsg(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
			}
		}else {
			paymentDetailResponseDto.setRsltCd(PaymentConstans.CD902);
			paymentDetailResponseDto.setRsltMsg(PaymentConstans.MSG902);
		}
		return paymentDetailResponseDto;
	}
}
