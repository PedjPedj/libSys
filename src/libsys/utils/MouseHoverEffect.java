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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author Pedj
 */
public class MouseHoverEffect {

//    public static void setHoverEffect(Component component, Color hoverColor, Color originalColor) {
//        component.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                component.setBackground(hoverColor);
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                component.setBackground(originalColor);
//            }
//        });
//    }
    public static void setHoverEffect(Component component, Color hoverColor, Color originalColor, boolean propagateToChildren, boolean applyToBackground) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (applyToBackground) {
                    component.setBackground(hoverColor);
                } else {
                    component.setForeground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (applyToBackground) {
                    component.setBackground(originalColor);
                } else {
                    component.setForeground(originalColor);
                }
            }
        });

        if (propagateToChildren && component instanceof Container) {
            Component[] children = ((Container) component).getComponents();
            for (Component child : children) {
                setHoverEffect(child, hoverColor, originalColor, true, applyToBackground);
            }
        }
    }
}
