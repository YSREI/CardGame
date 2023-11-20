
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Player implements PlayerInterface, Runnable{
    private Logger logger = Logger.getLogger(Player.class.getName());

    private boolean isWin = false;
    private boolean hasWinner = false;
    private int playerNumber;
    private int leftNumber;
    private int rightNumber;

    private ArrayList<Card> handCards = new ArrayList<>();
    private int handCardAmount;
    //to record how many card the player has

    private String playerFile;
    private String leftDeckFile;

    private String rightDeckFile;
    private Deck[] cardDecks;

    public boolean roundFinished;



    public Player(int playerNumber, int amountOfPlayer,  Deck[] cardDecks){
        this.playerNumber = playerNumber;
        this.cardDecks = cardDecks;
        if(playerNumber == amountOfPlayer){
            //last player
            this.rightNumber = 1;
        }else{
            //normal player
            this.rightNumber = ++playerNumber;
        }
        this.leftNumber = playerNumber;
        this.playerFile = "Player" + this.playerNumber +"_output";
        this.leftDeckFile = "Deck" + leftNumber + "_output";
        this.rightDeckFile = "Deck" + rightNumber + "_output";
        try {
            logger.addHandler(new FileHandler("Player" + Integer.toString(this.playerNumber) + ".log"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        while(true) {
            if (CardGame.isWin) {
                // Someone has won
                int winnerNumber = 0;
                // not finished variable
                System.out.println("Player" + winnerNumber + " has won");
                System.out.println("Player" + playerNumber + " exit");
                return;
            }
            if (checkIWin()) {
                declareAWin();
                // not implemented
                System.out.println("Player" + playerNumber + " has won");
                System.out.println("Player" + playerNumber + " exit");
                return;
            }

            if (!roundFinished) {
                // a boolean from CardGame that show if this round of play is over
                synchronized (cardDecks[leftNumber].getLock()) {
            /* pick card from the left deck, use synchronized to
             ensure any deck can be accessed by only one Player */
                    Card card = pickCard();
                    String message = "Player" + Integer.toString(this.playerNumber) +
                                     " draws a" + Integer.toString(card.getValue()) +
                                     " from deck " + Integer.toString(leftNumber);
                    logger.log(Level.INFO, message);
                }

                synchronized (cardDecks[rightNumber].getLock()) {
            /* pick card from the left deck, use synchronized to
             ensure any deck can be accessed by only one Player */
                    Card card = discardCard();
                    String message = "Player" + Integer.toString(this.playerNumber) +
                                     " discards a " + Integer.toString(card.getValue()) +
                                     " to deck " + Integer.toString(rightNumber);
                    logger.log(Level.INFO, message);
                }

                // log current hand
                StringBuilder message = new StringBuilder();
                for (Card card : handCards) {
                    message.append(card.toString()).append(" ");
                }
                logger.log(Level.INFO, message.toString());
                //roundFinished = true;
            }else{
                Thread.yield();//can be optimized by using wait/notify
            }
        }


    }
    @Override
    public boolean checkIWin() {
        return false;
    }

    @Override
    public boolean declareAWin() {
        return false;
    }

    @Override
    public Card pickCard() {
        Deck leftDeck = cardDecks[leftNumber];
        //from the Deck array(All the Deck) pick the right one.
        Card pickedCard = leftDeck.pickCard();
        //pick the first card of this deck.

        /* as now the game have a round concept, there
           is no chance that any deck can be empty.
         */


        //if(pickedCard != null){
        //pickCard() will return a null if the deck is empty.
        handCards.add(pickedCard) ;
        //ArrayList.add() will add Object to the end of the list by default
        return pickedCard;
        //} else {
        //   return false;
        //the error Output is left to CardGame class to deal with
        // }
    }

    @Override
    public Card discardCard() {
        int deleteNumber = -1;
        int most = mostFrequentNumber();
        for (int i = 0; i < handCards.size(); i++) {
            int num = handCards.get(i).getValue();
            //get every (value of card) of handCards
            if(num != most && num != playerNumber){
                deleteNumber = i;
                break;
            }
        }
        if(deleteNumber == -1){
            //that there are only frequent value and player number value
            //e.g. Player1 {1, 2, 2, 2}
            for (int i = 0; i < handCards.size(); i++) {
                int num = handCards.get(i).getValue();
                //get every (value of card) of handCards
                if(num != playerNumber){
                    deleteNumber = i;
                    break;
                }
            }
        }
        return handCards.remove(deleteNumber);
    }

    public int mostFrequentNumber(){
        int maxCount = 0;
        int maxValue = 0;
        for (int i = 0; i < handCards.size(); i++) {
            //each i compare frequency of every number
            int count = 0;
            int currentValue = handCards.get(i).getValue();

            for (int j = 0; j < handCards.size(); j++) {
                //each j find out frequency of every number
                if (handCards.get(j).getValue() == currentValue) {
                    count++;
                }
            }

            if (count > maxCount) {
                maxCount = count;
                maxValue = currentValue;
            }
        }
        return maxValue;
    }
    @Override
    public boolean outputDeck(int number) {
        ArrayList<Card> deck = cardDecks[number].getDeckOfCards();
        int size = deck.size();
        int[] deckCardsInt = new int[size];
        for (int i = 0; i < size; i++) {
            // deckCardsInt[i] = handCards.get(i).getValue();
            deckCardsInt[i] = deck.get(i).getValue();
            // 获得牌堆里的每一个card并转成数字
        }
        CardGame game = new CardGame();
        game.output(deckCardsInt);
        // 获取int数组，以及对应的路径
        return true;
    }

    @Override
    public boolean outputPlayer() {
        int size = handCards.size();
        int[] handCardsInt = new int[size];
        for (int i = 0; i < size; i++) {
            handCardsInt[i] = handCards.get(i).getValue();
        }
        CardGame game = new CardGame();
        game.output(handCardsInt);
        return true;
    }






    public ArrayList<Card> getHandCards() {
        return handCards;
    }
    public void setHandCards(ArrayList<Card> handCards) {
        this.handCards = handCards;
    }
    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String getPlayerFile() {
        return playerFile;
    }

    public void setPlayerFile(String playerFile) {
        this.playerFile = playerFile;
    }

    public String getLeftDeckFile() {
        return leftDeckFile;
    }

    public void setLeftDeckFile(String leftDeckFile) {
        this.leftDeckFile = leftDeckFile;
    }

    public String getRightDeckFile() {
        return rightDeckFile;
    }

    public void setRightDeckFile(String rightDeckFile) {
        this.rightDeckFile = rightDeckFile;
    }

    public Deck[] getCardDecks() {
        return cardDecks;
    }

    public void setCardDecks(Deck[] cardDecks) {
        this.cardDecks = cardDecks;
    }

    public int getHandCardAmount() {
        return handCardAmount;
    }

    public void setHandCardAmount(int handCardAmount) {
        this.handCardAmount = handCardAmount;
    }

}
