import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    // 계산 기능 (산술 연산 + 예외처리)
    public static String calc(String exp) {
        try {
            StringTokenizer st = new StringTokenizer(exp, " ");

            // 인자 개수 확인
            if (st.countTokens() < 3) {
                return "ERROR BAD_ARG_COUNT too_few_arguments";
            } else if (st.countTokens() > 3) {
                return "ERROR BAD_ARG_COUNT too_many_arguments";
            }

            // 연산자, 피연산자 추출
            String opcode = st.nextToken().toUpperCase(); // 연산자
            int op1 = Integer.parseInt(st.nextToken()); // 첫 번째 피연산자
            int op2 = Integer.parseInt(st.nextToken()); // 두 번째 피연산자

            int result;
            switch (opcode) {
                case "ADD":
                    result = op1 + op2;
                    break;
                case "SUB":
                    result = op1 - op2;
                    break;
                case "MUL":
                    result = op1 * op2;
                    break;
                case "DIV":
                    if (op2 == 0)
                        return "ERROR DIV_BY_ZERO cannot_divide_by_zero";
                    result = op1 / op2;
                    break;
                default:
                    return "ERROR BAD_OPCODE unknown_operation";
            }

            return "RESULT " + result;

        } catch (NumberFormatException e) {
            return "ERROR BAD_OPERAND not_a_number";
        } catch (Exception e) {
            return "ERROR UNKNOWN internal_server_error";
        }
    }

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

        // 서버 실행 (단일 클라이언트)
        try (ServerSocket listener = new ServerSocket(port)) {
            System.out.println("서버가 포트 " + port + "에서 실행 중입니다...");

            Socket socket = listener.accept(); // 클라이언트 연결 대기
            System.out.println("클라이언트 연결됨: " + socket.getInetAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            while (true) {
                String input = in.readLine(); // 클라이언트 메시지 수신
                if (input == null || input.equalsIgnoreCase("bye")) {
                    System.out.println("클라이언트 종료 요청 수신");
                    break;
                }

                System.out.println("[RECV] " + input);
                String result = calc(input);
                out.write(result + "\n");
                out.flush();
                System.out.println("[SEND] " + result);
            }

            // 종료 처리
            socket.close();
            java.time.LocalDateTime time = java.time.LocalDateTime.now();
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter
                    .ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println("[CLOSE] 소켓 종료: " + time.format(fmt));

        } catch (IOException e) {
            System.out.println("서버 오류: " + e.getMessage());
        }
    }
}
