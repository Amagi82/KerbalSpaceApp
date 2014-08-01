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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MissionPlanner extends Activity {

	StableArrayAdapter mAdapter;
	ListView mListView;
	BackgroundContainer mBackgroundContainer;
	ArrayList<MissionData> missionData = new ArrayList<MissionData>();
	HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();
	boolean mSwiping = false, mItemPressed = false;
	int totalDeltaV;
	TextView tvTotalDeltaV;

	private static final int SWIPE_DURATION = 250;
	private static final int MOVE_DURATION = 150;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mission_planner);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		getActionBar().setTitle(R.string.title_activity_mission_planner);

		if (savedInstanceState == null) {
			// Load saved missionData if available.
			try {
				FileInputStream inStream = new FileInputStream(Environment.getExternalStorageDirectory() + File.separator + "MissionData");
				ObjectInputStream objectInStream = new ObjectInputStream(inStream);
				int count = objectInStream.readInt();
				for (int i = 0; i < count; i++)
					missionData.add((MissionData) objectInStream.readObject());
				objectInStream.close();
			} catch (OptionalDataException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// if the list is empty, add the default planet
			if (missionData.size() == 0) {
				missionData = setFirstMissionData();
			}
		} else {
			missionData = savedInstanceState.getParcelableArrayList("key");
		}

		mBackgroundContainer = (BackgroundContainer) findViewById(R.id.listViewBackground);
		mListView = (ListView) findViewById(R.id.list);
		tvTotalDeltaV = (TextView) findViewById(R.id.tvTotalDeltaV);
		mAdapter = new StableArrayAdapter(this, missionData, mTouchListener);

		// add the newDestination button as a footer below the listview
		ImageView newDestination = new ImageView(this);
		newDestination.setImageResource(R.drawable.ic_plus);
		mListView.addFooterView(newDestination);
		newDestination.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int possibleIconState = 0; // Lets MissionDestination know which icons it's allowed to use
				if (missionData.size() < 1) {
					possibleIconState = 1;
				}
				Intent intent = new Intent(MissionPlanner.this, MissionDestination.class);
				intent.putExtra("possibleIconState", possibleIconState);
				intent.putExtra("isNewItem", true); // Places the result as a new item in the listview
				startActivityForResult(intent, 0);
			}
		});
		mListView.setAdapter(mAdapter);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("key", missionData);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		// This is in onResume so it refreshes deltaV when the user returns from adjusting settings
		SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
		int mClearanceValue = prefs.getInt("mClearanceValue", 1000);
		int mMarginsValues = prefs.getInt("mMarginsValue", 10);
		int mInclinationValues = prefs.getInt("mInclinationValue", 30);
		float mMarginsValue = (float) mMarginsValues / 100 + 1;
		float mInclinationValue = (float) mInclinationValues / 100;

		// Update OrbitalMechanics with the new values
		OrbitalMechanics.mClearanceValue = mClearanceValue;
		OrbitalMechanics.mMarginsValue = mMarginsValue;
		OrbitalMechanics.mInclinationValue = mInclinationValue;
		refreshDeltaV();
	}

	// This method calculates the total delta V and displays it at the bottom
	private void refreshDeltaV() {
		totalDeltaV = 0;
		for (int i = 0; i < missionData.size(); i++) {
			int takeoffDeltaV = 0, transferDeltaV = 0, landingDeltaV = 0;
			if (missionData.get(i).getIconStatus() == 3) {
				takeoffDeltaV = OrbitalMechanics.getToOrbit(missionData.get(i).getPlanetId(), missionData.get(i).getTakeoffAltitude(),
						missionData.get(i).getOrbitAltitude());
			}
			if (missionData.get(i).getLanding()) {
				landingDeltaV = OrbitalMechanics.getLandingDeltaV(missionData.get(i).getPlanetId(),
						missionData.get(i).getTakeoffAltitude(), missionData.get(i).getOrbitAltitude());
			}
			if (missionData.get(i).getToOrbit() && missionData.get(i).getLanding()) {
				takeoffDeltaV = OrbitalMechanics.getToOrbit(missionData.get(i).getPlanetId(), missionData.get(i).getTakeoffAltitude(),
						missionData.get(i).getOrbitAltitude());
			}
			if (i != 0) {
				transferDeltaV = OrbitalMechanics.getTransferDeltaV(missionData.get(i - 1).getPlanetId(), missionData.get(i).getPlanetId(),
						missionData.get(i - 1).getOrbitAltitude(), missionData.get(i).getOrbitAltitude());
			}
			totalDeltaV = totalDeltaV + takeoffDeltaV + transferDeltaV + landingDeltaV;
		}
		String value = NumberFormat.getNumberInstance(Locale.getDefault()).format(totalDeltaV);
		tvTotalDeltaV.setText(value + " m/s");
	}

	// Save missionData on pause
	@Override
	public void onPause() {
		super.onPause();
		try {
			File file = new File(Environment.getExternalStorageDirectory() + File.separator + "MissionData");
			file.createNewFile();
			FileOutputStream outStream = new FileOutputStream(file);
			ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
			objectOutStream.writeInt(missionData.size());
			for (MissionData r : missionData)
				objectOutStream.writeObject(r);
			objectOutStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Handles touch events to fade/move dragged items as they are swiped out
	private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
		float mDownX;
		private int mSwipeSlop = -1;

		@Override
		public boolean onTouch(final View v, MotionEvent event) {
			if (mSwipeSlop < 0) {
				mSwipeSlop = ViewConfiguration.get(MissionPlanner.this).getScaledTouchSlop();
			}
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mItemPressed) {
					// Multi-item swipes not handled
					return false;
				}
				mItemPressed = true;
				mDownX = event.getX();
				break;
			case MotionEvent.ACTION_CANCEL:
				v.setAlpha(1);
				v.setTranslationX(0);
				mItemPressed = false;
				break;
			case MotionEvent.ACTION_MOVE: {
				float x = event.getX() + v.getTranslationX();
				float deltaX = x - mDownX;
				float deltaXAbs = Math.abs(deltaX);
				if (!mSwiping) {
					if (deltaXAbs > mSwipeSlop) {
						mSwiping = true;
						mListView.requestDisallowInterceptTouchEvent(true);
						mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
					}
				}
				if (mSwiping) {
					v.setTranslationX((x - mDownX));
					v.setAlpha(1 - deltaXAbs / v.getWidth());
				}
			}
				break;
			case MotionEvent.ACTION_UP: {
				// User let go - figure out whether to animate the view out, or back into place
				if (mSwiping) {
					float x = event.getX() + v.getTranslationX();
					float deltaX = x - mDownX;
					float deltaXAbs = Math.abs(deltaX);
					float fractionCovered = 0;
					float endX;
					float endAlpha;
					final boolean remove;
					if (deltaXAbs > v.getWidth() / 4) {
						// Greater than a quarter of the width - animate it out
						fractionCovered = deltaXAbs / v.getWidth();
						endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
						endAlpha = 0;
						remove = true;
					} else {
						// Not far enough - animate it back
						fractionCovered = 1 - (deltaXAbs / v.getWidth());
						endX = 0;
						endAlpha = 1;
						remove = false;
					}
					// Animate position and alpha of swiped item
					long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
					mListView.setEnabled(false);
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						v.animate().setDuration(duration).alpha(endAlpha).translationX(endX).setListener(new AnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(Animator animation) {
								// Restore animated values
								v.setAlpha(1);
								v.setTranslationX(0);
								if (remove) {
									animateRemoval(mListView, v);
								} else {
									mBackgroundContainer.hideBackground();
									mSwiping = false;
									mListView.setEnabled(true);
								}
							}
						});
					} else {
						v.animate().setDuration(duration).alpha(endAlpha).translationX(endX).withEndAction(new Runnable() {
							@Override
							public void run() {
								// Restore animated values
								v.setAlpha(1);
								v.setTranslationX(0);
								if (remove) {
									animateRemoval(mListView, v);
								} else {
									mBackgroundContainer.hideBackground();
									mSwiping = false;
									mListView.setEnabled(true);
								}
							}
						});
					}
					mItemPressed = false;
					break;
				}
			}

				// Item was clicked - allow user to edit list item
				mItemPressed = false;
				int position = mListView.getPositionForView(v);
				int possibleIconState = 0;
				if (position == 0) {
					possibleIconState = 1;
				}
				Intent intent = new Intent(MissionPlanner.this, MissionDestination.class);
				intent.putExtra("listItem", (Parcelable) missionData.get(position));
				intent.putExtra("possibleIconState", possibleIconState);
				intent.putExtra("isNewItem", false);
				startActivityForResult(intent, position);
				break;
			default:
				return false;
			}
			return true;
		}
	};

	/**
	 * This method animates all other views in the ListView container (not including ignoreView) into their final positions. It is called
	 * after ignoreView has been removed from the adapter, but before layout has been run. The approach here is to figure out where
	 * everything is now, then allow layout to run, then figure out where everything is after layout, and then to run animations between all
	 * of those start/end positions.
	 */
	private void animateRemoval(final ListView listview, View viewToRemove) {
		int firstVisiblePosition = listview.getFirstVisiblePosition();
		for (int i = 0; i < listview.getChildCount(); ++i) {
			View child = listview.getChildAt(i);
			if (child != viewToRemove) {
				int position = firstVisiblePosition + i;
				long itemId = mAdapter.getItemId(position);
				mItemIdTopMap.put(itemId, child.getTop());
			}
		}
		// Delete the item from the adapter
		int position = mListView.getPositionForView(viewToRemove);
		mAdapter.remove(mAdapter.getItem(position));
		mAdapter.notifyDataSetChanged();
		refreshDeltaV();

		final ViewTreeObserver observer = listview.getViewTreeObserver();
		observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				observer.removeOnPreDrawListener(this);
				boolean firstAnimation = true;
				int firstVisiblePosition = listview.getFirstVisiblePosition();
				for (int i = 0; i < listview.getChildCount(); ++i) {
					final View child = listview.getChildAt(i);
					int position = firstVisiblePosition + i;
					long itemId = mAdapter.getItemId(position);
					Integer startTop = mItemIdTopMap.get(itemId);
					int top = child.getTop();
					if (startTop != null) {
						if (startTop != top) {
							int delta = startTop - top;
							child.setTranslationY(delta);
							child.animate().setDuration(MOVE_DURATION).translationY(0);
							if (firstAnimation) {
								if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
									child.animate().setListener(new AnimatorListenerAdapter() {
										@Override
										public void onAnimationEnd(Animator animation) {
											mBackgroundContainer.hideBackground();
											mSwiping = false;
											mListView.setEnabled(true);

										}
									});
								} else {
									child.animate().withEndAction(new Runnable() {
										@Override
										public void run() {
											mBackgroundContainer.hideBackground();
											mSwiping = false;
											mListView.setEnabled(true);
										}
									});
									firstAnimation = false;
								}
							}
						}
					} else {
						// Animate new views along with the others. The catch is that they did not
						// exist in the start state, so we must calculate their starting position
						// based on neighboring views.
						int childHeight = child.getHeight() + listview.getDividerHeight();
						startTop = top + (i > 0 ? childHeight : -childHeight);
						int delta = startTop - top;
						child.setTranslationY(delta);
						child.animate().setDuration(MOVE_DURATION).translationY(0);
						if (firstAnimation) {
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
								child.animate().setListener(new AnimatorListenerAdapter() {
									@Override
									public void onAnimationEnd(Animator animation) {
										mBackgroundContainer.hideBackground();
										mSwiping = false;
										mListView.setEnabled(true);

									}
								});
							} else {
								child.animate().withEndAction(new Runnable() {
									@Override
									public void run() {
										mBackgroundContainer.hideBackground();
										mSwiping = false;
										mListView.setEnabled(true);
									}
								});
								firstAnimation = false;
							}
						}
					}
				}
				mItemIdTopMap.clear();
				return true;
			}
		});

	}

	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mission_planner, menu);
		return true;
	}

	// Action bar functionality
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			break;
		case R.id.action_settings:
			startActivity(new Intent(this, Settings.class));
			break;
		case R.id.action_delete:
			missionData.clear();
			setFirstMissionData();
			mAdapter.notifyDataSetChanged();
			refreshDeltaV();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		if (Settings.language == null) {
			Settings.language = Locale.getDefault();
		} else if (!config.locale.equals(Settings.language) && !Locale.getDefault().equals(Settings.language)) {
			config.locale = Settings.language;
			Locale.setDefault(config.locale);
			getBaseContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());
			recreate();
		}
	}

	// Grab the parcel from MissionDestination, and either add a new listview item or update an existing one
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && data != null) {
			MissionData result = data.getParcelableExtra("returnItem");
			if (data.getBooleanExtra("isNewItem", true)) {
				missionData.add(result);
			} else {
				missionData.set(requestCode, result);
			}
			mAdapter.notifyDataSetChanged();
			refreshDeltaV();
		}
		if (resultCode == RESULT_CANCELED) {
		}

	}

	// Adds Kerbin as the default departure planet
	private ArrayList<MissionData> setFirstMissionData() {
		MissionData a = new MissionData(4, false, 0, true, 100000, 3);
		missionData.add(a);
		return missionData;
	}

}