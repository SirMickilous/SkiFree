import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
// import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import game2D.*;

// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc. By default GameCore
// will handle the 'Escape' key to quit the game but you should
// override this with your own event handler.

/**
 * @author Michael Lough
 *
 */
@SuppressWarnings("serial")

public class Game extends GameCore {

	// Useful game constants
	final static int noR = 120; // The number of player positions recorded
	final static int waitTime = 240; // The wait time of the player when crashing

	final static float maxSpeed = 0.16f; // Normal maximum speed of player
	final static float boostedMaxSpeed = 0.22f; // Boosted maximum speed of player
	final static float superChargedMaxSpeed = 0.28f; // Boosted & super charged speed of the player

	final static float monsterWalkSpd = 0.12f; // Monster maximum patrol speed
	final static float monsterChaseSpd = 0.20f; // Monster maximum chase speed
	final static float monsterPatrolDistance = 40; // Monster patrol distance
	final static float monsterSightRange = 128; // Monster range of sight
	final static float monsterSightOffset = 32; // The additional y range of the sight box
	final static int monsterStunDuration = 180; // The time each application of stun applies

	final static float snowBallSpd = 0.40f; // Speed of a snow ball
	
	static int screenWidth = 256; // Initial screen width
	static int screenHeight = 256; // Initial screen height
	static int seed; // The holder for the seed parameter

	int xo = 10; // Global X offset
	int yo = 0; // Global Y offset

	long total;
	double angle;

	int wait; // The timer variable for handling a player crash
	int chargeMeter; // The current count of super charge for the player
	int snowBallCharge;
	
	LinkedList<float[]> respawnLocations; // Stores the list of recorded player positions
	float[] respawnLocation = new float[2]; // Stores a single record of player position

	// Meta state flags
	boolean completed = false; // Completed the game

	// Game state flags
	boolean boost = false; // Is boosted (s)
	boolean superCharged = false; // Is super charged
	boolean turnLeft = false; // Is left key pressed (a)
	boolean turnRight = false; // Is right key pressed (d)
	boolean collidable = true; // Is the player collidable
	boolean crashed = false; // Has player crashed
	boolean crashing = false; // Has player finished crashing
	boolean caught = false; // Has the player been caught by a Monster
	boolean accessingSnowBalls; // Used to indicate when the list of all snow balls is being accessed, prevents concurrent access bug.
	boolean debugCollisions = false; // Is collision key pressed (c)
	boolean paused = false; // Is the game paused (p)

	ImageIcon sfIcon;
	Animation planeCrash;

	Player player;
	ArrayList<Coin> coins;
	ArrayList<Monster> monsters;
	ArrayList<SnowBall> snowballs;
	Sprite plane;
	
	LevelCreator lcfg;
	LevelCreator lcbg;
	TileMap tmapfg = new TileMap();
	TileMap tmapbg = new TileMap();

	Sound introScreen;
	Sound backgroundSound;
	
	Hashtable<String, LinkedList<Integer>> Scores;
	long totalScore;

	/**
	 * The obligatory main method that creates an instance of our class and starts
	 * it running
	 * 
	 * @param args The list of parameters this program might use (ignored)
	 */
	public static void main(String[] args) {

		Game gct = new Game();
		gct.init();
		// Start in windowed mode with the given screen height and width
		gct.run(false, screenWidth, screenHeight);
	}

	/**
	 * Initialise the class
	 */
	public void init() {

		sfIcon = new ImageIcon("images/skifree.png");
		
		planeCrash = new Animation();
		planeCrash.loadAnimationFromSheet("images/planeCrashFitted.png", 1, 1, 1000);

		Scores = new Hashtable<String, LinkedList<Integer>>();
		LoadPresetSeed4321Scores(Scores);
		
		seed = setUpTheSeed();
		// seed = 0; // Remember to change line 158, when using the demo level

		initialiseGame(seed);

	}

	/**
	 * Initialises the new game state from the seed provided
	 * 
	 * @param seed - The seed is used to generate the tile map and associated
	 *             sprites
	 */
	public void initialiseGame(int seed) {
		
		backgroundSound = new Sound("sounds/BackgroundMusic.wav", false, true);
		backgroundSound.start();

		xo = 10;
		yo = 0;

		total = 0;
		angle = 45;

		lcfg = new LevelCreator(seed, "maps/foreground");
		lcbg = new LevelCreator(seed, "maps/background");

		tmapbg = lcbg.getLevel();
		tmapfg = lcfg.getLevel();

		screenWidth = tmapbg.getPixelWidth() + 20;
		screenHeight = tmapbg.getPixelHeight() /12;  // replace with () / 2; //12; When using the demo mode, seed 0
		setSize(screenWidth, screenHeight);
		setVisible(true);

		totalScore = 100000;
		chargeMeter = 0;
		snowBallCharge = 5000;
		
		coins = new ArrayList<Coin>();
		monsters = new ArrayList<Monster>();
		snowballs = new ArrayList<SnowBall>();
		plane = new Sprite(planeCrash);
		plane.setX(256);
		plane.setY(256);
		plane.show();
		
		player = new Player();
		respawnLocations = new LinkedList<float[]>();
		player.setX((plane.getX()+128));
		player.setY((plane.getY()+128));
		player.setVelocityX(0);
		player.setVelocityY(0);
		player.show();

		loadCoins(tmapbg, coins, 'c');
		loadMonsters(tmapbg, monsters, 'm');
		InitialiseMonsters(monsters);
		setAllTileHitBoxes(tmapbg);

		System.out.println(tmapbg.toString());

		paused = false;
		completed = false;
	
	}

