/* A Rock-Paper-Scissors bot that counts the frequency of the opponent's moves
    and uses that to predict what they'll throw next. 
    Idea from http://www.dllu.net/programming/rps/

    @author Alden Hart
    2/17/2015
*/

public class FreakyCountzBot implements RoShamBot {

    private int rockScore;
    private int paperScore;
    private int scissorsScore;
    private Action mostFrequent;
    private Action winner;

    /* Keeps a "score" for each move. After each move, the respective score
        of the opponent's move. This will beat fixed move algorithms.
    */
    public FreakyCountzBot() {
        this.rockScore = 0;
        this.paperScore = 0;
        this.scissorsScore = 0;
        this.mostFrequent = Action.ROCK;
        this.winner = Action.PAPER;
    }

    /* Gives the next move to be played by the bot based on what the opponent
        has played before. Will play the move to beat the opponent's most
        frequent move.

        @param lastOpponentMove the last move played by the opponent

        @return the next action played
    */
    public Action getNextMove(Action lastOpponentMove) {
        
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
    
}