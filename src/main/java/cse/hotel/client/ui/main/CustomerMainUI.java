package cse.hotel.client.ui.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.client.network.HotelClient;
import cse.hotel.client.ui.clientReservation.ClientReservationUI;
import cse.hotel.client.ui.clientReservation.MyReservationUI;
import cse.hotel.client.ui.food.FoodOrderUI;

public class CustomerMainUI extends JFrame {

    // 로그인한 사용자 ID 저장 변수
    private String loggedInCustomerId;

    // 생성자: 로그인 화면에서 ID를 받아옴
    public CustomerMainUI(String customerId) {
        this.loggedInCustomerId = customerId;

        setTitle("👋 고객 서비스 센터 - " + customerId + "님 환영합니다");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);

        // 레이아웃 설정 (2행 3열)
        setLayout(new GridLayout(2, 3, 15, 15));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- 버튼 생성 ---
        JButton btnBooking = createButton("1. 🛎️ 객실 예약", new Color(230, 240, 255));
        JButton btnRoomService = createButton("2. 🍽️ 룸서비스", new Color(255, 245, 230));
        JButton btnCheckIn = createButton("3. ➡️ 체크인", new Color(230, 255, 230));
        JButton btnCheckOut = createButton("4. ⬅️ 체크아웃", new Color(255, 230, 230));
        JButton btnMyRes = createButton("5. 📋 내 예약 확인", new Color(240, 240, 240));
        JButton btnExit = createButton("❌ 종료", new Color(200, 200, 200));

        // --- 화면에 추가 ---
        add(btnBooking);
        add(btnRoomService);
        add(btnCheckIn);
        add(btnCheckOut);
        add(btnMyRes);
        add(btnExit);

        // --- 이벤트 리스너 연결 ---

        // 1. 객실 예약
        btnBooking.addActionListener(e -> {
            new ClientReservationUI(this.loggedInCustomerId); // ID 전달
        });

        // 2. 룸서비스 주문
        btnRoomService.addActionListener(e -> {
            new FoodOrderUI(this.loggedInCustomerId).setVisible(true); // ID 전달
        });

        // 3. 체크인 (본인 확인 로직 포함)
        btnCheckIn.addActionListener(this::handleCheckIn);

        // 4. 체크아웃 (본인 확인 로직 포함)
        btnCheckOut.addActionListener(this::handleCheckOut);

        // 5. 내 예약 확인
        btnMyRes.addActionListener(e -> {
            new MyReservationUI(this.loggedInCustomerId); // ID 전달
        });

        // 6. 종료
        btnExit.addActionListener(e -> dispose());

        setVisible(true);
    }

    // 버튼 디자인 헬퍼 메서드
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        return btn;
    }

    // --- [핵심 기능] 체크인 핸들러 ---
    private void handleCheckIn(ActionEvent e) {
        String roomNumStr = JOptionPane.showInputDialog(this, 
                "체크인하실 객실 번호를 입력해주세요 (예: 201):", 
                "셀프 체크인", JOptionPane.QUESTION_MESSAGE);

        if (roomNumStr != null && !roomNumStr.trim().isEmpty()) {
            try {
                int roomNumber = Integer.parseInt(roomNumStr.trim());
                
                // 데이터 포장 (방 번호 + 내 ID)
                HashMap<String, Object> dataMap = new HashMap<>();
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

    // --- [핵심 기능] 체크아웃 핸들러 ---
    private void handleCheckOut(ActionEvent e) {
        String roomNumStr = JOptionPane.showInputDialog(this, 
                "체크아웃하실 객실 번호를 입력해주세요:", 
                "셀프 체크아웃", JOptionPane.QUESTION_MESSAGE);

        if (roomNumStr != null && !roomNumStr.trim().isEmpty()) {
            try {
                int roomNumber = Integer.parseInt(roomNumStr.trim());

                // 데이터 포장
                HashMap<String, Object> dataMap = new HashMap<>();
                dataMap.put("roomNumber", roomNumber);
                dataMap.put("customerId", this.loggedInCustomerId);

                // 서버 전송
                Request req = new Request("CHECK_OUT", dataMap);
                Response res = HotelClient.sendRequest(req);

                if (res.isSuccess()) {
                    JOptionPane.showMessageDialog(this, 
                        "👋 체크아웃 완료. 이용해 주셔서 감사합니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "⛔ 체크아웃 실패: " + res.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "객실 번호는 숫자만 입력해야 합니다.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "통신 오류: " + ex.getMessage());
            }
        }
    }

    // 독립 실행용 메인 (테스트용)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerMainUI("TEST-USER"));
    }
}