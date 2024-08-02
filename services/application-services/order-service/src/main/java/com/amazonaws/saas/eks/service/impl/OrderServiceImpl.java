package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.cashdrawer.dto.responses.CashDrawerResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.ListCashDrawersResponse;
import com.amazonaws.saas.eks.clients.cashdrawer.CashDrawerServiceClient;
import com.amazonaws.saas.eks.clients.payments.PaymentServiceClient;
import com.amazonaws.saas.eks.clients.product.ProductServiceClient;
import com.amazonaws.saas.eks.clients.settings.SettingsServiceClient;
import com.amazonaws.saas.eks.exception.*;
import com.amazonaws.saas.eks.mapper.ServiceMapper;
import com.amazonaws.saas.eks.order.dto.requests.*;
import com.amazonaws.saas.eks.order.dto.requests.delivery.CreateDeliveryRequest;
import com.amazonaws.saas.eks.order.dto.requests.itemsinfo.ItemsInfoRequest;
import com.amazonaws.saas.eks.order.dto.requests.reasoncodes.ReasonCodeItemRequest;
import com.amazonaws.saas.eks.order.dto.requests.tax.CreateTaxRequest;
import com.amazonaws.saas.eks.order.dto.responses.*;
import com.amazonaws.saas.eks.order.dto.responses.delivery.DeliveryResponse;
import com.amazonaws.saas.eks.order.dto.responses.itemsinfo.ItemInfo;
import com.amazonaws.saas.eks.order.dto.responses.itemsinfo.ItemsInfoResponse;
import com.amazonaws.saas.eks.order.dto.responses.itemsinfo.ProductOrderDto;
import com.amazonaws.saas.eks.order.dto.responses.itemsinfo.ProductOrdersDetails;
import com.amazonaws.saas.eks.order.dto.responses.tax.TaxResponse;
import com.amazonaws.saas.eks.order.mapper.ChargeCodeMapper;
import com.amazonaws.saas.eks.order.mapper.DeliveryMapper;
import com.amazonaws.saas.eks.order.mapper.ItemsInfoMapper;
import com.amazonaws.saas.eks.order.mapper.OrderMapper;
import com.amazonaws.saas.eks.order.model.*;
import com.amazonaws.saas.eks.order.model.enums.*;
import com.amazonaws.saas.eks.order.model.search.OrderSearchResponse;
import com.amazonaws.saas.eks.payment.dto.responses.ListOrderPaymentsResponse;
import com.amazonaws.saas.eks.payment.dto.responses.OrderPaymentResponse;
import com.amazonaws.saas.eks.processors.payments.PaymentProcessor;
import com.amazonaws.saas.eks.processors.tax.TaxProcessor;
import com.amazonaws.saas.eks.product.dto.responses.product.ProductResponse;
import com.amazonaws.saas.eks.product.dto.responses.uom.UOMResponse;
import com.amazonaws.saas.eks.product.model.Product;
import com.amazonaws.saas.eks.repository.*;
import com.amazonaws.saas.eks.service.DeliveryService;
import com.amazonaws.saas.eks.service.LineItemService;
import com.amazonaws.saas.eks.service.OrderService;
import com.amazonaws.saas.eks.service.TransactionService;
import com.amazonaws.saas.eks.service.TaxService;
import com.amazonaws.saas.eks.settings.dto.responses.PurchasingSettingsResponse;
import com.amazonaws.saas.eks.settings.dto.responses.ReasonCodesSettingsResponse;
import com.amazonaws.saas.eks.settings.model.enums.SequenceNumberType;
import com.amazonaws.saas.eks.utils.AsyncUtils;
import com.amazonaws.saas.eks.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import feign.FeignException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.amazonaws.saas.eks.utils.Utils.roundValue;

@Service
public class OrderServiceImpl implements OrderService {
	private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

	private static final int DEFAULT_SEARCH_START = 0;
	private static final int DEFAULT_SEARCH_SIZE = 10;

	private final OrderRepository orderRepository;

	private final PaidOutCodeRepository paidOutCodeRepository;

    private final PaymentServiceClient paymentServiceClient;

	private final CashDrawerServiceClient cashDrawerServiceClient;

	private final AsyncUtils asyncUtils;

	private final PaymentProcessor paymentProcessor;

	private final SettingsServiceClient settingsServiceClient;
	private final CategorySalesRepository categorySalesRepository;
	private final ProductOrderRepository productOrderRepository;
	private final LineItemService lineItemService;
	private final ProductServiceClient productServiceClient;
	private final TransactionService transactionService;
	private final TaxProcessor taxProcessor;
	private final TaxService taxService;
	private final DeliveryService deliveryService;

