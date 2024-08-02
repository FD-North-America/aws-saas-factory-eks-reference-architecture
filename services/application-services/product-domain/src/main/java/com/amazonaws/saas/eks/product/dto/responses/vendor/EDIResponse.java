package com.amazonaws.saas.eks.product.dto.responses.vendor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EDIResponse {
    private String accountNumber;
    private String host;
    private String username;
    private String password;
    private String port;
    private Boolean isActive;
    private Boolean useDashSeparator;
    private Boolean updateItemDescriptions;
    private String itemNumberPrefix;
    private String itemNumberSuffix;
    private Boolean includeRetailPricesInOrders;
    private Boolean uploadRetailPrices;
    private Boolean uploadRetailInventory;
    private Boolean retailSalesUploadDirectory;
    private String priceLevel;
    private String defaultCategoryId;
    private Boolean printPriceChanges;
    private Boolean printRounding;
    private Boolean updateRetailPrice;
}
