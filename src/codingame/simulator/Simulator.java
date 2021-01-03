package codingame.simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import codingame.newplayer.NewPlayer;
import codingame.oldplayer.OldPlayer;

class Simulator {

	enum ItemType {
		RADAR, TRAP, OTHER
	}

	enum Action {
		MOVE, DIG, REQUEST, WAIT
	}

	private OldPlayer player1 = new OldPlayer();
	private NewPlayer player2 = new NewPlayer();
	private Random rand = new Random();
	private Point ORIGIN_POSITION = new Point(0, 0);
	private int PLAYERS = 2;
	private int ROBOTS = 5;
	public static int GRID_LENGTH = 30;
	public static int GRID_WIDTH = 15;
	public static Cell[][] grid = new Cell[GRID_LENGTH][GRID_WIDTH];
	private Map<Integer, Robot> playersMap = new HashMap<>();
	private Point TOP_LEFT = new Point(0, 0);
	private Point TOP_RIGHT = new Point(GRID_LENGTH - 1, 0);
	private Point BOTTOM_LEFT = new Point(0, GRID_WIDTH - 1);
	private Point BOTTOM_RIGHT = new Point(GRID_LENGTH - 1, GRID_WIDTH - 1);
	private int ROUNDS = 200;
	public static int VISIBLE_RANGE = 4;
	public static int TRAVERSAL_RANGE = 4;
	private int BOMB_RANGE = 1;
	private int RADAR_COUNT = 0;
	private int TRAP_COUNT = 0;
	public static Map<Integer, List<Robot>> playerToRobots = new HashMap<>();
	private Map<Integer, Robot> robotIdsToRobots = new HashMap<>();
	public static Map<Integer, Player> idsToPlayers = new HashMap<>();

	public static int runSimulator() {
		Simulator simulator = new Simulator();
		simulator.resetStaticVariables();
		simulator.init();
		simulator.roundsWithPlayer();
		if (idsToPlayers.get(0).isDefeated()) {
			return 1;
		} else if (idsToPlayers.get(1).isDefeated()) {
			return 0;
		} else if (idsToPlayers.get(0).getScore() > idsToPlayers.get(1).getScore()) {
			return 0;
		} else if (idsToPlayers.get(0).getScore() < idsToPlayers.get(1).getScore()) {
			return 1;
		} else if (idsToPlayers.get(0).getScore() == idsToPlayers.get(1).getScore()) {
			return -1;
		} else {
			throw new RuntimeException("Check the code, something went wrong!");
		}
	}

	private void resetStaticVariables() {
		player1 = new OldPlayer();
		player2 = new NewPlayer();
		rand = new Random();
		grid = new Cell[GRID_LENGTH][GRID_WIDTH];
		playersMap = new HashMap<>();
		playerToRobots = new HashMap<>();
		robotIdsToRobots = new HashMap<>();
		idsToPlayers = new HashMap<>();
	}

	private void init() {
		for (int i = 0; i < GRID_LENGTH; ++i) {
			for (int j = 0; j < GRID_WIDTH; ++j) {
				grid[i][j] = new Cell();
			}
		}
		for (int i = 0; i < PLAYERS; ++i) {
			playerToRobots.put(i, new ArrayList<>());
		}

		// Adding players to the grid
		List<Point> startingCoordinates = new ArrayList<>();
		for (int i = 0; i < ROBOTS; ++i) {
			Point startingCoordinate = startingCoordinate();
			while (!startingCoordinate.notPresent(startingCoordinates)) {
				startingCoordinate = startingCoordinate();
			}
			startingCoordinates.add(startingCoordinate);
			for (int j = 0; j < PLAYERS; ++j) {
				Robot robot = new Robot(j * ROBOTS + i, j, startingCoordinate);
				robotIdsToRobots.put(robot.robotId, robot);
				List<Robot> robotsOfPlayer = playerToRobots.get(j);
				robotsOfPlayer.add(robot);
				playerToRobots.put(j, robotsOfPlayer);
				grid[startingCoordinate.getX()][startingCoordinate.getY()].addRobot(robot);
			}
		}

		// Putting the players in the id -> Player map
		for (int j = 0; j < PLAYERS; ++j) {
			idsToPlayers.put(j, new Player(j, playerToRobots.get(j)));
		}

		List<Point> oreCoordinates = new ArrayList<>();
		// Adding ores to the grid
		while (oreCoordinates.size() <= 200) {
			Point oreCoordinate = oreCoordinate();
			while (!oreCoordinate.notPresent(startingCoordinates) && oreCoordinate.getX() != 0
					&& oreCoordinate.getY() != 0) {
				oreCoordinate = oreCoordinate();
			}
			oreCoordinates.add(oreCoordinate);
		}

		for (Point oreCoordinate : oreCoordinates) {
			int amount = rand.nextInt(10 - 1) + 1;
			Ore ore = new Ore(oreCoordinate, amount);
			grid(oreCoordinate).setOre(ore);
		}
	}

