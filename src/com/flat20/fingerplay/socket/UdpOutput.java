package com.flat20.fingerplay.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

import com.flat20.fingerplay.FingerPlayServer;
import com.flat20.fingerplay.Midi;
import com.flat20.fingerplay.MidiCode;
import com.flat20.fingerplay.MidiReceiver;
import com.flat20.fingerplay.MidiReceiver.IMidiListener;

public class UdpOutput implements IMidiListener
{
	protected int port;
	protected boolean multicast = false;
	
	private DatagramSocket clientSocket;
	private DatagramPacket tickPacket;
	private InetAddress ipAddress;
	
	private static UdpOutput instance = null;
	
	private static MidiReceiver mMidiReceiver = null;
	
	public static UdpOutput getInstance(){
	    if(instance==null){
	    	instance = new UdpOutput();
	    }
	    return instance;
	  }

	private UdpOutput() {
		if (mMidiReceiver==null){
			mMidiReceiver = new MidiReceiver(this);
		}
		
	}

	public String getName() 
	{
		return (multicast ? "Multicast" : "Unicast") + " :" + String.valueOf(port);
	}

	public MidiReceiver getMidiReceiver(){
		return mMidiReceiver;
	}
	public void init(InetAddress ipAddress, int port) {
			close();
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
		if (clientSocket!=null){
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
		this.port = 0;
		this.ipAddress = null;
		Midi.getInstance().close();
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
	
	public void sendToAll(byte[] message) {
		DatagramPacket packet;
		
		for (ServerSocketThread sock : FingerPlayServer.getSocksClients()){
			InetAddress ipAdd = sock.getClientConn().getInetAddress();
		
		if(message.length == 1 && message[0] == (byte)MidiCode.MIDI_REALTIME_CLOCK_TICK)
		{
			packet = new DatagramPacket(new byte[]{(byte)0xF8}, 1, ipAdd, port);
		}
		else
		{
			packet = new DatagramPacket(message, message.length, ipAdd, port);
		}
		
			try {
			if (!isClosed())
				clientSocket.send(packet);
			
		} catch (IOException e) {
			System.out.println( "Cannot send packet" + e.getMessage());
		
		}
			
		}
		
	}
	

	public void onNoteOn(int channel, int key, int velocity) {
		// TODO Auto-generated method stub
		//System.out.println("onNoteOn");
	}

	public void onNoteOff(int channel, int key, int velocity) {
		// TODO Auto-generated method stub
		//System.out.println("onNoteOff");
	}

	public void onControlChange(int channel, int key, int velocity) {
		// TODO Auto-generated method stub
		System.out.println("onControlChange");
	}

	public void onMidiSyncReceived(byte[] message) {
		  try {
	        	// System.out.println("sending MSG : " + (message[0] &0xFF));	
	        	 //send(message);
			  	sendToAll(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
		
	}
	
	public void assignMidiUDPReceiver(String device ,InetAddress clientIp){
		
			if (clientIp.toString().equals(this.ipAddress.toString())){

			MidiDevice midiDeviceIN = Midi.getInstance().open(device, false); // true = bForOutput
			
			if (midiDeviceIN != null) {
				//System.out.println("midiDeviceIN = " + midiDeviceIN);
				Transmitter t =null;
				try {
					t = midiDeviceIN.getTransmitter();
				} catch (MidiUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (t != null && clientIp.toString().equals(this.ipAddress.toString()))
					t.setReceiver(mMidiReceiver);
			}
			}
			
	
	}

}
