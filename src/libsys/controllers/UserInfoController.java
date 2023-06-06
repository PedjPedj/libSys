/*
 * The MIT License
 *
 * Copyright 2023 Pedj.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package libsys.controllers;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import libsys.swing.CustomFlatUITable;
import libsys.utils.LibUtils;
import libsys.utils.MySQLHandler;
import libsys.utils.Styles;
import libsys.views.UserInfo;

public class UserInfoController {

    public static void loadUserInfoTable(JScrollPane scrollPane, String userId) {
        Color pColor = Styles.ctxColors("primary");
        Color sColor1 = Styles.ctxColors("secondary1");
        Color sColor2 = Styles.ctxColors("secondary2");
        String sql = "SELECT borrowRecords.borrow_id,  borrowRecords.book_id, books.name, borrowRecords.quantity, borrowRecords.return_date, borrowRecords.borrow_date, borrowRecords.isReturned FROM borrowRecords INNER JOIN books ON borrowRecords.book_id = books.id WHERE user_id = ?";
        ResultSet res = null;
        DefaultTableModel tableModel = new DefaultTableModel();
        CustomFlatUITable newTableModel = new CustomFlatUITable(tableModel, pColor, Color.white, sColor1);
        newTableModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        try {
            res = MySQLHandler.executeQuery(sql, userId);

            newTableModel.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
                if (!e.getValueIsAdjusting() && newTableModel.getSelectedRow() != -1) {
                    int selectedViewRow = newTableModel.getSelectedRow();
                    int selectedModelRow = newTableModel.convertRowIndexToModel(selectedViewRow);

                    String borrowID = (String) newTableModel.getValueAt(selectedViewRow, 0);
                    String bookID = (String) newTableModel.getValueAt(selectedViewRow, 1);
                    int bookQty = (int) newTableModel.getValueAt(selectedViewRow, 3);
                    String returnDate = newTableModel.getValueAt(selectedViewRow, 4).toString();
                    String borrowDate = newTableModel.getValueAt(selectedViewRow, 5).toString();
                    UserInfo.borrowIDString = borrowID;
                    UserInfo.bookIDString = bookID;
                    UserInfo.bookQtyInt = bookQty;
                    UserInfo.userBDLabel.setText(borrowDate);
                    UserInfo.userRDLabel.setText(returnDate);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = dateFormat.format(Calendar.getInstance().getTime());
                    UserInfo.userTDLabel.setText(currentDate.toString());
                    int cost = 20;

                    double[] costs = LibUtils.calculateTotalCost(borrowDate, returnDate, cost);
                    double bCost = costs[0];
                    double aCost = costs[1];
                    double tCost = costs[2];
                    UserInfo.userPaymentLabel.setText("Base Cost:    " + bCost);
                    UserInfo.userPaymentLabel1.setText("Added Amount: " + aCost);
                    UserInfo.userPaymentLabel2.setText("Total Amount: " + tCost);

                }
            });

            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
            newTableModel.setRowSorter(sorter);

            JTextField filterField = UserInfo.userSearchField;

            filterField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    filterTable();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    filterTable();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    filterTable();
                }

                private void filterTable() {
                    String text = filterField.getText();
                    if (text.trim().length() == 0) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                    }
                }
            });

            Vector<String> columnNames = new Vector<>();
            columnNames.add("Borrow ID");
            columnNames.add("Book ID");
            columnNames.add("Book Name");
            columnNames.add("Quantity");
            columnNames.add("Return Date");
            columnNames.add("Borrow Date");
            columnNames.add("isReturned");
            tableModel.setColumnIdentifiers(columnNames);

            while (res.next()) {
                Object[] row = new Object[7];
                row[0] = res.getString("borrow_id");
                row[1] = res.getString("book_id");
                row[2] = res.getString("name");
                row[3] = res.getInt("quantity");
                row[4] = res.getDate("return_date");
                row[5] = res.getDate("borrow_date");
                row[6] = res.getInt("isReturned") == 1 ? "true" : "false";
                tableModel.addRow(row);
            }

            scrollPane.setViewportView(newTableModel);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void refreshTableModel(JScrollPane scrollPane, String userId) {
        UserInfoController.loadUserInfoTable(scrollPane, userId);
    }

    public static void updateIsReturned(String borrowID) {
        int isReturned = 1;  // Set isReturned to 1

        String sqlSelect = "SELECT isReturned FROM borrowRecords WHERE borrow_id = ?";

        try {
            ResultSet resultSet = MySQLHandler.executeQuery(sqlSelect, borrowID);
            if (resultSet.next()) {
                int currentIsReturned = resultSet.getInt("isReturned");
                if (currentIsReturned == 1) {
                    JOptionPane.showMessageDialog(null, "Record is already paid.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    resultSet.close();
                    return;
                }
            }
            resultSet.close();

            String sqlUpdate = "UPDATE borrowRecords SET isReturned = ? WHERE borrow_id = ?";

            int confirmResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to update the record?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirmResult != JOptionPane.YES_OPTION) {
                return;
            }

            boolean success = MySQLHandler.executeUpdate(sqlUpdate, isReturned, borrowID);
            if (success) {
                JOptionPane.showMessageDialog(null, "Record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Perform any necessary refresh or update operations
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update the record!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
