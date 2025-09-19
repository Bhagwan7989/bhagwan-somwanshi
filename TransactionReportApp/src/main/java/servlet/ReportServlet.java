package servlet;

import dao.TransactionDAO;
import model.Transaction;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportServlet extends HttpServlet {

    private static final int RECORDS_PER_PAGE = 20;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8"); // Handle special characters

        // Parse filters
        String fromDateStr = req.getParameter("fromDate");
        String toDateStr   = req.getParameter("toDate");
        String minAmtStr   = req.getParameter("minAmt");
        String maxAmtStr   = req.getParameter("maxAmt");
        String accountNumber = req.getParameter("accountNumber");
        String ifscCode     = req.getParameter("ifscCode");
        String status       = req.getParameter("status");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = null, toDate = null;
        Double minAmt = null, maxAmt = null;

        try {
            if (fromDateStr != null && !fromDateStr.isEmpty()) fromDate = sdf.parse(fromDateStr);
            if (toDateStr != null && !toDateStr.isEmpty()) toDate = sdf.parse(toDateStr);
            if (minAmtStr != null && !minAmtStr.isEmpty()) minAmt = Double.parseDouble(minAmtStr);
            if (maxAmtStr != null && !maxAmtStr.isEmpty()) maxAmt = Double.parseDouble(maxAmtStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Store filters in session for exports
        HttpSession session = req.getSession();
        session.setAttribute("fromDate", fromDate);
        session.setAttribute("toDate", toDate);
        session.setAttribute("minAmt", minAmt);
        session.setAttribute("maxAmt", maxAmt);
        session.setAttribute("accountNumber", accountNumber);
        session.setAttribute("ifscCode", ifscCode);
        session.setAttribute("status", status);

        // Get current page number from request, default to 1
        int page = 1;
        String pageStr = req.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // Fetch data from DAO
        TransactionDAO dao = new TransactionDAO();
        List<Transaction> allTransactions = dao.getTransactions(fromDate, toDate, minAmt, maxAmt, accountNumber, ifscCode, status);

        int totalRecords = allTransactions.size();
        int totalPages = (int) Math.ceil((double) totalRecords / RECORDS_PER_PAGE);

        // Calculate start and end index for current page
        int start = (page - 1) * RECORDS_PER_PAGE;
        int end = Math.min(start + RECORDS_PER_PAGE, totalRecords);

        List<Transaction> transactionsPage = allTransactions.subList(start, end);

        req.setAttribute("transactions", transactionsPage);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);

        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }
}


//package servlet;
//
//import dao.TransactionDAO;
//import model.Transaction;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.*;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//public class ReportServlet extends HttpServlet {
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
//            throws ServletException, IOException {
//
//        req.setCharacterEncoding("UTF-8"); // Handle special characters
//
//        // Parse filters
//        String fromDateStr = req.getParameter("fromDate");
//        String toDateStr   = req.getParameter("toDate");
//        String minAmtStr   = req.getParameter("minAmt");
//        String maxAmtStr   = req.getParameter("maxAmt");
//        String accountNumber = req.getParameter("accountNumber");
//        String ifscCode     = req.getParameter("ifscCode");
//        String status       = req.getParameter("status");
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date fromDate = null, toDate = null;
//        Double minAmt = null, maxAmt = null;
//
//        try {
//            if (fromDateStr != null && !fromDateStr.isEmpty()) fromDate = sdf.parse(fromDateStr);
//            if (toDateStr != null && !toDateStr.isEmpty()) toDate = sdf.parse(toDateStr);
//            if (minAmtStr != null && !minAmtStr.isEmpty()) minAmt = Double.parseDouble(minAmtStr);
//            if (maxAmtStr != null && !maxAmtStr.isEmpty()) maxAmt = Double.parseDouble(maxAmtStr);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Store filters in session for exports
//        HttpSession session = req.getSession();
//        session.setAttribute("fromDate", fromDate);
//        session.setAttribute("toDate", toDate);
//        session.setAttribute("minAmt", minAmt);
//        session.setAttribute("maxAmt", maxAmt);
//        session.setAttribute("accountNumber", accountNumber);
//        session.setAttribute("ifscCode", ifscCode);
//        session.setAttribute("status", status);
//
//        // Fetch data from DAO
//        TransactionDAO dao = new TransactionDAO();
//        List<Transaction> transactions = dao.getTransactions(fromDate, toDate, minAmt, maxAmt, accountNumber, ifscCode, status);
//
//        req.setAttribute("transactions", transactions);
//        req.getRequestDispatcher("index.jsp").forward(req, resp);
//    }
//}
