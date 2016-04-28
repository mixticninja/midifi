package com.flat20.fingerplay.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.flat20.fingerplay.MidiCode;

public class UdpOutput
{
	protected int port;
	protected boolean multicast = false;
	
	private DatagramSocket clientSocket;
	private DatagramPacket tickPacket;
	private InetAddress ipAddress;
	
	private static UdpOutput instance = null;
	
	public static UdpOutput getInstance(){
	    if(instance==null){
	    	instance = new UdpOutput();
	    }
	    return instance;
	  }

	private UdpOutput() {
		
	}

	public String getName() 
	{
		return (multicast ? "Multicast" : "Unicast") + " :" + String.valueOf(port);
	}

	public void init(InetAddress ipAddress, int port) {
		if (!isClosed()) { // only one client at a time
			close();
		}
		createSock( ipAddress,  port);
	}
	
	private void createSock(InetAddress ipAddress, int port){
		this.port = port;
		this.ipAddress = ipAddress;		
		try
		{
			if(multicast)
			{
				clientSocket = new MulticastSocket();
			}
			else
			{
				clientSocket = new DatagramSocket();
			}
			tickPacket = new DatagramPacket(new byte[]{(byte)0xF8}, 1, ipAddress, port);
		}
		catch(IOException e){
			System.out.println( "Cannot open Network" + e.getMessage());
		}
	}

	public boolean isClosed(){
		boolean closed = true;
		if (clientSocket!=null && !clientSocket.isClosed()){
			closed = false;
		}
		return closed;
	}

	public void close() 
	{
		if(clientSocket != null)
		{
			clientSocket.disconnect();
			clientSocket.close();
			clientSocket = null;
		}
	}


	public void send(byte[] message) 
	{
		DatagramPacket packet;
		if(message.length == 1 && message[0] == (byte)MidiCode.MIDI_REALTIME_CLOCK_TICK)
		{
			packet = tickPacket;
		}
		else
		{
			packet = new DatagramPacket(message, message.length, ipAddress, port);
		}
		
			try {
			if (!isClosed())
				clientSocket.send(packet);
			
		} catch (IOException e) {
			System.out.println( "Cannot send packet" + e.getMessage());
		
		}
		
	}

}
