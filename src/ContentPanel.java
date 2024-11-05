import javax.swing.*;
import javax.swing.border.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


class ContentPanel extends JPanel {
	private ContentPanel contentPanel;
	private MailAppClient mailAppClient;
	private SmtpMailService smtpMailService;
	
	JLabel senderLabel;
	JLabel receiverLabel;
	JLabel subjectLabel;
	JLabel fileLabel;
	JLabel fileListLabel;
	
	JTextField senderField;
	JTextField receiverField;
	JTextField subjectField;
	
	JButton sendButton;
	JButton attachButton;
	
	JTextArea contentArea;
	
	JTextArea fileField;
	JFileChooser fileSelector; // 첨부할 파일을 선택하는 기능을 함
	private List<File> selectedFiles = new ArrayList<>(); // 첨부할 파일들의 목록을 저장할 리스트

	
	ContentPanel (MailAppClient client, SmtpMailService smtpService){
		contentPanel = this;
		mailAppClient = client;
		smtpMailService = smtpService;
		setLayout(null);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 윈도우 상하좌우 맨 끝에서 10픽셀씩 떨어진 곳에 내부 여백을 추가하고 실제로 경계선을 그리지는 않음
		
		//setBounds(x 좌표, y 좌표, x 길이, y 길이) << 원하는 위치에 원하는 라벨이나 텍스트 필드, 버튼을 두기 위해 절대 좌표 사용함
		
		// Sender 라벨 및 텍스트 필드
        senderLabel = new JLabel("Sender");
        senderLabel.setBounds(20, 15, 70, 20);
        add(senderLabel);

        senderField = new JTextField();
        senderField.setBounds(100, 20, 170, 20);
        add(senderField);

        // Reciever 라벨 및 텍스트 필드
        receiverLabel = new JLabel("Reciever");
        receiverLabel.setBounds(20, 45, 70, 20);
        add(receiverLabel);

        receiverField = new JTextField();
        receiverField.setBounds(100, 50, 170, 20);
        add(receiverField);

        
        // Send 버튼
        sendButton = new JButton("전송");
        sendButton.setBounds(450, 270, 80, 30);
        add(sendButton);
        

        sendButton.addActionListener(new ActionListener() { // 전송 버튼 클릭시 일어나는 이벤트
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean result = smtpMailService.sendMail(senderField.getText(), receiverField.getText(), subjectField.getText(), contentArea.getText(), selectedFiles.toArray(new File[0]));
                // 각각의 텍스트 필드에서 송신자, 수신자, 제목, 본문을, 파일 리스트에 추가된 파일들을 인자로 하여 smtp sendmail 함수 호출해 메일 전송
                if(result) {
                	receiverField.setText("");
                    subjectField.setText("");
                    contentArea.setText("");
                    fileField.setText("");
                    selectedFiles.clear();
                    
                    //전송 후에는 송신자를 제외한 수신자, 제목, 본문, 파일 필드에 있던 값을 제거하고 파일 리스트에서도 선택된 파일이 없도록 초기화
                }
            }
        });
        
        // Subject 라벨 및 텍스트 필드
        subjectLabel = new JLabel("Subject");
        subjectLabel.setBounds(20, 80, 50, 20);
        add(subjectLabel);

        subjectField = new JTextField();
        subjectField.setBounds(20, 100, 370, 20);
        add(subjectField);

        // Content 영역
        contentArea = new JTextArea();
        contentArea.setBounds(20, 130, 380, 170);
        contentArea.setLineWrap(true); 
        add(contentArea);

        
        // 파일 첨부 관련
        fileField = new JTextArea();
        fileField.setBounds(430, 130, 120, 100);
        fileField.setEditable(false);
        add(fileField);
        
        // content와 filefield에서 jtextfield가 아닌 jtextarea를 사용한 이유는 area를 사용해야 줄바꿈이 가능하기 때문. 줄바꿈이 필요하다 판단된 부분에 area 사용함
        
        fileSelector = new JFileChooser();
        fileSelector.setMultiSelectionEnabled(true);
        
        attachButton = new JButton("파일 첨부");
        attachButton.setBounds(430, 90, 90, 30);
        attachButton.addActionListener(new ActionListener() { // 파일 첨부 버튼을 클릭 시 발생하는 이벤트
			@Override
			public void actionPerformed(ActionEvent e) {
			    int check = fileSelector.showOpenDialog(contentPanel); // 파일 선택 창 열고 사용자가 파일을 선택하거나 취소할 때까지 대기
			    if (check == JFileChooser.APPROVE_OPTION) { 
			    	// 사용자가 파일을 선택하면 showOpenDialog 메서드는 JFileChooser.APPROVE_OPTION을 반환함 (취소한 경우 CANCEL_OPTION)
			        File[] selectedFilesArray = fileSelector.getSelectedFiles(); // 사용자가 선택한 파일들을 File 배열 형태로 가져옴
			        for (File file : selectedFilesArray) {
			            selectedFiles.add(file); // 선택된 각 파일을 '리스트' selectedFiles에 추가
			        }

			        StringBuilder fileNames = new StringBuilder();
			        // 선택한 파일들의 이름을 저장할 StringBuilder 객체 생성
			        // Stringbuilder가 일반 String보다 문자열 추가 및 편집에 이점이 있어 사용

			        for (File file : selectedFiles) {
			            if (fileNames.length() > 0) 
			                fileNames.append("\n");
			            fileNames.append(file.getName());
			            // 각 파일의 이름을 filenames에 추가하고, 첫번째 파일이 아닌 경우 length가 0보다 크기 때문에 이를 조건으로 하여 줄바꿈을 실행
			        }

			        fileField.setText(fileNames.toString()); // fileField에 선택한 파일 이름 목록을 표시함
			    }
			}

		});
        add(attachButton);
        
	}

}
