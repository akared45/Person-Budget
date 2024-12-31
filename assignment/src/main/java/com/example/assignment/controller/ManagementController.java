package com.example.assignment.controller;

import com.example.assignment.data.TransactionDataStore;
import com.example.assignment.model.Transaction;
import com.example.assignment.model.TransactionTree;
import com.example.assignment.model.TransactionType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class ManagementController {
    @FXML
    private TableView<Transaction> transactionTable;

    @FXML
    private TableColumn<Transaction, LocalDate> dateColumn;

    @FXML
    private TableColumn<Transaction, String> descriptionColumn;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    @FXML
    private TableColumn<Transaction, TransactionType> typeColumn;

    @FXML
    private DatePicker transactionDatePicker;

    @FXML
    private TextField transactionDescriptionField;

    @FXML
    private TextField transactionAmountField;

    @FXML
    private ChoiceBox<String> transactionTypeChoiceBox;

    private TransactionTree transactionTree;

    @FXML
    public void initialize() {
        transactionTree = TransactionDataStore.getTransactionTree();
        initializeTables();
        loadTableData();
        transactionTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Transaction transaction, boolean empty) {
                super.updateItem(transaction, empty);
                if (transaction == null || empty) {
                    setStyle("");
                } else {
                    switch (transaction.getType()) {
                        case INCOME -> setStyle("-fx-background-color: #5ff55e;");
                        case EXPENSE -> setStyle("-fx-background-color: #ed6257;");
                        case SAVINGS -> setStyle("-fx-background-color: #55abeb;");
                        default -> setStyle("");
                    }
                }
            }
        });
    }
    private void initializeTables() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
    }
    private void loadTableData() {
        transactionTable.getItems().clear();
        transactionTree.inorderTraversal(transactionTree.getRoot(), transaction -> {
            transactionTable.getItems().add(transaction);
        });
    }

    public void onAddTransaction(ActionEvent actionEvent) {
        LocalDate date = transactionDatePicker.getValue();
        String description = transactionDescriptionField.getText();
        double amount = Double.parseDouble(transactionAmountField.getText());
        String typeString = transactionTypeChoiceBox.getValue();

        TransactionType type = switch (typeString) {
            case "Thu nhập" -> TransactionType.INCOME;
            case "Chi tiêu" -> TransactionType.EXPENSE;
            case "Tiết kiệm" -> TransactionType.SAVINGS;
            default -> throw new IllegalArgumentException("Invalid transaction type");
        };
        Transaction transaction = new Transaction(date, description, amount, type);
        transactionTree.addTransaction(transaction);
        transactionTable.getItems().add(transaction);

    }
}
