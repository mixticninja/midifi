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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;

import com.flat20.fingerplay.socket.ServerSocketThread;
import com.flat20.fingerplay.socket.UdpOutput;

public class FingerPlayServer implements Runnable {

	public static final String VERSION = "1.0.2";
	public static final int SERVERPORT = 4444;

	public static final int UDP_SERVERPORT = 9013;

	private String mLocalIP = null;
	private static int mPort = SERVERPORT;

	private ServerSocket serverSocket = null;

	/** Maximum number of connections */
	private static final int max_connections = 3;
	private SystemTray tray = null;

	private String multicastOutputMessage = "";

	private static final List<ServerSocketThread> socksClients = new ArrayList<ServerSocketThread>();

	public static List<ServerSocketThread> getSocksClients() {
		return socksClients;
	}

	public void run() {

		final Image image = Toolkit.getDefaultToolkit().createImage(
				this.getClass().getClassLoader()
						.getResource("com/flat20/fingerplay/connect_wait.png"));
		final Image imageR = Toolkit.getDefaultToolkit().createImage(
				this.getClass().getClassLoader()
						.getResource("com/flat20/fingerplay/connect_on.png"));

		PopupMenu popup = new PopupMenu();

		final TrayIcon trayIcon = new TrayIcon(image, "Midi.IO Server Running",
				popup);
		trayIcon.setImageAutoSize(true);

		try {
			Enumeration<NetworkInterface> netIO = NetworkInterface
					.getNetworkInterfaces();

			while (netIO.hasMoreElements()) {
				NetworkInterface n = netIO.nextElement();
				Enumeration<InetAddress> netAdress = n.getInetAddresses();
				while (netAdress.hasMoreElements()) {
					InetAddress i = netAdress.nextElement();

					if (i.isSiteLocalAddress()) {
						mLocalIP = i.getHostAddress();
						// System.out.println(i.getHostAddress());
					}
				}
			}

			if (mLocalIP == null) {
				InetAddress localAddress = InetAddress.getLocalHost();
				mLocalIP = localAddress.getHostAddress();
			}

		} catch (SocketException e2) {

			e2.printStackTrace();
		} catch (UnknownHostException e1) {

			e1.printStackTrace();
		}

		multicastOutputMessage = mLocalIP + ":" + mPort;

		if (SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();

			MenuItem item = new MenuItem("Info");

			item.addActionListener(new ShowMessageListener(trayIcon,
					"7Pad Midi.IO server", "Im Listening on "
							+ multicastOutputMessage, TrayIcon.MessageType.INFO));
			popup.add(item);

			item = new MenuItem("Clear connexions");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// clean client connections
					ListIterator<ServerSocketThread> iter = socksClients
							.listIterator();
					while (iter.hasNext()) {

						ServerSocketThread sock = iter.next();
						sock.stopMe();
						iter.remove();
					}
					Midi.number_of_connections = 0;
					trayIcon.setImage(image);
					trayIcon.displayMessage("7Pad Midi.IO", "v" + VERSION
							+ " \n Listening on : " + multicastOutputMessage,
							TrayIcon.MessageType.INFO);
					System.out.println("Waiting for connection...");
					System.out.println("Im Listening on "
							+ multicastOutputMessage);

				}
			});
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
			serverSocket = new ServerSocket(mPort);

		} catch (IOException e1) {

			e1.printStackTrace();
		} catch (IllegalThreadStateException e1) {

			e1.printStackTrace();
		}

		// serverSocket.setSoTimeout(timeout_length);

		startSrvLoop(serverSocket, trayIcon, image, imageR);

	}

	static class ShowMessageListener implements ActionListener {
		TrayIcon trayIcon;
		String title;
		String message;
		TrayIcon.MessageType messageType;

		ShowMessageListener(TrayIcon trayIcon, String title, String message,
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

	public void startSrvLoop(ServerSocket serverSocket, TrayIcon trayIcon,
			Image image, Image imageR) {
		try {

			trayIcon.displayMessage("7Pad Midi.IO", "v" + VERSION
					+ " \n Listening on : " + multicastOutputMessage,
					TrayIcon.MessageType.INFO);
			System.out.println("Waiting for connection...");
			System.out.println("Im Listening on " + multicastOutputMessage);

			while (!Thread.currentThread().isInterrupted()
					&& !serverSocket.isClosed()) {
				Socket client = null;
				if (!serverSocket.isClosed()) {
					
					// wait for client socket connexion
					client = serverSocket.accept();
				}

				// close if exist
				ListIterator<ServerSocketThread> iter = socksClients
						.listIterator();
				while (iter.hasNext()) {

					ServerSocketThread sock = iter.next();
					boolean remove = false;
					if (sock.getClientConn() != null && client != null) {
						// reconnect, same IP
						if (client.getInetAddress().equals(
								sock.getClientConn().getInetAddress())) {
							remove = true;
						} else {
							if (sock.getClientConn().isClosed()) {
								remove = true;
							}
						}
					} else {
						remove = true;
						// null client connection
					}
					if (remove) {
						sock.stopMe();
						iter.remove();
					

					}

				}
				
				
					
				if (Midi.number_of_connections < max_connections) {
					// for midi RT messages

					// create and run socket thread for new client
					ServerSocketThread st = new ServerSocketThread(client,
							trayIcon, image);

					// timestamps init for the first connected client
					UdpOutput.getInstance().init(client.getInetAddress(),
							FingerPlayServer.UDP_SERVERPORT);

					st.start();
					socksClients.add(st);

					Midi.number_of_connections++;
					trayIcon.setImage(imageR);
					trayIcon.displayMessage("Incoming client",
							"New client connexion accepted.",
							TrayIcon.MessageType.INFO);
				} else {
					client.close();
					trayIcon.displayMessage("New client error",
							"Client Connexion rejected max reached : "
									+ max_connections,
							TrayIcon.MessageType.INFO);
				}
			}
			serverSocket = null;
			UdpOutput.getInstance().close();
			Midi.getInstance().close();
		}

		catch (IOException e1) {

			e1.printStackTrace();
		} catch (IllegalThreadStateException e1) {

			e1.printStackTrace();

		}
	}

	public static void main(String[] args) {
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
