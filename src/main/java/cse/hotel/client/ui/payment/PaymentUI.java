package cse.hotel.client.ui.payment;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.model.Payment;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.client.ui.main.CustomerMainUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.UUID;

public class PaymentUI extends JFrame {

    private String customerId;
    private int roomNumber;
    private Map<String, Object> billData;
    private CustomerMainUI parentUI;

    private JTextField txtCardNum;
    private JRadioButton rbtnCard, rbtnCash;
    
    public PaymentUI(CustomerMainUI parent, String customerId, int roomNumber, Map<String, Object> billData) {
        this.parentUI = parent;
        this.customerId = customerId;
        this.roomNumber = roomNumber;
        this.billData = billData;

        setTitle("💳 체크아웃 결제");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 1. 청구서 내역 패널
        JPanel billPanel = new JPanel(new GridLayout(5, 2, 5, 10));
        billPanel.setBorder(BorderFactory.createTitledBorder("결제 상세 내역"));
        
        double roomFee = (double) billData.get("roomFee");
        double foodFee = (double) billData.get("foodFee");
        double total = (double) billData.get("totalAmount");
        String checkIn = (String) billData.get("checkIn");
        String checkOut = (String) billData.get("checkOut");

        billPanel.add(new JLabel("숙박 기간:"));
        billPanel.add(new JLabel(checkIn + " ~ " + checkOut));
        
        billPanel.add(new JLabel("객실 요금:"));
        billPanel.add(new JLabel(String.format("₩%,.0f", roomFee)));
        
        billPanel.add(new JLabel("룸서비스 요금:"));
        billPanel.add(new JLabel(String.format("₩%,.0f", foodFee)));
        
        billPanel.add(new JLabel("----------------"));
        billPanel.add(new JLabel("----------------"));
        
        JLabel lblTotal = new JLabel("총 결제 금액:");
        lblTotal.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        JLabel lblTotalVal = new JLabel(String.format("₩%,.0f", total));
        lblTotalVal.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        lblTotalVal.setForeground(Color.RED);
        
        billPanel.add(lblTotal);
        billPanel.add(lblTotalVal);
        
        add(billPanel, BorderLayout.NORTH);

        // 2. 결제 수단 패널
        JPanel methodPanel = new JPanel(new GridLayout(3, 1));
        methodPanel.setBorder(BorderFactory.createTitledBorder("결제 수단 선택"));
        
        ButtonGroup bg = new ButtonGroup();
        rbtnCash = new JRadioButton("현장 결제 (카운터)");
        rbtnCard = new JRadioButton("신용/체크카드", true);
        bg.add(rbtnCash); bg.add(rbtnCard);
        
        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cardPanel.add(new JLabel("카드 번호:"));
        txtCardNum = new JTextField(16);
        cardPanel.add(txtCardNum);
        
        methodPanel.add(rbtnCash);
        methodPanel.add(rbtnCard);
        methodPanel.add(cardPanel);
        
        // 라디오 버튼 이벤트 (현장결제 시 카드입력 비활성화)
        ActionListener radioListener = e -> {
            txtCardNum.setEnabled(rbtnCard.isSelected());
            if(!rbtnCard.isSelected()) txtCardNum.setText("");
        };
        rbtnCash.addActionListener(radioListener);
        rbtnCard.addActionListener(radioListener);

        add(methodPanel, BorderLayout.CENTER);

        // 3. 결제 버튼
        JButton btnPay = new JButton("결제 및 체크아웃 완료");
        btnPay.setBackground(new Color(50, 100, 200));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btnPay.setPreferredSize(new Dimension(100, 50));
        
        btnPay.addActionListener(e -> handlePayment());
        
        add(btnPay, BorderLayout.SOUTH);
        
        setVisible(true);
    }

   private void handlePayment() {
        // 1. 카드 정보 확인
        if (rbtnCard.isSelected() && txtCardNum.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "카드 번호를 입력해주세요.");
            return;
        }

        String method = rbtnCard.isSelected() ? "CARD" : "CASH";
        
        // 2. 데이터 준비 (안전하게 꺼내기)
        int days = 0;
        if (billData.get("stayDays") != null) {
            days = (int) billData.get("stayDays");
        }
        
        String foods = (String) billData.get("foodItems");
        if (foods == null) foods = "없음";

        // ▼▼▼ [추가] 날짜 정보 생성 ▼▼▼
        String inDate = (String) billData.get("checkIn");
        String outDate = (String) billData.get("checkOut");
        String stayPeriod = inDate + " ~ " + outDate; // 기간 문자열

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String paymentDate = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // 결제 일시
        // -----------------------------

        // 3. Payment 객체 생성 (필드 13개 - stayPeriod, paymentDate 포함)
        Payment payment = new Payment(
            "PAY-" + UUID.randomUUID().toString().substring(0, 8),
            customerId,
            roomNumber,
            stayPeriod,   // [신규] 기간 문자열
            inDate,
            outDate,
            paymentDate,  // [신규] 결제 일시
            days,
            foods,
            (double) billData.get("roomFee"),
            (double) billData.get("foodFee"),
            (double) billData.get("totalAmount"),
            method
        );

        try {
            // 4. 서버 전송
            Request req = new Request("PAY_AND_CHECKOUT", payment);
            Response res = HotelClient.sendRequest(req);

            if (res.isSuccess()) {
                JOptionPane.showMessageDialog(this, "✅ " + res.getMessage());
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "결제 실패: " + res.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}