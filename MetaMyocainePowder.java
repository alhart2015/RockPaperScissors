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

public class MetaMyocainePowder implements RoShamBot {

    private enum Strategy {
        RANDOM, FREQUENCY, HISTORY
    }

    private static final int META_STRATEGIES = 6;

    // Scores for each meta-strategy applied to each strategy
    private double[] randScores;
    private double[] freqScores;
    private double[] histScores;
    // The last move each strategy would have played
    private Action[] playerLastRandMove;
    private Action[] playerLastFreqMove;
    private Action[] playerLastHistMove;
    // The most effective meta-strategy
    private int bestMetaStrategy;

    public MetaMyocainePowder() {
        // One score for each meta-strategy
        this.randScores = new double[META_STRATEGIES];
        this.freqScores = new double[META_STRATEGIES];
        this.histScores = new double[META_STRATEGIES];

        this.playerLastRandMove = new Action[META_STRATEGIES];
        this.playerLastFreqMove = new Action[META_STRATEGIES];
        this.playerLastHistMove = new Action[META_STRATEGIES];
        for (int i = 0; i < META_STRATEGIES; i++) {
            this.playerLastRandMove[i] = Action.ROCK;
            this.playerLastFreqMove[i] = Action.ROCK;
            this.playerLastHistMove[i] = Action.ROCK;
        }
    }

    public Action getNextMove(Action lastOpponentMove) {

        // Find the strategy variant with the highest score
        double highestScore = 0;
        Strategy bestStrategy = Strategy.RANDOM;
        int bestMeta = 0;
        for (int i = 0; i < META_STRATEGIES; i++) {
            if (histScores[i] > highestScore) {
                highestScore = histScores[i];
                bestStrategy = Strategy.HISTORY;
                bestMeta = i;
            }
            if (randScores[i] > highestScore) {
                highestScore = randScores[i];
                bestStrategy = Strategy.RANDOM;
                bestMeta = i;
            }
            if (freqScores[i] > highestScore) {
                highestScore = freqScores[i];
                bestStrategy = Strategy.FREQUENCY;
                bestMeta = i;
            }
        }
        switch (bestStrategy) {
            case RANDOM:
                return this.playerLastRandMove[bestMetaStrategy];
            case FREQUENCY:
                return this.playerLastFreqMove[bestMetaStrategy];
            case HISTORY:
                return this.playerLastHistMove[bestMetaStrategy];
        }
        return Action.ROCK;
    }

    /* Predicts what the opponent will play for each strategy

        @param lastOpponentMove the last move played by the opponent
        @param metaStratagy the meta-strategy you're playing

        @return
    */
    private Action predict(Action lastOpponentMove, int metaStrategy) {
        
        return Action.ROCK;
    }

    /* Adjusts scores based on the last move you and your opponent played
    */
    private void adjustScores(Action lastOpponentMove) {

    }

    /////////////////////////////////////////////////////
    //////////// Meta-strategies             ////////////
    ///////////////////////////////////////////////////// 

    /* P.0 - Naive
        Assume the opponent is vulnerable to prediction by P. Predict
        their next move, and beat it.
    */
    private Action p0(Action lastOpponentMove) {
        this.adjustScores(lastOpponentMove);
        this.predict(lastOpponentMove);
        return Action.ROCK;
    }

    /* P.1 - Defeat second-guessing
        Assume the opponent thinks you will use P.0. If P predicts rock,
        P.0 would play paper, but your opponent predicts that, so they
        play scissors. Then you play rock to beat their scissors.
    */
    private Action p1(Action lastOpponentMove) {
        return Action.ROCK;
    }

    /* P.2 - Defeat triple-guessing
        Assume the opponent thinks you will use P.1, so beat it.
    */
    private Action p2(Action lastOpponentMove) {
        return Action.ROCK;
    }

    /* P'.0 - The opponent is using P.0 against you
    */
    private Action pPrime0(Action lastOpponentMove) {
        return Action.ROCK;
    }

    /* P'.1 - The opponent is using P.1 against you
    */
    private Action pPrime1(Action lastOpponentMove) {
        return Action.ROCK;
    }

    /* P'.2 - The opponent is using P.2 against you
    */
    private Action pPrime2(Action lastOpponentMove) {
        return Action.ROCK;
    } 

    /////////////////////////////////////////////////////
    //////////// Playing strategies          ////////////
    /////////////////////////////////////////////////////

    /////////
    // Random
    /////////

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

}