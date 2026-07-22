package com.keamanan.anak;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.database.Cursor;
public class WaNotifService extends NotificationListenerService {
    @Override public void onNotificationPosted(StatusBarNotification sbn){
        if(!"com.whatsapp".equals(sbn.getPackageName())) return;
        String title = sbn.getNotification().extras.getString("android.title");
        if(title==null||title.length()<3||"WhatsApp".equals(title)) return;
        boolean isContact = false;
        try{ Cursor c = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts.DISPLAY_NAME+"=?", new String[]{title}, null); if(c!=null){isContact=c.getCount()>0; c.close();} }catch(Exception e){}
        String type = isContact ? "Kontak" : "NOMOR BARU!";
        SharedPreferences sp = getSharedPreferences("keamanan_anak", MODE_PRIVATE);
        sp.edit().putString("wa_log", title+" ("+type+")\n" + sp.getString("wa_log","")).apply();
    }
}
