package com.common_lib.android.vending.billing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.AdapterView.OnItemClickListener;

import com.amazon.inapp.purchasing.Receipt;
import com.common_lib.android.vending.billing.util.IabResult;
import com.common_lib.android.vending.billing.util.Purchase;
import com.common_lib.android.fragment.nested.NestedFragment;
import com.common_lib.android.ui.templates.FragmentTemplateActivity;

import java.util.List;
import java.util.Set;


public abstract class IABListActivity extends FragmentTemplateActivity implements IAPCompositeDelegate {

	IABListFragment frag;

	@SuppressLint("ValidFragment")
	@Override
	public NestedFragment getFragment() {

		this.frag = new IABListFragment( hostedIAPMarket() ) {

			@Override
			public OnItemClickListener getOnItemClickListener() {

				return IABListActivity.this.getOnItemClickListener();
			}

			@Override
			protected List<String> getSKUs() {

				return IABListActivity.this.getSKUs();
			}

			@Override
			protected String getBase64PublicKey() {

				return IABListActivity.this.getBase64PublicKey();
			}
			
			@Override
			protected IAPCompositeDelegate getIABDelegate() {
				
				return IABListActivity.this;
			}
		};

		return frag;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (!frag.handleActivityResult(requestCode,resultCode,data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	// ---------------------------------------------------+
	// Abstract and override methods
	public abstract OnItemClickListener getOnItemClickListener();

	protected abstract List<String> getSKUs();

	protected abstract String getBase64PublicKey();

	protected abstract int hostedIAPMarket();
	
	// ---------------------------------------------------+
	// IABDelegate override-able
	
	// -- google play
	public boolean shouldStorePurchase(IabResult result, Purchase info){
		
		return true;
	}
	
	@Override
	public void purchaseStored(IabResult result, Purchase info) {
		
		
	}
	
	@Override
	public void productsRestored(Set<String> productsRestored) {
	
		
	}

	// -- amazon
	public boolean shouldStorePurchase(Receipt receipt){
		
		return true;
	}
	
	public void purchaseStored(Receipt receipt){
		
	}
	
	public boolean shouldClearRevoked() {
		
		return true;
	}
	
	// ---------------------------------------------------+

}
