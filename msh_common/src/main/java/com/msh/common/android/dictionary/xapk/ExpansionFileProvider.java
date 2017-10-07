package com.msh.common.android.dictionary.xapk;

import com.android.vending.expansion.zipfile.APEZProvider;
import com.msh.common.android.dictionary.Constants;

public class ExpansionFileProvider extends APEZProvider {

	@Override
	public String getAuthority() {
		return Constants.getString(Constants.CONST_KEY_XAPK_PROVIDER_AUTHORITY);
	}
}
