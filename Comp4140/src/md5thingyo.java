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
import java.util.concurrent.Semaphore;


//import org.apache.commons.codec.binary.Hex;
// the integer class has pre built in methods for this
// note int decimal = Integer.parseInt(hexNumber, 16); (converts it to decimal)
public class md5thingyo {
	public static final int MESSAGESIZE = 32; // 32 bytes
	private static List <byte[]> plaintextList = new ArrayList<byte[]>();
	public static HashMap<String, byte[]> md5Hash;
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException{
    	long time = System.nanoTime();
    	md5Hash = new HashMap<String, byte[]>();
    	byte[] plaintextTest = generateRandomBytes(MESSAGESIZE);
    	System.out.println("original byte array:   " + byteArrayToHex(plaintextTest));// just printing out plaintextTest was printing its memory address
    	System.out.println("MD5 hashed byte array: " + getMD5(plaintextTest)  + " input length: " + plaintextTest.length*2);// (2 hex chars per byte)
        System.out.println();
        

    	birthdayAttack(plaintextTest);
      //diffAnal();
        
        
        
        time = System.nanoTime() - time;
        System.out.printf("it took %.2f seconds to run everything\n",time*Math.pow(10, -9));
        System.out.println("end of processing...");
    }
    public static void birthdayAttack(byte[] plaintext){
    	 // note anything over 2^20 takes a long time 
    	 int pow = 20;
    	
    	 long time = System.nanoTime();
    	 System.out.println("testing collison with random table of size 2^" + pow);
    	// /* 
    	 int size = (int) Math.pow(2, pow);
    	 table[] t = new table[8];
    	 for(int i = 0; i < t.length;i++){
    		 t[i] = new table(pow,""+ i);
    		 t[i].start();
    	 }
		 while(md5Hash.size() < size){
			 System.out.println(md5Hash.size());
		 } 
		 System.out.println(md5Hash.size());
    	 // wait for the threads to finish
         
       //  */
         //largeRandomTable(20);
    	 
    	 
    	 time = System.nanoTime() - time;
         System.out.printf("it took %.2f seconds to generate the table\n",time*Math.pow(10, -9));
         // the random table is made in halve the time.
    	 // also increasing by double doubles time
         boolean end = false;
         int attempts = 0;
         byte[] attack = plaintext;
         
         int upperBound = 100000;
         
         while(!end){
        	 String hash = getMD5(attack);
        	// System.out.println(hash);
        	 if(md5Hash.get(hash) != null/* && md5Hash.get(hash).equals(attack)*/){
        		 end = true;
        		 System.out.printf("hash collision: %s %s\n%80s %s\n",md5Hash.get(hash),hash,byteArrayToHex(attack),getMD5(attack));
    		 }
        	 else if(attempts >= upperBound){// probably make this a constant some where its just an upper bound 
        		 end = true;
        		 System.out.println("ran "+ attempts + " iterations with no collison");
        	 }
        	 else{
	        	 // moddify byte array attack here to try and get a collision
        		 // this is slightly better code but still not perfect
        		 String temp = byteArrayToHex(attack);
        		// System.out.println("byteArrayToHex finished");
        		 temp = hexPlusPlus(temp);
        		// System.out.println("hexPlusPlus finished");
        		 attack = hexStringToByteArray(temp);	 
        		 //System.out.println("hexStringToByteArray finished");
	        	 attempts ++;
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
