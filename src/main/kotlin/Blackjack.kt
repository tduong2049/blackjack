/* Program for playing Blackjack with two other computer-controlled players
 * All players start with $1000. The betting range is $5 - $20.
 * Gameplay continues until the human player leaves the table or not longer has enough for
 * the minimum wager.
 *
 * Author: Tri Duong
 * Last Updated: March 20, 2019
 */

fun main() {
    val computerControl = 0
    val humanControlled = 1

    print("Welcome to the game of Blackjack!\nPlease enter your name: ")
    val humanName: String = readLine()!!

    val playerHuman = Player(humanName, humanControlled)
    val playerComputer1 = Player("Deckard (AI)", computerControl)
    val playerComputer2 = Player("Rachael (AI)", computerControl)

    val allPlayers: List<Player> = listOf(playerHuman, playerComputer1, playerComputer2)

    println()
    for (player in allPlayers) {
        println("${player.name} has entered the game!")
    }

    val invalidInput: Int = -1

    // Prompt the user to choose the amount of decks to use for the table.
    // If the user gives an invalid amount, keep prompting them until a valid amount is given.
    var deckCount: Int = chooseNumberOfDecks()
    while (deckCount == invalidInput) deckCount = chooseNumberOfDecks()

    val playRound = 1
    val watchRound = 2
    val leaveTable = 3

    val minimumWager = 5
    val maximumWager = 20

    // Prompt the user to decide to play a round, watch a round with AI players, or leave the table
    var playerChoice = mainMenu(allPlayers, minimumWager, maximumWager)
    while (playerChoice == invalidInput) playerChoice = mainMenu(allPlayers, minimumWager, maximumWager)

    // If the player's choice isn't to leave the table, the game may keep going until they decide
    // to leave or until they no longer have enough for the minimum wager.
    while(playerChoice != leaveTable) {
        var playersInRound: MutableList<Player> = mutableListOf()

        when (playerChoice) {
            playRound -> {
                for (player in allPlayers) {
                    if (player.canPlay(minimumWager)) {
                        playersInRound.add(player)
                    }
                }// end for allPlayers

                println("\n---------------------\nBetting Phase\n---------------------")
                collectBets(playersInRound, minimumWager, maximumWager)

                startRound(playersInRound, deckCount)
            }// end when playerChoice == playRound

            watchRound -> {
                for (player in allPlayers) {
                    if (player.controlType == computerControl && player.canPlay(minimumWager)) {
                        playersInRound.add(player)
                    }
                }// end for allPlayers

                println("\n---------------------\nBetting Phase\n---------------------")
                collectBets(playersInRound, minimumWager, maximumWager)

                startRound(playersInRound, deckCount)
            }// end when playerChoice == watchRound
        }// end when

        playerChoice = mainMenu(allPlayers, minimumWager, maximumWager)
    }// end while player doesn't leave table

    println()
    for (player in allPlayers) {
        println("${player.name} left with $${player.pool}")
    }
}

/* Function that prompts the user to decide the amount of decks to be used at the Blackjack table.
 *
 * arguments:
 * none
 *
 * returns:
 * Integer that represent the number of decks chosen or an invalid input
 */
fun chooseNumberOfDecks(): Int {
    print("\nHow many decks of cards will be used? (1-8 decks allowed): ")
    val choice: Int = readLine()!!.toInt()

    val invalidChoice: Int = -1

    if (choice < 1 || choice > 8) {
        println("Invalid amount of decks. Please choose again.")
        return invalidChoice
    }

    else return choice
}// end chooseNumberOfDecks func

/* Function that prompts the user to decide if they want to play a game, watch a game, or leave the table.
 *
 * arguments:
 * allPlayers - list of every Player object in the game
 * minimumBet - integer that represents the minimum betting amount
 * maximumBet - integer that represents the maximum betting amount
 *
 * returns:
 * An integer that corresponds to a specific choice or an invalid input
 */
fun mainMenu(allPlayers: List<Player>, minimumBet: Int, maximumBet: Int): Int {
    println("\n---------------------\nMain Menu\n---------------------")
    println("The betting range is $$minimumBet - $$maximumBet")
    println()
    for (player in allPlayers) {
        println("${player.name}'s current pool: $${player.pool}")
    }

    println("\n1. Play a Round\n2. Watch a Round\n3. Leave Table")
    print("\nWhat would you like to do? (Enter option number): ")
    val choice: Int = readLine()!!.toInt()

    val invalidChoice: Int = -1
    val playRound = 1
    val watchRound = 2
    val leaveTable = 3

    val humanPlayer: Player = allPlayers.get(0)

    when {
        choice == 1 && humanPlayer.canPlay(minimumBet) -> return playRound
        choice == 1 && !humanPlayer.canPlay(minimumBet) -> {
            println("You do not have enough to play. Please choose a different option.")
            return invalidChoice
        }
        choice == 2 -> return watchRound
        choice == 3 -> return leaveTable
        else -> {
            println("Invalid choice. Please choose again.")
            return invalidChoice
        }
    }// end when
}// end mainMenu func

