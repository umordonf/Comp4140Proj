import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class md5thingyo {
	public static final int MESSAGESIZE = 32; // 32 bytes
	private static List <byte[]> plaintextList = new ArrayList<byte[]>();
	public static HashMap<String, byte[]> md5Hash;
	public static HashMap<Integer,Integer> experiment;
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException{
    	long time = System.nanoTime();
    	byte[] plaintextTest = generateRandomBytes(MESSAGESIZE);
    	System.out.println("original byte array:   " + byteArrayToHex(plaintextTest));// just printing out plaintextTest was printing its memory address
    	System.out.println("MD5 hashed byte array: " + getMD5(plaintextTest)  + " input length: " + plaintextTest.length*2);// (2 hex chars per byte)
        System.out.println();
        
        int pow = MESSAGESIZE*2;
   	 	
   	 	birthdayAttackExperiment();
   	 	
   	 	byte[] plaintextTest1 = generateRandomBytes(MESSAGESIZE/8);
   	 	// 16 bits
   	 	System.out.println("testing with 1/4 the size md5 message");
   	 	birthdayAttack(plaintextTest1,MESSAGESIZE/2);
   	 	
   	 	//System.out.println("testing with full size md5 message");
        //birthdayAttack(plaintextTest,pow);
        
    	
      //diffAnal();
        
        
        
        time = System.nanoTime() - time;
        System.out.printf("it took %.2f seconds to run everything\n",time*Math.pow(10, -9));
        System.out.println("end of processing...");
    }
    public static void birthdayAttackExperiment(){
    	experiment = new HashMap<Integer,Integer>();
    	System.out.println("was not 100% sure how you would want an experement for a birthday attack but heres the typical same birthday version");
    	System.out.println("with 40 simulated people in this there is a very high chance of collison");
    	// birthdays between day 1 and day 365
    	int max = 365;
    	int min = 1;
    	// root 365 ~= 19;
    	int tableSize = 19;
    	for(int i = 0; i < tableSize;i++){
    		int val = randInt(min,max);
    		experiment.put(val,val);
    		
    	}
    	int position = -1;
    	boolean found = false;
    	for(int i = 0; i < 40 && !found;i++){
    		int pos = randInt(min,max);
    		if(experiment.get(pos) != null && pos == experiment.get(pos)){
    			found = true;
    			position = i;
    		}	
    	}
    	if(position > -1){
    		System.out.println("two people had the same birthday of day: " + position + "\n");
    	}
    	else{
    		System.out.println("no two people with the same birthday, thats odd as the chance of collisin was extreamly high\n");
    	}	
    } 
    
    public static void birthdayAttack(byte[] plaintext, int pow){
    	 // note anything over 2^20 takes a long time 
    	System.out.println("note: runing a birthday attack with 2^" + pow + " elements, this might take hours/days, even with the 8 threads were running");
    	
    	 md5Hash = new HashMap<String, byte[]>();
    	 long time = System.nanoTime();
    	 table[] table = new table[8];
    	 for(int i = 0; i < table.length;i++){
    		 table[i] = new table(plaintext.length,""+ i,pow);
    		 table[i].start();
    	 }
    	// wait for the threads to finish
    	 for (table thread : table) {
			try {
				thread.thread.join();
			} catch (InterruptedException e) {
				System.out.println("thread join error: " + e);
			}
    	 }
    	 
    	 time = System.nanoTime() - time;
         System.out.printf("it took %.2f seconds to generate the table of size %s\n\n",time*Math.pow(10, -9),md5Hash.size());
         boolean end = false;
         byte[] attack = plaintext;
         
         for(int i = 0; i < Math.pow(2, pow) && !end;i++){
        	 attack = generateRandomBytes(plaintext.length);
        	 String hash = getMD5(attack).substring(0,plaintext.length);
        	 if(md5Hash.get(hash) != null){
        		 String attackHex = byteArrayToHex(attack);
	        	 String md5Hex 	  = byteArrayToHex(md5Hash.get(hash));
	        	 if(!md5Hex.equalsIgnoreCase(attackHex)){
	        		 end = true;
	        		 byte[] temp = md5Hash.get(hash);
	        		 System.out.printf("hash collision: %s %s\ncollision with: %s %s\n\n",byteArrayToHex(temp),hash,byteArrayToHex(attack),getMD5(attack).substring(0,plaintext.length));
	    		 }
        	 }
         }
         
    }
    public static String hexPlusPlus(String input){
    	// note doesnt work on single hex chars
    	String result;
    	char[] ch = input.toCharArray();
    	int pos = ch.length-1;
    	boolean end = false;
    	for(int i = pos;pos > 0 && !end;i--){
    		int val = (int)ch[i];
    		if(val == 57){
    			// jump from 9 to a
    			val = 97;
    			end = true;
    		}
    		else if(val == 102){
    			// jump from f to 0
    			val = 48;
    		}
    		else{
    			// otherwise no issue
    			val++;
    			end = true;
    		}
    		ch[i] = (char)val;	
    		if(i == 0){
    			end = true;
    		}
    		
    	}
    	result = new String(ch);
    	return result;
    }
    public static byte[] deepCopy(byte[] input){
        byte[] result = new byte[input.length];
        for(int i = 0; i < result.length;i++){
            result[i] = input[i];
        }
        return result;
    }
    public static int randInt(int min, int max) {
    	Random generator = new Random(System.nanoTime());
        int randomNum = generator.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    public static void diffAnal(){
    	//ill split this crap up later but for now just leave it like this cause i hate scrolling around while bug fixing
    	// This is for starting the differential attack on round three
    	//first we need to create two 1024 bit messages, each in 512 bit blocks.
    	//let us randomly generate our first message and store it in a byte[][]
    	//the 512 bit blocks need to be separated into 16 parts of 32 bits each
    	int[][] messageBlockOne = new int[2][16];
    	int[][] messageBlockTwo = new int[2][16];
    	int max = Integer.MAX_VALUE+1; // need something to represent 2^31
    	ByteBuffer b1 = ByteBuffer.allocate(4);
    	ByteBuffer b2 = ByteBuffer.allocate(4);
    	ByteBuffer b3 = ByteBuffer.allocate(4);
    	ByteBuffer b4 = ByteBuffer.allocate(4);
    	int test = generateMessageBlock();
    	int test2 = test + max;
    	int test3 = test2-test;
    	b1.putInt(max);
    	b2.putInt(test);
    	b3.putInt(test2);
    	b4.putInt(test3);
    	byte[] temp1 = b1.array();
    	byte[] temp2 = b2.array();
    	byte[] temp3 = b3.array();
    	byte[] temp4 = b4.array();
    	System.out.println("2^31: " + toBinary(temp1) + " plus generated block: " + toBinary(temp2) + " equals: " + toBinary(temp3));
    	System.out.println("result - generated = " + toBinary(temp4));
    	//System.out.println(max);
    	int posOther = 32768;
    	int negOther = -32768;
    	//TODO: put message creation elsewhere
    	/*
    	for (int i = 0; i<2;i++){
    		for (int j = 0; j<16;j++){
    			int temp = generateMessageBlock();
    			messageBlockOne[i][j] = temp;
    			messageBlockTwo[i][j] = temp;
    		}
    	}
    	
    	//now that both messages are equal, we must change positions 5, 12 and 15 in both blocks of message two
    	//maybe i need longs? integers are 2^32 now in java 8 so this should be fine...
    	//the message  differences in block one positions 5, 12 and 15 must be 2^31, 2^15 and 2^31
    	//the message  differences in block one positions 5, 12 and 15 must be 2^31, -2^15 and 2^31
    	//all other message differences in the blocks must be zero (so equal)
    	messageBlockTwo[0][4] = messageBlockOne[0][4] + max;
    	messageBlockTwo[1][4] = messageBlockOne[0][4] + max;
    	messageBlockTwo[0][11] = messageBlockOne[0][4] + posOther;
    	messageBlockTwo[0][11] = messageBlockOne[0][4] + negOther;
    	*/
    	
    }
    public static byte[] generateRandomBytes(int size){
    	/*
    	long rgenseed = System.nanoTime();
    	Random generator = new Random();
    	generator.setSeed(rgenseed);
    	System.out.println("Random number generator seed is " + rgenseed);
    	*/
    	// i needed one with variable size but i didnt wanna break your references
        Random generator = new Random(System.nanoTime());
        byte[] newRandomByteArray = new byte[size];
        generator.nextBytes(newRandomByteArray);
        //System.out.println("Randomly generated input: " + byteArrayToHex(newRandomByteArray));
        //System.out.println(Hex.encodeHexString(newRandomByteArray));
        return newRandomByteArray;
    }
    public static int generateMessageBlock(){
    	int result = 0;
    	byte[] temp =  generateRandomBytes(4);
    	result = ByteBuffer.wrap(temp).getInt();
    	return result;
    }
    public static void generatePlaintext(){
    	//create 4-8 message blocks of 128 bits each
    	//might be useless now 
    	int numMessageBlocks = randInt(4,8);
    	for (int i = 0; i< numMessageBlocks;i++){
    		plaintextList.add(generateRandomBytes(MESSAGESIZE));
    	}
    }
    public static void printToFile(){
    	//this will work for both of us as it will get the current location of the github project that we are working in.
    	Path currentRelativePath = Paths.get("");
    	String path = currentRelativePath.toAbsolutePath().toString() + "\\src\\outputtest.txt"; // note \\ is escaping a single slash
    	//System.out.println("Current relative path is: " + path);
    	try{
    		for (int i = 0; i< plaintextList.size();i++){
    			Files.write(Paths.get(path), generateRandomBytes(MESSAGESIZE), StandardOpenOption.APPEND);
    		}
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	    	try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
    	    out.println("the text");
    	    //more code
    	    out.println("more text");
    	    //more code
    	}catch (IOException ex) {
    	    //exception handling left as an exercise for the reader
    	}}// no idea why but compiler insists on another closing bracket (or we get compile errors)
    }
    public static void plainTextLog(byte[] input){
    	String timeStamp = new SimpleDateFormat("MM,dd HH:mm").format(Calendar.getInstance().getTime());
    	Path currentRelativePath = Paths.get("");
    	String path = currentRelativePath.toAbsolutePath().toString() + "\\src\\outputtest.txt"; // note \\ is escaping a single slash
    	String str = toBinary(input);
    	String output = ("MessageBlock: " + byteArrayToHex(input) + " date: " + timeStamp);
    	try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
    	    out.println(output);
    	}catch (IOException e) {
    	    //exception handling left as an exercise for the reader
    	}
    }
    public static String toBinary( byte[] bytes ){
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }
    public static byte[] fromBinary( String s ){
        int sLen = s.length();
        byte[] toReturn = new byte[(sLen + Byte.SIZE - 1) / Byte.SIZE];
        char c;
        for( int i = 0; i < sLen; i++ )
            if( (c = s.charAt(i)) == '1' )
                toReturn[i / Byte.SIZE] = (byte) (toReturn[i / Byte.SIZE] | (0x80 >>> (i % Byte.SIZE)));
            else if ( c != '0' )
                throw new IllegalArgumentException();
        return toReturn;
    }
	public static void comparisonLog(String input){
    	String timeStamp = new SimpleDateFormat("MM,dd HH:mm").format(Calendar.getInstance().getTime());
    	Path currentRelativePath = Paths.get("");
    	String path = currentRelativePath.toAbsolutePath().toString() + "\\src\\comparisons.txt"; // note \\ is escaping a single slash
    	try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
    	    out.println(input);
    	}catch (IOException e) {
    	    //exception handling left as an exercise for the reader
    	}
    }
    public synchronized static String getMD5(byte[] text){ 
    	try{
            MessageDigest md;
            md = MessageDigest.getInstance("MD5");
            //byte[] bytesOfMessage = text.getBytes("UTF-8");
            //byte[] md5hash = md.digest(bytesOfMessage);//"iso-8859-1" if i want ascii i guess
            byte[] md5hash = md.digest(text);
            String hexString = byteArrayToHex(md5hash); 
            //String hexString = Hex.encodeHexString(md5hash); 
            return hexString;
    	}
    	catch(NoSuchAlgorithmException e){
            throw new RuntimeException(e);
    	}	
    }
    public static String byteArrayToHex(byte[] input){
        String result = "";
        for(int i = 0; i < input.length;i++){
            String temp = Integer.toBinaryString((int)input[i]);
            while(temp.length() < 8){
                temp = 0 + temp;
            }
            String fhalve = temp.substring(temp.length()-8,temp.length()-4);
            String shalve = temp.substring(temp.length()-4);
            result += Integer.toHexString(Integer.parseInt(fhalve,2)) + Integer.toHexString(Integer.parseInt(shalve,2));
        }
        return result;
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static String compare(String baseline, String two){
        String result = "'";
        String bitsThatDidntChange = "";
        
        for(int i = 0; i < baseline.length();i++){
            if(baseline.charAt(i) == two.charAt(i)){
                result += two.charAt(i);
                if(bitsThatDidntChange.equalsIgnoreCase("")){
                    bitsThatDidntChange = "" + i;
                }
                else{
                    bitsThatDidntChange += ", " + i;
                }
            }
            else{
                result += "*";
            }
        }
        result += "' Unchanged: " + bitsThatDidntChange;
        comparisonLog(result);
        return result;
    }
}
