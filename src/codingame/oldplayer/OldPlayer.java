package codingame.oldplayer;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import codingame.simulator.PlayerInput;

/**
 * This is a sample Player class which contains the previous or old version of
 * the code i.e Version 1.0
 */
public class OldPlayer {

	Scanner in;
	PlayerInput inputForPlayer;
	Board board;

	public static void main(String args[]) {
		new OldPlayer().run();
	}

	public OldPlayer() {
		board = new Board();
		inputForPlayer = new PlayerInput();
	}

	public PlayerInput getInputForPlayer() {
		return inputForPlayer;
	}

	public void setInputForPlayer(PlayerInput inputForPlayer) {
		this.inputForPlayer = inputForPlayer;
	}

	public void run() {
		// Parse initial conditions
		board = new Board(in);

		while (true) {
			// Parse current state of the game
			board.update(in);

			think();

			for (Robot robot : board.myTeam.robots) {
				if (!robot.hasAction()) {
					robot.action = Action.none();
				}
			}

			// Send your actions for this turn
			for (Robot robot : board.myTeam.robots) {
				System.out.println(robot.action);
			}
		}
	}

	public String run(boolean simulationRun) {
		// Parse initial conditions

		// Parse current state of the game
		board.update(inputForPlayer);

		think();

		for (Robot robot : board.myTeam.robots) {
			if (!robot.hasAction()) {
				robot.action = Action.none();
			}
		}
		StringBuilder res = new StringBuilder();
		// Send your actions for this turn
		for (Robot robot : board.myTeam.robots) {
			res.append(robot.action);
			res.append("\n");
		}
		return res.toString();
	}

