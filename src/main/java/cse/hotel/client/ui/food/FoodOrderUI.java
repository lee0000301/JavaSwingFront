package cse.hotel.client.ui.food;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.model.Food;
import cse.hotel.common.model.FoodOrder;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    public FoodOrderUI(String customerId) {
        this.customerId = customerId;
        
        setTitle("🍽️ 룸서비스 주문");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 1. 상단: 방 번호 입력 (로그인 정보에 있다면 자동 입력 가능)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("배달받을 객실 번호:"));
        txtRoomNumber = new JTextField(10);
        topPanel.add(txtRoomNumber);
        add(topPanel, BorderLayout.NORTH);

        // 2. 중앙: 메뉴판 테이블
        String[] cols = {"메뉴명", "가격", "분류"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("메뉴를 선택해주세요"));
        add(scroll, BorderLayout.CENTER);

        // 3. 하단: 주문 조작부
        JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botPanel.add(new JLabel("수량:"));
        spinnerCount = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1)); // 1~10개
        botPanel.add(spinnerCount);

        JButton btnOrder = new JButton("주문하기");
        btnOrder.setBackground(new Color(255, 165, 0)); // 주황색
        btnOrder.setForeground(Color.WHITE);
        
        JButton btnClose = new JButton("닫기");

        botPanel.add(btnOrder);
        botPanel.add(btnClose);
        add(botPanel, BorderLayout.SOUTH);

        // --- 이벤트 연결 ---
        btnOrder.addActionListener(e -> handleOrder());
        btnClose.addActionListener(e -> dispose());

        // 메뉴 로드
        loadMenu();
        
        setVisible(true);
    }

    // 서버에서 메뉴 목록 가져오기
    private void loadMenu() {
        try {
            Request req = new Request("GET_FOODS", null);
            Response res = HotelClient.sendRequest(req);

            if (res.isSuccess()) {
                List<Food> foods = (List<Food>) res.getResultData();
                
                // [수정] 테이블 컬럼에 '재고' 추가
                String[] cols = {"메뉴명", "가격", "설명", "재고"}; 
                model = new DefaultTableModel(cols, 0) {
                    @Override public boolean isCellEditable(int r, int c) { return false; }
                };
                table.setModel(model); // 모델 교체

                for (Food f : foods) {
                    // 재고가 0이면 "(품절)" 표시하거나 숫자로 표시
                    String stockStr = (f.getStock() > 0) ? String.valueOf(f.getStock()) : "품절";
                    
                    model.addRow(new Object[]{ 
                        f.getName(), 
                        f.getPrice(), 
                        f.getDescription(),
                        stockStr // [추가] 재고 표시
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 주문 전송 로직
    private void handleOrder() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "메뉴를 선택해주세요.");
            return;
        }
        
        String roomStr = txtRoomNumber.getText().trim();
        if (roomStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "객실 번호를 입력해주세요.");
            return;
        }

        try {
            // 데이터 준비
            String foodName = (String) model.getValueAt(row, 0);
            int price = (Integer) model.getValueAt(row, 1);
            int count = (Integer) spinnerCount.getValue();
            int totalPrice = price * count;
            int roomNum = Integer.parseInt(roomStr);
            String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);

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
                String msg = String.format("✅ 주문 완료!\n\n메뉴: %s (%d개)\n금액: ₩%d\n객실: %d호",
                        foodName, count, totalPrice, roomNum);
                JOptionPane.showMessageDialog(this, msg);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "주문 실패: " + res.getMessage());
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "객실 번호는 숫자만 입력하세요.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "오류: " + e.getMessage());
        }
    }
}