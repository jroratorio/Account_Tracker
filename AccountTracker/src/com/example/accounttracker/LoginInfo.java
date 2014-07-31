package com.example.accounttracker;

public class LoginInfo {
	private String username,password;
	
	public LoginInfo(String u,String p){
		username=u;
		password=p;
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getPassword(){
		return password;
	}
}
