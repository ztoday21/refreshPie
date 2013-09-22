package com.ztoday21.refreshPie;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class main extends Activity {
	
	public static String _saveName = "refreshPie";
	public static final String defaultInterval = "3";
	public static final String defaultTimeInterval = "200";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.main);
	    
	    // 버튼 리스너 등록
	    findViewById(R.id.btStart).setOnClickListener(mClickListener);
	    findViewById(R.id.btStop).setOnClickListener(mClickListener);
	    findViewById(R.id.btSetting).setOnClickListener(mClickListener);
//	    findViewById(R.id.btBind).setOnClickListener(mClickListener);
//	    findViewById(R.id.btUnbind).setOnClickListener(mClickListener);
	    findViewById(R.id.btExit).setOnClickListener(mClickListener);
	    
		// 스트림 읽기
		try
		{			SharedPreferences prefs = getSharedPreferences(_saveName, MODE_PRIVATE);


			EditText etInterval = (EditText)findViewById(R.id.etInterval);
			etInterval.setText( prefs.getString("interval", defaultInterval) );
			
			EditText etTimeInterval = (EditText)findViewById(R.id.etTimeInterval);
			etTimeInterval.setText( prefs.getString("time_interval", defaultTimeInterval) );
			
			CheckBox cbRestart = (CheckBox)findViewById(R.id.cbRestart);
			cbRestart.setChecked(prefs.getBoolean("restart", false));
		}
		catch(Exception e) 
		{
			Toast.makeText(main.this, e.toString(), Toast.LENGTH_LONG).show();
		}

	}
	
	//--------------------- 키 이벤트 처리
	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event )
	{
		return true;
	}
	
	public void save()
	{
		// 파일 저장
		// 스트림 쓰기
		try
		{
			SharedPreferences prefs = getSharedPreferences(_saveName, MODE_PRIVATE);
			SharedPreferences.Editor ed = prefs.edit();

			EditText etInterval = (EditText)findViewById(R.id.etInterval);
			ed.putString("interval", etInterval.getText().toString());
			
			EditText etTimeInterval = (EditText)findViewById(R.id.etTimeInterval);
			ed.putString("time_interval", etTimeInterval.getText().toString());
			
			/*
			CheckBox cbRestart = (CheckBox)findViewById(R.id.cbRestart);
			ed.putBoolean("restart", service_main._restart);
			cbRestart.setChecked(service_main._restart);
			*/
			
			ed.commit();
		}
		catch(Exception e) 
		{
			Toast.makeText(main.this, e.toString(), Toast.LENGTH_LONG).show();
		}
	}

	//--------------------------------------------------------------------------------------
	// 버튼 이벤트 처리
	private Button.OnClickListener mClickListener = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			switch(v.getId())
			{
			case R.id.btStart:
				{
					save();

/*					EditText etInterval = (EditText)findViewById(R.id.etInterval);
					service_main._interval = Integer.parseInt(etInterval.getText().toString());
					
					EditText etTimeInterval = (EditText)findViewById(R.id.etTimeInterval);
					service_main._timeInterval = Integer.parseInt(etTimeInterval.getText().toString());
					
					*//*
					CheckBox cbRestart = (CheckBox)findViewById(R.id.cbRestart);
					service_main._restart = cbRestart.isChecked();
*/
					
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

			case R.id.btSetting:
				{
					startActivity(new Intent(main.this, Setting.class));
				}
				break;
					
			case R.id.btExit:
				{
					save();
					finish();
				}
				break;
			}
		}
	};
}
