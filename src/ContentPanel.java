import java.awt.*;
import javax.swing.*;

public class ContentPanel extends JPanel {
	
	private MailAppClient mailAppClient;
	
	ContentPanel(MailAppClient client){
		mailAppClient = client;
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		add(createInputPanel(), BorderLayout.NORTH);
		
	}
	
	private JPanel createInputPanel() {
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout(10,10));
		
		//Label Section
		JPanel labelSection = new JPanel(new GridLayout(7,1,5,5));
		String[] labelNames = {"From", "To", "Subject", "Attach File", "Content"};
		Font labelFont = new Font("Verdana", Font.BOLD, 10);
		for (String labelName : labelNames) {
            JLabel label = new JLabel(labelName);
            label.setFont(labelFont);
            labelSection.add(label);
        }
		
		
		inputPanel.add(labelSection, BorderLayout.WEST);
		
		
		return inputPanel;
	}
	
}
