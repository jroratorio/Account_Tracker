package com.example.accounttracker;

public class Account {
	
	private String bankName,branchName,branchAddress,accountHolders,ifsc,currBal,accNo;
		
	public void setBankName(String s){
		bankName=s;
	}
	
	public void setBranchName(String s){
		branchName=s;
	}
	
	public void setBranchAddress(String s){
		branchAddress=s;
	}
	
	public void setAccountHolders(String s){
		accountHolders=s;
	}
	
	public void setIFSC(String s){
		ifsc=s;
	}
	
	public void setCurrBalance(String s){
		currBal=s;
	}
	
	public void setAccNumber(String s){
		accNo=s;
	}
	
	public String getBankName(){
		return bankName;
	}
	
	public String getBranchName(){
		return branchName;
	}
	
	public String getBranchAddress(){
		return branchAddress;
	}
	
	public String getAccountHolders(){
		return accountHolders;
	}
	
	public String getIFSC(){
		return ifsc;
	}
	
	public String getCurrBalance(){
		return currBal;
	}
	
	public String getAccNumber(){
		return accNo;
	}
}
