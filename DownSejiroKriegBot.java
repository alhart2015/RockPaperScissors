import java.util.*;
public class DownSejiroKriegBot implements RoShamBot {

    private ArrayList<Integer> theirMoves = new ArrayList<Integer>();
    private ArrayList<Integer> ourMoves = new ArrayList<Integer>();
    private int[] ourRock = new int[3];
    private int[] ourPaper = new int[3];
    private int[] ourScissors = new int[3];
    private int[][][] twoMove = new int[3][3][3];
    private ArrayList<Integer> oneList = new ArrayList<Integer>();
    private ArrayList<Integer> twoList = new ArrayList<Integer>();
    private ArrayList<Integer> threeList = new ArrayList<Integer>();
    private ArrayList<Integer> fiveList = new ArrayList<Integer>();
    private ArrayList<Integer> tenList = new ArrayList<Integer>();
    private double[] effective = new double[5]; 
    public Action getNextMove(Action lastOpponentMove) {
	int gameSize = ourMoves.size();
	collectStats(gameSize,lastOpponentMove);
	double fork = Math.random();
	int move = 0;
	int move1 = 0;

	move1 = randChoice();
	int[] moves = new int[5];
	if(gameSize >= 20) {
	    moves[0] = oneMove(gameSize);
	    moves[1] = twoMove(gameSize);
	    moves[2] = threeHist(gameSize);
	    moves[3] = fiveHist(gameSize);
	    moves[4] = tenHist(gameSize);
	    oneList.add(moves[0]);
	    twoList.add(moves[1]);
	    threeList.add(moves[2]);
	    fiveList.add(moves[3]);
	    tenList.add(moves[4]);
	}
	if(gameSize >= 21) updatePrediction();
	if(fork <= .4 || gameSize <= 500) {
	double coinFlip = Math.random();
	if(coinFlip < 0.05 || gameSize < 20) move = move1;
	else if(coinFlip >= 0.05 && coinFlip < 0.1) move = moves[0];
	else if(coinFlip >= 0.1 && coinFlip < 0.2) move = moves[1];
	else if(coinFlip >= 0.2 && coinFlip < 0.35) move = moves[2];
	else if((coinFlip >= 0.35 && coinFlip < 0.6) || move == 5) move = moves[3];
	else if(coinFlip >= 0.6 || move == 5) move = moves[4]; 
	if(move == 5 && coinFlip >= 0.6) move = moves[3];
	if(move == 5 && coinFlip >= 0.6) move = moves[2];
	if(move == 5) move = move1;
	}
	else {
	    int[] best = new int[3];
	    
	    
	    for(int i = 1;i < 5;i++) {
		if(effective[i] > effective[i-1]) best[0] = i;
	    }
	    for(int i = 1;i < 5;i++) {
		if(effective[i] > effective[i-1] && i != best[0]) best[1] = i;
	    }
	    for(int i = 1;i < 5;i++) {
		if(effective[i] > effective[i-1] && i != best[0] && i != best[1]) best[2] = i;
	    }
	    boolean stayIn = true;
	    int counter = 0;
	    while(stayIn) {
		move = moves[best[counter]];
		if(move != 5) stayIn = false;
		counter++;
	    }
	    
	    if(move == 5) move = move1;
	}
	ourMoves.add(move);
	if(move == 0) return Action.ROCK;
	if(move == 1) return Action.PAPER;
	return Action.SCISSORS;
    }
    private void updatePrediction() {
	int control = oneList.size() - 1;
	int[] winCount = new int[5];
	for(int i = 0;i < control;i++) {
	    
	    int p0 = oneList.get(i);
	    int p1 = twoList.get(i);
	    int p2 = threeList.get(i);
	    int p3 = fiveList.get(i);
	    int p4 = tenList.get(i);
	    int OM = theirMoves.get(i+20);
	    if(Arbitrate(OM,p0)) winCount[0]++;
	    if(Arbitrate(OM,p1)) winCount[1]++;
	    if(Arbitrate(OM,p2)) winCount[2]++;
	    if(Arbitrate(OM,p3)) winCount[3]++;
	    if(Arbitrate(OM,p4)) winCount[4]++;
	}
	for(int i = 0;i<5;i++) {
	    effective[i] = (double)winCount[i] / (double)control;
	}
	
    }

