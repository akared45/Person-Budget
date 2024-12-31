package com.example.assignment.controller;

import com.example.assignment.data.TransactionDataStore;
import com.example.assignment.model.Transaction;
import com.example.assignment.model.TransactionTree;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class HomeController {

    @FXML
    private TableView<Transaction> incomeTable, expenseTable, savingsTable;

    @FXML
    private PieChart incomeChart, expenseChart, savingsChart;

    @FXML
    private BarChart<String, Number> combinedChart;

    @FXML
    private ComboBox<String> monthSelector;

    @FXML
    private ComboBox<String> yearSelector;

    @FXML
    private TableColumn<Transaction, LocalDate> incomeDateColumn, expenseDateColumn, savingsDateColumn;

    @FXML
    private TableColumn<Transaction, String> incomeDescriptionColumn, expenseDescriptionColumn, savingsDescriptionColumn;

    @FXML
    private TableColumn<Transaction, Double> incomeAmountColumn, expenseAmountColumn, savingsAmountColumn;

    private TransactionTree transactionTree;

    @FXML
    public void initialize() {
        transactionTree = TransactionDataStore.getTransactionTree();
        initializeTables();
        loadTableData();
        loadSampleCharts();
    }

    @FXML
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
    private void loadSampleCharts() {
        incomeChart.getData().clear();
        expenseChart.getData().clear();
        savingsChart.getData().clear();

        final double[] totalIncome = {0};
        final double[] totalExpense = {0};
        final double[] totalSavings = {0};

        String[] incomeColors = {"#1b5e20", "#388e3c", "#66bb6a", "#81c784", "#a5d6a7"};
        String[] expenseColors = {"#b71c1c", "#d32f2f", "#e57373", "#ef9a9a", "#ffcdd2"};
        String[] savingsColors = {"#0d47a1", "#1976d2", "#42a5f5", "#64b5f6", "#bbdefb"};

        final int[] incomeIndex = {0};
        final int[] expenseIndex = {0};
        final int[] savingsIndex = {0};

        transactionTree.inorderTraversal(transactionTree.getRoot(), transaction -> {
            switch (transaction.getType()) {
                case INCOME -> {
                    PieChart.Data incomeData = new PieChart.Data(transaction.getDescription(), transaction.getAmount());
                    incomeChart.getData().add(incomeData);
                    totalIncome[0] += transaction.getAmount();
                    String color = incomeColors[incomeIndex[0] % incomeColors.length];
                    incomeData.getNode().setStyle("-fx-pie-color: " + color + ";");
                    incomeIndex[0]++;
                }
                case EXPENSE -> {
                    PieChart.Data expenseData = new PieChart.Data(transaction.getDescription(), transaction.getAmount());
                    expenseChart.getData().add(expenseData);
                    totalExpense[0] += transaction.getAmount();
                    String color = expenseColors[expenseIndex[0] % expenseColors.length];
                    expenseData.getNode().setStyle("-fx-pie-color: " + color + ";");
                    expenseIndex[0]++;
                }
                case SAVINGS -> {
                    PieChart.Data savingsData = new PieChart.Data(transaction.getDescription(), transaction.getAmount());
                    savingsChart.getData().add(savingsData);
                    totalSavings[0] += transaction.getAmount();
                    String color = savingsColors[savingsIndex[0] % savingsColors.length];
                    savingsData.getNode().setStyle("-fx-pie-color: " + color + ";");
                    savingsIndex[0]++;
                }
            }
        });
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tổng hợp");

        XYChart.Data<String, Number> barIncome = new XYChart.Data<>("Thu nhập", totalIncome[0]);
        XYChart.Data<String, Number> barExpense = new XYChart.Data<>("Chi tiêu", totalExpense[0]);
        XYChart.Data<String, Number> barSavings = new XYChart.Data<>("Tiết kiệm", totalSavings[0]);

        series.getData().addAll(barIncome, barExpense, barSavings);
        combinedChart.getData().add(series);

        barIncome.getNode().setStyle("-fx-bar-fill: #27ae60;");
        barExpense.getNode().setStyle("-fx-bar-fill: #c0392b;");
        barSavings.getNode().setStyle("-fx-bar-fill: #2980b9;");
    }

    @FXML
    private void onConfirm() {
        String selectedMonth = monthSelector.getValue();
        String selectedYear = yearSelector.getValue();

        incomeTable.getItems().clear();
        expenseTable.getItems().clear();
        savingsTable.getItems().clear();

        incomeChart.getData().clear();
        expenseChart.getData().clear();
        savingsChart.getData().clear();
        combinedChart.getData().clear();

        if (selectedMonth == null && selectedYear == null) {
            System.out.println("Vui lòng chọn ít nhất Tháng hoặc Năm.");
            return;
        }
        int month = selectedMonth != null ? Integer.parseInt(selectedMonth.substring(0, 2)) : -1;
        int year = selectedYear != null ? Integer.parseInt(selectedYear) : -1;

        final double[] totalIncome = {0};
        final double[] totalExpense = {0};
        final double[] totalSavings = {0};

        String[] incomeColors = {"#1b5e20", "#388e3c", "#66bb6a", "#81c784", "#a5d6a7"};
        String[] expenseColors = {"#b71c1c", "#d32f2f", "#e57373", "#ef9a9a", "#ffcdd2"};
        String[] savingsColors = {"#0d47a1", "#1976d2", "#42a5f5", "#64b5f6", "#bbdefb"};

        final int[] incomeIndex = {0};
        final int[] expenseIndex = {0};
        final int[] savingsIndex = {0};

        transactionTree.inorderTraversal(transactionTree.getRoot(), transaction -> {
            LocalDate date = transaction.getDate();
            boolean matchesMonth = month == -1 || date.getMonthValue() == month;
            boolean matchesYear = year == -1 || date.getYear() == year;

            if (matchesMonth && matchesYear) {
                switch (transaction.getType()) {
                    case INCOME -> {
                        incomeTable.getItems().add(transaction);
                        PieChart.Data incomeData = new PieChart.Data(transaction.getDescription(), transaction.getAmount());
                        incomeChart.getData().add(incomeData);
                        totalIncome[0] += transaction.getAmount();
                        String color = incomeColors[incomeIndex[0] % incomeColors.length];
                        incomeData.getNode().setStyle("-fx-pie-color: " + color + ";");
                        incomeIndex[0]++;
                    }
                    case EXPENSE -> {
                        expenseTable.getItems().add(transaction);
                        PieChart.Data expenseData = new PieChart.Data(transaction.getDescription(), transaction.getAmount());
                        expenseChart.getData().add(expenseData);
                        totalExpense[0] += transaction.getAmount();
                        String color = expenseColors[expenseIndex[0] % expenseColors.length];
                        expenseData.getNode().setStyle("-fx-pie-color: " + color + ";");
                        expenseIndex[0]++;
                    }
                    case SAVINGS -> {
                        savingsTable.getItems().add(transaction);
                        PieChart.Data savingsData = new PieChart.Data(transaction.getDescription(), transaction.getAmount());
                        savingsChart.getData().add(savingsData);
                        totalSavings[0] += transaction.getAmount();
                        String color = savingsColors[savingsIndex[0] % savingsColors.length];
                        savingsData.getNode().setStyle("-fx-pie-color: " + color + ";");
                        savingsIndex[0]++;
                    }
                }
            }
        });
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tổng hợp");

        XYChart.Data<String, Number> barIncome = new XYChart.Data<>("Thu nhập", totalIncome[0]);
        XYChart.Data<String, Number> barExpense = new XYChart.Data<>("Chi tiêu", totalExpense[0]);
        XYChart.Data<String, Number> barSavings = new XYChart.Data<>("Tiết kiệm", totalSavings[0]);

        series.getData().addAll(barIncome, barExpense, barSavings);
        combinedChart.getData().add(series);

        barIncome.getNode().setStyle("-fx-bar-fill: #27ae60;");
        barExpense.getNode().setStyle("-fx-bar-fill: #c0392b;");
        barSavings.getNode().setStyle("-fx-bar-fill: #2980b9;");
    }

}
