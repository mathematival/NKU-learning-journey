package one;

import java.util.Scanner;

public class View {
	private View(){}
	private static View instance ;
	public static View getInstance() {
		if (instance == null) {
			instance = new View();
		}
		return instance;
	}
	public void input(){
		Scanner in = new Scanner(System.in);
		while(true){
			System.out.println("row?");
			int row = in.nextInt();
			System.out.println("col?");
			int col = in.nextInt();
			Control.getInstance().putChess(row, col);
		}
	}
	public void hehe(int winner){
		if(winner == Model.SPACE){
			System.out.println("hehe");
		}else if(winner == Model.BLACK){
			System.out.println("Black hehe");
		}else if(winner == Model.WHITE){
			System.out.println("White hehe");
		}
	}
	public void update(){
		System.out.println();
		for (int row = 0; row < Model.WIDTH; row++) {
			for (int col = 0; col < Model.WIDTH; col++) {
				int color = Model.getInstance().getChess(row, col);
				if(color == Model.SPACE){
					System.out.print("Ê®");
				}else if(color == Model.BLACK){
					System.out.print("¡ð");
				}else{
					System.out.print("¡ñ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	
	
	
	
	
	
	
	
}
