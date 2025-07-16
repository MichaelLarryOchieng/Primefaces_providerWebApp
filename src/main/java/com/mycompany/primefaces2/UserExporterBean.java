/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.PDFOptions;
import org.primefaces.component.export.PDFOrientationType;

@Named("userExporterBean")
@RequestScoped
public class UserExporterBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private ExcelOptions excelOptions;
    private PDFOptions pdfOptions;

    public UserExporterBean() {
        customizeExportOptions();
    }

    public void customizeExportOptions() {
        excelOptions = new ExcelOptions();
        excelOptions.setFacetBgColor("#A0D9B9");
        excelOptions.setFacetFontSize("12");
        excelOptions.setFacetFontColor("#FFFFFF");
        excelOptions.setFacetFontStyle("BOLD");
        excelOptions.setCellFontColor("#333333");
        excelOptions.setCellFontSize("10");
        excelOptions.setFontName("Calibri");

        pdfOptions = new PDFOptions();
        pdfOptions.setFacetBgColor("#4CAF50");
        pdfOptions.setFacetFontColor("#FFFFFF");
        pdfOptions.setFacetFontStyle("BOLD");
        pdfOptions.setCellFontSize("10");
        pdfOptions.setFontName("Helvetica");
        pdfOptions.setOrientation(PDFOrientationType.LANDSCAPE);
    }

    public void onRowExportXLS(Object document) {
        SXSSFWorkbook wb = (SXSSFWorkbook) document;
        SXSSFSheet sheet = wb.getSheetAt(0);

        CellStyle highlightStyle = wb.createCellStyle();
        highlightStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        highlightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        highlightStyle.setAlignment(HorizontalAlignment.CENTER);

        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            SXSSFRow row = sheet.getRow(i);
            if (row != null && i > 0 && (i % 5 == 0)) {
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    SXSSFCell cell = row.getCell(j);
                    if (cell != null) {
                        cell.setCellStyle(highlightStyle);
                    }
                }
            }
        }
    }

    public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
        Document pdf = (Document) document;
        // pdf.open(); // <--- REMOVE THIS LINE! It's likely causing the issue.
        pdf.setPageSize(PageSize.A4.rotate());

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String separator = File.separator;
        String logoPath = externalContext.getRealPath("") + separator
                + "images" + separator + "Logo.png";

        File logoFile = new File(logoPath);
        if (logoFile.exists()) {
            Image logo = Image.getInstance(logoPath);
            logo.scaleAbsolute(100f, 30f);
            logo.setAlignment(Image.ALIGN_RIGHT);
            pdf.add(logo);
        } else {
            System.err.println("PDF Export: Logo file not found at " + logoPath);
        }
    }

    public ExcelOptions getExcelOptions() {
        return excelOptions;
    }

    public PDFOptions getPdfOptions() {
        return pdfOptions;
    }
}