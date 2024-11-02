import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EtchedBorder;


public class ReceiverPanel extends JPanel{
	private MailAppClient mailAppClient;
	private Pop3MailService pop3MailService;
	
	// 아래 mailJList와 혼동 주의! , mailJList는 UI임
	private List<ReceiveEmail> mailList = new ArrayList<ReceiveEmail>();
	
	JTextField senderField;
	JTextField subjectField;
	JTextArea textArea;
	
	DefaultListModel<String> listModel;
	JList<String> mailJList;
	
	ReceiverPanel (MailAppClient client, Pop3MailService service){
		mailAppClient = client;
		pop3MailService = service;
		setLayout(null);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		
		JButton changeMod = new JButton("메일쓰기");
		changeMod.setBounds(5, 10, 90, 50);
		changeMod.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mailAppClient.showContentPanel();
			}
		});
		add(changeMod);
		
		
		JButton refreshBtn = new JButton("새로고침");
		refreshBtn.setBounds(5, 70, 90, 50);
		refreshBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fetchReceiveMail();
			}
		});
		add(refreshBtn);
		
		
		JLabel RecievedMailLabel = new JLabel("수신함");
		RecievedMailLabel.setBounds(200, 70, 40, 20);
        add(RecievedMailLabel);
        
        JLabel senderLabel = new JLabel("Sender");
        senderLabel.setBounds(100, 170, 50, 20);
        add(senderLabel);

		JLabel subjectLabel = new JLabel("Subject");
        subjectLabel.setBounds(10, 180, 50, 20);
        add(subjectLabel);

        senderField = new JTextField();
        senderField.setBounds(180, 170, 400, 20);
        senderField.setEditable(false);
        add(senderField);
        
        subjectField = new JTextField();
        subjectField.setBounds(10, 200, 570, 20);
        subjectField.setEditable(false);
        add(subjectField);

        textArea = new JTextArea();
        textArea.setBounds(10, 230, 570, 220);
        textArea.setEditable(false);
        add(textArea);
        
        listModel = new DefaultListModel<>();
        mailJList = new JList<>(listModel);
        mailJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // JList 항목 선택 시 Email 데이터 표시
        mailJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // 값이 최종적으로 선택될 때만 동작
                int selectedIndex = mailJList.getSelectedIndex();
                System.out.println(selectedIndex);
                if (selectedIndex != -1) {
                    ReceiveEmail selectedEmail = mailList.get(selectedIndex);
                    subjectField.setText(selectedEmail.subject);
                    senderField.setText(selectedEmail.sender);
                    textArea.setText(selectedEmail.content + "\n\n");
                    
                    for(String fileName : selectedEmail.fileNameList) {
                    	textArea.setText(textArea.getText() + "첨부파일 : " + fileName +'\n');
                    }
                }
            }
        });
        
        JScrollPane mailListScrollPane = new JScrollPane(mailJList);
        mailListScrollPane.setBounds(250, 10, 330, 150);
        EtchedBorder border = new EtchedBorder(EtchedBorder.RAISED);
        mailListScrollPane.setBorder(border);
        add(mailListScrollPane);
        
        setVisible(true);
	}
	
	
	public void fetchReceiveMail() {
		mailList.clear();
		listModel.clear();
		mailJList.clearSelection();
		
		ReceiveEmail[] emailArray = pop3MailService.receiveMail();

		// 리스트 모델에 메일 제목 추가
	    for (ReceiveEmail email : emailArray) {
	    	mailList.add(email);
	        listModel.addElement(email.subject);
	    }
	}

}
