package com.flat20.fingerplay;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.swing.ImageIcon;

import com.flat20.fingerplay.socket.ClientSocketThread;
import com.flat20.fingerplay.socket.MulticastServer;
import com.flat20.fingerplay.socket.commands.SocketCommand;

public class FingerPlayServer implements Runnable{

	public static final String VERSION = "0.9.6";
	public static final int SERVERPORT = 4444;

	public static final String MULTICAST_SERVERIP = "230.0.0.1";
	public static final int MULTICAST_SERVERPORT = 9013;

	private Midi midi;
	
	private String mLocalIP = null;
	private static int mPort = SERVERPORT;

	private SystemTray tray = null;
	
	private  String multicastOutputMessage ="";
	
	public void run() {
		
		Image image = Toolkit.getDefaultToolkit().createImage(this.getClass().getClassLoader().getResource("com/flat20/fingerplay/connect_wait.png"));
		 Image imageR = Toolkit.getDefaultToolkit().createImage(this.getClass().getClassLoader().getResource("com/flat20/fingerplay/connect_on.png"));
		 
	
		 
         PopupMenu popup = new PopupMenu();
       
         final TrayIcon trayIcon = new TrayIcon(image, "MidiIo Server Running", popup);
         trayIcon.setImageAutoSize(true);

         Enumeration e;
		try {
			e = NetworkInterface.getNetworkInterfaces();
		
			while(e.hasMoreElements())
			{
			    NetworkInterface n = (NetworkInterface) e.nextElement();
			    Enumeration ee = n.getInetAddresses();
			    while (ee.hasMoreElements())
			    {
			        InetAddress i = (InetAddress) ee.nextElement();
			        
			        if (i.isSiteLocalAddress()){
			        	mLocalIP = i.getHostAddress();
			        	//System.out.println(i.getHostAddress());
			        }
			    }
			}
			
			if (mLocalIP == null) {
				InetAddress localAddress = InetAddress.getLocalHost();
				mLocalIP = localAddress.getHostAddress();
			}
		
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
			// Start multicast server

			 multicastOutputMessage = mLocalIP + ":" + mPort;
			 
     	if (SystemTray.isSupported()) {
	          tray = SystemTray.getSystemTray();

	           MenuItem item = new MenuItem("Info");
	           
	 
		  item.addActionListener(new ShowMessageListener(trayIcon,
	            "7Pad MidiIo server", "Im Listening on " + multicastOutputMessage, TrayIcon.MessageType.INFO));
	          popup.add(item);
	        
	          item = new MenuItem("Close");
		  item.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
		      tray.remove(trayIcon);
		      
		      System.exit(0);
	            }
		  });
	          popup.add(item);
	          try {
	            tray.add(trayIcon);
	          } catch (AWTException ex) {
	            System.err.println("Can't add to tray");
	          }
	        } else {
	          System.err.println("Tray unavailable");
	        }
     	
     	
		
		try {

			
			SocketCommand s = new SocketCommand();

 			// Open MIDI Device.
			midi = new Midi();
			

			Thread multicastServerThread = null;
			
				try {
					multicastServerThread = new Thread( new MulticastServer(MULTICAST_SERVERIP, MULTICAST_SERVERPORT, multicastOutputMessage) );
				
					multicastServerThread.start();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			//	trayIcon.displayMessage("Server info", "", TrayIcon.MessageType.INFO);
			
			trayIcon.displayMessage("7Pad MidiIo", "v" + VERSION + " \n Listening on : " + multicastOutputMessage, TrayIcon.MessageType.INFO);
			// Wait for client connection
			
			ServerSocket serverSocket = new ServerSocket(mPort);
			System.out.println("Waiting for connection...");		
			System.out.println("Im Listening on " + multicastOutputMessage);
			 while(!Thread.currentThread().isInterrupted()) {

				Socket client = serverSocket.accept();
				ClientSocketThread st = new ClientSocketThread(client, midi,trayIcon,image);
				Thread thread = new Thread( st );
				thread.start();
				//System.out.println("Accepted new client connection.");
				trayIcon.setImage(imageR);
				 trayIcon.displayMessage("Incoming client", "New client connexion accepted.", TrayIcon.MessageType.INFO);
			}
			 // interupted
			 if (multicastServerThread!=null){
				 multicastServerThread.interrupt();
			 }		
		} 
		
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalThreadStateException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
		
	}


	static class ShowMessageListener implements ActionListener {
	    TrayIcon trayIcon;
	    String title;
	    String message;
	    TrayIcon.MessageType messageType;
	    ShowMessageListener(
	        TrayIcon trayIcon,
	        String title,
	        String message,
	        TrayIcon.MessageType messageType) {
	      this.trayIcon = trayIcon;
	      this.title = title;
	      this.message = message;
	      this.messageType = messageType;
	    }
	    public void actionPerformed(ActionEvent e) {
	      trayIcon.displayMessage(title, message, messageType);
	    }
	  }
	

	public static void main (String[] args) {
		
		if (args.length > 0) {
			try {
				int port = Integer.parseInt(args[0]);
				mPort = port;
			} catch (NumberFormatException e) {
				System.out.println("Couldn't set server port to " + args[0]);
			}
		}
		
		final Thread desktopServerThread = new Thread(new FingerPlayServer());
		
		
		
		
		
		desktopServerThread.start();
	}
} 
