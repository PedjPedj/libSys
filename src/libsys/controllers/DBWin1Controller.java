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
import libsys.views.Dashboard;

/**
 *
 * @author Pedj
 */
public class DBWin1Controller {

    public static void loadCatalogTable(JScrollPane scrollPane) {
        Color pColor = Styles.ctxColors("primary");
        Color sColor1 = Styles.ctxColors("secondary1");
        Color sColor2 = Styles.ctxColors("secondary2");
        String sql = "SELECT * FROM books";
        ResultSet res = null;
        DefaultTableModel tableModel = new DefaultTableModel();
        CustomFlatUITable newTableModel = new CustomFlatUITable(tableModel, pColor, Color.white, sColor1);
        newTableModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        newTableModel.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting() && newTableModel.getSelectedRow() != -1) {
                int selectedViewRow = newTableModel.getSelectedRow();
                int selectedModelRow = newTableModel.convertRowIndexToModel(selectedViewRow);

                Object idObject = tableModel.getValueAt(selectedModelRow, 0);
                Object nameObject = tableModel.getValueAt(selectedModelRow, 1);
                Object authObject = tableModel.getValueAt(selectedModelRow, 2);
                Object qtyObject = tableModel.getValueAt(selectedModelRow, 3);
                Object pubObject = tableModel.getValueAt(selectedModelRow, 4);

                Dashboard.catalogIDField.setText(idObject.toString());
                Dashboard.bookNameField.setText(nameObject.toString());
                Dashboard.bookAuthorField.setText(authObject.toString());
                Dashboard.bookQtyField.setText(qtyObject.toString());
                Dashboard.bookPubField.setText(pubObject.toString());
            }
        });

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        newTableModel.setRowSorter(sorter);

        JTextField filterField = Dashboard.bookSearchField;

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

            scrollPane.setViewportView(newTableModel);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void refreshTableModel(JScrollPane jScrollPane) {
        DBWin1Controller.loadCatalogTable(jScrollPane);
    }

    public static void addToMySQLBooks(String id, String name, String author, Integer quantity, Integer publication) {
        String sqlCheck = "SELECT COUNT(*) FROM books WHERE id = ?";
        if (id.isEmpty() || id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }

        // Display confirmation dialog
        int confirmResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to add the query?", "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirmResult == JOptionPane.YES_OPTION) {
            try {
                // Check if id already exists
                boolean idExists = false;
                ResultSet resultSet = MySQLHandler.executeQuery(sqlCheck, id);
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    idExists = count > 0;
                }
                resultSet.close();

                if (idExists) {
                    JOptionPane.showMessageDialog(null, "ID already exists in the database!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String sqlInsert = "INSERT INTO books (id, name, author, quantity, publication) VALUES (?, ?, ?, ?, ?)";
                    boolean success = MySQLHandler.executeUpdate(sqlInsert, id, name, author, quantity, publication);

                    if (success) {
                        JOptionPane.showMessageDialog(null, "Query added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshTableModel(Dashboard.jScrollPane1);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add query!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteFromMySQLBooks(String id) {
        String sql = "DELETE FROM books WHERE id = ?";

        try {
            // Confirmation dialog
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the data?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            boolean success = MySQLHandler.executeUpdate(sql, id);
            if (success) {
                refreshTableModel(Dashboard.jScrollPane1);
                JOptionPane.showMessageDialog(null, "Data deleted successfully!", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete data!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public static void deleteFromMySQLBooks(String id) {
//        String sql = "DELETE FROM books WHERE id = ?";
//
//        try {
//            boolean success = MySQLHandler.executeUpdate(sql, id);
//            if (success) {
//                refreshTableModel(Dashboard.jScrollPane1);
//                JOptionPane.showMessageDialog(null, "Data deleted successfully!", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE);
//            } else {
//                JOptionPane.showMessageDialog(null, "Failed to delete data!", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    public static void updateToMySQLBooks(String id, String name, String author, Integer quantity, Integer publication) {
        String sqlCheck = "SELECT COUNT(*) FROM books WHERE id = ?";

        try {
            // Check if id already exists
            boolean idExists = false;
            ResultSet resultSet = MySQLHandler.executeQuery(sqlCheck, id);
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                idExists = count > 0;
            }
            resultSet.close();

            if (!idExists) {
                JOptionPane.showMessageDialog(null, "ID does not exist in the database!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirmation dialog
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to update the query?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            String sqlUpdate = "UPDATE books SET name = ?, author = ?, quantity = ?, publication = ? WHERE id = ?";
            boolean success = MySQLHandler.executeUpdate(sqlUpdate, name, author, quantity, publication, id);

            if (success) {
                JOptionPane.showMessageDialog(null, "Query updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTableModel(Dashboard.jScrollPane1);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update query!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public static void updateToMySQLBooks(String id, String name, String author, Integer quantity, Integer publication) {
//        String sqlCheck = "SELECT COUNT(*) FROM books WHERE id = ?";
//
//        try {
//            // Check if id already exists
//            boolean idExists = false;
//            ResultSet resultSet = MySQLHandler.executeQuery(sqlCheck, id);
//            if (resultSet.next()) {
//                int count = resultSet.getInt(1);
//                idExists = count > 0;
//            }
//            resultSet.close();
//
//            if (!idExists) {
//                JOptionPane.showMessageDialog(null, "ID does not exist in the database!", "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            String sqlUpdate = "UPDATE books SET name = ?, author = ?, quantity = ?, publication = ? WHERE id = ?";
//            boolean success = MySQLHandler.executeUpdate(sqlUpdate, name, author, quantity, publication, id);
//
//            if (success) {
//                JOptionPane.showMessageDialog(null, "Query updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
//                refreshTableModel(Dashboard.jScrollPane1);
//            } else {
//                JOptionPane.showMessageDialog(null, "Failed to update query!", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}
