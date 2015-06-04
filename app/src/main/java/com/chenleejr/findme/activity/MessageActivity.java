package com.chenleejr.findme.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.chenleejr.findme.R;
import com.chenleejr.findme.application.MyApplication;
import com.chenleejr.findme.bean.User;
import com.chenleejr.findme.thread.ConfirmMessageThread;
import com.chenleejr.findme.thread.LeaveMessageThread;

import java.util.concurrent.ExecutorService;

public class MessageActivity extends Activity implements OnEditorActionListener {
    @Override
    protected void onDestroy() {
        app.getList().remove(this);
        super.onStop();
    }

    private EditText et;
    private TextView tv;
    private MyApplication app;
    private User to;
    private ExecutorService pool;
    private Handler handler = new Handler() {
        public void handleMessage(Message m) {
            et.setEnabled(true);
            String message = "";
            switch (m.what) {
                case 1:
                    message = "leave message success";
                    break;
                case 2:
                    message = "leave message failed";
                    break;
                case 3:
                    message = "bad request";
                    break;
                case 4:
                    message = "confirm message failed";
                    break;
                case 5:
                    message = "confirm message success";
                    break;
                default:
                    message = "something wrong has happened";
                    break;
            }
            Toast.makeText(MessageActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    public static void actionStart(Context ctx, String message, int id) {
        Intent intent = new Intent(ctx, MessageActivity.class);
        intent.putExtra("message", message);
        intent.putExtra("id", id);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        et = (EditText) this.findViewById(R.id.et_message);
        tv = (TextView) this.findViewById(R.id.tv_message);
        app = (MyApplication) this.getApplication();
        pool = app.getCachedThreadPool();
        for (Activity a : app.getList()) {
            if (a instanceof MessageActivity) {
                a.finish();
            }
        }
        app.getList().add(this);
        et.setOnEditorActionListener(this);
        to = new User();
        //to.setId(getIntent().getExtras().getInt("id"));
        Intent intent = this.getIntent();
        if (!intent.getExtras().getString("message").equals("")) {
            et.setVisibility(View.GONE);
            String message = intent.getExtras().getString("message");
            message = message.replaceAll("&&&", "\n");
            tv.setText(message);
            //new ConfirmMessageThread(app, handler).start();
            pool.execute(new ConfirmMessageThread(app, handler));
        } else if (intent.getExtras().getInt("id") != 0) {
            to.setId(getIntent().getExtras().getInt("id"));
        }
    }

    @Override
    public boolean onEditorAction(TextView arg0, int actionId, KeyEvent arg2) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            et.setEnabled(false);
            //new LeaveMessageThread(app, handler, to, et.getText().toString().trim()).start();
            pool.execute(new LeaveMessageThread(app, handler, to, et.getText().toString().trim()));
        }
        return false;
    }
}
