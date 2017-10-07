package com.common_lib.android.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.common_lib.R;
import com.common_lib.android.ui.utils.ImageAdapter;

public abstract class GridViewActivity extends Activity implements OnItemClickListener {

	GridView gridView;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView();
		gridView = (GridView) findViewById(R.id.gridView1);
		this.gridAdapterSetup();
	}
	
	private void gridAdapterSetup(){
		
		int[] imageIDs = this.getImageIDs();
		ImageAdapter a = new ImageAdapter(this,imageIDs);
		this.configureAdapter(a);
		gridView.setAdapter(a);
		gridView.setOnItemClickListener(this);
	}
	
	protected int getDefaultLayoutResID(){
		
		return R.layout.image_grid;
	}
	
	public void refreshView(){
		
		gridAdapterSetup();
	}
	
	//-----------------------------------------------+
	// Abstract and override methods
	protected abstract int[] getImageIDs();
	
	protected void setContentView(){
		
		setContentView(this.getDefaultLayoutResID());
	}
	
	protected void configureAdapter(ImageAdapter adapter){
		
		
	}
	
	//-----------------------------------------------+
	
}