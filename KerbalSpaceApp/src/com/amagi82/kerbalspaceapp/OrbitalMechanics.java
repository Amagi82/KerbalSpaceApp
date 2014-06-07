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

public class OrbitalMechanics {

	// This class handles calculations related to Orbital Mechanics.

	static double gravParameter[] = { 1.1723328e18, 1.6860938e11, 8.1717302e12, 8289449.8, 3.5316e12, 6.5138398e10, 1.7658e9, 3.0136321e11,
			1.8568369e10, 2.1484489e10, 2.8252800e14, 1.962e12, 2.074815e11, 2.82528e12, 2.4868349e9, 7.2170208e8, 7.4410815e10 };
	static double scaleHeight[] = { 0, 0, 7000, 0, 5000, 0, 0, 3000, 0, 0, 10000, 4000, 0, 0, 0, 0, 0 }; // atmospheric scale height
	static double surfacePressure[] = { 0, 0, 5, 0, 1, 0, 0, 0.2, 0, 0, 15, 0.8, 0, 0, 0, 0, 0 }; // surface pressure in atmospheres
	static double equatorialRadius[] = { 0, 250000, 700000, 13000, 600000, 200000, 60000, 320000, 130000, 138000, 6000000, 500000, 300000,
			600000, 65000, 44000, 210000 };
	static double siderealRotationVelocity[] = { 0, 1.2982, 54.636, 2.8909, 174.53, 9.0416, 9.3315, 30.688, 12.467, 24.916, 1047.2, 59.297,
			17.789, 17.789, 0.75005, 0.30653, 1.2982 };
	static double semiMajorAxis[] = { 0, 5263138304L, 9832684544L, 31500000, 13599840256L, 12000000, 47000000, 20726155264L, 3200000,
			40839348203L, 68773560320L, 27184000, 43152000, 68500000, 128500000, 179890000, 90118820000L };
	static double eccentricity[] = { 0, 0.2, 0.01, 0.55, 0, 0, 0, 0.05, 0.03, 0.14, 0.05, 0, 0, 0, 0.24, 0.17, 0.26 };
	static double sphereOfInfluence[] = { 0, 9646663, 85109365, 126123, 84159286, 2429559, 2429559, 47921949, 1049599, 32832840,
			2455985200L, 3723646, 2406401, 10856518, 1221061, 1042139, 119100000 };
	static double inclination[] = { 0, 7, 2.1, 12, 0, 0, 6, 0.06, 0.2, 5, 1.304, 0, 0, 0.025, 15, 4.25, 6.15 };
	static int minOrbit[] = { 0, 6817, 96708, 6400, 69078, 7061, 5725, 41447, 12725, 5670, 138155, 55262, 7976, 12695, 21758, 5590, 3874 };
	static int highPoint[] = { 0, 6817, 7526, 6400, 6761, 7061, 5725, 8264, 12725, 5670, 0, 5600, 7976, 12695, 21758, 5590, 3874 };
	static boolean hasAtmosphere[] = { false, false, true, false, true, false, false, true, false, false, true, true, false, false, false,
			false, false };
	static int thatsNoMoon[] = { 0, 1, 2, 2, 4, 4, 4, 7, 7, 9, 10, 10, 10, 10, 10, 10, 16 };
	static boolean isMoon[] = { false, false, false, true, false, true, true, false, true, false, false, true, true, true, true, true,
			false };
	static int parentBody[] = { 0, 0, 0, 2, 0, 4, 4, 0, 7, 0, 0, 10, 10, 10, 10, 10, 0 };
	static double deltaVInclinationWorst, deltaVInclinationBest, deltaVInclinationChange;

	// Global settings
	public static int mClearanceValue;
	public static float mMarginsValue, mInclinationValue;

