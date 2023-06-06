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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author Pedj
 */
public class LibUtils {

    public static boolean isNumeric(String str) {
        try {
            double num = Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Integer convertStringtoNum(String numString) {
        if (numString.matches("\\d+") && numString.matches("\\d+")) {
            Integer newNum = Integer.valueOf(numString);
            return newNum;

        }
        return null;
    }

    public static void numOnlyTextField(JTextField jTextField) {
        jTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                char keyChar = event.getKeyChar();
                if (!Character.isDigit(keyChar)) {
                    event.consume();
                }
            }
        });
    }

    public static void numOnlyTextField(JTextField jTextField, int charAmount) {
        ((AbstractDocument) jTextField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String str, AttributeSet attr) throws BadLocationException {
                if (str.matches("\\d") && (fb.getDocument().getLength() + str.length()) <= charAmount) {
                    super.insertString(fb, offset, str, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet attrs) throws BadLocationException {
                if (str.matches("\\d") && (fb.getDocument().getLength() + str.length() - length) <= charAmount) {
                    super.replace(fb, offset, length, str, attrs);
                }
            }
        });
//        Wasted my time reading the documentation about AbstractDocument and used regEx and still doesn't work.
    }

    public static double[] calculateTotalCost(String borrowDateString, String returnDateString, int cost) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date borrowDate = dateFormat.parse(borrowDateString);
            Date returnDate = dateFormat.parse(returnDateString);

            // Calculate the difference between borrowDate and returnDate in days
            long differenceInMillis = returnDate.getTime() - borrowDate.getTime();
            long daysDifference = TimeUnit.MILLISECONDS.toDays(differenceInMillis);

            // Calculate the base cost
            double baseCost = cost * daysDifference;

            // Calculate the added amount for late days
            double addedAmount = 0.0;
            if (daysDifference > 0) {
                addedAmount = cost * (daysDifference - 1); // Subtract 1 day for the borrow day
            }

            // Calculate the total cost
            double totalCost = baseCost + addedAmount;

            // Return the calculated cost, added amount, total cost, and the difference in days
            return new double[]{baseCost, addedAmount, totalCost, daysDifference};
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Please provide dates in the format yyyy-MM-dd.", e);
        }
    }
}
