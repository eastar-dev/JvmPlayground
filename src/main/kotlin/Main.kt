import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {
    CoroutineScope(Dispatchers.Default).launch {
        print(getDelayedValue(1, 1))
    }
}

private suspend fun <T> getDelayedValue(
    delay: Long,
    value: T,
): T {
    delay(delay)
    return value
}
