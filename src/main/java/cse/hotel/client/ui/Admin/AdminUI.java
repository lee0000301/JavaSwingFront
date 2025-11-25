package cse.hotel.client.ui.Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.model.User;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.common.packet.UserManagementData;

public class AdminUI extends JFrame {
    private final User currentUser;
    private final DefaultTableModel tableModel;
    private final JTable userTable;

    // 색상 정의 (세련된 테마)
    private final Color MAIN_BG = new Color(245, 245, 245); // 밝은 회색 배경
    private final Color HEADER_BG = new Color(70, 70, 70);  // 어두운 회색 헤더
    private final Color TABLE_HEADER_BG = new Color(230, 230, 230); // 테이블 헤더
    private final Color BUTTON_BLUE = new Color(52, 101, 164); // 포인트 블루
    private final Color BUTTON_HOVER = new Color(40, 80, 130); // 버튼 호버
    private final Color TEXT_DARK = new Color(50, 50, 50);  // 짙은 회색 텍스트

    public AdminUI(User user) {
        super("사용자 정보 관리");
        this.currentUser = user;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // 전체 배경색 설정
        getContentPane().setBackground(MAIN_BG);

        // 테이블 모델 초기화
        this.tableModel = new DefaultTableModel(new String[]{"사용자 ID", "권한 등급"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 수정 방지
            }
        };
        
        // 테이블 커스터마이징
        this.userTable = new JTable(tableModel);
        setupTableStyle();

        setLayout(new BorderLayout(0, 0));

        // 1. 상단 헤더 (제목)
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. 중앙 테이블 패널 (스크롤 포함)
        add(createTablePanel(), BorderLayout.CENTER);

        // 3. 하단 버튼 패널
        add(createButtonPanel(), BorderLayout.SOUTH);

        // 초기 데이터 로드
        loadUsers();

        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setPreferredSize(new Dimension(0, 60));
        panel.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel titleLabel = new JLabel("사용자 계정 관리");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subLabel = new JLabel("관리자 및 직원 계정 관리창");
        subLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        subLabel.setForeground(new Color(200, 200, 200));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subLabel);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAIN_BG);
        panel.setBorder(new EmptyBorder(20, 20, 10, 20)); // 여백 추가

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.getViewport().setBackground(Color.WHITE); // 테이블 배경 흰색
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1)); // 테두리 깔끔하게

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void setupTableStyle() {
        userTable.setRowHeight(30);
        userTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        userTable.setSelectionBackground(new Color(232, 242, 254)); // 선택 시 연한 파랑
        userTable.setSelectionForeground(Color.BLACK);
        userTable.setGridColor(new Color(230, 230, 230));
        userTable.setShowVerticalLines(false); // 세로선 제거로 깔끔함 강조

        // 헤더 스타일
        JTableHeader header = userTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        // 셀 정렬 (가운데 정렬)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        userTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(MAIN_BG);
        panel.setBorder(new EmptyBorder(0, 20, 10, 20));

        JButton addButton = createStyledButton("사용자 추가", BUTTON_BLUE);
        JButton deleteButton = createStyledButton("선택 삭제", new Color(80, 80, 80)); // 삭제는 짙은 회색

        addButton.addActionListener(e -> showAddUserDialog());
        deleteButton.addActionListener(e -> deleteSelectedUser());

        panel.add(addButton);
        panel.add(deleteButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20)); // 내부 여백으로 크기 조절
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 마우스 오버 효과 (선택 사항)
        // btn.addMouseListener(...); 
        return btn;
    }

    // --- 기존 로직 유지 ---

    // 서버에서 사용자 목록을 불러와 테이블 갱신
    private void loadUsers() {
        UserManagementData data = new UserManagementData(UserManagementData.Action.GET_ALL_USERS);
        Request request = new Request("USER_MANAGE", data);
        
        try {
            Response response = HotelClient.sendRequest(request);
            
            if (response.isSuccess() && response.getResultData() instanceof UserManagementData) {
                UserManagementData resultData = (UserManagementData) response.getResultData();
                List<User> userList = resultData.getUserList();
                
                if (userList == null) {
                    System.out.println("DEBUG: ❌ 서버로부터 userList가 null로 수신됨.");
                } else {
                    System.out.println("DEBUG: ✅ 수신된 사용자 수: " + userList.size());
                }
                
                tableModel.setRowCount(0); 
                if (userList != null) {
                    for (User user : userList) {
                        tableModel.addRow(new Object[]{user.getId(), user.isAdmin() ? "관리자 (Admin)" : "일반 사용자 (Staff)"});
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
        JTextField idField = new JTextField(15);
        JPasswordField pwField = new JPasswordField(15);
        JCheckBox adminCheckBox = new JCheckBox("관리자 권한 부여 (Admin)", false);
        
        // 다이얼로그 UI 개선
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("사용자 ID:"), gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("비밀번호:"), gbc);
        gbc.gridx = 1;
        panel.add(pwField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(adminCheckBox, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, 
            "새 사용자 추가", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText().trim();
            String pw = new String(pwField.getPassword()).trim();
            boolean isAdmin = adminCheckBox.isSelected();

            if (id.isEmpty() || pw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID와 비밀번호를 모두 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                User newUser = new User(id, pw, isAdmin);
                UserManagementData data = new UserManagementData(UserManagementData.Action.ADD_USER, newUser);
                Request request = new Request("USER_MANAGE", data);
                
                Response response = HotelClient.sendRequest(request);

                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, response.getMessage());
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "추가 실패: " + response.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "통신 오류: 서버 연결 실패", "오류", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 사용자를 선택해주세요.", "선택 안됨", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String userId = (String) userTable.getValueAt(selectedRow, 0);
        
        // 삭제 확인 팝업
        int confirm = JOptionPane.showConfirmDialog(this, 
                "사용자 ID [" + userId + "] 를 정말 삭제하시겠습니까?", 
                "삭제 확인", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
        if (confirm == JOptionPane.YES_OPTION) {
            UserManagementData data = new UserManagementData(UserManagementData.Action.DELETE_USER, userId);
            Request request = new Request("USER_MANAGE", data);
            
            try {
                Response response = HotelClient.sendRequest(request);
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, response.getMessage());
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "삭제 실패: " + response.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "통신 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}