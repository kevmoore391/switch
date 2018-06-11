package Game;

import java.awt.Color;

import javax.swing.JLabel;

public class Card {
	//declaring the necessary variables
	private int suit;
	private int value;
	private int symbol;
	private boolean joker;
	//declaring the array  which will form the cards
	protected static final String[] nameSuit = { "Clubs", "Diamonds", "Hearts","Spades", "Black", "Red" };
	protected static final String[] nameSymbol = { "\u2663", "\u2666", "\u2665", "\u2660", "\u25A0"};
	protected static final String[] nameValue = { "Ace", "2", "3", "4", "5", "6","7", "8", "9", "10", "Jack", "Queen", "King", "Joker" };

	
	public Card(){
		
	}
	//card constructor to assign values
	public Card(int selectCardValue, int selectCardSuit) {
		this.value = selectCardValue;
		this.suit = selectCardSuit;
		this.symbol = selectCardSuit;
		
		//Because the jokers do not have the suit of clubs, hearts, diamonds or spades, This assigns the suits of red of black
		if (this.value == 13) {
			if (joker == false) {
				symbol = 4;
				suit = 4;
				joker = true;
			} else {
				symbol = 4;
				suit = 5;
			}
		}

	}
	//sets and gets for the cards suits and values
	public void setSuit(int newSuit){
		this.suit = newSuit;
	}
	
	public void setValue(int newValue){
		this.value = newValue;
	}
	
	public void setSymbol(int newSymbol){
		this.symbol = newSymbol;
	}

	public int getSuit() {
		return this.suit;
	}
	
	public String getSuitString(){
		return this.nameSuit[this.suit];
	}
	
	public String getValueString(){
		return this.nameValue[this.value];
	}

	public int getValue() {
		return this.value;
	}

	public int getSymbol(int selectCardValue) {
		return this.symbol;
	}

	public @Override
	String toString() {
		return nameSymbol[symbol] + " " + nameValue[value] + " "+ nameSuit[suit];
	}

	public String getUnicodeValueAndSuit() {
		String unicodeValueAndSuit = nameValue[this.value]+ nameSymbol[this.suit];
		return unicodeValueAndSuit;
	}
}