import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

class Email {
	String subject;
	String sender;
	String text;
}

public class ReceiverPanel extends JPanel{
private MailAppClient mailAppClient;
	
	ReceiverPanel (MailAppClient client){
		mailAppClient = client;
		setLayout(null);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JButton changeMod = new JButton("메일 쓰기");
		changeMod.setBounds(10, 10, 120, 50);
		add(changeMod);
		
		changeMod.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mailAppClient.changeMod();
			}
		});
		
		JLabel RecievedMailLabel = new JLabel("수신함");
		RecievedMailLabel.setBounds(180, 25, 60, 20);
        add(RecievedMailLabel);
        
        Email EM1 = new Email();
        EM1.subject = "Email 1";
        EM1.sender = "Kim@naver.com";
        EM1.text = "Hi";
        Email EM2 = new Email();
        EM2.subject = "Email 2";
        EM2.sender = "Lee@naver.com";
        EM2.text = "Bye";
        Email EM3 = new Email();
        EM3.subject = "Email 3";
        EM3.sender = "Park@naver.com";
        EM3.text = "Nice to meet you";
        Email EM4 = new Email();
        EM4.subject = "Email 4";
        EM4.sender = "Choi@naver.com";
        EM4.text = "Are you okay?";
        Email EM5 = new Email();
        EM5.subject = "Email 5";
        EM5.sender = "Jung@naver.com";
        EM5.text = "hello world";
        Email EM6 = new Email();
        EM6.subject = "Email 6";
        EM6.sender = "Jang@naver.com";
        EM6.text = "Do you like chicken?\nDo you like pizza?";
        Email EM7 = new Email();
        EM7.subject = "Email 7";
        EM7.sender = "Lim@naver.com";
        EM7.text = "It's so difficult";
        Email EM8 = new Email();
        EM8.subject = "Email 8";
        EM8.sender = "Bae@naver.com";
        EM8.text = "Call me please";
        Email EM9 = new Email();
        EM9.subject = "Email 9";
        EM9.sender = "Gang@naver.com";
        EM9.text = "I'm fine";
        Email EM10 = new Email();
        EM10.subject = "Email 10";
        EM10.sender = "Park@naver.com";
        EM10.text = "It's holiday!";
        
        Email[] EmailList = {
        		EM1, EM2, EM3, EM4, EM5, EM6, EM7, EM8, EM9, EM10
        };
        
        String[] EmailSubjects = new String[EmailList.length];
        for (int i = 0; i < EmailList.length; i++) {
            EmailSubjects[i] = EmailList[i].subject;
        }
        
        // JList 생성 및 JScrollPane에 추가
        JList<String> mailList = new JList<>(EmailSubjects);
        mailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane mailListScrollPane = new JScrollPane(mailList);
        mailListScrollPane.setBounds(250, 10, 330, 50);
        EtchedBorder border = new EtchedBorder(EtchedBorder.RAISED);
        mailListScrollPane.setBorder(border);
        add(mailListScrollPane);
        
        JLabel senderLabel = new JLabel("Sender");
        senderLabel.setBounds(100, 70, 50, 20);
        add(senderLabel);

        JTextField senderField = new JTextField();
        senderField.setBounds(180, 70, 400, 20);
        senderField.setEditable(false);
        add(senderField);
		
		JLabel subjectLabel = new JLabel("Subject");
        subjectLabel.setBounds(10, 80, 50, 20);
        add(subjectLabel);

        JTextField subjectField = new JTextField();
        subjectField.setBounds(10, 100, 570, 20);
        subjectField.setEditable(false);
        add(subjectField);

        JTextArea textArea = new JTextArea();
        textArea.setBounds(10, 130, 570, 220);
        textArea.setEditable(false);
        add(textArea);
        
        
        // JList 항목 선택 시 Email 데이터 표시
        mailList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // 값이 최종적으로 선택될 때만 동작
                int selectedIndex = mailList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Email selectedEmail = EmailList[selectedIndex];
                    subjectField.setText(selectedEmail.subject);
                    senderField.setText(selectedEmail.sender);
                    textArea.setText(selectedEmail.text);
                }
            }
        });
        
        setVisible(true);
	}
	
}
