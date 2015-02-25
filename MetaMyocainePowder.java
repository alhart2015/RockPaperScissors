/*
    Implements an adaptation of the full Iocaine Powder algorithm laid out by
    Dan Egnor at http://ofb.net/~egnor/iocaine.html

    Uses a mix of random playing, prediction based on frequency analysis, and
    prediction based on history pattern matching. One of six meta-strategies
    decides which of these playing algorithms to use, and a meta-meta-strategy
    chooses which meta-strategy to use.

    @author Alden Hart
    2/22/2015
*/

import java.util.List;
import java.util.ArrayList;

public class MetaMyocainePowder implements RoShamBot {

    private static final int META_STRATEGIES = 6;
    private static final double DECAY_FACTOR = 0.9;

    private enum Strategy {
        RANDOM, FREQUENCY, HISTORY
    }

    /* Private nested class for a meta-strategy. Tracks each strategy and its
        successes or failures
    */
    private class MetaStrategy {

        private double randomScore;
        private double frequencyScore;
        private double historyScore;
        private Action randMove;
        private Action freqMove;
        private Action histMove;

        private MetaStrategy() {
            this.randomScore = 0;
            this.frequencyScore = 0;
            this.historyScore = 0;
            this.randMove = Action.ROCK;
            this.freqMove = Action.ROCK;
            this.histMove = Action.ROCK;
        }

        /* Update the scores tracked by the meta-strategy based on what you
            predicted and what the opponent played

            @param lastOpponentMove the last move played by the opponent
        */
        private void updateScores(Action lastOpponentMove) {
            // Decay the scores
            this.randomScore *= DECAY_FACTOR;
            this.frequencyScore *= DECAY_FACTOR;
            this.historyScore *= DECAY_FACTOR;

            // Add the result of the last move
            this.randomScore += beats(this.randMove, lastOpponentMove);
            this.frequencyScore += beats(this.freqMove, lastOpponentMove);
            this.historyScore += beats(this.histMove, lastOpponentMove);
        }
    }

    ////////////////////////////////////////////
    // END OF THE NESTED CLASS. START OF THE BOT
    ////////////////////////////////////////////

    // Each of the meta-strategies
    private MetaStrategy p0;
    private MetaStrategy p1;
    private MetaStrategy p2;
    private MetaStrategy pPrime0;
    private MetaStrategy pPrime1;
    private MetaStrategy pPrime2;
    private List<Action> opponentHistory;

    public MetaMyocainePowder() {
        // Make each MetaStrategy object
        this.p0 = new MetaStrategy();
        this.p1 = new MetaStrategy();
        this.p2 = new MetaStrategy();
        this.pPrime0 = new MetaStrategy();
        this.pPrime1 = new MetaStrategy();
        this.pPrime2 = new MetaStrategy();
        // The full history of opponent's moves
        this.opponentHistory = new ArrayList<Action>();
    }

    /* Return the next move to be played by the bot

        @param lastOpponentMove the last move played by the opponent

        @return the next move your bot will play
    */
    public Action getNextMove(Action lastOpponentMove) {
    
        // Update meta-strategy scores
        this.p0.updateScores(lastOpponentMove);
        this.p1.updateScores(lastOpponentMove);
        this.p2.updateScores(lastOpponentMove);
        this.pPrime0.updateScores(lastOpponentMove);
        this.pPrime1.updateScores(lastOpponentMove);
        this.pPrime2.updateScores(lastOpponentMove);

        // For each strategy within each meta-strategy, predict a new move
        this.predictP0(lastOpponentMove, p0);
        this.predictP1(lastOpponentMove, p1);
        this.predictP2(lastOpponentMove, p2);
        this.predictPPrime0(lastOpponentMove, pPrime0);
        this.predictPPrime1(lastOpponentMove, pPrime1);
        this.predictPPrime2(lastOpponentMove, pPrime2);

        // Play the move of the highest scoring strategy out of all the
        // meta-strategies

        return Action.ROCK;
    }

    /////////////////////////////////////////////////////
    //////////// Meta-strategies             ////////////
    ///////////////////////////////////////////////////// 

    /* P.0 - Naive
        Assume the opponent is vulnerable to prediction by P. Predict their 
        next move, and beat it.

        For each strategy, predict what the opponent is going to do, and set 
        the field in the corresponding meta-strategy to the move that would
        beat the predicted move.

        @param lastOpponentMove the last move played by the opponent
        @param meta the MetaStrategy object you're dealing with
    */
    private void predictP0(Action lastOpponentMove, MetaStrategy meta) {
        // Make a random move
        meta.randMove = this.randMove();

        // Predict with frequency analysis

        // Predict with hitory matching
    }

