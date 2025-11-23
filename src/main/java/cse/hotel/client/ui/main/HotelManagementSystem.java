package cse.hotel.client.ui.main;


import javax.swing.SwingUtilities;
// 이 파일에서 메인 프로그램 실행
import cse.hotel.client.ui.login.LoginUI;

public class HotelManagementSystem {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            LoginUI loginScreen = new LoginUI(); 
            loginScreen.setVisible(true);
        });
    }
}