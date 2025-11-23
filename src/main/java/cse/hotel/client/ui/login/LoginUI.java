package cse.hotel.client.ui.login;

import cse.hotel.client.ui.main.HotelMainUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.common.model.User;
import cse.hotel.client.network.HotelClient;

public class LoginUI extends JFrame {

    // 입력 필드
    private JTextField idField;           // ID 입력 칸
    private JPasswordField passwordField;  // PW 입력 칸
    
    // 버튼
    private JButton loginButton;    // 로그인 버튼
    private JButton exitButton;   // 종료 버튼
    
    // 라벨
    private JLabel titleLabel;      // 제목 라벨
    private JLabel idLabel;         // ID 라벨
    private JLabel passwordLabel;   // PW 라벨

    private final Color HEADER_COLOR = new Color(52, 101, 164);
    private final Color BUTTON_BLUE = new Color(52, 101, 164);    
    private final Color BUTTON_GRAY = new Color(120, 130, 140);
    
    // 생성자
    public LoginUI() {
        setTitle("호텔 관리 시스템 - 로그인");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
        
        setVisible(true);
    }
    

    private void initComponents() {
            
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);  
           
        JPanel headerPanel = createHeaderPanel();
        
        JPanel contentPanel = createContentPanel();
        
        JPanel footerPanel = createFooterPanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        setupEventListeners();
        }
      
    // 헤더 패널 생성
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(550, 60));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
        
        JLabel titleLabel = new JLabel("호텔 관리 시스템");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel);
        
        return headerPanel;
    }
    
    // 컨텐츠 패널 생성
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(null);
        
        // 사용자 ID 라벨
        idLabel = new JLabel("사용자 ID:");
        idLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        idLabel.setBounds(50, 35, 120, 30);
        // (순ㅅ허대로 X좌표 Y좌표 너비 높이)
        
        // ID 입력 필드
        idField = new JTextField();
        idField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        idField.setBounds(50, 70, 450, 40);
        idField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        setPlaceholder(idField, "사용자 ID를 입력하세요");
        
        // 비밀번호 라벨
        passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        passwordLabel.setBounds(50, 135, 120, 30);
        
        // 비밀번호 입력 필드
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        passwordField.setBounds(50, 170, 450, 40);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        setPlaceholder(passwordField, "비밀번호를 입력하세요");
        
        // 로그인 버튼
        loginButton = new JButton("→  로그인");
        loginButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        loginButton.setBounds(50, 250, 220, 50);
        loginButton.setBackground(BUTTON_BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 종료 버튼
        exitButton = new JButton("X  종료");
        exitButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        exitButton.setBounds(280, 250, 220, 50);
        exitButton.setBackground(BUTTON_GRAY);
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 컴포넌트 추가
        contentPanel.add(idLabel);
        contentPanel.add(idField);
        contentPanel.add(passwordLabel);
        contentPanel.add(passwordField);
        contentPanel.add(loginButton);
        contentPanel.add(exitButton);
        
        return contentPanel;
    }
    
    // 버전 정보 패널 생성
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(220, 220, 220));
        footerPanel.setPreferredSize(new Dimension(550, 50));
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));
        
        JLabel versionLabel = new JLabel("호텔 관리 시스템 v1.0 | © 2025");
        versionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(100, 100, 100));
        
        footerPanel.add(versionLabel);
        
        return footerPanel;
    }
    
    // PlaceHolder 설정
    private void setPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }
    
    private void setPlaceholder(JPasswordField field, String placeholder) {
        field.setEchoChar((char) 0);  // 일반 텍스트로 표시
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('●');  // 비밀번호 가리기
                    field.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0);
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }
    
    // 버튼 클릭 이벤트 처리
    private void setupEventListeners() {
        // 로그인 버튼 클릭
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        // 종료 버튼 클릭
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleExit();
            }
        });
        
        // 비밀번호 필드에서 Enter 키 입력
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }
    
    // 로그인 처리
    private void handleLogin() {
        String id = idField.getText().trim();
        String password = new String(passwordField.getPassword());

        // 1. 유효성 검사
        if (id.isEmpty() || password.isEmpty() || id.equals("사용자 ID를 입력하세요")) {
            JOptionPane.showMessageDialog(this, "ID와 비밀번호를 모두 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 2. 요청 객체 생성 (명령어: "LOGIN", 데이터: User 객체)
            User loginAttemptUser = new User(id, password, false); 
            Request request = new Request("LOGIN", loginAttemptUser);

            // 3. 공통 통신 모듈(HotelClient)을 이용해 전송
            // (FoodUI와 똑같은 방식!)
            Response response = HotelClient.sendRequest(request);

            // 4. 응답 처리
            if (response.isSuccess()) {
                // 로그인 성공!
                String message = response.getMessage();

                // 서버가 보내준 "진짜 유저 정보(관리자 여부 포함)"를 꺼냄
                User loggedInUser = (User) response.getResultData(); 

                JOptionPane.showMessageDialog(this, message, "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // 로그인 창 닫기
                
                // 로그인한 유저 정보 메인 화면 생성자에 넣어서 전달
                new HotelMainUI(loggedInUser);
                
            } else {
                // 로그인 실패 (비밀번호 틀림, 아이디 없음 등)
                JOptionPane.showMessageDialog(this, response.getMessage(), "로그인 실패", JOptionPane.ERROR_MESSAGE);

                // 비밀번호 필드 비우기
                passwordField.setText("");
                passwordField.requestFocus();
            }

        } catch (Exception e) {
            // 서버가 꺼져있거나 통신 에러 발생 시
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "서버와 통신할 수 없습니다: " + e.getMessage(), "통신 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 종료 처리 
    private void handleExit() {
        int choice = JOptionPane.showConfirmDialog(this,
            "프로그램을 종료하시겠습니까?",
            "종료 확인",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}