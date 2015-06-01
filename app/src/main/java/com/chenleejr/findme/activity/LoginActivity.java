package com.chenleejr.findme.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.chenleejr.findme.R;
import com.chenleejr.findme.application.MyApplication;
import com.chenleejr.findme.thread.LoginThread;

public class LoginActivity extends Activity implements OnEditorActionListener, OnClickListener{
	private EditText etName;
	private EditText etPassword;
	private CheckBox cb;
	private Button button;
	private MyApplication app;
	private SharedPreferences sp;
	private final String fileName = "NameAndPwd";
	private Handler myHandler = new Handler(){
		public void handleMessage(Message m){
			button.setEnabled(true);
			etName.setEnabled(true);
			etPassword.setEnabled(true);
			String message;
			switch(m.what){
			case 1:message = "login failed";etPassword.setText("");break;
			case 2:message = "login success";
					Editor e = sp.edit();
					e.putString("name", etName.getText().toString().trim());
					if (cb.isChecked()){
						e.putString("pwd", etPassword.getText().toString().trim());
					} else {
						e.putString("pwd", "");
					}
					e.commit();
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				    startActivity(intent);
				    LoginActivity.this.finish();break;
			//case 3:message = "upload failed";break;
			case 4:message = "bad request";break;//net error
			default:message = "something wrong has happened";break;
			}	
			Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.login);
	    etName = (EditText) this.findViewById(R.id.username_edit);
	    etPassword = (EditText) this.findViewById(R.id.password_edit);
	    button = (Button) this.findViewById(R.id.bn_login);
	    cb = (CheckBox) this.findViewById(R.id.rem_pwd);
	    app = (MyApplication) this.getApplication();
	    app.getList().add(this);
	    sp = this.getSharedPreferences(fileName, Context.MODE_PRIVATE);
	    etName.setOnEditorActionListener(this);
	    etPassword.setOnEditorActionListener(this);
	    button.setOnClickListener(this);
	}
	
	@Override
	protected void onStart() {
		String name = sp.getString("name", "");
		String password = sp.getString("pwd", "");
		etName.setText(name);
		etPassword.setText(password);
		super.onStart();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE){
			doLogin();
		}
		return false;
	}
	private void doLogin(){
		String name = etName.getText().toString().trim();
		String password = etPassword.getText().toString().trim();
		button.setEnabled(false);
		etName.setEnabled(false);
		etPassword.setEnabled(false);
		new LoginThread(app, myHandler, name, password).start();
	}

	@Override
	public void onClick(View v) {
		doLogin();
	}

	@Override
	protected void onDestroy() {
		app.getList().remove(this);
		super.onDestroy();
	}
	
	
}
