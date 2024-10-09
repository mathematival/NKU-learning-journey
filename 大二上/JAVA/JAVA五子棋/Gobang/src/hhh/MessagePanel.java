package hhh;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MessagePanel extends JPanel{
	private JButton blackButton = new JButton("Black");
	private JButton whiteButton = new JButton("White");
	private static JLabel message1 = new JLabel("",JLabel.CENTER);
	
	
	private static final int TIME_LIMIT_SECONDS = 30;//单位：秒
	
	private Timer timer;
	private static JLabel timerLabel = new JLabel();
	private int remainingTimeInSeconds;
	private boolean isTurn = false;
	
	private boolean buttonClicked = false;
	
	private JPanel panel = new JPanel(new FlowLayout());
	
	static{
		message1.setFont(new Font("宋体", Font.PLAIN, 30));
		timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
	}
	
	public MessagePanel() {
		setLayout(new GridLayout(3, 1));
		setPreferredSize(new Dimension(1200,120));
		
		remainingTimeInSeconds = TIME_LIMIT_SECONDS;
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	            updateTimer();
	        }
	    }, 0, 1000);
		
		ButtonGroup buttonGroup = new ButtonGroup();
	    buttonGroup.add(blackButton);
	    buttonGroup.add(whiteButton);
	    blackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(buttonClicked==false){
            		ButtonSelected(blackButton);
            		Reset();
            	}
            }
        });

        whiteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(buttonClicked==false){
            		ButtonSelected(whiteButton);
            		Reset();
            	}
            }
        });    
        
        panel.add(blackButton);
        panel.add(whiteButton);
        
        add(panel);
		add(message1);
		add(timerLabel);
	}
	
	public void setIsTurn(boolean c){
		isTurn = c;
	}
	public void Reset(){
		remainingTimeInSeconds = TIME_LIMIT_SECONDS;
		updateTimerLabel();
	}
	
	public void ButtonReset(){
		blackButton.setEnabled(true);
		whiteButton.setEnabled(true);
		buttonClicked = false;
	}
	public void ButtonSelected(JButton p) {
		isTurn=true;
		
		p.setEnabled(false);
		buttonClicked = true;
		if(p.getText()=="Black"){
			Vars.control.setChessColor(Chess.BLACK);
			Vars.control.setCanputChess(true);
			Vars.net.sendClick("Black");
		 }
		else{
		    Vars.control.setChessColor(Chess.WHITE);
			Vars.control.setCanputChess(false);
			Vars.net.sendClick("White");
		}
	}
	public void otherButtonSelected(String s){
		isTurn=true;
		
		buttonClicked = true;
		if(s.startsWith("Black")){//对方选了黑旗，自己就设置为白旗
		    whiteButton.setEnabled(false);
			 
			Vars.control.setChessColor(Chess.WHITE);
			Vars.control.setCanputChess(false);
		}
		else{
			blackButton.setEnabled(false);
			 
			Vars.control.setChessColor(Chess.BLACK);
			Vars.control.setCanputChess(true);
		}

	}
	private void updateTimer() {
		if(isTurn==true){
			remainingTimeInSeconds--;
			updateTimerLabel();
	        if (remainingTimeInSeconds <= 0) {
	        	if(message1.getText()=="轮到黑方下棋"){
	        		Vars.paintPanel.hehe(Chess.WHITE);
	        	}
	        	else if(message1.getText()=="轮到白方下棋"){
	        		Vars.paintPanel.hehe(Chess.BLACK);
	        	}
	        }
		}
    }
	private void updateTimerLabel() {
	    int minutes = remainingTimeInSeconds / 60;
	    int seconds = remainingTimeInSeconds % 60;
	    timerLabel.setText("Timer: " +String.format("%02d:%02d", minutes, seconds));
	}
	public void updateMessage1(String s){
		message1.setText(s);
		Reset();;
	}
	
}
