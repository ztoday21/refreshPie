package com.ztoday21.refreshman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class main extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.main);
	    
	    // 버튼 리스너 등록
	    findViewById(R.id.btStart).setOnClickListener(mClickListener);
	    findViewById(R.id.btStop).setOnClickListener(mClickListener);
	    findViewById(R.id.btBind).setOnClickListener(mClickListener);
	    findViewById(R.id.btUnbind).setOnClickListener(mClickListener);
	    findViewById(R.id.btExit).setOnClickListener(mClickListener);
	}

	private Button.OnClickListener mClickListener = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			switch(v.getId())
			{
			case R.id.btStart:
				{
					EditText teInterval = (EditText)findViewById(R.id.etInterval);
					service_main._interval = Integer.parseInt(teInterval.getText().toString());
					
					 Intent bindIntent = new Intent(main.this, service_main.class);
					 startService(bindIntent);
				}
				break;
					
			case R.id.btStop:
				{
					Intent bindIntent = new Intent(main.this, service_main.class);
	                stopService(bindIntent);
				}
				break;
					
			case R.id.btExit:
				{
					finish();
				}
				break;
			}
			
			Toast.makeText(main.this, ((Button)v).getText().toString() +  " 버튼 눌림", Toast.LENGTH_SHORT).show();
		}
	};
}
