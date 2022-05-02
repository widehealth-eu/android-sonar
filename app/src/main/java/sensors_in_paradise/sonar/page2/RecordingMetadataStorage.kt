@file:Suppress("SwallowedException")
package sensors_in_paradise.sonar.page2

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import sensors_in_paradise.sonar.JSONStorage
import java.io.File

class RecordingMetadataStorage(file: File, initialJson: JSONObject? = null) : JSONStorage(file, initialJson) {
    data class LabelEntry(
        var timeStarted: Long,
        var activity: String
    )
    private lateinit var activities: JSONArray
    override fun onFileNewlyCreated() {
        activities = JSONArray()
        json.put("activities", activities)
        save()
    }

    override fun onJSONInitialized() {
        activities = json.getJSONArray("activities")
    }

    fun setData(
        activities: ArrayList<LabelEntry>,
        totalStartTime: Long,
        endTime: Long,
        person: String,
        sensorMacMap: Map<String, String>
    ) {
        for (activity in activities) {
            addActivity(activity)
        }
        setTimeFinished(endTime)
        setTimeStarted(totalStartTime)
        setPerson(person)
        setSensorMacMap(sensorMacMap)
        save()
    }
    fun setVideoCaptureStartedTime(timeStarted: Long, save: Boolean = false) {
        json.put("videoCaptureStartTime", timeStarted)
        if (save) {
            save()
        }
    }

    fun setPoseCaptureStartedTime(timeStarted: Long, save: Boolean = false) {
        json.put("poseCaptureStartTime", timeStarted)
        if (save) {
            save()
        }
    }
    fun setActivities(activities: ArrayList<LabelEntry>, save: Boolean = false) {
        clearActivities()
        for (activity in activities) {
            addActivity(activity)
        }
        if (save) {
            save()
        }
    }
    fun getActivities(): ArrayList<LabelEntry> {
        val result = ArrayList<LabelEntry>()
        for (i in 0 until activities.length()) {
            val activityObj = activities[i] as JSONObject
            val timeStarted = activityObj.getLong("timeStarted")
            val label = activityObj.getString("label")
            result.add(LabelEntry(timeStarted, label))
        }
        return result
    }
    fun getVideoCaptureStartedTime(): Long? {
        val v = json.optLong("videoCaptureStartTime")
        return if (v != 0L) v else null
    }
    fun getPoseCaptureStartedTime(): Long? {
        val v = json.optLong("poseCaptureStartTime")
        return if (v != 0L) v else null
    }
    fun getDuration(): Long {
        return getTimeEnded() - getTimeStarted()
    }
    fun getTimeStarted(): Long {
        return json.getLong("startTimestamp")
    }
    fun getTimeEnded(): Long {
        return json.getLong("endTimestamp")
    }
    fun getPerson(): String {
        return json.getString("person")
    }

    fun setRecordingState(state: String) {
        json.put("recordingState", state)
        save()
    }

    fun getRecordingState(): String? {
        return try {
            json.getString("recordingState")
        } catch (exception: JSONException) {
            null
        }
    }
    private fun clearActivities() {
       while (activities.length() > 0) {
               activities.remove(0)
           }
    }
    private fun addActivity(activity: LabelEntry) {
        val obj = JSONObject()
        obj.put("timeStarted", activity.timeStarted)
        obj.put("label", activity.activity)
        activities.put(obj)
    }

    private fun setTimeStarted(startTime: Long) {
        json.put("startTimestamp", startTime)
    }

    private fun setTimeFinished(endTime: Long) {
        json.put("endTimestamp", endTime)
    }

    private fun setPerson(person: String) {
        json.put("person", person)
    }
    private fun setSensorMacMap(sensorMacMap: Map<String, String>) {
        val obj = JSONObject()
        for (entry in sensorMacMap.entries) {
            obj.put(entry.key, entry.value)
        }
        json.put("sensorMapping", obj)
    }
    fun getSensorMacMap(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val obj = json.getJSONObject("sensorMapping")

        for (key in obj.keys()) {
            result[key] = obj.getString(key)
        }
        return result
    }
    fun clone(): RecordingMetadataStorage {
        return RecordingMetadataStorage(file, json)
    }
}