	private void think() {

		// detonate if possible
		List<Robot> robots = getFilteredRobots();

		for (Robot r : robots) {
			boolean bamPossible = board.myTrapPos.contains(r.pos);
			Cell bombCell = null;
			if (bamPossible) {
				bombCell = board.getCell(r.pos);
			}
			if (!bamPossible) {
				List<Cell> adj = board.getAdjuscent(board.getCell(r.pos).pos);
				for (Cell c : adj) {
					if (board.myTrapPos.contains(c.pos)) {
						bamPossible = true;
						bombCell = board.getCell(c.pos);
						break;
					}
				}
			}
			if (bamPossible) {
				List<Cell> impactedCells = getAllImpactedCellsWithBomb(bombCell);
				int myCount = 0;
				int oppCount = 0;
				for (Cell c : impactedCells) {
					for (Robot ri : board.opponentTeam.robots) {
						if (c.pos.equals(ri.pos)) {
							oppCount++;
						}
					}
					for (Robot ri : board.myTeam.robots) {
						if (c.pos.equals(ri.pos)) {
							myCount++;
						}
					}
				}
				if (oppCount > myCount) {
					r.action = Action.dig(r, board, bombCell, null);
				}
			}
		}

		for (Robot robot : board.myTeam.robots) {
			if (!robot.hasAction() && robot.isAlive()) {
				if (robot.hasOre()) {
					robot.action = Action.move(new Coord(0, robot.pos.y));
				}
			}
		}

		robots = getFilteredRobots();
		if (board.bam) {
			if (board.turn == 0) {
				robots.get(0).bamrobot = true;
			}
			for (Robot r : robots) {
				if (r.bamrobot) {
					if (board.bamCells.isEmpty()) {
						board.bam = false;
						r.bamrobot = false;
						break;
					}
					if (!r.hasTrap() && board.myTrapCooldown <= 1) {
						// careful about being in headquarters asking for trap when it is not ready
						r.action = Action.request(EntityType.TRAP, "BOMB!!!");
					} else if (!r.hasTrap() && board.myTrapCooldown > 1) {
						// dig nearest empty cell in the first row
						List<Cell> emptyCells = board.getAllEmptyCellsInRow(1);
						if (!emptyCells.isEmpty()) {
							Collections.sort(emptyCells, new DistanceAndClosestToBaseComparator(r.pos));
							r.action = Action.dig(r, board, emptyCells.get(0), null);
						} else {
							r.action = Action.none();
						}
					} else if (r.hasTrap()) {
						// place trap in the line and register in the cell, filter it later to avoid
						// death by our own robot
						Collections.sort(board.bamCells, new DistanceAndClosestToBaseComparator(r.pos));
						r.action = Action.dig(r, board, board.bamCells.get(0), null); // safe already

					}
					break;
				}
			}
		}

		List<Coord> remainingRadarTargets = getRemainingTargets(board.radarTargets);
		if (!remainingRadarTargets.isEmpty() && board.myRadarCooldown == 0) {
			// dont request if all radarTarget positions have been covered already
			robots = getFilteredRobots();
			Collections.sort(robots, new RobotToBaseComparator());
			if (!robots.isEmpty()) {
				for (Robot r : robots) {
					if (r.isAlive() && !r.hasAction() && !r.hasTrap()) {
						r.action = Action.request(EntityType.RADAR, "RAD!!!");
						break;
					}
				}
			}
		}
		// Trap logic

		if (!board.bam && board.myTrapCooldown == 0) {
			robots = getFilteredRobots();
			Collections.sort(robots, new RobotToBaseComparator());
			if (!robots.isEmpty()) {
				for (Robot r : robots) {
					if (r.isAlive() && !r.hasAction() && !r.hasRadar()) {
						r.action = Action.request(EntityType.TRAP, "BOMB!!!");
						break;
					}
				}
			}
		}
		for (Robot robot : board.myTeam.robots) {
			if (robot.isAlive() && !robot.hasAction()) {
				if (robot.hasRadar()) {
					Collections.sort(remainingRadarTargets, new DistanceAndClosestToBaseCoordComparator(robot.pos));
					if (!remainingRadarTargets.isEmpty()) {
						robot.action = Action.dig(robot, board, board.getCell(remainingRadarTargets.get(0)), "DIG RAD");
						remainingRadarTargets.remove(remainingRadarTargets.get(0));
					}
				}
			}
		}
		// get list of all ore vein cells
		if (board.oreCells != null && !board.oreCells.isEmpty()) {
			List<Cell> list = new ArrayList<>();
			for (Cell c : board.oreCells) {
				if (c.ore + c.takenByUs == c.orgore) {
					if ((!c.hole || (c.hole && c.dugByMe)) && !board.myTrapPos.contains(c.pos)) {
						list.add(c);
					}
				}
			}
			assignClosestCell(list, "Ore!");
		}
		List<Cell> undiscoveredOrKnown = board.undiscoveredCells.isEmpty() ? board.knownAndPastCells
				: board.undiscoveredCells;
		if (undiscoveredOrKnown != null && !undiscoveredOrKnown.isEmpty()) {
			List<Cell> list = new ArrayList<>();
			for (Cell c : undiscoveredOrKnown) {
				if (!c.hole || (c.hole && c.dugByMe)) {
					list.add(c);
				}
			}
			assignClosestCell(list, "More!");
		}

	}

	private List<Cell> getAllImpactedCellsWithBomb(Cell cell) {
		List<Cell> result = new ArrayList<>();
		if (board.myTrapPos.contains(cell.pos)) {
			getAllImpactedCellsWithBombInternal(cell, result);
		}
		return result;
	}

	private void getAllImpactedCellsWithBombInternal(Cell c, List<Cell> result) {
		if (result.contains(c)) {
			return;
		}
		result.add(c);
		if (!board.myTrapPos.contains(c.pos)) {
			return;
		}
		List<Cell> adj = board.getAdjuscent(c.pos);
		for (Cell c1 : adj) {
			getAllImpactedCellsWithBombInternal(c1, result);
		}
	}

