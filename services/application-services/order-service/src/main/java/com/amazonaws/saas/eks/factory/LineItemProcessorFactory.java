package com.amazonaws.saas.eks.factory;

import com.amazonaws.saas.eks.processors.lineitems.LineItemProcessor;
import com.amazonaws.saas.eks.processors.lineitems.discounts.DiscountProcessorParams;
import com.amazonaws.saas.eks.processors.lineitems.generic.GenericProcessorParams;
import com.amazonaws.saas.eks.processors.lineitems.products.ProductProcessorParams;

public interface LineItemProcessorFactory {
    LineItemProcessor<ProductProcessorParams> createProductLineItemProcessor();

    LineItemProcessor<DiscountProcessorParams> createDiscountLineItemProcessor();

    LineItemProcessor<GenericProcessorParams> createGenericLineItemProcessor();
}
