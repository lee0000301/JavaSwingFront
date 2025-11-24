// cse.hotel.client.ui.admin.AdminUserPanel.java (최종 수정)

package cse.hotel.client.ui.Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout; // 💡 오류 해결: BorderLayout 임포트
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.util.List;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.model.User;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.common.packet.UserManagementData; // 💡 새로 정의된 데이터 클래스 임포트

public class AdminUI extends JFrame {
    private final User currentUser;
    private final DefaultTableModel tableModel;
    private final JTable userTable;

    public AdminUI(User user) { 
        super("사용자 정보 관리");
        this.currentUser = user; // 전달받은 User 객체를 필드에 저장
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        
        // --- 이전 답변에서 누락된 초기화 코드 ---
        this.tableModel = new DefaultTableModel(new String[]{"ID", "Admin Status"}, 0);
        this.userTable = new JTable(tableModel);
        // ---------------------------------

        setLayout(new BorderLayout()); 
        add(new JScrollPane(userTable), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        
        loadUsers(); 
        
        // (만약 JFrame을 상속받았다면 setVisible(true); 등의 코드가 필요)
    }

    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        // 여기에 필요한 버튼(예: 사용자 추가, 삭제 버튼)을 생성하고 추가하는 로직이 들어갑니다.

        // 예시: 버튼을 생성하고 패널에 추가
        JButton addButton = new JButton("추가");
        JButton deleteButton = new JButton("삭제");
        
        addButton.addActionListener(e -> showAddUserDialog());
        deleteButton.addActionListener(e -> deleteSelectedUser());

        panel.add(addButton);
        panel.add(deleteButton);

        // 이 패널에 필요한 레이아웃 설정 (BorderLayout, FlowLayout 등)을 추가할 수 있습니다.
        return panel;
    }
    
    // 서버에서 사용자 목록을 불러와 테이블 갱신
    private void loadUsers() {
        // 1. 요청 데이터 생성
        UserManagementData data = new UserManagementData(UserManagementData.Action.GET_ALL_USERS);
        
        // 2. Request 객체에 데이터와 명령어("USER_MANAGE")를 담아 전송
        Request request = new Request("USER_MANAGE", data);
        
        try {
            Response response = HotelClient.sendRequest(request);
            
            if (response.isSuccess() && response.getResultData() instanceof UserManagementData) {
                UserManagementData resultData = (UserManagementData) response.getResultData();
                List<User> userList = resultData.getUserList();
                
                // 💡 디버그 로그 추가: 수신된 리스트가 null인지, 크기가 몇인지 확인
                if (userList == null) {
                    System.out.println("DEBUG: ❌ 서버로부터 userList가 null로 수신됨.");
                } else {
                    System.out.println("DEBUG: ✅ 수신된 사용자 수: " + userList.size());
                }
                
                tableModel.setRowCount(0); 
                if (userList != null) {
                    for (User user : userList) {
                        tableModel.addRow(new Object[]{user.getId(), user.isAdmin() ? "관리자" : "일반"});
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "목록 로드 실패: " + response.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
            tableModel.fireTableDataChanged();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "통신 오류: 서버 연결 실패", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    
private void showAddUserDialog() {
    // 1. 입력 필드 생성
    JTextField idField = new JTextField(15);
    JPasswordField pwField = new JPasswordField(15);
    JCheckBox adminCheckBox = new JCheckBox("관리자 권한 부여", false);

    // 2. 다이얼로그에 표시할 패널 구성
    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("ID:"));
    panel.add(idField);
    panel.add(new JLabel("비밀번호:"));
    panel.add(pwField);
    panel.add(adminCheckBox);

    // 3. 다이얼로그 표시
    int result = JOptionPane.showConfirmDialog(this, panel, 
        "새 사용자 추가", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    // 4. "확인" 버튼을 눌렀을 때 처리
    if (result == JOptionPane.OK_OPTION) {
        String id = idField.getText().trim();
        String pw = new String(pwField.getPassword()).trim();
        boolean isAdmin = adminCheckBox.isSelected();

        if (id.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID와 비밀번호를 모두 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 5. 서버에 사용자 추가 요청
        try {
            // 새 User 객체 생성
            User newUser = new User(id, pw, isAdmin);
            
            // 요청 데이터 생성
            UserManagementData data = new UserManagementData(UserManagementData.Action.ADD_USER, newUser);
            Request request = new Request("USER_MANAGE", data);
            
            Response response = HotelClient.sendRequest(request);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, response.getMessage() + "\n(" + (isAdmin ? "관리자" : "일반 사용자") + ")");
                loadUsers(); // 갱신된 목록 다시 로드하여 테이블에 반영
            } else {
                JOptionPane.showMessageDialog(this, "추가 실패: " + response.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "통신 오류: 서버 연결 실패", "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            }
        }
    }
    
    // 사용자 삭제 기능 (서버 통신 로직)
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            String userId = (String) userTable.getValueAt(selectedRow, 0);

            // 1. 요청 데이터 생성
            UserManagementData data = new UserManagementData(UserManagementData.Action.DELETE_USER, userId);

            // 2. Request 객체에 담아 전송
            Request request = new Request("USER_MANAGE", data);
            
            try {
                Response response = HotelClient.sendRequest(request);
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, response.getMessage());
                    loadUsers(); // 갱신된 목록 다시 로드
                } else {
                    JOptionPane.showMessageDialog(this, "삭제 실패: " + response.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "통신 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}