	private List<Coord> getRemainingTargets(List<Coord> radarTargets) {
		List<Coord> list = new ArrayList<>();
		for (Coord pos : board.radarTargets) {
			if (!board.myRadarPos.contains(pos)) {
				if (!board.getCell(pos).hole || board.getCell(pos).dugByMe) {
					list.add(pos);
				} else if (!board.getCell(pos).dugByMe) {
					List<Cell> adj = board.getAdjuscent(board.getCell(pos).pos);
					Coord firstPos = null;
					for (Cell c : adj) {
						if (!c.hole) {
							firstPos = c.pos;
						}
						if (board.myRadarPos.contains(c.pos)) {
							// we already have radar on neighbor ... so no radar
							firstPos = null;
							break;
						}
					}
					if (firstPos != null) {
						list.add(firstPos);
					}
				}
			}
		}
		return list;
	}

	List<Robot> getFilteredRobots() {
		List<Robot> robots = new ArrayList<>(board.myTeam.robots);
		List<Robot> filtered = new ArrayList<>();
		for (Robot r : robots) {
			if (r.isAlive() && !r.hasAction()) {
				filtered.add(r);
			}
		}
		return filtered;
	}

	private void assignClosestCell(List<Cell> cells, String msg) {
		for (Robot robot : board.myTeam.robots) {
			if (robot.isAlive() && !robot.hasAction()) {
				robot.tempCellsSorted = new ArrayList<>(cells);
				Collections.sort(robot.tempCellsSorted, new DistanceAndClosestToBaseComparator(robot.pos));
			}
		}

		List<Robot> robots = new ArrayList<>(board.myTeam.robots);
		List<Robot> filtered = new ArrayList<>();
		for (Robot r : robots) {
			if (r.isAlive() && !r.hasAction()) {
				filtered.add(r);
			}
		}
		Collections.sort(filtered, new ClosestCellAndRobotDistanceComparator());
		for (Robot r : filtered) {
			if (!r.hasAction() && !r.tempCellsSorted.isEmpty()) {
				Cell targetCell = null;
				if (r.hasTrap()) {
					for (Cell c : r.tempCellsSorted) {
						if (c.ore == 1) {
							targetCell = c;
							break;
						}
					}
					if (targetCell == null) {
						// go to nearest empty cell and place trap
						List<Cell> emptyCells = board.getAllEmptyCells();
						Collections.sort(emptyCells, new DistanceAndClosestToBaseComparator(r.pos));
						if (!emptyCells.isEmpty()) {
							targetCell = emptyCells.get(0);
						}
					}
				} else {
					targetCell = r.tempCellsSorted.get(0);
				}
				r.action = Action.dig(r, board, targetCell, msg);
				List<Coord> adj = board.getAdjuscentPos(targetCell.pos);
				adj.add(targetCell.pos);
				if (targetCell.ore > 0 && adj.contains(r.pos)) {
					targetCell.takenByUs += 1;
					targetCell.ore += -1;
				}
				for (Robot ri : robots) {
					if (ri.isAlive() && !ri.hasAction()) {
						if (!r.equals(ri)) {
							ri.tempCellsSorted.remove(r.tempCellsSorted.get(0));
						}
					}
				}
			}
		}
	}

	private Cell findCellToMoveToTarget(Robot r, Cell cell) {
		List<Cell> closestNeighbour = board.getAdjuscent(cell.pos);
		Collections.sort(closestNeighbour, new DistanceAndClosestToBaseComparator(r.pos));
		return closestNeighbour.get(0);
	}

	class OreComparator implements Comparator<Cell> {
		@Override
		public int compare(Cell o1, Cell o2) {
			return o2.ore - o1.ore;
		}
	}

	class ClosestCellAndRobotDistanceComparator implements Comparator<Robot> {

		@Override
		public int compare(Robot o1, Robot o2) {
			int diff = 0;
			if (o1.tempCellsSorted != null && !o1.tempCellsSorted.isEmpty() && o2.tempCellsSorted != null
					&& !o2.tempCellsSorted.isEmpty()) {
				diff = o1.tempCellsSorted.get(0).pos.distance(o1.pos) - o2.tempCellsSorted.get(0).pos.distance(o2.pos);
			} else {
				if (o1.tempCellsSorted == null || o1.tempCellsSorted.isEmpty()) {
					if (o2.tempCellsSorted == null || o2.tempCellsSorted.isEmpty()) {
						return 0;
					} else {
						return -1;
					}
				} else {
					return 1;
				}
			}
			return diff;
		}

	}

