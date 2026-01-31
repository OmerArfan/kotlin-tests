// way to import sounds
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import java.io.File

// play the sound
lateinit var winSound: Clip

val soundBoard = mutableMapOf<String, Clip>()

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
    println("Level $lvlno complete!")
    println("It took you $attempt attempts.")
}

fun main() {
    loadAllSounds()
    welcome()

    var playing = true

    while (playing) {
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
