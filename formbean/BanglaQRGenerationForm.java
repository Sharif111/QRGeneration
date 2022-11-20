package com.adminpanel.merchantadminpanel.qrgeneration.formbean;

import org.apache.struts.validator.ValidatorActionForm;

public class BanglaQRGenerationForm extends ValidatorActionForm {
	private String sAccountTitle="";
	private  String sAccountNo="";
	private String sAddress="";
	private String sErrorCode="";
	private String sErrorMessage="";
	
	
	public String getAccountTitle() {
		return sAccountTitle;
	}
	public void setAccountTitle(String accountTitle) {
		this.sAccountTitle = accountTitle;
	}
	public String getAccountNo() {
		return sAccountNo;
	}
	public void setAccountNo(String accountNo) {
		this.sAccountNo = accountNo;		
	}
	public String getAddress() {
		return sAddress;
	}
	public void setAddress(String address) {
		this.sAddress = address;
	}
	public String getErrorCode() {
		return sErrorCode;
	}
	public void setErrorCode(String errorCode) {
		this.sErrorCode = errorCode;
	}
	public String getErrorMessage() {
		return sErrorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.sErrorMessage = errorMessage;
	}


	
	
	
}