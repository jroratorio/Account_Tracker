package com.example.accounttracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnItemLongClickListener{
	
	private final Context ctx=this;
	private AccountTracker accountTracker;
	private Button btnAddAccount,btnAddTrans,btnRecentTrans,btnForgotPassword;
	private EditText userIDcreate,passwordCreate,confirmPasswordCreate;
	private EditText userIDLogin,passwordLogin;
	private EditText oldPassword,newPassword,confirmNewPassword;
	private EditText userIDreset,passwordReset,confirmPasswordReset;
	private ListView listAccount;
	private DBHelper dbhelper;
	private String clickedAcc;
	private Button btnEditAccount,btnDeleteAccount;
	
	private SharedPreferences prefs;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setApplicationContext();
		findViews();
		setAddAccountButton();
		setAddTransButton();
		setRecentTransButton();
		new CredentialHandler().showloginDialog();
		dbhelper=new DBHelper(this);
		dbhelper.getWritableDatabase();
	}
		
	@Override
	protected void onStart() {
		super.onStart();
		showAccount();
	}

	//nested class handling required functionalities regarding credential management
	class CredentialHandler{		
		public void showloginDialog() {
			prefs=accountTracker.sharedpref;
			if(prefs==null){
				//set up for first time
				firstTimeDialogBuilder();
			}
			else{
				//prompt to enter userid and password				
				normalLoginDialogBuilder();				
			}		
		}
		
		private void normalLoginDialogBuilder(){
			AlertDialog.Builder adb=new AlertDialog.Builder(ctx);
			View v=LayoutInflater.from(getApplicationContext()).inflate(R.layout.normallogin,null);
			userIDLogin=(EditText)v.findViewById(R.id.etUserIDLogin);
			passwordLogin=(EditText)v.findViewById(R.id.etEnterpasswordLogin);
			btnForgotPassword=(Button)v.findViewById(R.id.btnForgotPassword);
			btnForgotPassword.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					resetLoginDialogBuilder();					
				}
			});
			adb.setCancelable(false);
			adb.setPositiveButton("Login",loginListener);
			adb.setNegativeButton("Exit", exitListener);
			adb.setView(v);
			adb.create().show();
		}
		
		private void firstTimeDialogBuilder(){
			AlertDialog.Builder adb=new AlertDialog.Builder(ctx);
			View v=LayoutInflater.from(getApplicationContext()).inflate(R.layout.newuser,null);
			userIDcreate=(EditText)v.findViewById(R.id.etUserIDcreate);
			passwordCreate=(EditText)v.findViewById(R.id.etEnterpasswordCreate);
			confirmPasswordCreate=(EditText)v.findViewById(R.id.etConfirmPasswordCreate);
			adb.setCancelable(false);
			adb.setPositiveButton("Create User",createListener);
			adb.setNegativeButton("Cancel", cancelListener);
			adb.setView(v);
			adb.create().show();
		}
		
		private void changeCredentialsDialogBuilder(){
			AlertDialog.Builder adb=new AlertDialog.Builder(ctx);
			View v=LayoutInflater.from(getApplicationContext()).inflate(R.layout.changecredentials,null);
			oldPassword=(EditText)v.findViewById(R.id.etOldPassword);
			newPassword=(EditText)v.findViewById(R.id.etEnterpasswordChange);
			confirmNewPassword=(EditText)v.findViewById(R.id.etConfirmPasswordChange);
			adb.setCancelable(false);
			adb.setPositiveButton("Change Password",passwordUpdateListener);
			adb.setNegativeButton("Cancel", cancelPasswordUpdateListener);
			adb.setView(v);
			adb.create().show();
		}
		
		private void resetLoginDialogBuilder(){
			AlertDialog.Builder adb=new AlertDialog.Builder(ctx);
			View v=LayoutInflater.from(getApplicationContext()).inflate(R.layout.resetcredentials,null);
			userIDreset=(EditText)v.findViewById(R.id.etUserIDReset);
			passwordReset=(EditText)v.findViewById(R.id.etEnterpasswordReset);
			confirmPasswordReset=(EditText)v.findViewById(R.id.etConfirmPasswordReset);
			adb.setCancelable(true);
			adb.setPositiveButton("Change Password",passwordResetListener);
			adb.setNegativeButton("Cancel", cancelPasswordResetListener);
			adb.setView(v);
			adb.create().show();			
		}
		
		private void adduserID(){
			String u=userIDcreate.getText().toString().trim();
			String p=passwordCreate.getText().toString().trim();
			String cp=confirmPasswordCreate.getText().toString().trim();
			if(u.trim().length()>=1 && p.equals(cp) && p.length()>=6){
				accountTracker.savePreferences(new LoginInfo(u, p));
			}
			else{
				Toast.makeText(getApplicationContext(), "Field(s) missing/mismatch or insufficient password length", Toast.LENGTH_LONG).show();
				firstTimeDialogBuilder();
			}
		}
		
		private void resetUserID(){
			String u=userIDreset.getText().toString().trim();
			String p=passwordReset.getText().toString().trim();
			String cp=confirmPasswordReset.getText().toString().trim();
			if(u.equals(accountTracker.getPreferences().getUsername())){
				if(p.length()>=6){
					if(p.equals(cp)){
						accountTracker.savePreferences(new LoginInfo(accountTracker.getPreferences().getUsername(),p));							
					}
					else{
						Toast.makeText(getApplicationContext(), "Verify password", Toast.LENGTH_LONG).show();
						resetLoginDialogBuilder();
					}
				}
				else{
					Toast.makeText(getApplicationContext(), "Field(s) empty/Password insufficient length", Toast.LENGTH_LONG).show();
					resetLoginDialogBuilder();						
				}					
			}
			else{
				Toast.makeText(getApplicationContext(), "UserID mismatch", Toast.LENGTH_LONG).show();
				resetLoginDialogBuilder();
			}			
		}
		
		private void login(DialogInterface dialog){
			LoginInfo li=accountTracker.getPreferences();
			String u=userIDLogin.getText().toString().trim();
			String p=passwordLogin.getText().toString().trim();
			if(u.equals(li.getUsername()) && p.equals(li.getPassword())){
				dialog.dismiss();
				Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
			}				
			else{
				Toast.makeText(getApplicationContext(), "Invalid login credentials", Toast.LENGTH_LONG).show();
				normalLoginDialogBuilder();
			}			
		}
		
		private void updatePassword(){
			String o=oldPassword.getText().toString().trim();
			String n=newPassword.getText().toString().trim();
			String cn=confirmNewPassword.getText().toString().trim();
			LoginInfo li=accountTracker.getPreferences();
			if(!o.equals(li.getPassword())){
				Toast.makeText(getApplicationContext(), "Old password mismatch", Toast.LENGTH_LONG).show();
				changeCredentialsDialogBuilder();
			}
			else if (o.equals(li.getPassword()) && !n.equals(cn)){
				Toast.makeText(getApplicationContext(), "Verify new password", Toast.LENGTH_LONG).show();
				changeCredentialsDialogBuilder();
			}
			else if(o.equals(li.getPassword()) && n.equals(cn) && n.length()<6){
				Toast.makeText(getApplicationContext(), "Password length minimum 6", Toast.LENGTH_LONG).show();
				changeCredentialsDialogBuilder();
			}
			else{				
				//stub to save the new password
				accountTracker.savePreferences(new LoginInfo(li.getUsername(),n));
			}
		}
		
		private DialogInterface.OnClickListener createListener=new DialogInterface.OnClickListener(){		
			@Override
			public void onClick(DialogInterface dialog, int arg1){
				adduserID();
			}
		};
		
		private DialogInterface.OnClickListener cancelListener=new DialogInterface.OnClickListener(){		
			@Override
			public void onClick(DialogInterface arg0, int arg1){
				accountTracker.exitHandler(ctx,true);//mode persist exist handler			
			}
		};
		
		private DialogInterface.OnClickListener passwordUpdateListener=new DialogInterface.OnClickListener(){		
			@Override
			public void onClick(DialogInterface dialog, int arg1){
				updatePassword();
			}
		};
		
		private DialogInterface.OnClickListener cancelPasswordUpdateListener=new DialogInterface.OnClickListener(){		
			@Override
			public void onClick(DialogInterface dialog, int arg1){
				dialog.dismiss();
			}
		};
		
		private DialogInterface.OnClickListener loginListener=new DialogInterface.OnClickListener(){		
			@Override
			public void onClick(DialogInterface dialog, int arg1){
				login(dialog);									
			}
		};
		
		private DialogInterface.OnClickListener passwordResetListener=new DialogInterface.OnClickListener(){		
			@Override
			public void onClick(DialogInterface arg0, int arg1){
				resetUserID();				
			}
		};
		
		private DialogInterface.OnClickListener cancelPasswordResetListener=new DialogInterface.OnClickListener(){		
			@Override
			public void onClick(DialogInterface dialog, int arg1){
				dialog.dismiss();			
			}
		};
		
		private DialogInterface.OnClickListener exitListener=new DialogInterface.OnClickListener(){		
			@Override
			public void onClick(DialogInterface arg0, int arg1){
				finish();//end activity			
			}
		};
	}	
	
	//import/export of database to/from SDCard
	class ImportExport{
		public void exportDatabase() throws IOException{
			String inFileName="/data/data/com.example.accounttracker/databases/PERSONS.db";						
			File dbFile=new File(inFileName);
			FileInputStream fis=new FileInputStream(dbFile);			
			String outFileName=Environment.getExternalStorageDirectory()+"/PERSONS.db";
			File target=new File(outFileName);
			OutputStream output=new FileOutputStream(target);			
			byte[] buffer=new byte[1024];
			int length;
			while((length=fis.read(buffer))>0){
				output.write(buffer,0, length);
			}                                
			output.flush();
			output.close();
			fis.close();
		}
		
		public void importDatabase() throws IOException,FileNotFoundException{
			String outFileName="/data/data/com.example.accounttracker/databases/PERSONS.db";
			String inFileName=Environment.getExternalStorageDirectory()+"/PERSONS.db";						
			File dbFile=new File(inFileName);
			FileInputStream fis=new FileInputStream(dbFile);						
			File target=new File(outFileName);
			OutputStream output=new FileOutputStream(target);			
			byte[] buffer=new byte[1024];
			int length;
			while((length=fis.read(buffer))>0){
				output.write(buffer,0, length);
			}
			output.flush();
			output.close();
			fis.close();
		}
	}
	
	private void setApplicationContext(){
		accountTracker=(AccountTracker)getApplication();
		accountTracker.setContext(getApplicationContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}	
		
	private void setAddAccountButton(){		
		btnAddAccount.setOnClickListener(new OnClickListener() {			
			@Override			
			public void onClick(View arg0) {
				startActivity(new Intent(accountTracker,AddAccountActivity.class));				
			}
		});
	}	
	
	private void setAddTransButton(){
		btnAddTrans.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(accountTracker,AddTransActivity.class));				
			}
		});
	}
	
	private void setRecentTransButton(){
		btnRecentTrans.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(accountTracker,RecentTransActivity.class));				
			}
		});
	}
	
	private void findViews(){
		btnAddAccount=(Button)findViewById(R.id.btnAddAccount);
		btnAddTrans=(Button)findViewById(R.id.btnAddTrans);
		btnRecentTrans=(Button)findViewById(R.id.btnRecentTrans);
		listAccount=(ListView)findViewById(R.id.listAccount);
		listAccount.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int arg2,long arg3) {
				TextView tv=(TextView)v.findViewById(R.id.tvAccountNumber);
				String item=tv.getText().toString();
				clickedAcc=item;
				Intent intent = new Intent(accountTracker, EditAccountActivity.class);
				intent.putExtra("account_no",item);
				startActivity(intent);				
			}		
		});
		listAccount.setOnItemLongClickListener(this);
	}
	
	private void showAccount(){
		try {
			dbhelper = new DBHelper(this);
			SQLiteDatabase db = dbhelper.getReadableDatabase();
			Cursor acc=db.rawQuery("select "+Database.ACCOUNT_NUMBER+","+Database.ACCOUNT_BANK_NAME+","+Database.ACCOUNT_BALANCE+" from "+Database.ACCOUNT_TABLE_NAME,null);
			if(acc.getCount()==0){
				this.findViewById(R.id.tvNoAccounts).setVisibility(View.VISIBLE);
				listAccount.setAdapter(null);
			}
			else
				this.findViewById(R.id.tvNoAccounts).setVisibility(View.INVISIBLE);
			
			ArrayList<Map<String,String>> listAcc = new ArrayList<Map<String,String>>();
            acc.moveToFirst();
			do{
            	// get acc details for display
            	LinkedHashMap<String,String> lhm = new LinkedHashMap<String,String>();
            	lhm.put("acno",acc.getString(acc.getColumnIndex(Database.ACCOUNT_NUMBER)));
            	lhm.put("bankname",acc.getString(acc.getColumnIndex(Database.ACCOUNT_BANK_NAME)));
            	lhm.put("balance",acc.getString(acc.getColumnIndex(Database.ACCOUNT_BALANCE)));
            	listAcc.add(lhm);
            }while(acc.moveToNext());
            acc.close();
            db.close();
		    dbhelper.close();
		    
		    SimpleAdapter adapter=new SimpleAdapter(this,listAcc,R.layout.account, 
		    		new String [] {"acno","bankname", "balance"},
		    		new  int [] {R.id.tvAccountNumber,R.id.tvBankName, R.id.tvAccountBalance});
		    listAccount.setAdapter(adapter);
		}
		catch(Exception ex) {
			
		}
	}
	
	private void restartAppDialog(){
		AlertDialog.Builder adb=new AlertDialog.Builder(ctx);
        adb.setMessage("You must restart the app for changes to take effect.");
        adb.setTitle("Restart");
        adb.setCancelable(false);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {		
        	public void onClick(DialogInterface dialog, int which) {
        		finish();
        	}
        });        
	    adb.setIcon(R.drawable.crossbutton);
	    adb.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.action_contactUs:
				startActivity(new Intent(accountTracker, ContactUsActivity.class));
				break;
			case R.id.itemExit:
				accountTracker.exitHandler(ctx);
				break;
			case R.id.itemChangeCredentials:
				new CredentialHandler().changeCredentialsDialogBuilder();
				break;
			case R.id.itemImportDB:
				try {
					Runtime.getRuntime().exec("su");
					new ImportExport().importDatabase();
					Toast.makeText(getApplicationContext(), "Database imported", Toast.LENGTH_LONG).show();
					restartAppDialog();
				}
				catch(FileNotFoundException e){
					Toast.makeText(getApplicationContext(), "PERSONS.db not found in /sdcard", Toast.LENGTH_LONG).show();					
				}
				catch (IOException e) {
					Toast.makeText(getApplicationContext(), "Import error", Toast.LENGTH_LONG).show();
				}				
				break;
			case R.id.itemExportDB:				
				try{
					new ImportExport().exportDatabase();
					Toast.makeText(getApplicationContext(), "Database backed up successfully", Toast.LENGTH_LONG).show();
				}
				catch(IOException e){
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
				}
				break;
		}
		return true;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int arg2,			
			long arg3) {
		TextView tv=(TextView)v.findViewById(R.id.tvAccountNumber);
		clickedAcc=tv.getText().toString();
		showContext();
		onStart();		
		return false;
	}
	
	private void showContext(){
		AlertDialog.Builder adb=new AlertDialog.Builder(this);
		View v=LayoutInflater.from(getApplicationContext()).inflate(R.layout.accountcontext,null);
		btnEditAccount=(Button)v.findViewById(R.id.btnEditAcc);
		btnDeleteAccount=(Button)v.findViewById(R.id.btnDeleteAcc);
		btnEditAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i=new Intent(accountTracker, EditAccountActivity.class);
				i.putExtra("account_no",clickedAcc);
				startActivity(i);
			}
		});
		
		btnDeleteAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				deleteAccount(clickedAcc);
				onStart();				
			}
		});
		adb.setCancelable(true);		
		adb.setView(v);
		adb.create().show();		
	}
	
	private void deleteAccount(String acc_no){
		DBHelper dbhelper=new DBHelper(this);
		SQLiteDatabase db=dbhelper.getWritableDatabase();
		String sql_delete_trans="delete from "+Database.TRANSACTION_TABLE_NAME+" where "+Database.TRANSACTION_ACC_NO+" = "+acc_no;
		String sql_delete_acc="delete from "+Database.ACCOUNT_TABLE_NAME+" where "+Database.ACCOUNT_NUMBER+" = "+acc_no;
		try{
			db.execSQL(sql_delete_trans);
			db.execSQL(sql_delete_acc);
			Toast.makeText(getApplicationContext(), "Account deleted successfully", Toast.LENGTH_LONG).show();
		}
		catch(Exception e){
			
		}
	}	
}