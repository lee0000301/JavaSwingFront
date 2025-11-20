package cse.hotel.client.ui.room;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.common.model.Room;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * [RoomUI] - 객실 정보 관리 화면 (JFrame)
 * TCP 클라이언트 모드로 동작합니다.
 */
public class RoomUI extends javax.swing.JFrame {
    
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
    private JButton backButton; // 메인으로 돌아가기 버튼 추가

    // private final RoomService roomService; // <-- 삭제 (Service는 서버에 있음)

    public RoomUI() {
        super("객실 정보 관리 - 클라이언트 모드");
        // this.roomService = new RoomService(); // <-- 삭제
        initComponents();
        setSize(800, 600);
        refreshRoomList(); // 초기 목록 로드 (서버 통신)
        setLocationRelativeTo(null); 
    }

    
    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLayout(new BorderLayout());

        // --- 1. (중앙) 객실 목록 패널 ---
        roomListArea = new JTextArea();
        roomListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(roomListArea);
        add(scrollPane, BorderLayout.CENTER);

        // --- 2. (상단) 컨트롤 패널 (BoxLayout 사용) ---
        JPanel pnlControls = new JPanel();
        pnlControls.setLayout(new BoxLayout(pnlControls, BoxLayout.Y_AXIS));

        // 2-1. 객실 상태 변경 구역
        JPanel pnlStatusOps = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlStatusOps.setBorder(BorderFactory.createTitledBorder("객실 상태 변경"));
        roomNumberField = new JTextField(8);
        checkInButton = new JButton("체크인");
        checkOutButton = new JButton("체크아웃");
        finishCleaningButton = new JButton("청소 완료");
        
        pnlStatusOps.add(new JLabel("객실 번호 (대상):"));
        pnlStatusOps.add(roomNumberField);
        pnlStatusOps.add(checkInButton);
        pnlStatusOps.add(checkOutButton);
        pnlStatusOps.add(finishCleaningButton);

        // 2-2. 객실 정보 관리 구역
        JPanel pnlAdminOps = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlAdminOps.setBorder(BorderFactory.createTitledBorder("객실 정보 관리 (등록/수정/삭제)"));
        roomTypeField = new JTextField(10);
        roomPriceField = new JTextField(8);
        findButton = new JButton("정보 불러오기");
        createButton = new JButton("신규 등록");
        updateButton = new JButton("정보 수정");
        deleteButton = new JButton("객실 삭제");
        backButton = new JButton("메인 메뉴"); // 메인으로 돌아가기 버튼 추가

        pnlAdminOps.add(new JLabel("타입:"));
        pnlAdminOps.add(roomTypeField);
        pnlAdminOps.add(new JLabel("가격:"));
        pnlAdminOps.add(roomPriceField);
        pnlAdminOps.add(findButton);
        pnlAdminOps.add(createButton);
        pnlAdminOps.add(updateButton);
        pnlAdminOps.add(deleteButton);
        pnlAdminOps.add(backButton); // 버튼 추가
        
        pnlControls.add(pnlStatusOps);
        pnlControls.add(pnlAdminOps);
        add(pnlControls, BorderLayout.NORTH);
        
        // --- 이벤트 리스너 등록 (TCP 통신 로직 연결) ---
        checkInButton.addActionListener(e -> handleCheckIn());
        checkOutButton.addActionListener(e -> handleCheckOut());
        finishCleaningButton.addActionListener(e -> handleFinishCleaning());
        findButton.addActionListener(e -> handleFind());
        createButton.addActionListener(e -> handleCreate());
        updateButton.addActionListener(e -> handleUpdate());
        deleteButton.addActionListener(e -> handleDelete());
        backButton.addActionListener(this::jButton_BackActionPerformed); // 메인으로 돌아가기
        
        pack(); 
        setLocationRelativeTo(null); 
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
                StringBuilder sb = new StringBuilder("--- 객실 현황 (번호 / 타입 / 가격 / 상태) ---\n");
                for (Room room : rooms) {
                    // Room 객체에 toString() 메서드가 있다고 가정
                    sb.append(room.getRoomNumber()).append(" / ")
                      .append(room.getRoomType()).append(" / ")
                      .append(room.getPrice()).append("원 / ")
                      .append(room.getStatus()).append("\n");
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
        // 메인 메뉴 창 띄우기 (HotelMainUI가 있다고 가정하고, main 메서드는 이미 실행되었으므로 생략)
    }
}