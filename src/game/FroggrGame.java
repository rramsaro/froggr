package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import sprites.Fly;
import sprites.Lane;
import sprites.MovingObject;
import sprites.Platform;
import sprites.Player;
import sprites.Sprite;
import sprites.Vehicle;
import util.SoundEffect;

/**
 * This is a demo for a clone of the classic game "Frogger".
 * This is the main game class that processes the graphics, sound
 * and logic.
 * 
 * @author Raj Ramsaroop, Greg Westerfield, Jr.
 * @version 0.1
 * 
 */
public class FroggrGame extends Canvas implements Runnable, KeyListener {

	/**
	 * The player Sprite.
	 */
	private Player player;

	/**
	 * The number of lives the player starts with.
	 */
	private int startingLives = 3;

	/**
	 * The input class checks which keys the user is pressing on the keyboard.
	 */
	private Input input = new Input();

	/**
	 * Tracks how many fly's Froggr has consumed.
	 */
	public static int flysConsumed = 0;

	/**
	 * Variable to keep track of the player's score.
	 */
	private int score = 0;

	/**
	 * If the game is in a paused state.
	 */
	private boolean paused;

	/**
	 * The nextPointsPosition keeps track of the next YPos the user must reach
	 * to gain NEW_LANE_POINTS.
	 */
	private int nextPointsPosition = 600;

	/**
	 * Points that are earned when a fly is consumed.
	 */
	private final int CONSUME_FLY_BONUS = 100;

	/**
	 * Points that are earned when entering a lane for the first time.
	 */
	private final int NEW_LANE_POINTS = 25;

	/**
	 * ArrayList of Lane Sprites.
	 */
	private ArrayList<Lane> lanes = new ArrayList<Lane>();

	/**
	 * ArrayList of Vehicle Sprites.
	 */
	private ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();

	/**
	 * ArrayList of Platform Sprites.
	 */
	private ArrayList<Platform> platforms = new ArrayList<Platform>();

	/**
	 * ArrayList of Fly Sprites.
	 */
	private ArrayList<Fly> flys = new ArrayList<Fly>();

	/**
	 * Boolean for whether the player has lost the game or not.
	 */
	private static boolean gameOver;

	/**
	 * Boolean for whether the player has won the game or not.
	 */
	private static boolean gameWon;

	/**
	 * Regeneration rate for platforms in first water lane.
	 */
	private final static int FIRST_WATER_LANE_REGENERATION = 325;

	/**
	 * Regeneration rate for platforms in second water lane.
	 */
	private final static int SECOND_WATER_LANE_REGENERATION = 225;

	/**
	 * Regeneration rate for platforms in third water lane.
	 */
	private final static int THIRD_WATER_LANE_REGENERATION = 225;

	/**
	 * Regeneration rate for platforms in fourth water lane.
	 */
	private final static int FOURTH_WATER_LANE_REGENERATION = 325;

	/**
	 * Regeneration rate for platforms in fifth water lane.
	 */
	private final static int FIFTH_WATER_LANE_REGENERATION = 225;

	/**
	 * Regeneration rate for vehicles in the first road lane.
	 */
	private final static int FIRST_ROAD_LANE_REGENERATION = 175;

	/**
	 * Regeneration rate for the vehicles in the second road lane.
	 */
	private final static int SECOND_ROAD_LANE_REGENERATION = 225;

	/**
	 * Regeneration rate for the vehicles in the third road lane.
	 */
	private final static int THIRD_ROAD_LANE_REGENERATION = 350;

	/**
	 * Regeneration rate for the vehicles in the fourth road lane.
	 */
	private final static int FOURTH_ROAD_LANE_REGENERATION = 250;

	/**
	 * Width of the game canvas in pixels.
	 */
	public static final int GAME_WIDTH = 500;

	/**
	 * Height of the game canvas in pixels.
	 */
	public static final int GAME_HEIGHT = 700;

	/**
	 * Height of the lane in pixels.
	 */
	public static final int LANE_HEIGHT = 50;

	/**
	 * Number of lanes in the game.
	 */
	public static final int NUMBER_OF_LANES = 13;

