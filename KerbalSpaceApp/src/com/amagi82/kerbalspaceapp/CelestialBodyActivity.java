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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class CelestialBodyActivity extends FragmentActivity {

	public static final String EXTRA_PLANET_ID = "com.amagi82.kerbalspaceapp.planet_id";
	SectionsPagerAdapter mSectionsPagerAdapter; // Loads fragments into memory
	private ViewPager mViewPager; // Hosts section contents and allows swiping between pages
	private int mBiomesPage = 0, mTopographyPage = 0, mAtmospherePage = 0, mCurrentPage = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_celestial_body);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter, passing in the appropriate page
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(getIntent().getIntExtra(EXTRA_PLANET_ID, 0));
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			// onPageSelected finds the current page. invalidateOptionsMenu() resets the action bar for each page.
			@Override
			public void onPageSelected(int position) {
				mCurrentPage = position;
				invalidateOptionsMenu();
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
		});
		// Provides action bar settings on initial page creation
		mCurrentPage = getIntent().getIntExtra(EXTRA_PLANET_ID, 0);
	}

	// Sets up the action bar for each page and provides the variables for MapActivity.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.celestial_body, menu);
		if (mCurrentPage >= 4 && mCurrentPage <= 6) {
			menu.findItem(R.id.action_biomes).setVisible(true);
			mBiomesPage = mCurrentPage - 4;
		} else {
			menu.findItem(R.id.action_biomes).setVisible(false);
		}
		if (mCurrentPage == 0 || mCurrentPage == 10) {
			menu.findItem(R.id.action_topography).setVisible(false);
		} else {
			menu.findItem(R.id.action_topography).setVisible(true);
			mTopographyPage = mCurrentPage + 2;
		}
		if (mCurrentPage == 2 || mCurrentPage == 7) {
			menu.findItem(R.id.action_atmosphere).setVisible(true);
			mAtmospherePage = mCurrentPage + 17;
		} else {
			menu.findItem(R.id.action_atmosphere).setVisible(false);
		}
		return true;
	}

	// Home is the "Up" functionality. The others load a new MapActivity, passing in a map.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			break;
		case R.id.action_biomes:
			Intent intentBiomes = new Intent(this, MapActivity.class);
			intentBiomes.putExtra(MapActivity.EXTRA_MAPS_ID, mBiomesPage);
			startActivity(intentBiomes);
			break;
		case R.id.action_topography:
			Intent intentTopography = new Intent(this, MapActivity.class);
			intentTopography.putExtra(MapActivity.EXTRA_MAPS_ID, mTopographyPage);
			startActivity(intentTopography);
			break;
		case R.id.action_atmosphere:
			Intent intentAtmosphere = new Intent(this, MapActivity.class);
			intentAtmosphere.putExtra(MapActivity.EXTRA_MAPS_ID, mAtmospherePage);
			startActivity(intentAtmosphere);
			break;
		case R.id.action_settings:
			startActivity(new Intent(this, Settings.class));
			finish();
			break;
		default:
			break;
		}
		return true;
	}

	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		String[] planetList = { "Kerbol", "Moho", "Eve", "Gilly", "Kerbin", "Mun", "Minmus", "Duna", "Ike", "Dres", "Jool", "Laythe",
				"Vall", "Tylo", "Bop", "Pol", "Eeloo" };

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a CelestialBodyFragment (defined as a static inner class below) with the page number as its lone argument.
			Fragment fragment = new CelestialBodyFragment();
			Bundle args = new Bundle();
			args.putInt(CelestialBodyFragment.ARG_SECTION_NUMBER, position);
			fragment.setArguments(args);
			return fragment;
		}

		// Total number of pages
		@Override
		public int getCount() {
			return 17;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return planetList[position];
		}
	}

	// Fragment that contains the individual planets/moons to display, and all of their relevant data
	public static class CelestialBodyFragment extends Fragment {
		// Grabs the section number for the fragment
		public static final String ARG_SECTION_NUMBER = "section_number";

		public CelestialBodyFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_celestial_body, container, false);
			savedInstanceState = this.getArguments();
			ImageView mPlanetImage = (ImageView) rootView.findViewById(R.id.imageMain);
			TextView mTextDescription = (TextView) rootView.findViewById(R.id.textViewDescription);

			// Acquire the most recent settings data
			SharedPreferences prefs = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
			int mClearanceValue = prefs.getInt("mClearanceValue", 1000);
			int mMarginsValues = prefs.getInt("mMarginsValue", 10);
			int mInclinationValues = prefs.getInt("mInclinationValue", 30);
			float mMarginsValue = (float) mMarginsValues / 100 + 1;
			float mInclinationValue = (float) mInclinationValues / 100;

			// Pass the settings into OrbitalMechanics for the deltaV calculations
			OrbitalMechanics.mClearanceValue = mClearanceValue;
			OrbitalMechanics.mMarginsValue = mMarginsValue;
			OrbitalMechanics.mInclinationValue = mInclinationValue;

			Resources res = getResources();
			ArrayList<String> listDataHeader = new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.titles)));
			HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();
			HashMap<String, List<String>> listValues = new HashMap<String, List<String>>();

			// Grabs the categories from the string arrays. LinkedLists allow easy removal of irrelevant categories.
			List<String> orbital = new LinkedList<String>(Arrays.asList(res.getStringArray(R.array.orbital_categories)));
			List<String> physical = new LinkedList<String>(Arrays.asList(res.getStringArray(R.array.physical_categories)));
			List<String> atmospheric = new LinkedList<String>(Arrays.asList(res.getStringArray(R.array.atmosphere_categories)));
			List<String> science = new LinkedList<String>(Arrays.asList(res.getStringArray(R.array.science_multiplier)));
			List<String> deltaV = new LinkedList<String>(Arrays.asList(res.getStringArray(R.array.delta_v)));
			List<String> departurePlanets = new LinkedList<String>(Arrays.asList(res.getStringArray(R.array.departure_windows_planets)));
			List<String> departureJoolMoons = new LinkedList<String>(Arrays.asList(res.getStringArray(R.array.departure_windows_jool)));
			List<String> departureKerbinMoon = new ArrayList<String>();

			// Data list for orbital characteristics
			String[] o1Values = { "5,263,138,304 m", "6,315,765,980 m", "4,210,510,628 m", "0.2", "7.0°", "15°", "70°", "3.14 rad",
					"2,215,754 s, 25d 15h 29m 14s", "2,918,346.4 s", "12,186 to 18,279 m/s" };
			String[] o2Values = { "9,832,684,544 m", "9,931,011,387 m", "9,734,357,701 m", "0.01", "2.1", "0°", "15°", "3.14 rad",
					"5,657,995s, 65d 11h 39m 55s", "14,687,035s, 169d 23h 43m 55s", "10,810.5 to 11,028.9 m/s" };
			String[] o3Values = { "31,500,000 m", "48,825,000 m", "14,175,000 m", "0.55", "12°", "10°", "80°", "0.9 rad",
					"388,587 s, 4d 11h 56m 27s", "417,243s, 4d 19h 54m 3s", "274 to 945 m/s" };
			String[] o4Values = { "13,599,840,256 m", "13,599,840,256 m", "13,599,840,256 m", "0", "0°", "0°", "0°", "3.14 rad",
					"9,203,545 s, 106d 12h 32m 24.6s", "9284.5 m/s" };
			String[] o5Values = { "12,000,000 m", "12,000,000 m", "12,000,000 m", "0", "0°", "0°", "0°", "1.7 rad",
					"138,984 s, 1d 14h 36m 24 s", "141,115 s", "542.5 m/s" };
			String[] o6Values = { "47,000,000 m", "47,000,000 m", "47,000,000 m", "0", "6°", "38°", "78°", "0.9 rad",
					"1,077,311 s, 12d 11h 15m 11s", "1,220,132 s", "274.1 m/s" };
			String[] o7Values = { "20,726,155,264 m", "21,783,189,163 m", "19,669,121,365 m", "0.05", "0.06°", "0°", "135.5°", "3.14 rad",
					"17,315,400s, 200d 9h 50m 0s", "19,645,699 s, 227d 9h 8m 19s", "7,146.6 to 7,914.7 m/s" };
			String[] o8Values = { "3,200,000 m", "3,296,000 m", "3,104,000 m", "0.03", "0.2°", "0°", "0°", "1.7 rad",
					"65,518 s, 18h 11m 58s", "65,767s, 18h 16m 06s", "298 to 316 m/s" };
			String[] o9Values = { "40,839,348,203 m", "46,761,053,522 m", "34,917,642,884 m", "0.14", "5°", "90°", "280°", "3.14 rad",
					"47,893,063 s, 554d 7h 37m 43s", "11,392,903.3 s", "4,630 to 6,200 m/s" };
			String[] o10Values = { "68,773,560,320 m", "72,212,238,387 m", "65,334,882,253 m", "0.05", "1.304°", "0°", "52°", "0.1 rad",
					"104,661,432s, 3y 116d 8h 37m 12s", "10090902 s", "3,927.2 to 4,340.6 m/s" };
			String[] o11Values = { "27,184,000 m", "27,184,000 m", "27,184,000 m", "0", "0°", "0°", "0°", "3.14 rad",
					"52,981s, 14h 43m 0s", "53,008 s, 14h 43m 27s", "3,223.8 m/s" };
			String[] o12Values = { "43,152,000 m", "43,152,000 m", "43,152,000 m", "0", "0°", "0°", "0°", "0.9 rad",
					"105,962 s, 1d 5h 26m 2s", "106,069s, 1d 5h 27m 49s", "2,558.8 m/s" };
			String[] o13Values = { "68,500,000 m", "68,500,000 m", "68,500,000 m", "0", "0.025°", "0°", "0°", "3.14 rad",
					"211,926 s, 2d 10h 52m 6s", "212,356s, 2d 10h 59m 16s", "2,030.9 m/s" };
			String[] o14Values = { "128,500,000 m", "158,697,500 m", "98,302,500 m", "0.24", "15°", "25°", "10°", "0.9 rad",
					"544 507 s, 6d 7h 15m 7s", "547,355 s, 6d 8h 2m 35s", "1,167 to 1,884 m/s" };
			String[] o15Values = { "179,890,000 m", "210,624,206 m", "149,155,794 m", "0.17", "4.25°", "15°", "2°", "0.9 rad",
					"901,903 s, 10d 10h 31m 43s", "909,742 s, 10d 12h 42m 22s", "1,055 to 1,489 m/s" };
			String[] o16Values = { "90,118,820,000 m", "113,549,713,200 m", "66,687,926,800 m", "0.26", "6.15°", "260°", "50°", "3.14 rad",
					"156,992,048 s, 4y 357d 0h 54m 8s", "9,776,696 s", "2,764 to 4,706 m/s" };

			// Data list for physical characteristics
			String[] p0Values = { "261,600,000 m", "8.599e17 m2", "1.756e28 kg", "1.172e18 m3/s2", "234.24 kg/m3", "17.1 m/s2 (1.746 G)",
					"94,672 m/s", "432,000 s, 5d 0h 0m 0s", "3,804.8 m/s", "1,508,045,000 m", "∞" };
			String[] p1Values = { "250,000 m", "7.854e11 m2", "2.526e21 kg ", "1.686e11 m3/s2", "38,599.96 kg/m3", "2.70 m/s2 (0.275 G)",
					"1,161 m/s ", "1,210,000 s, 14d 0h 6m 40s", "2,665,723 s, 30d 20h 28m 43s", "1.298 m/s ", "18,173,170 m",
					"9,646,663 m", "6,817 m" };
			String[] p2Values = { "700,000 m", "6.157e12 m2", "1.224e23 kg", "8.171e12 m3/s2", "85,220.69 kg/m3", "16.7 m/s2 (1.7 G)",
					"4,832 m/s", "80,500 s, 22h 21m 40s", "81,662 s, 22h 41m 2s", "54.636 m/s", "10,328.47 km ", "85,109,365 m", "7,526 m" };
			String[] p3Values = { "13,000 m", "2.123e9 m2", "1.242e17 kg", "8,289,450 m3/s2", "13,496.490 kg/m3", "0.049 m/s2 (0.005 G)",
					"35.71 m/s", "28,255 s, 7h 50m 55s", "2.8909 m/s ", "42,138 m", "126,123 m", "6,400 m" };
			String[] p4Values = { "600,000 m", "4.524e12 m2", "5.291e22 kg", "3.531e12 m3/s2", "58,484.791 kg/m3", "9.81 m/s2 (1.0 G)",
					"3,431.03 m/s", "21,600 s, 6h 0m 0s", "21,651 s, 6h 0m 51s", "174.53 m/s", "2,868,751 m ", "84,159,286 m", "6,761 m" };
			String[] p5Values = { "200,000 m", "5.026e11 m2", "9.760e20 kg", "6.513e10 m3/s2", "29,125.425 kg/m3", "1.63 m/s2 (0.166 G)",
					"807.08 m/s ", "138,984.38 s, 1d 14h 36m 24.4s", "9.0416 m/s", "2,970,560 m", "2,429,559 m", "7,061 m" };
			String[] p6Values = { "60,000 m", "4.524e10 m2", "2.645e19 kg", "1.765e9 m3/s2", "29,242.396 kg/m3", "0.491 m/s2 (0.05 G)",
					"242.61 m/s", "40,400 s, 11h 13m 20s", "9.3315 m/s", "357,941 m", "2,247,428 m", "5,725 m" };
			String[] p7Values = { "320,000 m ", "1.287e12 m2", "4.515e21 kg", "3.013e11 m3/s2", "32,897.696 kg/m3", "2.94 m/s2 (0.3 G)",
					"1,372.41 m/s", "65,518 s, 18h 11m 58s", "65,767 s, 18h 16m 7s", "30.688 m/s ", "2,880,000 m", "47,921,949 m",
					"8,264 m" };
			String[] p8Values = { "130,000 m ", "2.124e11 m2", "2.782e20 kg", "1.857e10 m3/s2", "30,232.139 kg/m3", "1.10 m/s2 (0.112 G)",
					"534.48 m/s", "65,518 s, 18h 11m 58s", "12.467 m/s", "1,133,900 m", "1,049,599 m", "12,725 m" };
			String[] p9Values = { "138,000 m", "2.393e11 m2", "3.219e20 kg", "2.148e10 m3/s2", "29,242.396 kg/m3", "1.13 m/s2 (0.115 G)",
					"558.00 m/s", "34,800 s, 9h 40m 0s", "34,825 s 9h 40m 25 s", "24.916 m/s", "732,244 m", "32,832,840 m", "5,670 m" };
			String[] p10Values = { "6,000,000 m", "4.524e14 m2", "4.233e24 kg", "2.825e14 m3/s2", "4,678.7834 kg/m3", "7.85 m/s2 (0.8 G)",
					"9,704.43 m/s", "36,000 s, 10h 0m 0s", "36,012s, 10h 0m 12s", "1,047.2 m/s ", "15,010,461 m", "2,455,985,200 m" };
			String[] p11Values = { "500,000 m", "3.142e12 m2", "2.940e22 kg", "1.962e12 m3/s2", "56,145.401 kg/m3", "7.85 m/s2 (0.8 G)",
					"2,801.43 m/s ", "52,981 s, 14h 43m 1s", "59.297 m/s", "4,686,320 m", "3,723,646 m", "5,600m" };
			String[] p12Values = { "300,000 m", "1.131e12 m2", "3.109e21 kg", "2.075e11 m3/s2", "27,487.852 kg/m3", "2.31 m/s2 (0.235 G)",
					"1,176.10 m/s", "105,962.09 s, 1d 5h 26m 2.1s", "17.789 m/s", "3,593,200 m ", "2,406,401 m", "7,976 m" };
			String[] p13Values = { "600,000 m ", "4.524e12 m2", "4.233e22 kg", "2.825e12 m3/s2", "46,787.834 kg/m3", "7.85 m/s2 (0.8 G)",
					"3,068.81 m/s ", "211,926.36 s, 2d 10h 52m 6.4s", "17.789 m/s", "14,157,880 m", "10,856,518 m", "12,695 m" };
			String[] p14Values = { "65,000 m", "5.309e10 m2", "3.726e19 kg", "2.487e9 m3/s2", "32,391.576 kg/m3", "0.589 m/s2 (0.06 G)",
					"276.62 m/s", "544,507 s, 6d 7h 15m 7s", "0.750 m/s", "2,588,170 m ", "1,221,061 m", "21,758 m" };
			String[] p15Values = { "44,000 m", "2.433e10 m2", "1.081e19 kg", "7.217e8 m3/s2", "30,305.756 kg/m3", "0.373 m/s2 (0.038 G)",
					"181.12 m/s ", "901 903 s, 10d 10h 31m 43s", "0.307 m/s", "2,415,080 m ", "1,042,139 m", "5,590 m" };
			String[] p16Values = { "210,000 m", "5.542e11 m2", "1.115e21 kg", "7.441e10 m3/s2", "28,741.098 kg/m3", "1.69 m/s2 (0.172 G)",
					"841.83 m/s", "19,460 s, 5h 24m 20s", "19,462 s, 5h 24m 22s", "67.804 m/s", "683,691 m", "119,100,000 m", "3,874 m" };

			// Data list for atmospheric characteristics
			String[] a2Values = { "506.625 kPa (5 atm)", "7,000 m", "96,708 m", "-40.19 °C", "149.96 °C", "No" };
			String[] a4Values = { "101.325 kPa (1 atm)", "5,000 m", "69,078 m", "-40.19 °C", "20 °C", "Yes" };
			String[] a7Values = { "20.265 kPa (0.2 atm)", "3,000 m", "41,447 m", "-50.24 °C", "-30.17 °C", "No" };
			String[] a10Values = { "1,519.88 kPa (15 atm)", "10,000 m", "138,155 m", "-86.13 °C", "976.55 °C", "No" };
			String[] a11Values = { "81.060 kPa (0.8 atm)", "4,000 m ", "55,262 m", "-40.19 °C", "6.21 °C", "Yes" };

			// Data list for science multipliers
			String[] s0Values = { "11" };
			String[] s1Values = { "9", "8" };
			String[] s2Values = { "12", "7", "7" };
			String[] s3Values = { "9", "8" };
			String[] s4Values = { "0.3 (0.4 splashed)", "0.7", "1" };
			String[] s5Values = { "4", "3" };
			String[] s6Values = { "5", "4" };
			String[] s7Values = { "8", "7", "7" };
			String[] s8Values = { "9", "8" };
			String[] s9Values = { "8", "7" };
			String[] s10Values = { "7", "7" };
			String[] s11Values = { "10", "9", "9" };
			String[] s12Values = { "10", "9" };
			String[] s13Values = { "11", "10" };
			String[] s14Values = { "9", "8" };
			String[] s15Values = { "9", "8" };
			String[] s16Values = { "9", "8" };

			int minOrbit[] = { 0, 6817, 96708, 6400, 69078, 7061, 5725, 41447, 12725, 5670, 138155, 55262, 7976, 12695, 21758, 5590, 3874 };

			// Data list for delta V requirements (formatter is turned off because it makes this section unreadable)
			// @formatter:off
			String[] dv1Values = { OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 2, minOrbit[1], minOrbit[2]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 3, minOrbit[1], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 4, minOrbit[1], minOrbit[4]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 5, minOrbit[1], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 6, minOrbit[1], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 7, minOrbit[1], minOrbit[7]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 8, minOrbit[1], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 9, minOrbit[1], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 10, minOrbit[1], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 11, minOrbit[1], minOrbit[11]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 12, minOrbit[1], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(12, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 13, minOrbit[1], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 14, minOrbit[1], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 15, minOrbit[1], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(1, 16, minOrbit[1], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv2Values = { OrbitalMechanics.getToOrbit(2, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 1, minOrbit[2], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 3, minOrbit[2], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 4, minOrbit[2], minOrbit[4]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 5, minOrbit[2], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 6, minOrbit[2], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 7, minOrbit[2], minOrbit[7]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 8, minOrbit[2], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 9, minOrbit[2], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 10, minOrbit[2], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 11, minOrbit[2], minOrbit[11]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 12, minOrbit[2], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(12, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 13, minOrbit[2], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 14, minOrbit[2], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 15, minOrbit[2], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(2, 16, minOrbit[2], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv3Values = { OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 1, minOrbit[3], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 2, minOrbit[3], minOrbit[2]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 4, minOrbit[3], minOrbit[4]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 5, minOrbit[3], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 6, minOrbit[3], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 7, minOrbit[3], minOrbit[7]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 8, minOrbit[3], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 9, minOrbit[3], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 10, minOrbit[3], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 11, minOrbit[3], minOrbit[11]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 12, minOrbit[3], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(12, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 13, minOrbit[3], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 14, minOrbit[3], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 15, minOrbit[3], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(3, 16, minOrbit[3], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv4Values = { OrbitalMechanics.getToOrbit(4, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 1, minOrbit[4], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 2, minOrbit[4], minOrbit[2]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 3, minOrbit[4], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 5, minOrbit[4], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 6, minOrbit[4], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 7, minOrbit[4], minOrbit[7]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 8, minOrbit[4], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 9, minOrbit[4], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 10, minOrbit[4], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 11, minOrbit[4], minOrbit[11]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 12, minOrbit[4], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(12, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 13, minOrbit[4], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 14, minOrbit[4], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 15, minOrbit[4], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(4, 16, minOrbit[4], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv5Values = { OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 1, minOrbit[5], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 2, minOrbit[5], minOrbit[2]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 3, minOrbit[5], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 4, minOrbit[5], minOrbit[4]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 6, minOrbit[5], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 7, minOrbit[5], minOrbit[7]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 8, minOrbit[5], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 9, minOrbit[5], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 10, minOrbit[5], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 11, minOrbit[5], minOrbit[11]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 12, minOrbit[5], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(12, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 13, minOrbit[5], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 14, minOrbit[5], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 15, minOrbit[5], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(5, 16, minOrbit[6], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv6Values = { OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 1, minOrbit[6], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 2, minOrbit[6], minOrbit[2]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 3, minOrbit[6], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 4, minOrbit[6], minOrbit[4]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 5, minOrbit[6], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 7, minOrbit[6], minOrbit[7]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 8, minOrbit[6], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 9, minOrbit[6], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 10, minOrbit[6], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 11, minOrbit[6], minOrbit[11]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 12, minOrbit[6], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(12, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 13, minOrbit[6], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 14, minOrbit[6], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 15, minOrbit[6], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(6, 16, minOrbit[6], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv7Values = { OrbitalMechanics.getToOrbit(7, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 1, minOrbit[7], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 2, minOrbit[7], minOrbit[2]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 3, minOrbit[7], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 4, minOrbit[7], minOrbit[4]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 5, minOrbit[7], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 6, minOrbit[7], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 8, minOrbit[7], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 9, minOrbit[7], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 10, minOrbit[7], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 11, minOrbit[7], minOrbit[11]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 12, minOrbit[7], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(12, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 13, minOrbit[7], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 14, minOrbit[7], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 15, minOrbit[7], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(7, 16, minOrbit[7], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv8Values = { OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 1, minOrbit[8], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 2, minOrbit[8], minOrbit[2]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 3, minOrbit[8], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 4, minOrbit[8], minOrbit[4]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 5, minOrbit[8], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 6, minOrbit[8], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 7, minOrbit[8], minOrbit[7]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 9, minOrbit[8], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 10, minOrbit[8], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 11, minOrbit[8], minOrbit[11]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 12, minOrbit[8], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(12, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 13, minOrbit[8], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 14, minOrbit[8], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 15, minOrbit[8], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(8, 16, minOrbit[8], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv9Values = { OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 1, minOrbit[9], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 2, minOrbit[9], minOrbit[2]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 3, minOrbit[9], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 4, minOrbit[9], minOrbit[4]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 5, minOrbit[9], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 6, minOrbit[9], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 7, minOrbit[9], minOrbit[7]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 8, minOrbit[9], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 10, minOrbit[9], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 11, minOrbit[9], minOrbit[11]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 12, minOrbit[9], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(12, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 13, minOrbit[9], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 14, minOrbit[9], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 15, minOrbit[9], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(9, 16, minOrbit[9], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv10Values = { OrbitalMechanics.getToOrbit(10, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 1, minOrbit[10], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 2, minOrbit[10], minOrbit[2]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 3, minOrbit[10], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 4, minOrbit[10], minOrbit[4]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 5, minOrbit[10], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 6, minOrbit[10], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 7, minOrbit[10], minOrbit[7]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 8, minOrbit[10], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 9, minOrbit[10], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 11, minOrbit[10], minOrbit[11]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 12, minOrbit[10], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(12, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 13, minOrbit[10], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 14, minOrbit[10], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 15, minOrbit[10], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(10, 16, minOrbit[10], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv11Values = { OrbitalMechanics.getToOrbit(11, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 1, minOrbit[11], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 2, minOrbit[11], minOrbit[2]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 3, minOrbit[11], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 4, minOrbit[11], minOrbit[4]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 5, minOrbit[11], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 6, minOrbit[11], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 7, minOrbit[11], minOrbit[7]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 8, minOrbit[11], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 9, minOrbit[11], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 10, minOrbit[11], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 12, minOrbit[11], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(12, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 13, minOrbit[11], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 14, minOrbit[11], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 15, minOrbit[11], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(11, 16, minOrbit[11], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv12Values = { OrbitalMechanics.getToOrbit(12, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 1, minOrbit[12], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 2, minOrbit[12], minOrbit[2]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 3, minOrbit[12], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 4, minOrbit[12], minOrbit[4]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 5, minOrbit[12], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 6, minOrbit[12], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 7, minOrbit[12], minOrbit[7]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 8, minOrbit[12], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 9, minOrbit[12], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 10, minOrbit[12], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 11, minOrbit[12], minOrbit[11]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 13, minOrbit[12], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 14, minOrbit[12], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 15, minOrbit[12], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(12, 16, minOrbit[12], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv13Values = { OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 1, minOrbit[13], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 2, minOrbit[13], minOrbit[2]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 3, minOrbit[13], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 4, minOrbit[13], minOrbit[4]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 5, minOrbit[13], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 6, minOrbit[13], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 7, minOrbit[13], minOrbit[7]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 8, minOrbit[13], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 9, minOrbit[13], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 10, minOrbit[13], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 11, minOrbit[13], minOrbit[11]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 12, minOrbit[13], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 14, minOrbit[13], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 15, minOrbit[13], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(13, 16, minOrbit[13], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv14Values = { OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 1, minOrbit[14], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 2, minOrbit[14], minOrbit[2]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 3, minOrbit[14], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 4, minOrbit[14], minOrbit[4]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 5, minOrbit[14], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 6, minOrbit[14], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 7, minOrbit[14], minOrbit[7]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 8, minOrbit[14], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 9, minOrbit[14], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 10, minOrbit[14], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 11, minOrbit[14], minOrbit[11]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 12, minOrbit[14], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 13, minOrbit[14], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 15, minOrbit[14], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(14, 16, minOrbit[14], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv15Values = { OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 1, minOrbit[15], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 2, minOrbit[15], minOrbit[2]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 3, minOrbit[15], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 4, minOrbit[15], minOrbit[4]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 5, minOrbit[15], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 6, minOrbit[15], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 7, minOrbit[15], minOrbit[7]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 8, minOrbit[15], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 9, minOrbit[15], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 10, minOrbit[15], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 11, minOrbit[15], minOrbit[11]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 12, minOrbit[15], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(12, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 13, minOrbit[15], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 14, minOrbit[15], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(15, 16, minOrbit[15], minOrbit[16]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s"};
			String[] dv16Values = { OrbitalMechanics.getToOrbit(16, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 1, minOrbit[16], minOrbit[1]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(1, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 2, minOrbit[16], minOrbit[2]) + "m/s;Landing: 0 m/s", 
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 3, minOrbit[16], minOrbit[3]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(3, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 4, minOrbit[16], minOrbit[4]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 5, minOrbit[16], minOrbit[5]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(5, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 6, minOrbit[16], minOrbit[6]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(6, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 7, minOrbit[16], minOrbit[7]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 8, minOrbit[16], minOrbit[8]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(8, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 9, minOrbit[16], minOrbit[9]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(9, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 10, minOrbit[16], minOrbit[10]) + "m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 11, minOrbit[16], minOrbit[11]) + "m/s;Landing: 0 m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 12, minOrbit[16], minOrbit[12]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(12, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 13, minOrbit[16], minOrbit[13]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(13, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 14, minOrbit[16], minOrbit[14]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(14, 0, 0) + " m/s",
					"Transfer: " + OrbitalMechanics.getTransferDeltaV(16, 15, minOrbit[16], minOrbit[15]) + "m/s;Landing: " + OrbitalMechanics.getToOrbit(15, 0, 0) + " m/s"};
			// @formatter:on

			// Data list for departure windows
			String[] d1Values = { "58.94°", "76.05°", "90.64°", "103.67°", "108.92°", "110.7°" };
			String[] d2Values = { "-129.13°", "36.07°", "66.07°", "92.04°", "102.24°", "105.67°" };
			String[] d4Values = { "-251.79°", "-54.13°", "44.36°", "82.06°", "96.58°", "101.42°" };
			String d5Values = "90.49°";
			String d6Values = "-153.8°";
			String[] d7Values = { "-158.32°", "-168.68°", "-75.19°", "62.21°", "85.52°", "93.19°" };
			String[] d9Values = { "-29.86°", "-204.51°", "-329.68°", "-145.8°", "51.95°", "68.52°" };
			String[] d10Values = { "-297.62°", "-178.48°", "-48.65°", "-31.06°", "-99.83°", "31.01°" };
			String[] d11Values = { "47.57°", "74.94°", "89.98°", "95.37°" };
			String[] d12Values = { "-84.86°", "47.57°", "73.12°", "82.14°" };
			String[] d13Values = { "-240.26°", "-84.87°", "44.44°", "59.87°" };
			String[] d14Values = { "-138.51°", "-222.8°", "-75.42°", "25.73°" };
			String[] d15Values = { "-343.92°", "-331.04°", "-133.67°", "-33.78°" };
			String[] d16Values = { "-49.76°", "-82.55°", "-80.33°", "-247.09°", "-185.43°", "-43.49°" };

			// Values for each category
			List<String> orbitalValues = new ArrayList<String>();
			List<String> physicalValues = new ArrayList<String>();
			List<String> atmosphericValues = new ArrayList<String>();
			List<String> scienceValues = new ArrayList<String>();
			List<String> deltaVValues = new ArrayList<String>();
			List<String> departureValues = new ArrayList<String>();

			// Categories for each header
			listDataChild.put(listDataHeader.get(0), orbital);
			listDataChild.put(listDataHeader.get(1), physical);
			listDataChild.put(listDataHeader.get(2), atmospheric);
			listDataChild.put(listDataHeader.get(3), science);
			listDataChild.put(listDataHeader.get(4), deltaV);
			listDataChild.put(listDataHeader.get(5), departurePlanets);

			// Puts the values in each category
			listValues.put(listDataHeader.get(0), orbitalValues);
			listValues.put(listDataHeader.get(1), physicalValues);
			listValues.put(listDataHeader.get(2), atmosphericValues);
			listValues.put(listDataHeader.get(3), scienceValues);
			listValues.put(listDataHeader.get(4), deltaVValues);
			listValues.put(listDataHeader.get(5), departureValues);

			// The following loads all the relevant data for each celestial body description page.
			// Comments are provided only for case 0, but are in the same format for each case.
			// Any remove() function should be in descending order, or you will get incorrect results.
			switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
			case 0:
				// Load text description and image
				mTextDescription.setText(R.string.kerbol_description);
				mPlanetImage.setImageResource(R.drawable.kerbol);

				// Add the data from the arrays above
				physicalValues.addAll(Arrays.asList(p0Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				scienceValues.addAll(Arrays.asList(s0Values));
				listValues.put(listDataHeader.get(3), scienceValues);

				// Remove inapplicable stats
				physical.remove(12);
				physical.remove(8);
				listDataChild.put(listDataHeader.get(1), physical);

				science.remove(1);
				science.remove(0);
				listDataChild.put(listDataHeader.get(3), science);

				// Remove entire categories that are inapplicable for the given page
				listDataHeader.remove(5);
				listDataHeader.remove(4);
				listDataHeader.remove(2);
				listDataHeader.remove(0);
				listDataChild.remove(5);
				listDataChild.remove(4);
				listDataChild.remove(2);
				listDataChild.remove(0);
				break;
			case 1:
				mTextDescription.setText(R.string.moho_description);
				mPlanetImage.setImageResource(R.drawable.moho);

				orbitalValues.addAll(Arrays.asList(o1Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p1Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				scienceValues.addAll(Arrays.asList(s1Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv1Values));
				listValues.put(listDataHeader.get(4), deltaVValues);
				departureValues.addAll(Arrays.asList(d1Values));
				listValues.put(listDataHeader.get(5), departureValues);

				science.remove(1);
				listDataChild.put(listDataHeader.get(3), science);

				deltaV.remove(1);
				listDataChild.put(listDataHeader.get(4), deltaV);

				departurePlanets.remove(0);
				listDataChild.put(listDataHeader.get(5), departurePlanets);

				listDataHeader.remove(2);
				listDataChild.remove(2);
				break;
			case 2:
				mTextDescription.setText(R.string.eve_description);
				mPlanetImage.setImageResource(R.drawable.eve);

				orbitalValues.addAll(Arrays.asList(o2Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p2Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				atmosphericValues.addAll(Arrays.asList(a2Values));
				listValues.put(listDataHeader.get(2), atmosphericValues);
				scienceValues.addAll(Arrays.asList(s2Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv2Values));
				listValues.put(listDataHeader.get(4), deltaVValues);
				departureValues.addAll(Arrays.asList(d2Values));
				listValues.put(listDataHeader.get(5), departureValues);

				deltaV.remove(2);
				listDataChild.put(listDataHeader.get(4), deltaV);

				departurePlanets.remove(1);
				listDataChild.put(listDataHeader.get(5), departurePlanets);
				break;
			case 3:
				mTextDescription.setText(R.string.gilly_description);
				mPlanetImage.setImageResource(R.drawable.gilly);

				orbitalValues.addAll(Arrays.asList(o3Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p3Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				scienceValues.addAll(Arrays.asList(s3Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv3Values));
				listValues.put(listDataHeader.get(4), deltaVValues);

				physical.remove(8);
				listDataChild.put(listDataHeader.get(1), physical);

				science.remove(1);
				listDataChild.put(listDataHeader.get(3), science);

				deltaV.remove(3);
				listDataChild.put(listDataHeader.get(4), deltaV);

				listDataHeader.remove(5);
				listDataHeader.remove(2);
				listDataChild.remove(5);
				listDataChild.remove(2);
				break;
			case 4:
				mTextDescription.setText(R.string.kerbin_description);
				mPlanetImage.setImageResource(R.drawable.kerbin);

				orbitalValues.addAll(Arrays.asList(o4Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p4Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				atmosphericValues.addAll(Arrays.asList(a4Values));
				listValues.put(listDataHeader.get(2), atmosphericValues);
				scienceValues.addAll(Arrays.asList(s4Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv4Values));
				listValues.put(listDataHeader.get(4), deltaVValues);
				departureValues.addAll(Arrays.asList(d4Values));
				listValues.put(listDataHeader.get(5), departureValues);

				orbital.remove(9);
				listDataChild.put(listDataHeader.get(0), orbital);

				deltaV.remove(4);
				listDataChild.put(listDataHeader.get(4), deltaV);

				departurePlanets.remove(2);
				listDataChild.put(listDataHeader.get(5), departurePlanets);

				break;
			case 5:
				mTextDescription.setText(R.string.mun_description);
				mPlanetImage.setImageResource(R.drawable.mun);

				orbitalValues.addAll(Arrays.asList(o5Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p5Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				scienceValues.addAll(Arrays.asList(s5Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv5Values));
				listValues.put(listDataHeader.get(4), deltaVValues);
				departureValues.add(d5Values);
				listValues.put(listDataHeader.get(5), departureValues);

				physical.remove(8);
				listDataChild.put(listDataHeader.get(1), physical);

				science.remove(1);
				listDataChild.put(listDataHeader.get(3), science);

				deltaV.remove(5);
				listDataChild.put(listDataHeader.get(4), deltaV);

				departureKerbinMoon.add("Minmus");
				listDataChild.put(listDataHeader.get(5), departureKerbinMoon);

				listDataHeader.remove(2);
				listDataChild.remove(2);
				break;
			case 6:
				mTextDescription.setText(R.string.minmus_description);
				mPlanetImage.setImageResource(R.drawable.minmus);

				orbitalValues.addAll(Arrays.asList(o6Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p6Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				scienceValues.addAll(Arrays.asList(s6Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv6Values));
				listValues.put(listDataHeader.get(4), deltaVValues);
				departureValues.add(d6Values);
				listValues.put(listDataHeader.get(5), departureValues);

				physical.remove(8);
				listDataChild.put(listDataHeader.get(1), physical);

				science.remove(1);
				listDataChild.put(listDataHeader.get(3), science);

				deltaV.remove(6);
				listDataChild.put(listDataHeader.get(4), deltaV);

				departureKerbinMoon.add("Mun");
				listDataChild.put(listDataHeader.get(5), departureKerbinMoon);

				listDataHeader.remove(2);
				listDataChild.remove(2);
				break;
			case 7:
				mTextDescription.setText(R.string.duna_description);
				mPlanetImage.setImageResource(R.drawable.duna);

				orbitalValues.addAll(Arrays.asList(o7Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p7Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				atmosphericValues.addAll(Arrays.asList(a7Values));
				listValues.put(listDataHeader.get(2), atmosphericValues);
				scienceValues.addAll(Arrays.asList(s7Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv7Values));
				listValues.put(listDataHeader.get(4), deltaVValues);
				departureValues.addAll(Arrays.asList(d7Values));
				listValues.put(listDataHeader.get(5), departureValues);

				deltaV.remove(7);
				listDataChild.put(listDataHeader.get(4), deltaV);

				departurePlanets.remove(3);
				listDataChild.put(listDataHeader.get(5), departurePlanets);
				break;
			case 8:
				mTextDescription.setText(R.string.ike_description);
				mPlanetImage.setImageResource(R.drawable.ike);

				orbitalValues.addAll(Arrays.asList(o8Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p8Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				scienceValues.addAll(Arrays.asList(s8Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv8Values));
				listValues.put(listDataHeader.get(4), deltaVValues);

				physical.remove(8);
				listDataChild.put(listDataHeader.get(1), physical);

				science.remove(1);
				listDataChild.put(listDataHeader.get(3), science);

				deltaV.remove(8);
				listDataChild.put(listDataHeader.get(4), deltaV);

				listDataHeader.remove(5);
				listDataHeader.remove(2);
				listDataChild.remove(5);
				listDataChild.remove(2);
				break;
			case 9:
				mTextDescription.setText(R.string.dres_description);
				mPlanetImage.setImageResource(R.drawable.dres);

				orbitalValues.addAll(Arrays.asList(o9Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p9Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				scienceValues.addAll(Arrays.asList(s9Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv9Values));
				listValues.put(listDataHeader.get(4), deltaVValues);
				departureValues.addAll(Arrays.asList(d9Values));
				listValues.put(listDataHeader.get(5), departureValues);

				science.remove(1);
				listDataChild.put(listDataHeader.get(3), science);

				deltaV.remove(9);
				listDataChild.put(listDataHeader.get(4), deltaV);

				departurePlanets.remove(4);
				listDataChild.put(listDataHeader.get(5), departurePlanets);

				listDataHeader.remove(2);
				listDataChild.remove(2);
				break;
			case 10:
				mTextDescription.setText(R.string.jool_description);
				mPlanetImage.setImageResource(R.drawable.jool);

				orbitalValues.addAll(Arrays.asList(o10Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p10Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				atmosphericValues.addAll(Arrays.asList(a10Values));
				listValues.put(listDataHeader.get(2), atmosphericValues);
				scienceValues.addAll(Arrays.asList(s10Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv10Values));
				listValues.put(listDataHeader.get(4), deltaVValues);
				departureValues.addAll(Arrays.asList(d10Values));
				listValues.put(listDataHeader.get(5), departureValues);

				physical.remove(12);
				listDataChild.put(listDataHeader.get(1), physical);

				science.remove(0);
				listDataChild.put(listDataHeader.get(3), science);

				deltaV.remove(10);
				listDataChild.put(listDataHeader.get(4), deltaV);

				departurePlanets.remove(5);
				listDataChild.put(listDataHeader.get(5), departurePlanets);
				break;
			case 11:
				mTextDescription.setText(R.string.laythe_description);
				mPlanetImage.setImageResource(R.drawable.laythe);

				orbitalValues.addAll(Arrays.asList(o11Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p11Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				atmosphericValues.addAll(Arrays.asList(a11Values));
				listValues.put(listDataHeader.get(2), atmosphericValues);
				scienceValues.addAll(Arrays.asList(s11Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv11Values));
				listValues.put(listDataHeader.get(4), deltaVValues);
				departureValues.addAll(Arrays.asList(d11Values));
				listValues.put(listDataHeader.get(5), departureValues);

				physical.remove(8);
				listDataChild.put(listDataHeader.get(1), physical);

				deltaV.remove(11);
				listDataChild.put(listDataHeader.get(4), deltaV);

				departureJoolMoons.remove(0);
				listDataChild.put(listDataHeader.get(5), departureJoolMoons);
				break;
			case 12:
				mTextDescription.setText(R.string.vall_description);
				mPlanetImage.setImageResource(R.drawable.vall);

				orbitalValues.addAll(Arrays.asList(o12Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p12Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				scienceValues.addAll(Arrays.asList(s12Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv12Values));
				listValues.put(listDataHeader.get(4), deltaVValues);
				departureValues.addAll(Arrays.asList(d12Values));
				listValues.put(listDataHeader.get(5), departureValues);

				physical.remove(8);
				listDataChild.put(listDataHeader.get(1), physical);

				science.remove(1);
				listDataChild.put(listDataHeader.get(3), science);

				deltaV.remove(12);
				listDataChild.put(listDataHeader.get(4), deltaV);

				departureJoolMoons.remove(1);
				listDataChild.put(listDataHeader.get(5), departureJoolMoons);

				listDataHeader.remove(2);
				listDataChild.remove(2);
				break;
			case 13:
				mTextDescription.setText(R.string.tylo_description);
				mPlanetImage.setImageResource(R.drawable.tylo);

				orbitalValues.addAll(Arrays.asList(o13Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p13Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				scienceValues.addAll(Arrays.asList(s13Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv13Values));
				listValues.put(listDataHeader.get(4), deltaVValues);
				departureValues.addAll(Arrays.asList(d13Values));
				listValues.put(listDataHeader.get(5), departureValues);

				physical.remove(8);
				listDataChild.put(listDataHeader.get(1), physical);

				science.remove(1);
				listDataChild.put(listDataHeader.get(3), science);

				deltaV.remove(13);
				listDataChild.put(listDataHeader.get(4), deltaV);

				departureJoolMoons.remove(2);
				listDataChild.put(listDataHeader.get(5), departureJoolMoons);

				listDataHeader.remove(2);
				listDataChild.remove(2);
				break;
			case 14:
				mTextDescription.setText(R.string.bop_description);
				mPlanetImage.setImageResource(R.drawable.bop);

				orbitalValues.addAll(Arrays.asList(o14Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p14Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				scienceValues.addAll(Arrays.asList(s14Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv14Values));
				listValues.put(listDataHeader.get(4), deltaVValues);
				departureValues.addAll(Arrays.asList(d14Values));
				listValues.put(listDataHeader.get(5), departureValues);

				physical.remove(8);
				listDataChild.put(listDataHeader.get(1), physical);

				science.remove(1);
				listDataChild.put(listDataHeader.get(3), science);

				deltaV.remove(14);
				listDataChild.put(listDataHeader.get(4), deltaV);

				departureJoolMoons.remove(3);
				listDataChild.put(listDataHeader.get(5), departureJoolMoons);

				listDataHeader.remove(2);
				listDataChild.remove(2);
				break;
			case 15:
				mTextDescription.setText(R.string.pol_description);
				mPlanetImage.setImageResource(R.drawable.pol);

				orbitalValues.addAll(Arrays.asList(o15Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p15Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				scienceValues.addAll(Arrays.asList(s15Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv15Values));
				listValues.put(listDataHeader.get(4), deltaVValues);
				departureValues.addAll(Arrays.asList(d15Values));
				listValues.put(listDataHeader.get(5), departureValues);

				physical.remove(8);
				listDataChild.put(listDataHeader.get(1), physical);

				science.remove(1);
				listDataChild.put(listDataHeader.get(3), science);

				deltaV.remove(15);
				listDataChild.put(listDataHeader.get(4), deltaV);

				departureJoolMoons.remove(4);
				listDataChild.put(listDataHeader.get(5), departureJoolMoons);

				listDataHeader.remove(2);
				listDataChild.remove(2);

				break;
			case 16:
				mTextDescription.setText(R.string.eeloo_description);
				mPlanetImage.setImageResource(R.drawable.eeloo);

				orbitalValues.addAll(Arrays.asList(o16Values));
				listValues.put(listDataHeader.get(0), orbitalValues);
				physicalValues.addAll(Arrays.asList(p16Values));
				listValues.put(listDataHeader.get(1), physicalValues);
				scienceValues.addAll(Arrays.asList(s16Values));
				listValues.put(listDataHeader.get(3), scienceValues);
				deltaVValues.addAll(Arrays.asList(dv16Values));
				listValues.put(listDataHeader.get(4), deltaVValues);
				departureValues.addAll(Arrays.asList(d16Values));
				listValues.put(listDataHeader.get(5), departureValues);

				science.remove(1);
				listDataChild.put(listDataHeader.get(3), science);

				deltaV.remove(16);
				listDataChild.put(listDataHeader.get(4), deltaV);

				departurePlanets.remove(6);
				listDataChild.put(listDataHeader.get(5), departurePlanets);

				listDataHeader.remove(2);
				listDataChild.remove(2);
				break;
			}
			// Send all the data above through the ExpandableListAdapter, and return it to getItem() above
			ExpandableListAdapter listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild, listValues);
			ExpandableListView expListView = (ExpandableListView) rootView.findViewById(R.id.expListView);
			expListView.setAdapter(listAdapter);
			return rootView;
		}
	}
}