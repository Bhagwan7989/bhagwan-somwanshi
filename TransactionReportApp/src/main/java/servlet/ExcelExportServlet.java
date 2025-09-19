package servlet;

import dao.TransactionDAO;
import model.Transaction;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExcelExportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Date fromDate = null;
        Date toDate = null;
        Double minAmt = null;
        Double maxAmt = null;
        String accountNumber = null;
        String ifscCode = null;
        String paymentStatus = null;

        if (session != null) {
            fromDate = (Date) session.getAttribute("fromDate");
            toDate = (Date) session.getAttribute("toDate");
            minAmt = (Double) session.getAttribute("minAmt");
            maxAmt = (Double) session.getAttribute("maxAmt");
            accountNumber = (String) session.getAttribute("accountNumber");
            ifscCode = (String) session.getAttribute("ifscCode");
            paymentStatus = (String) session.getAttribute("paymentStatus");
        }

        TransactionDAO dao = new TransactionDAO();
        List<Transaction> transactions = dao.getTransactions(
                fromDate, toDate, minAmt, maxAmt, accountNumber, ifscCode, paymentStatus
        );

        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=transactions.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");
        Row header = sheet.createRow(0);

        String[] columns = {
                "ID", "Account No", "IFSC", "Beneficiary", "Sender",
                "Date", "Amount", "Currency", "Mode", "Status",
                "Reference No", "UTR No", "Branch", "Description", "Remarks"
        };

        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        int rowIdx = 1;

        for (Transaction t : transactions) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(t.getTransactionId());
            row.createCell(1).setCellValue(t.getAccountNumber());
            row.createCell(2).setCellValue(t.getIfscCode());
            row.createCell(3).setCellValue(t.getBeneficiaryName());
            row.createCell(4).setCellValue(t.getSenderName());
            row.createCell(5).setCellValue(t.getTransactionDate() != null ? sdf.format(t.getTransactionDate()) : "");
            row.createCell(6).setCellValue(t.getAmount());
            row.createCell(7).setCellValue(t.getCurrency());
            row.createCell(8).setCellValue(t.getMode());
            row.createCell(9).setCellValue(t.getStatus());
            row.createCell(10).setCellValue(t.getReferenceNumber());
            row.createCell(11).setCellValue(t.getUtrNumber());
            row.createCell(12).setCellValue(t.getBranch());
            row.createCell(13).setCellValue(t.getDescription());
            row.createCell(14).setCellValue(t.getRemarks());
        }

        for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);

        OutputStream out = resp.getOutputStream();
        workbook.write(out);
        workbook.close();
        out.close();
    }
}