	private void roundsWithPlayer() {
		for (int round = 1; round <= 200; ++round) {
			System.out.println("*************************************");
			System.out.println("Round - " + round);
			setting();
			sendRoundDetails();
			receiveOutputFromPlayer();
			if (isAnyPlayerDefeated()) {
				break;
			}
			resolveOutputForPlayer();
			printGrid();
			reset();
			System.out.println("*************************************");
		}
		System.out.println("Score A : " + idsToPlayers.get(0).getScore());
		System.out.println("Score B : " + idsToPlayers.get(1).getScore());
	}

	private boolean isAnyPlayerDefeated() {
		for (Player player : idsToPlayers.values()) {
			if (player.isDefeated()) {
				return true;
			}
		}
		return false;
	}

	private void setting() {
		idsToPlayers.get(0).set();
		idsToPlayers.get(1).set();
	}

	private void reset() {
		idsToPlayers.get(0).reset();
		idsToPlayers.get(1).reset();
	}

	private void sendRoundDetails() {

		for (Map.Entry<Integer, Player> entry : idsToPlayers.entrySet()) {

			int playerId = entry.getKey();
			Player player = entry.getValue();

			int playerScore = player.getScore();

			int opponentScore = playerId == 0 ? idsToPlayers.get(1).getScore() : idsToPlayers.get(0).getScore();

			// Creating the arena
			String[][] arena = new String[GRID_LENGTH][GRID_WIDTH];
			for (int i = 0; i < GRID_LENGTH; ++i) {
				for (int j = 0; j < GRID_WIDTH; ++j) {
					Point currentPoint = new Point(i, j);
					StringBuilder output = new StringBuilder();
					if (!currentPoint.notPresent(player.visibleCoordinates)) {
						if (grid[i][j].getOre() != null) {
							output.append(Integer.toString(grid[i][j].getOre().getAmount()));
						} else {
							output.append("0");
						}
					} else {
						output.append("?");
					}

					if (grid[i][j].isHole()) {
						output.append(" 1");
					} else {
						output.append(" 0");
					}
					arena[i][j] = output.toString();
				}
			}
			// Creating the entities input for every player
			List<Entity> entities = player.getInputEntities();
			int entityCount = entities.size();
			int radarCoolDown = player.radarCooldown;
			int trapCooldown = player.trapCooldown;

			StringBuilder entitiesInput = new StringBuilder();

			for (Entity entity : entities) {
				if (entity instanceof Robot) {
					entitiesInput.append(((Robot) entity).getInputDetails(playerId)).append("\n");
				} else if (entity instanceof Trap) {
					entitiesInput.append(((Trap) entity).getInputDetails()).append("\n");
				} else if (entity instanceof Radar) {
					entitiesInput.append(((Radar) entity).getInputDetails()).append("\n");
				}
			}
			PlayerInput inputForPlayer = new PlayerInput(GRID_LENGTH, GRID_WIDTH, playerScore, opponentScore, arena,
					entityCount, radarCoolDown, trapCooldown, entitiesInput.toString());
			if (player.teamId == 0) {
				player1.setInputForPlayer(inputForPlayer);
			} else {
				player2.setInputForPlayer(inputForPlayer);
			}
		}
	}

