import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.BitSet;

public class UDPClient {

	public static void main(String args[]) throws Exception {

		System.out.println("Client is running...");

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		DatagramSocket clientSocket = new DatagramSocket();

		InetAddress IPAddress = InetAddress.getByName("188.166.44.37");

		byte[] sendData = new byte[100];
		byte[] receiveData = new byte[100];
		
		//System.out.println("Please enter a string:");
		
		File file = new File("deneme.png");
		double bytesOfFile = file.length();
		
		String sentence = inFromUser.readLine();

		sendData = bytesOfFile.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 6767);
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		String modifiedSentence = new String(receivePacket.getData());
		System.out.println("FROM SERVER:" + modifiedSentence);
		
			
		byte[] b = new byte[100];
		BitSet bitSet = BitSet.valueOf(b);
		bitSet.clear(28, 160); 
		b = bitSet.toByteArray();


	}

}