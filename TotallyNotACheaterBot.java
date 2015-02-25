
import java.util.*;

/** Ben and Jackson's Rock Paper Scissors player submission.
  *
  * @author Jackson Spell, Ben Wiley
  */

// We should implement an algorithm switcher that, when it gets to the point that playing by 
// the current algorithm will let the opponent win if they guess correctly, we switch 
// algorithms to change it up so their algorithm stops being accurate.

public class TotallyNotACheaterBot implements RoShamBot {

	public List<Action> myHist;
	public List<Action> opHist;



	private static final List<Action> MOVES = 
		Collections.unmodifiableList(Arrays.asList(Action.values()));

	/** Constructor for TotallyNotACheaterBot.
	  *
	  *	Parameters:
	  * 	None
	  *
	  * Returns:
	  * 	None
	  */
	public TotallyNotACheaterBot() {
		myHist = new ArrayList<Action>();
		opHist = new ArrayList<Action>();
	}

	/** Returns the bot's next move.
	  *
	  *	Parameters:
	  * @param lastOpponentMove - Action - opponent's previous move
	  *
	  * Returns:
	  * @return Action - the bot's move
	  */
	public Action getNextMove(Action lastOpponentMove) {

		opHist.add(lastOpponentMove);

		Random randIntGen = new Random();
		// Action myMove = MOVES.get(randIntGen.nextInt(3));

		if (opHist.size() < 3) {
			return MOVES.get(randIntGen.nextInt(3));
		}

		Action myMove = lookBack(5, 500);

		myHist.add(myMove);
		return myMove;
	}

	/** Returns move based on opponent's most likely move historically.
	  *
	  *	Parameters:
	  * @param steps - int - number of previous steps to look back
	  * @param maxLookBack - int - max number of historical moves to check
	  *
	  * Returns:
	  * @return Action - move choice
	  */
	public Action lookBack(int steps, int maxLookBack) {

		List<Action> mostLikely = new ArrayList<Action>();

		List<Action> recent;
		List<Action> recentHist;

		if (opHist.size() - steps >= 0) {
			recent = opHist.subList(opHist.size() - steps, opHist.size());
		} else {
			recent = opHist.subList(0, opHist.size());
		}

		if (opHist.size() - maxLookBack >= 0) {
			recentHist = opHist.subList(opHist.size() - maxLookBack, opHist.size());
		} else {
			recentHist = opHist.subList(0, opHist.size());
		}

		//System.out.println(recentHist);

		int index = Collections.indexOfSubList(recentHist, recent);

		// Check for this move sequence in previous moves.
		while (index >= 0) {

			mostLikely.add(recentHist.get(index + recent.size() - 1));

			recentHist = recentHist.subList(index + recent.size(), recentHist.size());

			index = Collections.indexOfSubList(recentHist, recent);
		}

		Random randIntGen = new Random();

		// System.out.println(mostLikely.size());

		if (mostLikely.size() > 0) {
			//System.out.println("Choosing smart.");
			return beatThis(mostLikely.get(randIntGen.nextInt(mostLikely.size())));
		} else {
			return MOVES.get(randIntGen.nextInt(3));
		}
	}


	/** Returns move that will beat given move.
	  *
	  *	Parameters:
	  * @param move - Action - move to beat
	  *
	  * Returns:
	  * @return Action - move that beats input
	  */
	public Action beatThis(Action move) {
		if (move == Action.ROCK) return Action.PAPER;
		else if (move == Action.PAPER) return Action.SCISSORS;
		else {
			//System.out.println("He's gonna do scissors this time, I promise.");
			return Action.ROCK;
		}
	}
}