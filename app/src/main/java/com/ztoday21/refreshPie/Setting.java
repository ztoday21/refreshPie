package com.ztoday21.refreshPie;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: hermit
 * Date: 13. 9. 21
 * Time: 오후 9:52
 */
public class Setting extends Activity implements DialogInterface.OnDismissListener {
	public static final String keyAutostart = "Autostart";
	public static final String keyActivityFilter = "ActivityFilter";
	public static final String keyLogFrontActivityClassname = "LogFrontActivityClassname";
	public static final String keyFrontActivityInfos = "frontActivityInfos";
	public static final String keyInitialized = "keyInitialized_v117";
	public static final String useFullRefresh = "UseFullRefresh";

	private ListView lvActiveActivityClassnames;
	private CheckBox cbAutostart;
	private CheckBox cbActivityFilter;
	private CheckBox cbLogFrontActivityClassname;
	private CheckBox cbUseFullRefresh;

	private ArrayList<FrontActivityInfo> frontActivityInfos;
	private FrontActivityInfoAdapter adapter;
	private InputMethodManager imm;

	public void onCreate(Bundle savedInstanceState)
	{

		Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
		if ( !(handler instanceof CustomUncaughtExceptionHandler) ) {
			Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler(this));
		}



		super.onCreate(savedInstanceState);

		setContentView(R.layout.setting);

//		findViewById(R.id.buttonAddClassname).setOnClickListener(mClickListener);
//		findViewById(R.id.buttonDeleteClassname).setOnClickListener(mClickListener);
		findViewById(R.id.buttonClose).setOnClickListener(mClickListener);

		lvActiveActivityClassnames = (ListView) findViewById(R.id.listViewClassnames);
		cbAutostart = (CheckBox) findViewById(R.id.checkBoxAutostart);
		cbActivityFilter = (CheckBox) findViewById(R.id.checkBoxEnableActivityFilter);
		cbLogFrontActivityClassname = (CheckBox) findViewById(R.id.checkboxLogFrontActivityClassname);
		cbUseFullRefresh = (CheckBox) findViewById(R.id.checkboxUseFullRefresh);

		SharedPreferences prefs = getSharedPreferences(main._saveName, MODE_PRIVATE);

		cbAutostart.setChecked(prefs.getBoolean(keyAutostart, false));
		cbActivityFilter.setChecked(prefs.getBoolean(keyActivityFilter, false));
		cbLogFrontActivityClassname.setChecked(prefs.getBoolean(keyLogFrontActivityClassname, false));
		cbUseFullRefresh.setChecked(prefs.getBoolean(useFullRefresh, true));

		//list
		frontActivityInfos = getFrontActivityInfos(this);

		if (prefs.getBoolean(keyInitialized, false) == false)
			loadDefaults();

