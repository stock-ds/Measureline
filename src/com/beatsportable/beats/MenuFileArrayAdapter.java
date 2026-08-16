package com.beatsportable.beats;

import java.util.ArrayList;

import android.content.Context;
import android.view.*;
import android.widget.*;

public class MenuFileArrayAdapter extends ArrayAdapter<MenuFileItem>{

	private Context c;
	private int id;
	private ArrayList<MenuFileItem> items;
	
	public MenuFileArrayAdapter(Context context, int textViewResourceId, ArrayList<MenuFileItem> items) {
		super(context, textViewResourceId, items);
		this.c = context;
		this.id = textViewResourceId;
		this.items = items;
	}
	
	public MenuFileItem getItem (int i) {
		return items.get(i); 
	} 
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			 LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 v = vi.inflate(id, null);			 
		}
		final MenuFileItem i = items.get(position);
		if (i != null) {
			ImageView iv = (ImageView) v.findViewById(R.id.iconview);
			SongSelectInfo info = null;
			if (i.getFile() != null && (i.isDirectory() || Tools.isStepfile(i.getPath()))) {
				info = SongSelectInfo.forItem(i.getFile(), i.isDirectory());
			}
			if (iv != null) {
				String s = i.getName();
				android.graphics.Bitmap banner = (info != null) ? info.getBannerBitmap() : null;
				if (banner != null) {
					iv.setImageBitmap(banner);
				} else if (i.getFile() == null) {
					iv.setImageResource(R.drawable.icon_folder_parent);
				} else if (i.isDirectory()) {
					if (Tools.checkStepfileDir(i.getFile()) != null) {
						iv.setImageResource(R.drawable.icon_small);
					} else {
						iv.setImageResource(R.drawable.icon_folder);
					}
				} else if (Tools.isStepfile(i.getPath())) {
					if (Tools.isSMFile(s)) {
						iv.setImageResource(R.drawable.icon_sm);
					} else if (Tools.isDWIFile(s)) {
						iv.setImageResource(R.drawable.icon_dwi);
					} else if (Tools.isOSUFile(s)){
						iv.setImageResource(R.drawable.icon_osu);
					} else {
						iv.setImageResource(R.drawable.icon_warning);
					}
				} else if (Tools.isStepfilePack(s)) {
					iv.setImageResource(R.drawable.icon_zip);
				} else if (Tools.isLink(s)) {
					iv.setImageResource(R.drawable.icon_url);
				} else if (Tools.isText(s)) {
					iv.setImageResource(R.drawable.icon_text);
				} else {
					iv.setImageResource(R.drawable.icon_warning);
				}
			}
			TextView tv = (TextView) v.findViewById(R.id.textview);
			if (tv != null) {
				tv.setText(i.getName());	
			}
			TextView stats = (TextView) v.findViewById(R.id.statsview);
			if (stats != null) {
				if (info != null && info.statsText != null && info.statsText.length() > 0) {
					stats.setText(info.statsText);
					stats.setVisibility(View.VISIBLE);
				} else {
					stats.setText("");
					stats.setVisibility(View.GONE);
				}
			}
		}
		return v;
	}

}
