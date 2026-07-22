package com.keamanan.anak;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.*;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;
import android.app.usage.UsageStatsManager;
import android.app.usage.UsageStats;
import java.util.*;
public class MainActivity extends Activity {
    SharedPreferences sp; TextView txtApp, txtHist, txtWa, txtLoc;
    protected void onCreate(Bundle b){
        super.onCreate(b); setContentView(R.layout.activity_main);
        sp = getSharedPreferences("keamanan_anak", MODE_PRIVATE);
        txtApp=findViewById(R.id.txtApp); txtHist=findViewById(R.id.txtHistory); txtWa=findViewById(R.id.txtWa); txtLoc=findViewById(R.id.txtLoc);
        findViewById(R.id.btnStartLoc).setOnClickListener(v->{
            startForegroundService(new Intent(this, LocationService.class));
            Toast.makeText(this,"Lokasi real-time AKTIF",Toast.LENGTH_LONG).show();
        });
        findViewById(R.id.btnUsage).setOnClickListener(v->{
            AppOpsManager am = (AppOpsManager)getSystemService(APP_OPS_SERVICE);
            if(am.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName())!=AppOpsManager.MODE_ALLOWED){
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                Toast.makeText(this,"Aktifkan Keamanan Anak di daftar",Toast.LENGTH_LONG).show(); return;
            }
            loadApps();
        });
        findViewById(R.id.btnNotif).setOnClickListener(v-> startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")));
        findViewById(R.id.btnRefresh).setOnClickListener(v->{loadHistory();loadWa();loadApps();});
        loadHistory(); loadWa();
    }
    void loadApps(){
        UsageStatsManager usm = (UsageStatsManager)getSystemService(USAGE_STATS_SERVICE);
        long end=System.currentTimeMillis(); long start=end-24*60*60*1000;
        List<UsageStats> stats=usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,start,end);
        if(stats==null){txtApp.setText("Aktifkan izin dulu"); return;}
        stats.sort((a,c)-> Long.compare(c.getTotalTimeInForeground(), a.getTotalTimeInForeground()));
        StringBuilder sb=new StringBuilder("APLIKASI DIPAKAI HARI INI:\n"); int cnt=0;
        for(UsageStats s:stats){ if(s.getTotalTimeInForeground()>60000 && !s.getPackageName().contains("keamanan")){ sb.append(s.getPackageName()).append(" - ").append(s.getTotalTimeInForeground()/60000).append(" menit\n"); if(++cnt>=10)break; } }
        txtApp.setText(sb.toString());
    }
    void loadHistory(){ txtHist.setText(sp.getString("history","Belum ada riwayat")); txtLoc.setText("Last: "+sp.getString("last_loc","-")); }
    void loadWa(){ txtWa.setText(sp.getString("wa_log","Belum ada log")); }
}
