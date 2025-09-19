package dao;

import model.Transaction;
import dao.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionDAO {

    public List<Transaction> getTransactions(Date fromDate, Date toDate, Double minAmt, Double maxAmt,
                                             String accountNumber, String ifscCode, String status) {

        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {

            StringBuilder sql = new StringBuilder("SELECT * FROM transactions WHERE 1=1");

            if (fromDate != null) sql.append(" AND transaction_date >= ?");
            if (toDate != null) sql.append(" AND transaction_date <= ?");
            if (minAmt != null) sql.append(" AND amount >= ?");
            if (maxAmt != null) sql.append(" AND amount <= ?");
            if (accountNumber != null && !accountNumber.isEmpty()) sql.append(" AND account_number LIKE ?");
            if (ifscCode != null && !ifscCode.isEmpty()) sql.append(" AND ifsc_code LIKE ?");
            if (status != null && !status.isEmpty()) sql.append(" AND status = ?");

            sql.append(" ORDER BY transaction_date DESC");

            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                int idx = 1;
                if (fromDate != null) ps.setDate(idx++, new java.sql.Date(fromDate.getTime()));
                if (toDate != null) ps.setDate(idx++, new java.sql.Date(toDate.getTime()));
                if (minAmt != null) ps.setDouble(idx++, minAmt);
                if (maxAmt != null) ps.setDouble(idx++, maxAmt);
                if (accountNumber != null && !accountNumber.isEmpty()) ps.setString(idx++, "%" + accountNumber + "%");
                if (ifscCode != null && !ifscCode.isEmpty()) ps.setString(idx++, "%" + ifscCode + "%");
                if (status != null && !status.isEmpty()) ps.setString(idx++, status);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Transaction t = new Transaction();
                        t.setTransactionId(rs.getInt("transaction_id"));
                        t.setAccountNumber(rs.getString("account_number"));
                        t.setIfscCode(rs.getString("ifsc_code"));
                        t.setBeneficiaryName(rs.getString("beneficiary_name"));
                        t.setSenderName(rs.getString("sender_name"));
                        t.setTransactionDate(rs.getDate("transaction_date"));
                        t.setAmount(rs.getDouble("amount"));
                        t.setCurrency(rs.getString("currency"));
                        t.setMode(rs.getString("mode"));
                        t.setStatus(rs.getString("status"));
                        t.setReferenceNumber(rs.getString("reference_number"));
                        t.setUtrNumber(rs.getString("utr_number"));
                        t.setBranch(rs.getString("branch"));
                        t.setDescription(rs.getString("description"));
                        t.setRemarks(rs.getString("remarks"));
                        list.add(t);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
