package sensors_in_paradise.sonar.screen_train

import org.json.JSONArray
import org.json.JSONObject
import sensors_in_paradise.sonar.JSONStorage
import sensors_in_paradise.sonar.use_cases.UseCase
import kotlin.math.round

class PredictionHistoryStorage(
    useCase: UseCase,
    private val startTimestamp: Long,
    shouldStorePrediction: Boolean = false
) : JSONStorage(
    if (shouldStorePrediction) useCase.getPredictionHistoryJSONFile(startTimestamp)
    else null
) {
    data class Prediction(var label: String, var percentage: Float) {
        fun percentageAsString(): String {
            val roundedPercentage = round(percentage * 100) / 100
            return "$roundedPercentage%"
        }

        companion object {
            val PredictionsComparator = Comparator<Prediction> { left, right ->
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                when {
                    (left.percentage < right.percentage) -> 0
                    (left.percentage > right.percentage) -> -1
                    else -> 0
                }
            }
        }
    }

    private lateinit var history: JSONArray
    override fun onFileNewlyCreated() {
        json.put("startTimestamp", startTimestamp)
        json.put("history", JSONArray())
    }

    override fun onJSONInitialized() {
        history = json.getJSONArray("history")
    }

    fun addPrediction(
        prediction: Prediction
    ): Long {
        val predictionObj = JSONObject()
        predictionObj.put("label", prediction.label)
        predictionObj.put("percentage", prediction.percentage)
        val relativeTime = System.currentTimeMillis() - startTimestamp
        predictionObj.put("relativeTimestamp", relativeTime)
        history.put(predictionObj)
        save()
        return relativeTime
    }

    fun getPredictionHistory(sortLatestFirst: Boolean = true): ArrayList<Pair<Prediction, Long>> {
        val result = arrayListOf<Pair<Prediction, Long>>()
        for (i in 0 until history.length()) {
            val obj = history.getJSONObject(i)
            val label = obj.getString("label")
            val percentage = obj.getDouble("percentage").toFloat()
            val relativeTime = obj.getLong("relativeTimestamp")
            result.add(
                Pair(Prediction(label, percentage), relativeTime)
            )
        }
        if (sortLatestFirst) {
            result.sortByDescending { it.second }
        }
        return result
    }
}