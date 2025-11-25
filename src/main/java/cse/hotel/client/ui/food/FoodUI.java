package cse.hotel.client.ui.food;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.model.Food;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FoodUI extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    
    // 입력 필드들
    private JTextField txtName, txtPrice, txtDesc, txtStock; // [추가] 재고 입력 필드
    
    public FoodUI() {
        setTitle("관리자 - 식음료 메뉴 관리");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- 1. 입력 패널 (상단) ---
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5)); // 행 늘림
        inputPanel.setBorder(BorderFactory.createTitledBorder("메뉴 정보 입력"));

        txtName = new JTextField();
        txtPrice = new JTextField();
        txtDesc = new JTextField();
        txtStock = new JTextField("10"); // [추가] 기본 재고 10

        inputPanel.add(new JLabel("메뉴 이름:")); inputPanel.add(txtName);
        inputPanel.add(new JLabel("가격 (원):")); inputPanel.add(txtPrice);
        inputPanel.add(new JLabel("설명:")); inputPanel.add(txtDesc);
        inputPanel.add(new JLabel("초기 재고:")); inputPanel.add(txtStock); // [추가]

        // 버튼 패널
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAdd = new JButton("추가");
        JButton btnUpdate = new JButton("수정");
        JButton btnDelete = new JButton("삭제");
        JButton btnClear = new JButton("초기화");

        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); 
        btnPanel.add(btnDelete); btnPanel.add(btnClear);

        // 상단 컨테이너에 입력폼과 버튼 합치기
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(inputPanel, BorderLayout.CENTER);
        topContainer.add(btnPanel, BorderLayout.SOUTH);
        
        add(topContainer, BorderLayout.NORTH);

        // --- 2. 테이블 (중앙) ---
        // [수정] 컬럼에 '재고' 추가
        String[] cols = {"메뉴명", "가격", "설명", "재고"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        
        // 테이블 클릭 시 입력창에 값 채우기
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtName.setText(model.getValueAt(row, 0).toString());
                    txtPrice.setText(model.getValueAt(row, 1).toString());
                    txtDesc.setText(model.getValueAt(row, 2).toString());
                    txtStock.setText(model.getValueAt(row, 3).toString()); // 재고 채우기
                    txtName.setEditable(false); // 이름(ID역할)은 수정 불가
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- 이벤트 연결 ---
        btnAdd.addActionListener(e -> addFood());
        btnUpdate.addActionListener(e -> updateFood());
        btnDelete.addActionListener(e -> deleteFood());
        btnClear.addActionListener(e -> clearFields());

        // 초기 데이터 로드
        loadData();
        
        setVisible(true);
    }

    // --- 기능 메서드 ---

    private void loadData() {
        try {
            Request req = new Request("GET_FOODS", null);
            Response res = HotelClient.sendRequest(req);
            
            if (res.isSuccess()) {
                List<Food> list = (List<Food>) res.getResultData();
                model.setRowCount(0);
                for (Food f : list) {
                    model.addRow(new Object[]{ 
                        f.getName(), 
                        f.getPrice(), 
                        f.getDescription(), 
                        f.getStock() // [추가] 재고 표시
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void addFood() {
        try {
            String name = txtName.getText().trim();
            int price = Integer.parseInt(txtPrice.getText().trim());
            String desc = txtDesc.getText().trim();
            int stock = Integer.parseInt(txtStock.getText().trim()); // 재고 파싱

            if(name.isEmpty()) { JOptionPane.showMessageDialog(this, "이름을 입력하세요."); return; }

            Food food = new Food(name, price, desc, stock);
            Request req = new Request("ADD_FOOD", food);
            
            if (HotelClient.sendRequest(req).isSuccess()) {
                JOptionPane.showMessageDialog(this, "추가 성공");
                clearFields();
                loadData();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "가격과 재고는 숫자여야 합니다.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateFood() {
        try {
            String name = txtName.getText().trim();
            int price = Integer.parseInt(txtPrice.getText().trim());
            String desc = txtDesc.getText().trim();
            int stock = Integer.parseInt(txtStock.getText().trim());

            Food food = new Food(name, price, desc, stock);
            Request req = new Request("UPDATE_FOOD", food); // 서버에 UPDATE_FOOD 구현 필요
            
            if (HotelClient.sendRequest(req).isSuccess()) {
                JOptionPane.showMessageDialog(this, "수정 성공");
                clearFields();
                loadData();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void deleteFood() {
        String name = txtName.getText().trim();
        if(name.isEmpty()) return;
        
        if (JOptionPane.showConfirmDialog(this, "삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                Request req = new Request("DELETE_FOOD", name);
                if (HotelClient.sendRequest(req).isSuccess()) {
                    JOptionPane.showMessageDialog(this, "삭제 성공");
                    clearFields();
                    loadData();
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void clearFields() {
        txtName.setText(""); txtPrice.setText(""); txtDesc.setText(""); txtStock.setText("10");
        txtName.setEditable(true);
        table.clearSelection();
    }
}