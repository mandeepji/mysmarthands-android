package com.common_lib.android.vending.billing.amazon;

import com.common_lib.android.vending.billing.util.IabResult;
import com.common_lib.android.vending.billing.util.Purchase;

import java.util.Set;

public interface AmazonIABDelegate {

	public void productsRestored(Set<String> productsRestored);
	
	public boolean shouldStorePurchase(IabResult result, Purchase info);
	
	public void purchaseStored(IabResult result, Purchase info);
}
