import java.io.*;
import java.util.Base64;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JOptionPane;

public class MailService {
	String smtpServer = "smtp.naver.com";
    int port = 465;
    
    public SSLSocket socket;
    public BufferedReader reader;
    public PrintWriter writer;
    
    MailService(){
    	try {
    		socket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(smtpServer, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            String response = reader.readLine();
            if (!response.startsWith("220")) {
                showErrorMessage("SMTP 서버에 연결할 수 없습니다." + response);
                return;
            }
    	}
    	catch(Exception e){
    		showErrorMessage("SMTP 서버에 연결할 수 없습니다." + e.toString());
    	}
    }
    
    
    public boolean loginToServer(String id, String password) {
    	try {
            writer.println("AUTH LOGIN");
            writer.flush();
            checkResponse(reader.readLine(), "현재 명령어를 전송할 수 없습니다. SMTP 연결을 확인해주세요.");

            // 아이디 전송
            String encodedId = Base64.getEncoder().encodeToString(id.getBytes());
            writer.println(encodedId);
            writer.flush();
            checkResponse(reader.readLine(), "아이디를 확인해 주세요.");

            // 비밀번호 전송
            String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
            writer.println(encodedPassword);
            writer.flush();
            String response = reader.readLine();

            // 로그인 실패
            if (response.startsWith("535")) { 
            	System.out.println(response);
                showErrorMessage("로그인을 할 수 없습니다. \n 1. 비밀번호를 확인해주세요. \n 2. 네이버에서 SMTP 사용 허가를 설정해주세요. \n 3. 2단계 인증이 설정된 경우, 별도의 어플리케이션 비밀번호가 필요합니다.");
                return false;
            }
            
            // 다른 이유로 실패 시, 메세지를 통해 확인
            checkResponse(response, "로그인 할 수 없습니다. 로그를 확인해주세요.");
            return true;
            
        } catch (IOException e) {
        	System.out.println(e.toString());
            showErrorMessage("로그인 중 오류가 발생했습니다. 다시 시도해 주세요.");
            return false;
        }
    }
    
    
    
    
    
    // 응답 메세지를 확인한 후, n-smtp의 에러 메시지인 4나 5로 시작하는 코드이면 예외를 던지고 에러 메세지를 출력
    private static void checkResponse(String response, String errorMessage) throws IOException {
        if (response == null || response.startsWith("4") || response.startsWith("5")) {
            throw new IOException(errorMessage);
        }
    }
    
    
    private static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "error", JOptionPane.WARNING_MESSAGE);
    }
}
