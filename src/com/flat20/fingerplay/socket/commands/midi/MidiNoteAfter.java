package com.flat20.fingerplay.socket.commands.midi;
/**
 * Program change 
 * channel is 0-16
 * @author mixtic
 *
 */
public class MidiNoteAfter extends MidiSocketCommand {

	final public static int NA_COMMAND = 0xA0 ;

	public MidiNoteAfter() {
		super();
	}

	public MidiNoteAfter(int channel,  int value0, int value1) {
		
		super( NA_COMMAND, channel,value0 ,value1);
		
	}
	public void set( int channel,  int value0, int value1){

		super.set (NA_COMMAND,channel, value0, value1 );
	}

}
