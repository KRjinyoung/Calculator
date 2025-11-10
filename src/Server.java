import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    // 계산 기능 (산술 연산 + 예외처리)
    public static String calc(String exp) {
        try {
            // 입력 문자열을 공백 기준으로 분리
            StringTokenizer st = new StringTokenizer(exp, " ");

            // 인자 개수 체크
            if (st.countTokens() < 3) {
                return "ERROR BAD_ARG_COUNT too_few_arguments";
            } else if (st.countTokens() > 3) {
                return "ERROR BAD_ARG_COUNT too_many_arguments";
            }

            // 연산자(opcode) 및 피연산자(op1, op2) 추출
            String opcode = st.nextToken().toUpperCase(); // 연산자, 대문자로 변환
            int op1 = Integer.parseInt(st.nextToken()); // 첫 번째 숫자
            int op2 = Integer.parseInt(st.nextToken()); // 두 번째 숫자

            int result; // 결과 저장 변수

            // 연산자에 따라 계산 수행
            switch (opcode) {
                case "ADD": // 덧셈
                    result = op1 + op2;
                    break;
                case "SUB": // 뺄셈
                    result = op1 - op2;
                    break;
                case "MUL": // 곱셈
                    result = op1 * op2;
                    break;
                case "DIV": // 나눗셈
                    if (op2 == 0) // 0으로 나누는 경우
                        return "ERROR DIV_BY_ZERO cannot_divide_by_zero";
                    result = op1 / op2;
                    break;
                default: // 지원하지 않는 연산자
                    return "ERROR BAD_OPCODE unknown_operation";
            }

            // 정상 계산 결과 반환
            return "RESULT " + result;

        } catch (NumberFormatException e) { // 숫자 형식 오류
            return "ERROR BAD_OPERAND not_a_number";
        } catch (Exception e) { // 그 외 모든 예외
            return "ERROR UNKNOWN internal_server_error";
        }
    }

    public static void main(String[] args) {
        String serverIP = "localhost"; // 기본 IP
        int port = 9999; // 기본 포트
        String fileName = "server_info.dat"; // 설정 파일 이름

        // server_info.dat 읽기 (서버 정보 설정)
        try {
            File file = new File(fileName);
            if (file.exists()) {
                // 파일이 존재하면 IP와 포트번호를 읽음
                Scanner sc = new Scanner(file);
                if (sc.hasNextLine())
                    serverIP = sc.nextLine().trim();
                if (sc.hasNextLine())
                    port = Integer.parseInt(sc.nextLine().trim());
                sc.close();
                System.out.println("server_info.dat 읽음 → " + serverIP + ":" + port);
            } else {
                // 파일이 없으면 기본값 사용
                System.out.println("server_info.dat 없음 → 기본 설정 사용 (" + serverIP + ":" + port + ")");
            }
        } catch (Exception e) {
            // 파일 읽는 중 오류가 나면 기본값 사용
            System.out.println("server_info.dat 읽기 오류 → 기본 설정 사용 (" + serverIP + ":" + port + ")");
        }

        // 서버 실행
        try (ServerSocket listener = new ServerSocket(port)) {
            System.out.println("서버가 포트 " + port + "에서 실행 중입니다...");

            // 최대 20명 클라이언트를 동시에 처리하는 스레드풀 생성
            ExecutorService pool = Executors.newFixedThreadPool(20);

            // 클라이언트 접속을 계속 기다림
            while (true) {
                Socket socket = listener.accept(); // 클라이언트 접속 수락
                System.out.println("클라이언트 연결됨: " + socket.getInetAddress());
                // 새로운 클라이언트 연결을 스레드풀에 맡김
                pool.execute(new ClientHandler(socket));
            }

        } catch (IOException e) {
            System.out.println("서버 오류: " + e.getMessage());
        }
    }

    // Runnable 구현 클래스 — 클라이언트 1명 담당
    private static class ClientHandler implements Runnable {
        private final Socket socket; // 클라이언트와 연결된 소켓

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("새 스레드 시작: " + socket);
            try (
                    // 클라이언트 입력 스트림
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    // 클라이언트 출력 스트림
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                // 클라이언트와 통신 루프
                while (true) {
                    String input = in.readLine(); // 클라이언트로부터 메시지 수신

                    // 클라이언트가 연결 종료 요청
                    if (input == null || input.equalsIgnoreCase("bye")) {
                        break;
                    }

                    // 받은 메시지 로그 출력
                    System.out.println(socket.getInetAddress() + " >> " + input);

                    // 계산 수행 및 결과 생성
                    String result = calc(input);

                    // 계산 결과를 클라이언트로 전송
                    out.write(result + "\n");
                    out.flush();
                }

            } catch (IOException e) {
                // 통신 중 오류 발생
                System.out.println("클라이언트 통신 오류: " + e.getMessage());
            } finally {
                try {
                    // 소켓 닫기
                    socket.close();

                    // 닫힌 시간 표시 (yyyy-MM-dd HH:mm:ss 형식)
                    java.time.LocalDateTime time = java.time.LocalDateTime.now();
                    java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter
                            .ofPattern("yyyy-MM-dd HH:mm:ss");
                    System.out.println("소켓 닫힘: " + socket + " at " + time.format(fmt));
                } catch (IOException e) {
                    System.out.println("소켓 종료 오류");
                }
            }
        }
    }
}
