package demo;

import java.util.Scanner;

public class Boss {
	private DiskStore ds = new DiskStore();
	private UserStore us = new UserStore();
	
	Scanner in = new Scanner(System.in);
	
	public static void main(String[] args) {
		Boss boss = new Boss();
		boss.begin();
	}
	private void begin() {
		while(true){
			printMainMenu();
			int choice = in.nextInt();
			switch(choice){
			case 1:
				diskManage();
				break;
			case 2:
				userManage();
				break;
			case 3:
				borrowDisk();
				break;
			case 5:
				return;
			}
		}
	}
	private void borrowDisk(){
		System.out.println("diskID?");
		int diskId = in.nextInt();
		ds.print(diskId);
		System.out.println("disk Num?");
		int diskNum = in.nextInt();
		System.out.println("userID?");
		int userID = in.nextInt();
		us.print(userID);
		Disk d  = new Disk(diskId, "", 0, diskNum);
		ds.remove(d);
		User u = us.find(userID);
		u.getStore().add(d);
		Disk old = ds.find(diskId);
		int money = old.getPrice() * diskNum;
		money = u.getMoney() - money;
		u.setMoney(money);
	}
	private void diskManage() {
		while(true){
			printDiskManageMenu();
			int choice = in.nextInt();
			switch(choice){
			case 1:
				addDisk();
				break;
			case 4:
				return;
			}
		}
	}
	private void addDisk() {
		//id,name,price,num
		System.out.println("id:?");
		int id = in.nextInt();
		System.out.println("name?");
		String name = in.next();
		System.out.println("price?");
		int price = in.nextInt();
		System.out.println("num?");
		int num = in.nextInt();
		Disk d = new Disk(id, name, price, num);
		ds.add(d);
		ds.print();
	}
	private void printDiskManageMenu() {
		System.out.println("1:add Disk");
		System.out.println("2:remove disk");
		System.out.println("3:print disk");
		System.out.println("4:return ");
	}
	private void userManage() {
		// TODO Auto-generated method stub
		
	}
	private void printMainMenu() {
		System.out.println("1:Disk Manage");
		System.out.println("2:User Manage");
		System.out.println("3:Borrow Disk");
		System.out.println("4:Return Disk");
		System.out.println("5:EXIT");
	}
	
}




