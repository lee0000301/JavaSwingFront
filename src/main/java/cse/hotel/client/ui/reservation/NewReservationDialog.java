package cse.hotel.client.ui.reservation;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.common.model.ClientReservation;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

// JPanel -> JDialog로 변경하여 팝업창으로 동작하게 함
public class NewReservationDialog extends javax.swing.JDialog {

    private ReservationUI parentUI; // 부모창 새로고침용

    // 기본 생성자
    public NewReservationDialog(ReservationUI parent) {
        this(parent, "");
    }
    
    // 방 번호를 받아서 초기화하는 생성자
    public NewReservationDialog(ReservationUI parent, String defaultRoomNo) {
        super(parent, "신규 예약 등록", true); // 모달 다이얼로그 설정
        this.parentUI = parent;
        
        // 1. 팀원 UI 초기화
        initComponents(); 
        
        // 2. 전달받은 방 번호 세팅
        jTextField6.setText(defaultRoomNo); 

        // 3. 플레이스홀더 설정 (팀원 코드 활용)
        setupPlaceholders();
        
        // 4. 버튼 리스너 연결
        jButton3.addActionListener(evt -> handleRegister()); // 등록
        jButton2.addActionListener(evt -> dispose());       // 취소

        pack();
        setLocationRelativeTo(parent); // 부모창 중앙에 띄우기
    }
    
    // --- 서버에 예약 요청 전송 ---
    private void handleRegister() {
        try {
            // 1. 입력값 가져오기 (팀원 UI 컴포넌트에서)
            String customerId = jTextField2.getText().trim(); // 고객명(ID)
            String checkIn = jTextField4.getText().trim();
            String checkOut = jTextField5.getText().trim();
            String roomNumStr = jTextField6.getText().trim();
            // (참고: 전화번호 jTextField3, 예약번호 jTextField1은 현재 서버 모델에 없어서 생략)

            // 2. 필수값 검사 (플레이스홀더 값인지도 확인)
            if (customerId.isEmpty() || customerId.equals("고객명") ||
                roomNumStr.isEmpty() || roomNumStr.equals("객실 번호 (예: 201)") ||
                checkIn.equals("YYYY-MM-DD") || checkOut.equals("YYYY-MM-DD")) {
                JOptionPane.showMessageDialog(this, "필수 정보를 모두 입력해주세요.");
                return;
            }

            // 3. ClientReservation 객체 생성
            ClientReservation res = new ClientReservation(
                null, // ID는 서버에서 생성
                customerId,
                Integer.parseInt(roomNumStr),
                checkIn,
                checkOut,
                150000.0, // 가격 (임시)
                "CONFIRMED"
            );

            // 4. 서버 전송
            Request req = new Request("MAKE_RESERVATION", res);
            Response resp = HotelClient.sendRequest(req);

            if (resp.isSuccess()) {
                JOptionPane.showMessageDialog(this, "관리자 권한으로 예약이 등록되었습니다.");
                if (parentUI != null) parentUI.loadData(); // 부모창 새로고침
                dispose(); // 닫기
            } else {
                JOptionPane.showMessageDialog(this, "등록 실패: " + resp.getMessage());
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "객실 번호는 숫자여야 합니다.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "오류 발생: " + e.getMessage());
        }
    }

    // --- 플레이스홀더 설정 (팀원 코드 활용) ---
    private void setupPlaceholders() {
        addPlaceholder(jTextField1, "예약 번호 (자동 생성 가능)");
        addPlaceholder(jTextField2, "고객명");
        addPlaceholder(jTextField4, "YYYY-MM-DD");
        addPlaceholder(jTextField5, "YYYY-MM-DD");
        if(jTextField6.getText().isEmpty()) addPlaceholder(jTextField6, "객실 번호 (예: 201)");
        addPlaceholder(jTextField3, "XXX-XXXX-XXXX");
    }

    private void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText(""); textField.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder); textField.setForeground(Color.GRAY);
                }
            }
        });
    }

    // ================================================================================
    // ▼▼▼ [팀원분의 NetBeans 생성 코드] ▼▼▼
    // ================================================================================
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel29 = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel32 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jPanel33 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jTextField6 = new javax.swing.JTextField();

        jPanel29.setBackground(new java.awt.Color(215, 215, 215));
        jPanel29.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));

        jPanel30.setBackground(new java.awt.Color(170, 170, 170));
        jPanel30.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));

        jLabel12.setFont(new java.awt.Font("맑은 고딕", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("신규 예약 등록");

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel12)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel31.setBackground(new java.awt.Color(180, 180, 180));
        jPanel31.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128), 2));

        jLabel11.setFont(new java.awt.Font("맑은 고딕", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("예약 정보");

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel32.setBackground(new java.awt.Color(180, 180, 180));
        jPanel32.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128), 2));

        jLabel14.setFont(new java.awt.Font("맑은 고딕", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("객실 정보");

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel33.setBackground(new java.awt.Color(180, 180, 180));
        jPanel33.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128), 2));

        jLabel13.setFont(new java.awt.Font("맑은 고딕", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("고객 정보");

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSeparator2.setPreferredSize(new java.awt.Dimension(50, 20));

        jButton2.setText("취소");

        jButton3.setText("등록");

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("예약 번호");

        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setText("상태");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "예약됨", "점유중", "청소중" }));

        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setText("고객명");

        jLabel18.setForeground(new java.awt.Color(0, 0, 0));
        jLabel18.setText("전화번호");

        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setText("체크인 날짜");

        jLabel20.setForeground(new java.awt.Color(0, 0, 0));
        jLabel20.setText("체크아웃 날짜");

        jLabel21.setForeground(new java.awt.Color(0, 0, 0));
        jLabel21.setText("객실 타입");

        jLabel22.setForeground(new java.awt.Color(0, 0, 0));
        jLabel22.setText("객실 번호");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "스탠다드", "디럭스", "스위트", "패밀리" }));

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel29Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel29Layout.createSequentialGroup()
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(98, 98, 98)
                            .addComponent(jLabel20))
                        .addGroup(jPanel29Layout.createSequentialGroup()
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jTextField3))
                        .addGroup(jPanel29Layout.createSequentialGroup()
                            .addComponent(jLabel17)
                            .addGap(134, 134, 134)
                            .addComponent(jLabel18))
                        .addGroup(jPanel29Layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addGap(116, 116, 116)
                            .addComponent(jLabel16))
                        .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel29Layout.createSequentialGroup()
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel29Layout.createSequentialGroup()
                            .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))))
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(jComboBox1))
                .addGap(27, 27, 27)
                .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addGap(2, 2, 2)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addGap(27, 27, 27)
                .addComponent(jPanel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20))
                .addGap(2, 2, 2)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22))
                .addGap(3, 3, 3)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>                        


    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration                   
}