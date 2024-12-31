package com.example.assignment.data;

import com.example.assignment.model.TransactionTree;

public class TransactionDataStore {
    private static TransactionTree transactionTree;

    public static TransactionTree getTransactionTree() {
        if (transactionTree == null) {
            transactionTree = SampleDataLoader.loadSampleData();
        }
        return transactionTree;
    }

    public static void setTransactionTree(TransactionTree tree) {
        transactionTree = tree;
    }
}
