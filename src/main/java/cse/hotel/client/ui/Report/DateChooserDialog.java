package cse.hotel.client.ui.Report;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 날짜 선택을 위한 단순화된 팝업 다이얼로그 (Swing 표준 컴포넌트 사용)
 */
public class DateChooserDialog extends JDialog {
    private JSpinner yearSpinner, monthSpinner, daySpinner;
    private JTextField targetField;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public DateChooserDialog(JFrame parent, JTextField targetField) {
        super(parent, "날짜 선택", true);
        this.targetField = targetField;
        
        // 현재 날짜로 초기화
        Calendar calendar = Calendar.getInstance();
        
        // --- 스피너 모델 설정 ---
        SpinnerModel yearModel = new SpinnerNumberModel(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) - 10, calendar.get(Calendar.YEAR) + 10, 1);
        SpinnerModel monthModel = new SpinnerNumberModel(calendar.get(Calendar.MONTH) + 1, 1, 12, 1);
        SpinnerModel dayModel = new SpinnerNumberModel(calendar.get(Calendar.DAY_OF_MONTH), 1, 31, 1);

        yearSpinner = new JSpinner(yearModel);
        monthSpinner = new JSpinner(monthModel);
        daySpinner = new JSpinner(dayModel);
        
        // --- UI 구성 ---
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        datePanel.add(new JLabel("년:"));
        datePanel.add(yearSpinner);
        datePanel.add(new JLabel("월:"));
        datePanel.add(monthSpinner);
        datePanel.add(new JLabel("일:"));
        datePanel.add(daySpinner);

        JButton btnSelect = new JButton("선택");
        btnSelect.setBackground(new Color(52, 152, 219));
        btnSelect.setForeground(Color.WHITE);
        btnSelect.addActionListener(e -> selectDate());

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(datePanel, BorderLayout.CENTER);
        getContentPane().add(btnSelect, BorderLayout.SOUTH);
        
        // 다이얼로그 설정
        pack();
        setLocationRelativeTo(parent);
    }
    
    // 사용자가 선택한 날짜를 조합하고 필드에 반영
    private void selectDate() {
        int year = (Integer) yearSpinner.getValue();
        int month = (Integer) monthSpinner.getValue() - 1; // Calendar는 0부터 시작
        int day = (Integer) daySpinner.getValue();

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        targetField.setText(DATE_FORMAT.format(cal.getTime()));
        dispose();
    }
}