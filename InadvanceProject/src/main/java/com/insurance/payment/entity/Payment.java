package com.insurance.payment.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Version;

@Entity
public class Payment {

	@Id
	@Column
	private String id;
	
	@Column
	private long totalAmount;
	
	@Column
	private long totalVat;
	
	@Column
	@Version
	private long version;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DETAIL_ID")
	private PaymentDetail detail;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	public long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public long getTotalVat() {
		return totalVat;
	}

	public void setTotalVat(long totalVat) {
		this.totalVat = totalVat;
	}

	public PaymentDetail getDetail() {
		return detail;
	}

	public void setDetail(PaymentDetail detail) {
		this.detail = detail;
	}
	
	
}
