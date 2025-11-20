package cse.hotel.client.ui.customer;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.*;
import cse.hotel.common.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CustomerUI extends JFrame {

    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtName, txtPhone;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    
   

    private final String[] COLUMN_NAMES = {"ID", "이름", "전화번호"};

    public CustomerUI() {
        setTitle("고객 정보 관리");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); // 여백 추가

        // 1. 테이블 모델 및 테이블 초기화
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        customerTable = new JTable(tableModel);
        
        // 2. 컴포넌트 초기화 및 레이아웃 설정
        initializeComponents();
        
        // 3. 이벤트 리스너 연결
        attachListeners();

        // 4. 초기 데이터 로드 (서버 통신)
        refreshTable();
    }
    
    // --- 컴포넌트 초기화 및 패널 설정 ---
    private void initializeComponents() {
        // 입력 필드 패널 (상단)
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        txtId = new JTextField(20);
        txtName = new JTextField(20);
        txtPhone = new JTextField(20);
        
        // ID 필드는 서버에서 자동 생성되므로, 초기에는 사용자가 수정하지 못하게 설정
        txtId.setEnabled(false); 

        inputPanel.add(new JLabel("고객 ID:"));
        inputPanel.add(txtId);
        inputPanel.add(new JLabel("이름:"));
        inputPanel.add(txtName);
        inputPanel.add(new JLabel("전화번호:"));
        inputPanel.add(txtPhone);

        // 버튼 패널 (하단)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = new JButton("신규 등록");
        btnUpdate = new JButton("정보 수정");
        btnDelete = new JButton("고객 삭제");
        btnClear = new JButton("초기화");
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(customerTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    // --- 이벤트 리스너 연결 ---
    private void attachListeners() {
        btnAdd.addActionListener(this::addButtonActionPerformed);
        btnUpdate.addActionListener(this::updateButtonActionPerformed);
        btnDelete.addActionListener(this::deleteButtonActionPerformed);
        btnClear.addActionListener(e -> clearFields());
        customerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFieldsFromTableSelection();
            }
        });
    }

    // --- 3. 핵심 로직 메서드 (서버 통신 적용) ---

    /**
     * 서버에 GET_CUSTOMERS를 요청하여 JTable을 갱신합니다.
     */
    private void refreshTable() {
        tableModel.setRowCount(0);

        try {
            // 1. GET_CUSTOMERS 명령 요청
            Request request = new Request("GET_CUSTOMERS", null);
            Response response = HotelClient.sendRequest(request);

            if (response.isSuccess()) {
                // 2. 응답 데이터(List<Customer>)를 받아와 형 변환
                List<Customer> customerList = (List<Customer>) response.getResultData(); 
                
                for (Customer customer : customerList) {
                    tableModel.addRow(new Object[]{
                        customer.getCustomerId(), 
                        customer.getName(), 
                        customer.getPhoneNumber()
                    });
                }
            } else {
                 throw new Exception("목록 조회 실패: " + response.getMessage());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "서버 연결 또는 데이터 로드 오류: " + e.getMessage(), 
                                          "통신 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * JTable의 선택된 로우 정보를 입력 필드에 채웁니다.
     */
    private void fillFieldsFromTableSelection() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtPhone.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtId.setEnabled(false); // ID는 수정 불가 (Primary Key)
        }
    }

    /**
     * '등록' 버튼 클릭 처리 (ADD_CUSTOMER 요청)
     */
    private void addButtonActionPerformed(ActionEvent evt) {
        
        try {
            // ID는 서버에서 생성되므로, 여기서는 임시로 null이나 비어있는 값으로 보냅니다.
            Customer newCustomer = new Customer(
                null, // ID는 서버에서 자동 생성되도록 임시 처리
                txtName.getText(),
                txtPhone.getText()
            );

            Request request = new Request("ADD_CUSTOMER", newCustomer);
            Response response = HotelClient.sendRequest(request);
            
            if (response.isSuccess()) {
                // 1. 서버가 반환한 새로 등록된 Customer 객체를 받습니다.
             Customer registeredCustomer = (Customer) response.getResultData();
            
            // 2. 사용자에게 생성된 ID를 보여줍니다.
              String successMessage = response.getMessage() + 
                                    "\n생성된 ID: " + registeredCustomer.getCustomerId();
            
              JOptionPane.showMessageDialog(this, successMessage, "등록 성공", JOptionPane.INFORMATION_MESSAGE);
               
                clearFields();
                refreshTable();
            } else {
                // 서버에서 온 비즈니스 로직 오류 메시지 (전화번호 중복 등) 출력
                JOptionPane.showMessageDialog(this, response.getMessage(), "등록 실패", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "통신 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * '수정' 버튼 클릭 처리 (UPDATE_CUSTOMER 요청)
     */
    private void updateButtonActionPerformed(ActionEvent evt) {
        try {
            if (txtId.getText().isEmpty()) {
                 JOptionPane.showMessageDialog(this, "수정할 고객을 테이블에서 선택해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
                 return;
            }

            Customer updatedCustomer = new Customer(
                txtId.getText(), // ID는 선택된 값 그대로 사용
                txtName.getText(),
                txtPhone.getText()
            );

            Request request = new Request("UPDATE_CUSTOMER", updatedCustomer);
            Response response = HotelClient.sendRequest(request);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, response.getMessage());
                clearFields();
                refreshTable();
            } else {
                // 서버에서 데이터 없음/전화번호 중복 오류 메시지 출력
                JOptionPane.showMessageDialog(this, response.getMessage(), "수정 실패", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "통신 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * '삭제' 버튼 클릭 처리 (DELETE_CUSTOMER 요청)
     */
    private void deleteButtonActionPerformed(ActionEvent evt) {
        String customerIdToDelete = txtId.getText();

        if (customerIdToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "삭제할 고객을 입력하거나 테이블에서 선택해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "고객 ID '" + customerIdToDelete + "'를 정말 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // 삭제할 ID(String)를 데이터로 담아 서버에 전송
                Request request = new Request("DELETE_CUSTOMER", customerIdToDelete); 
                Response response = HotelClient.sendRequest(request);
                
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, response.getMessage()); 
                    clearFields();
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, response.getMessage(), "삭제 실패", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "통신 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 입력 필드를 초기화하고 ID 필드 활성화 상태로 되돌립니다.
     */
    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtId.setEnabled(true);
    }
    
    // --- 메인 메서드 (테스트용) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerUI().setVisible(true));
    }
}