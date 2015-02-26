import java.util.*;
/* Nancy & Rachel - metastrategy bot
 */ 
public class MetaBot implements RoShamBot {
  
  private static final int R = 0;
  private static final int P = 1;
  private static final int S = 2;
  
  private Queue<StrategyRecord> strategyQ;
  private static final double STRATEGY_DECAY = .95;
  
  private String myMoves = "";
  private String oppMoves = "";
  
  private double BOLTZ_DECAY = .5;
  private int[] myBoltzRatings = {0,0,0};
  private int[] oppBoltzRatings = {0,0,0};
  
  private static final int HISTORY_SEQ_LEN = 5;
  
  private static final double RANDOM_MOVE_RATE = .05;
  
  private static final Map<Integer, Action> numToAction;
  static{
    numToAction = new HashMap<Integer, Action>();
    numToAction.put(0, Action.ROCK);
    numToAction.put(1, Action.PAPER);
    numToAction.put(2, Action.SCISSORS);
  }
  
  private static final Map<Action, Integer> actionToNum;
  static{
    actionToNum = new HashMap<Action, Integer>();
    actionToNum.put(Action.ROCK, 0);
    actionToNum.put(Action.PAPER, 1);
    actionToNum.put(Action.SCISSORS, 2);
  }
  
  private class StrategyRecord implements Comparable<StrategyRecord>{
    public int metaStrategy; // P0 = 0, P1 = 1, P2 = 2; P0' = 3, P1' = 4, P2' = 5
    public int baseStrategy; // random = 0, history = 1, boltzFreq = 2
    public double rating; // high rating indicates better strategy
    public int prediction; // what will opponent play next?
    
    public StrategyRecord(int meta, int base){
      
      this.metaStrategy = meta;
      this.baseStrategy = base; 
      this.rating = 0.0;
      Random r = new Random();
      this.prediction  = r.nextInt(3);
    }
    @Override
    public int compareTo(StrategyRecord other){ //"better" strategies are less than "worse" strategies
      if(other.rating > this.rating)
        return 1;
      if(other.rating < this.rating)
        return -1;
      return 0;
    }
  }
  
  
  public MetaBot(){
    
    this.strategyQ = new PriorityQueue<StrategyRecord>();
    
    for(int i = 0; i < 18; i++){
      int metaStrategy = i%6;

      if ( i < 6){
        this.strategyQ.add(new StrategyRecord(metaStrategy, 0));


      }
      else if (i < 12){
        this.strategyQ.add(new StrategyRecord(metaStrategy, 1));


      }
      else{

        this.strategyQ.add(new StrategyRecord(metaStrategy, 2));
      }
    } 
  }
   /* What move will the opponent make next based on our strategies and ratings?
    */ 
  private int predictNextMove() { //choose randomly among strategy records with highest rating
    List<Integer> predictions = new ArrayList<Integer>();
    double maxRating = this.strategyQ.peek().rating;
    Queue<StrategyRecord> newStrategyQ = new PriorityQueue<StrategyRecord>();
    while(this.strategyQ.peek() != null && this.strategyQ.peek().rating == maxRating){
      predictions.add(this.strategyQ.peek().prediction);
      newStrategyQ.add(this.strategyQ.poll());
    }
    while(this.strategyQ.peek() != null)
      newStrategyQ.add(this.strategyQ.poll());
    
    this.strategyQ = newStrategyQ;
    
    Random r = new Random();
    return predictions.get(r.nextInt(predictions.size()));
  }

  private static int getCounterMove(int move){
    if(move == 0 || move == 1 )
      return move + 1;
    return 0;
  }
  private static int rotate(int move, int rotation){
    return (move + rotation) % 3;
  }
  
  
  private void updateStrategyRatings(){
    int lastOppMove = Character.getNumericValue(
                        this.oppMoves.charAt(this.oppMoves.length() - 1));
    Queue<StrategyRecord> newStrategyQ = new PriorityQueue<StrategyRecord>();
    while(this.strategyQ.peek() != null){
      StrategyRecord s = this.strategyQ.poll();
      s.rating *= STRATEGY_DECAY;
      if(s.prediction == lastOppMove)
        s.rating += 1.0;
      else if (s.prediction == rotate(lastOppMove, 1))
        s.rating -= 1.0;
      newStrategyQ.add(s);
    }
    
    this.strategyQ = newStrategyQ;
  }
  
  private int getRandomPrediction(){
    Random r = new Random();
    return r.nextInt(3);
  }
  
  private int getBoltzPrediction(int[] boltzRatings){
    Random rand = new Random();
    double randnum = rand.nextDouble() * (Math.exp(boltzRatings[0]) 
                                            + Math.exp(boltzRatings[1])
                                            + Math.exp(boltzRatings[2]));
    if(randnum < Math.exp(boltzRatings[0]))
      return 0;
    if(randnum < Math.exp(boltzRatings[0]) + Math.exp(boltzRatings[1]))
      return 1;
    return 2;     
  }
  
