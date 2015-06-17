package com.filos.app;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

public class ExpandedResultItemActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filos_app_results_list_item_expanded);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.filos_blue)));
		bar.setTitle("");
		bar.setIcon(R.drawable.shadowed_icon);
		
		Bundle extras = getIntent().getExtras();
		
		Bitmap bmp = extras.getParcelable("Bitmap");
		
	}
}
