package com.common_lib.android.vending.billing;

import android.app.Activity;
import android.content.Context;

import com.common_lib.android.vending.billing.util.IabHelper;
import com.common_lib.android.vending.billing.util.IabHelper.OnIabPurchaseFinishedListener;
import com.common_lib.android.vending.billing.util.IabHelper.OnIabSetupFinishedListener;
import com.common_lib.android.vending.billing.util.IabHelper.QueryInventoryFinishedListener;
import com.common_lib.android.vending.billing.util.IabResult;
import com.common_lib.android.vending.billing.util.Inventory;
import com.common_lib.android.vending.billing.util.Purchase;
import com.common_lib.android.vending.billing.util.SkuDetails;
import com.common_lib.android.storage.PreferencesHelper;
import com.common_lib.random.RandomGen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BillingHelper extends IabHelper implements
		OnIabSetupFinishedListener, OnIabPurchaseFinishedListener,
		QueryInventoryFinishedListener {

	public final String base64PublicKey;
	public Activity activity;
	private IabHelper iab;
	private RandomGen requestCodeGen;

	public BillingHelper(Activity activity, String base64PublicKey) {

		super(activity, base64PublicKey);
		this.base64PublicKey = base64PublicKey;
		this.activity = activity;
		this.startSetup(this);
	}

	// ---------------------------------------------------+
	// OnIabSetupFinishedListener
	@Override
	public void onIabSetupFinished(IabResult result) {

		if (result.isSuccess()) {
			requestCodeGen = new RandomGen();
		}
	}

	// ---------------------------------------------------+
	public String purchaseItem(String sku) {

		int requestCode = requestCodeGen.nextInt(1000, 5000);
		iab.launchPurchaseFlow(activity, sku, requestCode, this);

		return String.valueOf(requestCode);
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase info) {

		if (result.isSuccess()) {

		}
	}

	// ---------------------------------------------------+
	public void getPurchases() {

		this.queryInventoryAsync(this);
	}

	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inv) {

		if (result.isSuccess()) {

		}
	}

	// ---------------------------------------------------+
	// restore purchases - supports only non-consumables, 
	// should use RestoreDelegate for other consumables
	public static void restorePurchases(final Context context,
			String base64PublicKey,
			final String sku,
			final RestoreDelegate delegate) {
	
		List<String> skus = new ArrayList<String>(1);
		skus.add(sku);
		restorePurchases(context, base64PublicKey, skus, delegate);
	}
	
	public static void restorePurchases(final Context context,
										String base64PublicKey,
										final List<String> skus,
										final RestoreDelegate delegate) {

		final IabHelper iab = new IabHelper(context, base64PublicKey);
		// inventory query
		final QueryInventoryFinishedListener query = new QueryInventoryFinishedListener() {
			@Override
			public void onQueryInventoryFinished(IabResult result, Inventory inv) {
				if(result.isSuccess()){
					Map<String, Object> skuPurchaseStates = 
							new HashMap<String, Object>(skus.size());
					boolean purchased;
					for (String sku : skus) {
						purchased = inv.hasPurchase(sku);
						if(delegate !=null && purchased){
							delegate.skuISPurchased(inv.getSkuDetails(sku));
						}
						skuPurchaseStates.put(sku,purchased);
					}
					if(delegate ==null || delegate.shouldStorePurchasesInPrefs()){
						PreferencesHelper.set(context,skuPurchaseStates);
					}
					if(delegate !=null){
						delegate.restoreFinished();
					}
				}
			}
		};
		
		// iab setup
		OnIabSetupFinishedListener setup = new OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				if(result.isSuccess()){
					iab.queryInventoryAsync(true, skus,query);
				}
			}
		};

		iab.startSetup(setup);
	}
	
	public static Set<String> restorePurchases(Context context,List<String> skus,Inventory inv){
		
		boolean purchased;
		Map<String, Object> skuPurchaseStates = 
				new HashMap<String, Object>(skus.size());
		Set<String> productsRestored = new HashSet<String>(skus);
		for (String sku : skus) {
			purchased = inv.hasPurchase(sku);
			skuPurchaseStates.put(sku,purchased);
			if(!PreferencesHelper.contains(context, sku) && purchased){
				productsRestored.add(sku);
			}
		}
		PreferencesHelper.set(context,skuPurchaseStates);
		
		return productsRestored;
	}
	
	public interface RestoreDelegate{
		
		public boolean shouldStorePurchasesInPrefs();
		public void skuISPurchased(SkuDetails details);
		public void restoreFinished();
	}

	// ---------------------------------------------------+

}
