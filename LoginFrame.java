package desktop;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Login");
        setSize(300, 150);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ID 라벨 및 텍스트 필드
        JLabel idLabel = new JLabel("ID");
        idLabel.setBounds(10, 10, 30, 20);
        add(idLabel);

        JTextField idField = new JTextField();
        idField.setBounds(50, 10, 200, 20);
        add(idField);

        // PW 라벨 및 텍스트 필드
        JLabel pwLabel = new JLabel("PW");
        pwLabel.setBounds(10, 40, 30, 20);
        add(pwLabel);

        JTextField pwField = new JTextField();
        pwField.setBounds(50, 40, 200, 20);
        add(pwField);

        // Login 버튼
        JButton loginButton = new JButton("Log in");
        loginButton.setBounds(100, 70, 80, 30);
        add(loginButton);

        // Login 버튼 클릭 이벤트
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 로그인 버튼 클릭 시 이메일 인터페이스 창을 열고, 현재 창을 닫음
                new EmailInterface();
                dispose();
            }
        });
    }

    public static void main(String[] args) {
    	LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
    }
}

class EmailInterface extends JFrame {
    public EmailInterface() {
        setTitle("Email Interface");
        setSize(600, 400);
        setLayout(null); // Absolute positioning 사용
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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
        
        setVisible(true);
    }
}