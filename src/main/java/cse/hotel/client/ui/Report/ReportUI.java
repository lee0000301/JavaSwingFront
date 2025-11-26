package cse.hotel.client.ui.report;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.common.model.ReportData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * [ReportUI] 호텔 점유율, 예약률, 매출 보고서 대시보드 (SFR-901 ~ 907)
 */
public class ReportUI extends JFrame {

    private final Color HEADER_BG = new Color(70, 70, 70);
    private final Color MAIN_BG = new Color(245, 245, 245);
    private final Color PANEL_BG = Color.WHITE;
    private final Color BUTTON_BLUE = new Color(52, 101, 164);
    
    // 컴포넌트
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JComboBox<String> comboPeriod;
    private JButton btnGenerate;
    private JButton btnExport;
    private JLabel lblOccupancy;
    private JLabel lblReservation;
    private JLabel lblTotalRevenue;
    private JTextArea txtException;
    private JTable detailTable;
    private DefaultTableModel tableModel;

    public ReportUI() {
        setTitle("[관리자] 호텔 통합 보고서");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(MAIN_BG);
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // 1. 상단: 제목 및 닫기 버튼
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // 2. 중앙: 좌측 (필터/KPI) + 우측 (탭: 상세 데이터, 그래프)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        centerPanel.setBackground(MAIN_BG);
        
        centerPanel.add(createFilterKpiPanel(), BorderLayout.WEST);
        centerPanel.add(createReportDisplayPanel(), BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        setupListeners();
    }
    
    // --- 상단 패널 (제목) ---
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("통합 경영 보고서");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        panel.add(titleLabel, BorderLayout.WEST);
        
        JButton btnClose = new JButton("닫기");
        btnClose.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        btnClose.setBackground(BUTTON_BLUE);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(HEADER_BG);
        btnPanel.add(btnClose);
        panel.add(btnPanel, BorderLayout.EAST);
        
        return panel;
    }

    // --- 좌측 패널 (필터 및 KPI) ---
    private JPanel createFilterKpiPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBackground(MAIN_BG);
        
        // 1. 필터 설정 (SFR-903)
        JPanel filterPanel = createFilterPanel();
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(filterPanel);
        
        panel.add(Box.createVerticalStrut(10));
        
        // 2. 핵심 지표 표시 (SFR-901)
        JPanel kpiPanel = createKpiPanel();
        kpiPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(kpiPanel);

        panel.add(Box.createVerticalStrut(10));
        
        // 3. 예외 보고서 (SFR-905)
        JPanel exceptionPanel = createExceptionPanel();
        exceptionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(exceptionPanel);
        
