package com.example.assignment.controller;

import com.example.assignment.model.Transaction;
import com.example.assignment.model.TransactionTree;
import com.example.assignment.model.TransactionType;
import com.example.assignment.utils.SampleDataLoader;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.time.LocalDate;
import java.util.*;

public class ReportController {
    @FXML
    private TableView<Transaction> incomeTable, expenseTable, savingsTable;

    @FXML
    private TableColumn<Transaction, LocalDate> incomeDateColumn, expenseDateColumn, savingsDateColumn;

    @FXML
    private TableColumn<Transaction, String> incomeDescriptionColumn, expenseDescriptionColumn, savingsDescriptionColumn;

    @FXML
    private TableColumn<Transaction, Double> incomeAmountColumn, expenseAmountColumn, savingsAmountColumn;

    @FXML
    private PieChart transactionPieChart;

    @FXML
    private BarChart<String, Number> transactionBarChart;

    @FXML
    private TextField incomeSearchField, expenseSearchField, savingsSearchField;

    private TransactionTree transactionTree;

    @FXML
    public void initialize() {
        transactionTree = SampleDataLoader.loadSampleData();
        initializeTables();
        loadTableData();
        updateCharts();
    }

    private void initializeTables() {
        incomeDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        incomeDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        incomeAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        expenseDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        expenseDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        expenseAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        savingsDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        savingsDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        savingsAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    private void loadTableData() {
        transactionTree.inorderTraversal(transactionTree.getRoot(), transaction -> {
            switch (transaction.getType()) {
                case INCOME -> incomeTable.getItems().add(transaction);
                case EXPENSE -> expenseTable.getItems().add(transaction);
                case SAVINGS -> savingsTable.getItems().add(transaction);
            }
        });
    }

    private void updateCharts() {
        transactionPieChart.getData().clear();
        transactionBarChart.getData().clear();

        Map<Integer, Double> totalIncomeByYear = new HashMap<>();
        Map<Integer, Double> totalExpenseByYear = new HashMap<>();
        Map<Integer, Double> totalSavingsByYear = new HashMap<>();

        transactionTree.inorderTraversal(transactionTree.getRoot(), transaction -> {
            int year = transaction.getDate().getYear();
            switch (transaction.getType()) {
                case INCOME -> totalIncomeByYear.merge(year, transaction.getAmount(), Double::sum);
                case EXPENSE -> totalExpenseByYear.merge(year, transaction.getAmount(), Double::sum);
                case SAVINGS -> totalSavingsByYear.merge(year, transaction.getAmount(), Double::sum);
            }
        });

        int currentYear = LocalDate.now().getYear();
        double totalIncome = totalIncomeByYear.getOrDefault(currentYear, 0.0);
        double totalExpense = totalExpenseByYear.getOrDefault(currentYear, 0.0);
        double totalSavings = totalSavingsByYear.getOrDefault(currentYear, 0.0);

        PieChart.Data incomeData = new PieChart.Data("Thu nhập", totalIncome);
        PieChart.Data expenseData = new PieChart.Data("Chi tiêu", totalExpense);
        PieChart.Data savingsData = new PieChart.Data("Tiết kiệm", totalSavings);

        transactionPieChart.getData().addAll(incomeData, expenseData, savingsData);

        incomeData.getNode().setStyle("-fx-pie-color: #27ae60;");
        expenseData.getNode().setStyle("-fx-pie-color: #c0392b;");
        savingsData.getNode().setStyle("-fx-pie-color: #2980b9;");

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Thu nhập");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Chi tiêu");

        XYChart.Series<String, Number> savingsSeries = new XYChart.Series<>();
        savingsSeries.setName("Tiết kiệm");

        Set<Integer> allYears = new TreeSet<>();
        allYears.addAll(totalIncomeByYear.keySet());
        allYears.addAll(totalExpenseByYear.keySet());
        allYears.addAll(totalSavingsByYear.keySet());

        for (int year : allYears) {
            incomeSeries.getData().add(new XYChart.Data<>(String.valueOf(year), totalIncomeByYear.getOrDefault(year, 0.0)));
            expenseSeries.getData().add(new XYChart.Data<>(String.valueOf(year), totalExpenseByYear.getOrDefault(year, 0.0)));
            savingsSeries.getData().add(new XYChart.Data<>(String.valueOf(year), totalSavingsByYear.getOrDefault(year, 0.0)));
        }

        transactionBarChart.getData().addAll(incomeSeries, expenseSeries, savingsSeries);
        incomeSeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #27ae60;"));
        expenseSeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #c0392b;"));
        savingsSeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #2980b9;"));
    }

    @FXML
    public void searchTransactions(KeyEvent event) {
        Object source = event.getSource();
        if (source == incomeSearchField) {
            String keyword = incomeSearchField.getText().trim();
            updateTableWithSearchResults(keyword, TransactionType.INCOME, incomeTable);
        } else if (source == expenseSearchField) {
            String keyword = expenseSearchField.getText().trim();
            updateTableWithSearchResults(keyword, TransactionType.EXPENSE, expenseTable);
        } else if (source == savingsSearchField) {
            String keyword = savingsSearchField.getText().trim();
            updateTableWithSearchResults(keyword, TransactionType.SAVINGS, savingsTable);
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

}
