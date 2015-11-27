public class HashTable {
	private class Node{
		public String data;
		public Node(){
			data = "";
		}
	}
	private int size;
	private Node[] list;
	public HashTable(int size){
		this.size = size;
		// 32 hex values  ~= 2 ^ 128 values 
		list = new Node[size];
		for(int i = 0; i < size;i++){
			list[i] = new Node();
		}
	}
	private int hash(String data){
		return Integer.parseInt(data.substring(0,4),16);
	}
	public String find(String find){
		String result = "";
		int pos = hash(find);
		if(pos > 0){
			result = "" + list[pos];
		}
		return result;
	}
	public String add(String data){
		String result = "";
		int pos = hash(data);
		if (list[pos].data.equals("")){
			result = "collision at: " + pos;
		}
		else{
			list[pos].data = data;
		}
		return result;
	}
	
}