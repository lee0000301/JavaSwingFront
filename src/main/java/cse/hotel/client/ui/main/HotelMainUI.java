package cse.hotel.client.ui.main;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import cse.hotel.client.ui.room.RoomUI;
import cse.hotel.client.ui.food.FoodUI;
import cse.hotel.client.ui.customer.CustomerUI;
import cse.hotel.client.ui.reservation.ReservationUI;
import cse.hotel.client.ui.Admin.AdminUI;
import cse.hotel.client.ui.report.ReportUI;
import cse.hotel.client.ui.login.LoginUI;
import cse.hotel.common.model.User;

public class HotelMainUI extends JFrame {
    private User user;

    // --- 디자인 상수 ---
    private final Color MAIN_BG = new Color(240, 242, 245); // 배경 (아주 연한 회색)
    private final Color HEADER_BG = new Color(45, 45, 45);  // 헤더 (매우 짙은 회색)
    private final Color CARD_BG = Color.WHITE;              // 메뉴 카드 배경
    private final Color POINT_BLUE = new Color(52, 101, 164); // 포인트 블루
    private final Color TEXT_TITLE = new Color(255, 255, 255); // 헤더 제목
    private final Color TEXT_DARK = new Color(60, 60, 60);  // 일반 텍스트

    public HotelMainUI(User user) {
        this.user = user;
        
        // 1. 창 기본 설정
        if (user.isAdmin()) {
            setTitle("호텔 관리 시스템 [관리자 모드] - " + user.getId());
        } else {
            setTitle("호텔 관리 시스템 [직원 모드] - " + user.getId());
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 대시보드 형태라 크기를 조금 더 넓게 잡음
        setSize(900, 600); 
        setLocationRelativeTo(null);
        
        // 전체 배경 설정
        getContentPane().setBackground(MAIN_BG);
        setLayout(new BorderLayout());

        // 2. UI 컴포넌트 초기화 (디자인 적용)
        initDashboardUI();

        setVisible(true);
    } 

    // --- [UI 구성] 세련된 대시보드 디자인 적용 ---
    private void initDashboardUI() {
        // A. 상단 헤더 (로고 및 환영 메시지)
        add(createHeaderPanel(), BorderLayout.NORTH);

        // B. 중앙 메뉴 그리드 (카드 형태 버튼)
        add(createMenuGridPanel(), BorderLayout.CENTER);
        
        // C. 하단 푸터 (로그아웃 버튼 등)
        // (현재는 로그아웃 버튼을 헤더에 통합하여 깔끔하게 처리)
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setPreferredSize(new Dimension(0, 70));
        panel.setBorder(new EmptyBorder(0, 25, 0, 25));

        // 왼쪽: 시스템 제목
        JLabel titleLabel = new JLabel("Hotel Management System");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_TITLE);
        
        // 오른쪽: 유저 정보 및 로그아웃
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        userPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("환영합니다, " + user.getId() + "님 (" + (user.isAdmin() ? "관리자" : "직원") + ")");
        userLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        userLabel.setForeground(Color.LIGHT_GRAY);
        
        JButton btnLogout = new JButton("로그아웃");
        btnLogout.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        btnLogout.setBackground(new Color(80, 80, 80));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(new EmptyBorder(5, 10, 5, 10));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(this::handleLoginManagement); // 기존 로그아웃 로직 연결

        userPanel.add(userLabel);
        userPanel.add(btnLogout);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(userPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMenuGridPanel() {
        // 2열 3행 그리드 레이아웃 (여백 20)
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        gridPanel.setBackground(MAIN_BG);
        gridPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // 메뉴 버튼 생성 (아이콘 + 텍스트)
        // 1. 예약 관리
        JButton btnRes = createDashboardCard("예약 관리", "📅", "예약 확인 및 취소");
        btnRes.addActionListener(this::handleReservationManagement);
        
        // 2. 고객 관리
        JButton btnCust = createDashboardCard("고객 정보 관리", "👥", "고객 조회 및 수정");
        btnCust.addActionListener(this::handleCustomerManagement);
        
        // 3. 객실 관리
        JButton btnRoom = createDashboardCard("객실 정보 관리", "🛏️", "객실 상태 및 요금 설정");
        btnRoom.addActionListener(this::handleRoomManagement);
        
        // 4. 식음료 관리
        JButton btnFood = createDashboardCard("식음료(F&B) 관리", "🍽️", "메뉴 및 재고 관리");
        btnFood.addActionListener(this::handleFoodMenuManagement);
        
        // 5. 사용자 관리
        JButton btnUser = createDashboardCard("사용자(직원) 관리", "👤", "시스템 접속 계정 관리");
        btnUser.addActionListener(this::handleAdminUIManagement);
        
        // 6. 보고서
        JButton btnReport = createDashboardCard("경영 보고서", "📊", "매출 및 점유율 통계");
        btnReport.addActionListener(this::handleReportManagement);

        // 권한에 따른 비활성화 처리 (예시)
        if (!user.isAdmin()) {
            btnUser.setEnabled(false);
            btnUser.setToolTipText("관리자 전용 기능입니다.");
            // 시각적으로 비활성화 느낌 주기
            btnUser.setBackground(new Color(230, 230, 230));
        }

        gridPanel.add(btnRes);
        gridPanel.add(btnCust);
        gridPanel.add(btnRoom);
        gridPanel.add(btnFood);
        gridPanel.add(btnUser);
        gridPanel.add(btnReport);

        return gridPanel;
    }

    /**
     * 대시보드용 카드 스타일 버튼 생성 헬퍼 메서드
     */
    private JButton createDashboardCard(String title, String icon, String desc) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setBackground(CARD_BG);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 테두리 설정 (깔끔한 라인)
        btn.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // 내부 내용 구성
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48)); // 이모지 폰트
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_DARK);
        
        JLabel descLabel = new JLabel(desc, SwingConstants.CENTER);
        descLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(CARD_BG);
        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        btn.add(iconLabel, BorderLayout.CENTER);
        btn.add(textPanel, BorderLayout.SOUTH);

        // 마우스 호버 효과 (선택 사항: 마우스 올리면 테두리 파란색)
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBorder(new CompoundBorder(
                        new LineBorder(POINT_BLUE, 2), // 파란 테두리
                        new EmptyBorder(14, 14, 14, 14)
                    ));
                    btn.setBackground(new Color(248, 250, 255)); // 아주 연한 파란 배경
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBorder(new CompoundBorder(
                        new LineBorder(new Color(200, 200, 200), 1),
                        new EmptyBorder(15, 15, 15, 15)
                    ));
                    btn.setBackground(CARD_BG);
                }
            }
        });

        return btn;
    }

    // --- 기존 이벤트 핸들러 로직 (100% 유지) ---
    
    private void handleFoodMenuManagement(ActionEvent e) {
        new FoodUI().setVisible(true);
    }
    private void handleRoomManagement(ActionEvent e) {
        new RoomUI().setVisible(true);
    }
    private void handleReservationManagement(ActionEvent e) {
        new ReservationUI().setVisible(true);
    }
    private void handleCustomerManagement(ActionEvent e) {
        new CustomerUI().setVisible(true);
    }
    private void handleAdminUIManagement(ActionEvent e) {
        new AdminUI(this.user).setVisible(true);
    }
    private void handleReportManagement(ActionEvent e) {
        new ReportUI().setVisible(true);
    }
    private void handleLoginManagement(ActionEvent e) {
        // 로그아웃 시 현재 창 닫고 로그인 창 열기
        this.dispose();
        new LoginUI().setVisible(true);
    }
}