	class RobotToBaseComparator implements Comparator<Robot> {
		@Override
		public int compare(Robot o1, Robot o2) {
			return new Coord(0, o1.pos.y).distance(o1.pos) - new Coord(0, o2.pos.y).distance(o2.pos);
		}
	}

	class DistanceAndClosestToBaseComparator implements Comparator<Cell> {
		Coord source;

		DistanceAndClosestToBaseComparator(Coord source) {
			this.source = source;
		}

		@Override
		public int compare(Cell o1, Cell o2) {
			int diff = source.distance(o1.pos) - source.distance(o2.pos);
			if (diff == 0) {
				diff = new Coord(0, o1.pos.y).distance(o1.pos) - new Coord(0, o2.pos.y).distance(o2.pos);
			}
			return diff;
		}
	}

	class DistanceAndClosestToBaseCoordComparator implements Comparator<Coord> {
		Coord source;

		DistanceAndClosestToBaseCoordComparator(Coord source) {
			this.source = source;
		}

		@Override
		public int compare(Coord o1, Coord o2) {
			int diff = source.distance(o1) - source.distance(o2);
			if (diff == 0) {
				diff = new Coord(0, o1.y).distance(o1) - new Coord(0, o2.y).distance(o2);
			}
			return diff;
		}
	}
}

class Coord {
	final int x;
	final int y;

	Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}

	Coord(Scanner in) {
		this(in.nextInt(), in.nextInt());
	}

	Coord add(Coord other) {
		return new Coord(x + other.x, y + other.y);
	}

	/*
	 * Manhattan distance (for 4 directions maps) see:
	 * https://en.wikipedia.org/wiki/Taxicab_geometry
	 */
	int distance(Coord other) {
		return abs(x - other.x) + abs(y - other.y);
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + x;
		result = PRIME * result + y;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coord other = (Coord) obj;
		return (x == other.x) && (y == other.y);
	}

	public String toString() {
		return x + " " + y;
	}
}

class Cell {

	boolean past;
	boolean known;
	int ore;
	int orgore;
	int takenByUs;
	boolean hole;
	Entity myTrap;
	Entity myRadar;
	boolean explored;
	Coord pos;
	boolean dugByMe;

	Cell(boolean known, int ore, boolean hole) {
		this.known = known;
		this.ore = ore;
		this.hole = hole;
	}

	Cell(Scanner in, int x, int y) {
		pos = new Coord(x, y);
		String oreStr = in.next();
		if (oreStr.charAt(0) == '?') {
			known = false;
			ore = 0;
		} else {
			known = true;
			ore = Integer.parseInt(oreStr);
			explored = true;
			orgore = ore;
		}
		String holeStr = in.next();
		hole = (holeStr.charAt(0) != '0');
	}

	void update(Scanner in) {
		String oreStr = in.next();
		if (oreStr.charAt(0) == '?') {
			if (known) {
				past = true;
			}
			known = false;
			ore = 0;
		} else {
			past = false;
			known = true;
			ore = Integer.parseInt(oreStr);
			if (!explored) {
				explored = true;
				orgore = ore;
			}
		}
		String holeStr = in.next();
		hole = (holeStr.charAt(0) != '0');
	}

	void updateTrap(Entity entity) {
		myTrap = entity;
	}

	void updateRadar(Entity entity) {
		myRadar = entity;
	}

	@Override
	public String toString() {
		return "Cell [past=" + past + ", known=" + known + ", ore=" + ore + ", orgore=" + orgore + ", hole=" + hole
				+ ", explored=" + explored + ", pos=" + pos + "]";
	}
}

class Action {
	final String command;
	final Coord pos;
	final EntityType item;
	String message;

	private Action(String command, Coord pos, EntityType item) {
		this.command = command;
		this.pos = pos;
		this.item = item;
	}

