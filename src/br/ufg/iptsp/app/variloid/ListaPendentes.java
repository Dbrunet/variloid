package br.ufg.iptsp.app.variloid;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import br.ufg.iptsp.app.variloid.adapter.AdapterPendentes;
import br.ufg.iptsp.app.variloid.negocio.Entrevista;
import br.ufg.iptsp.app.variloid.provider.Data;
import br.ufg.iptsp.app.variloid.servico.Servico;

import com.actionbarsherlock.app.SherlockActivity;

public class ListaPendentes extends SherlockActivity implements
		OnItemClickListener {

	private ListView listView;
	private AdapterPendentes adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_formulario);

		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.background_vermelho));
		getSupportActionBar().setTitle(getString(R.string.lista_de_pendentes));

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		SharedPreferences pref = getSharedPreferences(Variloid.PREFERENCIAS, 0);
		new ServiceIdsPendentes(this,
				pref.getString(Variloid.ID_ENTREVISTADOR, "")).execute();
		super.onStart();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(ListaPendentes.this);
		alert.setTitle(getString(R.string.formulario_alerta));
		alert.setMessage(getString(R.string.notificacao_limpar_memoria));
		alert.setPositiveButton("Sim",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				Data.formularioDois = adapter.getEntrevista(arg2).getFormularioDois();
				Data.listaFormularioTres = adapter.getEntrevista(arg2).getFormularioTres();
				
				Intent intent = new Intent(ListaPendentes.this, Formulario2Activity.class);
				startActivity(intent);
				dialog.cancel();
			}
		});

		alert.setNegativeButton("Não",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();
		

	}

	private class ServiceIdsPendentes extends AsyncTask<Void, Void, Void> {

		private Context context;
		private ProgressDialog pd;
		private Servico servico;
		private String idUsuario;
		private List<Entrevista> entrevistas;

		public ServiceIdsPendentes(Context context, String idUsuario) {
			this.context = context;
			this.idUsuario = idUsuario;
		}

		@Override
		protected void onPreExecute() {
			servico = new Servico(context);
			pd = new ProgressDialog(context);
			pd.setMessage("Carregando...");
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			entrevistas = servico.getEntrevistaPendente(idUsuario);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			pd.dismiss();

			if (entrevistas != null) {
				if(!entrevistas.isEmpty()){
					
					adapter = new AdapterPendentes(context,
							R.layout.adapter_formulario, entrevistas);
					listView.setAdapter(adapter);
				}else{
					Toast.makeText(context,
							context.getString(R.string.nenhum_item_pendente),
							Toast.LENGTH_LONG).show();
				}

			} else {
				Toast.makeText(context,
						context.getString(R.string.nenhum_item_pendente),
						Toast.LENGTH_LONG).show();
			}

			super.onPostExecute(result);
		}
	}

}
