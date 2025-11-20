package cse.hotel.client.ui;


import cse.hotel.client.ui.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import cse.hotel.client.ui.room.RoomUI;
import cse.hotel.client.ui.food.FoodUI;
import cse.hotel.client.ui.food.FoodUI;

public class HotelMainUI extends JFrame {

    public HotelMainUI() {
        // 창 기본 설정
        setTitle("⭐ 호텔 관리 시스템 - 메인 화면 ⭐");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 7개의 버튼과 제목 레이블을 효율적으로 배치하기 위해 크기 조정
        setSize(500, 450); 
        setLocationRelativeTo(null); // 화면 중앙에 배치
        
        // 레이아웃 설정: 9개 항목(제목 + 버튼 7개 + 종료 버튼)을 배치하기 위해 GridLayout 사용
        // (9행 1열, 간격 10px)
        setLayout(new GridLayout(9, 1, 10, 10)); 

        // --- 컴포넌트 생성 ---
        JLabel titleLabel = new JLabel("원하시는 관리 항목을 선택하세요.", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        
        // 요구사항에 따른 7가지 관리 모듈 버튼
        JButton btnReservation = new JButton("1. 예약 관리");           // (SFR-100)
        JButton btnCustomer = new JButton("2. 고객 정보 관리");       // (SFR-200)
        JButton btnRoom = new JButton("3. 객실 정보 관리");          // (SFR-400)
        JButton btnFoodMenu = new JButton("4. 식음료 메뉴 관리");      // (SFR-500)
        JButton btnUser = new JButton("5. 사용자 정보 관리");         // (SFR-300 등)
        JButton btnRoomService = new JButton("6. 룸서비스/레스토랑 관리"); // (SFR-600)
        JButton btnReport = new JButton("7. 보고서 관리");           // (SFR-700)
        
        JButton btnExit = new JButton("프로그램 종료");

        // --- 컴포넌트 추가 ---
        add(titleLabel);
        add(btnReservation);
        add(btnCustomer);
        add(btnRoom);
        add(btnFoodMenu);
        add(btnUser);
        add(btnRoomService);
        add(btnReport);
        add(btnExit);

        // --- 이벤트 리스너 연결 ---
        
        // 구현된 모듈 연결
        btnFoodMenu.addActionListener(this::handleFoodMenuManagement);
        btnRoom.addActionListener(this::handleRoomManagement);
        btnReservation.addActionListener(this::handleReservationManagement);
        
        
        // 미구현 모듈들은 알림 메시지 연결
        btnCustomer.addActionListener(this::showNotImplementedAlert);
        btnUser.addActionListener(this::showNotImplementedAlert);
        btnRoomService.addActionListener(this::showNotImplementedAlert);
        btnReport.addActionListener(this::showNotImplementedAlert);
        
        // 종료 버튼 액션
        btnExit.addActionListener(e -> System.exit(0));
    }

    // --- 이벤트 핸들러 구현 ---
    
    /**
     * 식음료 메뉴 관리 모듈 실행
     */
    private void handleFoodMenuManagement(ActionEvent e) {
        // FoodUI 객체를 생성하고 보이게 합니다.
        new FoodUI().setVisible(true);
    }
    private void handleRoomManagement(ActionEvent e) {
        // RoomUI 객실 관리 모듈 실행
        new RoomUI().setVisible(true);
    }
     private void handleReservationManagement(ActionEvent e) {
        // RoomUI 객실 관리 모듈 실행
//        new ReservationUI().setVisible(true);
    }
    /**
     * 미구현 모듈 클릭 시 알림 메시지 표시
     */
    private void showNotImplementedAlert(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        String moduleName = source.getText().substring(3); // "1. " 등을 제거
        
        JOptionPane.showMessageDialog(this, 
            moduleName + " 모듈은 현재 개발 중입니다.", 
            "⚠️ 모듈 준비 중", JOptionPane.INFORMATION_MESSAGE);
    }
}