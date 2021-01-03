package codingame.simulator;

class Ore extends Item {
	int amount;
	boolean isFinished;

	public Ore(Point coordinate, int amount) {
		super(coordinate);
		this.amount = amount;
		this.isFinished = false;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void decreaseAmount() {
		--this.amount;
		if (this.amount <= 0) {
			this.isFinished = true;
		}
	}

	@Override
	public String toString() {
		return "Ore [amount=" + amount + ", isFinished=" + isFinished + "]";
	}
}