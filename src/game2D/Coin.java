package game2D;
// Formatted
public class Coin extends Sprite {
	
	// Animations
	Animation coinspin;
	
	// Sounds
	Sound soundEffect;
	
	/**
     * Constructor for Coin sets coinspin as the default animation
     */
	public Coin() {
		
		coinspin = new Animation();
		coinspin.loadAnimationFromSheet("images/coinsheet.png", 4, 1, 80);
		
		anim = coinspin;
        render = true;
        scale = 1.0f;
        rotation = 0.0f;
		
	}
	
	/**
     * Play sound effect for this Coin
     */ 
    public void playSoundEffect() {
    	soundEffect = new Sound("sounds/Coinflip.wav", false, false);
    	soundEffect.start();
    }

}
