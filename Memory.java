import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;


public class Memory {

	static Integer[] memory = new Integer[2000];
	
	static final Integer USER_MEMORY_LIMIT = 999;
	static final Integer SYSTEM_MEMORY_LIMIT = 1999;
	static final Integer USER_STACK_START = 899;
	static final Integer SYSTEM_STACK_START = 1899;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		populateMemory(args[0]);
		execute();
	}

	private static void execute() {
		
	Scanner sc = new Scanner(System.in);

      while (sc.hasNextLine()) {
          String value = sc.nextLine();
          int address;

          String[] array = value.split(" ");
          
          if(array.length == 1){
        	  address = Integer.valueOf(value);
              System.out.println(read(address));
          }else{
        	  write(Integer.valueOf(array[0]),Integer.valueOf(array[1]));
          }
      }
		
	}

	public static Integer read(Integer address){
		
		return memory [address];
	}
	
	public static Integer write(Integer address, Integer value){
		
		return memory[address] =  value;
	}
	
	
	public static void populateMemory(String args) {
		try{
			FileInputStream fstream = new FileInputStream(args);
	        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	        String strLine;
	        int i=0;
	        while ((strLine = br.readLine()) != null)   {
	          if(strLine.startsWith(".")){
	        	String test = strLine;
		        i = Integer.valueOf(test.replace('.', ' ').trim());
	          }
	          String[] stringArray = strLine.split(" ");
	          
	          for (String segment : stringArray) {
	        	  try{
	        		  Integer value;
	        		  value = Integer.valueOf(segment.trim());
	        		  memory[i] =value;
	        		  i++;
	        		  break;
	        	  }catch(NumberFormatException e){
	        	  }
	          }
	          
	        }
	        }catch (Exception e){
	          System.err.println("Error: " + e.getMessage());
	        }
	}

}
