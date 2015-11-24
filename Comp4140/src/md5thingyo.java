import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

//import org.apache.commons.codec.binary.Hex;
// the integer class has pre built in methods for this
// note int decimal = Integer.parseInt(hexNumber, 16); (converts it to decimal)
public class md5thingyo {
	
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        //System.out.println("ayyy lmao");
       /* 
        String[] inputs = {"The quick brown fox jumps over the lazy dog",
                           "The quick brown fox jumps over the lazy dog.",
                           ""};
        String[] results = {"9e107d9d372bb6826bd81d3542a419d6",
                            "e4d909c290d0fb1ca068ffaddf22cbd0",
                            "d41d8cd98f00b204e9800998ecf8427e"};
        
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
    	System.out.println("original byte array: " + plaintextTest);
    	System.out.println("MD5 hashed byte array: " + getMD5(plaintextTest)  + " input length: " + byteArrayToHex(plaintextTest).length());
        System.out.println();
    }
    public static void buildTable(byte[] data) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        byte[] temp = deepCopy(data);
        String baseline = getMD5(temp);
        long value = 1;
        for(int i = 0; i < data.length;i++){
            for(int j = 0; j < 8;j++){
                temp[i] ^= value;
                //System.out.println("unchanged: " + getMD5(data));
                String result = getMD5(temp);
                System.out.printf("changed bit: %d of byte: %2d %s comparison:(* means change) %s\n",j+1,i+1,result,compare(baseline, result));
                
                temp[i] ^= value;
                value = value << 1;
            }
            value = 1;
            System.out.println();
        }
        
    }
    public static byte[] deepCopy(byte[] input){
        byte[] result = new byte[input.length];
        for(int i = 0; i < result.length;i++){
            result[i] = input[i];
        }
        return result;
    }
    public static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
    	Random generator = new Random(System.nanoTime());

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
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
        String output = "";
        Random generator = new Random(System.nanoTime());
        byte[] newRandomByteArray = new byte[64];
        generator.nextBytes(newRandomByteArray);
        System.out.println("Randomly generated input: " + byteArrayToHex(newRandomByteArray));
        //System.out.println(Hex.encodeHexString(newRandomByteArray));
        return newRandomByteArray;
    }

    public static String getMD5(byte[] text) throws UnsupportedEncodingException{ 
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
        return result;
    }
}
