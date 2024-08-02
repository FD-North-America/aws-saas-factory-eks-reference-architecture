package com.amazonaws.saas.eks.generator.paidoutcodesanddiscounts;

import com.amazonaws.saas.eks.dto.requests.PaidOutCodesAndDiscountsReportRequest;
import com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts.DiscountResponse;
import com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts.PaidOutCodeListResponse;
import com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts.PaidOutCodeResponse;
import com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts.PaidOutCodesAndDiscountsReportResponse;
import com.amazonaws.saas.eks.generator.Generator;
import com.amazonaws.saas.eks.mapper.ReportMapper;
import com.amazonaws.saas.eks.model.enums.ReportName;
import com.amazonaws.saas.eks.order.model.Discount;
import com.amazonaws.saas.eks.order.model.PaidOutCode;
import com.amazonaws.saas.eks.order.model.enums.PaidOutCodeType;
import com.amazonaws.saas.eks.repository.PaidOutCodesAndDiscountsRepository;
import com.amazonaws.saas.eks.repository.SettingsRepository;
import com.amazonaws.saas.eks.settings.model.Settings;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

import static com.amazonaws.saas.eks.util.Utils.createToDate;

@Service
public class PaidOutCodesAndDiscountsReportGenerator
        implements Generator<PaidOutCodesAndDiscountsReportRequest, PaidOutCodesAndDiscountsReportResponse> {
    private static final Logger logger = LogManager.getLogger(PaidOutCodesAndDiscountsReportGenerator.class);

    private static final String[] PAID_OUT_TABLE_HEADERS = {
            "Paid out Code",
            "Amount",
            "Date",
            "Paid Out Code #",
            "REP"
    };
    private static final String[] DISCOUNT_TABLE_HEADERS = {
            "Discount Code",
            "Amount",
            "Date",
            "Invoice #",
            "REP (User)"
    };

    @Autowired
    private PaidOutCodesAndDiscountsRepository reportRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Override
    public PaidOutCodesAndDiscountsReportResponse generate(String tenantId, PaidOutCodesAndDiscountsReportRequest request) {
        logger.info("Generating report {}", ReportName.PAID_OUT_CODES_AND_DISCOUNTS);

        PaidOutCodesAndDiscountsReportResponse response = new PaidOutCodesAndDiscountsReportResponse();

        Settings settings = settingsRepository.get(tenantId);
        TimeZone timeZone = TimeZone.getTimeZone(ZoneId.of(settings.getTimeZone()));

        if (request.getToDate() == null) {
            request.setToDate(createToDate(settings.getTimeZone()));
        }
        populatePaidOutCodes(tenantId, request, PaidOutCodeType.ONLINE_REDEEM, response.getPaidOutCodes().getOnlineRedeem(), timeZone);
        populatePaidOutCodes(tenantId, request, PaidOutCodeType.INSTANT_REDEEM, response.getPaidOutCodes().getInstantRedeem(), timeZone);
        populatePaidOutCodes(tenantId, request, PaidOutCodeType.MISC, response.getPaidOutCodes().getMisc(), timeZone);

        populateDiscounts(tenantId, request, response, timeZone);

        return response;
    }

    @Override
    public byte[] generatePDF(String tenantId, PaidOutCodesAndDiscountsReportRequest request) throws DocumentException {
        PaidOutCodesAndDiscountsReportResponse data = generate(tenantId, request);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Paid Out Codes Table
        PdfPTable paidOutTable = new PdfPTable(5);
        addTableHeader(paidOutTable, PAID_OUT_TABLE_HEADERS);
        addPaidOutRows(paidOutTable, data);
        document.add(paidOutTable);

        // Discounts Table
        PdfPTable discountsTable = new PdfPTable(5);
        addTableHeader(discountsTable, DISCOUNT_TABLE_HEADERS);
        addDiscountRows(discountsTable, data);
        document.add(discountsTable);

        document.close();
        return outputStream.toByteArray();
    }

    private void populatePaidOutCodes(String tenantId, PaidOutCodesAndDiscountsReportRequest request,
                                      PaidOutCodeType type, PaidOutCodeListResponse listResponse, TimeZone timeZone) {
        List<PaidOutCode> paidOutCodes = reportRepository.getPaidOutCodes(tenantId, request.getFromDate(),
                request.getToDate(), type);
        if (paidOutCodes != null) {
            BigDecimal total = BigDecimal.ZERO;
            for (PaidOutCode paidOutCode: paidOutCodes) {
                listResponse.getItems().add(ReportMapper.INSTANCE.paidOutCodeToPaidOutCodeResponse(paidOutCode, timeZone));
                total = total.add(paidOutCode.getAmount());
            }
            listResponse.setTotalAmount(total);
        }
    }

    private void populateDiscounts(String tenantId, PaidOutCodesAndDiscountsReportRequest request,
                                   PaidOutCodesAndDiscountsReportResponse response, TimeZone timeZone) {
        List<Discount> discounts = reportRepository.getDiscounts(tenantId, request.getFromDate(),
                request.getToDate());
        if (discounts != null) {
            BigDecimal total = BigDecimal.ZERO;
            for (Discount discount: discounts) {
                response.getDiscounts().getItems().add(ReportMapper.INSTANCE.discountToDiscountResponse(discount, timeZone));
                total = total.add(discount.getPrice());
            }
            response.getDiscounts().setTotalAmount(total);
        }
    }

    private void addTableHeader(PdfPTable table, String[] headers) {
        for (String columnTitle : headers) {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            table.addCell(header);
        }
    }

    private void addPaidOutRows(PdfPTable table, PaidOutCodesAndDiscountsReportResponse data) {
        for (PaidOutCodeResponse code : data.getPaidOutCodes().getOnlineRedeem().getItems()) {
            table.addCell("Online Redeem");
            table.addCell(String.valueOf(code.getAmount()));
            table.addCell(String.valueOf(code.getCreated()));
            table.addCell(code.getCode());
            table.addCell(code.getRepUser());
        }

        for (PaidOutCodeResponse code : data.getPaidOutCodes().getInstantRedeem().getItems()) {
            table.addCell("Instant Redeem");
            table.addCell(String.valueOf(code.getAmount()));
            table.addCell(String.valueOf(code.getCreated()));
            table.addCell(code.getCode());
            table.addCell(code.getRepUser());
        }
    }

    private void addDiscountRows(PdfPTable table, PaidOutCodesAndDiscountsReportResponse data) {
        for (DiscountResponse discount : data.getDiscounts().getItems()) {
            table.addCell(discount.getCode());
            table.addCell(String.valueOf(discount.getAmount()));
            table.addCell(String.valueOf(discount.getCreated()));
            table.addCell(discount.getOrderNumber());
            table.addCell(discount.getRepUser());
        }
    }
}
