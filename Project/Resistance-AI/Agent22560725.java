package cits3001_2021;

import java.util.*;
import java.io.*;

/**
 * A Java class for an agent to play in Resistance.
 * @author Tim French
 * **/


public class Agent22560725 implements Agent{

  private String name;
  private static int agentCount;
  private int players;//number of players
  private int[] playersList; //list of all players
  private int[] playersExceptMe; // all player except me
  private int playerIndex; // index of me
  private int[] spies; //all spies
  private boolean iAmSpy; // true if I am spy
  private int[] resistanceMembers; // all resistances, only created if I am spy
  private int resistancesNum; // number of resistances
  private int spyNum; // number of spies
  private static final int[] gamesSpyNum = {2,2,3,3,3,4};
  private static final int[][] gameMissionNum = {{2,3,2,3,3},{2,3,4,3,4},{2,3,3,4,4},{3,4,4,5,5},{3,4,4,5,5},{3,4,4,5,5}};
  //missionNum[n-5][i] is the number to send on mission i in a  in an n player game
  private HashMap<ArrayList<Integer>, Double> suspicion; // suspicion of all possible teams
  private HashMap<ArrayList<Integer>, Double> missionTeams; // possible mission teams based on suspicion
  private int currentLeader;
  private int missionNum;
  private int missionCompleted;
  private int[] currProposedTeam;
  private int proposals;
  private int failures;  // number of round lost
  private int minSpiesRequired; //assume minimum spies for a mission

  private PrintStream out;
  private boolean logging = false;

  private final double RANDOM_PLAY = 0.1;
  private final double RESISTANCE_VOTE_TRUE = 0.5;
  private final double VOTING_DUMB = 0.2;
  private final double BETRAY_DUNB = 0.2;
  /**
   *Creates a random agent with the given name
   *@param name, the name given to the agent and used on the scoreboard.
   **/
  public Agent22560725(String name){
    this.out = System.out;
    this.name = name;
  }

  /**
   * returns an instance of this agent for testing.
   * The programme should allocate the agent's name,
   * and can use a counter to ensure no two agents have the same name.
   * @return an instance of the agent.
   * **/
  public static Agent init(){
    switch(agentCount++){
      case 0: return new Agent22560725("Agent-22560725--1st");
      case 1: return new Agent22560725("Agent-22560725--2nd");
      case 2: return new Agent22560725("Agent-22560725--3rd");
      default: return new Agent22560725("Agent-22560725-"+agentCount+"th");
    }
  }

  private void write(String s){
    if(logging) out.println(s);
  }

  /**
   * gets the name of the agent
   * @return the agent's name.
   * **/
  public String getName(){return name;}


  /**
   * Initialises a new game. 
   * The agent should drop their current gameState and reinitialise all their game variables.
   * @param numPlayers the number of players in the game.
   * @param playerIndex the players index in the game.
   * @param spies, the index of all the spies in the game, if this agent is a spy (i.e. playerIndex is an element of spies)
   * **/
  public void newGame(int numPlayers, int playerIndex, int[] spies){

    this.players = numPlayers;
    this.playerIndex = playerIndex;
    this.playersList = new int[players];
    this.playersExceptMe = new int[players-1];
    this.proposals = 0;
    this.missionCompleted = 0;
    //get every players' index list and players except me
    int i = 0;
    int j = 0;
    while(i<players && j<players-1){
      playersList[i] = i;
      if(i != playerIndex){
        playersExceptMe[j] = i;
        j++;
      }
      i++;
    }
    this.spies = spies;
    this.spyNum = gamesSpyNum[numPlayers-5];
    this.resistancesNum = numPlayers-spyNum;
    this.iAmSpy = contain(spies, playerIndex);
    if(iAmSpy){ // if I am spy, I get all resistance players list
      this.resistanceMembers = diffInTwoArray(playersList, spies);
    }
    //get all possible combinations of spies
    this.suspicion = getPlayersCombination(playersList, spyNum);
    //initialize suspicion with equal probability for all combinations
    double initSuspicion = 1.0 / (double)nCr(players, spyNum);
    suspicion.replaceAll((k, v) -> initSuspicion);


    this.currentLeader = -1;
    this.missionNum = gameMissionNum[numPlayers-5][0];
    this.proposals = 0;
    this.failures = 0;
    this.minSpiesRequired = (missionNum == 4 && players >6) ? 2:1;


  }
      
