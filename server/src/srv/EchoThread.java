package srv;
import javax.swing.*;

import java.awt.List;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class EchoThread extends Thread {
    protected Socket socket;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter dft = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static Integer ServerPrivateKey = ThreadedEchoServer.privateKey;
    public static Integer ServerPublicKey2 = ThreadedEchoServer.keys.get(2);

    public EchoThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        InputStream inp = null;
        BufferedReader brinp = null;
        DataOutputStream out = null;
        int zahl = 1;
        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        
        
        try {
        	DataOutputStream outToClient = null;
            outToClient = new DataOutputStream(socket.getOutputStream());
			outToClient.writeBytes(ThreadedEchoServer.keys.get(1).toString() + '\n');
	        outToClient.writeBytes(ThreadedEchoServer.keys.get(2).toString() + '\n');
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        String line;
        String room;
        Boolean login = true;
        String Benutzername;
        
        
        while (true) {
            try {
            	System.out.println("1");
                line = brinp.readLine();
                System.out.println("2");
                line = decode(line, ServerPrivateKey, ServerPublicKey2);
                System.out.println(line);
                String tempPublicKey1 = brinp.readLine();
                String tempPublicKey2 = brinp.readLine();               
                Integer publicKey1 = Integer.parseInt(tempPublicKey1);
                Integer publicKey2 = Integer.parseInt(tempPublicKey2);
                SocketKeys user = new SocketKeys(socket, publicKey1, publicKey2);
                ThreadedEchoServer.userList.add(user);
                out.writeInt(ThreadedEchoServer.keys.get(1));
                out.writeInt(ThreadedEchoServer.keys.get(2));
                
                LocalDateTime now = LocalDateTime.now();
                System.out.println("out"+login);
                if (login) {
                	Benutzername = line;
                	login = false;
                	System.out.println("in"+login);
                	ThreadedEchoServer.addUser(Benutzername);
                	String roomlist =ThreadedEchoServer.roomsToString();
                	out.writeBytes(roomlist+'\n');
                	room = brinp.readLine();
                	
                	int ind = ThreadedEchoServer.findRoom(room);
                	
                	if(ind < 0) {
                		ThreadedEchoServer.addRoom(room);
                		ind = ThreadedEchoServer.getRooms().size()-1;
                	}
            		ThreadedEchoServer.getRooms().get(ind).addSocket(socket);
            		ThreadedEchoServer.getRooms().get(ind).addUser(Benutzername);

                	String text = "Der Benutzer " + Benutzername + " hat Raum "+room+" betreten.";
                	System.out.println("<"+dft.format(now)+"> "+text);
					ThreadedEchoServer.sendToAll(text, socket);
					System.out.println(ThreadedEchoServer.roomsToString());
                }
                else {
                	System.out.print("<"+dtf.format(now)+"> ");
                    System.out.println(line);
                    ThreadedEchoServer.sendToAll(line, socket);
                }
            } catch (IOException e) {
            	e.printStackTrace();
            	/*
            	LocalDateTime now = LocalDateTime.now();
            	int ind = ThreadedEchoServer.getSockets().indexOf(socket);
            	try {
            		System.out.println("<"+dtf.format(now)+"> "+ThreadedEchoServer.getUser().get(ind)+" just left");
					ThreadedEchoServer.removeUser(ind);
					ThreadedEchoServer.sendToAll("<"+dft.format(now)+"> "+ThreadedEchoServer.getUser().get(ind)+" just left", socket);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            	
            	ThreadedEchoServer.removeSocket(socket);
                return; */
            }
        }
    }
    //----------------------------------------------------------------------------------------------------------------------------
    public static String decode(String text, Integer privateKey, Integer pub2) {
    	text = zahlfinder(text, BigInteger.valueOf(privateKey), BigInteger.valueOf(pub2));
		return text;
    	
    }
    public static String devigenere (String text) { //vigenere blo� in die andere Richtung bei Verst�ndnisproblemen, siehe vigenere Kommentare
		String codewort = "meincodewort";
		String endwort = "";
		int j = 0;
		for (int i = 0; i<text.length(); i++) {
			if (j==codewort.length()) {
				j -= codewort.length();
			}
			int intBuchstabe =  (int) text.charAt(i);
			int intCodeBuchstabe = (int) codewort.charAt(j);
			int newBuchstabe = 0;
			int addB = intCodeBuchstabe - 96;
			newBuchstabe = intBuchstabe - addB;
			if (newBuchstabe < 32) {
				newBuchstabe += 93; 
			}
			System.out.println(newBuchstabe);
			char ausgabe = (char) newBuchstabe;
			j +=1;
			endwort = endwort + ausgabe;
		}
		return endwort;
	}
	public static String primdecodierung(String zuDecodierendeZahlString, BigInteger privatKey, BigInteger publicKey, String decodierterText) {
		
		int zuDecodierendeZahl = Integer.parseInt(zuDecodierendeZahlString);
		BigInteger BigZuDecodierendeZahl = BigInteger.valueOf(zuDecodierendeZahl);
		
		BigInteger decodierteBigZahl = pot (BigZuDecodierendeZahl, BigZuDecodierendeZahl, privatKey, BigInteger.ONE);
		decodierteBigZahl = decodierteBigZahl.mod(publicKey);
		int decodierteZahl = decodierteBigZahl.intValue();
		char decodiert = (char) decodierteZahl;
		decodierterText = decodierterText + decodiert; 
		return decodierterText;
	}
	public static BigInteger pot(BigInteger zahl, BigInteger ursprungsZahl, BigInteger potenz, BigInteger momentan) {
		if (potenz.equals(momentan)) {//potenzberechnung beider mit einer biginteger potenz gerechnet wird
			return zahl;
		}
		else {
			zahl = zahl.multiply(ursprungsZahl);
			zahl = pot (zahl, ursprungsZahl, potenz, momentan.add(BigInteger.ONE) );
			
		}
		return zahl;
		
	}
	public static String zahlfinder(String text, BigInteger privatKey, BigInteger publicKey) {
		String decodierterText = "";
		int zahlenAnzahl = zahlenZaelen(text); //wieviele Zahlen wurden eingegeben?
		String[] parts = text.split(" "); //text unterteilen in die einzelnen buchstaben indem nach den freizeichen dazwischen gesucht und gespalten wird
		for (int i = 0; i < zahlenAnzahl ; i++) { //wird auf jeden buchstaben angewendet
			decodierterText = primdecodierung(parts[i], privatKey, publicKey, decodierterText); //anwendung der decodierung
		}
		String ausgabe = devigenere(decodierterText); //vigenere decodierung am Ende
		return ausgabe;
		
	}
	public static int zahlenZaelen(String text) { //z�hlt die zahlen aufgrund dessen wie viele freizeichen existieren
		int anzahl = 1;
		for(int i = 0; i<text.length();i++) {
			if (text.charAt(i) == ' ') {
				anzahl += 1;
			}
		}
		return anzahl;
		
	}
	//-----------------------------------------------------------------------------------------------------------------
	public static String vigenere (String text) {
		String codewort = "meincodewort"; //mit diesem Wort wird vigenere verschl�sselt
		String endwort = "";
		int j = 0;
		for (int i = 0; i<text.length(); i++) {
			if (j==codewort.length()) { //z�hlen ob das codewort bereits aufgebraucht wurde, falls ja, von vorne codieren
				j -= codewort.length();
			}
			int intBuchstabe =  (int) text.charAt(i); //umwandlung in ascii code vom buchstaben des textes mit dem index des aktuellen i
			int intCodeBuchstabe = (int) codewort.charAt(j);//umwandlung in ascii code vom buchstaben des codewortes mit dem index des aktuellen j
			int newBuchstabe = 0;
			int addB = intCodeBuchstabe - 96; //berechnung welche zahl addiert werden muss
			newBuchstabe = intBuchstabe + addB; //neuer Buchstabe wird berechnet
			if (newBuchstabe > 126) { //falls das ende des Vorgegebenen Ascii alphabets erreicht wurde, f�ngt es von vorne an
				newBuchstabe -= 93; 
			}
			char ausgabe = (char) newBuchstabe; //umwandlung zur�ck in buchstabe
			j +=1;
			endwort = endwort + ausgabe; //buchstaben werden zusammengef�hrt
		}
		return endwort;
	}
	public static String rsa(String text, Integer publicKey1, Integer publicKey2){
		String codeText = "";
		for (int i =0; i < text.length(); i++ ) {
			char zuCodierenderBuchstabe = text.charAt(i); //buchstabe an der stelle i einlesen
			Integer zuCodierendeZahl = (int)zuCodierenderBuchstabe; //eingelesenen buchstabe in ascii zahl umwandeln
			BigInteger codierteZahl = (pot(zuCodierendeZahl, publicKey1)); //ab hier Berechnung 
			BigInteger bigPublicKey2 = BigInteger.valueOf(publicKey2.intValue());
			codierteZahl = codierteZahl.mod(bigPublicKey2);
			codeText = codeText + String.valueOf(codierteZahl)+ " "; //Formatierung
		}
		
		codeText = codeText.substring(0, codeText.length()-1); //freizeichen am Ende l�schen
		return codeText;
	}
	
	public static BigInteger pot(Integer zahl, Integer potenz) {
		BigInteger ergebnis = BigInteger.ONE;
		BigInteger bigZahl = BigInteger.valueOf(zahl.intValue());
		for (int i=1; i < (potenz+1); i++) {
			ergebnis = ergebnis.multiply(bigZahl); //einfache potenzberechnung 			
		}
		return ergebnis;
	}
}
