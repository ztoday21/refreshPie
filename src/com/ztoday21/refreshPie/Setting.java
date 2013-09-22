package com.ztoday21.refreshPie;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * User: hermit
 * Date: 13. 9. 21
 * Time: 오후 9:52
 */
public class Setting extends Activity {
	public static final String keyAutostart = "Autostart";
	public static final String keyActivityFilter = "ActivityFilter";
	public static final String keyLogFrontActivityClassname = "LogFrontActivityClassname";
	public static final String keyClassNames = "classNames";
	public static final String keyInitialized = "keyInitialized";

	private ListView lvActiveActivityClassnames;
	private CheckBox cbAutostart;
	private CheckBox cbActivityFilter;
	private CheckBox cbLogFrontActivityClassname;

	private ArrayList<String> classNames;
	private ArrayAdapter<String> adapter;
	private InputMethodManager imm;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.setting);

		findViewById(R.id.buttonAddClassname).setOnClickListener(mClickListener);
		findViewById(R.id.buttonDeleteClassname).setOnClickListener(mClickListener);
		findViewById(R.id.buttonClose).setOnClickListener(mClickListener);

		lvActiveActivityClassnames = (ListView) findViewById(R.id.listViewClassnames);
		cbAutostart = (CheckBox) findViewById(R.id.checkBoxAutostart);
		cbActivityFilter = (CheckBox) findViewById(R.id.checkBoxEnableActivityFilter);
		cbLogFrontActivityClassname = (CheckBox) findViewById(R.id.checkboxLogFrontActivityClassname);

		SharedPreferences prefs = getSharedPreferences(main._saveName, MODE_PRIVATE);

		cbAutostart.setChecked(prefs.getBoolean(keyAutostart, false));
		cbActivityFilter.setChecked(prefs.getBoolean(keyActivityFilter, false));
		cbLogFrontActivityClassname.setChecked(prefs.getBoolean(keyLogFrontActivityClassname, false));

		//list
		Set<String> setClassNames = prefs.getStringSet(keyClassNames, new HashSet<String>());
		classNames = new ArrayList<String>(setClassNames);

		if (prefs.getBoolean(keyInitialized, false) == false)
			loadDefaults();

		adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1, classNames);
		lvActiveActivityClassnames.setAdapter(adapter);

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);


	}

	private void loadDefaults()
	{
		classNames.clear();

		classNames.add("com.initialcoms.ridi.epub.EPubReaderActivityPhone");                        //리디북스
		classNames.add("com.kyobo.ebook.common.b2c.viewer.epub.activity.ViewerEpubMainActivity");   //교보 eBook
		classNames.add("com.feelingk.epub.reader.EPubViewer");   //교보 도서관 (type3)
		classNames.add("com.kyobobook.b2b.phone.reader.WebViewer");   //교보 도서관 (type1 phone 용)
	}

	private void saveSetting()
	{
		SharedPreferences prefs = getSharedPreferences(main._saveName, MODE_PRIVATE);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putBoolean(keyAutostart, cbAutostart.isChecked());
		ed.putBoolean(keyActivityFilter, cbActivityFilter.isChecked());
		ed.putBoolean(keyLogFrontActivityClassname, cbLogFrontActivityClassname.isChecked());

		Set<String> names = new HashSet<String>(classNames);
		ed.putStringSet(keyClassNames, names);

		ed.putBoolean(keyInitialized, true);

		ed.commit();
	}

	private Button.OnClickListener mClickListener = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			switch(v.getId())
			{
				case R.id.buttonAddClassname:
				{
					EditText etClassNames = (EditText)findViewById(R.id.etClassname);
					if (etClassNames.getText().length() > 0) {
						classNames.add(etClassNames.getText().toString());
						etClassNames.setText("");

						adapter.notifyDataSetChanged();
						imm.hideSoftInputFromWindow(etClassNames.getWindowToken(), 0);
					}
				}
				break;

				case R.id.buttonDeleteClassname: {
					int pos = lvActiveActivityClassnames.getCheckedItemPosition();
					if (pos != ListView.INVALID_POSITION) {
						classNames.remove(pos);
						lvActiveActivityClassnames.clearChoices();
						adapter.notifyDataSetChanged();
					}
				}
				break;

				case R.id.buttonClose:
				{
					saveSetting();
					finish();
				}
				break;
			}
		}
	};
}
