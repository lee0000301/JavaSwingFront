package cse.hotel.client.ui.reservation;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.common.model.ClientReservation;
import cse.hotel.common.model.Room;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Vector;
import java.util.HashMap;
import java.util.ArrayList;

public class ReservationUI extends javax.swing.JFrame {

    private HashMap<String, JButton> roomButtonMap = new HashMap<>();
    private TableRowSorter<DefaultTableModel> sorter;
    private DefaultTableModel model;

    // --- 디자인 상수 (Color Palette) ---
    private final Color MAIN_BG = new Color(245, 245, 245); // 배경 (연회색)
    private final Color PANEL_BG = Color.WHITE;             // 패널 배경 (흰색)
    private final Color HEADER_BG = new Color(50, 50, 50);  // 헤더 (진한 회색)
    private final Color POINT_BLUE = new Color(52, 101, 164); // 포인트 (파란색)
    private final Color POINT_RED = new Color(220, 53, 69);   // 포인트 (빨강 - 취소용)
    private final Color TABLE_HEADER = new Color(230, 230, 230); // 테이블 헤더
    private final Color TEXT_DARK = new Color(60, 60, 60);  // 텍스트

    // UI 컴포넌트 (기존 로직과 연결될 변수들)
    // NetBeans 생성 코드를 대체하여 직접 초기화합니다.
    private JButton jButton1, jButton2, jButton3, jButton4, jButton5, jButton6;
    private JTextField jTextField2, jTextField3, jTextField4;
    private JTable jTable2;
    private JPanel jPanel8, jPanel9;

    public ReservationUI() {
        // 1. UI 초기화 (디자인 적용)
        initStylishComponents();

        setTitle("[관리자] 전체 예약 통합 관리");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700); // 대시보드 형태이므로 넓게 설정
        setLocationRelativeTo(null);
        getContentPane().setBackground(MAIN_BG);

        // 2. 테이블 모델 설정 (팀원 JTable에 연결)
        setupTableModel();

        // 3. 기능 연결 (서버 데이터 로드)
        loadRoomButtonsFromServer(); // 방 목록 가져와서 오른쪽 패널에 채우기
        loadData();                  // 예약 내역 가져와서 테이블에 채우기

        // 4. 이벤트 리스너 연결 (팀원 버튼에 우리 기능 연결)
        initListeners();

