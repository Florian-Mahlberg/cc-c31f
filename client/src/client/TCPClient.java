package client.src.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class TCPClient {
	static boolean running = true;
    private static final DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	 public static void main(String argv[]) throws Exception {
		  BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		  Socket clientSocket = new Socket("localhost", 50000);
		  DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		  
	      LocalDateTime now = LocalDateTime.now();
		  
		  String Benutzername;  
		  System.out.println("Benutzername eingeben! Wenn du keinen eingibst, wird dein Windows-"+'\n'+"benutzername gebraucht!");
		  Benutzername = inFromUser.readLine();
	  if (!(Benutzername == "") || !(Benutzername == "	")|| !(Benutzername == " ")) {
		  System.out.println("Dein Benutername ist "+Benutzername+"!");  
	  }
	  else {
		  Benutzername = System.getProperty("user.name");
		  System.out.println("Dein Benutername ist "+System.getProperty("user.name")+"!");
	  }
	  String to;  
	  System.out.print("Empf�nger eingeben!");
	  to = inFromUser.readLine();
	  if (!(Benutzername == "") || !(Benutzername == "	")|| !(Benutzername == " ")) {
		  System.out.println("Dein Empf�nger ist "+ to +"!");  
	  }
	  else {
		  System.out.println("Ung�ltiger Empf�nger!");
	  }
	  //outToServer.writeBytes(Benutzername + '\n');
	  
		  
		  new ThreadSend(clientSocket, Benutzername, to).start();
		  //new ThreadReceive(clientSocket).start();

		  
	 }
}