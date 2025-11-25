package cse.hotel.client.ui.customer;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.*;
import cse.hotel.common.model.Customer;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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

    // --- 디자인 상수 (Color Palette) ---
    private final Color MAIN_BG = new Color(245, 245, 245); // 배경 (연회색)
    private final Color PANEL_BG = Color.WHITE;             // 패널 배경 (흰색)
    private final Color HEADER_BG = new Color(50, 50, 50);  // 헤더 (진한 회색)
    private final Color POINT_BLUE = new Color(52, 101, 164); // 포인트 (파란색)
    private final Color TABLE_HEADER = new Color(230, 230, 230); // 테이블 헤더
    private final Color TEXT_DARK = new Color(60, 60, 60);  // 텍스트

    public CustomerUI() {
        setTitle("고객 정보 관리");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // 전체 배경 설정
        getContentPane().setBackground(MAIN_BG);
        setLayout(new BorderLayout(0, 0));

        // 1. 테이블 모델 초기화 (로직 유지를 위해 변수 초기화 먼저 수행)
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 테이블 수정 방지
            }
        };
        customerTable = new JTable(tableModel);
        
        // 2. UI 컴포넌트 초기화 및 디자인 적용 (기존 initializeComponents 대체)
        initStylishComponents();
        
        // 3. 이벤트 리스너 연결
        attachListeners();

        // 4. 초기 데이터 로드
        refreshTable();
        
        setVisible(true);
    }
    
    // --- [UI 구성] 세련된 디자인 적용 메서드 ---
    private void initStylishComponents() {
        // A. 상단 헤더
        add(createHeaderPanel(), BorderLayout.NORTH);

        // B. 중앙 컨텐츠 (좌측: 입력폼 / 우측: 테이블)
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(MAIN_BG);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        contentPanel.add(createInputPanel(), BorderLayout.WEST);
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setPreferredSize(new Dimension(0, 60));
        panel.setBorder(new EmptyBorder(0, 25, 0, 0));

        JLabel titleLabel = new JLabel("고객 정보 관리 시스템");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setPreferredSize(new Dimension(300, 0));
        // 둥근 느낌의 테두리 효과
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // 1. 폼 제목
        JLabel lblTitle = new JLabel("고객 정보 입력");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        // 2. 입력 필드 배치 (GridBagLayout)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.gridx = 0;

        // 필드 초기화
        txtId = createStyledTextField();
        txtId.setEnabled(false); // ID 비활성화 유지
        txtName = createStyledTextField();
        txtPhone = createStyledTextField();

        // 폼 추가
        addFormField(formPanel, gbc, "고객 ID (자동 생성)", txtId, 0);
        addFormField(formPanel, gbc, "고객 이름", txtName, 2);
        addFormField(formPanel, gbc, "전화번호", txtPhone, 4);

        // 남은 공간 채우기 (레이아웃 정렬용)
        gbc.weighty = 1.0;
        gbc.gridy = 6;
        formPanel.add(new JLabel(), gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // 3. 버튼 패널 (하단)
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setBackground(PANEL_BG);
        btnPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnAdd = createStyledButton("신규 등록", POINT_BLUE, Color.WHITE);
        btnUpdate = createStyledButton("정보 수정", new Color(80, 80, 80), Color.WHITE);
        btnDelete = createStyledButton("삭제", new Color(200, 60, 60), Color.WHITE);
        btnClear = createStyledButton("초기화", new Color(230, 230, 230), Color.BLACK);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int yPos) {
        gbc.gridy = yPos;
        gbc.weighty = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        label.setForeground(Color.GRAY);
        panel.add(label, gbc);

        gbc.gridy = yPos + 1;
        panel.add(field, gbc);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(new LineBorder(new Color(200, 200, 200), 1));

        // 테이블 스타일링
        customerTable.setRowHeight(30);
        customerTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        customerTable.setShowVerticalLines(false);
        customerTable.setGridColor(new Color(230, 230, 230));
        customerTable.setSelectionBackground(new Color(232, 242, 254));
        customerTable.setSelectionForeground(Color.BLACK);

        // 헤더 스타일
        JTableHeader header = customerTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setBackground(TABLE_HEADER);
        header.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        header.setForeground(TEXT_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        // 가운데 정렬
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<customerTable.getColumnCount(); i++){
            customerTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(null);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // --- 스타일 헬퍼 메서드 ---
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 35));
        field.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)), 
            new EmptyBorder(5, 10, 5, 10) // 내부 여백
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    // ====================================================================
    // ▼ 아래 로직은 요청하신 대로 100% 원본 유지 (단, initializeComponents는 위 코드로 대체됨) ▼
    // ====================================================================

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
}