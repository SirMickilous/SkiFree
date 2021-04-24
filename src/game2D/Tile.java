package game2D;
// Formatted
public class Tile {

	private char character=' ';	// The character associated with this tile
	private int xc=0;			// The tile's x coordinate in pixels
	private int yc=0;			// The tile's y coordinate in pixels
	
	// <<< NEW ATTRIBUTES >>>
	
	private float xTL;
	private float yTL;
	private float xBR;
	private float yBR;
	
 	
	/**
	 * Create an instance of a tile
	 * @param c	The character associated with this tile
	 * @param x The x tile coordinate in pixels
	 * @param y The y tile coordinate in pixels
	 */
	public Tile(char c, int x, int y)
	{
		character = c;
		xc = x;
		yc = y;
	}

	/**
	 * @return The character for this tile
	 */
	public char getCharacter() {
		return character;
	}

	/**
	 * @param character The character to set the tile to
	 */
	public void setCharacter(char character) {
		this.character = character;
	}

	/**
	 * @return The x coordinate (in pixels)
	 */
	public int getXC() {
		return xc;
	}

	/**
	 * @return The y coordinate (in pixels)
	 */
	public int getYC() {
		return yc;
	}

	// <<< NEW METHODS >>>
	
	/**
	 * @param The top left corner x component of this tiles hit box.
	 */
	public float getTLx() {
		return xTL;    
	}
	
	/**
	 * @param The top left corner y component of this tiles hit box.
	 */
	public float getTLy() {
		return yTL;    
	}
	
	/**
	 * @param The bottom right corner x component of this tiles hit box.
	 */
	public float getBRx() {
		return xBR;     
	}     
	
	/**
	 * @param The bottom right corner y component of this tiles hit box.
	 */
	public float getBRy() {
		return yBR;
	}
	
	/**
	 * @return The top left corner x component of this tiles hit box.
	 */
	public void setTLx(float tlx) {
		xTL = tlx;
	}
	
	/**
	 * @return The top left corner y component of this tiles hit box.
	 */
	public void setTLy(float tly) {
		yTL = tly;    
	} 
	
	/**
	 * @return The bottom right corner x component of this tiles hit box.
	 */
	public void setBRx(float brx) {
		xBR = brx;    
	}
	
	/**
	 * @return The bottom right corner y component of this tiles hit box.
	 */
	public void setBRy(float bry) {
		yBR = bry;  
	}
}
