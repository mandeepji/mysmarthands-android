package com.msh.common.android.dictionary.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.amazon.inapp.purchasing.Receipt;
import com.common_lib.android.vending.billing.util.IabResult;
import com.common_lib.android.vending.billing.util.Purchase;
import com.msh.common.android.dictionary.Constants;
import com.msh.common.android.dictionary.AppInstance;
import com.msh.common.android.dictionary.R;
import com.common_lib.android.vending.billing.IABListActivity;
import com.common_lib.android.vending.billing.IABListFragment;

public class UpgradeActivity extends IABListActivity implements
		OnItemClickListener {

	@Override
	public void setContentView() {

		setContentView(R.layout.upgrade_layout);
		TitleBarView titleBarView = (TitleBarView) findViewById(R.id.titleBarView);
		titleBarView.setTitle(getString(R.string.titleBarDefault));
		titleBarView.setHiddenLeftButton(true);
		titleBarView.setHiddenRightButton(true);
	}

	@Override
	public OnItemClickListener getOnItemClickListener() {

		return this;
	}

	@Override
	protected List<String> getSKUs() {

		List<String> skus = new ArrayList<String>(1);
		skus.add(Constants.getString(Constants.CONST_KEY_IAP_SKU_FULL_UNLOCK));
		return skus;
	}

	@Override
	protected String getBase64PublicKey() {

		return Constants.getString(Constants.CONST_KEY_XAPK_PUBLIC_KEY);
	}

	protected int hostedIAPMarket() {

		return (Constants.USE_AMAZON_IAP) 
				? IABListFragment.MARKET_AMAZON
				: IABListFragment.MARKET_GOOGLE_PLAY;
	}

	// --------------------------------------------------+
	// IABDelagate
	// -- google play
	@Override
	public void purchaseStored(IabResult result, Purchase info) {

		AppInstance.getCastContext().reloadDatabase();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	public void productsRestored(Set<String> productsRestored) {

		if (productsRestored.size() > 0) {
			AppInstance.getCastContext().reloadDatabase();
		}
	}

	// -- amazon
	public void purchaseStored(Receipt receipt) {

		AppInstance.getCastContext().reloadDatabase();
	}

	
	// --------------------------------------------------+
}
