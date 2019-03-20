/* Class that represents a blackjack player or dealer
 * Data:
 * name (string) - stores name
 * controlType (int) - indicates whether the player is controlled by human or computer
 * pool (int) - stores the current amount of money the player has
 * wager (int) - stores the player's wager placed in current round
 * hand (mutable list of Card objects) - stores the cards in the player's hand
 * status (int) - indicates the player's currrent status (starting, hitting, standing, busted)
 */
class Player(internal val name: String, internal val controlType: Int = 0) {
    /* Control Type meaning:
     * AI Player = 0
     * Human Player = 1
     */

    internal var pool: Int = 1000
    internal var wager: Int = 0
    internal var hand: MutableList<Card> = mutableListOf()

    /* Status meaning:
     * starting = 0
     * hitting = 1
     * standing = 2
     * busted = 3
     */
    internal var status: Int = 0;

    /* Function that returns a boolean to indicate
     * if the player can participate in the current round.
     */
    fun canPlay(minimumBetRequirement: Int): Boolean {
        return (this.pool >= minimumBetRequirement)
    }

    /* Function for CPU player.
     * Place a wager based on the given minimum and maximum bet requirements and
     * the amount of money that the player currently has
     */
    fun decideWager(minimumBet: Int, maximumBet: Int) {
        if (this.pool >= maximumBet) {
            val bet = (minimumBet..maximumBet).random()
            betMoney(bet)
        }

        else if(this.pool in minimumBet..this.pool) {
            val bet = (minimumBet..this.pool).random()
            betMoney(bet)
        }
    }// end decideBetAmount function

    /* Given a decided bet amount,
     * store its value and deduct it from the player's current money
     */
    fun betMoney(wager: Int) {
        this.wager = wager
        this.pool -= wager
    }

    /* In a rule set with equal returns
     *  calculate the player's winnings
     */
    fun calculateWinnings(): Int {
        return (this.wager * 2)
    }

    /* In a rule set with equal returns
     *  store the player's winnings into their pool
     */
    fun receiveWinnings(){
        this.pool += calculateWinnings()
    }

    /* Function that calculates the player's hand strength based on
     * the cards in their hand.
     */
    fun calculateHand(): Int {
        var handStrength = 0

        for (card: Card in this.hand) {
            handStrength += card.value
        }

        // If the hand is over 21, keeping checking for an ace cards
        // and use their alternative value of 1 until the hand is not longer over 21
        // or until there are no more ace cards exist with values of 11
        if (handStrength > 21) {
            for (card in this.hand) {
                if (card.value == 11) {
                    handStrength -= 11

                    card.value = 1
                    handStrength += 1
                }

                if (handStrength <= 21) break
            }
        }

        return handStrength
    }// end func calculateHand

    /* Function for dealer to
     * reveal their first card and its value
     */
    fun displayFirstCard() {
        val firstCard: Card = this.hand.elementAt(0)
        println("\n${this.name}'s hand: ${firstCard.value}")
        println(firstCard.name + "\n<Hidden>")
    }

    // Function to reveal the hand and its strength
    fun displayHand() {
        println("\n${this.name}'s hand: ${calculateHand()}")
        for (cards in this.hand) {
            println(cards.name)
        }
    }
}