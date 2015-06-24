package com.techlung.android.glow.ui;

import java.util.ArrayList;

import com.techlung.android.glow.GlowActivity;
import com.techlung.android.glow.R;
import com.techlung.android.glow.adapter.TractPairArrayAdapter;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.model.TractPair;

import android.support.v4.app.ListFragment;

public class SelectionListFragment extends ListFragment  {

	public static final String TAG = SelectionListFragment.class.getName();

	TractPairArrayAdapter arrayAdapter;
	
	public void onResume() {
		super.onResume();
		setupListe((GlowActivity) getActivity(), GlowData.getInstance().getPamphlets());
	}
	
	public void setupListe( GlowActivity activity, ArrayList<Tract> data) {
			arrayAdapter = new TractPairArrayAdapter(activity, R.layout.selection_list_item, TractPair.createPairList(data));
			setListAdapter(arrayAdapter);
			arrayAdapter.notifyDataSetChanged();			
			arrayAdapter.setListener(activity);
	}
}
