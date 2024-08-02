package com.amazonaws.saas.eks.controller;

import javax.validation.Valid;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.product.dto.requests.product.*;
import com.amazonaws.saas.eks.product.dto.responses.product.ListProductResponse;
import com.amazonaws.saas.eks.product.dto.responses.product.PricingResponse;
import com.amazonaws.saas.eks.product.dto.responses.product.ProductResponse;
import com.amazonaws.saas.eks.product.model.Permission;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.amazonaws.saas.eks.service.ProductService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ProductController {
	private static final Logger logger = LogManager.getLogger(ProductController.class);

	@Autowired
	private JwtAuthManager jwtAuthManager;

	@Autowired
	private ProductService productService;

	@PreAuthorize("hasAnyAuthority('" + Permission.PRODUCT_CREATE + "')")
	@PostMapping(value = "{tenantId}/products", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ProductResponse create(@RequestBody @Valid CreateProductRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return productService.create(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating product", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.PRODUCT_READ + "', '" + Permission.PRODUCT_INVOICING_READ + "')")
	@GetMapping(value = "{tenantId}/products", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ListProductResponse getAll(@Valid ListProductRequestParams params) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return productService.getAll(tu.getTenantId(), params);
		} catch (Exception e) {
			logger.error("Error listing products", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.PRODUCT_READ + "', '" + Permission.SERVER_PRODUCT_READ + "', '" + Permission.PRODUCT_INVOICING_READ + "')")
	@GetMapping(value = "{tenantId}/products/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE })
	public ProductResponse get(@PathVariable("productId") String productId, @PathVariable("tenantId") String tenantId) {
		try {
			return productService.get(tenantId, productId);
		} catch (Exception e) {
			logger.error(String.format("Product not found with ID: %s", productId), e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.PRODUCT_UPDATE + "')")
	@PutMapping(value = "{tenantId}/products/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ProductResponse update(@PathVariable("productId") String productId,
								  @RequestBody @Valid UpdateProductRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return productService.update(tu.getTenantId(), productId, request);
		} catch (Exception e) {
			logger.error("Error updating product", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.PRODUCT_DELETE + "')")
	@DeleteMapping(value = "{tenantId}/products/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public void delete(@PathVariable("productId") String productId) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			productService.delete(tu.getTenantId(), productId);
		} catch (Exception e) {
			logger.error("Error deleting product", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.SERVER_PRODUCT_READ + "')")
	@PostMapping(value = "{tenantId}/products/pricing", produces = {MediaType.APPLICATION_JSON_VALUE})
	public PricingResponse getPricingDetails(@PathVariable String tenantId, @RequestBody PricingRequestParams params) {
		try {
			return productService.getPricingDetails(tenantId, params);
		} catch (Exception e) {
			logger.error("Error fetching pricing details", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.SERVER_PRODUCT_UPDATE + "')")
	@PutMapping(value = "{tenantId}/products/counts", produces = {MediaType.APPLICATION_JSON_VALUE})
	public void updateProductCounts(@PathVariable String tenantId, @RequestBody UpdateCountRequestParams params) {
		try {
			productService.updateProductCounts(tenantId, params);
		} catch (Exception e) {
			logger.error("Error fetching pricing details", e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.PRODUCT_READ + "', '" + Permission.PRODUCT_INVOICING_READ + "')")
	@GetMapping(value = "{tenantId}/products/identifiers/{identifier}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ListProductResponse getByIdentifier(@PathVariable String identifier) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return productService.getByIdentifier(tu.getTenantId(), identifier);
		} catch (Exception e) {
			logger.error("Error fetching product by identifier", e);
			throw e;
		}
	}

	/**
	 * Heartbeat method to check if product service is up and running
	 *
	 */
	@GetMapping(value = "{tenantId}/products/health")
	public String health() {
		return "\"Product service is up!\"";
	}
}
