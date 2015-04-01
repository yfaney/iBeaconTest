package test.yfaney.ibeacontest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;


public class MyActivity extends Activity implements BeaconConsumer{
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = null;
    public final static int BLE_TX_POWER = -56;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        beaconManager = BeaconManager.getInstanceForApplication(this);
//        beaconManager.getBeaconParsers().add(new BeaconParser().
//                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=1AFF7700,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon beacon : beacons) {
                    String uuid = beacon.getId1().toUuidString();
                    Log.i(TAG, "The " + uuid + " is about " + getDistance(BLE_TX_POWER, beacon.getRssi()) + " meters away.");
                    makeToast("The " + uuid + " is about " + getDistance(BLE_TX_POWER, beacon.getRssi()) + " meters away.");
                }
            }

            private void makeToast(final String msg) {
                // create a handler to post messages to the main thread
                Handler mHandler = new Handler(getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("LondonTracker", null, null, null));
            Log.d(TAG, "Start Montoring!!");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private static double getDistance(double calibrated_power, double rssi){
        double ratio_db = calibrated_power - rssi;
        double ratio_linear = Math.pow(10, ratio_db / 10);
        return Math.sqrt(ratio_linear);
    }
}
