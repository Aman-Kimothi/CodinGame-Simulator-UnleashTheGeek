package codingame.simulator;

import codingame.simulator.Simulator.Action;
import codingame.simulator.Simulator.ItemType;

class Command implements Comparable<Command> {
	Action command;
	Point coordinate;
	ItemType itemType;
	Robot robot;
	int entityId;

	public Command(Action command, Point coordinate, Robot robot, ItemType itemType, int entityId) {
		super();
		this.command = command;
		this.coordinate = coordinate;
		this.itemType = itemType;
		this.robot = robot;
		this.entityId = entityId;
	}

	public Robot getRobot() {
		return robot;
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public Action getCommand() {
		return command;
	}

	public void setCommand(Action command) {
		this.command = command;
	}

	public Point getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Point coordinate) {
		this.coordinate = coordinate;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	@Override
	public String toString() {
		return "Command [command=" + command + ", coordinate=" + coordinate + ", itemType=" + itemType + ", robot="
				+ robot + ", entityId=" + entityId + "]";
	}

	@Override
	public int compareTo(Command commandSecond) {
		return this.robot.isHoldingItem() ? 1 : 0;
	}
}