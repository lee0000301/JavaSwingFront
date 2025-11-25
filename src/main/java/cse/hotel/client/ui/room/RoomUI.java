package cse.hotel.client.ui.room;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.common.model.Room;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * [RoomUI] - 객실 정보 관리 화면 (JFrame)
 * 디자인 리팩토링: 모던 대시보드 스타일 적용
 */
public class RoomUI extends JFrame {
    
    // --- GUI 컴포넌트 선언 ---
    private JTextArea roomListArea;
    private JTextField roomNumberField;
    private JButton checkInButton;
    private JButton checkOutButton;
    private JButton finishCleaningButton;
    private JTextField roomTypeField;
    private JTextField roomPriceField;
    private JButton findButton;
    private JButton createButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton backButton; 

    // --- 디자인 상수 (Color Palette) ---
    private final Color MAIN_BG = new Color(245, 245, 245); // 배경 (연회색)
    private final Color PANEL_BG = Color.WHITE;             // 패널 배경 (흰색)
    private final Color HEADER_BG = new Color(50, 50, 50);  // 헤더 (진한 회색)
    private final Color POINT_BLUE = new Color(52, 101, 164); // 포인트 (파란색)
    private final Color POINT_GREEN = new Color(40, 167, 69); // 상태 변경 (녹색)
    private final Color POINT_RED = new Color(220, 53, 69);   // 삭제 (빨강)
    private final Color TEXT_DARK = new Color(60, 60, 60);  // 텍스트

    public RoomUI() {
        super("객실 정보 관리 - 클라이언트 모드");
        
        // 기본 프레임 설정
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700); // 가로로 조금 더 넓게
        setLocationRelativeTo(null);
        getContentPane().setBackground(MAIN_BG);
        setLayout(new BorderLayout(0, 0));

        // 1. UI 초기화 (디자인 적용)
        initStylishComponents();
        
        // 2. 초기 데이터 로드 (기존 로직)
        refreshRoomList(); 
        