	private Action(String command, Coord pos, EntityType item, String message) {
		this.command = command;
		this.pos = pos;
		this.item = item;
		if (message != null) {
			this.message = " " + message;
		}
	}

	static Action none() {
		return new Action("WAIT", null, null, "I am free!");
	}

	static Action move(Coord pos) {
		return new Action("MOVE", pos, null, "MOVE!");
	}

	static Action move(Coord pos, String msg) {
		return new Action("MOVE", pos, null, msg);
	}

	static Action dig(Robot robot, Board board, Cell cell, String msg) {
		List<Coord> adj = board.getAdjuscentPos(cell.pos);
		adj.add(cell.pos);
		if (adj.contains(robot.pos) && !cell.hole) {
			cell.dugByMe = true;
		}
		return new Action("DIG", cell.pos, null, msg);
	}

	static Action dig(Cell cell) {
		cell.dugByMe = true;
		return new Action("DIG", cell.pos, null, "Dig!");
	}

	static Action dig(Coord pos, String msg) {
		return new Action("DIG", pos, null, msg);
	}

	static Action request(EntityType item) {
		return new Action("REQUEST", null, item, "Request!");
	}

	static Action request(EntityType item, String msg) {
		return new Action("REQUEST", null, item, msg);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder(command);
		if (pos != null) {
			builder.append(' ').append(pos);
		}
		if (item != null) {
			builder.append(' ').append(item);
		}
		if (message != null) {
			builder.append(' ').append(message);
		}
		return builder.toString();
	}
}

enum EntityType {
	NOTHING, ALLY_ROBOT, ENEMY_ROBOT, RADAR, TRAP, AMADEUSIUM;

	static EntityType valueOf(int id) {
		return values()[id + 1];
	}
}

class Entity {
	private static final Coord DEAD_POS = new Coord(-1, -1);

	// Updated every turn
	final int id;
	final EntityType type;
	final Coord pos;
	final EntityType item;

	// Computed for my robots
	Action action;

	Entity(Scanner in) {
		id = in.nextInt();
		type = EntityType.valueOf(in.nextInt());
		pos = new Coord(in);
		item = EntityType.valueOf(in.nextInt());
	}

	public Entity(int id, EntityType type, Coord pos, EntityType item, Action action) {
		super();
		this.id = id;
		this.type = type;
		this.pos = pos;
		this.item = item;
		this.action = action;
	}

	public Entity(Entity e) {
		super();
		this.id = e.id;
		this.type = e.type;
		this.pos = e.pos;
		this.item = e.item;
		this.action = e.action;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (id != other.id)
			return false;
		return true;
	}

	boolean isAlive() {
		return !DEAD_POS.equals(pos);
	}
}

class Robot extends Entity {

	List<Cell> tempCellsSorted;
	public boolean bamrobot;

	Robot(Scanner in) {
		super(in);
	}

	Robot(Entity e) {
		super(e);
	}

	boolean hasOre() {
		return item != null && item.equals(EntityType.AMADEUSIUM);
	}

	boolean hasRadar() {
		return item != null && item.equals(EntityType.RADAR);
	}

	boolean hasTrap() {
		return item != null && item.equals(EntityType.TRAP);
	}

	boolean hasItem() {
		return item != null;
	}

	boolean hasAction() {
		return action != null;
	}

}

class Team {
	int score;
	Collection<Robot> robots;

	void readScore(Scanner in) {
		score = in.nextInt();
		robots = new ArrayList<>();
	}
}

class Board {
	// Given at startup
	final int width;
	final int height;

	// Updated each turn
	final Team myTeam = new Team();
	final Team opponentTeam = new Team();
	private Cell[][] cells;
	int myRadarCooldown;
	int myTrapCooldown;
	Map<Integer, Entity> entitiesById;
	Collection<Coord> myRadarPos;
	Collection<Coord> myTrapPos;
	List<Cell> oreCells;
	List<Cell> undiscoveredCells;
	List<Cell> knownAndPastCells;
	List<Coord> radarTargets = new ArrayList<>();
	boolean bam = true;
	int turn = -1;
	int BAM_CELLS = 7;
	Coord BAM_START_POS = new Coord(1, 3);
	List<Coord> allBamPos = new ArrayList<>();
	List<Cell> bamCells = new ArrayList<>();

