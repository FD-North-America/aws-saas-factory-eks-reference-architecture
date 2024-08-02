package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.customer.dto.requests.certificate.CreateCertificateRequest;
import com.amazonaws.saas.eks.customer.dto.requests.certificate.ListCertificateRequestParams;
import com.amazonaws.saas.eks.customer.dto.requests.certificate.UpdateCertificateRequest;
import com.amazonaws.saas.eks.customer.dto.responses.certificate.CertificateResponse;
import com.amazonaws.saas.eks.customer.dto.responses.certificate.ListCertificateResponse;
import com.amazonaws.saas.eks.service.CertificateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class CertificateController {
    private static final Logger logger = LogManager.getLogger(CertificateController.class);

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private JwtAuthManager jwtAuthManager;

    @PostMapping(value = "{tenantId}/customers/certs", produces = { MediaType.APPLICATION_JSON_VALUE })
    public CertificateResponse create(@RequestBody @Valid CreateCertificateRequest request) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            return certificateService.create(tenantId, request);
        } catch (Exception e) {
            logger.error(String.format("Error creating certificate. Tenant ID: %s", tenantId), e);
            throw e;
        }
    }

    @GetMapping(value = "{tenantId}/customers/certs/{certId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public CertificateResponse get(@PathVariable String certId, @PathVariable String tenantId) {
        try {
            return certificateService.get(certId, tenantId);
        } catch (Exception e) {
            logger.error(String.format("Error fetching certificate. Certificate ID: %s, Tenant ID: %s", certId, tenantId), e);
            throw e;
        }
    }

    @GetMapping(value = "{tenantId}/customers/certs", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ListCertificateResponse getAll(@Valid ListCertificateRequestParams params) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            return certificateService.getAll(tenantId, params);
        } catch (Exception e) {
            logger.error(String.format("Error listing certificates. Tenant ID: %s", tenantId), e);
            throw e;
        }
    }

    @PutMapping(value = "{tenantId}/customers/certs/{certId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public CertificateResponse update(@PathVariable String certId,
                                      @RequestBody @Valid UpdateCertificateRequest request) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            return certificateService.update(certId, tenantId, request);
        } catch (Exception e) {
            logger.error(String.format("Error updating certificate. Certificate ID: %s, Tenant ID: %s", certId, tenantId), e);
            throw e;
        }
    }

    @DeleteMapping(value = "{tenantId}/customers/certs/{certId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public void delete(@PathVariable String certId) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            certificateService.delete(certId, tenantId);
        } catch (Exception e) {
            logger.error(String.format("Error deleting certificate. Certificate ID: %s, Tenant ID: %s", certId, tenantId), e);
            throw e;
        }
    }
}