	/**
	 * Draw the current state of the game
	 */
	public void draw(Graphics2D g) {

		// Calculate the y offset relative to the position of the player sprite
		if (player.getY() >= (screenHeight / 3)
				&& player.getY() <= tmapfg.getPixelHeight() - (2 * (screenHeight / 3))) 
		{
			yo = (int) (-player.getY() + (screenHeight / 3));
		}
		if (player.getY() > tmapfg.getPixelHeight() - (2 * (screenHeight / 3))) 
		{
			yo = (int) (screenHeight - tmapfg.getPixelHeight());
		}

		// Reset the image from previous draw
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

			plane.setOffsets(xo, yo);
			if ( ((int)(plane.getY()+plane.getHeight() + yo) > 0) && plane.isVisible()) 
			{
				plane.show();
				plane.draw(g);
			} 
			else 
			{
				plane.hide();
			}
		
		
		// Apply offsets to background layer and draw
		tmapbg.draw(g, xo, yo);

		// Draw the trail of the skier using the re-spawn locations list
		for (float[] h : respawnLocations) 
		{
			if (superCharged) 
			{
				g.setColor(Color.red);
			} 
			else 
			{
				g.setColor(Color.lightGray);
			}
			g.drawRect((int) h[0] + xo + (player.getWidth() / 3), (int) h[1] + yo + (int) (player.getHeight() * 0.6), 2,
					2);
			g.drawRect((int) h[0] + xo + (2 * (player.getWidth() / 3)),
					(int) h[1] + yo + (int) (player.getHeight() * 0.6), 2, 2);

		}
		
		accessingSnowBalls = true;
		for (SnowBall s : snowballs) 
		{
			s.setOffsets(xo, yo);
			
			if ((s.getY() + yo < 0)
			||  (s.getY() + yo > screenHeight)
			|| ((s.getX() + s.getWidth()) < 0)
			||  (s.getX() > screenWidth))			
			{
				s.hide();
			} 
			else 
			{
				s.drawTransformedRotate(g, angle);
			}
		}
		accessingSnowBalls = false;

		// Apply offsets to the player and draw, applying a transform if necessary
		player.setOffsets(xo, yo);

		if (player.isDirectionRight()) 
		{
			player.drawTransformedYFlip(g);
		} 
		else 
		{
			player.draw(g);
		}

		// Apply offsets to coins then draw them if they are within the screen limits
		for (Coin c : coins) 
		{
			c.setOffsets(xo, yo);
			
			if (((int) (c.getY() + yo) > 0) && ((int) (c.getY() + yo) < screenHeight)) 
			{
				c.show();
				c.draw(g);
			} 
			else 
			{
				c.hide();
			}
		}

		// Apply offsets to monsters then draw them if they are within the screen limits
		for (Monster m : monsters) 
		{
			m.setOffsets(xo, yo);
			
			if (((int) (m.getY() + yo) > 0) && ((int) (m.getY() + yo) < screenHeight)) 
			{
				m.show();
				if (m.isDirectionRight()) 
				{
					m.drawTransformedYFlip(g);
				} 
				else 
				{
					m.draw(g);
				}
			} 
			else 
			{
				m.hide();
			}

		}

		// Apply offsets to the foreground layer and draw it
		tmapfg.draw(g, xo, yo);
	
		// Draw score and charge meter statistics in the top right of the screen
		String msgScr = String.format("Score: %d", (int) totalScore / 100);
		g.setColor(Color.darkGray);
		g.drawString(msgScr, getWidth() - 100, 50);

		// Draw supercharge value if supercharged 
		String msgSup = String.format("Charge: " + chargeMeter);
		g.setColor(Color.darkGray);
		g.drawString(msgSup, getWidth() - 100, 80);
				
		String msgSnow = String.format("Snowballs: " + (int)(snowBallCharge/500));
		g.setColor(Color.darkGray);
		g.drawString(msgSnow, getWidth() - 100, 110);
		
		// String msgFPS = String.format("FPS: %f", getFPS());
		// g.drawString(msgFPS, getWidth() - 100, 140);
		
		if (paused) {
			g.setColor(Color.darkGray);
			g.drawString("<<<PAUSED>>>", screenWidth/2-30, screenHeight/2-50);
		}
	}

	/**
	 * Update the game state including all sprites
	 * 
	 * @param elapsed The elapsed time between this call and the previous call of
	 *                elapsed
	 */
	public void update(long elapsed) {
		if(elapsed < 100 && !paused) {
			
		// Check and handle if the end of the level is reached
		if (completed) 
		{
			endLevel();
		}

		total += elapsed;
		int change = (int) (elapsed / 5) % 360;
		angle = (angle + change) % 360;

		totalScore -= elapsed;
		
		if(snowBallCharge < 5000) 
		{
			snowBallCharge++;
		}
		
		// Update coin animations
		for (Coin c : coins) 
		{
			c.update(elapsed);
		}

		// Update monster animations
		for (Monster m : monsters) 
		{
			if (m.isStunned()) 
			{
				m.reduceStunTimer();
				if (m.getStunTimer() == 0) 
				{
					m.setStunned(false);
					if (m.isPatrolling() && m.isDirectionLeft()) 
					{
						m.setAnimWalking();
						m.setVelocityX(-monsterWalkSpd);
					}
					if (m.isPatrolling() && m.isDirectionRight()) 
					{
						m.setAnimWalking();
						m.setVelocityX(monsterWalkSpd);
					}
					if (m.isHunting())
					{
						m.setAnimChasing();
					}
					if (m.isReturning()) 
					{
						m.setAnimWalking();	
					}
				}
			}
			else
			{
				if (m.isPatrolling()) 
				{
					checkMonsterPosition(m);
					lookForPlayer(m, player);
				}
				if (m.isHunting()) 
				{
					if (!caught) 
					{
						chaseThePlayer(m, player);
						didWeGetThem(m, player);
						areTheyWorthIt(m);
					}
					if (caught) 
					{
						areTheyWorthIt(m);
					}
				}
				if (m.isReturning()) 
				{
					walkHome(m);
					areWeHomeYet(m);
				}
			}
			m.update(elapsed);
		}

		accessingSnowBalls = true;
		for (SnowBall s : snowballs) 
		{
			checkForSnowballHit(s, monsters);
			s.update(elapsed);
		}
		accessingSnowBalls = false;

		// Check and handle if the player has crashed, otherwise carry out regular
		// updates
		if (crashed) 
		{
			checkCrashStatus(player);
		} 
		else 
		{
			recordPlayerPosition(player);
			calculateSuperChargedState(player);
			calculateNewPlayerSpeed(player);
			calculateNewPlayerAnimation(player);
			handlePlayerScreenEdge(player, tmapbg);

			// After updating positional data check and handle collisions if collision is on
			if (collidable) 
			{
				checkPlayerTileCollision(player, tmapbg);
				checkCoinCollision(player, coins);
			}
		}
		player.update(elapsed);
		}
	}

