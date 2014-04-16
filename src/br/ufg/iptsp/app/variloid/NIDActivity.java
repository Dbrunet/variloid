package br.ufg.iptsp.app.variloid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class NIDActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_nid);
		
		Bundle bundle = getIntent().getExtras();
		TextView textView = (TextView) findViewById(R.id.nid);
		
		if(bundle!=null){
			String nid = bundle.getString("nid");
			boolean isPendente = bundle.getBoolean("ispendente");
			
//			SharedPreferences pref = getSharedPreferences(Variloid.PREFERENCIAS, 0);
//			if ((pref.getString(Variloid.IDS_PENDENTES, "").contains(nid))){
//				SharedPreferences prefs = getSharedPreferences(Variloid.PREFERENCIAS, MODE_PRIVATE);
//				SharedPreferences.Editor editor = prefs.edit();		
//				editor.putString(Variloid.IDS_PENDENTES, "");
//				editor.commit();
//			}
			
			textView.setText(nid);
			if(isPendente){
				Toast.makeText(this, "Dados enviados como Pendentes!", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, "Dados enviados com sucesso!", Toast.LENGTH_LONG).show();
			}
			
			
		}else{
			textView.setText(" "+ getString(R.string.nenhum_valor_retornado));	
			Toast.makeText(this, getString(R.string.nenhum_valor_retornado), Toast.LENGTH_LONG).show();
		}
		
		Button button = (Button) findViewById(R.id.buttonok);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(NIDActivity.this, VariloidActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra("sucesso", "sucesso");
				startActivity(i);
			}
		});
		super.onCreate(savedInstanceState);
	}
}
