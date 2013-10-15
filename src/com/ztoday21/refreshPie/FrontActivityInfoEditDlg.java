package com.ztoday21.refreshPie;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * User: hermit
 * Date: 13. 10. 15
 * Time: ¿ÀÈÄ 3:17
 */
public class FrontActivityInfoEditDlg extends Dialog implements View.OnClickListener{

	public Setting.FrontActivityInfo frontActivityInfo;
	public int listIndex = 0;
	public boolean deleted = false;

	TextView tvClassname;
	TextView tvDelaytime;

	public FrontActivityInfoEditDlg(Context context) {
		super(context);



	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.frontactivityinfo_editdialog);

		tvClassname = (TextView)findViewById(R.id.editTextClassName);
		tvDelaytime = (TextView)findViewById(R.id.editTextDelayTime);

		findViewById(R.id.buttonUpdate).setOnClickListener(this);
		findViewById(R.id.buttonDelete).setOnClickListener(this);
		findViewById(R.id.buttonClose).setOnClickListener(this);


		setTitle(frontActivityInfo.getPackageName());
		tvClassname.setText(frontActivityInfo.getClassName());
		tvDelaytime.setText(String.valueOf(frontActivityInfo.getRefreshDelaytime()));
	}


	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.buttonUpdate:
				frontActivityInfo = new Setting.FrontActivityInfo(frontActivityInfo.getPackageName(),
						tvClassname.getText().toString(), Integer.parseInt(tvDelaytime.getText().toString()));
				break;

			case R.id.buttonDelete:
				deleted = true;
				break;

			case R.id.buttonClose:
				break;
		}

		dismiss();
	}
}
