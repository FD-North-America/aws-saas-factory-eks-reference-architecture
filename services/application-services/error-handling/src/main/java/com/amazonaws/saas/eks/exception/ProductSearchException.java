package com.amazonaws.saas.eks.exception;

public class ProductSearchException extends RuntimeException {
    public ProductSearchException(String tenantId,
                                  int from,
                                  int size,
                                  String categoryId,
                                  String vendorId,
                                  String filter,
                                  String sortBy) {
        super(String.format("Error searching for Products with the following parameters: TenantId: %s," +
                        "From: %d, Size: %d, Category: %s,Vendor: %s, Filter: %s, SortBy: %s",
                tenantId, from, size, categoryId, vendorId, filter, sortBy));
    }
}
