import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Objects;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;

public class Calculator {
    int boardWidth = 360;
    int boardHeight = 540;

    Color customLightGray = new Color(212, 212, 210);
    Color customDarkGray = new Color(80, 80, 80);
    Color customBlack = new Color(28, 28, 28);
    Color customOrange = new Color(255, 149, 0);

    String[] buttonValues = {
            "AC", "+/-", "%", "÷",
            "7", "8", "9", "x",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "√", "="
    };
    String[] rightSymbols = { "÷", "x", "-", "+", "=" };
    String[] topSymbols = { "AC", "+/-", "%" };

    JFrame frame = new JFrame("Calculator");
    JLabel displayLabel = new JLabel();
    JPanel displayPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();

    String A = "0";
    String operator = null;
    String B = null;

    Calculator() {
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame.setUndecorated(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(customBlack);
        titleBar.setPreferredSize(new Dimension(boardWidth, 30));

        JPanel windowControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 7));
        windowControls.setBackground(customBlack);

        JButton closeButton = createWindowButton(new Color(255, 96, 92));
        JButton minimizeButton = createWindowButton(new Color(255, 189, 68));
        JButton maximizeButton = createWindowButton(new Color(39, 200, 64));

        closeButton.addActionListener(e -> System.exit(0));
        minimizeButton.addActionListener(e -> frame.setState(Frame.ICONIFIED));
        maximizeButton.addActionListener(e -> toggleMaximize());

        windowControls.add(closeButton);
        windowControls.add(minimizeButton);
        windowControls.add(maximizeButton);

        JLabel titleLabel = new JLabel("CALCULATOR");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        titleBar.add(windowControls, BorderLayout.WEST);
        titleBar.add(titleLabel, BorderLayout.EAST);

        Point dragStart = new Point();

        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragStart.setLocation(e.getPoint());
            }
        });

        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if ((frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0) {
                    frame.setExtendedState(frame.getExtendedState() & ~JFrame.MAXIMIZED_BOTH);
                }
                Point current = e.getLocationOnScreen();
                frame.setLocation(
                        current.x - dragStart.x,
                        current.y - dragStart.y
                );
            }
        });

        displayLabel.setBackground(customBlack);
        displayLabel.setForeground(Color.WHITE);
        displayLabel.setFont(new Font("Arial", Font.PLAIN, 80));
        displayLabel.setHorizontalAlignment(JLabel.RIGHT);
        displayLabel.setText("0");
        displayLabel.setOpaque(true);
        displayLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        displayPanel.setLayout(new BorderLayout());
        displayPanel.setBackground(customBlack);
        displayPanel.add(displayLabel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(customBlack);
        topPanel.add(titleBar, BorderLayout.NORTH);
        topPanel.add(displayPanel, BorderLayout.CENTER);

        frame.add(topPanel, BorderLayout.NORTH);

        buttonsPanel.setLayout(new GridLayout(5, 4));
        buttonsPanel.setBackground(customBlack);
        frame.add(buttonsPanel);

        for (String value : buttonValues) {
            JButton button = getjButton(value);
            buttonsPanel.add(button);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JButton button = (JButton) e.getSource();
                    String buttonValue = button.getText();
                    if (Arrays.asList(rightSymbols).contains(buttonValue)) {
                        if (Objects.equals(buttonValue, "=")) {
                            if (A != null) {
                                B = displayLabel.getText();
                                double numA = Double.parseDouble(A);
                                double numB = Double.parseDouble(B);

                                if (Objects.equals(operator, "+")) {
                                    displayLabel.setText(removeZeroDecimal(numA + numB));
                                } else if (Objects.equals(operator, "-")) {
                                    displayLabel.setText(removeZeroDecimal(numA - numB));
                                } else if (Objects.equals(operator, "x")) {
                                    displayLabel.setText(removeZeroDecimal(numA * numB));
                                } else if (Objects.equals(operator, "÷")) {
                                    displayLabel.setText(removeZeroDecimal(numA / numB));
                                }
                            }
                        } else if ("+-÷x".contains(buttonValue)) {
                            if (operator == null) {
                                A = displayLabel.getText();
                                displayLabel.setText("0");
                                B = "0";
                            }
                            operator = buttonValue;
                        }
                    } else if (Arrays.asList(topSymbols).contains(buttonValue)) {
                        if (Objects.equals(buttonValue, "AC")) {
                            clearAll();
                            displayLabel.setText("0");
                        } else if (Objects.equals(buttonValue, "+/-")) {
                            double numDisplay = Double.parseDouble(displayLabel.getText());
                            numDisplay *= -1;
                            displayLabel.setText(removeZeroDecimal(numDisplay));
                        } else if (Objects.equals(buttonValue, "%")) {
                            double numDisplay = Double.parseDouble(displayLabel.getText());
                            numDisplay /= 100;
                            displayLabel.setText(removeZeroDecimal(numDisplay));
                        }
                    } else {
                        if (Objects.equals(buttonValue, ".")) {
                            if (!displayLabel.getText().contains(buttonValue)) {
                                displayLabel.setText(displayLabel.getText() + buttonValue);
                            }
                        } else if ("0123456789".contains(buttonValue)) {
                            if (Objects.equals(displayLabel.getText(), "0")) {
                                displayLabel.setText(buttonValue);
                            } else {
                                displayLabel.setText(displayLabel.getText() + buttonValue);
                            }
                        }
                    }
                }
            });
        }

        frame.setVisible(true);
    }

    private JButton getjButton(String value) {
        JButton button = new JButton();
        button.setFont(new Font("Arial", Font.PLAIN, 30));
        button.setText(value);
        button.setFocusable(false);
        button.setBorder(new LineBorder(customBlack));

        if (Arrays.asList(topSymbols).contains(value)) {
            button.setBackground(customLightGray);
            button.setForeground(customBlack);
        } else if (Arrays.asList(rightSymbols).contains(value)) {
            button.setBackground(customOrange);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(customDarkGray);
            button.setForeground(Color.WHITE);
        }
        return button;
    }

    private JButton createWindowButton(Color color) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(12, 12));
        button.setBackground(color);
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setOpaque(true);

        button.setBorder(BorderFactory.createEmptyBorder());
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(button.getBackground());
                g2.fillOval(0, 0, c.getWidth(), c.getHeight());

                g2.dispose();
            }
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void toggleMaximize() {
        if ((frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0) {
            frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        } else {
            frame.setExtendedState(frame.getExtendedState() & ~JFrame.MAXIMIZED_BOTH);
        }
    }

    private void clearAll() {
        A = "0";
        operator = null;
        B = null;
    }

    private String removeZeroDecimal(double numDisplay) {
        if (numDisplay % 1 == 0) {
            return Integer.toString((int) numDisplay);
        } else {
            return Double.toString(numDisplay);
        }
    }
}
