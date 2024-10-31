import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Base64;
import java.util.HashMap;

import java.io.File;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; 


public class MailAppClient extends JFrame{
	private JPanel mainPanel;
	private CardLayout cardLayout;
	private SmtpMailService smtpMailService;
	private Pop3MailService pop3MailService;
	
	private char mode = 'c';
	
	MailAppClient(){
		smtpMailService = new SmtpMailService(this);
		pop3MailService = new Pop3MailService(this);
		setTitle("MailAppClient");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);
        mainPanel.add(new LoginPanel(this, smtpMailService, pop3MailService), "LoginPanel");
        mainPanel.add(new ContentPanel(this, smtpMailService, pop3MailService), "ContentPanel");
        mainPanel.add(new ReceiverPanel(this, pop3MailService), "ReceiverPanel");
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		
		JButton exitButton = new JButton("Exit");
		exitButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		exitButton.setPreferredSize(new Dimension(70, 30));	// 레이아웃 매니저가 크기를 관리하기 때문에, PreferredSize를 사용해야함
		exitButton.setBackground(new Color(128, 128, 128, 150));
		
		JPanel exitButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		exitButtonPanel.add(exitButton);
		getContentPane().add(exitButtonPanel, BorderLayout.SOUTH);
		
		setSize(350, 450);
		setVisible(true);
	}
	
	
	public void showLoginPanel() {
		cardLayout.show(mainPanel, "LoginPanel");
		setSize(350, 450);
	}
	
	public void showContentPanel() {
		cardLayout.show(mainPanel, "ContentPanel");
		setSize(600, 400);
	}
	
	public void showReceiverPanel() {
		cardLayout.show(mainPanel, "ReceiverPanel");
		setSize(600, 400);
	}
	
	
	public void changeMod() {
		if (mode == 'c') {
			mode = 'r';
			showReceiverPanel();
		}
		else if (mode == 'r') {
			mode = 'c';
			showContentPanel();
		}
	}
	
	
	public static void main(String[] args) {
		new MailAppClient();
	}
}
