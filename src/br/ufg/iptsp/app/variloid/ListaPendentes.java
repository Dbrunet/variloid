package br.ufg.iptsp.app.variloid;

import java.lang.reflect.Field;
import java.util.ArrayList;
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

	private void infoObjetoForm2(FormularioDois object)
			throws IllegalArgumentException, IllegalAccessException {
		// percorrendo as variáveis de instancia
		Class<?> classe = object.getClass();

		for (int i = 0; i < VariloidForm2.idCampos.length; i++) {

			switch (i) {
			
			case 0:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				Data.formularioDois.getListSucesso().add(false);
				break;
			case 3:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				Data.formularioDois.getListSucesso().add(false);
				break;
			case 28:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				Data.formularioDois.getListSucesso().add(false);
				break;
			case 33:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				Data.formularioDois.getListSucesso().add(false);
				break;
			case 44:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				Data.formularioDois.getListSucesso().add(false);
				break;
			case 52:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				Data.formularioDois.getListSucesso().add(false);
				break;
			case 57:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				Data.formularioDois.getListSucesso().add(false);
				break;
			case 75:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				Data.formularioDois.getListSucesso().add(false);
				break;
			case 86:
				Data.mapFormularioDois.add(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i]),
						VariloidForm2.idCampos[i]);
				Data.formularioDois.getListSucesso().add(false);
				break;
				
			default:

				for (Field f : classe.getDeclaredFields()) {
					f.setAccessible(true);

					if("id".equals(f.getName())){
						Data.mapService.add("formularioDois.id", String.valueOf(f.get(object)));
					}
					
					if("lesaoColetada".equals(f.getName())){
						f.setAccessible(true);
						Class<?> classeLesao = object.getLesaoColetada().getClass();
						for (Field field : classeLesao.getDeclaredFields()) {
							field.setAccessible(true);
							if("id".equals(field.getName())){
								Data.mapService.add(Data.FORM2_KEY.concat("lesaoColetada.id"), String.valueOf(field.get( object.getLesaoColetada())));
							}
						}
						
					}
					
					if("cartaoVacina".equals(f.getName())){
						f.setAccessible(true);
						Class<?> classeCartao = object.getCartaoVacina().getClass();
						for (Field field : classeCartao.getDeclaredFields()) {
							field.setAccessible(true);
							if("id".equals(field.getName())){
								Data.mapService.add(Data.FORM2_KEY.concat("cartaoVacina.id"), String.valueOf(field.get(object.getCartaoVacina())));
							}
						}
						
					}
					
					if("panoramica".equals(f.getName())){
						f.setAccessible(true);
						Class<?> classePanoramica = object.getPanoramica().getClass();
						for (Field field : classePanoramica.getDeclaredFields()) {
							field.setAccessible(true);
							if("id".equals(field.getName())){
								Data.mapService.add(Data.FORM2_KEY.concat("panoramica.id"), String.valueOf(field.get(object.getPanoramica())));
							}
						}
					}
					
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
				break;
			}
			Data.formularioDois.getListInativar().add(false);
		}
	}

	private void infoObjetoForm3(FormularioTres object, int posicao)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?> classe = object.getClass();

		mapFormularioTres = new LinkedMultiValueMap<String, Object>();
		
		for (int i = 0; i < VariloidForm3.idCampos.length; i++) {

			switch (i) {
			
			case 0:
				mapFormularioTres.add(
						Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(posicao)).concat("].")
								.concat(VariloidForm3.idCampos[i]),
						VariloidForm3.idCampos[i]);
				Data.listaFormularioTres.get(posicao).getListSucesso().add(false);
				break;
			case 4:
				mapFormularioTres.add(
						Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(posicao)).concat("].")
								.concat(VariloidForm3.idCampos[i]),
						VariloidForm3.idCampos[i]);
				Data.listaFormularioTres.get(posicao).getListSucesso().add(false);
				break;
			case 29:
				mapFormularioTres.add(
						Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(posicao)).concat("].")
								.concat(VariloidForm3.idCampos[i]),
						VariloidForm3.idCampos[i]);
				Data.listaFormularioTres.get(posicao).getListSucesso().add(false);
				break;
			case 37:
				mapFormularioTres.add(
						Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(posicao)).concat("].")
								.concat(VariloidForm3.idCampos[i]),
						VariloidForm3.idCampos[i]);
				Data.listaFormularioTres.get(posicao).getListSucesso().add(false);
				break;
			case 42:
				mapFormularioTres.add(
						Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(posicao)).concat("].")
								.concat(VariloidForm3.idCampos[i]),
						VariloidForm3.idCampos[i]);
				Data.listaFormularioTres.get(posicao).getListSucesso().add(false);
				break;
			case 59:
				mapFormularioTres.add(
						Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(posicao)).concat("].")
								.concat(VariloidForm3.idCampos[i]),
						VariloidForm3.idCampos[i]);
				Data.listaFormularioTres.get(posicao).getListSucesso().add(false);
				break;
			default:

				for (Field f : classe.getDeclaredFields()) {
					f.setAccessible(true);

					if("id".equals(f.getName())){
						f.setAccessible(true);
						Data.mapService.add(
								Data.FORM3_KEY.concat("[").concat(String.valueOf(posicao)).concat("].").concat("id"), String.valueOf(f.get(object)));
					}
					if("cartaoVacina".equals(f.getName())){
						f.setAccessible(true);
						Class<?> classeCartao = object.getCartaoVacina().getClass();
						for (Field field : classeCartao.getDeclaredFields()) {
							field.setAccessible(true);
							if("id".equals(field.getName())){
								Data.mapService.add(Data.FORM3_KEY.concat("[").concat(String.valueOf(posicao)).concat("].").concat("cartaoVacina.id"), String.valueOf(field.get(object.getCartaoVacina())));
							}
						}
					}
					
					if (VariloidForm3.idCampos[i].equals(f.getName())) {

						if (f.get(object) != null) {
							mapFormularioTres.add(
									Data.FORM3_KEY.concat("[")
											.concat(String.valueOf(posicao))
											.concat("].")
											.concat(VariloidForm3.idCampos[i]),
									f.get(object));
							Data.listaFormularioTres.get(posicao).getListSucesso().add(true);
						} else {
							mapFormularioTres.add(
									Data.FORM3_KEY.concat("[")
											.concat(String.valueOf(posicao))
											.concat("].")
											.concat(VariloidForm3.idCampos[i]),
									"");
							Data.listaFormularioTres.get(posicao).getListSucesso().add(false);
						}
						f.setAccessible(false);
					}
				}
				break;
			}
			Data.listaFormularioTres.get(posicao).getListInativar().add(false);
		}
//		Log.v("Comparar size", "getListSucesso: "+Data.listaFormularioTres.get(posicao).getListSucesso().size());
//		Log.v("Comparar size", "getListInativar: "+Data.listaFormularioTres.get(posicao).getListInativar().size());
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
			Data.mapService = new LinkedMultiValueMap<String, Object>();
			
			Data.mapService.add("id", String.valueOf(adapter.getEntrevista(params[0]).getId()));
			Data.mapService.add(Variloid.ID_ENTREVISTADOR, String.valueOf(adapter.getEntrevista(params[0]).getEntrevistador().getId()));
			Data.mapService.add("faixaEtaria", adapter.getEntrevista(params[0]).getFaixaEtaria());
			
			Data.formularioDois = new FormularioDois();
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

			Data.listaFormularioTres = new ArrayList<FormularioTres>();
			Data.listaFormularioTres = adapter.getEntrevista(params[0])
					.getFormulariosTres();
			
			for (int i = 0; i < Data.listaFormularioTres.size(); i++) {

				FormularioTres formularioTres = Data.listaFormularioTres.get(i);
				
				try {
					infoObjetoForm3(formularioTres, i);
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
