package com.smartahc.android.example.ibeacon.ibeacon.scan

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.support.annotation.RequiresApi
import com.smartahc.android.example.ibeacon.ibeacon.util.BeaconParser

/**
 * Created by yuan on 25/08/2017.
 * android 5.0 le -> scanner
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class BeaconLeCallback(onBeaconScanListener: OnBeaconScanListener) : ScanCallback() {

    val listener: OnBeaconScanListener = onBeaconScanListener

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)
        result?.let {
            // result
            val device = it.device
            val rssi = it.rssi
            val data = it.scanRecord.bytes
            val filter = listener.getScanFilter()
            // 无过滤
            if (filter.mac.isEmpty() && filter.name.isEmpty()) {
                // 无过滤，解析全部
                parse(device, rssi, data)
                return
            }

            // 过滤：mac
            if (filter.mac.isNotEmpty() && filter.name.isEmpty()) {
                // 过滤：mac
                device?.address?.let {
                    if (it == filter.mac) {
                        parse(device, rssi, data)
                        return
                    }
                }
            }

            // 过滤：name
            if (filter.name.isNotEmpty() && filter.mac.isEmpty()) {
                device?.name?.let {
                    if (it == filter.name) {
                        parse(device, rssi, data)
                        return
                    }
                }
            }

            // 过滤： name and mac
            if (filter.name.isNotEmpty() && filter.mac.isNotEmpty()) {
                device?.address?.let {
                    if (it == filter.mac) {
                        device.name?.let {
                            if (it == filter.name) {
                                parse(device, rssi, data)
                                return
                            }
                        }
                    }
                }
            }
        }


    }

    /**
     * 解析与回调
     */
    private fun parse(device: BluetoothDevice?, rssi: Int, data: ByteArray?) {
        data?.let {
            val beacon = BeaconParser.parse(device, rssi, it)
            listener.OnScanResult(beacon)
        }
    }


}
