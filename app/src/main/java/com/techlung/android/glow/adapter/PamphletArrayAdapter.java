package com.techlung.android.glow.adapter;

import java.util.ArrayList;

import com.techlung.android.glow.R;
import com.techlung.android.glow.model.Tract;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter fior displaying a list of colors, that can  be
 * @author hahn010
 *
 * @param <T>
 */
public class PamphletArrayAdapter extends ArrayAdapter<Tract> {

	private ArrayList<Tract> objects;
	private Context context;
	private PamphletArrayAdapter.Callback listener;


	public PamphletArrayAdapter(Context context, int resource, ArrayList<Tract> colors) {

		super(context, resource, colors);
		this.context = context;
		this.objects = colors;
	}

	public PamphletArrayAdapter.Callback getListener() {
		return listener;
	}

	public void setListener(PamphletArrayAdapter.Callback listener) {
		this.listener = listener;
	}

	public View getView(int pos, View inView, ViewGroup parent) {
		View v = inView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.selection_list_item, null, false);
		}
		

		final Tract object = objects.get(pos);
		
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onPamphletSelected(object);
				}
			}
		});
		
		/*
		
		ImageView image = (ImageView) v.findViewById(R.id.image);
		image.setImageDrawable(object.getCover());
		
		TextView title = (TextView) v.findViewById(R.id.title);
		title.setText(object.getTitle());
		

		TextView description = (TextView) v.findViewById(R.id.description);
		description.setText(Html.fromHtml(object.getHtmlContent().substring(0, 500)));
		*/
		return (v);
	}

	public interface Callback {
		public void onPamphletSelected(Tract f);
	}
}
