package game2D;

// import java.util.Hashtable;
// import java.util.LinkedList;

public class Unused {
	
	public Unused() {
		
		//<<<GAME>>>
		
		//<<<START UP>>>
		
//		Animation skibeginner;
//		Animation skibeginnercrash;
		
//		ArrayList<Sprite> skibeginners;
		
//		skibeginner = new Animation();
//		skibeginner.loadAnimationFromSheet("images/skibeginner.png", 3, 1, 120);
//		skibeginnercrash = new Animation();
//		skibeginnercrash.loadAnimationFromSheet("images/skibeginnercrash.png", 1, 1, 60);
		
//		skibeginners = new ArrayList<Sprite>();
		
//		loadSprites(tmapbg, skibeginners, skibeginner, 'e');
//		setInitialVelocity(skibeginners);
		
		
		// <<<DRAW>>>
		
//		for (Sprite s : skibeginners) {
//		s.setOffsets(xo, yo);
//		if (((int) (s.getY() + yo) > 0) && ((int) (s.getY() + yo) < screenHeight)) {
//			s.show();
//			s.setActivated();
//			s.draw(g);
//		} else {
//			s.hide();
//		}
//
//	}
		
		//<<<UPDATE>>>
		
//		for (Sprite skier : skibeginners) {
//		if (skier.isActivated()) {
//			handleScreenBounce(skier, tmapbg);
//			handleTileBounce(skier, tmapbg);
//			skier.update(elapsed);
//		}
//	}

		
		//<<<GENERAL METHODS>>>
		
//		public void handleScreenBounce(Sprite s, TileMap tmap) {
//		
//		if (s.getY() + s.getHeight() > tmap.getPixelHeight()) {
//			// Put the player back on the map 1 pixel above the bottom
//			s.setY(tmap.getPixelHeight() - s.getHeight() - 1);
//			s.setVelocityY(-s.getVelocityY() / 2);
//		}
//		if (s.getX() + s.getWidth() > tmap.getPixelWidth()) {
//			// Put the player back on the map 1 pixel above the bottom
//			s.setX(tmap.getPixelWidth() - s.getWidth() - 1);
//			s.setVelocityX(-s.getVelocityX());
//		}
//		if (s.getX() < 0) {
//
//			s.setX(0 + 1);
//			s.setVelocityX(-s.getVelocityX());
//		}
//
//	}
		
//		public void handleTileBounce(Sprite s, TileMap tmap) {
//			actionSkierMovement(s, getFullTileCollision(s, tmap));
//		}
		
//		public char[] getFullTileCollision(Sprite s, TileMap tmap) {
//		
//		float dsx = s.getWidth();
//		float dsy = s.getHeight();
//
//		float sxTL = s.getX();
//		float syTL = s.getY();
//		float sxTR = s.getX() + dsx;
//		float syTR = s.getY();
//		float sxBL = s.getX();
//		float syBL = s.getY() + dsy;
//		float sxBR = s.getX() + dsx;
//		float syBR = s.getY() + dsy;
//
//		// Find out how wide and how tall a tile is
//		float tileWidth = tmap.getTileWidth();
//		float tileHeight = tmap.getTileHeight();
//
//		// Divide the sprites x coordinate by the width of a tile, to get
//		// the number of tiles across the x axis that the sprite is positioned at, same
//		// applies to the y coordinate
//		// top left
//
//		char[] rtn = new char[4];
//
//		char tTL = checkFullCollision(tmap, s, tileWidth, tileHeight, sxTL, syTL);
//		char tTR = checkFullCollision(tmap, s, tileWidth, tileHeight, sxTR, syTR);
//		char tBL = checkFullCollision(tmap, s, tileWidth, tileHeight, sxBL, syBL);
//		char tBR = checkFullCollision(tmap, s, tileWidth, tileHeight, sxBR, syBR);
//
//		rtn[0] = tTL;
//		rtn[1] = tTR;
//		rtn[2] = tBL;
//		rtn[3] = tBR;
//
//		return rtn;
//	}
//
//	public char checkFullCollision(TileMap t, Sprite s, float tileW, float tileH, float sx, float sy) {
//
//		int xtile = (int) (sx / tileW);
//		int ytile = (int) (sy / tileH);
//		Tile tile = t.getTile(xtile, ytile);
//
//		char c = tile.getCharacter();
//		char rtn = '.';
//
//		if (c != '.') {
//			if (c == 't') {
//				rtn = 't';
//			}
//			if (c == 's') {
//				rtn = 's';
//
//			}
//			if (c == 'r') {
//				rtn = 'r';
//
//			}
//			if (c == 'f') {
//				rtn = 'f';
//			}
//		}
//		return rtn;
//	}
		
//		public void actionSkierMovement(Sprite s, char[] chars) {
		//
//				boolean temp[][] = new boolean[2][2];
//				int r = 0;
//				int c = 0;
//				for (char l : chars) {
//					if (l != '.') {
//						temp[r][c] = true;
//					} else {
//						temp[r][c] = false;
//					}
//					if (r < 1) {
//						r++;
//					} else {
//						c++;
//						r = 0;
//					}
//				}
		//
		//
//				if (temp[0][0] == false && temp[0][1] == false 
//				 && temp[1][1] == true && temp[1][1] == true && s.isMoveMade() == false) {s.setVelocityY(0); s.setMoveMade(true);}
		//
//				if (temp[0][0] == false && temp[0][1] == false 
//				 && temp[1][1] == true && temp[1][1] == false && s.isMoveMade() == false) {s.setVelocityY(0); s.setVelocityX(0.12f); s.setMoveMade(true);}
//		                                                                                                                           
//				if (temp[0][0] == false && temp[0][1] == false                                                                     
//				 && temp[1][1] == false && temp[1][1] == true && s.isMoveMade() == false) {s.setVelocityY(0); s.setVelocityX(-0.12f); s.setMoveMade(true);}
//				
//				if (temp[0][0] == false && temp[0][1] == false 
//				 && temp[1][1] == false && temp[1][1] == false && s.isMoveMade() == false) {s.setVelocityY(0.08f); s.setMoveMade(true);}
		//
//				if (temp[0][0] == true && temp[0][1] == false 
//				 && temp[1][1] == false && temp[1][1] == false && s.isMoveMade() == false) {s.setVelocityY(0.08f); s.setVelocityX(0.12f); s.setMoveMade(true);}
		//
//				if (temp[0][0] == false && temp[0][1] == true 
//			     && temp[1][1] == false && temp[1][1] == false && s.isMoveMade() == false) {s.setVelocityY(0.08f); s.setVelocityX(-0.12f); s.setMoveMade(true);}
		//
//				if (temp[0][0] == true && temp[0][1] == false 
//			     && temp[1][1] == true && temp[1][1] == false && s.isMoveMade() == false) {s.setVelocityY(0.08f); s.setVelocityX(0.12f); s.setMoveMade(true);}
		//
//				if (temp[0][0] == false && temp[0][1] == true 
//				 && temp[1][1] == false && temp[1][1] == true && s.isMoveMade() == false) {s.setVelocityY(0.08f); s.setVelocityX(-0.12f); s.setMoveMade(true);}
		//
//				if (temp[0][0] == true && temp[0][1] == false 
//				 && temp[1][1] == true && temp[1][1] == true && s.isMoveMade() == false) {s.setVelocityY(0); s.setVelocityX(0.12f); s.setMoveMade(true);}
		//
//				if (temp[0][0] == false && temp[0][1] == true 
//				 && temp[1][1] == true && temp[1][1] == true && s.isMoveMade() == false) {s.setVelocityY(0); s.setVelocityX(-0.12f); s.setMoveMade(true);}
//				
//				if (s.isMoveMade() == false) {
//					s.setVelocity(0, 0);
//				}
//				
//				s.setMoveMade(false);
		//
//			}

		//<<<SPRITE>>>
		
		
		//<<<VARIABLES>>>
		
//	    private boolean activated = false;
//	    private boolean moveMade = false;
		
		//<<<GETS&SETS>>>
		
//	    public void setActivated () {
//    	this.activated = true;
//    }
//    
//    public boolean isActivated() {
//    	return this.activated;
//    }
//    
//    public void setMoveMade(boolean b) {
//    	this.moveMade = b;
//    }
//    
//    public boolean isMoveMade() {
//    	return this.moveMade;
//    }

	}

}
