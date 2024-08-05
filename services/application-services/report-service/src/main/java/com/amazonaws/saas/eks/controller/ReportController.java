package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.dto.requests.*;
import com.amazonaws.saas.eks.dto.responses.cashdrawercheckoutreport.CashDrawerCheckoutReportResponse;
import com.amazonaws.saas.eks.dto.responses.categorySales.CategorySalesReportResponse;
import com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts.PaidOutCodesAndDiscountsReportResponse;
import com.amazonaws.saas.eks.dto.responses.returneditems.ReturnedItemsReportResponse;
import com.amazonaws.saas.eks.dto.responses.salesregister.SalesRegisterReportResponse;
import com.amazonaws.saas.eks.dto.responses.salestax.SalesTaxReportResponse;
import com.amazonaws.saas.eks.dto.responses.stockstatus.StockStatusReportResponse;
import com.amazonaws.saas.eks.service.ReportService;
import com.itextpdf.text.DocumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ReportController {
	private static final Logger logger = LogManager.getLogger(ReportController.class);

	@Autowired
	private ReportService reportService;

	@Autowired
	private JwtAuthManager jwtAuthManager;

	@PostMapping(value = "{tenantId}/reports/stock-status", produces = {MediaType.APPLICATION_JSON_VALUE})
	public StockStatusReportResponse generateStockStatusReport(@RequestBody @Valid StockStatusReportRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return reportService.generateStockStatusReport(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating stock level report", e);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/reports/stock-status/pdf", produces = {MediaType.APPLICATION_PDF_VALUE})
	public byte[] generateStockStatusReportPDF(@RequestBody @Valid StockStatusReportRequest request) throws DocumentException {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return reportService.generateStockStatusReportPDF(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating stock level report PDF", e);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/reports/cash-drawer-checkout", produces = {MediaType.APPLICATION_JSON_VALUE})
	public CashDrawerCheckoutReportResponse generateCashDrawerCheckoutReport(
			@RequestBody @Valid CashDrawerCheckoutReportRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return reportService.generateCashDrawerCheckoutReport(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating cash drawer checkout report", e);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/reports/cash-drawer-checkout/pdf", produces = {MediaType.APPLICATION_PDF_VALUE})
	public byte[] generateCashDrawerCheckoutReportPDF(
			@RequestBody @Valid CashDrawerCheckoutReportRequest request) throws DocumentException {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return reportService.generateCashDrawerCheckoutReportPDF(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating cash drawer checkout report PDF", e);
			throw e;
		}
	}

	@PostMapping(value ="{tenantId}/reports/sales-tax", produces = {MediaType.APPLICATION_JSON_VALUE})
	public SalesTaxReportResponse generateSalesTaxReport(@RequestBody @Valid SalesTaxReportRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return reportService.generateSalesTaxReport(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating sales tax report", e);
			throw e;
		}
	}

	@PostMapping(value ="{tenantId}/reports/sales-tax/pdf", produces = {MediaType.APPLICATION_PDF_VALUE})
	public byte[] generateSalesTaxReportPDF(@RequestBody @Valid SalesTaxReportRequest request) throws DocumentException {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return reportService.generateSalesTaxReportPDF(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating sales tax report", e);
			throw e;
		}
	}

	@PostMapping(value ="{tenantId}/reports/returned-items", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ReturnedItemsReportResponse generateReturnedItemsReport(@RequestBody @Valid ReturnedItemsReportRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return reportService.generateReturnedItemsReport(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating returned items report", e);
			throw e;
		}
	}

	@PostMapping(value ="{tenantId}/reports/returned-items/pdf", produces = {MediaType.APPLICATION_PDF_VALUE})
	public byte[] generateReturnedItemsReportPDF(@RequestBody @Valid ReturnedItemsReportRequest request) throws DocumentException {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return reportService.generateReturnedItemsReportPDF(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating returned items report", e);
			throw e;
		}
	}

	@PostMapping(value ="{tenantId}/reports/paid-out-codes-and-discounts", produces = {MediaType.APPLICATION_JSON_VALUE})
	public PaidOutCodesAndDiscountsReportResponse generatePaidOutCodesAndDiscountsReport(
			@RequestBody @Valid PaidOutCodesAndDiscountsReportRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return reportService.generatePaidOutCodesAndDiscountsReport(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating paid out codes and discounts report", e);
			throw e;
		}
	}

	@PostMapping(value ="{tenantId}/reports/paid-out-codes-and-discounts/pdf", produces = {MediaType.APPLICATION_PDF_VALUE})
	public byte[] generatePaidOutCodesAndDiscountsReportPDF(
			@RequestBody @Valid PaidOutCodesAndDiscountsReportRequest request) throws DocumentException {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return reportService.generatePaidOutCodesAndDiscountsReportPDF(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating paid out codes and discounts report", e);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/reports/sales-register", produces = {MediaType.APPLICATION_JSON_VALUE})
	public SalesRegisterReportResponse generateSalesRegisterReport(@RequestBody @Valid SalesRegisterReportRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return reportService.generateSalesRegisterReport(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating sales register report", e);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/reports/sales-register/pdf", produces = {MediaType.APPLICATION_PDF_VALUE})
	public byte[] generateSalesRegisterReportPDF(@RequestBody @Valid SalesRegisterReportRequest request) throws DocumentException {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return reportService.generateSalesRegisterReportPDF(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating sales register report", e);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/reports/category-sales", produces = {MediaType.APPLICATION_JSON_VALUE})
	public CategorySalesReportResponse generateCategorySalesReport(@RequestBody @Valid CategorySaleRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return reportService.generateCategorySalesReport(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating category sales report", e);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/reports/category-sales/pdf", produces = {MediaType.APPLICATION_PDF_VALUE})
	public byte[] generateCategorySalesReportPDF(@RequestBody @Valid CategorySaleRequest request) throws DocumentException {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return reportService.generateCategorySalesReportPDF(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating category sales report PDF", e);
			throw e;
		}
	}

	/**
	 * Heartbeat method to check if report service is up and running
	 *
	 */
	@GetMapping(value = "{tenantId}/reports/health")
	public String health() {
		return "\"Report service is up!\"";
	}
}
