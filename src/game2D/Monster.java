package game2D;
// Formatted
public class Monster extends Sprite {

    // States of the Monster
    private boolean patrolling;
    private boolean hunting;
    private boolean returning;
    private boolean stunned;
        
    // Patrol point coordinates
    private float patrolX;
    private float patrolY;
    
    // Internal counter for stun duration
    int stunTimer;
    
    // Animations
    Animation monsterwalk;
    Animation monsterchase;
    Animation monstereats;
    Animation monsterstunned;
    
    // Sound effects
    Sound roar;

    /**
     * Constructor for Monster sets monsterwalk as the default animation
     * and all flag states are false.
     */
	public Monster() {
			
		monsterwalk = new Animation();
		monsterwalk.loadAnimationFromSheet("images/monsterwalk.png", 3, 1, 120);
		monsterchase = new Animation();
		monsterchase.loadAnimationFromSheet("images/monsterchase.png", 2, 1, 120);
		monstereats = new Animation();
		monstereats.loadAnimationFromSheet("images/monstereats.png", 11, 1, 120);
		monstereats.setLoop(false);
		monsterstunned = new Animation();
		monsterstunned.loadAnimationFromSheet("images/monsterstunned.png", 2, 1, 120);
				
        anim = monsterwalk;
        render = true;
        scale = 1.0f;
        rotation = 0.0f;
        
        patrolling = false;
        hunting = false;
        returning = false;
        stunned = false;
        
        stunTimer = 0;
		
	}
	
	/**
     * @param The patrolling state of the monster
     */
    public void setPatrolling(boolean b) {
    	patrolling = b;
    }
    
	/**
     * @param The hunting state of the monster
     */
    public void setHunting(boolean b) {
    	hunting = b;
    }
    
	/**
     * @param The returning state of the monster
     */
    public void setReturning(boolean b) {
    	returning = b;
    }
    
	/**
     * @param The stunned state of the monster
     */
    public void setStunned(boolean b) {
    	stunned = b;
    }
    
	/**
     * @return The patrolling state of the monster
     */
    public boolean isPatrolling() {
    	return patrolling;
    }

	/**
     * @return The hunting state of the monster
     */
    public boolean isHunting() {
    	return hunting;
    }

	/**
     * @return The returning state of the monster
     */
    public boolean isReturning() {
    	return returning;
    }

	/**
     * @return The stunned state of the monster
     */
    public boolean isStunned() {
    	return stunned;
    }
    
	/**
	 * Method for setting the patrol point of the monster
	 * 
     * @param x		The x component of the patrol coordinate
     * @param y		The y component of the patrol coordinate
     */
    public void setPatrol(float x, float y) {
    	patrolX = x;
    	patrolY = y;
    }
    
	/**
     * @return The x component value of the patrol coordinate
     */
    public float getPatrolX() {
    	return patrolX;
    }
    
	/**
     * @return The y component value of the patrol coordinate
     */
    public float getPatrolY() {
    	return patrolY;
    }
    
	/**
     * Assign the monsterwalk animation as the current animation anim
     */
    public void setAnimWalking() {
    	this.anim =  monsterwalk;
    }
    
	/**
     * Assign the monsterchase animation as the current animation anim
     */
    public void setAnimChasing() {
    	this.anim =  monsterchase;
    }
    
	/**
     * Assign the monstereats animation as the current animation anim
     */
    public void setAnimEating() {
    	this.anim =  monstereats;
    }
    
	/**
     * Assign the monsterstunned animation as the current animation anim
     */
    public void setAnimStunned() {
    	this.anim =  monsterstunned;
    }
    
	/**
     * @param The value of the required stun duration
     */
    public void setStunTimer(int i) {
    	stunTimer = i;
    }
    
	/**
     * @return The value of the current stun duration
     */
    public int getStunTimer() {
    	return stunTimer;
    }
    
	/**
     * Reduces the stunTimer by 1
     */
    public void reduceStunTimer() {
    	stunTimer--;
    }
    
	/**
     * Play the roar sound effect
     */
	public void playRoar() {
		roar = new Sound("sounds/MonsterRoar.wav", false, false);
		roar.start();
	}
}
