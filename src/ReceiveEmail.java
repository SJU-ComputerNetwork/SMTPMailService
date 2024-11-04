
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class ReceiveEmail {
    public String sender;
    public String subject;
    public String content;
    public List<String> fileNameList;

    public ReceiveEmail(String sender, String subject, String content, String encodingMethod, List<String> fileNames) {
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

    
    
    public String decodeMime(String encodedText) {
        StringBuilder decodedText = new StringBuilder();
        Pattern pattern = Pattern.compile("=\\?(UTF-8|utf-8)\\?B\\?(.+?)\\?=");
        Matcher matcher = pattern.matcher(encodedText);

        while (matcher.find()) {
            String base64Encoded = matcher.group(2); // 인코딩된 텍스트 부분만 추출
            byte[] decodedBytes = Base64.getDecoder().decode(base64Encoded);
            decodedText.append(new String(decodedBytes)); // 디코딩한 텍스트를 추가
        }

        // 디코딩된 텍스트를 반환하거나 디코딩할 부분이 없으면 원본 반환
        return decodedText.length() > 0 ? decodedText.toString() : encodedText;
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
