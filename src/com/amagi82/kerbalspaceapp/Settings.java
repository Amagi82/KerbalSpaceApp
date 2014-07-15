/*
 * Kerbal Space App
 *
 *   Copyright (C) 2014 Jim Pekarek (Amagi82)
 *
 *   This program is free software: you can redistribute it and/or modify 
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.amagi82.kerbalspaceapp;

import java.text.NumberFormat;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class Settings extends Activity {

	// Clearance is the distance over the minimum possible stable orbit(clearing the tallest mountain and the atmosphere if present)
	// Margins is the percentage of extra deltaV above the minimum requirements
	// Inclination value is an arbitrary percentage of the best to worst inclination change possible

	int mClearanceValue, mMarginsValue, mInclinationValue;
	TextView mClearance, mMargins, mInclination;
	static Locale language;
	static Locale spanish = new Locale("es");
	static Locale[] langCode = { Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN, spanish };
	Spinner chooseLanguage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		getActionBar().setTitle(R.string.title_activity_settings);

		SeekBar clearance = (SeekBar) findViewById(R.id.seekBarClearance);
		SeekBar margins = (SeekBar) findViewById(R.id.seekBarMargins);
		SeekBar inclination = (SeekBar) findViewById(R.id.seekBarInclination);
		mClearance = (TextView) findViewById(R.id.tvClearance);
		mMargins = (TextView) findViewById(R.id.tvMargins);
		mInclination = (TextView) findViewById(R.id.tvInclination);
		chooseLanguage = (Spinner) findViewById(R.id.spinnerLanguages);

		SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
		mClearanceValue = prefs.getInt("mClearanceValue", 1000);
		mMarginsValue = prefs.getInt("mMarginsValue", 10);
		mInclinationValue = prefs.getInt("mInclinationValue", 30);
		chooseLanguage.setSelection(prefs.getInt("language", 0));

		// Set up spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActionBar().getThemedContext(), R.array.languages,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		chooseLanguage.setAdapter(adapter);
		// @formatter:off
		if (Locale.getDefault().getLanguage().equals("en"))chooseLanguage.setSelection(0);
		if (Locale.getDefault().getLanguage().equals("fr"))chooseLanguage.setSelection(1);
		if (Locale.getDefault().getLanguage().equals("de"))chooseLanguage.setSelection(2);
		if (Locale.getDefault().getLanguage().equals("es"))chooseLanguage.setSelection(3);

		// Set seekbar progress
		clearance.setProgress(mClearanceValue);
		margins.setProgress(mMarginsValue);
		inclination.setProgress(mInclinationValue);

		// Display values
		mClearance.setText(mClearanceValue + "m");
		mMargins.setText(mMarginsValue + "%");
		setInclinationText();

		clearance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				String value = NumberFormat.getNumberInstance(Locale.getDefault()).format(progress);
				mClearance.setText(value + "m");
				mClearanceValue = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		margins.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mMargins.setText(progress + "%");
				mMarginsValue = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		inclination.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mInclinationValue = progress;
				setInclinationText();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		chooseLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				language = langCode[chooseLanguage.getSelectedItemPosition()];
				onConfigurationChanged(getResources().getConfiguration());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}

	private void setInclinationText() {
		if (mInclinationValue == 0) {
			mInclination.setText(R.string.best_case);
		} else if (mInclinationValue < 34) {
			mInclination.setText(R.string.efficient);
		} else if (mInclinationValue < 68) {
			mInclination.setText(R.string.average);
		} else if (mInclinationValue < 100) {
			mInclination.setText(R.string.inefficient);
		} else {
			mInclination.setText(R.string.worst_case);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	// Displays a popup window with help topics
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_help) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
			builder.setTitle(R.string.action_help).setItems(R.array.help_descriptions, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						builder.setTitle(R.string.clearance).setMessage(R.string.help_clearance).setNeutralButton(R.string.dismiss, null)
								.show();
						break;
					case 1:
						builder.setTitle(R.string.safety_margins).setMessage(R.string.help_margins)
								.setNeutralButton(R.string.dismiss, null).show();
						break;
					case 2:
						builder.setTitle(R.string.inclination_efficiency).setMessage(R.string.help_inclination)
								.setNeutralButton(R.string.dismiss, null).show();
						break;
					}
				}
			}).setNeutralButton(R.string.dismiss, null).show();
		}
		if (item.getItemId() == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
		}
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		if (language == null) {
			language = Locale.getDefault();
		} else if (!config.locale.equals(language) && !Locale.getDefault().equals(language)) {
			config.locale = language;
			Locale.setDefault(config.locale);
			getBaseContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());
			recreate();
		}
	}

	// Save the values onPause
	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
		editor.putInt("mClearanceValue", mClearanceValue);
		editor.putInt("mMarginsValue", mMarginsValue);
		editor.putInt("mInclinationValue", mInclinationValue);
		editor.putInt("language", chooseLanguage.getSelectedItemPosition());
		editor.commit();
	}
}
