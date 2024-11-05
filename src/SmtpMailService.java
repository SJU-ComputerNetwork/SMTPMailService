import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JOptionPane;

public class SmtpMailService {
	private MailAppClient mailAppClient; // MailAppClient 객체 참조 필드
	
	private static final String smtpServer = "smtp.naver.com"; // 네이버 SMTP 서버 주소
	private static final int smtpPort = 465; // 네이버 SMTP 서버 포트 번호
 
    private String encodedId; // Base64로 인코딩한 사용자 ID
    private String encodedPassword; // Base64로 인코딩한 사용자 비밀번호

    //생성자
    SmtpMailService(MailAppClient client){
    	mailAppClient = client;
    } // 이메일 전송 실패 시, client를 통해 해결
    // 예를 들어 로그인 오류 발생 -> MailAppClient에서 로그인 화면 다시 표시
    
    public void cachingLoginInfo(String id, String password) {
        // 보안 강화를 위해서 ID와 비밀번호를 Base64로 인코딩한다
    	encodedId = Base64.getEncoder().encodeToString(id.getBytes());
    	encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
        // 문자열 -> 바이트 배열 -> Base64 인코딩(ASCII 문자열) -> 그 결과를 문자열로
    }
    
    
    private void
    authenticateToServer(BufferedReader reader, PrintWriter writer) throws Exception {
    	
        writer.println("AUTH LOGIN"); // AUTH LOGIN 명령어를 서버에 보내 인증 시작
                                      // SMTP 서버에서 사용자 인증 요구에 사용
        writer.flush(); // 보내기, 버퍼 내용 비우기
        // 서버 응답이 오류(4XX, 5XX)로 시작하면 예외 발생시킴
        checkResponse(reader.readLine(), "AUTH LOGIN 명령어 전송에 실패하였습니다.");

        // 아이디 전송
        writer.println(encodedId); // writer.println은 네트워크 스트림을 통해 SMTP 서버로 전송
        writer.flush(); // 버퍼에 있는 내용 즉시 서버로 전송
        // 아이디 틀렸을 때 서버응답오류(4XX, 5XX)로 시작하면 예외 발생시킴
        checkLoginResponse(reader.readLine(), "\n로그인을 할 수 없습니다. \n 1. 아이디와 비밀번호를 확인해주세요. \n 2. 네이버에서 SMTP 사용 허가를 설정해주세요. \n 3. 2단계 인증이 설정된 경우, 별도의 어플리케이션 비밀번호가 필요합니다.");

        // 비밀번호 전송
        writer.println(encodedPassword); // 네트워크 스트림을 통해 SMTP 서버로 전송
        writer.flush(); //버퍼에 있는 내용 즉시 서버로 전송
        // 비밀번호 틀렸을 때 서버 응답이 오류(4XX, 5XX)로 시작하면 예외 발생시킴
        checkLoginResponse(reader.readLine(), "\n로그인을 할 수 없습니다. \n 1. 아이디와 비밀번호를 확인해주세요. \n 2. 네이버에서 SMTP 사용 허가를 설정해주세요. \n 3. 2단계 인증이 설정된 경우, 별도의 어플리케이션 비밀번호가 필요합니다.");
    }
    
    
    private void transmitMailData(BufferedReader reader, PrintWriter writer, String sender, String receiver, String subject, String content, File[] attachedFile) throws Exception {
        // 1. 발신자 설정
        writer.println("MAIL FROM: <" + sender + ">"); // MAIL FROM 명령어 : 발신자 설정
        // 발신자 이메일 주소를 SMTP 서버에 전달
        writer.flush(); // 버퍼에 저장된 내용을 즉시 전송하도록 한다
        // 응답 확인, 에러가 발생한 경우 예외 던짐
        checkResponse(reader.readLine(), "발신자 설정에 실패했습니다.");

        // 2. 수신자 설정
        // 여러명의 수신자 주소 분리
        String[] receivers = receiver.split(","); // 문자열을 쉼표로 분리 (배열로)
        for (String rc : receivers) {
        	rc = rc.trim();	//공백 제거
            if (rc.length() > 0) {
                writer.println("RCPT TO:<" + rc + ">"); // RCPT TO 명령어 : 수신자 이메일 주소 지정
                writer.flush();
                // 수신자 설정 에러 예외처리
                checkResponse(reader.readLine(), rc + "수신자를 찾을 수 없습니다.");
            }
        }

        // 3. DATA 명령어 전송
        writer.println("DATA"); // DATA 명령어 : 이메일 본문과 첨부 파일 데이터를 전송할 준비를 하겠다
        writer.flush();
        // 서버가 DATA 명령어에 대해 준비되었는지 확인
        // 실패시 예외를 던진다
        checkResponse(reader.readLine(), "현재 DATA 명령어 전송이 불가능 합니다.");

        // 4. 이메일 헤더 작성
        // 발신자 이메일 주소
        writer.write("From: " + sender + "\r\n");

        // 수신자 이메일 주소 (쉼표와 공백으로 구분하여 하나의 문자열로 연결)
        writer.write("To: " + String.join(", ", receivers) + "\r\n");

        // 이메일 제목 지정
        writer.write("Subject: " + subject + "\r\n");

        // 이메일 본문, 첨부파일 포함을 위해 Content-Type을 multipart/mixed로 설정(MIME 표준)
        // multipart : 여러 파트를 가진 이메일
        // mixed : 본문과 첨부 파일이 모두 포함된 혼합 콘텐츠
        // boundary 생성 (랜덤하게 생성된 고유한 문자열)
        String boundary = "----=_Boundary_" + UUID.randomUUID().toString();
        // 헤더,본문,첨부파일을 구분하는 경계문자열 boundary
        writer.write("Content-Type: multipart/mixed; boundary=" + boundary + "\r\n");
        writer.write("\r\n");

        // 5. 이메일 본문 작성
        writer.write("--" + boundary + "\r\n"); // 이메일 전체 헤더를 구분해주기 위해 boundary
        writer.write("Content-Type: text/plain\r\n"); // 본문을 일반 텍스트로 작성하기 위함
        writer.write("\r\n"); // MIME 파트 헤더의 끝을 구분해주기 위해 줄 바꿈
        writer.write(content + "\r\n"); // 본문 내용 작성


        // 6. 첨부 파일 전송
        for (File file : attachedFile) { // 첨부파일 반복 처리
            writer.write("--" + boundary + "\r\n"); // 구분자로 이전 파트(본문 또는 이전 첨부 파일)를 구분
            writer.write("Content-Type: application/octet-stream\r\n"); // 클라이언트에게!! 이 데이터가 파일임을 알림. 일반 텍스트와 구분하게 함
            writer.write("Content-Transfer-Encoding: base64\r\n"); // 파일 데이터를 Base64로 인코딩하여 전송할 것을 명시
            writer.write("Content-Disposition: attachment; filename=\"" + file.getName() + "\"\r\n"); // 첨부파일임을 명시 (attachment), filename은 파일 이름
            writer.write("\r\n"); // 헤더가 끝나고 파일 데이터가 시작됨을 표시

            try {
                byte[] fileContent = Files.readAllBytes(file.toPath()); //파일의 모든 바이트 읽기 (바이트 배열), 일어날 수 있는 문제 : 큰파일 메모리문제
                String encodedFile = Base64.getEncoder().encodeToString(fileContent); //Base64로 인코딩 (이진데이터 -> 텍스트)
                writer.write(encodedFile + "\r\n"); //인코딩된 파일 내용을 서버로 전송
            } catch (Exception e) {
                // 파일 전송 예외처리, 문제가 발생한 파일의 이름을 띄워준다
                throw new IOException("첨부 파일 전송 중 오류가 발생했습니다: " + file.getName());
            }
        }

        // 7. 이메일 데이터 송신 종료
        writer.write("--" + boundary + "--\r\n"); //boundary로 모든 파트가 끝났음을 알림, -을 두번붙이면 이메일의 모든 MIME 파트 끝났음을 의미
        writer.write(".\r\n"); //데이터 전송 끝났음을 표시 . 추가
        writer.flush(); //버퍼의 모든 데이터를 서버로 보내고 버퍼 비워짐
        checkResponse(reader.readLine(), "메시지 전송에 실패했습니다."); //메시지 전송 실패 예외처리
    }
    

