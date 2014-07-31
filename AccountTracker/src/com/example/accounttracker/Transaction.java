package com.example.accounttracker;

public class Transaction {
	private String account,transaction,amount,mode,modeNumber,date,remarks;
	
	public void setAccount(String s){
		account=s;
	}
	
	public void setTransaction(String s){
		transaction=s;
	}
	
	public void setAmount(String s){
		amount=s;
	}
	
	public void setMode(String s){
		mode=s;
	}
	
	public void setModeNumber(String s){
		modeNumber=s;
	}
	
	public void setDate(String s){
		date=s;
	}
	
	public void setRemarks(String s){
		remarks=s;
	}
	
	public String getAccount(){
		return account;
	}
	
	public String getTransaction(){
		return transaction;
	}
	
	public String getAmount(){
		return amount;
	}
	
	public String getMode(){
		return mode;
	}
	
	public String getModeNumber(){
		return modeNumber;
	}
	
	public String getDate(){
		return date;
	}
	
	public String getRemarks(){
		return remarks;
	}
}
