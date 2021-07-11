package com.insurance.payment.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Cancel {

	@Id
	@Column
	private String id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENT_ID")
	private Payment payment;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DETAIL_ID")
	private PaymentDetail detail;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public PaymentDetail getDetail() {
		return detail;
	}

	public void setDetail(PaymentDetail detail) {
		this.detail = detail;
	}
	
}
