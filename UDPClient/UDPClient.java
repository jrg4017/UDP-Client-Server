/**
  * Name    :  Julianna Gabler            <BR>
  * Course  :  NSSA-290-02 2015:SEM 2145  <BR>
  * Homework:  Projects Part A            <BR>
  * Date    :  05/07/15 May 07, 2015      <BR>
  *
  * Class   :  UDPClient                  <BR>
  * Purpose :  the class is a UDP client that initates a
  *            menu that recieves user input based on a command
  *            each command initiates a different action. Commands:
  *            captialization, retreive a file's contents, get the
  *            date and time from the server and exit the program
  *                                       <BR>
  * @author julianna gabler
  * @version 1.2
  * @see UDPServer
  */

import java.io.*;
import java.net.*;
import java.util.*;

public class UDPClient{

////////////////////////////////////////////////////////
//////////// ATTRIBUTES ////////////////////////////////
DatagramSocket clientSocket = null;                   //the socket used for the client
InetAddress IPAddress = null;                         //the IPaddress that is passed in through the command line
DatagramPacket rcvdPacket = null;                     //the recieved Packet from the server
DatagramPacket sendPacket = null;                     //the packet to send to the server
BufferedReader input = new BufferedReader(new InputStreamReader(System.in));  //used to read input by the user
boolean rtn = true;                                   //controls the loop in the constructor; exits on false
byte[] rcvdData = new byte[4096];                     //loads the rcvdData into the the array when data is rcvd from server
byte[] sendData = null;                               //stores the data to send to the server
//////////// CONSTANTS /////////////////////////////////
final int SERVER_PORT = 2048;                         //the server port IS ALWAYS 2048
////////////////////////////////////////////////////////

//////////// CONSTRUCTOR ///////////////////////////////
   /**
     * sets the client socket, gets the ipaddress, execute user commands
     * lets the server know when a client exits, and exits the program 
     * @param arg String from the main class, IPaddress
     */
   public UDPClient(String arg){
      try{
         //let the program assign an open socket to the 
         clientSocket = new DatagramSocket();
         //set the ipaddress of the server
         IPAddress = InetAddress.getByName(arg);
         do{         
            //executeCmd based on choice
            rtn = executeCmd();
         }while(rtn == true);
         
         //let server know that client terminated
          String exit = "EXIT";
          sendData = exit.getBytes();
          sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, SERVER_PORT);
          clientSocket.send(sendPacket);
          
          //system exits
          System.exit(0);
      //catch and print the exception    
      }catch(IOException ioe){
         println("UDPCLIENT ERROR::" + ioe.getMessage());
         System.exit(1);
      }catch(Exception e){
         println("UDPCLIENT ERROR::" + e.getMessage());
         System.exit(1);
      }
   }//end UDPClient constructor
   
////////////////////////////////////////////////////////

//////////// METHODS ///////////////////////////////////
   /**
     * prints out the menu for the commands
     * @return choice int that corresponds with the menu choice
     * @throws IOException from input.readLine
     */
   private int menu() throws IOException{
      //print menu options
      println(" ");
      println("1) Captialize text");
      println("2) Date and time");
      println("3) Retrieve a file");
      println("4) Exit UDPClient");
      //do not want \n space
      print("Choice > ");
      
      //convert the line to int and use as a choice
      int choice = Integer.parseInt(input.readLine());
      
      //just a space between choice and output
      print("\n");
      return choice;
   }//end menu method
   
   /**
     * execuete the correct functions for each int cmd entered
     * by the user after the menu function is called
     * @return rtn boolean, if false the loop exits
     * @throws IOException from executeCmd's sub methods
     */
   private boolean executeCmd() throws IOException{
      //make sure byte array is empty and initalized
      sendData = new byte[4096];
      
      //print out menu and get choice returned
       int choice = menu();
       
       //execute each method based on the cmd chosen
       switch(choice){
         case 1:
            captialize();
            break;
         case 2:
            dateTime();
            break;
         case 3:
            retrieveFile();
            break;
         case 4:
            return false;
         default:
            println("ERROR: NOT A COMMAND. TRY AGAIN");
            return true;            
       }
       //send the data to server
       send();
       //recieve the data from the server
       recieve();
       
       //return true to keep while loop going
       return true;

   }//end execute cmd method

//////////// EXECUTE CMD SUB-METHODS ////////////////////

      /**
        * a function that gets the string to be captilized by the server
        * and enters it into the byte array
        * @throws IOException from input.readLine()
        */
      private void captialize() throws IOException{
         String entireMsg = "CAP:"; //default header for the desired command
         String line = null;        //for getting the string entered
         
         //print instruction menu
         println("Enter your text and terminate with a line containing only a period.");
         
         while(true){
            print(">> ");
            //get the input
            line = input.readLine();
           
            //terminates when a single . is entered
            if(line.equals(".")){ break; }
              
            //add to the entiremsg with a newline character
            else{ entireMsg += line + "\n"; }
         }
         
         //dump into sendData array
         sendData = entireMsg.getBytes();   
      }//end captialize method
      
      /**
        * adds the header to sendData for a request for the DATE and time
        */
      private void dateTime(){
         String date = "DATE";
         //dump into sendData array
         sendData = date.getBytes();
      }//end dateTime method
      
      /**
        * retrieves the file from user and adds it to output
        * @throws IOException from the input.readLine()
        */
      private void retrieveFile() throws IOException{
         String file = "FIL:";
         //ask for file name and retrieve it
         println("Please enter the path to the file you wish to open.");
         print(">> ");
         file += input.readLine();
         
         //load into the sendData array
         sendData = file.getBytes();
         
      }//end retrieveFile method      
      
      /**
        * sends the data to the server with the sendData array
        * @throws IOException from the DatagramPacket
        */
      private void send() throws IOException{
         sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, SERVER_PORT);
         clientSocket.send(sendPacket);
      }//end send method
      
      /**
        * recieves data from the server, formats it, and prints it in a string
        * @throws IOException from the datagram packets
        */
      private void recieve() throws IOException{
         //receive the data
         rcvdPacket = new DatagramPacket(rcvdData, rcvdData.length);
         clientSocket.receive(rcvdPacket);    
         
         //create as a new string, offset & getLength keeps program from printing 4096 characters        
         String rcvdStr = new String(rcvdPacket.getData(), rcvdPacket.getOffset(), rcvdPacket.getLength());  
         
         String header = "\nFROM: (" + rcvdPacket.getAddress() + ":" + rcvdPacket.getPort() + ")";
         //print everything
         println(header);
         println(rcvdStr);  
         
      }//end recieve method 

/////////////////////////////////////////////////////////         
  
  /**
    * a function that shortens code for printing a msg
    * "slang" for System.out.println("")
    * @param msg String to be printed
    */
  private void println(String msg){
      System.out.println(msg);
  }//end println method
  
  /**
    * a function that shortens code for printing a msg
    * "slang" for System.out.print("")
    * @param msg String to be printed
    */
  private void print(String msg){
      System.out.print(msg);
  }//end print msg
/////////// MAIN METHOD ////////////////////////////////
   /**
     * calls the constructor
     * @param args String arry
     */
     public static void main(String[] args){
         //make sure cmd line has one args (an ip address)
         //else print out error and quit
         if(args.length != 1){
            System.out.println("NAME_OR_IP_ADDRESS_OF_SERVER");
            System.exit(1);
         }else{
            UDPClient udpc = new UDPClient(args[0]);
         }   
     }//end UDPClient
////////////////////////////////////////////////////////

}//end UDPClient class