// Function that collects the bets of every player that's participating in the round.
fun collectBets(activePlayers: MutableList<Player>, minimumBet: Int, maximumBet: Int) {
    val humanControlled = 1

    for (player in activePlayers) {
        if (player.controlType == humanControlled) {
            while (!collectedHumanBet(player, minimumBet, maximumBet)) {
                collectedHumanBet(player, minimumBet, maximumBet)
            }
        }

        else player.decideWager(minimumBet, maximumBet)

        println("${player.name} is betting $${player.wager}")
    }// end for
}// end playRound func

// Function that prompts the user to decide how much they would like to bet for the round.
fun collectedHumanBet(player: Player, minimumBet: Int, maximumBet: Int): Boolean {
    println("The betting range is $$minimumBet - $$maximumBet")
    print("\nHow much would you like to bet?: $")
    val bet: Int = readLine()!!.toInt()

    when {
        bet > player.pool -> {
            println("You don't have enough. Please enter a lower bet.")
            return false
        }
        bet > maximumBet -> {
            println("The maximum wager is $$maximumBet. Please enter a lower bet.")
            return false
        }

        bet < minimumBet -> {
            println("The minimum wager is $$minimumBet. Please enter a higher bet.")
            return false
        }
        else -> {
            println()
            player.betMoney(bet)
            return true
        }
    }// end when
}// when collectHumanBet

/* Function that has begins a round of Blackjack for the participating players.
 * The size of the deck depends on the amount of decks that human player initially chose to be used
 * in the beginning.
 *
 * The deck is shuffled, pairs of cards are given to each player, and each player has a turn before
 * the dealer reveals the hole card.
 *
 * Based on their hands, the players and dealers are checked if they have busted.
 *
 * Players that have not busted have their hands compared to the dealers.
 * If their hands beat the dealer's, they win with equal wager.
 *
 * The players and dealer's hands are emptied and their status is restored to starting again.
 *
 * arguments:
 * activePlayers - a mutable list of Player objects
 * numberOfDecks - an integer that stores that number of decks chosen to be used
 *
 * return:
 * nothing
 */
fun startRound(activePlayers: MutableList<Player>, numberOfDecks: Int) {
    println("\n---------------------\nPlaying Phase\n---------------------")
    println("Dealing cards...")

    val dealer = Player("Dealer")

    val two = Card("Two", 2)
    val three = Card("Three", 3)
    val four = Card("Four", 4)
    val five = Card("Five", 5)
    val six = Card("Six", 6)
    val seven = Card("Seven", 7)
    val eight = Card("Eight", 8)
    val nine = Card("Nine", 9)
    val ten = Card("Ten", 10)
    val jack = Card("Jack", 10)
    val queen = Card("Queen", 10)
    val king = Card("King", 10)
    val aceEleven = Card("Ace", 11)

    var undealtCards: MutableList<Card> = mutableListOf()

    for (deck in 1..(4 * numberOfDecks)) {
        undealtCards.add(two)
        undealtCards.add(three)
        undealtCards.add(four)
        undealtCards.add(five)
        undealtCards.add(six)
        undealtCards.add(seven)
        undealtCards.add(eight)
        undealtCards.add(nine)
        undealtCards.add(ten)
        undealtCards.add(jack)
        undealtCards.add(queen)
        undealtCards.add(king)
        undealtCards.add(aceEleven)
    }

    undealtCards.shuffle()

    for(player in activePlayers) {
        player.hand.add(undealtCards.removeAt(0))
        player.hand.add(undealtCards.removeAt(0))
    }

    dealer.hand.add(undealtCards.removeAt(0))
    dealer.hand.add(undealtCards.removeAt(0))

    dealer.displayFirstCard()
    for (player in activePlayers) {
        player.displayHand()
    }

    for(player in activePlayers) {
       playerTurn(player, dealer, undealtCards)
    }
    dealerTurn(dealer, undealtCards)

    displayResults(dealer, activePlayers)

    for(player in activePlayers) {
        player.hand.clear()
        player.status = 0
        player.wager = 0
    }

    dealer.hand.clear()
}

