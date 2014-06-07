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

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class MissionData implements Parcelable, Serializable {

	// Custom object for the MissionPlanner

	private static final long serialVersionUID = 1L;
	private int planetId = 0;
	private boolean landing = false;
	private int takeoffAltitude = 0;
	private boolean toOrbit = true;
	private int orbitAltitude = 0;
	private int iconStatus = 0; // 0 = lander, 1 = parachute, 2 = orbit, 3 = rocket

	public MissionData(int planetId, boolean landing, int takeoffAltitude, boolean toOrbit, int orbitAltitude, int iconStatus) {
		this.planetId = planetId;
		this.landing = landing;
		this.takeoffAltitude = takeoffAltitude;
		this.toOrbit = toOrbit;
		this.orbitAltitude = orbitAltitude;
		this.iconStatus = iconStatus;
		switch (iconStatus) {
		case 0:
			landing = true;
			toOrbit = true;
			break;
		case 1:
			landing = false;
			toOrbit = false;
			break;
		case 2:
			landing = false;
			toOrbit = true;
			break;
		case 3:
			landing = false;
			toOrbit = true;
			break;
		}
	}

	public void setPlanetId(int planetId) {
		this.planetId = planetId;
	}

	public int getPlanetId() {
		return planetId;
	}

	public void setLanding(boolean landing) {
		this.landing = landing;
	}

	public boolean getLanding() {
		return landing;
	}

	public void setTakeoffAltitude(int takeoffAltitude) {
		this.takeoffAltitude = takeoffAltitude;
	}

	public int getTakeoffAltitude() {
		return takeoffAltitude;
	}

	public void setToOrbit(boolean toOrbit) {
		this.toOrbit = toOrbit;
	}

	public boolean getToOrbit() {
		return toOrbit;
	}

	public void setOrbitAltitude(int orbitAltitude) {
		this.orbitAltitude = orbitAltitude;
	}

	public int getOrbitAltitude() {
		return orbitAltitude;
	}

	public void setIconStatus(int iconStatus) {
		this.iconStatus = iconStatus;
		switch (iconStatus) {
		case 0:
			landing = true;
			toOrbit = true;
			break;
		case 1:
			landing = false;
			toOrbit = false;
			break;
		case 2:
			landing = false;
			toOrbit = true;
			break;
		case 3:
			landing = false;
			toOrbit = true;
			break;
		}
	}

	public int getIconStatus() {
		return iconStatus;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(planetId);
		out.writeByte((byte) (landing ? 1 : 0));
		out.writeInt(takeoffAltitude);
		out.writeByte((byte) (toOrbit ? 1 : 0));
		out.writeInt(orbitAltitude);
		out.writeInt(iconStatus);
	}

	private MissionData(Parcel in) {
		planetId = in.readInt();
		landing = in.readByte() != 0;
		takeoffAltitude = in.readInt();
		toOrbit = in.readByte() != 0;
		orbitAltitude = in.readInt();
		iconStatus = in.readInt();
	}

	public static final Parcelable.Creator<MissionData> CREATOR = new Parcelable.Creator<MissionData>() {
		@Override
		public MissionData createFromParcel(Parcel in) {
			return new MissionData(in);
		}

		@Override
		public MissionData[] newArray(int size) {
			return new MissionData[size];
		}
	};
}
