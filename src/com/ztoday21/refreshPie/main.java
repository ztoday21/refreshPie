package com.ztoday21.refreshPie;

import com.ztoday21.refreshPie.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class main extends Activity {
	
	public static String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/refreshman/";
	public static String fileName = "refresh_interval";

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
	    
		// 스트림 읽기
		try
		{
			SharedPreferences prefs = getSharedPreferences("refreshman", MODE_PRIVATE);

			EditText teInterval = (EditText)findViewById(R.id.etInterval);
			teInterval.setText( prefs.getString("interval", "5") );
		}
		catch(Exception e) 
		{
			Toast.makeText(main.this, e.toString(), Toast.LENGTH_LONG).show();
		}

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
					// 파일 저장
					// 스트림 쓰기
					try
					{
						SharedPreferences prefs = getSharedPreferences("refreshman", MODE_PRIVATE);
						SharedPreferences.Editor ed = prefs.edit();

						EditText teInterval = (EditText)findViewById(R.id.etInterval);
						ed.putString("interval", teInterval.getText().toString());
						ed.commit();
					}
					catch(Exception e) 
					{
						Toast.makeText(main.this, e.toString(), Toast.LENGTH_LONG).show();
					}
					
					
					finish();
				}
				break;
			}
		}
	};
}
