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
    // Useful for using the P' strategies
    private Action playerLastMove;
    // The full history of the opponent's moves
    private List<Action> opponentHistory;
    // The full history of your moves
    private List<Action> playerHistory;
    // Counts for frequency analysis
    private double rockCount;
    private double paperCount;
    private double scissorsCount;
    // Counts for predicting your own move with frequency analysis
    private double playerRockCount;
    private double playerPaperCount;
    private double playerScissorsCount;

    public MetaMyocainePowder() {
        // Make each MetaStrategy object
        this.p0 = new MetaStrategy();
        this.p1 = new MetaStrategy();
        this.p2 = new MetaStrategy();
        this.pPrime0 = new MetaStrategy();
        this.pPrime1 = new MetaStrategy();
        this.pPrime2 = new MetaStrategy();
        // Useful for using the P' strategies
        this.playerLastMove = Action.ROCK;
        // The full history of opponent's moves
        this.opponentHistory = new ArrayList<Action>();
        // The full history of your moves
        this.playerHistory = new ArrayList<Action>();
        // Counts for frequency analysis
        this.rockCount = 0;
        this.paperCount = 0;
        this.scissorsCount = 0;
        // Counts for predicting your own move with frequency analysis
        this.playerRockCount = 0;
        this.playerPaperCount = 0;
        this.playerScissorsCount = 0;

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
        this.predictPPrime0(pPrime0);
        this.predictPPrime1(pPrime1);
        this.predictPPrime2(pPrime2);

        // Play the move of the highest scoring strategy out of all the
        // meta-strategies
        double highestScore = 0;
        Action bestMove = Action.ROCK;
        // Does P.0 have the best score?
        if (this.p0.randomScore > highestScore) {
            highestScore = this.p0.randomScore;
            bestMove = this.p0.randMove;
        }
        if (this.p0.frequencyScore > highestScore) {
            highestScore = this.p0.frequencyScore;
            bestMove = this.p0.freqMove;
        }
        if (this.p0.historyScore > highestScore) {
            highestScore = this.p0.historyScore;
            bestMove = this.p0.histMove;
        }
        // What about P.1?
        if (this.p1.randomScore > highestScore) {
            highestScore = this.p1.randomScore;
            bestMove = this.p1.randMove;
        }
        if (this.p1.frequencyScore > highestScore) {
            highestScore = this.p1.frequencyScore;
            bestMove = this.p1.freqMove;
        }
        if (this.p1.historyScore > highestScore) {
            highestScore = this.p1.historyScore;
            bestMove = this.p1.histMove;
        }
        // Or P.2?
        if (this.p2.randomScore > highestScore) {
            highestScore = this.p2.randomScore;
            bestMove = this.p2.randMove;
        }
        if (this.p2.frequencyScore > highestScore) {
            highestScore = this.p2.frequencyScore;
            bestMove = this.p2.freqMove;
        }
        if (this.p2.historyScore > highestScore) {
            highestScore = this.p2.historyScore;
            bestMove = this.p2.histMove;
        }
        // Maybe P'.0?
        if (this.pPrime0.randomScore > highestScore) {
            highestScore = this.pPrime0.randomScore;
            bestMove = this.pPrime0.randMove;
        }
        if (this.pPrime0.frequencyScore > highestScore) {
            highestScore = this.pPrime0.frequencyScore;
            bestMove = this.pPrime0.freqMove;
        }
        if (this.pPrime0.historyScore > highestScore) {
            highestScore = this.pPrime0.historyScore;
            bestMove = this.pPrime0.histMove;
        }
        // Or P'.1 perhaps
        if (this.pPrime1.randomScore > highestScore) {
            highestScore = this.pPrime1.randomScore;
            bestMove = this.pPrime1.randMove;
        }
        if (this.pPrime1.frequencyScore > highestScore) {
            highestScore = this.pPrime1.frequencyScore;
            bestMove = this.pPrime1.freqMove;
        }
        if (this.pPrime1.historyScore > highestScore) {
            highestScore = this.pPrime1.historyScore;
            bestMove = this.pPrime1.histMove;
        }
        // Well what about P'.2 (finally)
        if (this.pPrime2.randomScore > highestScore) {
            highestScore = this.pPrime2.randomScore;
            bestMove = this.pPrime2.randMove;
        }
        if (this.pPrime2.frequencyScore > highestScore) {
            highestScore = this.pPrime2.frequencyScore;
            bestMove = this.pPrime2.freqMove;
        }
        if (this.pPrime2.historyScore > highestScore) {
            highestScore = this.pPrime2.historyScore;
            bestMove = this.pPrime2.histMove;
        }

        // This is the move you're going to play
        this.playerLastMove = bestMove;

        return bestMove;
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
        meta.randMove = this.randomMove();

        // Predict with frequency analysis
        Action freqPredicted = this.frequencyAnalysis(lastOpponentMove);
        switch (freqPredicted) {
            case ROCK:
                meta.freqMove = Action.PAPER;
                break;
            case PAPER:
                meta.freqMove = Action.SCISSORS;
                break;
            case SCISSORS:
                meta.freqMove = Action.ROCK;
                break;
        }

        // Predict with hitory analysis
        Action histPredicted = this.historyAnalysis(lastOpponentMove);
        switch (histPredicted) {
            case ROCK:
                meta.histMove = Action.PAPER;
                break;
            case PAPER:
                meta.histMove = Action.SCISSORS;
                break;
            case SCISSORS:
                meta.histMove = Action.ROCK;
                break;
        }
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
        meta.randMove = this.randomMove();

        // Predict with frequency analysis
        Action freqPredicted = this.frequencyAnalysis(lastOpponentMove);
        switch (freqPredicted) {
            case ROCK:
                meta.freqMove = Action.ROCK;
                break;
            case PAPER:
                meta.freqMove = Action.PAPER;
                break;
            case SCISSORS:
                meta.freqMove = Action.SCISSORS;
                break;
        }

        // Predict with hitory analysis
        Action histPredicted = this.historyAnalysis(lastOpponentMove);
        switch (histPredicted) {
            case ROCK:
                meta.histMove = Action.ROCK;
                break;
            case PAPER:
                meta.histMove = Action.PAPER;
                break;
            case SCISSORS:
                meta.histMove = Action.SCISSORS;
                break;
        }
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
        meta.randMove = this.randomMove();

        // Predict with frequency analysis
        Action freqPredicted = this.frequencyAnalysis(lastOpponentMove);
        switch (freqPredicted) {
            case ROCK:
                meta.freqMove = Action.SCISSORS;
                break;
            case PAPER:
                meta.freqMove = Action.ROCK;
                break;
            case SCISSORS:
                meta.freqMove = Action.PAPER;
                break;
        }

        // Predict with hitory analysis
        Action histPredicted = this.historyAnalysis(lastOpponentMove);
        switch (histPredicted) {
            case ROCK:
                meta.histMove = Action.SCISSORS;
                break;
            case PAPER:
                meta.histMove = Action.ROCK;
                break;
            case SCISSORS:
                meta.histMove = Action.PAPER;
                break;
        }
    }

    /* P'.0 - The opponent is using P.0 against you.

        Run P.0 to predict your move, then counter it. If P.0 predicts you play
        rock, your opponent will play paper, so you play scissors.

        @param lastOpponentMove the last move played by the opponent
        @param meta the MetaStrategy object you're dealing with
    */
    private void predictPPrime0(MetaStrategy meta) {
        // Make a random move
        meta.randMove = this.randomMove();

        // Predict with frequency analysis
        Action freqPredicted = this.selfFrequencyAnalysis();
        switch (freqPredicted) {
            case ROCK:
                meta.freqMove = Action.SCISSORS;
                break;
            case PAPER:
                meta.freqMove = Action.ROCK;
                break;
            case SCISSORS:
                meta.freqMove = Action.PAPER;
                break;
        }

        // Predict with hitory analysis
        Action histPredicted = this.selfHistoryAnalysis();
        switch (histPredicted) {
            case ROCK:
                meta.histMove = Action.SCISSORS;
                break;
            case PAPER:
                meta.histMove = Action.ROCK;
                break;
            case SCISSORS:
                meta.histMove = Action.PAPER;
                break;
        }

    }

    /* P'.1 - The opponent is using P.1 against you

        Your opponent uses P.1. If P predicts you play rock, they'll play rock
        as dictated by P.1. Then you play paper to beat their rock. Set the
        field to what would beat the original prediction.

        @param lastOpponentMove the last move played by the opponent
        @param meta the MetaStrategy object you're dealing with
    */
    private void predictPPrime1(MetaStrategy meta) {
        // Make a random move
        meta.randMove = this.randomMove();

        // Predict with frequency analysis
        Action freqPredicted = this.selfFrequencyAnalysis();
        switch (freqPredicted) {
            case ROCK:
                meta.freqMove = Action.PAPER;
                break;
            case PAPER:
                meta.freqMove = Action.SCISSORS;
                break;
            case SCISSORS:
                meta.freqMove = Action.ROCK;
                break;
        }

        // Predict with hitory analysis
        Action histPredicted = this.selfHistoryAnalysis();
        switch (histPredicted) {
            case ROCK:
                meta.freqMove = Action.PAPER;
                break;
            case PAPER:
                meta.freqMove = Action.SCISSORS;
                break;
            case SCISSORS:
                meta.freqMove = Action.ROCK;
                break;
        }
       
    }

    /* P'.2 - The opponent is using P.2 against you

        If P predicts you play rock, they'll play scissors as dictated by P.2.
        Play rock to counter it. Set the field to the original prediction of P.

        @param lastOpponentMove the last move played by the opponent
        @param meta the MetaStrategy object you're dealing with
    */
    private void predictPPrime2(MetaStrategy meta) {
        // Make a random move
        meta.randMove = this.randomMove();

        // Predict with frequency analysis
        Action freqPredicted = this.selfFrequencyAnalysis();
        switch (freqPredicted) {
            case ROCK:
                meta.freqMove = Action.ROCK;
                break;
            case PAPER:
                meta.freqMove = Action.PAPER;
                break;
            case SCISSORS:
                meta.freqMove = Action.SCISSORS;
                break;
        }

        // Predict with hitory analysis
        Action histPredicted = this.selfHistoryAnalysis();
        switch (histPredicted) {
            case ROCK:
                meta.freqMove = Action.ROCK;
                break;
            case PAPER:
                meta.freqMove = Action.PAPER;
                break;
            case SCISSORS:
                meta.freqMove = Action.SCISSORS;
                break;
        }
    
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

    /* Predicts what the opponent will play next based on the most common move 
        played by the opponent.

        @param lastOpponentMove the last move played by the opponent

        @return the opponent's most common move
    */
    private Action frequencyAnalysis(Action lastOpponentMove) {

        // Decay the counts
        this.rockCount *= DECAY_FACTOR;
        this.paperCount *= DECAY_FACTOR;
        this.scissorsCount *= DECAY_FACTOR;

        // Update them with what just got played
        switch (lastOpponentMove) {
            case ROCK:
                this.rockCount++;
                break;
            case PAPER:
                this.paperCount++;
                break;
            case SCISSORS:
                this.scissorsCount++;
                break;
        }

        // Return the most frequent move
        if (this.rockCount > this.scissorsCount) {
            if (this.rockCount > this.paperCount) {
                // Rock is the most frequent move
                return Action.ROCK;
            } else {
                // Paper is the most frequent
                return Action.PAPER;
            }
        } else if (this.scissorsCount > this.paperCount) {
            // Scissors is the most frequent
            return Action.SCISSORS;
        } else {
            // Paper is the most frequent
            return Action.PAPER;
        }
    }

    /* As if your opponent was using frequency analysis on you, predicts what
        you will play next based on your most frequent moves.

        @return your most common move
    */
    private Action selfFrequencyAnalysis() {

        // Decay the counts
        this.playerRockCount *= DECAY_FACTOR;
        this.playerPaperCount *= DECAY_FACTOR;
        this.playerScissorsCount *= DECAY_FACTOR;

        // Update them with what you played last
        switch (this.playerLastMove) {
            case ROCK:
                this.playerRockCount++;
                break;
            case PAPER:
                this.playerPaperCount++;
                break;
            case SCISSORS:
                this.playerScissorsCount++;
                break;            
        }

        // Return your most frequent move
        if (this.playerRockCount > this.playerScissorsCount) {
            if (this.playerRockCount > this.playerPaperCount) {
                // Rock is the most frequent move
                return Action.ROCK;
            } else {
                // Paper is the most frequent
                return Action.PAPER;
            }
        } else if (this.playerScissorsCount > this.playerPaperCount) {
            // Scissors is the most frequent
            return Action.SCISSORS;
        } else {
            // Paper is the most frequent
            return Action.PAPER;
        }        
    }

    /////////
    // History Matching
    /////////

    /* Predicts the opponent's next move based on the pattern of last played 
        moves.

        @param lastOpponentMove the last move played by the opponent

        @return the predicted next move in the pattern
    */
    private Action historyAnalysis(Action lastOpponentMove) {
        int length = 256;
        Action predictedMove = Action.ROCK;
        while (length > 1) {
            if (length < this.opponentHistory.size()) {
                boolean success = this.patternMatch(length, predictedMove);
                if (success) {
                    // You found the pattern, so bust out of the loop and use it
                    length = 1;
                } else {
                    // Just in case you go all the way through and never find a
                    // pattern
                    predictedMove = this.randomMove();
                }
            }
            length /= 2;
        }
        return predictedMove;
    }

    /* Implements history matching, looking in the past for another time the
        opponent has played this series of moves based on our series of moves.
    */
    private boolean patternMatch(int length, Action predictedMove) {
        List<Action> oppPattern = new ArrayList<Action>(length);

        // Make a list of the last length # of moves
        int patternPos = 0;
        for (int i = this.opponentHistory.size() - length; i < this.opponentHistory.size(); i++) {
            oppPattern.add(patternPos, this.opponentHistory.get(i));
            patternPos++;
        }

        int rCount = 0;
        int pCount = 0;
        int sCount = 0;
        boolean foundPattern = false;
        
        // Loop through the move history to see if your string matches any in
        // the past
        for (int i = 0; i < this.oppLastMoves.size() - length; i++) {
            patternPos = 0;
            int historyPos = i;
            Action played = this.oppLastMoves.get(historyPos);
            Action patternPlayed = oppPattern.get(patternPos);
            boolean findNext = false;
            // You matched the first move. Check the rest of them
            while (played == patternPlayed && !findNext) {
                historyPos++;
                patternPos++;
                played = this.oppLastMoves.get(historyPos);
                patternPlayed = oppPattern.get(patternPos);
                // You matched the whole string. Set winner to the thing that
                // beats the next move played in the history
                if (patternPos == length - 1) {
                    Action nextMove = this.oppLastMoves.get(historyPos+1);
                    switch (nextMove) {
                        case ROCK:
                            rCount++;
                            foundPattern = true;
                            findNext = true;
                            break;
                        case PAPER:
                            pCount++;
                            foundPattern = true;
                            findNext = true;
                            break;
                        case SCISSORS:
                            sCount++;
                            foundPattern = true;
                            findNext = true;
                            break;
                    }
                }
            }
        }

        if (foundPattern) {
            if (rCount > pCount && rCount > sCount) {
                this.winner = Action.PAPER;
                return true;
            }
            else if (pCount > rCount && pCount > sCount) {
                this.winner = Action.SCISSORS;
                return true;
            }
            else {
                this.winner = Action.ROCK;
                return true;
            }
        }
        // You looked through the whole string and didn't find that pattern
        return false;
    }

    /* As if your opponent does history matching on you. Predicts what you will
        play based on your move history.

        @return a prediction of what you'll play next based on your move history
    */
    private Action selfHistoryAnalysis() {
        int length = 256;
        Action predictedMove = Action.ROCK;
        while (length > 1) {
            if (length < this.playerHistory.size()) {
                boolean success = this.selfPatternMatch(length, predictedMove);
                if (success) {
                    // You found the pattern, so bust out of the loop and use it
                    length = 1;
                } else {
                    // Just in case you go all the way through and never find a
                    // pattern
                    predictedMove = this.randomMove();
                }
            }
            length /= 2;
        }
        return predictedMove;        
    }

    /* Implements history matching, looking in the past for another time you
        played this series of moves based on their series of moves.
    */
    private boolean selfPatternMatch(int length, Action predictedMove) {
        List<Action> playerPattern = new ArrayList<Action>(length);

        // Make a list of the last length # of moves
        int patternPos = 0;
        for (int i = this.playerHistory.size() - length; i < this.playerHistory.size(); i++) {
            playerPattern.add(patternPos, this.playerHistory.get(i));
            patternPos++;
        }

        // Loop through the move history to see if your string matches any in
        // the past
        for (int i = 0; i < this.playerHistory.size() - length; i++) {
            patternPos = 0;
            int historyPos = i;
            Action played = this.playerHistory.get(historyPos);
            Action patternPlayed = playerPattern.get(patternPos);
            // You matched the first move. Check the rest of them
            while (played == patternPlayed) {
                historyPos++;
                patternPos++;
                played = this.playerHistory.get(historyPos);
                patternPlayed = playerPattern.get(patternPos);
                // If this is true, you've matched the whole string. Set
                // predictedMove to the thing that comes next
                if (patternPos == length - 1) {
                    predictedMove = this.playerHistory.get(historyPos+1);
                    return true;
                }
            }
        }
        // You looked through the whole string and didn't find that pattern
        return false;
    }

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