	private void takeOutputFromPlayer(Player player, String[] output) {
		List<Integer> robotIds = player.getRobotsOrder();
		for (int i = 0; i < output.length; ++i) {
			String line = output[i];
			int robotId = robotIds.get(i);

			if (robotIdsToRobots.get(robotId).isKilled()) {
				continue;
			}
			if (line.contains(Action.WAIT.toString())) {
				player.addCommand(new Command(Action.WAIT, new Point(-1, -1), robotIdsToRobots.get(robotId),
						ItemType.OTHER, robotId));
			} else if (line.contains(Action.MOVE.toString())) {
				StringTokenizer str = new StringTokenizer(line, " ");
				str.countTokens();
				str.nextToken();
				player.addCommand(new Command(Action.MOVE,
						new Point(Integer.parseInt(str.nextToken()), Integer.parseInt(str.nextToken())),
						robotIdsToRobots.get(robotId), ItemType.OTHER, robotId));

			} else if (line.contains(Action.DIG.toString())) {
				StringTokenizer str = new StringTokenizer(line, " ");
				str.nextToken();
				player.addCommand(new Command(Action.DIG,
						new Point(Integer.parseInt(str.nextToken()), Integer.parseInt(str.nextToken())),
						robotIdsToRobots.get(robotId), ItemType.OTHER, robotId));

			} else if (line.contains(Action.REQUEST.toString())) {
				StringTokenizer str = new StringTokenizer(line, " ");
				str.nextToken();
				player.addCommand(new Command(Action.REQUEST, new Point(-1, -1), robotIdsToRobots.get(robotId),
						ItemType.valueOf(str.nextToken()), robotId));

			} else {
				player.setDefeated(true);
				break;
			}
		}
	}

	private void resolveOutputForPlayer() {

		List<Command> dig = new ArrayList<>();
		List<Command> request = new ArrayList<>();
		List<Command> move = new ArrayList<>();
		List<Command> wait = new ArrayList<>();

		for (Player player : idsToPlayers.values()) {
			for (Command command : player.commands) {
				if (command.getCommand().equals(Action.DIG)) {
					dig.add(command);
				} else if (command.getCommand().equals(Action.REQUEST)) {
					request.add(command);
				} else if (command.getCommand().equals(Action.MOVE)) {
					move.add(command);
				} else {
					wait.add(command);
				}
			}
		}

		resolveDigCommand(dig);

		resolveRequestCommand(request);

		timerDecremented(idsToPlayers.get(0));
		timerDecremented(idsToPlayers.get(1));

		resolveMoveCommand(move);

		deliverOreToHeadquarters(idsToPlayers.get(0));
		deliverOreToHeadquarters(idsToPlayers.get(1));
	}

	private void deliverOreToHeadquarters(Player player) {
		for (Robot robot : player.getRobots()) {
			if (robot.getPosition().getX() == 0 && robot.isHoldingOre()) {
				player.incrementScore();
				robot.setHoldingOre(false);
				robot.setItem(null);
			}
		}
	}

	private void resolveMoveCommand(List<Command> move) {
		for (Command command : move) {
			Robot robot = command.getRobot();
			if (robot.isKilled()) {
				continue;
			}
			Point robotPosition = robot.getPosition();
			Point coordinate = command.getCoordinate();
			Point nextPosition = robotPosition.getNextPosition(coordinate);
			if (nextPosition.isValidPoint()) {
				robot.setPosition(nextPosition);
				grid(robotPosition).removeRobot(robot);
				grid(nextPosition).addRobot(robot);
			}
		}
	}

	private void timerDecremented(Player player) {
		player.decrementTimers();
	}

