package com.amazonaws.saas.eks.generator;

import com.itextpdf.text.DocumentException;

public interface Generator<T, U> {
    U generate(String tenantId, T request);

    byte[] generatePDF(String tenantId, T request) throws DocumentException;
}
