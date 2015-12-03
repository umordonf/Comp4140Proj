import java.util.concurrent.Semaphore;

public class table implements Runnable {
	public Thread t;
	private int size;
	private String name;
	Semaphore lock = new Semaphore(1, true);
	public table(int pow,String name){
		size = (int) Math.pow(2, pow);
		this.name = name; 
	}
	public void run(){
		try {
			lock.acquire(1);
			while(md5thingyo.md5Hash.size() < size){
				byte[] t = md5thingyo.generateRandomBytes(md5thingyo.MESSAGESIZE);
				String hash = md5thingyo.getMD5(t);	
				if(md5thingyo.md5Hash.get(hash) == null){
					if(md5thingyo.md5Hash.size() < size){
						md5thingyo.md5Hash.putIfAbsent(hash, t);
					}
				}
			}
		} catch (Exception e) {
			// Logging
		}
		finally{
		   lock.release(1);
		}
	} 
	public void start (){
	     if (t == null){
	        t = new Thread (this, name);
	        t.setDaemon(true);
	        // Daemon threads die when system.exit is called, otherwise had threads continuing when application was done
	        t.start ();
	     }
     }
}