	private void resolveRequestCommand(List<Command> request) {
		Collections.sort(request);
		for (Command command : request) {
			Robot robot = command.getRobot();
			if (robot.isKilled()) {
				continue;
			}
			// If not at headquarters, move to 0,y
			if (!robot.getPosition().atHeadQuarters()) {
				Point currentPosition = robot.getPosition();
				Point nextPosition = currentPosition.getNextPosition(new Point(0, currentPosition.getY()));
				if (nextPosition.isValidPoint()) {
					robot.setPosition(nextPosition);
				}
				continue;
			}
			Player player = idsToPlayers.get(robot.getPlayerId());
			if (command.itemType.equals(ItemType.RADAR)) {
				if (player.getRadarCooldown() <= 0) {
					Radar radar = new Radar(ORIGIN_POSITION, player.getTeamId(), RADAR_COUNT);
					robot.setItem(radar);
					robot.setHoldingOre(false);
					player.setRadarCooldown();
				}
			} else if (command.itemType.equals(ItemType.TRAP)) {
				if (player.getTrapCooldown() <= 0) {
					Trap trap = new Trap(ORIGIN_POSITION, RADAR_COUNT);
					robot.setItem(trap);
					robot.setHoldingOre(false);
					player.setTrapCooldown();
				}
			}
		}
	}

	private void resolveDigCommand(List<Command> dig) {
		for (Command command : dig) {
			Point coordinate = command.getCoordinate();
			Robot robot = command.getRobot();
			if (robot.isKilled()) {
				continue;
			}
			Cell cell = grid(coordinate);
			Point robotPosition = robot.getPosition();
			List<Point> adjacentPositions = robotPosition.getAdjacentPositions();
			// if cell is not adjacent, move to the next position
			if (!coordinate.equals(robotPosition) && !adjacentPositions.contains(coordinate)) {
				Point currentPosition = robot.getPosition();
				Point nextPosition = currentPosition.getNextPosition(coordinate);
				if (nextPosition.isValidPoint()) {
					robot.setPosition(nextPosition);
				}
				continue;
			}
			if (cell.getRadar() != null) {
				Player player = idsToPlayers.get(robot.getPlayerId());
				player.radarBlow(cell);
			}
			if (cell.isTrapPresent()) {
				blowBombInChainReaction(coordinate, idsToPlayers.get(robot.getPlayerId()));
				cell.setHole(true);
			} else {
				cell.setHole(true);
				Item buriedItem = buryItemOfRobot(cell, robot, coordinate);
				// Taking 1 amount of ore if not taken
				if (!(buriedItem instanceof Ore)) {
					Ore ore = cell.getOre();
					if (ore != null && !ore.isFinished()) {
						robot.setHoldingOre(true);
						ore.decreaseAmount();
					}
				}
			}
		}
	}

	private Item buryItemOfRobot(Cell cell, Robot robot, Point coordinate) {
		Item item = robot.getItem();
		if (item != null) {
			robot.setItem(null);
			if (item instanceof Trap) {
				cell.setTrap((Trap) item);
				idsToPlayers.get(robot.getPlayerId()).addTrapsDeployed((Trap) item);
			} else if (item instanceof Radar) {
				cell.setRadar((Radar) item, coordinate);
				idsToPlayers.get(robot.getPlayerId()).addRadarsDeployed((Radar) item);
			} else if (item instanceof Ore) {
				cell.setOre((Ore) item);
			}
		}
		return item;
	}

	private void blowBombInChainReaction(Point coordinate, Player player) {
		if (coordinate.isValidPoint()) {
			Cell cell = grid(coordinate);
			for (Robot robot : cell.getRobot()) {
				robot.setKilled(true);
			}
			cell.setRobot(new ArrayList<>());
			cell.setTrap(null);
			player.radarBlow(cell);
			blowBombInChainReaction(coordinate, player, BOMB_RANGE);
		}
	}