	public OrderServiceImpl(OrderRepository orderRepository,
							PaidOutCodeRepository paidOutCodeRepository,
							PaymentServiceClient paymentServiceClient,
							CashDrawerServiceClient cashDrawerServiceClient,
							ProductServiceClient productServiceClient,
							AsyncUtils asyncUtils,
							PaymentProcessor paymentProcessor,
							SettingsServiceClient settingsServiceClient,
							CategorySalesRepository categorySalesRepository,
							ProductOrderRepository productOrderRepository,
							LineItemService lineItemService,
							TransactionService transactionService,
							TaxProcessor taxProcessor,
							TaxService taxService,
							DeliveryService deliveryService) {
		this.orderRepository = orderRepository;
		this.paidOutCodeRepository = paidOutCodeRepository;
        this.paymentServiceClient = paymentServiceClient;
		this.cashDrawerServiceClient = cashDrawerServiceClient;
		this.productServiceClient = productServiceClient;
		this.asyncUtils = asyncUtils;
        this.paymentProcessor = paymentProcessor;
		this.settingsServiceClient = settingsServiceClient;
		this.categorySalesRepository = categorySalesRepository;
		this.productOrderRepository = productOrderRepository;
        this.lineItemService = lineItemService;
		this.transactionService = transactionService;
		this.taxProcessor = taxProcessor;
		this.taxService = taxService;
		this.deliveryService = deliveryService;
    }