	/**
	 * Background color of the game canvas.
	 */
	public static final Color FOREGROUND_COLOR = Color.BLACK;

	/**
	 * Win lane index.
	 */
	public static final int LANE_WIN = 0;

	/**
	 * Fifth water lane index.
	 */
	public static final int LANE_WATER_FIFTH = 1;

	/**
	 * Fourth water lane index.
	 */
	public static final int LANE_WATER_FOURTH = 2;

	/**
	 * Third water lane index.
	 */
	public static final int LANE_WATER_THIRD = 3;

	/**
	 * Second water lane index.
	 */
	public static final int LANE_WATER_SECOND = 4;

	/**
	 * First water lane index.
	 */
	public static final int LANE_WATER_FIRST = 5;

	/**
	 * First safe grass lane index.
	 */
	public static final int LANE_GRASS_FIRST = 6;

	/**
	 * Second safe grass lane index.
	 */
	public static final int LANE_GRASS_SECOND = 7;

	/**
	 * Fourth road lane index.
	 */
	public static final int LANE_ROAD_FOURTH = 8;

	/**
	 * Third road lane index.
	 */
	public static final int LANE_ROAD_THIRD = 9;

	/**
	 * Second road lane index.
	 */
	public static final int LANE_ROAD_SECOND = 10;

	/**
	 * First road lane index.
	 */
	public static final int LANE_ROAD_FIRST = 11;

	/**
	 * Starting lane index.
	 */
	public static final int LANE_START = 12;

	/**
	 * Default constructor for FroggrGame. Sets up the game state, the Canvas
	 * size and background color.
	 */
	public FroggrGame() {
		addKeyListener(this);
		setForeground(FOREGROUND_COLOR);
		setSize(GAME_WIDTH, GAME_HEIGHT);
		gameOver = false;
		gameWon = false;
	}

	/**
	 * Returns if the game is over and the player has lost the game.
	 * 
	 * @return true if player has lost the game.
	 */
	public static boolean isGameOver() {
		return gameOver;
	}

	/**
	 * Returns whether the player has won the game.
	 * 
	 * @return true if the player has won the game.
	 */
	public static boolean isGameWon() {
		return gameWon;
	}

	/**
	 * Creates and initializes the win zones (flies).
	 */
	private void createWinZones() {
		for (int i = 0; i < 4; i++) {
			flys.add(new Fly(i * 150, 0));
		}
	}

	/**
	 * Initializes the ArrayList of lanes and sets the image for each one.
	 */
	private void createLanes() {
		// Create our 13 game lanes
		for (int i = 0; i < NUMBER_OF_LANES; i++) {
			lanes.add(new Lane(0, i * 50));
		}

		// Add the image for each lane
		lanes.get(LANE_WIN).setImage("res/sprites/lane/win.png");

		// Use alternating images randomly for water lanes
		for (int i = LANE_WATER_FIFTH; i <= LANE_WATER_FIRST; i++) {
			lanes.get(i).setImage("res/sprites/lane/water.gif");
		}

		// set water lane regeneration times
		lanes.get(LANE_WATER_FIRST).setRegeneration(
				FIRST_WATER_LANE_REGENERATION);
		lanes.get(LANE_WATER_SECOND).setRegeneration(
				SECOND_WATER_LANE_REGENERATION);
		lanes.get(LANE_WATER_THIRD).setRegeneration(
				THIRD_WATER_LANE_REGENERATION);
		lanes.get(LANE_WATER_FOURTH).setRegeneration(
				FOURTH_WATER_LANE_REGENERATION);
		lanes.get(LANE_WATER_FIFTH).setRegeneration(
				FIFTH_WATER_LANE_REGENERATION);

		// Safe area before water
		lanes.get(LANE_GRASS_FIRST).setImage("res/sprites/lane/grass.png");
		lanes.get(LANE_GRASS_SECOND).setImage("res/sprites/lane/grass.png");

		// Road lanes
		lanes.get(LANE_ROAD_FOURTH).setImage("res/sprites/lane/road-top.png");
		lanes.get(LANE_ROAD_THIRD).setImage("res/sprites/lane/road-middle.png");
		lanes.get(LANE_ROAD_SECOND)
				.setImage("res/sprites/lane/road-middle.png");
		lanes.get(LANE_ROAD_FIRST).setImage("res/sprites/lane/road-bottom.png");

		// Set regeneration time for road lanes
		lanes.get(LANE_ROAD_FIRST)
				.setRegeneration(FIRST_ROAD_LANE_REGENERATION);
		lanes.get(LANE_ROAD_SECOND).setRegeneration(
				SECOND_ROAD_LANE_REGENERATION);
		lanes.get(LANE_ROAD_THIRD)
				.setRegeneration(THIRD_ROAD_LANE_REGENERATION);
		lanes.get(LANE_ROAD_FOURTH).setRegeneration(
				FOURTH_ROAD_LANE_REGENERATION);

		// Start lane
		lanes.get(LANE_START).setImage("res/sprites/lane/grass.png");
	}

