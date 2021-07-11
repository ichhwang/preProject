package com.insurance.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insurance.payment.dto.CancelRequestDto;
import com.insurance.payment.dto.CancelResponseDto;
import com.insurance.payment.dto.PaymentDetailResponseDto;
import com.insurance.payment.dto.PaymentRequestDto;
import com.insurance.payment.dto.PaymentResponseDto;
import com.insurance.payment.service.PaymentService;

@RestController
public class PaymentController {

	@Autowired
	private PaymentService paymentService;
	
	/**
	 * 결제
	 * @param request
	 * @return
	 */
	@PutMapping("/payment")
	public ResponseEntity<?> putPayment(PaymentRequestDto request){
		PaymentResponseDto response = paymentService.payment(request);
		return new ResponseEntity<>(response , HttpStatus.OK);
	}
	
	/**
	 * 결제취소
	 * @param request
	 * @return
	 */
	@PostMapping("/cancel")
	public ResponseEntity<?> postCancel(CancelRequestDto request) {
		CancelResponseDto response = paymentService.cancel(request);
		return new ResponseEntity<>(response , HttpStatus.OK);
	}
	
	/**
	 * 결제 및 취소 정보 조회
	 * @param id
	 * @return
	 */
	@GetMapping("/info")
	public ResponseEntity<?> getPaymentDetail(String id) {
		PaymentDetailResponseDto response = paymentService.getPaymentDetail(id);
		return new ResponseEntity<>(response , HttpStatus.OK);
	}
	
}
