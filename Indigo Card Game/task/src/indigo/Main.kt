package indigo


var cardsOnTable = mutableListOf<String>()
var playerWon = mutableListOf<String>()
var comWon = mutableListOf<String>()
var lastWinner = ""

class Deck {
    val ranks = ("A, 2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K").split(", ")
    val suits = ("♠ ♥ ♦ ♣").split(" ")
    var cards = mutableListOf<String>()
    var lastCard = ""

    init {
        for (suit in suits) {
            for (rank in ranks) {
                cards.add(rank + suit)
            }
        }
        cards.shuffle()
    }

    fun getCard(turn: String): MutableList<String> {
        var cardsReturn = mutableListOf<String>()
        when (turn) {
            "start" -> {
                cardsReturn = cards.dropLast(48).toMutableList()
                cards = cards.drop(4).toMutableList()
            }
            else -> {
                cardsReturn = cards.dropLast(cards.size - 6).toMutableList()
                cards = cards.drop(6).toMutableList()
            }
        }
        return cardsReturn
    }
}

fun chooser(possibleOptions: MutableList<String>): String {
    var choices= mutableListOf<String>()
    for (i in 0 until possibleOptions.size - 1) {
        for (j in i + 1 until possibleOptions.size) {
            if (possibleOptions[i].last() == possibleOptions[j].last()){
                choices.add(possibleOptions[i])
                choices.add(possibleOptions[j])
            }
        }
    }
    if (choices.size > 0) return choices.distinct().random()
    for (i in 0 until possibleOptions.size - 1) {
        for (j in i + 1 until possibleOptions.size) {
            if (possibleOptions[i].substring(0, possibleOptions[i].lastIndex) == possibleOptions[j].substring(0, possibleOptions[j].lastIndex)){
                choices.add(possibleOptions[i])
                choices.add(possibleOptions[j])
            }
        }
    }
    if (choices.size > 0) return choices.distinct().random()

    return possibleOptions.random()
}

fun comBehavior(comHand: MutableList<String>): String{
    if (comHand.size == 1) return comHand[0]
    val candidateCards = mutableListOf<String>()
    repeat(comHand.size) {
        if (cardsOnTable.size >= 1 &&(
                    cardsOnTable.last().contains(comHand[it].substring(0, comHand[it].lastIndex)) ||
                            comHand[it].last() ==
                            cardsOnTable.last().last())) {
            candidateCards.add(comHand[it])
        }
    }
    if (candidateCards.size == 1) return candidateCards[0]

    if (candidateCards.isEmpty()) return chooser(comHand)

    return chooser(candidateCards)
}

fun playTurn(hand: MutableList<String>, turn: String) : String {
    if (turn == "Player") {
        print("Cards in hand:")
        repeat(hand.size) {
            print(" ${it + 1})${hand[it]}")
        }
        var playerMove = ""
        while (true) {
            println("\nChoose a card to play (1-${hand.size}):")
            playerMove = readln()
            if (playerMove == "exit") return "exit"
            ( playerMove.toIntOrNull() ?: continue)
            if (playerMove.toInt() in 1..hand.size) break
        }
        cardsOnTable.add(hand.removeAt(playerMove.toInt() - 1))
        return "Computer"
    } else {
        println(hand.joinToString(" "))
        var computerMove = comBehavior(hand)
        hand.remove(computerMove)
        cardsOnTable.add(computerMove)
        println("Computer plays $computerMove")
        return "Player"
    }
}

fun checkWinner(cardsOnTable: MutableList<String>, winner: String) : MutableList<String> {
    var wonList = mutableListOf<String>()
    if (cardsOnTable.size > 1 &&(
        cardsOnTable[cardsOnTable.size - 2].substring(0, cardsOnTable[cardsOnTable.size - 2].length - 1) ==
        cardsOnTable[cardsOnTable.size - 1].substring(0, cardsOnTable[cardsOnTable.size - 1].length - 1) ||
        cardsOnTable[cardsOnTable.size - 2].last() ==
        cardsOnTable[cardsOnTable.size - 1].last())) {
        wonList.addAll(cardsOnTable)
        cardsOnTable.clear()
        lastWinner = winner
    }

    return wonList
}


