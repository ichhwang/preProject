package com.insurance.payment.dto;

public class CancelRequestDto {
	private String id;
	private Long cancelAmount;
	private Long vat; //부가가치세
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getCancelAmount() {
		return cancelAmount;
	}
	public void setCancelAmount(Long cancelAmount) {
		this.cancelAmount = cancelAmount;
	}
	public Long getVat() {
		return vat;
	}
	public void setVat(Long vat) {
		this.vat = vat;
	}
	
}
