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
import java.util.UUID;
import java.util.HashMap;

public class FoodOrderUI extends JFrame {

    private String customerId; // 로그인한 고객 ID
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtRoomNumber;
    private JSpinner spinnerCount;

    // --- 디자인 상수 (Color Palette) ---
    private final Color MAIN_BG = new Color(245, 245, 245); // 배경 (연회색)
    private final Color PANEL_BG = Color.WHITE;             // 패널 배경 (흰색)
    private final Color HEADER_BG = new Color(50, 50, 50);  // 헤더 (진한 회색)
    private final Color POINT_BLUE = new Color(52, 101, 164); // 포인트 (파란색)
    private final Color BUTTON_HOVER = new Color(40, 80, 130);
    private final Color TABLE_HEADER = new Color(230, 230, 230); // 테이블 헤더
    private final Color TEXT_DARK = new Color(60, 60, 60);  // 텍스트

    public FoodOrderUI(String customerId) {
        this.customerId = customerId;
        
        setTitle("🍽️ Premium Room Service");
        setSize(700, 600); // 조금 더 넉넉하게
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // 전체 배경 설정
        getContentPane().setBackground(MAIN_BG);
        setLayout(new BorderLayout(0, 0));

        // 1. UI 초기화 (디자인 적용)
        initStylishComponents();

        // 2. 로직 연결 (기존 코드 유지)
        // 메뉴 로드
        loadMenu();
        
        setVisible(true);
    }

    // --- [UI 구성] 세련된 디자인 적용 ---
    private void initStylishComponents() {
        // A. 상단 헤더 (타이틀 + 방 번호 입력)
        add(createHeaderPanel(), BorderLayout.NORTH);

        // B. 중앙 메뉴 테이블
        add(createMenuPanel(), BorderLayout.CENTER);

        // C. 하단 주문 컨트롤 패널
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(new EmptyBorder(0, 25, 0, 25));

        // 타이틀
        JLabel titleLabel = new JLabel("Room Service Menu");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        // 방 번호 입력 영역 (우측 배치)
        JPanel roomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        roomPanel.setOpaque(false);
        
        JLabel lblRoom = new JLabel("Room No.");
        lblRoom.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        lblRoom.setForeground(Color.LIGHT_GRAY);
        
        txtRoomNumber = new JTextField(6);
        txtRoomNumber.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        txtRoomNumber.setHorizontalAlignment(JTextField.CENTER);
        txtRoomNumber.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        roomPanel.add(lblRoom);
        roomPanel.add(txtRoomNumber);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(roomPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAIN_BG);
        panel.setBorder(new EmptyBorder(20, 20, 10, 20));

        // 테이블 모델 설정 (기존 로직 유지)
        String[] cols = {"메뉴명", "가격", "설명", "재고"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 테이블 스타일링
        table.setRowHeight(35); // 행 높이 증가
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(232, 242, 254)); // 선택 시 연한 파랑
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

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
        // "메뉴를 선택해주세요" 타이틀은 라벨로 대체 (TitledBorder 대신 깔끔하게)
        JLabel lblGuide = new JLabel("원하시는 메뉴를 선택해 주세요.");
        lblGuide.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        lblGuide.setForeground(TEXT_DARK);
        lblGuide.setBorder(new EmptyBorder(0, 0, 10, 0));

        panel.add(lblGuide, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 25, 15, 25)
        ));

        // 좌측: 수량 선택
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        countPanel.setBackground(PANEL_BG);
        
        JLabel lblCount = new JLabel("수량 (Qty): ");
        lblCount.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        
        spinnerCount = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JComponent editor = spinnerCount.getEditor();
        JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor)editor;
        spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
        spinnerCount.setPreferredSize(new Dimension(60, 30));
        spinnerCount.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        countPanel.add(lblCount);
        countPanel.add(spinnerCount);

        // 우측: 버튼 그룹
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(PANEL_BG);

        JButton btnClose = createStyledButton("닫기", new Color(200, 200, 200), Color.BLACK);
        JButton btnOrder = createStyledButton("주문하기", POINT_BLUE, Color.WHITE);

        // 이벤트 연결
        btnOrder.addActionListener(e -> handleOrder());
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnClose);
        btnPanel.add(btnOrder);

        panel.add(countPanel, BorderLayout.WEST);
        panel.add(btnPanel, BorderLayout.EAST);

        return panel;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 40));
        return btn;
    }

    // =========================================================================
    // ▼ [기존 로직 유지] 서버 통신 및 주문 처리 (100% 동일) ▼
    // =========================================================================

    // 서버에서 메뉴 목록 가져오기
    private void loadMenu() {
        try {
            Request req = new Request("GET_FOODS", null);
            Response res = HotelClient.sendRequest(req);

            if (res.isSuccess()) {
                List<Food> foods = (List<Food>) res.getResultData();
                
                // [수정] 테이블 컬럼에 '재고' 추가 (기존 로직 유지)
                model.setRowCount(0); // 기존 데이터 초기화

                for (Food f : foods) {
                    // 재고가 0이면 "(품절)" 표시하거나 숫자로 표시
                    String stockStr = (f.getStock() > 0) ? String.valueOf(f.getStock()) : "품절";
                    
                    model.addRow(new Object[]{ 
                        f.getName(), 
                        f.getPrice(), 
                        f.getDescription(), 
                        stockStr 
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 주문 전송 로직
    private void handleOrder() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "메뉴를 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String roomStr = txtRoomNumber.getText().trim();
        if (roomStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "배달받을 객실 번호를 입력해주세요.", "입력 필요", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 데이터 준비
            String foodName = (String) model.getValueAt(row, 0);
            int price = (Integer) model.getValueAt(row, 1);
            int count = (Integer) spinnerCount.getValue();
            int totalPrice = price * count;
            int roomNum = Integer.parseInt(roomStr);
            // String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8); // (서버 생성 시 불필요)

            // ▼▼▼ [수정] FoodOrder 객체 대신 HashMap 사용! ▼▼▼
            HashMap<String, Object> orderData = new HashMap<>();
            orderData.put("roomNumber", roomNum);
            orderData.put("foodName", foodName);
            orderData.put("count", count);
            orderData.put("customerId", customerId);
            
            // 서버 전송
            Request req = new Request("ORDER_FOOD", orderData);
            Response res = HotelClient.sendRequest(req);

            if (res.isSuccess()) {
                String msg = String.format("✅ 주문이 접수되었습니다!\n\n메뉴: %s (%d개)\n금액: ₩%d\n객실: %d호",
                        foodName, count, totalPrice, roomNum);
                JOptionPane.showMessageDialog(this, msg, "주문 성공", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "주문 실패: " + res.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "객실 번호는 숫자만 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "오류: " + e.getMessage(), "통신 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}