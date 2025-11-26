package cse.hotel.client.ui.report;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.common.model.ReportData;
import cse.hotel.client.ui.Report.DateChooserDialog; // ★ DateChooserDialog 임포트

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
  * [ReportUI] 호텔 점유율, 예약률, 매출 보고서 대시보드 (기간 단위 제거 및 DatePicker 통합)
  */
public class ReportUI extends JFrame {

    private final Color HEADER_BG = new Color(44, 62, 80); // Dark Navy
    private final Color MAIN_BG = new Color(240, 243, 245); // Light Gray Background
    private final Color PANEL_BG = Color.WHITE;
    private final Color BUTTON_ACCENT = new Color(52, 152, 219); // Blue Accent
    private final Color TEXT_ACCENT = new Color(39, 174, 96); // Green for Revenue
    
            // 컴포넌트 (사용되는 것만 남김)
            private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JButton btnGenerate;
    private JLabel lblOccupancy;
    private JLabel lblReservation;
    private JLabel lblTotalRevenue;
    private JLabel lblRoomRev;
    private JLabel lblFnbRev;
    private JTextArea txtException;

    public ReportUI() {
        setTitle("[관리자] 호텔 통합 보고서");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 680); // 크기 유지
        setLocationRelativeTo(null);
        getContentPane().setBackground(MAIN_BG);
        
