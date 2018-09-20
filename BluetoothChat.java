package com.example.android.BluetoothChat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BluetoothChat extends Activity {
    private static final boolean D = true;
    public static final String DEVICE_NAME = "device_name";
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_WRITE = 3;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final String TAG = "BluetoothChat";
    public static final String TOAST = "toast";
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    private String mConnectedDeviceName = null;
    private ArrayAdapter<String> mConversationArrayAdapter;
    private ListView mConversationView;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothChat.REQUEST_CONNECT_DEVICE /*1*/:
                    Log.i(BluetoothChat.TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_NONE /*0*/:
                        case BluetoothChat.REQUEST_CONNECT_DEVICE /*1*/:
                            BluetoothChat.this.mTitle.setText(R.string.title_not_connected);
                            return;
                        case BluetoothChat.REQUEST_ENABLE_BT /*2*/:
                            BluetoothChat.this.mTitle.setText(R.string.title_connecting);
                            return;
                        case BluetoothChat.MESSAGE_WRITE /*3*/:
                            BluetoothChat.this.mTitle.setText(R.string.title_connected_to);
                            BluetoothChat.this.mTitle.append(BluetoothChat.this.mConnectedDeviceName);
                            BluetoothChat.this.mConversationArrayAdapter.clear();
                            return;
                        default:
                            return;
                    }
                case BluetoothChat.REQUEST_ENABLE_BT /*2*/:
                    byte[] readBuf = msg.obj;
                    String time_str = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                    if (new String(readBuf, 0, msg.arg1).toString().equalsIgnoreCase("a")) {
                        BluetoothChat.this.mConversationArrayAdapter.add("ON : " + time_str);
                        return;
                    }
                    return;
                case BluetoothChat.MESSAGE_WRITE /*3*/:
                    String writeMessage = new String(msg.obj);
                    return;
                case BluetoothChat.MESSAGE_DEVICE_NAME /*4*/:
                    BluetoothChat.this.mConnectedDeviceName = msg.getData().getString(BluetoothChat.DEVICE_NAME);
                    Toast.makeText(BluetoothChat.this.getApplicationContext(), "Connected to " + BluetoothChat.this.mConnectedDeviceName, 0).show();
                    return;
                case BluetoothChat.MESSAGE_TOAST /*5*/:
                    Toast.makeText(BluetoothChat.this.getApplicationContext(), msg.getData().getString(BluetoothChat.TOAST), 0).show();
                    return;
                default:
                    return;
            }
        }
    };
    private StringBuffer mOutStringBuffer;
    private Button mSendButton;
    private TextView mTitle;
    private OnEditorActionListener mWriteListener = new OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (actionId == 0 && event.getAction() == BluetoothChat.REQUEST_CONNECT_DEVICE) {
                BluetoothChat.this.sendMessage(view.getText().toString());
            }
            Log.i(BluetoothChat.TAG, "END onEditorAction");
            return BluetoothChat.D;
        }
    };
    private ToggleButton tButton;
    private ToggleButton tButton1;
    private ToggleButton tButton2;
    private ToggleButton tButton3;
    private ToggleButton tButton4;
    private ToggleButton tButton5;
    private ToggleButton tButton6;
    private ToggleButton tButton7;
    TextView txtView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "+++ ON CREATE +++");
        requestWindowFeature(7);
        setContentView(R.layout.mn);
        getWindow().setFeatureInt(7, R.layout.custom_title);
        this.mTitle = (TextView) findViewById(R.id.title_left_text);
        this.mTitle.setText(R.string.app_name);
        this.mTitle = (TextView) findViewById(R.id.title_right_text);
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", REQUEST_CONNECT_DEVICE).show();
            finish();
        }
    }

    public void onStart() {
        super.onStart();
        Log.e(TAG, "++ ON START ++");
        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), REQUEST_ENABLE_BT);
        } else if (this.mChatService == null) {
            setupChat();
        }
    }

    public synchronized void onResume() {
        super.onResume();
        Log.e(TAG, "+ ON RESUME +");
        if (this.mChatService != null && this.mChatService.getState() == 0) {
            this.mChatService.start();
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        this.mConversationArrayAdapter = new ArrayAdapter(this, R.layout.message);
        this.mConversationView = (ListView) findViewById(R.id.in);
        this.mConversationView.setAdapter(this.mConversationArrayAdapter);
        this.tButton = (ToggleButton) findViewById(R.id.toggle);
        this.tButton1 = (ToggleButton) findViewById(R.id.toggle1);
        this.tButton2 = (ToggleButton) findViewById(R.id.toggle2);
        this.tButton3 = (ToggleButton) findViewById(R.id.toggle3);
        this.tButton4 = (ToggleButton) findViewById(R.id.toggle4);
        this.tButton5 = (ToggleButton) findViewById(R.id.toggle5);
        this.tButton6 = (ToggleButton) findViewById(R.id.toggle6);
        this.tButton7 = (ToggleButton) findViewById(R.id.toggle7);
        this.tButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.toggle /*2131099659*/:
                        if (BluetoothChat.this.tButton.isChecked()) {
                            BluetoothChat.this.sendMessage("A");
                            return;
                        }
                        BluetoothChat.this.sendMessage("B");
                        return;
                    default:
                        return;
                }
            }
        });
        this.tButton1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.toggle1 /*2131099661*/:
                        if (BluetoothChat.this.tButton1.isChecked()) {
                            BluetoothChat.this.sendMessage("C");
                            return;
                        }
                        BluetoothChat.this.sendMessage("D");
                        return;
                    default:
                        return;
                }
            }
        });
        this.tButton2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.toggle2 /*2131099664*/:
                        if (BluetoothChat.this.tButton2.isChecked()) {
                            BluetoothChat.this.sendMessage("E");
                            return;
                        }
                        BluetoothChat.this.sendMessage("F");
                        return;
                    default:
                        return;
                }
            }
        });
        this.tButton3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.toggle3 /*2131099665*/:
                        if (BluetoothChat.this.tButton3.isChecked()) {
                            BluetoothChat.this.sendMessage("G");
                            return;
                        }
                        BluetoothChat.this.sendMessage("H");
                        return;
                    default:
                        return;
                }
            }
        });
        this.tButton4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.toggle4 /*2131099668*/:
                        if (BluetoothChat.this.tButton4.isChecked()) {
                            BluetoothChat.this.sendMessage("I");
                            return;
                        }
                        BluetoothChat.this.sendMessage("J");
                        return;
                    default:
                        return;
                }
            }
        });
        this.tButton5.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.toggle5 /*2131099669*/:
                        if (BluetoothChat.this.tButton5.isChecked()) {
                            BluetoothChat.this.sendMessage("K");
                            return;
                        }
                        BluetoothChat.this.sendMessage("L");
                        return;
                    default:
                        return;
                }
            }
        });
        this.tButton6.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.toggle6 /*2131099671*/:
                        if (BluetoothChat.this.tButton6.isChecked()) {
                            BluetoothChat.this.sendMessage("M");
                            return;
                        }
                        BluetoothChat.this.sendMessage("N");
                        return;
                    default:
                        return;
                }
            }
        });
        this.tButton7.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.toggle7 /*2131099673*/:
                        if (BluetoothChat.this.tButton7.isChecked()) {
                            BluetoothChat.this.sendMessage("O");
                            return;
                        }
                        BluetoothChat.this.sendMessage("P");
                        return;
                    default:
                        return;
                }
            }
        });
        this.mChatService = new BluetoothChatService(this, this.mHandler);
        this.mOutStringBuffer = new StringBuffer("");
    }

    public synchronized void onPause() {
        super.onPause();
        Log.e(TAG, "- ON PAUSE -");
    }

    public void onStop() {
        super.onStop();
        Log.e(TAG, "-- ON STOP --");
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mChatService != null) {
            this.mChatService.stop();
        }
        Log.e(TAG, "--- ON DESTROY ---");
    }

    private void ensureDiscoverable() {
        Log.d(TAG, "ensure discoverable");
        if (this.mBluetoothAdapter.getScanMode() != 23) {
            Intent discoverableIntent = new Intent("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
            discoverableIntent.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 300);
            startActivity(discoverableIntent);
        }
    }

    private void sendMessage(String message) {
        if (this.mChatService.getState() != MESSAGE_WRITE) {
            Toast.makeText(this, R.string.not_connected, 0).show();
        } else if (message.length() > 0) {
            this.mChatService.write(message.getBytes());
            this.mOutStringBuffer.setLength(0);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE /*1*/:
                if (resultCode == -1) {
                    this.mChatService.connect(this.mBluetoothAdapter.getRemoteDevice(data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS)));
                    return;
                }
                return;
            case REQUEST_ENABLE_BT /*2*/:
                if (resultCode == -1) {
                    setupChat();
                    return;
                }
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, 0).show();
                finish();
                return;
            default:
                return;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return D;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan /*2131099674*/:
                startActivityForResult(new Intent(this, DeviceListActivity.class), REQUEST_CONNECT_DEVICE);
                return D;
            case R.id.discoverable /*2131099675*/:
                ensureDiscoverable();
                return D;
            default:
                return false;
        }
    }
}
