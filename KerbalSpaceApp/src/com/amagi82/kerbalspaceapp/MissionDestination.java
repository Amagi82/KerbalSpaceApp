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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MissionDestination extends Activity {

	// This activity is presented as an alertdialog and allows the user to select a new listview item(destination planet). A new MissionData
	// object is created to hold the data back in the MissionPlanner class.
	int mClearanceValue, mMarginsValue, mInclinationValue, mLandingAltitudeValue, mOrbitAltitudeValue, possibleIconState;
	int mPlanetId = 5, mIconStatus = 0; // icon status: 0 = lander, 1 = parachute, 2 = orbit, 3 = rocket
	int mPlanetIconIds[] = { R.drawable.kerbol, R.drawable.moho, R.drawable.eve, R.drawable.gilly, R.drawable.kerbin, R.drawable.mun,
			R.drawable.minmus, R.drawable.duna, R.drawable.ike, R.drawable.dres, R.drawable.jool, R.drawable.laythe, R.drawable.vall,
			R.drawable.tylo, R.drawable.bop, R.drawable.pol, R.drawable.eeloo };
	int mLandingStatusIconIds[] = { R.drawable.ic_lander, R.drawable.ic_parachute, R.drawable.ic_orbit, R.drawable.ic_rocket };
	boolean mLanding = true, mToOrbit = true, isNewItem;
	String mInclinationStatus;
	TextView mLandingStatusIcon, mLandingAltitude, mOrbitAltitude, mSettingsStatus;
	ImageView mLandingIcon, mPlanetImage;
	SeekBar landingAltitude, orbitAltitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mission_destination);

		// Makes the dialog fill the width when the screen is in landscape. It squishes it otherwise.
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			WindowManager.LayoutParams layout = new WindowManager.LayoutParams();
			layout.copyFrom(getWindow().getAttributes());
			layout.width = WindowManager.LayoutParams.MATCH_PARENT;
			getWindow().setAttributes(layout);
		}

		landingAltitude = (SeekBar) findViewById(R.id.seekBarLandingAltitude);
		orbitAltitude = (SeekBar) findViewById(R.id.seekBarOrbitAltitude);
		mLandingStatusIcon = (TextView) findViewById(R.id.tvLandingIconStatus);
		mLandingAltitude = (TextView) findViewById(R.id.tvLandingAltitude);
		mOrbitAltitude = (TextView) findViewById(R.id.tvOrbitAltitude);
		mSettingsStatus = (TextView) findViewById(R.id.tvSettingsStatus);
		mLandingIcon = (ImageView) findViewById(R.id.landingIcon);
		mPlanetImage = (ImageView) findViewById(R.id.planetId);

		// Display current settings at the bottom
		SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
		mClearanceValue = prefs.getInt("mClearanceValue", 1000);
		mMarginsValue = prefs.getInt("mMarginsValue", 20);
		mInclinationValue = prefs.getInt("mInclinationValue", 20);

		// There is no direct numerical efficiency determination for an inclination change, so this gives the user an approximate idea
		if (mInclinationValue == 0) {
			mInclinationStatus = getString(R.string.best_case);
		} else if (mInclinationValue < 34) {
			mInclinationStatus = getString(R.string.efficient);
		} else if (mInclinationValue < 68) {
			mInclinationStatus = getString(R.string.average);
		} else if (mInclinationValue < 100) {
			mInclinationStatus = getString(R.string.inefficient);
		} else {
			mInclinationStatus = getString(R.string.worst_case);
		}
		mSettingsStatus.setText("Margins: " + mMarginsValue + "% / Inclination: " + mInclinationStatus);

		getIconState();
		getPlanetDetails();

		Intent intent = getIntent();
		isNewItem = intent.getBooleanExtra("isNewItem", true);
		// If updating an existing item, we load that data and display it
		if (!isNewItem) {
			MissionData listItemData = (MissionData) intent.getParcelableExtra("listItem");
			mPlanetId = listItemData.getPlanetId();
			mLanding = listItemData.getLanding();
			mLandingAltitudeValue = listItemData.getTakeoffAltitude();
			mToOrbit = listItemData.getToOrbit();
			mOrbitAltitudeValue = listItemData.getOrbitAltitude();
			mIconStatus = listItemData.getIconStatus();

			mPlanetImage.setImageResource(mPlanetIconIds[mPlanetId]);
			landingAltitude.setMax(OrbitalMechanics.highPoint[mPlanetId]);
			landingAltitude.setProgress(mLandingAltitudeValue);
			landingAltitude.setEnabled(mLanding);
			mLandingAltitude.setText(mLandingAltitudeValue + "m");
			orbitAltitude.setProgress(mOrbitAltitudeValue / 300); // This is a rough approximate
			orbitAltitude.setEnabled(mToOrbit);
			mOrbitAltitude.setText(mOrbitAltitudeValue + "m");
			getIconState();
		}
		setIcon();

		// Cycles through available iconStatuses for the given situation when clicked
		mLandingIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getIconState();
				switch (mIconStatus) {
				case 0:
					mIconStatus = 1;
					landingAltitude.setEnabled(false);
					orbitAltitude.setEnabled(false);
					mLanding = false;
					mToOrbit = false;
					break;
				case 1:
					mIconStatus = 2;
					if (possibleIconState == 2) {
						mIconStatus = 2;
					}
					landingAltitude.setEnabled(false);
					orbitAltitude.setEnabled(true);
					mLanding = false;
					mToOrbit = true;
					break;
				case 2:
					mIconStatus = 3;
					if (possibleIconState == 0) {
						mIconStatus = 0;
					} else if (possibleIconState == 2) {
						mIconStatus = 2;
					}
					landingAltitude.setEnabled(true);
					orbitAltitude.setEnabled(true);
					mLanding = true;
					mToOrbit = true;
					break;
				case 3:
					mIconStatus = 0;
					if (possibleIconState == 1) {
						mIconStatus = 2;
					}
					landingAltitude.setEnabled(false);
					orbitAltitude.setEnabled(true);
					mLanding = false;
					mToOrbit = true;
					break;
				}
				mLandingAltitude.setText("");
				mOrbitAltitude.setText("");
				setIcon();

			}

		});

		// Opens a list of planet choices
		mPlanetImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(MissionDestination.this, AlertDialog.THEME_HOLO_DARK);
				builder.setTitle(R.string.title_missionDestination).setItems(R.array.bodies, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mPlanetId = which + 1;
						getPlanetDetails();
						mPlanetImage.setImageResource(mPlanetIconIds[mPlanetId]);
						getIconState();
						setIcon();
					}
				}).show();
			}
		});

		// Seekbar for the altitude of the ground where the user lands/takes off
		landingAltitude.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mLandingAltitude.setText(progress + "m");
				mLandingAltitudeValue = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		// Seekbar for the altitude the user intends to orbit (orbits are assumed to be circular)
		orbitAltitude.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// Linear values for the seekbar are difficult to use in this instance, so we use an exponential value.
				double x = OrbitalMechanics.minOrbit[mPlanetId]
						+ Math.pow(300000 - OrbitalMechanics.minOrbit[mPlanetId], ((1.0f * progress) / 1000));
				// This is the linear value
				double y = ((1.0f * progress) / 1000) * (300000 - OrbitalMechanics.minOrbit[mPlanetId])
						+ OrbitalMechanics.minOrbit[mPlanetId];
				// The average produces the desired rate of increase
				x = (x + y) / 2;
				mOrbitAltitude.setText((int) x + "m");
				mOrbitAltitudeValue = (int) x;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}

	// Limits iconStatus options to what is possible
	private void getIconState() {
		if (mPlanetId == 10) {
			possibleIconState = 2;
			mIconStatus = 2;
			landingAltitude.setEnabled(false);
			orbitAltitude.setEnabled(true);
			mLanding = false;
			mToOrbit = true;
		} else {
			Intent intent = getIntent();
			possibleIconState = intent.getIntExtra("possibleIconState", 1);
		}
	}

	// Set the Seekbars for the planet in question
	private void getPlanetDetails() {
		landingAltitude.setMax(OrbitalMechanics.highPoint[mPlanetId]);
		landingAltitude.setProgress(0);
		orbitAltitude.setProgress(0);
	}

	// Sets the icon and updates its description
	private void setIcon() {
		mLandingIcon.setImageResource(mLandingStatusIconIds[mIconStatus]);
		String[] iconDescriptions = getResources().getStringArray(R.array.icon_descriptions);
		mLandingStatusIcon.setText(iconDescriptions[mIconStatus]);
	}

	// Activated by clicking the cancel button
	public void cancel(View view) {
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}

	// Mission Planner expects a result. Intercept the back button to close this page safely.
	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}

	// Activated by clicking the add button, sends a parcel back to MissionPlanner
	public void add(View view) {
		if (mOrbitAltitudeValue < OrbitalMechanics.minOrbit[mPlanetId]) {
			mOrbitAltitudeValue = OrbitalMechanics.minOrbit[mPlanetId];
		}
		Intent returnIntent = new Intent();
		returnIntent.putExtra("returnItem", (Parcelable) new MissionData(mPlanetId, mLanding, mLandingAltitudeValue, mToOrbit,
				mOrbitAltitudeValue, mIconStatus));
		returnIntent.putExtra("isNewItem", isNewItem);
		setResult(RESULT_OK, returnIntent);
		finish();
	}

}