    // 이메일 전송 성공 여부 반환 - boolean
    public boolean sendMail(String sender, String receiver, String subject, String content, File[] attachedFile) {

        // SMTP 서버와의 연결 설정
    	try(
                // smtpServer와 smtpPort로 지정된 SMTP 서버에 연결하는 SSL 소켓 생성
    			SSLSocket smtpSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(smtpServer, smtpPort);
    			// 서버의 응답을 읽기 위한 스트림
                BufferedReader reader = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
    			// SMTP 명령어와 이메일 데이터를 서버로 전송하기 위한 스트림
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(smtpSocket.getOutputStream()));
    			){
    		checkResponse(reader.readLine(), "현재 STMP 서버에 접속할 수 없습니다."); //SMTP 서버의 첫 번째 응답 확인, 인결이 성공적으로 이루어졌는지 여부 판단

            authenticateToServer(reader, writer); //서버 인증 (로그인 과정)

            transmitMailData(reader, writer, sender, receiver, subject, content, attachedFile); // 이메일 데이터 전송
            
            writer.println("QUIT"); //SMTP 연결 종료
            writer.flush();
            if (reader.readLine().startsWith("2")) { //2XX 응답은 이메일이 정상적으로 발송된 것
                JOptionPane.showMessageDialog(null, "메일이 성공적으로 발송되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
            }
            else { //2XX 응답이 아니면 예외 발생시킴
            	throw new Exception("QUIT 명령어 전송에 실패하였습니다.");
            }

    	}catch(Exception e) { //예외처리
            // 실패 메시지, 이메일 전송이 실패한 경우 예외 catch후 사용자에게 실패 메시지 표시
    		JOptionPane.showMessageDialog(null, "이메일 전송에 실패하였습니다. 원인 : " + e.toString(), "error", JOptionPane.WARNING_MESSAGE);
    		
            // 만약 예외가 로그인 관련이면 로그인창을 다시 표시
    		if (e instanceof LoginException) {
    			mailAppClient.showLoginPanel();
    		}
    		return false; // 이메일 전송에 실패
    	}
    	return true; // 이메일 전송에 성공
    }

    
    // 응답 메세지를 확인한 후, n-smtp의 에러 메시지인 4나 5로 시작하는 코드이면 예외를 던짐
    private void checkResponse(String response, String message) throws Exception{
    	if (response == null || response.startsWith("4") || response.startsWith("5"))
    			throw new Exception(response + ", message : " + message);
    }
    
    // 아이디, 비밀번호 관련 에러는 LoginException으로 따로 처리 --> 로그인 UI로 다시 돌아가기 위함
    private void checkLoginResponse(String response, String message) throws LoginException{
    	if (response == null || response.startsWith("4") || response.startsWith("5"))
    		throw new LoginException(response + ", message : " + message);
    }

}
