package com.flat20.fingerplay.socket.commands.midi;

/**
 * Program change 
 * channel is 0-16
 * @author mixtic
 *
 */
public class MidiPitchBend extends MidiSocketCommand {

	final public static int PB_COMMAND = 0xE0 ;

	public MidiPitchBend() {
		super();
	}

	public MidiPitchBend(int channel,  int value) {
		
		super( PB_COMMAND, channel,value & 0x7F,(value >> 7) & 0x7F);
		
	}
	public void set( int channel,  int value) {

		super.set (PB_COMMAND,channel, value & 0x7F, (value >> 7) & 0x7F);
	}

}
