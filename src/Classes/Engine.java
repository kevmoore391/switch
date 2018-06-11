package Game;

import gui.GameGUI;
import gui.Table;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Collections;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;


public class Engine{
	public ArrayList<Player> myPlayers;
	public ArrayList<Card> myDeck;
	private TableCards theTable;
	private Deck newDeck;
	private String name;
	private int playerTurn;
	private boolean userPlayedAce;
	private boolean clockwise;
	private boolean gameActive;
	private boolean userPlayed;
	private boolean pickUpModeOn;
	private int pickUpCount;
	private int currentSuit;
	private int missTurn;
	private int playerNo;
	private int tempSuitValue;
	public ActivityLogger logger;
	private Card joker;
	private int jokerPosition;
	
	private String username;
	private boolean active;
	
	private static BufferedImage winImg; //declares splash image
	private static BufferedImage lossImg; //declares splash image

	private Database db;
	private String loggedusername;
	private Long loggedgamesPlayed;
	private Long loggedgamesWon;
	private BigDecimal loggedoverall;
	private Long userId;
	
	private Connection connection;
	private PreparedStatement statement;
	private ResultSet resultSet;
	

	public Engine() {

		
		this.logger = new ActivityLogger();
		this.myPlayers = new ArrayList();
		this.myDeck = new ArrayList();
		this.theTable = new TableCards();
		this.newDeck = new Deck();
		this.userPlayedAce = false;
		this.pickUpModeOn = false;
		this.userPlayed = false;
		this.gameActive = true;
		this.clockwise = true;
		this.currentSuit = 4;
		this.pickUpCount = 0;
		this.playerTurn = 0;
		this.missTurn = 0;
	}

	public void start(int NumOfP, Long userId, String username, boolean active) {
		
		this.myDeck = this.newDeck.createNewDeck();
		this.userId = userId;
		this.username = username;
		this.active = active;
		
		getPlayerScores(userId, username);
		
		if ((username != "Player") || (active == true))
		{
			loggedgamesPlayed = loggedgamesPlayed + 1L;
			
			BigDecimal play = new BigDecimal(loggedgamesPlayed);
	        BigDecimal won = new BigDecimal(loggedgamesWon);     
	        loggedoverall = (won.divide(play, 9, RoundingMode.HALF_UP)); //used the games played and games won values to calculate overall score
			
			updateDatabase(); //updates games played and percentage in database
			
		}

		
		playerNo = (NumOfP + 1); // gets number of
								// players for game

		for (int player = 0; player < playerNo; player++) {
			ArrayList myHand = new ArrayList();
			for (int card = 0; card < 7; card++) {
				Card moveCard = (Card) this.myDeck.get(0);
				this.myDeck.remove(0);
				myHand.add(moveCard);
			}
			if (player == 0) {
				
		//		String playerName = "none";
				
				Player newPlayer = new Player(myHand, username , player);
				this.myPlayers.add(newPlayer);
				
			} else {

				Player newPlayer = new Player(myHand, username, player);
				newPlayer.setName("Comp " + (player));
				this.myPlayers.add(newPlayer);
			}
		}

	}


	public void determineFirstPlayer() {
		
		CheckTrick trickChecker = new CheckTrick();
		int nc = 0;
		boolean proceed = false;
		
		Card firstCard = this.myDeck.get(nc);
		
		do{
		if (trickChecker.CheckTrick(firstCard) == true){
			nc++;
			firstCard = this.myDeck.get(nc);
		} else {
			proceed = true;
		}
		}while(proceed == false);
		
		
		this.myDeck.remove(nc);
		this.theTable.addToTable(firstCard);
		Card topCard = this.theTable.getTopTableCard();
		//int firstPlay = topCard.getValue();
		//while (firstPlay > 3) {
		//	firstPlay -= 4;
		//}
		this.playerTurn = 0;

	}

