package cse.hotel.client.ui.clientReservation;

import cse.hotel.client.network.HotelClient;
import cse.hotel.common.packet.*;
import cse.hotel.common.model.ClientReservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyReservationUI extends JFrame {

    private String customerId;
    private JTable table;
    private DefaultTableModel tableModel;

    public MyReservationUI(String customerId) {
        this.customerId = customerId;
        
        setTitle("내 예약 관리");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. 상단 제목
        JLabel lblTitle = new JLabel(customerId + "님의 예약 내역", SwingConstants.CENTER);
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // 2. 중앙 테이블
        String[] cols = {"예약번호", "객실", "체크인", "체크아웃", "상태", "금액"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. 하단 버튼 (새로고침, 예약 취소, 닫기)
        JPanel btnPanel = new JPanel();
        JButton btnRefresh = new JButton("새로고침");
        JButton btnCancel = new JButton("선택한 예약 취소");
        JButton btnClose = new JButton("닫기");

        btnCancel.setBackground(Color.RED);
        btnCancel.setForeground(Color.WHITE);

        btnPanel.add(btnRefresh);
        btnPanel.add(btnCancel);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);

        // --- 이벤트 연결 ---
        btnRefresh.addActionListener(e -> loadReservations());
        btnClose.addActionListener(e -> dispose());
        
        // 예약 취소 버튼
        btnCancel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "취소할 예약을 선택해주세요.");
                return;
            }
            
            String status = (String) tableModel.getValueAt(row, 4);
            if ("CANCELLED".equals(status)) {
                JOptionPane.showMessageDialog(this, "이미 취소된 예약입니다.");
                return;
            }

            String resId = (String) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "정말 예약을 취소하시겠습니까?\n(예약번호: " + resId + ")", 
                    "취소 확인", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                cancelReservation(resId);
            }
        });

        // 시작 시 데이터 로드
        loadReservations();
        setVisible(true);
    }

    private void loadReservations() {
        tableModel.setRowCount(0);
        try {
            Request req = new Request("GET_MY_RESERVATIONS", customerId);
            Response res = HotelClient.sendRequest(req);

            if (res.isSuccess()) {
                List<ClientReservation> list = (List<ClientReservation>) res.getResultData();
                for (ClientReservation r : list) {
                    tableModel.addRow(new Object[]{
                        r.getReservationId(),
                        r.getRoomNumber(),
                        r.getCheckInDate(),
                        r.getCheckOutDate(),
                        r.getStatus(), // "CONFIRMED" or "CANCELLED"
                        (int)r.getTotalPrice()
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelReservation(String resId) {
        try {
            Request req = new Request("CANCEL_RESERVATION", resId);
            Response res = HotelClient.sendRequest(req);
            
            if (res.isSuccess()) {
                JOptionPane.showMessageDialog(this, "예약이 취소되었습니다.");
                loadReservations(); // 목록 갱신
            } else {
                JOptionPane.showMessageDialog(this, "취소 실패: " + res.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}