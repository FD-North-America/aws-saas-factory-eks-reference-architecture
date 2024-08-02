package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.exception.OrderNotFoundException;
import com.amazonaws.saas.eks.order.dto.requests.delivery.CreateDeliveryRequest;
import com.amazonaws.saas.eks.order.dto.requests.delivery.UpdateDeliveryRequest;
import com.amazonaws.saas.eks.order.dto.responses.delivery.DeliveryResponse;
import com.amazonaws.saas.eks.order.mapper.DeliveryMapper;
import com.amazonaws.saas.eks.order.model.Delivery;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.order.model.enums.EntityType;
import com.amazonaws.saas.eks.processors.tax.TaxProcessor;
import com.amazonaws.saas.eks.repository.DeliveryRepository;
import com.amazonaws.saas.eks.repository.OrderRepository;
import com.amazonaws.saas.eks.service.DeliveryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class DeliveryServiceImpl implements DeliveryService {
    private static final Logger logger = LogManager.getLogger(DeliveryService.class);

    private final DeliveryRepository repository;

    private final OrderRepository orderRepository;

    private final TaxProcessor taxProcessor;

    public DeliveryServiceImpl(DeliveryRepository repository,
                               OrderRepository orderRepository,
                               TaxProcessor taxProcessor) {
        this.repository = repository;
        this.orderRepository = orderRepository;
        this.taxProcessor = taxProcessor;
    }

    /**
     * Adds a delivery to the specified Order
     *
     * @param tenantId String
     * @param request  {@link CreateDeliveryRequest}
     * @return {@link DeliveryResponse}
     */
    @Override
    public DeliveryResponse create(String tenantId, CreateDeliveryRequest request) {
        Delivery delivery = DeliveryMapper.INSTANCE.createDeliveryRequestToDelivery(request);
        Delivery updatedDelivery = repository.save(tenantId, delivery);

        Order order = orderRepository.getOrderById(updatedDelivery.getOrderId(), tenantId);
        if (order == null) {
            throw new OrderNotFoundException(updatedDelivery.getOrderId());
        }
        order.setDeliveryId(updatedDelivery.getId());
        orderRepository.save(order, tenantId);
        return DeliveryMapper.INSTANCE.deliveryToDeliveryResponse(updatedDelivery);
    }

    /**
     * Returns the delivery associated with the order
     *
     * @param tenantId   String
     * @param deliveryId String
     * @return {@link DeliveryResponse}
     */
    @Override
    public DeliveryResponse get(String tenantId, String deliveryId) {
        Delivery delivery = repository.get(tenantId, deliveryId);
        if (delivery == null) {
            throw new EntityNotFoundException(EntityType.DELIVERIES.getLabel());
        }
        return DeliveryMapper.INSTANCE.deliveryToDeliveryResponse(delivery);
    }

    /**
     * Updates the delivery associated with the order
     *
     * @param tenantId   String
     * @param deliveryId String
     * @param request    {@link UpdateDeliveryRequest}
     * @return {@link DeliveryResponse}
     */
    @Override
    public DeliveryResponse update(String tenantId, String deliveryId, UpdateDeliveryRequest request) {
        Delivery model = repository.get(tenantId, deliveryId);
        if (model == null) {
            throw new EntityNotFoundException(EntityType.DELIVERIES.getLabel());
        }
        Delivery delivery = DeliveryMapper.INSTANCE.updateDeliveryRequestToDelivery(request);
        delivery.setId(deliveryId);
        delivery.setOrderId(model.getOrderId());
        Delivery updatedDelivery = repository.save(tenantId, delivery);

        // Process tax calculation for the updated delivery
        taxProcessor.handleAddressChange(tenantId, model.getOrderId(), updatedDelivery);

        return DeliveryMapper.INSTANCE.deliveryToDeliveryResponse(updatedDelivery);
    }

    /**
     * Deletes a delivery associated with the order
     *
     * @param tenantId   String
     * @param deliveryId String
     */
    @Override
    public void delete(String tenantId, String deliveryId) {
        Delivery delivery = repository.get(tenantId, deliveryId);
        repository.delete(tenantId, deliveryId);
        Order order = orderRepository.getOrderById(delivery.getOrderId(), tenantId);
        order.setDeliveryId("");
        orderRepository.save(order, tenantId);
    }
}
