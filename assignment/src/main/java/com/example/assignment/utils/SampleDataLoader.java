package com.example.assignment.utils;

import com.example.assignment.model.Transaction;
import com.example.assignment.model.TransactionTree;
import com.example.assignment.model.TransactionType;

import java.time.LocalDate;

public class SampleDataLoader {
    public static TransactionTree loadSampleData() {
        TransactionTree transactionTree = new TransactionTree();
        transactionTree.addTransaction(new Transaction(LocalDate.of(2024, 12, 1), "Lương", 2000, TransactionType.INCOME));
        transactionTree.addTransaction(new Transaction(LocalDate.of(2023, 12, 5), "Kinh Doanh", 5000, TransactionType.INCOME));
        transactionTree.addTransaction(new Transaction(LocalDate.of(2024, 12, 2), "Tiền Nhà", 7000, TransactionType.EXPENSE));
        transactionTree.addTransaction(new Transaction(LocalDate.of(2023, 12, 6), "Điện Nước", 200, TransactionType.EXPENSE));
        transactionTree.addTransaction(new Transaction(LocalDate.of(2024, 12, 10), "Quỹ Du Lịch", 300, TransactionType.SAVINGS));
        transactionTree.addTransaction(new Transaction(LocalDate.of(2023, 12, 15), "Quỹ Hưu Trí", 2000, TransactionType.SAVINGS));
        return transactionTree;
    }
}
