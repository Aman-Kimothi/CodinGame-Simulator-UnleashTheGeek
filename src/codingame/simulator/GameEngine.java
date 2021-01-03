package codingame.simulator;

public class GameEngine {

	public static void main(String args[]) {
		int oldPlayer = 0;
		int newPlayer = 0;
		int totalMatches = 500;
		for (int j = 1; j <= totalMatches; ++j) {
			int winner = Simulator.runSimulator();
			if (winner == 0) {
				++oldPlayer;
			} else if (winner == 1) {
				++newPlayer;
			}
		}
		System.out.println("*****************  RESULT *****************  ");
		System.out.println("Old Player wins " + oldPlayer + " matches");
		System.out.println("New Player wins " + newPlayer + " matches");
		System.out.println("Draw  " + (totalMatches - newPlayer - oldPlayer) + " matches");
		System.out.println("*****************  RESULT *****************  ");
	}
}
