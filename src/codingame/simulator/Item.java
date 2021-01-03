package codingame.simulator;

class Item extends Entity {
	Point coordinate;

	public Item(Point coordinate) {
		super();
		this.coordinate = coordinate;
	}

	public Point getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Point coordinate) {
		this.coordinate = coordinate;
	}

	@Override
	public String toString() {
		return "Item [coordinate=" + coordinate + "]";
	}
}