  /**
   * This method is called when the agent is required to lead (propose) a mission
   * @param teamSize the number of agents to go on the mission
   * @param failsRequired the number of agent fails required for the mission to fail
   * @return an array of player indexes, the proposed mission.
   * **/
  public int[] proposeMission(int teamSize, int failsRequired){

    this.currentLeader = playerIndex;
    //get all possible mission teams
    HashMap<ArrayList<Integer>, Double> minSuspicionTeam;
    if(!iAmSpy){
      //only trust myself
      minSuspicionTeam = getPlayersCombination(playersExceptMe, teamSize-1);
    } else{
      //if only one spy to do mission is most secure
      //then I only trust myself
      if (minSpiesRequired == 1){
        minSuspicionTeam = getPlayersCombination(resistanceMembers, teamSize-1);
      } else{
        //otherwise, we want to go with the other spy (only one other spy with me)
        minSuspicionTeam = getPlayersCombination(resistanceMembers, teamSize -2);
        HashMap<ArrayList<Integer>, Double> minTeamWithOtherSpy = new HashMap<>();
        //add the other spy to each team
        for(int spy : spies){
          if(playerIndex != spy){
            minTeamWithOtherSpy = updateMapKey(minTeamWithOtherSpy, spy);
            break;
          }
        }
        minSuspicionTeam = myClone(minTeamWithOtherSpy);
      }
    }
    //add me to each team
    HashMap<ArrayList<Integer>, Double> minTeamWithMe = updateMapKey(minSuspicionTeam, playerIndex);

    //fill suspicion for all possible mission teams
    if(!iAmSpy){
      //(from the agent view)
      minSuspicionTeam = inspectAllTeams(minTeamWithMe, suspicion, minSpiesRequired,playerIndex);
    }else{
      //(from external view)
      minSuspicionTeam = inspectAllTeams(minTeamWithMe, suspicion, minSpiesRequired);
    }
    HashMap<ArrayList<Integer>, Double> sortedSuspicionTeam = sortMapByValue(minSuspicionTeam);
    //return the mission team with minimum suspicion
    ArrayList<Integer> teamArray = new ArrayList<>();
    for(Map.Entry<ArrayList<Integer>, Double> entry:sortedSuspicionTeam.entrySet()){
       teamArray =  entry.getKey();
       break;
    }
    return arrayListToarray(teamArray);
  }

