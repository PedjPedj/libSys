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
import java.util.Date;
import java.util.UUID;
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
import libsys.utils.MySQLHandler;
import libsys.utils.Styles;
import libsys.views.BNRWindow;

/**
 *
 * @author Pedj
 */
public class BNRWindowController {

    public static void loadBNRWindowTable1(JScrollPane scrollPane) {
        Color pColor = Styles.ctxColors("primary");
        Color sColor1 = Styles.ctxColors("secondary1");
        Color sColor2 = Styles.ctxColors("secondary2");
        String sql = "SELECT * FROM books";
        ResultSet res = null;

        DefaultTableModel tableModel = new DefaultTableModel();
        CustomFlatUITable catalogTable = new CustomFlatUITable(tableModel, pColor, Color.white, sColor1);
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        catalogTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting() && catalogTable.getSelectedRow() != -1) {
                int selectedViewRow = catalogTable.getSelectedRow();
                int selectedModelRow = catalogTable.convertRowIndexToModel(selectedViewRow);

                Object idObject = tableModel.getValueAt(selectedModelRow, 0);
                Object nameObject = tableModel.getValueAt(selectedModelRow, 1);
                BNRWindow.BNRIdField2.setText(idObject.toString());
                BNRWindow.BNRNameField.setText(nameObject.toString());
            }
        });

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        catalogTable.setRowSorter(sorter);

        JTextField filterField = BNRWindow.BNRSearchField;

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
            res = MySQLHandler.executeQuery(sql);
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

            scrollPane.setViewportView(catalogTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void refreshTableModel(JScrollPane jScrollPane) {
        BNRWindowController.loadBNRWindowTable1(jScrollPane);

    }

    public static void addToMySQLBorrow(String userId, String bookId, Integer quantity, Date borrowDate, Date returnDate, Boolean isReturned) throws SQLException {
        // Generate a new UUID
        String borrowID = UUID.randomUUID().toString();

        // Proceed with the rest of the code
        if (quantity <= 0) {
            quantity = 1;
        }
        String sqlInsert = "INSERT INTO borrowrecords (borrow_id, user_Id, book_id, quantity, borrow_date, return_date, isReturned) VALUES (?, ?, ?, ?, ?, ?, ?)";
        boolean success = MySQLHandler.executeUpdate(sqlInsert, borrowID, userId, bookId, quantity, borrowDate, returnDate, isReturned);

        if (success) {
            JOptionPane.showMessageDialog(null, "Record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTableModel(BNRWindow.jScrollPane1);
        } else {
            JOptionPane.showMessageDialog(null, "Failed to add record!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void updateToMySQLBorrow(String borrowID, String userId, String bookId, Integer quantity, Date borrowDate, Date returnDate, Boolean isReturned) throws SQLException {
        String sqlUpdate = "UPDATE borrowrecords SET user_Id = ?, book_id = ?, quantity = ?, borrow_date = ?, return_date = ?, isReturned = ? WHERE borrow_id = ?";

        int confirmResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to update the record?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmResult != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = MySQLHandler.executeUpdate(sqlUpdate, userId, bookId, quantity, borrowDate, returnDate, isReturned, borrowID);
        if (success) {
            JOptionPane.showMessageDialog(null, "Record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTableModel(BNRWindow.jScrollPane1);

            // Perform any necessary refresh or update operations
        } else {
            JOptionPane.showMessageDialog(null, "Failed to update the record!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
