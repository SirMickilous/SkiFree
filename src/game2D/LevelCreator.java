package game2D;
// Formatted
import java.awt.Image;
import java.util.*;

public class LevelCreator {

	static final int tnos = 15; 	// total number of segments
	static final int nos = 10;		// number of segments to be selected
	static final long testseed = 0; // seed for demo version
		
	long seed;	// seed parameter

	String location; 	// location of map segments
	int[] sv;			// holds the seed generated order of segments for level
	
	private Map<String, Image> imagemap = new HashMap<String, Image>(); // holds hash map of images for tile characters
		
	TileMap [] tmapFullList; 	// holds all potential map segments
	TileMap [] tmapLevelList; 	// holds the segments for level	
	TileMap tmap; 				// holds final generated Level map
	
    /**
     * Constructor for LevelCreator sets up the desired Level using a seed
     * 
     * @param s		The seed value
     * @param l		The location of the map segments and associated tile images
     */
	public LevelCreator(long s, String l) {
		
		this.seed = s;
		this.location = l;
		sv = new int[nos+2];
		tmapFullList = new TileMap[tnos+2];
		tmapLevelList = new TileMap[nos+2];
		
		if (seed == testseed) 
		{
			tmapLevelList = new TileMap[2];
			
			TileMap start = new TileMap();
			start.loadMap(location, "mapSeg0.txt");
			tmapLevelList[0] = start;
			
			TileMap finish = new TileMap();
			finish.loadMap(location, "mapSeg99.txt");
			tmapLevelList[1] = finish;
		
			compileFullMap(tmap, tmapLevelList);
		}
		else 
		{
			sv = createSeedValues(seed, nos, tnos);
			compileMapSegments(location, tmapFullList, tnos);
			compileLevelSegments(tmapFullList, tmapLevelList, sv);
			compileFullMap(tmap, tmapLevelList);
		}

	}
	
    /**
     * @return Final level generated from given seed
     */
	public TileMap getLevel() {
		return tmap;
	}

    /**
     * Creates a set of seed values and stores them in an integer array
     * 
     * @return int[] 	The array of tile segments selected for level
     * 
     * @param s			The seed value
     * @param nos		The number of random segments to be fitted in between start and finish tiles
     * @param tnos		The total number of segments available for selection
     */
	public int[] createSeedValues(long s, int nos, int tnos) {
		
		int[] seedValues = new int[nos+2];
		Random random = new Random();
		random.setSeed(s);
		seedValues[0] = 0;
		
		for (int i = 1; i <= nos; i++) 
		{
			int ran = 0;
			while (true) 
			{
				ran = random.nextInt(tnos);
				if (ran != 0) break;
			}
			seedValues[i] = ran;
		}
		seedValues[nos+1] = tnos+1;
		return seedValues;
	}
	
    /**
     * Creates the loaded list of all possible map segments
     * 
     * @param location		The location of the map segments
     * @param tmapFullList	The list to hold of all possible map segments
     * @param tnos			The total number of segments available for selection
     */
	public void compileMapSegments(String location, TileMap [] tmapFullList, int tnos) {
		
		TileMap start = new TileMap();
		start.loadMap(location, "mapSeg0.txt");
		tmapFullList[0] = start;
		
		for (int i=1; i<=tnos; i++) 
		{
			TileMap temp = new TileMap();
			temp.loadMap(location, "mapSeg"+i+".txt");
			tmapFullList[i] = temp;
		}
		
		TileMap finish = new TileMap();
		finish.loadMap(location, "mapSeg99.txt");
		tmapFullList[tnos+1] = finish;
		
	}
	
	/**
     * Creates the loaded list of level map segments in order
     * 
     * @param tmapFullList	The list of all possible map segments
     * @param tmapLevelList	The list to hold all of the map segments in level order for processing into final level
     * @param sv			The seed generated segment selection numbers
     */
	public void compileLevelSegments(TileMap [] tmapFullList, TileMap [] tmapLevelList, int[] sv) {
		
		for (int i=0; i<sv.length; i++) 
		{
			tmapLevelList[i] = tmapFullList[sv[i]];
		}
		
	}
	
	/**
     * Combines all the level map segments into one final tile map of the entire level.
     * 
     * @param tmap			The TileMap which will hold the final level
     * @param tmapLevelList	The list of all the map segments in level order
     */
	public void compileFullMap (TileMap tmap, TileMap [] tmapLevelList) {
		
		int height = 0;
		int width = 0;
		TileMap temp;	
		
		for (int i=0; i<tmapLevelList.length; i++) 
		{
		  temp = tmapLevelList[i];
		  int add = temp.getMapHeight();
		  height += add;
		}
		
		temp = tmapLevelList[1];
		width = temp.getMapWidth();
		int th = tmapLevelList[1].getTileHeight();
		int tw = tmapLevelList[1].getTileWidth();
		imagemap = tmapLevelList[1].getImageMap(); 
		
		
		tmap = new TileMap();
		tmap.newBlankMap(width, height, th, tw, imagemap);
		
		for (int i=0; i<tmapLevelList.length; i++) 
		{
			addTileMap(tmap,tmapLevelList[i], i);
		}
		
		this.tmap = tmap;
	}

	/**
     * Used to load each segment into the level map 
     * 
     * @param tmap		The TileMap which will hold the final level
     * @param t			The current segment to be added
     * @param p			The index of the current segment to be added, used to offset the copy position
     */
	private void addTileMap(TileMap tmap, TileMap t, int p) {
		
		char c;
		for (int col=0; col<t.getMapWidth(); col++) {
			int tRow = 0;
			for (int row=t.getMapHeight()*p; row<t.getMapHeight()*(p+1); row++) {
				c = t.getTileChar(col, tRow);
				tmap.setTileChar(c, col, row);
				tRow++;
			}
		}
		
		System.out.println("done");
		
	}
		
}
