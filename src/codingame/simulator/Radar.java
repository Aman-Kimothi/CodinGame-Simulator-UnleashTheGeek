package codingame.simulator;

class Radar extends Item {
	int radarId;
	int playerId;
	boolean deployed;

	public Radar(Point coordinate, int radarId, int playerId) {
		super(coordinate);
		this.radarId = radarId;
		this.playerId = playerId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public boolean isDeployed() {
		return deployed;
	}

	public void setDeployed(Point coordinate, boolean deployed) {
		this.setCoordinate(coordinate);
		this.deployed = deployed;
	}

	public int getRadarId() {
		return radarId;
	}

	public void setRadarId(int radarId) {
		this.radarId = radarId;
	}

	public String getInputDetails() {
		StringBuilder result = new StringBuilder();
		result.append(radarId + " ");
		result.append("2 ");
		result.append(this.coordinate.getX() + " " + this.coordinate.getY() + " ");
		result.append("-1 ");
		return result.toString();
	}

	@Override
	public String toString() {
		return "Radar [radarId=" + radarId + ", deployed=" + deployed + "]";
	}
}