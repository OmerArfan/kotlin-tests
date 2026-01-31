// way to import sounds
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import java.io.File

// play the sound
lateinit var winSound: Clip

val soundBoard = mutableMapOf<String, Clip>()
val highScores = mutableMapOf<Int, Int>()

fun loadAllSounds() {
    val soundFiles = listOf("win.wav", "wrong.wav")

    for (fileName in soundFiles) {
        try {
            val file = File("src/res/$fileName")
            val stream = AudioSystem.getAudioInputStream(file)
            val clip = AudioSystem.getClip()
            clip.open(stream)
            soundBoard[fileName] = clip
        } catch (e: Exception) {
            println("Couldn't load $fileName")
        }
    }
}

fun sfx(name: String) {
    val clip = soundBoard[name]
    if (clip != null) {
        clip.framePosition = 0 // Rewind
        clip.start()           // Play
    }
}

fun welcome(){
    println("LIL ROBO STUDIOS")
    println("Welcome to my first Kotlin project!")
}

// Save system
fun prinths(){
    println("\n--- HIGH SCORES ---")
    for (i in 1..5) {
        val best = highScores[i] ?: "No record"
        println("Level $i: $best")
    }
    print("\nSelect Level (1-5) or 0 to quit: ")
}

fun saveStatsToFile() {
    val file = File("stats.txt")
    // Converts the map into a string like "1:3\n2:11"
    val data = highScores.map { "${it.key}:${it.value}" }.joinToString("\n")
    file.writeText(data)
}

fun loadStatsFromFile() {
    val file = File("stats.txt")
    if (file.exists()) {
        file.forEachLine { line ->
            val parts = line.split(":")
            if (parts.size == 2) {
                highScores[parts[0].toInt()] = parts[1].toInt()
            }
        }
    }
}

data class LevelConfig(val option: Int, val min: Int, val max: Int)

fun select(): LevelConfig {
    print("Input a value between 1 and 5 to play a level, but input anything else to quit.")
    val choice = readlnOrNull()?.toIntOrNull() ?: 0

    return when (choice) {
        1 -> LevelConfig(1, 1, 10)
        2 -> LevelConfig(2, 1, 25)
        3 -> LevelConfig(3, 1, 99)
        4 -> LevelConfig(4, 20, 150)
        5 -> LevelConfig(5, 47, 212)
        else -> LevelConfig(0, 0, 0)
    }
}

fun level(lvlno : Int, min : Int, max : Int) {

    var attempt = 0

    var guess: Int? = null
    var target = (min..max).random()

    do {
        attempt++

        println("I'm thinking of a number between $min and $max. Guess it!")
        guess = readLine()?.toIntOrNull()

        when {
            guess == null -> {
                println("Enter a valid number!")
                sfx("wrong.wav")
            }
            guess == target -> {
                sfx("win.wav")
                Thread.sleep(300)
            }
            guess > max || guess < min -> {
                println("Input a value in the given range!")
                sfx("wrong.wav")
            }
            guess < target -> {
                println("Too low!")
                sfx("wrong.wav")
            }
            guess > target -> {
                println("Too high!")
                sfx("wrong.wav")
            }
        }

    } while (guess != target)
    println("Level $lvlno complete in $attempt attempts.")

    // 1. Get the previous best (or a huge number if they've never played)
    val previousBest = highScores[lvlno] ?: Int.MAX_VALUE

    // 2. Compare
    if (attempt < previousBest) {
        println("🎊 NEW BEST RECORD for Level $lvlno!")
        highScores[lvlno] = attempt // Update the "dictionary"

        // 3. Save to file so it's permanent
        saveStatsToFile()
    } else {
        println("Best record for this level: $previousBest")
    }
}

fun main() {
    loadAllSounds()
    loadStatsFromFile()
    welcome()

    var playing = true

    while (playing) {
        prinths()
        val config = select()

        if (config.option == 0) {
            playing = false
            println("Thanks for playing!")
        } else {
            level(config.option, config.min, config.max)

            println("\nBack to main menu...")
        }
    }
}