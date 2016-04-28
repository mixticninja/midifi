package com.flat20.fingerplay.socket.commands.midi;

import javax.sound.midi.ShortMessage;

import com.flat20.fingerplay.socket.commands.SocketCommand;

public class MidiSocketCommand extends SocketCommand {

	public int midiCommand;
	public int channel;
	public int data1;
	public int data2;

	
	public MidiSocketCommand() {
		super(COMMAND_MIDI_SHORT_MESSAGE);
	}

	public MidiSocketCommand(ShortMessage msg){
		super(COMMAND_MIDI_SHORT_MESSAGE);
		set(msg.getCommand(), msg.getChannel(), msg.getData1(), msg.getData2());
	}
	
	public MidiSocketCommand(int midiCommand, int channel, int data1, int data2) {
		super(COMMAND_MIDI_SHORT_MESSAGE);
		set(midiCommand, channel, data1, data2);
	}

	// Makes them unsigned.
	public MidiSocketCommand(byte midiCommand, byte channel, byte data1, byte data2) {
		super(COMMAND_MIDI_SHORT_MESSAGE);
		set(midiCommand, channel, data1, data2);
	}


	public void set(int midiCommand, int channel, int data1, int data2) {
		this.midiCommand = midiCommand;
		this.channel = channel;
		this.data1 = data1;
		this.data2 = data2;
	}

	public void set(byte midiCommand, byte channel, byte data1, byte data2) {
		this.midiCommand = midiCommand & 0xFF;
		this.channel = channel & 0xFF;
		this.data1 = data1 & 0xFF;
		this.data2 = data2 & 0xFF;
	}

}
