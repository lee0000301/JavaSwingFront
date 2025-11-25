package cse.hotel.client.ui.food;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.model.Food;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class FoodUI extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    
    // 입력 필드들
    private JTextField txtName, txtPrice, txtDesc, txtStock;
    
    // 버튼들 (로직 연결을 위해 멤버 변수 유지)
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    // --- 디자인 상수 (Color Palette) ---
    private final Color MAIN_BG = new Color(245, 245, 245); // 전체 배경 (연회색)
    private final Color PANEL_BG = Color.WHITE;             // 패널 배경 (흰색)
    private final Color HEADER_BG = new Color(50, 50, 50);  // 헤더 배경 (진한 회색)
    private final Color POINT_BLUE = new Color(52, 101, 164); // 포인트 컬러 (파란색)
    private final Color TABLE_HEADER = new Color(230, 230, 230); // 테이블 헤더
    private final Color TEXT_DARK = new Color(60, 60, 60);  // 기본 텍스트

    public FoodUI() {
        setTitle("관리자 - 식음료 메뉴 관리");
        setSize(950, 650); // 가로로 조금 더 넓게 확보
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // 전체 배경색 설정
        getContentPane().setBackground(MAIN_BG);
        setLayout(new BorderLayout(0, 0));

        // 1. UI 컴포넌트 초기화 및 배치 (디자인 적용)
        initStylishComponents();

        // 2. 이벤트 리스너 연결 (기존 로직 유지)
        setupListeners();

        // 3. 초기 데이터 로드 (기존 로직 유지)
        loadData();
        
        setVisible(true);
    }

    // --- [UI 구성] 세련된 디자인 적용 ---
    private void initStylishComponents() {
        // A. 상단 헤더 (제목)
        add(createHeaderPanel(), BorderLayout.NORTH);

        // B. 중앙 컨텐츠 (좌측: 입력폼 / 우측: 테이블)
        JPanel contentPanel = new JPanel(new BorderLayout(15, 0));
        contentPanel.setBackground(MAIN_BG);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // 전체 여백

        contentPanel.add(createInputPanel(), BorderLayout.WEST);
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setPreferredSize(new Dimension(0, 60));
        panel.setBorder(new EmptyBorder(0, 25, 0, 0));

        JLabel titleLabel = new JLabel("식음료(F&B) 메뉴 관리");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setPreferredSize(new Dimension(320, 0));
        // 둥근 테두리 느낌을 위한 복합 보더
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // 1. 폼 제목
        JLabel lblTitle = new JLabel("메뉴 정보 입력");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        // 2. 입력 필드들 (GridBagLayout으로 정교하게 배치)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0); // 아래쪽 여백
        gbc.gridx = 0; 
        
        // 필드 초기화
        txtName = createStyledTextField();
        txtPrice = createStyledTextField();
        txtDesc = createStyledTextField();
        txtStock = createStyledTextField();
        txtStock.setText("10"); // 기본값

        // 폼 추가 헬퍼
        addFormField(formPanel, gbc, "메뉴 이름", txtName, 0);
        addFormField(formPanel, gbc, "가격 (KRW)", txtPrice, 2);
        addFormField(formPanel, gbc, "메뉴 설명", txtDesc, 4);
        addFormField(formPanel, gbc, "초기 재고", txtStock, 6);

        panel.add(formPanel, BorderLayout.CENTER);

        // 3. 버튼 패널 (하단)
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // 2행 2열 그리드
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

        // 모델 설정
        String[] cols = {"메뉴명", "가격", "설명", "재고"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);

        // 테이블 스타일링
        table.setRowHeight(30); // 행 높이 증가
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(232, 242, 254)); // 선택색 연한 파랑
        table.setSelectionForeground(Color.BLACK);

        // 헤더 스타일
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setBackground(TABLE_HEADER);
        header.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        header.setForeground(TEXT_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        // 가운데 정렬
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<table.getColumnCount(); i++){
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
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
        // 내부 여백(Padding) 추가
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)), 
            new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); // 플랫 스타일
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- [로직] 기존 코드 100% 유지 ---
    
    private void setupListeners() {
        // 기존 버튼들에 리스너 연결
        btnAdd.addActionListener(e -> addFood());
        btnUpdate.addActionListener(e -> updateFood());
        btnDelete.addActionListener(e -> deleteFood());
        btnClear.addActionListener(e -> clearFields());

        // 테이블 클릭 리스너
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtName.setText(model.getValueAt(row, 0).toString());
                    txtPrice.setText(model.getValueAt(row, 1).toString());
                    txtDesc.setText(model.getValueAt(row, 2).toString());
                    txtStock.setText(model.getValueAt(row, 3).toString());
                    txtName.setEditable(false);
                }
            }
        });
    }

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
                        f.getStock() 
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void addFood() {
        try {
            String name = txtName.getText().trim();
            String priceStr = txtPrice.getText().trim();
            String desc = txtDesc.getText().trim();
            String stockStr = txtStock.getText().trim();

            if(name.isEmpty()) { JOptionPane.showMessageDialog(this, "이름을 입력하세요."); return; }
            if(priceStr.isEmpty() || stockStr.isEmpty()) { JOptionPane.showMessageDialog(this, "가격과 재고를 입력하세요."); return; }

            int price = Integer.parseInt(priceStr);
            int stock = Integer.parseInt(stockStr);

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
            String priceStr = txtPrice.getText().trim();
            String desc = txtDesc.getText().trim();
            String stockStr = txtStock.getText().trim();

            if(name.isEmpty()) return;

            int price = Integer.parseInt(priceStr);
            int stock = Integer.parseInt(stockStr);

            Food food = new Food(name, price, desc, stock);
            Request req = new Request("UPDATE_FOOD", food);
            
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