  /**
   * This method is called when an agent is required to vote on whether a mission should proceed
   * @param mission the array of agent indexes who will be going on the mission.
   * @param leader the index of the agent who proposed the mission.
   * @return true is this agent votes that the mission should go ahead, false otherwise.
   * **/
  public boolean vote(int[] mission, int leader){
    this.proposals++;

    //Advanced models should propose high suspicions to confirm high suspicion
    // or low suspicions including leader self to get mission win
    this.currentLeader = leader;
    this.currProposedTeam = mission;
    //If I'm not leader, check my suspicion for the team proposed with performing Bayesian to update suspicions
    //P(suspicion keyList are spies | mission proposed by leader)
    double prior;
    double likelihood;
    double unnormPos;
    double totalProb = 0.0;
    double randomTeam = 1.0/(double) (nCr(players, currProposedTeam.length));
    ArrayList<Double> unnormPosteriors = new ArrayList<>(suspicion.size());
    int[] assumedSpies;
    //Iterate through all possibly spy combinations to find unnorm posterior possibility and total possibility
    for(Map.Entry<ArrayList<Integer>, Double> entry: suspicion.entrySet()){
      //Determine likelihood = P(mission proposed | suspicion keyList are spies)
      assumedSpies = commonInTwoArray(arrayListToarray(entry.getKey()), currProposedTeam);
      //Inherit prior possiblity from suspicion list's suspicion
      prior = entry.getValue();

      likelihood = RANDOM_PLAY * randomTeam;
      if(minSpiesRequired > 1 && contain(assumedSpies, leader)){
        // ensure the right number of spies for the mission
        int numCombinations = nCr(resistancesNum, currProposedTeam.length - minSpiesRequired)
                * nCr(spyNum-1, minSpiesRequired-1);
        //non-random play
        likelihood += (1.0 - RANDOM_PLAY) * (1.0/(double)(numCombinations));
      } else{
        //otherwise, play randomly
        likelihood += (1.0-RANDOM_PLAY) * (1.0/(double)(nCr(players-1, currProposedTeam.length-1)));
      }

      unnormPos = prior * likelihood;
      unnormPosteriors.add(unnormPos);
      totalProb += unnormPos;
    }

    //Got the total possibility and unormPosteriors, update prior with new calculated posterior
    int index = 0;
    for(Map.Entry<ArrayList<Integer>, Double> entry: suspicion.entrySet()){
      //Add posterior probability
      suspicion.put(entry.getKey(), unnormPosteriors.get(index) / totalProb);
      index++;
    }


    //mission team including me will have lower suspicion
    HashMap<ArrayList<Integer>, Double> missionTeamsUnsort;
    missionTeamsUnsort = getPlayersCombination(playersList, currProposedTeam.length);
    missionTeamsUnsort = inspectAllTeams(missionTeamsUnsort, suspicion, minSpiesRequired, playerIndex);
    missionTeams = sortMapByValue(missionTeamsUnsort);

    //now we have updated suspicion based on knowledge from voting status
    //then vote based on we have got before
    if(currentLeader == playerIndex || proposals == 4){
      return true;
    }
    //spies only consider the mission team whether it has enough spies to go
    //if not enough, rejected
    //if enough, means all spies can betray with minimum information leaked
    //if too much, only allow one to do betray
    if(iAmSpy){
      if(commonInTwoArray(spies, currProposedTeam).length < minSpiesRequired){
        return false;
      } else if(commonInTwoArray(spies, currProposedTeam).length == minSpiesRequired){
        return true;
      } else{
        if(failures == 2) return true;
        return contain(spies, currentLeader) && minSpiesRequired==1;
      }
    } else{
      //resistances consider each current mission team's suspicion whether it over the average
      suspicion = sortMapByValue(suspicion);
      double sumSuspicion = 0.0;
      for(Map.Entry<ArrayList<Integer>, Double> entry: missionTeams.entrySet()){
        sumSuspicion += entry.getValue();
      }
      double average = sumSuspicion / (double) players;
      Arrays.sort(currProposedTeam);
//      write("mission Team: "+missionTeams);
//      write(playerIndex+"- average"+ average + " propseTeam: " + Arrays.toString(currProposedTeam) + " missionTeam: "+ missionTeams);
      for(Map.Entry<ArrayList<Integer>, Double> entry: missionTeams.entrySet()){
        int[] key = arrayListToarray(entry.getKey());
        Arrays.sort(key);
        if(Arrays.equals(key, currProposedTeam)) return entry.getValue() < average;
      }
    }
    return true;
  }

