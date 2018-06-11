package Game;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

//This is the deck class. This class creates a deck of cards using an array list of cards
public class Deck {
	//initialising variables
	public static int nValues = 13; //number of Values
	public static int nSuits = 4; // number of suits
	public static int nCard = nValues * nSuits+2; // number of cards in deck + jokers
	ArrayList<Card> Deck = new ArrayList<Card>();//create an array list with card arguements
	int i;
	Random rgen = new Random();  // Random number generator
		

	 //This creates a deck of 56 cards. 
	
	   public ArrayList createNewDeck()
	   {
	    for (int i = 0; i < 4; i++)
	       {
	       for (int j = 0; j < 14; j++)
	        {
	          Card newCard = new Card(j, i);
	         this.Deck.add(newCard);
	        }
	       }
	    
	    // Remove two of the jokers so deck contains only 2, so the deck will have 54 cards. 
	    this.Deck.remove(13);
	    this.Deck.remove(26);
	    this.Deck.get(39).setSuit(5);
	    shuffleDeck();
	    return this.Deck;
	    }
	   //this shuffles the array list to make the deck ready for the game.
	   public void shuffleDeck()
	    {
	      Collections.shuffle(this.Deck);
	      }
	   
	    public Card returnCard(int pos)
	     {
	     assert (pos < this.Deck.size()) : "Retrieving index out of array range";
	   
	      return (Card)this.Deck.get(pos);
	      }
	   }