	// This method calculates the exact deltaV required to get to orbit around any body in KSP. It will use a desired final orbit
	// altitude if that value is higher than the minimum orbit plus the global clearance value.
	public static int getToOrbit(int planet, double takeOffAltitude, double finalOrbit) {
		double r1 = equatorialRadius[planet] + takeOffAltitude;
		double r2 = equatorialRadius[planet] + minOrbit[planet] + mClearanceValue;
		if (finalOrbit > r2) {
			r2 = finalOrbit;
		}
		double gravity = (gravParameter[planet]) / (r1 * r1); // force of gravity at a given altitude
		double pressure = surfacePressure[planet] * Math.exp(-takeOffAltitude / scaleHeight[planet]); // pressure at a given altitude
		double atmosphericDensity = 1.2230948554874 * pressure;
		double terminalVelocity = Math.sqrt((1250 * gravParameter[planet]) / (r1 * r1 * atmosphericDensity));
		double deltaV;

		// Includes calculations for atmospheric losses only when atmosphere is present
		if (hasAtmosphere[planet]) {
			deltaV = (Math.sqrt(gravParameter[planet] / r1) * Math.sqrt((2 * r2) / (r1 + r2))
					+ (Math.sqrt(gravParameter[planet] / r2) * (1 - (Math.sqrt((2 * r1) / (r1 + r2))))) - siderealRotationVelocity[planet])
					+ ((4 * gravity * scaleHeight[planet]) / terminalVelocity);
		} else {
			deltaV = Math.sqrt(gravParameter[planet] / r1) * Math.sqrt((2 * r2) / (r1 + r2))
					+ (Math.sqrt(gravParameter[planet] / r2) * (1 - (Math.sqrt((2 * r1) / (r1 + r2))))) - siderealRotationVelocity[planet];
		}
		return (int) Math.round(deltaV * mMarginsValue);
	}

	// Landing deltaV is assumed to be zero (using parachutes) for any body with an atmosphere. In reality, you may need a small amount for
	// certain planets, notably Duna, but these values vary dramatically based on craft design, and are generally small, so they are ignored
	// for convenience.
	public static int getLandingDeltaV(int planet, double landingAltitude, double startOrbit) {
		if (hasAtmosphere[planet]) {
			return 0;
		}
		return getToOrbit(planet, landingAltitude, startOrbit);
	}

