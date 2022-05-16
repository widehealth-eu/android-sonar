package sensors_in_paradise.sonar.screen_recording

import android.util.Log
import sensors_in_paradise.sonar.GlobalValues
import sensors_in_paradise.sonar.ObservableArrayList
import java.io.File

class RecordingDataManager(recordingsDir: File) :
    ObservableArrayList<Recording>() {
    var recordingsDir: File = recordingsDir
        set(value) {
            field = value
            loadRecordingsFromStorage()
        }

    init {
        loadRecordingsFromStorage()
    }

    fun reloadRecordingsFromStorage() {
        loadRecordingsFromStorage()
    }

    private fun loadRecordingsFromStorage() {
        clear()

        recordingsDir.walk().forEach {
            if (it.isDirectory) {
                if (isRecordingDir(it)) {
                    add(Recording(it))
                }
            }
        }
        sortByDescending { recording -> recording.metadataStorage.getTimeStarted() }
    }

    private fun isRecordingDir(file: File): Boolean {
        val childDirs = file.listFiles { dir, filename -> dir.resolve(filename).isDirectory }
        if (childDirs == null || childDirs.isEmpty()) {
            val activeFlagFile = file.resolve(GlobalValues.ACTIVE_RECORDING_FLAG_FILENAME)
            val metadataFile = file.resolve(GlobalValues.METADATA_JSON_FILENAME)
            return metadataFile.exists() && !activeFlagFile.exists()
        }
        return false
    }

    fun getNumberOfRecordingsPerActivity(): Map<String, Int> {
        val activities = ArrayList<String>()
        for (rec in this) {
            val storage = rec.metadataStorage
            activities.addAll(storage.getActivities().map { (_, label) -> label })
        }
        return activities.groupingBy { it }.eachCount()
    }

    fun deleteRecording(recording: Recording) {
        recording.delete()
        remove(recording)
    }

    fun getActivityDurationsOfTrainableRecordings(filterForPerson: String? = null): Map<String, Long> {
        Log.d(
            "RecordingDataManager",
            "Getting activity durations of trainable recordings for $filterForPerson"
        )
        val result = mutableMapOf<String, Long>()
        for (recording in this) {
            val metadata = recording.metadataStorage
            if (filterForPerson == null || metadata.getPerson() == filterForPerson) {
                if (!metadata.hasBeenUsedForOnDeviceTraining()) {
                    val activities = metadata.getActivities()
                    for ((index, labelEntry) in activities.withIndex()) {
                        val duration = RecordingMetadataStorage.getDurationOfActivity(
                            activities,
                            index,
                            metadata.getTimeEnded()
                        )
                        result[labelEntry.activity] = duration + (result[labelEntry.activity] ?: 0L)
                    }
                }
            }
        }
        return result
    }

    fun getPeopleDurationsOfTrainableRecordings(filterForActivity: String? = null): Map<String, Long> {
        val result = mutableMapOf<String, Long>()
        for (recording in this.filter { !it.metadataStorage.hasBeenUsedForOnDeviceTraining() }) {
            val metadata = recording.metadataStorage
            val person = metadata.getPerson()
            if (filterForActivity != null) {
                val activities = metadata.getActivities()
                for ((index, labelEntry) in activities.withIndex()) {
                    val (_, activity) = labelEntry
                    if (activity == filterForActivity) {
                        val duration = RecordingMetadataStorage.getDurationOfActivity(
                            activities,
                            index,
                            metadata.getTimeEnded()
                        )
                        result[person] = duration + (result[person] ?: 0L)
                    }
                }
            } else {
                result[person] = metadata.getDuration() + (result[person] ?: 0L)
            }
        }
        return result
    }
}
