package sensors_in_paradise.sonar.page1

import com.xsens.dot.android.sdk.models.XsensDotDevice

class XSENSArrayList : ArrayList<XsensDotDevice>() {
    fun contains(deviceAddress: String): Boolean {
        for (device in this) {
            if (device.address == deviceAddress) {
                return true
            }
        }
        return false
    }
    fun get(deviceAddress: String): XsensDotDevice? {
        for (device in this) {
            if (device.address == deviceAddress) {
                return device
            }
        }
        return null
    }
    fun indexOf(deviceAddress: String): Int {
        for ((index, device) in this.withIndex()) {
            if (device.address == deviceAddress) {
                return index
            }
        }
        return -1
    }
    fun getConnected(): XSENSArrayList {
        val devices = XSENSArrayList()
        for (device in this) {
            if (device.connectionState == XsensDotDevice.CONN_STATE_CONNECTING) {
                devices.add(device)
            }
        }
        return devices
    }
}