        initComponents();
        setVisible(true);
    }
    
            private void initComponents() {
        setLayout(new BorderLayout());
        
        // 1. 상단: 제목 및 닫기 버튼
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // 2. 중앙: 스크롤 가능한 단일 컨테이너
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainContainer.setBackground(MAIN_BG);
        
        // 섹션 추가
        mainContainer.add(createFilterPanel());
        mainContainer.add(Box.createVerticalStrut(20));
        mainContainer.add(createKpiPanel());
        mainContainer.add(Box.createVerticalStrut(20));
        mainContainer.add(createExceptionPanel());
        mainContainer.add(Box.createVerticalGlue());
        
        add(new JScrollPane(mainContainer), BorderLayout.CENTER);
        
        setupListeners();
    }
    
            // --- 상단 패널 (제목) ---
            private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setPreferredSize(new Dimension(0, 50));
        
        JLabel titleLabel = new JLabel("통합 경영 보고서");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 15, 0, 0));
        panel.add(titleLabel, BorderLayout.WEST);
        
        JButton btnClose = new JButton("닫기");
        btnClose.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btnClose.setBackground(new Color(231, 76, 60)); 
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(HEADER_BG);
        btnPanel.add(btnClose);
        panel.add(btnPanel, BorderLayout.EAST);
        
        return panel;
    }

    // --- 필터 설정 패널 ---
            private JPanel createFilterPanel() {
        // ★ 4행에서 3행으로 변경됨 (기간 단위 제거)
        JPanel panel = new RoundedPanel(new GridLayout(3, 2, 10, 10), PANEL_BG, 10);
        panel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "기간 및 조회 설정")
                        ));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        txtStartDate = new JTextField("2025-11-01");
        txtEndDate = new JTextField("2025-11-30");
        btnGenerate = new JButton("📊 보고서 생성");
        
        btnGenerate.setBackground(BUTTON_ACCENT);
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setFocusPainted(false);
        
        panel.add(new JLabel("시작일 (YYYY-MM-DD):"));
        panel.add(txtStartDate);
        panel.add(new JLabel("종료일 (YYYY-MM-DD):"));
        panel.add(txtEndDate);
        // 기간 단위가 제거되고, 빈 줄을 채우고 버튼을 배치합니다.
        panel.add(new JLabel(""));
        panel.add(btnGenerate);
        
        return panel;
    }

    // --- 핵심 지표 (KPI) 패널 ---
            private JPanel createKpiPanel() {
        JPanel panel = new RoundedPanel(new BorderLayout(10, 10), PANEL_BG, 10);
        panel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "핵심 지표")
                        ));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 1. 점유율 / 예약률
        JPanel ratePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        ratePanel.setBackground(PANEL_BG);
        lblOccupancy = new JLabel("점유율: 0.0%");
        lblReservation = new JLabel("예약률: 0.0%");
        ratePanel.add(styleKpiLabel(lblOccupancy, new Color(255, 165, 0))); 
        ratePanel.add(styleKpiLabel(lblReservation, new Color(135, 206, 250))); 
        panel.add(ratePanel, BorderLayout.NORTH);

        // 2. 매출 요약 (총 매출)
        JPanel revenueSummaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        revenueSummaryPanel.setBackground(PANEL_BG);
        lblTotalRevenue = new JLabel("총 매출: 0원");
        lblTotalRevenue.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        lblTotalRevenue.setForeground(TEXT_ACCENT); 
        revenueSummaryPanel.add(lblTotalRevenue);
        panel.add(revenueSummaryPanel, BorderLayout.CENTER);

        // 3. 상세 매출 (객실/식음료)
        JPanel revenueDetailPanel = new JPanel(new GridLayout(1, 2));
        revenueDetailPanel.setBackground(PANEL_BG);
        lblRoomRev = new JLabel("객실 매출: 0원");
        lblFnbRev = new JLabel("식음료 매출: 0원");
        revenueDetailPanel.add(styleKpiLabel(lblRoomRev, new Color(100, 100, 100)));
        revenueDetailPanel.add(styleKpiLabel(lblFnbRev, new Color(100, 100, 100)));
        panel.add(revenueDetailPanel, BorderLayout.SOUTH);
        
        // KPI 라벨 참조를 업데이트하기 위한 맵
        Map<String, JLabel> kpiLabels = new HashMap<>();
        kpiLabels.put("occupancy", lblOccupancy);
        kpiLabels.put("reservation", lblReservation);
        kpiLabels.put("totalRevenue", lblTotalRevenue);
        kpiLabels.put("roomRevenue", lblRoomRev);
        kpiLabels.put("fnbRevenue", lblFnbRev);
        btnGenerate.putClientProperty("kpiLabels", kpiLabels); 

        return panel;
    }

    // KPI 라벨 스타일링 헬퍼
    private JLabel styleKpiLabel(JLabel label, Color fgColor) {
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        label.setForeground(fgColor);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    // --- 예외 보고서 패널 ---
            private JPanel createExceptionPanel() {
        JPanel panel = new RoundedPanel(new BorderLayout(), PANEL_BG, 10);
        panel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "예외 보고서")
                        ));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setPreferredSize(new Dimension(0, 200)); 

        txtException = new JTextArea("요금 정책 및 특이 사항이 여기에 표시됩니다.");
        txtException.setEditable(false);
        txtException.setLineWrap(true);
        txtException.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scroll = new JScrollPane(txtException);
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }

    // --- 이벤트 리스너 설정 (날짜 필드 클릭 이벤트 추가) ---
            private void setupListeners() {
        btnGenerate.addActionListener(e -> handleGenerateReport());

        // ★ 시작일 텍스트 필드 클릭 시 DateChooserDialog 실행
        txtStartDate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new DateChooserDialog(ReportUI.this, txtStartDate).setVisible(true);
            }
        });

        // ★ 종료일 텍스트 필드 클릭 시 DateChooserDialog 실행
        txtEndDate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new DateChooserDialog(ReportUI.this, txtEndDate).setVisible(true);
            }
        });
    }

    // --- 보고서 생성 요청 (기간 단위 제거 반영) ---
            private void handleGenerateReport() {
        String startDateStr = txtStartDate.getText();
        String endDateStr = txtEndDate.getText();
        // String periodType = (String) comboPeriod.getSelectedItem(); // ★ 제거됨
        
        // 간단한 유효성 검사 (날짜 포맷 검증은 생략)
        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "시작일과 종료일을 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. 요청 파라미터 Map 생성
            Map<String, Object> params = new HashMap<>();
            params.put("startDate", startDateStr);
            params.put("endDate", endDateStr);
            params.put("periodType", "Daily"); // ★ 기본값 'Daily'로 고정
            
            // 2. 서버 요청 (GENERATE_REPORT 명령)
            Request req = new Request("GENERATE_REPORT", params);
            Response res = HotelClient.sendRequest(req);
            
            if (res.isSuccess()) {
                ReportData data = (ReportData) res.getResultData();
                updateUIWithReportData(data);
                JOptionPane.showMessageDialog(this, "보고서 생성 완료.", "성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "보고서 생성 실패: " + res.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "서버 통신 오류: " + e.getMessage(), "통신 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- UI 갱신 ---
            private void updateUIWithReportData(ReportData data) {
        // 1. KPI 업데이트 
        Map<String, JLabel> kpiLabels = (Map<String, JLabel>) btnGenerate.getClientProperty("kpiLabels");
        
        kpiLabels.get("occupancy").setText(String.format("점유율: %.2f%%", data.getOccupancyRate()));
        kpiLabels.get("reservation").setText(String.format("예약률: %.2f%%", data.getReservationRate()));
        kpiLabels.get("totalRevenue").setText(String.format("총 매출: %,d원", (int)data.getTotalRevenue()));
        kpiLabels.get("roomRevenue").setText(String.format("객실 매출: %,d원", (int)data.getRoomRevenue()));
        kpiLabels.get("fnbRevenue").setText(String.format("식음료 매출: %,d원", (int)data.getFnbRevenue()));
        
        // 2. 예외 보고서 업데이트 
        txtException.setText(data.getExceptionReportDetails());
        }

    // 둥근 모서리 패널을 위한 커스텀 클래스 (기존 유지)
    class RoundedPanel extends JPanel {
        private int arc;

        public RoundedPanel(LayoutManager layout, Color color, int arc) {
            super(layout);
            setOpaque(false);
            setBackground(color);
            this.arc = arc;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(arc, arc);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.setColor(getBackground());
            graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);

            graphics.setColor(Color.LIGHT_GRAY);
            graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);
        }
    }
}