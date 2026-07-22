package com.keamanan.anak;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.database.Cursor;
import android.net.Uri;
public class WaNotifService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        if(!sbn.getPackageName().equals("com.whatsapp")) return;
        String title = ""+ sbn.getNotification().extras.getString("android.title");
        if(title==null||title.equals("null")||title.equals("WhatsApp")||title.length()<3) return;
        boolean isContact = isInContacts(title);
        String type = isContact ? "Kontak" : "NOMOR BARU!";
        String entry = System.currentTimeMillis()+ "|" + title + " ("+type+")\n";
        SharedPreferences sp = getSharedPreferences("keamanan_anak", MODE_PRIVATE);
        sp.edit().putString("wa_log", entry + sp.getString("wa_log","")).apply();
    }
    boolean isInContacts(String name){
        try{
            Cursor c = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts.DISPLAY_NAME+"=?", new String[]{name}, null);
            boolean found = c!=null && c.getCount()>0;
            if(c!=null) c.close();
            return found;
        }catch(Exception e){ return false; }
    }
}
