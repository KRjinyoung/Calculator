## 프로젝트 목적
이 과제의 목적은 **Java Socket 프로그래밍을 이용하여 클라이언트-서버 간 통신이 가능한 네트워크 기반 산술 계산기**를 구현하는 것이다.  
클라이언트는 사칙연산 명령을 서버에 전송하고, 서버는 이를 계산한 결과를 **ASCII 기반 프로토콜 형식**으로 응답한다.

## 프로토콜 명세 (ASCII 텍스트 기반)
모든 통신은 **ASCII 문자열**로 구성되며, 각 메시지는 개행(`\n`)으로 구분된다.  
클라이언트는 명령(command)을 전송하고, 서버는 해당 결과(result) 또는 에러(error)를 반환한다.

| 구분 | 클라이언트 입력 예시 | 서버 응답 예시 | 설명 |
|------|----------------------|----------------|------|
| 덧셈 | `ADD 2 3` | `RESULT 5` | 정상 연산 |
| 뺄셈 | `SUB 5 2` | `RESULT 3` | 정상 연산 |
| 곱셈 | `MUL 4 6` | `RESULT 24` | 정상 연산 |
| 나눗셈 | `DIV 10 2` | `RESULT 5` | 정상 연산 |
| 잘못된 연산자 | `XYZ 2 3` | `ERROR BAD_OPCODE unknown_operation` | 존재하지 않는 연산자 |
| 인자 부족 | `ADD 5` | `ERROR BAD_ARG_COUNT too_few_arguments` | 피연산자 부족 |
| 인자 초과 | `ADD 5 3 1` | `ERROR BAD_ARG_COUNT too_many_arguments` | 피연산자 초과 |
| 0으로 나눔 | `DIV 10 0` | `ERROR DIV_BY_ZERO cannot_divide_by_zero` | 0으로 나누는 오류 |
| 숫자 아님 | `ADD A 3` | `ERROR BAD_OPERAND not_a_number` | 피연산자가 숫자가 아님 |
| 종료 명령 | `bye` | (연결 종료) | 세션 종료 요청 |

## 주요 코드 설명

### Server.java
- **calc() 메서드**  
  문자열로 전달된 명령어를 파싱하여 산술 연산을 수행  
  예외 발생 시 정의된 에러 코드로 반환  
- **main()**  
  서버 소켓을 생성하여 클라이언트 연결을 대기하고,  
  명령을 수신해 처리 후 응답 전송  

### Client2.java
- **server_info.dat**를 읽어 서버 IP/포트 자동 설정  
- 사용자 입력을 받아 서버로 전송  
- 서버로부터 수신한 결과를 출력  
- "bye" 입력 시 연결 종료  

## 예시 실행 로그

### Server
```
server_info.dat 없음 → 기본 설정 사용 (localhost:9999)
서버가 포트 9999에서 실행 중입니다...
클라이언트 연결됨: /127.0.0.1
[RECV] ADD 10 20
[SEND] RESULT 30
[RECV] DIV 10 0
[SEND] ERROR DIV_BY_ZERO cannot_divide_by_zero
[CLOSE] 소켓 종료: 2025-11-10 14:32:51
```

### Client
```
server_info.dat 없음 → 기본 설정 사용 (localhost:9999)
서버에 연결되었습니다 (localhost:9999)
>> ADD 10 20
Server >> RESULT 30
>> DIV 10 0
Server >> ERROR DIV_BY_ZERO cannot_divide_by_zero
>> bye
연결 종료 요청 전송...
클라이언트 종료
```

