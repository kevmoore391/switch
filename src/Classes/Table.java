package gui;

import java.awt.*;

import javax.imageio.ImageIO;
import javax.print.DocFlavor.URL;
import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

import Game.Engine;
import Game.ActivityLogger;
import Game.Card;
import Game.CheckTrick;
import Game.Deck;
import Game.Player;
import Game.TableCards;

public class Table {

	private JFrame frame;
	private JComboBox<String> suitComboBox;
	private JComboBox<String> valueComboBox;
	private ArrayList<Card> cardsToAddAL;
	private ArrayList<Card> originalsAL;
	private Object[] cardsToAdd;
	private JTextPane logList;
	private JList selectedCtxt;
	private String tableName;
	
	private ImageIcon table;
	private ImageIcon deck;
	private ImageIcon deckcurl;
	private ImageIcon topCard;
	private ImageIcon pCardimg;
	private ImageIcon handIcon;
	private ImageIcon clockWise;
	private ImageIcon antiClockWise;
	private ImageIcon hearts;
	private ImageIcon diamonds;
	private ImageIcon spades;
	private ImageIcon clubs;
	
	private JButton direction;
	private JButton currentSuit;
	private JScrollPane handScroll;
	private JScrollPane selectedScroll;
	private JScrollPane logScroll;
	private JPanel handPanel;
	private int numberOfOpponents;
	public static Engine gameEngine;
	private Card originalSpecial;
	private Card newCardChosen;
	private Card c;
	private Card cFromOriginalAL;
	private Card cardOnTable;
	private JButton btnTopCard;
	private String player0Str;
	private String player1Str;
	private String player2Str;
	private String player3Str;
	private String player4Str;
	private JLabel player0Lbl;
	private JLabel player1Lbl;
	private JLabel player2Lbl;
	private JLabel player3Lbl;
	private JLabel player4Lbl;
	private JLabel currentPickUpLbl;
	private String currentPickUpStr;
	private String fullActivityLog;
	private JButton btnChangeValues;
	private boolean active;
	Iterator logIterator;

	public Table(String tn, int num, Long userId, String username, boolean active) {
		setTableName(tn);
		setNumberOfOpponents(num);
		initialize(tn, userId, username, active);
		this.active = active;
	}

	private void initialize(String tn, final Long userId, final String username, final boolean active) {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.LIGHT_GRAY);
		frame.setVisible(true);
		frame.setTitle(getTableName());
		frame.setBounds(100, 100, 1100, 715);
		frame.setLocationRelativeTo(null);

		// Start Engine
		gameEngine = new Engine();
		gameEngine.start(getNumberOfOpponents(), userId, username, active);
		gameEngine.determineFirstPlayer();
		logIterator = gameEngine.logger.createIterator();
		fullActivityLog = "";
		gameEngine.myPlayers.get(0).setName(username);

