/*
    Based on the strategies employed in the Iocaine Powder bot described by
    Dan Egnor at http://ofb.net/~egnor/iocaine.html. Uses three prediction
    strategies, and a simple meta-strategy to pick which of those is most
    likely to win. Does not employ any meta-meta-strategy.

    @author Alden Hart
    2/21/2015
*/
import java.util.List;
import java.util.ArrayList;

public class MyocainePowderSimple implements RoShamBot {

    private enum Strategy {
        RANDOM, FREQUENCY, HISTORY
    }

    private int rockScore;      // Used for frequency analysis prediction
    private int paperScore;
    private int scissorsScore;
    private Action mostFrequent;
    private Action winner;
    private List<Action> oppLastMoves;    // Used for history matching
    private Action playerRandLastMove;
    private Action playerFreqLastMove;
    private Action playerHistLastMove;
    private Strategy strategy;  // The current strategy the bot is using
    private int randomScore;    // For picking a strategy with a meta-strategy
    private int frequencyScore;
    private int historyScore;

    public MyocainePowderSimple() {
        this.rockScore = 0;
        this.paperScore = 0;
        this.scissorsScore = 0;
        this.mostFrequent = Action.ROCK;
        this.winner = Action.PAPER;
        this.oppLastMoves = new ArrayList<Action>(5);
        this.playerRandLastMove = null;
        this.playerFreqLastMove = null;
        this.playerHistLastMove = null;
        this.strategy = Strategy.RANDOM;
        this.randomScore = 0; 
        this.frequencyScore = 0;
        this.historyScore = 0;
    }

    /* Gives the next move to be played by the bot based on the combination of
        strategies, meta-strategies, and meta-meta strategies used in the
        Iocaine Powder bot.

        @param lastOpponentMove the last move played by the oppnenent

        @return the next move played by MyocainePowder
    */
    public Action getNextMove(Action lastOpponentMove) {
        this.oppLastMoves.add(lastOpponentMove);
        this.strategy = this.metaStrategy(lastOpponentMove);

        switch (this.strategy) {
            case RANDOM:
                return this.playerRandLastMove;
            case FREQUENCY:
                return this.playerFreqLastMove;
            case HISTORY:
                return this.playerHistLastMove;
        }
        // This should never happen
        return Action.ROCK;
    }

    /* Determines which strategy to use based on the state of the game

        @param lastOpponentMove the last move the opponent played

        @return the strategy to be played
    */
    private Strategy metaStrategy(Action lastOpponentMove) {

        // Based on the last move you played and the last move he played,
        // adjust the scores
        this.randomScore += beats(playerRandLastMove, lastOpponentMove);
        this.frequencyScore += beats(playerFreqLastMove, lastOpponentMove);
        this.historyScore += beats(playerHistLastMove, lastOpponentMove);

        // Update the last move you would have played for each strategy
        this.playerRandLastMove = this.randomMove();
        this.playerFreqLastMove = this.frequencyAnalysis(lastOpponentMove);
        this.playerHistLastMove = this.historyAnalysis(lastOpponentMove);

        // Return the strategy with the best score
        if (this.randomScore > this.frequencyScore) {
            if (this.randomScore > this.historyScore) {
                return Strategy.RANDOM;
            }
        } else if (this.frequencyScore > this.historyScore) {
            return Strategy.FREQUENCY;
        } else {
            return Strategy.HISTORY;
        }

        // This should never happen
        return Strategy.HISTORY;
    }

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

    /* Plays a move to counter the most common move played by the opponent.

        @param lastOpponentMove the last move played by the opponent

        @return a move that beats the opponent's most common move
    */
    private Action frequencyAnalysis(Action lastOpponentMove) {
        switch (lastOpponentMove) {
            case ROCK:
                this.rockScore++;
                this.scissorsScore--;
                break;
            case PAPER:
                this.paperScore++;
                this.rockScore--;
                break;
            case SCISSORS:
                this.scissorsScore++;
                this.paperScore--;
                break;
            default:    // This should never happen
                break;
        }

        this.updateMostFrequent();
        this.updateWinner();

        return this.winner;
    }

    /* Updates the mostFrequent field.
    */
    private void updateMostFrequent(){
        if (this.rockScore > this.scissorsScore) {
            if (this.rockScore > this.paperScore) {
                this.mostFrequent = Action.ROCK;
            } else {
                this.mostFrequent = Action.PAPER;
            }
        } else if (this.scissorsScore > this.paperScore) {
            this.mostFrequent = Action.SCISSORS;
        } else {
            this.mostFrequent = Action.PAPER;
        }
    }

    /* Updates the field to beat the opponent's most frequent move
    */
    private void updateWinner() {
        switch (this.mostFrequent) {
            case ROCK:
                this.winner = Action.PAPER;
                break;
            case PAPER:
                this.winner = Action.SCISSORS;
                break;
            case SCISSORS:
                this.winner = Action.ROCK;
                break;
            default:    // This should also never happen
                break;
        }
    }

    /* Plays the move based on the pattern of last played moves.

        @param lastOpponentMove the last move played by the opponent

        @return the move to beat the predicted next move in the pattern
    */
    private Action historyAnalysis(Action lastOpponentMove) {
        int length = 128;
        while (length > 1) {
            if (length < this.oppLastMoves.size()) {
                boolean success = this.patternMatch(length);
                if (success) {
                    // You found the pattern, so bust out of the loop and use it
                    length = 1;
                } else {
                    // Just in case you go all the way through and never find a
                    // pattern
                    this.winner = this.randomMove();
                }
            }
            length /= 2;
        }
        return this.winner;
    }

    /* Implements history matching, looking in the past for another time the
        opponent has played this series of moves based on our series of moves.
    */
    private boolean patternMatch(int length) {

        List<Action> oppPattern = new ArrayList<Action>(length);
        
        // Make a list of the last length# of moves
        int patternPos = 0;
        for (int i = this.oppLastMoves.size() - length; i < this.oppLastMoves.size(); i++) {
            oppPattern.add(patternPos, this.oppLastMoves.get(i));
            patternPos++;
        }

        // Loop through the move history to see if your string matches any
        // in the past
        for (int i = 0; i < this.oppLastMoves.size() - length; i++) {
            patternPos = 0;
            int historyPos = i;
            Action played = this.oppLastMoves.get(historyPos);
            Action patternPlayed = oppPattern.get(patternPos);
            // You matched the first move. Check the rest of them
            while (played == patternPlayed) {
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
                            this.winner = Action.PAPER;
                            return true;
                        case PAPER:
                            this.winner = Action.SCISSORS;
                            return true;
                        case SCISSORS:
                            this.winner = Action.ROCK;
                            return true;
                    }
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
