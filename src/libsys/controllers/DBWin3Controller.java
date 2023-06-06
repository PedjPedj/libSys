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
public class DBWin3Controller {

    public static void loadUserTable(JScrollPane scrollPane) {
        Color pColor = Styles.ctxColors("primary");
        Color sColor1 = Styles.ctxColors("secondary1");
        Color sColor2 = Styles.ctxColors("secondary2");
        String sql = "SELECT * FROM users";
        ResultSet res = null;
        DefaultTableModel tableModel = new DefaultTableModel();
        CustomFlatUITable catalogTable = new CustomFlatUITable(tableModel, pColor, Color.white, sColor1);
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        catalogTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting() && catalogTable.getSelectedRow() != -1) {
                int selectedViewRow = catalogTable.getSelectedRow();
                int selectedModelRow = catalogTable.convertRowIndexToModel(selectedViewRow);

//                Object idObject = tableModel.getValueAt(selectedModelRow, 0);
//                Dashboard.catalogIDField.setText(idObject.toString());
                Object idObject = tableModel.getValueAt(selectedViewRow, 0);
                Object nameObject = tableModel.getValueAt(selectedViewRow, 1);
                Object emailObject = tableModel.getValueAt(selectedViewRow, 2);
                Dashboard.USERidField.setText(idObject.toString());
                Dashboard.USERnameField.setText(nameObject.toString());
                Dashboard.USERemailField.setText(emailObject.toString());
            }
        });

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        catalogTable.setRowSorter(sorter);

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
        DBWin3Controller.loadUserTable(jScrollPane);

    }

    public static boolean userExists(String userID) {
        String sqlCheck = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        try (ResultSet resultSet = MySQLHandler.executeQuery(sqlCheck, userID)) {
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void addToMySQLUsers(String id, String name, String email) throws SQLException {
        String sqlCheckEmail = "SELECT COUNT(*) FROM users WHERE email = ?";
        String sqlCheckId = "SELECT COUNT(*) FROM users WHERE user_id = ?";

        if (id.isEmpty() || id == null) {
            id = UUID.randomUUID().toString();
        } else {
            // Display update confirmation dialog
            int updateConfirmResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to update the record?", "Update Confirmation", JOptionPane.YES_NO_OPTION);
            if (updateConfirmResult != JOptionPane.YES_OPTION) {
                return;
            }

            // Update existing record
            String sqlUpdate = "UPDATE users SET name = ?, email = ? WHERE user_id = ?";
            boolean success = MySQLHandler.executeUpdate(sqlUpdate, name, email, id);
            if (success) {
                JOptionPane.showMessageDialog(null, "Record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTableModel(Dashboard.jScrollPane3);
                return;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update record!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Display confirmation dialog
        int confirmResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to add the query?", "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirmResult == JOptionPane.YES_OPTION) {
            try {
                // Check if email already exists
                boolean emailExists = false;
                ResultSet emailResultSet = MySQLHandler.executeQuery(sqlCheckEmail, email);
                if (emailResultSet.next()) {
                    int count = emailResultSet.getInt(1);
                    emailExists = count > 0;
                }
                emailResultSet.close();

                if (emailExists) {
                    JOptionPane.showMessageDialog(null, "Email already exists in the database!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Check if id already exists
                    boolean idExists = false;
                    ResultSet idResultSet = MySQLHandler.executeQuery(sqlCheckId, id);
                    if (idResultSet.next()) {
                        int count = idResultSet.getInt(1);
                        idExists = count > 0;
                    }
                    idResultSet.close();

                    if (idExists) {
                        JOptionPane.showMessageDialog(null, "User ID already exists in the database!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        String sqlInsert = "INSERT INTO users (user_id, name, email) VALUES (?, ?, ?)";
                        boolean success = MySQLHandler.executeUpdate(sqlInsert, id, name, email);

                        if (success) {
                            JOptionPane.showMessageDialog(null, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            refreshTableModel(Dashboard.jScrollPane3);
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to add user!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

//    public static void addToMySQLUsers(String id, String name, String email) throws SQLException {
//        String sqlCheckEmail = "SELECT COUNT(*) FROM users WHERE email = ?";
//        String sqlCheckId = "SELECT COUNT(*) FROM users WHERE user_id = ?";
//
//        if (id.isEmpty() || id == null) {
//            id = UUID.randomUUID().toString();
//        } else {
//            // Update existing record
//            String sqlUpdate = "UPDATE users SET name = ?, email = ? WHERE user_id = ?";
//            boolean success = MySQLHandler.executeUpdate(sqlUpdate, name, email, id);
//            if (success) {
//                JOptionPane.showMessageDialog(null, "Record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
//                refreshTableModel(Dashboard.jScrollPane3);
//                return;
//            } else {
//                JOptionPane.showMessageDialog(null, "Failed to update record!", "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//        }
//
//        // Display confirmation dialog
//        int confirmResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to add the query?", "Confirmation", JOptionPane.YES_NO_OPTION);
//
//        if (confirmResult == JOptionPane.YES_OPTION) {
//            try {
//                // Check if email already exists
//                boolean emailExists = false;
//                ResultSet emailResultSet = MySQLHandler.executeQuery(sqlCheckEmail, email);
//                if (emailResultSet.next()) {
//                    int count = emailResultSet.getInt(1);
//                    emailExists = count > 0;
//                }
//                emailResultSet.close();
//
//                if (emailExists) {
//                    JOptionPane.showMessageDialog(null, "Email already exists in the database!", "Error", JOptionPane.ERROR_MESSAGE);
//                } else {
//                    // Check if id already exists
//                    boolean idExists = false;
//                    ResultSet idResultSet = MySQLHandler.executeQuery(sqlCheckId, id);
//                    if (idResultSet.next()) {
//                        int count = idResultSet.getInt(1);
//                        idExists = count > 0;
//                    }
//                    idResultSet.close();
//
//                    if (idExists) {
//                        JOptionPane.showMessageDialog(null, "User ID already exists in the database!", "Error", JOptionPane.ERROR_MESSAGE);
//                    } else {
//                        String sqlInsert = "INSERT INTO users (user_id, name, email) VALUES (?, ?, ?)";
//                        boolean success = MySQLHandler.executeUpdate(sqlInsert, id, name, email);
//
//                        if (success) {
//                            JOptionPane.showMessageDialog(null, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
//                            refreshTableModel(Dashboard.jScrollPane3);
//                        } else {
//                            JOptionPane.showMessageDialog(null, "Failed to add user!", "Error", JOptionPane.ERROR_MESSAGE);
//                        }
//                    }
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    public static void deleteToMySQLUsers(String user_Id) throws SQLException {
        String sqlDeleteBorrow = "DELETE FROM borrowrecords WHERE user_Id = ?";
        String sqlDeleteUser = "DELETE FROM users WHERE user_Id = ?";

        // Double confirmation dialog
        int confirmResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the user and associated records?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmResult != JOptionPane.YES_OPTION) {
            return;
        }

        // Delete associated borrow records
        boolean successBorrow = MySQLHandler.executeUpdate(sqlDeleteBorrow, user_Id);
        if (!successBorrow) {
            JOptionPane.showMessageDialog(null, "Failed to delete associated borrow records! \nUser probably has no associated records. \nProceeding to delete User.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Delete user record
        boolean successUser = MySQLHandler.executeUpdate(sqlDeleteUser, user_Id);
        if (successUser) {
            JOptionPane.showMessageDialog(null, "User and associated records deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTableModel(Dashboard.jScrollPane3);
        } else {
            JOptionPane.showMessageDialog(null, "Failed to delete user and associated records!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
