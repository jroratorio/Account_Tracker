package com.example.accounttracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddAccountActivity extends Activity implements OnClickListener {
	
	private EditText bankName,accNumber,branchName,branchAddress,accHolder,ifsc,currBalance;
	private Button createAccount;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_account);		
		findViews();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//need not inflate any menu for this activity
		return true;
	}
	
	private void findViews(){
		bankName=(EditText)findViewById(R.id.etBankName);
		accNumber=(EditText)findViewById(R.id.etAccountNumber);
		branchName=(EditText)findViewById(R.id.etBranchName);
		branchAddress=(EditText)findViewById(R.id.etBranchAddress);
		accHolder=(EditText)findViewById(R.id.etAccountHolder);
		ifsc=(EditText)findViewById(R.id.etIFSC);
		currBalance=(EditText)findViewById(R.id.etCurrentBalance);
		createAccount=(Button)findViewById(R.id.btnCreate);
		createAccount.setOnClickListener(this);		
	}

	@Override
	public void onClick(View arg0) {
		boolean flag=true;
		Account new_acc=new Account();
		
		//check for blank account number
		String s=accNumber.getText().toString();
		if(s.length()==0)//null account number
			flag=false;
		else
			new_acc.setAccNumber(s.trim());
		
		//check for blank account holder
		s=accHolder.getText().toString();
		if(s.length()==0)//null account holder
			flag=false;
		else
			new_acc.setAccountHolders(s.trim());
		
		//check for blank bank name
		s=bankName.getText().toString();
		if(s.length()==0)
			flag=false;
		else
			new_acc.setBankName(s.trim());
		
		new_acc.setBranchAddress(branchAddress.getText().toString().trim());
		new_acc.setBranchName(branchName.getText().toString().trim());
		
		//check for blank current balance
		s=currBalance.getText().toString();
		if(s.trim().length()==0)//null balance
			flag=false;
		else
			new_acc.setCurrBalance(s.trim());		
		
		new_acc.setIFSC(ifsc.getText().toString().trim());
		
		if(flag){
			try {
				DBHelper dbhelper = new DBHelper(this); 
				SQLiteDatabase db = dbhelper.getWritableDatabase();
                //putting contents into ContentValue object
				ContentValues values = new ContentValues();
				values.put( Database.ACCOUNT_NUMBER, new_acc.getAccNumber());				
				values.put( Database.ACCOUNT_HOLDERS, new_acc.getAccountHolders());
				values.put( Database.ACCOUNT_BANK_NAME,new_acc.getBankName());
				values.put( Database.ACCOUNT_BRANCH_NAME,new_acc.getBranchName());
				values.put( Database.ACCOUNT_BRANCH_ADDRESS,new_acc.getBranchAddress());
				values.put( Database.ACCOUNT_IFSC,new_acc.getIFSC());
				values.put( Database.ACCOUNT_BALANCE,new_acc.getCurrBalance());
				//inserting to database
				Log.d("Add account","before");
				long rows = db.insert(Database.ACCOUNT_TABLE_NAME, null, values);
				db.close();
				if(rows>0)  {
				    Toast.makeText(getApplicationContext(), "Added Account Successfully!",	Toast.LENGTH_LONG).show();
				    this.finish();
				}
				else
					Toast.makeText(this, "Could not add account! Verify if same account number already exists",Toast.LENGTH_LONG).show();				
			} catch (Exception ex) {
				Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		else
			Toast.makeText(getApplicationContext(), "Account number/holder/bank name/current balance cant be empty", Toast.LENGTH_LONG).show();
	}
}
