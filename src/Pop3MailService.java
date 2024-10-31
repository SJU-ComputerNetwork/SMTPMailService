import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JOptionPane;

public class Pop3MailService {
	private MailAppClient mailAppClient;
	
	private static final String pop3Server = "pop.naver.com";
    private static final int pop3Port = 995;
    private static final int MAX_EMAILS = 10;
    
    private String encodedId;
    private String encodedPassword;
    
    Pop3MailService(MailAppClient client){
    	mailAppClient = client;
    }
    
    public void cachingLoginInfo(String id, String password) {
    	encodedId = id;
    	encodedPassword = password;
    }
    
    private void authenticateToServer(BufferedReader reader, PrintWriter writer) throws Exception {
    	
    	 //아이디 전송
    	 writer.println("USER " + encodedId);
         writer.flush();

         // 비밀번호 전송
         writer.println("PASS " + encodedPassword);
         writer.flush();
         checkLoginResponse(reader.readLine(), "\n로그인을 할 수 없습니다. \\n 1. 아이디와 비밀번호를 확인해주세요. \\n 2. 네이버에서 SMTP 사용 허가를 설정해주세요. \\n 3. 2단계 인증이 설정된 경우, 별도의 어플리케이션 비밀번호가 필요합니다.");
    }
    
    
    
    public ReceiveEmail[] receiveMail() {
    	List<ReceiveEmail> emailList = new ArrayList<>();
    	try(
    			SSLSocket pop3Socket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(pop3Server, pop3Port);
    			BufferedReader reader = new BufferedReader(new InputStreamReader(pop3Socket.getInputStream()));
    			PrintWriter writer = new PrintWriter(new OutputStreamWriter(pop3Socket.getOutputStream()));
    			){
    		checkResponse(reader.readLine(), "현재 POP3 서버에 접속할 수 없습니다.");
    		
    		authenticateToServer(reader, writer);
    		
    		writer.println("STAT");
            writer.flush();
            checkResponse(reader.readLine(), "메일 수신에 실패하였습니다.");

            String[] statParts = reader.readLine().split(" ");
            int totalEmails = Integer.parseInt(statParts[1]);
            int retrieveCount = Math.min(totalEmails, MAX_EMAILS);
            // 최신 메일부터 10개까지 읽어와서 ReceiveEmail 객체로 저장
            for (int i = totalEmails; i > totalEmails - retrieveCount; i--) {
                ReceiveEmail email = fetchEmail(reader, writer, i);
                if (email != null) {
                    emailList.add(email);
                }
            }
            
            writer.println("QUIT");
            writer.flush();
            System.out.println("[POP3] 연결 종료");
    		
    	}catch(Exception e) {
    		JOptionPane.showMessageDialog(null, "이메일 수신에 실패하였습니다. 원인 : " + e.toString(), "error", JOptionPane.WARNING_MESSAGE);
    		
    		if (e instanceof LoginException) {
    			mailAppClient.showLoginPanel();
    		}
    		return null;
    	}
		
    	return emailList.toArray(new ReceiveEmail[0]);
    }
    
    
     
    // 각 메일을 가져와 ReceiveEmail 객체로 변환
    private ReceiveEmail fetchEmail(BufferedReader reader, PrintWriter writer, int emailIndex) throws IOException {
        writer.println("RETR " + emailIndex);
        writer.flush();
        String response = reader.readLine();

        if (!response.startsWith("+OK")) {
            System.out.println("[POP3] " + emailIndex + "번째 메일을 가져오지 못했습니다.");
            return null;
        }

        String line;
        StringBuilder contentBuilder = new StringBuilder();
        boolean isBase64Content = false;
        boolean isReadingContent = false;

        String sender = "";
        String subject = "";

        while (!(line = reader.readLine()).equals(".")) {
            if (line.startsWith("From:")) {
                sender = line.substring(5).trim();
            } else if (line.startsWith("Subject:")) {
                subject = line.substring(8).trim();
            } else if (line.startsWith("Content-Transfer-Encoding:")) {
                isBase64Content = line.toLowerCase().contains("base64");
            }

            // 본문 내용 시작
            if (line.isEmpty()) {
                isReadingContent = true;
                continue;
            }

            // 본문 내용 처리
            if (isReadingContent) {
                if (isBase64Content) {
                    contentBuilder.append(line);
                } else {
                    contentBuilder.append(line).append("\n");
                }
            }
        }

        // Base64로 인코딩된 경우 디코딩 후 본문 설정
        String content;
        if (isBase64Content) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(contentBuilder.toString());
                content = new String(decodedBytes);
            } catch (Exception e) {
                System.err.println("[POP3 에러] Base64 디코딩 오류: " + e.getMessage());
                content = "[내용 디코딩 오류]";
            }
        } else {
            content = contentBuilder.toString();
        }

        return new ReceiveEmail(sender, subject, content);
    }
    
    
    // 응답 메세지를 확인한 후, pop3의 응답 코드로 +OK를 받지 않으면 예외를 던짐
    private void checkResponse(String response, String message) throws Exception{
    	if (response == null || !response.startsWith("+OK"))
    			throw new Exception(response + ", message : " + message);
    }
    
    // 아이디, 비밀번호 관련 에러는 LoginException으로 따로 처리 --> 로그인 UI로 다시 돌아가기 위함
    private void checkLoginResponse(String response, String message) throws LoginException{
    	if (response == null || !response.startsWith("+OK"))
    		throw new LoginException(response + ", message : " + message);
    }
}
