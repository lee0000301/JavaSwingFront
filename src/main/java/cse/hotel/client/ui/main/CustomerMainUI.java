package cse.hotel.client.ui.main; // 메인 실행 패키지에 위치

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import cse.hotel.client.ui.clientReservation.ClientReservationUI;
import cse.hotel.client.ui.clientReservation.MyReservationUI;


public class CustomerMainUI extends JFrame {

    public CustomerMainUI() {
        super("👋 고객 서비스 센터");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 이 창만 닫고 서버는 유지
        setSize(450, 300); 
        setLocationRelativeTo(null); 
        
        // 4개 버튼을 깔끔하게 배치하기 위해 GridLayout 사용
        setLayout(new GridLayout(2, 3, 20, 20)); // 3행 2열, 간격 20px

        // --- 컴포넌트 생성 ---
        JButton btnBooking = new JButton("1. 🛎️ 객실 예약");
        JButton btnMyRes = new JButton("5. 📋 내 예약 확인");
        JButton btnRoomService = new JButton("2. 🍽️ 룸서비스 주문");
        JButton btnCheckIn = new JButton("3. ➡️ 체크인");
        JButton btnCheckOut = new JButton("4. ⬅️ 체크아웃");

        // --- 디자인 및 추가 ---
        add(btnBooking);
        add(btnRoomService);
        add(btnCheckIn);
        add(btnCheckOut);
        add(btnMyRes);

        // --- 이벤트 리스너 연결 ---
        btnBooking.addActionListener(e -> {
    // 실제로는 로그인 후 저장된 ID를 써야 하지만, 지금은 테스트용 ID 사용
    // (나중에 로그인 기능 만들면 그 변수로 교체하면 됩니다)
              String tempCustomerId = "CUST-TEST-001";
               new ClientReservationUI(tempCustomerId); });
        
        btnMyRes.addActionListener(e -> {
   // 테스트용 아이디 직접 입력
    String tempId = "CUST-TEST-001"; 
    new MyReservationUI(tempId); });
        
        btnRoomService.addActionListener(e -> handleAction("룸서비스 주문"));
        btnCheckIn.addActionListener(this::handleCheckIn);
        btnCheckOut.addActionListener(this::handleCheckOut);

        setVisible(true);
    }
    
    // --- 기능별 핸들러 ---
    
    // (1, 2번 버튼용) 미구현된 UI 대신 알림 처리
    private void handleAction(String action) {
        JOptionPane.showMessageDialog(this, action + " 기능 UI는 현재 개발 중입니다.", 
                                      "안내", JOptionPane.INFORMATION_MESSAGE);
        // [TODO]: 실제로는 각 기능의 UI를 띄워야 함 (e.g., new ReservationBookingUI().setVisible(true);)
    }

    // 3. 체크인 버튼 핸들러 (Room 모듈의 CHECK_IN 명령 사용)
    private void handleCheckIn(ActionEvent e) {
        String roomNumStr = JOptionPane.showInputDialog(this, "체크인할 객실 번호를 입력하세요 (예: 101):");
        if (roomNumStr != null && !roomNumStr.trim().isEmpty()) {
            try {
                int roomNumber = Integer.parseInt(roomNumStr);
                // [TODO]: HotelClient를 통해 CHECK_IN 명령을 서버로 전송하는 로직 구현 필요
                JOptionPane.showMessageDialog(this, 
                    "체크인 요청을 서버로 전송합니다: " + roomNumber + "호", 
                    "진행 중", JOptionPane.INFORMATION_MESSAGE);
                
                // Example of what the code would look like:
                /*
                Request request = new Request("CHECK_IN", roomNumber);
                Response response = HotelClient.sendRequest(request);
                if (response.isSuccess()) { ... }
                */
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "객실 번호는 숫자만 입력해야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // 4. 체크아웃 버튼 핸들러 (Room 모듈의 CHECK_OUT 명령 사용)
    private void handleCheckOut(ActionEvent e) {
        String roomNumStr = JOptionPane.showInputDialog(this, "체크아웃할 객실 번호를 입력하세요 (예: 101):");
        if (roomNumStr != null && !roomNumStr.trim().isEmpty()) {
            // [TODO]: HotelClient를 통해 CHECK_OUT 명령을 서버로 전송하는 로직 구현 필요
            JOptionPane.showMessageDialog(this, 
                "체크아웃 요청을 서버로 전송합니다: " + roomNumStr + "호", 
                "진행 중", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // --- 독립 실행을 위한 main 메서드 (테스트용) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerMainUI::new);
    }
}