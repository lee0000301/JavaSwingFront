package cse.hotel.client.ui.reservation;

import javax.swing.JOptionPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;

public class RoomManagement extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RoomManagement.class.getName());
    private String currentStatus;
    private String roomNo; // 방 번호 저장용 (서버 전송 시 필요)
    
    private Runnable refreshCallback; // 부모 창 새로고침할 함수 저장 변수
    
    public RoomManagement(String reservationNo, String customerName, 
                          String checkInDate, String checkOutDate, 
                          String roomNo, String status, String roomType,
                          Runnable callback) {
        
        this.refreshCallback = callback;

        initComponents(); // UI 초기화
        
        // 멤버 변수 초기화
        this.roomNo = roomNo;
        this.currentStatus = status;

        // 텍스트 필드에 전달받은 값 세팅
        jTextField7.setText(reservationNo); // 예약 번호
        jTextField1.setText(customerName);  // 고객명
        jTextField3.setText(checkInDate);   // 체크인 날짜
        jTextField5.setText(checkOutDate);  // 체크아웃 날짜
        jTextField4.setText(roomNo);        // 객실 번호
        jTextField6.setText(roomType);      // 객실 타입
        
        // 변경 불가 필드 설정
        jTextField7.setEditable(false);
        jTextField4.setEditable(false);
        jTextField6.setEditable(false);
        jTextField1.setEditable(false);
        jTextField3.setEditable(false);
        jTextField5.setEditable(false);
        
        // [핵심] 상태에 따라 버튼 모양 초기화
        updateStatusButtonState(); 

        // 버튼 리스너 연결
        jButton4.addActionListener(evt -> handleSave());
        
        // [핵심] 상태 변경 버튼 (체크인/체크아웃/청소)
        jButton2.addActionListener(evt -> handleStatusButtonAction());
        
        // 닫기 설정
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow(); 
            }
        });
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("# 예약 번호");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(215, 215, 215));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel2.setBackground(new java.awt.Color(170, 170, 170));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128), 2));

        jButton1.setBackground(new java.awt.Color(170, 170, 170));
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setText("X");
        jButton1.setBorderPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("맑은 고딕", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("객실 정보 관리");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128), 2));

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("# 예약 번호");

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("고객명");

        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("체크아웃 날짜");

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("체크인 날짜");

        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("객실 번호");

        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("객실 타입");

        jTextField1.setText("이상혁");

        jTextField3.setText("2025-10-02");

        jTextField4.setText("201");

        jTextField5.setText("2025-10-03");

        jTextField6.setText("Standard");
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jTextField7.setText("232152");
        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(150, 150, 150));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128), 2));

        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("현재 상태");

        jButton2.setText("체크인");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 202, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 8, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jButton4.setBackground(new java.awt.Color(150, 150, 150));
        jButton4.setForeground(new java.awt.Color(0, 0, 0));
        jButton4.setText("저장");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(150, 150, 150));
        jButton5.setForeground(new java.awt.Color(0, 0, 0));
        jButton5.setText("닫기");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setPreferredSize(new java.awt.Dimension(0, 20));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
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
        closeWindow(); // 함수 호출 (종료하시겠습니까?)
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        closeWindow(); // 함수 호출 (종료하시겠습니까?)
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void updateStatusButtonState() {
        switch (this.currentStatus) {
            case "예약됨":
                jButton2.setText("체크인");
                jButton2.setEnabled(true);
                jButton2.setBackground(new java.awt.Color(204, 255, 204)); 
                break;
            case "점유중":
                jButton2.setText("체크아웃");
                jButton2.setEnabled(true);
                jButton2.setBackground(new java.awt.Color(255, 204, 204)); 
                break;
            case "청소중":
                jButton2.setText("청소완료");
                jButton2.setEnabled(true);
                jButton2.setBackground(new java.awt.Color(204, 229, 255)); 
                break;
            case "빈 객실":
                jButton2.setText("-");
                jButton2.setEnabled(false);
                jButton2.setBackground(java.awt.Color.LIGHT_GRAY);
                break;
            default:
                jButton2.setText("상태 불명");
                jButton2.setEnabled(false);
                break;
        }
    }

    // --- 버튼 클릭 시 상태 변경 (서버 통신) ---
    private void handleStatusButtonAction() {
        String nextCommand = "";
        String confirmMsg = "";
        
        if (this.currentStatus.equals("예약됨")) {
            nextCommand = "CHECK_IN";
            confirmMsg = "체크인을 진행하시겠습니까?";
        } else if (this.currentStatus.equals("점유중")) {
            nextCommand = "CHECK_OUT";
            confirmMsg = "체크아웃을 진행하시겠습니까?";
        } else if (this.currentStatus.equals("청소중")) {
            nextCommand = "FINISH_CLEANING";
            confirmMsg = "청소를 완료하고 '빈 객실'로 변경하시겠습니까?";
        } else {
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, confirmMsg, "상태 변경", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                int roomNumInt = Integer.parseInt(this.roomNo);
                Request request = new Request(nextCommand, roomNumInt);
                Response response = HotelClient.sendRequest(request);

                if (response.isSuccess()) {
                    // 성공 시 내부 상태 변경
                    if (this.currentStatus.equals("예약됨")) {
                        this.currentStatus = "점유중";
                    } else if (this.currentStatus.equals("점유중")) {
                        this.currentStatus = "청소중";
                    } else if (this.currentStatus.equals("청소중")) {
                        this.currentStatus = "빈 객실";
                    }
                    
                    updateStatusButtonState(); // 버튼 UI 갱신
                    JOptionPane.showMessageDialog(this, response.getMessage());
                    
                    // ★★★ [핵심] 부모 창(ReservationUI) 새로고침 실행 ★★★
                    if (this.refreshCallback != null) {
                        this.refreshCallback.run();
                    }
                    
                } else {
                    JOptionPane.showMessageDialog(this, "요청 실패: " + response.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "통신 오류: " + e.getMessage());
            }
        }
    }
    
    // --- 저장 버튼 ---
    private void handleSave() {
        int result = JOptionPane.showConfirmDialog(this, "저장하시겠습니까?", "저장 확인", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            // 저장 로직 수행 후에도 새로고침
            if (this.refreshCallback != null) {
                this.refreshCallback.run();
            }
            this.dispose();
        }
    }
    
    private void updateCheckInButtonState() {
 
        if ("예약됨".equals(this.currentStatus)) {
            jButton2.setText("체크인");
            jButton2.setEnabled(true); // '예약됨' 상태일 때만 버튼 활성화
        } else {
            // "점유중", "청소중" 또는 그 외 모든 상태
            jButton2.setText("체크인");
            jButton2.setEnabled(false); // 그 외 상태일 땐 비활성화
        }
    }
    
    /**
     * 체크인 버튼(jButton2)을 클릭했을 때 호출
     */
    private void handleCheckIn() {
        // 내부 상태를 '점유중'으로 변경
        this.currentStatus = "점유중";
        
        // UI(라벨, 버튼)를 즉시 갱신
        updateCheckInButtonState(); 
        
        // (참고: 이 변경 사항은 '저장' 버튼을 눌러야 최종 반영됩니다.)
    }
    /**
    * [Feature 3]
    * 창을 닫기 전 사용자에게 확인 팝업을 띄웁니다.
    */
   private void closeWindow() {
       int result = JOptionPane.showConfirmDialog(
               this, // 부모 창
               "종료하시겠습니까?", // 메시지
               "종료 확인", // 타이틀
               JOptionPane.YES_NO_OPTION);

       if (result == JOptionPane.YES_OPTION) {
           this.dispose(); // '예'를 누르면 현재 창만 닫기
       }
   }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables
}
