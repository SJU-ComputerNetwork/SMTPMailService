import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
public class LoginPanel extends JPanel {
	
	private MailAppClient mailAppClient;
	
	LoginPanel(MailAppClient client){
		mailAppClient = client;
		
		setLayout(new BorderLayout(0, 0));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createDescriptionPanel(), BorderLayout.CENTER);
        add(createLoginPanel(), BorderLayout.SOUTH);
	}
	
	
	 // 제목 패널 생성
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
    
    
    // 설명 패널 생성
    private JPanel createDescriptionPanel() {
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));
        JLabel descriptionLabel = new JLabel("<html>설명...................................................<br><br>" +
                "1..........................................................<br><br>" +
                "2..........................................................<br><br>" +
                "3..........................................................<br><br>" +
                "4..........................................................<br><br>" +
                "5..........................................................</html>");
        Font font = new Font("굴림", Font.BOLD, 13);
        descriptionLabel.setFont(font);
        descriptionLabel.setHorizontalAlignment(JLabel.CENTER);
        descriptionPanel.add(descriptionLabel, BorderLayout.NORTH);

        return descriptionPanel;
    }

    
    // 로그인 패널 생성
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

        // ID와 Password 필드 배치
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

        // 로그인 버튼을 ID/Password 필드 오른쪽에 위치
        JButton loginButton = new JButton("   Login   ");
        loginButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	mailAppClient.successLogin();
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
