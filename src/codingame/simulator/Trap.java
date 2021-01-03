package codingame.simulator;

class Trap extends Item {
	int trapId;
	boolean exploded;

	public Trap(Point coordinate, int trapId) {
		super(coordinate);
		this.trapId = trapId;
		this.exploded = false;
	}

	public int getTrapId() {
		return trapId;
	}

	public boolean isExploded() {
		return exploded;
	}

	public void setExploded(boolean exploded) {
		this.exploded = exploded;
	}

	public void setTrapId(int trapId) {
		this.trapId = trapId;
	}

	public String getInputDetails() {
		StringBuilder result = new StringBuilder();
		result.append(trapId + " ");
		result.append("3 ");
		result.append(this.coordinate.getX() + " " + this.coordinate.getY() + " ");
		result.append("-1 ");
		return result.toString();
	}

	@Override
	public String toString() {
		return "Trap [trapId=" + trapId + "]";
	}
}