	public boolean userTurn(int cardPos) {
		boolean played = false;
		if (!this.pickUpModeOn) {
			int onlyValue = 0;
			if (this.userPlayed) {
				onlyValue = this.theTable.getTopTableCard().getValue();
			}
			played = checkFirstCard((Card) ((Player) this.myPlayers.get(0)).myHand.get(cardPos));
		} else {
			played = userTurnPickUpMode(cardPos);
		}

		return played;
	}

	public boolean userTurnPickUpMode(int cardPos) {
		boolean played = false;
		int cardValue = ((Player) this.myPlayers.get(0)).getCard(cardPos).getValue();
		int cardSuit = ((Player) this.myPlayers.get(0)).getCard(cardPos).getSuit();
		if ((cardValue == 1) || (cardValue == 2)|| ((cardValue == 12)&&(cardSuit == 1))||((cardValue == 12)&&(cardSuit == 2)) ) {
			played = true;
		}
		if ((cardValue == 0)&&(cardSuit ==3)&& (!this.userPlayed)) {
			played = true;
		}
		return played;
	}

	public boolean checkCard(Card checkCard, Card previousCard) {
		boolean valid = false;
		if (checkCard.getValue() == previousCard.getValue()) {
			valid = true;
		}

		return valid;
	}

	public boolean checkFirstCard(Card checkCard) {

		boolean valid = false;
		int myVal = checkCard.getValue();
		int mySuit = checkCard.getSuit();
		int tableVal = this.theTable.getTopTableCard().getValue();
		int tableSuit = this.theTable.getTopTableCard().getSuit();

		if (((myVal == 0) && (this.pickUpModeOn() == false)) ||((myVal == 0) && (mySuit == 3)) || (myVal == tableVal)|| ((!isAceChanged()) && (mySuit == tableSuit) && (this.pickUpModeOn() == false))|| ((isAceChanged()) && (mySuit == this.currentSuit) && (this.pickUpModeOn() == false))) {
			this.currentSuit = 4;
			valid = true;
		} else {
			valid = false;
		}
		
		return valid;
	}

	public void compTurn(){

		
		Player playerInTurn = (Player) this.myPlayers.get(this.playerTurn);
		int handCards = playerInTurn.countHandCards();
		CheckTrick cT = new CheckTrick();
		if((handCards==1)&&(cT.CheckTrick(playerInTurn.myHand.get(0)))){
			int draw = 1;
			draw(draw);
			endTurn();
		}else{
		Random random = new Random();
		boolean suitFirst = random.nextBoolean();
		if (this.gameActive) {
			if (this.pickUpModeOn) {

				compTurnPickUpManager(handCards);
			} else {
				compTurnRegularManager(suitFirst, handCards);
			}
		}}
		
	}
	
	public boolean compCheckForJoker(){
		boolean haveJoker = false;
		
		for(int i = 0; i < this.myPlayers.get(this.playerTurn).countHandCards(); i++){
			int currentCardValue = this.myPlayers.get(playerTurn).getCard(i).getValue();
			// Check current cards value against jokers values
			if(currentCardValue == (13)){
				haveJoker = true;
				joker = this.myPlayers.get(playerTurn).getCard(i);
				jokerPosition = i;
			}
		}
		
		return haveJoker;
	}

	public void compTurnRegularManager(boolean suitFirst, int handCards) {

		boolean play = false;
		for (int i = handCards - 1; i >= 0; i--) {
			play = compRegModeChecker(i, play, suitFirst);
		}

		if (!play) {
			if(compCheckForJoker() == true){
				// Get current player
				Player playerInTurn = (Player) this.myPlayers.get(this.playerTurn);
				// change joker values to table cards values and make note in log
				int previousCardValue = this.theTable.getTopTableCard().getValue();
				int previousCardSuit = this.theTable.getTopTableCard().getSuit();
				joker.setSuit(previousCardSuit);
				joker.setValue(previousCardValue);
				joker.setSymbol(previousCardSuit);
				this.logger.addToLog(playerInTurn.getName() + " has played a Joker as the " + joker.toString());
				playCard(joker, jokerPosition);
				endTurn();
			} else {
				draw(1);
				endTurn();
			}
		} else {
			checkCompPlayMulti();
			endTurn();
		}
	}
	