fun calculatePoints(player: MutableList<String>, com: MutableList<String>, winner: String): Unit {
    val pointCards = Regex("[AJQK]|10")
    var playerPoint = player.fold(0) { points, string -> if (pointCards.containsMatchIn(string)) points + 1 else points }
    var comPoint = com.fold(0) { points, string -> if (pointCards.containsMatchIn(string)) points + 1 else points }
    println("$winner wins cards")
    println("Score: Player $playerPoint - Computer $comPoint")
    println("Cards: Player ${player.size} - Computer ${com.size}")
    player
}

fun finalCalculatePoints(player: MutableList<String>, com: MutableList<String>): Unit {
    val pointCards = Regex("[AJQK]|10")
    var playerPoint = player.fold(0) { points, string -> if (pointCards.containsMatchIn(string)) points + 1 else points }
    var comPoint = com.fold(0) { points, string -> if (pointCards.containsMatchIn(string)) points + 1 else points }
    when {
        player.size > com.size -> playerPoint += 3
        player.size < com.size -> comPoint += 3
    }
    println("Score: Player $playerPoint - Computer $comPoint")
    println("Cards: Player ${player.size} - Computer ${com.size}")
    println("Game Over")
    player
}

fun main() {
    var deck = Deck()
    var playerHand = mutableListOf<String>()
    var comHand = mutableListOf<String>()
    var turn = "start"
    println("Indigo Card Game")
    while (turn == "start") {
        println("Play first?")
        when (readln()) {
            "yes" -> turn = "Player"
            "no" -> turn = "Computer"
        }
    }

    //put 4 cards on table, add 6 cards to player and com hands
    cardsOnTable.addAll(deck.getCard("start"))
    println("Initial cards on the table: ${cardsOnTable.joinToString(" ")}")
    playerHand.addAll(deck.getCard("Player"))
    comHand.addAll((deck.getCard("Computer")))

    //game loop
    while (deck.cards.size != 0 || playerHand.size != 0 || comHand.size != 0){
        val tempTurn = turn
        if (playerHand.size == 0 && comHand.size == 0){
            playerHand.addAll(deck.getCard("Player"))
            comHand.addAll((deck.getCard("Computer")))
        }
        if (cardsOnTable.size > 0) println("${cardsOnTable.size} cards on the table, and the top card is ${cardsOnTable.last()}") else println("No cards on the table")
        turn = if (turn == "Player") playTurn(playerHand, turn) else playTurn(comHand, turn)
        if (turn == "exit") break

        when (tempTurn) {
            "Player" -> playerWon.addAll(checkWinner(cardsOnTable, tempTurn))
            "Computer" -> comWon.addAll(checkWinner(cardsOnTable, tempTurn))
        }

        if (cardsOnTable.size == 0) {
            calculatePoints(playerWon, comWon, tempTurn)
        }
        when {
            (deck.cards.size == 0 && playerHand.size == 0 && comHand.size == 0 && lastWinner == "Player") -> {
                if (cardsOnTable.size > 0) println("${cardsOnTable.size} cards on the table, and the top card is ${cardsOnTable.last()}") else println("No cards on the table")
                playerWon.addAll(cardsOnTable)
                cardsOnTable.clear()
            }
            (deck.cards.size == 0 && playerHand.size == 0 && comHand.size == 0 && lastWinner == "Computer") -> {
                if (cardsOnTable.size > 0) println("${cardsOnTable.size} cards on the table, and the top card is ${cardsOnTable.last()}") else println("No cards on the table")
                comWon.addAll(cardsOnTable)
                cardsOnTable.clear()
            }
            (turn == "Player" && cardsOnTable.size == 52) -> {
                if (cardsOnTable.size > 0) println("${cardsOnTable.size} cards on the table, and the top card is ${cardsOnTable.last()}") else println("No cards on the table")
                playerWon.addAll(cardsOnTable)
                cardsOnTable.clear()
            }
            (turn == "Computer" && cardsOnTable.size == 52) -> {
                if (cardsOnTable.size > 0) println("${cardsOnTable.size} cards on the table, and the top card is ${cardsOnTable.last()}") else println("No cards on the table")
                comWon.addAll(cardsOnTable)
                cardsOnTable.clear()
            }

        }
    }

    if (playerHand.size == 0) finalCalculatePoints(playerWon, comWon) else println("Game Over")

}