	private void blowBombInChainReaction(Point coordinate, Player player, int range) {
		if (range < 0) {
			return;
		}
		if (coordinate.isValidPoint()) {
			Cell cell = grid(coordinate);
			for (Robot robot : cell.getRobot()) {
				robot.setKilled(true);
			}
			cell.setRobot(new ArrayList<>());
			if (cell.getTrap() != null) {
				blowBombInChainReaction(coordinate, player, range);
			}
			blowBombInChainReaction(new Point(coordinate.getX() - 1, coordinate.getY()), player, range - 1);
			blowBombInChainReaction(new Point(coordinate.getX() + 1, coordinate.getY()), player, range - 1);
			blowBombInChainReaction(new Point(coordinate.getX(), coordinate.getY() - 1), player, range - 1);
			blowBombInChainReaction(new Point(coordinate.getX(), coordinate.getY() + 1), player, range - 1);
		}
	}

	public static Cell grid(Point coordinate) {
		return grid[coordinate.getX()][coordinate.getY()];
	}

	public static List<Point> getAllVisibleCoordinates(Point p, int range) {
		List<Point> visibleCoordinates = new ArrayList<>();
		getAllVisibleCoordinatesRecur(p, range, visibleCoordinates);
		return visibleCoordinates;

	}

	public static void getAllVisibleCoordinatesRecur(Point p, int range, List<Point> visibleCoordinates) {
		if (range < 0) {
			return;
		}
		if (p.isValidPoint() && p.notPresent(visibleCoordinates)) {
			visibleCoordinates.add(p);
			getAllVisibleCoordinatesRecur(new Point(p.getX() - 1, p.getY()), range - 1, visibleCoordinates);
			getAllVisibleCoordinatesRecur(new Point(p.getX() + 1, p.getY()), range - 1, visibleCoordinates);
			getAllVisibleCoordinatesRecur(new Point(p.getX(), p.getY() - 1), range - 1, visibleCoordinates);
			getAllVisibleCoordinatesRecur(new Point(p.getX(), p.getY() + 1), range - 1, visibleCoordinates);
		}
	}

	private Point startingCoordinate() {
		return new Point(0, rand.nextInt(GRID_WIDTH - 0) + 0);
	}

	private Point oreCoordinate() {
		return new Point(rand.nextInt(GRID_LENGTH - 1) + 1, rand.nextInt(GRID_WIDTH - 0) + 0);
	}

	private void receiveOutputFromPlayer() {

		String output1 = player1.run(true);
		String output2 = player2.run(true);

		takeOutputFromPlayer(idsToPlayers.get(0), output1.split("\n"));
		takeOutputFromPlayer(idsToPlayers.get(1), output2.split("\n"));

		System.out.println(output1);
		System.out.println(output2);
	}

	private void printGrid() {
		StringBuilder gridDisplay = new StringBuilder();
		gridDisplay.append("\t\t");
		for (int i = 0; i < GRID_LENGTH; ++i) {
			gridDisplay.append(i + "\t\t");
		}
		gridDisplay.append("\n");

		for (int i = 0; i < GRID_WIDTH; ++i) {
			gridDisplay.append(i + "\t\t");
			for (int j = 0; j < GRID_LENGTH; ++j) {
				if (grid[j][i].isRobotPresent()) {
					gridDisplay.append("B" + grid[j][i].getRobotIds());
				}
				if (grid[j][i].getOre() != null && grid[j][i].getOre().getAmount() > 0) {
					gridDisplay.append("O" + grid[j][i].getOre().getAmount());
				}
				if (grid[j][i].getTrap() != null) {
					gridDisplay.append("T");
				}
				if (grid[j][i].getRadar() != null) {
					gridDisplay.append("R");
				}
				if (grid[j][i].isHole()) {
					gridDisplay.append("(H)");
				} else {
					gridDisplay.append(".");
				}
				gridDisplay.append("\t\t");
			}
			gridDisplay.append("\n");
		}
		System.out.println(gridDisplay.toString());
	}
}