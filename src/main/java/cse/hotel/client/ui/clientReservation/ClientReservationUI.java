package cse.hotel.client.ui.clientReservation;

import cse.hotel.common.model.*;
import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ClientReservationUI extends JFrame {

    private String loggedInCustomerId;
    private int selectedRoomNumber = -1;
    private int selectedRoomPricePerNight = 0;

    // UI 컴포넌트
    private JTextField txtCustomerId;
    private JTextField txtCheckIn;
    private JTextField txtCheckOut;

    // [추가된 요구사항] 객실 종류, 인원수
    private JComboBox<String> comboRoomType;
    private JTextField txtPersonCount;
    private JButton btnSearch; // 조회 버튼

    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotalPrice;
    private JButton btnReserve;

    public ClientReservationUI(String customerId) {
        this.loggedInCustomerId = customerId;

        setTitle("🛎️ 객실 예약 서비스 (요구사항 반영됨)");
        setSize(650, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. 상단: 검색 조건 패널 ---
        JPanel topPanel = new JPanel(new GridLayout(5, 2, 10, 10)); // 5행으로 늘림
        topPanel.setBorder(BorderFactory.createTitledBorder("1. 예약 조건 입력"));

        txtCustomerId = new JTextField(loggedInCustomerId);
        txtCustomerId.setEditable(false);
        txtCustomerId.setBackground(Color.LIGHT_GRAY);

        txtCheckIn = new JTextField(LocalDate.now().toString());
        txtCheckOut = new JTextField(LocalDate.now().plusDays(1).toString());

        // [추가] 객실 타입 선택
        String[] types = {"전체", "Single", "Double", "Suite"};
        comboRoomType = new JComboBox<>(types);

        // [추가] 인원수 입력
        txtPersonCount = new JTextField("2");

        topPanel.add(new JLabel("고객 ID:"));
        topPanel.add(txtCustomerId);
        topPanel.add(new JLabel("체크인 (YYYY-MM-DD):"));
        topPanel.add(txtCheckIn);
        topPanel.add(new JLabel("체크아웃 (YYYY-MM-DD):"));
        topPanel.add(txtCheckOut);
        topPanel.add(new JLabel("객실 종류:")); // 추가됨
        topPanel.add(comboRoomType);
        topPanel.add(new JLabel("인원 수:"));   // 추가됨
        topPanel.add(txtPersonCount);

        // 조회 버튼 별도 패널
        JPanel searchBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSearch = new JButton("🔍 조건에 맞는 빈 객실 조회");
        btnSearch.setBackground(new Color(255, 140, 0)); // 주황색
        btnSearch.setForeground(Color.WHITE);
        searchBtnPanel.add(btnSearch);

        // 상단 패널 조합
        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.add(topPanel, BorderLayout.CENTER);
        headerContainer.add(searchBtnPanel, BorderLayout.SOUTH);

        add(headerContainer, BorderLayout.NORTH);

        // --- 2. 중앙: 객실 목록 ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("2. 예약 가능 객실 선택"));

        String[] colNames = {"객실 번호", "타입", "1박 가격(원)"};
        tableModel = new DefaultTableModel(colNames, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        roomTable = new JTable(tableModel);
        centerPanel.add(new JScrollPane(roomTable), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- 3. 하단: 결제 및 예약 ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotalPrice = new JLabel("총 결제 금액: 0원");
        lblTotalPrice.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        lblTotalPrice.setForeground(Color.BLUE);
        pricePanel.add(lblTotalPrice);

        btnReserve = new JButton("예약 확정하기");
        btnReserve.setPreferredSize(new Dimension(150, 40));
        btnReserve.setBackground(new Color(70, 130, 180));
        btnReserve.setForeground(Color.WHITE);
        btnReserve.setEnabled(false); // 방 선택 전까지 비활성화

        bottomPanel.add(pricePanel, BorderLayout.NORTH);
        bottomPanel.add(btnReserve, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- 이벤트 연결 ---
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

        setVisible(true);
    }

    // --- 로직 메서드 ---
    private void searchAvailableRooms() {
        // 간단한 유효성 검사
        try {
            LocalDate.parse(txtCheckIn.getText());
            LocalDate.parse(txtCheckOut.getText());
            Integer.parseInt(txtPersonCount.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "날짜 형식(YYYY-MM-DD)과 인원수(숫자)를 확인하세요.");
            return;
        }

        tableModel.setRowCount(0); // 기존 목록 초기화

        // [원래는] 서버에 날짜/인원수를 보내서 필터링된 리스트를 받아야 함 (Req: SEARCH_ROOMS)
        // [지금은] 편의상 전체 방을 가져와서 클라이언트에서 필터링하는 방식으로 구현
        try {
            Request req = new Request("GET_ROOMS", null);
            Response res = HotelClient.sendRequest(req);

            if (res.isSuccess()) {
                List<Room> rooms = (List<Room>) res.getResultData();
                String selectedType = (String) comboRoomType.getSelectedItem();

                for (Room r : rooms) {
                    // 1. 상태 체크 (AVAILABLE 인지)
                    boolean isAvailable = (r.getStatus() == RoomStatus.AVAILABLE);

                    // 2. 타입 필터링 (전체가 아니면 타입이 일치해야 함)
                    boolean isTypeMatch = selectedType.equals("전체") || r.getRoomType().equalsIgnoreCase(selectedType);

                    // TODO: 인원수 제한 로직이 Room에 있다면 여기서 체크 (예: if (r.getCapacity() < personCount) skip)
                    if (isAvailable && isTypeMatch) {
                        tableModel.addRow(new Object[]{
                            r.getRoomNumber(),
                            r.getRoomType(),
                            String.format("%,d", r.getPrice())
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
        // 1. 방 선택 여부 확인
        if (selectedRoomNumber == -1) {
            JOptionPane.showMessageDialog(this, "먼저 목록에서 예약할 객실을 클릭해주세요!", "안내", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 3. 날짜 및 가격 확정
            LocalDate inDate = LocalDate.parse(txtCheckIn.getText().trim());
            LocalDate outDate = LocalDate.parse(txtCheckOut.getText().trim());
            long days = ChronoUnit.DAYS.between(inDate, outDate);
            double finalPrice = (double) (days * selectedRoomPricePerNight);

            // 4. 예약 객체 생성 (DTO)
            // (ClientReservation 클래스가 import 되어 있어야 합니다)
            ClientReservation reservation = new ClientReservation(
                            null, // ID는 서버에서 생성
                            loggedInCustomerId,
                            selectedRoomNumber,
                            inDate.toString(),
                            outDate.toString(),
                            finalPrice,
                            "PENDING"
                    );

            // 5. 서버로 전송
            Request req = new Request("MAKE_RESERVATION", reservation);
            Response res = HotelClient.sendRequest(req);

            // 6. 응답 처리
            if (res.isSuccess()) { ClientReservation saved = (ClientReservation) res.getResultData();
                String msg = String.format("✅ 예약이 완료되었습니다!\n\n예약번호: %s\n객실: %d호 (%s)\n일정: %s ~ %s",
                        saved.getReservationId(),
                        saved.getRoomNumber(),
                        (String) comboRoomType.getSelectedItem(), // 선택한 타입 표시
                        saved.getCheckInDate(),
                        saved.getCheckOutDate());

                JOptionPane.showMessageDialog(this, msg, "예약 성공", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // 성공 시 창 닫기
            } else {
                JOptionPane.showMessageDialog(this, "실패: " + res.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "오류 발생: " + e.getMessage());
        }
    }
}
