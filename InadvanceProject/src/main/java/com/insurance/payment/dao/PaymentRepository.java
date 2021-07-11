package com.insurance.payment.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.insurance.payment.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
	/**
	 * org.springframework.orm.ObjectOptimisticLockingFailureException을 발생시키며 
	 * Second Lost Updates Problem을 방지하고 최초 커밋만 인정하게 된다.
	 * @param id
	 * @return
	 */
	@Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    public Payment findById(long id);
}
