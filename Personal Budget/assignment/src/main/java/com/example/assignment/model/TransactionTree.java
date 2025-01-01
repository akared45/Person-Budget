package com.example.assignment.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TransactionTree {
    private TreeNode<Transaction> root;

    public TransactionTree() {
        this.root = null;
    }

    public TreeNode<Transaction> getRoot() {
        return root;
    }

    public void addTransaction(Transaction transaction) {
        root = insertRecursive(root, transaction);
    }

    private TreeNode<Transaction> insertRecursive(TreeNode<Transaction> current, Transaction transaction) {
        if (current == null) {
            return new TreeNode<>(transaction);
        }
        if (transaction.getType() == TransactionType.INCOME) {
            if (current.getLeft() == null) {
                current.setLeft(new TreeNode<>(transaction));
            } else {
                insertIncomeRecursive(current.getLeft(), transaction);
            }
        }
        else if (transaction.getType() == TransactionType.EXPENSE) {
            if (current.getRight() == null) {
                current.setRight(new TreeNode<>(transaction));
            } else {
                insertExpenseRecursive(current.getRight(), transaction);
            }
        }
        return current;
    }

    private void insertIncomeRecursive(TreeNode<Transaction> current, Transaction transaction) {
        if (transaction.getDate().isBefore(current.getData().getDate())) {
            if (current.getLeft() == null) {
                current.setLeft(new TreeNode<>(transaction));
            } else {
                insertIncomeRecursive(current.getLeft(), transaction);
            }
        } else {
            if (current.getRight() == null) {
                current.setRight(new TreeNode<>(transaction));
            } else {
                insertIncomeRecursive(current.getRight(), transaction);
            }
        }
    }

    private void insertExpenseRecursive(TreeNode<Transaction> current, Transaction transaction) {
        if (transaction.getDate().isBefore(current.getData().getDate())) {
            if (current.getLeft() == null) {
                current.setLeft(new TreeNode<>(transaction));
            } else {
                insertExpenseRecursive(current.getLeft(), transaction);
            }
        } else {
            if (current.getRight() == null) {
                current.setRight(new TreeNode<>(transaction));
            } else {
                insertExpenseRecursive(current.getRight(), transaction);
            }
        }
    }

    public List<Transaction> searchTransactions(String keyword, TransactionType type) {
        List<Transaction> results = new ArrayList<>();
        inorderSearchByType(root, keyword.toLowerCase(), type, results);
        return results;
    }

    private void inorderSearchByType(TreeNode<Transaction> node, String keyword, TransactionType type, List<Transaction> results) {
        if (node == null) {
            return;
        }
        Transaction transaction = node.getData();
        if (type == TransactionType.INCOME) {
            inorderSearchByType(node.getLeft(), keyword, type, results);
        }
        if (type == TransactionType.EXPENSE) {
            inorderSearchByType(node.getRight(), keyword, type, results);
        }
        if (transaction.getType() == type) {
            if (transaction.getDescription().toLowerCase().contains(keyword) ||
                    transaction.getDate().toString().contains(keyword) ||
                    Double.toString(transaction.getAmount()).contains(keyword)) {
                results.add(transaction);
            }
        }
    }

    public void inorderTraversal(TreeNode<Transaction> node, Consumer<Transaction> action) {
        if (node != null) {
            inorderTraversal(node.getLeft(), action);
            action.accept(node.getData());
            inorderTraversal(node.getRight(), action);
        }
    }

    public void removeTransaction(Transaction transaction) {
        root = removeRecursive(root, transaction);
    }

    private TreeNode<Transaction> removeRecursive(TreeNode<Transaction> current, Transaction transaction) {
        if (current == null) {
            return null;
        }
        if (transaction.getDate().isBefore(current.getData().getDate())) {
            current.setLeft(removeRecursive(current.getLeft(), transaction));
        } else if (transaction.getDate().isAfter(current.getData().getDate())) {
            current.setRight(removeRecursive(current.getRight(), transaction));
        } else {
            if (current.getLeft() == null && current.getRight() == null) {
                return null;
            }
            if (current.getLeft() == null) {
                return current.getRight();
            }
            if (current.getRight() == null) {
                return current.getLeft();
            }
            TreeNode<Transaction> smallestValue = findSmallestNode(current.getRight());
            current.setData(smallestValue.getData());
            current.setRight(removeRecursive(current.getRight(), smallestValue.getData()));
        }
        return current;
    }

    private TreeNode<Transaction> findSmallestNode(TreeNode<Transaction> current) {
        return current.getLeft() == null ? current : findSmallestNode(current.getLeft());
    }

}
