package com.example.assignment.controller;

import com.example.assignment.data.LanguageManager;
import com.example.assignment.data.TransactionDataStore;
import com.example.assignment.model.Transaction;
import com.example.assignment.model.TransactionTree;
import com.example.assignment.model.TransactionType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DashBoardController {
    @FXML
    private Label totalIncomeLabel,totalExpenseLabel;

    @FXML
    private TitledPane incomePane,expensePane;

    @FXML
    private TableView<Transaction> incomeTable, expenseTable;

    @FXML
    private TableColumn<Transaction, LocalDateTime> incomeDateColumn, expenseDateColumn;

    @FXML
    private TableColumn<Transaction, String> incomeDescriptionColumn, expenseDescriptionColumn;

    @FXML
    private TableColumn<Transaction, Double> incomeAmountColumn, expenseAmountColumn;

    @FXML
    private PieChart transactionPieChart;

    @FXML
    private BarChart<String, Number> transactionBarChart;

    @FXML
    private TextField incomeSearchField, expenseSearchField;

    @FXML
    private TextField filterIncomeTextField;

    @FXML
    private TextField filterExpenseTextField;

    @FXML
    private Button filterExpenseButton,filterIncomeButton;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private Label lblPieChart,lblBarChart,dashboardTitle;

    private TransactionTree transactionTree;

    @FXML
    public void initialize() {
        transactionTree = TransactionDataStore.getTransactionTree();
        initializeTables();
        loadTableData();
        updateCharts();
        updateUI();

        LanguageManager.setOnLanguageChangeListener(this::updateUI);
        updateTotals();
    }

    private void initializeTables() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        incomeDateColumn.setCellFactory(column -> new TableCell<>() {
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
        expenseDateColumn.setCellFactory(column -> new TableCell<>() {
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
        incomeDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        incomeDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        incomeAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        expenseDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        expenseDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        expenseAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    private void loadTableData() {
        transactionTree.inorderTraversal(transactionTree.getRoot(), transaction -> {
            switch (transaction.getType()) {
                case INCOME -> incomeTable.getItems().add(transaction);
                case EXPENSE -> expenseTable.getItems().add(transaction);
            }
        });
    }

    private void updateCharts() {
        Map<Integer, Double> totalIncomeByYear = new HashMap<>();
        Map<Integer, Double> totalExpenseByYear = new HashMap<>();

        transactionTree.inorderTraversal(transactionTree.getRoot(), transaction -> {
            int year = transaction.getDate().getYear();
            switch (transaction.getType()) {
                case INCOME -> totalIncomeByYear.merge(year, transaction.getAmount(), Double::sum);
                case EXPENSE -> totalExpenseByYear.merge(year, transaction.getAmount(), Double::sum);
            }
        });

        double totalIncome = totalIncomeByYear.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalExpense = totalExpenseByYear.values().stream().mapToDouble(Double::doubleValue).sum();

        PieChart.Data incomeData = new PieChart.Data(LanguageManager.get("dashboard.incomeLabel"), totalIncome);
        PieChart.Data expenseData = new PieChart.Data(LanguageManager.get("dashboard.expenseLabel"), totalExpense);
        transactionPieChart.getData().clear();
        transactionPieChart.getData().addAll(incomeData, expenseData);

        Platform.runLater(() -> {
            incomeData.getNode().setStyle("-fx-pie-color: #27ae60;");
            expenseData.getNode().setStyle("-fx-pie-color: #c0392b;");
        });

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName(LanguageManager.get("dashboard.incomeLabel"));

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName(LanguageManager.get("dashboard.expenseLabel"));

        Set<Integer> allYears = new TreeSet<>();
        allYears.addAll(totalIncomeByYear.keySet());
        allYears.addAll(totalExpenseByYear.keySet());

        for (int year : allYears) {
            incomeSeries.getData().add(new XYChart.Data<>(String.valueOf(year), totalIncomeByYear.getOrDefault(year, 0.0)));
            expenseSeries.getData().add(new XYChart.Data<>(String.valueOf(year), totalExpenseByYear.getOrDefault(year, 0.0)));
        }

        transactionBarChart.getData().clear();
        transactionBarChart.getData().addAll(incomeSeries, expenseSeries);

        Platform.runLater(() -> {
            incomeSeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #27ae60;"));
            expenseSeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #c0392b;"));
        });
    }

    public void searchTransactions(KeyEvent event) {
        Object source = event.getSource();
        if (source == incomeSearchField) {
            String keyword = incomeSearchField.getText().trim();
            updateTableWithSearchResults(keyword, TransactionType.INCOME, incomeTable);
        } else if (source == expenseSearchField) {
            String keyword = expenseSearchField.getText().trim();
            updateTableWithSearchResults(keyword, TransactionType.EXPENSE, expenseTable);
        }
    }

    private void updateTableWithSearchResults(String keyword, TransactionType type, TableView<Transaction> tableView) {
        if (keyword.isEmpty()) {
            tableView.getItems().clear();
            transactionTree.inorderTraversal(transactionTree.getRoot(), transaction -> {
                if (transaction.getType() == type) {
                    tableView.getItems().add(transaction);
                }
            });
        } else {
            List<Transaction> searchResults = transactionTree.searchTransactions(keyword, type);
            tableView.getItems().clear();
            tableView.getItems().addAll(searchResults);
        }
    }

    public void onFilterIncomeByAmount() {
        String amountText = filterIncomeTextField.getText();
        filterTransactionsByAmount(amountText, TransactionType.INCOME, incomeTable);
    }

    public void onFilterExpenseByAmount() {
        String amountText = filterExpenseTextField.getText();
        filterTransactionsByAmount(amountText, TransactionType.EXPENSE, expenseTable);
    }

    private void filterTransactionsByAmount(String amountText, TransactionType transactionType, TableView<Transaction> table) {
        StringBuilder errors = new StringBuilder();
        if (amountText == null || amountText.trim().isEmpty()) {
            errors.append("- Vui lòng nhập số tiền để lọc.\n");
        }
        double maxAmount = 0;
        try {
            if (errors.isEmpty()) {
                maxAmount = Double.parseDouble(amountText);
                if (maxAmount < 0) {
                    errors.append("- Số tiền không được nhỏ hơn 0.\n");
                }
            }
        } catch (NumberFormatException e) {
            errors.append("- Vui lòng nhập một số tiền hợp lệ.\n");
        }
        if (!errors.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", errors.toString());
            return;
        }
        table.getItems().clear();
        double finalMaxAmount = maxAmount;
        transactionTree.inorderTraversal(transactionTree.getRoot(), transaction -> {
            if (transaction.getType() == transactionType && transaction.getAmount() <= finalMaxAmount) {
                table.getItems().add(transaction);
            }
        });
        showAlert(Alert.AlertType.INFORMATION, "Thành công",
                "Đã lọc thành công các giao dịch " +
                        (transactionType == TransactionType.INCOME ? "thu nhập" : "chi tiêu") +
                        " có số tiền <= " + maxAmount);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void updateTotals() {
        double totalIncome = incomeTable.getItems().stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = expenseTable.getItems().stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        Platform.runLater(() -> {
            totalIncomeLabel.setText(LanguageManager.get("dashboard.totalIncomeLabel") + String.format("%.2f", totalIncome));
            totalExpenseLabel.setText(LanguageManager.get("dashboard.totalExpenseLabel") + String.format("%.2f", totalExpense));
        });
    }

    private void updateUI() {
        dashboardTitle.setText(LanguageManager.get("dashboard.dashboardTitle"));
        incomePane.setText(LanguageManager.get("dashboard.incomePane"));
        incomeDateColumn.setText(LanguageManager.get("dashboard.incomeDateColumn"));
        incomeDescriptionColumn.setText(LanguageManager.get("dashboard.incomeDescriptionColumn"));
        incomeAmountColumn.setText(LanguageManager.get("dashboard.incomeAmountColumn"));

        expensePane.setText(LanguageManager.get("dashboard.expensePane"));
        expenseDateColumn.setText(LanguageManager.get("dashboard.expenseDateColumn"));
        expenseDescriptionColumn.setText(LanguageManager.get("dashboard.expenseDescriptionColumn"));
        expenseAmountColumn.setText(LanguageManager.get("dashboard.expenseAmountColumn"));

        filterIncomeButton.setText(LanguageManager.get("dashboard.filterIncomeButton"));
        filterExpenseButton.setText(LanguageManager.get("dashboard.filterExpenseButton"));

        incomeSearchField.setPromptText(LanguageManager.get("dashboard.incomeSearchField"));
        expenseSearchField.setPromptText(LanguageManager.get("dashboard.expenseSearchField"));

        filterIncomeTextField.setPromptText(LanguageManager.get("dashboard.filterIncomeTextField"));
        filterExpenseTextField.setPromptText(LanguageManager.get("dashboard.filterExpenseTextField"));

        lblPieChart.setText(LanguageManager.get("dashboard.lblPieChart"));
        lblBarChart.setText(LanguageManager.get("dashboard.lblBarChart"));
        xAxis.setLabel(LanguageManager.get("dashboard.xAxisLabel"));
        yAxis.setLabel(LanguageManager.get("dashboard.yAxisLabel"));
    }


}