	Board() {
		width = 30;
		height = 15;
		boardInit();
	}

	Board(Scanner in) {
		width = in.nextInt();
		height = in.nextInt();
		boardInit();
	}

	private void boardInit() {
		cells = new Cell[width][height];
		initRadarTargets();
		for (int y = BAM_START_POS.y; y <= (BAM_START_POS.y + BAM_CELLS); y++) {
			allBamPos.add(new Coord(1, y));
		}
	}

	private void initRadarTargets() {
		radarTargets.add(new Coord(5, 4));
		radarTargets.add(new Coord(14, 4));
		radarTargets.add(new Coord(23, 4));
		radarTargets.add(new Coord(10, 9));
		radarTargets.add(new Coord(19, 9));
		radarTargets.add(new Coord(28, 9));
		radarTargets.add(new Coord(5, 12));
		radarTargets.add(new Coord(14, 12));
		radarTargets.add(new Coord(23, 12));
		radarTargets.add(new Coord(10, 1));
		radarTargets.add(new Coord(20, 1));
		radarTargets.add(new Coord(29, 1));
		radarTargets.add(new Coord(10, 14));
	}

	void update(Scanner in) {
		// Read new data
		turn++;
		myTeam.readScore(in);
		opponentTeam.readScore(in);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (cells[x][y] == null) {
					cells[x][y] = new Cell(in, x, y);
				} else {
					cells[x][y].update(in);
				}

			}
		}
		int entityCount = in.nextInt();
		myRadarCooldown = in.nextInt();
		myTrapCooldown = in.nextInt();
		entitiesById = new HashMap<>();
		myRadarPos = new ArrayList<>();
		myTrapPos = new ArrayList<>();
		for (int i = 0; i < entityCount; i++) {
			Entity entity = new Entity(in);
			entitiesById.put(entity.id, entity);
			if (entity.type == EntityType.ALLY_ROBOT) {
				Robot robot = new Robot(entity);
				myTeam.robots.add(robot);
			} else if (entity.type == EntityType.ENEMY_ROBOT) {
				Robot robot = new Robot(entity);
				opponentTeam.robots.add(robot);
			} else if (entity.type == EntityType.RADAR) {
				myRadarPos.add(entity.pos);
				getCell(entity.pos).updateRadar(entity);
			} else if (entity.type == EntityType.TRAP) {
				myTrapPos.add(entity.pos);
				getCell(entity.pos).updateTrap(entity);
			}
		}
		oreCells = getAllOreCells();
		undiscoveredCells = getUndiscoveredCells();
		knownAndPastCells = getKnownAndPastKnownCells();
		bamCells = updateBamCells();
	}

	void update(PlayerInput inputForPlayer) {
		// Read new data
		Scanner in = new Scanner(inputForPlayer.sendInputAsString());
		turn++;
		myTeam.readScore(in);
		opponentTeam.readScore(in);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (cells[x][y] == null) {
					cells[x][y] = new Cell(in, x, y);
				} else {
					cells[x][y].update(in);
				}

			}
		}
		int entityCount = in.nextInt();
		myRadarCooldown = in.nextInt();
		myTrapCooldown = in.nextInt();
		entitiesById = new HashMap<>();
		myRadarPos = new ArrayList<>();
		myTrapPos = new ArrayList<>();
		for (int i = 0; i < entityCount; i++) {
			Entity entity = new Entity(in);
			entitiesById.put(entity.id, entity);
			if (entity.type == EntityType.ALLY_ROBOT) {
				Robot robot = new Robot(entity);
				myTeam.robots.add(robot);
			} else if (entity.type == EntityType.ENEMY_ROBOT) {
				Robot robot = new Robot(entity);
				opponentTeam.robots.add(robot);
			} else if (entity.type == EntityType.RADAR) {
				myRadarPos.add(entity.pos);
				getCell(entity.pos).updateRadar(entity);
			} else if (entity.type == EntityType.TRAP) {
				myTrapPos.add(entity.pos);
				getCell(entity.pos).updateTrap(entity);
			}
		}
		oreCells = getAllOreCells();
		undiscoveredCells = getUndiscoveredCells();
		knownAndPastCells = getKnownAndPastKnownCells();
		bamCells = updateBamCells();
	}

	private List<Cell> updateBamCells() {
		List<Cell> cells = new ArrayList<>();
		for (Coord pos : allBamPos) {
			Cell c = getCell(pos);
			// Add a check for immediate detonation
			if (!myTrapPos.contains(pos) && (!c.hole || c.dugByMe)) {
				cells.add(c);
			}
		}
		return cells;
	}

	boolean cellExist(Coord pos) {
		return (pos.x >= 0) && (pos.y >= 0) && (pos.x < width) && (pos.y < height);
	}

	Cell getCell(Coord pos) {
		return cells[pos.x][pos.y];
	}

	boolean isBase(Coord pos) {
		return pos.x == 0;
	}

	boolean isBase(Cell c) {
		return c.pos.x == 0;
	}

	List<Coord> getAdjuscentPos(Coord pos) {
		List<Coord> list = new ArrayList<>();
		Coord position = new Coord(pos.x + 1, pos.y);
		if (cellExist(position) && !isBase(position)) {
			list.add(getCell(position).pos);
		}
		position = new Coord(pos.x, pos.y + 1);
		if (cellExist(position) && !isBase(position)) {
			list.add(getCell(position).pos);
		}
		position = new Coord(pos.x - 1, pos.y);
		if (cellExist(position) && !isBase(position)) {
			list.add(getCell(position).pos);
		}
		position = new Coord(pos.x, pos.y - 1);
		if (cellExist(position) && !isBase(position)) {
			list.add(getCell(position).pos);
		}
		return list;
	}

	List<Cell> getAdjuscent(Coord pos) {
		List<Cell> list = new ArrayList<>();
		Coord position = new Coord(pos.x + 1, pos.y);
		if (cellExist(position) && !isBase(position)) {
			list.add(getCell(position));
		}
		position = new Coord(pos.x, pos.y + 1);
		if (cellExist(position) && !isBase(position)) {
			list.add(getCell(position));
		}
		position = new Coord(pos.x - 1, pos.y);
		if (cellExist(position) && !isBase(position)) {
			list.add(getCell(position));
		}
		position = new Coord(pos.x, pos.y - 1);
		if (cellExist(position) && !isBase(position)) {
			list.add(getCell(position));
		}
		return list;
	}

	List<Cell> getAllOreCells() {
		List<Cell> list = new ArrayList<>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (cells[i][j].ore > 0 && !isBase(cells[i][j])) {
					list.add(cells[i][j]);
				}
			}
		}
		return list;
	}

	List<Cell> getUndiscoveredCells() {
		List<Cell> list = new ArrayList<>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (!cells[i][j].explored && !isBase(cells[i][j]) && !cells[i][j].hole) {
					list.add(cells[i][j]);
				}
			}
		}
		return list;
	}

	List<Cell> getKnownAndPastKnownCells() {
		List<Cell> list = new ArrayList<>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if ((cells[i][j].known || cells[i][j].past) && !isBase(cells[i][j])) {
					list.add(cells[i][j]);
				}
			}
		}
		return list;
	}

	List<Cell> getAllEmptyCells() {
		List<Cell> list = new ArrayList<>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (!cells[i][j].hole && !isBase(cells[i][j])) {
					list.add(cells[i][j]);
				}
			}
		}
		return list;
	}

	List<Cell> getAllEmptyCellsInRow(int row) {
		List<Cell> list = new ArrayList<>();
		for (int j = 0; j < height; j++) {
			if (!cells[row][j].hole) {
				list.add(cells[row][j]);
			}
		}
		return list;
	}
}