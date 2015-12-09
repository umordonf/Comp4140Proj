import java.util.concurrent.Semaphore;

public class table implements Runnable {
	public Thread thread;
	private int size;
	private String name;
	private int messageSize;
	private int pow;
	Semaphore lock = new Semaphore(1, true);
	public table(int pow,String name,int messageSize){
		size = (int) Math.pow(2,messageSize);
		this.name = name; 
		this.messageSize = messageSize;
		this.pow = pow;
	}
	public void run(){
		while(md5thingyo.md5Hash.size() < size){
			byte[] t = md5thingyo.generateRandomBytes(pow);
			String hash = md5thingyo.getMD5(t).substring(0,pow);	
			try {
				lock.acquire(1);
				if(md5thingyo.md5Hash.get(hash) == null){
					if(md5thingyo.md5Hash.size() < size){
						md5thingyo.md5Hash.put(hash, t);
					}
				}
			} catch (Exception e) {
				// Logging
			}
			finally{
			   lock.release(1);
			}
		}
	} 
	public void start (){
	     if (thread == null){
	    	 thread = new Thread (this, name);
	    	 thread.setDaemon(true);
	        // Daemon threads die when system.exit is called, otherwise had threads continuing when application was done
	    	 thread.start ();
	     }
     }
}