        panel.add(Box.createVerticalGlue()); // 하단 여백 채우기
        
        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "기간 및 조회 설정"));
        panel.setBackground(PANEL_BG);
        panel.setMaximumSize(new Dimension(300, 180));

        txtStartDate = new JTextField("2025-11-01");
        txtEndDate = new JTextField("2025-11-30");
        comboPeriod = new JComboBox<>(new String[]{"Daily", "Weekly", "Monthly"});
        btnGenerate = new JButton("보고서 생성");
        
        btnGenerate.setBackground(BUTTON_BLUE);
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setFocusPainted(false);
        
        panel.add(new JLabel("시작일 (YYYY-MM-DD):"));
        panel.add(txtStartDate);
        panel.add(new JLabel("종료일 (YYYY-MM-DD):"));
        panel.add(txtEndDate);
        panel.add(new JLabel("기간 단위:"));
        panel.add(comboPeriod);
        panel.add(new JLabel(""));
        panel.add(btnGenerate);
        
        return panel;
    }

    private JPanel createKpiPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "핵심 지표"));
        panel.setBackground(PANEL_BG);
        panel.setMaximumSize(new Dimension(300, 200));

        lblOccupancy = new JLabel("점유율: 0.0%");
        lblReservation = new JLabel("예약률: 0.0%");
        lblTotalRevenue = new JLabel("총 매출: 0원");

        // 매출 상세 라벨
        JLabel lblRoomRev = new JLabel("객실 매출: 0원");
        JLabel lblFnbRev = new JLabel("식음료 매출: 0원");
        
        panel.add(lblOccupancy);
        panel.add(lblReservation);
        panel.add(lblTotalRevenue);
        
        // SFR-907 상세 매출 표시
        JPanel revenueDetailPanel = new JPanel(new GridLayout(1, 2));
        revenueDetailPanel.setBackground(PANEL_BG);
        revenueDetailPanel.add(lblRoomRev);
        revenueDetailPanel.add(lblFnbRev);
        panel.add(revenueDetailPanel);
        
        // KPI 라벨 참조를 업데이트하기 위한 맵
        Map<String, JLabel> kpiLabels = new HashMap<>();
        kpiLabels.put("occupancy", lblOccupancy);
        kpiLabels.put("reservation", lblReservation);
        kpiLabels.put("totalRevenue", lblTotalRevenue);
        kpiLabels.put("roomRevenue", lblRoomRev);
        kpiLabels.put("fnbRevenue", lblFnbRev);
        btnGenerate.putClientProperty("kpiLabels", kpiLabels); // 액션 리스너에서 사용하기 위해 저장

        return panel;
    }
    
    private JPanel createExceptionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "예외 보고서"));
        panel.setBackground(PANEL_BG);
        panel.setPreferredSize(new Dimension(300, 200));

        txtException = new JTextArea("기간별 요금 변경 내역 표시...");
        txtException.setEditable(false);
        txtException.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(txtException);
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }

    // --- 우측 패널 (보고서 표시) ---
    private JPanel createReportDisplayPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(PANEL_BG);
        tabbedPane.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        
        // 1. 상세 데이터 테이블 (SFR-904)
        tabbedPane.addTab("기간별 상세 데이터 (표)", createTablePanel());
        
        // 2. 그래프 (SFR-904)
        tabbedPane.addTab("그래프 (매출/점유율)", createGraphPlaceholderPanel());

        // 3. 인쇄/파일 버튼 (SFR-906)
        btnExport = new JButton("보고서 인쇄/파일 저장");
        btnExport.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btnExport.setBackground(new Color(190, 190, 190));
        btnExport.setForeground(Color.BLACK);
        btnExport.setFocusPainted(false);
        btnExport.addActionListener(e -> JOptionPane.showMessageDialog(this, "인쇄/파일 저장 기능은 구현이 필요합니다.", "기능 안내", JOptionPane.INFORMATION_MESSAGE));
        
        JPanel wrapper = new JPanel(new BorderLayout(5, 5));
        wrapper.setBackground(MAIN_BG);
        wrapper.add(tabbedPane, BorderLayout.CENTER);
        wrapper.add(btnExport, BorderLayout.SOUTH);
        
        return wrapper;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        
        String[] columnNames = {"기간", "객실 매출", "식음료 매출", "총 매출", "점유율 (%)"};
        tableModel = new DefaultTableModel(columnNames, 0);
        detailTable = new JTable(tableModel);
        
        detailTable.setRowHeight(25);
        detailTable.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        
        panel.add(new JScrollPane(detailTable), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createGraphPlaceholderPanel() {
        // 그래프 라이브러리(JFreeChart 등)가 필요하므로 Placeholder로 대체
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        JLabel label = new JLabel("여기에 객실 점유율 및 매출 그래프가 표시됩니다");
        label.setForeground(Color.GRAY);
        label.setFont(new Font("맑은 고딕", Font.ITALIC, 16));
        panel.add(label);
        return panel;
    }

    // --- 이벤트 리스너 설정 ---
    private void setupListeners() {
        btnGenerate.addActionListener(e -> handleGenerateReport());
    }

    // --- 보고서 생성 요청 ---
    private void handleGenerateReport() {
        String startDateStr = txtStartDate.getText();
        String endDateStr = txtEndDate.getText();
        String periodType = (String) comboPeriod.getSelectedItem();
        
        // 간단한 유효성 검사 (날짜 포맷 검증은 생략)
        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "시작일과 종료일을 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 날짜 파싱 (실제 시스템에서는 yyyy-MM-dd 포맷으로 Date 객체 변환 필요)
            // 여기서는 단순성을 위해 String으로 전송하거나 mock Date 객체 사용

            
            // 1. 요청 파라미터 Map 생성
            Map<String, Object> params = new HashMap<>();
            params.put("startDate", startDateStr);
            params.put("endDate", endDateStr);
            params.put("periodType", periodType);
            
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
        // 1. KPI 업데이트 (SFR-901, SFR-907)
        Map<String, JLabel> kpiLabels = (Map<String, JLabel>) btnGenerate.getClientProperty("kpiLabels");
        
        kpiLabels.get("occupancy").setText(String.format("점유율: %.2f%%", data.getOccupancyRate()));
        kpiLabels.get("reservation").setText(String.format("예약률: %.2f%%", data.getReservationRate()));
        kpiLabels.get("totalRevenue").setText(String.format("총 매출: %,d원", (int)data.getTotalRevenue()));
        kpiLabels.get("roomRevenue").setText(String.format("객실 매출: %,d원", (int)data.getRoomRevenue()));
        kpiLabels.get("fnbRevenue").setText(String.format("식음료 매출: %,d원", (int)data.getFnbRevenue()));
        
        // 2. 예외 보고서 업데이트 (SFR-905)
        txtException.setText(data.getExceptionReportDetails());
        
        // 3. 상세 테이블 업데이트 (SFR-904)
        tableModel.setRowCount(0);
        List<Map<String, Object>> details = data.getPeriodDetails();
        
        if (details != null) {
            for (Map<String, Object> detail : details) {
                Object period = detail.get("period");
                int roomRev = (int) detail.get("roomRevenue");
                int fnbRev = (int) detail.get("fnbRevenue");
                String occupancy = (String) detail.get("occupancy");
                
                tableModel.addRow(new Object[]{
                    period,
                    String.format("%,d", roomRev),
                    String.format("%,d", fnbRev),
                    String.format("%,d", roomRev + fnbRev), // 총 매출 합산
                    occupancy
                });
            }
        }
    }
}