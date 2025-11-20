package cse.hotel.client.network; // Request/Response와 같은 패키지 사용 권장

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.InputStream;
import java.util.Properties;
import cse.hotel.common.packet.*;

/**
 * 서버와의 통신을 전담하는 클라이언트 유틸리티 클래스.
 * 모든 UI 요청은 이 클래스를 통해 서버로 전달됩니다.
 */
public class HotelClient {
    
    // 서버 연결 정보
    private static String SERVER_IP; // 로컬 테스트용 IP
    private static int SERVER_PORT;        // HotelServer와 동일한 포트
    
    
    static {
        // 클래스가 로드될 때 config.properties 파일의 값을 읽어옵니다.
        try (InputStream input = HotelClient.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.err.println("❌ config.properties 파일을 찾을 수 없습니다. 기본값으로 설정합니다.");
                SERVER_IP = "127.0.0.1";
                SERVER_PORT = 9999;
            } else {
                prop.load(input);
                SERVER_IP = prop.getProperty("server.ip");
                SERVER_PORT = Integer.parseInt(prop.getProperty("server.port"));
                System.out.println("✅ 서버 IP 자동 로드 완료: " + SERVER_IP);
            }
        } catch (Exception e) {
            System.err.println("❌ config.properties 로드 중 오류 발생. 기본값 사용.");
            SERVER_IP = "127.0.0.1";
            SERVER_PORT = 9999;
        }
    }
    /**
     * 서버로 Request 객체를 전송하고 Response 객체를 받아 반환합니다.
     * @param request 서버로 보낼 요청 객체 (명령어와 데이터 포함)
     * @return 서버로부터 받은 응답 객체
     * @throws Exception 통신 중 발생하는 모든 I/O 및 객체 관련 예외
     */
    public static Response sendRequest(Request request) throws Exception {
        
        // try-with-resources 구문: Socket, InputStream, OutputStream이 자동으로 닫힙니다.
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             // OutputStream을 먼저 열어야 서버에서 ObjectInputStream을 열 때 Deadlock이 발생하지 않습니다.
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) 
        {
            // 1. 서버로 Request 객체 전송
            oos.writeObject(request);
            oos.flush();
            
            // 2. 서버로부터 Response 객체 수신
            Response response = (Response) ois.readObject();
            
            return response;
            
        } catch (Exception e) {
            // 통신 또는 객체 직렬화/역직렬화 오류 발생 시
            System.err.println("❌ 클라이언트 통신 오류: 서버 연결 실패 또는 객체 수신 실패.");
            throw new Exception("서버 연결에 실패했거나 통신 중 오류가 발생했습니다.", e);
        }
    }
}