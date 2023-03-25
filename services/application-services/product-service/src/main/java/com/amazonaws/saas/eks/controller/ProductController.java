package com.amazonaws.saas.eks.controller;

import javax.validation.Valid;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.dto.requests.product.*;
import com.amazonaws.saas.eks.dto.responses.product.ListProductResponse;
import com.amazonaws.saas.eks.dto.responses.product.ProductResponse;
import com.amazonaws.saas.eks.model.Permission;
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

	@PreAuthorize("hasAnyAuthority('" + Permission.PRODUCT_READ + "')")
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

	@PreAuthorize("hasAnyAuthority('" + Permission.PRODUCT_READ + "')")
	@GetMapping(value = "{tenantId}/products/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE })
	public ProductResponse get(@PathVariable("productId") String productId) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return productService.get(tu.getTenantId(), productId);
		} catch (Exception e) {
			logger.error("Product not found with ID: " + productId, e);
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

	/**
	 * Heartbeat method to check if product service is up and running
	 * 
	 */
	@GetMapping(value = "{tenantId}/products/health")
	public String health() {
		return "\"Product service is up!\"";
	}
}
