package com.common_lib.android.vending.billing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.common_lib.R;
import com.common_lib.android.vending.billing.util.IabHelper;
import com.common_lib.android.vending.billing.util.IabHelper.OnIabPurchaseFinishedListener;
import com.common_lib.android.vending.billing.util.IabHelper.OnIabSetupFinishedListener;
import com.common_lib.android.vending.billing.util.IabHelper.QueryInventoryFinishedListener;
import com.common_lib.android.vending.billing.util.IabResult;
import com.common_lib.android.vending.billing.util.Inventory;
import com.common_lib.android.vending.billing.util.Purchase;
import com.common_lib.android.vending.billing.util.SkuDetails;
import com.common_lib.android.storage.PreferencesHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IABListAdapter extends IAPListAdapter implements
		QueryInventoryFinishedListener, OnIabSetupFinishedListener,
		OnClickListener, OnIabPurchaseFinishedListener {

	public static final int IAB_STATE_WAITING = 0;
	public static final int IAB_STATE_LOADED = 1;
	public static final int IAB_STATE_ERROR = 2;

	Context context;
	List<String> skus;
	IabHelper iab;
	Inventory inventory;
	// because inventory can remove locally but not add!!
	Set<String> currentPurchasedSkus = new HashSet<String>();
	IabResult result;
	int iabStatus;

	IABDelegate iabDelegate;

	public IABListAdapter(Context context, String base64PublicKey,
			List<String> skus) {

		this.context = context;
		this.skus = skus;
		this.iab = new IabHelper(context, base64PublicKey);
		iabStatus = IAB_STATE_WAITING;
		iab.startSetup(this);
	}

	public IABListAdapter(Context context, String base64PublicKey,
			List<String> skus, IABDelegate iabDelegate) {

		this(context, base64PublicKey, skus);
		this.iabDelegate = iabDelegate;
	}

	public void dispose(){
		
		iab.dispose();
	}
	
	// --------------------------------------------------------+
	// Base Adapter Methods
	@Override
	public int getCount() {

		// might have ot validate skus to make sure they are in the inventory.
		// (why no count on inventory object)
		if (iabStatus == IAB_STATE_LOADED) {
			return skus.size();
		}

		return 1; // error or loading cell
	}

	@Override
	public Object getItem(int position) {

		if (iabStatus == IAB_STATE_LOADED) {
			return skus.get(position);
		}
		return null;

	}

	@Override
	public long getItemId(int position) {

		if (iabStatus == IAB_STATE_LOADED) {
			return position;
		}
		return -1;
	}

	@Override
	public View getView(int position, View rowView, ViewGroup parent) {

		View ret = null;
		switch (iabStatus) {
		case IAB_STATE_ERROR:
			ret = this.getErrorView(position, rowView, parent);
			break;
		case IAB_STATE_WAITING:
			ret = this.getWatingView(position, rowView, parent);
			break;
		default:
			ret = this.getRowView(position, rowView, parent);
			break;
		}

		return ret;
	}

	protected View getErrorView(int position, View rowView, ViewGroup parent) {

		if (rowView == null) {
			rowView = this.initializeRowView(position, parent);
		}

		Holder holder = (Holder) rowView.getTag();
		holder.purchasBtn.setText("Retry");
		holder.productNameTV.setText(result.getMessage());

		return rowView;
	}

	protected View getWatingView(int position, View rowView, ViewGroup parent) {

		if (rowView == null) {
			rowView = this.initializeRowView(position, parent);
		}

		Holder holder = (Holder) rowView.getTag();
		holder.purchasBtn.setVisibility(View.GONE);
		holder.productNameTV.setText("Loading... Please Wait");

		return rowView;
	}

	protected View getRowView(int position, View rowView, ViewGroup parent) {

		if (rowView == null) {
			rowView = this.initializeRowView(position, parent);
		}

		Holder holder = (Holder) rowView.getTag();
		holder.purchasBtn.setVisibility(View.VISIBLE);
		holder.purchasBtn.setTag(position);

		SkuDetails details = inventory.getSkuDetails(skus.get(position));
		if (inventory.hasPurchase(details.getSku())
				|| currentPurchasedSkus.contains(details.getSku())) {
			holder.purchasBtn.setText("Purchased");
			holder.purchasBtn.setEnabled(false);
		} else {
			holder.purchasBtn.setText("Buy: " + details.getPrice());
			holder.purchasBtn.setEnabled(true);
		}
		holder.productNameTV.setText(details.getTitle());

		return rowView;
	}

	private View initializeRowView(int position, ViewGroup parent) {

		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		View rowView = inflater.inflate(this.getRowContentView(), null);
		Holder holder = new Holder();
		holder.productNameTV = (TextView) rowView
				.findViewById(R.id.productNameTV);
		holder.purchasBtn = (Button) rowView.findViewById(R.id.purchaseBtn);
		holder.purchasBtn.setOnClickListener(this);
		rowView.setTag(holder);

		return rowView;
	}

	private int getRowContentView() {

		return R.layout.iab_list_item;
	}

	// --------------------------------------------------------+
	// IAB Methods
	@Override
	public void onIabSetupFinished(IabResult result) {

		if (result.isSuccess()) {
			iab.queryInventoryAsync(true, skus, this);
		} else {
			this.result = result;
			iabStatus = IAB_STATE_ERROR;
			this.notifyDataSetInvalidated();
		}
	}

	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inv) {

		if (result.isSuccess()) {
			inventory = inv;
			iabStatus = IAB_STATE_LOADED;
			Set<String> restored = BillingHelper.restorePurchases(context, skus, inv);
			if(iabDelegate !=null){
				iabDelegate.productsRestored(restored);
			}
		}
		else {
			this.result = result;
			iabStatus = IAB_STATE_ERROR;
		}

		// reload table
		this.notifyDataSetInvalidated();
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase info) {

		if (result.isSuccess()) {
			if (iabDelegate != null
					&& iabDelegate.shouldStorePurchase(result, info)) {
				PreferencesHelper.set(context, info.getSku(), true);
				iabDelegate.purchaseStored(result, info);
			}
			currentPurchasedSkus.add(info.getSku());
			// reload table
			this.notifyDataSetInvalidated();
		} else {
			Log.d("RBI", result.getMessage());
		}

	}

	// purchase hit
	@Override
	public void onClick(View v) {

		int index = (Integer) v.getTag();

		iab.launchPurchaseFlow((Activity) context, 
				skus.get(index), 
				index, 
				this);
	}

	// --------------------------------------------------------+
	// UI customization

	// --------------------------------------------------------+
	// Holder
	private class Holder {

		TextView productNameTV;
		Button purchasBtn;
	}

	// --------------------------------------------------------+

}