  private void updateBoltzRatings(){
    for(int i = 0; i < 3; i++){
      this.myBoltzRatings[i] *= BOLTZ_DECAY;
      this.oppBoltzRatings[i] *= BOLTZ_DECAY;
    }
    int lastOppMove = Character.getNumericValue(
                     this.oppMoves.charAt(this.oppMoves.length() - 1));
    int myLastMove =  Character.getNumericValue(
                     this.myMoves.charAt(this.myMoves.length() - 1));
    
    if(lastOppMove == 0){
      this.myBoltzRatings[1] += .1;
      this.myBoltzRatings[2] -= .1;
    }
    else if (lastOppMove == 1){
      this.myBoltzRatings[2] += .1;
      this.myBoltzRatings[0] -= .1;
    }
    else{
      this.myBoltzRatings[0] += .1;
      this.myBoltzRatings[1] -= .1;
    }
    
    if(myLastMove == 0){
      this.oppBoltzRatings[1] += .1;
      this.oppBoltzRatings[2] -= .1;
    }
    else if (myLastMove == 1){
      this.oppBoltzRatings[2] += .1;
      this.oppBoltzRatings[0] -= .1;
    }
    else{
      this.oppBoltzRatings[0] += .1;
      this.oppBoltzRatings[1] -= .1;
    } 
  }
  
  private int[] countNextMoves(String history){
    int[] moveCount = {0,0,0};
    int i = 0;
    String search = "";
    if(history.length() > HISTORY_SEQ_LEN){
      search = history.substring(history.length()
                              - HISTORY_SEQ_LEN, history.length());
      i = history.indexOf(search, 0);
      
      while(i < history.length() - HISTORY_SEQ_LEN && i > -1){
        String nextMove = "" + history.charAt(i + HISTORY_SEQ_LEN);
        moveCount[Integer.parseInt(nextMove)]++;
        i = history.indexOf(search, i + 1);
      }
    }
    return moveCount;
  }
  
  private static List<Integer> getMaxIndices(int[] array){
    int max = 0;
    List<Integer> maxList = new ArrayList<Integer>();
    for (int i = 0; i < array.length; i++){
      if(array[i] > max)
        max = array[i];
    }
    for(int i = 0; i < array.length; i++){
      if(array[i] == max)
        maxList.add(i);
    }
    return maxList;   
  }
  
  private int getHistoryPrediction(String history){
    int[] moveCount = countNextMoves(history);
    Random rand = new Random();
    double r = rand.nextDouble();

    List<Integer> maxList = getMaxIndices(moveCount);
    for(int i = 0; i < maxList.size(); i++){
      if( r < (float)(i+1)/(float)maxList.size())
        return maxList.get(i);
    }
    return 0; //should never get here.
  }
  
  
  private void updateStrategies(){
    this.updateStrategyRatings();
    
    StrategyRecord[] strategyArray = new StrategyRecord[18];

    while (this.strategyQ.peek() != null){
      StrategyRecord s = this.strategyQ.poll();
      int index = 6 * s.baseStrategy + s.metaStrategy;

      strategyArray[index] = s;
    }

    strategyArray[0].prediction = getRandomPrediction();

    strategyArray[1].prediction = rotate(strategyArray[0].prediction, 2);
    strategyArray[2].prediction = rotate(strategyArray[1].prediction, 2);
    
    strategyArray[3].prediction = getRandomPrediction();
    strategyArray[4].prediction = rotate(strategyArray[3].prediction, 2);
    strategyArray[5].prediction = rotate(strategyArray[4].prediction, 2);
    
    strategyArray[6].prediction = getHistoryPrediction(this.oppMoves);
    strategyArray[7].prediction = rotate(strategyArray[6].prediction, 2);
    strategyArray[8].prediction = rotate(strategyArray[7].prediction, 2);
    
    strategyArray[9].prediction = getCounterMove(getHistoryPrediction(this.myMoves));
    strategyArray[10].prediction = rotate(strategyArray[9].prediction, 2);
    strategyArray[11].prediction = rotate(strategyArray[10].prediction, 2);
    
    this.updateBoltzRatings();
    
    strategyArray[12].prediction = getCounterMove(getBoltzPrediction(this.myBoltzRatings));
    strategyArray[13].prediction = rotate(strategyArray[12].prediction, 2);
    strategyArray[14].prediction = rotate(strategyArray[13].prediction, 2);
    
    strategyArray[15].prediction = getBoltzPrediction(this.oppBoltzRatings);
    strategyArray[16].prediction = rotate(strategyArray[15].prediction, 2);
    strategyArray[17].prediction = rotate(strategyArray[16].prediction, 2);
    
    for(int i = 0; i < 18; i++)
      this.strategyQ.add(strategyArray[i]);
  }
  
  public Action getNextMove(Action oppLastMove){
    if(this.myMoves.length() == 0)
      this.myMoves += 0;
    this.oppMoves += this.actionToNum.get(oppLastMove);
    this.updateStrategies();
    Random r = new Random();
    if(r.nextDouble() < RANDOM_MOVE_RATE){
      int randomMove = getRandomPrediction();
      this.myMoves += randomMove;
      return this.numToAction.get(randomMove);
    }
    
    this.myMoves += getCounterMove(this.predictNextMove());
    return this.numToAction.get(getCounterMove(this.predictNextMove()));
  }
}