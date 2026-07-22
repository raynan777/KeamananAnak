package com.keamanan.anak;
import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.content.*;
import android.os.BatteryManager;
import android.view.View;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

public class MainActivity extends Activity implements LocationListener {
    TextView txtBattery, txtLocation, txtInfo;
    LocationManager lm;
    String lastLoc = "Belum ada lokasi";

    protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        txtBattery = findViewById(R.id.txtBattery);
        txtLocation = findViewById(R.id.txtLocation);
        txtInfo = findViewById(R.id.txtInfo);

        // Battery
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : 0;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 100) : 100;
        int pct = (int)((level / (float)scale) * 100);
        int status = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1) : -1;
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        txtBattery.setText("🔋 Baterai: " + pct + "%" + (isCharging ? " (Charging)" : ""));

        // Device info
        txtInfo.setText("📱 Info HP:\nModel: " + android.os.Build.MODEL + "\nAndroid: " + android.os.Build.VERSION.RELEASE + "\nID: " + android.os.Build.ID);

        lm = (LocationManager)getSystemService(LOCATION_SERVICE);

        findViewById(R.id.btnGPS).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){ getLocation(); }
        });
        findViewById(R.id.btnSOS).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "🚨 SOS! Lokasi terakhir: " + lastLoc + "\n(Dalam versi full akan kirim SMS ke orang tua)", Toast.LENGTH_LONG).show();
            }
        });
    }

    void getLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        txtLocation.setText("📍 Mencari lokasi...");
        try{
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(loc == null) loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(loc != null) updateLoc(loc);
        }catch(Exception e){ txtLocation.setText("Error: " + e.getMessage()); }
    }

    void updateLoc(Location loc){
        lastLoc = loc.getLatitude() + "," + loc.getLongitude();
        txtLocation.setText("📍 Lokasi Anak:\nLat: " + loc.getLatitude() + "\nLng: " + loc.getLongitude() + "\nhttps://maps.google.com/?q=" + lastLoc);
    }

    public void onLocationChanged(Location loc){ updateLoc(loc); }
    public void onStatusChanged(String s,int i,Bundle b){}
    public void onProviderEnabled(String s){}
    public void onProviderDisabled(String s){}
}
