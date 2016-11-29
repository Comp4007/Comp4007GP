package MyApp.kiosk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class RFID {
	private ArrayList<String> id = new ArrayList<String>();
	private int floor = 0;
	
	public RFID(){}
	
	public int getFloorById(String id){
		try{
     	   File database = new File("etc/RFID_DB");
     	   BufferedReader br = new BufferedReader(new FileReader(database));

     	   String line = null;
     	   // Read from the database file
     	   // unless content matches data to get the floor 
     	   while ((line = br.readLine()) != null) {
     		   if (line.contains(id)) {
     			   floor = Integer.parseInt(line.split(",")[1]);
     			   break;
     		   }
     	   }
     	   br.close();
           }catch (Exception ex){
        		  System.out.println("File not found");
           }     
		return floor;
	}
	
	public ArrayList<String> getAllTheId(){
		try{
	     	   File database = new File("etc/RFID_DB");
	     	   BufferedReader br = new BufferedReader(new FileReader(database));

	     	   String line = null; int count = 0;
	     	   // Read from the database file
	     	   // Get all the RFID
	     	   while ((line = br.readLine()) != null) {
	     		   if(count != 0){
	     			  id.add(line.split(",")[0]);
	     		   }
	     		   count++;
	     	   }
	     	   br.close();
	           }catch (Exception ex){
	        		  System.out.println("File not found");
	           }     
		
		return id;
	}
}