package br.ufg.iptsp.app.variloid;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
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
import br.ufg.iptsp.app.variloid.adapter.MyAdapterForm2;
import br.ufg.iptsp.app.variloid.provider.Data;
import br.ufg.iptsp.app.variloid.servico.ServicoConexao;
import br.ufg.iptsp.app.variloid.util.Mask;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class Formulario2Activity extends BaseActivity implements OnItemClickListener, LocationListener{
	
	private LayoutInflater layoutInflater;
	private MyAdapterForm2 myAdapter;
	LocationManager locationManager;
	private Button buttonTirarFotoLesao;
	private Uri imageUri;
	private double latitude;
	private double longitude;
	private List<Integer> list;
	private boolean isPendente;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_formulario);
		
		ServicoConexao.verificaTipoConexao(this);
		getSupportActionBar().setTitle(getString(R.string.formulario_dois));
		layoutInflater = getLayoutInflater();
		
		list = new ArrayList<Integer>();
		for (Integer integer : VariloidForm2.campos) {
			list.add(integer);
		}
		
		for (String strings : VariloidForm2.idCampos) {
			
			if (Data.mapFormularioDois.size() == 0) {
				Data.mapFormularioDois.add(Data.FORM2_KEY.concat(strings), "");
			}

			if (Data.formularioDois.getListInativar().size() != VariloidForm2.idCampos.length)
				Data.formularioDois.getListInativar().add(false);
			if (Data.formularioDois.getListSucesso().size() != VariloidForm2.idCampos.length)
				Data.formularioDois.getListSucesso().add(false);
		}
		
		
		View layoutFooter = layoutInflater.inflate(R.layout.button_layout, null);
		
		buttonTirarFotoLesao = (Button) layoutFooter.findViewById(R.id.button_tirar_foto_lesao);
		buttonTirarFotoLesao.setVisibility(View.VISIBLE);
		
		if(Data.mapFormularioDois.get(Data.FORM2_KEY.concat(Variloid.FORM_FOTO_LESAO_COLETADA))==null){
			buttonTirarFotoLesao.setText(getString(R.string.tirar_foto_lesao));
		}else{
			buttonTirarFotoLesao.setText(getString(R.string.tirar_foto_lesao) + "(Imagem Carregada)");
		}
		
		buttonTirarFotoLesao.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(Data.mapFormularioDois.get(Data.FORM2_KEY.concat(Variloid.FORM_FOTO_LESAO_COLETADA))==null){
					createDialog(1);
				}else{
				
					final AlertDialog.Builder alert = new AlertDialog.Builder(Formulario2Activity.this);
					alert.setTitle(getString(R.string.formulario_alerta));
					alert.setMessage(getString(R.string.formulario_imagem_carregada));
					alert.setPositiveButton("Sim",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							createDialog(1);
						}
					});

					alert.setNegativeButton("Não",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.cancel();
						}
					});
					alert.show();
				}	
			}
		});

		Button button = (Button) layoutFooter.findViewById(R.id.button_avancar_finalizar);
		button.setVisibility(View.VISIBLE);
		button.setText(getString(R.string.formulario_tres));
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				int count = 0;
				for (int i = 0; i < Data.formularioDois.getListSucesso().size(); i++) {
					if(Data.formularioDois.getListSucesso().get(i)){
						count++;
					}
				}
				
				if(count > 0){
					
					final AlertDialog.Builder alert = new AlertDialog.Builder(Formulario2Activity.this);
					alert.setTitle(getString(R.string.formulario_alerta));
					alert.setMessage(getString(
							R.string.formulario_campos_nao_preenchidos)
							.concat(" ")
							.concat(String.valueOf(Data.formularioDois.getListSucesso().size() - count))
							.concat(" ")
							.concat(getString(R.string.formulario_deseja_continuar)));
					alert.setPositiveButton("Sim",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							
							Data.mapService.add(Data.FORM2_KEY.concat("latitude"), String.valueOf(latitude));
							Data.mapService.add(Data.FORM2_KEY.concat("longitude"), String.valueOf(longitude));
							Data.mapService.add("faixaEtaria", getString(R.string.faixa_etaria1));
							Intent intent = new Intent(Formulario2Activity.this, Formulario3Activity.class);
							intent.putExtra("isPendente", isPendente);
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
				}else{
					
					Data.mapService.add(Data.FORM2_KEY.concat("latitude"), String.valueOf(latitude));
					Data.mapService.add(Data.FORM2_KEY.concat("longitude"), String.valueOf(longitude));
					Data.mapService.add("faixaEtaria", getString(R.string.faixa_etaria1));
					Intent intent = new Intent(Formulario2Activity.this, Formulario3Activity.class);
					intent.putExtra("isPendente", isPendente);
					startActivity(intent);
				}
			}
		});
		
		ListView listView = (ListView) findViewById(R.id.listView);
		listView.addFooterView(layoutFooter);
		myAdapter = new MyAdapterForm2(Formulario2Activity.this, R.layout.adapter_formulario, list);
		listView.setAdapter(myAdapter);
		listView.setOnItemClickListener(this);
	}
	
	protected void createDialog(int dialogId) {
		switch (dialogId) {
		case 1:
			try {
				Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
				File photoLesao = new File(
						Environment.getExternalStorageDirectory(), "PicLesaoColetada.jpg");	
				i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoLesao));
				i.putExtra("data", photoLesao);
				imageUri = Uri.fromFile(photoLesao);
				startActivityForResult(i,1);

			} catch (Exception e) {
				Log.w("IMAGE_CAPTURE", "Falha ao acessar o arquivo onde a imagem seria salva.", e);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				try {
					if(TextUtils.isEmpty(Data.mapService.get(Data.FORM2_KEY.concat(Variloid.FORM_FOTO_LESAO_COLETADA)).toString())){
						Data.mapService.add(Data.FORM2_KEY.concat(Variloid.FORM_FOTO_LESAO_COLETADA), getPathFromURI(imageUri));
					}else{
						Data.mapService.set(Data.FORM2_KEY.concat(Variloid.FORM_FOTO_LESAO_COLETADA), getPathFromURI(imageUri));						
					}
					buttonTirarFotoLesao.setText(getString(R.string.tirar_foto_lesao) + "(Imagem Carregada)");
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
					Formulario2Activity.this);
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
	public void onItemClick(AdapterView<?> arg0, final View view, final int arg2, long arg3) {
		
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(Formulario2Activity.this);
		
		switch (arg2) {
		case VariloidForm2.NOME:
			final EditText inputNome = new EditText(Formulario2Activity.this);
			
			if (!TextUtils.isEmpty(Data.formularioDois.getNomeCrianca()))
				inputNome.setText(Data.formularioDois.getNomeCrianca());
			
			alert.setTitle(getString(R.string.formulario2_nome));
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
		case VariloidForm2.SEXO:
			
			View layout = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton1 = (RadioButton) layout.findViewById(R.id.radio1);
			radioButton1.setText(getString(R.string.formulario2_opcao_masculino));
			final RadioButton radioButton2 = (RadioButton) layout.findViewById(R.id.radio2);
			radioButton2.setText(getString(R.string.formulario2_opcao_feminino));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getSexo())){
				
				if(Data.formularioDois.getSexo().equalsIgnoreCase(getString(R.string.formulario2_opcao_masculino))){
					radioButton1.setChecked(true);
				}else if(Data.formularioDois.getSexo().equalsIgnoreCase(getString(R.string.formulario2_opcao_feminino))){
					radioButton2.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_sexo));
			alert.setView(layout);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton1.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_masculino), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton2.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_feminino), false, true);
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
		case VariloidForm2.DATA_NASCIMENTO:
			final EditText dataNascimento = new EditText(Formulario2Activity.this);
			dataNascimento.setInputType(InputType.TYPE_CLASS_DATETIME);
			dataNascimento.addTextChangedListener(Mask.insert(
					"##/##/####", dataNascimento));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getDataNascimento())){
				String replaceCampo = Data.formularioDois.getDataNascimento().replaceAll("/", "");
				dataNascimento.setText(replaceCampo);	
			}
			
			alert.setTitle(getString(R.string.formulario2_data));
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
		case VariloidForm2.JA_TEVE_CATAPORA:
			View layout2 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton3 = (RadioButton) layout2.findViewById(R.id.radio1);
			radioButton3.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton4 = (RadioButton) layout2.findViewById(R.id.radio2);
			radioButton4.setText(getString(R.string.formulario2_opcao_nao));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getCriancaCataporaAnt())){
				
				if(Data.formularioDois.getCriancaCataporaAnt().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton3.setChecked(true);
				}else if(Data.formularioDois.getCriancaCataporaAnt().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton4.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_ja_teve_catapora));
			alert.setView(layout2);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton3.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton4.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
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
			
		case VariloidForm2.CONTATO_COM_QUEM_JA_TEVE:
			
			View layout3 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton5 = (RadioButton) layout3.findViewById(R.id.radio1);
			radioButton5.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton6 = (RadioButton) layout3.findViewById(R.id.radio2);
			radioButton6.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton7 = (RadioButton) layout3.findViewById(R.id.radio3);
			radioButton7.setVisibility(View.VISIBLE);
			radioButton7.setText(getString(R.string.formulario2_opcao_ignorado));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getContatoPessoaCatapora())){
				
				if(Data.formularioDois.getContatoPessoaCatapora().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton5.setChecked(true);
				}else if(Data.formularioDois.getContatoPessoaCatapora().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton6.setChecked(true);
				}else if(Data.formularioDois.getContatoPessoaCatapora().equalsIgnoreCase(getString(R.string.formulario2_opcao_ignorado))){
					radioButton7.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_contato_com_ja_teve_catapora));
			alert.setView(layout3);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton5.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton6.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton7.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_ignorado), false, true);
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
			
		case VariloidForm2.LOCAL_CONTATO:
			View layout4 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			
			final EditText outros1 = (EditText) layout4.findViewById(R.id.edittext_gruporadio);
			int maxLength2 = 90;    
			outros1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength2)});
			
			final EditText outros2 = (EditText) layout4.findViewById(R.id.edittext_botton);
			int maxLength1 = 90;    
			outros2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength1)});
			
			final RadioButton radioButton8 = (RadioButton) layout4.findViewById(R.id.radio1);
			radioButton8.setText(getString(R.string.formulario2_opcao_cmei));
			radioButton8.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
						outros1.setVisibility(View.VISIBLE);
					}else{
						outros1.setVisibility(View.GONE);
					}
				}
			});
			
			final RadioButton radioButton9 = (RadioButton) layout4.findViewById(R.id.radio2);
			radioButton9.setText(getString(R.string.formulario2_opcao_comunidade));
			final RadioButton radioButton10 = (RadioButton) layout4.findViewById(R.id.radio3);
			radioButton10.setText(getString(R.string.formulario2_opcao_domicilio));
			radioButton10.setVisibility(View.VISIBLE);
			final RadioButton radioButton11 = (RadioButton) layout4.findViewById(R.id.radio4);
			radioButton11.setVisibility(View.VISIBLE);
			radioButton11.setText(getString(R.string.formulario2_opcao_hospital));
			final RadioButton radioButton12 = (RadioButton) layout4.findViewById(R.id.radio5);
			radioButton12.setVisibility(View.VISIBLE);
			radioButton12.setText(getString(R.string.formulario2_opcao_cais_ubs));
			final RadioButton radioButton13 = (RadioButton) layout4.findViewById(R.id.radio6);
			radioButton13.setVisibility(View.VISIBLE);
			radioButton13.setText(getString(R.string.formulario2_opcao_nao_se_aplica));
			final RadioButton radioButton14 = (RadioButton) layout4.findViewById(R.id.radio7);
			radioButton14.setVisibility(View.VISIBLE);
			radioButton14.setText(getString(R.string.formulario2_opcao_outro));
			radioButton14.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
						outros2.setVisibility(View.VISIBLE);
					}else{
						outros2.setVisibility(View.GONE);
					}
				}
			});
			
			
			if(!TextUtils.isEmpty(Data.formularioDois.getLocalContato())){
				if(Data.formularioDois.getLocalContato().indexOf(getString(R.string.formulario2_opcao_cmei))!=-1){
					radioButton8.setChecked(true);
					int posicao = Data.formularioDois.getLocalContato().indexOf(getString(R.string.formulario2_opcao_cmei));
					outros1.setText(Data.formularioDois.getLocalContato().substring(posicao + getString(R.string.formulario2_opcao_cmei).length(), Data.formularioDois.getLocalContato().length()));
				}else if(Data.formularioDois.getLocalContato().equalsIgnoreCase(getString(R.string.formulario2_opcao_comunidade))){
					radioButton9.setChecked(true);
				}else if(Data.formularioDois.getLocalContato().equalsIgnoreCase(getString(R.string.formulario2_opcao_domicilio))){
					radioButton10.setChecked(true);
				}else if(Data.formularioDois.getLocalContato().equalsIgnoreCase(getString(R.string.formulario2_opcao_hospital))){
					radioButton11.setChecked(true);
				}else if(Data.formularioDois.getLocalContato().equalsIgnoreCase(getString(R.string.formulario2_opcao_cais_ubs))){
					radioButton12.setChecked(true);
				}else if(Data.formularioDois.getLocalContato().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_se_aplica))){
					radioButton13.setChecked(true);
				}else if(Data.formularioDois.getLocalContato().indexOf(getString(R.string.formulario2_opcao_outro))!=-1){
					radioButton14.setChecked(true);
					int posicao = Data.formularioDois.getLocalContato().indexOf(getString(R.string.formulario2_opcao_outro));
					outros2.setText(Data.formularioDois.getLocalContato().substring(posicao + getString(R.string.formulario2_opcao_outro).length(), Data.formularioDois.getLocalContato().length()));
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_local_contato));
			alert.setView(layout4);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String valor = null;
					if(radioButton8.isChecked()){
						valor = getString(R.string.formulario2_opcao_cmei) + outros1.getText().toString();
						setPreferences(arg2, valor, false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton9.isChecked()){
						valor = getString(R.string.formulario2_opcao_comunidade);
						setPreferences(arg2, valor, false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton10.isChecked()){
						valor = getString(R.string.formulario2_opcao_domicilio);
						setPreferences(arg2, valor, false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton11.isChecked()){
						valor = getString(R.string.formulario2_opcao_hospital);
						setPreferences(arg2, valor, false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton12.isChecked()){
						valor = getString(R.string.formulario2_opcao_cais_ubs);
						setPreferences(arg2, valor, false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton13.isChecked()){
						valor = getString(R.string.formulario2_opcao_nao_se_aplica);
						setPreferences(arg2, valor, false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton14.isChecked()){
						valor = getString(R.string.formulario2_opcao_outro) + outros2.getText().toString();
						setPreferences(arg2, valor, false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}
					else{
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
		case VariloidForm2.RESIDENCIA:
			
			View layout7 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton16 = (RadioButton) layout7.findViewById(R.id.radio1);
			radioButton16.setText(getString(R.string.formulario2_opcao_urbana));
			final RadioButton radioButton17 = (RadioButton) layout7.findViewById(R.id.radio2);
			radioButton17.setText(getString(R.string.formulario2_opcao_nao_urbana)); 
			
			if(!TextUtils.isEmpty(Data.formularioDois.getResidencia())){
				
				if(Data.formularioDois.getResidencia().equalsIgnoreCase(getString(R.string.formulario2_opcao_urbana))){
					radioButton16.setChecked(true);
				}else if(Data.formularioDois.getResidencia().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_urbana))){
					radioButton17.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_residencia));
			alert.setView(layout7);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
					if(radioButton16.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_urbana), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton17.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_urbana), false,  true);
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
		case VariloidForm2.TIPO_LOGRADOURO:
			
			View layout8 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			
			final EditText outros3 = (EditText) layout8.findViewById(R.id.edittext_botton);
			int maxLength3 = 10;    
			outros3.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength3)});
			
			final RadioButton radioButton18 = (RadioButton) layout8.findViewById(R.id.radio1);
			radioButton18.setText(getString(R.string.formulario2_opcao_avenida));
			final RadioButton radioButton19 = (RadioButton) layout8.findViewById(R.id.radio2);
			radioButton19.setText(getString(R.string.formulario2_opcao_rua));
			final RadioButton radioButton20 = (RadioButton) layout8.findViewById(R.id.radio3);
			radioButton20.setVisibility(View.VISIBLE);
			radioButton20.setText(getString(R.string.formulario2_opcao_alameda));
			final RadioButton radioButton21 = (RadioButton) layout8.findViewById(R.id.radio4);
			radioButton21.setVisibility(View.VISIBLE);
			radioButton21.setText(getString(R.string.formulario2_opcao_rodovia));
			final RadioButton radioButton22 = (RadioButton) layout8.findViewById(R.id.radio5);
			radioButton22.setVisibility(View.VISIBLE);
			radioButton22.setText(getString(R.string.formulario2_opcao_travessa));
			final RadioButton radioButton23 = (RadioButton) layout8.findViewById(R.id.radio6);
			radioButton23.setVisibility(View.VISIBLE);
			radioButton23.setText(getString(R.string.formulario2_opcao_outro));
			radioButton23.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						outros3.setVisibility(View.VISIBLE);
					}else{
						outros3.setVisibility(View.GONE);
					}
				}
			});
			
			if(!TextUtils.isEmpty(Data.formularioDois.getTipoLogradouro())){
				if(Data.formularioDois.getTipoLogradouro().equalsIgnoreCase(getString(R.string.formulario2_opcao_avenida))){
					radioButton18.setChecked(true);
				}else if(Data.formularioDois.getTipoLogradouro().equalsIgnoreCase(getString(R.string.formulario2_opcao_rua))){
					radioButton19.setChecked(true);
				}else if(Data.formularioDois.getTipoLogradouro().equalsIgnoreCase(getString(R.string.formulario2_opcao_alameda))){
					radioButton20.setChecked(true);
				}else if(Data.formularioDois.getTipoLogradouro().equalsIgnoreCase(getString(R.string.formulario2_opcao_rodovia))){
					radioButton21.setChecked(true);
				}else if(Data.formularioDois.getTipoLogradouro().equalsIgnoreCase(getString(R.string.formulario2_opcao_travessa))){
					radioButton22.setChecked(true);
				}else if(Data.formularioDois.getTipoLogradouro().split(",")[0].equalsIgnoreCase(getString(R.string.formulario2_opcao_outro))){
					radioButton23.setChecked(true);
					outros3.setText(Data.formularioDois.getTipoLogradouro().split(",")[1]);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_tipo_logradouro));
			alert.setView(layout8);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					if(radioButton18.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_avenida), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton19.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_rua) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton20.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_alameda) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton21.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_rodovia) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton22.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_travessa) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton23.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_outro) + "," + outros3.getText().toString() , false, true);
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
		case VariloidForm2.NOME_LOGRADOURO:
			final EditText inputNomeLogradouro = new EditText(Formulario2Activity.this);
			int maxLength4 = 90;    
			inputNomeLogradouro.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength4)});
			
			if(!TextUtils.isEmpty(Data.formularioDois.getNomeLogradouro()))inputNomeLogradouro.setText(Data.formularioDois.getNomeLogradouro());
			
			alert.setTitle(getString(R.string.formulario2_nome_logradouro));
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
		case VariloidForm2.QUADRA:
			final EditText inputQuadra = new EditText(Formulario2Activity.this);
			int maxLength5 = 10;    
			inputQuadra.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength5)});
			
			if(!TextUtils.isEmpty(Data.formularioDois.getQuadra()))inputQuadra.setText(Data.formularioDois.getQuadra());
			
			alert.setTitle(getString(R.string.formulario2_qudra));
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
		case VariloidForm2.LOTE:
			final EditText inputLote = new EditText(Formulario2Activity.this);
			int maxLength6 = 8;    
			inputLote.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength6)});
			
			if(!TextUtils.isEmpty(Data.formularioDois.getLote()))inputLote.setText(Data.formularioDois.getLote());
			
			alert.setTitle(getString(R.string.formulario2_lote));
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
		case VariloidForm2.NUMERO:
			final EditText inputNumero = new EditText(Formulario2Activity.this);
			int maxLength7 = 8;    
			inputNumero.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength7)});
			inputNumero.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(Data.formularioDois.getNumero()))inputNumero.setText(Data.formularioDois.getNumero());
			
			alert.setTitle(getString(R.string.formulario2_numero));
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
		case VariloidForm2.COMPLEMENTO:
			final EditText inputComplemento = new EditText(Formulario2Activity.this);
			int maxLength8 = 20;    
			inputComplemento.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength8)});
			
			if(!TextUtils.isEmpty(Data.formularioDois.getComplemento()))inputComplemento.setText(Data.formularioDois.getComplemento());
			
			alert.setTitle(getString(R.string.formulario2_complemento));
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
		case VariloidForm2.BAIRRO:
			final EditText inputBairro = new EditText(Formulario2Activity.this);
			int maxLength9 = 50;    
			inputBairro.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength9)});
			
			if(!TextUtils.isEmpty(Data.formularioDois.getBairro()))inputBairro.setText(Data.formularioDois.getBairro());
			
			alert.setTitle(getString(R.string.formulario2_bairro));
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
		case VariloidForm2.RASH:
			View layout9 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton24 = (RadioButton) layout9.findViewById(R.id.radio1);
			radioButton24.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton25 = (RadioButton) layout9.findViewById(R.id.radio2);
			radioButton25.setText(getString(R.string.formulario2_opcao_nao));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getErupcaoDifusaAgudo())){
				
				if(Data.formularioDois.getErupcaoDifusaAgudo().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton24.setChecked(true);
				}else if(Data.formularioDois.getErupcaoDifusaAgudo().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton25.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_rash));
			alert.setView(layout9);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton24.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						
						for (int i = VariloidForm2.TCLE; i < VariloidForm2.campos.length; i++) {
							setPreferences(i, "", false,  false);
						}
						for (int i = VariloidForm2.TCLE; i < VariloidForm2.campos.length; i++) {
							myAdapter.updateItens(i, VariloidForm2.campos[i]);
						}
						myAdapter.notifyDataSetChanged();
						
					}else if(radioButton25.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						
						for (int i = VariloidForm2.TCLE; i < VariloidForm2.campos.length; i++) {
							setPreferences(i, "", true,  false);
						}
						for (int i = VariloidForm2.TCLE; i < VariloidForm2.campos.length; i++) {
							myAdapter.updateItens(i, VariloidForm2.campos[i]);
						}
						myAdapter.notifyDataSetChanged();
						
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
						
						for (int i = VariloidForm2.TCLE; i < VariloidForm2.campos.length; i++) {
							setPreferences(i, "", false,  false);
						}
						for (int i = VariloidForm2.TCLE; i < VariloidForm2.campos.length; i++) {
							myAdapter.updateItens(i, VariloidForm2.campos[i]);
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
		case VariloidForm2.TCLE:
			View layout10 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton26 = (RadioButton) layout10.findViewById(R.id.radio1);
			radioButton26.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton27 = (RadioButton) layout10.findViewById(R.id.radio2);
			radioButton27.setText(getString(R.string.formulario2_opcao_nao));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getTcleAssinado())){
				if(Data.formularioDois.getTcleAssinado().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton26.setChecked(true);
				}else if(Data.formularioDois.getTcleAssinado().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton27.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_tcle));
			alert.setView(layout10);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton26.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						
						for (int i = VariloidForm2.NOME_MAE; i < VariloidForm2.campos.length; i++) {
							setPreferences(i, "", false,  false);
						}
						for (int i = VariloidForm2.NOME_MAE; i < VariloidForm2.campos.length; i++) {
							myAdapter.updateItens(i, VariloidForm2.campos[i]);
						}
						myAdapter.notifyDataSetChanged();
						
					}else if(radioButton27.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						
						for (int i = VariloidForm2.NOME_MAE; i < VariloidForm2.campos.length; i++) {
							setPreferences(i, "", true,  false);
						}
						for (int i = VariloidForm2.NOME_MAE; i < VariloidForm2.campos.length; i++) {
							myAdapter.updateItens(i, VariloidForm2.campos[i]);
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
		case VariloidForm2.NOME_MAE:
			final EditText inputNomeMae = new EditText(Formulario2Activity.this);
			int maxLength10 = 90;    
			inputNomeMae.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength10)});
			
			if (!TextUtils.isEmpty(Data.formularioDois.getNomeMae()))
				inputNomeMae.setText(Data.formularioDois.getNomeMae());
			
			alert.setTitle(getString(R.string.formulario2_nome_mae));
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
		case VariloidForm2.IDADE_MAE:
			final EditText inputIdadeMae = new EditText(Formulario2Activity.this);
			int maxLength11 = 2;    
			inputIdadeMae.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength11)});
			inputIdadeMae.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if(!TextUtils.isEmpty(Data.formularioDois.getIdadeMae()))inputIdadeMae.setText(Data.formularioDois.getIdadeMae());
			
			alert.setTitle(getString(R.string.formulario2_idade_mae));
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
		case VariloidForm2.NOME_PAI:
			final EditText inputNomePai = new EditText(Formulario2Activity.this);
			int maxLength12 = 90;    
			inputNomePai.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength12)});
			
			if (!TextUtils.isEmpty(Data.formularioDois.getNomePai()))
				inputNomePai.setText(Data.formularioDois.getNomePai());
			
			alert.setTitle(getString(R.string.formulario2_nome_pai));
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
		case VariloidForm2.TELEFONE:
			final EditText inputTelFixo = new EditText(Formulario2Activity.this);
			int maxLength13 = 20;    
			inputTelFixo.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength13)});
			inputTelFixo.setInputType(InputType.TYPE_CLASS_PHONE);
			
			if (!TextUtils.isEmpty(Data.formularioDois.getTelefoneFixo()))
				inputTelFixo.setText(Data.formularioDois.getTelefoneFixo());
			
			alert.setTitle(getString(R.string.formulario2_tel_fixo));
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
		case VariloidForm2.CELULAR1:
			final EditText NumeroCelular1 = new EditText(Formulario2Activity.this);
			int maxLengthCel1 = 20;    
			NumeroCelular1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthCel1)});
			NumeroCelular1.setInputType(InputType.TYPE_CLASS_PHONE);
			
			if (!TextUtils.isEmpty(Data.formularioDois.getCelular1()))
				NumeroCelular1.setText(Data.formularioDois.getCelular1());
			
			alert.setTitle(getString(R.string.formulario2_tel_celular1));
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
		case VariloidForm2.OPER_CELULAR1:
			View layout11 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			
			final RadioButton radioButton28 = (RadioButton) layout11.findViewById(R.id.radio1);
			radioButton28.setText(getString(R.string.formulario2_operadoras_tim));
			final RadioButton radioButton29 = (RadioButton) layout11.findViewById(R.id.radio2);
			radioButton29.setText(getString(R.string.formulario2_operadoras_claro));
			final RadioButton radioButton30 = (RadioButton) layout11.findViewById(R.id.radio3);
			radioButton30.setVisibility(View.VISIBLE);
			radioButton30.setText(getString(R.string.formulario2_operadoras_oi));
			final RadioButton radioButton31 = (RadioButton) layout11.findViewById(R.id.radio4);
			radioButton31.setVisibility(View.VISIBLE);
			radioButton31.setText(getString(R.string.formulario2_operadoras_vivo));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getOperadoraCelular1())){
				
				if(Data.formularioDois.getOperadoraCelular1().indexOf(getString(R.string.formulario2_operadoras_tim))!=-1){
					radioButton28.setChecked(true);
				}else if(Data.formularioDois.getOperadoraCelular1().indexOf(getString(R.string.formulario2_operadoras_claro))!=-1){
					radioButton29.setChecked(true);
				}else if(Data.formularioDois.getOperadoraCelular1().indexOf(getString(R.string.formulario2_operadoras_oi))!=-1){
					radioButton30.setChecked(true);
				}else if(Data.formularioDois.getOperadoraCelular1().indexOf(getString(R.string.formulario2_operadoras_vivo))!=-1){
					radioButton31.setChecked(true);
				}
			}
					
			alert.setTitle(getString(R.string.formulario2_operadora1));
			alert.setView(layout11);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int whichButton) {
					
					if(radioButton28.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_operadoras_tim) , false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton29.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_operadoras_claro) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton30.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_operadoras_oi) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton31.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_operadoras_vivo) , false, true);
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
		case VariloidForm2.CELULAR2:
			final EditText NumeroCelular2 = new EditText(Formulario2Activity.this);
			int maxLengthCel2 = 20;    
			NumeroCelular2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthCel2)});
			NumeroCelular2.setInputType(InputType.TYPE_CLASS_PHONE);
			
			if (!TextUtils.isEmpty(Data.formularioDois.getCelular2()))
				NumeroCelular2.setText(Data.formularioDois.getCelular2());
			
			alert.setTitle(getString(R.string.formulario2_tel_celular2));
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
		case VariloidForm2.OPER_CELULAR2:
			View layout12 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			
			final RadioButton radioButton32 = (RadioButton) layout12.findViewById(R.id.radio1);
			radioButton32.setText(getString(R.string.formulario2_operadoras_tim));
			final RadioButton radioButton33 = (RadioButton) layout12.findViewById(R.id.radio2);
			radioButton33.setText(getString(R.string.formulario2_operadoras_claro));
			final RadioButton radioButton34 = (RadioButton) layout12.findViewById(R.id.radio3);
			radioButton34.setVisibility(View.VISIBLE);
			radioButton34.setText(getString(R.string.formulario2_operadoras_oi));
			final RadioButton radioButton35 = (RadioButton) layout12.findViewById(R.id.radio4);
			radioButton35.setVisibility(View.VISIBLE);
			radioButton35.setText(getString(R.string.formulario2_operadoras_vivo));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getOperadoraCelular2())){
				
				if(Data.formularioDois.getOperadoraCelular2().indexOf(getString(R.string.formulario2_operadoras_tim))!=-1){
					radioButton32.setChecked(true);
				}else if(Data.formularioDois.getOperadoraCelular2().indexOf(getString(R.string.formulario2_operadoras_claro))!=-1){
					radioButton33.setChecked(true);
				}else if(Data.formularioDois.getOperadoraCelular2().indexOf(getString(R.string.formulario2_operadoras_oi))!=-1){
					radioButton34.setChecked(true);
				}else if(Data.formularioDois.getOperadoraCelular2().indexOf(getString(R.string.formulario2_operadoras_vivo))!=-1){
					radioButton35.setChecked(true);
				}
			}
					
			alert.setTitle(getString(R.string.formulario2_operadora2));
			alert.setView(layout12);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
					if(radioButton32.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_operadoras_tim) , false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton33.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_operadoras_claro) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton34.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_operadoras_oi) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton35.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_operadoras_vivo) , false, true);
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
		case VariloidForm2.COBERTURA_PSF:
			View layout13 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton36 = (RadioButton) layout13.findViewById(R.id.radio1);
			radioButton36.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton37 = (RadioButton) layout13.findViewById(R.id.radio2);
			radioButton37.setText(getString(R.string.formulario2_opcao_nao));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getCriancaCadastradaPSF())){
				if(Data.formularioDois.getCriancaCadastradaPSF().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton36.setChecked(true);
				}else if(Data.formularioDois.getCriancaCadastradaPSF().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton37.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_cobertura_psf));
			alert.setView(layout13);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton36.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						
					}else if(radioButton37.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
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
		case VariloidForm2.DT_INICIO_VARICELA:
			final EditText dataInicioVar = new EditText(Formulario2Activity.this);
			dataInicioVar.setInputType(InputType.TYPE_CLASS_DATETIME);
			dataInicioVar.addTextChangedListener(Mask.insert(
					"##/##/####", dataInicioVar));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getDataInicioCatapora())){
				String replaceCampo = Data.formularioDois.getDataInicioCatapora().replaceAll("/", "");
				dataInicioVar.setText(replaceCampo);
			}
			
			alert.setTitle(getString(R.string.formulario2_data_inicio_varicela));
			alert.setView(dataInicioVar);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(dataInicioVar.getText().toString())){
						setPreferences(arg2, dataInicioVar.getText().toString(), false,  true);
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
		case VariloidForm2.FEBRE:
			View layout14 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton38 = (RadioButton) layout14.findViewById(R.id.radio1);
			radioButton38.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton39 = (RadioButton) layout14.findViewById(R.id.radio2);
			radioButton39.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton40 = (RadioButton) layout14.findViewById(R.id.radio3);
			radioButton40.setVisibility(View.VISIBLE);
			radioButton40.setText(getString(R.string.formulario2_opcao_ignorado));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getFebre())){
				
				if(Data.formularioDois.getFebre().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton38.setChecked(true);
				}else if(Data.formularioDois.getFebre().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton39.setChecked(true);
				}else if(Data.formularioDois.getFebre().equalsIgnoreCase(getString(R.string.formulario2_opcao_ignorado))){
					radioButton40.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_febre));
			alert.setView(layout14);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton38.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton39.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton40.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_ignorado), false, true);
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
		case VariloidForm2.ANOREXIA:
			View layout15 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton41 = (RadioButton) layout15.findViewById(R.id.radio1);
			radioButton41.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton42 = (RadioButton) layout15.findViewById(R.id.radio2);
			radioButton42.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton43 = (RadioButton) layout15.findViewById(R.id.radio3);
			radioButton43.setVisibility(View.VISIBLE);
			radioButton43.setText(getString(R.string.formulario2_opcao_ignorado));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getAnorexia())){
				
				if(Data.formularioDois.getAnorexia().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton41.setChecked(true);
				}else if(Data.formularioDois.getAnorexia().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton42.setChecked(true);
				}else if(Data.formularioDois.getAnorexia().equalsIgnoreCase(getString(R.string.formulario2_opcao_ignorado))){
					radioButton43.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_anorexia));
			alert.setView(layout15);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton41.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton42.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton43.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_ignorado), false, true);
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
		case VariloidForm2.MALESTAR:
			View layout16 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton44 = (RadioButton) layout16.findViewById(R.id.radio1);
			radioButton44.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton45 = (RadioButton) layout16.findViewById(R.id.radio2);
			radioButton45.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton46 = (RadioButton) layout16.findViewById(R.id.radio3);
			radioButton46.setVisibility(View.VISIBLE);
			radioButton46.setText(getString(R.string.formulario2_opcao_ignorado));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getMalEstar())){
				
				if(Data.formularioDois.getMalEstar().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton44.setChecked(true);
				}else if(Data.formularioDois.getMalEstar().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton45.setChecked(true);
				}else if(Data.formularioDois.getMalEstar().equalsIgnoreCase(getString(R.string.formulario2_opcao_ignorado))){
					radioButton46.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_malestar));
			alert.setView(layout16);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton44.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton45.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton46.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_ignorado), false, true);
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
		case VariloidForm2.DOR_ABDOMINAL:
			View layout17 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton47 = (RadioButton) layout17.findViewById(R.id.radio1);
			radioButton47.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton48 = (RadioButton) layout17.findViewById(R.id.radio2);
			radioButton48.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton49 = (RadioButton) layout17.findViewById(R.id.radio3);
			radioButton49.setVisibility(View.VISIBLE);
			radioButton49.setText(getString(R.string.formulario2_opcao_ignorado));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getDorAbdominal())){
				
				if(Data.formularioDois.getDorAbdominal().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton47.setChecked(true);
				}else if(Data.formularioDois.getDorAbdominal().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton48.setChecked(true);
				}else if(Data.formularioDois.getDorAbdominal().equalsIgnoreCase(getString(R.string.formulario2_opcao_ignorado))){
					radioButton49.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_dor_abdominal));
			alert.setView(layout17);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton47.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton48.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton49.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_ignorado), false, true);
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
		case VariloidForm2.DT_RASH:
			final EditText dataRash = new EditText(Formulario2Activity.this);
			dataRash.setInputType(InputType.TYPE_CLASS_DATETIME);
			dataRash.addTextChangedListener(Mask.insert(
					"##/##/####", dataRash));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getDataInicioRash())){
				String replaceCampo = Data.formularioDois.getDataInicioRash().replaceAll("/", "");
				dataRash.setText(replaceCampo);
			}
			
			alert.setTitle(getString(R.string.formulario2_dt_rash));
			alert.setView(dataRash);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(dataRash.getText().toString())){
						setPreferences(arg2, dataRash.getText().toString(), false,  true);
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
		case VariloidForm2.LOC_RASH:
			View layout18 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton50 = (RadioButton) layout18.findViewById(R.id.radio1);
			radioButton50.setText(getString(R.string.formulario2_opcao_generalizado));
			final RadioButton radioButton51 = (RadioButton) layout18.findViewById(R.id.radio2);
			radioButton51.setText(getString(R.string.formulario2_opcao_focal));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getLocalizacaoRash()))
			if(Data.formularioDois.getLocalizacaoRash().equalsIgnoreCase(getString(R.string.formulario2_opcao_generalizado))){
				radioButton50.setChecked(true);
			}else if(Data.formularioDois.getLocalizacaoRash().equalsIgnoreCase(getString(R.string.formulario2_opcao_focal))){
				radioButton51.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario2_loc_rash));
			alert.setView(layout18);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton50.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_generalizado), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton51.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_focal), false, true);
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
		case VariloidForm2.LOC_RASH2:
			View layout19= layoutInflater.inflate(R.layout.checkbox_formulario, null);
			
			layout19.findViewById(R.id.checkBox6).setVisibility(View.GONE);
			layout19.findViewById(R.id.checkBox7).setVisibility(View.GONE);
			layout19.findViewById(R.id.checkBox8).setVisibility(View.GONE);
			layout19.findViewById(R.id.checkBox9).setVisibility(View.GONE);
			layout19.findViewById(R.id.checkBox10).setVisibility(View.GONE);
			layout19.findViewById(R.id.checkBox11).setVisibility(View.GONE);
			layout19.findViewById(R.id.checkBox12).setVisibility(View.GONE);
			layout19.findViewById(R.id.checkBox13).setVisibility(View.GONE);
			layout19.findViewById(R.id.checkBox14).setVisibility(View.GONE);
			layout19.findViewById(R.id.checkBox15).setVisibility(View.GONE);
			layout19.findViewById(R.id.checkBox16).setVisibility(View.GONE);
			
			final CheckBox checkBox52 = (CheckBox) layout19.findViewById(R.id.checkBox1);
			checkBox52.setText(getString(R.string.formulario2_opcao_face_cabeca));
			final CheckBox checkBox53 = (CheckBox) layout19.findViewById(R.id.checkBox2);
			checkBox53.setText(getString(R.string.formulario2_opcao_torax));
			final CheckBox checkBox54 = (CheckBox) layout19.findViewById(R.id.checkBox3);
			checkBox54.setText(getString(R.string.formulario2_opcao_bracos));
			final CheckBox checkBox55 = (CheckBox) layout19.findViewById(R.id.checkBox4);
			checkBox55.setText(getString(R.string.formulario2_opcao_pernas));
			final CheckBox checkBox56 = (CheckBox) layout19.findViewById(R.id.checkBox5);
			checkBox56.setText(getString(R.string.formulario2_opcao_cavidade_oral));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getPrimeiralocalizacaoRash()))
			if(Data.formularioDois.getPrimeiralocalizacaoRash().indexOf(getString(R.string.formulario2_opcao_face_cabeca))!=-1){
				checkBox52.setChecked(true);
			}else if(Data.formularioDois.getPrimeiralocalizacaoRash().indexOf(getString(R.string.formulario2_opcao_torax))!=-1){
				checkBox53.setChecked(true);
			}else if(Data.formularioDois.getPrimeiralocalizacaoRash().indexOf(getString(R.string.formulario2_opcao_bracos))!=-1){
				checkBox54.setChecked(true);
			}else if(Data.formularioDois.getPrimeiralocalizacaoRash().indexOf(getString(R.string.formulario2_opcao_pernas))!=-1){
				checkBox55.setChecked(true);
			}else if(Data.formularioDois.getPrimeiralocalizacaoRash().indexOf(getString(R.string.formulario2_opcao_cavidade_oral))!=-1){
				checkBox56.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario2_loc_rash2));
			alert.setView(layout19);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				List<String> lisStrings = new ArrayList<String>();
				String valor;
				
				public void onClick(DialogInterface dialog, int whichButton) {
					
					if(checkBox52.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_face_cabeca));
					}
					if(checkBox53.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_torax));
					}
					if(checkBox54.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_bracos));
					}
					if(checkBox55.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_pernas));
					}
					if(checkBox56.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_cavidade_oral));
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
		case VariloidForm2.N_LESOES:
			View layout20= layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton57 = (RadioButton) layout20.findViewById(R.id.radio1);
			radioButton57.setText(getString(R.string.formulario2_opcao_menor_cinquenta));
			final RadioButton radioButton58 = (RadioButton) layout20.findViewById(R.id.radio2);
			radioButton58.setText(getString(R.string.formulario2_opcao_cinquenta_a_noventa_e_nove));
			final RadioButton radioButton59 = (RadioButton) layout20.findViewById(R.id.radio3);
			radioButton59.setVisibility(View.VISIBLE);
			radioButton59.setText(getString(R.string.formulario2_opcao_cem_a_duzentos_e_quarenta_e_nove));
			final RadioButton radioButton60 = (RadioButton) layout20.findViewById(R.id.radio4);
			radioButton60.setVisibility(View.VISIBLE);
			radioButton60.setText(getString(R.string.formulario2_opcao_duzentos_e_cinquenta_a_quatrocentos_e_noventa_e_nove));
			final RadioButton radioButton61 = (RadioButton) layout20.findViewById(R.id.radio5);
			radioButton61.setVisibility(View.VISIBLE);
			radioButton61.setText(getString(R.string.formulario2_opcao_maior_que_quinhentos));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getNumLesoes())){
				
				if(Data.formularioDois.getNumLesoes().equalsIgnoreCase(getString(R.string.formulario2_opcao_menor_cinquenta))){
					radioButton57.setChecked(true);
				}else if(Data.formularioDois.getNumLesoes().equalsIgnoreCase(getString(R.string.formulario2_opcao_cinquenta_a_noventa_e_nove))){
					radioButton58.setChecked(true);
				}else if(Data.formularioDois.getNumLesoes().equalsIgnoreCase(getString(R.string.formulario2_opcao_cem_a_duzentos_e_quarenta_e_nove))){
					radioButton59.setChecked(true);
				}else if(Data.formularioDois.getNumLesoes().equalsIgnoreCase(getString(R.string.formulario2_opcao_duzentos_e_cinquenta_a_quatrocentos_e_noventa_e_nove))){
					radioButton60.setChecked(true);
				}else if(Data.formularioDois.getNumLesoes().equalsIgnoreCase(getString(R.string.formulario2_opcao_maior_que_quinhentos))){
					radioButton61.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_n_lesoes));
			alert.setView(layout20);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton57.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_menor_cinquenta), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton58.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_cinquenta_a_noventa_e_nove), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton59.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_cem_a_duzentos_e_quarenta_e_nove), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton60.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_duzentos_e_cinquenta_a_quatrocentos_e_noventa_e_nove), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton61.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_maior_que_quinhentos), false, true);
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
		case VariloidForm2.RASH_MP:
			View layout21 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton62 = (RadioButton) layout21.findViewById(R.id.radio1);
			radioButton62.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton63 = (RadioButton) layout21.findViewById(R.id.radio2);
			radioButton63.setText(getString(R.string.formulario2_opcao_nao));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getMaculoPapular())){
				
				if(Data.formularioDois.getMaculoPapular().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton62.setChecked(true);
				}else if(Data.formularioDois.getMaculoPapular().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton63.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_rash_mp));
			alert.setView(layout21);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton62.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton63.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
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
		case VariloidForm2.RASH_V:
			View layout22 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton64 = (RadioButton) layout22.findViewById(R.id.radio1);
			radioButton64.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton65 = (RadioButton) layout22.findViewById(R.id.radio2);
			radioButton65.setText(getString(R.string.formulario2_opcao_nao));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getVesicular())){
				
				if(Data.formularioDois.getVesicular().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton64.setChecked(true);
				}else if(Data.formularioDois.getVesicular().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton65.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_rash_v));
			alert.setView(layout22);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton64.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton65.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
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
		case VariloidForm2.RASH_C:
			View layout23 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton66 = (RadioButton) layout23.findViewById(R.id.radio1);
			radioButton66.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton67 = (RadioButton) layout23.findViewById(R.id.radio2);
			radioButton67.setText(getString(R.string.formulario2_opcao_nao));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getCrosta())){
				
				if(Data.formularioDois.getCrosta().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton66.setChecked(true);
				}else if(Data.formularioDois.getCrosta().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton67.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_rash_c));
			alert.setView(layout23);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton66.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton67.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
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
		case VariloidForm2.LESOES:
			View layout24 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton68 = (RadioButton) layout24.findViewById(R.id.radio1);
			radioButton68.setText(getString(R.string.formulario2_opcao_maculo_papular));
			final RadioButton radioButton69 = (RadioButton) layout24.findViewById(R.id.radio2);
			radioButton69.setText(getString(R.string.formulario2_opcao_vesicular));
			final RadioButton radioButton70 = (RadioButton) layout24.findViewById(R.id.radio3);
			radioButton70.setText(getString(R.string.formulario2_opcao_crosta));
			radioButton70.setVisibility(View.VISIBLE);
			
			if(!TextUtils.isEmpty(Data.formularioDois.getMaioriaLesoes())){
				
				if(Data.formularioDois.getMaioriaLesoes().equalsIgnoreCase(getString(R.string.formulario2_opcao_maculo_papular))){
					radioButton68.setChecked(true);
				}else if(Data.formularioDois.getMaioriaLesoes().equalsIgnoreCase(getString(R.string.formulario2_opcao_vesicular))){
					radioButton69.setChecked(true);
				}else if(Data.formularioDois.getMaioriaLesoes().equalsIgnoreCase(getString(R.string.formulario2_opcao_crosta))){
					radioButton70.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_lesoes));
			alert.setView(layout24);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton68.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_maculo_papular), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton69.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_vesicular), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton70.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_crosta), false, true);
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
		case VariloidForm2.DIAG_QUEM:
			View layout25 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton71 = (RadioButton) layout25.findViewById(R.id.radio1);
			radioButton71.setText(getString(R.string.formulario2_opcao_medico));
			final RadioButton radioButton72 = (RadioButton) layout25.findViewById(R.id.radio2);
			radioButton72.setText(getString(R.string.formulario2_opcao_outro_profissional_de_saude));
			final RadioButton radioButton73 = (RadioButton) layout25.findViewById(R.id.radio3);
			radioButton73.setText(getString(R.string.formulario2_opcao_professora_monitora));
			radioButton73.setVisibility(View.VISIBLE);
			final RadioButton radioButton74 = (RadioButton) layout25.findViewById(R.id.radio4);
			radioButton74.setText(getString(R.string.formulario2_opcao_pais_parentes));
			radioButton74.setVisibility(View.VISIBLE);
			final RadioButton radioButton75 = (RadioButton) layout25.findViewById(R.id.radio5);
			radioButton75.setText(getString(R.string.formulario2_opcao_amigos_outros));
			radioButton75.setVisibility(View.VISIBLE);
			
			if(!TextUtils.isEmpty(Data.formularioDois.getDiagnosticoFeitoPor())){
				
				if(Data.formularioDois.getDiagnosticoFeitoPor().equalsIgnoreCase(getString(R.string.formulario2_opcao_medico))){
					radioButton71.setChecked(true);
				}else if(Data.formularioDois.getDiagnosticoFeitoPor().equalsIgnoreCase(getString(R.string.formulario2_opcao_outro_profissional_de_saude))){
					radioButton72.setChecked(true);
				}else if(Data.formularioDois.getDiagnosticoFeitoPor().equalsIgnoreCase(getString(R.string.formulario2_opcao_professora_monitora))){
					radioButton73.setChecked(true);
				}else if(Data.formularioDois.getDiagnosticoFeitoPor().equalsIgnoreCase(getString(R.string.formulario2_opcao_pais_parentes))){
					radioButton74.setChecked(true);
				}else if(Data.formularioDois.getDiagnosticoFeitoPor().equalsIgnoreCase(getString(R.string.formulario2_opcao_amigos_outros))){
					radioButton75.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_diag_quem));
			alert.setView(layout25);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton71.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_medico), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton72.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_outro_profissional_de_saude), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton73.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_professora_monitora), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton74.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_pais_parentes), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton75.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_amigos_outros), false, true);
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
		case VariloidForm2.PESONASCER:
			final EditText pesoNascer = new EditText(Formulario2Activity.this);
			int maxLength16 = 4;    
			pesoNascer.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength16)});
			pesoNascer.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if (!TextUtils.isEmpty(Data.formularioDois
					.getPesoNascimentoGramas()))
				pesoNascer.setText(Data.formularioDois
						.getPesoNascimentoGramas());
			
			alert.setTitle(getString(R.string.formulario2_peso_nascer));
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
		case VariloidForm2.IDADEGESTACIONAL:
			final EditText idadeGestacional = new EditText(Formulario2Activity.this);
			int maxLength17 = 3;    
			idadeGestacional.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength17)});
			idadeGestacional.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if (!TextUtils.isEmpty(Data.formularioDois.getIdadeGestacional()))
				idadeGestacional.setText(Data.formularioDois
						.getIdadeGestacional());
			
			alert.setTitle(getString(R.string.formulario2_idade_gestacional));
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
		case VariloidForm2.RACA:
			View layout26 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton76 = (RadioButton) layout26.findViewById(R.id.radio1);
			radioButton76.setText(getString(R.string.formulario2_opcao_branca));
			final RadioButton radioButton77 = (RadioButton) layout26.findViewById(R.id.radio2);
			radioButton77.setText(getString(R.string.formulario2_opcao_parda));
			final RadioButton radioButton78 = (RadioButton) layout26.findViewById(R.id.radio3);
			radioButton78.setText(getString(R.string.formulario2_opcao_negra));
			radioButton78.setVisibility(View.VISIBLE);
			final RadioButton radioButton79 = (RadioButton) layout26.findViewById(R.id.radio4);
			radioButton79.setText(getString(R.string.formulario2_opcao_asiatica));
			radioButton79.setVisibility(View.VISIBLE);
			final RadioButton radioButton80 = (RadioButton) layout26.findViewById(R.id.radio5);
			radioButton80.setText(getString(R.string.formulario2_opcao_indigena));
			radioButton80.setVisibility(View.VISIBLE);
			final RadioButton radioButton200 = (RadioButton) layout26.findViewById(R.id.radio6);
			radioButton200.setText(getString(R.string.formulario2_opcao_ignorado));
			radioButton200.setVisibility(View.VISIBLE);
			
			if(!TextUtils.isEmpty(Data.formularioDois.getRaca())){
				
				if(Data.formularioDois.getRaca().equalsIgnoreCase(getString(R.string.formulario2_opcao_branca))){
					radioButton76.setChecked(true);
				}else if(Data.formularioDois.getRaca().equalsIgnoreCase(getString(R.string.formulario2_opcao_parda))){
					radioButton77.setChecked(true);
				}else if(Data.formularioDois.getRaca().equalsIgnoreCase(getString(R.string.formulario2_opcao_negra))){
					radioButton78.setChecked(true);
				}else if(Data.formularioDois.getRaca().equalsIgnoreCase(getString(R.string.formulario2_opcao_asiatica))){
					radioButton79.setChecked(true);
				}else if(Data.formularioDois.getRaca().equalsIgnoreCase(getString(R.string.formulario2_opcao_indigena))){
					radioButton80.setChecked(true);
				}else if(Data.formularioDois.getRaca().equalsIgnoreCase(getString(R.string.formulario2_opcao_ignorado))){
					radioButton200.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_raca));
			alert.setView(layout26);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton76.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_branca), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton77.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_parda), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton78.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_negra), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton79.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_asiatica), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton80.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_indigena), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton200.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_ignorado), false, true);
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
		case VariloidForm2.VACVARICELA:
			View layout27 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton81 = (RadioButton) layout27.findViewById(R.id.radio1);
			radioButton81.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton82 = (RadioButton) layout27.findViewById(R.id.radio2);
			radioButton82.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton83 = (RadioButton) layout27.findViewById(R.id.radio3);
			radioButton83.setVisibility(View.VISIBLE);
			radioButton83.setText(getString(R.string.formulario2_opcao_nao_sabe_e_nao_tem_cartao));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getCriancaRecebeuVacinaCatapora())){
				
				if(Data.formularioDois.getCriancaRecebeuVacinaCatapora().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton81.setChecked(true);
				}else if(Data.formularioDois.getCriancaRecebeuVacinaCatapora().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton82.setChecked(true);
				}else if(Data.formularioDois.getCriancaRecebeuVacinaCatapora().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe_e_nao_tem_cartao))){
					radioButton83.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_vac_varicela));
			alert.setView(layout27);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton81.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton82.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton83.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe_e_nao_tem_cartao), false, true);
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
		case VariloidForm2.MOTIVO_NAO_VACINACAO:
			View layout28 = layoutInflater.inflate(R.layout.checkbox_formulario, null);
			final EditText editText = (EditText) layout28.findViewById(R.id.edit_text);
			int maxLengthEd = 90;    
			editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthEd)});
			
			final CheckBox checkbox1 = (CheckBox) layout28.findViewById(R.id.checkBox1);
			checkbox1.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao1));
			final CheckBox checkbox2 = (CheckBox) layout28.findViewById(R.id.checkBox2);
			checkbox2.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao2));
			final CheckBox checkbox3 = (CheckBox) layout28.findViewById(R.id.checkBox3);
			checkbox3.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao3));
			final CheckBox checkbox4 = (CheckBox) layout28.findViewById(R.id.checkBox4);
			checkbox4.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao4));
			final CheckBox checkbox5 = (CheckBox) layout28.findViewById(R.id.checkBox5);
			checkbox5.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao5));
			final CheckBox checkbox6 = (CheckBox) layout28.findViewById(R.id.checkBox6);
			checkbox6.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao6));
			final CheckBox checkbox7 = (CheckBox) layout28.findViewById(R.id.checkBox7);
			checkbox7.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao7));
			final CheckBox checkbox8 = (CheckBox) layout28.findViewById(R.id.checkBox8);
			checkbox8.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao8));
			final CheckBox checkbox9 = (CheckBox) layout28.findViewById(R.id.checkBox9);
			checkbox9.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao9));
			final CheckBox checkbox10 = (CheckBox) layout28.findViewById(R.id.checkBox10);
			checkbox10.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao10));
			final CheckBox checkbox11 = (CheckBox) layout28.findViewById(R.id.checkBox11);
			checkbox11.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao11));
			final CheckBox checkbox12 = (CheckBox) layout28.findViewById(R.id.checkBox12);
			checkbox12.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao12));
			final CheckBox checkbox13 = (CheckBox) layout28.findViewById(R.id.checkBox13);
			checkbox13.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao13));
			final CheckBox checkbox14 = (CheckBox) layout28.findViewById(R.id.checkBox14);
			checkbox14.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao14));
			final CheckBox checkbox15 = (CheckBox) layout28.findViewById(R.id.checkBox15);
			checkbox15.setText(getString(R.string.formulario2_opcao_motivo_nao_vacinacao15));
			final CheckBox checkbox16 = (CheckBox) layout28.findViewById(R.id.checkBox16);
			checkbox16.setText(getString(R.string.formulario2_opcao_outros));
			checkbox16.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						editText.setVisibility(View.VISIBLE);
					}else{
						editText.setVisibility(View.GONE);
						editText.setText("");
						editText.setHint(getString(R.string.formulario2_opcao_outros));
					}
				}
			});
							
			if(!TextUtils.isEmpty(Data.formularioDois.getRazoesNaoVacinacao())){			
				if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao1))!=-1){
					checkbox1.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao2))!=-1){
					checkbox2.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao3))!=-1){
					checkbox3.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao4))!=-1){
					checkbox4.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao5))!=-1){
					checkbox5.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao6))!=-1){
					checkbox6.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao7))!=-1){
					checkbox7.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao8))!=-1){
					checkbox8.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao9))!=-1){
					checkbox9.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao10))!=-1){
					checkbox10.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao11))!=-1){
					checkbox11.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao12))!=-1){
					checkbox12.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao13))!=-1){
					checkbox13.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao14))!=-1){
					checkbox14.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao15))!=-1){
					checkbox15.setChecked(true);
				}if(Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_outro_check))!=-1){
					checkbox16.setChecked(true);
					int posicao = Data.formularioDois.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_outro_check));
					editText.setText(Data.formularioDois.getRazoesNaoVacinacao().substring(posicao + getString(R.string.formulario2_opcao_outro_check).length(), Data.formularioDois.getRazoesNaoVacinacao().length()));					
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_motivo_nao_vacinacao));
			alert.setView(layout28);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				List<String> lisStrings = new ArrayList<String>();
				String valor;
				public void onClick(DialogInterface dialog, int whichButton) {
					if(checkbox1.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao1));
					}
					if(checkbox2.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao2));
					}
					if(checkbox3.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao3));
					}
					if(checkbox4.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao4));
					}
					if(checkbox5.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao5));
					}
					if(checkbox6.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao6));
					}
					if(checkbox7.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao7));
					}
					if(checkbox8.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao8));
					}
					if(checkbox9.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao9));
					}
					if(checkbox10.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao10));
					}
					if(checkbox11.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao11));
					}
					if(checkbox12.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao12));
					}
					if(checkbox13.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao13));
					}
					if(checkbox14.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao14));
					}
					if(checkbox15.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_motivo_nao_vacinacao15));
					}
					if(checkbox16.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_outro_check) + editText.getText().toString());
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
		case VariloidForm2.N_DOSES_VARIC:
			final EditText inputNDosesVaric = new EditText(Formulario2Activity.this);
			int maxLength18 = 2;    
			inputNDosesVaric.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength18)});
			inputNDosesVaric.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if (!TextUtils.isEmpty(Data.formularioDois
					.getNumDosesRecebidasContraTripliceViral()))
				inputNDosesVaric.setText(Data.formularioDois
						.getNumDosesRecebidasContraTripliceViral());
			
			alert.setTitle(getString(R.string.formulario2_n_doses_varic));
			alert.setView(inputNDosesVaric);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputNDosesVaric.getText().toString())){
						if(inputNDosesVaric.getText().toString().equalsIgnoreCase("0")){
							setPreferences(arg2, inputNDosesVaric.getText().toString(), false,  true);
							((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
							
							for (int i = VariloidForm2.VAC_VARIC_DT_DOSE1; i < VariloidForm2.TITULO_VACINA_TRIPLICE; i++) {
								setPreferences(i, "", true,  false);
							}
							for (int i = VariloidForm2.TCLE; i < VariloidForm2.campos.length; i++) {
								myAdapter.updateItens(i, VariloidForm2.campos[i]);
							}
							myAdapter.notifyDataSetChanged();	
						}else{
							setPreferences(arg2, inputNDosesVaric.getText().toString(), false,  true);
							((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
							
							for (int i = VariloidForm2.VAC_VARIC_DT_DOSE1; i < VariloidForm2.TITULO_VACINA_TRIPLICE; i++) {
								setPreferences(i, "", false,  false);
							}
							for (int i = VariloidForm2.TCLE; i < VariloidForm2.campos.length; i++) {
								myAdapter.updateItens(i, VariloidForm2.campos[i]);
							}
							myAdapter.notifyDataSetChanged();	
						}
						
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
						
						for (int i = VariloidForm2.VAC_VARIC_DT_DOSE1; i < VariloidForm2.TITULO_VACINA_TRIPLICE; i++) {
							setPreferences(i, "", false,  false);
						}
						for (int i = VariloidForm2.TCLE; i < VariloidForm2.campos.length; i++) {
							myAdapter.updateItens(i, VariloidForm2.campos[i]);
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
		case VariloidForm2.VAC_VARIC_DT_DOSE1:
			final EditText vacVaricDtDoses1 = new EditText(Formulario2Activity.this);
			vacVaricDtDoses1.setInputType(InputType.TYPE_CLASS_DATETIME);
			vacVaricDtDoses1.addTextChangedListener(Mask.insert(
					"##/##/####", vacVaricDtDoses1));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getDataPrimeiraDose())){
				String replaceCampo = Data.formularioDois.getDataPrimeiraDose().replaceAll("/", "");
				vacVaricDtDoses1.setText(replaceCampo);
			}
			
			alert.setTitle(getString(R.string.formulario2_vac_varic_dt_dose1));
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
		case VariloidForm2.VAC_VARIC_QUAL_DOSE1:
			View layout29 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton84 = (RadioButton) layout29.findViewById(R.id.radio1);
			radioButton84.setText(getString(R.string.formulario2_opcao_vacina_tetraviral));
			final RadioButton radioButton85 = (RadioButton) layout29.findViewById(R.id.radio2);
			radioButton85.setText(getString(R.string.formulario2_opcao_vacina_glaxo_varilrix));
			final RadioButton radioButton86 = (RadioButton) layout29.findViewById(R.id.radio3);
			radioButton86.setVisibility(View.VISIBLE);
			radioButton86.setText(getString(R.string.formulario2_opcao_vacina_merck_varivax));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getQualVacinaUsadaPrimeiraDose())){
				
				if(Data.formularioDois.getQualVacinaUsadaPrimeiraDose().equalsIgnoreCase(getString(R.string.formulario2_opcao_vacina_tetraviral))){
					radioButton84.setChecked(true);
				}else if(Data.formularioDois.getQualVacinaUsadaPrimeiraDose().equalsIgnoreCase(getString(R.string.formulario2_opcao_vacina_glaxo_varilrix))){
					radioButton85.setChecked(true);
				}else if(Data.formularioDois.getQualVacinaUsadaPrimeiraDose().equalsIgnoreCase(getString(R.string.formulario2_opcao_vacina_merck_varivax))){
					radioButton86.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_vac_varic_qual_dose1));
			alert.setView(layout29);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton84.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_vacina_tetraviral), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton85.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_vacina_glaxo_varilrix), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton86.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_vacina_merck_varivax), false, true);
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
		case VariloidForm2.VAC_VARIC_DT_DOSE2:
			final EditText vacVaricDtDoses2 = new EditText(Formulario2Activity.this);
			vacVaricDtDoses2.setInputType(InputType.TYPE_CLASS_DATETIME);
			vacVaricDtDoses2.addTextChangedListener(Mask.insert(
					"##/##/####", vacVaricDtDoses2));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getDataSegundaDose())){
				String replaceCampo = Data.formularioDois.getDataSegundaDose().replaceAll("/", "");
				vacVaricDtDoses2.setText(replaceCampo);
			}
			
			alert.setTitle(getString(R.string.formulario2_vac_varic_dt_dose2));
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
		case VariloidForm2.VAC_VARIC_QUAL_DOSE2:
			View layout30 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton87 = (RadioButton) layout30.findViewById(R.id.radio1);
			radioButton87.setText(getString(R.string.formulario2_opcao_vacina_tetraviral));
			final RadioButton radioButton88 = (RadioButton) layout30.findViewById(R.id.radio2);
			radioButton88.setText(getString(R.string.formulario2_opcao_vacina_glaxo_varilrix));
			final RadioButton radioButton89 = (RadioButton) layout30.findViewById(R.id.radio3);
			radioButton89.setVisibility(View.VISIBLE);
			radioButton89.setText(getString(R.string.formulario2_opcao_vacina_merck_varivax));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getQualVacinaUsadaSegundaDose())){
				
				if(Data.formularioDois.getQualVacinaUsadaSegundaDose().equalsIgnoreCase(getString(R.string.formulario2_opcao_vacina_tetraviral))){
					radioButton87.setChecked(true);
				}else if(Data.formularioDois.getQualVacinaUsadaSegundaDose().equalsIgnoreCase(getString(R.string.formulario2_opcao_vacina_glaxo_varilrix))){
					radioButton88.setChecked(true);
				}else if(Data.formularioDois.getQualVacinaUsadaSegundaDose().equalsIgnoreCase(getString(R.string.formulario2_opcao_vacina_merck_varivax))){
					radioButton89.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_vac_varic_qual_dose2));
			alert.setView(layout30);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton87.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_vacina_tetraviral), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton88.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_vacina_glaxo_varilrix), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton89.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_vacina_merck_varivax), false, true);
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
		case VariloidForm2.VACTRIPLICE:
			
			View layout272 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton812 = (RadioButton) layout272.findViewById(R.id.radio1);
			radioButton812.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton822 = (RadioButton) layout272.findViewById(R.id.radio2);
			radioButton822.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton832 = (RadioButton) layout272.findViewById(R.id.radio3);
			radioButton832.setVisibility(View.VISIBLE);
			radioButton832.setText(getString(R.string.formulario2_opcao_ignorado));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getCriancaRecebeuVacinaTripliceViral())){
				
				if(Data.formularioDois.getCriancaRecebeuVacinaTripliceViral().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton812.setChecked(true);
				}else if(Data.formularioDois.getCriancaRecebeuVacinaTripliceViral().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton822.setChecked(true);
				}else if(Data.formularioDois.getCriancaRecebeuVacinaTripliceViral().equalsIgnoreCase(getString(R.string.formulario2_opcao_ignorado))){
					radioButton832.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_vac_triplice_viral));
			alert.setView(layout272);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton812.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton822.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton832.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_ignorado), false, true);
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
		case VariloidForm2.N_DOSES_TRIPLICE:
			
			final EditText inputNDosesTriplice = new EditText(Formulario2Activity.this);
			int maxLength181 = 2;    
			inputNDosesTriplice.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength181)});
			inputNDosesTriplice.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if (!TextUtils.isEmpty(Data.formularioDois
					.getNumDosesRecebidasContraTripliceViral()))
				inputNDosesTriplice.setText(Data.formularioDois
						.getNumDosesRecebidasContraTripliceViral());
			
			alert.setTitle(getString(R.string.formulario2_n_doses_triplice_viral));
			alert.setView(inputNDosesTriplice);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(inputNDosesTriplice.getText().toString())){
						setPreferences(arg2, inputNDosesTriplice.getText().toString(), false,  true);
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
		case VariloidForm2.MMR1:
			final EditText dataMmr1 = new EditText(Formulario2Activity.this);
			dataMmr1.setInputType(InputType.TYPE_CLASS_DATETIME);
			dataMmr1.addTextChangedListener(Mask.insert(
					"##/##/####", dataMmr1));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getDataPrimeiraDoseMMR())){
				String replaceCampo = Data.formularioDois.getDataPrimeiraDoseMMR().replaceAll("/", "");
				dataMmr1.setText(replaceCampo);
			}
			
			alert.setTitle(getString(R.string.formulario2_mmr1));
			alert.setView(dataMmr1);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(dataMmr1.getText().toString())){
						setPreferences(arg2, dataMmr1.getText().toString(), false,  true);
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
		case VariloidForm2.MMR2:
			final EditText dataMmr2 = new EditText(Formulario2Activity.this);
			dataMmr2.setInputType(InputType.TYPE_CLASS_DATETIME);
			dataMmr2.addTextChangedListener(Mask.insert(
					"##/##/####", dataMmr2));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getDataSegundaDoseMMR())){
				String replaceCampo = Data.formularioDois.getDataSegundaDoseMMR().replaceAll("/", "");
				dataMmr2.setText(replaceCampo);
			}
			
			alert.setTitle(getString(R.string.formulario2_mmr2));
			alert.setView(dataMmr2);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(dataMmr2.getText().toString())){
						setPreferences(arg2, dataMmr2.getText().toString(), false,  true);
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
		case VariloidForm2.FREQCRECHE1:
			View layout31 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton90 = (RadioButton) layout31.findViewById(R.id.radio1);
			radioButton90.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton91 = (RadioButton) layout31.findViewById(R.id.radio2);
			radioButton91.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton92 = (RadioButton) layout31.findViewById(R.id.radio3);
			radioButton92.setVisibility(View.VISIBLE);
			radioButton92.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getAlguemFrequentouCreche())){
				
				if(Data.formularioDois.getAlguemFrequentouCreche().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton90.setChecked(true);
				}else if(Data.formularioDois.getAlguemFrequentouCreche().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton91.setChecked(true);
				}else if(Data.formularioDois.getAlguemFrequentouCreche().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton92.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_freq_creche1));
			alert.setView(layout31);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton90.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						setPreferences(VariloidForm2.FREQCRECHE2, "", false,  false);
						myAdapter.notifyDataSetChanged();
					}else if(radioButton91.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						setPreferences(VariloidForm2.FREQCRECHE2, "", true,  false);
						myAdapter.notifyDataSetChanged();
					}else if(radioButton92.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						setPreferences(VariloidForm2.FREQCRECHE2, "", false,  false);
						myAdapter.notifyDataSetChanged();
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
						setPreferences(VariloidForm2.FREQCRECHE2, "", false,  false);
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
		case VariloidForm2.FREQCRECHE2:
			
			View layout32 = layoutInflater.inflate(R.layout.checkbox_formulario, null);
			
			final CheckBox checkbox31 = (CheckBox) layout32.findViewById(R.id.checkBox1);
			checkbox31.setText(getString(R.string.formulario2_opcao_crianca_com_catapora));
			final CheckBox checkbox32 = (CheckBox) layout32.findViewById(R.id.checkBox2);
			checkbox32.setText(getString(R.string.formulario2_opcao_pais_outra_crianca_menos_que_dez_anos));
			final CheckBox checkbox33 = (CheckBox) layout32.findViewById(R.id.checkBox3);
			checkbox33.setText(getString(R.string.formulario2_opcao_familia_trabalha_creche));
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
			
			if(!TextUtils.isEmpty(Data.formularioDois.getQuemFamiliaFrequentouCreche())){
				if(Data.formularioDois.getQuemFamiliaFrequentouCreche().indexOf(getString(R.string.formulario2_opcao_crianca_com_catapora))!=-1){
					checkbox31.setChecked(true);
				}if(Data.formularioDois.getQuemFamiliaFrequentouCreche().indexOf(getString(R.string.formulario2_opcao_pais_outra_crianca_menos_que_dez_anos))!=-1){
					checkbox32.setChecked(true);
				}if(Data.formularioDois.getQuemFamiliaFrequentouCreche().indexOf(getString(R.string.formulario2_opcao_familia_trabalha_creche))!=-1){
					checkbox33.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_freq_creche2));
			alert.setView(layout32);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				List<String> lisStrings = new ArrayList<String>();
				String valor;
				public void onClick(DialogInterface dialog, int whichButton) {
					if(checkbox31.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_crianca_com_catapora));
					}
					if(checkbox32.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_pais_outra_crianca_menos_que_dez_anos));
					}
					if(checkbox33.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_familia_trabalha_creche));
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
		case VariloidForm2.FREQSERVSAUDE1:
			View layout33 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton96 = (RadioButton) layout33.findViewById(R.id.radio1);
			radioButton96.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton97 = (RadioButton) layout33.findViewById(R.id.radio2);
			radioButton97.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton98 = (RadioButton) layout33.findViewById(R.id.radio3);
			radioButton98.setVisibility(View.VISIBLE);
			radioButton98.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getAlguemFrequentouServicoSaude()))
			if(Data.formularioDois.getAlguemFrequentouServicoSaude().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
				radioButton96.setChecked(true);
			}else if(Data.formularioDois.getAlguemFrequentouServicoSaude().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
				radioButton97.setChecked(true);
			}else if(Data.formularioDois.getAlguemFrequentouServicoSaude().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
				radioButton98.setChecked(true);
			}
			
			alert.setTitle(getString(R.string.formulario2_freq_serv_saude1));
			alert.setView(layout33);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton96.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						setPreferences(VariloidForm2.FREQSERVSAUDE2, "", false,  false);
						myAdapter.notifyDataSetChanged();
					}else if(radioButton97.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						setPreferences(VariloidForm2.FREQSERVSAUDE2, "", true,  false);
						myAdapter.notifyDataSetChanged();
					}else if(radioButton98.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						setPreferences(VariloidForm2.FREQSERVSAUDE2, "", false,  false);
						myAdapter.notifyDataSetChanged();
					}else{
						setPreferences(arg2, "", false, false);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
						setPreferences(VariloidForm2.FREQSERVSAUDE2, "", false,  false);
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
		case VariloidForm2.FREQSERVSAUDE2:
			
			View layout34 = layoutInflater.inflate(R.layout.checkbox_formulario, null);
			
			final CheckBox checkbox311 = (CheckBox) layout34.findViewById(R.id.checkBox1);
			checkbox311.setText(getString(R.string.formulario2_opcao_crianca_com_catapora));
			final CheckBox checkbox312 = (CheckBox) layout34.findViewById(R.id.checkBox2);
			checkbox312.setText(getString(R.string.formulario2_opcao_criancas_ate_dez_anos));
			final CheckBox checkbox313 = (CheckBox) layout34.findViewById(R.id.checkBox3);
			checkbox313.setText(getString(R.string.formulario2_opcao_familia_trabalha_em_instituicao_de_saude));
			final CheckBox checkbox314 = (CheckBox) layout34.findViewById(R.id.checkBox4);
			checkbox314.setVisibility(View.GONE);
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
			
			if(!TextUtils.isEmpty(Data.formularioDois.getQuemFamiliaFrequentouServicoSaude())){			
				if(Data.formularioDois.getQuemFamiliaFrequentouServicoSaude().indexOf(getString(R.string.formulario2_opcao_crianca_com_catapora))!=-1){
					checkbox311.setChecked(true);
				}if(Data.formularioDois.getQuemFamiliaFrequentouServicoSaude().indexOf(getString(R.string.formulario2_opcao_criancas_ate_dez_anos))!=-1){
					checkbox312.setChecked(true);
				}if(Data.formularioDois.getQuemFamiliaFrequentouServicoSaude().indexOf(getString(R.string.formulario2_opcao_familia_trabalha_em_instituicao_de_saude))!=-1){
					checkbox313.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_freq_serv_saude2));
			alert.setView(layout34);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				List<String> lisStrings = new ArrayList<String>();
				String valor;
				public void onClick(DialogInterface dialog, int whichButton) {
					if(checkbox311.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_crianca_com_catapora));
					}
					if(checkbox312.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_criancas_ate_dez_anos));
					}
					if(checkbox313.isChecked()){
						lisStrings.add(getString(R.string.formulario2_opcao_familia_trabalha_em_instituicao_de_saude));
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
		case VariloidForm2.HOSPITALIZACAO:
			View layout35 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton102 = (RadioButton) layout35.findViewById(R.id.radio1);
			radioButton102.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton103 = (RadioButton) layout35.findViewById(R.id.radio2);
			radioButton103.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton104 = (RadioButton) layout35.findViewById(R.id.radio3);
			radioButton104.setVisibility(View.VISIBLE);
			radioButton104.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getCriancaInternou())){
				
				if(Data.formularioDois.getCriancaInternou().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton102.setChecked(true);
				}else if(Data.formularioDois.getCriancaInternou().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton103.setChecked(true);
				}else if(Data.formularioDois.getCriancaInternou().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton104.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_hospitalizacao));
			alert.setView(layout35);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton102.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton103.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton104.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.MAE_TRABALHA:
			View layout36 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton105 = (RadioButton) layout36.findViewById(R.id.radio1);
			radioButton105.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton106 = (RadioButton) layout36.findViewById(R.id.radio2);
			radioButton106.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton107 = (RadioButton) layout36.findViewById(R.id.radio3);
			radioButton107.setVisibility(View.VISIBLE);
			radioButton107.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getMaeTrabalhaFora())){
				
				if(Data.formularioDois.getMaeTrabalhaFora().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton105.setChecked(true);
				}else if(Data.formularioDois.getMaeTrabalhaFora().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton106.setChecked(true);
				}else if(Data.formularioDois.getMaeTrabalhaFora().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton107.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_mae_trabalha));
			alert.setView(layout36);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton105.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton106.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton107.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.ES_CMAE:
			View layout37 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton108 = (RadioButton) layout37.findViewById(R.id.radio1);
			radioButton108.setText(getString(R.string.formulario2_opcao_nao_alfabetizada));
			final RadioButton radioButton109 = (RadioButton) layout37.findViewById(R.id.radio2);
			radioButton109.setText(getString(R.string.formulario2_opcao_ensino_fundamental));
			final RadioButton radioButton110 = (RadioButton) layout37.findViewById(R.id.radio3);
			radioButton110.setVisibility(View.VISIBLE);
			radioButton110.setText(getString(R.string.formulario2_opcao_ensino_medio));
			final RadioButton radioButton111 = (RadioButton) layout37.findViewById(R.id.radio4);
			radioButton111.setVisibility(View.VISIBLE);
			radioButton111.setText(getString(R.string.formulario2_opcao_ensino_superior));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getEscolaridadeMae())){
				
				if(Data.formularioDois.getEscolaridadeMae().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_alfabetizada))){
					radioButton108.setChecked(true);
				}else if(Data.formularioDois.getEscolaridadeMae().equalsIgnoreCase(getString(R.string.formulario2_opcao_ensino_fundamental))){
					radioButton109.setChecked(true);
				}else if(Data.formularioDois.getEscolaridadeMae().equalsIgnoreCase(getString(R.string.formulario2_opcao_ensino_medio))){
					radioButton110.setChecked(true);
				}else if(Data.formularioDois.getEscolaridadeMae().equalsIgnoreCase(getString(R.string.formulario2_opcao_ensino_superior))){
					radioButton111.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_esc_mae));
			alert.setView(layout37);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton108.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_alfabetizada), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton109.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_ensino_fundamental), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton110.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_ensino_medio), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton111.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_ensino_superior), false, true);
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
		case VariloidForm2.NCCAS_CASA5:
			final EditText numeroCriancas1 = new EditText(Formulario2Activity.this);
			int maxLength19 = 2;    
			numeroCriancas1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength19)});
			numeroCriancas1.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if (!TextUtils.isEmpty(Data.formularioDois
					.getNumCriancasMenos5anosDomicilio()))
				numeroCriancas1.setText(Data.formularioDois
						.getNumCriancasMenos5anosDomicilio());
			
			alert.setTitle(getString(R.string.formulario2_nc_cas_casa5));
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
		case VariloidForm2.NCCAS_CASA10:
			final EditText numeroCriancas2 = new EditText(Formulario2Activity.this);
			int maxLength20 = 2;    
			numeroCriancas2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength20)});
			numeroCriancas2.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if (!TextUtils.isEmpty(Data.formularioDois
					.getNumCriancasMenos10anosDomicilio()))
				numeroCriancas2.setText(Data.formularioDois
						.getNumCriancasMenos10anosDomicilio());
			
			alert.setTitle(getString(R.string.formulario2_nc_cas_casa10));
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
		case VariloidForm2.QT_DE_PESSOAS_CASA:
			final EditText qtdPessoasCasa = new EditText(Formulario2Activity.this);
			int maxLength21 = 2;    
			qtdPessoasCasa.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength21)});
			qtdPessoasCasa.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if (!TextUtils.isEmpty(Data.formularioDois.getQuantasPessoasCasa()))
				qtdPessoasCasa.setText(Data.formularioDois
						.getQuantasPessoasCasa());
			
			alert.setTitle(getString(R.string.formulario2_qtd_pessoas_casa));
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
		case VariloidForm2.QTD_EPESSOAS_QTO:
			final EditText qtdPessoasQuarto = new EditText(Formulario2Activity.this);
			int maxLength22 = 2;    
			qtdPessoasQuarto.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength22)});
			qtdPessoasQuarto.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if (!TextUtils.isEmpty(Data.formularioDois
					.getQuantasPessoasDormemMesmoComodoCrianca()))
				qtdPessoasQuarto.setText(Data.formularioDois
						.getQuantasPessoasDormemMesmoComodoCrianca());
			
			alert.setTitle(getString(R.string.formulario2_qtd_pessoas_quarto));
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
		case VariloidForm2.DOENCAFALCIFORME:
			View layout38 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton112 = (RadioButton) layout38.findViewById(R.id.radio1);
			radioButton112.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton113 = (RadioButton) layout38.findViewById(R.id.radio2);
			radioButton113.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton114 = (RadioButton) layout38.findViewById(R.id.radio3);
			radioButton114.setVisibility(View.VISIBLE);
			radioButton114.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if(!TextUtils.isEmpty(Data.formularioDois.getDoencaFalciforme())){
				
				if(Data.formularioDois.getDoencaFalciforme().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton112.setChecked(true);
				}else if(Data.formularioDois.getDoencaFalciforme().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton113.setChecked(true);
				}else if(Data.formularioDois.getDoencaFalciforme().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton114.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_doenca_falciforme));
			alert.setView(layout38);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton112.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton113.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton114.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.PREMATURIDADE:
			View layout39 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton115 = (RadioButton) layout39.findViewById(R.id.radio1);
			radioButton115.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton116 = (RadioButton) layout39.findViewById(R.id.radio2);
			radioButton116.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton117 = (RadioButton) layout39.findViewById(R.id.radio3);
			radioButton117.setVisibility(View.VISIBLE);
			radioButton117.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if (!TextUtils.isEmpty(Data.formularioDois.getPrematuridade())) {
				if (Data.formularioDois.getPrematuridade().equalsIgnoreCase(
						getString(R.string.formulario2_opcao_sim))) {
					radioButton115.setChecked(true);
				} else if (Data.formularioDois.getPrematuridade()
						.equalsIgnoreCase(
								getString(R.string.formulario2_opcao_nao))) {
					radioButton116.setChecked(true);
				} else if (Data.formularioDois.getPrematuridade()
						.equalsIgnoreCase(
								getString(R.string.formulario2_opcao_nao_sabe))) {
					radioButton117.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_prematuridade));
			alert.setView(layout39);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton115.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton116.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton117.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.INFECHIV:
			View layout40 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton118 = (RadioButton) layout40.findViewById(R.id.radio1);
			radioButton118.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton119 = (RadioButton) layout40.findViewById(R.id.radio2);
			radioButton119.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton120 = (RadioButton) layout40.findViewById(R.id.radio3);
			radioButton120.setVisibility(View.VISIBLE);
			radioButton120.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if (!TextUtils.isEmpty(Data.formularioDois.getInfeccaoHIVeAIDS())) {
				
				if(Data.formularioDois.getInfeccaoHIVeAIDS().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton118.setChecked(true);
				}else if(Data.formularioDois.getInfeccaoHIVeAIDS().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton119.setChecked(true);
				}else if(Data.formularioDois.getInfeccaoHIVeAIDS().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton120.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_infec_hiv));
			alert.setView(layout40);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton118.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton119.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton120.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.IMUNODEFICIENCIA:
			View layout41 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton121 = (RadioButton) layout41.findViewById(R.id.radio1);
			radioButton121.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton122 = (RadioButton) layout41.findViewById(R.id.radio2);
			radioButton122.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton123 = (RadioButton) layout41.findViewById(R.id.radio3);
			radioButton123.setVisibility(View.VISIBLE);
			radioButton123.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if (!TextUtils.isEmpty(Data.formularioDois.getImunodeficiencia())) {
				
				if(Data.formularioDois.getImunodeficiencia().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton121.setChecked(true);
				}else if(Data.formularioDois.getImunodeficiencia().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton122.setChecked(true);
				}else if(Data.formularioDois.getImunodeficiencia().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton123.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_imuno_deficiencia));
			alert.setView(layout41);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton121.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton122.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton123.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.ASMA:
			View layout42 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton124 = (RadioButton) layout42.findViewById(R.id.radio1);
			radioButton124.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton125 = (RadioButton) layout42.findViewById(R.id.radio2);
			radioButton125.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton126 = (RadioButton) layout42.findViewById(R.id.radio3);
			radioButton126.setVisibility(View.VISIBLE);
			radioButton126.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if (!TextUtils.isEmpty(Data.formularioDois.getAsma())) {
				
				if(Data.formularioDois.getAsma().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton124.setChecked(true);
				}else if(Data.formularioDois.getAsma().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton125.setChecked(true);
				}else if(Data.formularioDois.getAsma().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton126.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_asma));
			alert.setView(layout42);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton124.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton125.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton126.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.CORTICOIDE:
			View layout43 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton127 = (RadioButton) layout43.findViewById(R.id.radio1);
			radioButton127.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton128 = (RadioButton) layout43.findViewById(R.id.radio2);
			radioButton128.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton129 = (RadioButton) layout43.findViewById(R.id.radio3);
			radioButton129.setVisibility(View.VISIBLE);
			radioButton129.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if (!TextUtils.isEmpty(Data.formularioDois.getUsoCorticoideSistemico())) {
				
				if(Data.formularioDois.getUsoCorticoideSistemico().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton127.setChecked(true);
				}else if(Data.formularioDois.getUsoCorticoideSistemico().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton128.setChecked(true);
				}else if(Data.formularioDois.getUsoCorticoideSistemico().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton129.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_corticoide));
			alert.setView(layout43);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton127.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton128.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton129.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.CANCER:
			View layout44 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton130 = (RadioButton) layout44.findViewById(R.id.radio1);
			radioButton130.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton131 = (RadioButton) layout44.findViewById(R.id.radio2);
			radioButton131.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton132 = (RadioButton) layout44.findViewById(R.id.radio3);
			radioButton132.setVisibility(View.VISIBLE);
			radioButton132.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if (!TextUtils.isEmpty(Data.formularioDois.getCancer())) {
				
				if(Data.formularioDois.getCancer().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton130.setChecked(true);
				}else if(Data.formularioDois.getCancer().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton131.setChecked(true);
				}else if(Data.formularioDois.getCancer().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton132.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_cancer));
			alert.setView(layout44);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton130.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton131.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton132.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.DOENCAPULMONARCRONICA:
			View layout45 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton133 = (RadioButton) layout45.findViewById(R.id.radio1);
			radioButton133.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton134 = (RadioButton) layout45.findViewById(R.id.radio2);
			radioButton134.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton135 = (RadioButton) layout45.findViewById(R.id.radio3);
			radioButton135.setVisibility(View.VISIBLE);
			radioButton135.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if (!TextUtils.isEmpty(Data.formularioDois.getDoencaPulmonarCronica())) {
				
				if(Data.formularioDois.getDoencaPulmonarCronica().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton133.setChecked(true);
				}else if(Data.formularioDois.getDoencaPulmonarCronica().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton134.setChecked(true);
				}else if(Data.formularioDois.getDoencaPulmonarCronica().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton135.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_doenca_pulmonar_cronica));
			alert.setView(layout45);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton133.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton134.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton135.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.DOENCARENALCRONICA:
			View layout46 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton136 = (RadioButton) layout46.findViewById(R.id.radio1);
			radioButton136.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton137 = (RadioButton) layout46.findViewById(R.id.radio2);
			radioButton137.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton138 = (RadioButton) layout46.findViewById(R.id.radio3);
			radioButton138.setVisibility(View.VISIBLE);
			radioButton138.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if (!TextUtils.isEmpty(Data.formularioDois.getDoencaRenalCronica())) {
				
				if(Data.formularioDois.getDoencaRenalCronica().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton136.setChecked(true);
				}else if(Data.formularioDois.getDoencaRenalCronica().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton137.setChecked(true);
				}else if(Data.formularioDois.getDoencaRenalCronica().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton138.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_doenca_renal_cronica));
			alert.setView(layout46);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton136.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton137.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton138.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.DOENCACARDIACA:
			View layout47 = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton139 = (RadioButton) layout47.findViewById(R.id.radio1);
			radioButton139.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton140 = (RadioButton) layout47.findViewById(R.id.radio2);
			radioButton140.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton141 = (RadioButton) layout47.findViewById(R.id.radio3);
			radioButton141.setVisibility(View.VISIBLE);
			radioButton141.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if (!TextUtils.isEmpty(Data.formularioDois.getDoencaCardiaca())) {
				
				if(Data.formularioDois.getDoencaCardiaca().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton139.setChecked(true);
				}else if(Data.formularioDois.getDoencaCardiaca().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton140.setChecked(true);
				}else if(Data.formularioDois.getDoencaCardiaca().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton141.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_doenca_cardiaca));
			alert.setView(layout47);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton139.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton140.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton141.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.DOENCAHEMATOLOGICA:
			View layout48= layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton142 = (RadioButton) layout48.findViewById(R.id.radio1);
			radioButton142.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton143 = (RadioButton) layout48.findViewById(R.id.radio2);
			radioButton143.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton144 = (RadioButton) layout48.findViewById(R.id.radio3);
			radioButton144.setVisibility(View.VISIBLE);
			radioButton144.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if (!TextUtils.isEmpty(Data.formularioDois.getDoencaHematologicaCronica())) {
				
				if(Data.formularioDois.getDoencaHematologicaCronica().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton142.setChecked(true);
				}else if(Data.formularioDois.getDoencaHematologicaCronica().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton143.setChecked(true);
				}else if(Data.formularioDois.getDoencaHematologicaCronica().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton144.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_doenca_hematologica));
			alert.setView(layout48);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton142.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton143.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton144.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.DOENCAHEPATICA:
			View layout49= layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton145 = (RadioButton) layout49.findViewById(R.id.radio1);
			radioButton145.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton146 = (RadioButton) layout49.findViewById(R.id.radio2);
			radioButton146.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton147 = (RadioButton) layout49.findViewById(R.id.radio3);
			radioButton147.setVisibility(View.VISIBLE);
			radioButton147.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if (!TextUtils.isEmpty(Data.formularioDois.getDoencaHepaticaCronica())) {
				
				if(Data.formularioDois.getDoencaHepaticaCronica().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton145.setChecked(true);
				}else if(Data.formularioDois.getDoencaHepaticaCronica().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton146.setChecked(true);
				}else if(Data.formularioDois.getDoencaHepaticaCronica().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton147.setChecked(true);
				}
			}
			
			
			alert.setTitle(getString(R.string.formulario2_doenca_hepatica));
			alert.setView(layout49);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton145.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton146.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton147.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.ASPLENIA:
			View layout50= layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton148 = (RadioButton) layout50.findViewById(R.id.radio1);
			radioButton148.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton149 = (RadioButton) layout50.findViewById(R.id.radio2);
			radioButton149.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton150 = (RadioButton) layout50.findViewById(R.id.radio3);
			radioButton150.setVisibility(View.VISIBLE);
			radioButton150.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if (!TextUtils.isEmpty(Data.formularioDois.getAspleniaCongenitaOuFuncional())) {
				
				if(Data.formularioDois.getAspleniaCongenitaOuFuncional().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton148.setChecked(true);
				}else if(Data.formularioDois.getAspleniaCongenitaOuFuncional().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton149.setChecked(true);
				}else if(Data.formularioDois.getAspleniaCongenitaOuFuncional().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton150.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_asplenia));
			alert.setView(layout50);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton148.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton149.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton150.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.DIABETES:
			View layout51= layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton151 = (RadioButton) layout51.findViewById(R.id.radio1);
			radioButton151.setText(getString(R.string.formulario2_opcao_sim));
			final RadioButton radioButton152 = (RadioButton) layout51.findViewById(R.id.radio2);
			radioButton152.setText(getString(R.string.formulario2_opcao_nao));
			final RadioButton radioButton153 = (RadioButton) layout51.findViewById(R.id.radio3);
			radioButton153.setVisibility(View.VISIBLE);
			radioButton153.setText(getString(R.string.formulario2_opcao_nao_sabe));
			
			if (!TextUtils.isEmpty(Data.formularioDois.getDiabetes())) {
				
				if(Data.formularioDois.getDiabetes().equalsIgnoreCase(getString(R.string.formulario2_opcao_sim))){
					radioButton151.setChecked(true);
				}else if(Data.formularioDois.getDiabetes().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao))){
					radioButton152.setChecked(true);
				}else if(Data.formularioDois.getDiabetes().equalsIgnoreCase(getString(R.string.formulario2_opcao_nao_sabe))){
					radioButton153.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario2_diabetes));
			alert.setView(layout51);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton151.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton152.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton153.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao_sabe), false, true);
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
		case VariloidForm2.N_SWABS:
			final EditText numeroSwabs = new EditText(Formulario2Activity.this);
			int maxLength23 = 1;    
			numeroSwabs.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength23)});
			numeroSwabs.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			if (!TextUtils.isEmpty(Data.formularioDois
					.getNumeroSwabsColetados()))
				numeroSwabs.setText(Data.formularioDois
						.getNumeroSwabsColetados());
			
			alert.setTitle(getString(R.string.formulario2_n_swabs));
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
		case VariloidForm2.ID_SWAB_MACULA:
			final EditText numeroIdSwabs = new EditText(Formulario2Activity.this);
			int maxLength24 = 3;    
			numeroIdSwabs.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength24)});
			numeroIdSwabs.setInputType(InputType.TYPE_CLASS_TEXT);
			
			if (!TextUtils.isEmpty(Data.formularioDois
					.getIdSwabColetadoMaculoPapular()))
				numeroIdSwabs.setText(Data.formularioDois
						.getIdSwabColetadoMaculoPapular());
			
			alert.setTitle(getString(R.string.formulario2_id_swabs_macula));
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
		case VariloidForm2.ID_SWAB_VESICULA:
			final EditText numeroIdSwabsVesicula = new EditText(Formulario2Activity.this);
			int maxLength25 = 3;    
			numeroIdSwabsVesicula.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength25)});
			numeroIdSwabsVesicula.setInputType(InputType.TYPE_CLASS_TEXT);
			
			if (!TextUtils.isEmpty(Data.formularioDois
					.getIdSwabColetadoVesicula()))
				numeroIdSwabsVesicula.setText(Data.formularioDois
						.getIdSwabColetadoVesicula());
			
			alert.setTitle(getString(R.string.formulario2_id_swabs_visicula));
			alert.setView(numeroIdSwabsVesicula);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(numeroIdSwabsVesicula.getText().toString())){
						setPreferences(arg2, numeroIdSwabsVesicula.getText().toString(), false,  true);
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
		case VariloidForm2.ID_SWAB_CROSTA:
			final EditText numeroIdSwabsCrosta = new EditText(Formulario2Activity.this);
			int maxLength26 = 3;    
			numeroIdSwabsCrosta.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength26)});
			numeroIdSwabsCrosta.setInputType(InputType.TYPE_CLASS_TEXT);
			
			if (!TextUtils.isEmpty(Data.formularioDois
					.getIdSwabColetadoCrosta()))
				numeroIdSwabsCrosta.setText(Data.formularioDois
						.getIdSwabColetadoCrosta());
			
			alert.setTitle(getString(R.string.formulario2_id_swabs_crosta));
			alert.setView(numeroIdSwabsCrosta);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(!TextUtils.isEmpty(numeroIdSwabsCrosta.getText().toString())){
						setPreferences(arg2, numeroIdSwabsCrosta.getText().toString(), false,  true);
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
		case VariloidForm2.OBS_CASO:
			final EditText obsCasp = new EditText(Formulario2Activity.this);
			int maxLength27 = 250;    
			obsCasp.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength27)});
			
			if (!TextUtils.isEmpty(Data.formularioDois
					.getObservacoesRelacionadasCaso()))
				obsCasp.setText(Data.formularioDois
						.getObservacoesRelacionadasCaso());
			
			alert.setTitle(getString(R.string.formulario2_obs_caso));
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
		Data.formularioDois.setSucesso(posicao, sucesso);
		Data.formularioDois.setInativar(posicao, inativar);
		setAtribute(posicao, imput);
		myAdapter.notifyDataSetInvalidated();
	}
	
	public void setAtribute(int posicao, String string){
		switch (posicao) {
		case VariloidForm2.NOME:
			Data.formularioDois.setNomeCrianca(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.SEXO:
			Data.formularioDois.setSexo(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.DATA_NASCIMENTO:
			Data.formularioDois.setDataNascimento(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.JA_TEVE_CATAPORA:
			Data.formularioDois.setCriancaCataporaAnt(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.CONTATO_COM_QUEM_JA_TEVE:
			Data.formularioDois.setContatoPessoaCatapora(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.LOCAL_CONTATO:
			Data.formularioDois.setLocalContato(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.RESIDENCIA:
			Data.formularioDois.setResidencia(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.TIPO_LOGRADOURO:
			Data.formularioDois.setTipoLogradouro(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.NOME_LOGRADOURO:
			Data.formularioDois.setNomeLogradouro(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.QUADRA:
			Data.formularioDois.setQuadra(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.NUMERO:
			Data.formularioDois.setNumero(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.COMPLEMENTO:
			Data.formularioDois.setComplemento(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.BAIRRO:
			Data.formularioDois.setBairro(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.TITULO_CRITERIO_INCLUSAO:
			//
			break;
		case VariloidForm2.RASH:
			Data.formularioDois.setErupcaoDifusaAgudo(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.TCLE:
			Data.formularioDois.setTcleAssinado(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.NOME_MAE:
			Data.formularioDois.setNomeMae(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.IDADE_MAE:
			Data.formularioDois.setIdadeMae(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.NOME_PAI:
			Data.formularioDois.setNomePai(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.TELEFONE:
			Data.formularioDois.setTelefoneFixo(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.CELULAR1:
			Data.formularioDois.setCelular1(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.OPER_CELULAR1:
			Data.formularioDois.setOperadoraCelular1(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.CELULAR2:
			Data.formularioDois.setCelular2(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.OPER_CELULAR2:
			Data.formularioDois.setOperadoraCelular2(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.COBERTURA_PSF:
			Data.formularioDois.setCriancaCadastradaPSF(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.DT_INICIO_VARICELA:
			Data.formularioDois.setDataInicioCatapora(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.TITULO_SINAIS_ANTES_DO_RASH:
			//
			break;
		case VariloidForm2.FEBRE:
			Data.formularioDois.setFebre(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.ANOREXIA:
			Data.formularioDois.setAnorexia(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.MALESTAR:
			Data.formularioDois.setMalEstar(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.DOR_ABDOMINAL:
			Data.formularioDois.setDorAbdominal(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.TITULO_RASH:
			//
			break;
		case VariloidForm2.DT_RASH:
			Data.formularioDois.setDataInicioRash(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.LOC_RASH:
			Data.formularioDois.setLocalizacaoRash(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.LOC_RASH2:
			Data.formularioDois.setPrimeiralocalizacaoRash(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.N_LESOES:
			Data.formularioDois.setNumLesoes(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.RASH_MP:
			Data.formularioDois.setMaculoPapular(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.RASH_V:
			Data.formularioDois.setVesicular(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.RASH_C:
			Data.formularioDois.setCrosta(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.LESOES:
			Data.formularioDois.setMaioriaLesoes(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.DIAG_QUEM:
			Data.formularioDois.setDiagnosticoFeitoPor(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.PESONASCER:
			Data.formularioDois.setPesoNascimentoGramas(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.IDADEGESTACIONAL:
			Data.formularioDois.setIdadeGestacional(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.RACA:
			Data.formularioDois.setRaca(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.VACVARICELA:
			Data.formularioDois.setCriancaRecebeuVacinaCatapora(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.MOTIVO_NAO_VACINACAO:
			Data.formularioDois.setRazoesNaoVacinacao(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.TITULO_VACINA_CONTRA_VARICELA:
			//
			break;
		case VariloidForm2.N_DOSES_VARIC:
			Data.formularioDois.setNumDosesRecebidasContraVaricela(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.VAC_VARIC_DT_DOSE1:
			Data.formularioDois.setDataPrimeiraDose(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.VAC_VARIC_QUAL_DOSE1:
			Data.formularioDois.setQualVacinaUsadaPrimeiraDose(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.VAC_VARIC_DT_DOSE2:
			Data.formularioDois.setDataSegundaDose(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.VAC_VARIC_QUAL_DOSE2:
			Data.formularioDois.setQualVacinaUsadaSegundaDose(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.TITULO_VACINA_TRIPLICE:
			//
			break;
		case VariloidForm2.VACTRIPLICE:
			Data.formularioDois.setCriancaRecebeuVacinaTripliceViral(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.N_DOSES_TRIPLICE:
			Data.formularioDois.setNumDosesRecebidasContraTripliceViral(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.MMR1:
			Data.formularioDois.setDataPrimeiraDoseMMR(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.MMR2:
			Data.formularioDois.setDataSegundaDoseMMR(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.FREQCRECHE1:
			Data.formularioDois.setAlguemFrequentouCreche(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.FREQCRECHE2:
			Data.formularioDois.setQuemFamiliaFrequentouCreche(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.FREQSERVSAUDE1:
			Data.formularioDois.setAlguemFrequentouServicoSaude(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.FREQSERVSAUDE2:
			Data.formularioDois.setQuemFamiliaFrequentouServicoSaude(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.HOSPITALIZACAO:
			Data.formularioDois.setCriancaInternou(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.MAE_TRABALHA:
			Data.formularioDois.setMaeTrabalhaFora(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.ES_CMAE:
			Data.formularioDois.setEscolaridadeMae(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.NCCAS_CASA5:
			Data.formularioDois.setNumCriancasMenos5anosDomicilio(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.NCCAS_CASA10:
			Data.formularioDois.setNumCriancasMenos10anosDomicilio(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.QT_DE_PESSOAS_CASA:
			Data.formularioDois.setQuantasPessoasCasa(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.QTD_EPESSOAS_QTO:
			Data.formularioDois.setQuantasPessoasDormemMesmoComodoCrianca(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.TITULO_DOENCAFALCIFORME:
			//
			break;
		case VariloidForm2.DOENCAFALCIFORME:
			Data.formularioDois.setDoencaFalciforme(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.PREMATURIDADE:
			Data.formularioDois.setPrematuridade(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.INFECHIV:
			Data.formularioDois.setInfeccaoHIVeAIDS(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.IMUNODEFICIENCIA:
			Data.formularioDois.setImunodeficiencia(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.ASMA:
			Data.formularioDois.setAsma(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.CORTICOIDE:
			Data.formularioDois.setUsoCorticoideSistemico(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.CANCER:
			Data.formularioDois.setCancer(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.DOENCAPULMONARCRONICA:
			Data.formularioDois.setDoencaPulmonarCronica(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.DOENCARENALCRONICA:
			Data.formularioDois.setDoencaRenalCronica(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.DOENCACARDIACA:
			Data.formularioDois.setDoencaCardiaca(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.DOENCAHEMATOLOGICA:
			Data.formularioDois.setDoencaHematologicaCronica(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.DOENCAHEPATICA:
			Data.formularioDois.setDoencaHepaticaCronica(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.ASPLENIA:
			Data.formularioDois.setAspleniaCongenitaOuFuncional(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.DIABETES:
			Data.formularioDois.setDiabetes(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.N_SWABS:
			Data.formularioDois.setNumeroSwabsColetados(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.ID_SWAB_MACULA:
			Data.formularioDois.setIdSwabColetadoMaculoPapular(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.ID_SWAB_VESICULA:
			Data.formularioDois.setIdSwabColetadoVesicula(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.ID_SWAB_CROSTA:
			Data.formularioDois.setIdSwabColetadoCrosta(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		case VariloidForm2.OBS_CASO:
			Data.formularioDois.setObservacoesRelacionadasCaso(string);
			Data.mapFormularioDois.set(Data.FORM2_KEY.concat(VariloidForm2.idCampos[posicao]), string);
			break;
		}
	}
}
