package phonon.xc.util.debugtimings

import kotlin.math.roundToInt

class CircularBuffer<T>(val size: Int) {
    private val buffer: Array<T?> = arrayOfNulls(size)
    private var index = 0
    private var count = 0

    fun add(value: T) {
        buffer[index] = value
        index = (index + 1) % size
        if (count < size) count++
    }

    fun values(): List<T> = buffer.take(count).filterNotNull()

    fun mean(): Double where T : Number {
        val vals = values()
        if (vals.isEmpty()) return 0.0
        return vals.sumOf { it.toDouble() } / vals.size
    }

    fun min(): Double where T : Number {
        val vals = values()
        if (vals.isEmpty()) return 0.0
        return vals.minOf { it.toDouble() }
    }

    fun max(): Double where T : Number {
        val vals = values()
        if (vals.isEmpty()) return 0.0
        return vals.maxOf { it.toDouble() }
    }

    fun percentile(p: Double): Double where T : Number {
        val vals = values().map { it.toDouble() }.sorted()
        if (vals.isEmpty()) return 0.0
        val idx = ((vals.size - 1) * p).roundToInt().coerceIn(0, vals.size - 1)
        return vals[idx]
    }
}

class DebugTimings(val enabled: Boolean = false, val bufferSize: Int = 200) {
    private val timings: MutableMap<String, CircularBuffer<Long>> = mutableMapOf()
    private val startTimes: MutableMap<String, Long> = mutableMapOf()

    fun start(name: String) {
        if (!enabled) return
        startTimes[name] = System.nanoTime()
    }

    fun end(name: String) {
        if (!enabled) return
        val startTime = startTimes.remove(name) ?: return
        val elapsed = System.nanoTime() - startTime
        timings.getOrPut(name) { CircularBuffer(bufferSize) }.add(elapsed)
    }

    fun getStats(name: String): TimingStats? {
        if (!enabled) return null
        val buffer = timings[name] ?: return null
        return TimingStats(
            mean = buffer.mean() / 1_000_000.0,  // ms
            min = buffer.min() / 1_000_000.0,
            max = buffer.max() / 1_000_000.0,
            p95 = buffer.percentile(0.95) / 1_000_000.0,
            p99 = buffer.percentile(0.99) / 1_000_000.0,
            samples = buffer.values().size
        )
    }

    fun printStats(name: String) {
        val stats = getStats(name) ?: return
        println("[$name] mean=${stats.mean}%.3fms min=${stats.min}%.3fms max=${stats.max}%.3fms p95=${stats.p95}%.3fms p99=${stats.p99}%.3fms n=${stats.samples}")
    }
}

data class TimingStats(
    val mean: Double,
    val min: Double,
    val max: Double,
    val p95: Double,
    val p99: Double,
    val samples: Int
)
