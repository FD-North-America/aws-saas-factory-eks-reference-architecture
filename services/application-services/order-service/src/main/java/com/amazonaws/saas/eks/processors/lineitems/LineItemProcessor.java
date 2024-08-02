package com.amazonaws.saas.eks.processors.lineitems;

import com.amazonaws.saas.eks.order.dto.requests.UpdateSingleLineItemRequest;
import com.amazonaws.saas.eks.order.model.LineItem;
import com.amazonaws.saas.eks.product.dto.responses.product.PricingResponse;

/**
 * Interface to interact with Order Line Items
 * @param <T>
 */
public interface LineItemProcessor<T> {
    /**
     * Takes the given parameters and process any calculations needed to create a LineItem object
     * @param parameters needed to process the LineItem type {@link com.amazonaws.saas.eks.order.model.enums.LineItemType}
     * @return new {@link LineItem}
     */
    LineItem process(T parameters);

    /**
     * Updates the passed in LineItem object and returns it updated with the request and pricing updates
     * @param lineItem to update
     * @param request holds the new attributes
     * @param pricingResponse holds the pricing details for the new values
     * @return updated {@link LineItem}
     */
    LineItem update(LineItem lineItem, UpdateSingleLineItemRequest request, PricingResponse pricingResponse);
}
