package codingame.simulator;

import java.util.ArrayList;
import java.util.List;

class Point {
	int x;
	int y;

	public Point() {
	}

	public Point getNextPosition(Point coordinate) {

		Point nextPosition = new Point();

		if (Math.abs(coordinate.getX() - this.x) >= Simulator.TRAVERSAL_RANGE) {
			if (coordinate.getX() - this.x > 0) {
				nextPosition.setX(this.getX() + Simulator.TRAVERSAL_RANGE);
			} else {
				nextPosition.setX(this.getX() - Simulator.TRAVERSAL_RANGE);
			}
			nextPosition.setY(this.getY());
		}

		else if (Math.abs(coordinate.getX() - this.x) < Simulator.TRAVERSAL_RANGE) {
			int numberOfMovesLeft = Simulator.TRAVERSAL_RANGE - Math.abs(coordinate.getX() - this.x);
			nextPosition.setX(this.getX() + coordinate.getX() - this.x);

			if (coordinate.getY() - this.y > 0 && coordinate.getY() - this.y >= numberOfMovesLeft) {
				nextPosition.setY(this.getY() + numberOfMovesLeft);
			} else if (coordinate.getY() - this.y > 0 && coordinate.getY() - this.y < numberOfMovesLeft) {
				nextPosition.setY(this.getY() + coordinate.getY() - this.y);
			} else if (coordinate.getY() - this.y < 0 && Math.abs(coordinate.getY() - this.y) >= numberOfMovesLeft) {
				nextPosition.setY(this.getY() - numberOfMovesLeft);
			} else {
				nextPosition.setY(this.getY() - Math.abs(coordinate.getY() - this.y));
			}
		}

		return nextPosition;
	}

	public List<Point> getAdjacentPositions() {
		List<Point> li = new ArrayList<>();
		li.add(upCoordinate(this));
		li.add(downCoordinate(this));
		li.add(leftCoordinate(this));
		li.add(rightCoordinate(this));
		return li;
	}

	public boolean notPresent(List<Point> startingCoordinates) {
		for (Point p : startingCoordinates) {
			if (this.x == p.getX() && this.y == p.getY()) {
				return false;
			}
		}
		return true;
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point point) {
		this.x = point.getX();
		this.y = point.getY();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getDistance(Point p) {
		return Math.abs(this.x - p.getX()) + Math.abs(this.y - p.getY());
	}

	boolean isValidPoint() {
		return (this.y >= 0 && this.y < Simulator.GRID_WIDTH && this.x >= 0 && this.x < Simulator.GRID_LENGTH);
	}

	Point upCoordinate(Point p) {
		int yAxis = p.getY() - 1;
		if (yAxis >= 0) {
			return new Point(p.getX(), yAxis);
		}
		return p;
	}

	Point downCoordinate(Point p) {
		int yAxis = p.getY() + 1;
		if (yAxis < Simulator.GRID_WIDTH) {
			return new Point(p.getX(), yAxis);
		}
		return p;
	}

	Point leftCoordinate(Point p) {
		int xAxis = p.getX() - 1;
		if (xAxis >= 0) {
			return new Point(xAxis, p.getY());
		}
		return p;
	}

	Point rightCoordinate(Point p) {
		int xAxis = p.getX() + 1;
		if (xAxis < Simulator.GRID_LENGTH) {
			return new Point(xAxis, p.getY());
		}
		return p;
	}

	boolean atHeadQuarters() {
		return this.x == 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public String getValue() {
		return (this.x + " " + this.y);
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
}