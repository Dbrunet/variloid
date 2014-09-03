package br.ufg.iptsp.app.variloid;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.ufg.iptsp.app.variloid.negocio.Usuario;
import br.ufg.iptsp.app.variloid.provider.Data;
import br.ufg.iptsp.app.variloid.servico.Servico;
import br.ufg.iptsp.app.variloid.servico.ServicoConexao;

public class LoadingActivity extends BaseActivity {

	private Spinner spinner;
	private boolean isEscolhido;
	private String idAuxiliar;
	private BroadcastReceiver br;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_load);

		// Log.v("Valida Campos", "campos: "+VariloidForm4.campos.length);
		// Log.v("Valida Campos", "idCampos: "+VariloidForm4.idCampos.length);

		registerReceiver(br = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				boolean isNetworkDown = intent.getBooleanExtra(
						ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

				ServicoConexao.verificaTipoConexao(LoadingActivity.this);

				if (isNetworkDown) {
					((RelativeLayout) findViewById(R.id.relativelayout)).setVisibility(View.GONE);
					((Button) findViewById(R.id.button_reloader)).setVisibility(View.VISIBLE);
				}

			}
		}, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		((Button) findViewById(R.id.button_reloader))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ServicoConexao
								.verificaTipoConexao(LoadingActivity.this);
						if (ServicoConexao.isConectadoRede()) {
							new Service(LoadingActivity.this).execute();
						}

					}
				});

		spinner = (Spinner) findViewById(R.id.nome_aulixiar);
		((Button) findViewById(R.id.button_logar))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						ServicoConexao
								.verificaTipoConexao(LoadingActivity.this);
						if (ServicoConexao.isConectadoRede()) {

							if (!TextUtils.isEmpty(idAuxiliar)) {
								isEscolhido = true;
							} else {
								isEscolhido = false;
							}

							if (!isEscolhido) {
								Toast.makeText(
										LoadingActivity.this,
										getString(R.string.edt_id_auxiliar_insira),
										Toast.LENGTH_SHORT).show();
							} else {
								startActivity(new Intent(LoadingActivity.this,
										VariloidActivity.class));
								finish();
							}

						}
					}
				});

	}

	@Override
	protected void onStart() {
		new Service(this).execute();
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(br);
		super.onDestroy();
	}

	private class Service extends AsyncTask<Void, Void, Void> {

		private Servico servico;
		private List<Usuario> listaDeUsuarios;
		private Context context;
		private ProgressDialog pd;

		public Service(Context context) {
			this.context = context;
			servico = new Servico(context);
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(context);
			pd.setMessage("Aguarde. Carregando...");
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			listaDeUsuarios = servico.getUsuarios();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			pd.dismiss();
			if (listaDeUsuarios != null) {
				spinner(listaDeUsuarios);
				if (((RelativeLayout) findViewById(R.id.relativelayout))
						.getVisibility() == View.GONE) {
					((Button) findViewById(R.id.button_reloader))
							.setVisibility(View.GONE);
					((RelativeLayout) findViewById(R.id.relativelayout))
							.setVisibility(View.VISIBLE);
				}
			}
			
			new ServiceVersao(context).execute();
			super.onPostExecute(result);
		}
	}

	private class ServiceVersao extends AsyncTask<Void, Void, String> {

		private Servico servico;
		private Context context;

		public ServiceVersao(Context context) {
			this.context = context;
			servico = new Servico(context);
		}

		@Override
		protected String doInBackground(Void... params) {
			return servico.getVersao();
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null && getSoftwareVersion()!=null) {
				if (!result.equals(getSoftwareVersion())) {
					
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
					alertDialogBuilder.setTitle("Notificação de Versão");
					alertDialogBuilder.setMessage("Existe uma nova versão do aplicativo no sistema.")
					.setCancelable(false)
					.setNeutralButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(
							DialogInterface dialog, int id) {
							//
							}
						});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
					
				}
			}else{
				Log.e("ERRO Versão", "null");
			}
			super.onPostExecute(result);
		}
	}

	private String getSoftwareVersion() {
			PackageInfo packageAppInfo = null;
			try {
				packageAppInfo = getPackageManager().getPackageInfo(
						getPackageName(), 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return packageAppInfo.versionName;
	}

	private void spinner(final List<Usuario> listaDeUsuario) {

		String[] nomeUsuario = new String[listaDeUsuario.size() + 1];

		for (int i = 0; i < nomeUsuario.length; i++) {
			if (i > 0) {
				nomeUsuario[i] = listaDeUsuario.get(i - 1).getNome();
			} else {
				nomeUsuario[0] = "Escolhe o nome do Auxiliar.";
			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, nomeUsuario);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int posicao, long id) {

				if (posicao > 0) {
					((TextView) v).setTextColor(Color.BLACK);
					idAuxiliar = String.valueOf(listaDeUsuario.get(posicao - 1)
							.getId());

					Data.mapService.add(Variloid.ID_ENTREVISTADOR, idAuxiliar);

					SharedPreferences pref = getSharedPreferences(
							Variloid.PREFERENCIAS, MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();
					editor.putString(Variloid.ID_ENTREVISTADOR, idAuxiliar);
					editor.commit();

				} else {
					((TextView) v).setTextColor(Color.BLACK);
					idAuxiliar = "";
				}

//				Log.v("testeee", "idAuxiliar=" + idAuxiliar);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.variloid, menu);
		return super.onCreateOptionsMenu(menu);
	}

}
