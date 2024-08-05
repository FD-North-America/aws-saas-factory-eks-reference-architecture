package com.amazonaws.saas.eks.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class PDFUtils {
    public static void addTable(Document document, PdfPTable table) throws DocumentException {
        table.setWidthPercentage(100);
        Paragraph p = new Paragraph();
        p.add(table);
        p.setSpacingAfter(20);
        document.add(p);
    }

    public static void addHeaderText(Document document, String text) throws DocumentException {
        Paragraph paragraph = new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 18));
        document.add(paragraph);
        document.add(Chunk.SPACETABBING);
    }

    public static void addHeaderTextMedium(Document document, String text) throws DocumentException {
        Paragraph paragraph = new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 14));
        document.add(paragraph);
        document.add(Chunk.SPACETABBING);
    }

    public static void addTableHeaderCell(PdfPTable table, String text) {
        PdfPCell header = getTableHeaderCell(text);
        table.addCell(header);
    }

    public static void addTableHeaderCellACenter(PdfPTable table, String text) {
        PdfPCell header = getTableHeaderCell(text);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }

    private static PdfPCell getTableHeaderCell(String text) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(2);
        header.setPhrase(new Phrase(text));
        return header;
    }

    public static void addTableCellARight(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPhrase(new Phrase(text));
        table.addCell(cell);
    }

    public static void addTableCellACenter(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase(text));
        table.addCell(cell);
    }

    public static void addTableHeaderRow(PdfPTable table, String text, int columnCount) {
        PdfPCell mergedCell = new PdfPCell();
        mergedCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        mergedCell.setPhrase(new Phrase(text));
        mergedCell.setColspan(columnCount);
        table.addCell(mergedCell);
    }
}