	public boolean compRegModeChecker(int index, boolean play, boolean suitFirst) {

		if ((!play) && (suitFirst)) {
			play = compCheckSuitMatch(index);
			if (!play) {
				play = compCheckValueMatch(index);
			}
		} else if ((!play) && (!suitFirst)) {
			play = compCheckValueMatch(index);
			if (!play) {
				play = compCheckSuitMatch(index);
			}
		}
		return play;
	}
	
	public void compTurnPickUpManager(int handCards) {

		boolean play = false;
		play = compPickUpModeAttack(handCards);
		if (!play) {
			play = compPickUpModeBlock(handCards);
		}
		if (play) {
			endTurn();
		} else {
			draw(this.pickUpCount);
			this.pickUpModeOn = false;
			endTurn();
		}
	}

	public boolean compCheckSuitMatch(int checkSpot) {

		Player playerInTurn = (Player) this.myPlayers.get(this.playerTurn);
		Card tableCard = this.theTable.getTopTableCard();
		Card handCard = playerInTurn.getCard(checkSpot);
		boolean play = false;
		if ((isAceChanged()) && (this.currentSuit == handCard.getSuit())) {
			this.currentSuit = 4;
			playCard(handCard, checkSpot);
			
			play = true;
		} else if ((!isAceChanged())
				&& (tableCard.getSuit() == handCard.getSuit())) {
			playCard(handCard, checkSpot);
			play = true;
		}
		return play;
	}

	public boolean compCheckValueMatch(int checkSpot) {
		Player playerInTurn = (Player) this.myPlayers.get(this.playerTurn);
		Card tableCard = this.theTable.getTopTableCard();
		boolean play = false;
		Card handCard = playerInTurn.getCard(checkSpot);
		if (tableCard.getValue() == handCard.getValue()) {
			playCard(handCard, checkSpot);
			play = true;
		}
		return play;
	}

	public void checkCompPlayMulti() {
		int compPlayerVal = this.theTable.getTopTableCard().getValue();
		Player playerInTurn = (Player) this.myPlayers.get(this.playerTurn);
		for (int i = playerInTurn.countHandCards() - 1; i >= 0; i--) {
			if (playerInTurn.getCard(i).getValue() == compPlayerVal) {
				playCard(playerInTurn.getCard(i), i);
			}
		}
	}

	public boolean compPickUpModeAttack(int handCards) {
		Player playerInTurn = (Player) this.myPlayers.get(this.playerTurn);
		Card previous = this.theTable.getTopTableCard();
		boolean play = false;
		for (int i = handCards - 1; i >= 0; i--) {
			Card selectedCard = playerInTurn.getCard(i);
			if (((selectedCard.getValue() == 1) && (previous.getValue() == 1))||
				((selectedCard.getValue() == 2) && (previous.getValue() == 2))||
				((selectedCard.getValue() == 12)&&(selectedCard.getSuit()==1) && (previous.getValue() == 12))||
				((selectedCard.getValue() ==12) && (selectedCard.getSuit()==2) && (previous.getValue() == 12))) {
				playCard(selectedCard, playerInTurn.myHand.indexOf(playerInTurn.getCard(i)));
				play = true;
			}
		}
		return play;
		}

	public boolean compPickUpModeBlock(int handCards) {
		Player playerInTurn = (Player) this.myPlayers.get(this.playerTurn);
		Card previous = this.theTable.getTableCard();
		boolean play = false;
		for (int i = handCards - 1; i >= 0; i--) {
			Card selectedCard = playerInTurn.getCard(i);
			if ((selectedCard.getValue() == 0) && (selectedCard.getSuit()==3)) {
				playCard(selectedCard,
				playerInTurn.myHand.indexOf(playerInTurn.getCard(i)));
				this.pickUpModeOn = false;
				this.pickUpCount = 0;
				play = true;
			}
		}
		return play;
	}

