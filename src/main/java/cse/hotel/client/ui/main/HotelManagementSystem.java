package cse.hotel.client.ui.main;

import javax.swing.SwingUtilities;
// 이 파일에서 메인 프로그램 실행

public class HotelManagementSystem {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            HotelMainUI mainMenu = new HotelMainUI();
            mainMenu.setVisible(true);
        });
    }
}