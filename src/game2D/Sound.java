package game2D;
// Formatted
import java.io.*;
import javax.sound.sampled.*;

public class Sound extends Thread {

	String filename; 	// The name of the file to play
	boolean finished; 	// A flag showing that the thread has finished
	boolean filtered; 	// Do you want to apply the filter?
	boolean continuous; // Do you want the sound to repeat until closed?

	public Sound(String fname, boolean f, boolean c) {
		filename = fname;
		filtered = f;
		continuous = c;
		finished = false;
	}

	/**
	 * Run will play the actual sound but you should not call it directly.
	 * You need to call the 'start' method of your sound object (inherited
	 * from Thread, you do not need to declare your own). 'run' will
	 * eventually be called by 'start' when it has been scheduled by
	 * the process scheduler.
	 */
	public void run() 
	{
		try 
		{
			// Set up variables
			File file = new File(filename);
			AudioInputStream stream = AudioSystem.getAudioInputStream(file);
			AudioFormat	format = stream.getFormat();
			Clip clip;
			
			// Create the clip using the transform if applicable, 
			// could be turned into multiple filters assigned using 
			// an integer parameter and a switch statement
			if (filtered) 
			{
				FadeOutFilter filtering = new FadeOutFilter(stream);
				AudioInputStream f = new AudioInputStream(filtering,format,stream.getFrameLength());
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				clip = (Clip)AudioSystem.getLine(info);
				clip.open(f);
				clip.start();
			}
			else
			{
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				clip = (Clip)AudioSystem.getLine(info);
				clip.open(stream);
				clip.start();
			}
			
			Thread.sleep(5);
			
			if (continuous) 
			{				
				clip.loop(Clip.LOOP_CONTINUOUSLY);
				
				while (clip.isRunning() && !finished) { Thread.sleep(100); }
				clip.close();
			}
			else 
			{
				while (clip.isRunning() || !finished) { Thread.sleep(100); }
				clip.close();
			}
		}
		catch (Exception e) {	}
		finished = true;

	}
	
	public void setFinished(boolean b) {
		this.finished = b;
	}
	
	public boolean isFinished() {
		return this.finished;
	}

}
