/**
  * Name    :  Julianna Gabler            <BR>
  * Course  :  NSSA-290-02 2015:SEM 2145  <BR>
  * Homework:  Projects Part A            <BR>
  * Date    :  05/07/15 May 07, 2015      <BR>
  *
  * Class   :  UDPServer                  <BR>
  * Purpose :  the class is a UDP server that sends and
  *            receives messages from the client program
  *            in which the server determines which of the four
  *            commands that were entered and executes the correct
  *            actions related to the desired command. Commands
  *            are: captilization of a string, retrieve a file's
  *            contents, retrieve the date, and exit. 
  *                                       <BR>
  * @author julianna gabler
  * @version 2.0
  * @see UDPClient
  */
  
import java.io.*;
import java.net.*;
import java.util.*;  

public class UDPServer{

////////////////////////////////////////////////////////
//////////// ATTRIBUTES ////////////////////////////////
DatagramSocket serverSocket = null;                   //the socket that is created for the server
DatagramPacket rcvdPacket = null;                     //the packet revieved by the server
DatagramPacket sendPacket = null;                     //the packet to be sent to the client
byte[] sendData = null;                               //data to send to server
byte[] received = new byte[4096];                     //data that was received 
//////////// CONSTANTS /////////////////////////////////
final int PORT = 2048;                                //server always uses port 2048
////////////////////////////////////////////////////////

//////////// CONSTRUCTOR ///////////////////////////////  
   /**
     * When the class is called, the constructor starts the process
     */
   public UDPServer(){
      //set the socket with the correct port number
      try{
         serverSocket = new DatagramSocket(PORT);
         //if successful, print msgs
         println("UDPServer is online and ready for clients to connect");
         println("----------------------------------------------------");
         
         while(true){
            //send and recieve msgs
            sendReceive();
         } 
         
         //System exits
      }catch(IOException ioe){
         //catch and print error
         println("UDPSERVER ERROR::" + ioe.getMessage());
         System.exit(1);
      }catch(Exception e){
         //catch and print error
         println("UDPSERVER ERROR::" + e.getMessage());
         System.exit(1);
      }
   }
   
//////////////////////////////////////////////////////////


/////////// METHODS //////////////////////////////////////
 /**
   * handles sending and recieveing the msgs between the client and server
   * @throws IOException from sub functions
   */
   private void sendReceive() throws IOException{
      //receive the packet and put it in recieved byte array   
      rcvdPacket = new DatagramPacket(received, received.length);
      serverSocket.receive(rcvdPacket);
      
      //convert the packet into a string, offset + length makes sure only actual length not 4096 is rcvd
      String rcvdStr = new String(rcvdPacket.getData(), rcvdPacket.getOffset(), rcvdPacket.getLength());
      
      //combine ipaddress, client port, and rcvdStr in one variable and print to server
      String print = "(" + rcvdPacket.getAddress() + ":" + rcvdPacket.getPort() + ") " + rcvdStr;
      println(print);
      
      //find command that user wants to execute
      int cmd = getCommand(rcvdStr);
      
      //execute cmd
      executeCmd(cmd, rcvdStr);
      
      //send the sendData back to the client
      sendPacket = new DatagramPacket(sendData, sendData.length, rcvdPacket.getAddress(), rcvdPacket.getPort());
      serverSocket.send(sendPacket);
      
      
   }//end sendReceive method
   
  /**
    * determines the command from the rcvdStr
    * @param rcvdStr the String received from the client
    * @return cmd that corresponds with each cmd
    */
    private int getCommand(String rcvdStr){
      //save the first four letters of the string
      String command = rcvdStr.substring(0,4);
      int cmd = 0;
      
      if(command.equals("CAP:")){ cmd = 1; }
      else if(command.equals("DATE")){ cmd = 2; }
      else if(command.equals("FIL:")){ cmd = 3; }
      else if(command.equals("EXIT")){ cmd = 4; }
      
      return cmd;
      
    }//end getCommand method
 
  /**
    * receives the cmd and directs the program to the right set of instructions
    * @param cmd the cmd that the client asked for
    * @param rcvd the String from the client
    * @throws IOException from retrieveFile()
    */
  private void executeCmd(int cmd, String rcvd) throws IOException{
      //make sure sendData is empty before filling and initialized
      sendData = new byte[4096];
      //put cmd into switch to determine the correct action
      switch(cmd){
         case 1: 
            captialize(rcvd);
            break;
         case 2: 
             getDate();
             break;
         case 3:
           retrieveFile(rcvd);
           break;
         case 4:
            println("Client has terminated connection");
            break;
         default:
            println("ERROR: Unknown command");               
      }
  }//end execute method
  
////////// ExecuteCMD sub methods ///////////////////////////////////  
  /**
    * captializes function and puts it into sendData
    * @param msg the message to be captialized
    */
  private void captialize(String msg){
       //captialize
       String caps = msg.toUpperCase();
       //empty in the data array
       sendData = caps.getBytes();
  }//end captialize method
  
  /**
    * gets the current date and puts it in the sendData array
    */
  private void getDate(){
      //get date and convert to string
      Date date = new Date();
      String dStr = date.toString();
      
      //put in array
      sendData = dStr.getBytes();
      
  }//end date method
  
  /**
    * gets the contents of a file
    * @param rcvdStr String from the client
    * @throws IOException with bufferedReader / readLine()
    */
  private void retrieveFile(String rcvdStr) throws IOException{
      String line = null, fileLines = "";
      
      /* get the file name from the str (4= everything after the cmd)
       * load it into a file object for opening and reading
       * open the fileReader in buffered reader to read it
       */
      File file = new File(rcvdStr.substring(4));  
      
      if(file.isFile()){  //check to see if file exists  
         BufferedReader br = new BufferedReader(new FileReader(file));
         
         //read until the file returns null
         while( ( line = br.readLine() ) != null){
            fileLines += line + "\n";
         }
      
         //load into the sendData arry
         sendData = fileLines.getBytes();
         
      }else{
         String nf ="File not found";
         println(nf);
         
         sendData = nf.getBytes();
      }
      
  }//end retrieveFile method
  
/////////////////////////////////////////////////////////         
  
  /**
    * a function that shortens code for printing a msg
    * "slang" for System.out.println("")
    * @param msg String to be printed
    */
  private void println(String msg){
      System.out.println(msg);
  }//end println method


////////// MAIN METHOD ///////////////////////////////////
  
   /**
     * calls the constructor of the UDPServer class
     * @param args String[]
     */
   public static void main(String[] args){
      UDPServer udps = new UDPServer();
   }//end main method class
   
/////////////////////////////////////////////////////////
   
}//END UDPServer class