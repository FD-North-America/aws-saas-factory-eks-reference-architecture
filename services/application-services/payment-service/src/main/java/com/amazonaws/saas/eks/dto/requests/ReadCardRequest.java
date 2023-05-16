package com.amazonaws.saas.eks.dto.requests;

import lombok.Getter;
import lombok.Setter;

public class ReadCardRequest {
    @Getter
    @Setter
    private String amount;

    @Getter
    @Setter
    private Boolean includeSignature;

    @Getter
    @Setter
    private Boolean gzipSignature;

    @Getter
    @Setter
    private String signatureFormat;

    @Getter
    @Setter
    private String signatureImageType;

    @Getter
    @Setter
    private String signatureDimensions;

    @Getter
    @Setter
    private Boolean includeAmountDisplay;

    @Getter
    @Setter
    private Boolean confirmAmount;

    @Getter
    @Setter
    private Boolean beep;

    @Getter
    @Setter
    private String aid;
}
