package br.ufg.iptsp.app.variloid;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class BaseActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.background_vermelho));
	}

}
