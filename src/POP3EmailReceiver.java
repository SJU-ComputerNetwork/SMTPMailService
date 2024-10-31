import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class POP3EmailReceiver {
    private static final String POP3_SERVER = "pop.naver.com";
    private static final int POP3_PORT = 995;
    private static final int MAX_EMAILS = 3;  // 가져올 메일 수

    private SSLSocket pop3Socket;
    private BufferedReader reader;
    private PrintWriter writer;

    // POP3 서버와 연결을 시작
    private boolean connectToServer(String username, String password) {
        try {
            pop3Socket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(POP3_SERVER, POP3_PORT);
            reader = new BufferedReader(new InputStreamReader(pop3Socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(pop3Socket.getOutputStream()));

            String response = reader.readLine();
            System.out.println("[POP3] 서버 응답: " + response);
            if (!response.startsWith("+OK")) {
                System.out.println("[POP3] 서버 연결 실패: " + response);
                return false;
            }

            return login(username, password);

        } catch (IOException e) {
            System.err.println("[POP3 에러] 서버 연결 실패: " + e.getMessage());
            return false;
        }
    }

    // POP3 서버 로그인
    private boolean login(String username, String password) throws IOException {
        writer.println("USER " + username);
        writer.flush();
        String response = reader.readLine();
        System.out.println("[POP3] USER 명령 응답: " + response);
        if (!response.startsWith("+OK")) {
            return false;
        }

        writer.println("PASS " + password);
        writer.flush();
        response = reader.readLine();
        System.out.println("[POP3] PASS 명령 응답: +OK [보안]");
        return response.startsWith("+OK");
    }

    // 메일함에서 최신 3개의 메일을 수신하여 ReceiveEmail[] 배열로 반환
    public ReceiveEmail[] receiveMail(String username, String password) {
        List<ReceiveEmail> emailList = new ArrayList<>();

        if (!connectToServer(username, password)) {
            System.out.println("[POP3] 서버 연결 또는 로그인 실패");
            return new ReceiveEmail[0];
        }

        try {
            writer.println("STAT");
            writer.flush();
            String response = reader.readLine();
            System.out.println("[POP3] STAT 응답: " + response);

            if (!response.startsWith("+OK")) {
                System.out.println("[POP3] 메일 수신 실패");
                return new ReceiveEmail[0];
            }

            String[] statParts = response.split(" ");
            int totalEmails = Integer.parseInt(statParts[1]);
            int retrieveCount = Math.min(totalEmails, MAX_EMAILS);

            // 최신 메일부터 3개까지 읽어와서 ReceiveEmail 객체로 저장
            for (int i = totalEmails; i > totalEmails - retrieveCount; i--) {
                ReceiveEmail email = fetchEmail(i);
                if (email != null) {
                    emailList.add(email);
                }
            }

            // 연결 종료
            writer.println("QUIT");
            writer.flush();
            System.out.println("[POP3] 연결 종료");

        } catch (IOException e) {
            System.err.println("[POP3 에러] 메일 수신 중 오류: " + e.getMessage());
        }

        return emailList.toArray(new ReceiveEmail[0]);
    }

    // 각 메일을 가져와 ReceiveEmail 객체로 변환
    private ReceiveEmail fetchEmail(int emailIndex) throws IOException {
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


    public static void main(String[] args) {
        POP3EmailReceiver receiver = new POP3EmailReceiver();
        ReceiveEmail[] emails = receiver.receiveMail("이메일@naver.com", "비밀번호");

        // 배열을 이용해 출력
        for (ReceiveEmail email : emails) {
            System.out.println("Sender: " + email.getSender());
            System.out.println("Subject: " + email.getSubject());
            System.out.println("Content: " + email.getContent());
            System.out.println("========================================");
        }
    }


}
