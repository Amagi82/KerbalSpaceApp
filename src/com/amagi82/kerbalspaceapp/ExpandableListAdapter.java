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

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	// This Adapter is used to display all the data in CelestialBodyActivity

	private final Context context;
	private final List<String> listDataHeader; // header titles
	private final HashMap<String, List<String>> listDataChild, listDataValues; // sub-categories and numerical values, respectively

	public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData,
			HashMap<String, List<String>> listValues) {
		this.context = context;
		this.listDataHeader = listDataHeader;
		this.listDataChild = listChildData;
		this.listDataValues = listValues;
	}

	// Returns the sub-categories
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition);
	}

	// Returns the numerical values
	public Object getOtherChild(int groupPosition, int childPosition) {
		return this.listDataValues.get(this.listDataHeader.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		final String childText = (String) getChild(groupPosition, childPosition);
		final String childValues = (String) getOtherChild(groupPosition, childPosition);

		// Finds the display width in dp, then chooses a single or multi-line layout for each
		DisplayMetrics displayMetrics = parent.getResources().getDisplayMetrics();
		float screenWidth = displayMetrics.widthPixels / displayMetrics.density;
		LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// list_item_multiline is used to display two lines of text for the deltaV section
		if (childValues.contains(";")) {
			convertView = inflater.inflate(R.layout.celestial_list_item_multiline, null);
			String splitValues[] = childValues.split(";");
			TextView mListItemText = (TextView) convertView.findViewById(R.id.listItem);
			mListItemText.setText(childText);
			TextView mListItemValueText = (TextView) convertView.findViewById(R.id.listItemValue);
			mListItemValueText.setText(splitValues[1]);
			TextView mListItemValueExtraText = (TextView) convertView.findViewById(R.id.listItemExtraValue);
			mListItemValueExtraText.setText(splitValues[0]);
			return convertView;
		} else if (10 * (childText.length() + childValues.length()) > screenWidth) {
			convertView = inflater.inflate(R.layout.celestial_list_item_twoline, null);
		} else {
			convertView = inflater.inflate(R.layout.celestial_list_item, null);
		}
		TextView mListItemText = (TextView) convertView.findViewById(R.id.listItem);
		mListItemText.setText(childText);
		TextView mListItemValueText = (TextView) convertView.findViewById(R.id.listItemValue);
		mListItemValueText.setText(childValues);

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this.listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.celestial_list_group, null);
		}

		TextView mListHeader = (TextView) convertView.findViewById(R.id.listHeader);
		mListHeader.setTypeface(null, Typeface.BOLD);
		mListHeader.setText(headerTitle);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}