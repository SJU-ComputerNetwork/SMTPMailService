
import javax.swing.*;
import javax.swing.border.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


class ContentPanel extends JPanel {
	
	private MailAppClient mailAppClient;
	
	ContentPanel (MailAppClient client){
		mailAppClient = client;
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// Sender 라벨 및 텍스트 필드
        JLabel senderLabel = new JLabel("Sender");
        senderLabel.setBounds(240, 10, 70, 20);
        add(senderLabel);

        JTextField senderField = new JTextField();
        senderField.setBounds(320, 10, 170, 20);
        add(senderField);

        // Reciever 라벨 및 텍스트 필드
        JLabel receiverLabel = new JLabel("Reciever");
        receiverLabel.setBounds(240, 40, 70, 20);
        add(receiverLabel);

        JTextField receiverField = new JTextField();
        receiverField.setBounds(320, 40, 170, 20);
        add(receiverField);

        // Send 버튼
        JButton sendButton = new JButton("Send");
        sendButton.setBounds(500, 10, 80, 50);
        add(sendButton);

        // Attach 버튼
        JButton exitButton = new JButton("Attach");
        exitButton.setBounds(500, 70, 80, 50);
        add(exitButton);

        // Subject 라벨 및 텍스트 필드
        JLabel subjectLabel = new JLabel("Subject");
        subjectLabel.setBounds(10, 80, 50, 20);
        add(subjectLabel);

        JTextField subjectField = new JTextField();
        subjectField.setBounds(10, 100, 370, 20);
        add(subjectField);

        // Text 영역
        JTextArea textArea = new JTextArea();
        textArea.setBounds(10, 130, 370, 220);
        add(textArea);

        // File 라벨
        JLabel fileLabel = new JLabel("File");
        fileLabel.setBounds(390, 80, 50, 20);
        add(fileLabel);
        
        // Filelist 라벨
        JLabel fileListLabel = new JLabel();
        EtchedBorder border;
        border = new EtchedBorder(EtchedBorder.RAISED);
        fileListLabel.setBorder(border);
        fileListLabel.setBounds(390, 130, 190, 220);
        add(fileListLabel);
	}

}