package sensors_in_paradise.xsens.page1

import com.xsens.dot.android.sdk.models.XsensDotDevice
import sensors_in_paradise.xsens.StatefulBluetoothDevice

interface ConnectionInterface {
    fun onConnectedDevicesChanged(devices: ArrayList<XsensDotDevice>)
}