	/**
	 * Determines what platforms, length, direction, and regeneration rate to
	 * add to each water lane.
	 */
	private void addPlatformsToLanes() {
		generatePlatform(lanes.get(LANE_WATER_FIFTH), 3,
				MovingObject.DIRECTION_LEFT, Platform.LOG);
		generatePlatform(lanes.get(LANE_WATER_FOURTH), 2,
				MovingObject.DIRECTION_RIGHT, Platform.TURTLE);
		generatePlatform(lanes.get(LANE_WATER_THIRD), 3,
				MovingObject.DIRECTION_LEFT, Platform.LOG);
		generatePlatform(lanes.get(LANE_WATER_SECOND), 3,
				MovingObject.DIRECTION_RIGHT, Platform.TURTLE);
		generatePlatform(lanes.get(LANE_WATER_FIRST), 3,
				MovingObject.DIRECTION_LEFT, Platform.LILY);
	}

	/**
	 * Determines what vehicles, length, direction and regeneration rate to add
	 * to the lanes. Trucks are always length 2. Other vehicles can be of length
	 * 1, 2 or 3.
	 */
	private void addVehiclesToLanes() {
		generateVehicle(lanes.get(LANE_ROAD_FIRST), 1,
				MovingObject.DIRECTION_RIGHT, Vehicle.CAR);
		generateVehicle(lanes.get(LANE_ROAD_SECOND), 2,
				MovingObject.DIRECTION_LEFT, Vehicle.CAR);
		generateVehicle(lanes.get(LANE_ROAD_THIRD), 3,
				MovingObject.DIRECTION_RIGHT, Vehicle.CAR);
		generateVehicle(lanes.get(LANE_ROAD_FOURTH), 2,
				MovingObject.DIRECTION_LEFT, Vehicle.TRUCK);
	}

	/**
	 * Generates vehicles for the road lanes.
	 * 
	 * @param lane
	 *            Lane sprite object to generate the vehicle for.
	 * @param length
	 *            Length of the vehicle (1, 2 or 3 for cars, 3 for trucks).
	 * @param direction
	 *            The direction that the vehicle moves.
	 * @param vehicleType
	 *            The type of vehicle to generate.
	 */
	private void generateVehicle(Lane lane, int length, int direction,
			int vehicleType) {
		lane.setTime(lane.getTime() + 1);
		if (lane.getTime() > lane.getRegeneration()) {
			lane.setTime(0);
			int startPosition = (direction == MovingObject.DIRECTION_LEFT) ? GAME_WIDTH
					: 0 - (length * 50);
			Vehicle v = new Vehicle(startPosition, lane.getYPos(), length,
					direction);
			v.setVehicleType(vehicleType);
			vehicles.add(v);
		}
	}

