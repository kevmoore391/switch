function CheckTrick () {
    this.checkTrick = function(card){
        var handSize;
        var isTrick = false;
        var value = card.getValue();
        var suit = card.getSuit();
        //This class checks if the card passed in is a trick card or not. It returns true if it is, and false if it is not. 
        switch (value) {
            case 0:  isTrick = true;  //Ace
            break;
            case 1:  isTrick = true; //2
            break;
            case 2:  isTrick = true; //3
            break;
            case 3:  isTrick = false; //4
            break;
            case 4:  isTrick = false; //5
            break;
            case 5:  isTrick = false; //6
            break;
            case 6:  isTrick = false; //7
            break;
            case 7:  isTrick = true; //8
            break;
            case 8:  isTrick = false; //9
            break;
            case 9:  isTrick = false; //10
            break;
            case 10:  isTrick = true; //Jack
            break;
            case 11:  isTrick = false; //Queen
            break;
            case 12:  isTrick = true; //King
            break;
            case 13:  isTrick = true; //Joker
            break;
            default: console.log("Invalid Value");
            break;
        }
        
        return isTrick;
    }
    
    
}

module.exports = CheckTrick;