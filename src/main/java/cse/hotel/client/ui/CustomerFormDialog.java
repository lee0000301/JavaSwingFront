package cse.hotel.client.ui; 

import cse.hotel.client.ui.*;
import cse.hotel.common.model.Customer;
import cse.hotel.common.packet.Request; 
import cse.hotel.common.packet.Response; 
import cse.hotel.client.network.HotelClient;

import javax.swing.*;
import java.awt.*;

class CustomerFormDialog extends JDialog {

    private JTextField idField, nameField, phoneField, emailField, addressField;
    private JTextArea memoArea;
    private boolean isEditMode; // 수정 모드인지 확인용 플래그
    
    public CustomerFormDialog(JFrame owner, String title, boolean modal) {
        super(owner, title, modal);
        
        // 제목에 "수정"이 포함되어 있으면 수정 모드로 간주
        this.isEditMode = title.contains("수정");
        
        setSize(800, 500);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(owner);
        
        JPanel formPanel = createFormPanel(title);
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        
        JPanel buttonPanel = createDialogButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    // 수정 시 데이터를 채워넣기 위한 메서드 (외부에서 호출)
    public void setCustomerData(Customer customer) {
        idField.setText(customer.getCustomerId());
        nameField.setText(customer.getName());
        phoneField.setText(customer.getPhoneNumber());
        // [주의] Customer 모델에 아래 필드들이 추가되어 있어야 합니다.
        // emailField.setText(customer.getEmail());
        // addressField.setText(customer.getAddress());
        // memoArea.setText(customer.getMemo());
    }
    
    private JPanel createFormPanel(String dialogTitle) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); 

        idField = new JTextField(20);
        nameField = new JTextField(20);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        addressField = new JTextField(20);
        memoArea = new JTextArea(5, 20);
        
        JLabel[] labels = {new JLabel("고객 ID:"), new JLabel("이름:"), new JLabel("연락처:"), new JLabel("이메일:"), new JLabel("주소:"), new JLabel("메모:")};
        JComponent[] fields = {idField, nameField, phoneField, emailField, addressField, new JScrollPane(memoArea)};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
            panel.add(labels[i], gbc);
            
            gbc.gridx = 1; gbc.gridy = i; gbc.weightx = 1.0; 
            if (labels[i].getText().contains("메모")) {
                gbc.gridheight = 2;
                panel.add(fields[i], gbc);
                gbc.gridheight = 1;
                i++;
            } else {
                panel.add(fields[i], gbc);
            }
        }
        
        if (isEditMode) { 
            idField.setEditable(false);
            idField.setBackground(Color.LIGHT_GRAY);
        }

        return panel;
    }
    
    private JPanel createDialogButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10)); 
        
        JButton saveButton = new JButton("저장");
        saveButton.addActionListener(e -> handleSave()); // [수정] 저장 로직 메서드 분리
        panel.add(saveButton);
        
        JButton cancelButton = new JButton("취소");
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);
        
        return panel;
    }

    // [핵심] 서버로 데이터를 전송하는 로직
    private void handleSave() {
        // 1. 입력값 가져오기
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();
        String memo = memoArea.getText();

        // 2. 유효성 검사 (간단 예시)
        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID와 이름은 필수 입력 항목입니다.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 3. Customer 객체 생성
            // [주의] Common 프로젝트의 Customer 생성자를 수정해야 이 코드가 동작합니다.
            // 현재는 기존 생성자(3개 인자)만 사용하고 나머지는 무시하거나, Customer 클래스를 수정해야 함
            Customer customer = new Customer(id, name, phone); 
            // customer.setEmail(email); // Customer 모델 업데이트 후 주석 해제
            // customer.setAddress(address); // Customer 모델 업데이트 후 주석 해제
            // customer.setMemo(memo); // Customer 모델 업데이트 후 주석 해제

            // 4. 요청 객체 생성 (수정이면 UPDATE, 신규면 ADD)
            String command = isEditMode ? "UPDATE_CUSTOMER" : "ADD_CUSTOMER";
            Request request = new Request(command, customer);

            // 5. 서버 전송 및 응답 처리
            Response response = HotelClient.sendRequest(request);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, response.getMessage(), "성공", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // 성공 시 창 닫기
            } else {
                JOptionPane.showMessageDialog(this, "저장 실패: " + response.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "통신 오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}