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
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class MapActivity extends Activity {

	// Used to display maps from CelestialBodyActivity. Uses a webview for the simple built-in pinch to zoom functionality

	public static final String EXTRA_MAPS_ID = "com.amagi82.kerbalspaceapp.planet_id";
	private boolean black = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Full screen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_map);

		String imageUrl = "file:///android_asset/kerbin_biomes.png";

		switch (getIntent().getIntExtra(EXTRA_MAPS_ID, 0)) {
		case 0:
			imageUrl = "file:///android_asset/kerbin_biomes.png";
			break;
		case 1:
			imageUrl = "file:///android_asset/mun_biomes.png";
			break;
		case 2:
			imageUrl = "file:///android_asset/minmus_biomes.png";
			break;
		case 3:
			imageUrl = "file:///android_asset/topographical_moho.jpg";
			break;
		case 4:
			imageUrl = "file:///android_asset/topographical_eve.jpg";
			break;
		case 5:
			imageUrl = "file:///android_asset/topographical_gilly.jpg";
			break;
		case 6:
			imageUrl = "file:///android_asset/topographical_kerbin.jpg";
			break;
		case 7:
			imageUrl = "file:///android_asset/topographical_mun.jpg";
			break;
		case 8:
			imageUrl = "file:///android_asset/topographical_minmus.jpg";
			break;
		case 9:
			imageUrl = "file:///android_asset/topographical_duna.jpg";
			break;
		case 10:
			imageUrl = "file:///android_asset/topographical_ike.jpg";
			break;
		case 11:
			imageUrl = "file:///android_asset/topographical_dres.jpg";
			break;
		case 13:
			imageUrl = "file:///android_asset/topographical_laythe.jpg";
			break;
		case 14:
			imageUrl = "file:///android_asset/topographical_vall.jpg";
			break;
		case 15:
			imageUrl = "file:///android_asset/topographical_tylo.jpg";
			break;
		case 16:
			imageUrl = "file:///android_asset/topographical_bop.jpg";
			break;
		case 17:
			imageUrl = "file:///android_asset/topographical_pol.jpg";
			break;
		case 18:
			imageUrl = "file:///android_asset/topographical_eeloo.jpg";
			break;
		case 19:
			black = false;
			imageUrl = "file:///android_asset/atmosphere_eve.png";
			break;
		case 24:
			black = false;
			imageUrl = "file:///android_asset/atmosphere_duna.png";
			break;
		}
		WebView wv = (WebView) findViewById(R.id.mapView);
		wv.getSettings().setBuiltInZoomControls(true);
		wv.getSettings().setLoadWithOverviewMode(true);
		wv.getSettings().setUseWideViewPort(true);
		if (black) {
			wv.setBackgroundColor(Color.parseColor("#000000"));
		}
		wv.loadUrl(imageUrl);
	}

}