	// This method calculates the exact deltaV required to get from a parking orbit around one body to another. Yes, this is a ridiculously
	// lengthy method, but OrbitalMechanics is complicated.
	public static int getTransferDeltaV(int planetStart, int planetEnd, double altitudeStart, double altitudeEnd) {
		double deltaV1, deltaV2, deltaV3, parentEccentricity, apoapsisVelocity, periapsisVelocity;
		double orbitSemiMajorAxis; // semi major axis of the Hohmann transfer between two planets
		double r1 = altitudeStart + equatorialRadius[planetStart];
		double r2 = altitudeEnd + equatorialRadius[planetEnd];
		double r2b = minOrbit[parentBody[planetStart]] + equatorialRadius[parentBody[planetStart]];

		// Set variables for the situation
		if (parentBody[planetStart] == parentBody[planetEnd] && isMoon[planetStart]) {
			if (planetStart > planetEnd) {
				parentEccentricity = getEccentricity(semiMajorAxis[planetStart], semiMajorAxis[planetEnd]);
				orbitSemiMajorAxis = getSemiMajorAxis(semiMajorAxis[planetStart], semiMajorAxis[planetEnd]);
			} else {
				parentEccentricity = getEccentricity(semiMajorAxis[planetEnd], semiMajorAxis[planetStart]);
				orbitSemiMajorAxis = getSemiMajorAxis(semiMajorAxis[planetEnd], semiMajorAxis[planetStart]);
			}
			apoapsisVelocity = getApoapsisVelocity(orbitSemiMajorAxis, parentEccentricity, gravParameter[parentBody[planetStart]]);
			periapsisVelocity = getPeriapsisVelocity(orbitSemiMajorAxis, parentEccentricity, gravParameter[parentBody[planetStart]]);
		} else if (thatsNoMoon[planetStart] > thatsNoMoon[planetEnd]) {
			parentEccentricity = getEccentricity(semiMajorAxis[thatsNoMoon[planetStart]], semiMajorAxis[thatsNoMoon[planetEnd]]);
			orbitSemiMajorAxis = getSemiMajorAxis(semiMajorAxis[thatsNoMoon[planetStart]], semiMajorAxis[thatsNoMoon[planetEnd]]);
			apoapsisVelocity = getApoapsisVelocity(orbitSemiMajorAxis, parentEccentricity, gravParameter[0]);
			periapsisVelocity = getPeriapsisVelocity(orbitSemiMajorAxis, parentEccentricity, gravParameter[0]);
		} else {
			parentEccentricity = getEccentricity(semiMajorAxis[thatsNoMoon[planetEnd]], semiMajorAxis[thatsNoMoon[planetStart]]);
			orbitSemiMajorAxis = getSemiMajorAxis(semiMajorAxis[thatsNoMoon[planetEnd]], semiMajorAxis[thatsNoMoon[planetStart]]);
			apoapsisVelocity = getApoapsisVelocity(orbitSemiMajorAxis, parentEccentricity, gravParameter[0]);
			periapsisVelocity = getPeriapsisVelocity(orbitSemiMajorAxis, parentEccentricity, gravParameter[0]);
		}

		deltaVInclinationBest = getDeltaVInclinationChange(planetStart, planetEnd, apoapsisVelocity);
		deltaVInclinationWorst = getDeltaVInclinationChange(planetStart, planetEnd, periapsisVelocity);
		deltaVInclinationChange = ((deltaVInclinationWorst - deltaVInclinationBest) * mInclinationValue + deltaVInclinationBest);

		// Hohmann transfer around the same planet for a higher or lower orbit
		if (planetStart == planetEnd) {
			altitudeStart = (equatorialRadius[planetStart] + altitudeStart);
			altitudeEnd = (equatorialRadius[planetEnd] + altitudeEnd);
			deltaV1 = Math.abs(Math.sqrt(gravParameter[planetStart] / altitudeStart)
					* (Math.sqrt((2 * altitudeEnd) / (altitudeStart + altitudeEnd)) - 1));
			deltaV2 = Math.abs(Math.sqrt(gravParameter[planetStart] / altitudeEnd)
					* (1 - Math.sqrt((2 * altitudeStart) / (altitudeStart + altitudeEnd))));
			return (int) Math.round((deltaV1 + deltaV2) * mMarginsValue);
		}

		// Between two planets
		if (isMoon[planetStart] == false && isMoon[planetEnd] == false) {
			deltaV1 = getDeltaVInjectionBurn(planetStart, planetEnd, altitudeStart);
			if (hasAtmosphere[planetEnd]) {
				deltaV2 = 0;
				deltaVInclinationChange = deltaVInclinationWorst * mInclinationValue;
			} else {
				deltaV2 = getDeltaVInsertionBurn(planetStart, planetEnd, altitudeEnd);

			}
			return (int) Math.round((deltaV1 + deltaV2 + deltaVInclinationChange) * mMarginsValue);
		}
		// Between two moons of the same body
		if (parentBody[planetStart] == parentBody[planetEnd]) {
			deltaV1 = Math.sqrt((gravParameter[parentBody[planetStart]] / semiMajorAxis[planetStart])
					* Math.pow(Math.sqrt((2 * semiMajorAxis[planetEnd] / (semiMajorAxis[planetStart] + semiMajorAxis[planetEnd]))) - 1, 2)
					+ gravParameter[planetStart]
					* ((2 / (equatorialRadius[planetStart] + altitudeStart)) - (2 / sphereOfInfluence[planetStart])))
					- Math.sqrt(gravParameter[planetStart] / (equatorialRadius[planetStart] + altitudeStart));
			if (hasAtmosphere[planetEnd]) {
				deltaV2 = 0;
				deltaVInclinationChange = deltaVInclinationWorst * mInclinationValue;
			} else {
				deltaV2 = Math.sqrt((gravParameter[parentBody[planetStart]] / semiMajorAxis[planetEnd])
						* Math.pow(
								Math.sqrt((2 * semiMajorAxis[planetStart] / (semiMajorAxis[planetStart] + semiMajorAxis[planetEnd]))) - 1,
								2) + gravParameter[planetEnd]
						* ((2 / (equatorialRadius[planetEnd] + altitudeEnd)) - (2 / sphereOfInfluence[planetEnd])))
						- Math.sqrt(gravParameter[planetEnd] / (equatorialRadius[planetEnd] + altitudeEnd));
			}
			return (int) Math.round((deltaV1 + deltaV2 + deltaVInclinationChange) * mMarginsValue);
		}
		// Planet to its own moon
		if (isMoon[planetStart] == false && parentBody[planetEnd] == planetStart) {
			deltaV1 = Math.sqrt(gravParameter[planetStart] / r1)
					* (Math.sqrt((2 * semiMajorAxis[planetEnd]) / (r1 + semiMajorAxis[planetEnd])) - 1);
			if (hasAtmosphere[planetEnd]) {
				deltaV2 = 0;
			} else {
				deltaV2 = Math.sqrt((gravParameter[planetStart] / semiMajorAxis[planetEnd])
						* Math.pow(Math.sqrt((2 * r1 / (r1 + semiMajorAxis[planetEnd]))) - 1, 2) + gravParameter[planetEnd]
						* ((2 / r2) - (2 / sphereOfInfluence[planetEnd])))
						- Math.sqrt(gravParameter[planetEnd] / r2);
			}
			return (int) Math.round((deltaV1 + deltaV2) * mMarginsValue);
		}

		// Planet to another planet's moon
		if (isMoon[planetStart] == false) {
			deltaV1 = getDeltaVInjectionBurn(planetStart, parentBody[planetEnd], altitudeStart);
			if (hasAtmosphere[planetEnd]) {
				deltaV2 = 0;
				deltaVInclinationChange = deltaVInclinationWorst * mInclinationValue;
			} else {
				deltaV2 = Math.sqrt((gravParameter[planetStart] / semiMajorAxis[planetEnd])
						* Math.pow(Math.sqrt((2 * r1 / (r1 + semiMajorAxis[planetEnd]))) - 1, 2) + gravParameter[planetEnd]
						* ((2 / r2) - (2 / sphereOfInfluence[planetEnd])))
						- Math.sqrt(gravParameter[planetEnd] / r2);
			}
			return (int) Math.round((deltaV1 + deltaV2 + deltaVInclinationChange) * mMarginsValue);
		}
		// Moon to parent body
		if (isMoon[planetStart] && planetEnd == parentBody[planetStart]) {
			deltaV1 = Math.sqrt((gravParameter[planetEnd] / semiMajorAxis[planetStart])
					* Math.pow(Math.sqrt((2 * r2 / (r2 + semiMajorAxis[planetStart]))) - 1, 2) + gravParameter[planetStart]
					* ((2 / r1) - (2 / sphereOfInfluence[planetStart])))
					- Math.sqrt(gravParameter[planetStart] / r1);
			return (int) Math.round(deltaV1 * mMarginsValue);
		}

		// Moon to moon of another body
		// @formatter:off
		if (isMoon[planetEnd]) {
			deltaV1 = Math.abs(Math.sqrt(gravParameter[parentBody[planetStart]] / r1) * (1 - Math.sqrt((2 * r2b) / (r1 + r2b))));
			deltaV2 = Math.sqrt((gravParameter[0] / semiMajorAxis[parentBody[planetStart]])* Math.pow(
							Math.sqrt((2 * semiMajorAxis[parentBody[planetEnd]] / (semiMajorAxis[parentBody[planetStart]] 
							+ semiMajorAxis[parentBody[planetEnd]]))) - 1, 2) + gravParameter[parentBody[planetStart]]
							* ((2 / (equatorialRadius[parentBody[planetStart]] + altitudeStart)) - (2 / sphereOfInfluence[parentBody[planetStart]])))
							- Math.sqrt(gravParameter[parentBody[planetStart]] * (2 / (equatorialRadius[parentBody[planetStart]] + altitudeStart) 
							- (1 / (((equatorialRadius[parentBody[planetStart]] + altitudeStart) + semiMajorAxis[planetStart]) / 2))));
			if (hasAtmosphere[planetEnd]) {
				deltaV3 = 0;
				deltaVInclinationChange = deltaVInclinationWorst * mInclinationValue;
			} else {
				deltaV3 = Math.sqrt(gravParameter[planetEnd] / r2) * (1 - Math.sqrt((2 * r1) / (r1 + r2)));
			}
			return (int) Math.round((deltaV1 + deltaV2 + deltaV3 + deltaVInclinationChange)* mMarginsValue);
		}
		// Moon to planet
		deltaV1 = Math.abs(Math.sqrt(gravParameter[parentBody[planetStart]] / r1) * (1 - Math.sqrt((2 * r2b) / (r1 + r2b))));
		deltaV2 = Math.sqrt((gravParameter[0] / semiMajorAxis[parentBody[planetStart]])* Math.pow(
						Math.sqrt((2 * semiMajorAxis[planetEnd] / (semiMajorAxis[parentBody[planetStart]] + semiMajorAxis[planetEnd]))) - 1,2)
						+ gravParameter[parentBody[planetStart]] * ((2 / (equatorialRadius[parentBody[planetStart]] + altitudeStart)) 
						- (2 / sphereOfInfluence[parentBody[planetStart]]))) - Math.sqrt(gravParameter[parentBody[planetStart]]
						* (2 / (equatorialRadius[parentBody[planetStart]] + altitudeStart) - (1 / (((equatorialRadius[parentBody[planetStart]] 
						+ altitudeStart) + semiMajorAxis[planetStart]) / 2))));
		if (hasAtmosphere[planetEnd]) {
			deltaV3 = 0;
			deltaVInclinationChange = deltaVInclinationWorst * mInclinationValue;
		} else {
			deltaV3 = getDeltaVInsertionBurn(parentBody[planetStart], planetEnd, altitudeEnd);
		}
		return (int) Math.round((deltaV1 + deltaV2 + deltaV3 + deltaVInclinationChange)* mMarginsValue);
	}

