package MyApp.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RFID {
	private ArrayList<String> id = new ArrayList<String>();
	//Used to check if valid floor is inputed
	
	public RFID(){
	}
	
	public String getFloorById(String id){
		String floor = "na";
		try{
     	   File database = new File("etc/RFID_DB");
     	   BufferedReader br = new BufferedReader(new FileReader(database));
     	   String line = null;
     	   // Read from the database file
     	   // unless content matches data to get the floor 
     	   while ((line = br.readLine()) != null) {
     		   if (line.contains(id)) {
     			   if(line.split(",")[0].contentEquals(id))
     				   floor = line.split(",")[1];
     			   break;
     		   }
     	   }
     	   br.close();
           }catch (Exception ex){
        		  System.out.println("File not found");
           }     
		System.out.println(floor);
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
	
	public void insertData(String data){
		//Append the data in the RFID_DB
        try(FileWriter fw = new FileWriter("etc/RFID_DB", true);
        	    BufferedWriter bw = new BufferedWriter(fw);
        	    PrintWriter out = new PrintWriter(bw))
        	{ 
        	    out.println(data);
        	    bw.close();
        	    out.close();
        	    System.out.println("Insert data sucessfully");
        	} catch (IOException ex) {
        	    System.out.println("Data cannot insert to the databse.");
        	}
	}
	
	public void updateData(String id, String data){
		try{
     	   File originalFile = new File("etc/RFID_DB");
     	   BufferedReader br = new BufferedReader(new FileReader(originalFile));
     	   // Construct the new file that will later be renamed to the original
     	   // filename.
     	   File tempFile = new File("etc/myTempFile");
     	   PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

     	   String line = null;
     	   // Read from the original file and write to the new
     	   // unless content matches data to be removed.
     	   while ((line = br.readLine()) != null) {
     		   if (line.contains(id)) {
     			   line = data;
     		   }
     		   pw.println(line);
     		   pw.flush();
     	   }
     	  System.out.println("Update data sucessfully");
     	   pw.close();
     	   br.close();
     	   // Delete the original file
     	   if (!originalFile.delete()) {
     		   System.out.println("Could not delete file");
     		   return;
     	   }
     	   // Rename the new file to the filename the original file had.
     	   if (!tempFile.renameTo(originalFile))
     		   System.out.println("Could not rename file");
        		}catch (Exception ex){
        		   System.out.println("Update error");
        		} 
	}
	
	public void deleteData(String data){
		 //Delete data in the RFID_DB 
        File inputFile = new File("etc/RFID_DB");
        File tempFile = new File("etc/myTempFile");

        try{
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));	
        String lineToRemove = data;
        String currentLine;

        while((currentLine = reader.readLine()) != null) {
            // trim newline when comparing with lineToRemove
            String trimmedLine = currentLine.trim();
            if(trimmedLine.equals(lineToRemove)) {
            }else{
            	writer.write(currentLine + System.getProperty("line.separator"));
            }
        }
        writer.close(); 
        reader.close(); 
        boolean successful = tempFile.renameTo(inputFile);
        System.out.println("Delete data " + successful);
        }catch (Exception ex){
        	System.out.println("Delete Error");
        }
	}
}