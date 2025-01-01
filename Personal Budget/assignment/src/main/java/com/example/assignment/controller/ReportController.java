package com.example.assignment.controller;

import com.example.assignment.data.LanguageManager;
import com.example.assignment.data.TransactionDataStore;
import com.example.assignment.model.Transaction;
import com.example.assignment.model.TransactionTree;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ReportController {
    @FXML
    private Label lblSelectMY;

    @FXML
    private TitledPane incomePane,expensePane;

    @FXML
    private Button onConfirmButton;

    @FXML
    private TableView<Transaction> incomeTable, expenseTable;

    @FXML
    private PieChart incomeChart, expenseChart;

    @FXML
    private BarChart<String, Number> combinedChart;

    @FXML
    private ComboBox<String> monthSelector;

    @FXML
    private ComboBox<String> yearSelector;

    @FXML
    private TableColumn<Transaction, LocalDateTime> incomeDateColumn, expenseDateColumn;

    @FXML
    private TableColumn<Transaction, String> incomeDescriptionColumn, expenseDescriptionColumn;

    @FXML
    private TableColumn<Transaction, Double> incomeAmountColumn, expenseAmountColumn;

    @FXML
    private CategoryAxis categoryAxis;

    @FXML
    private NumberAxis valueAxis;

    private TransactionTree transactionTree;

    @FXML
    public void initialize() {
        transactionTree = TransactionDataStore.getTransactionTree();
        initializeTables();
        loadTableData();
        loadSampleCharts();
        updateUI();
        LanguageManager.setOnLanguageChangeListener(this::updateUI);
    }

    @FXML
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

    private void loadSampleCharts() {
        incomeChart.getData().clear();
        expenseChart.getData().clear();

        final double[] totalIncome = {0};
        final double[] totalExpense = {0};

        String[] incomeColors = {"#1b5e20", "#388e3c", "#66bb6a", "#81c784", "#a5d6a7"};
        String[] expenseColors = {"#b71c1c", "#d32f2f", "#e57373", "#ef9a9a", "#ffcdd2"};

        final int[] incomeIndex = {0};
        final int[] expenseIndex = {0};

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
            }
        });
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tổng hợp");

        XYChart.Data<String, Number> barIncome = new XYChart.Data<>(LanguageManager.get("dashboard.incomeLabel"), totalIncome[0]);
        XYChart.Data<String, Number> barExpense = new XYChart.Data<>(LanguageManager.get("dashboard.expenseLabel"), totalExpense[0]);

        series.getData().addAll(barIncome, barExpense);
        combinedChart.getData().add(series);

        barIncome.getNode().setStyle("-fx-bar-fill: #27ae60;");
        barExpense.getNode().setStyle("-fx-bar-fill: #c0392b;");
    }

    @FXML
    private void onConfirm() {
        String selectedMonth = monthSelector.getValue();
        String selectedYear = yearSelector.getValue();

        incomeTable.getItems().clear();
        expenseTable.getItems().clear();

        incomeChart.getData().clear();
        expenseChart.getData().clear();
        combinedChart.getData().clear();

        if (selectedMonth == null && selectedYear == null) {
            System.out.println("Vui lòng chọn ít nhất Tháng hoặc Năm.");
            return;
        }
        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("January", 1);
        monthMap.put("February", 2);
        monthMap.put("March", 3);
        monthMap.put("April", 4);
        monthMap.put("May", 5);
        monthMap.put("June", 6);
        monthMap.put("July", 7);
        monthMap.put("August", 8);
        monthMap.put("September", 9);
        monthMap.put("October", 10);
        monthMap.put("November", 11);
        monthMap.put("December", 12);

        int month = selectedMonth != null ? monthMap.getOrDefault(selectedMonth, -1) : -1;
        int year = selectedYear != null ? Integer.parseInt(selectedYear) : -1;

        final double[] totalIncome = {0};
        final double[] totalExpense = {0};

        String[] incomeColors = {"#1b5e20", "#388e3c", "#66bb6a", "#81c784", "#a5d6a7"};
        String[] expenseColors = {"#b71c1c", "#d32f2f", "#e57373", "#ef9a9a", "#ffcdd2"};

        final int[] incomeIndex = {0};
        final int[] expenseIndex = {0};

        transactionTree.inorderTraversal(transactionTree.getRoot(), transaction -> {
            LocalDateTime dateTime = transaction.getDate();
            boolean matchesMonth = month == -1 || dateTime.getMonthValue() == month;
            boolean matchesYear = year == -1 || dateTime.getYear() == year;

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
                }
            }
        });
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tổng hợp");

        XYChart.Data<String, Number> barIncome = new XYChart.Data<>("Thu nhập", totalIncome[0]);
        XYChart.Data<String, Number> barExpense = new XYChart.Data<>("Chi tiêu", totalExpense[0]);

        series.getData().addAll(barIncome, barExpense);
        combinedChart.getData().add(series);

        barIncome.getNode().setStyle("-fx-bar-fill: #27ae60;");
        barExpense.getNode().setStyle("-fx-bar-fill: #c0392b;");
    }

    private void updateUI() {
        lblSelectMY.setText(LanguageManager.get("report.selectMonthYear"));
        monthSelector.setItems(FXCollections.observableArrayList(
                LanguageManager.get("month.january"),
                LanguageManager.get("month.february"),
                LanguageManager.get("month.march"),
                LanguageManager.get("month.april"),
                LanguageManager.get("month.may"),
                LanguageManager.get("month.june"),
                LanguageManager.get("month.july"),
                LanguageManager.get("month.august"),
                LanguageManager.get("month.september"),
                LanguageManager.get("month.october"),
                LanguageManager.get("month.november"),
                LanguageManager.get("month.december")
        ));

        monthSelector.setPromptText(LanguageManager.get("report.selectMonth"));
        yearSelector.setPromptText(LanguageManager.get("report.selectYear"));
        onConfirmButton.setText(LanguageManager.get("report.confirmButton"));

        incomePane.setText(LanguageManager.get("report.incomePane"));
        incomeDateColumn.setText(LanguageManager.get("report.incomeDateColumn"));
        incomeDescriptionColumn.setText(LanguageManager.get("report.incomeDescriptionColumn"));
        incomeAmountColumn.setText(LanguageManager.get("report.incomeAmountColumn"));

        expensePane.setText(LanguageManager.get("report.expensePane"));
        expenseDateColumn.setText(LanguageManager.get("report.expenseDateColumn"));
        expenseDescriptionColumn.setText(LanguageManager.get("report.expenseDescriptionColumn"));
        expenseAmountColumn.setText(LanguageManager.get("report.expenseAmountColumn"));

        incomeChart.setTitle(LanguageManager.get("report.incomeChartTitle"));
        expenseChart.setTitle(LanguageManager.get("report.expenseChartTitle"));
        combinedChart.setTitle(LanguageManager.get("report.combinedChartTitle"));

        categoryAxis.setLabel(LanguageManager.get("report.categoryAxis"));
        valueAxis.setLabel(LanguageManager.get("report.valueAxis"));
    }


}
