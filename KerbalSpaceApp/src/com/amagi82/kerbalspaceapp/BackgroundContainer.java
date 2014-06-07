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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class BackgroundContainer extends FrameLayout {

	boolean mShowing = false, mUpdateBounds = false;
	int mOpenAreaTop, mOpenAreaBottom, mOpenAreaHeight;
	Drawable mShadowedBackground;

	public BackgroundContainer(Context context) {
		super(context);
		initialize();
	}

	public BackgroundContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public BackgroundContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	private void initialize() {
		mShadowedBackground = getContext().getResources().getDrawable(R.drawable.shadowed_background);
	}

	public void showBackground(int top, int bottom) {
		setWillNotDraw(false);
		mOpenAreaTop = top;
		mOpenAreaHeight = bottom;
		mShowing = true;
		mUpdateBounds = true;
	}

	public void hideBackground() {
		setWillNotDraw(true);
		mShowing = false;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mShowing) {
			if (mUpdateBounds) {
				mShadowedBackground.setBounds(0, 0, getWidth(), mOpenAreaHeight);
			}
			canvas.save();
			canvas.translate(0, mOpenAreaTop);
			mShadowedBackground.draw(canvas);
			canvas.restore();
		}
	}

}
