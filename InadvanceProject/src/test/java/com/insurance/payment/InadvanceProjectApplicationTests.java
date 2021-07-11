package com.insurance.payment;

import java.math.BigInteger;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.insurance.payment.dao.PaymentRepository;
import com.insurance.payment.dto.CancelRequestDto;
import com.insurance.payment.dto.CancelResponseDto;
import com.insurance.payment.dto.PaymentDetailResponseDto;
import com.insurance.payment.dto.PaymentRequestDto;
import com.insurance.payment.dto.PaymentResponseDto;
import com.insurance.payment.entity.Payment;
import com.insurance.payment.service.PaymentService;

@SpringBootTest
class InadvanceProjectApplicationTests{
	
	@Autowired
	PaymentService service;
	@Autowired
	PaymentRepository paymentRepository;

	/**
	 * 동일카드번호 중복결제 방지
	 * LockModeType.PESSIMISTIC_FORCE_INCREMENT 테스트
	 * 결제테이블에 Version 추가
	 * @Transation 관리
	 * JpaSystemException 발생
	 * TransactionException 발생
	 * JdbcSQLNonTransientConnectionException 발생
	 * @throws InterruptedException
	 */
	@Test
	void testPaymentLock() throws InterruptedException {
		System.out.println("***** START testPaymentLock START *****");
		try {
            ArrayList<PaymentLockingThread> threads = new ArrayList<PaymentLockingThread>();
            for (int i = 0; i < 10; i++) {
                threads.add(new PaymentLockingThread(service, paymentRepository));
            }
            for (var thread : threads) {
                thread.start();
            }
             
            System.out.println("End");
            
        } catch (Exception e) {
        }
		System.out.println("***** START testPaymentLock END *****");
    }
	
	/**
	 * 중복 취소 방지
	 * LockModeType.PESSIMISTIC_FORCE_INCREMENT 테스트
	 * 결제테이블에 Version 추가
	 * org.springframework.orm.ObjectOptimisticLockingFailureException 발생하여
	 * Second Lost Updates Problem을 방지하고 최초 커밋만 인정하게 된다.
	 * @throws InterruptedException
	 */
	@Test
	void testCancelLock() throws InterruptedException {
		System.out.println("***** START testCancelLock START *****");
		// 결제
		PaymentResponseDto response = this.paymentRequest("1234567890123456", 222, 1122, 12, (long)90000, (long)9000);
		PaymentDetailResponseDto detail = service.getPaymentDetail(response.getId());
		System.out.println("***결재금액:"+detail.getAmount()+" vat:"+detail.getVat());
		
		try {
            ArrayList<CancelLockingThread> threads = new ArrayList<CancelLockingThread>();
            for (int i = 0; i < 10; i++) {
                threads.add(new CancelLockingThread(service, paymentRepository, response.getId()));
            }
            for (var thread : threads) {
                thread.start();
            }
            System.out.println("End");
            
        } catch (Exception e) {
        }

        Payment payment = paymentRepository.findById(response.getId()).orElseThrow();
		System.out.println("***부분취소결과 ,최종결재금액:"+payment.getTotalAmount()+" vat:"+payment.getTotalVat());
		
		System.out.println("***** START testCancelLock END *****");
    }
	
	
	@Test
	void testCase1() {
		System.out.println("***** START testCase1 START *****");
		// 결제
		PaymentResponseDto response = this.paymentRequest("1234567890123456", 222, 1122, 12, (long)11000, (long)1000);
		
		PaymentDetailResponseDto detail = service.getPaymentDetail(response.getId());
		System.out.println("***결재금액:"+detail.getAmount()+" vat:"+detail.getVat());
		
		// 부분취소1
		CancelResponseDto cancelResonse1 = this.cancelRequest(response.getId(), (long)1100, (long)100);
		Payment payment = paymentRepository.findById(response.getId()).orElseThrow();
		System.out.println("***취소1 "+cancelResonse1.getRsltMsg()+" ,최종결재금액:"+payment.getTotalAmount()+" vat:"+payment.getTotalVat());
		// 부분취소2
		CancelResponseDto cancelResonse2 = this.cancelRequest(response.getId(), (long)3300, null);
		payment = paymentRepository.findById(response.getId()).orElseThrow();
		System.out.println("***취소2 "+cancelResonse2.getRsltMsg()+" ,최종결재금액:"+payment.getTotalAmount()+" vat:"+payment.getTotalVat());
		// 부분취소3
		CancelResponseDto cancelResonse3 = this.cancelRequest(response.getId(), (long)7000, null);
		payment = paymentRepository.findById(response.getId()).orElseThrow();
		System.out.println("***취소3 "+cancelResonse3.getRsltMsg()+" ,최종결재금액:"+payment.getTotalAmount()+" vat:"+payment.getTotalVat());
		// 부분취소4
		CancelResponseDto cancelResonse4 = this.cancelRequest(response.getId(), (long)6600, (long)700);
		payment = paymentRepository.findById(response.getId()).orElseThrow();
		System.out.println("***취소4 "+cancelResonse4.getRsltMsg()+" ,최종결재금액:"+payment.getTotalAmount()+" vat:"+payment.getTotalVat());
		// 부분취소5
		CancelResponseDto cancelResonse5 = this.cancelRequest(response.getId(), (long)6600, (long)600);
		payment = paymentRepository.findById(response.getId()).orElseThrow();
		System.out.println("***취소5 "+cancelResonse5.getRsltMsg()+" ,최종결재금액:"+payment.getTotalAmount()+" vat:"+payment.getTotalVat());
		// 부분취소6
		CancelResponseDto cancelResonse6 = this.cancelRequest(response.getId(), (long)100, null);
		payment = paymentRepository.findById(response.getId()).orElseThrow();
		System.out.println("***취소6 "+cancelResonse6.getRsltMsg()+" ,최종결재금액:"+payment.getTotalAmount()+" vat:"+payment.getTotalVat());
		
		
		System.out.println("***** END testCase1 END *****");
	}
	
