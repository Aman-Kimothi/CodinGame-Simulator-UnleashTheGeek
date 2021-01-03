package codingame.simulator;

import java.util.Arrays;

public class PlayerInput {

	int width, height;
	int myScore;
	int opponentScore;
	String arena[][];
	int entityCount;
	int radarCoolDown;
	int trapCooldown;
	String entitiesInput;

	public PlayerInput() {
	}

	public PlayerInput(int width, int height, int myScore, int opponentScore, String[][] arena, int entityCount,
			int radarCoolDown, int trapCooldown, String entitiesInput) {
		super();
		this.width = width;
		this.height = height;
		this.myScore = myScore;
		this.opponentScore = opponentScore;
		this.arena = arena;
		this.entityCount = entityCount;
		this.radarCoolDown = radarCoolDown;
		this.trapCooldown = trapCooldown;
		this.entitiesInput = entitiesInput;
	}

	public String sendInitialInputAsString() {
		StringBuilder result = new StringBuilder();
		result.append(width + " " + height);
		result.append("\n");
		result.append(sendInputAsString());
		return result.toString();
	}

	public String sendInputAsString() {
		StringBuilder result = new StringBuilder();
		result.append(myScore + " " + opponentScore);
		result.append("\n");
		result.append(getArenaString());
		result.append(entityCount);
		result.append("\n");
		result.append(radarCoolDown);
		result.append("\n");
		result.append(trapCooldown);
		result.append("\n");
		result.append(entitiesInput);
		result.append("\n");
		return result.toString();
	}

	public String getArenaString() {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < arena[0].length; ++i) {
			for (int j = 0; j < arena.length; ++j) {
				result.append(arena[j][i] + " ");
			}
			result.append("\n");
		}
		return result.toString();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getMyScore() {
		return myScore;
	}

	public int getOpponentScore() {
		return opponentScore;
	}

	public String[][] getArena() {
		return arena;
	}

	public int getEntityCount() {
		return entityCount;
	}

	public int getRadarCoolDown() {
		return radarCoolDown;
	}

	public int getTrapCooldown() {
		return trapCooldown;
	}

	public String getEntitiesInput() {
		return entitiesInput;
	}

	@Override
	public String toString() {
		return "PlayerInput [width=" + width + ", height=" + height + ", myScore=" + myScore + ", opponentScore="
				+ opponentScore + ", arena=" + Arrays.toString(arena) + ", entityCount=" + entityCount
				+ ", radarCoolDown=" + radarCoolDown + ", trapCooldown=" + trapCooldown + ", entitiesInput="
				+ entitiesInput + "]";
	}
}