	/**
	 * @inheritDoc
	 */
	@Override
	public OrderResponse create(CreateOrderRequest request, String tenantId, String username) {
		Order order = new Order();
		order.setStatus(OrderStatus.PENDING.toString());

		// Setting Cash Drawer details
		CashDrawerResponse cashDrawerResponse = getCashDrawerForUser(tenantId, username);
		order.setCashDrawerId(cashDrawerResponse.getId());
		order.setSalesRep(username);

		String number = settingsServiceClient.getPOSNextSequence(tenantId, SequenceNumberType.ORDER.toString()).getBody();
		order.setNumber(number);

		// Setting Line Items
		List<LineItem> lineItems = lineItemService.createLineItems(request.getLineItems(), order, cashDrawerResponse, tenantId);
		order.setLineItems(lineItems);

		// Saving Order to generate Id
		order = orderRepository.save(order, tenantId);

		// Setting Paid Out Codes
		List<PaidOutCode> paidOutCodes = createPaidOutCodes(request, tenantId, cashDrawerResponse, order.getId());
		order.setPaidOutCodeItems(paidOutCodes.stream()
				.map(OrderMapper.INSTANCE::paidOutCodeToPaidOutCodeItem)
				.collect(Collectors.toList()));
		if (!paidOutCodes.isEmpty()) {
			addTransactionPerPaidOutCode(paidOutCodes, order);
		}

		// Setting linkedOrderId
		if (request.getLinkedOrderId() != null) {
			order.setLinkedOrderId(validateLinkedOrderId(request.getLinkedOrderId(), tenantId));
		}

		// Setting Reason Codes
		if (!request.getReasonCodes().isEmpty()) {
			order.setReasonCodes(getReasonCodes(request.getReasonCodes(), tenantId, order));
		}

		// Saving tax details
		initializeTaxAndDeliveryInfo(tenantId, order);

		recalculateOrder(order, tenantId);
		order = orderRepository.save(order, tenantId);
		return buildOrderResponse(order);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void delete(String orderId, String tenantId) {
		Order order = getOrderById(orderId, tenantId);
		deleteProductOrders(tenantId, order);

		orderRepository.delete(orderId, tenantId);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public OrderResponse update(String orderId, String tenantId, UpdateOrderRequest request) {
		Order order = OrderMapper.INSTANCE.updateOrderRequestToOrder(request);
		order.setId(orderId);
		Order updatedOrder = orderRepository.update(order, tenantId);
		return OrderMapper.INSTANCE.orderToOrderResponse(updatedOrder);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public OrderResponse get(String orderId, String tenantId) {
		Order order = getOrderById(orderId, tenantId);
		return buildOrderResponse(order);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public OrderResponse patch(String orderId, JsonPatch patch, String tenantId) {
		Order order = getOrderById(orderId, tenantId);

		Order patchedOrder;
		try {
			patchedOrder = applyPatchToOrder(patch, order);
		} catch (JsonProcessingException | JsonPatchException e) {
			throw new OrderException(String.format("Failed to patch order %s", orderId));
		}

		Order updatedOrder = orderRepository.update(patchedOrder, tenantId);
		return buildOrderResponse(updatedOrder);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ListOrdersResponse search(OrderSearchRequest request, String tenantId) {
		int from = request.getFrom() == null ? DEFAULT_SEARCH_START : request.getFrom();
		int size = request.getSize() == null ? DEFAULT_SEARCH_SIZE : request.getSize();
		OrderSearchResponse searchResponse = orderRepository.search(tenantId, from, size,
				request.getFilter(), OrderType.valueOfLabel(request.getType()), request.getSortBy());
		return OrderMapper.INSTANCE.orderSearchResponseToListOrdersResponse(searchResponse);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public OrderResponse finalize(String orderId, String tenantId) {
		Order order = getOrderById(orderId, tenantId);

		String previousOrderType = order.getType();
		order.setType(OrderType.INVOICE.toString());
		order.setStatus(OrderStatus.PAID.toString());
		updateOrderAnalytics(order);
		Order updatedOrder = orderRepository.update(order, tenantId);

		// If the order was a sale, the update already occurred
		if (previousOrderType == null || !previousOrderType.equals(OrderType.SALE.toString())) {
			asyncUtils.updateProductCounts(tenantId, updatedOrder.getLineItems());
		}

		saveProductOrders(tenantId, order);
		saveCategorySales(tenantId, order);
		return buildOrderResponse(updatedOrder);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public OrderResponse saveType(UpdateOrderTypeRequest request, String orderId, String tenantId) {
		String type = request.getType();
		if (type.equals(OrderType.INVOICE.toString())) {
			throw new OrderException("Cannot change order type to Invoice. Must be finalized.");
		}

		Order order = getOrderById(orderId, tenantId);
		order.setType(type);
		Date expirationDate = request.getExpirationDate();
		if (expirationDate != null && type.equals(OrderType.QUOTE.toString())) {
			order.setExpirationDate(expirationDate);
		}
		if (type.equals(OrderType.SALE.toString())) {
			order.setTendered(order.getCashPaymentAmount().add(order.getCreditPaymentAmount()));
		}
		Order updatedOrder = orderRepository.update(order, tenantId);

		if (updatedOrder.getType().equals(OrderType.SALE.toString())) {
			// Need to commit the inventory count for items in a Sale
			asyncUtils.updateProductCounts(tenantId, updatedOrder.getLineItems());
			saveProductOrders(tenantId, order);
		}
		return buildOrderResponse(updatedOrder);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public OrderResponse importLineItems(String orderId, String tenantId, ImportLineItemsRequest request) {
		Order order = getOrderById(orderId, tenantId);

		Order orderRequest = getOrderById(request.getOrderId(), tenantId);

		if (Boolean.TRUE.equals(request.getUseQuotePricing())) {
			order.setLineItems(orderRequest.getLineItems());
			order.setTotal(orderRequest.getTotal());
			order.setTaxTotal(orderRequest.getTaxTotal());
			order.setSubTotal(orderRequest.getSubTotal());
			order.setTaxableSubTotal(orderRequest.getTaxableSubTotal());
			order.setNonTaxableSubTotal(orderRequest.getNonTaxableSubTotal());
			order.setDiscountTotal(orderRequest.getDiscountTotal());
		} else {
			List<LineItem> lineItems = lineItemService.getLineItemsToImport(request, orderRequest, order);
			CashDrawerResponse cashDrawerResponse = cashDrawerServiceClient.get(tenantId, order.getCashDrawerId())
					.getBody();
			List<LineItemRequest> lineItemRequests = OrderMapper.INSTANCE.lineItemsToLineItemRequest(lineItems);
			List<LineItem> updatedLineItems = lineItemService.createLineItems(lineItemRequests, order, cashDrawerResponse, tenantId);
			order.setLineItems(updatedLineItems);
			recalculateOrder(order, tenantId);
		}

		Order updatedOrder = orderRepository.save(order, tenantId);
		return buildOrderResponse(updatedOrder);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public OrderResponse updateLineItems(UpdateLineItemsRequest request, String tenantId, String orderId) {
		Order order = getOrderById(orderId, tenantId);

		if (!order.getStatus().equals(OrderStatus.PENDING.toString())) {
			throw new OrderUpdateStatusInvalidException(orderId);
		}

		if (request.getLineItems().isEmpty()) {
			order.setLineItems(new ArrayList<>());
			clearTotals(order);
			order.setHasReturns(false);
		} else {
			List<LineItem> updatedLineItems = lineItemService.updateLineItems(request, order, tenantId);
			order.setLineItems(updatedLineItems);
			recalculateOrder(order, tenantId);
		}
		Order model = orderRepository.save(order, tenantId);

		return buildOrderResponse(model);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public OrderResponse updateSingleLineItem(UpdateSingleLineItemRequest request,
											  String tenantId,
											  String orderId,
											  String lineItemId) {
		Order order = getOrderById(orderId, tenantId);

		if (!order.getStatus().equals(OrderStatus.PENDING.toString())) {
			throw new OrderUpdateStatusInvalidException(orderId);
		}

		// Finding Line Item in list
		int index = 0;
		LineItem currentItem = null;
		for (LineItem l : order.getLineItems()) {
			if (l.getId().equals(lineItemId)) {
				currentItem = l;
				break;
			}
			index++;
		}
		if (currentItem == null) {
			throw new LineItemNotFoundException(tenantId, orderId, lineItemId);
		}

		LineItem updatedLineItem = lineItemService.update(request, currentItem, tenantId);

		// Adding updated Line Item back to order and recalculating totals
		order.getLineItems().set(index, updatedLineItem);

		Order updatedOrder = recalculateAndSaveOrder(order, tenantId);
		return buildOrderResponse(updatedOrder);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ResponseEntity<OrderResponse> addTransaction(String orderId, String tenantId, TransactionRequest request) {
		Order order = getOrderById(orderId, tenantId);

		if (request.getType().equals(PaymentType.CARD.toString())) {
			paymentProcessor.process(tenantId, order.getNumber(), request.getAmount());
			order.setStatus(OrderStatus.PAYMENT_PENDING.toString());
			Order updatedOrder = orderRepository.update(order, tenantId);
			return ResponseEntity.accepted().body(OrderMapper.INSTANCE.orderToOrderResponse(updatedOrder));
		} else if (request.getType().equals(PaymentType.CASH.toString())) {
			transactionService.addCashTransactionToOrder(order, request.getAmount(), false);
		}

		Order updatedOrder = recalculateAndSaveOrder(order, tenantId);
		return ResponseEntity.ok().body(buildOrderResponse(updatedOrder));
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public OrderResponse deleteTransaction(String orderId, String transactionId, String tenantId) {
		Order order = getOrderById(orderId, tenantId);
		transactionService.deleteTransactionFromOrder(order, transactionId);
		Order updatedOrder = recalculateAndSaveOrder(order, tenantId);
		logger.info("Transaction deleted successfully from Order");
		return buildOrderResponse(updatedOrder);
	}

	private Order recalculateAndSaveOrder(Order order, String tenantId) {
		recalculateOrder(order, tenantId);
        return orderRepository.save(order, tenantId);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public OrderResponse getOrderWithCardTransactions(String orderId, String tenantId) {
		Order order = getOrderById(orderId, tenantId);

		List<OrderPaymentResponse> payments;
		try {
			ListOrderPaymentsResponse orderPaymentsResponse = paymentServiceClient.getOrderPayments(tenantId, order.getNumber());
			payments = orderPaymentsResponse.getPayments();
		} catch (FeignException fe) {
			// No payments yet, still pending
			if (fe.status() == HttpStatus.NOT_FOUND.value()) {
				return buildOrderResponse(order);
			} else {
				throw new OrderPaymentFailure("Failed to retrieve payments for Order. Status code: " + fe.status());
			}
		}

		Map<String, Transaction> transactionMap = order
				.getTransactions()
				.stream()
				.filter(t -> t.getPaymentType().equals(PaymentType.CARD.toString()))
				.collect(Collectors.toMap(Transaction::getRetRef, t -> t));

		int successfulTransactionCount = 0;
		int failedTransactionCount = 0;
		for (OrderPaymentResponse r : payments) {
			if (transactionService.isFailedTransaction(r)) {
				failedTransactionCount++;
			} else {
				successfulTransactionCount++;
			}
		}

		if (successfulTransactionCount == transactionMap.values().size() && order.getFailedTransactionCount() == failedTransactionCount) {
			return buildOrderResponse(order);
		} else {
			transactionService.addCardPaymentToOrder(orderRepository, tenantId, order, payments, transactionMap);
		}

		recalculateOrder(order, tenantId);
		Order updatedOrder = orderRepository.save(order, tenantId);

		return buildOrderResponse(updatedOrder);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public OrdersByCashDrawerResponse getOrdersByCashDrawer(String tenantId, String cashDrawerId) {
		OrdersByCashDrawerResponse response = new OrdersByCashDrawerResponse();
		List<Order> orders = orderRepository.getOrdersByCashDrawer(cashDrawerId, tenantId);
		response.setOrders(orders);
		response.setCount(orders.size());
		return response;
	}

	private void saveProductOrders(String tenantId, Order order) {
		List<CompletableFuture<ProductOrder>> productOrderFutures = new ArrayList<>();

		// Get list of Products associated with orderId
		List<ProductOrder> productOrders = ProductOrder.buildProductFromOrders(order);

		for (ProductOrder productOrder : productOrders) {
			productOrderFutures.add(CompletableFuture.supplyAsync(() ->
					productOrderRepository.save(productOrder, tenantId)
			));
		}

		Utils.completeFutures(productOrderFutures);
	}

	private void deleteProductOrders(String tenantId, Order order) {
		List<CompletableFuture<Void>> productOrderFutures = new ArrayList<>();
		List<LineItem> filterLineItems = order.getLineItems().stream()
				.filter(l -> !l.getType().equals(LineItemType.DISCOUNT.toString())).collect(Collectors.toList());

		for (LineItem lineItem : filterLineItems) {
			productOrderFutures.add(CompletableFuture.runAsync(() ->
					productOrderRepository.delete(lineItem.getId(), order.getId(), tenantId)
			));
		}

		Utils.completeFutures(productOrderFutures);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ItemsInfoResponse getItemsInfo(ItemsInfoRequest request, String tenantId) {
		// Build futures for each unique product ID
		List<ItemInfo> itemsInfo = Arrays.stream(request.getProductIds())
				.distinct()
				.map(productId -> CompletableFuture.supplyAsync(() -> {
					try {
						// Attempt to build orders by product response
						return buildItemInfo(productId, tenantId);
					} catch (Exception e) {
						// If an exception occurs during response building, return null
						return null;
					}
				}))
				.map(CompletableFuture::join)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		// Create the final response and set the orders by product
		ItemsInfoResponse response = new ItemsInfoResponse();
		response.setItemsInfo(itemsInfo);
		return response;
	}

	private ItemInfo buildItemInfo (String productId, String tenantId) {
		ProductResponse product = productServiceClient.get(tenantId, productId).getBody();

		if (product == null) {
			throw new ProductNotFoundException(productId, Product.STORE_ID);
		}

		ItemInfo response = new ItemInfo();

		List<ProductOrderDto> productOrders = orderRepository.getOrdersByProduct(productId, tenantId).stream()
				.map(ItemsInfoMapper.INSTANCE::productOrderToProductOrderDto).collect(Collectors.toList());
		ProductOrdersDetails productDetails = buildProductOrdersDetails(product, productOrders);

		response.setProductId(productId);
		response.setProductOrders(productOrders);
		response.setProductOrdersDetails(productDetails);

		return response;
	}

	private static ProductOrdersDetails buildProductOrdersDetails(ProductResponse product, List<ProductOrderDto> productOrders) {

		ProductOrdersDetails productOrderDetails = new ProductOrdersDetails();
		int committedQty = 0;
		int onOrderQty = 0;

		for (ProductOrderDto productOrder : productOrders) {
			if (productOrder.getStatus().equals(ProductOrderStatus.COMMITTED.toString())) {
				committedQty += productOrder.getQuantity();
			} else if (productOrder.getStatus().equals(ProductOrderStatus.ON_ORDER.toString())) {
				onOrderQty += productOrder.getQuantity();
			}
		}

		for (UOMResponse uom : product.getUnitsOfMeasure()) {
			if (uom.getId().equals(product.getQuantityUomId())) {
				productOrderDetails.setQuantityUom(uom.getName());
			}
			if (uom.getId().equals(product.getPricingUomId())) {
				productOrderDetails.setPricingUom(uom.getName());
			}
		}

		productOrderDetails.setCost(product.getCost());
		productOrderDetails.setRetailPrice(product.getRetailPrice());
		productOrderDetails.setQuantityOnHand(product.getQuantityOnHand());
		productOrderDetails.setCommittedQty(committedQty);
		productOrderDetails.setOnOrderQty(onOrderQty);
		productOrderDetails.setAvailableQty(product.getQuantityOnHand() - committedQty);

		return productOrderDetails;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ChargeCodeListResponse getChargeCodes(String orderId, String tenantId) {
		ChargeCodeListResponse response = new ChargeCodeListResponse();
		response.setOrderId(orderId);
		response.setChargeCodes(orderRepository.getChargeCodes(orderId, tenantId).stream().map(
				ChargeCodeMapper.INSTANCE::chargeCodeToChargeCodeListItem).collect(Collectors.toList()));
		response.setCount(response.getChargeCodes().size());
		return response;
	}

	private Order applyPatchToOrder(JsonPatch patch, Order order) throws JsonProcessingException, JsonPatchException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode patched = patch.apply(mapper.convertValue(order, JsonNode.class));
		return mapper.treeToValue(patched, Order.class);
	}

	private List<PaidOutCode> createPaidOutCodes(CreateOrderRequest request,
												 String tenantId,
												 CashDrawerResponse cashDrawerResponse,
												 String orderId) {
		List<PaidOutCode> paidOutCodes = new ArrayList<>();

		if (!request.getPaidOutCodes().isEmpty()) {
			for (PaidOutCodeRequest paidOutCodeRequest : request.getPaidOutCodes()) {
				PaidOutCode paidOutCode = OrderMapper.INSTANCE.paidOutCodeRequestToPaidOutCode(paidOutCodeRequest);
				paidOutCode.setOrderId(orderId);
				paidOutCode.setCashDrawerId(cashDrawerResponse.getId());
				paidOutCode.setRepUser(cashDrawerResponse.getAssignedUser());
				paidOutCodeRepository.save(paidOutCode, tenantId);
				paidOutCodes.add(paidOutCode);
			}
		}

		return paidOutCodes;
	}

	private String validateLinkedOrderId(String orderId, String tenantId) {
		Order linkedOrder = getOrderById(orderId, tenantId);
		return linkedOrder.getId();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public OrderResponse addReasonCodes(String orderId, String tenantId, UpdateOrderRequest request) {
		Order order = getOrderById(orderId, tenantId);

		// Setting linkedOrderId
		if (StringUtils.hasLength(request.getLinkedOrderId()) && request.getLinkedOrderId().equals(orderId)) {
			throw new OrderException("The orderId cannot be equal to the linkedOrderId.");
		} else if (request.getLinkedOrderId() != null) {
			order.setLinkedOrderId(validateLinkedOrderId(request.getLinkedOrderId(), tenantId));
		}

		order.setReasonCodes(getReasonCodes(request.getReasonCodes(), tenantId, order));

		Order updatedOrder = orderRepository.save(order, tenantId);
		return buildOrderResponse(updatedOrder);
	}

	private Order getOrderById(String orderId, String tenantId) {
		Order order = orderRepository.getOrderById(orderId, tenantId);
		if (order == null) {
			throw new OrderNotFoundException(orderId);
		}
		return order;
	}

	private List<ReasonCodeItem> getReasonCodes(List<ReasonCodeItemRequest> reasonCodesRequest,
												String tenantId,
												Order order) {
		Map<String, ReasonCodeItem> orderReasonCodesMap = order.getReasonCodes().stream()
				.collect(Collectors.toMap(ReasonCodeItem::getCode, reasonCode -> reasonCode));

		boolean sameReasonCodes = reasonCodesRequest.stream()
				.allMatch(request -> orderReasonCodesMap.containsKey(request.getCode()));

		if (sameReasonCodes) {
			return order.getReasonCodes();
		}

		ReasonCodesSettingsResponse settings = settingsServiceClient.getReasonCodes(tenantId).getBody();
		if (settings == null) {
			return order.getReasonCodes();
		}

		Map<String, ReasonCodeItem> settingsMap = settings.getReasonCodes().stream()
				.map(ServiceMapper.INSTANCE::reasonCodeToReasonCodeItem)
				.collect(Collectors.toMap(ReasonCodeItem::getCode, reasonCode -> reasonCode));

		List<ReasonCodeItem> updatedReasonCodes = new ArrayList<>(order.getReasonCodes());

		reasonCodesRequest.stream()
				.filter(item -> settingsMap.containsKey(item.getCode()) && !orderReasonCodesMap.containsKey(item.getCode()))
				.forEach(item -> updatedReasonCodes.add(settingsMap.get(item.getCode())));

		return updatedReasonCodes;
	}

	private void addTransactionPerPaidOutCode(List<PaidOutCode> paidOutCodes, Order order) {
		for (PaidOutCode paidOutCode: paidOutCodes) {
			transactionService.addCashTransactionToOrder(order, paidOutCode.getAmount().negate(), true);
		}
	}

	private static OrderResponse buildOrderResponse(Order model) {
		OrderResponse response = OrderMapper.INSTANCE.orderToOrderResponse(model);
		for (LineItem l : model.getLineItems()) {
			LineItemResponse itemResponse = OrderMapper.INSTANCE.lineItemToLineItemResponse(l);
			if (itemResponse.getType().equals(LineItemType.DISCOUNT.toString())) {
				BigDecimal price = itemResponse.getPrice().negate();
				itemResponse.setPrice(price);
				itemResponse.setExtendedPrice(price);
			}
		}
		return response;
	}

	private void recalculateOrder(Order order, String tenantId) {
		BigDecimal taxRate = taxProcessor.process(tenantId, order.getTaxId());
		calculateTotals(order, taxRate);

		boolean hasReturns = order.getLineItems().stream().anyMatch(l -> l.getQuantity() < 0);
		if (hasReturns) {
			calculateReturnTotals(order, taxRate);
			order.setHasReturns(true);
		}
	}

	private void calculateReturnTotals(Order order, BigDecimal taxRate) {
		List<LineItem> returns = order.getLineItems()
				.stream()
				.filter(l -> l.getQuantity() < 0)
				.collect(Collectors.toList());

		for (LineItem r : returns) {
			if (r.getTaxable()) {
				order.setReturnTaxableSubTotal(order.getReturnTaxableSubTotal().add(r.getExtendedPrice()));
			} else {
				order.setReturnNonTaxableSubTotal(order.getReturnNonTaxableSubTotal().add(r.getExtendedPrice()));
			}
		}

		order.setReturnTotal(roundValue(order.getReturnTaxableSubTotal().add(order.getReturnNonTaxableSubTotal())));
		order.setReturnTaxTotal(roundValue(order.getReturnTaxableSubTotal().multiply(taxRate)));
	}

	private void calculateTotals(Order order, BigDecimal taxRate) {
		BigDecimal taxableSubTotal = BigDecimal.ZERO;
		BigDecimal nonTaxableSubTotal = BigDecimal.ZERO;
		BigDecimal discountTotal = BigDecimal.ZERO;
		BigDecimal paidOutTotal = BigDecimal.ZERO;

		for (LineItem l : order.getLineItems()) {
			if (l.getType().equals(LineItemType.DISCOUNT.toString())) {
				discountTotal = discountTotal.add(l.getPrice());
			} else {
				if (l.getTaxable()) {
					taxableSubTotal = taxableSubTotal.add(l.getExtendedPrice());
				} else {
					nonTaxableSubTotal = nonTaxableSubTotal.add(l.getExtendedPrice());
				}
			}
		}

		for (PaidOutCodeItem p : order.getPaidOutCodeItems()) {
			paidOutTotal = paidOutTotal.add(p.getAmount());
		}

		order.setNonTaxableSubTotal(roundValue(nonTaxableSubTotal));
		order.setSubTotal(roundValue(taxableSubTotal.add(nonTaxableSubTotal)));
		order.setDiscountTotal(roundValue(discountTotal));

		BigDecimal taxTotal;
		if (taxableSubTotal.compareTo(discountTotal) > 0) {
			taxableSubTotal = taxableSubTotal.subtract(discountTotal);
		}
		taxTotal = taxableSubTotal.multiply(taxRate);

		order.setTaxableSubTotal(roundValue(taxableSubTotal));
		order.setTaxTotal(roundValue(taxTotal));
		BigDecimal total = roundValue(order.getSubTotal().subtract(order.getDiscountTotal()).add(order.getTaxTotal()));
		order.setTaxTotal(roundValue(taxTotal));
		order.setTotal(roundValue(total.subtract(paidOutTotal)));
		order.setBalanceDue(order.getTotal());
		if (order.getCashPaymentAmount().compareTo(BigDecimal.ZERO) > 0
				|| order.getCreditPaymentAmount().compareTo(BigDecimal.ZERO) > 0) {
			order.setBalanceDue(order.getTotal().subtract(order.getCashPaymentAmount()).subtract(order.getCreditPaymentAmount()));
		}

		transactionService.calculateChange(order);
	}

	private void clearTotals(Order order) {
		order.setTotal(BigDecimal.ZERO);
		order.setTaxTotal(BigDecimal.ZERO);
		order.setSubTotal(BigDecimal.ZERO);
		order.setTaxableSubTotal(BigDecimal.ZERO);
		order.setNonTaxableSubTotal(BigDecimal.ZERO);
		order.setDiscountTotal(BigDecimal.ZERO);
	}

	private CashDrawerResponse getCashDrawerForUser(String tenantId, String username) {
		ListCashDrawersResponse response = cashDrawerServiceClient.getByAssignedUser(tenantId, username).getBody();
		if (response == null || response.getCashDrawers().isEmpty()) {
			throw new OrderException("Cash Drawer not found for user " + username);
		}

		return response.getCashDrawers().get(0);
	}

	private void updateOrderAnalytics(Order order) {
		BigDecimal totalCost = order.getLineItems()
				.stream()
				.filter(l -> !l.getType().equals(LineItemType.DISCOUNT.toString()))
				.map(LineItem::getCost)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal paidOutTotal = order.getPaidOutCodeItems()
				.stream()
				.map(PaidOutCodeItem::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		int cashTransactionCount = 0;
		int cardTransactionCount = 0;
		for (Transaction t : order.getTransactions()) {
			if (t.getType().equals(PaymentType.CASH.toString())) {
				cashTransactionCount++;
			} else if (t.getType().equals(PaymentType.CARD.toString())) {
				cardTransactionCount++;
			}
		}

		order.setTotalCost(roundValue(totalCost));
		order.setPaidOutTotal(roundValue(paidOutTotal));
		order.setCashTransactionCount(cashTransactionCount);
		order.setCardTransactionCount(cardTransactionCount);
		order.setProfit(roundValue(order.getSubTotal().subtract(order.getTotalCost())));
		if (!order.getSubTotal().equals(BigDecimal.ZERO)) {
			order.setMargin(order.getProfit().divide(order.getSubTotal(), RoundingMode.CEILING));
		}
	}

	private void saveCategorySales(String tenantId, Order order) {
		List<CompletableFuture<CategorySale>> saleFutures = new ArrayList<>();
		Map<String, List<LineItem>> categoryMap = order.getLineItems()
				.stream()
				.filter(l -> !l.getType().equals(LineItemType.DISCOUNT.toString()))
				.collect(Collectors.groupingBy(LineItem::getCategoryId));
		categoryMap.forEach((k, v) -> {
			// Building order object to connect to Category
			CategoryOrder categoryOrder = new CategoryOrder();
			categoryOrder.setOrderId(order.getId());
			categoryOrder.setDate(order.getModified());

			CategorySale sale = categorySalesRepository.get(tenantId, k, order.getModified());
			if (sale == null) {
				sale = new CategorySale();
				sale.setCategoryId(k);
				sale.setCreated(order.getModified());
				sale.getOrders().add(categoryOrder);
			} else {
				sale.getOrders().add(categoryOrder);
			}

			CategorySale finalSale = sale;
			saleFutures.add(CompletableFuture.supplyAsync(() -> categorySalesRepository.save(finalSale, tenantId)));
		});
		Utils.completeFutures(saleFutures);
	}

	private void initializeTaxAndDeliveryInfo(String tenantId, Order order) {
		PurchasingSettingsResponse response = settingsServiceClient.getPurchasingSettings(tenantId);

		CreateTaxRequest taxRequest = buildCreateTaxRequest(response, order.getId());
		TaxResponse tax = taxService.create(taxRequest, tenantId);

		CreateDeliveryRequest deliveryRequest = buildCreateDeliveryRequest(tax, order.getId());
		DeliveryResponse delivery = deliveryService.create(tenantId, deliveryRequest);

		order.setTaxId(tax.getId());
		order.setDeliveryId(delivery.getId());
	}

	private CreateTaxRequest buildCreateTaxRequest(PurchasingSettingsResponse response, String orderId) {
		return CreateTaxRequest.builder()
				.orderId(orderId)
				.type(TaxType.TAXABLE.toString())
				.streetAddress(response.getAddress())
				.city(response.getCity())
				.county(response.getCounty())
				.state(response.getState())
				.zip(response.getZip())
				.build();
	}

	private CreateDeliveryRequest buildCreateDeliveryRequest(TaxResponse tax, String orderId) {
		return CreateDeliveryRequest.builder()
				.orderId(orderId)
				.streetAddress(DeliveryMapper.INSTANCE.deliveryLineResponseToDeliveryLineRequest(tax.getStreetAddress()))
				.city(DeliveryMapper.INSTANCE.deliveryLineResponseToDeliveryLineRequest(tax.getCity()))
				.county(DeliveryMapper.INSTANCE.deliveryLineResponseToDeliveryLineRequest(tax.getCounty()))
				.state(DeliveryMapper.INSTANCE.deliveryLineResponseToDeliveryLineRequest(tax.getState()))
				.build();
	}
}