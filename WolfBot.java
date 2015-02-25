
import java.util.*;
/** A Win or Learn Fast, rock paper scissors bot
  * 
  * @author Ross Kruse
  */
public class WolfBot implements RoShamBot {
    public Map<List<Integer>,List<Double>> qValues;
    public Map<List<Integer>,List<Double>> piValues;
    public Map<List<Integer>,List<Double>> avgPiValues;
    public Map<List<Integer>,Integer> cValues;
    public double alpha;
    public double deltaL;
    public double deltaW;
    public double gamma;
    public int numStates;
    public List<Integer> curState;
    public Action lastAction;

    /*The constructor for the WolfBot class, learning rates must be changed
     * at the top of the function
     * 
     * currently only works with one-state, to change number of states, 
     * must also be sure to create all enumerations of states
     */ 
    public WolfBot(){
        //Changing learning rates
        this.alpha = 0.1;
        this.deltaL = 0.2;
        this.deltaW = 0.1;
        this.gamma = 0.1;
        
        this.lastAction = Action.ROCK;
        //Number of previous states to remember
        this.numStates = 3;
        
        //Creating first Current State
        this.curState = new ArrayList<Integer>();
        this.curState.add(0);
        //Creating first "last move"
        this.lastAction = Action.ROCK;
        
        
        //Storage values
        this.qValues =  new HashMap<List<Integer>,List<Double>>();
        this.piValues =  new HashMap<List<Integer>,List<Double>>();
        this.cValues =  new HashMap<List<Integer>,Integer>();
        this.avgPiValues = new HashMap<List<Integer>,List<Double>>();
        List<Integer> tempList; 
        List<Double> zeroes;
        List<Double> piVal, piVal2;      
        
        for (int i = 0; i < this.numStates; i++){
            tempList = new ArrayList<Integer>();
            tempList.add(i);
            //Initilizing Q values
            zeroes = new ArrayList<Double>();
            zeroes.add(0.0);
            zeroes.add(0.0);
            zeroes.add(0.0);
            this.qValues.put(tempList, zeroes);
            
            //Initilizing Pi Values
            piVal = new ArrayList<Double>();
            double oneThird = 1.0/3.0;
            piVal.add(oneThird);
            piVal.add(oneThird);
            piVal.add(oneThird);
            this.piValues.put(tempList, piVal);
            
            //Initilizing Avg Pi Values
            piVal2 = new ArrayList<Double>();
            piVal2.add(0.0);
            piVal2.add(0.0);
            piVal2.add(0.0);
            this.avgPiValues.put(tempList, piVal2);
            
            //Initilizing C Values
            this.cValues.put(tempList, 0);
            
        } 
    }
    
    private Action pickAction(List<Integer> state){
        List<Double> actions = piValues.get(state);
        double choice = Math.random();
        double rockChance = actions.get(0);
        double papChance = actions.get(1);
        double sciChance = actions.get(2);
        
        if (choice < rockChance){
            this.lastAction = Action.ROCK;
            this.curState.set(0,0);
            return Action.ROCK;
        }
        else if(choice < (rockChance + papChance)){
            this.lastAction = Action.PAPER;
            this.curState.set(0,1);
            return Action.PAPER;
        }
        else{
            this.lastAction = Action.SCISSORS;
            this.curState.set(0,2);
            return Action.SCISSORS;
        } 
    }
    
    
    private Double getReward(Action lastOppMove){
        Action a1 = this.lastAction;
        Action a2 = lastOppMove;
        if (        ((a1 == Action.ROCK) && (a2 == Action.SCISSORS))
                 || ((a1 == Action.PAPER) && (a2 == Action.ROCK))
                || ((a1 == Action.SCISSORS) && (a2 == Action.PAPER))){
            return 1.0;
        }
        else if ((       (a2 == Action.ROCK) && (a1 == Action.SCISSORS))
                 || ((a2 == Action.PAPER) && (a1 == Action.ROCK))
                     || ((a2 == Action.SCISSORS) && (a1 == Action.PAPER))){
            return -2.0;
        }
        else{
            return -1.0;
        } 
    }
    
    private int getActionIndex(){
        if (this.lastAction == Action.ROCK){
            return 0;
        }
        else if (this.lastAction == Action.PAPER){
            return 1;
        }
        else{
            return 2;
        }
    }
    
