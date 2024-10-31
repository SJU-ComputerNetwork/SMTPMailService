import java.io.*;
import java.nio.file.Files;
import java.util.Base64;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JOptionPane;

public class MailService {
	private MailAppClient mailAppClient;
	
	private static String smtpServer = "smtp.naver.com";
	private static int smtpPort = 465;
 
    private static String pop3Server = "pop.naver.com";
    private static int pop3Port = 995;
    
    private String encodedId;
    private String encodedPassword;
    
    MailService(MailAppClient client){
    	mailAppClient = client;
    }
    
    public void cachingLoginInfo(String id, String password) {
    	encodedId = Base64.getEncoder().encodeToString(id.getBytes());
    	encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
    }
    
    
    private void authenticateToServer(BufferedReader reader, PrintWriter writer) throws Exception {
    	
        writer.println("AUTH LOGIN");
        writer.flush();
        checkResponse(reader.readLine(), "AUTH LOGIN 명령어 전송에 실패하였습니다.");

        // 아이디 전송
        writer.println(encodedId);
        writer.flush();
        checkLoginResponse(reader.readLine(), "입력한 아이디를 찾을 수 없습니다.");

        // 비밀번호 전송
        writer.println(encodedPassword);
        writer.flush();
        checkLoginResponse(reader.readLine(), "\n로그인을 할 수 없습니다. \n 1. 비밀번호를 확인해주세요. \n 2. 네이버에서 SMTP 사용 허가를 설정해주세요. \n 3. 2단계 인증이 설정된 경우, 별도의 어플리케이션 비밀번호가 필요합니다.");
    }
    
    
    private void transmitMailData(BufferedReader reader, PrintWriter writer, String sender, String receiver, String subject, String content, File[] attachedFile) throws Exception {

        writer.write("MAIL FROM: <" + sender + ">\n");
        writer.flush();
        checkResponse(reader.readLine(), "발신자 설정에 실패했습니다.");

        String[] receivers = receiver.split(",");
        for (String rc : receivers) {
        	rc = rc.trim();	//공백 제거
            if (rc.length() > 0) {
                writer.write("RCPT TO:<" + rc + ">\n");
                writer.flush();
                checkResponse(reader.readLine(), rc + "수신자를 찾을 수 없습니다.");
            }
        }

        writer.write("DATA\n");
        writer.flush();
        checkResponse(reader.readLine(), "현재 DATA 명령어 전송이 불가능 합니다.");

        // 헤더
        writer.write("From: " + sender + "\r\n");
        writer.write("To: " + String.join(", ", receivers) + "\r\n");
        writer.write("Subject: " + subject + "\r\n");
        writer.write("Content-Type: multipart/mixed; boundary=frontier\r\n");
        writer.write("\r\n");

        // 본문
        writer.write("--frontier\r\n");
        writer.write("Content-Type: text/plain\r\n");
        writer.write("\r\n");
        writer.write(content + "\r\n");
        
        
        // 첨부 파일
        for (File file : attachedFile) {
            writer.write("--frontier\r\n");
            writer.write("Content-Type: application/octet-stream\r\n");
            writer.write("Content-Transfer-Encoding: base64\r\n");
            writer.write("Content-Disposition: attachment; filename=\"" + file.getName() + "\"\r\n");
            writer.write("\r\n");

            try {
                byte[] fileContent = Files.readAllBytes(file.toPath());
                String encodedFile = Base64.getEncoder().encodeToString(fileContent);
                writer.write(encodedFile + "\r\n");
            } catch (Exception e) {
                throw new IOException("첨부 파일 전송 중 오류가 발생했습니다: " + file.getName());
            }
        }

        // 이메일 데이터 송신 종료
        writer.write("--frontier--\r\n");
        writer.write(".\r\n");
        writer.flush();
        checkResponse(reader.readLine(), "메시지 전송에 실패했습니다.");
    }
    

    public void sendMail(String sender, String receiver, String subject, String content, File[] attachedFile) {
    	
    	try(
    			SSLSocket smtpSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(smtpServer, smtpPort);
    			BufferedReader reader = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
    			PrintWriter writer = new PrintWriter(new OutputStreamWriter(smtpSocket.getOutputStream()));
    			){
    		checkResponse(reader.readLine(), "STMP 서버 접속");

            authenticateToServer(reader, writer);

            transmitMailData(reader, writer, sender, receiver, subject, content, attachedFile);
            
            writer.println("QUIT");
            writer.flush();
            if (reader.readLine().startsWith("2")) {
                JOptionPane.showMessageDialog(null, "메일이 성공적으로 발송되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            else {
            	throw new Exception("QUIT 명령어 전송에 실패하였습니다.");
            }

    	}catch(Exception e) {
    		JOptionPane.showMessageDialog(null, "이메일 전송에 실패하였습니다. 원인 : " + e.toString(), "error", JOptionPane.WARNING_MESSAGE);
    		
    		if (e instanceof LoginException) {
    			mailAppClient.showLoginPanel();
    		}
    		return;
    	}
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
