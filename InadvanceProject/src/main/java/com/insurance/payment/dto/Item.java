package com.insurance.payment.dto;

public class Item {
	private String name;
	private int length;
	private String align; // LEFT RIGHT
	private String type;
	private String blank;
	private String value;
	
	public Item(String name, int length, String align, String type, String blank, String value) {
		this.name = name;
		this.length = length;
		this.align = align;
		this.type = type;
		this.blank = blank;
		this.value = value;
	}
	

	
	@Override
	public String toString() {
		String result = this.getValue();
		
		int padLength = this.getLength() - this.getValue().length();
		
		for(int i=0; i<padLength; i++) {
			if(this.getAlign() == "L") {
				result = result + this.getBlank();
			}else {
				result = this.getBlank() + result;
			}
		}	
				
		return result;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBlank() {
		return blank;
	}
	public void setBlank(String blank) {
		this.blank = blank;
	}
	/**
	 * 공백문자제거
	 * @return
	 */
	public String getValue() {
		value = value.replace(" ", "");
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
	
