
/** A mimicry-based Rock-Paper-Scissors player.
  * 
  * @author RR
  */
public class ApeBot implements RoShamBot {
    
    /** Returns the same action as the opponent's previous action.
      * 
      * @param lastRound the actions that were played by the two agents on the
      *        last round.
      * @return the next action to play.
      */
    public Action getNextMove(Action lastOpponentMove) {
        return lastOpponentMove;
    }
    
}