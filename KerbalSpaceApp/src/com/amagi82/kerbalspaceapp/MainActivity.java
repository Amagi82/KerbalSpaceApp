package com.amagi82.kerbalspaceapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
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
    CustomDrawerAdapter adapter;
    List<DrawerItem> dataList;
    
	/* This Activity is the default, and presents a list of icons for each planet 
	* so the user may access them quickly. */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dataList = new ArrayList<DrawerItem>();
		mTitle = mDrawerTitle = getTitle();
        //mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        	// set up the drawer's list view with items and click listener
        	// Add Drawer Item to dataList
     	dataList.add(new DrawerItem("Planets")); // adding a header to the list
     	dataList.add(new DrawerItem("Kerbol", R.drawable.kerbol));
     	dataList.add(new DrawerItem("Moho", R.drawable.moho));
     	dataList.add(new DrawerItem("Eve", R.drawable.eve));
     	dataList.add(new DrawerItem("Kerbin", R.drawable.kerbin));
     	dataList.add(new DrawerItem("Duna", R.drawable.duna));
     	dataList.add(new DrawerItem("Dres", R.drawable.dres));
     	dataList.add(new DrawerItem("Jool", R.drawable.jool));
     	dataList.add(new DrawerItem("Eeloo", R.drawable.eeloo));
     		
     	dataList.add(new DrawerItem("Calculators")); // adding a header to the list
     	dataList.add(new DrawerItem("Delta-V", R.drawable.ic_launcher));
     		
     	adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, dataList);

     	mDrawerList.setAdapter(adapter);

     	mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
        
    }

   

    private void selectItem(int position) {
    	
    	switch(position){
    	case 0:
    		startActivity(new Intent(this, CelestialBodyActivity.class));
    		break;
    	case 1:
    		Log.e(null, "goMoho");
    		goMoho(null);
    		break;
    	case 2:
    		Log.e(null, "goEve");
    		goEve(null);
    		break;
    	case 3:
    		goKerbin(null);
    		break;
    	case 4:
    		goDuna(null);
    		break;
    	case 5:
    		goDres(null);
    		break;
    	case 6:
    		goJool(null);
    		break;
    	case 7:
    		goEeloo(null);
    		break;
    	default: 
    		break;
    	}
        mDrawerList.setItemChecked(position, true);
        setTitle(dataList.get(position).getItemName());
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
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	if (dataList.get(position).getTitle() == null) {
				selectItem(position);
			}
        }
    }
	
	//The following methods send you to the correct ViewPager in CelestialBodyActivity
	public void goKerbol(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 0);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 0);
  		startActivity(i);
  	}
	public void goMoho(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 1);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 0);
  		startActivity(i);
  	}
	public void goEve(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 2);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 0);
  		startActivity(i);
  	}
	public void goGilly(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 3);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 1);
  		startActivity(i);
  	}
	public void goKerbin(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 3);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 0);
  		startActivity(i);
  	}
	public void goMun(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 3);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 2);
  		startActivity(i);
  	}
	public void goMinmus(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 4);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 2);
  		startActivity(i);
  	}
	public void goDuna(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 4);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 0);
  		startActivity(i);
  	}
	public void goIke(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 3);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 3);
  		startActivity(i);
  	}
	public void goDres(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 5);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 0);
  		startActivity(i);
  	}
	public void goJool(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 6);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 0);
  		startActivity(i);
  	}
	public void goLaythe(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 3);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 4);
  		startActivity(i);
  	}
	public void goVall(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 4);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 4);
  		startActivity(i);
  	}
	public void goTylo(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 5);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 4);
  		startActivity(i);
  	}
	public void goBop(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 6);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 4);
  		startActivity(i);
  	}
	public void goPol(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 7);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 4);
  		startActivity(i);
  	}
	public void goEeloo(View view){
  		Intent i = new Intent(this, CelestialBodyActivity.class);
  		i.putExtra(CelestialBodyActivity.EXTRA_PLANET_ID, 7);
  		i.putExtra(CelestialBodyActivity.CELESTIAL_BODY_TYPE, 0);
  		startActivity(i);
  	}

}
