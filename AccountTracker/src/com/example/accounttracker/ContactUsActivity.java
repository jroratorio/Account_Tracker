package com.example.accounttracker;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ContactUsActivity extends Activity implements OnClickListener {

	private String target;
	private Button sayan,anuran,rajorsi;
	
	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_us);
		findViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//need not inflate any menu for this activity
		return true;
	}
	
	private void findViews(){
		sayan=(Button)findViewById(R.id.btnSayanID);
		sayan.setOnClickListener(this);
		anuran=(Button)findViewById(R.id.btnAnuranID);
		anuran.setOnClickListener(this);
		rajorsi=(Button)findViewById(R.id.btnRajorsiID);
		rajorsi.setOnClickListener(this);				
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
			case R.id.btnSayanID:
				target=sayan.getText().toString();
				break;
			case R.id.btnAnuranID:
				target=anuran.getText().toString();
				break;
			case R.id.btnRajorsiID:
				target=rajorsi.getText().toString();
		}
		sendMail(target);		
	}
	
	public void sendMail(String mailto){
		Intent intent=new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto",mailto, null));
		intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback of Account Tracker");
		startActivity(intent);		
	}
}
