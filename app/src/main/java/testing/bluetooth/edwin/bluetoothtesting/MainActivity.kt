package testing.bluetooth.edwin.bluetoothtesting

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView


class MainActivity : AppCompatActivity() {

    private var address_nameList = mutableMapOf<String?, Any?>()
    private var newly_append = mutableMapOf<String?, Any?>()

    private val bleScanner = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            //Log.d("Edwin", "onScanResult(): ${result?.device?.address} - ${result?.device?.name}")
            var address = result?.device?.address
            var name = result?.device?.name
            var found = false

            for ((naddress, nname) in address_nameList){
                if(address == naddress){
                    found = true
                }
            }
            if(found == false){
                address_nameList[address] = name;
                Log.d("Edwin", "onScanResult(): ${address} - ${name}")
            }


        }
    }

    private val bluetoothLeScanner: BluetoothLeScanner
        get() {
            val bluetoothManager = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter
            if (!bluetoothAdapter.isEnabled()) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                Log.e("Edwin", bluetoothAdapter.bluetoothLeScanner.toString())

            }
            return bluetoothAdapter.bluetoothLeScanner
        }

    class ListDevicesAdapter(context: Context?, resource: Int) : ArrayAdapter<String>(context, resource) {
    }

    private val REQUEST_ENABLE_BT = 2

    override fun onActivityResult(requestCode: Int, resultCode: Int,  data: Intent) {
        if(requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == RESULT_OK){
                Log.e("Edwin", "Enabled")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //startupBluetooth();
        val listDevices = findViewById(R.id.list_devices) as ListView
        listDevices.adapter = ListDevicesAdapter(this, R.id.list_devices)
    }

    override fun onStart() {
        Log.d("Edwin", "onStart()")
        super.onStart()
        when (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> {
                bluetoothLeScanner.startScan(bleScanner)
            }
            else -> requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> when (grantResults) {
                intArrayOf(PackageManager.PERMISSION_GRANTED) -> {
                    Log.d("Edwin", "onRequestPermissionsResult(PERMISSION_GRANTED)")
                    bluetoothLeScanner.startScan(bleScanner)
                }
                else -> {
                    Log.d("Edwin", "onRequestPermissionsResult(not PERMISSION_GRANTED)")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onStop() {
        Log.d("Edwin", "onStop()")
        super.onStop()
        bluetoothLeScanner.stopScan(bleScanner)
    }
}
