package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.payment.dto.requests.*;
import com.amazonaws.saas.eks.payment.dto.responses.ListOrderPaymentsResponse;
import com.amazonaws.saas.eks.payment.model.Permission;
import com.amazonaws.saas.eks.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class PaymentController {
	private static final Logger logger = LogManager.getLogger(PaymentController.class);

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private JwtAuthManager jwtAuthManager;

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_CREATE + "','" + Permission.POS_SERVER_UPDATE + "')")
	@PostMapping(value = "{tenantId}/payments/connect", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> connect(@PathVariable String tenantId,
										  @Valid @RequestBody ConnectRequest request) {
		try {
			return paymentService.connect(tenantId, request);
		} catch (Exception e) {
			logger.error("Error connecting", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_READ + "')")
	@PostMapping(value = "{tenantId}/payments/ping", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> ping() {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return paymentService.ping(tu.getTenantId());
		} catch (Exception e) {
			logger.error("Error on ping", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_READ + "')")
	@PostMapping(value = "{tenantId}/payments/listTerminals", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> listTerminals() {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return paymentService.listTerminals(tu.getTenantId());
		} catch (Exception e) {
			logger.error("Error listing terminals", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_DELETE + "')")
	@PostMapping(value = "{tenantId}/payments/disconnect", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> disconnect() {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return paymentService.disconnect(tu.getTenantId());
		} catch (Exception e) {
			logger.error("Error disconnecting", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_READ + "')")
	@PostMapping(value = "{tenantId}/payments/terminalDetails", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> terminalDetails() {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return paymentService.terminalDetails(tu.getTenantId());
		} catch (Exception e) {
			logger.error("Error showing terminal details", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_UPDATE + "')")
	@PostMapping(value = "{tenantId}/payments/display", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> display(@Valid @RequestBody DisplayRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return paymentService.display(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error displaying a text", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_UPDATE + "')")
	@PostMapping(value = "{tenantId}/payments/readInput", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> readInput(@Valid @RequestBody ReadInputRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return paymentService.readInput(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error reading input", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_UPDATE + "')")
	@PostMapping(value = "{tenantId}/payments/readSignature", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> readSignature(@Valid @RequestBody ReadSignatureRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return paymentService.readSignature(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error reading signature", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_UPDATE + "')")
	@PostMapping(value = "{tenantId}/payments/readConfirmation", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> readConfirmation(@Valid @RequestBody ReadConfirmationRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return paymentService.readConfirmation(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error reading confirmation", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_UPDATE + "')")
	@PostMapping(value = "{tenantId}/payments/cancel", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> cancel() {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return paymentService.cancel(tu.getTenantId());
		} catch (Exception e) {
			logger.error("Error canceling", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_UPDATE + "')")
	@PostMapping(value = "{tenantId}/payments/readManual", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> readManual(@Valid @RequestBody ReadManualRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return paymentService.readManual(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error to read manually", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_UPDATE + "')")
	@PostMapping(value = "{tenantId}/payments/readCard", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> readCard(@Valid @RequestBody ReadCardRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return paymentService.readCard(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error to read card", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_UPDATE + "','" + Permission.POS_SERVER_UPDATE + "')")
	@PostMapping(value = "{tenantId}/payments/authCard", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> authCard(@PathVariable String tenantId,
										   @Valid @RequestBody AuthCardRequest request) {
		try {
			return paymentService.authCard(tenantId, request);
		} catch (Exception e) {
			logger.error("Error to auth card", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_UPDATE + "')")
	@PostMapping(value = "{tenantId}/payments/authManual", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> authManual(@Valid @RequestBody AuthManualRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return paymentService.authManual(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error to auth manually", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_UPDATE + "')")
	@PostMapping(value = "{tenantId}/payments/tip", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> tip(@Valid @RequestBody TipRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return paymentService.tip(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error to tip", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.POS_SERVER_READ + "')")
	@GetMapping(value = "{tenantId}/payments/{orderNumber}", produces = { MediaType.APPLICATION_JSON_VALUE})
	public ListOrderPaymentsResponse getOrderPayments(@PathVariable String tenantId, @PathVariable String orderNumber) throws JsonProcessingException {
		try {
			return paymentService.getOrderPayments(tenantId, orderNumber);
		} catch (Exception e) {
			logger.error("Error reading payments for Order: " + orderNumber, e);
			throw e;
		}
	}

	/**
	 * Heartbeat method to check if payment service is up and running
	 *
	 */
	@GetMapping(value = "{tenantId}/payments/health")
	public String health() {
		return "\"Payment service is up!\"";
	}
}
