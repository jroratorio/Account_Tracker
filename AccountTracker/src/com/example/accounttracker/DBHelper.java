package com.example.accounttracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	
	public static final int DB_VER=1;
	public static String DBNAME="PERSONS.db";
		
	public DBHelper(Context ctx) {
		super(ctx, DBNAME, null, DB_VER);		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		
		createTables(db);		
	}
	
	private void createTables(SQLiteDatabase db){
		//create both tables query
		String account_table_sql = "create table " + Database.ACCOUNT_TABLE_NAME + " ( " +
				Database.ACCOUNT_NUMBER + " integer primary key," +
				Database.ACCOUNT_HOLDERS + " varchar," +
				Database.ACCOUNT_BANK_NAME + " varchar," +
				Database.ACCOUNT_BRANCH_NAME + " varchar," + 
				Database.ACCOUNT_BRANCH_ADDRESS + " varchar," +
				Database.ACCOUNT_IFSC + " varchar," + 
				Database.ACCOUNT_BALANCE + " float)";
		
		String transaction_table_sql = "create table " + Database.TRANSACTION_TABLE_NAME + " ( " +
				Database.TRANSACTION_ID 	+ " integer  primary key autoincrement," + 
				Database.TRANSACTION_ACC_NO + " integer," +
				Database.TRANSACTION_DATE + " integer," +
				Database.TRANSACTION_AMOUNT + " float," +
				Database.TRANSACTION_TYPE+ " varchar," +
				Database.TRANSACTION_MODE + " varchar," +
				Database.TRANSACTION_MODE_NO + " varchar," +
				Database.TRANSACTION_REMARKS + " varchar)";
		
		try {
			db.execSQL(account_table_sql);			
			db.execSQL(transaction_table_sql);			
		}
		catch(Exception ex){
			
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
				
	}
}
