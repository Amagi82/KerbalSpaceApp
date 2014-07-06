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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String version;
	NavigationDrawerAdapter adapter;
	List<NavigationDrawerItem> dataList;

	// This Activity is the default, and presents a list of icons for each planet so the user may access them quickly.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dataList = new ArrayList<NavigationDrawerItem>();
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// Set up the drawer's list view with items and click listener
		dataList.add(new NavigationDrawerItem(this.getString(R.string.utilities))); // adding a header to the list
		dataList.add(new NavigationDrawerItem(this.getString(R.string.title_activity_mission_planner), R.drawable.ic_action_event));
		// dataList.add(new NavigationDrawerItem("Aerocapture Calculator", R.drawable.ic_action_cloud)); - not implemented yet
		dataList.add(new NavigationDrawerItem(this.getString(R.string.title_activity_settings), R.drawable.ic_action_settings));

		dataList.add(new NavigationDrawerItem("")); // adding a header to the list
		dataList.add(new NavigationDrawerItem(this.getString(R.string.feedback), R.drawable.ic_action_paste));
		dataList.add(new NavigationDrawerItem(this.getString(R.string.about), R.drawable.ic_action_about));

		adapter = new NavigationDrawerAdapter(this, R.layout.navigation_drawer_item, dataList);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Get the app version name from the manifest for the About section
		try {
			version = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			version = "Unknown";
		}

		// Enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the interactions between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	public void onResume() {
		super.onResume();
		setTitle(R.string.app_name);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		if (item.getItemId() == R.id.action_settings) {
			startActivity(new Intent(this, Settings.class));
		}
		return false;
	}

	// Item selected functionality
	private void selectItem(int position) {
		switch (position) {
		case 1:
			startActivity(new Intent(this, MissionPlanner.class));
			break;
		case 2:
			startActivity(new Intent(this, Settings.class));
			break;
		case 4:
			// opens the KSP forum feedback thread
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://forum.kerbalspaceprogram.com/threads/82203-Kerbal-Space-App-for-Android!")));
			break;
		case 5:
			// alertdialog for the About section
			AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
			builder.setMessage(getResources().getString(R.string.app_name) + " v" + version + getResources().getString(R.string.version))
					.setNeutralButton(R.string.dismiss, null).show();
			break;
		default:
			break;
		}
		mDrawerList.setItemChecked(position, true);
		// setTitle(dataList.get(position).getItemName());
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	// The click listener for the ListView in the navigation drawer
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (dataList.get(position).getTitle() == null) {
				selectItem(position);
			}
		}
	}

	// The following methods send you to the appropriate ViewPager in CelestialBodyActivity
	public void goKerbol(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 0);
		startActivity(i);
	}

	public void goMoho(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 1);
		startActivity(i);
	}

	public void goEve(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 2);
		startActivity(i);
	}

	public void goGilly(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 3);
		startActivity(i);
	}

	public void goKerbin(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 4);
		startActivity(i);
	}

	public void goMun(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 5);
		startActivity(i);
	}

	public void goMinmus(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 6);
		startActivity(i);
	}

	public void goDuna(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 7);
		startActivity(i);
	}

	public void goIke(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 8);
		startActivity(i);
	}

	public void goDres(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 9);
		startActivity(i);
	}

	public void goJool(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 10);
		startActivity(i);
	}

	public void goLaythe(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 11);
		startActivity(i);
	}

	public void goVall(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 12);
		startActivity(i);
	}

	public void goTylo(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 13);
		startActivity(i);
	}

	public void goBop(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 14);
		startActivity(i);
	}

	public void goPol(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 15);
		startActivity(i);
	}

	public void goEeloo(View view) {
		Intent i = new Intent(this, CelestialBodyActivity.class);
		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 16);
		startActivity(i);
	}
}