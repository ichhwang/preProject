package com.insurance.payment;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;

import com.insurance.payment.dao.PaymentRepository;
import com.insurance.payment.dto.PaymentDetailResponseDto;
import com.insurance.payment.dto.PaymentRequestDto;
import com.insurance.payment.dto.PaymentResponseDto;
import com.insurance.payment.service.PaymentService;

public class PaymentLockingThread extends Thread {
	@Autowired
	PaymentService service;
	@Autowired
	PaymentRepository paymentRepository;
	
	private static int threadCnt = 1;

    private int cnt;
    

    public PaymentLockingThread(PaymentService service, PaymentRepository paymentRepository) {
        super();
        this.service = service;
        this.paymentRepository = paymentRepository; 
        cnt = threadCnt++;
    }

    @Override
    public void run() {
        System.out.println("Thread-"+ cnt + " run");

        try {
            
        	// 결제
    		PaymentResponseDto response = this.paymentRequest("1234567890123456", 222, 1122, 12, (long)11000, (long)1000);
    		
    		PaymentDetailResponseDto detail = service.getPaymentDetail(response.getId());
    		System.out.println("*** 결제"+response.getId()+" 결재금액:"+detail.getAmount()+" vat:"+detail.getVat());
    		
    		Thread.sleep(1);
            
        } catch (InterruptedException e) { }
        System.out.println("Thread-"+ cnt + " end");
    }
    
    /**
	 * 결재
	 * @param cardNo
	 * @param cvc
	 * @param expiryDate
	 * @param installmentMonth
	 * @param amount
	 * @param vat
	 * @return
	 */
	private PaymentResponseDto paymentRequest(String cardNo, int cvc, int expiryDate, int installmentMonth, Long amount, Long vat) {
		PaymentRequestDto request = new PaymentRequestDto();
		request.setCardNo(new BigInteger(cardNo));
		request.setCvc(cvc);
		request.setExpiryDate(expiryDate);
		request.setInstallmentMonth(installmentMonth);
		request.setPaymentAmount(amount);
		request.setVat(vat);
		
		PaymentResponseDto response = service.payment(request);
		return response;
	}
}