        setVisible(true);
    }

    // --- [UI 구성] 세련된 디자인 적용 ---
    private void initStylishComponents() {
        // A. 상단 헤더
        add(createHeaderPanel(), BorderLayout.NORTH);

        // B. 중앙 컨텐츠 (좌측: 관리 패널 / 우측: 목록 패널)
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(MAIN_BG);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        contentPanel.add(createLeftControlPanel(), BorderLayout.WEST);
        contentPanel.add(createRightListPanel(), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setPreferredSize(new Dimension(0, 60));
        panel.setBorder(new EmptyBorder(0, 25, 0, 15));

        JLabel titleLabel = new JLabel("객실 통합 관리 시스템");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        
        // 메인 메뉴 버튼을 헤더 우측에 배치
        backButton = createStyledButton("메인 메뉴", new Color(80, 80, 80), Color.WHITE);
        backButton.addActionListener(this::jButton_BackActionPerformed);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(backButton);
        panel.add(btnPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createLeftControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAIN_BG);
        panel.setPreferredSize(new Dimension(350, 0));

        // 1. 객실 상태 변경 섹션
        JPanel statusPanel = createSectionPanel("객실 상태 변경");
        
        JPanel statusForm = new JPanel(new GridBagLayout());
        statusForm.setBackground(PANEL_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0; gbc.weightx = 1.0;

        roomNumberField = createStyledTextField();
        addLabeledField(statusForm, gbc, "대상 객실 번호:", roomNumberField, 0);

        JPanel statusBtnPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        statusBtnPanel.setBackground(PANEL_BG);
        checkInButton = createStyledButton("체크인", POINT_GREEN, Color.WHITE);
        checkOutButton = createStyledButton("체크아웃", new Color(23, 162, 184), Color.WHITE); // Cyan 계열
        finishCleaningButton = createStyledButton("청소 완료", new Color(108, 117, 125), Color.WHITE); // Grey

        statusBtnPanel.add(checkInButton);
        statusBtnPanel.add(checkOutButton);
        statusBtnPanel.add(finishCleaningButton);
        
        gbc.gridy = 2;
        statusForm.add(statusBtnPanel, gbc);
        
        statusPanel.add(statusForm, BorderLayout.CENTER);
        panel.add(statusPanel);

        panel.add(Box.createVerticalStrut(20)); // 간격

        // 2. 객실 정보 관리 섹션
        JPanel adminPanel = createSectionPanel("객실 정보 관리 (CRUD)");
        
        JPanel adminForm = new JPanel(new GridBagLayout());
        adminForm.setBackground(PANEL_BG);
        
        roomTypeField = createStyledTextField();
        roomPriceField = createStyledTextField();
        
        addLabeledField(adminForm, gbc, "객실 타입:", roomTypeField, 0);
        addLabeledField(adminForm, gbc, "객실 가격:", roomPriceField, 2);

        // 버튼 그룹 1 (조회, 생성)
        JPanel adminBtnPanel1 = new JPanel(new GridLayout(1, 2, 5, 0));
        adminBtnPanel1.setBackground(PANEL_BG);
        findButton = createStyledButton("정보 불러오기", POINT_BLUE, Color.WHITE);
        createButton = createStyledButton("신규 등록", POINT_BLUE, Color.WHITE);
        adminBtnPanel1.add(findButton);
        adminBtnPanel1.add(createButton);

        // 버튼 그룹 2 (수정, 삭제)
        JPanel adminBtnPanel2 = new JPanel(new GridLayout(1, 2, 5, 0));
        adminBtnPanel2.setBackground(PANEL_BG);
        updateButton = createStyledButton("정보 수정", new Color(255, 193, 7), Color.BLACK); // Yellow
        deleteButton = createStyledButton("객실 삭제", POINT_RED, Color.WHITE);
        adminBtnPanel2.add(updateButton);
        adminBtnPanel2.add(deleteButton);

        gbc.gridy = 4;
        adminForm.add(adminBtnPanel1, gbc);
        gbc.gridy = 5;
        adminForm.add(Box.createVerticalStrut(5), gbc);
        gbc.gridy = 6;
        adminForm.add(adminBtnPanel2, gbc);

        adminPanel.add(adminForm, BorderLayout.CENTER);
        panel.add(adminPanel);
        
        // 이벤트 리스너 연결 (기존 로직 연결)
        setupListeners();

        return panel;
    }

    private JPanel createRightListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        // 둥근 테두리 효과
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTitle = new JLabel("실시간 객실 현황");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        roomListArea = new JTextArea();
        roomListArea.setEditable(false);
        roomListArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // 고정폭 폰트 사용
        roomListArea.setForeground(new Color(50, 50, 50));
        roomListArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(roomListArea);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // --- 스타일 헬퍼 메서드 ---
    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        lblTitle.setForeground(POINT_BLUE);
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        return panel;
    }

    private void addLabeledField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int yPos) {
        gbc.gridy = yPos;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        label.setForeground(TEXT_DARK);
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
            new EmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- [로직] 기존 코드 100% 유지 (이벤트 연결 및 처리) ---
    private void setupListeners() {
        checkInButton.addActionListener(e -> handleCheckIn());
        checkOutButton.addActionListener(e -> handleCheckOut());
        finishCleaningButton.addActionListener(e -> handleFinishCleaning());
        findButton.addActionListener(e -> handleFind());
        createButton.addActionListener(e -> handleCreate());
        updateButton.addActionListener(e -> handleUpdate());
        deleteButton.addActionListener(e -> handleDelete());
    }

    // --- 헬퍼 메서드 ---
    private int getRoomNumberFromField() throws NumberFormatException {
        if (roomNumberField.getText().isEmpty()) {
            throw new NumberFormatException("객실 번호가 비어있음");
        }
        return Integer.parseInt(roomNumberField.getText());
    }
    private void clearInputFields() {
        roomNumberField.setText("");
        roomTypeField.setText("");
        roomPriceField.setText("");
    }
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "성공", JOptionPane.INFORMATION_MESSAGE);
        refreshRoomList();
    }
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * 서버에 GET_ROOMS를 요청하여 객실 목록을 JTextArea에 출력합니다.
     */
    private void refreshRoomList() {
        roomListArea.setText("--- 객실 현황 로딩 중... ---\n");
        
        try {
            Request request = new Request("GET_ROOMS", null);
            Response response = HotelClient.sendRequest(request);

            if (response.isSuccess()) {
                List<Room> rooms = (List<Room>) response.getResultData();
                StringBuilder sb = new StringBuilder();
                
                // 테이블 헤더 느낌으로 출력
                sb.append(String.format("%-8s %-12s %-12s %-10s\n", "번호", "타입", "가격", "상태"));
                sb.append("----------------------------------------------------\n");
                
                for (Room room : rooms) {
                    sb.append(String.format("%-8d %-12s %-12d %-10s\n", 
                        room.getRoomNumber(), 
                        room.getRoomType(), 
                        room.getPrice(), 
                        room.getStatus()));
                }
                roomListArea.setText(sb.toString());
            } else {
                 showError("목록 조회 실패: " + response.getMessage());
            }
        } catch (Exception e) {
            showError("서버 통신 오류: " + e.getMessage());
        }
    }
    
    // --- 객실 상태 변경 핸들러 (CHECK_IN/OUT, CLEANING 요청) ---
    private void handleCheckIn() {
        try {
            int roomNumber = getRoomNumberFromField();
            Request request = new Request("CHECK_IN", roomNumber); // 새로운 명령 가정
            Response response = HotelClient.sendRequest(request);
            
            if (response.isSuccess()) showSuccess(roomNumber + "호 체크인 성공!");
            else showError("체크인 실패: " + response.getMessage());
        } catch (NumberFormatException ex) { showError("객실 번호를 숫자로 입력하세요."); 
        } catch (Exception ex) { showError("통신 오류: " + ex.getMessage()); }
    }
    
    private void handleCheckOut() {
        try {
            int roomNumber = getRoomNumberFromField();
            Request request = new Request("CHECK_OUT", roomNumber); // 새로운 명령 가정
            Response response = HotelClient.sendRequest(request);
            
            if (response.isSuccess()) showSuccess(roomNumber + "호 체크아웃 성공! (청소 필요)");
            else showError("체크아웃 실패: " + response.getMessage());
        } catch (NumberFormatException ex) { showError("객실 번호를 숫자로 입력하세요."); 
        } catch (Exception ex) { showError("통신 오류: " + ex.getMessage()); }
    }
    
    private void handleFinishCleaning() {
        try {
            int roomNumber = getRoomNumberFromField();
            Request request = new Request("FINISH_CLEANING", roomNumber); // 새로운 명령 가정
            Response response = HotelClient.sendRequest(request);
            
            if (response.isSuccess()) showSuccess(roomNumber + "호 청소 완료!");
            else showError("처리 실패: " + response.getMessage());
        } catch (NumberFormatException ex) { showError("객실 번호를 숫자로 입력하세요."); 
        } catch (Exception ex) { showError("통신 오류: " + ex.getMessage()); }
    }

    // --- 객실 정보 관리 핸들러 (CRUD 요청) ---
    
    private void handleFind() {
        try {
            int roomNumber = getRoomNumberFromField();
            
            // GET_ROOM_INFO 명령을 가정 (ClientHandler에서 findRoomByNumber를 호출하도록 구현 필요)
            Request request = new Request("GET_ROOM_INFO", roomNumber); 
            Response response = HotelClient.sendRequest(request);
            
            if (response.isSuccess()) {
                Room room = (Room) response.getResultData();
                roomTypeField.setText(room.getRoomType());
                roomPriceField.setText(String.valueOf(room.getPrice()));
                JOptionPane.showMessageDialog(this, roomNumber + "호 정보 불러오기 성공");
            } else { showError("정보 불러오기 실패: " + response.getMessage()); }
        } catch (NumberFormatException ex) { showError("객실 번호를 숫자로 입력하세요."); 
        } catch (Exception ex) { showError("통신 오류: " + ex.getMessage()); }
    }
    
    private void handleCreate() {
        try {
            int roomNumber = getRoomNumberFromField();
            String roomType = roomTypeField.getText();
            int price = Integer.parseInt(roomPriceField.getText());
            if (roomType.isEmpty()) { showError("객실 타입을 입력하세요."); return; }
            
            Room newRoom = new Room(roomNumber, roomType, price);
            
            Request request = new Request("ADD_ROOM", newRoom);
            Response response = HotelClient.sendRequest(request);
            
            if (response.isSuccess()) {
                showSuccess(roomNumber + "호 신규 등록 성공!");
                clearInputFields();
            } else { showError("등록 실패: " + response.getMessage()); }
            
        } catch (NumberFormatException ex) { showError("객실 번호와 가격을 숫자로 입력하세요."); 
        } catch (Exception ex) { showError("통신 오류: " + ex.getMessage()); }
    }
    
    private void handleUpdate() {
        try {
            int roomNumber = getRoomNumberFromField();
            String roomType = roomTypeField.getText();
            int price = Integer.parseInt(roomPriceField.getText());
            if (roomType.isEmpty()) { showError("객실 타입을 입력하세요."); return; }
            
            Room updatedRoom = new Room(roomNumber, roomType, price);
            
            Request request = new Request("UPDATE_ROOM", updatedRoom);
            Response response = HotelClient.sendRequest(request);
            
            if (response.isSuccess()) {
                showSuccess(roomNumber + "호 정보 수정 성공!");
                clearInputFields();
            } else { showError("수정 실패: " + response.getMessage()); }
            
        } catch (NumberFormatException ex) { showError("객실 번호와 가격을 숫자로 입력하세요."); 
        } catch (Exception ex) { showError("통신 오류: " + ex.getMessage()); }
    }
    
    private void handleDelete() {
        try {
            int roomNumber = getRoomNumberFromField();
            int choice = JOptionPane.showConfirmDialog(this, roomNumber + "호 객실을 정말 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                Request request = new Request("DELETE_ROOM", roomNumber);
                Response response = HotelClient.sendRequest(request);
                
                if (response.isSuccess()) {
                    showSuccess(roomNumber + "호 삭제 성공!");
                    clearInputFields();
                } else { showError("삭제 실패: " + response.getMessage()); }
            }
        } catch (NumberFormatException ex) { showError("객실 번호를 숫자로 입력하세요."); 
        } catch (Exception ex) { showError("통신 오류: " + ex.getMessage()); }
    }

    // 메인 메뉴로 돌아가기 (기존 로직 유지)
    private void jButton_BackActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
        // 메인 메뉴 창 띄우기 로직 (호출한 쪽에서 처리하거나 필요시 추가)
    }
}