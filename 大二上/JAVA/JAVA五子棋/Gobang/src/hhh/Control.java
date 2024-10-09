package hhh;


public class Control {
	private Control(){}
	private static Control instance=null;
	public static Control getinstance(){
		if(instance==null){
			instance=new Control();
		}
		return instance;
	}
	
	private int localColor=Chess.BLACK;
	//决定能否下棋
	private boolean ifConnect = false;//是否连接（防止一开始的时候按钮被误点）
	private boolean ifStart = false;//游戏是否开始（用来控制界面的切换,计时是否开始）
	private boolean canputChess = false;//是否可以下棋（用于控制只有一方能下棋，还有没有决定棋子颜色的时候也不能下棋）
	
	private boolean canRegret = false;//如果还没下棋，就不能悔棋
	
	public void reStart(){
		Vars.model.Restart();
		Vars.messagePanel.ButtonReset();
		Vars.messagePanel.updateMessage1("轮到黑方下棋");
		Vars.eastPanel.changeStart();
		ifStart = true;
		canputChess = false;
		canRegret = false;
		
		Vars.paintPanel.repaint();
	}
	
	public void RegretChess() {
		Vars.model.regret(localColor);
		canputChess=true;
		canRegret = Vars.model.canRegret(localColor);
		//更新提示信息
		if(localColor==Chess.BLACK){
			Vars.messagePanel.updateMessage1("轮到黑方下棋");
		}
		else if(localColor==Chess.WHITE){
			Vars.messagePanel.updateMessage1("轮到白方下棋");
		}
		
		Vars.paintPanel.repaint();
	}
	public void otherRegretChess() {
		Vars.model.regret(-localColor);
		canputChess=false;
		canRegret = Vars.model.canRegret(localColor);
		//更新提示信息
		if(localColor==Chess.BLACK){
			Vars.messagePanel.updateMessage1("轮到白方下棋");
		}
		else if(localColor==Chess.WHITE){
			Vars.messagePanel.updateMessage1("轮到黑方下棋");
		}
		
		Vars.paintPanel.repaint();
	}
	
	public void reportUserPressMouse(int row, int col) {
		if(ifConnect==false||ifStart==false||canputChess==false){
			return;
		}
		boolean success = Vars.model.putChess(row,col,localColor);
		if(success){
			
			//localColor = -localColor;
			canputChess= false;
			canRegret = true;
			if(localColor==Chess.BLACK){
				Vars.messagePanel.updateMessage1("轮到白方下棋");
			}
			else if(localColor==Chess.WHITE){
				Vars.messagePanel.updateMessage1("轮到黑方下棋");
			}
			Vars.paintPanel.repaint();
			Vars.net.sendChess(row, col);
			//whoWin
			int winner=Model.getinstance().whoWin();
			//hehe
			Vars.paintPanel.hehe(winner);
		}
	}

	public void otherChess(int row, int col) {
		boolean success = Vars.model.putChess(row,col,-localColor);
		if(success){
			
			//localColor = -localColor;
			canputChess= true;
			if(localColor==Chess.BLACK){
				Vars.messagePanel.updateMessage1("轮到黑方下棋");
			}
			else if(localColor==Chess.WHITE){
				Vars.messagePanel.updateMessage1("轮到白方下棋");
			}
			Vars.paintPanel.repaint();
			
			//whoWin
			int winner=Model.getinstance().whoWin();
			//hehe
			Vars.paintPanel.hehe(winner);
		}
	}
	
	public void setChessColor(int color){
		localColor = color;
	}
	public void setCanputChess(boolean canputChess) {
		this.canputChess = canputChess;
	}
	public void setIfConnect(boolean ifConnect) {
		this.ifConnect = ifConnect;
	}
	public void setIfStart(boolean p) {
		this.ifStart = p;
	}

	public boolean getIfConnect() {
		return ifConnect;
	}
	public boolean getIfStart() {
		return ifStart;
	}
	public boolean getCanputChess() {
		return canputChess;
	}
	public boolean getCanRegret() {
		return canRegret;
	}
	public int getLocalColor() {
		return localColor;
	}

	
}
