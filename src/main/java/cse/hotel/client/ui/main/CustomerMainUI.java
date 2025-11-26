package cse.hotel.client.ui.main;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;


import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.client.network.HotelClient;
import cse.hotel.client.ui.clientReservation.ClientReservationUI;
import cse.hotel.client.ui.clientReservation.MyReservationUI;
import cse.hotel.client.ui.food.FoodOrderUI;
import cse.hotel.client.ui.login.LoginUI;
import cse.hotel.client.ui.payment.PaymentUI;

public class CustomerMainUI extends JFrame {

    private String loggedInCustomerId;

    // --- 디자인 상수 ---
    private final Color MAIN_BG = new Color(249, 249, 249); 
    private final Color HEADER_BG = new Color(44, 62, 80);  
    private final Color CARD_BG = Color.WHITE;              
    private final Color POINT_BLUE = new Color(52, 101, 164); 
    private final Color TEXT_DARK = new Color(60, 60, 60);  

    public CustomerMainUI(String customerId) {
        this.loggedInCustomerId = customerId;
        
        setTitle("고객 서비스 센터");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 650); 
        setLocationRelativeTo(null);
        
        getContentPane().setBackground(MAIN_BG);
        setLayout(new BorderLayout(0, 0));

        initStylishUI();

        setVisible(true);
    }
    
    public CustomerMainUI() {
        this("GUEST");
    }

    // --- [UI 구성] ---
    private void initStylishUI() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMenuGridPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(new EmptyBorder(0, 30, 0, 30));

        JLabel titleLabel = new JLabel("Welcome, " + loggedInCustomerId + "님");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subLabel = new JLabel("편안한 투숙을 위한 스마트 호텔 서비스입니다.");
        subLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        subLabel.setForeground(new Color(200, 200, 200));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subLabel);

        panel.add(textPanel, BorderLayout.CENTER);
        
        JLabel iconLabel = new JLabel("🏨");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        panel.add(iconLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMenuGridPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        gridPanel.setBackground(MAIN_BG);
        gridPanel.setBorder(new EmptyBorder(30, 40, 10, 40));

        JButton btnBooking = createMenuCard("객실 예약", "🛎️", "새로운 예약하기", new Color(235, 245, 255));
        JButton btnRoomService = createMenuCard("룸서비스", "🍽️", "식음료 주문", new Color(255, 248, 235));
        JButton btnMyRes = createMenuCard("내 예약 확인", "📋", "예약 내역 조회", new Color(245, 245, 245));
        JButton btnCheckIn = createMenuCard("셀프 체크인", "➡️", "입실 수속", new Color(235, 255, 235));
        JButton btnCheckOut = createMenuCard("체크아웃", "⬅️", "퇴실 및 결제", new Color(255, 235, 235));
        
        btnBooking.addActionListener(e -> new ClientReservationUI(this.loggedInCustomerId));
        btnRoomService.addActionListener(e -> new FoodOrderUI(this.loggedInCustomerId).setVisible(true));
        btnMyRes.addActionListener(e -> new MyReservationUI(this.loggedInCustomerId));
        btnCheckIn.addActionListener(this::handleCheckIn);
        btnCheckOut.addActionListener(this::handleCheckOut);

        gridPanel.add(btnBooking);
        gridPanel.add(btnRoomService);
        gridPanel.add(btnMyRes);
        gridPanel.add(btnCheckIn);
        gridPanel.add(btnCheckOut);
        
        JPanel emptyPanel = new JPanel();
        emptyPanel.setOpaque(false);
        gridPanel.add(emptyPanel);

        return gridPanel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(MAIN_BG);
        panel.setBorder(new EmptyBorder(10, 0, 20, 40));
        
        JButton btnExit = new JButton("종료 (Exit)");
        btnExit.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btnExit.setBackground(new Color(100, 100, 100));
        btnExit.setForeground(Color.WHITE);
        btnExit.setFocusPainted(false);
        btnExit.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExit.addActionListener(e ->  {
           dispose();
           new LoginUI();
        });
              
        
        panel.add(btnExit);
        return panel;
    }

    private JButton createMenuCard(String title, String icon, String desc, Color bgColor) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setBackground(CARD_BG);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setBackground(bgColor);
        iconPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconPanel.add(iconLabel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_DARK);
        
        JLabel descLabel = new JLabel(desc, SwingConstants.CENTER);
        descLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(CARD_BG);
        textPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        btn.add(iconPanel, BorderLayout.CENTER);
        btn.add(textPanel, BorderLayout.SOUTH);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBorder(new CompoundBorder(
                    new LineBorder(POINT_BLUE, 2),
                    new EmptyBorder(14, 14, 14, 14)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBorder(new CompoundBorder(
                    new LineBorder(new Color(220, 220, 220), 1),
                    new EmptyBorder(15, 15, 15, 15)
                ));
            }
        });

        return btn;
    }

    // =========================================================================
    // ▼ [NEW] 커스텀 다이얼로그 디자인 및 핸들러 수정 ▼
    // =========================================================================

    /**
     * 세련된 디자인의 입력 다이얼로그를 띄우는 헬퍼 메서드
     */
    private String showStylishInputDialog(String title, String message, String btnText) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setUndecorated(true); // 기본 프레임 제거 (커스텀 디자인 위해)
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // 1. 헤더 패널
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        dialog.add(headerPanel, BorderLayout.NORTH);
        
        // 2. 중앙 입력 패널
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        JLabel lblMessage = new JLabel(message, SwingConstants.CENTER);
        lblMessage.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        lblMessage.setForeground(TEXT_DARK);
        
        JTextField txtInput = new JTextField();
        txtInput.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        txtInput.setHorizontalAlignment(JTextField.CENTER);
        txtInput.setBorder(new CompoundBorder(
            new LineBorder(POINT_BLUE, 2),
            new EmptyBorder(5, 5, 5, 5)
        ));
        
        centerPanel.add(lblMessage);
        centerPanel.add(txtInput);
        dialog.add(centerPanel, BorderLayout.CENTER);
        
        // 3. 하단 버튼 패널
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(new Color(245, 245, 245));
        
        JButton btnConfirm = new JButton(btnText);
        btnConfirm.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btnConfirm.setBackground(POINT_BLUE);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setPreferredSize(new Dimension(100, 40));
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnCancel = new JButton("취소");
        btnCancel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        btnCancel.setBackground(new Color(220, 220, 220));
        btnCancel.setForeground(TEXT_DARK);
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 결과값 저장을 위한 배열 (final 래퍼)
        final String[] result = { null };
        
        btnConfirm.addActionListener(e -> {
            result[0] = txtInput.getText().trim();
            dialog.dispose();
        });
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(btnCancel);
        btnPanel.add(btnConfirm);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        // 테두리 추가 (Shadow 효과 대용)
        ((JPanel)dialog.getContentPane()).setBorder(new LineBorder(new Color(200, 200, 200), 1));

        dialog.setVisible(true);
        return result[0];
    }

   // 3. [체크인] 버튼 핸들러
    private void handleCheckIn(ActionEvent e) {
        // [디자인 적용] 커스텀 다이얼로그 사용
        String roomNumStr = showStylishInputDialog(
            "Self Check-In", 
            "체크인하실 객실 번호를 입력해주세요 (예: 201)", 
            "입실하기"
        );

        if (roomNumStr != null && !roomNumStr.isEmpty()) {
            try {
                int roomNumber = Integer.parseInt(roomNumStr);
                
                // 데이터 포장 (방 번호 + 내 ID)
                java.util.HashMap<String, Object> dataMap = new java.util.HashMap<>();
                dataMap.put("roomNumber", roomNumber);
                dataMap.put("customerId", this.loggedInCustomerId);

                // 서버 전송
                Request req = new Request("CHECK_IN", dataMap);
                Response res = HotelClient.sendRequest(req);

                if (res.isSuccess()) {
                    JOptionPane.showMessageDialog(this, 
                        "✅ 체크인 완료! 즐거운 시간 되세요.", "성공", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "⛔ 체크인 실패: " + res.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "객실 번호는 숫자만 입력해야 합니다.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "통신 오류: " + ex.getMessage());
            }
        }
    }

   // 4. [체크아웃] 버튼 핸들러
    private void handleCheckOut(ActionEvent e) {
        // [디자인 적용] 커스텀 다이얼로그 사용
        String roomNumStr = showStylishInputDialog(
            "Self Check-Out", 
            "체크아웃하실 객실 번호를 입력해주세요", 
            "결제 및 퇴실"
        );

        if (roomNumStr != null && !roomNumStr.isEmpty()) {
            try {
                int roomNumber = Integer.parseInt(roomNumStr);
                
                // 로그인 ID가 없으면 테스트 ID 사용 (안전장치)
                String currentId = (this.loggedInCustomerId != null) ? this.loggedInCustomerId : "GUEST";

                // 1. 먼저 청구서(금액 정보)를 요청합니다.
                java.util.HashMap<String, Object> reqMap = new java.util.HashMap<>();
                reqMap.put("roomNumber", roomNumber);
                reqMap.put("customerId", currentId);

                Request req = new Request("REQUEST_BILL", reqMap);
                Response res = HotelClient.sendRequest(req);

                if (res.isSuccess()) {
                    // 2. 성공하면 결제창을 띄웁니다. (받아온 billData 전달)
                    java.util.Map<String, Object> billData = (java.util.Map<String, Object>) res.getResultData();
                    
                    // PaymentUI 생성 및 표시
                    new cse.hotel.client.ui.payment.PaymentUI(this, currentId, roomNumber, billData).setVisible(true);
                    
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "⛔ 체크아웃 진행 불가: " + res.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "객실 번호는 숫자만 입력해야 합니다.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "오류: " + ex.getMessage());
            }
        }
    }
    
    
}