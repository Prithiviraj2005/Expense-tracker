package com.expensetracker.service;

import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.TransactionType;
import com.expensetracker.repository.TransactionRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportService {

    private final TransactionRepository transactionRepository;

    public ExportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    private List<Transaction> getTransactions(Long userId, TransactionType type, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
        } else {
            transactions = transactionRepository.findByUserId(userId);
        }

        if (type != null) {
            return transactions.stream().filter(t -> t.getType() == type).collect(Collectors.toList());
        }
        return transactions;
    }

    public byte[] exportCsv(Long userId, TransactionType type, LocalDate startDate, LocalDate endDate) throws Exception {
        List<Transaction> transactions = getTransactions(userId, type, startDate, endDate);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));
        
        String[] header = {"ID", "Date", "Description", "Category", "Type", "Payment Method", "Amount"};
        writer.writeNext(header);

        for (Transaction t : transactions) {
            String[] data = {
                String.valueOf(t.getId()),
                t.getTransactionDate().toString(),
                t.getDescription() != null ? t.getDescription() : "",
                t.getCategory().getName(),
                t.getType().toString(),
                t.getPaymentMethod() != null ? t.getPaymentMethod().toString() : "",
                t.getAmount().toString()
            };
            writer.writeNext(data);
        }
        
        writer.close();
        return out.toByteArray();
    }

    public byte[] exportExcel(Long userId, TransactionType type, LocalDate startDate, LocalDate endDate) throws Exception {
        List<Transaction> transactions = getTransactions(userId, type, startDate, endDate);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Date", "Description", "Category", "Type", "Payment Method", "Amount"};
        for (int i = 0; i < columns.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (Transaction t : transactions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(t.getId());
            row.createCell(1).setCellValue(t.getTransactionDate().toString());
            row.createCell(2).setCellValue(t.getDescription() != null ? t.getDescription() : "");
            row.createCell(3).setCellValue(t.getCategory().getName());
            row.createCell(4).setCellValue(t.getType().toString());
            row.createCell(5).setCellValue(t.getPaymentMethod() != null ? t.getPaymentMethod().toString() : "");
            row.createCell(6).setCellValue(t.getAmount().doubleValue());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    public byte[] exportPdf(Long userId, TransactionType type, LocalDate startDate, LocalDate endDate) throws Exception {
        List<Transaction> transactions = getTransactions(userId, type, startDate, endDate);
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        Paragraph title = new Paragraph("Transactions Report", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);

        String[] headers = {"ID", "Date", "Description", "Category", "Type", "Payment", "Amount"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setPhrase(new Phrase(header));
            table.addCell(cell);
        }

        for (Transaction t : transactions) {
            table.addCell(String.valueOf(t.getId()));
            table.addCell(t.getTransactionDate().toString());
            table.addCell(t.getDescription() != null ? t.getDescription() : "");
            table.addCell(t.getCategory().getName());
            table.addCell(t.getType().toString());
            table.addCell(t.getPaymentMethod() != null ? t.getPaymentMethod().toString() : "");
            table.addCell(t.getAmount().toString());
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }
}
