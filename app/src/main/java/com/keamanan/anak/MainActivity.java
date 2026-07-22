package com.keamanan.anak;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.Gravity;
import android.graphics.Color;
public class MainActivity extends Activity {
    protected void onCreate(Bundle b){
        super.onCreate(b);
        TextView tv=new TextView(this);
        tv.setText("\n\nKEAMANAN ANAK\n\nBuild Berhasil!\nVersi 2.0\n\nAplikasi jalan ✅");
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(22);
        tv.setTextColor(Color.BLACK);
        tv.setBackgroundColor(Color.WHITE);
        tv.setPadding(40,100,40,40);
        setContentView(tv);
    }
}
