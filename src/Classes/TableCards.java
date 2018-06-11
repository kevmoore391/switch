package Game;

import java.util.ArrayList;
//this class sets up the card that would be on display on the table
public class TableCards {
	ArrayList<Card> tableCards = new ArrayList();

	public void addToTable(Card addCard) {
		try {
			this.tableCards.add(0, addCard);
		} catch (NullPointerException e) {
			System.out.println("No card could be added to the table.");
		}
	}
//sets and gets for the table cards
	public Card getTopTableCard() {
		return (Card) this.tableCards.get(0);
	}

	public int countCardsOnTable() {
		return this.tableCards.size();
	}

	public Card getTableCard() {
		return (Card) this.tableCards.get(1);
	}
	
	public ArrayList<Card> getAllTableCards(){
		return this.tableCards;
	}
}
