package cse.hotel.client.ui; // [수정] 패키지 변경

import cse.hotel.client.ui.*;
import cse.hotel.common.model.Customer; // [추가] 공통 모델
import cse.hotel.common.packet.Request; // [추가] 요청 패킷
import cse.hotel.common.packet.Response; // [추가] 응답 패킷
import cse.hotel.client.network.HotelClient; // [추가] 통신 모듈

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerManagerGUI extends JFrame {

    // 데이터를 제어하기 위해 멤버 변수로 승격
    private JTable customerTable;
    private DefaultTableModel tableModel;

    public CustomerManagerGUI() {
        setTitle("고객 정보 관리 시스템 (Client Mode)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 메인 메뉴에서 열리므로 EXIT 대신 DISPOSE 권장
        setSize(800, 500); 
        setLayout(new BorderLayout(5, 5));

        add(createSearchPanel(), BorderLayout.NORTH);
        add(createCenterContentPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        
        // 창이 켜지자마자 서버에서 목록 불러오기
        loadCustomerList(); 
    }
    
    // --- [핵심] 서버 통신 메서드: 고객 목록 불러오기 ---
    private void loadCustomerList() {
        tableModel.setRowCount(0); // 기존 테이블 초기화

        try {
            Request request = new Request("GET_ALL_CUSTOMERS", null);
            Response response = (Response)HotelClient.sendRequest(request);

            if (response.isSuccess()) {
                List<Customer> customerList = (List<Customer>) response.getResultData();
                for (Customer c : customerList) {
                    // 테이블 컬럼 순서: ID, 이름, 연락처, 등급(임시)
                    tableModel.addRow(new Object[]{
                        c.getCustomerId(), 
                        c.getName(), 
                        c.getPhoneNumber(), 
                        "SILVER" // 등급은 Customer 모델에 없어서 임시 값 넣음
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "목록 로드 실패: " + response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "서버 통신 오류: " + e.getMessage());
        }
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.add(new JLabel("검색:"));
        JTextField searchField = new JTextField(15);
        panel.add(searchField);
        
        JButton searchBtn = new JButton("🔍 검색");
        // 검색 기능은 나중에 구현 (GET_CUSTOMER_BY_NAME 등)
        searchBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "검색 기능 준비 중입니다."));
        panel.add(searchBtn);
        
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return panel;
    }

    private JSplitPane createCenterContentPanel() {
        String[] columnNames = {"ID", "이름", "연락처", "등급"};
        // 수정 불가한 테이블 모델
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 하나만 선택 가능
        JScrollPane listScrollPane = new JScrollPane(customerTable);
        
        // 상세 탭 패널 (기존 유지)
        JTabbedPane detailTabbedPane = new JTabbedPane();
        detailTabbedPane.addTab("기본 정보", createBasicInfoPanel());
        detailTabbedPane.addTab("예약/결제", createHistoryPanel("예약/결제 정보"));
        detailTabbedPane.addTab("미처리 내역", createHistoryPanel("미처리 내역"));
        detailTabbedPane.addTab("피드백", createFeedbackPanel());
        detailTabbedPane.addTab("등급 및 혜택", createTierPanel());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, detailTabbedPane);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.35); 
        return splitPane;
    }
    
    // --- 하위 패널 생성 메서드들은 UI 디자인 요소이므로 그대로 유지 ---
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("고객 상세 정보"));
        // (실제로는 테이블 선택 시 여기에 데이터를 채워주는 로직이 필요하지만, 일단 UI만 유지)
        panel.add(new JLabel("ID:")); panel.add(new JTextField(10));
        panel.add(new JLabel("이름:")); panel.add(new JTextField(10));
        panel.add(new JLabel("연락처:")); panel.add(new JTextField(10));
        panel.add(new JLabel("주소:")); panel.add(new JTextField(10));
        return panel;
    }

    private JPanel createHistoryPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = (title.contains("예약")) ? new String[]{"날짜", "내용", "상태"} : new String[]{"날짜", "사유", "담당자"};
        JTable table = new JTable(new DefaultTableModel(columns, 0));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        if (title.contains("미처리")) {
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPanel.add(new JButton("미처리 내역 기록"));
            panel.add(btnPanel, BorderLayout.NORTH);
        }
        return panel;
    }

    private JPanel createFeedbackPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"날짜", "유형", "요약"};
        JTable historyTable = new JTable(new DefaultTableModel(columns, 0));
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("새 피드백 입력"));
        JTextArea feedbackArea = new JTextArea(3, 20);
        inputPanel.add(new JScrollPane(feedbackArea), BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(new JButton("피드백 저장"));
        inputPanel.add(southPanel, BorderLayout.SOUTH);
        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel createTierPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel tierLabel = new JLabel("현재 고객 등급: SILVER", JLabel.CENTER); // 기본값 수정
        tierLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        tierLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(tierLabel, BorderLayout.NORTH);
        String[] columns = {"혜택명", "설명", "만료일"};
        JTable benefitTable = new JTable(new DefaultTableModel(columns, 0));
        panel.add(new JScrollPane(benefitTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        // 대기자 명단 (WaitingListDialog가 있다면 주석 해제)
        JButton waitingListButton = new JButton("대기자 명단");
        // waitingListButton.addActionListener(e -> new WaitingListDialog(this, "대기자 명단 관리", true).setVisible(true));
        panel.add(waitingListButton); 

        JButton registerButton = new JButton("고객 등록");
        registerButton.addActionListener(e -> {
            CustomerFormDialog dialog = new CustomerFormDialog(this, "고객 신규 등록", true);
            dialog.setVisible(true);
            loadCustomerList(); // 다이얼로그 닫히면 목록 새로고침
        });
        panel.add(registerButton);

        JButton modifyButton = new JButton("고객 수정");
        modifyButton.addActionListener(e -> handleModify());
        panel.add(modifyButton);

        JButton deleteButton = new JButton("고객 삭제");
        deleteButton.addActionListener(e -> handleDelete());
        panel.add(deleteButton);

        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return panel;
    }
    
    // --- 수정 버튼 핸들러 ---
    private void handleModify() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "수정할 고객을 선택해주세요.");
            return;
        }
        
        // 테이블에서 기본 정보 가져오기
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        String phone = (String) tableModel.getValueAt(selectedRow, 2);
        
        // Customer 객체 임시 생성 (상세 정보는 다시 조회하는게 정석이지만 약식으로 처리)
        Customer selectedCustomer = new Customer(id, name, phone);
        
        CustomerFormDialog dialog = new CustomerFormDialog(this, "고객 정보 수정", true);
        dialog.setCustomerData(selectedCustomer); // 데이터 전달
        dialog.setVisible(true);
        
        loadCustomerList(); // 수정 후 새로고침
    }

    // --- 삭제 버튼 핸들러 ---
    private void handleDelete() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 고객을 선택해주세요.");
            return;
        }

        String customerId = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, 
                "정말 고객 ID [" + customerId + "] 정보를 삭제하시겠습니까?", 
                "삭제 확인", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Request request = new Request("DELETE_CUSTOMER", customerId);
                Response response = (Response) HotelClient.sendRequest(request);

                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, "삭제되었습니다.");
                    loadCustomerList(); // 목록 새로고침
                } else {
                    JOptionPane.showMessageDialog(this, "삭제 실패: " + response.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "통신 오류: " + e.getMessage());
            }
        }
    }
}