	public void compAceChange() {
		int change = 0;
		if (!getPlayer().myHand.isEmpty()) {
			change = ((Card) getPlayer().myHand.get(0)).getSuit();
		}
		for (int i = 0; i < 4; i++) {
			if (change == i) {
				String[] suits = { "Clubs", "Diamonds", "Hearts", "Spades" };
				this.currentSuit = i;
				tempSuitValue = i;
				this.logger.addToLog(this.myPlayers.get(this.getPlayerInTurn()).getName() + " has changed the suit to " + suits[i]);
			}
		}
	}

	public void playCard(Card playThis, int pos) {
		Player playerInTurn = (Player) this.myPlayers.get(this.playerTurn);
		checkEffect(playThis, playerInTurn);
		this.theTable.addToTable(playThis);
		playerInTurn.removeCard(pos);
		int cardsLeft = playerInTurn.countHandCards();
		this.logger.addToLog(playerInTurn.getName() + " played "+ playThis.getUnicodeValueAndSuit() + "\n"
							+ playerInTurn.getName() + " has " + cardsLeft+ " cards left");
		
	}
	
	public void playUserCard(Card playThis) {
		Player playerInTurn = (Player) this.myPlayers.get(this.playerTurn);
		checkEffect(playThis, playerInTurn);
		this.theTable.addToTable(playThis);
		int cardsLeft = playerInTurn.countHandCards();
		this.logger.addToLog(playerInTurn.getName() + " played "+ playThis.getUnicodeValueAndSuit() + "\n"
				+ playerInTurn.getName() + " has " + (cardsLeft - 1)+ " cards left");

		
	}

	public void draw(int times) {
		Player playerInTurn = (Player) this.myPlayers.get(this.playerTurn);
		int initialCards = playerInTurn.countHandCards();
		for (int i = 0; i < times; i++) {
			reStockDeck();
			Card drawCard = (Card) this.myDeck.get(0);
			this.myDeck.remove(0);
			playerInTurn.addCard(drawCard);
		}
		if (this.pickUpModeOn) {
			this.pickUpModeOn = false;
			this.logger.addToLog(playerInTurn.getName()+ " picked up " + this.pickUpCount + " cards.\n"
								+ playerInTurn.getName() + " has "+ playerInTurn.countHandCards()+ " cards left.");

			this.pickUpCount = 0;
		} else {
			this.logger.addToLog(playerInTurn.getName()+ " picked up 1 card.\n"
								+ playerInTurn.getName() + " has "+ playerInTurn.countHandCards()+ " cards left.");
		}

	}

	public void endTurn() {
		this.userPlayed = false;
		this.userPlayedAce = false;
		logEndTurn();
		checkWin();
		reStockDeck();
		nextPlayer();
		missTurn();
		if (!getPlayer().equals(this.myPlayers.get(0)))
			{
			compTurn();
			}
		}

	public void logEndTurn() {
		if (getPlayer().countHandCards() == 1)
		{
			this.logger.addToLog("and has ended their turn.\n"+ getPlayer().getName() + ": LAST CARD!\n");
			}
		else
		{
			this.logger.addToLog("and has ended their turn.\n");
			}
		if (getPlayer().equals(this.myPlayers.get(0)))
		{
			this.logger.addToLog("---------------------------------------\n");
			}
		}

	public void nextPlayer()
	{

		if (this.clockwise) {
			this.playerTurn++;
			if (this.playerTurn == playerNo) {
				this.playerTurn = 0;
			}
		} else {
			this.playerTurn = this.playerTurn - 1;
			if (this.playerTurn < 0) {
				this.playerTurn = (playerNo - 1);
			}
		}
	}

