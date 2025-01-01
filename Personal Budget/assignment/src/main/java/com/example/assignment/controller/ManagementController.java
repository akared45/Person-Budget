package com.example.assignment.controller;

import com.example.assignment.data.LanguageManager;
import com.example.assignment.data.TransactionDataStore;
import com.example.assignment.model.Transaction;
import com.example.assignment.model.TransactionTree;
import com.example.assignment.model.TransactionType;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ManagementController {
    @FXML
    private Label manageTitleLabel,sortCriteriaLabel,sortAlgorithmLabel,transactionDateLabel,transactionDescriptionLabel
                ,transactionAmountLabel,addTransactionTitleLabel,transactionTypeLabel,transactionListTitleLabel;
    @FXML
    private Button sortButton;

    @FXML
    private TableView<Transaction> transactionTable;

    @FXML
    private TableColumn<Transaction, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Transaction, String> descriptionColumn;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    @FXML
    private TableColumn<Transaction, TransactionType> typeColumn;

    @FXML
    private TableColumn<Transaction, Void> actionColumn;

    @FXML
    private DatePicker transactionDatePicker;

    @FXML
    private TextField transactionDescriptionField;

    @FXML
    private TextField transactionAmountField;

    @FXML
    private ChoiceBox<String> transactionTypeChoiceBox;

    @FXML
    private Button transactionButton;

    @FXML
    private ChoiceBox<String> sortCriteriaChoiceBox;

    @FXML
    private ChoiceBox<String> sortAlgorithmChoiceBox;

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
                        default -> setStyle("");
                    }
                }
            }
        });
        updateUI();
        LanguageManager.setOnLanguageChangeListener(this::updateUI);
    }

    private void initializeTables() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime dateTime, boolean empty) {
                super.updateItem(dateTime, empty);
                if (empty || dateTime == null) {
                    setText(null);
                } else {
                    setText(dateTime.toLocalDate().format(formatter));
                }
            }
        });
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button(LanguageManager.get("button.edit"));
            private final Button deleteButton = new Button(LanguageManager.get("button.delete"));
            private final HBox actionBox = new HBox(10, editButton, deleteButton);
            {
                editButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    onEditTransaction(transaction);
                });

                deleteButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    onDeleteTransaction(transaction);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });
    }

    private void loadTableData() {
        transactionTable.getItems().clear();
        transactionTree.inorderTraversal(transactionTree.getRoot(), transaction -> transactionTable.getItems().add(transaction));
    }

    public void onAddTransaction(ActionEvent actionEvent) {
        if (!validateInput()) {
            return;
        }
        try {
            LocalDate date = transactionDatePicker.getValue();
            LocalTime timeNow = LocalTime.now();
            LocalDateTime dateTime = LocalDateTime.of(date, timeNow);

            String description = transactionDescriptionField.getText();
            double amount = Double.parseDouble(transactionAmountField.getText());
            String typeString = transactionTypeChoiceBox.getValue();

            TransactionType type;
            if (typeString.equals(LanguageManager.get("dashboard.incomeLabel"))) {
                type = TransactionType.INCOME;
            } else if (typeString.equals(LanguageManager.get("dashboard.expenseLabel"))) {
                type = TransactionType.EXPENSE;
            } else {
                throw new IllegalArgumentException("Invalid transaction type: " + typeString);
            }

            Transaction transaction = new Transaction(dateTime, description, amount, type);
            transactionTree.addTransaction(transaction);
            transactionTable.getItems().add(transaction);

            clearForm();
            showAlert(Alert.AlertType.INFORMATION,
                    LanguageManager.get("alert.successTitle"),
                    LanguageManager.get("alert.transactionAddedSuccess"));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                    LanguageManager.get("alert.errorTitle"),
                    LanguageManager.get("alert.transactionAddError") + ": " + e.getMessage());
        }
    }

    private void onEditTransaction(Transaction transaction) {
        LocalDateTime dateTime = transaction.getDate();
        transactionDatePicker.setValue(dateTime.toLocalDate());
        transactionDescriptionField.setText(transaction.getDescription());
        transactionAmountField.setText(String.valueOf(transaction.getAmount()));
        transactionTypeChoiceBox.setValue(transaction.getType() == TransactionType.INCOME
                ? LanguageManager.get("dashboard.incomeLabel")
                : LanguageManager.get("dashboard.expenseLabel"));

        transactionTypeLabel.setText(LanguageManager.get("manage.editTransactionTitle"));
        transactionButton.setText(LanguageManager.get("manage.editTransactionButton"));
        transactionButton.setOnAction(event -> onSaveChanges(transaction));
    }

    private void onSaveChanges(Transaction transaction) {
        try {
            LocalDate newDate = transactionDatePicker.getValue();
            LocalTime originalTime = transaction.getDate().toLocalTime();
            LocalDateTime newDateTime = LocalDateTime.of(newDate, originalTime);

            String newDescription = transactionDescriptionField.getText();
            double newAmount = Double.parseDouble(transactionAmountField.getText());
            String newType = transactionTypeChoiceBox.getValue();
            TransactionType transactionType = newType.equals(LanguageManager.get("dashboard.incomeLabel"))
                    ? TransactionType.INCOME
                    : TransactionType.EXPENSE;
            transaction.setDate(newDateTime);
            transaction.setDescription(newDescription);
            transaction.setAmount(newAmount);
            transaction.setType(transactionType);
            transactionTable.refresh();
            clearForm();
            transactionTypeLabel.setText(LanguageManager.get("manage.addTransactionTitle"));
            transactionButton.setText(LanguageManager.get("manage.addTransactionButton"));
            transactionButton.setOnAction(this::onAddTransaction);
            showAlert(Alert.AlertType.INFORMATION,
                    LanguageManager.get("alert.successTitle"),
                    LanguageManager.get("alert.transactionUpdatedSuccess"));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                    LanguageManager.get("alert.errorTitle"),
                    LanguageManager.get("alert.transactionUpdateError") + ": " + e.getMessage());
        }
    }


    private void clearForm() {
        transactionDatePicker.setValue(null);
        transactionDescriptionField.clear();
        transactionAmountField.clear();
        transactionTypeChoiceBox.setValue(null);
    }

    private void onDeleteTransaction(Transaction transaction) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle(LanguageManager.get("alert.confirmationTitle"));
        confirmationAlert.setHeaderText(LanguageManager.get("alert.deleteTransactionHeader"));
        confirmationAlert.setContentText(LanguageManager.get("alert.deleteTransactionContent"));
        var result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            transactionTree.removeTransaction(transaction);
            transactionTable.getItems().remove(transaction);

            showAlert(Alert.AlertType.INFORMATION,
                    LanguageManager.get("alert.successTitle"),
                    LanguageManager.get("alert.transactionDeletedSuccess"));
        }

    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onSort() {
        String criteria = sortCriteriaChoiceBox.getValue();
        String algorithm = sortAlgorithmChoiceBox.getValue();

        if (criteria == null || algorithm == null) {
            System.out.println("Vui lòng chọn tiêu chí và thuật toán sắp xếp.");
            return;
        }
        if (criteria.equals(LanguageManager.get("sort.date"))) {
            switch (algorithm) {
                case "Bubble Sort" -> sortByDateBubble();
                case "Insertion Sort" -> sortByDateInsertion();
                case "Selection Sort" -> sortByDateSelection();
            }
        } else if (criteria.equals(LanguageManager.get("sort.description"))) {
            switch (algorithm) {
                case "Bubble Sort" -> sortByDescriptionBubble();
                case "Insertion Sort" -> sortByDescriptionInsertion();
                case "Selection Sort" -> sortByDescriptionSelection();
            }
        } else if (criteria.equals(LanguageManager.get("sort.amount"))) {
            switch (algorithm) {
                case "Bubble Sort" -> sortByAmountBubble();
                case "Insertion Sort" -> sortByAmountInsertion();
                case "Selection Sort" -> sortByAmountSelection();
            }
        }
    }

    private void sortByDateBubble() {
        var transactions = transactionTable.getItems();
        for (int i = 0; i < transactions.size() - 1; i++) {
            for (int j = 0; j < transactions.size() - i - 1; j++) {
                if (transactions.get(j).getDate().isAfter(transactions.get(j + 1).getDate())) {
                    Transaction temp = transactions.get(j);
                    transactions.set(j, transactions.get(j + 1));
                    transactions.set(j + 1, temp);
                }
            }
        }
        transactionTable.refresh();
    }

    private void sortByDateInsertion() {
        var transactions = transactionTable.getItems();
        for (int i = 1; i < transactions.size(); i++) {
            Transaction key = transactions.get(i);
            int j = i - 1;
            while (j >= 0 && transactions.get(j).getDate().isAfter(key.getDate())) {
                transactions.set(j + 1, transactions.get(j));
                j--;
            }
            transactions.set(j + 1, key);
        }
        transactionTable.refresh();
    }

    private void sortByDateSelection() {
        var transactions = transactionTable.getItems();
        for (int i = 0; i < transactions.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < transactions.size(); j++) {
                if (transactions.get(j).getDate().isBefore(transactions.get(minIndex).getDate())) {
                    minIndex = j;
                }
            }
            Transaction temp = transactions.get(minIndex);
            transactions.set(minIndex, transactions.get(i));
            transactions.set(i, temp);
        }
        transactionTable.refresh();
    }

    private void sortByDescriptionBubble() {
        var transactions = transactionTable.getItems();
        for (int i = 0; i < transactions.size() - 1; i++) {
            for (int j = 0; j < transactions.size() - i - 1; j++) {
                if (transactions.get(j).getDescription().compareToIgnoreCase(transactions.get(j + 1).getDescription()) > 0) {
                    Transaction temp = transactions.get(j);
                    transactions.set(j, transactions.get(j + 1));
                    transactions.set(j + 1, temp);
                }
            }
        }
        transactionTable.refresh();
    }

    private void sortByDescriptionInsertion() {
        var transactions = transactionTable.getItems();
        for (int i = 1; i < transactions.size(); i++) {
            Transaction key = transactions.get(i);
            int j = i - 1;
            while (j >= 0 && transactions.get(j).getDescription().compareToIgnoreCase(key.getDescription()) > 0) {
                transactions.set(j + 1, transactions.get(j));
                j--;
            }
            transactions.set(j + 1, key);
        }
        transactionTable.refresh();
    }

    private void sortByDescriptionSelection() {
        var transactions = transactionTable.getItems();
        for (int i = 0; i < transactions.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < transactions.size(); j++) {
                if (transactions.get(j).getDescription().compareToIgnoreCase(transactions.get(minIndex).getDescription()) < 0) {
                    minIndex = j;
                }
            }
            Transaction temp = transactions.get(minIndex);
            transactions.set(minIndex, transactions.get(i));
            transactions.set(i, temp);
        }
        transactionTable.refresh();
    }

    private void sortByAmountBubble() {
        var transactions = transactionTable.getItems();
        for (int i = 0; i < transactions.size() - 1; i++) {
            for (int j = 0; j < transactions.size() - i - 1; j++) {
                if (transactions.get(j).getAmount() > transactions.get(j + 1).getAmount()) {
                    Transaction temp = transactions.get(j);
                    transactions.set(j, transactions.get(j + 1));
                    transactions.set(j + 1, temp);
                }
            }
        }
        transactionTable.refresh();
    }

    private void sortByAmountInsertion() {
        var transactions = transactionTable.getItems();
        for (int i = 1; i < transactions.size(); i++) {
            Transaction key = transactions.get(i);
            int j = i - 1;
            while (j >= 0 && transactions.get(j).getAmount() > key.getAmount()) {
                transactions.set(j + 1, transactions.get(j));
                j--;
            }
            transactions.set(j + 1, key);
        }
        transactionTable.refresh();
    }

    private void sortByAmountSelection() {
        var transactions = transactionTable.getItems();
        for (int i = 0; i < transactions.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < transactions.size(); j++) {
                if (transactions.get(j).getAmount() < transactions.get(minIndex).getAmount()) {
                    minIndex = j;
                }
            }
            Transaction temp = transactions.get(minIndex);
            transactions.set(minIndex, transactions.get(i));
            transactions.set(i, temp);
        }
        transactionTable.refresh();
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        if (transactionDatePicker.getValue() == null) {
            errors.append("Ngày giao dịch không được để trống.\n");
        }
        if (transactionDescriptionField.getText().isEmpty()) {
            errors.append("Mô tả không được để trống.\n");
        }
        try {
            double amount = Double.parseDouble(transactionAmountField.getText());
            if (amount <= 0) {
                errors.append("Số tiền phải lớn hơn 0.\n");
            }
        } catch (NumberFormatException e) {
            errors.append("Số tiền không hợp lệ.\n");
        }
        if (transactionTypeChoiceBox.getValue() == null || transactionTypeChoiceBox.getValue().isEmpty()) {
            errors.append("Loại giao dịch không được để trống.\n");
        }
        if (!errors.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", errors.toString());
            return false;
        }
        return true;
    }

    public void updateUI() {
        transactionListTitleLabel.setText(LanguageManager.get("manage.transactionListTitle"));
        manageTitleLabel.setText(LanguageManager.get("manage.title"));
        addTransactionTitleLabel.setText(LanguageManager.get("manage.addTransactionTitle"));

        sortCriteriaLabel.setText(LanguageManager.get("manage.sortCriteria"));
        sortAlgorithmLabel.setText(LanguageManager.get("manage.sortAlgorithm"));
        transactionDateLabel.setText(LanguageManager.get("manage.transactionDate"));
        transactionDescriptionLabel.setText(LanguageManager.get("manage.transactionDescription"));
        transactionAmountLabel.setText(LanguageManager.get("manage.transactionAmount"));
        transactionTypeLabel.setText(LanguageManager.get("manage.transactionType"));

        sortButton.setText(LanguageManager.get("manage.sortButton"));
        if (!transactionButton.getText().equals(LanguageManager.get("manage.editTransactionButton"))) {
            transactionButton.setText(LanguageManager.get("manage.addTransactionButton"));
        }
        sortCriteriaChoiceBox.setItems(FXCollections.observableArrayList(
                LanguageManager.get("sort.date"),
                LanguageManager.get("sort.description"),
                LanguageManager.get("sort.amount")
        ));
        sortCriteriaChoiceBox.setValue(LanguageManager.get("sort.transactionListTitle"));
        dateColumn.setText(LanguageManager.get("manage.dateColumn"));
        descriptionColumn.setText(LanguageManager.get("manage.descriptionColumn"));
        amountColumn.setText(LanguageManager.get("manage.amountColumn"));
        typeColumn.setText(LanguageManager.get("manage.typeColumn"));
        actionColumn.setText(LanguageManager.get("manage.actionColumn"));

        transactionDescriptionField.setPromptText(LanguageManager.get("manage.transactionDescriptionField"));
        transactionAmountField.setPromptText(LanguageManager.get("manage.transactionAmountField"));

        transactionTypeChoiceBox.setItems(FXCollections.observableArrayList(
                LanguageManager.get("dashboard.incomeLabel"),
                LanguageManager.get("dashboard.expenseLabel")
        ));
    }

}