  /**
   * The method is called on an agent to inform them of the outcome of a vote, 
   * and which agent voted for or against the mission.
   * @param mission the array of agent indexes represent the mission team
   * @param leader the agent index of the leader, who proposed the mission
   * @param votes an array of booleans such that votes[i] is true if and only if agent i voted for the mission to go ahead.
   * **/
  public void voteOutcome(int[] mission, int leader, boolean[] votes){
    //implement Bayesian to update suspicion
    double prior;
    double likelihood;
    double unnormPos;
    double totalProb = 0.0;
    ArrayList<Double> unnormPosteriors = new ArrayList<>(suspicion.size());

    //get the votes true and false list
    ArrayList<Integer> truesList = new ArrayList<>();
    ArrayList<Integer> falsesList = new ArrayList<>();
    for(int i=0; i<votes.length; i++){
      if(votes[i]) truesList.add(i);
      else falsesList.add(i);
    }
    int[] trues = arrayListToarray(truesList);
    int[] falses = arrayListToarray(falsesList);
    int[] assumedSpies;

    for(Map.Entry<ArrayList<Integer>,Double> entry: suspicion.entrySet()){
      assumedSpies = commonInTwoArray(currProposedTeam, arrayListToarray(entry.getKey()));
      int assumedSpiesNum = assumedSpies.length;
      prior = entry.getValue(); //inherit last posterior
      //default assuming spies vote for false
      boolean spiesVote = false;
      double resistances_vote_true = RESISTANCE_VOTE_TRUE;

      //if it is the last proposal, assume same likelihood
      //since spies and resistance both likely make dumb vote
      if(proposals == 4){
        spiesVote = true;
        resistances_vote_true = 1-VOTING_DUMB;
      } else{
        //spies do not vote when spies not enough
        //spies likely to vote when spies just equal to minrequired
        //if much and leader in the mission team, should vote one
        if(assumedSpiesNum < minSpiesRequired){
          spiesVote = false;
        } else if(assumedSpiesNum == minSpiesRequired){
          spiesVote = true;
        } else{
          if(failures == 2){
            spiesVote = true;
          } else if(entry.getKey().contains(currentLeader) && minSpiesRequired == 1){
            spiesVote = true;
          }
        }
      }

      //likelihood that a spy will vote true when it should be voting true
      double spy_vote_true = spiesVote ? 1.0-VOTING_DUMB:VOTING_DUMB;
      //likelihood is product of the independent likelihoods for each player
      likelihood = 1.0;
      for(int vote_true: trues){
        if(entry.getKey().contains(vote_true)) likelihood *= spy_vote_true;
        else likelihood *= resistances_vote_true;
      }
      for(int vote_false: falses){
        if(entry.getKey().contains(vote_false)) likelihood *= 1.0-spy_vote_true;
        else likelihood *= 1.0-resistances_vote_true;
      }

      unnormPos = prior * likelihood;
      unnormPosteriors.add(unnormPos);
      totalProb += unnormPos;
    }

    int index = 0;
    for(Map.Entry<ArrayList<Integer>, Double> entry: suspicion.entrySet()){
      suspicion.put(entry.getKey(), unnormPosteriors.get(index) / totalProb);
      index++;
    }
//    write(playerIndex + "- suspicion 3:" + suspicion);

  }

  /**
  * This method is called on an agent who has a choice to betray (fail) the mission
  * @param mission the array of agent indexes representing the mission team
  * @param leader the agent who proposed the mission
  * @return true is the agent chooses to betray (fail) the mission
  * **/
  public boolean betray(int[] mission, int leader){
    int spiesOnMission = commonInTwoArray(spies, mission).length;
    if(!iAmSpy) {return false;}
    if(spiesOnMission < minSpiesRequired) {return false;}
    else if(spiesOnMission == minSpiesRequired) {return true;}
    else{
      if(failures == 2){return true;}
      if(missionNum - failures == 3){return true;}
      if(missionCompleted == missionNum-1){return true;}
      if(currentLeader == playerIndex && minSpiesRequired == 1){return true;}
      return currentLeader != playerIndex && (spiesOnMission - minSpiesRequired == 1)
              && contain(spies, currentLeader);
    }
  }

