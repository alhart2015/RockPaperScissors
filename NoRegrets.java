import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import java.util.*;

/** Ben and Jackson's Rock Paper Scissors player submission.
  *
  * @author Jackson Spell, Ben Wiley
  */

// We should implement an algorithm switcher that, when it gets to the point that playing by 
// the current algorithm will let the opponent win if they guess correctly, we switch 
// algorithms to change it up so their algorithm stops being accurate.

public class NoRegrets implements RoShamBot {

	private static final List<Action> MOVES = 
		Collections.unmodifiableList(Arrays.asList(Action.values()));
	private static final int TRACEBACK = 50;

	private double[] regretTotal, strategyTotal;
	private double[] myStrat, actionUtility;//, opStrat // these arrays persist but their values are continually reset.
	// FOR ALL ARRAYS: 0 = Rock, 1 = Paper, 2 = Scissors
	private List<Action> myHist;
	private List<Action> opHist;
	private int moves;


	/** Constructor for NoRegrets.
	  *
	  *	Parameters:
	  * 	None
	  *
	  * Returns:
	  * 	None
	  */
	public NoRegrets() {
		this.regretTotal = new double[3];
		this.strategyTotal = new double[3];
		this.myStrat = new double[3];
		this.actionUtility = new double[3];
		//this.opStrat = { .3, .3, .3 };
		this.myHist = new ArrayList<Action>();
		this.opHist = new ArrayList<Action>();
		this.moves = -1;
		// this.last = Action.ROCK;
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
		this.opHist.add(lastOpponentMove);
		this.moves++;

		int myAction, otherAction;

		if(this.myHist.size() <= TRACEBACK) {
			Action next = MOVES.get((int)(Math.random()*3));
			this.myHist.add(next);
			return next;
		}

		//System.out.println("Here");

		for(int i = 0; i < 3; i++) 
				regretTotal[i] = 0;

		for(int i = (moves - TRACEBACK >= 0)? moves - TRACEBACK: 0; i < moves; i++) {

			myAction = 0;
			otherAction = 0;

			for(int j = 0; j < 3; j++)
				this.actionUtility[j] = 0;

			if(this.myHist.get(i) == Action.PAPER) myAction = 1;
			else if(this.myHist.get(i) == Action.SCISSORS) myAction = 2;
			if(this.opHist.get(i) == Action.PAPER) otherAction = 1;
			else if(this.opHist.get(i) == Action.SCISSORS) otherAction = 2;

			//actionUtility[otherAction] += 0;
			this.actionUtility[otherAction == 2 ? 0 : otherAction + 1] += 1;
			this.actionUtility[otherAction == 0 ? 2 : otherAction - 1] += -1;

			for(int j = 0; j < 3; j++)
				this.regretTotal[j] += this.actionUtility[j] - this.actionUtility[myAction];
		}

		double regretSum = 0;

		for(int i = 0; i < 3; i++) {
			regretSum += this.regretTotal[i];
		}

		for(int i = 0; i < 3; i++) {
			this.regretTotal[i] = this.regretTotal[i] / regretSum;
		}

		//get new strategy and return next move
		//this.updateStrategy();
		double d = Math.random();
		double cumulativeProb = 0;
		int i;

		for(i = 0; i < 2 && d >= cumulativeProb; i++)
			cumulativeProb += this.regretTotal[i];
		if(d < cumulativeProb) i--;

		Action next;

		if(i == 0) next = Action.ROCK;
		else if(i == 1) next = Action.PAPER;
		else next = Action.SCISSORS;

		this.myHist.add(next);
		return next;
	}

	/** Updates the bot's mixed strategy.
	  *
	  *	Parameters:
	  * 	None
	  *
	  * Returns:
	  * 	None
	  */
	private void updateStrategy() {
		double normalizingSum = 0;
		for(int i = 0; i < 3; i++) {
			if(this.regretTotal[i] > 0)
				this.myStrat[i] = regretTotal[i];
			else this.myStrat[i] = 0;
			normalizingSum += this.myStrat[i];
		}
		for(int i = 0; i < 3; i++) {
			if(normalizingSum > 0)
				this.myStrat[i] /= normalizingSum;
			else this.myStrat[i] = 1.0 / 3;
			this.strategyTotal[i] += this.myStrat[i];
		}
	}

	/** Updates the opponent's mixed strategy.
	  *
	  *	Parameters:
	  * 	None
	  *
	  * Returns:
	  * 	None
	  */
	/*private void updateOpStrategy() {
		if(this.moves >= TRACEBACK) {
			double rock = 0.0;
			double paper = 0.0;
			double scissors = 0.0;
			for(int i = this.moves - 1; i >= this.moves - TRACEBACK; i--) {
				Action a = opHist.get(i);
				if(a == ROCK) rock++;
				else if(a == PAPER) paper++;
				else scissors++;
			}
			this.opStrat = { rock / TRACEBACK, paper / TRACEBACK, scissors / TRACEBACK };
		}
	}*/

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
		else return Action.ROCK;
	}
}