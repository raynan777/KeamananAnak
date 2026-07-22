package com.keamanan.anak;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.location.LocationManager;
import java.util.*;
import android.Manifest;
import androidx.core.app.ActivityCompat;

public class MainActivity extends Activity {
    SharedPreferences sp;
    TextView txtApp, txtHist, txtWa, txtLoc;

    protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("keamanan_anak", MODE_PRIVATE);
        txtApp = findViewById(R.id.txtApp);
        txtHist = findViewById(R.id.txtHistory);
        txtWa = findViewById(R.id.txtWa);
        txtLoc = findViewById(R.id.txtLoc);

        findViewById(R.id.btnStartLoc).setOnClickListener(v->{
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_CONTACTS},1);return;
            }
            startForegroundService(new Intent(this, LocationService.class));
            Toast.makeText(this,"Lokasi real-time AKTIF (15 menit update)",Toast.LENGTH_LONG).show();
        });
        findViewById(R.id.btnUsage).setOnClickListener(v->{
            if(!hasUsageAccess()){ startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)); Toast.makeText(this,"Aktifkan Keamanan Anak",Toast.LENGTH_LONG).show(); return; }
            loadApps();
        });
        findViewById(R.id.btnNotif).setOnClickListener(v-> startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")));
        findViewById(R.id.btnRefresh).setOnClickListener(v->{loadHistory();loadWa();loadApps();});
        loadHistory(); loadWa();
    }
    boolean hasUsageAccess(){
        AppOpsManager appOps = (AppOpsManager)getSystemService(APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
        return mode==AppOpsManager.MODE_ALLOWED;
    }
    void loadApps(){
        UsageStatsManager usm = (UsageStatsManager)getSystemService(USAGE_STATS_SERVICE);
        long end = System.currentTimeMillis(); long start = end - 24*60*60*1000;
        List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
        if(stats==null){ txtApp.setText("Belum ada data"); return; }
        stats.sort((a,b)-> Long.compare(b.getTotalTimeInForeground(), a.getTotalTimeInForeground()));
        StringBuilder sb = new StringBuilder("APLIKASI DIPAKAI HARI INI:\n");
        int count=0;
        for(UsageStats s: stats){
            if(s.getTotalTimeInForeground()>60*1000 && !s.getPackageName().startsWith("com.keamanan")){
                long mins = s.getTotalTimeInForeground()/60000;
                sb.append("• ").append(s.getPackageName()).append(" - ").append(mins).append(" menit\n");
                if(++count>=15) break;
            }
        }
        txtApp.setText(sb.toString());
    }
    void loadHistory(){ txtHist.setText(sp.getString("history","Belum ada riwayat\nKlik START LOKASI REAL-TIME")); txtLoc.setText("Last: "+sp.getString("last_loc","-")); }
    void loadWa(){ txtWa.setText(sp.getString("wa_log","Belum ada log WA\nAktifkan izin notifikasi dulu")); }
}
