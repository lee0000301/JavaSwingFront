package cse.hotel.client.ui.clientReservation;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.*;
import cse.hotel.common.model.ClientReservation;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MyReservationUI extends JFrame {

    private String customerId;
    private JTable table;
    private DefaultTableModel tableModel;
    
    // 버튼 참조 (스타일 적용 후 이벤트 연결용)
    private JButton btnRefresh;
    private JButton btnCancel;
    private JButton btnClose;

    // --- 디자인 상수 (Color Palette) ---
    private final Color MAIN_BG = new Color(245, 245, 245); // 배경 (연회색)
    private final Color PANEL_BG = Color.WHITE;             // 패널 배경 (흰색)
    private final Color HEADER_BG = new Color(50, 50, 50);  // 헤더 (진한 회색)
    private final Color POINT_BLUE = new Color(52, 101, 164); // 포인트 (파란색)
    private final Color POINT_RED = new Color(220, 53, 69);   // 경고/취소 (빨강)
    private final Color TABLE_HEADER = new Color(230, 230, 230); // 테이블 헤더
    private final Color TEXT_DARK = new Color(60, 60, 60);  // 텍스트

    public MyReservationUI(String customerId) {
        this.customerId = customerId;
        
        setTitle("📋 내 예약 내역 확인");
        setSize(800, 500); // 가로 폭을 조금 넓힘
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // 전체 배경 설정
        getContentPane().setBackground(MAIN_BG);
        setLayout(new BorderLayout(0, 0));

        // 1. UI 컴포넌트 초기화 (디자인 적용)
        initStylishComponents();

        // 2. 이벤트 리스너 연결 (기존 로직 유지)
        setupListeners();

        // 3. 시작 시 데이터 로드 (기존 로직 유지)
        loadReservations();
        
        setVisible(true);
    }

    // --- [UI 구성] 세련된 디자인 적용 ---
    private void initStylishComponents() {
        // A. 상단 헤더
        add(createHeaderPanel(), BorderLayout.NORTH);

        // B. 중앙 테이블 패널
        add(createTablePanel(), BorderLayout.CENTER);

        // C. 하단 버튼 패널
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setPreferredSize(new Dimension(0, 60));
        panel.setBorder(new EmptyBorder(0, 25, 0, 0));

        // 아이콘 + 텍스트 조합
        JLabel titleLabel = new JLabel(customerId + "님의 예약 내역");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAIN_BG);
        // 여백을 주어 카드처럼 보이게 함
        panel.setBorder(new EmptyBorder(20, 20, 10, 20));

        // 모델 설정 (기존 컬럼 유지)
        String[] cols = {"예약번호", "객실", "체크인", "체크아웃", "상태", "금액"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);

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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        // 스크롤페인 테두리 깔끔하게
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panel.setBackground(MAIN_BG);
        panel.setBorder(new EmptyBorder(0, 20, 10, 20));

        // 버튼 생성 및 스타일 적용
        btnRefresh = createStyledButton("새로고침", POINT_BLUE, Color.WHITE);
        btnCancel = createStyledButton("선택한 예약 취소", POINT_RED, Color.WHITE);
        btnClose = createStyledButton("닫기", new Color(200, 200, 200), Color.BLACK);

        panel.add(btnRefresh);
        panel.add(btnCancel);
        panel.add(btnClose);

        return panel;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 40));
        return btn;
    }

    // =========================================================================
    // ▼ [기존 로직 유지] 이벤트 처리 및 서버 통신 (100% 동일) ▼
    // =========================================================================

    private void setupListeners() {
        // 1. 새로고침
        btnRefresh.addActionListener(e -> loadReservations());
        
        // 2. 닫기
        btnClose.addActionListener(e -> dispose());
        
        // 3. 예약 취소 버튼 로직 (원본 유지)
        btnCancel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "취소할 예약을 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String status = (String) tableModel.getValueAt(row, 4);
            if ("CANCELLED".equals(status)) {
                JOptionPane.showMessageDialog(this, "이미 취소된 예약입니다.", "안내", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String resId = (String) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "정말 예약을 취소하시겠습니까?\n(예약번호: " + resId + ")", 
                    "취소 확인", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                cancelReservation(resId);
            }
        });
    }

    private void loadReservations() {
        tableModel.setRowCount(0);
        try {
            Request req = new Request("GET_MY_RESERVATIONS", customerId);
            Response res = HotelClient.sendRequest(req);

            if (res.isSuccess()) {
                List<ClientReservation> list = (List<ClientReservation>) res.getResultData();
                for (ClientReservation r : list) {
                    tableModel.addRow(new Object[]{
                        r.getReservationId(),
                        r.getRoomNumber(),
                        r.getCheckInDate(),
                        r.getCheckOutDate(),
                        r.getStatus(), 
                        (int)r.getTotalPrice()
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelReservation(String resId) {
        try {
            Request req = new Request("CANCEL_CLIENT_RESERVATION", resId);
            Response res = HotelClient.sendRequest(req);
            
            if (res.isSuccess()) {
                JOptionPane.showMessageDialog(this, "예약이 취소되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                loadReservations(); // 목록 갱신
            } else {
                JOptionPane.showMessageDialog(this, "취소 실패: " + res.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}