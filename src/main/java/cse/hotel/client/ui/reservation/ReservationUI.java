package cse.hotel.client.ui.reservation;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.common.model.ClientReservation;
import cse.hotel.common.model.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    public ReservationUI() {
        // 1. 팀원분의 UI 디자인 초기화 (가장 중요)
        initComponents();

        setTitle("[관리자] 전체 예약 통합 관리");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 2. 테이블 모델 설정 (팀원 JTable에 연결)
        setupTableModel();

        // 3. 기능 연결 (서버 데이터 로드)
        loadRoomButtonsFromServer(); // 방 목록 가져와서 오른쪽 패널에 채우기
        loadData();                  // 예약 내역 가져와서 테이블에 채우기

        // 4. 이벤트 리스너 연결 (팀원 버튼에 우리 기능 연결)
        initListeners();

        setVisible(true);
    }

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
                    btn.setBackground(new Color(144, 238, 144)); // 기본 녹색
                    btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
            btn.setBackground(new Color(144, 238, 144));
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            String roomNo = String.valueOf(model.getValueAt(i, 4));
            String status = (String) model.getValueAt(i, 5);
            if (roomButtonMap.containsKey(roomNo) && !"CANCELLED".equals(status)) {
                roomButtonMap.get(roomNo).setBackground(new Color(255, 102, 102)); // 빨강
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
        if (JOptionPane.showConfirmDialog(this, "정말 취소하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                if (HotelClient.sendRequest(new Request("CANCEL_RESERVATION", id)).isSuccess()) {
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

    // ================================================================================
    // ▼▼▼ [여기서부터 팀원분의 NetBeans 생성 코드입니다] ▼▼▼
    // (디자인을 그대로 유지하기 위해 이 부분을 통째로 복사해서 넣었습니다.)
    // ================================================================================
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setRollover(true);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setFont(new java.awt.Font("맑은 고딕", 1, 18)); // NOI18N
        jLabel1.setText("전체 예약 통합 관리");

        jButton1.setText("닫기");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1)
                                .addGap(19, 19, 19))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(jButton1))
                                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(204, 204, 204));

        jLabel2.setFont(new java.awt.Font("맑은 고딕", 1, 14)); // NOI18N
        jLabel2.setText("검색 필터");

        jLabel6.setText("고객 ID");

        jLabel7.setText("예약 번호");

        jLabel8.setText("날짜");

        jButton2.setText("조회");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addGap(15, 15, 15)
                                                .addComponent(jLabel2))
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jLabel7))
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jLabel8))
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(24, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jButton2)
                                .addGap(16, 16, 16))
        );
        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jLabel2)
                                .addGap(36, 36, 36)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton2)
                                .addGap(18, 18, 18))
        );

        jPanel7.setBackground(new java.awt.Color(204, 204, 204));

        jLabel3.setFont(new java.awt.Font("맑은 고딕", 1, 14)); // NOI18N
        jLabel3.setText("예약 목록");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "예약번호", "고객ID", "체크인", "체크아웃", "객실번호", "상태", "타입"
                }
        ));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel3)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel7Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel9.setBackground(new java.awt.Color(204, 204, 204));

        jLabel4.setFont(new java.awt.Font("맑은 고딕", 1, 14)); // NOI18N
        jLabel4.setText("빈 객실");

        jButton3.setText("새로고침");

        jButton4.setText("전체보기");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
                jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4)
                                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
                jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(jButton3)
                                        .addComponent(jButton4))
                                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));

        jLabel5.setText("관리자 모드");

        jButton5.setText("신규 예약");

        jButton6.setBackground(new java.awt.Color(255, 102, 102));
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("선택 예약 강제 취소");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton5)
                                .addGap(18, 18, 18)
                                .addComponent(jButton6)
                                .addGap(22, 22, 22))
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(jButton5)
                                        .addComponent(jButton6))
                                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration                   
}
