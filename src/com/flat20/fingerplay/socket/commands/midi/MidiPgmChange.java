package com.flat20.fingerplay.socket.commands.midi;

/**
 * Program change 
 * channel is 0-16
 * @author mixtic
 *
 */
public class MidiPgmChange extends MidiSocketCommand {

	final public static int PC_COMMAND = 0xC0 ;

	public MidiPgmChange() {
		super();
	}

	public MidiPgmChange(int channel,  int value) {
		
		super( PC_COMMAND, channel,value,value);
		
	}
	public void set( int channel,  int value) {

		super.set (PC_COMMAND,channel, value, value);
	}

}
