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
                // 파일이 존재하면 서버 IP와 포트번호를 읽음
                Scanner sc = new Scanner(file);
                if (sc.hasNextLine())
                    serverIP = sc.nextLine().trim(); // 첫 번째 줄 = IP
                if (sc.hasNextLine())
                    port = Integer.parseInt(sc.nextLine().trim()); // 두 번째 줄 = 포트
                sc.close();
                System.out.println("server_info.dat 읽음 → " + serverIP + ":" + port);
            } else {
                // 파일이 없으면 기본 default 사용
                System.out.println("server_info.dat 없음 → 기본 설정 사용 (" + serverIP + ":" + port + ")");
            }
        } catch (Exception e) {
            // 파일 읽기 중 예외 발생 시 default 사용
            System.out.println("server_info.dat 읽기 오류 → 기본 설정 사용 (" + serverIP + ":" + port + ")");
        }

        // 소켓 연결 및 통신
        try (
                Socket socket = new Socket(serverIP, port); // 서버에 연결
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 서버 입력 스트림
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // 서버 출력 스트림
                Scanner scanner = new Scanner(System.in) // 사용자 입력용 스캐너
        ) {

            System.out.println("서버에 연결되었습니다 (" + serverIP + ":" + port + ")");

            // 사용자 입력을 계속 받아 서버와 통신
            while (true) {
                System.out.print(">> ");
                String msg = scanner.nextLine(); // 사용자 입력

                // bye (대소문자 구분 X) 입력하면 서버에 bye 전송 후 클라이언트 종료
                if (msg.equalsIgnoreCase("bye")) {
                    out.write("bye\n"); // 서버에 종료 요청
                    out.flush();
                    System.out.println("연결 종료 요청 전송...");
                    break; // while문 종료
                }

                // 서버로 메시지 전송
                out.write(msg + "\n"); // 입력된 문자열 전송
                out.flush(); // 즉시 전송

                // 서버로부터 응답 수신
                String response = in.readLine(); // 서버 응답 읽기
                System.out.println("Server >> " + response); // 결과 출력
            }

        } catch (IOException e) {
            // 서버 연결 실패 또는 통신 오류 시
            System.out.println("서버 연결 실패: " + e.getMessage());
        }

        // 프로그램 종료 로그
        System.out.println("클라이언트 종료");
    }
}
