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
import libsys.views.Dashboard;

/**
 *
 * @author Pedj
 */
public class DBWin2Controller {

    public static void loadBNRTable(JScrollPane scrollPane) {
        Color pColor = Styles.ctxColors("primary");
        Color sColor1 = Styles.ctxColors("secondary1");
        Color sColor2 = Styles.ctxColors("secondary2");
//        String sql = "SELECT * FROM borrowRecords";
        String sqlJoined = "SELECT borrowRecords.borrow_id, borrowRecords.user_id, borrowRecords.book_id, books.name, borrowRecords.quantity, borrowRecords.borrow_date, borrowRecords.return_date FROM borrowRecords INNER JOIN books ON borrowRecords.book_id = books.id";

        ResultSet res = null;
        DefaultTableModel tableModel = new DefaultTableModel();
        CustomFlatUITable newTableModel = new CustomFlatUITable(tableModel, pColor, Color.white, sColor1);
        newTableModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        newTableModel.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting() && newTableModel.getSelectedRow() != -1) {
                int selectedViewRow = newTableModel.getSelectedRow();
                int selectedModelRow = newTableModel.convertRowIndexToModel(selectedViewRow);
                Object bIdObject = tableModel.getValueAt(selectedViewRow, 0);
                Object idObject = tableModel.getValueAt(selectedViewRow, 1);
                Object bookIdObject = tableModel.getValueAt(selectedViewRow, 2);
                Object bookNameObject = tableModel.getValueAt(selectedViewRow, 3);
                Object bookQtyObject = tableModel.getValueAt(selectedViewRow, 4);
                Object bookBDate = tableModel.getValueAt(selectedViewRow, 5);
                Object bookRDate = tableModel.getValueAt(selectedViewRow, 6);

                Dashboard.BNRBorrowID = bIdObject.toString();
                Dashboard.BNRUserIdField1.setText(idObject.toString());
                Dashboard.BNRBookIdField2.setText(bookIdObject.toString());
                Dashboard.BNRNameField.setText(bookNameObject.toString());
                Dashboard.BNRQtyField.setText(bookQtyObject.toString());

                int cost = 20;
//                int multiplier = (int) bookQtyObject;

                double[] costs = LibUtils.calculateTotalCost(bookBDate.toString(), bookRDate.toString(), cost);
                double cCost = costs[0];
                double xCost = costs[1];
                double tCost = costs[2];
                Dashboard.BNRAmountField.setText("P " + tCost + " = " + xCost + " + " + cCost);

            }
        });

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        newTableModel.setRowSorter(sorter);

        JTextField filterField = Dashboard.BNRSearchField2;

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

        try {
            res = MySQLHandler.executeQuery(sqlJoined);
            int columnCount = res.getMetaData().getColumnCount();
            Vector<String> columnNames = new Vector<>();

            // Retrieve column names from ResultSet metadata
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(res.getMetaData().getColumnLabel(i));
            }
            tableModel.setColumnIdentifiers(columnNames);

            while (res.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = res.getObject(i);
                }
                tableModel.addRow(row);
            }

            scrollPane.setViewportView(newTableModel);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void refreshTableModel(JScrollPane jScrollPane) {
        DBWin1Controller.loadCatalogTable(jScrollPane);
    }

    public static void deleteFromBorrowRecords(String borrowID) {
        String sqlDelete = "DELETE FROM borrowRecords WHERE borrow_id = ?";

        // Double confirmation dialog
        int confirmResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected record?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmResult != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean success = MySQLHandler.executeUpdate(sqlDelete, borrowID);
            if (success) {
                JOptionPane.showMessageDialog(null, "Record deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTableModel(Dashboard.jScrollPane2);
                // Perform any necessary refresh or update operations
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete the record!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
