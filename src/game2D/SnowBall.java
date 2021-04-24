package game2D;
// Formatted
public class SnowBall extends Sprite {

    // Target point coordinates
    private float targetX;
    private float targetY;
    
    // This snow balls animation
    Animation snowball;
    
    // Instance of sound which is directly associated with this snow ball
    Sound soundEffect;
    
    /**
     *  Creates a new Sprite object
     *  
     *  @param tX	The x component of the snow balls target coordinate
     *  @param tY	The y component of the snow balls target coordinate
     */
    public SnowBall(float tX, float tY) {
     	
    	snowball = new Animation();
		snowball.loadAnimationFromSheet("images/snowball.png", 1, 1, 60);
    	
        this.anim = snowball;
        render = true;
        scale = 1.0f;
        rotation = 0.0f;
        
        this.targetX = tX;
        this.targetY = tY;
    }
    
    /**
     *  Sets the snow balls target x and y coordinate components
     *  
     *  @param x	The x component of the snow balls target coordinate
     *  @param x	The y component of the snow balls target coordinate
     */
    public void setTarget(float x, float y) {
    	this.targetX = x;
    	this.targetY = y;
    }
    
    /**
     *  @return The x component of the snow balls target
     */
    public float getTargetX() {
    	return this.targetX;
    }
    
    /**
     *  @return The y component of the snow balls target
     */
    public float getTargetY() {
    	return this.targetY;
    }
    
    /**
     *  Method used to play the snow balls sound effect with a filter
     */
    public void playSoundEffect() {
    	soundEffect = new Sound("sounds/Whoosh6Extended.wav", true, false);
    	soundEffect.start();
    }
    
    /**
     * @return Reference to the snow balls sound effect
     */
    public Sound getSoundEffect() {
    	return this.soundEffect;
    }

    
}