	// Minimum deltaV from orbit around planetStart necessary to intersect planetEnd's orbit, not including inclination changes. 
	//This only works between bodies orbiting the sun- slightly modified equations are required for other situations
	static double getDeltaVInjectionBurn(int planetStart, int planetEnd, double altitudeStart) {
		return Math.sqrt((gravParameter[0] / semiMajorAxis[planetStart])
				* Math.pow(Math.sqrt((2 * semiMajorAxis[planetEnd] / (semiMajorAxis[planetStart] + semiMajorAxis[planetEnd]))) - 1, 2)
				+ gravParameter[planetStart]
				* ((2 / (equatorialRadius[planetStart] + altitudeStart)) - (2 / sphereOfInfluence[planetStart])))
				- Math.sqrt(gravParameter[planetStart] / (equatorialRadius[planetStart] + altitudeStart));
	}

	// Minimum deltaV necessary for capture and circularization at planetEnd, assuming no aerobraking
	//This only works between bodies orbiting the sun- slightly modified equations are required for other situations
	static double getDeltaVInsertionBurn(int planetStart, int planetEnd, double altitudeEnd) {
		return Math.sqrt((gravParameter[0] / semiMajorAxis[planetEnd])
				* Math.pow(Math.sqrt((2 * semiMajorAxis[planetStart] / (semiMajorAxis[planetStart] + semiMajorAxis[planetEnd]))) - 1, 2)
				+ gravParameter[planetEnd] * ((2 / (equatorialRadius[planetEnd] + altitudeEnd)) - (2 / sphereOfInfluence[planetEnd])))
				- Math.sqrt(gravParameter[planetEnd] / (equatorialRadius[planetEnd] + altitudeEnd));
	}

