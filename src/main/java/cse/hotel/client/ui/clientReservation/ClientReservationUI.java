package cse.hotel.client.ui.clientReservation;

import cse.hotel.common.model.*;
import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ClientReservationUI extends JFrame {

    private String loggedInCustomerId;
    private int selectedRoomNumber = -1;
    private int selectedRoomPricePerNight = 0;

    // UI 컴포넌트 (기존 로직과 연결될 변수들)
    private JTextField txtCustomerId;
    private JTextField txtCheckIn;
    private JTextField txtCheckOut;
    private JComboBox<String> comboRoomType;
    private JTextField txtPersonCount;
    private JButton btnSearch;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotalPrice;
    private JButton btnReserve;

    // --- 디자인 상수 ---
    private final Color MAIN_BG = new Color(245, 245, 245);
    private final Color PANEL_BG = Color.WHITE;
    private final Color HEADER_BG = new Color(50, 50, 50);
    private final Color POINT_BLUE = new Color(52, 101, 164);
    private final Color BUTTON_ORANGE = new Color(255, 140, 0);
    private final Color TABLE_HEADER = new Color(230, 230, 230);
    private final Color TEXT_DARK = new Color(60, 60, 60);

    public ClientReservationUI(String customerId) {
        this.loggedInCustomerId = customerId;

        setTitle("🛎️ 객실 예약 서비스");
        setSize(950, 750); // 넓이를 조금 확보
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // 전체 배경 설정
        getContentPane().setBackground(MAIN_BG);
        setLayout(new BorderLayout(0, 0));

        // 1. UI 초기화 및 배치 (디자인 적용)
        initStylishComponents();

        // 2. 이벤트 리스너 연결 (기존 로직 연결)
        setupListeners();

        setVisible(true);
    }

    // --- [UI 구성] 세련된 디자인 적용 ---
    private void initStylishComponents() {
        // A. 상단 헤더
        add(createHeaderPanel(), BorderLayout.NORTH);

        // B. 중앙 컨텐츠 (좌측: 검색조건 / 우측: 객실리스트)
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(MAIN_BG);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        contentPanel.add(createSearchPanel(), BorderLayout.WEST);
        contentPanel.add(createListPanel(), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
        
        // C. 하단 결제 바
        add(createBottomActionPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setPreferredSize(new Dimension(0, 60));
        panel.setBorder(new EmptyBorder(0, 25, 0, 0));

        JLabel titleLabel = new JLabel("Book Your Stay");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // 타이틀
        JLabel lblTitle = new JLabel("예약 조건 입력");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        // 입력 폼 (GridBagLayout)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.gridx = 0; 

        // 컴포넌트 초기화
        txtCustomerId = createStyledTextField();
        txtCustomerId.setText(loggedInCustomerId);
        txtCustomerId.setEditable(false);
        txtCustomerId.setBackground(new Color(240, 240, 240)); // ReadOnly 느낌

        txtCheckIn = createStyledTextField();
        txtCheckIn.setText(LocalDate.now().toString());

        txtCheckOut = createStyledTextField();
        txtCheckOut.setText(LocalDate.now().plusDays(1).toString());

        String[] types = {"전체", "스탠다드", "디럭스", "스위트", "패밀리"};
        comboRoomType = new JComboBox<>(types);
        comboRoomType.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        comboRoomType.setBackground(Color.WHITE);
        ((JComponent)comboRoomType.getRenderer()).setBorder(new EmptyBorder(5,5,5,5));

        txtPersonCount = createStyledTextField();
        txtPersonCount.setText("2");

        // 폼 추가
        addFormField(formPanel, gbc, "고객 ID", txtCustomerId, 0);
        addFormField(formPanel, gbc, "체크인 (YYYY-MM-DD)", txtCheckIn, 2);
        addFormField(formPanel, gbc, "체크아웃 (YYYY-MM-DD)", txtCheckOut, 4);
        
        // 콤보박스는 별도 처리
        gbc.gridy = 6;
        JLabel lblType = new JLabel("객실 종류");
        lblType.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        lblType.setForeground(Color.GRAY);
        formPanel.add(lblType, gbc);
        gbc.gridy = 7;
        formPanel.add(comboRoomType, gbc);

        addFormField(formPanel, gbc, "인원 수", txtPersonCount, 8);

        panel.add(formPanel, BorderLayout.CENTER);

        // 검색 버튼
        btnSearch = new JButton("빈 객실 조회");
        styleButton(btnSearch, BUTTON_ORANGE, Color.WHITE);
        btnSearch.setPreferredSize(new Dimension(0, 45));
        
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setBackground(PANEL_BG);
        btnPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        btnPanel.add(btnSearch, BorderLayout.CENTER);
        
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(new LineBorder(new Color(200, 200, 200), 1));

        // 테이블 모델
        String[] colNames = {"객실 번호", "타입", "1박 가격(원)"};
        tableModel = new DefaultTableModel(colNames, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        roomTable = new JTable(tableModel);

        // 테이블 스타일링
        roomTable.setRowHeight(35);
        roomTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        roomTable.setShowVerticalLines(false);
        roomTable.setGridColor(new Color(230, 230, 230));
        roomTable.setSelectionBackground(new Color(232, 242, 254));
        roomTable.setSelectionForeground(Color.BLACK);

        // 헤더 스타일
        JTableHeader header = roomTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setBackground(TABLE_HEADER);
        header.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        header.setForeground(TEXT_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        // 가운데 정렬
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<roomTable.getColumnCount(); i++){
            roomTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(roomTable);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(null);

        // 안내 라벨
        JLabel lblGuide = new JLabel("   조건을 입력하고 조회를 누르면 예약 가능한 객실이 표시됩니다.");
        lblGuide.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        lblGuide.setForeground(Color.GRAY);
        lblGuide.setPreferredSize(new Dimension(0, 40));
        
        panel.add(lblGuide, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomActionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setPreferredSize(new Dimension(0, 70));
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 25, 10, 25)
        ));

        lblTotalPrice = new JLabel("총 결제 금액: 0원");
        lblTotalPrice.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        lblTotalPrice.setForeground(POINT_BLUE);

        btnReserve = new JButton("예약 확정하기");
        styleButton(btnReserve, POINT_BLUE, Color.WHITE);
        btnReserve.setPreferredSize(new Dimension(180, 45));
        btnReserve.setEnabled(false);

        panel.add(lblTotalPrice, BorderLayout.WEST);
        panel.add(btnReserve, BorderLayout.EAST);

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

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // =========================================================================
    // ▼ [기존 로직 유지] 이벤트 및 비즈니스 로직 (100% 동일) ▼
    // =========================================================================

    private void setupListeners() {
        // 1. 조회 버튼 클릭 -> 방 목록 로드
        btnSearch.addActionListener(e -> searchAvailableRooms());

        // 2. 테이블 클릭 -> 가격 계산
        roomTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = roomTable.getSelectedRow();
                if (row != -1) {
                    selectedRoomNumber = (int) tableModel.getValueAt(row, 0);
                    String priceStr = tableModel.getValueAt(row, 2).toString().replace(",", "");
                    selectedRoomPricePerNight = Integer.parseInt(priceStr);
                    calculateTotalPrice();
                    btnReserve.setEnabled(true); // 선택 시 활성화
                }
            }
        });

        // 3. 예약 확정 버튼
        btnReserve.addActionListener(e -> processReservation());
    }

    private void searchAvailableRooms() {
        try {
            LocalDate.parse(txtCheckIn.getText());
            LocalDate.parse(txtCheckOut.getText());
            Integer.parseInt(txtPersonCount.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "날짜 형식(YYYY-MM-DD)과 인원수(숫자)를 확인하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0); 

        try {
            Request req = new Request("GET_ROOMS", null);
            Response res = HotelClient.sendRequest(req);

            if (res.isSuccess()) {
                List<Room> rooms = (List<Room>) res.getResultData();
                String selectedType = (String) comboRoomType.getSelectedItem();

                for (Room r : rooms) {
                    boolean isAvailable = (r.getStatus() == RoomStatus.AVAILABLE);
                    boolean isTypeMatch = selectedType.equals("전체") || r.getRoomType().equalsIgnoreCase(selectedType);

                    if (isAvailable && isTypeMatch) {
                        tableModel.addRow(new Object[]{
                            r.getRoomNumber(),
                            r.getRoomType(),
                            String.format("%,d", r.getPrice()) // 천단위 콤마는 로직에서 처리됨 (그대로 유지)
                        });
                    }
                }

                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "조건에 맞는 빈 객실이 없습니다.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateTotalPrice() {
        try {
            LocalDate inDate = LocalDate.parse(txtCheckIn.getText().trim());
            LocalDate outDate = LocalDate.parse(txtCheckOut.getText().trim());
            long days = ChronoUnit.DAYS.between(inDate, outDate);

            if (days <= 0) {
                lblTotalPrice.setText("날짜 범위 오류");
                btnReserve.setEnabled(false);
                return;
            }

            long total = days * selectedRoomPricePerNight;
            lblTotalPrice.setText(String.format("총 결제 금액: %,d원 (%d박)", total, days));
        } catch (Exception e) {
        }
    }

    private void processReservation() {
        if (selectedRoomNumber == -1) {
            JOptionPane.showMessageDialog(this, "먼저 목록에서 예약할 객실을 클릭해주세요!", "안내", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate inDate = LocalDate.parse(txtCheckIn.getText().trim());
            LocalDate outDate = LocalDate.parse(txtCheckOut.getText().trim());
            long days = ChronoUnit.DAYS.between(inDate, outDate);
            double finalPrice = (double) (days * selectedRoomPricePerNight);

            ClientReservation reservation = new ClientReservation(
                null, 
                loggedInCustomerId,
                selectedRoomNumber,
                inDate.toString(),
                outDate.toString(),
                finalPrice,
                "PENDING"
            );

            Request req = new Request("MAKE_RESERVATION", reservation);
            Response res = HotelClient.sendRequest(req);

            if (res.isSuccess()) { 
                ClientReservation saved = (ClientReservation) res.getResultData();
                String msg = String.format("✅ 예약이 완료되었습니다!\n\n예약번호: %s\n객실: %d호 (%s)\n일정: %s ~ %s",
                        saved.getReservationId(),
                        saved.getRoomNumber(),
                        (String) comboRoomType.getSelectedItem(),
                        saved.getCheckInDate(),
                        saved.getCheckOutDate());

                JOptionPane.showMessageDialog(this, msg, "예약 성공", JOptionPane.INFORMATION_MESSAGE);
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "실패: " + res.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "오류 발생: " + e.getMessage());
        }
    }
}