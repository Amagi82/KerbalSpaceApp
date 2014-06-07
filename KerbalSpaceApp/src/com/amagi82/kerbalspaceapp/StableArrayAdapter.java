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
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StableArrayAdapter extends ArrayAdapter<MissionData> {

	// This is the Adapter that handles the data for the MissionPlanner listview.

	HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
	private final Activity context;
	private final ArrayList<MissionData> missionData;
	View.OnTouchListener mTouchListener;
	int totalDeltaV;

	// ViewHolder increases speed and efficiency by recycling views rather than doing many findViewByIds, which are expensive.
	// Not strictly necessary for a small listview like this one, but a good practice to get into nonetheless
	static class ViewHolder {
		public TextView text;
		public ImageView image;
		public ImageView imagePlanetSelection;
		public ImageView imageLandingStatus;
		public TextView tvOrbitHeight;
		public TextView tvTakeOffHeight;
		public TextView tvDeltaVSum;
		public TextView tvDeltaVData1;
		public TextView tvDeltaVData2;
		public TextView tvDeltaVData3;
	}

	public StableArrayAdapter(Activity context, ArrayList<MissionData> missionData, View.OnTouchListener listener) {
		super(context, R.layout.opaque_text_view, R.layout.mission_list_item, missionData);
		this.context = context;
		this.missionData = missionData;
		mTouchListener = listener;
	}

	@Override
	public MissionData getItem(int position) {
		return missionData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		int takeoffDeltaV = 0, transferDeltaV = 0, landingDeltaV = 0;
		int planetId[] = { R.drawable.kerbol, R.drawable.moho, R.drawable.eve, R.drawable.gilly, R.drawable.kerbin, R.drawable.mun,
				R.drawable.minmus, R.drawable.duna, R.drawable.ike, R.drawable.dres, R.drawable.jool, R.drawable.laythe, R.drawable.vall,
				R.drawable.tylo, R.drawable.bop, R.drawable.pol, R.drawable.eeloo };

		// Reuse views
		if (convertView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(R.layout.mission_list_item, null);
			// Configure ViewHolder
			viewHolder = new ViewHolder();
			viewHolder.imagePlanetSelection = (ImageView) convertView.findViewById(R.id.imagePlanetSelection);
			viewHolder.imageLandingStatus = (ImageView) convertView.findViewById(R.id.imageLandingStatus);
			viewHolder.tvOrbitHeight = (TextView) convertView.findViewById(R.id.tvOrbitHeight);
			viewHolder.tvTakeOffHeight = (TextView) convertView.findViewById(R.id.tvTakeOffHeight);
			viewHolder.tvDeltaVSum = (TextView) convertView.findViewById(R.id.tvDeltaVSum);
			viewHolder.tvDeltaVData1 = (TextView) convertView.findViewById(R.id.tvDeltaVData1);
			viewHolder.tvDeltaVData2 = (TextView) convertView.findViewById(R.id.tvDeltaVData2);
			viewHolder.tvDeltaVData3 = (TextView) convertView.findViewById(R.id.tvDeltaVData3);
			convertView.setTag(viewHolder);
		}
		// Fill data
		viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.imagePlanetSelection.setImageResource(planetId[missionData.get(position).getPlanetId()]);
		int resId = R.drawable.ic_rocket;

		// Checks for inappropriate IconStatus for position 0, and resets it to Orbit if detected
		if (missionData.get(0).getIconStatus() == 0 || missionData.get(0).getIconStatus() == 1) {
			missionData.get(0).setIconStatus(2);
			missionData.get(0).setOrbitAltitude(OrbitalMechanics.minOrbit[missionData.get(0).getPlanetId()]);
		}
		// Checks for parachute(landing with no takeoff) at any point besides the end, and replaces it with a lander
		for (int i = 0; i < missionData.size() - 1; i++) {
			if (missionData.get(i).getIconStatus() == 1) {
				missionData.get(i).setIconStatus(0);
			}
		}

		// This makes the different delta V categories invisible/visible where relevant, sets the lander icon, and gets the values
		// from the OrbitalMechanics class
		switch (missionData.get(position).getIconStatus()) {
		case 0:
			resId = R.drawable.ic_lander;
			takeoffDeltaV = OrbitalMechanics.getToOrbit(missionData.get(position).getPlanetId(), missionData.get(position)
					.getTakeoffAltitude(), missionData.get(position).getOrbitAltitude());
			if (position != 0) {
				transferDeltaV = OrbitalMechanics.getTransferDeltaV(missionData.get(position - 1).getPlanetId(), missionData.get(position)
						.getPlanetId(), missionData.get(position - 1).getOrbitAltitude(), missionData.get(position).getOrbitAltitude());
			}
			landingDeltaV = OrbitalMechanics.getLandingDeltaV(missionData.get(position).getPlanetId(), missionData.get(position)
					.getTakeoffAltitude(), missionData.get(position).getOrbitAltitude());
			viewHolder.tvDeltaVData1.setVisibility(View.VISIBLE);
			viewHolder.tvDeltaVData2.setVisibility(View.VISIBLE);
			viewHolder.tvDeltaVData3.setVisibility(View.VISIBLE);
			break;
		case 1:
			resId = R.drawable.ic_parachute;
			if (position != 0) {
				transferDeltaV = OrbitalMechanics.getTransferDeltaV(missionData.get(position - 1).getPlanetId(), missionData.get(position)
						.getPlanetId(), missionData.get(position - 1).getOrbitAltitude(), missionData.get(position).getOrbitAltitude());
			}
			landingDeltaV = OrbitalMechanics.getLandingDeltaV(missionData.get(position).getPlanetId(), missionData.get(position)
					.getTakeoffAltitude(), missionData.get(position).getOrbitAltitude());
			viewHolder.tvDeltaVData1.setVisibility(View.VISIBLE);
			viewHolder.tvDeltaVData2.setVisibility(View.INVISIBLE);
			viewHolder.tvDeltaVData3.setVisibility(View.INVISIBLE);
			break;
		case 2:
			resId = R.drawable.ic_orbit;
			viewHolder.tvDeltaVData1.setVisibility(View.INVISIBLE);
			if (position != 0) {
				transferDeltaV = OrbitalMechanics.getTransferDeltaV(missionData.get(position - 1).getPlanetId(), missionData.get(position)
						.getPlanetId(), missionData.get(position - 1).getOrbitAltitude(), missionData.get(position).getOrbitAltitude());
				viewHolder.tvDeltaVData1.setVisibility(View.VISIBLE);
			}

			viewHolder.tvDeltaVData2.setVisibility(View.INVISIBLE);
			viewHolder.tvDeltaVData3.setVisibility(View.INVISIBLE);
			break;

		case 3:
			resId = R.drawable.ic_rocket;
			takeoffDeltaV = OrbitalMechanics.getToOrbit(missionData.get(position).getPlanetId(), missionData.get(position)
					.getTakeoffAltitude(), missionData.get(position).getOrbitAltitude());
			viewHolder.tvDeltaVData1.setVisibility(View.INVISIBLE);
			viewHolder.tvDeltaVData2.setVisibility(View.INVISIBLE);
			viewHolder.tvDeltaVData3.setVisibility(View.VISIBLE);
			break;
		}

		// Totals and displays the deltaV for each mission destination
		totalDeltaV = takeoffDeltaV + transferDeltaV + landingDeltaV;
		viewHolder.tvDeltaVData1.setText("Transfer: " + transferDeltaV + "m/s");
		viewHolder.tvDeltaVData2.setText("Landing: " + landingDeltaV + "m/s");
		viewHolder.tvDeltaVData3.setText("Takeoff: " + takeoffDeltaV + "m/s");
		viewHolder.tvDeltaVSum.setText(totalDeltaV + "m/s");
		viewHolder.imageLandingStatus.setImageResource(resId);

		// If the user entered an orbit of 0, replace it with the minimum orbit
		if (missionData.get(position).getOrbitAltitude() == 0) {
			viewHolder.tvOrbitHeight.setText(OrbitalMechanics.minOrbit[position] / 1000 + "km");
		} else {
			viewHolder.tvOrbitHeight.setText(missionData.get(position).getOrbitAltitude() / 1000 + "km");
		}
		viewHolder.tvTakeOffHeight.setText(missionData.get(position).getTakeoffAltitude() + "m");
		convertView.setOnTouchListener(mTouchListener);

		return convertView;
	}
}