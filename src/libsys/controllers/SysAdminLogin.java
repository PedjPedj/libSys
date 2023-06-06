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

import java.sql.ResultSet;
import java.sql.SQLException;
import libsys.utils.MySQLHandler;

/**
 *
 * @author Pedj
 */
public class SysAdminLogin {

    public static boolean loginManager(String username, String password) {
        // SQL query to check if a sysadmin with the provided username and password exists
        String sql = "SELECT COUNT(*) AS count FROM SysAdmins WHERE username = ? AND password = ?";
        ResultSet resultSet = null;

        try {
            // Retrieve the count value from the result set
            resultSet = MySQLHandler.executeQuery(sql, username, password);
            if (resultSet.next()) {
                // Retrieve the count value from the result set
                int count = resultSet.getInt("count");
                // Return true if the count is greater than 0
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        // Return false if an error occurred or no sysadmin with the provided credentials exists
        return false;
    }

}