		// Create Menubar and menu items.
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmRestart = new JMenuItem("Restart");
		mnFile.add(mntmRestart);
		mntmRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				frame.dispose();
				Table newT = new Table("Piece Of Cake", getNumberOfOpponents(), userId, username, active);
				
			}
			
		});
		
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				frame.dispose();
			}
			
		});
		
		JMenuItem mntmAccount = new JMenuItem("Edit Account");
		mnEdit.add(mntmAccount);
		if ((username !="Player")&&(active==true))
		{
			mntmAccount.setEnabled(true);
		}
		else
		{
			mntmAccount.setEnabled(false);
		}
		mntmAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String urlText = "http://localhost/cake/account.html";
			    
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			        	
			        	URI uri = new URI(urlText);
			            desktop.browse(uri);
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
			    }
			}
			
		});
		
		JMenuItem mntmHow = new JMenuItem("How To Play");
		mnHelp.add(mntmHow);
		mntmHow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String urlText = "http://localhost/cake/howtoplay.html";
			    
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			        	
			        	URI uri = new URI(urlText);
			            desktop.browse(uri);
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
			    }
			}
			
		});
		JMenuItem mntmAbout = new JMenuItem("About CAKE");
		mnHelp.add(mntmAbout);
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String urlText = "http://localhost/cake/contact.html";
			    
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			        	
			        	URI uri = new URI(urlText);
			            desktop.browse(uri);
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
			    }
			}
			
		});
		
		frame.getContentPane().setLayout(null);

		logList = new JTextPane();
		logList.setEditable(false);
		logScroll = new JScrollPane(logList);
		logScroll.setBounds(10, 27, 188, 349);
		refreshLog();
		frame.getContentPane().add(logScroll);

		// Set up the current suit picture
		java.net.URL ht = Table.class.getClassLoader().getResource("images/hearts.png");
		hearts = new ImageIcon(ht);
		java.net.URL di = Table.class.getClassLoader().getResource("images/diamonds.png");
		diamonds = new ImageIcon(di);
		java.net.URL sp = Table.class.getClassLoader().getResource("images/spades.png");
		spades = new ImageIcon(sp);
		java.net.URL cl = Table.class.getClassLoader().getResource("images/clubs.png");
		clubs = new ImageIcon(cl);

		currentSuit = new JButton(hearts);
		currentSuit.setEnabled(false);
		currentSuit.setBounds(935, 313, 97, 85);
		frame.getContentPane().add(currentSuit);

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(210, 11, 700, 387);
		frame.getContentPane().add(layeredPane);

		// Create images of other players
		java.net.URL hnd = Table.class.getClassLoader().getResource("images/backofhand.png");
		handIcon = new ImageIcon(((new ImageIcon(hnd)).getImage())
						.getScaledInstance(80, 78, java.awt.Image.SCALE_SMOOTH));

		JLabel player1Hand = new JLabel(handIcon);
		player1Hand.setBounds(0, 282, 80, 78);
		player1Hand.setVisible(false);
		layeredPane.add(player1Hand);

		JLabel player2Hand = new JLabel(handIcon);
		player2Hand.setBounds(0, 0, 80, 78);
		player2Hand.setVisible(false);
		layeredPane.add(player2Hand);

		JLabel player3Hand = new JLabel(handIcon);
		player3Hand.setBounds(608, 0, 80, 78);
		player3Hand.setVisible(false);
		layeredPane.add(player3Hand);

		JLabel player4Hand = new JLabel(handIcon);
		player4Hand.setBounds(608, 282, 80, 78);
		player4Hand.setVisible(false);
		layeredPane.add(player4Hand);

		if (getNumberOfOpponents() == 2) {
			player1Hand.setVisible(true);
			player2Hand.setVisible(true);
		} else if (getNumberOfOpponents() == 3) {
			player1Hand.setVisible(true);
			player2Hand.setVisible(true);
			player3Hand.setVisible(true);
		} else if (getNumberOfOpponents() == 4) {
			player1Hand.setVisible(true);
			player2Hand.setVisible(true);
			player3Hand.setVisible(true);
			player4Hand.setVisible(true);
		}

		// Set up button for drawing cards from the deck
		java.net.URL dck = Table.class.getClassLoader().getResource("cards/back.jpg");
		deck = new ImageIcon(((new ImageIcon(dck)).getImage())
				.getScaledInstance(80, 130, java.awt.Image.SCALE_SMOOTH));

		
		java.net.URL dckcrl = Table.class.getClassLoader().getResource("cards/backcurl.jpg");
		deckcurl = new ImageIcon(((new ImageIcon(dckcrl)).getImage())
						.getScaledInstance(80, 130, java.awt.Image.SCALE_SMOOTH));
		
		JButton btnDeck = new JButton(deck);
		btnDeck.setToolTipText("Draw a card from the Deck.");
		btnDeck.setRolloverEnabled(true);
		btnDeck.setRolloverIcon(deckcurl);
		btnDeck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (gameEngine.gameActive() == true) {

					if (gameEngine.pickUpModeOn()) {
						refreshDirection();
						gameEngine.draw(gameEngine.pickUpModeCount());
						refreshHand();
						refreshHandCounts();
						refreshLog();
						gameEngine.endTurn();
						refreshDirection();
						refreshHandCounts();
					} else {
						refreshDirection();
						gameEngine.draw(1);
						refreshHand();
						refreshHandCounts();
						refreshLog();
						gameEngine.endTurn();
						refreshDirection();
						refreshHandCounts();
					}
				} else {
					
					refreshDirection();
					refreshHand();
					refreshHandCounts();
					refreshLog();
					JOptionPane
							.showMessageDialog(null,
									"Game Over! Please restart a new game to play again");
				}

			}
		});
		btnDeck.setBounds(240, 110, 80, 130);
		layeredPane.add(btnDeck);

		// Set up a label to show the player the current pickup
		Font pickUpFont = new Font("Verdana", Font.BOLD, 20);
		currentPickUpStr = "Pick Up: " + gameEngine.pickUpModeCount();
		currentPickUpLbl = new JLabel(currentPickUpStr);
		currentPickUpLbl.setVisible(false);
		currentPickUpLbl.setForeground(Color.WHITE);
		currentPickUpLbl.setFont(pickUpFont);
		currentPickUpLbl.setBounds(275, 70, 160, 30);
		layeredPane.add(currentPickUpLbl);

		// Set the picture of the top card on the table
		Card topCardC = gameEngine.getTable().getTopTableCard();
		String firstCard = topCardC.getValueString() + topCardC.getSuitString()
				+ ".jpg";
		
		java.net.URL tcrd = Table.class.getClassLoader().getResource("cards/" + firstCard);
		topCard = new ImageIcon(((new ImageIcon(tcrd)).getImage()).getScaledInstance(80, 130, java.awt.Image.SCALE_SMOOTH));
		btnTopCard = new JButton(deck);
		btnTopCard.setBounds(340, 110, 80, 130);
		btnTopCard.setDisabledIcon(topCard);
		btnTopCard.setEnabled(false);
		layeredPane.add(btnTopCard);
		refreshSuit(topCardC);

		// Set up background of table
		java.net.URL tbl = Table.class.getClassLoader().getResource("images/tablePic.png");
		table = new ImageIcon(tbl);
		JLabel tableLbl = new JLabel(table);
		tableLbl.setBounds(10, 0, 700, 387);
		layeredPane.add(tableLbl);

		// Set up the directional arrow picture
		java.net.URL clkws = Table.class.getClassLoader().getResource("images/clockwise.png");
		clockWise = new ImageIcon((new ImageIcon(clkws)).getImage());
		
		java.net.URL anticlkws = Table.class.getClassLoader().getResource("images/anticlockwise.png");
		antiClockWise = new ImageIcon((new ImageIcon(anticlkws)).getImage());

		direction = new JButton(clockWise);
		direction.setDisabledIcon(clockWise);
		direction.setBounds(935, 225, 97, 85);
		direction.setEnabled(false);
		frame.getContentPane().add(direction);

		player0Str = gameEngine.myPlayers.get(0).getName() + ": "+ gameEngine.myPlayers.get(0).countHandCards();
		player0Lbl = new JLabel(player0Str);
		player0Lbl.setHorizontalAlignment( SwingConstants.CENTER );
		Font curFont = player0Lbl.getFont();
		player0Lbl.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 15));
		player0Lbl.setBounds(136, 360, 399, 16);
		layeredPane.add(player0Lbl);

		try {
			player1Str = "Comp 1: "+ gameEngine.myPlayers.get(1).countHandCards();
			player1Lbl = new JLabel(player1Str);
			Font curFonta = player1Lbl.getFont();
			player1Lbl.setFont(new Font(curFonta.getFontName(), curFonta.getStyle(), 11));
			player1Lbl.setBounds(0, 360, 91, 16);
			layeredPane.add(player1Lbl);
		} catch (Exception p1Ex) {
			// Not created because number of players is less than this player
		}

		try {
			player2Str = "Comp 2: "
					+ gameEngine.myPlayers.get(2).countHandCards();
			player2Lbl = new JLabel(player2Str);
			player2Lbl.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 11));
			player2Lbl.setBounds(0, 78, 80, 16);
			layeredPane.add(player2Lbl);
		} catch (Exception p1Ex) {
			// Not created because number of players is less than this player
		}

		try {
			player3Str = "Comp 3: "+ gameEngine.myPlayers.get(3).countHandCards();
			player3Lbl = new JLabel(player3Str);
			player3Lbl.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 11));
			player3Lbl.setBounds(608, 78, 80, 16);
			layeredPane.add(player3Lbl);
		} catch (Exception p1Ex) {
			// Not created because number of players is less than this player
		}

		try {
			player4Str = "Comp 4: "+ gameEngine.myPlayers.get(4).countHandCards();
			player4Lbl = new JLabel(player4Str);
			player4Lbl.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 11));
			player4Lbl.setBounds(608, 360, 80, 16);
			layeredPane.add(player4Lbl);
		} catch (Exception p1Ex) {
			// Not created because number of players is less than this player
		}

		JLabel lblGameLog = new JLabel("Game Log:");
		lblGameLog.setBounds(10, 11, 62, 14);
		frame.getContentPane().add(lblGameLog);

		handPanel = new JPanel();
		handScroll = new JScrollPane(handPanel);

		// load players cards to the GUI
		refreshHand();

		handPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 20));
		handScroll.setBounds(10, 406, 829, 200);
		frame.getContentPane().add(handScroll);

		// Button to play selected cards
		JButton btnPlayCards = new JButton("");
		btnPlayCards.setToolTipText("Play your selected Cards.");
		
		java.net.URL pln = Table.class.getClassLoader().getResource("images/playPlain.png");
		ImageIcon playPlain = new ImageIcon(pln);
		java.net.URL roll = Table.class.getClassLoader().getResource("images/playRoll.png");
		ImageIcon playRoll = new ImageIcon(roll);
		java.net.URL press = Table.class.getClassLoader().getResource("images/playPressed.png");
		ImageIcon playPressed = new ImageIcon(press);
		
		btnPlayCards.setIcon(playPlain);
		btnPlayCards.setRolloverEnabled(true);
		btnPlayCards.setRolloverIcon(playRoll);
		btnPlayCards.setPressedIcon(playPressed);
		btnPlayCards.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (gameEngine.gameActive() == true) {

					cardOnTable = gameEngine.getTable().getTopTableCard();
					try{
					c = cardsToAddAL.get(0);
					}
					catch (Exception e)
					{
						System.out.print(e);
					}
					
					cFromOriginalAL = originalsAL.get(0);
					gameEngine.setSuit(cardOnTable.getSuit());
					CheckTrick ct = new CheckTrick();
					boolean canPlay = true;
					if (cardsToAddAL.size() == gameEngine.myPlayers.get(0).getHand().size()) {
						for (int r = 0; r < cardsToAddAL.size(); r++) {

							Card checkTrickCard = originalsAL.get(r);
							// If the card is a trick and not a joker then display message
							if (ct.CheckTrick(checkTrickCard) && (checkTrickCard.getValue() != 13)) {
								JOptionPane.showMessageDialog(null,"Can not finish on a trick card!");
								canPlay = false;
							} else {
								
							}
						}
						boolean notAllJokers = false;
						for(int n = 0; n < cardsToAddAL.size(); n++){
							
							Card checkIfNotJoker = originalsAL.get(n);
							// If the card is a trick and not a joker then display message
							if (checkIfNotJoker.getValue() != 13) {
								notAllJokers = true;
							} else {
								
							}
						}
						if((canPlay ==  true) && (notAllJokers == true)){
							Play();
						}

					} else {
						Play();
					}

				} else {

					JOptionPane.showMessageDialog(null,
							"The game is over! Please restart for a new game.");
				}
			}
		});
		btnPlayCards.setBackground(Color.RED);
		btnPlayCards.setBounds(849, 521, 209, 85);
		frame.getContentPane().add(btnPlayCards);

		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		btnExit.setBounds(969, 617, 89, 23);
		frame.getContentPane().add(btnExit);

		// Set up JList for displaying selected cards

		selectedCtxt = new JList();
		selectedScroll = new JScrollPane(selectedCtxt);
		selectedScroll.setBounds(849, 406, 209, 100);
		frame.getContentPane().add(selectedScroll);

		Panel panel = new Panel();
		panel.setBackground(Color.GRAY);
		panel.setBounds(910, 52, 148, 167);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		suitComboBox = new JComboBox();
		suitComboBox.addItem("Clubs");
		suitComboBox.addItem("Diamonds");
		suitComboBox.addItem("Hearts");
		suitComboBox.addItem("Spades");
		suitComboBox.setEnabled(false);
		suitComboBox.setBounds(10, 11, 128, 20);
		panel.add(suitComboBox);

		valueComboBox = new JComboBox();
		valueComboBox.addItem("Ace");
		valueComboBox.addItem("2");
		valueComboBox.addItem("3");
		valueComboBox.addItem("4");
		valueComboBox.addItem("5");
		valueComboBox.addItem("6");
		valueComboBox.addItem("7");
		valueComboBox.addItem("8");
		valueComboBox.addItem("9");
		valueComboBox.addItem("10");
		valueComboBox.addItem("Jack");
		valueComboBox.addItem("Queen");
		valueComboBox.addItem("King");
		valueComboBox.setEnabled(false);
		valueComboBox.setBounds(10, 82, 128, 20);
		panel.add(valueComboBox);

		// Button to change the values of a special card played
		btnChangeValues = new JButton("Change Values");
		btnChangeValues.setToolTipText("Change values when Ace or Joker selected.");
		btnChangeValues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int specialCardIndex = (cardsToAddAL.size() - 1);
				Card specialCard = cardsToAddAL.get(specialCardIndex);
				newCardChosen = new Card();
				int nSuit = suitComboBox.getSelectedIndex();
				int nValue = valueComboBox.getSelectedIndex();

				if (specialCard.getValue() == 0) {
					if (gameEngine.pickUpModeOn() == true) {

						JOptionPane.showMessageDialog(null,
								"Only the Ace of Spades can cancel a pickup");

					} else {

						originalSpecial = cardsToAddAL.get(specialCardIndex);
						newCardChosen.setSuit(nSuit);
						newCardChosen.setSymbol(nSuit);
						newCardChosen.setValue(0);

						cardsToAddAL.remove(specialCardIndex);
						cardsToAddAL.add(newCardChosen);
						cardsToAdd = cardsToAddAL.toArray();
						selectedCtxt.setListData(cardsToAdd);
					}

				} else {
					if (specialCard.getValueString() == "Joker") {

						originalSpecial = cardsToAddAL.get(specialCardIndex);
						newCardChosen.setSuit(nSuit);
						newCardChosen.setSymbol(nSuit);
						newCardChosen.setValue(nValue);

						cardsToAddAL.remove(specialCardIndex);
						cardsToAddAL.add(newCardChosen);
						cardsToAdd = cardsToAddAL.toArray();
						selectedCtxt.setListData(cardsToAdd);

					} else {

						JOptionPane
								.showMessageDialog(
										null,
										"This button is used to change the suit(Ace or Joker) or value(Joker) of a card played");

					}
				}

				suitComboBox.setEnabled(false);
				valueComboBox.setEnabled(false);

			}
		});
		btnChangeValues.setBounds(10, 129, 128, 25);
		panel.add(btnChangeValues);

		JLabel lblSpecialCardActions = new JLabel("Special Card Actions:");
		lblSpecialCardActions.setBounds(910, 30, 148, 14);
		frame.getContentPane().add(lblSpecialCardActions);

		JLabel lblPlayersHand = new JLabel(""+username+"'s Hand:");
		lblPlayersHand.setBounds(10, 389, 97, 16);
		frame.getContentPane().add(lblPlayersHand);

		JButton btnRestart = new JButton("Restart");
		btnRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				Table newT = new Table("Piece Of Cake", getNumberOfOpponents(), userId, username, active);
			}
		});
		btnRestart.setBounds(20, 616, 97, 25);
		frame.getContentPane().add(btnRestart);

	}

	public void setTableName(String tablName) {
		tableName = tablName;
	}

	public void refreshHand() {

		handPanel.removeAll();
		cardsToAddAL = new ArrayList<Card>();
		originalsAL = new ArrayList<Card>();
		int playersHandSize = gameEngine.myPlayers.get(0).countHandCards();

		for (int h = 0; h < playersHandSize; h++) {

			// Get card from the Engine
			final Card playerCard = gameEngine.myPlayers.get(0).getCard(h);

			// Get picture for card from cards folder
			String playerCardStr = playerCard.getValueString()+ playerCard.getSuitString() + ".jpg";
			java.net.URL pCard = Table.class.getClassLoader().getResource("cards/" + playerCardStr);
			pCardimg = new ImageIcon(((new ImageIcon(pCard)).getImage()).getScaledInstance(80, 130,java.awt.Image.SCALE_SMOOTH));
			cardsToAddAL.clear();
			originalsAL.clear();

			final JButton playerCardbtn = new JButton(pCardimg);

			playerCardbtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					if (playerCardbtn.getBackground() == Color.RED) {

						suitComboBox.setEnabled(false);
						valueComboBox.setEnabled(false);
						// check to see if the the ArrayList is holding the
						// selected card
						if (cardsToAddAL.contains(playerCard)) {

							playerCardbtn.setBackground(null);
							cardsToAddAL.remove(playerCard);
							originalsAL.remove(playerCard);

							cardsToAdd = cardsToAddAL.toArray();
							selectedCtxt.setListData(cardsToAdd);

						} else {

							if (originalsAL.contains(playerCard)) {
								int cIndex = originalsAL.indexOf(playerCard);
								cardsToAddAL.remove(cIndex);
								originalsAL.remove(cIndex);

							}
							playerCardbtn.setBackground(null);

							cardsToAdd = cardsToAddAL.toArray();
							selectedCtxt.setListData(cardsToAdd);
						}

					} else {

						if (playerCard.getValue() == 0) {
							suitComboBox.setEnabled(true);
						} else {
							if (playerCard.getValueString() == "Joker") {
								suitComboBox.setEnabled(true);
								valueComboBox.setEnabled(true);
							} else {
								suitComboBox.setEnabled(false);
								valueComboBox.setEnabled(false);
							}
						}

						playerCardbtn.setBackground(Color.RED);
						cardsToAddAL.add(playerCard);
						originalsAL.add(playerCard);

						cardsToAdd = cardsToAddAL.toArray();
						selectedCtxt.setListData(cardsToAdd);

					}

				}
			});

			playerCardbtn.setEnabled(true);
			handPanel.add(playerCardbtn);
			handPanel.revalidate();
			handPanel.repaint();
			handScroll.revalidate();

			

		}

	}

	public void refreshLog() {

		Thread timer = new Thread() {
			public void run() {

				while (logIterator.hasNext()) {
					refreshTopCard();
					fullActivityLog = fullActivityLog + logIterator.next()
							+ "\n";
					logList.setText(fullActivityLog);
					logList.setCaretPosition(logList.getDocument().getLength());
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		timer.start();
	}

	public void refreshTopCard() {

		Card topCardC = gameEngine.getTable().getTopTableCard();
		String firstCard = topCardC.getValueString() + topCardC.getSuitString()
				+ ".jpg";
		java.net.URL tCard = Table.class.getClassLoader().getResource("cards/" + firstCard);
		topCard = new ImageIcon(((new ImageIcon(tCard)).getImage())
						.getScaledInstance(80, 130, java.awt.Image.SCALE_SMOOTH));
		btnTopCard.setDisabledIcon(topCard);
		refreshSuit(topCardC);
	}

	public void setNumberOfOpponents(int n) {
		numberOfOpponents = n;
	}

	public String getTableName() {
		return tableName;
	}

	public int getNumberOfOpponents() {
		return numberOfOpponents;
	}

	public void makeTable(String tbName, Long userId, String username){
		initialize(tbName, userId, username, active);
	}

	public void refreshDirection() {

		if (gameEngine.isClockwise()) {
			direction.setDisabledIcon(clockWise);
		} else {
			direction.setDisabledIcon(antiClockWise);
		}

	}

	public void refreshSuit(Card tCard) {

		Card top = tCard;

		ImageIcon[] suitImages = { clubs, diamonds, hearts, spades };
		int cSuit = tCard.getSuit();
		currentSuit.setDisabledIcon(suitImages[cSuit]);

	}

	public void Play() {
		if (c.getValue() == cardOnTable.getValue()
				|| ((c.getSuit() == gameEngine.getCurrentSuit()) && (gameEngine
						.pickUpModeOn() == false)) || (c.getValue() == 0)) {

			if (gameEngine.checkFirstCard(c) == true) {

				gameEngine.playUserCard(c);

				// try catch to remove the played card from the users hand
				try {
					Player playerInTurn = (Player) gameEngine.myPlayers.get(0);
					playerInTurn.removeUserCard(c);

				} catch (Exception removeEx) {

				}

				try {
					Player playerInTurn = (Player) gameEngine.myPlayers.get(0);
					playerInTurn.removeUserCard(cFromOriginalAL);
				} catch (Exception sRemoveC) {

				}

				for (int y = 0; y < (cardsToAddAL.size() - 1); y++) {

					Card nextCard = cardsToAddAL.get(y + 1);
					Card nextFromOriginal = originalsAL.get(y + 1);

					if (gameEngine.checkCard(nextCard, c) == true) {

						gameEngine.playUserCard(nextCard);

						// try catch to remove the played card from the users
						// hand
						try {
							Player playerInTurn = (Player) gameEngine.myPlayers
									.get(0);
							playerInTurn.removeUserCard(nextCard);
						} catch (Exception removeEx) {

						}

						try {
							Player playerInTurn = (Player) gameEngine.myPlayers
									.get(0);
							playerInTurn.removeUserCard(nextFromOriginal);
						} catch (Exception removeEx) {

						}

					} else if (gameEngine.checkCard(nextCard, c) == false) {

						JOptionPane
						.showMessageDialog(
								null,
								nextCard.toString() + " could not be played because it did not match the preceeding card.");

					}
				}
				refreshHand();
				refreshDirection();
				gameEngine.endTurn();
				refreshDirection();
				refreshLog();
				cardsToAddAL.clear();
				originalsAL.clear();
				cardsToAdd = cardsToAddAL.toArray();
				selectedCtxt.setListData(cardsToAdd);
				refreshTopCard();
				refreshHand();
				refreshHandCounts();

			} else {
				
				refreshTopCard();
				
				JOptionPane
				.showMessageDialog(
						null,
						c.toString() + " can not be played on the previous one.");

			}
		} else {
			
			JOptionPane
			.showMessageDialog(
					null,
					c.toString() + " can not be played on the previous one.");

			
		}
	}
	
	public void refreshHandCounts(){
		
		player0Str = gameEngine.myPlayers.get(0).getName() + ": "
		+ gameEngine.myPlayers.get(0).countHandCards();
		player0Lbl.setText(player0Str);

		try {

			player1Str = "Comp 1: "
					+ gameEngine.myPlayers.get(1).countHandCards();
			player1Lbl.setText(player1Str);

		} catch (Exception playerHandEx) {
			// Player does not Exist
		}
		try {

			player2Str = "Comp 2: "
					+ gameEngine.myPlayers.get(2).countHandCards();
			player2Lbl.setText(player2Str);

		} catch (Exception playerHandEx) {
			// Player does not Exist
		}
		try {

			player3Str = "Comp 3: "
					+ gameEngine.myPlayers.get(3).countHandCards();
			player3Lbl.setText(player3Str);

		} catch (Exception playerHandEx) {
			// Player does not Exist
		}
		try {

			player4Str = "Comp 4: "
					+ gameEngine.myPlayers.get(4).countHandCards();
			player4Lbl.setText(player4Str);

		} catch (Exception playerHandEx) {
			// Player does not Exist
		}

		if (gameEngine.pickUpModeOn()) {
			currentPickUpStr = "Pick Up: " + gameEngine.pickUpModeCount();
			currentPickUpLbl.setText(currentPickUpStr);
			currentPickUpLbl.setVisible(true);
		} else {
			currentPickUpStr = "Pick Up: " + gameEngine.pickUpModeCount();
			currentPickUpLbl.setText(currentPickUpStr);
			currentPickUpLbl.setVisible(false);
		}
		
	}
}
