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

import java.awt.Color;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 *
 * @author Pedj
 */
public class TxtAlertHandler {

    private static Timer timer;

    public static JLabel setJLabel(JLabel jLabel, String msgType, String msg, boolean setBackground) {
        resetLabel(jLabel);
        msgType = msgType.toUpperCase();

        int fadeDuration = 5000;
        if (setBackground) {
            jLabel.setOpaque(true);
            switch (msgType) {
                case "ERROR" -> {
                    jLabel.setBackground(Color.RED);
                    jLabel.setForeground(Color.WHITE);
                }
                case "WARNING" -> {
                    jLabel.setBackground(Color.YELLOW);
                    jLabel.setForeground(Color.BLACK);
                }
                case "SUCCESS" -> {
                    jLabel.setBackground(Color.GREEN);
                    jLabel.setForeground(Color.BLACK);
                }
                default -> {
                    jLabel.setBackground(Color.YELLOW);
                    jLabel.setForeground(Color.BLACK);
                }
            }
        } else {
            switch (msgType) {
                case "ERROR" ->
                    jLabel.setForeground(Color.RED);
                case "WARNING" ->
                    jLabel.setForeground(Color.YELLOW);
                case "SUCCESS" ->
                    jLabel.setForeground(Color.GREEN);
                default ->
                    jLabel.setForeground(Color.BLACK);
            }
        }

        jLabel.setText(msg);
        fadeColors(jLabel, fadeDuration);
        return jLabel;
    }

//    Time wasted: 3 Hours.
//    Note: Animations are a pain in the ass.
    private static void fadeColors(JLabel jLabel, int fadeDuration) {
        timer = new Timer(30, null); // Initialize the Timer as a class-level variable
        timer.setInitialDelay(0);

        final AtomicInteger currentStep = new AtomicInteger(0);
        int totalSteps = fadeDuration / timer.getDelay();

        final Color initialBackground = jLabel.getBackground();
        final Color initialForeground = jLabel.getForeground();
        final Color targetColor = Color.WHITE;

        ActionListener fadeAction = new ActionListener() {
            private float alpha = 1.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                currentStep.incrementAndGet();
                if (currentStep.get() >= totalSteps) {
                    jLabel.setText("");
                    jLabel.setOpaque(false);
                    timer.stop();
                } else {
//                    Note: No idea what I'm doing here. This is the fuck around and find out part.
//                          Reading the Java Documentation is making me nauseassses or whatever that word is spelt. idc at this point.
                    alpha = 1.0f - ((float) currentStep.get() / (float) totalSteps);

                    int redBackground = (int) (initialBackground.getRed() * alpha + targetColor.getRed() * (1 - alpha));
                    int greenBackground = (int) (initialBackground.getGreen() * alpha + targetColor.getGreen() * (1 - alpha));
                    int blueBackground = (int) (initialBackground.getBlue() * alpha + targetColor.getBlue() * (1 - alpha));
                    Color fadedBackground = new Color(redBackground, greenBackground, blueBackground);

                    int redForeground = (int) (initialForeground.getRed() * alpha + targetColor.getRed() * (1 - alpha));
                    int greenForeground = (int) (initialForeground.getGreen() * alpha + targetColor.getGreen() * (1 - alpha));
                    int blueForeground = (int) (initialForeground.getBlue() * alpha + targetColor.getBlue() * (1 - alpha));
                    Color fadedForeground = new Color(redForeground, greenForeground, blueForeground);

                    jLabel.setBackground(fadedBackground);
                    jLabel.setForeground(fadedForeground);
                    jLabel.repaint();
                }
            }
        };

        timer.addActionListener(fadeAction);
        timer.start();
    }

    public static void resetLabel(JLabel jLabel) {
        resetLabel(jLabel, timer); // Invoke the overloaded resetLabel method with the Timer instance
    }

    public static void resetLabel(JLabel jLabel, Timer timer) {
        if (timer != null) { // Check if timer is not null before stopping it
            timer.stop();
        }
        jLabel.setText("");
        jLabel.setIcon(null);
        jLabel.setForeground(null);
        jLabel.setBackground(null);
    }

    public static Label setLabel(Label label, String type, String msg, Boolean setBackground) {

        if (setBackground) {
            switch (type) {
                case "ERROR" -> {
                    label.setBackground(Color.RED);
                    label.setForeground(Color.WHITE);

                }
                case "WARNING" -> {
                    label.setBackground(Color.YELLOW);
                    label.setForeground(Color.BLACK);
                }
                case "SUCCESS" -> {
                    label.setBackground(Color.GREEN);
                    label.setForeground(Color.BLACK);
                }
                default -> {
                    label.setBackground(Color.YELLOW);
                    label.setForeground(Color.BLACK);
                }
            }
        } else {
            switch (type) {
                case "ERROR" ->
                    label.setForeground(Color.RED);
                case "WARNING" ->
                    label.setForeground(Color.YELLOW);
                case "SUCCESS" ->
                    label.setForeground(Color.GREEN);
                default ->
                    label.setForeground(Color.BLACK);

            }

        }
        label.setText(msg);
        return label;

    }

}

// Notes: I hate this. I needed to overload it cuz I used AWT and not SWING on other parts of this project. They're apparently not the same component!
// too Lazy to refactor and check if things works as intended.
