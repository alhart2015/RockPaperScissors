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

    /* Private nested class for a meta-strategy. Tracks each strategy and its
        successes or failures
    */
    private class MetaStrategy {

        private double randomScore;
        private double frequencyScore;
        private double historyScore;
        private Strategy bestStrategy;
        private double bestScore;
        private Action randMove;
        private Action freqMove;
        private Action histMove;

        private MetaStrategy() {
            this.randomScore = 0;
            this.frequencyScore = 0;
            this.historyScore = 0;
            this.bestStrategy = Strategy.RANDOM;
            this.bestScore = 0;
        }

        private void updateRandomScore(double diff) {
            this.randomScore += diff;
        }

        private void updateFrequencyScore(double diff) {
            this.frequencyScore += diff;
        }

        private void updateHistoryScore(double diff) {
            this.historyScore += diff;
        }

        private void updateBestStrategy() {
            if (this.randomScore > this.frequencyScore) {
                if (this.randomScore > this.historyScore) {
                    this.bestStrategy = Strategy.RANDOM;
                    this.bestScore = this.randomScore;
                }
            } else if (this.frequencyScore > this.historyScore) {
                this.bestStrategy = Strategy.FREQUENCY;
                this.bestScore = this.frequencyScore;
            } else {
                this.bestStrategy = Strategy.HISTORY;
                this.bestScore = this.historyScore;
            }
        }

        private Strategy getBestStrategy() {
            return this.bestStrategy;
        }

        private double getBestScore() {
            return this.bestScore;
        }
    }

    private enum Strategy {
        RANDOM, FREQUENCY, HISTORY
    }

    private static final int META_STRATEGIES = 6;

    // Each of the meta-strategies
    private MetaStrategy p0;
    private MetaStrategy p1;
    private MetaStrategy p2;
    private MetaStrategy pPrime0;
    private MetaStrategy pPrime1;
    private MetaStrategy pPrime2;
    // The last move for each strategy
    private Action playerLastRandMove;
    private Action playerLastFreqMove;
    private Action playerLastHistMove;
    // For the prime meta-strategies, what you expect your opponent will
    // predict for your move
    private Action predictedRandMove;
    private Action predictedFreqMove;
    private Action predictedHistMove;

    public MetaMyocainePowder() {
        // Make each MetaStrategy object
        this.p0 = new MetaStrategy();
        this.p1 = new MetaStrategy();
        this.p2 = new MetaStrategy();
        this.pPrime0 = new MetaStrategy();
        this.pPrime1 = new MetaStrategy();
        this.pPrime2 = new MetaStrategy();

        this.playerLastRandMove = Action.ROCK;
        this.playerLastFreqMove = Action.ROCK;
        this.playerLastHistMove = Action.ROCK;

        this.predictedRandMove = Action.ROCK;
        this.predictedFreqMove = Action.ROCK;
        this.predictedHistMove = Action.ROCK;
    }

    /* Return the next move to be played by the bot

        @param lastOpponentMove the last move played by the opponent

        @return the next move your bot will play
    */
    public Action getNextMove(Action lastOpponentMove) {
    
        this.predict(lastOpponentMove);
        MetaStrategy bestMeta = this.metaMetaStrategy();
        Strategy bestStrat = bestMeta.getBestStrategy();
        switch (bestStrat) {
            case RANDOM:
                return this.playerLastRandMove;
            case FREQUENCY:
                return this.playerLastFreqMove;
            case HISTORY:
                return this.playerLastHistMove;
        }
        // This should never happen
        return Action.ROCK;
    }

    /* Performs the meta-meta-strategy of checking every strategy for every
        meta-startegy. Compares every meta-strategies best score to that of
        every other meta-strategy.

        @return the metaStrategy with the highest total score
    */
    private MetaStrategy metaMetaStrategy() {
        
        MetaStrategy best = this.p0;
        int bestMetaScore = best.getBestScore();
        // Unfortunately can't loop through these the way I did it...
        if (this.p0.getBestScore() > bestMetaScore) {
            bestMetaScore = this.p0.getBestScore();
            best = this.p0;
        }
        if (this.p1.getBestScore() > bestMetaScore) {
            bestMetaScore = this.p1.getBestScore();
            best = this.p1;
        }
        if (this.p2.getBestScore() > bestMetaScore) {
            bestMetaScore = this.p2.getBestScore();
            best = this.p2;
        }
        if (this.pPrime0.getBestScore() > bestMetaScore) {
            bestMetaScore = this.pPrime0.getBestScore();
            best = this.pPrime0;
        }
        if (this.pPrime1.getBestScore() > bestMetaScore) {
            bestMetaScore = this.pPrime1.getBestScore();
            best = this.pPrime1;
        }
        if (this.pPrime2.getBestScore() > bestMetaScore) {
            bestMetaScore = this.pPrime2.getBestScore();
            best = this.pPrime2;
        }
        return best;        
    }

    /* Predicts the move that each strategy will play for each meta-strategy

        @param lastOpponentMove the last move played by the opponent
    */
    private void predict(Action lastOpponentMove) {

    }

    /////////////////////////////////////////////////////
    //////////// Meta-strategies             ////////////
    ///////////////////////////////////////////////////// 

    /* P.0 - Naive
        Assume the opponent is vulnerable to prediction by P. Predict
        their next move, and beat it.
    */
    private Action p0Predict(Action lastOpponentMove, MetaStrategy p0) {
        Action move = Action.ROCK;
        switch (p0.getBestStrategy()) {
            case Strategy.RANDOM:
                move = p0.randMove;
                break;
            case Strategy.FREQUENCY:
                move = p0.freqMove;
                break;
            case Strategy.HISTORY:
                move = p0.histMove;
                break;
        }
        p0.updateBestStrategy();
        return move;
    }

    /* P.1 - Defeat second-guessing
        Assume the opponent thinks you will use P.0. If P predicts rock,
        P.0 would play paper, but your opponent predicts that, so they
        play scissors. Then you play rock to beat their scissors.
    */
    private Action p1Predict(Action lastOpponentMove) {

    }

    /* P.2 - Defeat triple-guessing
        Assume the opponent thinks you will use P.1, so beat it.
    */
    private Action p2Predict(Action lastOpponentMove) {

    }

    /* P'.0 - The opponent is using P.0 against you
    */
    private Action p0PrimePredict(Action lastOpponentMove) {

    }

    /* P'.1 - The opponent is using P.1 against you
    */
    private Action p1PrimePredict(Action lastOpponentMove) {

    }

    /* P'.2 - The opponent is using P.2 against you
    */
    private Action p2PrimePredict(Action lastOpponentMove) {

    }

    /////////////////////////////////////////////////////
    //////////// Playing strategies          ////////////
    /////////////////////////////////////////////////////

    /////////
    // Random
    /////////

    /////////
    // Frequency Analysis
    /////////

    /////////
    // History Matching
    /////////

}