	public void missTurn() {
		if (this.missTurn > 0) {
			this.missTurn -= 1;
			this.logger.addToLog(getPlayer().getName() + " missed a turn.");
			endTurn();
		}
	}
	// Had to modify to make a new deck to stop duplicate cards appearing if put from the table to the deck(Ask Andy)
	public void reStockDeck() {
		if (this.myDeck.size() < 2) {
			int tableCards = this.theTable.countCardsOnTable() - 1;
			if (/*(this.theTable.tableCards.size() <= 2)&&*/ (this.myDeck.size() <= 0)) {
				this.myDeck = this.newDeck.createNewDeck();
				this.logger.addToLog("\n\nNo more cards, a new Deck will be added.\n\n\n");
				Collections.shuffle(this.myDeck);
			}
			/*
			for (int i = 1; i < tableCards; i++) {
				Card move = this.theTable.getTableCard();
				this.theTable.tableCards.remove(1);
				this.myDeck.add(move);
				Collections.shuffle(this.myDeck);
			}
			*/
		}

		assert (this.myDeck.size() >= 1) : "Deck is empty because the restock has failed.";
		/*     */}

	public void checkWin() {
		Player currentPlayer = (Player) this.myPlayers.get(this.playerTurn);
		if (currentPlayer.countHandCards() == 0) {
			this.logger.addToLog("*******   GAME OVER!   *******\n\n"+ currentPlayer.getName()+ " IS THE WINNER!");
			
			this.gameActive = false;
			
			if(currentPlayer == this.myPlayers.get(0)){
				
				if ((username != "Player") || (active == true))
				{
					loggedgamesWon++;
					
					BigDecimal play = new BigDecimal(loggedgamesPlayed);
		            BigDecimal won = new BigDecimal(loggedgamesWon);
		                
		            loggedoverall = (won.divide(play, 9, RoundingMode.HALF_UP)); //used the games played and games won values to calculate overall score
					
					updateDatabase(); //updates games won and percentage in database
				}

				this.logger.addToLog("Congratulations "+ currentPlayer.getName()+ ".\nA point has been added to your overall score.");
					
					 Thread threadA = new Thread(new Runnable(){
				            public void run(){
				               	try {
				               		loadWin();
				    				} catch (IOException e) {
				    					// TODO Auto-generated catch block
				    					e.printStackTrace();
				    				} catch (InterruptedException e) {
				    					// TODO Auto-generated catch block
				    					e.printStackTrace();
				    				} //loads splash image
				            }
				        }, "Thread A");	
					 
					 threadA.start();

			}
			else
			{

					 Thread threadA = new Thread(new Runnable(){
				            public void run(){
				               	try {
				               		loadLoss();
				    				} catch (IOException e) {
				    					// TODO Auto-generated catch block
				    					e.printStackTrace();
				    				} catch (InterruptedException e) {
				    					// TODO Auto-generated catch block
				    					e.printStackTrace();
				    				} //loads splash image
				            }
				        }, "Thread A");
					
					 threadA.start();
			}
			
		} else {
			assert (this.gameActive == true) : "Game still checking win when game is inactive.";
		}
	}

	

