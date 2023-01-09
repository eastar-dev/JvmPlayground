@file:Suppress("ConstPropertyName")

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

private const val Debug = false

val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

data class Chat(val date: Long, val name: String, val message: String)

fun main() {
    val targetFile = File("release/KakaoTalk_Chat.csv")
    val reportFile = File("release/report.txt").also { it.delete() }
    val errorFile = File("release/error.txt").also { it.delete() }

    targetFile.readText()
        .split("\n(?=\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})".toRegex())
        .asSequence()
        .map { it.split(",", limit = 3) }
        .mapNotNull { chat ->
            runCatching {
                Chat(
                    date = dateFormatter.parse(chat.getOrNull(0)).time,
                    name = chat.getOrNull(1)?.trim('"') ?: "",
                    message = chat.getOrNull(2) ?: ""
                )
            }.getOrElse {
                if (Debug) {
                    errorFile.appendText(chat.toString())
                    errorFile.appendText(it.toString())
                }
                null
            }
        }
        .filterNot { it.message.contains("${it.name}님을 내보냈습니다") || it.message.contains("${it.name}님이 나갔습니다.") || it.message.contains("${it.name}님이 들어왔습니다.") }
        .groupBy { it.name }
        .mapNotNull { (_, chats) -> chats.maxByOrNull { it.date } }
        .sortedByDescending { it.date }
        .mapIndexed { index, chat -> "${index + 1}:${dateFormatter.format(Date(chat.date))}:${chat.name}" }
        .onEach { println(it) }
        .toList()
        .also { reportFile.writeText(it.joinToString("\n")) }
}
