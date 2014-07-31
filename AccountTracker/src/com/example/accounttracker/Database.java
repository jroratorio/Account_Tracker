package com.example.accounttracker;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Database {
	public static final String ACCOUNT_TABLE_NAME="Accounts";
	public static final String ACCOUNT_NUMBER="ACC_NO";
	public static final String ACCOUNT_BANK_NAME="BANK_NAME";
	public static final String ACCOUNT_BRANCH_NAME="BRANCH_NAME";
	public static final String ACCOUNT_BRANCH_ADDRESS="BRANCH_ADDRESS";
	public static final String ACCOUNT_HOLDERS="ACC_HOLDERS";
	public static final String ACCOUNT_IFSC="IFSC";
	public static final String ACCOUNT_BALANCE="BALANCE";
	
	public static final String TRANSACTION_TABLE_NAME="Transactions";
	public static final String TRANSACTION_ID="TRANSACTION_ID";
	public static final String TRANSACTION_ACC_NO="ACC_NO";
	public static final String TRANSACTION_TYPE="TYPE";
	public static final String TRANSACTION_AMOUNT="AMOUNT";
	public static final String TRANSACTION_MODE="MODE";
	public static final String TRANSACTION_MODE_NO="MODE_NO";
	public static final String TRANSACTION_DATE="DATE";
	public static final String TRANSACTION_REMARKS="REMARKS";
	
	public static Account cursorToAccount(Cursor accounts) {
		Account acc = new Account();
		acc.setAccNumber(accounts.getString(accounts.getColumnIndex(Database.ACCOUNT_NUMBER)));
		acc.setAccountHolders(accounts.getString(accounts.getColumnIndex(Database.ACCOUNT_HOLDERS)));
		acc.setBankName(accounts.getString(accounts.getColumnIndex(Database.ACCOUNT_BANK_NAME)));
        return acc;
	}
	
	public static void populateAccounts(Spinner spinnerAccounts) {
		Context context = spinnerAccounts.getContext();
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor accounts = db.query(Database.ACCOUNT_TABLE_NAME, null, null,null, null, null, null);
		ArrayList<String> list = new ArrayList<String>();

	    while (accounts.moveToNext()) {
			Account account =  Database.cursorToAccount(accounts);
			list.add(account.getAccNumber()+" - "+account.getBankName()+" - "+account.getAccountHolders());
		}
		accounts.close();
		db.close();
		dbhelper.close();
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,list);
		spinnerAccounts.setAdapter(adapter);
		spinnerAccounts.setSelection(0);
	}
	
	public static boolean updateAccountBalance(SQLiteDatabase db, String accNo, String transType, double amount, String transDate) {
		try{
			if(transType.equals("deposit"))  
			    db.execSQL("update "+Database.ACCOUNT_TABLE_NAME + " set BALANCE = BALANCE +" +amount+ " where " + Database.ACCOUNT_NUMBER+ "=" +accNo);
	        else{		     	
	        	db.execSQL("update " +Database.ACCOUNT_TABLE_NAME + " set BALANCE = BALANCE - " +amount+ " where " + Database.ACCOUNT_NUMBER + "=" + accNo);
	        }
	        return true; 
		}
		catch(Exception ex) {			
			return false; 
		}
	}	
	
	public static boolean addTransaction(Transaction trans,Context ctx){
		DBHelper dbhelper=null;
		SQLiteDatabase db=null;
		try {
			dbhelper = new DBHelper(ctx);
			db=dbhelper.getWritableDatabase();
			db.beginTransaction();
			// execute insert command
			ContentValues values = new ContentValues();
			values.put(Database.TRANSACTION_ACC_NO, trans.getAccount());
			values.put(Database.TRANSACTION_DATE, trans.getDate());
			values.put(Database.TRANSACTION_AMOUNT,trans.getAmount());
			values.put(Database.TRANSACTION_MODE_NO,trans.getModeNumber());
			values.put(Database.TRANSACTION_REMARKS,trans.getRemarks());
			values.put(Database.TRANSACTION_MODE, trans.getMode());
			values.put(Database.TRANSACTION_TYPE, trans.getTransaction());

			long rowid=db.insert(Database.TRANSACTION_TABLE_NAME,null,values);
			
			if(rowid!=-1){
				 // update Accounts Table 
				 boolean done=Database.updateAccountBalance(db,trans.getAccount(),trans.getTransaction(),Double.parseDouble(trans.getAmount()),trans.getDate());
 			     if(done){
					 db.setTransactionSuccessful();
					 db.endTransaction();
					 return true;
				 }
				 else {
					 db.endTransaction();
					 return false; 
				 }
			}
			else
				return false; 
	    }
		catch(Exception ex) {			
			return false; 
		}
		finally {
			if (db!=null && db.isOpen()) {
				db.close();
			}
		}
	}
}