	/**
	 * Generates the platforms for the water lanes.
	 * 
	 * @param lane
	 *            The Lane sprite object to generate the platform for.
	 * @param length
	 *            The length of the platform.
	 * @param direction
	 *            The direction the platform moves in.
	 * @param platformType
	 *            The type of platform to generate.
	 */
	private void generatePlatform(Lane lane, int length, int direction,
			int platformType) {
		lane.setTime(lane.getTime() + 1);
		if (lane.getTime() > lane.getRegeneration()) {
			lane.setTime(0);
			int startPosition = (direction == MovingObject.DIRECTION_LEFT) ? GAME_WIDTH
					: 0 - (length * 50);
			Platform p = new Platform(startPosition, lane.getYPos(), length,
					direction);
			p.setPlatformType(platformType);
			platforms.add(p);
		}
	}

	/**
	 * Removes all Platform and Vehicle Sprites from their 
	 * corresponding lists that aren't beings used.
	 */
	private void removeUnusedSpritesFromLists() {
		Iterator<Vehicle> vehicleIterator = vehicles.iterator();
		while (vehicleIterator.hasNext()) {
			Vehicle v = vehicleIterator.next();
			if (v.isRemoved()) {
				vehicleIterator.remove();
			}
		}
	
		Iterator<Platform> platformIterator = platforms.iterator();
		while (platformIterator.hasNext()) {
			Platform p = platformIterator.next();
			if (p.isRemoved()) {
				platformIterator.remove();
			}
		}
	}

	/**
	 * Creates a new Player sprite at the starting location with a set number of
	 * lives.
	 * 
	 * @param lives
	 *            The number of lives the player starts with
	 */
	private void spawnPlayer(int lives) {
		this.player = new Player(250, GAME_HEIGHT - (2 * LANE_HEIGHT), lives);
	}

	/**
	 * Calculates the final score when the game is over (win or lose).
	 * 
	 * @return The player's final score.
	 */
	private int calculateFinalScore() {
		if (player.getLives() > 0) {
			return score * player.getLives();
		} else {
			return score;
		}
	}

	/**
	 * Draws the images on the canvas for the lanes. Used in the game render
	 * loop.
	 * 
	 * @param g
	 *            The Graphics object used by the Canvas.
	 */
	private void processLanes(Graphics g) {
		for (Lane l : lanes) {
			g.drawImage(l.getImage(), l.getXPos(), l.getYPos(), this);
		}
	}

	/**
	 * Draws the win zone images on the canvas.
	 * 
	 * @param g
	 *            The Graphics object used by the Canvas.
	 */
	private void processWinZones(Graphics g) {
		for (int i = 0; i < flys.size(); i++) {
			g.drawImage(flys.get(i).getImage(), flys.get(i).getXPos(), flys
					.get(i).getYPos(), this);
		}
	}

	/**
	 * Draws the image for the player in the main game render loop. Also,
	 * process all of the players activity in the game.
	 * 
	 * @param g
	 *            The Graphics object used in the Canvas.
	 */
	private void processPlayer(Graphics g) {
		player.tick(input);
		g.drawImage(player.getImage(), player.getXPos(), player.getYPos(), this);
	
		/*
		 * Keeps track of the next position the player must reach to gain
		 * points. If player dies he must reach the last nextPointsPosition to
		 * gain NEW_LANE_POINTS
		 */
		if (player.getYPos() < nextPointsPosition) {
			score = score + NEW_LANE_POINTS;
			nextPointsPosition = nextPointsPosition - LANE_HEIGHT;
		}
	
		/*
		 * Check if player has collided with a vehicle
		 */
		for (int i = 0; i < vehicles.size(); i++) {
			if (vehicles.get(i).hasCollidedWith(player)) {
				if (player.isAlive()) {
					SoundEffect.play(SoundEffect.COLLISION);
					player.kill();
					g.drawImage(player.getImage(), player.getXPos(),
							player.getYPos(), this);
				}
			}
		}
	
		/*
		 * Only runs if the player has entered into the water lanes. This is set
		 * up so if the player is not on a platform he is going to die.
		 */
		if (player.getYPos() < lanes.get(LANE_WATER_FIRST + 1).getYPos()) {
			int currentPlatform = -1;
			for (int i = 0; i < platforms.size(); i++) {
				// Checks if player lands on platform, if so he will sail on it.
				if (platforms.get(i).hasCollidedWith(player)) {
					player.sail(input, platforms.get(i));
					currentPlatform = i;
				}
			}
	
			if (currentPlatform != -1) {
				// While sailing on the platform this checks if the player jumps
				// off a platform into water
				if (!player.isOnPlatform(platforms.get(currentPlatform))
						&& player.isAlive()) {
					SoundEffect.play(SoundEffect.SPLASH);
					player.kill();
				}
			} else {
				int check = 0;
				for (int i = 0; i < flys.size(); i++) {
					check++;
					// Checks if the player has reached an accessible win zone.
					// If not, he dies.
					if (flys.get(i).hasCollidedWith(player)
							&& flys.get(i).isConsumed() == false) {
						flys.get(i).setConsumed(true);
						// add bonus points to player score for consuming a fly.
						score = score + CONSUME_FLY_BONUS;
						// reset the position at which the frog can gain more
						// points
						nextPointsPosition = 600;
						flysConsumed++;
						SoundEffect.play(SoundEffect.VICTORY);
						spawnPlayer(player.getLives());
						check = 0;
					} else {
						if (check == 4) {
							if (player.isAlive()) {
								if (player.getYPos() == 0) {
									SoundEffect.play(SoundEffect.COLLISION);
								} else {
									SoundEffect.play(SoundEffect.SPLASH);
								}
							}
							player.kill();
						}
					}
				}
			}
		}
	}