  /**
  * Informs all agents of the outcome of the mission, including the number of agents who failed the mission.
  * @param mission the array of agent indexes representing the mission team
  * @param leader the agent who proposed the mission
  * @param numFails the number of agent's who failed the mission
  * @param missionSuccess true if and only if the mission succeeded.
  * **/
  public void missionOutcome(int[] mission, int leader, int numFails, boolean missionSuccess){
    if(!missionSuccess) failures++;
    //implement Bayesian to update suspicion
    double prior;
    double likelihood;
    double unnormPos;
    double totalProb = 0.0;
    ArrayList<Double> unnormPosteriors = new ArrayList<>(suspicion.size());
    int[] assumedSpies;
    //if one betray exists, need to add suspicion for all teams included him
    for(Map.Entry<ArrayList<Integer>,Double> entry: suspicion.entrySet()){
      assumedSpies = commonInTwoArray(mission, arrayListToarray(entry.getKey()));
      int assumedSpiesNum = assumedSpies.length;
      prior = entry.getValue();
      //assume not to vote as voting to betray gives up information
      boolean leaderBetray = false;
      boolean nonLeaderBetray = false;
      //Pre-assumed, but it still possible for spay to betray when should not
      if(assumedSpiesNum < minSpiesRequired){
        leaderBetray = false;
        nonLeaderBetray = false;
      } else if(assumedSpiesNum == minSpiesRequired){
        leaderBetray = true;
        nonLeaderBetray = true;
      } else{
        if(failures == 2){
          leaderBetray = true;
          nonLeaderBetray = true;
        } else if(missionNum - failures == 3){
          leaderBetray = true;
          nonLeaderBetray = true;
        } else if(entry.getKey().contains(currentLeader)){
          if(minSpiesRequired == 1){
            leaderBetray = true;
            nonLeaderBetray = false;
          } else if(assumedSpiesNum - minSpiesRequired == 1){
            leaderBetray = false;
            nonLeaderBetray = true;
          }
        }
      }

      if(!entry.getKey().contains(currentLeader)){
        double betrayProb = nonLeaderBetray ? 1.0-BETRAY_DUNB : BETRAY_DUNB;
        likelihood = Math.pow(betrayProb, numFails) *
                Math.pow(1.0-betrayProb, assumedSpiesNum-numFails) * nCr(assumedSpiesNum, numFails);
      } else{
        double leaderBetrayProb = leaderBetray ? 1.0-BETRAY_DUNB : BETRAY_DUNB;
        double nonLeaderBetrayProb = nonLeaderBetray ? 1.0-BETRAY_DUNB: BETRAY_DUNB;

        likelihood = 0.0;
        likelihood += leaderBetrayProb * Math.pow(nonLeaderBetrayProb, numFails-1) *
                Math.pow(1.0-nonLeaderBetrayProb, assumedSpiesNum-numFails) * nCr(assumedSpiesNum-1, numFails-1);
        likelihood += (1-leaderBetrayProb) * Math.pow(nonLeaderBetrayProb, numFails) *
                Math.pow(1.0-nonLeaderBetrayProb, assumedSpiesNum-numFails-1) * nCr(assumedSpiesNum-1, numFails);
      }

      unnormPos = prior * likelihood;
      unnormPosteriors.add(unnormPos);
      totalProb += unnormPos;
    }
    int index = 0;
    for(Map.Entry<ArrayList<Integer>, Double> entry: suspicion.entrySet()){
      suspicion.put(entry.getKey(), unnormPosteriors.get(index) / totalProb);
      index++;
    }
    suspicion = sortMapByValue(suspicion);
//    write(playerIndex + "- mission outcome suspicion 4: " + suspicion);
  }

  /**
  * Informs all agents of the game state at the end of the round
  * @param roundsComplete the number of rounds played so far
  * @param roundsLost the number of rounds lost so far
  * **/
  public void roundOutcome(int roundsComplete, int roundsLost){
    this.failures = roundsLost;
    this.missionCompleted = roundsComplete;
    proposals = 0;
  }
    

  /**
  * Informs all agents of the outcome of the game, including the identity of the spies.
  * @param roundsLost the number of rounds the Resistance lost
  * @param spies an array with the indexes of all the spies in the game.
  * **/
  public void gameOutcome(int roundsLost, int[] spies){
  }

