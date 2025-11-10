import java.io.*;
import java.net.*;
import java.util.*;

public class Client2 {
    public static void main(String[] args) {
        String serverIP = "localhost"; // 기본 IP
        int port = 9999; // 기본 포트
        String fileName = "server_info.dat"; // 서버 정보 파일 이름

        // server_info.dat 읽기
        try {
            File file = new File(fileName);
            if (file.exists()) {
                Scanner sc = new Scanner(file);
                if (sc.hasNextLine())
                    serverIP = sc.nextLine().trim();
                if (sc.hasNextLine())
                    port = Integer.parseInt(sc.nextLine().trim());
                sc.close();
                System.out.println("server_info.dat 읽음 → " + serverIP + ":" + port);
            } else {
                System.out.println("server_info.dat 없음 → 기본 설정 사용 (" + serverIP + ":" + port + ")");
            }
        } catch (Exception e) {
            System.out.println("server_info.dat 읽기 오류 → 기본 설정 사용 (" + serverIP + ":" + port + ")");
        }

        // 소켓 연결 및 통신
        try (
                Socket socket = new Socket(serverIP, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                Scanner scanner = new Scanner(System.in)) {
            System.out.println("서버에 연결되었습니다 (" + serverIP + ":" + port + ")");

            while (true) {
                System.out.print(">> ");
                String msg = scanner.nextLine();

                if (msg.equalsIgnoreCase("bye")) {
                    out.write("bye\n");
                    out.flush();
                    System.out.println("연결 종료 요청 전송...");
                    break;
                }

                out.write(msg + "\n");
                out.flush();

                String response = in.readLine();
                System.out.println("Server >> " + response);
            }

        } catch (IOException e) {
            System.out.println("서버 연결 실패: " + e.getMessage());
        }

        System.out.println("클라이언트 종료");
    }
}
