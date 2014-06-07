package com.amagi82.kerbalspaceapp;

import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

public class CelestialBodyActivity extends FragmentActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;        //Loads fragments into memory
	private ViewPager mViewPager;                      //Hosts section contents and allows swiping between pages
	public static final String EXTRA_PLANET_ID = "com.amagi82.kerbalspaceapp.planet_id";
	public static final String CELESTIAL_BODY_TYPE = "com.amagi82.kerbalspaceapp.planet_type";
	int navigationState;                               //ViewPager state- shows moons only when navigationState != 0

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_celestial_body);
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		
		// Set up the ViewPager with the sections adapter, passing in the appropriate state and page
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		navigationState = getIntent().getIntExtra(CELESTIAL_BODY_TYPE,0);
		mViewPager.setCurrentItem(getIntent().getIntExtra(EXTRA_PLANET_ID, 0));

	}

	  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.planet, menu);
		return true;
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		//Default number of pages- changed when altering navigationState
		int currentPages = 8;
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a CelestialBodyFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new CelestialBodyFragment();
			Bundle args = new Bundle();
			args.putInt(CelestialBodyFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return currentPages;
		}

		//Displayed planets/moons depend on which navigationState we are in. Swiping away from the initially selected moon
		//brings you back to the default navigationState, 0. 
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			if (navigationState == 0){
				if(currentPages != 8){
					currentPages = 8;
					notifyDataSetChanged();}
				switch (position) {
				case 0:
					return getString(R.string.Kerbol).toUpperCase(l);
				case 1:
					return getString(R.string.Moho).toUpperCase(l);
				case 2:
					return getString(R.string.Eve).toUpperCase(l);
				case 3:
					return getString(R.string.Kerbin).toUpperCase(l);
				case 4:
					return getString(R.string.Duna).toUpperCase(l);
				case 5:
					return getString(R.string.Dres).toUpperCase(l);
				case 6:
					return getString(R.string.Jool).toUpperCase(l);
				case 7:
					return getString(R.string.Eeloo).toUpperCase(l);
				}
			}else if (navigationState == 1){
				if(currentPages != 7){
					currentPages = 7;
					notifyDataSetChanged();}
				switch (position) {
				case 0:
					navigationState = 0;
					mViewPager.setCurrentItem(1);
					return getString(R.string.Kerbol).toUpperCase(l);
				case 1:
					return getString(R.string.Moho).toUpperCase(l);
				case 2:
					return getString(R.string.Eve).toUpperCase(l);
				case 3:
					return getString(R.string.Gilly).toUpperCase(l);
				case 4:
					return getString(R.string.Kerbin).toUpperCase(l);
				case 5:
					return getString(R.string.Duna).toUpperCase(l);
				case 6:
					navigationState = 0;
					mViewPager.setCurrentItem(3);
					return getString(R.string.Dres).toUpperCase(l);
				}
			}else if (navigationState == 2){
				if(currentPages != 8){
					currentPages = 8;
					notifyDataSetChanged();}
				switch (position) {
				case 0:
					navigationState = 0;
					mViewPager.setCurrentItem(3);
					return getString(R.string.Moho).toUpperCase(l);
				case 1:
					return getString(R.string.Eve).toUpperCase(l);
				case 2:
					return getString(R.string.Kerbin).toUpperCase(l);
				case 3:
					return getString(R.string.Mun).toUpperCase(l);
				case 4:
					return getString(R.string.Minmus).toUpperCase(l);
				case 5:
					return getString(R.string.Duna).toUpperCase(l);
				case 6:
					return getString(R.string.Dres).toUpperCase(l);
				case 7:
					navigationState = 0;
					mViewPager.setCurrentItem(4);
					return getString(R.string.Jool).toUpperCase(l);
				}
			}else if (navigationState == 3){
				if(currentPages != 7){
					currentPages = 7;
					notifyDataSetChanged();}
				switch (position) {
				case 0:
					navigationState = 0;
					mViewPager.setCurrentItem(4);
					return getString(R.string.Eve).toUpperCase(l);
				case 1:
					return getString(R.string.Kerbin).toUpperCase(l);
				case 2:
					return getString(R.string.Duna).toUpperCase(l);
				case 3:
					return getString(R.string.Ike).toUpperCase(l);
				case 4:
					return getString(R.string.Dres).toUpperCase(l);
				case 5:
					return getString(R.string.Jool).toUpperCase(l);
				case 6:
					navigationState = 0;
					mViewPager.setCurrentItem(6);
					return getString(R.string.Eeloo).toUpperCase(l);
				}
			}else{
				if(currentPages != 11){
					currentPages = 11;
					notifyDataSetChanged();}
				switch (position) {
				case 0:
					navigationState = 0;
					mViewPager.setCurrentItem(5);
					return getString(R.string.Duna).toUpperCase(l);
				case 1:
					return getString(R.string.Dres).toUpperCase(l);
				case 2:
					return getString(R.string.Jool).toUpperCase(l);
				case 3:
					return getString(R.string.Laythe).toUpperCase(l);
				case 4:
					return getString(R.string.Vall).toUpperCase(l);
				case 5:
					return getString(R.string.Tylo).toUpperCase(l);
				case 6:
					return getString(R.string.Bop).toUpperCase(l);
				case 7:
					return getString(R.string.Pol).toUpperCase(l);
				case 8:
					return getString(R.string.Eeloo).toUpperCase(l);
				case 9:
					return null;
				case 10:
					navigationState = 0;
					mViewPager.setCurrentItem(6);
					return null;
				}
			}
			return null;
		}
	}

	//Fragment that contains the individual planets/moons to display
	public static class CelestialBodyFragment extends Fragment {
		//Grabs the section number for the fragment
		public static final String ARG_SECTION_NUMBER = "section_number";
		
		public CelestialBodyFragment(){
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_celestial_body, container, false);
			//TODO Modify fragment contents
			//ImageButton imageButtonMain = (ImageButton) getActivity().findViewById(R.id.imageButtonMain);
			//imageButtonMain.setImageResource(R.drawable.kerbol);
			
			return rootView;
		}
	}
}