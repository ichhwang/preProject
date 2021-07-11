package com.insurance.payment;

import org.springframework.beans.factory.annotation.Autowired;

import com.insurance.payment.dao.PaymentRepository;
import com.insurance.payment.dto.CancelRequestDto;
import com.insurance.payment.dto.CancelResponseDto;
import com.insurance.payment.entity.Payment;
import com.insurance.payment.service.PaymentService;

public class CancelLockingThread extends Thread {
	@Autowired
	PaymentService service;
	@Autowired
	PaymentRepository paymentRepository;
	
	private static int threadCnt = 1;

    private int cnt;
    
    private String id;

    public CancelLockingThread(PaymentService service, PaymentRepository paymentRepository, String id) {
        super();
        this.service = service;
        this.paymentRepository = paymentRepository;
        this.id = id;
        cnt = threadCnt++;
    }

    @Override
    public void run() {
        System.out.println("Thread-"+ cnt + " run");

        try {
            
        	CancelResponseDto response = this.cancelRequest(id, (long)90000, (long)9000);
        	Payment payment = paymentRepository.findById(id).orElseThrow();
    		System.out.println("***취소 "+cnt+":"+response.getRsltMsg()+" ,최종결재금액:"+payment.getTotalAmount()+" vat:"+payment.getTotalVat());
    		
    		Thread.sleep(1);
            
        } catch (InterruptedException e) { }
        System.out.println("Thread-"+ cnt + " end");
    }
    
	/**
	 * 취소
	 * @param id
	 * @param amt
	 * @param vat
	 * @return
	 */
	private CancelResponseDto cancelRequest(String id, Long amt, Long vat) {
		CancelRequestDto request = new CancelRequestDto();
		request.setId(id);
		request.setCancelAmount(amt);
		request.setVat(vat);
		CancelResponseDto response = service.cancel(request);
		return response;
	}
}
