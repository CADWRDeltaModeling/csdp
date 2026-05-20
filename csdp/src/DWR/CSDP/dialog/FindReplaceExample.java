package DWR.CSDP.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FindReplaceExample extends JFrame {
    private JTextArea textArea;

    public FindReplaceExample() {
        setTitle("Find and Replace Example");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton findButton = new JButton("Find");
        JButton replaceButton = new JButton("Replace");

        findButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchText = JOptionPane.showInputDialog("Enter text to find:");
                if (searchText != null) {
                    findText(searchText);
                }
            }
        });

        replaceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchText = JOptionPane.showInputDialog("Enter text to find:");
                if (searchText != null) {
                    String replaceText = JOptionPane.showInputDialog("Enter text to replace with:");
                    if (replaceText != null) {
                        replaceText(searchText, replaceText);
                    }
                }
            }
        });

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(findButton);
        buttonPanel.add(replaceButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void findText(String searchText) {
        String content = textArea.getText();
        int index = content.indexOf(searchText);
        if (index != -1) {
            textArea.setSelectionStart(index);
            textArea.setSelectionEnd(index + searchText.length());
        } else {
            JOptionPane.showMessageDialog(this, "Text not found.");
        }
    }

    private void replaceText(String searchText, String replaceText) {
        String content = textArea.getText();
        String modifiedContent = content.replace(searchText, replaceText);
        textArea.setText(modifiedContent);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FindReplaceExample().setVisible(true);
            }
        });
    }
}