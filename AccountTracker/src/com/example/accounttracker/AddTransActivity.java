package com.example.accounttracker;

import java.util.Calendar;
import java.util.StringTokenizer;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddTransActivity extends Activity implements OnClickListener {
	private TextView tvDate;
	private Spinner spAcc,spMode;
	private RadioButton deposit,withdraw;
	private EditText etAmount,etModenumber,etRemarks;
	private ImageButton btnDatePick;
	private Button addTransaction;	
	
	private static final int ID_DATEPICKER=0;
	private int myYear,myDay,myMonth;
	
	private final String[] paymentMode={"Cash","Cheque","Demand Draft","Account Transfer","Others"};
	
	private Button.OnClickListener datePickerButtonOnClickListener= new Button.OnClickListener(){
		@SuppressWarnings("deprecation")
	    public void onClick(View v) {
	        Calendar c = Calendar.getInstance();
	        myYear = c.get(Calendar.YEAR);
	        myMonth = c.get(Calendar.MONTH);
	        myDay = c.get(Calendar.DAY_OF_MONTH);
	        showDialog(ID_DATEPICKER);
	    }
	};
	
	private DatePickerDialog.OnDateSetListener myDateSetListener=new DatePickerDialog.OnDateSetListener(){
	    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	        String date=pad(dayOfMonth)+"-"+pad(monthOfYear+1)+"-"+pad(year);
	        tvDate.setText(date);
	    } 
	};
	
	private String pad(int n){
		if(n<10) return "0"+n;
		else return ""+n;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch(id){
	        case ID_DATEPICKER:	            
	            return new DatePickerDialog(this,myDateSetListener,myYear, myMonth, myDay);
	        default:
	            return null;
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_trans);		
		findViews();
		setDefaultDate();
		btnDatePick.setOnClickListener(datePickerButtonOnClickListener);		
		//set up spinner mode
		setSpinnerMode();
		//set up account spinner
		Database.populateAccounts(spAcc);
		spAcc.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int arg2, long arg3) {
				((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
								
			}			
		});		
	}
	
	private void setSpinnerMode(){
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,paymentMode);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spMode.setAdapter(adapter);
		spMode.setSelection(0);
		spMode.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int arg2, long arg3) {
				((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
								
			}			
		});		
	}
	
	private void findViews(){
		tvDate=(TextView)findViewById(R.id.tvDate);
		spAcc=(Spinner)findViewById(R.id.spinnerAccount);
		spMode=(Spinner)findViewById(R.id.spinnerMode);
		deposit=(RadioButton)findViewById(R.id.rbDeposit);
		deposit.setChecked(true);
		withdraw=(RadioButton)findViewById(R.id.rbWithdraw);
		etAmount=(EditText)findViewById(R.id.etAmount);
		etModenumber=(EditText)findViewById(R.id.etInstruNumber);
		etRemarks=(EditText)findViewById(R.id.etRemarks);
		btnDatePick=(ImageButton)findViewById(R.id.imgDatePick);
		addTransaction=(Button)findViewById(R.id.btnCreateTransaction);
		addTransaction.setOnClickListener(this);
	}
	
	private void setDefaultDate(){
		tvDate.setText(padDate());
	}	
	
	private String padDate(){
		String d,m,y;
		Calendar c=Calendar.getInstance();
		if(c.get(Calendar.DAY_OF_MONTH)<10)
			d="0"+(c.get(Calendar.DAY_OF_MONTH));
		else
			d=""+(c.get(Calendar.DAY_OF_MONTH)-1);
		if(c.get(Calendar.MONTH)-1<10)
			m="0"+(Calendar.MONTH-1);
		else
			m=""+(c.get(Calendar.MONTH)-1);		
		y=""+c.get(Calendar.YEAR);
		return d+"-"+m+"-"+y;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//need not inflate any menu for this activity
		return true;
	}

	private String getTransactionType(){
		String t="";
		if(withdraw.isChecked())
			t="withdraw";
		else if(deposit.isChecked())
			t="deposit";
		return t;		
	}
	
	private double getBalance(String acc){
		try{
			DBHelper dbhelper=new DBHelper(this);
			SQLiteDatabase db=dbhelper.getWritableDatabase();
			String sql="select "+Database.ACCOUNT_BALANCE+" from "+Database.ACCOUNT_TABLE_NAME+" where "+Database.ACCOUNT_NUMBER+" = "+acc;
			Cursor c=db.rawQuery(sql,null);
			c.moveToFirst();
			return c.getDouble(c.getColumnIndex(Database.ACCOUNT_BALANCE));
		}
		catch(Exception e){			
			return 0;
		}
	}
	
	private int noOfAccountCreated(){
		try{
			DBHelper dbhelper=new DBHelper(this);
			SQLiteDatabase db=dbhelper.getWritableDatabase();
			String sql="select * from "+Database.ACCOUNT_TABLE_NAME;
			Cursor c=db.rawQuery(sql,null);
			return c.getCount();
		}
		catch(Exception e){			
			return 0;
		}
	}
	@Override
	public void onClick(View arg0) {
		boolean flag=true;
		Transaction new_trans=new Transaction();		
		
		//checking for no account created condition
		if(noOfAccountCreated()==0){
			flag=false;
			Toast.makeText(getApplicationContext(), "No accounts selected/exists", Toast.LENGTH_SHORT).show();
		}
		else{
			StringTokenizer st=new StringTokenizer(spAcc.getSelectedItem().toString(), " -");
			new_trans.setAccount(st.nextToken().trim());			
		}
		
		//checking for blank amount
		String s=etAmount.getText().toString();
		if(s.trim().length()==0){
			flag=false;			
			Toast.makeText(getApplicationContext(), "Amount cant be blank",Toast.LENGTH_SHORT).show();
		}
		else
			new_trans.setAmount(s.trim());
		StringTokenizer st=new StringTokenizer(tvDate.getText().toString()," -");
		String d=st.nextToken();
		String m=st.nextToken();
		String y=st.nextToken();
		String date=y+""+m+""+d;
		new_trans.setDate(date.trim());
		new_trans.setMode(spMode.getSelectedItem().toString().trim());
		new_trans.setModeNumber(etModenumber.getText().toString().trim());
		new_trans.setRemarks(etRemarks.getText().toString().trim());
		if(getTransactionType().trim().length()==0){//transaction not selected
			flag=false;			
			Toast.makeText(getApplicationContext(), "Choose transaction type", Toast.LENGTH_SHORT).show();
		}
		else
			new_trans.setTransaction(getTransactionType().trim());
		double amount=0;
		if(flag)
			amount=getBalance(new_trans.getAccount().trim());
		if(flag && noOfAccountCreated()!=0 && new_trans.getTransaction().trim().equalsIgnoreCase("Withdraw")){
			if(Double.parseDouble(new_trans.getAmount())>amount){//checking to prevent balance underflow
				flag=false;
				Toast.makeText(getApplicationContext(), "Insufficient balance", Toast.LENGTH_SHORT).show();
			}
		}		
		if(flag){
			boolean done=Database.addTransaction(new_trans, this);
			if(done){
				Toast.makeText(getApplicationContext(), "Transaction added successfully",Toast.LENGTH_LONG).show();
				finish();
			}
			else
				Toast.makeText(getApplicationContext(), "Transaction failed",Toast.LENGTH_LONG).show();			
		}		
	}
}
