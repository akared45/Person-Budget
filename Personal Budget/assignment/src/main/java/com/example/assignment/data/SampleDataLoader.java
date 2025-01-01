package com.example.assignment.data;

import com.example.assignment.model.Transaction;
import com.example.assignment.model.TransactionTree;
import com.example.assignment.model.TransactionType;

import java.time.LocalDateTime;

public class SampleDataLoader {
    public static TransactionTree loadSampleData() {
        TransactionTree transactionTree = new TransactionTree();

        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2023, 5, 1, 9, 0, 0), "Lương", 5000, TransactionType.INCOME));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2023, 6, 3, 14, 15, 30), "Kinh Doanh", 7000, TransactionType.INCOME));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2023, 7, 5, 18, 30, 0), "Ăn Uống", 300, TransactionType.EXPENSE));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2023, 8, 7, 10, 45, 0), "Tiền Nhà", 7000, TransactionType.EXPENSE));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2023, 9, 10, 8, 20, 15), "Mua Sắm", 1500, TransactionType.EXPENSE));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2023, 10, 12, 11, 0, 0), "Đầu Tư", 3000, TransactionType.INCOME));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2023, 11, 15, 16, 30, 30), "Quỹ Du Lịch", 500, TransactionType.INCOME));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2023, 12, 18, 19, 15, 45), "Điện Nước", 400, TransactionType.EXPENSE));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2024, 1, 20, 13, 10, 0), "Xăng", 300, TransactionType.EXPENSE));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2024, 2, 22, 9, 0, 5), "Quỹ Hưu Trí", 2000, TransactionType.EXPENSE));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2024, 3, 25, 15, 40, 25), "Lương", 6000, TransactionType.INCOME));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2024, 4, 28, 17, 50, 10), "Bán Hàng", 4000, TransactionType.INCOME));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2024, 5, 1, 10, 5, 0), "Quỹ Khẩn Cấp", 1000, TransactionType.EXPENSE));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2024, 6, 3, 14, 25, 0), "Kinh Doanh", 8000, TransactionType.INCOME));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2024, 7, 5, 18, 35, 30), "Bảo Hiểm", 2500, TransactionType.EXPENSE));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2024, 8, 7, 12, 10, 0), "Ăn Uống", 500, TransactionType.EXPENSE));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2022, 9, 10, 8, 55, 20), "Du Lịch", 2000, TransactionType.EXPENSE));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2022, 10, 12, 15, 0, 0), "Đầu Tư", 5000, TransactionType.INCOME));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2022, 11, 15, 10, 20, 30), "Mua Sắm", 1200, TransactionType.EXPENSE));
        transactionTree.addTransaction(new Transaction(LocalDateTime.of(2022, 12, 18, 20, 15, 45), "Điện Nước", 350, TransactionType.EXPENSE));

        return transactionTree;
    }
}
