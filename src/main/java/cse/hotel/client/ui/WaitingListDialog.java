package cse.hotel.client.ui;

import cse.hotel.common.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

class WaitingListDialog extends JDialog {
    
    public WaitingListDialog(JFrame owner, String title, boolean modal) {
        super(owner, title, modal);
        
        setSize(800, 500);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(owner);
        
        String[] columnNames = {"순번", "이름", "인원", "등록 시각"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable waitingTable = new JTable(tableModel);
        
        tableModel.addRow(new Object[]{1, "박선영", 2, "14:30"});
        tableModel.addRow(new Object[]{2, "최현우", 4, "14:45"});
        
        add(new JScrollPane(waitingTable), BorderLayout.CENTER);
        
        JPanel controlPanel = createControlPanel(tableModel, waitingTable);
        add(controlPanel, BorderLayout.SOUTH);
    }
    
        private JPanel createControlPanel(DefaultTableModel tableModel, JTable waitingTable) {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel registerForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        registerForm.setBorder(BorderFactory.createTitledBorder("신규 대기 등록"));
        
        JTextField nameField = new JTextField(10);
        JTextField countField = new JTextField(5);
        
        registerForm.add(new JLabel("이름:"));
        registerForm.add(nameField);
        registerForm.add(new JLabel("인원:"));
        registerForm.add(countField);
        
        JButton registerButton = new JButton("대기 등록");
        registerButton.addActionListener(e -> {

            int newSeq = tableModel.getRowCount() + 1;
            tableModel.addRow(new Object[]{newSeq, nameField.getText(), countField.getText(), java.time.LocalTime.now().toString().substring(0, 5)});
            nameField.setText("");
            countField.setText("");
        });
        registerForm.add(registerButton);
        panel.add(registerForm, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton completeButton = new JButton("처리 완료");
        completeButton.addActionListener(e -> {
            int selectedRow = waitingTable.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "선택된 대기자를 명단에서 제거했습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "처리할 대기자를 선택해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });
        buttonPanel.add(completeButton);

        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }
}