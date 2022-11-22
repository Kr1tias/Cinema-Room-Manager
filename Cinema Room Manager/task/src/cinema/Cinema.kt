package cinema

const val NUMBER_OF_SEATS_TO_CHANGE_PRICE = 60
const val BASE_PRICE = 10
const val LOWER_PRICE = 8
const val PERCENTAGE = 100
const val TEXT_ROWS = 2

enum class State {
    BuyTicket,
    MainMenu,
    Print,
    Exit
}

data class Layout(var rows: Int, var seats: Int)

fun main() {
    val map = Layout(0, 0)
    println("Enter the number of rows:")
    map.rows = readln().toInt()
    println("Enter the number of seats in each row:")
    map.seats = readln().toInt()
    val layout = MutableList(map.rows + 2) { mutableListOf<String>() } // +2 cause first two strings are not the rows
    var state = State.MainMenu
    var rowNumber = 0
    var seatNumber = 0
    printLayout(map, rowNumber, seatNumber, state, layout)
    while (state != State.Exit) {
        menu()
        //state = State.MainMenu
        val input = readln().toInt()
        when (input) {
            1 -> {
                state = State.Print
                printLayout(map, rowNumber, seatNumber, state, layout)
            }

            2 -> {
                state = State.BuyTicket
                while (state == State.BuyTicket) {
                    println("\nEnter a row number:")
                    rowNumber = readln().toInt()
                    println("Enter a seat number in that row:")
                    seatNumber = readln().toInt()
                    state = try {
                        if (layout[rowNumber + 1][seatNumber] == "B") throw Exception("That ticket has already been purchased!")
                        printLayout(map, rowNumber, seatNumber, state, layout)
                        State.MainMenu
                    } catch (e: IndexOutOfBoundsException) {
                        println("Wrong input!")
                        State.BuyTicket
                    } catch (e: Exception) {
                        println(e.message)
                        State.BuyTicket
                    }
                    if (state != State.BuyTicket) calculatePrice(map, rowNumber)
                }
            }

            3 -> statistics(map, layout)

            0 -> state = State.Exit
        }
    }
}

fun calculatePrice(map: Layout, rowNumber: Int) {
    println(
        if (map.rows * map.seats <= NUMBER_OF_SEATS_TO_CHANGE_PRICE) {
            "\nTicket price: $$BASE_PRICE"
        } else {
            when {
                rowNumber <= map.rows / 2 -> "\nTicket price: $$BASE_PRICE"
                else -> "\nTicket price: $$LOWER_PRICE"
            }
        }
    )
}

fun menu() {
    println()
    println("1. Show the seats")
    println("2. Buy a ticket")
    println("3. Statistics")
    println("0. Exit")
}

fun printLayout(map: Layout, rowNumber: Int, seatNumber: Int, state: State, layout: MutableList<MutableList<String>>) {
    if (layout[0].isEmpty()) {
        layout[0].add("Cinema:")                  // first string of layout
        for (i in 1..map.rows + 1) {            //
            for (k in 0..map.seats) {
                if (i == 1) {                     // adding the row with seat numbers
                    if (k == 0) layout[i].add(k, " ")
                    else layout[i].add(k, "$k")
                } else if (k == 0) layout[i].add(k, "${i - 1}") else layout[i].add(k, "S")
                // k = 0 - column with row numbers
            }
        }
    }
    if (rowNumber != 0 && seatNumber != 0) layout[rowNumber + 1][seatNumber] = "B"
    if (state == State.Print) {
        for (j in 0..map.rows + 1) {
            println(layout[j].joinToString(" "))
        }
    }
}

fun statistics(/*firstHalfTicketsBought: Int, secondHalfTicketsBought: Int,*/ map: Layout, layout: MutableList<MutableList<String>>) {
    val totalSeats = map.seats * map.rows
    val numberOfTicketsBought = calculateSoldTickets(TEXT_ROWS, map.rows, map.seats, layout)
    println("Number of purchased tickets: $numberOfTicketsBought")
    val percentage = "%.2f".format(numberOfTicketsBought.toDouble() / totalSeats * PERCENTAGE)
    println("Percentage: $percentage%")
    val currentIncome = if (totalSeats < NUMBER_OF_SEATS_TO_CHANGE_PRICE) {
    numberOfTicketsBought * BASE_PRICE
    } else {
        val frontRowTicketsSold = calculateSoldTickets(TEXT_ROWS,map.rows / 2, map.seats, layout)
        val backRowTicketsSold = calculateSoldTickets((map.rows - map.rows / 2) + 1, map.rows, map.seats, layout)
        frontRowTicketsSold * BASE_PRICE + backRowTicketsSold * LOWER_PRICE
    }
    println("Current income: $$currentIncome")
    val totalIncome = if (totalSeats < NUMBER_OF_SEATS_TO_CHANGE_PRICE) {
        totalSeats * BASE_PRICE
    } else {
        map.rows / 2 * map.seats * BASE_PRICE + (map.rows - map.rows / 2) * map.seats * LOWER_PRICE
    }
    println("Total income: $$totalIncome")
}

fun calculateSoldTickets(rowsFrom: Int, rowsTo: Int, seats: Int, layout: MutableList<MutableList<String>>): Int {
    var counter = 0
        for (i in rowsFrom..rowsTo + 1) {
            for (k in 1..seats) {
                if (layout[i][k] == "B") counter += 1
            }
        }
    return counter
}