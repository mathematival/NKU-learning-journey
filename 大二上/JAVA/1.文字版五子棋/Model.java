package one;

public class Model {
	private Model() {
	}

	private static Model instance;

	public static Model getInstance() {
		if (instance == null) {
			instance = new Model();
		}
		return instance;
	}

	public static final int BLACK = 1;
	public static final int WHITE = -1;
	public static final int SPACE = 0;
	public static final int WIDTH = 19;

	private int[][] data = new int[WIDTH][WIDTH];

	private int lastRow,lastCol;
	
	public boolean putChess(int row, int col, int color) {
		if (row >= 0 && row < WIDTH 
				&& col >= 0 && col < WIDTH
				&& data[row][col] == SPACE) {
			data[row][col] = color;
			lastRow = row;
			lastCol = col;
			return true;
		}
		return false;
	}
	public int getChess(int row,int col){
		if(row >= 0 && row < WIDTH 
				&& col >= 0 && col < WIDTH){
			return data[row][col];
		}
		return SPACE;
	}
	
	public int whoWin(){
		
		return SPACE;
	}
}
