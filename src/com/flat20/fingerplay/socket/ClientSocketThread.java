package com.flat20.fingerplay.socket;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Transmitter;

import com.flat20.fingerplay.FingerPlayServer;
import com.flat20.fingerplay.Midi;
import com.flat20.fingerplay.MidiReceiver;
import com.flat20.fingerplay.MidiReceiver.IMidiListener;
import com.flat20.fingerplay.socket.commands.FingerReader;
import com.flat20.fingerplay.socket.commands.FingerReader.IReceiver;
import com.flat20.fingerplay.socket.commands.FingerWriter;
import com.flat20.fingerplay.socket.commands.midi.MidiControlChange;
import com.flat20.fingerplay.socket.commands.midi.MidiNoteOff;
import com.flat20.fingerplay.socket.commands.midi.MidiSocketCommand;
import com.flat20.fingerplay.socket.commands.misc.DeviceList;
import com.flat20.fingerplay.socket.commands.misc.RequestMidiDeviceList;
import com.flat20.fingerplay.socket.commands.misc.SetMidiDevice;
import com.flat20.fingerplay.socket.commands.misc.Version;

public class ClientSocketThread implements Runnable, IReceiver, IMidiListener {

	final private Socket client;
	final private Midi midi;
	final private DataInputStream in;
	final private DataOutputStream out;
	final private FingerWriter mWriter;

	final private MidiReceiver mMidiReceiver;
	
	private TrayIcon trayIco = null;
	private Image image =null;
	
	public ClientSocketThread(Socket client, Midi midi,TrayIcon trayIco,Image image) throws IOException {
		this.client = client;
		this.midi = midi;
		this.trayIco = trayIco;
		this.image = image;
		in = new DataInputStream(client.getInputStream());
		out = new DataOutputStream(client.getOutputStream());
		mWriter = new FingerWriter(out);
		mMidiReceiver = new MidiReceiver(this);

	}

	public void run() {
		try {

			final FingerReader reader = new FingerReader(in, this);
			//System.out.println("run thread");
			while (client.isConnected()) {
					try {
						// Reads one command
						reader.readCommand();
					} catch (SocketTimeoutException e) {
						
						System.out.println("socket read timed out..");
					}
			}

		} catch (SocketException e) {
			//System.out.println(e);
		} catch (EOFException e) {
			//System.out.println(e);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Client message too long for buffer.");
			System.out.println(e);
		} catch(Exception e) {
			System.out.println("S: Error");
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException io) {
			}
		}
		//System.out.println("Client disconnected.");
		if(SystemTray.isSupported()){
			 trayIco.setImage(image);
			 trayIco.displayMessage("Deconnexion", "Client disconnected.", TrayIcon.MessageType.INFO);
		
		}
		
		
		
		return;
	}

	public void onVersion(Version clientVersion) throws Exception {
		System.out.println("Client version: " + clientVersion.message);

		Version version = new Version(FingerPlayServer.VERSION);

		mWriter.write(version);
	}

	public void onMidiSocketCommand(MidiSocketCommand socketCommand) throws Exception {
	//	System.out.println("midiCommand = " + socketCommand.midiCommand + " channel = " + socketCommand.channel + ", data1 = " + socketCommand.data1 + " data2 = " + socketCommand.data2);
		synchronized (midi) {
			midi.sendShortMessage(socketCommand.midiCommand, socketCommand.channel, socketCommand.data1, socketCommand.data2);						
		}
	}

	public void onRequestMidiDeviceList(RequestMidiDeviceList request) throws Exception {
		String[] deviceNames = Midi.getDeviceNames(false, true);

		String allDevices = "";
		for (int i=0; i<deviceNames.length; i++) {
			allDevices += deviceNames[i] + "%";
		}
		if (deviceNames.length > 0)
			allDevices = allDevices.substring(0, allDevices.length()-1);

		DeviceList deviceList = new DeviceList( allDevices );

		mWriter.write(deviceList);
	}

	public void onSetMidiDevice(SetMidiDevice ssm) throws Exception {

		String device = ssm.message;

		System.out.println("Setting MIDI Device: " + device);
		synchronized (midi) {
			midi.close();
			MidiDevice midiDeviceIN = midi.open(device, false); // true = bForOutput
			midi.open(device, true); // true = bForOutput

			System.out.println("midiDeviceIN = " + midiDeviceIN);

			if (midiDeviceIN != null) {
				Transmitter	t = midiDeviceIN.getTransmitter();
				if (t != null)
					t.setReceiver(mMidiReceiver);
			}

		}
	}

	public void onDeviceList(DeviceList deviceList) throws Exception {
		
	}

	public void onControlChange(int channel, int control2, int value) {
		MidiControlChange mcc = new MidiControlChange(0xB0, channel, control2, value);
		try {
			mWriter.write( mcc );
		} catch (Exception e) {
			
		}
	}

	public void onNoteOff(int channel, int key, int velocity) {
		MidiNoteOff mno = new MidiNoteOff(channel, key, velocity);
		try {
			mWriter.write( mno );
		} catch (Exception e) {
			
		}
	}

	public void onNoteOn(int channel, int key, int velocity) {
		MidiNoteOff mno = new MidiNoteOff(channel, key, velocity);
		try {
			mWriter.write( mno );
		} catch (Exception e) {
			
		}
	}

}