	/**
	 * This method processes the images in the lower left hand corner of the
	 * screen. They are used as counts so the player knows how many lives he or
	 * she has remaining.
	 * 
	 * @param g
	 */
	private void processPlayerLives(Graphics g) {
		BufferedImage playerImage = null;
		try {
			playerImage = ImageIO.read(Sprite.class.getClassLoader()
					.getResource("res/sprites/player/player-idle.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		for (int i = 0; i < player.getLives(); i++) {
			g.drawImage(playerImage, 50 * i, GAME_HEIGHT - 50, this);
		}
	}

	/**
	 * Draws the vehicles in the main game render loop.
	 * 
	 * @param g
	 *            The Graphics object used by the Canvas.
	 */
	private void processVehicles(Graphics g) {
		for (Vehicle v : vehicles) {
			if (!v.isRemoved()) {
				v.tick(input);
				g.drawImage(v.getImage(), v.getXPos(), v.getYPos(), this);
			}
		}
	}

	/**
	 * Draws the platforms in the main game render loop.
	 * 
	 * @param g
	 *            The Graphics object used by the Canvas.
	 */
	private void processPlatforms(Graphics g) {
		for (Platform p : platforms) {
			if (!p.isRemoved()) {
				p.tick(input);
				g.drawImage(p.getImage(), p.getXPos(), p.getYPos(), this);
			}
		}
	}

	/**
	 * This method draws the graphics on the screen and processes a lot of the
	 * game logic
	 * 
	 * @param g
	 *            The Graphics object used in the Canvas.
	 */
	private void processGameplay(Graphics g) {
		if (!player.isAlive() && player.getLives() > 0) {
			spawnPlayer(player.getLives());
		}
	
		// Checks if the game is over
		if (player.getLives() == 0) {
			g.drawString("GAME OVER", 225, GAME_HEIGHT - 25);
			gameOver = true;
		}
	
		// Checks if the player wins the game.
		if (flysConsumed == 4) {
			g.drawString("YOU WIN!", 225, GAME_HEIGHT - 25);
			gameWon = true;
		}
	
		// Keeps track of the score
		g.drawString("SCORE: " + score, 400, GAME_HEIGHT - 25);
	
	}

	/**
	 * Creates the options for the menu that pops up when the player either
	 * wins or loses the game.
	 * @return A String array containing each option.
	 */
	private String[] createEndGameOptions() {
		String[] options = { "Restart Game", "Back to Main Menu", "Quit Game" };
		return options;
	}

	/**
	 * Runs the method when an in game choice is made from an in game menu.
	 * @param choice The choice/method to run.
	 */
	private void runEndGameChoice(int choice) {
		if (choice == 0 || choice == -1) {
			restartGame();
		} else if (choice == 1) {
			restartGame();
			showMainMenu();
		} else if (choice == 2) {
			quit();
		}
	}

	/**
	 * This is to be displayed if the player wins the game.
	 */
	private void showWinDialog() {
		// Custom button text
		String[] options = createEndGameOptions();
		int choice = JOptionPane
				.showOptionDialog(
						this,
						"You won the game! You get an imaginary achievment that you can brag about to your friends!"
								+ "\nFinal Score: " + calculateFinalScore(),
						"You Win!", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null, options,
						options[0]);
		runEndGameChoice(choice);
	}

	/**
	 * This method is called when the player loses the game.
	 */
	private void showLoseDialog() {
		// Custom button text
		String[] options = createEndGameOptions();
		int choice = JOptionPane.showOptionDialog(this,
				"You just lost the game!" + "\nFinal Score: "
						+ calculateFinalScore(), "Game Over",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		runEndGameChoice(choice);

	}

	/**
	 * Pauses the game and displays a pause menu.
	 */
	private void showPauseMenu() {
		setPaused(true);
		// Custom button text
		String[] options = { "Resume Game", "Quit Game" };
		int choice = JOptionPane.showOptionDialog(this,
				"You just paused the game. What now?", "Paused",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (choice == 0 || choice == -1) {
			setPaused(false);
		} else {
			quit();
		}
	}

	/**
	 * Displays the main menu.
	 */
	private void showMainMenu() {
		setPaused(true);
		FroggrGameApplication.showTitleScreen();
	}

	/**
	 * Resets the score and game state and clears all sprites. Then spawns
	 * a new Player.
	 */
	private void restartGame() {
		// reset game over
		gameOver = false;
		gameWon = false;
	
		// reset score
		score = 0;
		flysConsumed = 0;
		nextPointsPosition = 600;
	
		// reset vehicles and platforms
		vehicles.clear();
		platforms.clear();
	
		// set flys to unconsumed
		for (Fly f : flys) {
			f.setConsumed(false);
			f.setImage("res/sprites/lane/fly.png");
		}
	
		// unpause game
		setPaused(false);
	
		// spawn player
		spawnPlayer(startingLives);
	}

	/**
	 * Exits the application. Will close the Frame as well.
	 */
	private void quit() {
		FroggrGameApplication.quitGame();
		System.exit(0);
	}

	/**
	 * The main game loop. All the graphics processing is done here.
	 */
	private void render() {

		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2);
			requestFocus();
			return;
		}

		Graphics g = bs.getDrawGraphics();
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.GREEN);

		/*
		 * Show the sprite's here. Make sure the method to add vehicles and
		 * platforms are displayed here otherwise it won't display. They must
		 * also be placed in the order that they are displayed in terms of
		 * layers. For example, the player will be before the vehicles, so that
		 * it gives the appearance that vehicles run over Froggr.
		 */
		addPlatformsToLanes();
		addVehiclesToLanes();
		processLanes(g);
		processWinZones(g);
		processPlatforms(g);
		processPlayer(g);
		processVehicles(g);
		processPlayerLives(g);
		processGameplay(g);

		// This must be called after all graphics processing
		g.dispose();
		bs.show();

		if (gameOver) {
			showLoseDialog();
		} else if (gameWon) {
			showWinDialog();
		}

		removeUnusedSpritesFromLists();

		// game is too fast without this delay
		try {
			Thread.sleep(15);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		while (!isPaused()) {
			render();
		}
	}

	/**
	 * This method starts the game by initializing the lanes 
	 * and then starting a new Thread.
	 */
	public void start() {
		setPaused(false);
		createLanes();
		createWinZones();
		
		// Needed to reset elements (sprites etc) and spawns new Player
		restartGame();
		
		new Thread(this).start();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		input.set(e.getKeyCode(), false);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		input.set(e.getKeyCode(), true);
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			showPauseMenu();
		}
	}

	/**
	 * Returs whether the game is in a paused state.
	 * @return True if paused.
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Set the game to a paused state.
	 * @param paused
	 *            Set to true to pause the game.
	 */
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

}
