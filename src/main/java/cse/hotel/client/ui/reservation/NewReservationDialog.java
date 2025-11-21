package cse.hotel.client.ui.reservation;

import javax.swing.table.DefaultTableModel;
import java.awt.Color; // 플레이스홀더 색상
import java.awt.Window; // 창 닫기 기능
import java.awt.event.FocusAdapter; // 플레이스홀더
import java.awt.event.FocusEvent; // 플레이스홀더
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane; // 확인 팝업
import javax.swing.SwingUtilities; // 창 닫기 기능
import cse.hotel.common.packet.*;
import cse.hotel.client.network.HotelClient;
import cse.hotel.common.model.Reservation;



public class NewReservationDialog extends javax.swing.JPanel {

    // ReservationUI로부터 테이블 모델을 전달받아 저장할 멤버 변수
    private DefaultTableModel mainTableModel;

    public NewReservationDialog() {
        initComponents();        
    }
    
    public NewReservationDialog(DefaultTableModel tableModel) {
        initComponents(); // NetBeans GUI 디자이너가 생성한 컴포넌트 초기화

        jButton2.addActionListener(evt -> handleCancel());
        jButton3.addActionListener(evt -> handleRegister());
        
        // 전달받은 테이블 모델을 이 클래스의 멤버 변수에 저장
        this.mainTableModel = tableModel;

        // ⬇⬇⬇ [추가] 1. 플레이스홀더 설정 ⬇⬇⬇
        addPlaceholder(jTextField1, "예약 번호 (자동 생성 가능)");
        addPlaceholder(jTextField2, "고객명");
        addPlaceholder(jTextField4, "YYYY-MM-DD");
        addPlaceholder(jTextField5, "YYYY-MM-DD");
        addPlaceholder(jTextField6, "객실 번호 (예: 201)");
        addPlaceholder(jTextField3, "XXX-XXXX-XXXX");
        
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("예약 번호");

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setText("상태");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "예약됨", "점유중", "청소중" }));

        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setText("고객명");

        jLabel18.setForeground(new java.awt.Color(0, 0, 0));
        jLabel18.setText("전화번호");

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setText("체크인 날짜");

        jLabel20.setForeground(new java.awt.Color(0, 0, 0));
        jLabel20.setText("체크아웃 날짜");

        jLabel21.setForeground(new java.awt.Color(0, 0, 0));
        jLabel21.setText("객실 타입");

        jLabel22.setForeground(new java.awt.Color(0, 0, 0));
        jLabel22.setText("객실 번호");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "스탠다드", "디럭스", "스위트", "패밀리" }));

        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    try {
        // 1) 입력 값 수집
        String reservationId = jTextField1.getText().trim();
        String status = (String) jComboBox1.getSelectedItem();
        String customerName = jTextField2.getText().trim();
        String phone = (String) jTextField3.getText().trim();
        String checkIn = jTextField4.getText().trim();
        String checkOut = jTextField5.getText().trim();
        String roomType = (String) jComboBox2.getSelectedItem();
        String roomNumberStr = jTextField6.getText().trim();

        // 2) 필수값 검사
        if (customerName.isEmpty() || roomNumberStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "필수 입력값(고객명, 객실 번호)을 모두 입력해주세요.");
            return;
        }

        int roomNumber;
        try {
            roomNumber = Integer.parseInt(roomNumberStr);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "객실 번호는 숫자로 입력하세요.");
            return;
        }

        // 3) Reservation 객체 생성 및 채우기
        Reservation reservation = new Reservation();

        // 필드 네이밍은 서버 Reservation 클래스에 따라 변경 필요
        reservation.setReservationId(reservationId);    // 또는 setReservationNo(...) 등
        reservation.setStatus(status);                 // 서버 모델에 필드가 있다면
        reservation.setCustomerId(customerName);     // 서버 모델과 이름 일치시
        reservation.setPhone(phone);                   // 서버 모델에 phone이 있으면
        reservation.setCheckInDate(checkIn);
        reservation.setCheckOutDate(checkOut);
        reservation.setRoomType(roomType);
        reservation.setRoomNumber(roomNumber);         // 또는 setRoomNo(...)

        // 4) Request 생성 — 서버에서 기대하는 command 문자열을 정확히 적기
        Request req = new Request("RESERVATION_CREATE", reservation);

        // 5) 전송
        Response resp = HotelClient.sendRequest(req);

        // 6) 응답 처리
        if (resp.isSuccess()) {
            JOptionPane.showMessageDialog(this, "예약 등록이 완료되었습니다!");
            closeDialog();
        } else {
            JOptionPane.showMessageDialog(this, "예약 등록 실패: " + resp.getMessage());
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "오류 발생: " + e.getMessage());
    }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    // End of variables declaration//GEN-END:variables

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
    
    
    
    
    // 현재 JPanel이 속한 부모 JDialog 창을 닫는 공통 헬퍼 메서드 
    private void closeDialog() {
        // 'this'(JPanel)가 속한 최상위 창(JDialog)을 찾습니다.
        Window hostWindow = SwingUtilities.getWindowAncestor(this);
        // 찾은 창(JDialog)을 닫습니다.
        hostWindow.dispose(); 
    }
    
    
    /**  
    * [Feature 4]
    * '취소' 버튼(jButton2) 클릭 시 실행됩니다.
    */
   private void handleCancel() {
       // 1. 사용자에게 종료 여부를 묻는 팝업을 띄웁니다.
       int result = JOptionPane.showConfirmDialog(
               this, // 현재 컴포넌트(JPanel)를 기준으로
               "등록을 취소하고 창을 닫으시겠습니까?", // 메시지
               "취소 확인", // 타이틀
               JOptionPane.YES_NO_OPTION);

       // 2. '예(YES)'를 선택했을 때만 창을 닫습니다.
       if (result == JOptionPane.YES_OPTION) {
           closeDialog(); // 공통 헬퍼 메서드를 호출하여 창 닫기
       }
   }
   
   // 등록 버튼
   private void handleRegister() {
        // --- (1) UI 컴포넌트에서 입력값 가져오기 ---
        String resId = jTextField1.getText().trim();
        String status = jComboBox1.getSelectedItem().toString(); // 콤보박스 값
        String customerName = jTextField2.getText().trim();
        String phone = jTextField3.getText().trim(); // 전화번호 (테이블엔 안 들어감)
        String checkIn = jTextField4.getText().trim();
        String checkOut = jTextField5.getText().trim();
        String roomType = jComboBox2.getSelectedItem().toString(); // 콤보박스 값
        String roomNumStr = jTextField6.getText().trim();

        // --- (2) 비어있는 항목(필수 입력) 검사 ---
        // 플레이스홀더 기능이 있으므로, 실제 값이 플레이스홀더 문자열과 같은지도 확인합니다.
        // (필수 항목을 '고객명', '전화번호', '체크인', '체크아웃', '객실 번호'로 가정)
        if (customerName.isEmpty() || customerName.equals("고객명") ||
            phone.isEmpty() || phone.equals("XXX-XXXX-XXXX") ||
            checkIn.isEmpty() || checkIn.equals("YYYY-MM-DD") ||
            checkOut.isEmpty() || checkOut.equals("YYYY-MM-DD") ||
            roomNumStr.isEmpty() || roomNumStr.equals("객실 번호 (예: 201)")) 
        {
            // --- (3) 경고문 표시 ---
            JOptionPane.showMessageDialog(this,
                    "필수 항목을 모두 올바르게 입력해주세요.\n(고객명, 전화번호, 날짜, 객실 번호)",
                    "입력 오류",
                    JOptionPane.WARNING_MESSAGE);
            return; // 등록 처리를 중단합니다.
        }
        
        // (부가 기능) 예약 번호가 비어있으면 자동 생성 (예시)
        if (resId.isEmpty() || resId.equals("예약 번호 (자동 생성 가능)")) {
            // 간단하게 현재 시간을 기반으로 ID 생성
            resId = "R" + (System.currentTimeMillis() % 100000); 
        }

        // (부가 기능) 객실 번호(String)를 Integer로 변환 (테이블 정렬을 위해)
        Integer roomNum;
        try {
            roomNum = Integer.valueOf(roomNumStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "객실 번호는 숫자로만 입력해야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- (4) (검증 통과) 테이블에 추가할 데이터 배열 생성 ---
        // ⚠️ 중요: ReservationUI의 JTable 컬럼 순서와 정확히 일치해야 합니다!
        // (전화번호(phone)는 수집하지만, 테이블 모델에는 추가하지 않습니다.)
        Object[] newRowData = {
            resId,          // [0] 예약번호
            customerName,   // [1] 고객명
            checkIn,        // [2] 체크인 날짜
            checkOut,       // [3] 체크아웃 날짜
            roomNum,        // [4] 객실번호 (Integer 타입)
            status,         // [5] 상태
            roomType        // [6] 타입
        };

        // --- (5) ReservationUI의 테이블 모델(mainTableModel)에 새 행(row) 추가 ---
        if (this.mainTableModel != null) {
            this.mainTableModel.addRow(newRowData);
            
            // --- (6) (주석) 추후 파일 처리를 위한 위치 ---
            // TODO: (파일 처리) 여기에 새 예약 정보(newRowData)와 'phone' 변수를
            //       파일(e.g., CSV, JSON, 객체 직렬화)에 저장하는 코드를 연결해야 합니다.
            //       (예: reservationFileManager.saveNewReservation(newRowData, phone);)
            
            // --- (7) 등록 완료 후 다이얼로그 닫기 ---
            closeDialog(); // 공통 헬퍼 메서드 호출
            
        } else {
            // (방어 코드) 모델이 알 수 없는 이유로 null일 경우
            JOptionPane.showMessageDialog(this, 
                                        "테이블 모델이 연결되지 않아 등록할 수 없습니다.", 
                                        "시스템 오류", 
                                        JOptionPane.ERROR_MESSAGE);
        }
    }

}