  /**
   * Helper function for proposeMission
   * finds the intersection of 2 arrays
   * @param large simply assume the fist array must longer than second
   * @param small same as above
   * @return the intersection of large and small arrage
   * */
  private static int[] commonInTwoArray(int[] large, int[] small){
    ArrayList<Integer> result = new ArrayList<>();
    if (large.length < small.length) return small;
    if(small.length == 0) {
      return large;
    } else{
      for (int k : large) {
        boolean found = false;
        for (int i : small) {
          if (k == i) {
            found = true;
            break;
          }
        }
        if (found) {
          result.add(k);
        }
      }
    }
    return arrayListToarray(result);
  }

  /**
   * Helper function to determine large array - small array
   * @param large simply assume the fist array must longer than second
   * @param small same as above
   * @return the remaining large array after removing small array inside
   * */
  private static int[] diffInTwoArray(int[] large, int[] small){
    ArrayList<Integer> result = new ArrayList<>();
    if(small.length == 0){
      return large;
    }else{
      for (int k : large) {
        boolean found = false;
        for (int i : small) {
          if (k == i) {
            found = true;
            break;
          }
        }
        if (!found) {
          result.add(k);
        }
      }
    }
    return arrayListToarray(result);
  }

  /**
   * Helper function to build all combinations of players with specified size
   * @param players all players list
   * @param size the size of each combination
   * @return all combination with size in players
   * */
  private static HashMap<ArrayList<Integer>, Double> getPlayersCombination(int[] players, int size){
    HashMap<ArrayList<Integer>, Double> suspicion = new HashMap<>();
    int num = players.length;
    for(int i=0; i<num; i++){
      ArrayList<Integer> combination = new ArrayList<>();
      combination.add(players[i]);
      if(size == 1){suspicion.put(combination, 0.0);}
      else{ //size>=2
        for(int j=i+1; j<num; j++){
          ArrayList<Integer> combination1 = (ArrayList) combination.clone();
          combination1.add(players[j]);
          if(size == 2){suspicion.put(combination1, 0.0);}
          else{ //size>=3
            for(int m=j+1; m<num; m++){
              ArrayList<Integer> combination2 = (ArrayList) combination1.clone();
              combination2.add(players[m]);
              if(size == 3){suspicion.put(combination2, 0.0);}
              else{ //size>=4
                for(int n=m+1; n<num; n++){
                  ArrayList<Integer> combination3 = (ArrayList) combination2.clone();
                  combination3.add(players[n]);
                  if(size == 4){suspicion.put(combination3, 0.0);}
                  else{ //size==5
                    for(int k=n+1; k<num; k++){
                      ArrayList<Integer> combination4 = (ArrayList) combination3.clone();
                      combination4.add(players[k]);
                      suspicion.put(combination4, 0.0);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return suspicion;
  }

  /**
   * Inspect all teams with all players
   * Helper function to determine a possible team's suspicion based on current probabilities
   * @param allPossibleTeams all possible teams to consider, assumes all suspicion is 0.0
   * @param suspicions all suspicion, mostly same as global suspicion
   * @param minSpiesRequired same as global minSpiesRequired
   * */
  private static HashMap<ArrayList<Integer>, Double> inspectAllTeams
          (HashMap<ArrayList<Integer>, Double> allPossibleTeams, HashMap<ArrayList<Integer>, Double> suspicions, int minSpiesRequired){
    return inspectAllTeams(allPossibleTeams, suspicions, minSpiesRequired,Integer.MAX_VALUE);
  }

  /**
   * Inspect all teams with all players
   * Helper function to determine a possible team's suspicion based on current probabilities
   * @param allPossibleTeams all possible teams to consider, assumes all suspicion is 0.0
   * @param suspicions all suspicion, mostly same as global suspicion
   * @param minSpiesRequired same as global minSpiesRequired
   * @param excludePerson the index if player who want to exclude from suspicion, if Infinite, do not exclude anyone
   * */
  private static HashMap<ArrayList<Integer>, Double> inspectAllTeams
          (HashMap<ArrayList<Integer>, Double> allPossibleTeams, HashMap<ArrayList<Integer>, Double> suspicions, int minSpiesRequired, int excludePerson){

    double normalisation = 1.0;
    //exclude this person
    if(excludePerson != Integer.MAX_VALUE){
      normalisation = 0.0;
      for(Map.Entry<ArrayList<Integer>, Double> entry: suspicions.entrySet()){
        //check if minimum spies present
        if(!entry.getKey().contains(excludePerson)){
          //accumulate the suspicion and normalise
          normalisation += entry.getValue();
        }
      }
    }
    HashMap<ArrayList<Integer>, Double> allPossibles = myClone(allPossibleTeams);
    for(Map.Entry<ArrayList<Integer>, Double> possibleTeam: allPossibleTeams.entrySet()){
      for(Map.Entry<ArrayList<Integer>, Double> possibleSpyTeam: suspicions.entrySet()){
        if(excludePerson != Integer.MAX_VALUE && possibleSpyTeam.getKey().contains(excludePerson)){continue;}

        int possibleSpies = commonInTwoArray(arrayListToarray(possibleTeam.getKey()), arrayListToarray(possibleSpyTeam.getKey())).length;
        if(possibleSpies >= minSpiesRequired){
          double newSuspicion = possibleTeam.getValue() + possibleSpyTeam.getValue() / normalisation;
          allPossibles.put(possibleTeam.getKey(), newSuspicion);
        }
      }
      }
    return allPossibles;
  }

  /**
   * Helper function for nCr
   * Calculate the factorial of n
   */
  private int factorial(int n){
    if(n<0){
      return -1;
    }
    if(n==0){
      return 1;
    }
    int result = 1;
    for(int i=n; i>0; i--){
      result *= i;
    }
    return result;
  }

  /**
   * Prabability function helper, determines how many unique options of size r can be extracted from size n
   * @param n the pool size which wants to be extracted
   * @param r the size of combinations being looked for inside of n
   * @return number of unique options possible
   */
  private int nCr(int n, int r){
    if(r<0 || r>n){return 0;}
    return factorial(n) / (factorial(r) * factorial(n-r));
  }

  /**
   * Update hash map key for the map key is arrayList with same value
   * @param map Hashmap
   * @param keyAdd add this in each keys arrayList
   * @return new independent Hashmap
   */
  private static HashMap<ArrayList<Integer>, Double> updateMapKey(HashMap<ArrayList<Integer>, Double> map, int keyAdd){
    HashMap<ArrayList<Integer>, Double> copy = myClone(map);

    for(Map.Entry<ArrayList<Integer>, Double> entry: copy.entrySet()){
      ArrayList<Integer> key = entry.getKey();
      key.add(keyAdd);
    }
    return copy;
  }

  /**
   * Check array whether contains value or not
   * @return True if contains, otherwise, False
   */
  private static Boolean contain(int[] array, int value){
    boolean res = false;
    for(int item: array){
      if(value == item){
        res = true;
        break;
      }
    }
    return res;
  }

  /**
   * Convert arrayList to array
   */
  private static int[] arrayListToarray(ArrayList<Integer> array){
    return array.stream().mapToInt(i->i).toArray();
  }

  /**
   * Copy any object without referencing
   */
  private static <T extends Serializable> T myClone(T obj) {
    T clonedObj = null;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(obj);
      oos.close();

      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bais);
      clonedObj = (T) ois.readObject();
      ois.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return clonedObj;
  }

  /**
   * sort hashmap based on the value
   */
  private static HashMap<ArrayList<Integer>, Double> sortMapByValue(HashMap<ArrayList<Integer>, Double> UnsortMap){
    List<Map.Entry<ArrayList<Integer>, Double>> list = new ArrayList<>(UnsortMap.entrySet());
    list.sort(Map.Entry.comparingByValue());
    HashMap<ArrayList<Integer>, Double> map = new LinkedHashMap<>();
    for(HashMap.Entry<ArrayList<Integer>, Double> entry: list){
      map.put(entry.getKey(), entry.getValue());
    }
    return map;
  }

}
