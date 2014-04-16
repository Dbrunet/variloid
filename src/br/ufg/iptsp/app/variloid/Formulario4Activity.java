package br.ufg.iptsp.app.variloid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
import br.ufg.iptsp.app.variloid.adapter.MyAdapterForm4;
import br.ufg.iptsp.app.variloid.servico.Servico;
import br.ufg.iptsp.app.variloid.servico.ServicoConexao;
import br.ufg.iptsp.app.variloid.util.Mask;

public class Formulario4Activity extends BaseActivity implements OnItemClickListener, LocationListener{
	
	private LayoutInflater layoutInflater;
	private MyAdapterForm4 myAdapter;
	private Uri imageUri;
	private Button buttonTirarFotoCartao;
	
	private double latitude;
	private double longitude;
	LocationManager locationManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_formulario);
		ServicoConexao.verificaTipoConexao(this);
		
		getSupportActionBar().setTitle(getString(R.string.formulario_quatro));
		layoutInflater = getLayoutInflater();
		
		List<Integer> list = new ArrayList<Integer>();
		for (Integer integer : VariloidForm4.campos) {
			list.add(integer);
		}
		
		View layoutFooter = layoutInflater.inflate(R.layout.button_layout, null);
		buttonTirarFotoCartao = (Button) layoutFooter.findViewById(R.id.button_tirar_foto_cartao);
		buttonTirarFotoCartao.setVisibility(View.VISIBLE);
		
		SharedPreferences pref = getSharedPreferences(Variloid.PREFERENCIAS, MODE_PRIVATE);
		String caminhoFoto = pref.getString(Variloid.FORM_FOTO_CARTAO_VACINA4, "");
		if(caminhoFoto.equalsIgnoreCase("")){
			buttonTirarFotoCartao.setText(getString(R.string.tirar_foto_cartao));
		}else{
			buttonTirarFotoCartao.setText(getString(R.string.tirar_foto_cartao) + "(Imagem Carregada)");
		}

		buttonTirarFotoCartao.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog(0);
			}
		});
		
		Button buttonFinalizar = (Button) layoutFooter.findViewById(R.id.button_avancar_finalizar);
		buttonFinalizar.setVisibility(View.VISIBLE);
		buttonFinalizar.setText(getString(R.string.finalizar_caso_controle));
		buttonFinalizar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int count=0;
				if(ServicoConexao.isConectadoRede()){
					final SharedPreferences pref = getSharedPreferences(Variloid.PREFERENCIAS, MODE_PRIVATE);
					
					for (int i = 0; i < VariloidForm4.idCampos.length; i++) {
						switch (i) {
						case 32:
							break;
						case 42:
							break;
						case 47:
							break;
						case 52:
							break;
						case 82:
							break;
						case 87:
							break;
						default:
							if(TextUtils.isEmpty(pref.getString(VariloidForm4.FORM4_CAMPO + i, ""))){
								count++;
							}
							break;
						}
					}
					
					final AlertDialog.Builder alert = new AlertDialog.Builder(Formulario4Activity.this);
					alert.setTitle(getString(R.string.formulario_alerta));
					alert.setMessage(getString(R.string.formulario_campos_nao_preenchidos) + " " + count + " " + getString(R.string.formulario_deseja_continuar));
					alert.setPositiveButton("Sim",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

							SharedPreferences.Editor editor = pref.edit();
							editor.putString(VariloidForm4.FORM4_CAMPO + "latitude", String.valueOf(latitude));
							editor.putString(VariloidForm4.FORM4_CAMPO + "longitude", String.valueOf(longitude));
							editor.commit();
							
							new FormAsyntask(Formulario4Activity.this).execute();	
							dialog.cancel();
						}
					});

					alert.setNegativeButton("Não",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.cancel();
						}
					});
					alert.show();
				}else{
					Toast.makeText(Formulario4Activity.this, R.string.advertencia_conexao_off,
							 Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		ListView listView = (ListView) findViewById(R.id.listView);
		listView.addFooterView(layoutFooter);
		myAdapter = new MyAdapterForm4(Formulario4Activity.this, R.layout.adapter_formulario, list);
		listView.setAdapter(myAdapter);
		listView.setOnItemClickListener(this);
	}
	
	private class FormAsyntask extends AsyncTask<Void, Void, Void>{

		private Servico servico;
		private ProgressDialog pd;
		private Context context;
//		private boolean sucesso;
		private String nid;
		
		public FormAsyntask(Context context){
			this.context = context;
		}
		
		protected void onPreExecute() {
			servico = new Servico(context);
			pd = new ProgressDialog(context);
			pd.setMessage("Aguarde. Enviando...");
			pd.show();
			super.onPreExecute();
		};
		
		@Override
		protected Void doInBackground(Void... params) {
			nid = servico.enviarForm4();
			return null;
		}
		
		protected void onPostExecute(Void result) {
			pd.dismiss();
			if(nid!=null){
				Intent i = new Intent(context, NIDActivity.class);
				i.putExtra("nid", nid);
				i.putExtra("ispendente", false);
				context.startActivity(i);
			}else{
				Toast.makeText(context, "Problema de comunicação com o servidor!", Toast.LENGTH_LONG).show();
			}
			
			super.onPostExecute(result);
		};
		
	}
	
	@Override
	protected void onStart() {

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		boolean enabledGPS = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!enabledGPS) {
			Intent intent = new Intent(
					Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
			
			Toast.makeText(this, "GPS sinal não encontrado!",
					Toast.LENGTH_LONG).show();
			Toast.makeText(this, "Ative o GPS do dispositivo!",
					Toast.LENGTH_LONG).show();

		} else {

			Criteria criteria = new Criteria();
			// Getting the name of the best provider
			String provider = locationManager.getBestProvider(criteria,
					true);
			// Getting Current Location
			Location location = locationManager
					.getLastKnownLocation(provider);
			if (location != null) {
				onLocationChanged(location);
			}
			locationManager.requestLocationUpdates(provider, 20000, 0,
					Formulario4Activity.this);
		}		
		super.onStart();
	}


	@Override
	public void onLocationChanged(Location location) {
	latitude = location.getLatitude();
	longitude = location.getLongitude();
	}

	@Override
	public void onProviderDisabled(String provider) {}
	
	@Override
	public void onProviderEnabled(String provider) {}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	protected Dialog onCreateDialog(int dialogId) {
		try {
			Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
			File photoUsuario = new File(
					Environment.getExternalStorageDirectory(), "PicCartaoVacina.jpg");
			i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoUsuario));
			i.putExtra("data", photoUsuario);
			imageUri = Uri.fromFile(photoUsuario);
			startActivityForResult(i,1);

		} catch (Exception e) {
			Log.w("IMAGE_CAPTURE", "Falha ao acessar o arquivo onde a imagem seria salva.", e);
		}
		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				try {
					SharedPreferences pref = getSharedPreferences(Variloid.PREFERENCIAS, MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();
					editor.putString(Variloid.FORM_FOTO_CARTAO_VACINA4, getPathFromURI(imageUri));
					editor.commit();
					buttonTirarFotoCartao.setText(getString(R.string.tirar_foto_cartao) + "(Imagem Carregada)");
				} catch (Exception e) {
					Toast.makeText(this, "Erro ao carregar Imagem. Tente Novamente!", Toast.LENGTH_LONG).show();
				}

			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@SuppressWarnings("deprecation")
	public String getPathFromURI(Uri contentUri) {
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(contentUri, proj, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (Exception e) {
			return contentUri.getPath();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, final View view, final int arg2, long arg3) {
		
		SharedPreferences pref = getSharedPreferences(Variloid.PREFERENCIAS, 0);
		String campo = pref.getString(VariloidForm4.FORM4_CAMPO + arg2, "");
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(Formulario4Activity.this);
		
		switch (arg2) {
		case VariloidForm4.NOME:
			final EditText inputNome = new EditText(Formulario4Activity.this);
			
			if(!TextUtils.isEmpty(campo))inputNome.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_nome));
			alert.setView(inputNome);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputNome.getText().toString())){
						setPreferences(arg2, inputNome.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.SEXO:
			
			View layout = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton1 = (RadioButton) layout.findViewById(R.id.radio1);
			radioButton1.setText(getString(R.string.formulario4_opcao_masculino));
			final RadioButton radioButton2 = (RadioButton) layout.findViewById(R.id.radio2);
			radioButton2.setText(getString(R.string.formulario4_opcao_feminino));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_masculino))){
				radioButton1.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_feminino))){
				radioButton2.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_sexo));
			alert.setView(layout);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton1.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_masculino), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton2.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_feminino), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.DATA_NASCIMENTO:
			final EditText dataNascimento = new EditText(Formulario4Activity.this);
			dataNascimento.setInputType(InputType.TYPE_CLASS_DATETIME);
			dataNascimento.addTextChangedListener(Mask.insert(
					"##/##/####", dataNascimento));
			
			if(!TextUtils.isEmpty(campo)){
				String replaceCampo = campo.replaceAll("/", "");
				dataNascimento.setText(replaceCampo);	
			}
			
			alert.setTitle(getString(R.string.formulario4_data));
			alert.setView(dataNascimento);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(dataNascimento.getText().toString())){
						setPreferences(arg2, dataNascimento.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.LOCAL:
			final EditText local = new EditText(Formulario4Activity.this);
			int maxLengthLocal = 90;    
			local.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthLocal)});
			
			if(!TextUtils.isEmpty(campo))local.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_local));
			alert.setView(local);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(local.getText().toString())){
						setPreferences(arg2, local.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.RESIDENCIA:
			
			View layout7 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton16 = (RadioButton) layout7.findViewById(R.id.radio1);
			radioButton16.setText(getString(R.string.formulario4_opcao_urbana));
			final RadioButton radioButton17 = (RadioButton) layout7.findViewById(R.id.radio2);
			radioButton17.setText(getString(R.string.formulario4_opcao_nao_urbana)); 
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_urbana))){
				radioButton16.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_urbana))){
				radioButton17.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_residencia));
			alert.setView(layout7);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
					if(radioButton16.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_urbana), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton17.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_urbana), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.TIPO_LOGRADOURO:
			
			View layout8 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			
			final EditText outros3 = (EditText) layout8.findViewById(R.id.edittext_botton);
			int maxLength3 = 10;    
			outros3.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength3)});
			
			final RadioButton radioButton18 = (RadioButton) layout8.findViewById(R.id.radio1);
			radioButton18.setText(getString(R.string.formulario4_opcao_avenida));
			final RadioButton radioButton19 = (RadioButton) layout8.findViewById(R.id.radio2);
			radioButton19.setText(getString(R.string.formulario4_opcao_rua));
			final RadioButton radioButton20 = (RadioButton) layout8.findViewById(R.id.radio3);
			radioButton20.setVisibility(View.VISIBLE);
			radioButton20.setText(getString(R.string.formulario4_opcao_alameda));
			final RadioButton radioButton21 = (RadioButton) layout8.findViewById(R.id.radio4);
			radioButton21.setVisibility(View.VISIBLE);
			radioButton21.setText(getString(R.string.formulario4_opcao_rodovia));
			final RadioButton radioButton22 = (RadioButton) layout8.findViewById(R.id.radio5);
			radioButton22.setVisibility(View.VISIBLE);
			radioButton22.setText(getString(R.string.formulario4_opcao_travessa));
			final RadioButton radioButton23 = (RadioButton) layout8.findViewById(R.id.radio6);
			radioButton23.setVisibility(View.VISIBLE);
			radioButton23.setText(getString(R.string.formulario4_opcao_outro));
			radioButton23.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
						outros3.setVisibility(View.VISIBLE);
					}else{
						outros3.setVisibility(View.GONE);
					}
				}
			});
			
			if(!campo.equalsIgnoreCase("")){
				if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_avenida))){
					radioButton18.setChecked(true);
				}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_rua))){
					radioButton19.setChecked(true);
				}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_alameda))){
					radioButton20.setChecked(true);
				}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_rodovia))){
					radioButton21.setChecked(true);
				}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_travessa))){
					radioButton22.setChecked(true);
				}else if(campo.split(",")[0].equalsIgnoreCase(getString(R.string.formulario4_opcao_outro))){
					radioButton23.setChecked(true);
					outros3.setText(campo.split(",")[1]);
				}
			}
			
			alert.setTitle(getString(R.string.formulario4_tipo_logradouro));
			alert.setView(layout8);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					if(radioButton18.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_avenida), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton19.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_rua) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton20.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_alameda) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton21.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_rodovia) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton22.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_travessa) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton23.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_outro) + "," + outros3.getText().toString() , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			
			break;
		case VariloidForm4.NOME_LOGRADOURO:
			final EditText inputNomeLogradouro = new EditText(Formulario4Activity.this);
			int maxLength4 = 90;    
			inputNomeLogradouro.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength4)});
			
			if(!TextUtils.isEmpty(campo))inputNomeLogradouro.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_nome_logradouro));
			alert.setView(inputNomeLogradouro);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputNomeLogradouro.getText().toString())){
						setPreferences(arg2, inputNomeLogradouro.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.QUADRA:
			final EditText inputQuadra = new EditText(Formulario4Activity.this);
			int maxLength5 = 10;    
			inputQuadra.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength5)});
			
			if(!TextUtils.isEmpty(campo))inputQuadra.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_qudra));
			alert.setView(inputQuadra);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputQuadra.getText().toString())){
						setPreferences(arg2, inputQuadra.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.LOTE:
			final EditText inputLote = new EditText(Formulario4Activity.this);
			int maxLength6 = 8;    
			inputLote.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength6)});
			
			if(!TextUtils.isEmpty(campo))inputLote.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_lote));
			alert.setView(inputLote);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputLote.getText().toString())){
						setPreferences(arg2, inputLote.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.NUMERO:
			final EditText inputNumero = new EditText(Formulario4Activity.this);
			int maxLength7 = 8;    
			inputNumero.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength7)});
			inputNumero.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))inputNumero.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_numero));
			alert.setView(inputNumero);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputNumero.getText().toString())){
						setPreferences(arg2, inputNumero.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.COMPLEMENTO:
			final EditText inputComplemento = new EditText(Formulario4Activity.this);
			int maxLength8 = 20;    
			inputComplemento.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength8)});
			
			if(!TextUtils.isEmpty(campo))inputComplemento.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_complemento));
			alert.setView(inputComplemento);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputComplemento.getText().toString())){
						setPreferences(arg2, inputComplemento.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.BAIRRO:
			final EditText inputBairro = new EditText(Formulario4Activity.this);
			int maxLength9 = 50;    
			inputBairro.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength9)});
			
			if(!TextUtils.isEmpty(campo))inputBairro.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_bairro));
			alert.setView(inputBairro);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputBairro.getText().toString())){
						setPreferences(arg2, inputBairro.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.TCLE:
			View layout10 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton26 = (RadioButton) layout10.findViewById(R.id.radio1);
			radioButton26.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton27 = (RadioButton) layout10.findViewById(R.id.radio2);
			radioButton27.setText(getString(R.string.formulario4_opcao_nao));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton26.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton27.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_tcle));
			alert.setView(layout10);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton26.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						
						for (int i = VariloidForm4.NOME_MAE; i < VariloidForm4.campos.length; i++) {
							setPreferences(i, "", false,  false);
						}
						for (int i = VariloidForm4.NOME_MAE; i < VariloidForm4.campos.length; i++) {
							myAdapter.updateItens(i, VariloidForm4.campos[i]);
						}
						myAdapter.notifyDataSetChanged();
						
					}else if(radioButton27.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						
						for (int i = VariloidForm4.NOME_MAE; i < VariloidForm4.campos.length; i++) {
							setPreferences(i, "", true,  false);
						}
						for (int i = VariloidForm4.NOME_MAE; i < VariloidForm4.campos.length; i++) {
							myAdapter.updateItens(i, VariloidForm4.campos[i]);
						}
						myAdapter.notifyDataSetChanged();
						
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.NOME_MAE:
			final EditText inputNomeMae = new EditText(Formulario4Activity.this);
			int maxLength10 = 90;    
			inputNomeMae.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength10)});
			
			if(!TextUtils.isEmpty(campo))inputNomeMae.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_nome_mae));
			alert.setView(inputNomeMae);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputNomeMae.getText().toString())){
						setPreferences(arg2, inputNomeMae.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.IDADE_MAE:
			final EditText inputIdadeMae = new EditText(Formulario4Activity.this);
			int maxLength11 = 2;    
			inputIdadeMae.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength11)});
			inputIdadeMae.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))inputIdadeMae.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_idade_mae));
			alert.setView(inputIdadeMae);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputIdadeMae.getText().toString())){
						setPreferences(arg2, inputIdadeMae.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.NOME_PAI:
			final EditText inputNomePai = new EditText(Formulario4Activity.this);
			int maxLength12 = 90;    
			inputNomePai.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength12)});
			
			if(!TextUtils.isEmpty(campo))inputNomePai.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_nome_pai));
			alert.setView(inputNomePai);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputNomePai.getText().toString())){
						setPreferences(arg2, inputNomePai.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.TELEFONE:
			final EditText inputTelFixo = new EditText(Formulario4Activity.this);
			int maxLength13 = 20;    
			inputTelFixo.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength13)});
			inputTelFixo.setInputType(InputType.TYPE_CLASS_PHONE);
			
			if(!TextUtils.isEmpty(campo))inputTelFixo.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_tel_fixo));
			alert.setView(inputTelFixo);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputTelFixo.getText().toString())){
						setPreferences(arg2, inputTelFixo.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.CELULAR1:
			final EditText NumeroCelular1 = new EditText(Formulario4Activity.this);
			int maxLengthCel1 = 20;    
			NumeroCelular1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthCel1)});
			NumeroCelular1.setInputType(InputType.TYPE_CLASS_PHONE);
			
			if(!TextUtils.isEmpty(campo))NumeroCelular1.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_tel_celular1));
			alert.setView(NumeroCelular1);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(NumeroCelular1.getText().toString())){
						setPreferences(arg2, NumeroCelular1.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.OPER_CELULAR1:
			View layout11 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			
			final RadioButton radioButton28 = (RadioButton) layout11.findViewById(R.id.radio1);
			radioButton28.setText(getString(R.string.formulario4_operadoras_tim));
			final RadioButton radioButton29 = (RadioButton) layout11.findViewById(R.id.radio2);
			radioButton29.setText(getString(R.string.formulario4_operadoras_claro));
			final RadioButton radioButton30 = (RadioButton) layout11.findViewById(R.id.radio3);
			radioButton30.setVisibility(View.VISIBLE);
			radioButton30.setText(getString(R.string.formulario4_operadoras_oi));
			final RadioButton radioButton31 = (RadioButton) layout11.findViewById(R.id.radio4);
			radioButton31.setVisibility(View.VISIBLE);
			radioButton31.setText(getString(R.string.formulario4_operadoras_vivo));
			
			if(campo.indexOf(getString(R.string.formulario4_operadoras_tim))!=-1){
					radioButton28.setChecked(true);
			}else if(campo.indexOf(getString(R.string.formulario4_operadoras_claro))!=-1){
				radioButton29.setChecked(true);
			}else if(campo.indexOf(getString(R.string.formulario4_operadoras_oi))!=-1){
				radioButton30.setChecked(true);
			}else if(campo.indexOf(getString(R.string.formulario4_operadoras_vivo))!=-1){
				radioButton31.setChecked(true);
			}
					
			alert.setTitle(getString(R.string.formulario4_operadora1));
			alert.setView(layout11);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int whichButton) {
					
					if(radioButton28.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_operadoras_tim) , false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton29.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_operadoras_claro) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton30.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_operadoras_oi) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton31.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_operadoras_vivo) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.CELULAR2:
			final EditText NumeroCelular2 = new EditText(Formulario4Activity.this);
			int maxLengthCel2 = 20;    
			NumeroCelular2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthCel2)});
			NumeroCelular2.setInputType(InputType.TYPE_CLASS_PHONE);
			
			if(!TextUtils.isEmpty(campo))NumeroCelular2.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_tel_celular2));
			alert.setView(NumeroCelular2);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(NumeroCelular2.getText().toString())){
						setPreferences(arg2, NumeroCelular2.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.OPER_CELULAR2:
			View layout12 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			
			final RadioButton radioButton32 = (RadioButton) layout12.findViewById(R.id.radio1);
			radioButton32.setText(getString(R.string.formulario4_operadoras_tim));
			final RadioButton radioButton33 = (RadioButton) layout12.findViewById(R.id.radio2);
			radioButton33.setText(getString(R.string.formulario4_operadoras_claro));
			final RadioButton radioButton34 = (RadioButton) layout12.findViewById(R.id.radio3);
			radioButton34.setVisibility(View.VISIBLE);
			radioButton34.setText(getString(R.string.formulario4_operadoras_oi));
			final RadioButton radioButton35 = (RadioButton) layout12.findViewById(R.id.radio4);
			radioButton35.setVisibility(View.VISIBLE);
			radioButton35.setText(getString(R.string.formulario4_operadoras_vivo));
			
			if(campo.indexOf(getString(R.string.formulario4_operadoras_tim))!=-1){
				radioButton32.setChecked(true);
			}else if(campo.indexOf(getString(R.string.formulario4_operadoras_claro))!=-1){
				radioButton33.setChecked(true);
			}else if(campo.indexOf(getString(R.string.formulario4_operadoras_oi))!=-1){
				radioButton34.setChecked(true);
			}else if(campo.indexOf(getString(R.string.formulario4_operadoras_vivo))!=-1){
				radioButton35.setChecked(true);
			}
					
			alert.setTitle(getString(R.string.formulario4_operadora2));
			alert.setView(layout12);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
					if(radioButton32.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_operadoras_tim) , false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton33.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_operadoras_claro) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton34.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_operadoras_oi) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton35.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_operadoras_vivo) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.COBERTURA_PSF:
			View layout13 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton36 = (RadioButton) layout13.findViewById(R.id.radio1);
			radioButton36.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton37 = (RadioButton) layout13.findViewById(R.id.radio2);
			radioButton37.setText(getString(R.string.formulario4_opcao_nao));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton36.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton37.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_cobertura_psf));
			alert.setView(layout13);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton36.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						
					}else if(radioButton37.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;

		case VariloidForm4.PESONASCER:
			final EditText pesoNascer = new EditText(Formulario4Activity.this);
			int maxLength16 = 4;    
			pesoNascer.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength16)});
			pesoNascer.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))pesoNascer.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_peso_nascer));
			alert.setView(pesoNascer);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(pesoNascer.getText().toString())){
						setPreferences(arg2, pesoNascer.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.IDADEGESTACIONAL:
			final EditText idadeGestacional = new EditText(Formulario4Activity.this);
			int maxLength17 = 3;    
			idadeGestacional.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength17)});
			idadeGestacional.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))idadeGestacional.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_idade_gestacional));
			alert.setView(idadeGestacional);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(idadeGestacional.getText().toString())){
						setPreferences(arg2, idadeGestacional.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.RACA:
			View layout26 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton76 = (RadioButton) layout26.findViewById(R.id.radio1);
			radioButton76.setText(getString(R.string.formulario4_opcao_branca));
			final RadioButton radioButton77 = (RadioButton) layout26.findViewById(R.id.radio2);
			radioButton77.setText(getString(R.string.formulario4_opcao_parda));
			final RadioButton radioButton78 = (RadioButton) layout26.findViewById(R.id.radio3);
			radioButton78.setText(getString(R.string.formulario4_opcao_negra));
			radioButton78.setVisibility(View.VISIBLE);
			final RadioButton radioButton79 = (RadioButton) layout26.findViewById(R.id.radio4);
			radioButton79.setText(getString(R.string.formulario4_opcao_asiatica));
			radioButton79.setVisibility(View.VISIBLE);
			final RadioButton radioButton80 = (RadioButton) layout26.findViewById(R.id.radio5);
			radioButton80.setText(getString(R.string.formulario4_opcao_indigena));
			radioButton80.setVisibility(View.VISIBLE);
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_branca))){
				radioButton76.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_parda))){
				radioButton77.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_negra))){
				radioButton78.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_asiatica))){
				radioButton79.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_indigena))){
				radioButton80.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_raca));
			alert.setView(layout26);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton76.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_branca), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton77.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_parda), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton78.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_negra), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton79.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_asiatica), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton80.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_indigena), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.ES_CMAE:
			View layout37 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton108 = (RadioButton) layout37.findViewById(R.id.radio1);
			radioButton108.setText(getString(R.string.formulario4_opcao_nao_alfabetizada));
			final RadioButton radioButton109 = (RadioButton) layout37.findViewById(R.id.radio2);
			radioButton109.setText(getString(R.string.formulario4_opcao_ensino_fundamental));
			final RadioButton radioButton110 = (RadioButton) layout37.findViewById(R.id.radio3);
			radioButton110.setVisibility(View.VISIBLE);
			radioButton110.setText(getString(R.string.formulario4_opcao_ensino_medio));
			final RadioButton radioButton111 = (RadioButton) layout37.findViewById(R.id.radio4);
			radioButton111.setVisibility(View.VISIBLE);
			radioButton111.setText(getString(R.string.formulario4_opcao_ensino_superior));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_alfabetizada))){
				radioButton108.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_ensino_fundamental))){
				radioButton109.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_ensino_medio))){
				radioButton110.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_ensino_superior))){
				radioButton111.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_esc_mae));
			alert.setView(layout37);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton108.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_alfabetizada), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton109.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_ensino_fundamental), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton110.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_ensino_medio), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton111.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_ensino_superior), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.NCCAS_CASA5:
			final EditText numeroCriancas1 = new EditText(Formulario4Activity.this);
			int maxLength19 = 2;    
			numeroCriancas1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength19)});
			numeroCriancas1.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))numeroCriancas1.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_nc_cas_casa5));
			alert.setView(numeroCriancas1);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(numeroCriancas1.getText().toString())){
						setPreferences(arg2, numeroCriancas1.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.NCCAS_CASA10:
			final EditText numeroCriancas2 = new EditText(Formulario4Activity.this);
			int maxLength20 = 2;    
			numeroCriancas2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength20)});
			numeroCriancas2.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))numeroCriancas2.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_nc_cas_casa10));
			alert.setView(numeroCriancas2);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(numeroCriancas2.getText().toString())){
						setPreferences(arg2, numeroCriancas2.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.QT_DE_PESSOAS_CASA:
			final EditText qtdPessoasCasa = new EditText(Formulario4Activity.this);
			int maxLength21 = 2;    
			qtdPessoasCasa.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength21)});
			qtdPessoasCasa.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))qtdPessoasCasa.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_qtd_pessoas_casa));
			alert.setView(qtdPessoasCasa);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(qtdPessoasCasa.getText().toString())){
						setPreferences(arg2, qtdPessoasCasa.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.QTD_EPESSOAS_QTO:
			final EditText qtdPessoasQuarto = new EditText(Formulario4Activity.this);
			int maxLength22 = 2;    
			qtdPessoasQuarto.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength22)});
			qtdPessoasQuarto.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))qtdPessoasQuarto.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_qtd_pessoas_quarto));
			alert.setView(qtdPessoasQuarto);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(qtdPessoasQuarto.getText().toString())){
						setPreferences(arg2, qtdPessoasQuarto.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.VACPCV:
			View layout27 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton81 = (RadioButton) layout27.findViewById(R.id.radio1);
			radioButton81.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton82 = (RadioButton) layout27.findViewById(R.id.radio2);
			radioButton82.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton83 = (RadioButton) layout27.findViewById(R.id.radio3);
			radioButton83.setVisibility(View.VISIBLE);
			radioButton83.setText(getString(R.string.formulario4_opcao_nao_sabe_e_nao_tem_cartao));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton81.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton82.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe_e_nao_tem_cartao))){
				radioButton83.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_vac_pcv));
			alert.setView(layout27);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton81.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton82.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton83.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe_e_nao_tem_cartao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.MOTIVO_NAO_VACINACAO:
			View layout28 = layoutInflater.inflate(R.layout.checkbox_formulario, null);
			final EditText editText = (EditText) layout28.findViewById(R.id.edit_text);
			editText.setVisibility(View.GONE);
			int maxLengthEd = 90;    
			editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthEd)});
			
			final CheckBox checkbox1 = (CheckBox) layout28.findViewById(R.id.checkBox1);
			checkbox1.setText(getString(R.string.formulario4_opcao_motivo_nao_vacinacao1));
			final CheckBox checkbox2 = (CheckBox) layout28.findViewById(R.id.checkBox2);
			checkbox2.setText(getString(R.string.formulario4_opcao_motivo_nao_vacinacao2));
			final CheckBox checkbox3 = (CheckBox) layout28.findViewById(R.id.checkBox3);
			checkbox3.setText(getString(R.string.formulario4_opcao_motivo_nao_vacinacao3));
			final CheckBox checkbox4 = (CheckBox) layout28.findViewById(R.id.checkBox4);
			checkbox4.setText(getString(R.string.formulario4_opcao_motivo_nao_vacinacao4));
			final CheckBox checkbox5 = (CheckBox) layout28.findViewById(R.id.checkBox5);
			checkbox5.setText(getString(R.string.formulario4_opcao_motivo_nao_vacinacao5));
			final CheckBox checkbox6 = (CheckBox) layout28.findViewById(R.id.checkBox6);
			checkbox6.setText(getString(R.string.formulario4_opcao_motivo_nao_vacinacao6));
			final CheckBox checkbox7 = (CheckBox) layout28.findViewById(R.id.checkBox7);
			checkbox7.setText(getString(R.string.formulario4_opcao_motivo_nao_vacinacao7));
			final CheckBox checkbox8 = (CheckBox) layout28.findViewById(R.id.checkBox8);
			checkbox8.setText(getString(R.string.formulario4_opcao_motivo_nao_vacinacao8));
			final CheckBox checkbox9 = (CheckBox) layout28.findViewById(R.id.checkBox9);
			checkbox9.setText(getString(R.string.formulario4_opcao_motivo_nao_vacinacao9));
			final CheckBox checkbox10 = (CheckBox) layout28.findViewById(R.id.checkBox10);
			checkbox10.setText(getString(R.string.formulario4_opcao_motivo_nao_vacinacao10));
			final CheckBox checkbox11 = (CheckBox) layout28.findViewById(R.id.checkBox11);
			checkbox11.setText(getString(R.string.formulario4_opcao_motivo_nao_vacinacao11));
			final CheckBox checkbox12 = (CheckBox) layout28.findViewById(R.id.checkBox12);
			checkbox12.setText(getString(R.string.formulario4_opcao_motivo_nao_vacinacao12));
			final CheckBox checkbox13 = (CheckBox) layout28.findViewById(R.id.checkBox13);
			checkbox13.setText(getString(R.string.formulario4_opcao_motivo_nao_vacinacao13));
			final CheckBox checkbox14 = (CheckBox) layout28.findViewById(R.id.checkBox14);
			checkbox14.setText(getString(R.string.formulario4_opcao_outros));
			checkbox14.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
						editText.setVisibility(View.VISIBLE);
					}else{
						editText.setVisibility(View.GONE);
						editText.setText("");
						editText.setHint(getString(R.string.formulario4_opcao_outros));
					}
				}
			});
			
			final CheckBox checkbox15 = (CheckBox) layout28.findViewById(R.id.checkBox15);
			checkbox15.setVisibility(View.GONE);
			final CheckBox checkbox16 = (CheckBox) layout28.findViewById(R.id.checkBox16);
			checkbox16.setVisibility(View.GONE);
			
			if(!campo.equalsIgnoreCase("")){			
				if(campo.indexOf(getString(R.string.formulario4_opcao_motivo_nao_vacinacao1))!=-1){
					checkbox1.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_motivo_nao_vacinacao2))!=-1){
					checkbox2.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_motivo_nao_vacinacao3))!=-1){
					checkbox3.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_motivo_nao_vacinacao4))!=-1){
					checkbox4.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_motivo_nao_vacinacao5))!=-1){
					checkbox5.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_motivo_nao_vacinacao6))!=-1){
					checkbox6.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_motivo_nao_vacinacao7))!=-1){
					checkbox7.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_motivo_nao_vacinacao8))!=-1){
					checkbox8.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_motivo_nao_vacinacao9))!=-1){
					checkbox9.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_motivo_nao_vacinacao10))!=-1){
					checkbox10.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_motivo_nao_vacinacao11))!=-1){
					checkbox11.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_motivo_nao_vacinacao12))!=-1){
					checkbox12.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_motivo_nao_vacinacao13))!=-1){
					checkbox13.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_outro_check))!=-1){
					checkbox14.setChecked(true);
					int posicao = campo.indexOf(getString(R.string.formulario4_opcao_outro_check));
					editText.setText(campo.substring(posicao + getString(R.string.formulario4_opcao_outro_check).length(), campo.length()));					
				}
			}
			
			alert.setTitle(getString(R.string.formulario4_motivo_nao_vacinacao));
			alert.setView(layout28);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				List<String> lisStrings = new ArrayList<String>();
				String valor;
				public void onClick(DialogInterface dialog, int whichButton) {
					if(checkbox1.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_motivo_nao_vacinacao1));
					}
					if(checkbox2.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_motivo_nao_vacinacao2));
					}
					if(checkbox3.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_motivo_nao_vacinacao3));
					}
					if(checkbox4.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_motivo_nao_vacinacao4));
					}
					if(checkbox5.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_motivo_nao_vacinacao5));
					}
					if(checkbox6.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_motivo_nao_vacinacao6));
					}
					if(checkbox7.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_motivo_nao_vacinacao7));
					}
					if(checkbox8.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_motivo_nao_vacinacao8));
					}
					if(checkbox9.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_motivo_nao_vacinacao9));
					}
					if(checkbox10.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_motivo_nao_vacinacao10));
					}
					if(checkbox11.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_motivo_nao_vacinacao11));
					}
					if(checkbox12.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_motivo_nao_vacinacao12));
					}
					if(checkbox13.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_motivo_nao_vacinacao13));
					}
					if(checkbox14.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_outro_check) + editText.getText().toString());
					}
					
					if(!lisStrings.isEmpty()){
						for (String string : lisStrings) {
							if(valor==null){
								valor = string;
							}
							valor = valor +","+ string;
						}
						setPreferences(arg2, valor, false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});
	
			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.N_DOSES_PCV:
			final EditText inputNDosesVaric = new EditText(Formulario4Activity.this);
			int maxLength18 = 2;    
			inputNDosesVaric.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength18)});
			inputNDosesVaric.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))inputNDosesVaric.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_n_doses_pcv));
			alert.setView(inputNDosesVaric);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputNDosesVaric.getText().toString())){
						if(inputNDosesVaric.getText().toString().equalsIgnoreCase("0")){
							setPreferences(arg2, inputNDosesVaric.getText().toString(), false,  true);
							((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
							
							for (int i = VariloidForm4.VAC_PCV_DT_DOSE1; i < VariloidForm4.TITULO_VACINA_CONTRA_HIB; i++) {
								setPreferences(i, "", true,  false);
							}
							for (int i = VariloidForm4.VAC_PCV_DT_DOSE1; i < VariloidForm4.TITULO_VACINA_CONTRA_HIB; i++) {
								myAdapter.updateItens(i, VariloidForm4.campos[i]);
							}
							myAdapter.notifyDataSetChanged();	
						}else{
							setPreferences(arg2, inputNDosesVaric.getText().toString(), false,  true);
							((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
							
							for (int i = VariloidForm4.VAC_PCV_DT_DOSE1; i < VariloidForm4.TITULO_VACINA_CONTRA_HIB; i++) {
								setPreferences(i, "", false,  false);
							}
							for (int i = VariloidForm4.VAC_PCV_DT_DOSE1; i < VariloidForm4.TITULO_VACINA_CONTRA_HIB; i++) {
								myAdapter.updateItens(i, VariloidForm4.campos[i]);
							}
							myAdapter.notifyDataSetChanged();	
						}
						
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
						
						for (int i = VariloidForm4.VAC_PCV_DT_DOSE1; i < VariloidForm4.TITULO_VACINA_CONTRA_HIB; i++) {
							setPreferences(i, "", false,  false);
						}
						for (int i = VariloidForm4.VAC_PCV_DT_DOSE1; i < VariloidForm4.TITULO_VACINA_CONTRA_HIB; i++) {
							myAdapter.updateItens(i, VariloidForm4.campos[i]);
						}
						myAdapter.notifyDataSetChanged();
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.VAC_PCV_DT_DOSE1:
			final EditText vacVaricDtDoses1 = new EditText(Formulario4Activity.this);
			vacVaricDtDoses1.setInputType(InputType.TYPE_CLASS_DATETIME);
			vacVaricDtDoses1.addTextChangedListener(Mask.insert(
					"##/##/####", vacVaricDtDoses1));
			
			if(!TextUtils.isEmpty(campo)){
				String replaceCampo = campo.replaceAll("/", "");
				vacVaricDtDoses1.setText(replaceCampo);
			}
			
			alert.setTitle(getString(R.string.formulario4_vac_pcv_dt_dose1));
			alert.setView(vacVaricDtDoses1);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(vacVaricDtDoses1.getText().toString())){
						setPreferences(arg2, vacVaricDtDoses1.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.VAC_PCV_QUAL_DOSE1:
			View layout29 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton84 = (RadioButton) layout29.findViewById(R.id.radio1);
			radioButton84.setText(getString(R.string.formulario4_opcao_vacina_10_v));
			final RadioButton radioButton85 = (RadioButton) layout29.findViewById(R.id.radio2);
			radioButton85.setText(getString(R.string.formulario4_opcao_vacina_13_v));
			final RadioButton radioButton86 = (RadioButton) layout29.findViewById(R.id.radio3);
			radioButton86.setVisibility(View.VISIBLE);
			radioButton86.setText(getString(R.string.formulario4_opcao_vacina_7_v));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_vacina_10_v))){
				radioButton84.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_vacina_13_v))){
				radioButton85.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_vacina_7_v))){
				radioButton86.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_vac_pcv_qual_dose1));
			alert.setView(layout29);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton84.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_vacina_10_v), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton85.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_vacina_13_v), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton86.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_vacina_7_v), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.VAC_PCV_DT_DOSE2:
			final EditText vacVaricDtDoses2 = new EditText(Formulario4Activity.this);
			vacVaricDtDoses2.setInputType(InputType.TYPE_CLASS_DATETIME);
			vacVaricDtDoses2.addTextChangedListener(Mask.insert(
					"##/##/####", vacVaricDtDoses2));
			
			if(!TextUtils.isEmpty(campo)){
				String replaceCampo = campo.replaceAll("/", "");
				vacVaricDtDoses2.setText(replaceCampo);
			}
			
			alert.setTitle(getString(R.string.formulario4_vac_pcv_dt_dose2));
			alert.setView(vacVaricDtDoses2);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(vacVaricDtDoses2.getText().toString())){
						setPreferences(arg2, vacVaricDtDoses2.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.VAC_PCV_QUAL_DOSE2:
			View layout30 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton87 = (RadioButton) layout30.findViewById(R.id.radio1);
			radioButton87.setText(getString(R.string.formulario4_opcao_vacina_10_v));
			final RadioButton radioButton88 = (RadioButton) layout30.findViewById(R.id.radio2);
			radioButton88.setText(getString(R.string.formulario4_opcao_vacina_13_v));
			final RadioButton radioButton89 = (RadioButton) layout30.findViewById(R.id.radio3);
			radioButton89.setVisibility(View.VISIBLE);
			radioButton89.setText(getString(R.string.formulario4_opcao_vacina_7_v));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_vacina_10_v))){
				radioButton87.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_vacina_13_v))){
				radioButton88.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_vacina_7_v))){
				radioButton89.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_vac_pcv_qual_dose2));
			alert.setView(layout30);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton87.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_vacina_10_v), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton88.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_vacina_13_v), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton89.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_vacina_7_v), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.VAC_PCV_DT_DOSE3:
			final EditText vacVaricDtDoses3 = new EditText(Formulario4Activity.this);
			vacVaricDtDoses3.setInputType(InputType.TYPE_CLASS_DATETIME);
			vacVaricDtDoses3.addTextChangedListener(Mask.insert(
					"##/##/####", vacVaricDtDoses3));
			
			if(!TextUtils.isEmpty(campo)){
				String replaceCampo = campo.replaceAll("/", "");
				vacVaricDtDoses3.setText(replaceCampo);
			}
			
			alert.setTitle(getString(R.string.formulario4_vac_pcv_dt_dose3));
			alert.setView(vacVaricDtDoses3);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(vacVaricDtDoses3.getText().toString())){
						setPreferences(arg2, vacVaricDtDoses3.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.VAC_PCV_QUAL_DOSE3:
			View layout52 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton54 = (RadioButton) layout52.findViewById(R.id.radio1);
			radioButton54.setText(getString(R.string.formulario4_opcao_vacina_10_v));
			final RadioButton radioButton55 = (RadioButton) layout52.findViewById(R.id.radio2);
			radioButton55.setText(getString(R.string.formulario4_opcao_vacina_13_v));
			final RadioButton radioButton56 = (RadioButton) layout52.findViewById(R.id.radio3);
			radioButton56.setVisibility(View.VISIBLE);
			radioButton56.setText(getString(R.string.formulario4_opcao_vacina_7_v));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_vacina_10_v))){
				radioButton54.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_vacina_13_v))){
				radioButton55.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_vacina_7_v))){
				radioButton56.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_vac_pcv_qual_dose3));
			alert.setView(layout52);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton54.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_vacina_10_v), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton55.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_vacina_13_v), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton56.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_vacina_7_v), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.VAC_PCV_DT_DOSE4:
			final EditText vacVaricDtDoses4 = new EditText(Formulario4Activity.this);
			vacVaricDtDoses4.setInputType(InputType.TYPE_CLASS_DATETIME);
			vacVaricDtDoses4.addTextChangedListener(Mask.insert(
					"##/##/####", vacVaricDtDoses4));
			
			if(!TextUtils.isEmpty(campo)){
				String replaceCampo = campo.replaceAll("/", "");
				vacVaricDtDoses4.setText(replaceCampo);
			}
			
			alert.setTitle(getString(R.string.formulario4_vac_pcv_dt_dose4));
			alert.setView(vacVaricDtDoses4);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(vacVaricDtDoses4.getText().toString())){
						setPreferences(arg2, vacVaricDtDoses4.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.VAC_PCV_QUAL_DOSE4:
			View layout53 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton57 = (RadioButton) layout53.findViewById(R.id.radio1);
			radioButton57.setText(getString(R.string.formulario4_opcao_vacina_10_v));
			final RadioButton radioButton58 = (RadioButton) layout53.findViewById(R.id.radio2);
			radioButton58.setText(getString(R.string.formulario4_opcao_vacina_13_v));
			final RadioButton radioButton59 = (RadioButton) layout53.findViewById(R.id.radio3);
			radioButton59.setVisibility(View.VISIBLE);
			radioButton59.setText(getString(R.string.formulario4_opcao_vacina_7_v));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_vacina_10_v))){
				radioButton57.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_vacina_13_v))){
				radioButton58.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_vacina_7_v))){
				radioButton59.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_vac_pcv_qual_dose4));
			alert.setView(layout53);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton57.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_vacina_10_v), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton58.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_vacina_13_v), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton59.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_vacina_7_v), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.N_DOSES_HIB:
			final EditText inputNDosesHib = new EditText(Formulario4Activity.this);
			int maxLengthHib = 3;    
			inputNDosesHib.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthHib)});
			inputNDosesHib.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))inputNDosesHib.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_n_doses_hib));
			alert.setView(inputNDosesHib);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if (!TextUtils.isEmpty(inputNDosesHib.getText().toString())) {
						setPreferences(arg2, inputNDosesHib.getText().toString(), false, true);
						((ImageView) view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
	
					} else {
						setPreferences(arg2, "0", false, false);
						((ImageView) view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();

				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.VAC_HIB_DT_DOSE1:
			final EditText dataHib1 = new EditText(Formulario4Activity.this);
			dataHib1.setInputType(InputType.TYPE_CLASS_DATETIME);
			dataHib1.addTextChangedListener(Mask.insert(
					"##/##/####", dataHib1));
			
			if(!TextUtils.isEmpty(campo)){
				String replaceCampo = campo.replaceAll("/", "");
				dataHib1.setText(replaceCampo);
			}
			
			alert.setTitle(getString(R.string.formulario4_vac_hib_dt_dose1));
			alert.setView(dataHib1);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(dataHib1.getText().toString())){
						setPreferences(arg2, dataHib1.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.VAC_HIB_DT_DOSE2:
			final EditText dataHib2 = new EditText(Formulario4Activity.this);
			dataHib2.setInputType(InputType.TYPE_CLASS_DATETIME);
			dataHib2.addTextChangedListener(Mask.insert(
					"##/##/####", dataHib2));
			
			if(!TextUtils.isEmpty(campo)){
				String replaceCampo = campo.replaceAll("/", "");
				dataHib2.setText(replaceCampo);
			}
			
			alert.setTitle(getString(R.string.formulario4_vac_hib_dt_dose2));
			alert.setView(dataHib2);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(dataHib2.getText().toString())){
						setPreferences(arg2, dataHib2.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.VAC_HIB_DT_DOSE3:
			final EditText dataHib3 = new EditText(Formulario4Activity.this);
			dataHib3.setInputType(InputType.TYPE_CLASS_DATETIME);
			dataHib3.addTextChangedListener(Mask.insert(
					"##/##/####", dataHib3));
			
			if(!TextUtils.isEmpty(campo)){
				String replaceCampo = campo.replaceAll("/", "");
				dataHib3.setText(replaceCampo);
			}
			
			alert.setTitle(getString(R.string.formulario4_vac_hib_dt_dose3));
			alert.setView(dataHib3);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(dataHib3.getText().toString())){
						setPreferences(arg2, dataHib3.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.FREQCRECHE1:
			View layout31 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton90 = (RadioButton) layout31.findViewById(R.id.radio1);
			radioButton90.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton91 = (RadioButton) layout31.findViewById(R.id.radio2);
			radioButton91.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton92 = (RadioButton) layout31.findViewById(R.id.radio3);
			radioButton92.setVisibility(View.VISIBLE);
			radioButton92.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton90.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton91.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton92.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_freq_creche1));
			alert.setView(layout31);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton90.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						setPreferences(VariloidForm4.FREQCRECHE2, "", false,  false);
						myAdapter.notifyDataSetChanged();
						
					}else if(radioButton91.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
							setPreferences(VariloidForm4.FREQCRECHE2, "", true,  false);
							myAdapter.notifyDataSetChanged();
						
					}else if(radioButton92.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						setPreferences(VariloidForm4.FREQCRECHE2, "", false,  false);
						myAdapter.notifyDataSetChanged();
						
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
						setPreferences(VariloidForm4.FREQCRECHE2, "", false,  false);
						myAdapter.notifyDataSetChanged();
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.FREQCRECHE2:
			
			View layout32 = layoutInflater.inflate(R.layout.checkbox_formulario, null);
			
			final CheckBox checkbox31 = (CheckBox) layout32.findViewById(R.id.checkBox1);
			checkbox31.setText(getString(R.string.formulario4_opcao_crianca_com_pneumonia));
			final CheckBox checkbox32 = (CheckBox) layout32.findViewById(R.id.checkBox2);
			checkbox32.setText(getString(R.string.formulario4_opcao_pais_outra_crianca_menos_que_dez_anos));
			final CheckBox checkbox33 = (CheckBox) layout32.findViewById(R.id.checkBox3);
			checkbox33.setText(getString(R.string.formulario4_opcao_familia_trabalha_creche));
			final CheckBox checkbox34 = (CheckBox) layout32.findViewById(R.id.checkBox4);
			checkbox34.setVisibility(View.GONE);
			final CheckBox checkbox35 = (CheckBox) layout32.findViewById(R.id.checkBox5);
			checkbox35.setVisibility(View.GONE);
			final CheckBox checkbox36 = (CheckBox) layout32.findViewById(R.id.checkBox6);
			checkbox36.setVisibility(View.GONE);
			final CheckBox checkbox37 = (CheckBox) layout32.findViewById(R.id.checkBox7);
			checkbox37.setVisibility(View.GONE);
			final CheckBox checkbox38 = (CheckBox) layout32.findViewById(R.id.checkBox8);
			checkbox38.setVisibility(View.GONE);
			final CheckBox checkbox39 = (CheckBox) layout32.findViewById(R.id.checkBox9);
			checkbox39.setVisibility(View.GONE);
			final CheckBox checkbox40 = (CheckBox) layout32.findViewById(R.id.checkBox10);
			checkbox40.setVisibility(View.GONE);
			final CheckBox checkbox41 = (CheckBox) layout32.findViewById(R.id.checkBox11);
			checkbox41.setVisibility(View.GONE);
			final CheckBox checkbox42 = (CheckBox) layout32.findViewById(R.id.checkBox12);
			checkbox42.setVisibility(View.GONE);
			final CheckBox checkbox43 = (CheckBox) layout32.findViewById(R.id.checkBox13);
			checkbox43.setVisibility(View.GONE);
			final CheckBox checkbox44 = (CheckBox) layout32.findViewById(R.id.checkBox14);
			checkbox44.setVisibility(View.GONE);
			final CheckBox checkbox45 = (CheckBox) layout32.findViewById(R.id.checkBox15);
			checkbox45.setVisibility(View.GONE);
			final CheckBox checkbox46 = (CheckBox) layout32.findViewById(R.id.checkBox16);
			checkbox46.setVisibility(View.GONE);
			
			if(!campo.equalsIgnoreCase("")){			
				if(campo.indexOf(getString(R.string.formulario4_opcao_crianca_com_pneumonia))!=-1){
					checkbox31.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_pais_outra_crianca_menos_que_dez_anos))!=-1){
					checkbox32.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_familia_trabalha_creche))!=-1){
					checkbox33.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario4_freq_creche2));
			alert.setView(layout32);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				List<String> lisStrings = new ArrayList<String>();
				String valor;
				public void onClick(DialogInterface dialog, int whichButton) {
					if(checkbox31.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_crianca_com_pneumonia));
					}
					if(checkbox32.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_pais_outra_crianca_menos_que_dez_anos));
					}
					if(checkbox33.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_familia_trabalha_creche));
					}
					
					if(!lisStrings.isEmpty()){
						for (String string : lisStrings) {
							if(valor==null){
								valor = string;
							}
							valor = valor +","+ string;
						}
						setPreferences(arg2, valor, false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});
	
			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.FREQSERVSAUDE1:
			View layout33 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton96 = (RadioButton) layout33.findViewById(R.id.radio1);
			radioButton96.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton97 = (RadioButton) layout33.findViewById(R.id.radio2);
			radioButton97.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton98 = (RadioButton) layout33.findViewById(R.id.radio3);
			radioButton98.setVisibility(View.VISIBLE);
			radioButton98.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton96.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton97.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton98.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_freq_serv_saude1));
			alert.setView(layout33);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton96.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						setPreferences(VariloidForm4.FREQSERVSAUDE2, "", false,  false);
						myAdapter.notifyDataSetChanged();
					}else if(radioButton97.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						setPreferences(VariloidForm4.FREQSERVSAUDE2, "", true,  false);
						myAdapter.notifyDataSetChanged();
					}else if(radioButton98.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						setPreferences(VariloidForm4.FREQSERVSAUDE2, "", false,  false);
						myAdapter.notifyDataSetChanged();
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
						setPreferences(VariloidForm4.FREQSERVSAUDE2, "", false,  false);
						myAdapter.notifyDataSetChanged();
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.FREQSERVSAUDE2:
			
			View layout34 = layoutInflater.inflate(R.layout.checkbox_formulario, null);
			
			final CheckBox checkbox311 = (CheckBox) layout34.findViewById(R.id.checkBox1);
			checkbox311.setText(getString(R.string.formulario4_opcao_crianca_recrutada));
			final CheckBox checkbox312 = (CheckBox) layout34.findViewById(R.id.checkBox2);
			checkbox312.setText(getString(R.string.formulario4_opcao_criancas_ate_dez_anos));
			final CheckBox checkbox313 = (CheckBox) layout34.findViewById(R.id.checkBox3);
			checkbox313.setText(getString(R.string.formulario4_opcao_familia_trabalha_em_instituicao_de_saude));
			final CheckBox checkbox314 = (CheckBox) layout34.findViewById(R.id.checkBox4);
			checkbox314.setText(getString(R.string.formulario4_opcao_outro_adulto));
			final CheckBox checkbox315 = (CheckBox) layout34.findViewById(R.id.checkBox5);
			checkbox315.setVisibility(View.GONE);
			final CheckBox checkbox316 = (CheckBox) layout34.findViewById(R.id.checkBox6);
			checkbox316.setVisibility(View.GONE);
			final CheckBox checkbox317 = (CheckBox) layout34.findViewById(R.id.checkBox7);
			checkbox317.setVisibility(View.GONE);
			final CheckBox checkbox318 = (CheckBox) layout34.findViewById(R.id.checkBox8);
			checkbox318.setVisibility(View.GONE);
			final CheckBox checkbox319 = (CheckBox) layout34.findViewById(R.id.checkBox9);
			checkbox319.setVisibility(View.GONE);
			final CheckBox checkbox410 = (CheckBox) layout34.findViewById(R.id.checkBox10);
			checkbox410.setVisibility(View.GONE);
			final CheckBox checkbox411 = (CheckBox) layout34.findViewById(R.id.checkBox11);
			checkbox411.setVisibility(View.GONE);
			final CheckBox checkbox412 = (CheckBox) layout34.findViewById(R.id.checkBox12);
			checkbox412.setVisibility(View.GONE);
			final CheckBox checkbox413 = (CheckBox) layout34.findViewById(R.id.checkBox13);
			checkbox413.setVisibility(View.GONE);
			final CheckBox checkbox414 = (CheckBox) layout34.findViewById(R.id.checkBox14);
			checkbox414.setVisibility(View.GONE);
			final CheckBox checkbox415 = (CheckBox) layout34.findViewById(R.id.checkBox15);
			checkbox415.setVisibility(View.GONE);
			final CheckBox checkbox416 = (CheckBox) layout34.findViewById(R.id.checkBox16);
			checkbox416.setVisibility(View.GONE);
			
			if(!campo.equalsIgnoreCase("")){			
				if(campo.indexOf(getString(R.string.formulario4_opcao_crianca_recrutada))!=-1){
					checkbox311.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_criancas_ate_dez_anos))!=-1){
					checkbox312.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_familia_trabalha_em_instituicao_de_saude))!=-1){
					checkbox313.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_outro_adulto))!=-1){
					checkbox314.setChecked(true);
				}
				
			}
			
			alert.setTitle(getString(R.string.formulario4_freq_serv_saude2));
			alert.setView(layout34);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				List<String> lisStrings = new ArrayList<String>();
				String valor;
				public void onClick(DialogInterface dialog, int whichButton) {
					if(checkbox311.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_crianca_com_pneumonia));
					}
					if(checkbox312.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_criancas_ate_dez_anos));
					}
					if(checkbox313.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_familia_trabalha_em_instituicao_de_saude));
					}
					if(checkbox314.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_outro_adulto));
					}
					if(!lisStrings.isEmpty()){
						for (String string : lisStrings) {
							if(valor==null){
								valor = string;
							}
							valor = valor +","+ string;
						}
						setPreferences(arg2, valor, false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});
	
			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			
			break;
		case VariloidForm4.OTITE6:
			
			final EditText inputOtite6 = new EditText(Formulario4Activity.this);
			int maxLengthCampoOtite6 = 3;    
			inputOtite6.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthCampoOtite6)});
			inputOtite6.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))inputOtite6.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_otite6));
			alert.setView(inputOtite6);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputOtite6.getText().toString())){
						setPreferences(arg2, inputOtite6.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			
//			View layout35 = layoutInflater.inflate(R.layout.group_box_formulario, null);
//			final RadioButton radioButton102 = (RadioButton) layout35.findViewById(R.id.radio1);
//			radioButton102.setText(getString(R.string.formulario4_opcao_sim));
//			final RadioButton radioButton103 = (RadioButton) layout35.findViewById(R.id.radio2);
//			radioButton103.setText(getString(R.string.formulario4_opcao_nao));
//			final RadioButton radioButton104 = (RadioButton) layout35.findViewById(R.id.radio3);
//			radioButton104.setVisibility(View.VISIBLE);
//			radioButton104.setText(getString(R.string.formulario4_opcao_nao_sabe));
//			
//			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
//				radioButton102.setChecked(true);
//			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
//				radioButton103.setChecked(true);
//			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
//				radioButton104.setChecked(true);
//			}
//			
//			alert.setTitle(getString(R.string.formulario4_otite6));
//			alert.setView(layout35);
//			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//
//					if(radioButton102.isChecked()){
//						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
//					}else if(radioButton103.isChecked()){
//						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
//					}else if(radioButton104.isChecked()){
//						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
//					}else{
//						setPreferences(arg2, "", false, false);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
//					}
//					dialog.cancel();
//				}
//			});
//
//			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//					dialog.cancel();
//				}
//			});
//			alert.show();
			break;
		case VariloidForm4.OTITE12:
			final EditText inputOtite12 = new EditText(Formulario4Activity.this);
			int maxLengthCampoOtite12 = 3;    
			inputOtite12.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthCampoOtite12)});
			inputOtite12.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))inputOtite12.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_otite12));
			alert.setView(inputOtite12);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputOtite12.getText().toString())){
						setPreferences(arg2, inputOtite12.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			
//			View layout14 = layoutInflater.inflate(R.layout.group_box_formulario, null);
//			final RadioButton radioButton38 = (RadioButton) layout14.findViewById(R.id.radio1);
//			radioButton38.setText(getString(R.string.formulario4_opcao_sim));
//			final RadioButton radioButton39 = (RadioButton) layout14.findViewById(R.id.radio2);
//			radioButton39.setText(getString(R.string.formulario4_opcao_nao));
//			final RadioButton radioButton40 = (RadioButton) layout14.findViewById(R.id.radio3);
//			radioButton40.setVisibility(View.VISIBLE);
//			radioButton40.setText(getString(R.string.formulario4_opcao_nao_sabe));
//			
//			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
//				radioButton38.setChecked(true);
//			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
//				radioButton39.setChecked(true);
//			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
//				radioButton40.setChecked(true);
//			}
//			
//			alert.setTitle(getString(R.string.formulario4_otite12));
//			alert.setView(layout14);
//			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//
//					if(radioButton38.isChecked()){
//						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
//					}else if(radioButton39.isChecked()){
//						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
//					}else if(radioButton40.isChecked()){
//						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
//					}else{
//						setPreferences(arg2, "", false, false);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
//					}
//					dialog.cancel();
//				}
//			});
//
//			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//					dialog.cancel();
//				}
//			});
//			alert.show();
			break;
		case VariloidForm4.HOSPITALIZACAO:
			View layout15 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton41 = (RadioButton) layout15.findViewById(R.id.radio1);
			radioButton41.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton42 = (RadioButton) layout15.findViewById(R.id.radio2);
			radioButton42.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton43 = (RadioButton) layout15.findViewById(R.id.radio3);
			radioButton43.setVisibility(View.VISIBLE);
			radioButton43.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton41.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton42.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton43.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_hospitalizacao));
			alert.setView(layout15);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton41.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton42.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton43.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.PNM6:
			
			final EditText inputPneumo6 = new EditText(Formulario4Activity.this);
			int maxLengthPneumo6 = 3;    
			inputPneumo6.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthPneumo6)});
			inputPneumo6.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))inputPneumo6.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_pnm6));
			alert.setView(inputPneumo6);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputPneumo6.getText().toString())){
						setPreferences(arg2, inputPneumo6.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			
//			View layout16 = layoutInflater.inflate(R.layout.group_box_formulario, null);
//			final RadioButton radioButton44 = (RadioButton) layout16.findViewById(R.id.radio1);
//			radioButton44.setText(getString(R.string.formulario4_opcao_sim));
//			final RadioButton radioButton45 = (RadioButton) layout16.findViewById(R.id.radio2);
//			radioButton45.setText(getString(R.string.formulario4_opcao_nao));
//			final RadioButton radioButton46 = (RadioButton) layout16.findViewById(R.id.radio3);
//			radioButton46.setVisibility(View.VISIBLE);
//			radioButton46.setText(getString(R.string.formulario4_opcao_nao_sabe));
//			
//			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
//				radioButton44.setChecked(true);
//			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
//				radioButton45.setChecked(true);
//			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
//				radioButton46.setChecked(true);
//			}
//			
//			alert.setTitle(getString(R.string.formulario4_pnm6));
//			alert.setView(layout16);
//			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//
//					if(radioButton44.isChecked()){
//						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
//					}else if(radioButton45.isChecked()){
//						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
//					}else if(radioButton46.isChecked()){
//						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
//					}else{
//						setPreferences(arg2, "", false, false);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
//					}
//					dialog.cancel();
//				}
//			});
//
//			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//					dialog.cancel();
//				}
//			});
//			alert.show();
			break;
		case VariloidForm4.PNM12:
			
			final EditText inputPneumo12 = new EditText(Formulario4Activity.this);
			int maxLengthPneumo12 = 3;    
			inputPneumo12.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthPneumo12)});
			inputPneumo12.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))inputPneumo12.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_pnm12));
			alert.setView(inputPneumo12);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputPneumo12.getText().toString())){
						setPreferences(arg2, inputPneumo12.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
//			View layout17 = layoutInflater.inflate(R.layout.group_box_formulario, null);
//			final RadioButton radioButton47 = (RadioButton) layout17.findViewById(R.id.radio1);
//			radioButton47.setText(getString(R.string.formulario4_opcao_sim));
//			final RadioButton radioButton48 = (RadioButton) layout17.findViewById(R.id.radio2);
//			radioButton48.setText(getString(R.string.formulario4_opcao_nao));
//			final RadioButton radioButton49 = (RadioButton) layout17.findViewById(R.id.radio3);
//			radioButton49.setVisibility(View.VISIBLE);
//			radioButton49.setText(getString(R.string.formulario4_opcao_nao_sabe));
//			
//			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
//				radioButton47.setChecked(true);
//			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
//				radioButton48.setChecked(true);
//			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
//				radioButton49.setChecked(true);
//			}
//			
//			alert.setTitle(getString(R.string.formulario4_pnm12));
//			alert.setView(layout17);
//			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//
//					if(radioButton47.isChecked()){
//						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
//					}else if(radioButton48.isChecked()){
//						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
//					}else if(radioButton49.isChecked()){
//						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
//					}else{
//						setPreferences(arg2, "", false, false);
//						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
//					}
//					dialog.cancel();
//				}
//			});
//
//			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//					dialog.cancel();
//				}
//			});
//			alert.show();
			break;
		case VariloidForm4.HOSPITALIZACAO_PNM6:
			View layout18 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton50 = (RadioButton) layout18.findViewById(R.id.radio1);
			radioButton50.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton51 = (RadioButton) layout18.findViewById(R.id.radio2);
			radioButton51.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton52 = (RadioButton) layout18.findViewById(R.id.radio3);
			radioButton52.setVisibility(View.VISIBLE);
			radioButton52.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton50.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton51.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton52.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_hospitalizacao_pnm6));
			alert.setView(layout18);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton50.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton51.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton52.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.HOSPITALIZACAO_PNM12:
			View layout36 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton105 = (RadioButton) layout36.findViewById(R.id.radio1);
			radioButton105.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton106 = (RadioButton) layout36.findViewById(R.id.radio2);
			radioButton106.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton107 = (RadioButton) layout36.findViewById(R.id.radio3);
			radioButton107.setVisibility(View.VISIBLE);
			radioButton107.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton105.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton106.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton107.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_hospitalizacao_pnm12));
			alert.setView(layout36);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton105.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton106.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton107.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		
		case VariloidForm4.TEVE_CHIADO_NO_PEITO_NO_PASSADO:
			View layoutChiadoNoPeito = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButtonR1 = (RadioButton) layoutChiadoNoPeito.findViewById(R.id.radio1);
			radioButtonR1.setText(getString(R.string.formulario4_opcao_sim_mais_de_doze_meses));
			final RadioButton radioButtonR2 = (RadioButton) layoutChiadoNoPeito.findViewById(R.id.radio2);
			radioButtonR2.setText(getString(R.string.formulario4_opcao_sim_menos_de_doze_meses));
			final RadioButton radioButtonR3 = (RadioButton) layoutChiadoNoPeito.findViewById(R.id.radio3);
			radioButtonR3.setVisibility(View.VISIBLE);
			radioButtonR3.setText(getString(R.string.formulario4_opcao_nao));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim_mais_de_doze_meses))){
				radioButtonR1.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim_menos_de_doze_meses))){
				radioButtonR2.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButtonR3.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_teve_chiado_no_peito_no_passado));
			alert.setView(layoutChiadoNoPeito);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButtonR1.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim_mais_de_doze_meses), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						
						for (int i = VariloidForm4.QTD_CRISES_NOS_ULTIMOS_DOZE_MESES; i < VariloidForm4.TEVE_ASMA; i++) {
							setPreferences(i, "", false,  false);
						}
						
					}else if(radioButtonR2.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim_menos_de_doze_meses), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						
						for (int i = VariloidForm4.QTD_CRISES_NOS_ULTIMOS_DOZE_MESES; i < VariloidForm4.TEVE_ASMA; i++) {
							setPreferences(i, "", false,  false);
						}
						
					}else if(radioButtonR3.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						
						for (int i = VariloidForm4.QTD_CRISES_NOS_ULTIMOS_DOZE_MESES; i < VariloidForm4.TEVE_ASMA; i++) {
							setPreferences(i, "", true,  false);
						}
						
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
			
		case VariloidForm4.QTD_CRISES_NOS_ULTIMOS_DOZE_MESES:
			View layoutQtdCrises = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButtonR4 = (RadioButton) layoutQtdCrises.findViewById(R.id.radio1);
			radioButtonR4.setText(getString(R.string.formulario4_opcao_nenhuma));
			final RadioButton radioButtonR5 = (RadioButton) layoutQtdCrises.findViewById(R.id.radio2);
			radioButtonR5.setText(getString(R.string.formulario4_opcao_uma_a_tres));
			final RadioButton radioButtonR6 = (RadioButton) layoutQtdCrises.findViewById(R.id.radio3);
			radioButtonR6.setVisibility(View.VISIBLE);
			radioButtonR6.setText(getString(R.string.formulario4_opcao_quatro_ou_mais));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nenhuma))){
				radioButtonR4.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_uma_a_tres))){
				radioButtonR5.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_quatro_ou_mais))){
				radioButtonR6.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_qtd_crises_nos_ultimos_doze_meses));
			alert.setView(layoutQtdCrises);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButtonR4.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nenhuma), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonR5.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_uma_a_tres), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonR6.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_quatro_ou_mais), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
			
		case VariloidForm4.FREQ_DE_SONO_PREJUDICADO_POR_CHIADO:
			View layoutFrequenciaSono = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButtonR7 = (RadioButton) layoutFrequenciaSono.findViewById(R.id.radio1);
			radioButtonR7.setText(getString(R.string.formulario4_opcao_nunca_acordou_por_chiado));
			final RadioButton radioButtonR8 = (RadioButton) layoutFrequenciaSono.findViewById(R.id.radio2);
			radioButtonR8.setText(getString(R.string.formulario4_opcao_menos_de_uma_noite_por_semana));
			final RadioButton radioButtonR9 = (RadioButton) layoutFrequenciaSono.findViewById(R.id.radio3);
			radioButtonR9.setVisibility(View.VISIBLE);
			radioButtonR9.setText(getString(R.string.formulario4_opcao_uma_ou_mais_noites_por_semana));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nunca_acordou_por_chiado))){
				radioButtonR7.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_menos_de_uma_noite_por_semana))){
				radioButtonR8.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_uma_ou_mais_noites_por_semana))){
				radioButtonR9.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_frequencia_de_sono_prejudicado_por_chiado_no_peito));
			alert.setView(layoutFrequenciaSono);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButtonR7.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nunca_acordou_por_chiado), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonR8.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_menos_de_uma_noite_por_semana), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonR9.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_uma_ou_mais_noites_por_semana), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.CHIADO_IMPEDIU_DIZER_PALAVRAS:
			View layoutImpediuPalavras = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButtonR10 = (RadioButton) layoutImpediuPalavras.findViewById(R.id.radio1);
			radioButtonR10.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButtonR11 = (RadioButton) layoutImpediuPalavras.findViewById(R.id.radio2);
			radioButtonR11.setText(getString(R.string.formulario4_opcao_nao));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButtonR10.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButtonR11.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_chiado_impediu_dizer_palavras));
			alert.setView(layoutImpediuPalavras);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButtonR10.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonR11.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.TEVE_ASMA:
			View layoutTeveAsma = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButtonR12 = (RadioButton) layoutTeveAsma.findViewById(R.id.radio1);
			radioButtonR12.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButtonR13 = (RadioButton) layoutTeveAsma.findViewById(R.id.radio2);
			radioButtonR13.setText(getString(R.string.formulario4_opcao_nao));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButtonR12.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButtonR13.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_teve_asma));
			alert.setView(layoutTeveAsma);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButtonR12.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonR13.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.TEVE_CHIADO_APOS_EXERCICIOS:
			View layoutTeveChiadoPosExercicios = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButtonR14 = (RadioButton) layoutTeveChiadoPosExercicios.findViewById(R.id.radio1);
			radioButtonR14.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButtonR15 = (RadioButton) layoutTeveChiadoPosExercicios.findViewById(R.id.radio2);
			radioButtonR15.setText(getString(R.string.formulario4_opcao_nao));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButtonR14.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButtonR15.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_chiado_apos_exercicios));
			alert.setView(layoutTeveChiadoPosExercicios);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButtonR14.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonR15.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.TEVE_TOSSE_SEM_GRIPAR:
			View layoutTeveTosseSemGripar = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButtonR16 = (RadioButton) layoutTeveTosseSemGripar.findViewById(R.id.radio1);
			radioButtonR16.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButtonR17 = (RadioButton) layoutTeveTosseSemGripar.findViewById(R.id.radio2);
			radioButtonR17.setText(getString(R.string.formulario4_opcao_nao));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButtonR16.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButtonR17.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_tosse_sem_estar_gripado));
			alert.setView(layoutTeveTosseSemGripar);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButtonR16.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonR17.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.TEVE_CHIADO_NOS_ULTIMOS_DOZE_MESES:
			View layoutTeveChiadoNosUltimosMeses = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButtonR18 = (RadioButton) layoutTeveChiadoNosUltimosMeses.findViewById(R.id.radio1);
			radioButtonR18.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButtonR19 = (RadioButton) layoutTeveChiadoNosUltimosMeses.findViewById(R.id.radio2);
			radioButtonR19.setText(getString(R.string.formulario4_opcao_nao));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButtonR18.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButtonR19.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_teve_chiado_nos_ultimos_doze_meses));
			alert.setView(layoutTeveChiadoNosUltimosMeses);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButtonR18.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonR19.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.DOENCAFALCIFORME:
			View layout38 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton112 = (RadioButton) layout38.findViewById(R.id.radio1);
			radioButton112.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton113 = (RadioButton) layout38.findViewById(R.id.radio2);
			radioButton113.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton114 = (RadioButton) layout38.findViewById(R.id.radio3);
			radioButton114.setVisibility(View.VISIBLE);
			radioButton114.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton112.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton113.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton114.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_doenca_falciforme));
			alert.setView(layout38);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton112.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton113.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton114.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.PREMATURIDADE:
			View layout39 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton115 = (RadioButton) layout39.findViewById(R.id.radio1);
			radioButton115.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton116 = (RadioButton) layout39.findViewById(R.id.radio2);
			radioButton116.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton117 = (RadioButton) layout39.findViewById(R.id.radio3);
			radioButton117.setVisibility(View.VISIBLE);
			radioButton117.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton115.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton116.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton117.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_prematuridade));
			alert.setView(layout39);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton115.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton116.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton117.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.INFECHIV:
			View layout40 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton118 = (RadioButton) layout40.findViewById(R.id.radio1);
			radioButton118.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton119 = (RadioButton) layout40.findViewById(R.id.radio2);
			radioButton119.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton120 = (RadioButton) layout40.findViewById(R.id.radio3);
			radioButton120.setVisibility(View.VISIBLE);
			radioButton120.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton118.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton119.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton120.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_infec_hiv));
			alert.setView(layout40);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton118.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton119.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton120.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.IMUNODEFICIENCIA:
			View layout41 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton121 = (RadioButton) layout41.findViewById(R.id.radio1);
			radioButton121.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton122 = (RadioButton) layout41.findViewById(R.id.radio2);
			radioButton122.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton123 = (RadioButton) layout41.findViewById(R.id.radio3);
			radioButton123.setVisibility(View.VISIBLE);
			radioButton123.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton121.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton122.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton123.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_imuno_deficiencia));
			alert.setView(layout41);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton121.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton122.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton123.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.ASMA:
			View layout42 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton124 = (RadioButton) layout42.findViewById(R.id.radio1);
			radioButton124.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton125 = (RadioButton) layout42.findViewById(R.id.radio2);
			radioButton125.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton126 = (RadioButton) layout42.findViewById(R.id.radio3);
			radioButton126.setVisibility(View.VISIBLE);
			radioButton126.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton124.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton125.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton126.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_asma));
			alert.setView(layout42);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton124.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton125.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton126.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.CORTICOIDE:
			View layout43 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton127 = (RadioButton) layout43.findViewById(R.id.radio1);
			radioButton127.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton128 = (RadioButton) layout43.findViewById(R.id.radio2);
			radioButton128.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton129 = (RadioButton) layout43.findViewById(R.id.radio3);
			radioButton129.setVisibility(View.VISIBLE);
			radioButton129.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton127.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton128.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton129.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_corticoide));
			alert.setView(layout43);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton127.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton128.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton129.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.CANCER:
			View layout44 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton130 = (RadioButton) layout44.findViewById(R.id.radio1);
			radioButton130.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton131 = (RadioButton) layout44.findViewById(R.id.radio2);
			radioButton131.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton132 = (RadioButton) layout44.findViewById(R.id.radio3);
			radioButton132.setVisibility(View.VISIBLE);
			radioButton132.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton130.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton131.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton132.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_cancer));
			alert.setView(layout44);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton130.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton131.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton132.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.DOENCAPULMONARCRONICA:
			View layout45 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton133 = (RadioButton) layout45.findViewById(R.id.radio1);
			radioButton133.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton134 = (RadioButton) layout45.findViewById(R.id.radio2);
			radioButton134.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton135 = (RadioButton) layout45.findViewById(R.id.radio3);
			radioButton135.setVisibility(View.VISIBLE);
			radioButton135.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton133.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton134.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton135.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_doenca_pulmonar_cronica));
			alert.setView(layout45);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton133.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton134.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton135.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.DOENCARENALCRONICA:
			View layout46 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton136 = (RadioButton) layout46.findViewById(R.id.radio1);
			radioButton136.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton137 = (RadioButton) layout46.findViewById(R.id.radio2);
			radioButton137.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton138 = (RadioButton) layout46.findViewById(R.id.radio3);
			radioButton138.setVisibility(View.VISIBLE);
			radioButton138.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton136.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton137.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton138.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_doenca_renal_cronica));
			alert.setView(layout46);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton136.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton137.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton138.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.DOENCACARDIACA:
			View layout47 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton139 = (RadioButton) layout47.findViewById(R.id.radio1);
			radioButton139.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton140 = (RadioButton) layout47.findViewById(R.id.radio2);
			radioButton140.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton141 = (RadioButton) layout47.findViewById(R.id.radio3);
			radioButton141.setVisibility(View.VISIBLE);
			radioButton141.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton139.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton140.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton141.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_doenca_cardiaca));
			alert.setView(layout47);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton139.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton140.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton141.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.DOENCAHEMATOLOGICA:
			View layout48= layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton142 = (RadioButton) layout48.findViewById(R.id.radio1);
			radioButton142.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton143 = (RadioButton) layout48.findViewById(R.id.radio2);
			radioButton143.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton144 = (RadioButton) layout48.findViewById(R.id.radio3);
			radioButton144.setVisibility(View.VISIBLE);
			radioButton144.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton142.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton143.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton144.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_doenca_hematologica));
			alert.setView(layout48);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton142.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton143.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton144.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.DOENCAHEPATICA:
			View layout49= layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton145 = (RadioButton) layout49.findViewById(R.id.radio1);
			radioButton145.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton146 = (RadioButton) layout49.findViewById(R.id.radio2);
			radioButton146.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton147 = (RadioButton) layout49.findViewById(R.id.radio3);
			radioButton147.setVisibility(View.VISIBLE);
			radioButton147.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton145.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton146.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton147.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_doenca_hepatica));
			alert.setView(layout49);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton145.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton146.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton147.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.ASPLENIA:
			View layout50= layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton148 = (RadioButton) layout50.findViewById(R.id.radio1);
			radioButton148.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton149 = (RadioButton) layout50.findViewById(R.id.radio2);
			radioButton149.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton150 = (RadioButton) layout50.findViewById(R.id.radio3);
			radioButton150.setVisibility(View.VISIBLE);
			radioButton150.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton148.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton149.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton150.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_asplenia));
			alert.setView(layout50);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton148.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton149.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton150.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.DIABETES:
			View layout51= layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton151 = (RadioButton) layout51.findViewById(R.id.radio1);
			radioButton151.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton152 = (RadioButton) layout51.findViewById(R.id.radio2);
			radioButton152.setText(getString(R.string.formulario4_opcao_nao));
			final RadioButton radioButton153 = (RadioButton) layout51.findViewById(R.id.radio3);
			radioButton153.setVisibility(View.VISIBLE);
			radioButton153.setText(getString(R.string.formulario4_opcao_nao_sabe));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton151.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton152.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao_sabe))){
				radioButton153.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_diabetes));
			alert.setView(layout51);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton151.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton152.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton153.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
			
		case VariloidForm4.QUADRO1_QUESTAO1:
			
			View layoutQuadro1Q1 = layoutInflater.inflate(R.layout.checkbox_formulario, null);
			int maxLengthEdt = 50;  
			final EditText outros91 = (EditText) layoutQuadro1Q1.findViewById(R.id.edit_text_outros7);
			outros91.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthEdt)});
			final EditText outros92 = (EditText) layoutQuadro1Q1.findViewById(R.id.edit_text);
			outros92.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthEdt)});
			
			final CheckBox checkboxQuestao1 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox1);
			checkboxQuestao1.setText(getString(R.string.formulario4_opcao_caminhada));
			final CheckBox checkboxQuestao2 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox2);
			checkboxQuestao2.setText(getString(R.string.formulario4_opcao_parquinho));
			final CheckBox checkboxQuestao3 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox3);
			checkboxQuestao3.setText(getString(R.string.formulario4_opcao_andar_bicicleta));
			final CheckBox checkboxQuestao4 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox4);
			checkboxQuestao4.setText(getString(R.string.formulario4_opcao_andar_com_cachorro));
			final CheckBox checkboxQuestao5 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox5);
			checkboxQuestao5.setText(getString(R.string.formulario4_opcao_dancar));
			final CheckBox checkboxQuestao6 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox6);
			checkboxQuestao6.setText(getString(R.string.formulario4_opcao_skate_patins));
			final CheckBox checkboxQuestao7 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox7);
			checkboxQuestao7.setText(getString(R.string.formulario4_opcao_outra_atividade_aerobica1));
			final CheckBox checkboxQuestao8 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox8);
			checkboxQuestao8.setText(getString(R.string.formulario4_opcao_corrida));
			final CheckBox checkboxQuestao9 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox9);
			checkboxQuestao9.setText(getString(R.string.formulario4_opcao_pula_corda));
			final CheckBox checkboxQuestao10 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox10);
			checkboxQuestao10.setText(getString(R.string.formulario4_opcao_ginastica));
			final CheckBox checkboxQuestao11 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox11);
			checkboxQuestao11.setText(getString(R.string.formulario4_opcao_correr_bicicleta));
			final CheckBox checkboxQuestao12 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox12);
			checkboxQuestao12.setText(getString(R.string.formulario4_opcao_dancar_com_vigor));
			final CheckBox checkboxQuestao13 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox13);
			checkboxQuestao13.setText(getString(R.string.formulario4_opcao_futebol_basquetebol));
			final CheckBox checkboxQuestao14 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox14);
			checkboxQuestao14.setText(getString(R.string.formulario4_opcao_caminhou_com_mochila));
			final CheckBox checkboxQuestao15 = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox15);
			checkboxQuestao15.setText(getString(R.string.formulario4_opcao_outra_atividade_aerobica2));
			final CheckBox checkboxGone = (CheckBox) layoutQuadro1Q1.findViewById(R.id.checkBox16);
			checkboxGone.setVisibility(View.GONE);
			
			checkboxQuestao7.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
						outros91.setVisibility(View.VISIBLE);
					}else{
						outros91.setVisibility(View.GONE);
					}
				}
			});
			
			checkboxQuestao15.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
						outros92.setVisibility(View.VISIBLE);
					}else{
						outros92.setVisibility(View.GONE);
					}
				}
			});

			if(!campo.equalsIgnoreCase("")){			
				if(campo.indexOf(getString(R.string.formulario4_opcao_caminhada))!=-1){
					checkboxQuestao1.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_parquinho))!=-1){
					checkboxQuestao2.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_andar_bicicleta))!=-1){
					checkboxQuestao3.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_andar_com_cachorro))!=-1){
					checkboxQuestao4.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_dancar))!=-1){
					checkboxQuestao5.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_skate_patins))!=-1){
					checkboxQuestao6.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_outra_atividade_aerobica1))!=-1){
					checkboxQuestao7.setChecked(true);
					int posicao = campo.indexOf(getString(R.string.formulario4_opcao_outra_atividade_aerobica1));
					outros91.setText(campo.substring(posicao + getString(R.string.formulario4_opcao_outra_atividade_aerobica1).length(), campo.length()).split(",")[0]);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_corrida))!=-1){
					checkboxQuestao8.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_pula_corda))!=-1){
					checkboxQuestao9.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_ginastica))!=-1){
					checkboxQuestao10.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_correr_bicicleta))!=-1){
					checkboxQuestao11.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_dancar_com_vigor))!=-1){
					checkboxQuestao12.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_futebol_basquetebol))!=-1){
					checkboxQuestao13.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_caminhou_com_mochila))!=-1){
					checkboxQuestao14.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_outra_atividade_aerobica2))!=-1){
					checkboxQuestao15.setChecked(true);
					int posicao = campo.indexOf(getString(R.string.formulario4_opcao_outra_atividade_aerobica2));
					outros92.setText(campo.substring(posicao + getString(R.string.formulario4_opcao_outra_atividade_aerobica2).length(), campo.length()).split(",")[0]);
				}
			}
			
			alert.setTitle(getString(R.string.formulario4_quadro_um_a_crianca_praticou));
			alert.setView(layoutQuadro1Q1);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				List<String> lisStrings = new ArrayList<String>();
				String valor;
				public void onClick(DialogInterface dialog, int whichButton) {
					if(checkboxQuestao1.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_caminhada));
					}
					if(checkboxQuestao2.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_parquinho));
					}
					if(checkboxQuestao3.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_andar_bicicleta));
					}
					if(checkboxQuestao4.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_andar_com_cachorro));
					}
					if(checkboxQuestao5.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_dancar));
					}
					if(checkboxQuestao6.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_skate_patins));
					}
					if(checkboxQuestao7.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_outra_atividade_aerobica1).concat(outros91.getText().toString()));
					}
					if(checkboxQuestao8.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_corrida));
					}
					if(checkboxQuestao9.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_pula_corda));
					}
					if(checkboxQuestao10.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_ginastica));
					}
					if(checkboxQuestao11.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_correr_bicicleta));
					}
					if(checkboxQuestao12.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_dancar_com_vigor));
					}
					if(checkboxQuestao13.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_futebol_basquetebol));
					}
					if(checkboxQuestao14.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_caminhou_com_mochila));
					}
					if(checkboxQuestao15.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_outra_atividade_aerobica2).concat(outros92.getText().toString()));
					}
					
					if(!lisStrings.isEmpty()){
						for (String string : lisStrings) {
							if(valor==null){
								valor = string;
							}
							valor = valor +","+ string;
						}
						setPreferences(arg2, valor, false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});
	
			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.QUADRO1_QUESTAO2:
			
			View quadro1Questao2 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton13 = (RadioButton) quadro1Questao2.findViewById(R.id.radio1);
			radioButton13.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton14 = (RadioButton) quadro1Questao2.findViewById(R.id.radio2);
			radioButton14.setText(getString(R.string.formulario4_opcao_nao));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton13.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton14.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_seu_filho_faz_pelo_menos_sessenta_minutos));
			alert.setView(quadro1Questao2);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton13.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton14.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			
			break;
		case VariloidForm4.QUADRO2_QUESTAO1:
			
			View layoutQuadro2Q1 = layoutInflater.inflate(R.layout.checkbox_formulario, null);
			int maxLengthEdt2 = 50;  
			final EditText outros11 = (EditText) layoutQuadro2Q1.findViewById(R.id.edit_text_outros5);
			outros11.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthEdt2)});
			final EditText outros12 = (EditText) layoutQuadro2Q1.findViewById(R.id.edit_text);
			outros12.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthEdt2)});
			
			final CheckBox checkbox2Questao1 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox1);
			checkbox2Questao1.setText(getString(R.string.formulario4_opcao_ginastica));
			final CheckBox checkbox2Questao2 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox2);
			checkbox2Questao2.setText(getString(R.string.formulario4_opcao_futebol_basquetebol));
			final CheckBox checkbox2Questao3 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox3);
			checkbox2Questao3.setText(getString(R.string.formulario4_opcao_flexoes_abdominais));
			final CheckBox checkbox2Questao4 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox4);
			checkbox2Questao4.setText(getString(R.string.formulario4_opcao_cabo_de_guerra));
			final CheckBox checkbox2Questao5 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox5);
			checkbox2Questao5.setText(getString(R.string.formulario4_opcao_outra_atividade_de_fortalecimento_muscular));
			final CheckBox checkbox2Questao6 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox6);
			checkbox2Questao6.setText(getString(R.string.formulario4_opcao_pular_e_escalar));
			final CheckBox checkbox2Questao7 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox7);
			checkbox2Questao7.setText(getString(R.string.formulario4_opcao_ginastica_futebol_basquetebol));
			final CheckBox checkbox2Questao8 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox8);
			checkbox2Questao8.setText(getString(R.string.formulario4_opcao_outro_esporte_na_agua));
			final CheckBox checkbox2Questao9 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox9);
			checkbox2Questao9.setText(getString(R.string.formulario4_opcao_correr_com_bicicleta));
			final CheckBox checkbox2Questao10 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox10);
			checkbox2Questao10.setText(getString(R.string.formulario4_opcao_amarelinha));
			final CheckBox checkbox2Questao11 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox11);
			checkbox2Questao11.setText(getString(R.string.formulario4_opcao_pula_corda));
			final CheckBox checkbox2Questao12 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox12);
			checkbox2Questao12.setText(getString(R.string.formulario4_opcao_dancar));
			final CheckBox checkbox2Questao13 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox13);
			checkbox2Questao13.setText(getString(R.string.formulario4_opcao_caminhada));
			final CheckBox checkbox2Questao14 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox14);
			checkbox2Questao14.setText(getString(R.string.formulario4_opcao_correr));
			final CheckBox checkbox2Questao15 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox15);
			checkbox2Questao15.setText(getString(R.string.formulario4_opcao_outra_atividade_de_fortalecimento_ossos));
			final CheckBox checkboxGone2 = (CheckBox) layoutQuadro2Q1.findViewById(R.id.checkBox16);
			checkboxGone2.setVisibility(View.GONE);
			
			checkbox2Questao5.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
						outros11.setVisibility(View.VISIBLE);
					}else{
						outros11.setVisibility(View.GONE);
					}
				}
			});
			
			checkbox2Questao15.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
						outros12.setVisibility(View.VISIBLE);
					}else{
						outros12.setVisibility(View.GONE);
					}
				}
			});

			if(!campo.equalsIgnoreCase("")){			
				if(campo.indexOf(getString(R.string.formulario4_opcao_ginastica))!=-1){
					checkbox2Questao1.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_futebol_basquetebol))!=-1){
					checkbox2Questao2.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_flexoes_abdominais))!=-1){
					checkbox2Questao3.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_cabo_de_guerra))!=-1){
					checkbox2Questao4.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_outra_atividade_de_fortalecimento_muscular))!=-1){
					checkbox2Questao5.setChecked(true);
					int posicao = campo.indexOf(getString(R.string.formulario4_opcao_outra_atividade_de_fortalecimento_muscular));
					outros11.setText(campo.substring(posicao + getString(R.string.formulario4_opcao_outra_atividade_de_fortalecimento_muscular).length(), campo.length()).split(",")[0]);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_pular_e_escalar))!=-1){
					checkbox2Questao6.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_ginastica_futebol_basquetebol))!=-1){
					checkbox2Questao6.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_outro_esporte_na_agua))!=-1){
					checkbox2Questao8.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_correr_com_bicicleta))!=-1){
					checkbox2Questao9.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_amarelinha))!=-1){
					checkbox2Questao10.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_pula_corda))!=-1){
					checkbox2Questao11.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_dancar))!=-1){
					checkbox2Questao12.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_caminhada))!=-1){
					checkbox2Questao13.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_correr))!=-1){
					checkbox2Questao14.setChecked(true);
				}if(campo.indexOf(getString(R.string.formulario4_opcao_outra_atividade_de_fortalecimento_ossos))!=-1){
					checkbox2Questao15.setChecked(true);
					int posicao = campo.indexOf(getString(R.string.formulario4_opcao_outra_atividade_de_fortalecimento_ossos));
					outros12.setText(campo.substring(posicao + getString(R.string.formulario4_opcao_outra_atividade_de_fortalecimento_ossos).length(), campo.length()).split(",")[0]);
				}
			}
			
			alert.setTitle(getString(R.string.formulario4_quadro_dois_a_crianca_praticou));
			alert.setView(layoutQuadro2Q1);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				List<String> lisStrings = new ArrayList<String>();
				String valor;
				public void onClick(DialogInterface dialog, int whichButton) {
					if(checkbox2Questao1.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_ginastica));
					}
					if(checkbox2Questao2.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_futebol_basquetebol));
					}
					if(checkbox2Questao3.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_flexoes_abdominais));
					}
					if(checkbox2Questao4.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_cabo_de_guerra));
					}
					if(checkbox2Questao5.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_outra_atividade_de_fortalecimento_muscular).concat(outros11.getText().toString()));
					}
					if(checkbox2Questao6.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_pular_e_escalar));
					}
					if(checkbox2Questao7.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_ginastica_futebol_basquetebol));
					}
					if(checkbox2Questao8.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_outro_esporte_na_agua));
					}
					if(checkbox2Questao9.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_correr_com_bicicleta));
					}
					if(checkbox2Questao10.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_amarelinha));
					}
					if(checkbox2Questao11.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_pula_corda));
					}
					if(checkbox2Questao12.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_dancar));
					}
					if(checkbox2Questao13.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_caminhada));
					}
					if(checkbox2Questao14.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_correr));
					}
					if(checkbox2Questao15.isChecked()){
						lisStrings.add(getString(R.string.formulario4_opcao_outra_atividade_de_fortalecimento_ossos).concat(outros12.getText().toString()));
					}
					
					if(!lisStrings.isEmpty()){
						for (String string : lisStrings) {
							if(valor==null){
								valor = string;
							}
							valor = valor +","+ string;
						}
						setPreferences(arg2, valor, false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});
	
			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			
			break;
		case VariloidForm4.QUADRO2_QUESTAO2:
			
			View quadro2Questao2 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton233 = (RadioButton) quadro2Questao2.findViewById(R.id.radio1);
			radioButton233.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton244 = (RadioButton) quadro2Questao2.findViewById(R.id.radio2);
			radioButton244.setText(getString(R.string.formulario4_opcao_nao));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton233.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton244.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_seu_filho_faz_atividade_fisica_pelos_menos_tres_vezes));
			alert.setView(quadro2Questao2);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton233.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton244.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			
			break;
			
		case VariloidForm4.USO_ATB:
			View layout2 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton3 = (RadioButton) layout2.findViewById(R.id.radio1);
			radioButton3.setText(getString(R.string.formulario4_opcao_sim));
			final RadioButton radioButton4 = (RadioButton) layout2.findViewById(R.id.radio2);
			radioButton4.setText(getString(R.string.formulario4_opcao_nao));
			
			if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_sim))){
				radioButton3.setChecked(true);
			}else if(campo.equalsIgnoreCase(getString(R.string.formulario4_opcao_nao))){
				radioButton4.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario4_uso_atb));
			alert.setView(layout2);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton3.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton4.isChecked()){
						setPreferences(arg2, getString(R.string.formulario4_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.QUAL_ATB:
			final EditText qualAtb = new EditText(Formulario4Activity.this);
			int maxLengthAtb = 90;    
			qualAtb.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthAtb)});
			
			if(!TextUtils.isEmpty(campo))qualAtb.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_qual_atb));
			alert.setView(qualAtb);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(qualAtb.getText().toString())){
						setPreferences(arg2, qualAtb.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.N_SWABS:
			final EditText numeroSwabs = new EditText(Formulario4Activity.this);
			int maxLength23 = 1;    
			numeroSwabs.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength23)});
			numeroSwabs.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))numeroSwabs.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_n_swabs));
			alert.setView(numeroSwabs);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(numeroSwabs.getText().toString())){
						setPreferences(arg2, numeroSwabs.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.DATA_SWABS:
			
			final EditText dataSwabs = new EditText(Formulario4Activity.this);
			dataSwabs.setInputType(InputType.TYPE_CLASS_DATETIME);
			dataSwabs.addTextChangedListener(Mask.insert(
					"##/##/####", dataSwabs));
			
			if(!TextUtils.isEmpty(campo)){
				String replaceCampo = campo.replaceAll("/", "");
				dataSwabs.setText(replaceCampo);
			}
			
			alert.setTitle(getString(R.string.formulario4_data_swab));
			alert.setView(dataSwabs);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(dataSwabs.getText().toString())){
						setPreferences(arg2, dataSwabs.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
			
		case VariloidForm4.ID_SWAB_COLETADO:
			final EditText numeroIdSwabs = new EditText(Formulario4Activity.this);
			int maxLength24 = 4;    
			numeroIdSwabs.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength24)});
			numeroIdSwabs.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(campo))numeroIdSwabs.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_id_swabs_coletado));
			alert.setView(numeroIdSwabs);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(numeroIdSwabs.getText().toString())){
						setPreferences(arg2, numeroIdSwabs.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "0", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();
			break;
		case VariloidForm4.OBS_CASO:
			final EditText obsCasp = new EditText(Formulario4Activity.this);
			int maxLength27 = 250;    
			obsCasp.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength27)});
			
			if(!TextUtils.isEmpty(campo))obsCasp.setText(campo);
			
			alert.setTitle(getString(R.string.formulario4_obs_caso));
			alert.setView(obsCasp);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(obsCasp.getText().toString())){
						setPreferences(arg2, obsCasp.getText().toString(), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
					}
					dialog.cancel();
				}
			});

			alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();

			break;
		default:
			break;
		}
	}

	private void setPreferences(int posicao, String imput, boolean inativar, boolean sucesso) {
		SharedPreferences pref = getSharedPreferences(Variloid.PREFERENCIAS, MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(VariloidForm4.FORM4_CAMPO + posicao, imput);
		editor.putBoolean(VariloidForm4.FORM4_IMAGEM + posicao, sucesso);
		editor.putBoolean(VariloidForm4.FORM4_INATIVAR + posicao, inativar);
		editor.commit();
		myAdapter.notifyDataSetInvalidated();
	}

}
