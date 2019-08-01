package client;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import java.time.LocalDateTime;

public class TCPClient {
	static boolean running = true;
    private static final DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_GREEN = "\u001B[32m";
	
	
	 public static void main(String argv[]) throws Exception {

		 boolean ent = true;
		 int eingabe = 2;
		 String Benutzername = null;
		 String raumname = null;
		 String[] raumliste;
			java.util.Date now = new java.util.Date(System.currentTimeMillis());

			 String eingabeForm = JOptionPane.showInputDialog("W�hle die Eingabeform: (f)enster oder (c)onsole!");
			 
			 try {
				 if (eingabeForm.equalsIgnoreCase("F") || eingabeForm.equalsIgnoreCase("FENSTER")) {
				 
				 	ent = false;
				 	eingabe = 1;
			 	}else if (eingabeForm.equalsIgnoreCase("C") || eingabeForm.equalsIgnoreCase("CONSOLE")){
				 	ent = false;
				 	eingabe = 2;
			 	}
			 }
			 catch(NullPointerException e) {
				JOptionPane.showConfirmDialog(null,"Du musst das Programm neu starten, um weiter zu kommen!");
				System.exit(0);
			 }
	 
			  BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			  DataOutputStream outToServer = null;
			  Socket clientSocket = null;

		      InputStream inp = null;
		      BufferedReader brinp = null;
			 //Fehlerbehebung von zeile 50 bis 83

			  
			 try {
				  clientSocket = new Socket("172.24.0.42", 1988); //Moritz : .42 FLorian : .16
				  outToServer = new DataOutputStream(clientSocket.getOutputStream());
				  inp = clientSocket.getInputStream();
		          brinp = new BufferedReader(new InputStreamReader(inp));
			 } 
			  catch (SocketException e) {
	
				  if (eingabe == 1) {
					  	JOptionPane.showMessageDialog(null,"Warnung" + '\n'+ "Kein Aufbau zum Server");
					  	TimeUnit.SECONDS.sleep(30);
					  	System.exit(1);
				  }
				  else {
					  System.out.println("Kein Aufbau zum Server");
					  TimeUnit.SECONDS.sleep(30);
					  System.exit(1);
				  }	
			  }
		if(eingabe == 2) {
			
			String userName;
			userName = inFromUser.readLine();
			Benutzername = userName;
			
			  if (Benutzername.equals("") || Benutzername.equals("	")|| Benutzername.equals(" ")) {
				 // System.out.println(userName + "2User - Ben" + Benutzername);
				  userName = System.getProperty("user.name");
				  Benutzername = userName;
			  }
			  
			System.out.print("<"+sdf.format(now)+">"+" Dein Benutername ist "+ Benutzername +"!"+'\n');
			raumname = brinp.readLine();
			System.out.print("In welchen der folgenden R�ume m�chtest du beitreten?");
		      raumliste =  raumname.split(";");
		      for (String name:raumliste) {
		    	  System.out.print(name);
		      }
		      raumname = inFromUser.readLine();

		      outToServer.writeBytes(raumname + '\n');
		} 
		else if (eingabe == 1){
			Benutzername = JOptionPane.showInputDialog("Gib deinen Benutzernamen ein! (Windows-Benutzername wird als Default verwendet.)");
				if ((Benutzername.equals(""))||(Benutzername.equals("	"))|| (Benutzername.equals(" "))) {
					String userName = System.getProperty("user.name");	
					Benutzername = userName;
				}
			 	JOptionPane.showMessageDialog(null, sdf.format(now) +"Dein Benutername ist "+ Benutzername +"!"+'\n'); //
			 	
			 	outToServer.writeBytes(Benutzername + '\n');
				
				JOptionPane.showMessageDialog(null, "In welchen der folgenden R�ume m�chtest du beitreten?");
			      
			      
			      
			      raumname = brinp.readLine();
			      raumliste = raumname.split(";");
			      
			      for (String name:raumliste) {
			    	  System.out.print(name);
			      }
			      raumname = JOptionPane.showInputDialog("Raum");
			      outToServer.writeBytes(raumname);
		}

		    
		
	  
			new ThreadSend(clientSocket, Benutzername, raumname).start();
			new ThreadReceive(clientSocket).start();


		 }
	}	  