	/**
	 * Method for displaying initial set up screens, explains game objective, controls and
	 * requests a seed value used in level creation.
	 */
	public int setUpTheSeed() {

		introScreen = new Sound("sounds/Intro.wav", false, true);
		introScreen.start();

		seed = 0;

		JOptionPane.showMessageDialog(null,
				"Welcome to Ski Free, you have crash landed on the way to a skiing holiday you must make it down the mountain to the \n"
						+ "village at the bottom as fast as possible. Collect as many coins as you can as they will increase your \n"
						+ "speed and score. Watch out for monsters lurking in the mountain they won't hesitate having a second breakfast \n"
						+ "if they catch sight of you. To generate a new random level just enter a seed \n"
						+ "value in the final prompt that is between 1 and lets say 9,999,999 \n" + "\nGood Luck!",
				"Ski Free", JOptionPane.PLAIN_MESSAGE, sfIcon);
		JOptionPane.showMessageDialog(null,
				" CONTROLS\n" + "--- use \"a\" & \"d\" keys to move your skier left and right. \n"
						+ "--- press the \"s\" key to get a little boost but coins and magical rocks are much more effective. \n"
						+ "--- click on the screen to throw a snowball at monsters and stun them, the effects stack! \n"
						+ "    if you do crash don't worry, you will respawn nearby at a safe location and can continue the level. \n",
				"Ski Free", JOptionPane.PLAIN_MESSAGE, sfIcon);

		seed = promptForSeed();

		introScreen.setFinished(true);
		return seed;

	}
	/**
	 * Method for getting and validating the users choice of seed or exiting
	 * appropriately
	 */
	public int promptForSeed() {

		String response = JOptionPane.showInputDialog(null, "Please enter a seed value?", "Ski Free",
				JOptionPane.PLAIN_MESSAGE);
		if (response == null) 
		{
			System.exit(0);
		}
		response = response.replaceAll("[^0-9]", "");
		
		if (response.equals("")) 
		{
			response = response + 0;
		}

		while (Integer.parseInt(response) < 1 || Integer.parseInt(response) > 99999999) 
		{
			response = JOptionPane.showInputDialog(null, "Try Again...\n" + "Please enter a valid seed value?",
					"Ski Free", JOptionPane.PLAIN_MESSAGE);
			
			if (response == null) 
			{
				System.exit(0);
			}
			response = response.replaceAll("[^0-9]", "");
			
			if (response.equals("")) 
			{
				response = response + 0;
			}

		}
		seed = Integer.parseInt(response);
		return seed;
	}
	/**
	 * Method handling the end of the game offering either to repeat the last seed
	 * or input a new one
	 */
	public void endLevel() {

		backgroundSound.setFinished(true);

		Sound effect = new Sound("sounds/Chime.wav", false, false);
		effect.start();

		int rtn = calculateAndRecordScore(Scores, totalScore, seed);
		int response = -1;
		
		if (rtn < 0) 
		{
			String[] options = new String[] { "Start Again", "New Seed", "Cancel" };
			response = JOptionPane.showOptionDialog(null, "There was a problem recording your score, would you \n"
					+ "like to start the same level again or select a new seed?", "SkiFree", JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		}
		else if (rtn <= 3)
		{
			Sound thirtyApp = new Sound("sounds/App30.wav", false, false);
			thirtyApp.start();
			String[] options = new String[] { "Start Again", "New Seed", "Cancel" };
			response = JOptionPane.showOptionDialog(null, "Welldone you are number: " + rtn + " in the leaderboards for this level. \n"
					+ "would you like to start the same level again or select a new seed?" , "SkiFree", JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			thirtyApp.setFinished(true);
		}
		else
		{
			Sound oneApp = new Sound("sounds/App1.wav", false, false);
			oneApp.start();
			String[] options = new String[] { "Start Again", "New Seed", "Cancel" };
			response = JOptionPane.showOptionDialog(null, "Not quite the top three, but you are number: " + rtn + " in the leaderboards \n"
					+ "for this level. Keep trying would you like to start the \n"
					+ "same level again or select a new seed?" , "SkiFree", JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			oneApp.setFinished(true);
		}
		
		switch (response) 
		{
		case 0:
			initialiseGame(seed);
			break;
		case 1:
			initialiseGame(promptForSeed());
			break;
		default:
			System.exit(0);
		}
	}
	/**
	 * Method used to record the score for the round and display 
	 * an appropriate message about the score position relative
	 * to other attempts.
	 * 
	 * @param scores		A Hashtable of all the seeds and their associated ordered lists of scores. 
	 * @param totalScore	This is the score for the current attempt.
	 * @param seed			This is the seed value of the current attempt. 
	 */
	public int calculateAndRecordScore(Hashtable<String, LinkedList<Integer>> scores, long totalScore, int seed) {
		
		
		String seedString = "" + seed; 
		int actualScore = ((int)totalScore/100);
				
		if (Scores.containsKey(seedString)) 
		{	

			for (int i = 0; i < Scores.get(seedString).size(); i++) 
			{			
				if (actualScore > Scores.get(seedString).get(i))
				{
					Scores.get(seedString).add(i,actualScore);
					return i+1;
				}
								
			}
			if (actualScore < Scores.get(seedString).getLast())
			{
				Scores.get(seedString).addLast(actualScore);
				return Scores.get(seedString).size();
			}
		}
		else
		{		
			LinkedList<Integer> newScore = new LinkedList<Integer>();
			newScore.add(actualScore);
			Scores.put(seedString,newScore);
			return 1;
		}
		return -1;
	}	
		
	/**
	 * Method for handling the update of a crashed player
	 * 
	 * @param p 	The Player
	 */
	public void checkCrashStatus(Player p) {
		if (!crashing) 
		{
			crashing = true;
			wait = 0;
		} 
		else if (crashing && wait < waitTime) 
		{
			wait++;
		} 
		else 
		{
			respawnLocation = respawnLocations.remove();
			respawnLocations.clear();

			int tSpawnX = (int) (respawnLocation[0] / tmapbg.getTileWidth());
			int tSpawnY = (int) (respawnLocation[1] / tmapbg.getTileHeight());

			Tile tSpawn = tmapbg.getTile(tSpawnX, tSpawnY);
			while (tSpawn.getCharacter() != '.') {
				tSpawn = tmapbg.getTile(tSpawnX, tSpawnY++);
			}
			respawnLocation[0] = (float) (tSpawn.getXC() + 10);
			respawnLocation[1] = (float) (tSpawn.getYC() + 10);
			p.setPosition(respawnLocation[0], respawnLocation[1]);
			respawnLocations.add(respawnLocation);

			crashed = false;
			crashing = false;
			
			p.show();
		}
	}
	/**
	 * Method for calculating the current super charge level
	 * 
	 * @param p 	The Player
	 */
	public void calculateSuperChargedState(Player p) {
		if (chargeMeter == 0) 
		{
			superCharged = false;
		}
		if (chargeMeter > 0) 
		{
			chargeMeter--;
		}

	}
	/**
	 * Method for calculating the current speed of the player
	 * 
	 * @param p 	The Player
	 */
	public void calculateNewPlayerSpeed(Player p) {

		p.setAnimationSpeed(1.0f);

		float currentMaxSpeed;
		if (superCharged && boost) 
		{
			currentMaxSpeed = superChargedMaxSpeed;
			p.setVelocityY(p.getVelocityY() + 0.07f);
		} 
		else if (boost || superCharged) 
		{
			currentMaxSpeed = boostedMaxSpeed;
			p.setVelocityY(p.getVelocityY() + 0.055f);
		} 
		else 
		{
			currentMaxSpeed = maxSpeed;
			p.setVelocityY(p.getVelocityY() + 0.045f);
		}

		if (turnLeft || turnRight) 
		{
			if (superCharged && boost) 
			{
				p.setVelocityY(p.getVelocityY() - 0.072f);
			} 
			else if (superCharged || boost) 
			{
				p.setVelocityY(p.getVelocityY() - 0.0565f);
			} 
			else 
			{
				p.setVelocityY(p.getVelocityY() - 0.046f);
			}
		}

		if (turnRight && !turnLeft) 
		{
			p.setAnimationSpeed(1.5f);
			if (p.getVelocityX() < 0.15f) 
			{
				p.setVelocityX(p.getVelocityX() + 0.012f);
			} 
			else if (player.getVelocityX() < 0.3f) 
			{
				p.setVelocityX(p.getVelocityX() + 0.009f);
			} 
			else 
			{
				p.setVelocityX(p.getVelocityX() + 0.005f);
			}
		}

		if (turnLeft && !turnRight) 
		{
			p.setAnimationSpeed(1.5f);
			
			if (p.getVelocityX() > -0.15f) 
			{
				p.setVelocityX(p.getVelocityX() - 0.012f);
			} 
			else if (p.getVelocityX() > -0.3f) 
			{
				p.setVelocityX(p.getVelocityX() - 0.009f);
			} 
			else 
			{
				p.setVelocityX(p.getVelocityX() - 0.005f);
			}
		}

		if (p.getVelocityY() < 0) 
		{
			p.setVelocityY(0);
		}

		if (p.getVelocityY() > currentMaxSpeed) 
		{
			p.setVelocityY(currentMaxSpeed);
		}
		if (p.getVelocityX() > currentMaxSpeed) 
		{
			p.setVelocityX(currentMaxSpeed);
		}
		if (p.getVelocityX() < -currentMaxSpeed) 
		{
			p.setVelocityX(-currentMaxSpeed);
		}
	}
	/**
	 * Method for calculating the current player animation
	 * 
	 * @param p 	The Player
	 */
	public void calculateNewPlayerAnimation(Player p) {

		if (p.getVelocityX() < -0.1f) 
		{
			p.setAnimSkislope();
			p.setDirectionLeft(true);
			p.setDirectionRight(false);
		} 
		else if (p.getVelocityX() > 0.1f) 
		{
			p.setAnimSkislope();
			p.setDirectionLeft(false);
			p.setDirectionRight(true);
		} 
		else 
		{
			p.setAnimSkidown();
			p.setDirectionLeft(false);
			p.setDirectionRight(false);
		}
	}
	/**
	 * Method for recording the players current position in the re-spawn list
	 * 
	 * @param p 	The Player
	 */
	public void recordPlayerPosition(Player p) {

		float[] pos = respawnLocations.peekLast();

		if (respawnLocations.size() < noR) 
		{
			if (pos != null && pos[0] == p.getX() && pos[1] == p.getY())
				return;
			respawnLocation[0] = p.getX();
			respawnLocation[1] = p.getY();
			respawnLocations.add(respawnLocation.clone());

		} 
		else 
		{
			if (pos[0] == p.getX() && pos[1] == p.getY())
				return;
			respawnLocation[0] = p.getX();
			respawnLocation[1] = p.getY();
			respawnLocations.add(respawnLocation.clone());
			respawnLocations.remove();
		}
	}
	/**
	 * Method that checks and handles player collisions with the edge of the screen
	 * 
	 * @param p       The Player
	 * @param tmap    The TileMap to check
	 */
	public void handlePlayerScreenEdge(Player p, TileMap tmap) {

		if (p.getY() + p.getHeight() > tmap.getPixelHeight()) 
		{
			p.setY(tmap.getPixelHeight() - p.getHeight() - 1);
			p.setVelocityY(-p.getVelocityY() / 2);
		}
		if (p.getY() < 0) 
		{
			p.setY(0 + 20);
			p.setX(tmap.getPixelWidth() / 2);
			p.setVelocityY(0);
			p.setVelocityX(0);
		}
		if ((p.getX() + p.getWidth()) > tmap.getPixelWidth()) 
		{
			p.setX(tmap.getPixelWidth() - p.getWidth() - 1);
			p.setVelocityY(p.getVelocityY() - 0.001f);
		}
		if (p.getX() < 0) 
		{
			p.setX(0 + 1);
			p.setVelocityY(p.getVelocityY() - 0.001f);
		}
	}
	/**
	 * Method for checking and responding to any tile collisions by the Player
	 * 
	 * @param p       The Player
	 * @param tmap    The TileMap to check
	 */
	public void checkPlayerTileCollision(Player p, TileMap tmap) {

		Tile t;

		t = tmap.getTile((int) (p.getTLx() / tmap.getTileWidth()), (int) (p.getTLy() / tmap.getTileHeight()));

		if (t != null && t.getCharacter() != '.') 
		{
			if (isPointInBox(p.getTLx(), p.getTLy(), t.getTLx(), t.getTLy(), t.getBRx(), t.getBRy())) 
			{
				if (t.getCharacter() == 't') 
				{
					p.setVelocityX((float) (0.90 * p.getVelocityX()));
					p.setVelocityY((float) (0.95 * p.getVelocityY()));
				}
				if (t.getCharacter() == 's') 
				{
					p.setVelocityY((float) (1.05 * p.getVelocityY()));
					chargeMeter = +500;
					superCharged = true;
					totalScore += 5000;
				}
			}
		}

		t = tmap.getTile((int) (p.getTRx() / tmap.getTileWidth()), (int) (p.getTRy() / tmap.getTileHeight()));

		if (t != null && t.getCharacter() != '.') 
		{
			if (isPointInBox(p.getTRx(), p.getTRy(), t.getTLx(), t.getTLy(), t.getBRx(), t.getBRy())) 
			{
				if (t.getCharacter() == 't') 
				{
					p.setVelocityX((float) (0.90 * p.getVelocityX()));
					p.setVelocityY((float) (0.95 * p.getVelocityY()));
				}
				if (t.getCharacter() == 's') 
				{
					p.setVelocityY((float) (1.05 * p.getVelocityY()));
					chargeMeter = +500;
					superCharged = true;
				}
			}
		}

		t = tmap.getTile((int)(p.getBLx() / tmap.getTileWidth()), (int)(p.getBLy() / tmap.getTileHeight()));

		if (t != null && t.getCharacter() != '.') 
		{
			if (isPointInBox(p.getBLx(), p.getBLy(), t.getTLx(), t.getTLy(), t.getBRx(), t.getBRy())) 
			{
				if (t.getCharacter() == 't') 
				{
					p.setAnimSkicrash();
					p.setVelocity(0, 0);
					p.playOoft();
					crashed = true;
					chargeMeter = 0;
				}
				if (t.getCharacter() == 'r') 
				{
					p.setAnimSkicrash();
					p.setVelocity(0, 0);
					p.playOoft();
					crashed = true;
					chargeMeter = 0;
				}
				if (t.getCharacter() == 's') 
				{
					p.setAnimSkicrash();
					p.setVelocity(0, 0);
					p.playOoft();
					crashed = true;
					chargeMeter = 0;
				}
				if (t.getCharacter() == 'f' || t.getCharacter() == 'g') 
				{
					p.setAnimSkicelebrate();
					p.setVelocity(0, 0);
					completed = true;
				}
			}
		}

		t = tmap.getTile((int)(p.getBRx() / tmap.getTileWidth()), (int)(p.getBRy() / tmap.getTileHeight()));

		if (t != null && t.getCharacter() != '.') 
		{
			if (isPointInBox(p.getBRx(), p.getBRy(), t.getTLx(), t.getTLy(), t.getBRx(), t.getBRy())) 
			{
				if (t.getCharacter() == 't') 
				{
					p.setAnimSkicrash();
					p.setVelocity(0, 0);
					p.playOoft();
					crashed = true;
					chargeMeter = 0;
				}
				if (t.getCharacter() == 'r') 
				{
					p.setAnimSkicrash();
					p.setVelocity(0, 0);
					p.playOoft();
					crashed = true;
					chargeMeter = 0;
				}
				if (t.getCharacter() == 's') 
				{
					p.setAnimSkicrash();
					p.setVelocity(0, 0);
					p.playOoft();
					crashed = true;
					chargeMeter = 0;
				}
				if (t.getCharacter() == 'f') 
				{
					p.setAnimSkicrash();
					p.setVelocity(0, 0);
					completed = true;
				}
			}
		}
	}
	
	/**
	 * Method for checking the position and direction of the monster while
	 * patrolling
	 * 
	 * @param m The Monster
	 */
	public void checkMonsterPosition(Monster m) {
		
		if (m.getX() < m.getPatrolX() - monsterPatrolDistance) 
		{
			m.setVelocityX(-m.getVelocityX());
			m.setDirectionRight(true);
			m.setDirectionLeft(false);
		}
		if (m.getX() > m.getPatrolX() + monsterPatrolDistance) 
		{
			m.setVelocityX(-m.getVelocityX());
			m.setDirectionLeft(true);
			m.setDirectionRight(false);
		}
	}
	/**
	 * Method for checking if the player is in the vision range of the Monster
	 * 
	 * @param m The Monster
	 * @param p The Player
	 */
	public void lookForPlayer(Monster m, Player p) {

		if (m.isDirectionRight()) 
		{
			if (isPointInBox(p.getTLx(), p.getTLy(), m.getTRx(), m.getTRy() - monsterSightOffset,
					m.getBRx() + monsterSightRange, m.getBRy() + monsterSightOffset) && (!crashed || !crashing)) 
			{
				activateHuntingMode(m);
			}
			if (isPointInBox(p.getBLx(), p.getBLy(), m.getTRx(), m.getTRy() - monsterSightOffset,
					m.getBRx() + monsterSightRange, m.getBRy() + monsterSightOffset) && (!crashed || !crashing)) 
			{
				activateHuntingMode(m);
			}
		}
		if (m.isDirectionLeft()) 
		{
			if (isPointInBox(p.getTRx(), p.getTRy(), m.getTLx() - monsterSightRange, m.getTLy() - monsterSightOffset,
					m.getBLx(), m.getBLy() + monsterSightOffset) && (!crashed || !crashing)) 
			{
				activateHuntingMode(m);

			}
			if (isPointInBox(p.getBRx(), p.getBRy(), m.getTLx() - monsterSightRange, m.getTLy() - monsterSightOffset,
					m.getBLx(), m.getBLy() + monsterSightOffset) && (!crashed || !crashing)) 
			{
				activateHuntingMode(m);
			}
		}
	}
	/**
	 * Method for putting a monster into hunter mode
	 * 
	 * @param m The Monster
	 */
	public void activateHuntingMode(Monster m) {
		m.setPatrolling(false);
		m.setDirectionLeft(false);
		m.setDirectionRight(false);
		m.setAnimChasing();
		m.playRoar();
		m.setHunting(true);
	}
	/**
	 * Method for calculating the fastest path to the player
	 * 
	 * @param m The Monster
	 * @param p The Player
	 */
	public void chaseThePlayer(Monster m, Player p) {
		calculateNewTrajectory(m, monsterChaseSpd, calculateAngle(m.getCenterX(), m.getCenterY(), p.getCenterX(), p.getCenterY()));
	}
	/**
	 * Method for checking if the player has been caught
	 * 
	 * @param m The Monster
	 * @param p The Player
	 */
	public void didWeGetThem(Monster m, Player p) {

		if (isPointInBox(m.getTLx(), m.getTLy(), p.getTLx(), p.getTLy(), p.getBRx(), p.getBRy())
				|| isPointInBox(m.getTRx(), m.getTRy(), p.getTLx(), p.getTLy(), p.getBRx(), p.getBRy())
				|| isPointInBox(m.getBLx(), m.getBLy(), p.getTLx(), p.getTLy(), p.getBRx(), p.getBRy())
				|| isPointInBox(m.getBRx(), m.getBRy(), p.getTLx(), p.getTLy(), p.getBRx(), p.getBRy())
				|| (calculateDistance(m.getCenterX(), m.getCenterY(), p.getCenterX(),
						p.getCenterY()) < ((m.getWidth() / 2) + (p.getWidth() / 2)))) 
		{
			m.setVelocity(0, 0);
			m.setAnimEating();
			m.getAnimation().start();
			p.playScream();
			p.hide();
			caught = true;
			chargeMeter = 0;
			totalScore -= 5000;
			crashing = false;
			crashed = true;
		}
	}
	/**
	 * Method for working out if they should continue to chase the player
	 * 
	 * @param m The Monster
	 */
	public void areTheyWorthIt(Monster m) {

		if ((calculateDistance(m.getPatrolX(), m.getPatrolY(), m.getX(), m.getY()) > 250 || caught)
				&& m.getAnimation().hasLooped()) 
		{
			m.setHunting(false);
			caught = false;
			m.setAnimWalking();

			if (m.getCenterX() < m.getPatrolX()) 
			{
				m.setDirectionRight(true);
			}
			if (m.getCenterX() >= m.getPatrolX()) 
			{
				m.setDirectionLeft(true);
			}

			m.setReturning(true);
		}
	}
	/**
	 * Method for directing the monster back to their patrol
	 * 
	 * @param m The Monster
	 */
	public void walkHome(Monster m) {

		calculateNewTrajectory(m, monsterWalkSpd, calculateAngle(m.getX(), m.getY(), m.getPatrolX(), m.getPatrolY()));
	}
	/**
	 * Method for handling the return of the monster to their patrol
	 * 
	 * @param m The Monster
	 */
	public void areWeHomeYet(Monster m) {
		
		if (m.getY() < m.getPatrolY() + 1 && m.getY() > m.getPatrolY() - 1) 
		{
			m.setX(m.getPatrolX());
			m.setY(m.getPatrolY());
			m.setReturning(false);
			
			if (m.isDirectionRight())
			{
				m.setVelocityX(monsterWalkSpd);
				m.setVelocityY(0);
			}
			if (m.isDirectionLeft()) 
			{
				m.setVelocityX(-monsterWalkSpd);
				m.setVelocityY(0);
			}
			m.setPatrolling(true);
		}
	}

	/**
	 * Method for checking and handling if any coins are collected
	 * 
	 * @param s    The player
	 * @param tmap The list of all coins
	 */
	public void checkCoinCollision(Sprite s, ArrayList<Coin> c) {

		checkCoinCollision(s.getTLx(), s.getTLy(), c);
		checkCoinCollision(s.getTRx(), s.getTRy(), c);
		checkCoinCollision(s.getBLx(), s.getBLy(), c);
		checkCoinCollision(s.getBRx(), s.getBRy(), c);

	}
	/**
	 * Method for checking if an x,y coordinate lies inside of any visible coin
	 * borders
	 * 
	 * @param x    The x coordinate
	 * @param y    The y coordinate
	 * @param tmap The list of all coins
	 */
	public void checkCoinCollision(float x, float y, ArrayList<Coin> coins) {
		
		for (int i = 0; i < coins.size(); i++) 
		{
			Sprite c = coins.get(i);
			
			if (c.isVisible()) 
			{
				if (isPointInBox(x, y, c.getTLx(), c.getTLy(), c.getBRx(), c.getBRy())) 
				{
					if ((calculateDistance(x, y, c.getCenterX(), c.getCenterY())) < c.getRadius()) 
					{
						coins.remove(i).playSoundEffect();
						chargeMeter = +300;
						superCharged = true;
						totalScore += 2000;
					}
				}
			}
		}
	}
	
	/**
	 * Method for checking the collision of any visible SnowBalls with any visible Monsters
	 * 
	 * @param snowballs		The full list of SnowBalls
	 * @param monsters		The full list of Monsters
	 */
	public void checkForSnowballHit(SnowBall s, ArrayList<Monster> monsters) {
		
		if (s.isVisible())
		{
			for(Monster m: monsters) 
			{
				if (m.isVisible()) 
				{
					if ((isPointInBox(s.getTLx(), s.getTLy(), m.getTLx(), m.getTLy(), m.getBRx(), m.getBRy()))
					|| (isPointInBox(s.getTRx(), s.getTRy(), m.getTLx(), m.getTLy(), m.getBRx(), m.getBRy()))
					|| (isPointInBox(s.getBLx(), s.getBLy(), m.getTLx(), m.getTLy(), m.getBRx(), m.getBRy()))
					|| (isPointInBox(s.getBRx(), s.getBRy(), m.getTLx(), m.getTLy(), m.getBRx(), m.getBRy()))) 
					{
						if(calculateDistance(s.getCenterX(), s.getCenterY(), m.getCenterX(), m.getCenterY()) < s.getWidth()*2) 
							{
								s.getSoundEffect().setFinished(true);
								s.hide();
								m.setVelocity(0, 0);
								m.setAnimStunned();
								m.setStunTimer(m.getStunTimer()+monsterStunDuration);
								m.setStunned(true);		
								Sound splat = new Sound("sounds/Splat.wav", false, false);
								splat.start();
								totalScore += 2500;
							}
						
					}
				}
			}
		}
	}
	
	/**
	 * Method for loading the Coins in this instance
	 * 
	 * @param tamp The TileMap containing the locations of all the Coins
	 * @param list The list of all Coins
	 * @param l    The character that associates with Coins
	 */
	public void loadCoins(TileMap tmap, ArrayList<Coin> list, char l) {

		for (int row = 0; row < tmap.getMapHeight(); row++) 
		{
			for (int col = 0; col < tmap.getMapWidth(); col++) 
			{
				if (tmap.getTileChar(col, row) == l) 
				{
					Coin c = new Coin();
					c.setX((tmap.getTileXC(col, row)) + (tmap.getTileWidth() / 2) - (c.getWidth() / 2));
					c.setY((tmap.getTileYC(col, row)) + (tmap.getTileHeight() / 2) - (c.getHeight() / 2));
					c.hide();
					list.add(c);
					tmap.setTileChar('.', col, row);
				}
			}
		}
	}
	/**
	 * Method for loading the Monsters in this instance
	 * 
	 * @param tamp The TileMap containing the locations of all the Monsters
	 * @param list The list of all Monsters
	 * @param l    The character that associates with Monsters
	 */
	public void loadMonsters(TileMap tmap, ArrayList<Monster> list, char l) {

		for (int r = 0; r < tmap.getMapHeight(); r++) 
		{
			for (int c = 0; c < tmap.getMapWidth(); c++) 
			{
				if (tmap.getTileChar(c, r) == l) 
				{
					Monster s = new Monster();
					s.setX((tmap.getTileXC(c, r)) + (tmap.getTileWidth() / 2) - (s.getWidth() / 2));
					s.setY((tmap.getTileYC(c, r)) + (tmap.getTileHeight() / 2) - s.getHeight());
					s.setPatrol(s.getX() + (s.getWidth() / 2), s.getY());
					s.hide();
					list.add(s);
					tmap.setTileChar('.', c, r);
				}
			}
		}
	}
	/**
	 * Method for setting up initial scores on seed 4321
	 * 
	 * @param scores		The Hashtable containing the seed keys and linked list of ordered scores.
	 */
	public void LoadPresetSeed4321Scores(Hashtable<String, LinkedList<Integer>> scores) {
		
		String seed = "4321";
		
		LinkedList<Integer> presetScores = new LinkedList<Integer>();
		
		presetScores.add(0, 1274);
		presetScores.add(1, 1003);
		presetScores.add(2, 923);
		presetScores.add(3, 865);
		presetScores.add(4, 709);
		
		scores.put(seed, presetScores);
		
	}
	/**
	 * Method for setting up initial direction and movement of Monsters in the level
	 * 
	 * @param monsters		The list of all Monsters
	 */
	public void InitialiseMonsters(ArrayList<Monster> monsters) {
		
		for (Monster m : monsters) 
		{
			m.setPatrolling(true);
			if (m.getX() < screenWidth / 2) 
			{
				m.setVelocityX(monsterWalkSpd);
				m.setDirectionRight(true);
			} 
			else 
			{
				m.setVelocityX(-monsterWalkSpd);
				m.setDirectionLeft(true);
			}
		}
	}
	/**
	 * Method for setting up all the relevant tiles hit box area
	 * 
	 * @param tmap		The tile map used for collision checking
	 */
	public void setAllTileHitBoxes(TileMap tmap) {
		
		for (int row = 0; row < tmap.getMapHeight(); row++) 
		{
			for (int col = 0; col < tmap.getMapWidth(); col++) 
			{
				Tile t = tmap.getTile(col, row);
				char c = t.getCharacter();

				if (c != '.') 
				{
					switch (c) 
					{
					case 't':
						t.setTLx(t.getXC() + (tmap.getTileWidth() / 4));
						t.setTLy(t.getYC() + (tmap.getTileHeight() / 2));
						t.setBRx(t.getXC() + tmap.getTileWidth() - (tmap.getTileWidth() / 4));
						t.setBRy(t.getYC() + tmap.getTileHeight());
						break;
					case 'r':
						t.setTLx(t.getXC() + (tmap.getTileWidth() / 4));
						t.setTLy(t.getYC() + (tmap.getTileHeight() / 4));
						t.setBRx(t.getXC() + tmap.getTileWidth() - (tmap.getTileWidth() / 4));
						t.setBRy(t.getYC() + tmap.getTileHeight() - (tmap.getTileHeight() / 4));
						break;
					case 's':
						t.setTLx(t.getXC() + (tmap.getTileWidth() / 4));
						t.setTLy(t.getYC() + (tmap.getTileHeight() / 4));
						t.setBRx(t.getXC() + tmap.getTileWidth() - (tmap.getTileWidth() / 4));
						t.setBRy(t.getYC() + tmap.getTileHeight() - (tmap.getTileHeight() / 4));
						break;
					case 'f':
						t.setTLx(t.getXC());
						t.setTLy(t.getYC() + (tmap.getTileHeight() / 5));
						t.setBRx(t.getXC() + tmap.getTileWidth());
						t.setBRy(t.getYC() + tmap.getTileHeight() - (tmap.getTileHeight() / 5));
						break;
					case 'g':
						t.setTLx(t.getXC());
						t.setTLy(t.getYC() + (tmap.getTileHeight() / 5));
						t.setBRx(t.getXC() + tmap.getTileWidth());
						t.setBRy(t.getYC() + tmap.getTileHeight() - (tmap.getTileHeight() / 5));
						break;

					default:
						break;
					}
				}
			}
		}
	}

	/**
	 * Method for calculating if an x, y point falls within a defined quadrilateral
	 * 
	 * @param tX   The x coordinate of the Origin
	 * @param tY   The y coordinate of the Origin
	 * @param bTLx The x coordinate of the Top Left corner of the box
	 * @param bTLy The y coordinate of the Top Left corner of the box
	 * @param bBRx The x coordinate of the Bottom Right corner of the box
	 * @param bBRy The y coordinate of the Bottom Right corner of the box
	 * 
	 * @return True if the point lies within the boundaries of the box
	 */
	public boolean isPointInBox(float tX, float tY, float bTLx, float bTLy, float bBRx, float bBRy) {

		if ((tX > bTLx && tX < bBRx) && (tY > bTLy && tY < bBRy)) 
		{
			return true;
		} 
		else 
		{
			return false;
		}
	}
	/**
	 * Method for calculating the directionally specific angle between an origin and
	 * a target x,y coordinate relative to that origin
	 * 
	 * @param ox The x coordinate of the Origin
	 * @param oy The y coordinate of the Origin
	 * @param ox The x coordinate of the Target
	 * @param oy The y coordinate of the Target
	 * 
	 * @return The float returned is the angle starting at the 3pm position on a
	 *         clock
	 */
	public float calculateAngle(float ox, float oy, float tx, float ty) {

		double dx = tx - ox;
		double dy = oy - ty;

		double inRads = Math.atan2(dy, dx);

		if (inRads < 0) 
		{
			inRads = Math.abs(inRads);
		} 
		else 
		{
			inRads = 2 * Math.PI - inRads;
		}
		
		return (float) Math.toDegrees(inRads);
	}
	/**
	 * Method for calculating an x and y speed component for a Sprite based on a
	 * total speed and angle
	 * 
	 * @param s     The Sprite to apply the components to
	 * @param speed The the total speed
	 * @param angle The angle of direction
	 */
	public void calculateNewTrajectory(Sprite s, float speed, float angle) {
		
		Velocity current = new Velocity(speed, angle);
		s.setVelocityX((float) current.getdx());
		s.setVelocityY((float) current.getdy());
	}
	/**
	 * Method for calculating distance between two points
	 * 
	 * @param ox The x coordinate of the Origin
	 * @param oy The y coordinate of the Origin
	 * @param ox The x coordinate of the Target
	 * @param oy The y coordinate of the Target
	 * 
	 * @return The float returned is the distance from the origin to the target
	 */
	public float calculateDistance(float ox, float oy, float tx, float ty) {

		int dXsqr = (int) ((ox - tx) * (ox - tx));
		int dYsqr = (int) ((oy - ty) * (oy - ty));
		return (float) Math.sqrt(dXsqr + dYsqr);

	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_B)
			debugCollisions = !debugCollisions;

		if (key == KeyEvent.VK_ESCAPE)
			stop();

		if (key == KeyEvent.VK_S)
			if (!crashed && !crashing) {
				boost = true;
			}

		if (key == KeyEvent.VK_A)
			if (!crashed && !crashing) {
				turnLeft = true;
			}

		if (key == KeyEvent.VK_D)
			if (!crashed && !crashing) {
				turnRight = true;
			}

		if (key == KeyEvent.VK_C) {
			collidable = !collidable;
		}
		
		if (key == KeyEvent.VK_P) {
			paused = !paused;
		}
	}
	public void keyReleased(KeyEvent e) {

		int key = e.getKeyCode();

		switch (key) 
		{
		case KeyEvent.VK_ESCAPE:
			stop();
			break;
		case KeyEvent.VK_S:
			boost = false;
			break;
		case KeyEvent.VK_A:
			turnLeft = false;
			break;
		case KeyEvent.VK_D:
			turnRight = false;
			break;
		case KeyEvent.VK_C:
			break;
		case KeyEvent.VK_P:
			break;
		default:
			break;
		}
	}
	public void mouseClicked(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {


		if ((!crashed && !crashing) && snowBallCharge >= 500 && !accessingSnowBalls) 
		{

		snowBallCharge -= 500;	
			
		SnowBall s = new SnowBall((e.getX() - xo), (e.getY() - yo));

		s.setX(player.getCenterX() - (s.getWidth() / 2));
		s.setY(player.getCenterY() - (s.getHeight() / 2));

		calculateNewTrajectory(s, snowBallSpd, calculateAngle(s.getCenterX(), s.getCenterY(), s.getTargetX(), s.getTargetY()));

		s.playSoundEffect();
		snowballs.add(s);

		}
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
}
