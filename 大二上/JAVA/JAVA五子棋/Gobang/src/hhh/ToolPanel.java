package hhh;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ToolPanel extends JPanel{
	
	private JTextField ipTF = new JTextField(20);
	private JTextField portTF = new JTextField(10);
	private JButton listenBtn = new JButton("listen");
	private JButton connectBtn = new JButton("connect");
	
	public ToolPanel() {
		ipTF.setText("localhost");
		portTF.setText("8000");
		
		listenBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Vars.net.startListen();
				
				listenBtn.setEnabled(false);//让按钮只能被点击一次
			}
		});
		connectBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String ip= ipTF.getText();
				String port = portTF.getText();
				
				Vars.net.connect(ip, Integer.parseInt(port));
				
			}
		});
		
		add(ipTF);
		add(portTF);
		add(listenBtn);
		add(connectBtn);
	}
	
}
