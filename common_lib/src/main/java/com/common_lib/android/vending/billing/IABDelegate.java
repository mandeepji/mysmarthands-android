package com.common_lib.android.vending.billing;

import com.common_lib.android.vending.billing.util.IabResult;
import com.common_lib.android.vending.billing.util.Purchase;

import java.util.Set;

public interface IABDelegate {

	public void productsRestored(Set<String> productsRestored);
	
	public boolean shouldStorePurchase(IabResult result, Purchase info);
	
	public void purchaseStored(IabResult result, Purchase info);
}
