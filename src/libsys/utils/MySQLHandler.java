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
package libsys.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Pedj
 */
public class MySQLHandler {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mylibraryapp";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    public static boolean checkConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            con.close();
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.print(e);
            return false;
        }
    }

//    This part is for dynamically handling SQL queries. Uses Function overloading.
    public static ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            statement = con.prepareStatement(sql);
            // Set parameter values, if any
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            resultSet = statement.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            if (resultSet != null) {
                resultSet.close();
            }
            throw e;
        }
    }

    public static boolean executeUpdate(String sql, Object... params) throws SQLException {
        Connection con = null;
        PreparedStatement statement = null;

        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            statement = con.prepareStatement(sql);
            // Set parameter values, if any
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }

    public static ResultSet executeQuery(String sql) throws SQLException {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            statement = con.prepareStatement(sql);
            resultSet = statement.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            if (resultSet != null) {
                resultSet.close();
            }
            throw e;
        }
    }
}