    private boolean Arbitrate(int opp, int us) {
	if((us == 0 && opp == 2) || (us == 1 && opp == 0) || (us == 2 && opp == 1)) return true;
	return false;
    }
    private int tenHist(int gameSize) {
	int[] OLTen = new int[10];
	int[] TLTen = new int[10];
	for(int i = 0;i < 10;i++) {
	    OLTen[9-i] = ourMoves.get(gameSize - 1 - i);
	    TLTen[9-i] = theirMoves.get(gameSize - 1 - i);
	}

	for(int i = 0; i < gameSize - 10;i++) {
	    if(ourMoves.get(i) == OLTen[0] && ourMoves.get(i+1) == OLTen[1] && 
	       ourMoves.get(i+2) == OLTen[2] && ourMoves.get(i+3) == OLTen[3] &&
	       ourMoves.get(i+4) == OLTen[4] && ourMoves.get(i+5) == OLTen[5] &&
	       ourMoves.get(i+6) == OLTen[6] && ourMoves.get(i+7) == OLTen[7] &&
	       ourMoves.get(i+8) == OLTen[8] && ourMoves.get(i+9) == OLTen[9] &&
	       theirMoves.get(i) == TLTen[0] && theirMoves.get(i+1) == TLTen[1] && 
	       theirMoves.get(i+2) == TLTen[2] && theirMoves.get(i+3) == TLTen[3] &&  
	       theirMoves.get(i+4) == TLTen[4] && theirMoves.get(i+5) == TLTen[5] && 
	       theirMoves.get(i+6) == TLTen[6] && theirMoves.get(i+7) == TLTen[7] && 
	       theirMoves.get(i+8) == TLTen[8] && theirMoves.get(i+9) == TLTen[9]) {
		int predicted = theirMoves.get(i+10);
		if(predicted == 0) return 1;
		if(predicted == 1) return 2;
		return 0;
	    }
	}
	return 5;
    }

    private int fiveHist(int gameSize) {
	int[] OLFive = new int[5];
	int[] TLFive = new int[5];
	for(int i = 0;i < 5;i++) {
	    OLFive[4-i] = ourMoves.get(gameSize - 1 - i);
	    TLFive[4-i] = theirMoves.get(gameSize - 1 - i);
	}

	for(int i = 0; i < gameSize - 5;i++) {
	    if(ourMoves.get(i) == OLFive[0] && ourMoves.get(i+1) == OLFive[1] && 
	       ourMoves.get(i+2) == OLFive[2] && ourMoves.get(i+3) == OLFive[3] &&
	       ourMoves.get(i+4) == OLFive[4] && theirMoves.get(i) == TLFive[0] &&
	       theirMoves.get(i+1) == TLFive[1] && theirMoves.get(i+2) == TLFive[2] &&
	        theirMoves.get(i+3) == TLFive[3] &&  theirMoves.get(i+4) == TLFive[4]) {
		int predicted = theirMoves.get(i+5);
		if(predicted == 0) return 1;
		if(predicted == 1) return 2;
		return 0;
	    }
	}
	return 5;

    }

    
    private int threeHist(int gameSize) {
	int[] OLThree = new int[3];
	int[] TLThree = new int[3];
	for(int i = 0;i < 3;i++) {
	    OLThree[2-i] = ourMoves.get(gameSize - 1 - i);
	    TLThree[2-i] = theirMoves.get(gameSize - 1 - i);
	}

	for(int i = 0; i < gameSize - 3;i++) {
	    if(ourMoves.get(i) == OLThree[0] && ourMoves.get(i+1) == OLThree[1] && 
	       ourMoves.get(i+2) == OLThree[2] && theirMoves.get(i) == TLThree[0] &&
	       theirMoves.get(i+1) == TLThree[1] && theirMoves.get(i+2) == TLThree[2]) {
		int predicted = theirMoves.get(i+3);
		if(predicted == 0) return 1;
		if(predicted == 1) return 2;
		return 0;
	    }
	}
	return 5;
    }

