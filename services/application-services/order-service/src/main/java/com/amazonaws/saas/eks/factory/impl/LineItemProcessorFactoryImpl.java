package com.amazonaws.saas.eks.factory.impl;

import com.amazonaws.saas.eks.factory.LineItemProcessorFactory;
import com.amazonaws.saas.eks.processors.lineitems.LineItemProcessor;
import com.amazonaws.saas.eks.processors.lineitems.discounts.DiscountProcessor;
import com.amazonaws.saas.eks.processors.lineitems.discounts.DiscountProcessorParams;
import com.amazonaws.saas.eks.processors.lineitems.generic.GenericProcessor;
import com.amazonaws.saas.eks.processors.lineitems.generic.GenericProcessorParams;
import com.amazonaws.saas.eks.processors.lineitems.products.ProductProcessor;
import com.amazonaws.saas.eks.processors.lineitems.products.ProductProcessorParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LineItemProcessorFactoryImpl implements LineItemProcessorFactory {

    @Autowired
    private ProductProcessor productProcessor;

    @Autowired
    private DiscountProcessor discountProcessor;

    @Autowired
    private GenericProcessor genericProcessor;

    @Override
    public LineItemProcessor<ProductProcessorParams> createProductLineItemProcessor() {
        return productProcessor;
    }

    @Override
    public LineItemProcessor<DiscountProcessorParams> createDiscountLineItemProcessor() {
        return discountProcessor;
    }

    @Override
    public LineItemProcessor<GenericProcessorParams> createGenericLineItemProcessor() {
        return genericProcessor;
    }

}