	@Test
	void testCase2() {
		System.out.println("***** START testCase2 START *****");
		// 결제
		PaymentResponseDto response = this.paymentRequest("1234567890123456", 222, 1122, 12, (long)20000, (long)909);
		
		PaymentDetailResponseDto detail = service.getPaymentDetail(response.getId());
		System.out.println("***결재금액:"+detail.getAmount()+" vat:"+detail.getVat());
		
		// 부분취소1
		CancelResponseDto cancelResonse1 = this.cancelRequest(response.getId(), (long)10000, (long)0);
		Payment payment = paymentRepository.findById(response.getId()).orElseThrow();
		System.out.println("***취소1 "+cancelResonse1.getRsltMsg()+" ,최종결재금액:"+payment.getTotalAmount()+" vat:"+payment.getTotalVat());
		// 부분취소2
		CancelResponseDto cancelResonse2 = this.cancelRequest(response.getId(), (long)10000, (long)0);
		payment = paymentRepository.findById(response.getId()).orElseThrow();
		System.out.println("***취소2 "+cancelResonse2.getRsltMsg()+" ,최종결재금액:"+payment.getTotalAmount()+" vat:"+payment.getTotalVat());
		// 부분취소3
		CancelResponseDto cancelResonse3 = this.cancelRequest(response.getId(), (long)10000, (long)909);
		payment = paymentRepository.findById(response.getId()).orElseThrow();
		System.out.println("***취소3 "+cancelResonse3.getRsltMsg()+" ,최종결재금액:"+payment.getTotalAmount()+" vat:"+payment.getTotalVat());
		
		System.out.println("***** END testCase2 END *****");
	}
	
	@Test
	void testCase3() {
		System.out.println("***** START testCase3 START *****");
		// 결제
		PaymentResponseDto response = this.paymentRequest("1234567890123456", 222, 1122, 12, (long)20000, null);
		
		PaymentDetailResponseDto detail = service.getPaymentDetail(response.getId());
		System.out.println("***결재금액:"+detail.getAmount()+" vat:"+detail.getVat());
		
		// 부분취소1
		CancelResponseDto cancelResonse1 = this.cancelRequest(response.getId(), (long)10000, (long)1000);
		Payment payment = paymentRepository.findById(response.getId()).orElseThrow();
		System.out.println("***취소1 "+cancelResonse1.getRsltMsg()+" ,최종결재금액:"+payment.getTotalAmount()+" vat:"+payment.getTotalVat());
		// 부분취소2
		CancelResponseDto cancelResonse2 = this.cancelRequest(response.getId(), (long)10000, (long)909);
		payment = paymentRepository.findById(response.getId()).orElseThrow();
		System.out.println("***취소2 "+cancelResonse2.getRsltMsg()+" ,최종결재금액:"+payment.getTotalAmount()+" vat:"+payment.getTotalVat());
		// 부분취소3
		CancelResponseDto cancelResonse3 = this.cancelRequest(response.getId(), (long)10000, null);
		payment = paymentRepository.findById(response.getId()).orElseThrow();
		System.out.println("***취소3 "+cancelResonse3.getRsltMsg()+" ,최종결재금액:"+payment.getTotalAmount()+" vat:"+payment.getTotalVat());
		
		System.out.println("***** END testCase3 END *****");
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
