package com.example.accounttracker;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RecentTransActivity extends Activity implements OnItemLongClickListener{
	private ListView listTransactions;
	private Button btnDeleteTransaction;
	private String selectedID,from,to,l,u;
	private EditText fromDay,fromMonth,fromYear;
	private EditText toDay,toMonth,toYear;
	private EditText lower,upper;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent_trans);		
		Toast.makeText(getApplicationContext(), "Showing details of your previous transactions", Toast.LENGTH_SHORT).show();		
		listTransactions=(ListView)this.findViewById(R.id.listRecentTrans);		
		listTransactions.setOnItemLongClickListener(this);		
	}	
	
	private void inflateDateSearch(){
		AlertDialog.Builder adb=new AlertDialog.Builder(this);
		View v=LayoutInflater.from(getApplicationContext()).inflate(R.layout.searchdate,null);		
		fromDay=(EditText)v.findViewById(R.id.etFromDay);
		fromMonth=(EditText)v.findViewById(R.id.etFromMonth);
		fromYear=(EditText)v.findViewById(R.id.etFromYear);
		toDay=(EditText)v.findViewById(R.id.etToDay);
		toMonth=(EditText)v.findViewById(R.id.etToMonth);
		toYear=(EditText)v.findViewById(R.id.etToYear);
		adb.setCancelable(true);
		adb.setPositiveButton("Search", searchDateListener);
		adb.setView(v);
		adb.create().show();						
	}
	
	private void inflateAmountSearch(){
		AlertDialog.Builder adb=new AlertDialog.Builder(this);
		View v=LayoutInflater.from(getApplicationContext()).inflate(R.layout.searchamount,null);		
		lower=(EditText)v.findViewById(R.id.etLower);
		upper=(EditText)v.findViewById(R.id.etUpper);
		adb.setCancelable(true);
		adb.setPositiveButton("Search", searchAmountListener);
		adb.setView(v);
		adb.create().show();						
	}
	
	private boolean setDates(){
		String f_day=fromDay.getText().toString().trim();
		String f_month=fromMonth.getText().toString().trim();
		String f_year=fromYear.getText().toString().trim();
		String t_day=toDay.getText().toString().trim();
		String t_month=toMonth.getText().toString().trim();
		String t_year=toYear.getText().toString().trim();
		if(f_day.length()==0 || f_month.length()==0 || f_year.length()==0 || 
				t_day.length()==0 || t_month.length()==0 || t_year.length()==0){
			Toast.makeText(getApplicationContext(), "No field can be empty", Toast.LENGTH_LONG).show();
			inflateDateSearch();
			return false;
		}		
		if(f_day.trim().length()==1)
			f_day="0"+f_day;		
		if(f_month.trim().length()==1)
			f_month="0"+f_month;
		
		if(Integer.parseInt(f_day)>31 || Integer.parseInt(f_day)<=0){
			Toast.makeText(getApplicationContext(), "Day should be within 1-31", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(Integer.parseInt(f_month)>12 || Integer.parseInt(f_month)<=0){
			Toast.makeText(getApplicationContext(), "Month should be within 1-12", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		from=f_year+""+f_month+""+f_day;
		
		if(t_day.trim().length()==1)
			t_day="0"+t_day;		
		if(t_month.trim().length()==1)
			t_month="0"+t_month;		
		
		if(Integer.parseInt(t_day)>31 || Integer.parseInt(t_day)<=0){
			Toast.makeText(getApplicationContext(), "Day should be within 1-31", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(Integer.parseInt(t_month)>12 || Integer.parseInt(t_month)<=0){
			Toast.makeText(getApplicationContext(), "Month should be within 1-12", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		to=t_year+""+t_month+""+t_day;		
		return true;
	}
	
	private boolean setAmount(){
		String s1=lower.getText().toString().trim();
		String s2=upper.getText().toString().trim();
		if(s1.length()==0 || s2.length()==0){
			Toast.makeText(getApplicationContext(), "No field can be empty", Toast.LENGTH_LONG).show();
			inflateAmountSearch();
			return false;			
		}
		l=s1;
		u=s2;
		return true;
	}
	
	private DialogInterface.OnClickListener searchDateListener=new DialogInterface.OnClickListener(){		
		@Override
		public void onClick(DialogInterface dialog, int arg1){
			if(setDates())				
				searchDate(from,to);
		}
	};
	
	private DialogInterface.OnClickListener searchAmountListener=new DialogInterface.OnClickListener(){		
		@Override
		public void onClick(DialogInterface dialog, int arg1){
			if(setAmount())			
				searchAmount(l,u);
		}
	};
	
	private void searchDate(String from,String to){
		try {
			DBHelper dbhelper = new DBHelper(this);
			SQLiteDatabase db = dbhelper.getReadableDatabase();
			Cursor trans=db.rawQuery("select * from "+Database.TRANSACTION_TABLE_NAME+" where "+Database.TRANSACTION_DATE+" >= "+from+" and "+Database.TRANSACTION_DATE+" <="+to+" order by "+Database.TRANSACTION_DATE+" desc",null);
			if(trans.getCount()==0){				
				Toast.makeText(getApplicationContext(), "No results found", Toast.LENGTH_LONG).show();
				listTransactions.setAdapter(null);
			}
			else
				this.findViewById(R.id.tvNoTrans).setVisibility(View.INVISIBLE);
			
			ArrayList<Map<String,String>> listTrans = new ArrayList<Map<String,String>>();
            trans.moveToFirst();
			do{
            	// get trans details for display
            	LinkedHashMap<String,String> tran = new LinkedHashMap<String,String>();
            	tran.put("transid",trans.getString(trans.getColumnIndex(Database.TRANSACTION_ID)));
            	tran.put("acno",trans.getString(trans.getColumnIndex(Database.TRANSACTION_ACC_NO)) + " - " +  getBank(trans.getString(trans.getColumnIndex(Database.TRANSACTION_ACC_NO))));
            	tran.put("transdate",trans.getString(trans.getColumnIndex(Database.TRANSACTION_DATE)));
            	tran.put("transtype",trans.getString(trans.getColumnIndex(Database.TRANSACTION_TYPE)));
            	tran.put("transamount",trans.getString(trans.getColumnIndex(Database.TRANSACTION_AMOUNT)));
            	tran.put("transremarks",trans.getString(trans.getColumnIndex(Database.TRANSACTION_REMARKS)));
            	String mode=trans.getString(trans.getColumnIndex(Database.TRANSACTION_MODE));
            	String modeNo=trans.getString(trans.getColumnIndex(Database.TRANSACTION_MODE_NO));
            	String transDetails="Cash";
            	if(!mode.trim().equals("Cash"))
            		 transDetails=mode+" no: "+modeNo;
            	tran.put("transdetails",transDetails);
            	listTrans.add(tran);
            }while(trans.moveToNext());
            trans.close();
            db.close();
		    dbhelper.close();
		    
		    SimpleAdapter adapter=new SimpleAdapter(this,listTrans,R.layout.transaction, 
		    		new String [] {"transid","acno", "transdate", "transdetails", "transtype", "transamount" ,"transremarks"},
		    		new  int [] {R.id.tvTransID,R.id.tvAcno,  R.id.tvTransDate, R.id.tvTransDetails, R.id.tvTransType, R.id.tvTransAmount, R.id.tvTransRemarks});
		    listTransactions.setAdapter(adapter);
		}
		catch(Exception ex) {
			
		}		
	}
	
	private void searchAmount(String l,String u){
		try {
			DBHelper dbhelper = new DBHelper(this);
			SQLiteDatabase db = dbhelper.getReadableDatabase();
			Cursor trans=db.rawQuery("select "+Database.TRANSACTION_ID+","+Database.TRANSACTION_ACC_NO+","+Database.TRANSACTION_DATE+","+Database.TRANSACTION_AMOUNT+","+Database.TRANSACTION_TYPE+","+Database.TRANSACTION_MODE+","+Database.TRANSACTION_MODE_NO+","+Database.TRANSACTION_REMARKS+" from "+Database.TRANSACTION_TABLE_NAME+" where "+Database.TRANSACTION_AMOUNT+ ">= "+l+" and "+Database.TRANSACTION_AMOUNT+"<= "+u+" order by "+Database.TRANSACTION_DATE+" desc",null);
			if(trans.getCount()==0){				
				Toast.makeText(getApplicationContext(), "No results found", Toast.LENGTH_LONG).show();
				listTransactions.setAdapter(null);
			}
			else
				this.findViewById(R.id.tvNoTrans).setVisibility(View.INVISIBLE);
			
			ArrayList<Map<String,String>> listTrans = new ArrayList<Map<String,String>>();
            trans.moveToFirst();
			do{
            	// get trans details for display
            	LinkedHashMap<String,String> tran = new LinkedHashMap<String,String>();
            	tran.put("transid",trans.getString(trans.getColumnIndex(Database.TRANSACTION_ID)));
            	tran.put("acno",trans.getString(trans.getColumnIndex(Database.TRANSACTION_ACC_NO)) + " - " +  getBank(trans.getString(trans.getColumnIndex(Database.TRANSACTION_ACC_NO))));
            	tran.put("transdate",trans.getString(trans.getColumnIndex(Database.TRANSACTION_DATE)));
            	tran.put("transtype",trans.getString(trans.getColumnIndex(Database.TRANSACTION_TYPE)));
            	tran.put("transamount",trans.getString(trans.getColumnIndex(Database.TRANSACTION_AMOUNT)));
            	tran.put("transremarks",trans.getString(trans.getColumnIndex(Database.TRANSACTION_REMARKS)));
            	String mode=trans.getString(trans.getColumnIndex(Database.TRANSACTION_MODE));
            	String modeNo=trans.getString(trans.getColumnIndex(Database.TRANSACTION_MODE_NO));
            	String transDetails="Cash";
            	if(!mode.trim().equals("Cash"))
            		 transDetails=mode+" no: "+modeNo;
            	tran.put("transdetails",transDetails);
            	listTrans.add(tran);
            }while(trans.moveToNext());
            trans.close();
            db.close();
		    dbhelper.close();
		    
		    SimpleAdapter adapter=new SimpleAdapter(this,listTrans,R.layout.transaction, 
		    		new String [] {"transid","acno", "transdate", "transdetails", "transtype", "transamount" ,"transremarks"},
		    		new  int [] {R.id.tvTransID,R.id.tvAcno,  R.id.tvTransDate, R.id.tvTransDetails, R.id.tvTransType, R.id.tvTransAmount, R.id.tvTransRemarks});
		    listTransactions.setAdapter(adapter);
		}
		catch(Exception ex) {
			
		}		
	}
	
	private String getBank(String acc_no){
		DBHelper dbhelper=new DBHelper(this);
		SQLiteDatabase db=dbhelper.getWritableDatabase();
		String sql="select "+Database.ACCOUNT_BANK_NAME+" from "+Database.ACCOUNT_TABLE_NAME+" where "+Database.ACCOUNT_NUMBER+" = "+acc_no;
		Cursor c=db.rawQuery(sql,null);
		c.moveToFirst();
		return c.getString(c.getColumnIndex(Database.ACCOUNT_BANK_NAME));		
	}
	
	private void showAll(){
		try {
			DBHelper dbhelper = new DBHelper(this);
			SQLiteDatabase db = dbhelper.getReadableDatabase();
			Cursor trans=db.rawQuery("select "+Database.TRANSACTION_ID+","+Database.TRANSACTION_ACC_NO+","+Database.TRANSACTION_DATE+","+Database.TRANSACTION_AMOUNT+","+Database.TRANSACTION_TYPE+","+Database.TRANSACTION_MODE+","+Database.TRANSACTION_MODE_NO+","+Database.TRANSACTION_REMARKS+" from "+Database.TRANSACTION_TABLE_NAME,null);
			if(trans.getCount()==0){
				this.findViewById(R.id.tvNoTrans).setVisibility(View.VISIBLE);
				listTransactions.setAdapter(null);
			}
			else
				this.findViewById(R.id.tvNoTrans).setVisibility(View.INVISIBLE);
			
			ArrayList<Map<String,String>> listTrans = new ArrayList<Map<String,String>>();
            trans.moveToFirst();
			do{
            	// get trans details for display
            	LinkedHashMap<String,String> tran = new LinkedHashMap<String,String>();
            	tran.put("transid",trans.getString(trans.getColumnIndex(Database.TRANSACTION_ID)));
            	tran.put("acno",trans.getString(trans.getColumnIndex(Database.TRANSACTION_ACC_NO)) + " - " +  getBank(trans.getString(trans.getColumnIndex(Database.TRANSACTION_ACC_NO))));
            	tran.put("transdate",trans.getString(trans.getColumnIndex(Database.TRANSACTION_DATE)));
            	tran.put("transtype",trans.getString(trans.getColumnIndex(Database.TRANSACTION_TYPE)));
            	tran.put("transamount",trans.getString(trans.getColumnIndex(Database.TRANSACTION_AMOUNT)));
            	tran.put("transremarks",trans.getString(trans.getColumnIndex(Database.TRANSACTION_REMARKS)));
            	String mode=trans.getString(trans.getColumnIndex(Database.TRANSACTION_MODE));
            	String modeNo=trans.getString(trans.getColumnIndex(Database.TRANSACTION_MODE_NO));
            	String transDetails="Cash";
            	if(!mode.trim().equals("Cash"))
            		 transDetails=mode+" no: "+modeNo;
            	tran.put("transdetails",transDetails);
            	listTrans.add(tran);
            }while(trans.moveToNext());
            trans.close();
            db.close();
		    dbhelper.close();
		    
		    SimpleAdapter adapter=new SimpleAdapter(this,listTrans,R.layout.transaction, 
		    		new String [] {"transid","acno", "transdate", "transdetails", "transtype", "transamount" ,"transremarks"},
		    		new  int [] {R.id.tvTransID,R.id.tvAcno,  R.id.tvTransDate, R.id.tvTransDetails, R.id.tvTransType, R.id.tvTransAmount, R.id.tvTransRemarks});
		    listTransactions.setAdapter(adapter);
		}
		catch(Exception ex) {
			
		}
	}
	
	private void show25(){
		try {
			DBHelper dbhelper = new DBHelper(this);
			SQLiteDatabase db = dbhelper.getReadableDatabase();
			Cursor trans=db.rawQuery("select "+Database.TRANSACTION_ID+","+Database.TRANSACTION_ACC_NO+","+Database.TRANSACTION_DATE+","+Database.TRANSACTION_AMOUNT+","+Database.TRANSACTION_TYPE+","+Database.TRANSACTION_MODE+","+Database.TRANSACTION_MODE_NO+","+Database.TRANSACTION_REMARKS+" from "+Database.TRANSACTION_TABLE_NAME+" order by "+Database.TRANSACTION_DATE+" desc LIMIT 25",null);
			if(trans.getCount()==0){
				this.findViewById(R.id.tvNoTrans).setVisibility(View.VISIBLE);
				listTransactions.setAdapter(null);
			}
			else
				this.findViewById(R.id.tvNoTrans).setVisibility(View.INVISIBLE);
			
			ArrayList<Map<String,String>> listTrans = new ArrayList<Map<String,String>>();
            trans.moveToFirst();
			do{
            	// get trans details for display
            	LinkedHashMap<String,String> tran = new LinkedHashMap<String,String>();
            	tran.put("transid",trans.getString(trans.getColumnIndex(Database.TRANSACTION_ID)));
            	tran.put("acno",trans.getString(trans.getColumnIndex(Database.TRANSACTION_ACC_NO)) + " - " +  getBank(trans.getString(trans.getColumnIndex(Database.TRANSACTION_ACC_NO))));
            	tran.put("transdate",trans.getString(trans.getColumnIndex(Database.TRANSACTION_DATE)));
            	tran.put("transtype",trans.getString(trans.getColumnIndex(Database.TRANSACTION_TYPE)));
            	tran.put("transamount",trans.getString(trans.getColumnIndex(Database.TRANSACTION_AMOUNT)));
            	tran.put("transremarks",trans.getString(trans.getColumnIndex(Database.TRANSACTION_REMARKS)));
            	String mode=trans.getString(trans.getColumnIndex(Database.TRANSACTION_MODE));
            	String modeNo=trans.getString(trans.getColumnIndex(Database.TRANSACTION_MODE_NO));
            	String transDetails="Cash";
            	if(!mode.trim().equals("Cash"))
            		 transDetails=mode+" no: "+modeNo;
            	tran.put("transdetails",transDetails);
            	listTrans.add(tran);
            }while(trans.moveToNext()); 
            trans.close();
            db.close();
		    dbhelper.close();
		    
		    SimpleAdapter adapter=new SimpleAdapter(this,listTrans,R.layout.transaction, 
		    		new String [] {"transid","acno", "transdate", "transdetails", "transtype", "transamount" ,"transremarks"},
		    		new  int [] {R.id.tvTransID,R.id.tvAcno,  R.id.tvTransDate, R.id.tvTransDetails, R.id.tvTransType, R.id.tvTransAmount, R.id.tvTransRemarks});
		    listTransactions.setAdapter(adapter);
		}
		catch(Exception ex) {
			
		}
	}
	
	private void showSortedAccount(){
		try {
			DBHelper dbhelper = new DBHelper(this);
			SQLiteDatabase db = dbhelper.getReadableDatabase();
			Cursor trans=db.rawQuery("select "+Database.TRANSACTION_ID+","+Database.TRANSACTION_ACC_NO+","+Database.TRANSACTION_DATE+","+Database.TRANSACTION_AMOUNT+","+Database.TRANSACTION_TYPE+","+Database.TRANSACTION_MODE+","+Database.TRANSACTION_MODE_NO+","+Database.TRANSACTION_REMARKS+" from "+Database.TRANSACTION_TABLE_NAME+" order by "+Database.TRANSACTION_ACC_NO,null);
			if(trans.getCount()==0){
				this.findViewById(R.id.tvNoTrans).setVisibility(View.VISIBLE);
				listTransactions.setAdapter(null);
			}
			else
				this.findViewById(R.id.tvNoTrans).setVisibility(View.INVISIBLE);
			
			ArrayList<Map<String,String>> listTrans = new ArrayList<Map<String,String>>();
            trans.moveToFirst();
			do{
            	// get trans details for display
            	LinkedHashMap<String,String> tran = new LinkedHashMap<String,String>();
            	tran.put("transid",trans.getString(trans.getColumnIndex(Database.TRANSACTION_ID)));
            	tran.put("acno",trans.getString(trans.getColumnIndex(Database.TRANSACTION_ACC_NO)) + " - " +  getBank(trans.getString(trans.getColumnIndex(Database.TRANSACTION_ACC_NO))));
            	tran.put("transdate",trans.getString(trans.getColumnIndex(Database.TRANSACTION_DATE)));
            	tran.put("transtype",trans.getString(trans.getColumnIndex(Database.TRANSACTION_TYPE)));
            	tran.put("transamount",trans.getString(trans.getColumnIndex(Database.TRANSACTION_AMOUNT)));
            	tran.put("transremarks",trans.getString(trans.getColumnIndex(Database.TRANSACTION_REMARKS)));
            	String mode=trans.getString(trans.getColumnIndex(Database.TRANSACTION_MODE));
            	String modeNo=trans.getString(trans.getColumnIndex(Database.TRANSACTION_MODE_NO));
            	String transDetails="Cash";
            	if(!mode.trim().equals("Cash"))
            		 transDetails=mode+" no: "+modeNo;
            	tran.put("transdetails",transDetails);
            	listTrans.add(tran);
            }while(trans.moveToNext());
            trans.close();
            db.close();
		    dbhelper.close();
		    
		    SimpleAdapter adapter=new SimpleAdapter(this,listTrans,R.layout.transaction, 
		    		new String [] {"transid","acno", "transdate", "transdetails", "transtype", "transamount" ,"transremarks"},
		    		new  int [] {R.id.tvTransID,R.id.tvAcno,  R.id.tvTransDate, R.id.tvTransDetails, R.id.tvTransType, R.id.tvTransAmount, R.id.tvTransRemarks});
		    listTransactions.setAdapter(adapter);
		}
		catch(Exception ex) {
			
		}
	}
	
	private void showSortedDate(){
		try {
			DBHelper dbhelper = new DBHelper(this);
			SQLiteDatabase db = dbhelper.getReadableDatabase();
			Cursor trans=db.rawQuery("select "+Database.TRANSACTION_ID+","+Database.TRANSACTION_ACC_NO+","+Database.TRANSACTION_DATE+","+Database.TRANSACTION_AMOUNT+","+Database.TRANSACTION_TYPE+","+Database.TRANSACTION_MODE+","+Database.TRANSACTION_MODE_NO+","+Database.TRANSACTION_REMARKS+" from "+Database.TRANSACTION_TABLE_NAME+" order by "+Database.TRANSACTION_DATE+" desc",null);
			if(trans.getCount()==0){
				this.findViewById(R.id.tvNoTrans).setVisibility(View.VISIBLE);
				listTransactions.setAdapter(null);
			}
			else
				this.findViewById(R.id.tvNoTrans).setVisibility(View.INVISIBLE);
			
			ArrayList<Map<String,String>> listTrans = new ArrayList<Map<String,String>>();
            trans.moveToFirst();
			do{
            	// get trans details for display
            	LinkedHashMap<String,String> tran = new LinkedHashMap<String,String>();
            	tran.put("transid",trans.getString(trans.getColumnIndex(Database.TRANSACTION_ID)));
            	tran.put("acno",trans.getString(trans.getColumnIndex(Database.TRANSACTION_ACC_NO)) + " - " +  getBank(trans.getString(trans.getColumnIndex(Database.TRANSACTION_ACC_NO))));
            	tran.put("transdate",trans.getString(trans.getColumnIndex(Database.TRANSACTION_DATE)));
            	tran.put("transtype",trans.getString(trans.getColumnIndex(Database.TRANSACTION_TYPE)));
            	tran.put("transamount",trans.getString(trans.getColumnIndex(Database.TRANSACTION_AMOUNT)));
            	tran.put("transremarks",trans.getString(trans.getColumnIndex(Database.TRANSACTION_REMARKS)));
            	String mode=trans.getString(trans.getColumnIndex(Database.TRANSACTION_MODE));
            	String modeNo=trans.getString(trans.getColumnIndex(Database.TRANSACTION_MODE_NO));
            	String transDetails="Cash";
            	if(!mode.trim().equals("Cash"))
            		 transDetails=mode+" no: "+modeNo;
            	tran.put("transdetails",transDetails);
            	listTrans.add(tran);
            }while(trans.moveToNext()); 
            trans.close();
            db.close();
		    dbhelper.close();
		    
		    SimpleAdapter adapter=new SimpleAdapter(this,listTrans,R.layout.transaction, 
		    		new String [] {"transid","acno", "transdate", "transdetails", "transtype", "transamount" ,"transremarks"},
		    		new  int [] {R.id.tvTransID,R.id.tvAcno,  R.id.tvTransDate, R.id.tvTransDetails, R.id.tvTransType, R.id.tvTransAmount, R.id.tvTransRemarks});
		    listTransactions.setAdapter(adapter);
		}
		catch(Exception ex) {
			
		}
	}
	
	private void showContext(){
		AlertDialog.Builder adb=new AlertDialog.Builder(this);
		View v=LayoutInflater.from(getApplicationContext()).inflate(R.layout.transactioncontext,null);
		btnDeleteTransaction=(Button)v.findViewById(R.id.btnDeleteTransaction);
		btnDeleteTransaction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				deleteTransaction(selectedID);
				onStart();				
			}
		});
		adb.setCancelable(true);		
		adb.setView(v);
		adb.create().show();		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		showAll();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recent_trans, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.itemShowAll:
				Toast.makeText(getApplicationContext(), "Showing all transactions", Toast.LENGTH_LONG).show();
				showAll();
				break;
			case R.id.itemSortAccountNumber:
				Toast.makeText(getApplicationContext(), "Sorted according to account number", Toast.LENGTH_LONG).show();
				showSortedAccount();
				break;
			case R.id.itemSortDate:
				Toast.makeText(getApplicationContext(), "Sorted according to date", Toast.LENGTH_LONG).show();
				showSortedDate();
				break;
			case R.id.itemShow25:
				Toast.makeText(getApplicationContext(), "Showing last 25 transactions", Toast.LENGTH_LONG).show();
				show25();
				break;
			case R.id.itemSearchDate:
				inflateDateSearch();
				break;
			case R.id.itemSearchAmount:
				inflateAmountSearch();
				break;
		}
		return true;
	}

	private void deleteTransaction(String trans_id){
		DBHelper dbhelper=new DBHelper(this);
		SQLiteDatabase db=dbhelper.getWritableDatabase();
		String sql="delete from "+Database.TRANSACTION_TABLE_NAME+" where "+Database.TRANSACTION_ID+" = "+trans_id;
		try{
			db.execSQL(sql);
			Toast.makeText(getApplicationContext(), "Transaction deleted successfully", Toast.LENGTH_LONG).show();
		}
		catch(Exception e){
			Toast.makeText(getApplicationContext(), "Couldn't delete transaction", Toast.LENGTH_LONG).show();
		}		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int arg2,
			long arg3) {
		TextView tv=(TextView)v.findViewById(R.id.tvTransID);
		selectedID=tv.getText().toString();
		showContext();
		onStart();
		return false;
	}	
}
