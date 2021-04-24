package game2D;
// Formatted
public class Player extends Sprite {

	// Declare all Player animations
	Animation skidown;
	Animation skislope;
	Animation skicrash;
	Animation skicelebrate;

	// Declare all sounds associated with the player
	Sound ooft;
	Sound scream;

	/**
     * Constructor for Player sets skidown as the default animation
     */
	public Player() {

		// Load all the animations
		skidown = new Animation();
		skidown.loadAnimationFromSheet("images/skidown.png", 3, 1, 120);
		skislope = new Animation();
		skislope.loadAnimationFromSheet("images/skislope.png", 2, 1, 120);
		skicrash = new Animation();
		skicrash.loadAnimationFromSheet("images/skicrash.png", 1, 1, 1000);
		skicelebrate = new Animation();
		skicelebrate.loadAnimationFromSheet("images/skicomplete.png", 1, 1, 1000);
		
		// Constructor
		anim = skidown;
		render = true;
		scale = 1.0f;
		rotation = 0.0f;

	}

	/**
     * Assign the skidown animation as the current animation anim
     */
	public void setAnimSkidown() {
		this.anim = skidown;
	}

	/**
     * Assign the skislope animation as the current animation anim
     */
	public void setAnimSkislope() {
		this.anim = skislope;
	}

	/**
     * Assign the skicrash animation as the current animation anim
     */
	public void setAnimSkicrash() {
		this.anim = skicrash;
	}
	
	/**
     * Assign the skicelebrate animation as the current animation anim
     */
	public void setAnimSkicelebrate() {
		this.anim = skicelebrate;
	}	

	/**
     * Play the ooft sound effect
     */
	public void playOoft() {
		ooft = new Sound("sounds/Ooft.wav", false, false);
		ooft.start();
	}

	/**
     * Play the scream sound effect
     */
	public void playScream() {
		scream = new Sound("sounds/WilhelmScream.wav", false, false);
		scream.start();
	}
	
	/**
     * @return The adjusted Bottom Left corner y component of the Sprite
     * <<< Overriding method >>>
     */
	public float getBLy() {
		if (this.isDirectionRight())
		{
			return y+(height/2);
		}
		else
		{
			return y+height;
		}
	}
	
	/**
     * @return The adjusted Bottom Right corner y component of the Sprite
     * <<< Overriding method >>>
     */
	public float getBRy() {
		if (this.isDirectionLeft())
		{
			return y+(height/2);
		}
		else
		{
			return y+height;
		}
		
	}

}
