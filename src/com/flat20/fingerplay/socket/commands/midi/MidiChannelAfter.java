package com.flat20.fingerplay.socket.commands.midi;

/**
 * Program change 
 * channel is 0-16
 * @author mixtic
 *
 */
public class MidiChannelAfter extends MidiSocketCommand {

	final public static int CA_COMMAND = 0xD0 ;

	public MidiChannelAfter() {
		super();
	}

	public MidiChannelAfter(int channel,  int value) {
		
		super( CA_COMMAND, channel,value,value);
		
	}
	public void set( int channel,  int value) {

		super.set (CA_COMMAND,channel, value , value );
	}

}
