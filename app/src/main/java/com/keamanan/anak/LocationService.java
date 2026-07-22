package com.keamanan.anak;
import android.app.*;
import android.content.*;
import android.location.*;
import android.os.IBinder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class LocationService extends Service implements LocationListener {
    LocationManager lm;
    @Override public int onStartCommand(Intent intent,int f,int id){
        NotificationChannel ch = new NotificationChannel("loc","Lokasi", NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        Notification n = new Notification.Builder(this,"loc").setContentTitle("Keamanan Anak aktif").setContentText("Pantau lokasi real-time").setSmallIcon(android.R.drawable.ic_menu_mylocation).build();
        startForeground(1,n);
        lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        try{ lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 900000, 10, this); }catch(Exception e){}
        try{ lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 900000, 10, this); }catch(Exception e){}
        return START_STICKY;
    }
    public void onLocationChanged(Location loc){
        String time = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault()).format(new Date());
        String entry = time + " - https://maps.google.com/?q="+loc.getLatitude()+","+loc.getLongitude()+"\n";
        SharedPreferences sp = getSharedPreferences("keamanan_anak", MODE_PRIVATE);
        sp.edit().putString("history", entry + sp.getString("history","")).putString("last_loc", loc.getLatitude()+","+loc.getLongitude()).apply();
    }
    public IBinder onBind(Intent i){return null;}
    public void onProviderEnabled(String s){} public void onProviderDisabled(String s){}
}
