package com.example.lab5_1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;
    private IntentFilter filter;

    public void processReceive(Context context, Intent intent) {
        // Hiển thị Toast thông báo tin nhắn mới
        Toast.makeText(context, context.getString(R.string.you_have_a_new_message), Toast.LENGTH_LONG).show();

        // Tham chiếu đến TextView để hiển thị nội dung SMS
        TextView tvContent = (TextView) findViewById(R.id.tv_content);

        // Lấy dữ liệu từ intent
        final String SMS_EXTRA = "pdus";
        Bundle bundle = intent.getExtras();
        Object[] messages = (Object[]) bundle.get(SMS_EXTRA);

        String sms = "";
        SmsMessage smsMsg;

        // Duyệt qua từng tin nhắn nhận được
        for (int i = 0; i < messages.length; i++) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                smsMsg = SmsMessage.createFromPdu((byte[]) messages[i], bundle.getString("format"));
            } else {
                smsMsg = SmsMessage.createFromPdu((byte[]) messages[i]);
            }

            // Lấy nội dung tin nhắn và số điện thoại gửi đến
            String msgBody = smsMsg.getMessageBody();
            String address = smsMsg.getDisplayOriginatingAddress();
            sms += address + ": " + msgBody + "\n";
        }

        // Hiển thị nội dung SMS lên TextView
        tvContent.setText(sms);
    }
    private void initBroadcastReceiver() {
        // Tạo bộ lọc để lắng nghe sự kiện SMS_RECEIVED
        filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

        // Tạo BroadcastReceiver để xử lý khi có tin nhắn đến
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Gọi hàm xử lý khi nhận được tin nhắn
                processReceive(context, intent);
            }
        };
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Khởi tạo BroadcastReceiver nếu chưa tồn tại
        if (broadcastReceiver == null) {
            initBroadcastReceiver();
        }

        // Đăng ký receiver để lắng nghe tin nhắn đến
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBroadcastReceiver();

        // Kiểm tra quyền SMS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},
                    100);
        }
    }

}
