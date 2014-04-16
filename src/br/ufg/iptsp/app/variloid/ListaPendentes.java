package br.ufg.iptsp.app.variloid;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

public class ListaPendentes extends BaseActivity implements
		OnItemClickListener {

	private ListView listView;
	private AdapterPendentes adapter;
	MultiValueMap<String, Object> mapFormularioTres;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_formulario);

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
							Data.formularioDois.getListSucesso().add(true);
						} else {
							Data.mapFormularioDois.add(Data.FORM2_KEY
									.concat(VariloidForm2.idCampos[i]), "");
							Data.formularioDois.getListSucesso().add(false);
						}
						f.setAccessible(false);
						
					}
				}
				Data.formularioDois.getListInativar().add(false);
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
				mapFormularioTres.add(
						Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(posicao)).concat("].")
								.concat(VariloidForm3.idCampos[i]),
						VariloidForm3.idCampos[i]);
				break;
			case 28:
				mapFormularioTres.add(
						Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(posicao)).concat("].")
								.concat(VariloidForm3.idCampos[i]),
						VariloidForm3.idCampos[i]);
				break;
			case 34:
				mapFormularioTres.add(
						Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(posicao)).concat("].")
								.concat(VariloidForm3.idCampos[i]),
						VariloidForm3.idCampos[i]);
				break;
			case 50:
				mapFormularioTres.add(
						Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(posicao)).concat("].")
								.concat(VariloidForm3.idCampos[i]),
						VariloidForm3.idCampos[i]);
				break;
			default:

				for (Field f : classe.getDeclaredFields()) {
					f.setAccessible(true);

					if (VariloidForm3.idCampos[i].equals(f.getName())) {

						if (f.get(object) != null) {
							mapFormularioTres.add(
									Data.FORM3_KEY.concat("[")
											.concat(String.valueOf(posicao))
											.concat("].")
											.concat(VariloidForm3.idCampos[i]),
									f.get(object));
						} else {
							mapFormularioTres.add(
									Data.FORM3_KEY.concat("[")
											.concat(String.valueOf(posicao))
											.concat("].")
											.concat(VariloidForm3.idCampos[i]),
									"");
						}
						f.setAccessible(false);
						Data.listaFormularioTres.get(posicao).getListInativar().add(false);
						Data.listaFormularioTres.get(posicao).getListSucesso().add(false);
					}
				}
				break;
			}

		}
		Data.listaMapFormularioTres.add(mapFormularioTres);
	}

	private class ServiceStartActivity extends AsyncTask<Integer, Void, Boolean> {

		private Context context;
		private boolean sucesso;
		
		public ServiceStartActivity(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			Data.listaFormularioTres.clear();
			Data.formularioDois = new FormularioDois();
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
					.getFormulariosTres();
			
			for (int i = 0; i < Data.listaFormularioTres.size(); i++) {

				FormularioTres formularioTres = adapter.getEntrevista(params[0])
						.getFormulariosTres().get(i);
				mapFormularioTres = new LinkedMultiValueMap<String, Object>();

				try {
					infoObjetoForm3(formularioTres, params[0]);
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
				intent.putExtra("listaPendentes", result);
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
