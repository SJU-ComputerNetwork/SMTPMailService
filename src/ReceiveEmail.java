
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class ReceiveEmail {
    public String sender;
    public String subject;
    public String content;
    public List<String> fileNameList;

    public ReceiveEmail(String sender, String subject, String content, List<String> fileNames) {
        this.sender = decodeMime(sender);
        this.subject = decodeMime(subject);
        this.content = extractPlainContent(content);
        this.fileNameList = fileNames;
    }

    public String getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Sender: " + sender + "\nSubject: " + subject + "\nContent: " + content;
    }

    // MIME 인코딩된 문자열을 디코딩
    private String decodeMime(String encodedText) {
        if (encodedText.contains("=?UTF-8?B?")) {
            Pattern pattern = Pattern.compile("=\\?UTF-8\\?B\\?(.+?)\\?=");
            Matcher matcher = pattern.matcher(encodedText);
            if (matcher.find()) {
                String base64Encoded = matcher.group(1);
                byte[] decodedBytes = Base64.getDecoder().decode(base64Encoded);
                return new String(decodedBytes);
            }
        }
        else if(encodedText.contains("=?utf-8?B?")) {
        	Pattern pattern = Pattern.compile("=\\?utf-8\\?B\\?(.+?)\\?=");
            Matcher matcher = pattern.matcher(encodedText);
            if (matcher.find()) {
                String base64Encoded = matcher.group(1);
                byte[] decodedBytes = Base64.getDecoder().decode(base64Encoded);
                return new String(decodedBytes);
            }
        }
        return encodedText; // 디코딩이 필요하지 않으면 그대로 반환
    }

    // 본문에서 필요한 `plain text` 내용만 추출
    private String extractPlainContent(String rawContent) {
        StringBuilder plainTextContent = new StringBuilder();
        String[] lines = rawContent.split("\n");
        boolean isReadingContent = false;

        for (String line : lines) {
            // Content-Type을 확인하고 plain text 부분만 읽기 시작
            if (line.contains("Content-Type: text/plain")) {
                isReadingContent = true;
                continue;
            } else if (line.contains("Content-Type: text/html") || line.startsWith("--")) {
                isReadingContent = false;
            }

            if (isReadingContent && !line.trim().isEmpty()) {
                plainTextContent.append(line.trim()).append("\n");
            }
        }
        return plainTextContent.toString().trim();
    }
}
