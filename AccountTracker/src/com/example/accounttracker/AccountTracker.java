package com.example.accounttracker;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class AccountTracker extends Application{
	
	public static String DBNAME = "PERSONS.db";
	public SharedPreferences sharedpref;
	Context ctx;
	public DBHelper dbhelper;
			
	public void setContext(Context c){
		ctx=c;
	}
	
	public Context getContext(){
		return ctx;
	}
	
	@Override
	public void onCreate() {		
		super.onCreate();		
		dbhelper=new DBHelper(getApplicationContext());
		dbhelper.getWritableDatabase();
	}
	
	//sure you want to exit? Yes, No
	public void exitHandler(Context ctx){
		AlertDialog.Builder adb=new AlertDialog.Builder(ctx);
        adb.setMessage("Are you sure you want to exit?");
        adb.setTitle("Exit");
        adb.setCancelable(false);
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {		
        	public void onClick(DialogInterface dialog, int which) {
        		System.exit(0);
        	}
        });
        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {		
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
        });
	    adb.setIcon(R.drawable.crossbutton);
	    adb.show();	    
	}
	
	//you must enter credentials to continue. OK
	public void exitHandler(Context ctx,boolean modePersist){
		AlertDialog.Builder adb=new AlertDialog.Builder(ctx);
        adb.setMessage("You must enter credentials to continue.");
        adb.setTitle("Warning");
        adb.setCancelable(false);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {		
        	public void onClick(DialogInterface dialog, int which) {
        		System.exit(0);
        	}
        });        
	    adb.setIcon(R.drawable.crossbutton);
	    adb.show();	    
	}	
	
	public void savePreferences(LoginInfo obj){
		sharedpref=PreferenceManager.getDefaultSharedPreferences(ctx);
		Editor editor = sharedpref.edit();
        editor.putString("userid",obj.getUsername());
        editor.putString("password",obj.getPassword());
        editor.commit();
        Toast.makeText(getContext(), "Preferences saved", Toast.LENGTH_LONG).show();
	}
	
	public LoginInfo getPreferences(){
		sharedpref=PreferenceManager.getDefaultSharedPreferences(ctx);
		String u= sharedpref.getString("userid", "");
		String p=sharedpref.getString("password", "");
		return new LoginInfo(u, p);
	}
}
