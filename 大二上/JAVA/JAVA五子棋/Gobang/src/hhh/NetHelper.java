package hhh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

/*
 * 传一个字符串，冒号前面代表选择
 * chess:3,3
 * chat:xxxxx
 * regret:?
 * regret:Yes    JOptionPanel showConfirmdialog
 * regret:NO     弹出一个对话框，请求被拒绝
 * 
 * restart:?
 * restart:Yes
 * restart:No
 * 
 * giveup:true
 * connect:true
 * 
 * click:Black
 * click:White
 */

public class NetHelper {
	public static final int PROT = 8000;
	private Socket s;
	private BufferedReader in;
	private PrintStream out;
	
	//使用多线程，防止程序卡死在接受信号这一步上
	public void startListen(){
		new Thread(){
			public void run() {
				listen();
			}
		}.start();
	}
	private void listen(){
		try {
			ServerSocket ss =new ServerSocket(PROT);
			Socket s = ss.accept();
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintStream(s.getOutputStream(),true);
			startReadThread();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void connect(String ip,int port){
		try {
			Socket s =new Socket(ip,port);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintStream(s.getOutputStream(),true);
			
			out.println("connect:true");
			Vars.control.setIfConnect(true);
			Vars.control.setIfStart(true);
			Vars.westPanel.addText("连接成功");
			Vars.messagePanel.updateMessage1("轮到黑方下棋");
			
			startReadThread();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void startReadThread() {
		new Thread(){
			public void run() {
				while(true){
					try {
						String line = in.readLine();
						if(line.startsWith("connect:true")){
							Vars.control.setIfConnect(true);
							Vars.control.setIfStart(true);
							Vars.westPanel.addText("连接成功");
							Vars.messagePanel.updateMessage1("轮到黑方下棋");
						}
						else if(line.startsWith("chess:")){
							otherChess(line.substring("chess:".length()));
						}
						else if(line.startsWith("chat:")){
							Vars.westPanel.addText(line.substring("chat:".length()));
						}
						else if(line.startsWith("regret:")){
							RegretChess(line.substring("regret:".length()));
						}
						else if(line.startsWith("restart:")){
							Restart(line.substring("restart:".length()));
						}
						else if(line.startsWith("giveup:true")){
							int winner = Vars.control.getLocalColor();
							Vars.paintPanel.hehe(winner);
						}
						else if(line.startsWith("click:")){
							Vars.messagePanel.otherButtonSelected(line.substring("click:".length()));
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	private void Restart(String s) {
		if(s.startsWith("?")){
			int option = JOptionPane.showConfirmDialog(null, "对方想要重新开始，是否同意？");
			if(option==JOptionPane.YES_OPTION){
				sendRestart("Yes");
				Vars.control.reStart();
				Vars.westPanel.addText("对局重新开始");
			}
			else{
				sendRestart("No");
				Vars.westPanel.addText("拒绝对局重新开始");
			}
		}
		else if(s.startsWith("Yes")){
			Vars.control.reStart();
			Vars.westPanel.addText("对局重新开始");
		}
		else if(s.startsWith("No")){
			JOptionPane.showMessageDialog(null, "对方拒绝了你的请求");
			Vars.westPanel.addText("对方拒绝对局重新开始");
		}
	}
	private void RegretChess(String s){
		if(s.startsWith("?")){
			int option = JOptionPane.showConfirmDialog(null, "对方想要悔棋，是否同意？");
			if(option==JOptionPane.YES_OPTION){
				sendRegret("Yes");
				Vars.control.otherRegretChess();
				Vars.westPanel.addText("同意对方悔棋");
			}
			else{
				sendRegret("No");
				Vars.westPanel.addText("拒绝对方悔棋");
			}
		}
		else if(s.startsWith("Yes")){
			Vars.control.RegretChess();
			Vars.westPanel.addText("对方同意了你的悔棋");
		}
		else if(s.startsWith("No")){
			JOptionPane.showMessageDialog(null, "对方拒绝了你的请求");
			Vars.westPanel.addText("对方拒绝了你的悔棋");
		}
	}
	private void otherChess(String line) {
		String[]param = line.split(",");
		int row = Integer.parseInt(param[0]);
		int col = Integer.parseInt(param[1]);
		Vars.control.otherChess(row,col);
		
	}
	
	public void sendChess(int row,int col){
		if(out!=null){
			out.println("chess:"+row+","+col);
		}
	}
	public void sendChat(String line){
		if(out!=null){
			out.println("chat:"+line);
		}
	}
	public void sendGiveup(boolean p){
		if(out!=null){
			if(p==true){
				out.println("giveup:true");
			}
			
		}
	}
	public void sendRegret(String s) {
		if(out!=null){
			out.println("regret:"+s);
		}
	}
	public void sendClick(String s) {
		if(out!=null){
			out.println("click:"+s);
		}
	}
	public void sendRestart(String s) {
		if(out!=null){
			out.println("restart:"+s);
		}
	}
	
}
