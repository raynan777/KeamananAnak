package com.keamanan.anak;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.view.Gravity;
public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle b){ super.onCreate(b); TextView tv=new TextView(this); tv.setText("Keamanan Anak v2\nBuild Berhasil!"); tv.setGravity(Gravity.CENTER); tv.setTextSize(20); tv.setPadding(40,300,40,40); setContentView(tv); }
}