    /* P.1 - Defeat second-guessing
        Assume the opponent thinks you will use P.0. If P predicts rock,
        P.0 would play paper, but your opponent predicts that, so they
        play scissors. Then you play rock to beat their scissors.

        For each strategy, predict what the opponent is going to do and assume
        they know you've predicted this. Thus you set the field in the
        corresponding meta-strategy to the move you predicted, as that is what
        the cycle of you-think-they'll-play-so-they-play-so-you-play dictates

        @param lastOpponentMove the last move played by the opponent
        @param meta the MetaStrategy object you're dealing with
    */
    private void predictP1(Action lastOpponentMove, MetaStrategy meta) {
        // Make a random move
        meta.randMove = this.randMove();

        // Predict with frequency analysis

        // Predict with hitory matching
    }

    /* P.2 - Defeat triple-guessing
        Assume the opponent thinks you will use P.1, so beat it.

        Your opponent will expect you to play the move to beat the move they 
        play in response to their expectation that you've predicted their move
        (whew!). Set the field in the corresponding meta-strategy to the move
        that would have lost to the original predicted move.

        @param lastOpponentMove the last move played by the opponent
        @param meta the MetaStrategy object you're dealing with
    */
    private void predictP2(Action lastOpponentMove, MetaStrategy meta) {
        // Make a random move
        meta.randMove = this.randMove();

        // Predict with frequency analysis

        // Predict with hitory matching
    }

    /* P'.0 - The opponent is using P.0 against you.

        Run P.0 to predict your move, then counter it. If P.0 predicts you play
        rock, your opponent will play paper, so you play scissors.

        @param lastOpponentMove the last move played by the opponent
        @param meta the MetaStrategy object you're dealing with
    */
    private void predictPPrime0(Action lastOpponentMove, MetaStrategy meta) {
        // Make a random move
        meta.randMove = this.randMove();

        // Predict with frequency analysis

        // Predict with hitory matching
    }

    /* P'.1 - The opponent is using P.1 against you

        Your opponent uses P.1. If P predicts you play rock, they'll play rock
        as dictated by P.1. Then you play paper to beat their rock. Set the
        field to what would beat the original prediction.

        @param lastOpponentMove the last move played by the opponent
        @param meta the MetaStrategy object you're dealing with
    */
    private void predictPPrime1(Action lastOpponentMove, MetaStrategy meta) {
        // Make a random move
        meta.randMove = this.randMove();

        // Predict with frequency analysis

        // Predict with hitory matching
    }

    /* P'.2 - The opponent is using P.2 against you

        If P predicts you play rock, they'll play scissors as dictated by P.2.
        Play rock to counter it. Set the field to the original prediction of P.

        @param lastOpponentMove the last move played by the opponent
        @param meta the MetaStrategy object you're dealing with
    */
    private void predictPPrime2(Action lastOpponentMove, MetaStrategy meta) {
        // Make a random move
        meta.randMove = this.randMove();

        // Predict with frequency analysis

        // Predict with hitory matching
    }

    /////////////////////////////////////////////////////
    //////////// Playing strategies          ////////////
    /////////////////////////////////////////////////////

    /////////
    // Random
    /////////

    /* Plays a random move to ensure the bot doesn't get exploited and
        slaughtered and can at least come out not losing that much.

        @param none

        @return a random move
    */
    private Action randomMove() {

        double ONE_THIRD = 1.0/3.0;
        double TWO_THIRDS = 2.0/3.0;

        double play = Math.random();
        if (play < ONE_THIRD) {
            return Action.ROCK;
        } else if (play < TWO_THIRDS) {
            return Action.PAPER;
        } else {
            return Action.SCISSORS;
        }
    }

    /////////
    // Frequency Analysis
    /////////

    /////////
    // History Matching
    /////////

    /* Helper method to determine the outcome when two players play a move.
        If playerMove beats opponentMove, returns 1. If they tie, returns
        0. If opponentMove beats playerMove, returns -1

        @param playerMove the move played by your bot
        @param opponentMove the move played by the opponent's bot

        @return an int corresponding to the outcome of the moves
    */
    private static int beats(Action playerMove, Action opponentMove) {
        if (playerMove == Action.ROCK) {
            if (opponentMove == Action.ROCK) {
                return 0;
            } else if (opponentMove == Action.PAPER) {
                return -1;
            } else {
                return 1;
            }
        } else if (playerMove == Action.PAPER) {
            if (opponentMove == Action.ROCK) {
                return 1;
            } else if (opponentMove == Action.PAPER) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (opponentMove == Action.ROCK) {
                return -1;
            } else if (opponentMove == Action.PAPER) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}