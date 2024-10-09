package one;

public class Control {
	private Control(){}
	private static Control instance ;
	public static Control getInstance() {
		if (instance == null) {
			instance = new Control();
		}
		return instance;
	}
	private int localColor = Model.BLACK;
	
	
	public void putChess(int row,int col){
		boolean success = Model.getInstance().putChess(row, col, localColor);
		if(success){
			// view update
			View.getInstance().update();
			// localColor reverse
			localColor = -localColor;
			// whoWin
			int winner = Model.getInstance().whoWin();
			// hehe
			View.getInstance().hehe(winner);
		}
	}
	
	
	
	
	
	
	
	
}
