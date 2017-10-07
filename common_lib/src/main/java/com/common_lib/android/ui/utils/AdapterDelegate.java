package com.common_lib.android.ui.utils;

import android.view.View;

public interface AdapterDelegate<E>{
	
	public Object bindHolder(View row);
	
	public void setContent(int position, E obj, Object holder);
	
}