		adapter = new FrontActivityInfoAdapter(this, R.layout.row, frontActivityInfos);
		lvActiveActivityClassnames.setAdapter(adapter);

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		lvActiveActivityClassnames.setOnItemClickListener(mListClickListener);


	}

	public static ArrayList<FrontActivityInfo> getFrontActivityInfos(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(main._saveName, MODE_PRIVATE);


		String json = prefs.getString(keyFrontActivityInfos, null);

		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();

		ArrayList<FrontActivityInfo> frontActivityInfos = new ArrayList<FrontActivityInfo>();

		FrontActivityInfo[] array = gson.fromJson(json, FrontActivityInfo[].class);

		if (array != null)
			frontActivityInfos = new ArrayList<FrontActivityInfo>(Arrays.asList(array));

		return frontActivityInfos;
	}

	public static void setFrontActivityInfos(Context ctx, ArrayList<FrontActivityInfo> infos)
	{
		SharedPreferences prefs = ctx.getSharedPreferences(main._saveName, MODE_PRIVATE);
		SharedPreferences.Editor ed = prefs.edit();

		Gson gson = new Gson();
		ed.putString(keyFrontActivityInfos, gson.toJson(infos));

		ed.commit();
	}

	private class FrontActivityInfoAdapter extends ArrayAdapter<FrontActivityInfo> {

		private ArrayList<FrontActivityInfo> items;

		public FrontActivityInfoAdapter(Context context, int textViewResourceId, ArrayList<FrontActivityInfo> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}
			FrontActivityInfo p = items.get(position);
			if (p != null) {
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				if (tt != null){
					tt.setText(p.getPackageName());
				}
				if(bt != null){
					String text = "클래스명 : " + p.getClassName() + "\n" +
							"지연시간 : " + String.valueOf(p.getRefreshDelaytime());
					bt.setText(text);
				}
			}
			return v;
		}
	}
	public static class FrontActivityInfo {

		private String packageName;
		private String className;
		private int refreshDelaytime;

		public FrontActivityInfo(String _packageName, String _className, int _refreshDelaytime){
			this.packageName = _packageName;
			this.className = _className;
			this.refreshDelaytime = _refreshDelaytime;
		}

		public String getPackageName() {
			return packageName;
		}

		public String getClassName() {
			return className;
		}

		public int getRefreshDelaytime() {
			return refreshDelaytime;
		}

	}

	private void loadDefaults()
	{
		frontActivityInfos = new ArrayList<FrontActivityInfo>(Arrays.asList(
				new FrontActivityInfo("리디북스", "com.initialcoms.ridi.epub.EPubReaderActivityPhone", 950),
				new FrontActivityInfo("교보 eBook", "com.kyobo.ebook.common.b2c.viewer.epub.activity.ViewerEpubMainActivity", 900),
				new FrontActivityInfo("교보 도서관 (type3)", "com.feelingk.epub.reader.EPubViewer", 900),
				new FrontActivityInfo("교보 도서관 (type1 phone)", "com.kyobobook.b2b.phone.reader.WebViewer", 900)
		));
	}

	private void saveSetting()
	{
		SharedPreferences prefs = getSharedPreferences(main._saveName, MODE_PRIVATE);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putBoolean(keyAutostart, cbAutostart.isChecked());
		ed.putBoolean(keyActivityFilter, cbActivityFilter.isChecked());
		ed.putBoolean(keyLogFrontActivityClassname, cbLogFrontActivityClassname.isChecked());
		ed.putBoolean(useFullRefresh, cbUseFullRefresh.isChecked());


		Gson gson = new Gson();
		ed.putString(keyFrontActivityInfos, gson.toJson(frontActivityInfos));

		ed.putBoolean(keyInitialized, true);

		ed.commit();
	}

	AdapterView.OnItemClickListener mListClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			FrontActivityInfoEditDlg dlg = new FrontActivityInfoEditDlg(Setting.this);
			dlg.frontActivityInfo = frontActivityInfos.get(position);
			dlg.listIndex = position;
			dlg.setOnDismissListener(Setting.this);

			dlg.show();

		}
	};

	@Override
	public void onDismiss(DialogInterface dialog) {
		FrontActivityInfoEditDlg dlg = (FrontActivityInfoEditDlg) dialog;

		if (dlg.deleted) {
			frontActivityInfos.remove(dlg.listIndex);
			lvActiveActivityClassnames.clearChoices();

		}
		else {
			frontActivityInfos.set(dlg.listIndex, dlg.frontActivityInfo);
		}

		adapter.notifyDataSetChanged();
	}

	private Button.OnClickListener mClickListener = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			switch(v.getId())
			{
/*
				case R.id.buttonAddClassname:
				{
					EditText etClassNames = (EditText)findViewById(R.id.etClassname);
					if (etClassNames.getText().length() > 0) {
						frontActivityInfos.add(etClassNames.getText().toString());
						etClassNames.setText("");

						adapter.notifyDataSetChanged();
						imm.hideSoftInputFromWindow(etClassNames.getWindowToken(), 0);
					}
				}
				break;


				case R.id.buttonDeleteClassname: {
					int pos = lvActiveActivityClassnames.getCheckedItemPosition();
					if (pos != ListView.INVALID_POSITION) {
						frontActivityInfos.remove(pos);
						lvActiveActivityClassnames.clearChoices();
						adapter.notifyDataSetChanged();
					}
				}
				break;
*/

				case R.id.buttonClose:
				{
					saveSetting();

					Intent bindIntent = new Intent(Setting.this, service_main.class);
					stopService(bindIntent);
					startService(bindIntent);

					finish();
				}
				break;
			}
		}
	};
}