	// delta V required to perform inclination change
	static double getDeltaVInclinationChange(int planetStart, int planetEnd, double velocity) {
		if(parentBody[planetStart] == parentBody[planetEnd] && isMoon[planetStart]){
			return Math.sqrt(2 * velocity * velocity * (1 - Math.cos(Math.toRadians(inclination[planetStart] - inclination[planetEnd]))));
		}
		return Math.sqrt(2 * velocity * velocity* (1 - Math.cos(Math.toRadians(inclination[thatsNoMoon[planetStart]] - inclination[thatsNoMoon[planetEnd]]))));
	}

	static double getSemiMajorAxis(double rApoapsis, double rPeriapsis) {
		return (rApoapsis + rPeriapsis) / 2;
	}

	static double getEccentricity(double rApoapsis, double rPeriapsis) {
		return (rApoapsis - rPeriapsis) / (rApoapsis + rPeriapsis);
	}

	static double getApoapsisVelocity(double semiMajorAxis, double eccentricity, double gravParameter) {
		return Math.sqrt(((1 - eccentricity) * gravParameter) / ((1 + eccentricity) * semiMajorAxis));
	}

	static double getPeriapsisVelocity(double semiMajorAxis, double eccentricity, double gravParameter) {
		return Math.sqrt(((1 + eccentricity) * gravParameter) / ((1 - eccentricity) * semiMajorAxis));
	}
}
