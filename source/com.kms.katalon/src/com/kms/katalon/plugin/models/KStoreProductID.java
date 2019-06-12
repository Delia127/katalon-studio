package com.kms.katalon.plugin.models;

import java.util.List;

public class KStoreProductID {
List<Long> productIds ;

public KStoreProductID(List<Long> productIds) {
    super();
    this.productIds = productIds;
}

public List<Long> getProductID() {
    return productIds;
}

public void setProductID(List<Long> productIds) {
    this.productIds = productIds;
}

}
