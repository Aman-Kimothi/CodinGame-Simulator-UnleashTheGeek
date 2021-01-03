package codingame.simulator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class Player {
	int teamId;
	int score;
	int radarCooldown;
	int trapCooldown;
	List<Robot> robots = new ArrayList<>();
	List<Point> visibleCoordinates = new ArrayList<>();
	List<Radar> radarsDeployed = new ArrayList<>();
	List<Trap> trapsDeployed = new ArrayList<>();
	List<Integer> robotsOrder = new LinkedList<>();
	List<Command> commands = new LinkedList<>();
	boolean isDefeated;

	public Player() {
		super();
	}

	public void radarBlow(Cell cell) {
		if (cell.getRadar() != null && cell.getRadar().getPlayerId() != teamId) {
			Simulator.idsToPlayers.get(cell.getRadar().getPlayerId()).removeRadar(cell.getRadar());
		}
	}

	void removeRadar(Radar radar) {
		radarsDeployed.remove(radar);
	}

	public void incrementScore() {
		++this.score;
	}

	public void set() {
		findVisibleCoordinates();
		robotsOrder = new LinkedList<>();
	}

	public void reset() {
		commands = new LinkedList<>();
	}

	public Player(int teamId, List<Robot> robots) {
		super();
		this.teamId = teamId;
		this.score = 0;
		this.radarCooldown = 0;
		this.trapCooldown = 0;
		this.robots = robots;
		this.isDefeated = false;
	}

	public List<Integer> getRobotsOrder() {
		return robotsOrder;
	}

	public void setRobotsOrder(List<Integer> robotsOrder) {
		this.robotsOrder = robotsOrder;
	}

	public void decrementTimers() {
		this.radarCooldown = Math.max(this.radarCooldown - 1, 0);
		this.trapCooldown = Math.max(this.trapCooldown - 1, 0);
	}

	public void addCommand(Command command) {
		this.commands.add(command);
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	public boolean isDefeated() {
		return isDefeated;
	}

	public void setDefeated(boolean isDefeated) {
		this.isDefeated = isDefeated;
	}

	public List<Radar> getRadarsDeployed() {
		return radarsDeployed;
	}

	public void setRadarsDeployed(List<Radar> radarsDeployed) {
		this.radarsDeployed = radarsDeployed;
	}

	public void addRadarsDeployed(Radar radarDeployed) {
		this.radarsDeployed.add(radarDeployed);
	}

	public List<Trap> getTrapsDeployed() {
		return trapsDeployed;
	}

	public void addTrapsDeployed(Trap trapDeployed) {
		this.trapsDeployed.add(trapDeployed);
	}

	public void setTrapsDeployed(List<Trap> trapsDeployed) {
		this.trapsDeployed = trapsDeployed;
	}

	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getRadarCooldown() {
		return radarCooldown;
	}

	public void setRadarCooldown() {
		this.radarCooldown = 6;
	}

	public int getTrapCooldown() {
		return trapCooldown;
	}

	public void setTrapCooldown() {
		this.trapCooldown = 6;
	}

	public List<Robot> getRobots() {
		return robots;
	}

	public void setRobots(List<Robot> robots) {
		this.robots = robots;
	}

	public List<Entity> getInputEntities() {

		List<Entity> entities = new ArrayList<>();
		List<Robot> opponentRobots = teamId == 0 ? Simulator.playerToRobots.get(1) : Simulator.playerToRobots.get(0);

		for (Robot robot : opponentRobots) {
			entities.add(robot);
		}
		for (Robot robot : robots) {
			entities.add(robot);
			robotsOrder.add(robot.robotId);
		}
		for (Radar radar : radarsDeployed) {
			entities.add(radar);
		}
		for (Trap trap : trapsDeployed) {
			entities.add(trap);
		}
		return entities;
	}

	void findVisibleCoordinates() {
		visibleCoordinates = new ArrayList<>();
		for (Radar radar : radarsDeployed) {
			visibleCoordinates
					.addAll(Simulator.getAllVisibleCoordinates(radar.getCoordinate(), Simulator.VISIBLE_RANGE));
		}
	}

	@Override
	public String toString() {
		return "Player [teamId=" + teamId + ", score=" + score + ", radarCooldown=" + radarCooldown + ", trapCooldown="
				+ trapCooldown + ", robots=" + robots + ", visibleCoordinates=" + visibleCoordinates
				+ ", radarsDeployed=" + radarsDeployed + ", trapsDeployed=" + trapsDeployed + ", robotsOrder="
				+ robotsOrder + ", commands=" + commands + ", isDefeated=" + isDefeated + "]";
	}
}