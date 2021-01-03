package codingame.simulator;

class Robot extends Entity {
	int playerId;
	int robotId;
	Point position;
	boolean isKilled;
	boolean isHoldingItem;
	boolean isHoldingOre;
	Item item;

	public Robot(int robotId, int playerId, Point position) {
		this.position = position;
		this.playerId = playerId;
		this.robotId = robotId;
		this.isKilled = false;
		this.isHoldingItem = false;
		this.isHoldingOre = false;
	}

	public boolean isHoldingItem() {
		return isHoldingItem;
	}

	public void setHoldingOre(boolean isHoldingOre) {
		this.isHoldingOre = isHoldingOre;
	}

	public boolean isKilled() {
		return isKilled;
	}

	public boolean isHoldingOre() {
		return isHoldingOre;
	}

	public void setHoldingItem(boolean isHoldingItem) {
		this.isHoldingItem = isHoldingItem;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
		this.isHoldingItem = true;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		if (this.isKilled) {
			return;
		}
		Point currentPosition = this.position;
		Simulator.grid(currentPosition).removeRobot(this);
		Simulator.grid(position).addRobot(this);
		this.position = position;
	}

	public void setKilled(boolean isKilled) {
		this.isKilled = isKilled;
		this.item = null;
		this.isHoldingItem = false;
		this.isHoldingOre = false;
		position = new Point(-1, -1);
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getRobotId() {
		return robotId;
	}

	public void setRobotId(int robotId) {
		this.robotId = robotId;
	}

	public String getInputDetails(int teamId) {
		StringBuilder result = new StringBuilder();

		result.append(robotId + " ");

		if (this.playerId == teamId) {
			result.append("0 ");
		} else {
			result.append("1 ");
		}

		if (!isKilled) {
			result.append(position.getX() + " " + position.getY() + " ");
		} else {
			result.append("-1 -1 ");
		}

		if (item == null) {
			result.append("-1 ");
		} else {
			if (item instanceof Radar) {
				result.append("2 ");
			} else if (item instanceof Trap) {
				result.append("3 ");
			} else if (item instanceof Ore) {
				result.append(((Ore) item).getAmount() + " ");
			}
		}
		return result.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + robotId;
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
		Robot other = (Robot) obj;
		if (robotId != other.robotId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Robot [playerId=" + playerId + ", robotId=" + robotId + ", position=" + position + ", isKilled="
				+ isKilled + ", isHoldingItem=" + isHoldingItem + ", isHoldingOre=" + isHoldingOre + ", item=" + item
				+ "]";
	}
}