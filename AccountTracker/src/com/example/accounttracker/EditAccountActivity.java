package com.example.accounttracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditAccountActivity extends Activity implements OnClickListener {
	
	private EditText bankName,accNumber,branchName,branchAddress,accHolder,ifsc,currBalance;
	private Button editAccount;
	private String selectedAcc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_account);
		Toast.makeText(getApplicationContext(), "Edit details of your account", Toast.LENGTH_SHORT).show();
		findViews();
		Intent i=getIntent();
		selectedAcc=i.getStringExtra("account_no");
		//fill text fields
		fillFields();
	}
	
	private void fillFields(){
		try{
			DBHelper dbhelper=new DBHelper(this);
			SQLiteDatabase db=dbhelper.getWritableDatabase();
			String fetch_query="select * from "+Database.ACCOUNT_TABLE_NAME+" where "+Database.ACCOUNT_NUMBER+" ="+selectedAcc;
			Cursor c=db.rawQuery(fetch_query, null);
			c.moveToFirst();
			bankName.setText(c.getString(c.getColumnIndex(Database.ACCOUNT_BANK_NAME)));
			accNumber.setText(c.getString(c.getColumnIndex(Database.ACCOUNT_NUMBER)));
			branchName.setText(c.getString(c.getColumnIndex(Database.ACCOUNT_BRANCH_NAME)));
			branchAddress.setText(c.getString(c.getColumnIndex(Database.ACCOUNT_BRANCH_ADDRESS)));
			accHolder.setText(c.getString(c.getColumnIndex(Database.ACCOUNT_HOLDERS)));
			ifsc.setText(c.getString(c.getColumnIndex(Database.ACCOUNT_IFSC)));
			currBalance.setText(c.getString(c.getColumnIndex(Database.ACCOUNT_BALANCE)));
		}
		catch(Exception e){
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//no need to inflate any menu here
		return true;
	}
	
	private void findViews(){
		bankName=(EditText)findViewById(R.id.etEditBankName);
		accNumber=(EditText)findViewById(R.id.etEditAccountNumber);
		branchName=(EditText)findViewById(R.id.etEditBranchName);
		branchAddress=(EditText)findViewById(R.id.etEditBranchAddress);
		accHolder=(EditText)findViewById(R.id.etEditAccountHolder);
		ifsc=(EditText)findViewById(R.id.etEditIFSC);
		currBalance=(EditText)findViewById(R.id.etEditCurrentBalance);
		editAccount=(Button)findViewById(R.id.btnEditAccount);
		editAccount.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		boolean flag=true;
		Account update_acc=new Account();
		
		//check for blank account number
		String s=accNumber.getText().toString();
		if(s.length()==0)//null account number
			flag=false;
		else
			update_acc.setAccNumber(s);
		
		//check for blank account holder
		s=accHolder.getText().toString();
		if(s.length()==0)//null account holder
			flag=false;
		else
			update_acc.setAccountHolders(s);
		
		//check for blank bank name
		s=bankName.getText().toString();
		if(s.length()==0)
			flag=false;
		else
			update_acc.setBankName(s);
		
		update_acc.setBranchAddress(branchAddress.getText().toString());
		update_acc.setBranchName(branchName.getText().toString());
		
		//check for blank current balance
		s=currBalance.getText().toString();
		if(s.length()==0)//null balance
			flag=false;
		else
			update_acc.setCurrBalance(s);		
		
		update_acc.setIFSC(ifsc.getText().toString());
		
		if(flag){
			try{
				DBHelper dbhelper=new DBHelper(this);
				SQLiteDatabase db=dbhelper.getWritableDatabase();
				String update_query="update "+Database.ACCOUNT_TABLE_NAME+" set "+Database.ACCOUNT_BALANCE+" = '"+update_acc.getCurrBalance()+"', "+Database.ACCOUNT_BANK_NAME+" = '"+update_acc.getBankName()+"', "+Database.ACCOUNT_BRANCH_ADDRESS+" = '"+update_acc.getBranchAddress()+"', "+Database.ACCOUNT_BRANCH_NAME+" = '"+update_acc.getBranchName()+"', "+Database.ACCOUNT_HOLDERS+" = '"+update_acc.getAccountHolders()+"', "+Database.ACCOUNT_IFSC+" = '"+update_acc.getIFSC()+"', "+Database.ACCOUNT_NUMBER+" = '"+update_acc.getAccNumber()+"' where "+Database.ACCOUNT_NUMBER+" = "+selectedAcc;
				db.execSQL(update_query);
				Toast.makeText(getApplicationContext(), "Account updated successfully", Toast.LENGTH_LONG).show();
				finish();
			}
			catch(Exception e){
				
			}
		}
		else
			Toast.makeText(getApplicationContext(), "Account number/holder/bank name/current balance cant be empty", Toast.LENGTH_LONG).show();		
	}
}
