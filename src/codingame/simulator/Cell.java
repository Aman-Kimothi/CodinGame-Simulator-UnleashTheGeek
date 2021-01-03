package codingame.simulator;

import java.util.ArrayList;
import java.util.List;

class Cell {
	List<Robot> robots = new ArrayList<>();
	Ore ore = null;
	Radar radar = null;
	Trap trap = null;
	boolean isHole;

	public Cell() {
		super();
	}

	public void removeRobot(Robot robot) {
		robots.remove(robot);
	}

	public Cell(List<Robot> robots, Ore ore, Radar radar, Trap trap) {
		super();
		this.robots = robots;
		this.ore = ore;
		this.radar = radar;
		this.trap = trap;
		this.isHole = false;
	}

	public boolean isTrapPresent() {
		return trap != null;
	}

	public boolean isHole() {
		return isHole;
	}

	public void setHole(boolean isHole) {
		this.isHole = isHole;
	}

	public String getRobotIds() {
		String ids = "";
		for (Robot robot : robots) {
			ids += robot.getRobotId();
		}
		return ids;
	}

	public List<Robot> getRobot() {
		return robots;
	}

	public void setRobot(List<Robot> robot) {
		this.robots = robot;
	}

	public void addRobot(Robot robot) {
		boolean remove = false;
		for (Robot currentRobot : robots) {
			if (currentRobot.getRobotId() == robot.getRobotId()) {
				remove = true;
				break;
			}
		}
		if (remove) {
			robots.remove(robot);
		}
		this.robots.add(robot);
	}

	public boolean isRobotPresent() {
		return !robots.isEmpty();
	}

	public Ore getOre() {
		return ore;
	}

	public void setOre(Ore ore) {
		this.ore = ore;
	}

	public Radar getRadar() {
		return radar;
	}

	public void setRadar(Radar radar, Point coordinate) {
		this.radar = radar;
		this.radar.setCoordinate(coordinate);
	}

	public Trap getTrap() {
		return trap;
	}

	public void setTrap(Trap trap) {
		this.trap = trap;
	}

	@Override
	public String toString() {
		return "Cell [robots=" + robots + ", ore=" + ore + ", radar=" + radar + ", trap=" + trap + ", isHole=" + isHole
				+ "]";
	}
}