        setVisible(true);
    }

    // --- [UI 구성] 세련된 디자인 적용 ---
    private void initStylishComponents() {
        setLayout(new BorderLayout(0, 0));

        // A. 상단 헤더
        add(createHeaderPanel(), BorderLayout.NORTH);

        // B. 중앙 컨텐츠 (좌:검색 / 중:테이블 / 우:객실현황)
        JPanel contentPanel = new JPanel(new BorderLayout(15, 0));
        contentPanel.setBackground(MAIN_BG);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        contentPanel.add(createSearchPanel(), BorderLayout.WEST);
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);
        contentPanel.add(createRoomStatusPanel(), BorderLayout.EAST);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setPreferredSize(new Dimension(0, 60));
        panel.setBorder(new EmptyBorder(0, 25, 0, 15));

        JLabel titleLabel = new JLabel("전체 예약 통합 관리");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);

        // 닫기 버튼 (jButton1)
        jButton1 = createStyledButton("닫기", new Color(80, 80, 80), Color.WHITE);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(jButton1);
        panel.add(btnPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 15, 20, 15)
        ));

        JLabel lblTitle = new JLabel("검색 필터");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.gridx = 0; 

        // 필드 초기화
        jTextField2 = createStyledTextField(); // 고객 ID
        jTextField3 = createStyledTextField(); // 예약 번호
        jTextField4 = createStyledTextField(); // 날짜

        addFormField(formPanel, gbc, "고객 ID", jTextField2, 0);
        addFormField(formPanel, gbc, "예약 번호", jTextField3, 2);
        addFormField(formPanel, gbc, "날짜 (YYYY-MM-DD)", jTextField4, 4);

        panel.add(formPanel, BorderLayout.CENTER);

        // 조회 버튼 (jButton2)
        jButton2 = createStyledButton("조회", POINT_BLUE, Color.WHITE);
        jButton2.setPreferredSize(new Dimension(0, 45));
        
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setBackground(PANEL_BG);
        btnPanel.add(jButton2, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(new LineBorder(new Color(200, 200, 200), 1));

        // 테이블 초기화
        jTable2 = new JTable();
        // 기본 모델 (나중에 setupTableModel에서 덮어씌워짐, 초기화용)
        jTable2.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"예약번호", "고객ID", "체크인", "체크아웃", "객실번호", "상태", "타입"}));

        // 테이블 스타일링
        jTable2.setRowHeight(30);
        jTable2.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        jTable2.setShowVerticalLines(false);
        jTable2.setGridColor(new Color(230, 230, 230));
        jTable2.setSelectionBackground(new Color(232, 242, 254));
        jTable2.setSelectionForeground(Color.BLACK);

        JTableHeader header = jTable2.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setBackground(TABLE_HEADER);
        header.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        header.setForeground(TEXT_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        JScrollPane scrollPane = new JScrollPane(jTable2);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(null);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRoomStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAIN_BG);
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // 상단: 상태바 (jPanel9 대체)
        jPanel9 = new JPanel(new BorderLayout());
        jPanel9.setBackground(PANEL_BG);
        jPanel9.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblStatus = new JLabel("실시간 객실 현황");
        lblStatus.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        lblStatus.setForeground(TEXT_DARK);
        
        // 새로고침, 전체보기 버튼 (기존 jButton3, jButton4 유지)
        JPanel topBtnGroup = new JPanel(new GridLayout(1, 2, 5, 0));
        topBtnGroup.setBackground(PANEL_BG);
        jButton3 = createStyledButton("새로고침", new Color(240, 240, 240), TEXT_DARK);
        jButton4 = createStyledButton("전체보기", new Color(240, 240, 240), TEXT_DARK);
        jButton3.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        jButton4.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        topBtnGroup.add(jButton3);
        topBtnGroup.add(jButton4);

        jPanel9.add(lblStatus, BorderLayout.WEST);
        jPanel9.add(topBtnGroup, BorderLayout.EAST);

        // 중앙: 객실 버튼 그리드 (jPanel8)
        jPanel8 = new JPanel();
        jPanel8.setBackground(PANEL_BG);
        // 레이아웃은 loadRoomButtonsFromServer()에서 설정되므로 여기선 기본 설정만
        
        JScrollPane scrollPane = new JScrollPane(jPanel8);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // 하단: 관리 버튼 (jButton5, jButton6)
        JPanel adminBtnPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        adminBtnPanel.setBackground(MAIN_BG);
        adminBtnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        jButton5 = createStyledButton("신규 예약 등록", POINT_BLUE, Color.WHITE);
        jButton6 = createStyledButton("선택 예약 강제 취소", POINT_RED, Color.WHITE);
        
        adminBtnPanel.add(jButton5);
        adminBtnPanel.add(jButton6);

        panel.add(jPanel9, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(adminBtnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // --- 스타일 헬퍼 ---
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int yPos) {
        gbc.gridy = yPos;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        label.setForeground(Color.GRAY);
        panel.add(label, gbc);

        gbc.gridy = yPos + 1;
        panel.add(field, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 35));
        field.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)), 
            new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // =========================================================================
    // ▼ [기존 로직 유지] 아래 코드는 원본 로직을 100% 유지합니다. ▼
    // =========================================================================

    // --- [핵심] 팀원 UI 컴포넌트에 기능 연결 ---
    private void initListeners() {
        // [검색] 버튼 (jButton2)
        jButton2.addActionListener(e -> handleSearch());

        // [새로고침] 버튼 (팀원 UI에 없으면 생략, 필요시 추가)
        // jButtonRefresh.addActionListener(e -> { loadData(); refreshRoomStatus(); });
        // [신규 예약] 버튼 (jButton5)
        jButton5.addActionListener(e -> new NewReservationDialog(this).setVisible(true));

        // [선택 취소] 버튼 (jButton6)
        jButton6.addActionListener(e -> cancelSelectedReservation());

        // [닫기] 버튼 (jButton1)
        jButton1.addActionListener(e -> dispose());
    }

    // --- 검색 로직 ---
    private void handleSearch() {
        String id = jTextField2.getText();  // 고객 ID 필드
        String no = jTextField3.getText();  // 예약 번호 필드
        String date = jTextField4.getText();// 날짜 필드

        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        // (인덱스는 테이블 컬럼 순서에 따라 조정 필요: 0:예약번호, 1:고객ID, 2:체크인)
        if (!id.isEmpty()) {
            filters.add(RowFilter.regexFilter(id, 1));
        }
        if (!no.isEmpty()) {
            filters.add(RowFilter.regexFilter(no, 0));
        }
        if (!date.isEmpty()) {
            filters.add(RowFilter.regexFilter(date, 2));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    // --- 서버에서 방 목록 가져와 버튼 동적 생성 (오른쪽 패널 jPanel8 이용) ---
    private void loadRoomButtonsFromServer() {
        try {
            Request req = new Request("GET_ROOMS", null);
            Response res = HotelClient.sendRequest(req);

            if (res.isSuccess()) {
                List<Room> list = (List<Room>) res.getResultData();

                // 팀원분이 만들어둔 오른쪽 빈 패널(jPanel8)을 사용합니다.
                jPanel8.removeAll();
                roomButtonMap.clear();

                // 2열 그리드 레이아웃 적용
                jPanel8.setLayout(new GridLayout(0, 2, 5, 5));

                ActionListener roomListener = e -> {
                    JButton clickedBtn = (JButton) e.getSource();
                    handleRoomClick(clickedBtn.getText());
                };

                for (Room r : list) {
                    String roomNo = String.valueOf(r.getRoomNumber());
                    JButton btn = new JButton(roomNo);
                    btn.setPreferredSize(new Dimension(80, 40));
                    btn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
                    btn.setBackground(new Color(220, 220, 220)); // 기본 (회색)
                    btn.setForeground(TEXT_DARK);
                    btn.setBorder(BorderFactory.createLineBorder(new Color(180,180,180)));
                    btn.setFocusPainted(false);
                    btn.addActionListener(roomListener);

                    roomButtonMap.put(roomNo, btn);
                    jPanel8.add(btn);
                }
                jPanel8.revalidate();
                jPanel8.repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- 데이터 로드 ---
    public void loadData() {
        try {
            // 1. 먼저 '방 정보'를 다 가져옵니다. (타입을 알아내기 위해)
            Request roomReq = new Request("GET_ROOMS", null);
            Response roomRes = HotelClient.sendRequest(roomReq);
            List<Room> roomList = (List<Room>) roomRes.getResultData();

            // 2. 방 번호별로 타입을 쉽게 찾ける 있도록 맵(Map)에 정리해둡니다.
            HashMap<Integer, String> roomTypeMap = new HashMap<>();
            if (roomList != null) {
                for (Room r : roomList) {
                    roomTypeMap.put(r.getRoomNumber(), r.getRoomType());
                }
            }

            // 3. 이제 예약 목록을 가져옵니다.
            Request req = new Request("GET_ALL_RESERVATIONS", null);
            Response res = HotelClient.sendRequest(req);

            if (res.isSuccess()) {
                List<ClientReservation> list = (List<ClientReservation>) res.getResultData();
                model.setRowCount(0);
                
                for (ClientReservation r : list) {

                    String status = r.getStatus();
                    
                    if (status != null && (status.equals("CANCELLED") || status.equals("CANCELED"))) {
                        continue;
                    }

                    // ★ 핵심: 예약 정보에 있는 방 번호로 진짜 타입을 찾습니다!
                    String realType = roomTypeMap.getOrDefault(r.getRoomNumber(), "Standard");
                    
                    // 찾은 타입을 한글로 변환
                    String korType = convertTypeToKorean(realType);

                    model.addRow(new Object[]{
                        r.getReservationId(), 
                        r.getCustomerId(), 
                        r.getCheckInDate(),
                        r.getCheckOutDate(), 
                        r.getRoomNumber(), 
                        r.getStatus(), 
                        korType // <--- 이제 진짜 타입이 들어갑니다!
                    });
                }
                refreshRoomStatus();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ▼▼▼ [추가] 변환 헬퍼 메서드 (클래스 내부 아무데나 추가하세요) ▼▼▼
    private String convertTypeToKorean(String type) {
        if (type == null) {
            return "";
        }
        switch (type) {
            case "Standard":
                return "스탠다드";
            case "Deluxe":
                return "디럭스";
            case "Suite":
                return "스위트";
            case "Family":
                return "패밀리";
            default:
                return type; // 매칭 안 되면 그대로 출력
        }
    }

    // --- 버튼 색상 갱신 ---
    public void refreshRoomStatus() {
        for (JButton btn : roomButtonMap.values()) {
            btn.setBackground(new Color(220, 220, 220)); // 기본 상태 (연회색)
            btn.setForeground(TEXT_DARK);
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            String roomNo = String.valueOf(model.getValueAt(i, 4));
            String status = (String) model.getValueAt(i, 5);
            if (roomButtonMap.containsKey(roomNo) && !"CANCELLED".equals(status)) {
                // 점유중인 방은 눈에 띄는 색 (예: 주황색 계열)
                JButton btn = roomButtonMap.get(roomNo);
                btn.setBackground(new Color(255, 152, 0)); 
                btn.setForeground(Color.WHITE);
            }
        }
    }

    // --- 방 버튼 클릭 처리 ---
    private void handleRoomClick(String roomNo) {
        for (int i = 0; i < model.getRowCount(); i++) {
            String tRoom = String.valueOf(model.getValueAt(i, 4));
            String status = (String) model.getValueAt(i, 5);
            if (tRoom.equals(roomNo) && !"CANCELLED".equals(status)) {
                JOptionPane.showMessageDialog(this, roomNo + "호는 현재 예약중입니다.");
                return;
            }
        }
        // 빈 방이면 신규 예약 창으로 이동 (방 번호 전달)
        if (JOptionPane.showConfirmDialog(this, "빈 객실입니다. 예약하시겠습니까?", "예약", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new NewReservationDialog(this, roomNo).setVisible(true);
        }
    }

    // --- 예약 취소 ---
    private void cancelSelectedReservation() {
        int row = jTable2.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "선택된 예약이 없습니다.");
            return;
        }

        String id = (String) model.getValueAt(jTable2.convertRowIndexToModel(row), 0);
        System.out.println("클라이언트가 보낸 ID (공백 포함 여부 확인): [" + id + "]");
        
        if (JOptionPane.showConfirmDialog(this, "정말 취소하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                if (HotelClient.sendRequest(new Request("CANCEL_RESERVATION", id.trim())).isSuccess()) {
                    JOptionPane.showMessageDialog(this, "취소 완료");
                    loadData();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // --- 테이블 설정 및 더블클릭 이벤트 ---
    private void setupTableModel() {
        // 팀원분이 만든 jTable2의 모델을 가져와서 설정
        DefaultTableModel oldModel = (DefaultTableModel) jTable2.getModel();
        Vector<String> columnNames = new Vector<>();
        for (int i = 0; i < oldModel.getColumnCount(); i++) {
            columnNames.add(oldModel.getColumnName(i));
        }

        model = new DefaultTableModel(oldModel.getDataVector(), columnNames) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        jTable2.setModel(model);
        sorter = new TableRowSorter<>(model);
        jTable2.setRowSorter(sorter);
        jTable2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 셀 가운데 정렬
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<jTable2.getColumnCount(); i++){
            jTable2.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // 더블 클릭 시 상세정보(RoomManagement) 열기
        jTable2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = jTable2.getSelectedRow();
                    if (row >= 0) {
                        int modelRow = jTable2.convertRowIndexToModel(row);
                        new RoomManagement(
                                (String) model.getValueAt(modelRow, 0), // 예약번호
                                (String) model.getValueAt(modelRow, 1), // 고객ID
                                (String) model.getValueAt(modelRow, 2), // 체크인
                                (String) model.getValueAt(modelRow, 3), // 체크아웃
                                String.valueOf(model.getValueAt(modelRow, 4)), // 방번호
                                (String) model.getValueAt(modelRow, 5), // 상태
                                (String) model.getValueAt(modelRow, 6), // 타입
                                ReservationUI.this // 부모창 참조 전달
                        ).setVisible(true);
                    }
                }
            }
        });
    }
}