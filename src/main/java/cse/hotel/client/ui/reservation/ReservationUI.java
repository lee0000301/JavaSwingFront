package cse.hotel.client.ui.reservation;

// 테이블 관련 
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
// 필터(검색) 관련 
import javax.swing.RowFilter;
import java.util.List;
import java.util.ArrayList;
// 이벤트(마우스, 창 종료) 관련 
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
// 플레이스홀더 색상 관련 
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
// 편집 불가 모델링을 위한 Vector 
import java.util.Vector;
// 팝업창 
import javax.swing.JOptionPane;
// 파일 입출력
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cse.hotel.common.model.Reservation;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.client.network.HotelClient;
import javax.swing.JFrame;



public class ReservationUI extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ReservationUI.class.getName());

    // 객실 버튼 관리할 맵
    private java.util.HashMap<String, javax.swing.JButton> roomButtonMap = new java.util.HashMap<>();
    
    // TableRowSorter 멤버 변수 선언
    private TableRowSorter<DefaultTableModel> sorter;
    private DefaultTableModel model;
    
public ReservationUI() {
        // 1. NetBeans가 UI 컴포넌트를 생성 (가장 먼저 실행되어야 함)
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 테이블 모델 초기화 
        DefaultTableModel oldModel = (DefaultTableModel) jTable2.getModel();
        
        // 원본 데이터 구조 가져오기
        Vector data = oldModel.getDataVector();
        Vector<String> columnNames = new Vector<>();
        for (int i = 0; i < oldModel.getColumnCount(); i++) {
            columnNames.add(oldModel.getColumnName(i));
        }

        // '편집 불가능한' 새 모델을 생성하여 멤버 변수 model에 할당
        model = new DefaultTableModel(data, columnNames) {
            // 컬럼별 데이터 타입 정의
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex >= 0 && columnIndex < types.length) {
                    return types [columnIndex];
                }
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
               // 모든 셀 편집 불가
               return false;
            }
        };
        
        // jTable2에 완성된 모델 연결
        jTable2.setModel(model);
        
        // 정렬기(Sorter) 연결
        sorter = new TableRowSorter<>(model);
        jTable2.setRowSorter(sorter);
        
        // 테이블 UI 설정 (단일 선택, 컬럼 이동 금지)
        jTable2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable2.getTableHeader().setReorderingAllowed(false);

        // -----------------------------------------------------------
        // [Step 2] 테이블 이벤트 리스너 등록 (더블 클릭 상세 조회)
        // -----------------------------------------------------------
        jTable2.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                // 더블 클릭 시
                if (e.getClickCount() == 2) {
                    int viewRow = jTable2.rowAtPoint(e.getPoint());
                    if (viewRow >= 0) { 
                        // 정렬된 상태일 수 있으므로 모델 인덱스로 변환
                        int modelRow = jTable2.convertRowIndexToModel(viewRow);

                        // 모델에서 데이터 추출 (순서: 예약번호, 이름, 체크인, 체크아웃, 방번호, 상태, 타입)
                        String reservationNo = (String) model.getValueAt(modelRow, 0);
                        String customerName  = (String) model.getValueAt(modelRow, 1);
                        String checkInDate   = (String) model.getValueAt(modelRow, 2);
                        String checkOutDate  = (String) model.getValueAt(modelRow, 3);
                        String roomNo        = String.valueOf(model.getValueAt(modelRow, 4));
                        String status        = (String) model.getValueAt(modelRow, 5);
                        String roomType      = (String) model.getValueAt(modelRow, 6);

                        // 상세 창 띄우기
                        // [수정] 마지막에 람다식 () -> { ... } 추가!
                        RoomManagement detailWindow = new RoomManagement(
                        reservationNo, customerName, checkInDate, 
                        checkOutDate, roomNo, status, roomType,
                            () -> { 
                                // 창이 닫히거나 저장하면 이 코드가 실행됨
                                loadData();          // 1. 서버에서 데이터 다시 가져오기
                                refreshRoomStatus(); // 2. 방 버튼 색깔 다시 칠하기
                            }
                        );
                        detailWindow.setLocationRelativeTo(ReservationUI.this);
                        detailWindow.setVisible(true);
                    }
                }
            }
        });

        // -----------------------------------------------------------
        // [Step 3] 데이터 로드 및 화면 갱신 (이제 안전함)
        // -----------------------------------------------------------
        
        // 1. 객실 버튼들(201호, 202호...)을 맵에 등록하고 리스너 연결
        initRoomButtons();
        
        // 2. 서버에서 예약 데이터를 가져와 테이블(model)에 채움
        loadData(); 
        
        // 3. 채워진 데이터를 바탕으로 버튼 색상(빈방/예약됨) 변경
        refreshRoomStatus(); 
        
        // -----------------------------------------------------------
        // [Step 4] 창 설정 및 기타 UI 세팅
        // -----------------------------------------------------------
        setSize(800, 600); // 창 크기 설정
        setLocationRelativeTo(null); // 화면 중앙 배치
        
        // 검색 필드 플레이스홀더 적용
        addPlaceholder(jTextField2, "고객 이름");
        addPlaceholder(jTextField3, "예약 번호");
        addPlaceholder(jTextField4, "YYYY-MM-DD");
    }

    /**
     * JTextField에 플레이스홀더(안내 문구) 기능을 추가하는 헬퍼 메소드
     */
    private void addPlaceholder(javax.swing.JTextField textField, String placeholder) {
        final java.awt.Color PLACEHOLDER_COLOR = java.awt.Color.GRAY;
        final java.awt.Color TEXT_COLOR = java.awt.Color.BLACK;

        // 초기 상태 설정: 안내 문구 + 회색 글씨
        textField.setText(placeholder);
        textField.setForeground(PLACEHOLDER_COLOR);

        // 포커스 리스너 추가 (FocusAdapter 사용)
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            
            @Override                   
            public void focusGained(java.awt.event.FocusEvent e) {
                // 포커스를 얻었을 때 (클릭했을 때)
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // 포커스를 잃었을 때 (다른 곳을 클릭했을 때)
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
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
        jButton4 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

        jToolBar1.setRollover(true);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("예약 검색");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(170, 170, 170));

        jLabel1.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("예약 관리");

        jButton1.setBackground(new java.awt.Color(170, 170, 170));
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setText("⇦ 뒤로");
        jButton1.setBorderPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(235, 235, 235));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        jPanel3.setForeground(new java.awt.Color(235, 235, 235));

        jPanel5.setBackground(new java.awt.Color(180, 180, 180));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(235, 235, 235)));

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("예약 검색");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
        );

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("예약 번호");

        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("체크인 날짜");

        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("고객 이름");

        jTextField2.setBackground(new java.awt.Color(255, 255, 255));
        jTextField2.setPreferredSize(new java.awt.Dimension(95, 30));
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jTextField3.setBackground(new java.awt.Color(255, 255, 255));
        jTextField3.setPreferredSize(new java.awt.Dimension(95, 30));
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jTextField4.setBackground(new java.awt.Color(255, 255, 255));
        jTextField4.setPreferredSize(new java.awt.Dimension(95, 30));
        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setText("검색");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                        .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel6.setBackground(new java.awt.Color(235, 235, 235));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        jPanel6.setForeground(new java.awt.Color(235, 235, 235));

        jPanel7.setBackground(new java.awt.Color(180, 180, 180));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(235, 235, 235)));

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("예약 목록");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
        );

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"021713", "이상혁", "2025-10-02", "2025-10-02",  new Integer(201), "예약됨", "Standard"},
                {"182739", "이지은", "2025-10-02", "2025-10-02",  new Integer(201), "점유중", "Deluxe"},
                {"136531", "박서준", "2025-10-02", "2025-10-02",  new Integer(201), "청소중", "Suite"},
                {"131452", "정유진", "2025-10-02", "2025-10-02",  new Integer(201), "점유중", "Family"},
                {"156854", "최동욱", "2025-10-02", "2025-10-02",  new Integer(201), "예약됨", "Deluxe"},
                {"642324", "강하늘", "2025-10-02", "2025-10-02",  new Integer(201), "예약됨", "Deluxe"},
                {"974325", "윤서아", "2025-10-02", "2025-10-02",  new Integer(201), "청소중", "Standard"},
                {"975942", "송지호", "2025-10-03", "2025-10-02",  new Integer(201), "청소중", "Standard"},
                {"378564", "한소희", "2025-10-02", "2025-10-02",  new Integer(201), "점유중", "Standard"},
                {"378231", "김민지", "2025-10-02", "2025-10-02",  new Integer(201), "점유중", "Family"}
            },
            new String [] {
                "예약번호", "고객명", "체크인 날짜", "체크아웃 날짜", "객실번호", "상태", "타입"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel8.setBackground(new java.awt.Color(235, 235, 235));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        jPanel8.setForeground(new java.awt.Color(235, 235, 235));

        jPanel9.setBackground(new java.awt.Color(180, 180, 180));
        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(235, 235, 235)));

        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("빈 객실");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jButton4.setText("201");
        jButton4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton4.setBorderPainted(false);

        jButton13.setText("201");
        jButton13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton13.setBorderPainted(false);

        jButton14.setText("201");
        jButton14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton14.setBorderPainted(false);

        jButton15.setText("201");
        jButton15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton15.setBorderPainted(false);

        jButton16.setText("201");
        jButton16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton16.setBorderPainted(false);

        jButton17.setText("201");
        jButton17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton17.setBorderPainted(false);

        jButton18.setText("201");
        jButton18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton18.setBorderPainted(false);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton3.setText("예약 수정");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton5.setText("신규 예약");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("예약 취소");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                            .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
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
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String customerName = jTextField2.getText().trim();
        String reservationNo = jTextField3.getText().trim();
        String date = jTextField4.getText().trim();
        
        final String NAME_PLACEHOLDER = "고객 이름";
        final String NO_PLACEHOLDER = "예약 번호";
        final String DATE_PLACEHOLDER = "YYYY-MM-DD";

        // 여러 필터를 조합하기 위해 List를 생성
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        // 각 필드에 대한 RowFilter를 생성하여 리스트에 추가
        // 고객명 (jTable2의 1번 인덱스 컬럼)
        if (!customerName.isEmpty()&& !customerName.equals(NAME_PLACEHOLDER)) {
            // "포함" 검색을 위해 regexFilter 사용
            filters.add(RowFilter.regexFilter(customerName, 1)); 
        }
        // 예약번호 (jTable2의 0번 인덱스 컬럼)
        if (!reservationNo.isEmpty() && !reservationNo.equals(NO_PLACEHOLDER)) {
            filters.add(RowFilter.regexFilter(reservationNo, 0));
        }
        // 날짜 (jTable2의 2번 인덱스 컬럼 - '체크인 날짜' 기준)
        if (!date.isEmpty() && !date.equals(DATE_PLACEHOLDER)) {
            filters.add(RowFilter.regexFilter(date, 2));
        }
        // 모든 필터를 AND 조건으로 결합
        // 만약 모든 필드가 비어있다면, filters 리스트가 비게 되고 모든 행이 보이게 됩니다.
        RowFilter<Object, Object> combinedFilter = RowFilter.andFilter(filters);
        // Sorter에 최종 필터를 적용
        sorter.setRowFilter(combinedFilter);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // 1. 현재 테이블에서 선택된 행의 인덱스(view)를 가져옵니다.
        int viewRow = jTable2.getSelectedRow();
         
        // 2. 선택된 행이 있는지 확인합니다.
        if (viewRow < 0) {
            // 선택된 행이 없다면, 사용자에게 알림 팝업을 띄웁니다.
            JOptionPane.showMessageDialog(
                    this, // 현재 창을 기준으로
                    "수정할 예약을 목록에서 선택해주세요.", // 메시지
                    "선택 오류", // 팝업창 제목
                    JOptionPane.WARNING_MESSAGE // 경고 아이콘
            );
            return; // 행이 선택되지 않았으므로, 메서드를 여기서 종료합니다.
        }

        // 3. 선택된 행(view)을 실제 데이터 모델의 인덱스(model)로 변환합니다.
        //    (이유: 정렬이나 필터링이 적용된 상태일 수 있으므로 이 변환이 필수)
        int modelRow = jTable2.convertRowIndexToModel(viewRow);

        // 4. [중요] 7개의 데이터를 '멤버 변수 model'에서 가져옵니다.
        //    (더블 클릭 리스너와 동일한 로직)
        String reservationNo = (String) model.getValueAt(modelRow, 0); // 예약번호
        String customerName  = (String) model.getValueAt(modelRow, 1); // 고객명
        String checkInDate   = (String) model.getValueAt(modelRow, 2); // 체크인
        String checkOutDate  = (String) model.getValueAt(modelRow, 3); // 체크아웃
        String roomNo        = String.valueOf(model.getValueAt(modelRow, 4)); // 객실번호
        String status        = (String) model.getValueAt(modelRow, 5); // 상태
        String roomType      = (String) model.getValueAt(modelRow, 6); // [7번째] 타입

        // 새로고침 로직
        RoomManagement detailWindow = new RoomManagement(
            reservationNo, customerName, checkInDate,  
            checkOutDate, roomNo, status, roomType,
            () -> {
                loadData();
                refreshRoomStatus();
            }
        );
        
        detailWindow.setLocationRelativeTo(this); // 부모 창 기준 정가운데 정렬
        detailWindow.setVisible(true); // 새 창 띄우기
    }//GEN-LAST:event_jButton3ActionPerformed

    // 신규 예약 버튼
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        NewReservationDialog newReservationPanel = new NewReservationDialog(model);        
        javax.swing.JDialog dialog = new javax.swing.JDialog(this, "신규 예약", true);
        dialog.getContentPane().add(newReservationPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setLocationRelativeTo(ReservationUI.this); // 부모 창 정중앙 정렬
        dialog.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
int viewRow = jTable2.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "취소할 예약을 선택해주세요.");
            return; 
        }

        int modelRow = jTable2.convertRowIndexToModel(viewRow);
        String reservationNo = (String) model.getValueAt(modelRow, 0); // 예약번호
        String status = (String) model.getValueAt(modelRow, 5); // 상태

        // 이미 체크인 된 방이나 지난 예약은 취소 불가 처리
        if (status.equals("점유중") || status.equals("체크아웃")) {
             JOptionPane.showMessageDialog(this, "이미 체크인되었거나 종료된 예약은 취소할 수 없습니다.", "취소 불가", JOptionPane.ERROR_MESSAGE);
             return;
        }

        int result = JOptionPane.showConfirmDialog(this, "정말로 예약을 취소하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) { 
            try {
                // 서버에 취소 요청 전송 (ID 기준)
                Request request = new Request("CANCEL_RESERVATION", reservationNo);
                Response response = HotelClient.sendRequest(request);

                if (response.isSuccess()) {
                      model.removeRow(modelRow);
                      refreshRoomStatus(); // 버튼 색상 갱신
                      JOptionPane.showMessageDialog(this, "예약이 정상적으로 취소되었습니다.");
                } else {
                      JOptionPane.showMessageDialog(this, "취소 실패: " + response.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "통신 오류: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_jButton6ActionPerformed
    
// [변경] 서버에서 예약 정보 불러와 테이블에 세팅 (HotelClient 사용)
    private void loadData() {
        try {
            // 1. 서버에 "LOAD_RESERVATIONS" 요청 전송
            Request req = new Request("LOAD_RESERVATIONS", null);
            Response res = HotelClient.sendRequest(req);

            // 2. 응답 처리
            if (res.isSuccess()) {
                // 3. 받은 데이터(ArrayList)로 테이블 갱신
                List<?> list = (List<?>) res.getResultData();
                
                model.setRowCount(0); // 기존 데이터 초기화
                
                for (Object obj : list) {
                    if (obj instanceof Reservation) {
                        Reservation r = (Reservation) obj;
                        // 테이블 컬럼 순서에 맞게 데이터 추가
                        model.addRow(new Object[] {
                            r.getReservationNo(), 
                            r.getCustomerName(),    
                            r.getCheckInDate(),
                            r.getCheckOutDate(),
                            r.getRoomNo(),   
                            "예약됨",             // status 필드가 없다면 임의값 혹은 r.getStatus()
                            r.getRoomType()       // roomType 필드가 있다면 r.getRoomType()
                        });
                    }
                }
                System.out.println("서버로부터 데이터 로드 완료: " + list.size() + "건");
            } else {
                JOptionPane.showMessageDialog(this, "서버 오류: " + res.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "서버 연결 실패: " + e.getMessage());
        }
    }

    private void initRoomButtons() {
        // 1. 버튼 텍스트 설정
        jButton4.setText("201");
        jButton13.setText("202");
        jButton14.setText("203");
        jButton15.setText("204");
        jButton16.setText("205");
        jButton17.setText("206");
        jButton18.setText("207");

        // 2. 맵에 등록
        roomButtonMap.put("201", jButton4);
        roomButtonMap.put("202", jButton13);
        roomButtonMap.put("203", jButton14);
        roomButtonMap.put("204", jButton15);
        roomButtonMap.put("205", jButton16);
        roomButtonMap.put("206", jButton17);
        roomButtonMap.put("207", jButton18);

        // --- [핵심] 공통 클릭 리스너 생성 ---
        java.awt.event.ActionListener roomListener = new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // 클릭된 버튼을 알아내서
                javax.swing.JButton clickedBtn = (javax.swing.JButton) e.getSource();
                // 그 버튼의 텍스트(방 번호)를 가져와 로직을 실행
                handleRoomButtonClick(clickedBtn.getText());
            }
        };

        // 3. 디자인 설정 및 리스너 연결
        for (javax.swing.JButton btn : roomButtonMap.values()) {
            btn.setBackground(new java.awt.Color(144, 238, 144)); // 기본: 빈 방 색
            btn.setForeground(java.awt.Color.BLACK);
            
            // [중요] 여기서 리스너를 달아줘야 클릭이 됩니다!
            btn.addActionListener(roomListener); 
        }
        }
    
    public void refreshRoomStatus() {
        // 1. 먼저 모든 방을 '빈 방(초록색)' 상태로 초기화
        for (javax.swing.JButton btn : roomButtonMap.values()) {
            btn.setBackground(new java.awt.Color(144, 238, 144)); // Light Green
            btn.setToolTipText("빈 객실");
        }

        // 2. 테이블 모델(model)의 전체 데이터를 스캔
        int rowCount = model.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            // 테이블의 4번째 컬럼(인덱스 4)이 '객실번호', 5번째 컬럼(인덱스 5)이 '상태'
            String roomNo = String.valueOf(model.getValueAt(i, 4)); 
            String status = (String) model.getValueAt(i, 5);

            // 3. 해당 방 번호에 매핑된 버튼이 있는지 확인
            if (roomButtonMap.containsKey(roomNo)) {
                javax.swing.JButton targetBtn = roomButtonMap.get(roomNo);
                
                // 4. 상태가 '예약됨', '점유중', '청소중' 등인 경우 색상 변경
                // (빈 객실이 아닌 경우)
                if (!status.equals("빈 객실")) {
                    targetBtn.setBackground(new java.awt.Color(255, 102, 102)); // 연한 빨강 (점유됨)
                    targetBtn.setToolTipText("현재 상태: " + status);
                    // 필요하다면 버튼을 비활성화 할 수도 있음
                    // targetBtn.setEnabled(false); 
                }
            }
        }
    }
    
    private void handleRoomButtonClick(String roomNo) {
        boolean found = false;

        // 테이블 모델 전체를 돌면서 클릭한 '방 번호'와 일치하는 예약이 있는지 찾음
        for (int i = 0; i < model.getRowCount(); i++) {
            // 모델의 4번째 컬럼이 객실번호 (String으로 변환하여 비교)
            String tableRoomNo = String.valueOf(model.getValueAt(i, 4));
            
            if (tableRoomNo.equals(roomNo)) {
                // 예약 내역 발견! -> 상세 정보 가져오기
                String reservationNo = (String) model.getValueAt(i, 0);
                String customerName  = (String) model.getValueAt(i, 1);
                String checkInDate   = (String) model.getValueAt(i, 2);
                String checkOutDate  = (String) model.getValueAt(i, 3);
                String status        = (String) model.getValueAt(i, 5);
                String roomType      = (String) model.getValueAt(i, 6);

                // [수정] 여기도 추가!
                RoomManagement detailWindow = new RoomManagement(
                    reservationNo, customerName, checkInDate, 
                    checkOutDate, tableRoomNo, status, roomType,
                    () -> {
                        loadData();
                        refreshRoomStatus();
                    }
                );
                detailWindow.setLocationRelativeTo(this);
                detailWindow.setVisible(true);
                
                found = true;
                break; // 찾았으니 루프 종료
            }
        }

        // 해당 방 번호로 된 예약이 테이블에 없을 경우 (빈 방)
        if (!found) {
            int result = JOptionPane.showConfirmDialog(
                this, 
                roomNo + "호는 현재 빈 객실입니다.\n신규 예약을 진행하시겠습니까?",
                "빈 객실 선택",
                JOptionPane.YES_NO_OPTION
            );
            
            if (result == JOptionPane.YES_OPTION) {
                // 신규 예약 버튼 눌렀을 때와 동일한 동작
                jButton5.doClick(); 
            }
        }
    }
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

}