    private void updateQScores(Action lastOpponentMove){
        
        int curActionIndex = this.getActionIndex();
        
        List<Double> currentQScores = this.qValues.get(this.curState);
        Double currentQ = currentQScores.get(curActionIndex);
        Double curReward = getReward(lastOpponentMove);
        Double maxNextQ = -10000000.0;
        
        for (int i =0; i < currentQScores.size(); i++){
            if (currentQScores.get(i) > maxNextQ){
                maxNextQ = currentQScores.get(i);
            }
        }
        //System.out.print("Reward");
        //System.out.println(curReward);
        
        currentQ = (1.0-this.alpha)*currentQ + this.alpha*
            (curReward + this.gamma*(maxNextQ));
        
        currentQScores.set(curActionIndex, currentQ);
        
    }
    
    private void updateAverage(Action lastOpponentMove){
        //updating C values
        int curCVal = cValues.get(this.curState);
        curCVal = curCVal + 1;
        cValues.put(this.curState , curCVal);
        
        List<Double> actionsAvg = this.avgPiValues.get(this.curState);
        List<Double> actions = this.piValues.get(this.curState);
        double curValue, curRealVal;
        for(int i = 0; i < actionsAvg.size(); i++){
           curValue = actionsAvg.get(i);
           curRealVal = actions.get(i);
           curValue = curValue + ((1.0/curCVal)*(curRealVal - curValue));
           
           actionsAvg.set(i, curValue);
        }
        
    }
    
    private void updatePiValues(Action lasOpponentMove){
        List<Double> actions = this.piValues.get(this.curState);
        List<Double> avgActions = this.avgPiValues.get(this.curState);
        double realSum = 0;
        double avgSum = 0;
        double maxValue = -10000000000.0;
        double curQVal, curValue, curAvgValue;
        int maxIndex = 0;
        List<Double> curQValues = this.qValues.get(this.curState);
        
        //Checking to see which Delta to use
        for(int i = 0; i < actions.size(); i++){
            curQVal = curQValues.get(i);
            curValue = actions.get(i);
            curAvgValue = avgActions.get(i);
            if (curQVal > maxValue){
                maxIndex = i;
                maxValue = curQVal;
            }
            
            realSum += curValue * curQVal;
            avgSum += curAvgValue * curQVal;
        }
        double curDelta;
        if (realSum > avgSum){
            //System.out.println("Winning");
            curDelta = this.deltaW;
        }
        else{
            //System.out.println("Losing");
            curDelta = this.deltaL;
        }
        
        //Finding current Index
        int curIndex = 0;
        if (this.lastAction == Action.ROCK){
            curIndex = 0;
        }
        else if (this.lastAction == Action.PAPER){
            curIndex = 1;
        }
        else{
            curIndex = 2;
        }
        //System.out.println(curIndex);
        //System.out.println(maxIndex);
        curValue = actions.get(curIndex);
        if (curIndex == maxIndex){
            curValue += curDelta;
        }
        else{
            curValue += ((curDelta *-1.0) / (actions.size() -1.0));
            curValue = Math.max(0.0, curValue);
        }
        
        actions.set(curIndex, curValue);
        
        this.normalizePiValues();

    }
    
    private void normalizePiValues(){
        List<Double> actions = this.piValues.get(this.curState);
        List<Double> avgActions = this.avgPiValues.get(this.curState);
        
        Double sum = 0.0;
        Double avgSum = 0.0;
        for (int i = 0; i<actions.size();i++){
            sum+=actions.get(i);
            avgSum += avgActions.get(i);
        }
        
        for(int i = 0; i<actions.size();i++){
            actions.set(i, (actions.get(i)/sum));
            avgActions.set(i, (avgActions.get(i)/avgSum));
            
        }
    }
    
    
    /** Returns the move that beats opponent's previous move
      * 
      * @param lastOpponentMove the action that was played by the opponent on
      *        the last round .
      * @return the next action to play.
      */
    public Action getNextMove(Action lastOpponentMove) {
        this.updateQScores(lastOpponentMove);
        this.updateAverage(lastOpponentMove);
        this.updatePiValues(lastOpponentMove);
        /*
        System.out.print("Current State ");
        System.out.println(this.curState);
        System.out.print("Pi Values ");
        System.out.println(this.avgPiValues);
        //System.out.print("Q Values ");
        //System.out.println(this.qValues);
        */
        
        return this.pickAction(this.curState);

            
            


    }
    
}