/* Function that repeatedly displays the current player's hand and the dealer's first card
 * and prompts the current player to hit or stand.
 *
 * It will also change the player's status if they decide to stand or have busted
 */
fun playerTurn(player: Player, dealer:Player, cards: MutableList<Card>) {
    println("\n---------------------\n${player.name}'s Turn\n---------------------")
    dealer.displayFirstCard()
    player.displayHand()

    val hitting = 1
    val standing = 2
    val busted = 3

    val humanControlled = 1
    if (player.controlType == humanControlled) {
        while(player.status != standing && player.status != busted) {

            println("\n1. Hit\n2. Stand")

            print("\nWhat would you like to do? (Enter an option): ")
            val choice: Int = readLine()!!.toInt()

            when (choice) {
                standing -> {
                    println("${player.name} is standing.")
                    player.status = standing
                }// when player stands

                hitting -> {
                    println("${player.name} is hitting.")
                    player.hand.add(cards.removeAt(0))
                    dealer.displayFirstCard()
                    player.displayHand()
                }//end when player's hand

                else -> println("Invalid choice. Please choose again.")
            }// end when choice

            when {
                player.calculateHand() > 21 -> {
                    println("\nBusted!")
                    player.status = busted
                }// end when player has busted
                player.calculateHand() == 21 -> {
                    println("\nBlackjack!")
                    player.status = standing
                }// end when player gets blackjack
            }// end when player's hand
        }// end while player is not standing and has not busted
    }// end if human player's turn


    if (player.controlType != humanControlled) {
        while(player.status != standing && player.status != busted) {
            when {
                player.calculateHand() > 16 -> {
                    println("\n${player.name} is standing.")
                    player.status = standing
                }

                player.calculateHand() <= 16 -> {
                    println("\n${player.name} is hitting.")
                    player.hand.add(cards.removeAt(0))
                    dealer.displayFirstCard()
                    player.displayHand()
                }
            }

            when {
                player.calculateHand() > 21 -> {
                    println("\nBusted!")
                    player.status = busted
                }// end when player has busted
                player.calculateHand() == 21 -> {
                    println("\nBlackjack!")
                    player.status = standing
                }// end when player gets blackjack
            }// end when player's hand
        }// end while player is not standing and has not busted
    }// end if computer player's turn
}// end playerTurn

/* Functions similarly to the playerTurn function. The dealer's full hand is displayed.
 * The dealer must then determine if they should keep hitting or stand.
 *
 */
fun dealerTurn(dealer: Player, cards: MutableList<Card>) {
    println("\n---------------------\nDealer's Turn\n---------------------")
    dealer.displayHand()

    val standing = 2
    val busted = 3

    while(dealer.status != standing && dealer.status != busted) {
        when {
            dealer.calculateHand() > 16 -> {
                println("\n${dealer.name} is standing.")
                dealer.status = standing
            }

            dealer.calculateHand() <= 16 -> {
                println("\n${dealer.name} is hitting.")
                dealer.hand.add(cards.removeAt(0))
                dealer.displayHand()
            }
        }// end when dealer's hand

        when {
            dealer.calculateHand() > 21 -> {
                println("\nBusted!")
                dealer.status = busted
            }// end when player has busted
            dealer.calculateHand() == 21 -> {
                println("\nBlackjack!")
                dealer.status = standing
            }// end when player gets blackjack
        }// end when dealer's hand
    }// end while dealer is not standing or has not busted
}// end dealerTurn func


// Function that displays the final results of the round, including the players' and dealer's final
// hands. It will also display who won, lost, and the amount of they earned or lost.
fun displayResults(dealer: Player, activePlayers: MutableList<Player>) {
    println("\n---------------------\nResults\n---------------------")
    dealer.displayHand()
    for (player in activePlayers){
        player.displayHand()
    }

    val standing = 2
    val busted = 3

    println()
    for (player in activePlayers){
        when {
            dealer.status == busted && player.status == standing -> {
                println("${player.name} won $${player.calculateWinnings()}!")
                player.receiveWinnings()
            }

            dealer.status == standing && player.status == standing -> {
                if (player.calculateHand() > dealer.calculateHand()) {
                    println("${player.name} won $${player.calculateWinnings()}!")
                    player.receiveWinnings()
                }
                else println("${player.name} lost $${player.wager}.")
            }

            player.status == busted -> println("${player.name} lost $${player.wager}")
        }// end when
    }// end for players
}// end displayResults