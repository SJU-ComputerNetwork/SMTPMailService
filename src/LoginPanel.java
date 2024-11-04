import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Base64;

import javax.swing.*;
public class LoginPanel extends JPanel {
	
	private MailAppClient mailAppClient;
	private SmtpMailService smtpMailService;
	
	LoginPanel(MailAppClient client, SmtpMailService smtpService){
		mailAppClient = client;
		smtpMailService = smtpService;
		setLayout(new BorderLayout(0, 0));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createDescriptionPanel(), BorderLayout.CENTER);
        add(createLoginPanel(), BorderLayout.SOUTH);
	}
	
	
	 // 제목 섹션 생성
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 0)); // Padding 추가
        JLabel titleLabel = new JLabel("SMTP를 사용한 이메일 전송 앱");
        Font font = new Font("굴림", Font.BOLD, 20);
        titleLabel.setFont(font);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel);
        return titlePanel;
    }
    
    
    // 설명 섹션 생성
    private JPanel createDescriptionPanel() {
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));
        JLabel descriptionLabel = new JLabel(
        		"<html>1. 본 어플리케이션은 네이버 메일 서버에 접속합니다. 따라서 네이버 아이디로 접속해주세요.<br><br>" +
                "2. 로그인이 안 될 경우 아래 사항을 확인해주세요.<br>" +
                " 		- 2단계 인증을 사용하는 경우, 네이버에서 어플리케이션 암호를 따로 만들어서 사용해야 합니다.<br>" +
                " 		- 네이버 메일 설정에서 SMTP 사용 옵션 체크해주세요.<br><br>" +
                "3. 서버 상황에 따라 가끔 접속이 안 될 수도 있습니다. 접속이 안되면 껐다가 다시 켜주세요.</html>");
        Font font = new Font("굴림", Font.BOLD, 13);
        descriptionLabel.setFont(font);
        descriptionLabel.setHorizontalAlignment(JLabel.CENTER);
        descriptionPanel.add(descriptionLabel, BorderLayout.NORTH);

        return descriptionPanel;
    }

    
    // 로그인 입력 섹션 생성
    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new BorderLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel inputSection = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);	//내부의 각 컴포넌트 간격 조정

        // ID와 Password 입력 필드
        JLabel idLabel = new JLabel("ID");
        JTextField idField = new JTextField(14);
        idField.setBorder(BorderFactory.createLineBorder(Color.GREEN));

        JLabel passwordLabel = new JLabel("Password");
        JPasswordField passwordField = new JPasswordField(14);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GREEN));

        // ID와 Password Field 배치
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputSection.add(idLabel, gbc);

        gbc.gridx = 1;
        inputSection.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputSection.add(passwordLabel, gbc);

        gbc.gridx = 1;
        inputSection.add(passwordField, gbc);

        // 로그인 버튼 생성, 버튼 클릭 이벤트에 로그인 기능 등록
        JButton loginButton = new JButton("   Login   ");
        loginButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String idString = idField.getText();
            	char[] passwordChars = passwordField.getPassword();
                String passwordString = new String(passwordChars);
                smtpMailService.cachingLoginInfo(idString, passwordString);
                mailAppClient.showContentPanel();
            }
        });
        
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2; // 로그인 버튼을 ID와 Password 필드 높이에 맞추기 위해 2행에 걸쳐 배치
        gbc.fill = GridBagConstraints.BOTH;
        inputSection.add(loginButton, gbc);
        
        loginPanel.add(inputSection, BorderLayout.CENTER);

        return loginPanel;
    }

}
