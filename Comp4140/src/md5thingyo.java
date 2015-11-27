import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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


//import org.apache.commons.codec.binary.Hex;
// the integer class has pre built in methods for this
// note int decimal = Integer.parseInt(hexNumber, 16); (converts it to decimal)
public class md5thingyo {
	static List <byte[]> plaintextList = new ArrayList<byte[]>();
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        //System.out.println("ayyy lmao");
       /* 
        
        byte[] plaintext = generateRandomBytes();
        System.out.println("MD5 hashed byte array: " + getMD5(plaintext)  + " input length: " + byteArrayToHex(plaintext).length());
        System.out.println();
        
        ArrayList<byte[]> plaintexts = new ArrayList<>();
        
        for(int i = 0; i < inputs.length;i++){
            plaintexts.add(inputs[i].getBytes());
            System.out.println("Hashing input:         " + "'" + inputs[i] + "'" + ", input length: " + plaintexts.get(i).length);
            System.out.println("MD5 hashed byte array: " + getMD5(plaintexts.get(i)));
            System.out.println("Excepted md5 hash:     " + results[i]);
            System.out.println();
        }

        //buildTable(plaintext);
        
      */
    	byte[] plaintextTest = generateRandomBytes();
    	System.out.println("original byte array:   " + byteArrayToHex(plaintextTest));// just printing out plaintextTest was printing its memory address
    	System.out.println("MD5 hashed byte array: " + getMD5(plaintextTest)  + " input length: " + plaintextTest.length*2);// (2 hex chars per byte)
        System.out.println();
        
        birthdayAttack(plaintextTest);
        
        System.out.println("end of processing...");
    }
    public static void birthdayAttack(byte[] plaintext){
    	 byte[][] data = buildTable(plaintext);
         HashMap<Integer, String> hash = new HashMap<Integer, String>();
         for(int i = 0; i < data.length;i++){
         	
         	if (hash.get(data[i].hashCode()) == null){
         		hash.put(getMD5(data[i]).hashCode(), getMD5(data[i]));
         	}
         	else{
         		System.out.println("hash collision: " + hash.get(data[i].hashCode()) + " with " + getMD5(data[i]));
         	}
         	//System.out.println("new plaintexts: " + byteArrayToHex(data[i]));
         }
    }
    
    
    public static byte[][] buildTable(byte[] data){
        byte[][] finalResult = new byte[(data.length*8)][data.length];
    	byte[] temp = deepCopy(data);
    	int count = 0;
        long value = 1;
        for(int i = 0; i < data.length;i++){
            for(int j = 0; j < 8;j++){
                temp[i] ^= value;
                finalResult[count] = deepCopy(temp);
                count++;
                temp[i] ^= value;
                value = value << 1;
            }
            value = 1;
        }
        return finalResult;
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
    
    public static void diffAnal(byte[] input){
    	//ill split this crap up later but for now just leave it like this cause i hate scrolling around while bug fixing
    	// This is for starting the differential attack on round three
    	byte[] diffAnalAttempt = new byte[input.length];
    	for (int i = 29; i< 33; i++){
    		diffAnalAttempt[i] = input[i];
    	}
    	//This is supposed to create a message difference that can be kept in check throughout the third round?
    	int randomValueforselectingMessageDifference = randInt(32, 45);
    	//method call for creating message difference equal to 2^31... shiiit 
    }
    
    public static byte[] calcDifferenceForTheorem621(byte[] original, byte[] diffattempt){
    	
    	return diffattempt;
    }
        
	/*
	 private static String convertToHex(byte[] data) { 
	        StringBuffer buf = new StringBuffer();
	        for (int i = 0; i < data.length; i++) { 
	            int halfbyte = (data[i] >>> 4) & 0x0F;
	            int two_halfs = 0;
	            do { 
	                if ((0 <= halfbyte) && (halfbyte <= 9)) 
	                    buf.append((char) ('0' + halfbyte));
	                else 
	                    buf.append((char) ('a' + (halfbyte - 10)));
	                halfbyte = data[i] & 0x0F;
	            } while(two_halfs++ < 1);
	        } 
	        return buf.toString();
	    } 
	    */
    public static byte[] generateRandomBytes(){
    	/*
    	long rgenseed = System.nanoTime();
    	Random generator = new Random();
    	generator.setSeed(rgenseed);
    	System.out.println("Random number generator seed is " + rgenseed);
    	*/
    	
        Random generator = new Random(System.nanoTime());
        byte[] newRandomByteArray = new byte[32];
        generator.nextBytes(newRandomByteArray);
        System.out.println("Randomly generated input: " + byteArrayToHex(newRandomByteArray));
        //System.out.println(Hex.encodeHexString(newRandomByteArray));
        return newRandomByteArray;
    }
    public static void generatePlaintext(){
    	//create 4-8 message blocks of 128 bits each
    	int numMessageBlocks = randInt(4,8);
    	for (int i = 0; i< numMessageBlocks;i++){
    		plaintextList.add(generateRandomBytes());
    	}
    }
    public static void printToFile(){
    	//this will work for both of us as it will get the current location of the github project that we are working in.
    	Path currentRelativePath = Paths.get("");
    	String path = currentRelativePath.toAbsolutePath().toString() + "\\src\\outputtest.txt"; // note \\ is escaping a single slash
    	//System.out.println("Current relative path is: " + path);
    	try{
    		for (int i = 0; i< plaintextList.size();i++){
    			Files.write(Paths.get(path), generateRandomBytes(), StandardOpenOption.APPEND);
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

    public static String getMD5(byte[] text){ 
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