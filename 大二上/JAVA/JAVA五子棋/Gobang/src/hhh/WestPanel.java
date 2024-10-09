package hhh;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

public class WestPanel extends JPanel{
	private JTextArea textArea;
	private JTextField textTF;
	private JButton enter = new JButton("发送") ;
		
	public WestPanel() {
		setLayout(new GridBagLayout());
		
        JScrollPane scrollPane = new JScrollPane(getTextArea());

        //设置布局约束
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;//填充水平和垂直
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;//文本区域在垂直方向上拉伸
        gbc.insets = new Insets(10, 10, 0, 10);
        add(scrollPane, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weighty = 0;//恢复权重
        gbc.fill = GridBagConstraints.HORIZONTAL;//只填充水平
        gbc.insets = new Insets(5, 10, 5, 5);
        add(getTextTF(), gbc);

        gbc.gridx++;
        gbc.weightx = 0;//恢复权重
        gbc.fill = GridBagConstraints.NONE;//不填充
        gbc.insets = new Insets(5, 5, 5, 10);
        add(enter, gbc);
		
		enter.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Vars.control.getIfConnect()==true){
					String text = textTF.getText();
					if(text.isEmpty()==false){
						textTF.setText("");//清空文本框
						addText("我："+text);
						Vars.net.sendChat("对方："+text);
					}
				}
			}
		});
	}
	
	
	private JTextField getTextTF(){
		if(textTF==null){
			textTF = new JTextField(15);
			textTF.setFont(new Font("宋体", Font.PLAIN, 30));
			
		}
		return textTF;
	}
	private JTextArea getTextArea(){
		if(textArea==null){
			textArea = new JTextArea(20,15);
			
			DefaultCaret caret = (DefaultCaret) textArea.getCaret();//自动更新光标位置，确保始终可见最新的文本
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			
			textArea.setFont(new Font("宋体", Font.PLAIN, 30));
			textArea.setEditable(false);//不可编辑
			
		}
		return textArea;
	}
	
	public void addText(String s){
		textArea.append(s);
		textArea.append("\n");
	}
}
