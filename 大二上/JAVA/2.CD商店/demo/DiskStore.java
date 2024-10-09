package demo;

public class DiskStore {
	private Disk[] data = new Disk[100];
	public void print(){
		for(Disk d : data){
			if( d !=null){
				System.out.println(d);
			}
		}
	}
	public void print(int id){
		System.out.println(data[id]);
	}
	public void remove(Disk d){
		// d(id,,,num)
		// zugou×ã¹»,
		//²»¹»
		
	}
	public void add(Disk d){
		int id = d.getId();
		if(data[id] == null){
			data[id] = d;
		}else{
			int num = data[id].getNum();
			num = num+d.getNum();
			data[id].setNum(num);
		}
	}
	public Disk find(int diskId) {
		return data[diskId];
	}
}
