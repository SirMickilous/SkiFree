package game2D;
// Formatted
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FadeOutFilter extends FilterInputStream {

	FadeOutFilter(InputStream in) { super(in); }

	/**
     * Get a value from the array 'buffer' at the given 'position'
	 * and convert it into short big-endian format
  	 */
	public short getSample(byte[] buffer, int position)
	{
		return (short) (((buffer[position+1] & 0xff) << 8) |
					     (buffer[position] & 0xff));
	}

	/**
     * Set a short value 'sample' in the array 'buffer' at the
	 * given 'position' in little-endian format
  	 */
	public void setSample(byte[] buffer, int position, short sample)
	{
		buffer[position] = (byte)(sample & 0xFF);
		buffer[position+1] = (byte)((sample >> 8) & 0xFF);
	}

	
	public int read(byte [] sample, int offset, int length) throws IOException	
	{
		int 	bytesRead = super.read(sample,offset,length); // Get the number of bytes in the data stream
		int		p;			// Loop variable
		short 	amp = 0;	// The amplitude read from the sound sample
		short	val = 0;	// The value read from further down the sample array
		short	faded = 0;	// The amplitude for the faded sound

		int		delay = bytesRead/10;	// The delay, where to write the faded sound 
		int		delayed = 0;			// Position of the faded delay in the 'sample' array

		//	Loop through the sample 2 bytes at a time
		for (p=0; p<bytesRead; p += 2)
		{
			// Get the value at the front of the sound buffer
			amp = getSample(sample,p);

			// Work out where to put the new echoed sound
			delayed = p + delay;
			if (delayed < bytesRead)
			{
				// Get the delayed value, add a fade version of it
				val = getSample(sample,p+delay);
				faded = (short)((amp * 0.4) + val);
				delayed = p + delay;

				// Now put the new value back in the sample array.
				setSample(sample,delayed,faded);
			}

		}
		return length;
	}
}