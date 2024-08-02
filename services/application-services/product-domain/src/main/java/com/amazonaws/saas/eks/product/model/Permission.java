package com.amazonaws.saas.eks.product.model;

public class Permission {
    public static final String CATEGORY_CREATE = "department-class-category-sub-category-create";
    public static final String CATEGORY_READ = "department-class-category-sub-category-read";
    public static final String CATEGORY_UPDATE = "department-class-category-sub-category-update";
    public static final String CATEGORY_DELETE = "department-class-category-sub-category-delete";

    public static final String PRODUCT_CREATE = "inventory-maintenance-record-create";
    public static final String PRODUCT_READ = "inventory-maintenance-record-read";
    public static final String PRODUCT_UPDATE = "inventory-maintenance-record-update";
    public static final String PRODUCT_DELETE = "inventory-maintenance-record-delete";
    public static final String SERVER_PRODUCT_READ = "products/get";
    public static final String SERVER_PRODUCT_UPDATE = "products/update";
    public static final String PRODUCT_INVOICING_READ = "invoicing-read";

    public static final String VENDOR_CREATE = "inventory-vendor-items-create";
    public static final String VENDOR_READ = "inventory-vendor-items-read";
    public static final String VENDOR_UPDATE = "inventory-vendor-items-update";
    public static final String VENDOR_DELETE = "inventory-vendor-items-delete";

    public static final String VOLUME_DISCOUNT_CREATE = "volume-discounts-create";
    public static final String VOLUME_DISCOUNT_READ = "volume-discounts-read";
    public static final String VOLUME_DISCOUNT_UPDATE = "volume-discounts-update";
    public static final String VOLUME_DISCOUNT_DELETE = "volume-discounts-delete";

    private Permission() {
        throw new IllegalStateException();
    }
}
