package br.ufg.iptsp.app.variloid;

import java.lang.reflect.Field;
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
import br.ufg.iptsp.app.variloid.negocio.FormularioDois;
import br.ufg.iptsp.app.variloid.negocio.FormularioTres;
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
		new ServiceIdsPendentes(this, pref.getString(Variloid.ID_ENTREVISTADOR,
				"")).execute();
		super.onStart();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
			long arg3) {

		final AlertDialog.Builder alert = new AlertDialog.Builder(
				ListaPendentes.this);
		alert.setTitle(getString(R.string.formulario_alerta));
		alert.setMessage(getString(R.string.notificacao_limpar_memoria));
		alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				new ServiceStartActivity(ListaPendentes.this)
						.execute(new Integer[] { arg2 });
			}
		});

		alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();

	}

	private void infoObjetoForm2(Object object)
			throws IllegalArgumentException, IllegalAccessException {
		// percorrendo as variáveis de instancia
		Class<?> classe = object.getClass();

		for (int i = 0; i < VariloidForm2.idCampos.length; i++) {

			switch (i) {
			case 14:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				break;
			case 27:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				break;
			case 32:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				break;
			case 47:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				break;
			case 53:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				break;
			case 69:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				break;
			default:

				for (Field f : classe.getDeclaredFields()) {
					f.setAccessible(true);

					if (VariloidForm2.idCampos[i].equals(f.getName())) {

						if (f.get(object) != null) {
							Data.mapFormularioDois.add(Data.FORM2_KEY
									.concat(VariloidForm2.idCampos[i]), f
									.get(object));
						} else {
							Data.mapFormularioDois.add(Data.FORM2_KEY
									.concat(VariloidForm2.idCampos[i]), "");
						}
						f.setAccessible(false);
					}
				}

				break;
			}
		}
	}

	private void infoObjetoForm3(Object object, int posicao)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?> classe = object.getClass();

		for (int i = 0; i < VariloidForm3.idCampos.length; i++) {

			switch (i) {
			case 14:
				Data.mapFormularioTres.add(
						Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(posicao)).concat("]")
								.concat(VariloidForm3.idCampos[i]),
						VariloidForm3.idCampos[i]);
				break;
			case 28:
				Data.mapFormularioTres.add(
						Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(posicao)).concat("]")
								.concat(VariloidForm3.idCampos[i]),
						VariloidForm3.idCampos[i]);
				break;
			case 34:
				Data.mapFormularioTres.add(
						Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(posicao)).concat("]")
								.concat(VariloidForm3.idCampos[i]),
						VariloidForm3.idCampos[i]);
				break;
			case 50:
				Data.mapFormularioTres.add(
						Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(posicao)).concat("]")
								.concat(VariloidForm3.idCampos[i]),
						VariloidForm3.idCampos[i]);
				break;
			default:

				for (Field f : classe.getDeclaredFields()) {
					f.setAccessible(true);

					if (VariloidForm3.idCampos[i].equals(f.getName())) {

						if (f.get(object) != null) {
							Data.mapFormularioTres.add(
									Data.FORM3_KEY.concat("[")
											.concat(String.valueOf(posicao))
											.concat("]")
											.concat(VariloidForm3.idCampos[i]),
									f.get(object));
						} else {
							Data.mapFormularioTres.add(
									Data.FORM3_KEY.concat("[")
											.concat(String.valueOf(posicao))
											.concat("]")
											.concat(VariloidForm3.idCampos[i]),
									"");
						}
						f.setAccessible(false);
					}
				}
				break;
			}

		}
		Data.listaMapFormularioTres.add(Data.mapFormularioTres);
	}

	private class ServiceStartActivity extends AsyncTask<Integer, Void, Boolean> {

		private Context context;
		private boolean sucesso;
		
		public ServiceStartActivity(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			Data.formularioDois = new FormularioDois();
			Data.formularioTres = new FormularioTres();
			Data.listaFormularioTres.clear();
			
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			Data.formularioDois = adapter.getEntrevista(params[0])
					.getFormularioDois();

			try {
				infoObjetoForm2(Data.formularioDois);
				sucesso=true;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				sucesso=false;
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				sucesso=false;
				e.printStackTrace();
			}

			Data.listaFormularioTres = adapter.getEntrevista(params[0])
					.getFormularioTres();
			
			for (int i = 0; i < Data.listaFormularioTres.size(); i++) {

				Data.formularioTres = adapter.getEntrevista(params[0])
						.getFormularioTres().get(i);

				try {
					infoObjetoForm3(Data.formularioTres, params[0]);
					sucesso=true;
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					sucesso=false;
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					sucesso=false;
					e.printStackTrace();
				}
			}

			return sucesso;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			
			if(result){
				Intent intent = new Intent(context, Formulario2Activity.class);
				context.startActivity(intent);
			}
			
			super.onPostExecute(result);
		}
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
				if (!entrevistas.isEmpty()) {

					adapter = new AdapterPendentes(context,
							R.layout.adapter_formulario, entrevistas);
					listView.setAdapter(adapter);
				} else {
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
