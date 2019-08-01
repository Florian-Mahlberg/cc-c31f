package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ThreadSend extends Thread {
	protected Socket socket;
	
	String username;
	String to;
	int type;
	
	public ThreadSend(Socket socket, String username, String to) {
		this.socket = socket;
		this.username = username;
		this.to = to;
	}
	public void run(){
		
		String sentence;
		StringBuilder text = new StringBuilder();
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		
		DataOutputStream outToServer;
		try {
			outToServer = new DataOutputStream(socket.getOutputStream());
			while (true) {
				try {
					System.out.print("Nachricht / Datei: ");
					Message message = null;
					sentence = inFromUser.readLine();
					if(sentence != null) {
						File file = new File(sentence);
						if(file.exists()) {
							type = 2;
						}else {
							type = 1;
						}
						if(type == 1) {
							System.out.println(sentence);
							message = new Message(username, to, sentence, type);
							System.out.println("Deine Nachricht lautet: " + message.getOutputString());
							}
						} else /*if(type == 2)*/{
							String mes = Message.encodeFile(text.toString());
							message = new Message(username, to, mes, type);
							//System.out.println("Working 2.");
						}
						outToServer.writeBytes(message.getOutputString() + '\n');
						outToServer.flush();
					
					
					
				}catch(Exception e){
					System.out.println("Error: "+ e);
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Error: "+ e1);
		}
				
		
	}
	
		  	
}
