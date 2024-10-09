package hhh;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class EastPanel extends JPanel{
	
	private JButton Btn1 = new JButton("悔棋");
	private JButton Btn1two = new JButton("上一步");
	
	private JButton Btn2 = new JButton("认输");
	private JButton Btn2two = new JButton("下一步");
	
	private JButton Btn3 = new JButton("再来一局");
	private JButton Btn4 = new JButton("退出游戏");
	
	private JPanel cardPanel1 = new JPanel(new CardLayout());
	private CardLayout cardLayout1;
	
	private JPanel cardPanel2 = new JPanel(new CardLayout());
	private CardLayout cardLayout2;
	
	public EastPanel() {
		setLayout(new GridLayout(4, 1,0,30));
		
        cardPanel1.add(Btn1, "悔棋");
        cardPanel1.add(Btn1two, "上一步");
        cardLayout1 = (CardLayout) cardPanel1.getLayout();
        
        cardPanel2.add(Btn2, "认输");
        cardPanel2.add(Btn2two, "下一步");
        cardLayout2 = (CardLayout) cardPanel2.getLayout();
        
		Btn1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Vars.control.getIfConnect()==true&&Vars.control.getIfStart()==true){
					if(Vars.control.getCanRegret()==false){
						JOptionPane.showMessageDialog(null, "你现在不能悔棋");
						return;
					}
					int option = JOptionPane.showConfirmDialog(null, "你真的要悔棋吗？");
					if(option==JOptionPane.YES_OPTION){
						Vars.net.sendRegret("?");
					}
				}
			}
		});
		Btn1two.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Vars.control.getIfConnect()==true){
					Vars.model.Back();
					int p = Vars.model.getCurr();
					Vars.messagePanel.updateMessage1("复盘:第"+p+"步");
					Vars.paintPanel.repaint();
				}
			}
		});
		
		Btn2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Vars.control.getIfConnect()==true&&Vars.control.getIfStart()==true){
					int option = JOptionPane.showConfirmDialog(null, "你真的要认输吗？");
					if(option==JOptionPane.YES_OPTION){
						int winner = -Vars.control.getLocalColor();
						Vars.net.sendGiveup(true);
						Vars.paintPanel.hehe(winner);
					}
				}
			}
		});
		Btn2two.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Vars.control.getIfConnect()==true){
					Vars.model.Next();
					int p = Vars.model.getCurr();
					Vars.messagePanel.updateMessage1("复盘:第"+p+"步");
					Vars.paintPanel.repaint();
				}
			}
		});
		
		Btn3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Vars.control.getIfConnect()==true){
					int option = JOptionPane.showConfirmDialog(null, "确定要再来一局吗？");
					if(option==JOptionPane.YES_OPTION){
						Vars.net.sendRestart("?");
					}
				}
			}
		});
		Btn4.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int option = JOptionPane.showConfirmDialog(null, "你真的要退出游戏吗？");
				if(option==JOptionPane.YES_OPTION){
					System.exit(0);
				}
			}
		});
		
		add(cardPanel1);
		add(cardPanel2);
		add(Btn3);
		add(Btn4);
	}
	
	public void changeEnd(){
		cardLayout1.show(cardPanel1,"上一步");
		cardLayout2.show(cardPanel2,"下一步");
	}
	public void changeStart(){
		cardLayout1.show(cardPanel1,"悔棋");
		cardLayout2.show(cardPanel2,"认输");
	}
}
