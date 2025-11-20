package cse.hotel.client.ui.food;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.common.model.Food;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FoodUI extends JFrame {

    // private final FoodService foodService = FoodService.getInstance(); // <--- 삭제 (Service는 서버에 있음)

    // UI 컴포넌트 선언
    private JTable menuTable;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtName, txtPrice, txtStock, txtCategory;
    private JButton btnAdd, btnUpdate, btnDelete;

    private final String[] COLUMN_NAMES = {"ID", "이름", "가격", "재고", "분류"};

    public FoodUI() {
        setTitle("식음료 메뉴 관리 (SFR-500) - 클라이언트 모드");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 크기 설정 (원래 설정 유지)
        setSize(800,600); 

        // 1. 테이블 모델 및 테이블 초기화
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        menuTable = new JTable(tableModel);
        
        // 2. 컴포넌트 초기화 및 패널 설정
        initializeComponents();
        
        // 3. 이벤트 리스너 연결
        attachListeners();

        // 4. 초기 데이터 로드 (서버 통신 필요)
        refreshTable();
        
        // setSize()를 사용하셨으므로 pack()은 불필요하거나 조정이 필요하지만, 원본 코드에 따라 유지합니다.
        pack(); 
        setLocationRelativeTo(null); // 화면 중앙 배치
    }
    
    // --- 컴포넌트 초기화 메서드 (레이아웃 설정) ---
    private void initializeComponents() {
        // 입력 필드 패널 (North)
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        txtId = new JTextField(10);
        txtName = new JTextField(10);
        txtPrice = new JTextField(10);
        txtStock = new JTextField(10);
        txtCategory = new JTextField(10);

        inputPanel.add(new JLabel("메뉴 ID:"));
        inputPanel.add(txtId);
        inputPanel.add(new JLabel("이름:"));
        inputPanel.add(txtName);
        inputPanel.add(new JLabel("가격:"));
        inputPanel.add(txtPrice);
        inputPanel.add(new JLabel("재고:"));
        inputPanel.add(txtStock);
        inputPanel.add(new JLabel("분류:"));
        inputPanel.add(txtCategory);

        // 버튼 패널 (South)
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnAdd = new JButton("등록");
        btnUpdate = new JButton("수정");
        btnDelete = new JButton("삭제");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);

        // 프레임에 추가
        add(new JScrollPane(menuTable), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    // --- 이벤트 리스너 연결 ---
    private void attachListeners() {
        btnAdd.addActionListener(this::addButtonActionPerformed);
        btnUpdate.addActionListener(this::updateButtonActionPerformed);
        btnDelete.addActionListener(this::deleteButtonActionPerformed);
        menuTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFieldsFromTableSelection();
            }
        });
    }

    // --- 3. 핵심 로직 메서드 (서버 통신 적용) ---

    /**
     * 서버에 GET_FOODS를 요청하여 JTable을 갱신합니다.
     */
    private void refreshTable() {
        tableModel.setRowCount(0);

        try {
            // 1. 요청 객체 생성: GET_FOODS 명령 요청
            Request request = new Request("GET_FOODS", null);
            Response response = HotelClient.sendRequest(request);

            if (response.isSuccess()) {
                // 2. 응답 데이터(List<Food>)를 받아와 형 변환
                List<Food> menuList = (List<Food>) response.getResultData(); 
                
                for (Food food : menuList) {
                    tableModel.addRow(new Object[]{
                        food.getMenuId(), 
                        food.getMenuName(), 
                        food.getPrice(), 
                        food.getStock(), 
                        food.getCategory()
                    });
                }
            } else {
                 // 서버에서 오류 응답이 온 경우
                 throw new Exception("서버 조회 오류: " + response.getMessage());
            }
        } catch (Exception e) {
            // 통신 오류, 클래스 형 변환 오류 등
            JOptionPane.showMessageDialog(this, "데이터 로드 중 통신 오류 발생: " + e.getMessage(), 
                                          "통신 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * JTable의 선택된 로우 정보를 입력 필드에 채웁니다.
     */
    private void fillFieldsFromTableSelection() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow >= 0) {
            txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtPrice.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtStock.setText(tableModel.getValueAt(selectedRow, 3).toString());
            txtCategory.setText(tableModel.getValueAt(selectedRow, 4).toString());
            txtId.setEnabled(false);
        }
    }

    // --- 4. 버튼 이벤트 핸들러 (서버 통신 기반 CRUD) ---

    /**
     * '등록' 버튼 클릭 처리 (ADD_FOOD 요청)
     */
    private void addButtonActionPerformed(ActionEvent evt) {
        try {
            Food newMenu = new Food(
                txtId.getText(),
                txtName.getText(),
                Integer.parseInt(txtPrice.getText()), 
                Integer.parseInt(txtStock.getText()),
                txtCategory.getText()
            );

            Request request = new Request("ADD_FOOD", newMenu);
            Response response = HotelClient.sendRequest(request);
            
            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, response.getMessage()); 
                clearFields();
                refreshTable();
            } else {
                // 서버에서 온 비즈니스 로직 오류 메시지 출력
                JOptionPane.showMessageDialog(this, response.getMessage(), "등록 실패", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "가격과 재고는 숫자만 입력해야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "통신 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * '수정' 버튼 클릭 처리 (UPDATE_FOOD 요청)
     */
    private void updateButtonActionPerformed(ActionEvent evt) {
        try {
            Food updatedMenu = new Food(
                txtId.getText(), 
                txtName.getText(),
                Integer.parseInt(txtPrice.getText()),
                Integer.parseInt(txtStock.getText()),
                txtCategory.getText()
            );

            Request request = new Request("UPDATE_FOOD", updatedMenu);
            Response response = HotelClient.sendRequest(request);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, response.getMessage());
                clearFields();
                refreshTable();
            } else {
                // 서버에서 데이터 없음/유효성 오류 메시지 출력
                JOptionPane.showMessageDialog(this, response.getMessage(), "수정 실패", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "가격과 재고는 숫자만 입력해야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "통신 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * '삭제' 버튼 클릭 처리 (DELETE_FOOD 요청)
     */
    private void deleteButtonActionPerformed(ActionEvent evt) {
        String menuIdToDelete = txtId.getText();

        if (menuIdToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "삭제할 메뉴 ID를 입력하거나 테이블에서 선택해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "정말로 메뉴 ID '" + menuIdToDelete + "'를 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Request request = new Request("DELETE_FOOD", menuIdToDelete); 
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
     * 입력 필드를 초기화하고 ID 필드 활성화
     */
    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        txtCategory.setText("");
        txtId.setEnabled(true);
    }
}