	public void checkEffect(Card identifyEffect, Player effectAppliedBy) {
		int myVal = identifyEffect.getValue();
		int mySuit = identifyEffect.getSuit();
		Card lastCardPlayed = this.theTable.getTopTableCard();
		if ((myVal == 0) && (this.pickUpModeOn() == false)) {
			this.userPlayedAce = true;
			aceChange();		
			// Doing this will change the suit of the card to the suit the computer wanted to change to.
			if(this.getPlayerInTurn() != 0){
				identifyEffect.setSuit(tempSuitValue);
			}
		} else if (myVal == 1) {
			this.pickUpModeOn = true;
			this.pickUpCount += 2;
			this.logger.addToLog("Pick up is: " + this.pickUpCount);
		} else if (myVal == 2){
			this.pickUpModeOn = true;
			this.pickUpCount += 3;
			this.logger.addToLog("Pick up is: " + this.pickUpCount);
		}else if ((myVal == 0)&&(mySuit == 3)) {
			if (this.pickUpModeOn) {
				this.pickUpModeOn = false;
				this.pickUpCount = 0;
				this.logger.addToLog(getPlayer().getName()+ " blocked the pickup with Ace\u2660.");
			}			
		} else if (myVal == 7) {
			this.missTurn += 1;
		} else if (myVal == 10) {
			if (this.clockwise == true){
				this.clockwise = false;
			} else {
				this.clockwise = true;
			}
			this.logger.addToLog(getPlayer().getName()+ " reversed direction of play with a Jack");
		} else if (myVal == 12 && (mySuit == 0 || mySuit == 3)){
			this.pickUpModeOn = true;
			this.pickUpCount += 5;
			this.logger.addToLog("Pick up is: " + this.pickUpCount);
			//if the value == King AND the suit is red AND pickupmode is on AND last card played == King.
		} else if (myVal == 12 && (mySuit == 1 || mySuit == 2) && pickUpModeOn == true && (lastCardPlayed.getValue() == 12)){
			this.pickUpCount = this.pickUpCount - 5;
			if (this.pickUpCount <= 0){
				this.pickUpCount = 0;
				this.pickUpModeOn = false;
			}
			this.logger.addToLog("Pick up is: " + this.pickUpCount);
		}
	}

	public void aceChange() {

		if (this.playerTurn == 0) {
			this.logger.addToLog("Suit Change!");
		} else {
			compAceChange();
		}
	}

	public Player getPlayer() {
		return (Player) this.myPlayers.get(this.playerTurn);
	}

	public int getCurrentSuit() {
		return this.currentSuit;
	}

	public int pickUpModeCount() {

		return this.pickUpCount;
	}

	public boolean pickUpModeOn() {
		return this.pickUpModeOn;
	}

	public boolean gameActive() {
		return this.gameActive;
	}

	public boolean isClockwise() {
		return this.clockwise;
	}

	public boolean isUserPlayed() {
		return this.userPlayed;
	}

	public boolean isPlayedAce() {
		return this.userPlayedAce;
	}

	public boolean isAceChanged() {
		boolean aceSuit = false;
		if (this.currentSuit != 4) {
			aceSuit = true;
		}
		return aceSuit;
	}

	public void setSuit(int newSuit) {
		this.currentSuit = newSuit;
	}

	public void setPlayed() {
		this.userPlayed = true;
	}

	public TableCards getTable() {
		return this.theTable;
	}
	
