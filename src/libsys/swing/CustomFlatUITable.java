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
package libsys.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Pedj
 *
 */
public class CustomFlatUITable extends JTable {

    public CustomFlatUITable(DefaultTableModel tableModel, Color selectionBackgroundColor,
            Color selectionForegroundColor, Color gridColor) {
        super(tableModel);

        setUI(new BasicTableUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                LookAndFeel.installColorsAndFont(table, "Table.background", "Table.foreground", "Table.font");
                table.setShowGrid(false);
                table.setIntercellSpacing(new Dimension(0, 0));
                table.setRowHeight(30);
                table.setSelectionBackground(selectionBackgroundColor);
                table.setSelectionForeground(selectionForegroundColor);
                table.setGridColor(gridColor);
            }
        });

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        setDefaultRenderer(Object.class, renderer);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                setRowSelectionInterval(row, row);
            }
        });
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // Make all cells uneditable
    }

}

// Time wasted : 5 Hours
// Notes: I spent way too much time on this. I don't even know if half of this actually works as I wanted. lmao