    private int twoMove(int gameSize) {
	if(ourMoves.get(gameSize - 1) == 0 && ourMoves.get(gameSize - 2) == 0) {
	    int total = twoMove[0][0][0] + twoMove[0][0][1] + twoMove[0][0][2];
	    double a = (double)twoMove[0][0][0] / (double)total;
	    double b = (double)twoMove[0][0][1] / (double)total + a;
	    double coinFlip = Math.random();
	    if(coinFlip < a) {
		return 1;
	    }
	    else if((coinFlip >= a) && (coinFlip < b)) {
		return 2;
	    }
	    else return 0;
	}

	if(ourMoves.get(gameSize - 1) == 0 && ourMoves.get(gameSize - 2) == 1) {
	    int total = twoMove[0][1][0] + twoMove[0][1][1] + twoMove[0][1][2];
	    double a = (double)twoMove[0][1][0] / (double)total;
	    double b = (double)twoMove[0][1][1] / (double)total + a;
	    double coinFlip = Math.random();
	    if(coinFlip < a) {
		return 1;
	    }
	    else if((coinFlip >= a) && (coinFlip < b)) {
		return 2;
	    }
	    else return 0;
	}

	if(ourMoves.get(gameSize - 1) == 0 && ourMoves.get(gameSize - 2) == 2) {
	    int total = twoMove[0][2][0] + twoMove[0][2][1] + twoMove[0][2][2];
	    double a = (double)twoMove[0][2][0] / (double)total;
	    double b = (double)twoMove[0][2][1] / (double)total + a;
	    double coinFlip = Math.random();
	    if(coinFlip < a) {
		return 1;
	    }
	    else if((coinFlip >= a) && (coinFlip < b)) {
		return 2;
	    }
	    else return 0;
	}

	if(ourMoves.get(gameSize - 1) == 1 && ourMoves.get(gameSize - 2) == 0) {
	    int total = twoMove[1][0][0] + twoMove[1][0][1] + twoMove[1][0][2];
	    double a = (double)twoMove[1][0][0] / (double)total;
	    double b = (double)twoMove[1][0][1] / (double)total + a;
	    double coinFlip = Math.random();
	    if(coinFlip < a) {
		return 1;
	    }
	    else if((coinFlip >= a) && (coinFlip < b)) {
		return 2;
	    }
	    else return 0;
	}

	if(ourMoves.get(gameSize - 1) == 1 && ourMoves.get(gameSize - 2) == 1) {
	    int total = twoMove[1][1][0] + twoMove[1][1][1] + twoMove[1][1][2];
	    double a = (double)twoMove[1][1][0] / (double)total;
	    double b = (double)twoMove[1][1][1] / (double)total + a;
	    double coinFlip = Math.random();
	    if(coinFlip < a) {
		return 1;
	    }
	    else if((coinFlip >= a) && (coinFlip < b)) {
		return 2;
	    }
	    else return 0;
	}

	if(ourMoves.get(gameSize - 1) == 1 && ourMoves.get(gameSize - 2) == 2) {
	    int total = twoMove[1][2][0] + twoMove[1][2][1] + twoMove[1][2][2];
	    double a = (double)twoMove[1][2][0] / (double)total;
	    double b = (double)twoMove[1][2][1] / (double)total + a;
	    double coinFlip = Math.random();
	    if(coinFlip < a) {
		return 1;
	    }
	    else if((coinFlip >= a) && (coinFlip < b)) {
		return 2;
	    }
	    else return 0;
	}

	if(ourMoves.get(gameSize - 1) == 2 && ourMoves.get(gameSize - 2) == 0) {
	    int total = twoMove[2][0][0] + twoMove[2][0][1] + twoMove[2][0][2];
	    double a = (double)twoMove[2][0][0] / (double)total;
	    double b = (double)twoMove[2][0][1] / (double)total + a;
	    double coinFlip = Math.random();
	    if(coinFlip < a) {
		return 1;
	    }
	    else if((coinFlip >= a) && (coinFlip < b)) {
		return 2;
	    }
	    else return 0;
	}

	if(ourMoves.get(gameSize - 1) == 2 && ourMoves.get(gameSize - 2) == 1) {
	    int total = twoMove[2][1][0] + twoMove[2][1][1] + twoMove[2][1][2];
	    double a = (double)twoMove[2][1][0] / (double)total;
	    double b = (double)twoMove[2][1][1] / (double)total + a;
	    double coinFlip = Math.random();
	    if(coinFlip < a) {
		return 1;
	    }
	    else if((coinFlip >= a) && (coinFlip < b)) {
		return 2;
	    }
	    else return 0;
	}


	if(ourMoves.get(gameSize - 1) == 2 && ourMoves.get(gameSize - 2) == 2) {
	    int total = twoMove[2][2][0] + twoMove[2][2][1] + twoMove[2][2][2];
	    double a = (double)twoMove[2][2][0] / (double)total;
	    double b = (double)twoMove[2][2][1] / (double)total + a;
	    double coinFlip = Math.random();
	    if(coinFlip < a) {
		return 1;
	    }
	    else if((coinFlip >= a) && (coinFlip < b)) {
		return 2;
	    }
	    else return 0;
	}
	return 0;
    }
    private int oneMove(int gameSize) {
	if(ourMoves.get(gameSize - 1) == 0) {
		int total = ourRock[0] + ourRock[1] + ourRock[2];
		double a = (double)ourRock[0] / (double)total;
		double b = (double)ourRock[1] / (double)total + a;
		
		double coinFlip = Math.random();
		if(coinFlip < a) {
		    return 1;
		}
		else if((coinFlip >= a) && (coinFlip < b)) {
		    return 2;
		}
		else return 0;
	    }

	    else if(ourMoves.get(gameSize - 1) == 1) {
		int total = ourPaper[0] + ourPaper[1] + ourPaper[2];
		double a = (double)ourPaper[0] / (double)total;
		double b = (double)ourPaper[1] / (double)total + a;
		
		double coinFlip = Math.random();
		if(coinFlip < a) {
		    return 1;
		}
		else if((coinFlip >= a) && (coinFlip < b)) {
		    return 2;
		}
		else return 0;
	    }

	    else {
		int total = ourScissors[0] + ourScissors[1] + ourScissors[2];
		double a = (double)ourRock[0] / (double)total;
		double b = (double)ourRock[1] / (double)total + a;
		
		double coinFlip = Math.random();
		if(coinFlip < a) {
		    return 1;
		}
		else if((coinFlip >= a) && (coinFlip < b)) {
		    return 2;
		}
		else return 0;
	    }
    }
    private int randChoice() {
	double coinFlip = Math.random();
	if(coinFlip < 1.0/3.0) return 0;
	else if(coinFlip < 2.0 / 3.0) return 1;
	return 2;
    }
    
    private void collectStats(int gameSize, Action lastOpponentMove) {
	if(gameSize == 0) return;
	int oppMove = 2;
	if(lastOpponentMove == Action.ROCK) {
	    theirMoves.add(0);
	    oppMove = 0;
	}
	else if(lastOpponentMove == Action.PAPER) {
	    theirMoves.add(1);
	    oppMove = 1;
	}
	else theirMoves.add(2);
	    
	if(gameSize > 1) {
	    int ourCause = ourMoves.get(gameSize - 2);

	    if(ourCause == 0) {
		ourRock[oppMove]++;
	    }
	    else if(ourCause == 1) {
		ourPaper[oppMove]++;
	    }
	    else {
		ourScissors[oppMove]++;
	    }
	}
	    
	if(gameSize > 2) {
	    int ourCause = ourMoves.get(gameSize - 2);
	    int secondCause = ourMoves.get(gameSize - 3);
	    twoMove[secondCause][ourCause][oppMove]++;
	}
	
    }



}