	public int getPlayerInTurn(){
		return this.playerTurn;
	}
	
	
	
	
	
private static void loadLoss() throws IOException, InterruptedException {
  		
  		 try {
  	 	      InputStream in = Engine.class.getClassLoader().getResourceAsStream("images/youLost.png");
  	 	      lossImg = ImageIO.read(in);
  	 	    } catch (Exception e) {
  	 	      e.printStackTrace();
  	 	    }
  		
  		JFrame loseScreen = new JFrame("CAKE"); //creates new splash frame
  		
  		@SuppressWarnings("serial")
  		final JPanel panel = new JPanel(){
              @Override
              protected void paintComponent(Graphics g){
                  Graphics g2 = g.create();
                  g2.drawImage(lossImg, 0, 0, getWidth(), getHeight(), null); //draw image in frame
                  g2.dispose();
              }

              @Override
              public Dimension getPreferredSize(){
                  return new Dimension(lossImg.getWidth(), (lossImg.getHeight())); //sets frame size to fit image
              }
          };
  		
        loseScreen.getContentPane().add(panel);
        loseScreen.setUndecorated(true); //hides frame border and default frame buttons from splash 
        loseScreen.pack();
        loseScreen.setResizable(false);
        loseScreen.setBackground(new Color(0,0,0,0));
        loseScreen.setLocationRelativeTo(null); //centers to screen
        loseScreen.setVisible(true);
        loseScreen.toFront();
  		Thread.sleep(5000);
  		loseScreen.setVisible(false); //frame disappears after 3secs
  	}
	

private static void loadWin() throws IOException, InterruptedException {
		
	 try {
 	      InputStream in = Engine.class.getClassLoader().getResourceAsStream("images/youWon.png");
 	      winImg = ImageIO.read(in);
 	    } catch (Exception e) {
 	      e.printStackTrace();
 	    }

		JFrame winScreen = new JFrame("CAKE"); //creates new splash frame
		
		@SuppressWarnings("serial")
		final JPanel panel = new JPanel(){
          @Override
          protected void paintComponent(Graphics g){
              Graphics g2 = g.create();
              g2.drawImage(winImg, 0, 0, getWidth(), getHeight(), null); //draw image in frame
              g2.dispose();
          }

          @Override
          public Dimension getPreferredSize(){
              return new Dimension(winImg.getWidth(), (winImg.getHeight())); //sets frame size to fit image
          }
      };
		
	  winScreen.getContentPane().add(panel);
	  winScreen.setUndecorated(true); //hides frame border and default frame buttons from splash 
	  winScreen.pack();
	  winScreen.setResizable(false);
	  winScreen.setBackground(new Color(0,0,0,0));
	  winScreen.setLocationRelativeTo(null); //centers to screen
	  winScreen.setVisible(true);
	  winScreen.toFront();
	  Thread.sleep(5000);
	  winScreen.setVisible(false); //frame disappears after 3secs
	}




	private void getPlayerScores(Long userId, String username) {
		// TODO Gets the results for the Best Overall Result leaderboard 
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cake","root","");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			System.out.print("Connection problem");
			e1.printStackTrace();
		}
		
		try{
				
			statement = connection.prepareStatement("SELECT users.username, history.gamesplayed, history.gameswon, history.winnum FROM users LEFT JOIN history ON users.id = history.userid WHERE history.userId = ?");
			
			statement.setLong(1, userId);
			
			resultSet = statement.executeQuery();
	
			if (resultSet != null) {
				
			    while (resultSet.next()) {
			    	
			    	loggedusername = (String) resultSet.getObject(1);
	                loggedgamesPlayed = (Long) resultSet.getObject(2);
	                loggedgamesWon = (Long) resultSet.getObject(3);
	                
	                BigDecimal play = new BigDecimal(loggedgamesPlayed);
	                BigDecimal won = new BigDecimal(loggedgamesWon);
	                
	                loggedoverall = (won.divide(play, 4, RoundingMode.HALF_UP)); //used the games played and games won values to calculate overall score
	                
	                if ( ! (loggedusername.equals(username))){
	                	
	                	 JOptionPane.showMessageDialog(null, "There was a mismatch error in the database. Please restart the application and log in again.","Database Error",
	                     JOptionPane.ERROR_MESSAGE);
	                	 
	                	 System.exit(0);
	                	
	                }			    	
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Unable to connect to database. Please restart the application and log in again.","Database Error",
	            JOptionPane.ERROR_MESSAGE);
				db.dbDisconnect();
			}
	     }
	    catch (Exception e) 
	    {
	        System.out.println(e);
	    }
		
	}



	private void updateDatabase() {
		// TODO Auto-generated method stub
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cake","root","");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try{
			
			statement = connection.prepareStatement("UPDATE history SET gamesplayed = ?, gameswon = ?, winnum = ? WHERE userId = ?");
			
			statement.setLong(1, loggedgamesPlayed);
			statement.setLong(2, loggedgamesWon);
			statement.setBigDecimal(3, loggedoverall);
			statement.setLong(4, userId);
			
			statement.executeUpdate();
			
         }
        catch (Exception e) 
        {
            System.out.println(e);
        }
		
		
	}

	
}
