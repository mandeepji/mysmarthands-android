package com.common_lib.android.vending.billing;

import android.content.Intent;
import android.widget.BaseAdapter;

import com.common_lib.android.fragment.nested.NestedListFragment;
import com.common_lib.android.vending.billing.amazon.AmazonIABListAdapter;

import java.util.List;


public abstract class IABListFragment extends NestedListFragment {

	public static final int MARKET_GOOGLE_PLAY = 0;
	public static final int MARKET_AMAZON = 1;

	IAPListAdapter iabAdapter;
	int market = -1;

	public IABListFragment(int market) {

		this.market = market;
	}

	@Override
	public BaseAdapter getListAdapter() {

		if (iabAdapter == null) {
			switch (market) {
			case MARKET_GOOGLE_PLAY:
				iabAdapter = new IABListAdapter(this.getActivity(),
						this.getBase64PublicKey(), this.getSKUs(),
						this.getIABDelegate());
				break;
			case MARKET_AMAZON:
				// FIXME Mandeep Amazon stuff
				/*iabAdapter = new AmazonIABListAdapter(this.getActivity(),
						this.getSKUs(), this.getIABDelegate());*/
				break;
			default:
				throw new RuntimeException("Uknown Market (" + market + ") set");
			}
		}

		return iabAdapter;
	}

	public boolean handleActivityResult(int requestCode, int resultCode,
			Intent data) {

		return ((market == MARKET_GOOGLE_PLAY) && ((IABListAdapter) iabAdapter).iab
				.handleActivityResult(requestCode, resultCode, data));
	}

	// --------------------------------------------------+
	protected abstract String getBase64PublicKey();

	protected abstract List<String> getSKUs();

	protected IAPCompositeDelegate getIABDelegate(){
		
		return null;
	}

	@Override
	public void onDestroy() {

		iabAdapter.dispose();
		super.onDestroy();
	}

	// --------------------------------------------------+

}