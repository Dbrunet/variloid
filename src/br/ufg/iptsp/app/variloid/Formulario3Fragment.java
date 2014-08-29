package br.ufg.iptsp.app.variloid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import br.ufg.iptsp.app.variloid.adapter.CropOptionAdapter;
import br.ufg.iptsp.app.variloid.adapter.MyAdapterForm3;
import br.ufg.iptsp.app.variloid.negocio.CropOption;
import br.ufg.iptsp.app.variloid.negocio.FormularioTres;
import br.ufg.iptsp.app.variloid.provider.Data;
import br.ufg.iptsp.app.variloid.servico.Servico;
import br.ufg.iptsp.app.variloid.servico.ServicoConexao;
import br.ufg.iptsp.app.variloid.util.Mask;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Formulario3Fragment extends SherlockFragment implements
		OnItemClickListener {

	private static final int PICK_FROM_CAMERA = 0;
	private static final int CROP_FROM_CAMERA = 1;
	
	private LayoutInflater layoutInflater;
	private MyAdapterForm3 myAdapter;
	private Button buttonTirarFotoCartao;

	private List<Integer> list;
	private int paginaFragment;
	private MultiValueMap<String, Object> mapFormularioTres;
	private FormularioTres formularioTres;
	private Uri mImageCaptureUri;
	private Bitmap bitmap;
	private String fileAbsolutePath;
	private ImageView fotoCartaoCarregada;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Bundle bundle = getArguments();
		if (bundle != null) {
			paginaFragment = bundle.getInt("paginaForm3");
		}

		list = new ArrayList<Integer>();
		for (Integer integer : VariloidForm3.campos) {
			list.add(integer);
		}
		
		if (Data.listaMapFormularioTres.isEmpty()) {
			
			mapFormularioTres = new LinkedMultiValueMap<String, Object>();
			formularioTres = new FormularioTres();
			Data.listaFormularioTres.add(formularioTres);

			for (String strings : VariloidForm3.idCampos) {
				
				mapFormularioTres.add(Data.FORM3_KEY.concat("[")
						.concat(String.valueOf(paginaFragment)).concat("].")
						.concat(strings), "");

				if (Data.listaFormularioTres.get(paginaFragment).getListInativar().size() != VariloidForm3.idCampos.length)
					Data.listaFormularioTres.get(paginaFragment).getListInativar().add(false);
				if (Data.listaFormularioTres.get(paginaFragment).getListSucesso().size() != VariloidForm3.idCampos.length)
					Data.listaFormularioTres.get(paginaFragment).getListSucesso().add(false);
			}

			Data.listaMapFormularioTres.add(mapFormularioTres);
			
		}


		ServicoConexao.verificaTipoConexao(getSherlockActivity());
		layoutInflater = getSherlockActivity().getLayoutInflater();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_formulario, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View layoutFooter = layoutInflater.inflate(R.layout.button_layout, null);
		
		fotoCartaoCarregada = (ImageView) layoutFooter.findViewById(R.id.imgFotoCartaoCarregada);
		
		buttonTirarFotoCartao = (Button) layoutFooter.findViewById(R.id.button_tirar_foto_cartao);
		buttonTirarFotoCartao.setText(getString(R.string.tirar_foto_cartao));
		buttonTirarFotoCartao.setBackgroundResource(R.drawable.seletor_btn);
		buttonTirarFotoCartao.setTextColor(Color.WHITE);
		buttonTirarFotoCartao.setVisibility(View.VISIBLE);

		if (Data.listaMapFormularioTres.get(paginaFragment).get(Data.FORM3_KEY.concat("[")
						.concat(String.valueOf(paginaFragment)).concat("].")
						.concat(Variloid.FORM_FOTO_CARTAO_VACINA))!=null) {
			fotoCartaoCarregada.setVisibility(View.VISIBLE);
		} else {
			fotoCartaoCarregada.setVisibility(View.GONE);
		}

		buttonTirarFotoCartao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
					createDialog(1);
			}
		});

		Button buttonPendente = (Button) layoutFooter
				.findViewById(R.id.button_tirar_foto_lesao);
		buttonPendente.setVisibility(View.VISIBLE);
		buttonPendente.setText(getString(R.string.caso_controle_pendente));
		buttonPendente.setBackgroundResource(R.drawable.seletor_btn);
		buttonPendente.setTextColor(Color.WHITE);
		buttonPendente.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (ServicoConexao.isConectadoRede()) {
					int count = 0;
					for (int i = 0; i < Data.listaFormularioTres.get(paginaFragment).getListSucesso().size(); i++) {
						if(Data.listaFormularioTres.get(paginaFragment).getListSucesso().get(i)){
							count++;
						}
					}
					
					if(count > 0){
						
						final AlertDialog.Builder alert = new AlertDialog.Builder(
								getSherlockActivity());
						alert.setTitle(getString(R.string.formulario_alerta));
						alert.setMessage(getString(R.string.formulario_campos_nao_preenchidos)
								.concat(" ")
								.concat(String.valueOf((Data.listaFormularioTres.get(paginaFragment).getListSucesso().size() - 6) - count))
								.concat(" ")
								+ getString(R.string.formulario_deseja_continuar));
						alert.setPositiveButton("Sim",
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								
								Data.mapService.add(Variloid.STATUS, "1");
								
								new FormAsyntask(getSherlockActivity()).execute();
								dialog.cancel();
							}
						});
						
						alert.setNegativeButton("Não",
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						});
						alert.show();
					}else{
						
						Data.mapService.add(Variloid.STATUS, "1");
					}

				} else {
					Toast.makeText(getSherlockActivity(),
							R.string.advertencia_conexao_off,
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		Button buttonFinalizar = (Button) layoutFooter
				.findViewById(R.id.button_avancar_finalizar);
		buttonFinalizar.setVisibility(View.VISIBLE);
		buttonFinalizar.setText(getString(R.string.finalizar_caso_controle));
		buttonFinalizar.setBackgroundResource(R.drawable.seletor_btn);
		buttonFinalizar.setTextColor(Color.WHITE);
		buttonFinalizar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (ServicoConexao.isConectadoRede()) {
					int count = 0;
					for (int i = 0; i < Data.listaFormularioTres.get(paginaFragment).getListSucesso().size(); i++) {
						if(Data.listaFormularioTres.get(paginaFragment).getListSucesso().get(i)){
							count++;
						}
					}
					
					if(count > 0){
						
						final AlertDialog.Builder alert = new AlertDialog.Builder(
								getSherlockActivity());
						alert.setTitle(getString(R.string.formulario_alerta));
						alert.setMessage(getString(R.string.formulario_campos_nao_preenchidos)
								.concat(" ")
								.concat(String.valueOf((Data.listaFormularioTres.get(paginaFragment).getListSucesso().size() - 6) - count))
								.concat(" ")
								+ getString(R.string.formulario_deseja_continuar));
						alert.setPositiveButton("Sim",
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Data.mapService.add(Variloid.STATUS, "0");
								new FormAsyntask(getSherlockActivity()).execute();
								dialog.cancel();
							}
						});
						
						alert.setNegativeButton("Não",
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						});
						alert.show();
					}else{
						Data.mapService.add(Variloid.STATUS, "0");
						new FormAsyntask(getSherlockActivity()).execute();
					}
					

				} else {
					Toast.makeText(getSherlockActivity(),
							R.string.advertencia_conexao_off,
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		ListView listView = (ListView) view.findViewById(R.id.listView);
		listView.addFooterView(layoutFooter);
		myAdapter = new MyAdapterForm3(getSherlockActivity(),
				R.layout.adapter_formulario, paginaFragment, list);
		listView.setAdapter(myAdapter);
		listView.setOnItemClickListener(this);

		super.onViewCreated(view, savedInstanceState);
	}

	private class FormAsyntask extends AsyncTask<Void, Void, Void> {

		private Servico servico;
		private ProgressDialog pd;
		private Context context;
		private String nid;

		public FormAsyntask(Context context) {
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
			nid = servico.enviarForm2e3();
			return null;
		}

		protected void onPostExecute(Void result) {
			pd.dismiss();
			if (nid != null) {
				Intent i = new Intent(context, NIDActivity.class);
				i.putExtra("nid", nid);
				context.startActivity(i);
			} else {
				Toast.makeText(context,
						"Problema de comunicação com o servidor!",
						Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		};

	}

	protected void createDialog(int dialogId) {
		Intent intent = null;
		switch (dialogId) {
		case 1:
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			mImageCaptureUri = Uri.fromFile(new File(Environment
					.getExternalStorageDirectory(), "tmp_foto_"
					+ String.valueOf(System.currentTimeMillis())
					+ ".jpg"));

			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					mImageCaptureUri);

			try {
				intent.putExtra("return-data", true);

				startActivityForResult(intent, PICK_FROM_CAMERA);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}

			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (resultCode != Activity.RESULT_OK)
			return;
		
		switch (requestCode) {
		case PICK_FROM_CAMERA:
			/**
			 * After taking a picture, do the crop
			 */
			doCrop();

			break;

		case CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();
			/**
			 * After cropping the image, get the bitmap of the cropped image and
			 * display it on imageview.
			 */
			if (extras != null) {
				
				bitmap = extras.getParcelable("data");
				
				File mediaDir = new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						.concat("/").concat("variloid").concat("/"));
				if (!mediaDir.exists()) {
					mediaDir.mkdirs();
				}

				File foto = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath().concat("/").concat("variloid")
						.concat("/"), String.valueOf(System.currentTimeMillis()).concat(".jpg"));
				
				FileOutputStream fOut = null;
				
				try {
					fOut = new FileOutputStream(foto);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fOut);
				
				fileAbsolutePath = foto.getAbsolutePath();
			
				Data.listaMapFormularioTres.get(paginaFragment).
				set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(Variloid.FORM_FOTO_CARTAO_VACINA), new FileSystemResource(fileAbsolutePath));
				fotoCartaoCarregada.setVisibility(View.VISIBLE);
				
				File fileTemp = new File(mImageCaptureUri.getPath());
				if (fileTemp.exists())
					fileTemp.delete();
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
		/**
		 * Open image crop app by starting an intent
		 * ‘com.android.camera.action.CROP‘.
		 */
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		/**
		 * Check if there is image cropper app installed.
		 */
		List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(
				intent, 0);

		int size = list.size();

		/**
		 * If there is no image cropper app, display warning message
		 */
		if (size == 0) {

			Toast.makeText(getActivity(), "Can not find image crop app",
					Toast.LENGTH_SHORT).show();

			return;
		} else {
			/**
			 * Specify the image path, crop dimension and scale
			 */
			intent.setData(mImageCaptureUri);

			intent.putExtra("outputX", 256);
			intent.putExtra("outputY", 256);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);
			/**
			 * There is posibility when more than one image cropper app exist,
			 * so we have to check for it first. If there is only one app, open
			 * then app.
			 */

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));

				startActivityForResult(i, CROP_FROM_CAMERA);
			} else {
				/**
				 * If there are several app exist, create a custom chooser to
				 * let user selects the app.
				 */
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = getActivity().getPackageManager().getApplicationLabel(
							res.activityInfo.applicationInfo);
					co.icon = getActivity().getPackageManager().getApplicationIcon(
							res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						getActivity(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Choose Crop App");
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										CROP_FROM_CAMERA);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (mImageCaptureUri != null) {
							getActivity().getContentResolver().delete(mImageCaptureUri, null,
									null);
							mImageCaptureUri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}
	}

//	@SuppressWarnings("deprecation")
//	public String getPathFromURI(Uri contentUri) {
//		try {
//			String[] proj = { MediaStore.Images.Media.DATA };
//			Cursor cursor = getSherlockActivity().managedQuery(contentUri,
//					proj, null, null, null);
//			int column_index = cursor
//					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//			cursor.moveToFirst();
//			return cursor.getString(column_index);
//		} catch (Exception e) {
//			return contentUri.getPath();
//		}
//	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.variloid, menu);
		menu.findItem(R.id.menu_novo_controle).setVisible(true);
		menu.findItem(R.id.menu_remover_controle).setVisible(false);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		final AlertDialog.Builder alert = new AlertDialog.Builder(
				getSherlockActivity());
		switch (item.getItemId()) {
		case R.id.menu_novo_controle:
			alert.setTitle(getString(R.string.formulario_alerta));
			alert.setMessage(getString(R.string.notificacao_add_controle));
			alert.setPositiveButton("Sim",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							((Formulario3Activity) getSherlockActivity())
									.addItemPager(paginaFragment+1);
						}
					});

			alert.setNegativeButton("Não",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			break;
		case R.id.menu_remover_controle:
			alert.setTitle(getString(R.string.formulario_alerta));
			alert.setMessage(getString(R.string.notificacao_remove_controle).concat(" ")
					.concat(getSherlockActivity().getSupportActionBar()
							.getTitle().toString()));
			alert.setPositiveButton("Sim",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							((Formulario3Activity) getSherlockActivity())
									.removeItemPager(paginaFragment);
						}
					});

			alert.setNegativeButton("Não",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			break;
		}
		alert.show();

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, final View view,
			final int arg2, long arg3) {

		final AlertDialog.Builder alert = new AlertDialog.Builder(
				getSherlockActivity());

		switch (arg2) {
		
		case VariloidForm3.CRIANCA_JA_TEVE_CATAPORA:
			View layoutJaTeveCatapora = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButton3 = (RadioButton) layoutJaTeveCatapora.findViewById(R.id.radio1);
			radioButton3.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton4 = (RadioButton) layoutJaTeveCatapora.findViewById(R.id.radio2);
			radioButton4.setText(getString(R.string.formulario3_opcao_nao));
			
			if(!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getCriancaCataporaAnt())){
				
				if(Data.listaFormularioTres.get(paginaFragment)
						.getCriancaCataporaAnt().equalsIgnoreCase(getString(R.string.formulario3_opcao_sim))){
					radioButton3.setChecked(true);
				}else if(Data.listaFormularioTres.get(paginaFragment)
						.getCriancaCataporaAnt().equalsIgnoreCase(getString(R.string.formulario3_opcao_nao))){
					radioButton4.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario3_ja_teve_catapora));
			alert.setView(layoutJaTeveCatapora);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButton3.isChecked()){
						setPreferences(arg2, getString(R.string.formulario3_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButton4.isChecked()){
						setPreferences(arg2, getString(R.string.formulario3_opcao_nao), false, true);
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
			
		case VariloidForm3.CRIANCA_MORA_NA_VIZINHANCA:
			View layoutMoraNaVizinhanca = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButtonSim = (RadioButton) layoutMoraNaVizinhanca.findViewById(R.id.radio1);
			radioButtonSim.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButtonNao = (RadioButton) layoutMoraNaVizinhanca.findViewById(R.id.radio2);
			radioButtonNao.setText(getString(R.string.formulario3_opcao_nao));
			
			if(!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getCriancaMoraVizinhanca())){
				
				if(Data.listaFormularioTres.get(paginaFragment)
						.getCriancaMoraVizinhanca().equalsIgnoreCase(getString(R.string.formulario3_opcao_sim))){
					radioButtonSim.setChecked(true);
				}else if(Data.listaFormularioTres.get(paginaFragment)
						.getCriancaMoraVizinhanca().equalsIgnoreCase(getString(R.string.formulario3_opcao_nao))){
					radioButtonNao.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario3_crianca_mora_na_vizinhanca));
			alert.setView(layoutMoraNaVizinhanca);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButtonSim.isChecked()){
						setPreferences(arg2, getString(R.string.formulario3_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonNao.isChecked()){
						setPreferences(arg2, getString(R.string.formulario3_opcao_nao), false, true);
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
			
		case VariloidForm3.NOME:
			final EditText inputNome = new EditText(getSherlockActivity());

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getNomeCrianca()))
				inputNome.setText(Data.listaFormularioTres.get(paginaFragment)
						.getNomeCrianca());

			alert.setTitle(getString(R.string.formulario3_nome));
			alert.setView(inputNome);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(inputNome.getText()
									.toString())) {
								setPreferences(arg2, inputNome.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.SEXO:

			View layout = layoutInflater.inflate(R.layout.group_box_formulario,
					null);
			final RadioButton radioButton1 = (RadioButton) layout
					.findViewById(R.id.radio1);
			radioButton1
					.setText(getString(R.string.formulario3_opcao_masculino));
			final RadioButton radioButton2 = (RadioButton) layout
					.findViewById(R.id.radio2);
			radioButton2
					.setText(getString(R.string.formulario3_opcao_feminino));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getSexo())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getSexo()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_masculino))) {
					radioButton1.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getSexo()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_feminino))) {
					radioButton2.setChecked(true);
				}

			}

			alert.setTitle(getString(R.string.formulario3_sexo));
			alert.setView(layout);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton1.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_masculino),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton2.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_feminino),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.DATA_NASCIMENTO:
			final EditText dataNascimento = new EditText(getSherlockActivity());
			dataNascimento.setInputType(InputType.TYPE_CLASS_DATETIME);
			dataNascimento.addTextChangedListener(Mask.insert("##/##/####",
					dataNascimento));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getDataNascimento())) {
				String replaceCampo = Data.listaFormularioTres
						.get(paginaFragment).getDataNascimento()
						.replaceAll("/", "");
				dataNascimento.setText(replaceCampo);
			}

			alert.setTitle(getString(R.string.formulario3_data));
			alert.setView(dataNascimento);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(dataNascimento.getText()
									.toString())) {
								setPreferences(arg2, dataNascimento.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		// case VariloidForm3.IDADE_EM_MESES:
		// final EditText idadeEmMeses = new EditText(getSherlockActivity());
		// idadeEmMeses.setInputType(InputType.TYPE_CLASS_NUMBER);
		// int maxLengthIdade = 3;CL
		// idadeEmMeses.setFilters(new InputFilter[] {new
		// InputFilter.LengthFilter(maxLengthIdade)});
		//
		// alert.setTitle(getString(R.string.formulario3_idade_em_meses));
		// alert.setView(idadeEmMeses);
		// alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		//
		// if(!TextUtils.isEmpty(idadeEmMeses.getText().toString())){
		// setPreferences(arg2, idadeEmMeses.getText().toString(), false, true);
		// ((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
		// }else{
		// setPreferences(arg2, "", false, false);
		// ((ImageView)view.findViewById(R.id.nome_img_check)).setImageDrawable(null);
		// }
		// dialog.cancel();
		// }
		// });
		//
		// alert.setNegativeButton("Cancelar",new
		// DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// dialog.cancel();
		// }
		// });
		// alert.show();
		// break;
//		case VariloidForm3.JA_TEVE_CATAPORA:
//			View layout2 = layoutInflater.inflate(
//					R.layout.group_box_formulario, null);
//			final RadioButton radioButton3 = (RadioButton) layout2
//					.findViewById(R.id.radio1);
//			radioButton3.setText(getString(R.string.formulario3_opcao_sim));
//			final RadioButton radioButton4 = (RadioButton) layout2
//					.findViewById(R.id.radio2);
//			radioButton4.setText(getString(R.string.formulario3_opcao_nao));
//
//			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
//					.getCriancaCataporaAnt())) {
//
//				if (Data.listaFormularioTres
//						.get(paginaFragment)
//						.getCriancaCataporaAnt()
//						.equalsIgnoreCase(
//								getString(R.string.formulario3_opcao_sim))) {
//					radioButton3.setChecked(true);
//				} else if (Data.listaFormularioTres
//						.get(paginaFragment)
//						.getCriancaCataporaAnt()
//						.equalsIgnoreCase(
//								getString(R.string.formulario3_opcao_nao))) {
//					radioButton4.setChecked(true);
//				}
//			}
//
//			alert.setTitle(getString(R.string.formulario3_ja_teve_catapora));
//			alert.setView(layout2);
//			alert.setPositiveButton("Ok",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
//
//							if (radioButton3.isChecked()) {
//								setPreferences(
//										arg2,
//										getString(R.string.formulario3_opcao_sim),
//										false, true);
//								((ImageView) view
//										.findViewById(R.id.nome_img_check))
//										.setImageResource(R.drawable.ic_certo);
//							} else if (radioButton4.isChecked()) {
//								setPreferences(
//										arg2,
//										getString(R.string.formulario3_opcao_nao),
//										false, true);
//								((ImageView) view
//										.findViewById(R.id.nome_img_check))
//										.setImageResource(R.drawable.ic_certo);
//							} else {
//								setPreferences(arg2, "", false, false);
//								((ImageView) view
//										.findViewById(R.id.nome_img_check))
//										.setImageDrawable(null);
//							}
//							dialog.cancel();
//						}
//					});
//
//			alert.setNegativeButton("Cancelar",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
//							dialog.cancel();
//						}
//					});
//			alert.show();
//			break;

		case VariloidForm3.CONTATO_COM_QUEM_JA_TEVE:

			View layout3 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton5 = (RadioButton) layout3
					.findViewById(R.id.radio1);
			radioButton5.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton6 = (RadioButton) layout3
					.findViewById(R.id.radio2);
			radioButton6.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton7 = (RadioButton) layout3
					.findViewById(R.id.radio3);
			radioButton7.setVisibility(View.VISIBLE);
			radioButton7
					.setText(getString(R.string.formulario3_opcao_ignorado));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getContatoPessoaCatapora())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getContatoPessoaCatapora()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton5.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getContatoPessoaCatapora()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton6.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getContatoPessoaCatapora()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_ignorado))) {
					radioButton7.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario2_contato_com_ja_teve_catapora));
			alert.setView(layout3);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton5.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton6.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton7.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_ignorado),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;

		case VariloidForm3.LOCAL_CONTATO:
			View layout4 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);

			final EditText outros1 = (EditText) layout4
					.findViewById(R.id.edittext_gruporadio);
			int maxLength2 = 90;
			outros1.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					maxLength2) });

			final EditText outros2 = (EditText) layout4
					.findViewById(R.id.edittext_botton);
			int maxLength1 = 90;
			outros2.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					maxLength1) });

			final RadioButton radioButton8 = (RadioButton) layout4
					.findViewById(R.id.radio1);
			radioButton8.setText(getString(R.string.formulario3_opcao_cmei));
			radioButton8
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton arg0,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								outros1.setVisibility(View.VISIBLE);
							} else {
								outros1.setVisibility(View.GONE);
							}
						}
					});

			final RadioButton radioButton9 = (RadioButton) layout4
					.findViewById(R.id.radio2);
			radioButton9
					.setText(getString(R.string.formulario3_opcao_comunidade));
			final RadioButton radioButton10 = (RadioButton) layout4
					.findViewById(R.id.radio3);
			radioButton10
					.setText(getString(R.string.formulario3_opcao_domicilio));
			radioButton10.setVisibility(View.VISIBLE);
			final RadioButton radioButton11 = (RadioButton) layout4
					.findViewById(R.id.radio4);
			radioButton11.setVisibility(View.VISIBLE);
			radioButton11
					.setText(getString(R.string.formulario3_opcao_hospital));
			final RadioButton radioButton12 = (RadioButton) layout4
					.findViewById(R.id.radio5);
			radioButton12.setVisibility(View.VISIBLE);
			radioButton12
					.setText(getString(R.string.formulario3_opcao_cais_ubs));
			final RadioButton radioButton13 = (RadioButton) layout4
					.findViewById(R.id.radio6);
			radioButton13.setVisibility(View.VISIBLE);
			radioButton13
					.setText(getString(R.string.formulario3_opcao_nao_se_aplica));
			final RadioButton radioButton14 = (RadioButton) layout4
					.findViewById(R.id.radio7);
			radioButton14.setVisibility(View.VISIBLE);
			radioButton14.setText(getString(R.string.formulario3_opcao_outro));
			radioButton14
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								outros2.setVisibility(View.VISIBLE);
							} else {
								outros2.setVisibility(View.GONE);
							}
						}
					});

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getLocalContato())) {

				if (!Data.listaFormularioTres.get(paginaFragment)
						.getLocalContato().equalsIgnoreCase("")) {
					if (Data.listaFormularioTres
							.get(paginaFragment)
							.getLocalContato()
							.indexOf(getString(R.string.formulario3_opcao_cmei)) != -1) {
						radioButton8.setChecked(true);
						int posicao = Data.listaFormularioTres
								.get(paginaFragment)
								.getLocalContato()
								.indexOf(
										getString(R.string.formulario3_opcao_cmei));
						outros1.setText(Data.listaFormularioTres
								.get(paginaFragment)
								.getLocalContato()
								.substring(
										posicao
												+ getString(
														R.string.formulario3_opcao_cmei)
														.length(),
										Data.listaFormularioTres
												.get(paginaFragment)
												.getLocalContato().length()));
					} else if (Data.listaFormularioTres
							.get(paginaFragment)
							.getLocalContato()
							.equalsIgnoreCase(
									getString(R.string.formulario3_opcao_comunidade))) {
						radioButton9.setChecked(true);
					} else if (Data.listaFormularioTres
							.get(paginaFragment)
							.getLocalContato()
							.equalsIgnoreCase(
									getString(R.string.formulario3_opcao_domicilio))) {
						radioButton10.setChecked(true);
					} else if (Data.listaFormularioTres
							.get(paginaFragment)
							.getLocalContato()
							.equalsIgnoreCase(
									getString(R.string.formulario3_opcao_hospital))) {
						radioButton11.setChecked(true);
					} else if (Data.listaFormularioTres
							.get(paginaFragment)
							.getLocalContato()
							.equalsIgnoreCase(
									getString(R.string.formulario3_opcao_cais_ubs))) {
						radioButton12.setChecked(true);
					} else if (Data.listaFormularioTres
							.get(paginaFragment)
							.getLocalContato()
							.equalsIgnoreCase(
									getString(R.string.formulario3_opcao_nao_se_aplica))) {
						radioButton13.setChecked(true);
					} else if (Data.listaFormularioTres
							.get(paginaFragment)
							.getLocalContato()
							.indexOf(
									getString(R.string.formulario3_opcao_outro)) != -1) {
						radioButton14.setChecked(true);
						int posicao = Data.listaFormularioTres
								.get(paginaFragment)
								.getLocalContato()
								.indexOf(
										getString(R.string.formulario3_opcao_outro));
						outros2.setText(Data.listaFormularioTres
								.get(paginaFragment)
								.getLocalContato()
								.substring(
										posicao
												+ getString(
														R.string.formulario3_opcao_outro)
														.length(),
										Data.listaFormularioTres
												.get(paginaFragment)
												.getLocalContato().length()));
					}
				}
			}

			alert.setTitle(getString(R.string.formulario3_local_contato));
			alert.setView(layout4);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String valor = null;
							if (radioButton8.isChecked()) {
								valor = getString(R.string.formulario3_opcao_cmei)
										+ outros1.getText().toString();
								setPreferences(arg2, valor, false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton9.isChecked()) {
								valor = getString(R.string.formulario3_opcao_comunidade);
								setPreferences(arg2, valor, false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton10.isChecked()) {
								valor = getString(R.string.formulario3_opcao_domicilio);
								setPreferences(arg2, valor, false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton11.isChecked()) {
								valor = getString(R.string.formulario3_opcao_hospital);
								setPreferences(arg2, valor, false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton12.isChecked()) {
								valor = getString(R.string.formulario3_opcao_cais_ubs);
								setPreferences(arg2, valor, false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton13.isChecked()) {
								valor = getString(R.string.formulario3_opcao_nao_se_aplica);
								setPreferences(arg2, valor, false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton14.isChecked()) {
								valor = getString(R.string.formulario3_opcao_outro)
										+ outros2.getText().toString();
								setPreferences(arg2, valor, false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;

		case VariloidForm3.RESIDENCIA:

			View layout7 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton16 = (RadioButton) layout7
					.findViewById(R.id.radio1);
			radioButton16.setText(getString(R.string.formulario3_opcao_urbana));
			final RadioButton radioButton17 = (RadioButton) layout7
					.findViewById(R.id.radio2);
			radioButton17
					.setText(getString(R.string.formulario3_opcao_nao_urbana));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getResidencia())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getResidencia()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_urbana))) {
					radioButton16.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getResidencia()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_urbana))) {
					radioButton17.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_residencia));
			alert.setView(layout7);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton16.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_urbana),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton17.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_urbana),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.TIPO_LOGRADOURO:

			View layout8 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);

			final EditText outros3 = (EditText) layout8
					.findViewById(R.id.edittext_botton);
			int maxLength3 = 10;
			outros3.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					maxLength3) });

			final RadioButton radioButton18 = (RadioButton) layout8
					.findViewById(R.id.radio1);
			radioButton18
					.setText(getString(R.string.formulario3_opcao_avenida));
			final RadioButton radioButton19 = (RadioButton) layout8
					.findViewById(R.id.radio2);
			radioButton19.setText(getString(R.string.formulario3_opcao_rua));
			final RadioButton radioButton20 = (RadioButton) layout8
					.findViewById(R.id.radio3);
			radioButton20.setVisibility(View.VISIBLE);
			radioButton20
					.setText(getString(R.string.formulario3_opcao_alameda));
			final RadioButton radioButton21 = (RadioButton) layout8
					.findViewById(R.id.radio4);
			radioButton21.setVisibility(View.VISIBLE);
			radioButton21
					.setText(getString(R.string.formulario3_opcao_rodovia));
			final RadioButton radioButton22 = (RadioButton) layout8
					.findViewById(R.id.radio5);
			radioButton22.setVisibility(View.VISIBLE);
			radioButton22
					.setText(getString(R.string.formulario3_opcao_travessa));
			final RadioButton radioButton23 = (RadioButton) layout8
					.findViewById(R.id.radio6);
			radioButton23.setVisibility(View.VISIBLE);
			radioButton23.setText(getString(R.string.formulario3_opcao_outro));
			radioButton23
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								outros3.setVisibility(View.VISIBLE);
							} else {
								outros3.setVisibility(View.GONE);
							}
						}
					});

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getTipoLogradouro())) {

				if (!Data.listaFormularioTres.get(paginaFragment)
						.getTipoLogradouro().equalsIgnoreCase("")) {
					if (Data.listaFormularioTres
							.get(paginaFragment)
							.getTipoLogradouro()
							.equalsIgnoreCase(
									getString(R.string.formulario3_opcao_avenida))) {
						radioButton18.setChecked(true);
					} else if (Data.listaFormularioTres
							.get(paginaFragment)
							.getTipoLogradouro()
							.equalsIgnoreCase(
									getString(R.string.formulario3_opcao_rua))) {
						radioButton19.setChecked(true);
					} else if (Data.listaFormularioTres
							.get(paginaFragment)
							.getTipoLogradouro()
							.equalsIgnoreCase(
									getString(R.string.formulario3_opcao_alameda))) {
						radioButton20.setChecked(true);
					} else if (Data.listaFormularioTres
							.get(paginaFragment)
							.getTipoLogradouro()
							.equalsIgnoreCase(
									getString(R.string.formulario3_opcao_rodovia))) {
						radioButton21.setChecked(true);
					} else if (Data.listaFormularioTres
							.get(paginaFragment)
							.getTipoLogradouro()
							.equalsIgnoreCase(
									getString(R.string.formulario3_opcao_travessa))) {
						radioButton22.setChecked(true);
					} else if (Data.listaFormularioTres.get(paginaFragment)
							.getTipoLogradouro().split(",")[0]
							.equalsIgnoreCase(getString(R.string.formulario3_opcao_outro))) {
						radioButton23.setChecked(true);
						outros3.setText(Data.listaFormularioTres
								.get(paginaFragment).getTipoLogradouro()
								.split(",")[1]);
					}
				}
			}

			alert.setTitle(getString(R.string.formulario3_tipo_logradouro));
			alert.setView(layout8);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (radioButton18.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_avenida),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton19.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_rua),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton20.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_alameda),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton21.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_rodovia),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton22.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_travessa),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton23.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_outro)
												+ ","
												+ outros3.getText().toString(),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();

			break;
		case VariloidForm3.NOME_LOGRADOURO:
			final EditText inputNomeLogradouro = new EditText(
					getSherlockActivity());
			int maxLength4 = 90;
			inputNomeLogradouro
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength4) });

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getNomeLogradouro()))
				inputNomeLogradouro.setText(Data.listaFormularioTres.get(
						paginaFragment).getNomeLogradouro());

			alert.setTitle(getString(R.string.formulario3_nome_logradouro));
			alert.setView(inputNomeLogradouro);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(inputNomeLogradouro
									.getText().toString())) {
								setPreferences(arg2, inputNomeLogradouro
										.getText().toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.QUADRA:
			final EditText inputQuadra = new EditText(getSherlockActivity());
			int maxLength5 = 10;
			inputQuadra
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength5) });

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getQuadra()))
				inputQuadra.setText(Data.listaFormularioTres
						.get(paginaFragment).getQuadra());

			alert.setTitle(getString(R.string.formulario3_qudra));
			alert.setView(inputQuadra);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(inputQuadra.getText()
									.toString())) {
								setPreferences(arg2, inputQuadra.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.LOTE:
			final EditText inputLote = new EditText(getSherlockActivity());
			int maxLength6 = 8;
			inputLote
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength6) });

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getLote()))
				inputLote.setText(Data.listaFormularioTres.get(paginaFragment)
						.getLote());

			alert.setTitle(getString(R.string.formulario3_lote));
			alert.setView(inputLote);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(inputLote.getText()
									.toString())) {
								setPreferences(arg2, inputLote.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.NUMERO:
			final EditText inputNumero = new EditText(getSherlockActivity());
			int maxLength7 = 8;
			inputNumero
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength7) });
			inputNumero.setInputType(InputType.TYPE_CLASS_NUMBER);

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getNumero()))
				inputNumero.setText(Data.listaFormularioTres
						.get(paginaFragment).getNumero());

			alert.setTitle(getString(R.string.formulario3_numero));
			alert.setView(inputNumero);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(inputNumero.getText()
									.toString())) {
								setPreferences(arg2, inputNumero.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.COMPLEMENTO:
			final EditText inputComplemento = new EditText(
					getSherlockActivity());
			int maxLength8 = 20;
			inputComplemento
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength8) });

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getComplemento()))
				inputComplemento.setText(Data.listaFormularioTres.get(
						paginaFragment).getComplemento());

			alert.setTitle(getString(R.string.formulario3_complemento));
			alert.setView(inputComplemento);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(inputComplemento.getText()
									.toString())) {
								setPreferences(arg2, inputComplemento.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.BAIRRO:
			final EditText inputBairro = new EditText(getSherlockActivity());
			int maxLength9 = 50;
			inputBairro
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength9) });

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getBairro()))
				inputBairro.setText(Data.listaFormularioTres
						.get(paginaFragment).getBairro());

			alert.setTitle(getString(R.string.formulario3_bairro));
			alert.setView(inputBairro);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(inputBairro.getText()
									.toString())) {
								setPreferences(arg2, inputBairro.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		
		case VariloidForm3.MUNICIPIO:
			View layoutMunicipio = layoutInflater.inflate(R.layout.group_box_formulario, null);
			
			final RadioButton radioButtonSp = (RadioButton) layoutMunicipio.findViewById(R.id.radio1);
			radioButtonSp.setText(getString(R.string.formulario3_opcao_sao_paulo));
			final RadioButton radioButtonGo = (RadioButton) layoutMunicipio.findViewById(R.id.radio2);
			radioButtonGo.setText(getString(R.string.formulario3_opcao_goiania));
			final RadioButton radioButtonTab = (RadioButton) layoutMunicipio.findViewById(R.id.radio3);
			radioButtonTab.setVisibility(View.VISIBLE);
			radioButtonTab.setText(getString(R.string.formulario3_opcao_taboao));
			final RadioButton radioButtonRe = (RadioButton) layoutMunicipio.findViewById(R.id.radio4);
			radioButtonRe.setVisibility(View.VISIBLE);
			radioButtonRe.setText(getString(R.string.formulario3_opcao_recife));
			
			if(!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getMunicipio())){
				
				if(Data.listaFormularioTres.get(paginaFragment)
						.getMunicipio().indexOf(getString(R.string.formulario3_opcao_sao_paulo))!=-1){
					radioButtonSp.setChecked(true);
				}else if(Data.listaFormularioTres.get(paginaFragment)
						.getMunicipio().indexOf(getString(R.string.formulario3_opcao_goiania))!=-1){
					radioButtonGo.setChecked(true);
				}else if(Data.listaFormularioTres.get(paginaFragment)
						.getMunicipio().indexOf(getString(R.string.formulario3_opcao_taboao))!=-1){
					radioButtonTab.setChecked(true);
				}else if(Data.listaFormularioTres.get(paginaFragment)
						.getMunicipio().indexOf(getString(R.string.formulario3_opcao_recife))!=-1){
					radioButtonRe.setChecked(true);
				}
			}
					
			alert.setTitle(getString(R.string.formulario2_municipio));
			alert.setView(layoutMunicipio);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int whichButton) {
					
					if(radioButtonSp.isChecked()){
						setPreferences(arg2, getString(R.string.formulario3_opcao_sao_paulo) , false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonGo.isChecked()){
						setPreferences(arg2, getString(R.string.formulario3_opcao_goiania) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonTab.isChecked()){
						setPreferences(arg2, getString(R.string.formulario3_opcao_taboao) , false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonRe.isChecked()){
						setPreferences(arg2, getString(R.string.formulario3_opcao_recife) , false, true);
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
			
		case VariloidForm3.TCLE:
			View layout10 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton26 = (RadioButton) layout10
					.findViewById(R.id.radio1);
			radioButton26.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton27 = (RadioButton) layout10
					.findViewById(R.id.radio2);
			radioButton27.setText(getString(R.string.formulario3_opcao_nao));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getTcleAssinado())) {
				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getTcleAssinado()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton26.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getTcleAssinado()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton27.setChecked(true);
				}

			}

			alert.setTitle(getString(R.string.formulario3_tcle));
			alert.setView(layout10);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton26.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);

//								for (int i = VariloidForm3.NOME_MAE; i < VariloidForm3.campos.length; i++) {
//									setPreferences(i, "", false, false);
//								}
//								for (int i = VariloidForm3.NOME_MAE; i < VariloidForm3.campos.length; i++) {
//									myAdapter.updateItens(i,
//											VariloidForm3.campos[i]);
//								}
//								myAdapter.notifyDataSetChanged();

							} else if (radioButton27.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);

//								for (int i = VariloidForm3.NOME_MAE; i < VariloidForm3.campos.length; i++) {
//									setPreferences(i, "", true, false);
//								}
//								for (int i = VariloidForm3.NOME_MAE; i < VariloidForm3.campos.length; i++) {
//									myAdapter.updateItens(i,
//											VariloidForm3.campos[i]);
//								}
//								myAdapter.notifyDataSetChanged();

							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.NOME_MAE:
			final EditText inputNomeMae = new EditText(getSherlockActivity());
			int maxLength10 = 90;
			inputNomeMae
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength10) });

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getNomeMae()))
				inputNomeMae.setText(Data.listaFormularioTres.get(
						paginaFragment).getNomeMae());

			alert.setTitle(getString(R.string.formulario3_nome_mae));
			alert.setView(inputNomeMae);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(inputNomeMae.getText()
									.toString())) {
								setPreferences(arg2, inputNomeMae.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.IDADE_MAE:
			final EditText inputIdadeMae = new EditText(getSherlockActivity());
			int maxLength11 = 2;
			inputIdadeMae
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength11) });
			inputIdadeMae.setInputType(InputType.TYPE_CLASS_NUMBER);

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getIdadeMae()))
				inputIdadeMae.setText(Data.listaFormularioTres.get(
						paginaFragment).getIdadeMae());

			alert.setTitle(getString(R.string.formulario3_idade_mae));
			alert.setView(inputIdadeMae);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(inputIdadeMae.getText()
									.toString())) {
								setPreferences(arg2, inputIdadeMae.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.NOME_PAI:
			final EditText inputNomePai = new EditText(getSherlockActivity());
			int maxLength12 = 90;
			inputNomePai
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength12) });

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getNomePai()))
				inputNomePai.setText(Data.listaFormularioTres.get(
						paginaFragment).getNomePai());

			alert.setTitle(getString(R.string.formulario3_nome_pai));
			alert.setView(inputNomePai);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(inputNomePai.getText()
									.toString())) {
								setPreferences(arg2, inputNomePai.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.TELEFONE:
			final EditText inputTelFixo = new EditText(getSherlockActivity());
			int maxLength13 = 20;
			inputTelFixo
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength13) });
			inputTelFixo.setInputType(InputType.TYPE_CLASS_PHONE);

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getTelefoneFixo()))
				inputTelFixo.setText(Data.listaFormularioTres.get(
						paginaFragment).getTelefoneFixo());

			alert.setTitle(getString(R.string.formulario3_tel_fixo));
			alert.setView(inputTelFixo);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(inputTelFixo.getText()
									.toString())) {
								setPreferences(arg2, inputTelFixo.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.CELULAR1:
			final EditText NumeroCelular1 = new EditText(getSherlockActivity());
			int maxLengthCel1 = 20;
			NumeroCelular1
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLengthCel1) });
			NumeroCelular1.setInputType(InputType.TYPE_CLASS_PHONE);

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getCelular1()))
				NumeroCelular1.setText(Data.listaFormularioTres.get(
						paginaFragment).getCelular1());

			alert.setTitle(getString(R.string.formulario3_tel_celular1));
			alert.setView(NumeroCelular1);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(NumeroCelular1.getText()
									.toString())) {
								setPreferences(arg2, NumeroCelular1.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.OPER_CELULAR1:
			View layout11 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);

			final RadioButton radioButton28 = (RadioButton) layout11
					.findViewById(R.id.radio1);
			radioButton28
					.setText(getString(R.string.formulario3_operadoras_tim));
			final RadioButton radioButton29 = (RadioButton) layout11
					.findViewById(R.id.radio2);
			radioButton29
					.setText(getString(R.string.formulario3_operadoras_claro));
			final RadioButton radioButton30 = (RadioButton) layout11
					.findViewById(R.id.radio3);
			radioButton30.setVisibility(View.VISIBLE);
			radioButton30
					.setText(getString(R.string.formulario3_operadoras_oi));
			final RadioButton radioButton31 = (RadioButton) layout11
					.findViewById(R.id.radio4);
			radioButton31.setVisibility(View.VISIBLE);
			radioButton31
					.setText(getString(R.string.formulario3_operadoras_vivo));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getOperadoraCelular1())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getOperadoraCelular1()
						.indexOf(getString(R.string.formulario3_operadoras_tim)) != -1) {
					radioButton28.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getOperadoraCelular1()
						.indexOf(
								getString(R.string.formulario3_operadoras_claro)) != -1) {
					radioButton29.setChecked(true);
				} else if (Data.listaFormularioTres.get(paginaFragment)
						.getOperadoraCelular1()
						.indexOf(getString(R.string.formulario3_operadoras_oi)) != -1) {
					radioButton30.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getOperadoraCelular1()
						.indexOf(
								getString(R.string.formulario3_operadoras_vivo)) != -1) {
					radioButton31.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_operadora1));
			alert.setView(layout11);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton28.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_operadoras_tim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton29.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_operadoras_claro),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton30.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_operadoras_oi),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton31.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_operadoras_vivo),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.CELULAR2:
			final EditText NumeroCelular2 = new EditText(getSherlockActivity());
			int maxLengthCel2 = 20;
			NumeroCelular2
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLengthCel2) });
			NumeroCelular2.setInputType(InputType.TYPE_CLASS_PHONE);

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getCelular2()))
				NumeroCelular2.setText(Data.listaFormularioTres.get(
						paginaFragment).getCelular2());

			alert.setTitle(getString(R.string.formulario3_tel_celular2));
			alert.setView(NumeroCelular2);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(NumeroCelular2.getText()
									.toString())) {
								setPreferences(arg2, NumeroCelular2.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.OPER_CELULAR2:
			View layout12 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);

			final RadioButton radioButton32 = (RadioButton) layout12
					.findViewById(R.id.radio1);
			radioButton32
					.setText(getString(R.string.formulario3_operadoras_tim));
			final RadioButton radioButton33 = (RadioButton) layout12
					.findViewById(R.id.radio2);
			radioButton33
					.setText(getString(R.string.formulario3_operadoras_claro));
			final RadioButton radioButton34 = (RadioButton) layout12
					.findViewById(R.id.radio3);
			radioButton34.setVisibility(View.VISIBLE);
			radioButton34
					.setText(getString(R.string.formulario3_operadoras_oi));
			final RadioButton radioButton35 = (RadioButton) layout12
					.findViewById(R.id.radio4);
			radioButton35.setVisibility(View.VISIBLE);
			radioButton35
					.setText(getString(R.string.formulario3_operadoras_vivo));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getOperadoraCelular2())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getOperadoraCelular2()
						.indexOf(getString(R.string.formulario3_operadoras_tim)) != -1) {
					radioButton32.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getOperadoraCelular2()
						.indexOf(
								getString(R.string.formulario3_operadoras_claro)) != -1) {
					radioButton33.setChecked(true);
				} else if (Data.listaFormularioTres.get(paginaFragment)
						.getOperadoraCelular2()
						.indexOf(getString(R.string.formulario3_operadoras_oi)) != -1) {
					radioButton34.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getOperadoraCelular2()
						.indexOf(
								getString(R.string.formulario3_operadoras_vivo)) != -1) {
					radioButton35.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_operadora2));
			alert.setView(layout12);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton32.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_operadoras_tim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton33.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_operadoras_claro),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton34.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_operadoras_oi),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton35.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_operadoras_vivo),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.COBERTURA_PSF:
			View layout13 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton36 = (RadioButton) layout13
					.findViewById(R.id.radio1);
			radioButton36.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton37 = (RadioButton) layout13
					.findViewById(R.id.radio2);
			radioButton37.setText(getString(R.string.formulario3_opcao_nao));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getCriancaCadastradaPSF())) {
				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getCriancaCadastradaPSF()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton36.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getCriancaCadastradaPSF()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton37.setChecked(true);
				}

			}

			alert.setTitle(getString(R.string.formulario3_cobertura_psf));
			alert.setView(layout13);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton36.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);

							} else if (radioButton37.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);

							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		
		case VariloidForm3.VACVARICELA:
			View layoutVacVaricela = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButtonVacSim = (RadioButton) layoutVacVaricela.findViewById(R.id.radio1);
			radioButtonVacSim.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButtonVacNao = (RadioButton) layoutVacVaricela.findViewById(R.id.radio2);
			radioButtonVacNao.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButtonVacNaoSabe = (RadioButton) layoutVacVaricela.findViewById(R.id.radio3);
			radioButtonVacNaoSabe.setVisibility(View.VISIBLE);
			radioButtonVacNaoSabe.setText(getString(R.string.formulario3_opcao_nao_sabe_e_nao_tem_cartao));
			
			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getCriancaRecebeuVacinaCatapora())) {
				
				if(Data.listaFormularioTres.get(paginaFragment)
						.getCriancaRecebeuVacinaCatapora().equalsIgnoreCase(getString(R.string.formulario3_opcao_sim))){
					radioButtonVacSim.setChecked(true);
				}else if(Data.listaFormularioTres.get(paginaFragment)
						.getCriancaRecebeuVacinaCatapora().equalsIgnoreCase(getString(R.string.formulario3_opcao_nao))){
					radioButtonVacNao.setChecked(true);
				}else if(Data.listaFormularioTres.get(paginaFragment)
						.getCriancaRecebeuVacinaCatapora().equalsIgnoreCase(getString(R.string.formulario3_opcao_nao_sabe_e_nao_tem_cartao))){
					radioButtonVacNaoSabe.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario3_crianca_recebeu_vacina_catapora));
			alert.setView(layoutVacVaricela);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButtonVacSim.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonVacNao.isChecked()){
						setPreferences(arg2, getString(R.string.formulario2_opcao_nao), false, true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
					}else if(radioButtonVacNaoSabe.isChecked()){
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
		case VariloidForm3.MOTIVO_NAO_VACINACAO:
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
							
			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getRazoesNaoVacinacao())) {		
				
				if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao1))!=-1){
					checkbox1.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao2))!=-1){
					checkbox2.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao3))!=-1){
					checkbox3.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao4))!=-1){
					checkbox4.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao5))!=-1){
					checkbox5.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao6))!=-1){
					checkbox6.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao7))!=-1){
					checkbox7.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao8))!=-1){
					checkbox8.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao9))!=-1){
					checkbox9.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao10))!=-1){
					checkbox10.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao11))!=-1){
					checkbox11.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao12))!=-1){
					checkbox12.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao13))!=-1){
					checkbox13.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao14))!=-1){
					checkbox14.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_motivo_nao_vacinacao15))!=-1){
					checkbox15.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_outro_check))!=-1){
					checkbox16.setChecked(true);
					int posicao = Data.listaFormularioTres.get(paginaFragment)
							.getRazoesNaoVacinacao().indexOf(getString(R.string.formulario2_opcao_outro_check));
					editText.setText(Data.listaFormularioTres.get(paginaFragment)
							.getRazoesNaoVacinacao().substring(posicao + getString(R.string.formulario2_opcao_outro_check).length(), Data.listaFormularioTres.get(paginaFragment)
									.getRazoesNaoVacinacao().length()));					
				}
			}
			
			alert.setTitle(getString(R.string.formulario3_razoes_nao_vacinacao));
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
			
		case VariloidForm3.PESONASCER:
			final EditText pesoNascer = new EditText(getSherlockActivity());
			int maxLength16 = 4;
			pesoNascer
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength16) });
			pesoNascer.setInputType(InputType.TYPE_CLASS_NUMBER);

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getPesoNascimentoGramas()))
				pesoNascer.setText(Data.listaFormularioTres.get(paginaFragment)
						.getPesoNascimentoGramas());

			alert.setTitle(getString(R.string.formulario3_peso_nascer));
			alert.setView(pesoNascer);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(pesoNascer.getText()
									.toString())) {
								setPreferences(arg2, pesoNascer.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.IDADEGESTACIONAL:
			final EditText idadeGestacional = new EditText(
					getSherlockActivity());
			int maxLength17 = 3;
			idadeGestacional
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength17) });
			idadeGestacional.setInputType(InputType.TYPE_CLASS_NUMBER);

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getIdadeGestacional()))
				idadeGestacional.setText(Data.listaFormularioTres.get(
						paginaFragment).getIdadeGestacional());

			alert.setTitle(getString(R.string.formulario3_idade_gestacional));
			alert.setView(idadeGestacional);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(idadeGestacional.getText()
									.toString())) {
								setPreferences(arg2, idadeGestacional.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.RACA:
			View layout26 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton76 = (RadioButton) layout26
					.findViewById(R.id.radio1);
			radioButton76.setText(getString(R.string.formulario3_opcao_branca));
			final RadioButton radioButton77 = (RadioButton) layout26
					.findViewById(R.id.radio2);
			radioButton77.setText(getString(R.string.formulario3_opcao_parda));
			final RadioButton radioButton78 = (RadioButton) layout26
					.findViewById(R.id.radio3);
			radioButton78.setText(getString(R.string.formulario3_opcao_negra));
			radioButton78.setVisibility(View.VISIBLE);
			final RadioButton radioButton79 = (RadioButton) layout26
					.findViewById(R.id.radio4);
			radioButton79
					.setText(getString(R.string.formulario3_opcao_asiatica));
			radioButton79.setVisibility(View.VISIBLE);
			final RadioButton radioButton80 = (RadioButton) layout26
					.findViewById(R.id.radio5);
			radioButton80
					.setText(getString(R.string.formulario3_opcao_indigena));
			radioButton80.setVisibility(View.VISIBLE);

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getRaca())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getRaca()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_branca))) {
					radioButton76.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getRaca()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_parda))) {
					radioButton77.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getRaca()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_negra))) {
					radioButton78.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getRaca()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_asiatica))) {
					radioButton79.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getRaca()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_indigena))) {
					radioButton80.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_raca));
			alert.setView(layout26);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton76.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_branca),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton77.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_parda),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton78.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_negra),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton79.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_asiatica),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton80.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_indigena),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.N_DOSES_VARIC:
			final EditText inputNDosesVaric = new EditText(
					getSherlockActivity());
			int maxLength18 = 2;
			inputNDosesVaric
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength18) });
			inputNDosesVaric.setInputType(InputType.TYPE_CLASS_NUMBER);

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getNumDosesRecebidasContraVaricela()))
				inputNDosesVaric.setText(Data.listaFormularioTres.get(
						paginaFragment).getNumDosesRecebidasContraVaricela());

			alert.setTitle(getString(R.string.formulario3_n_doses_varic));
			alert.setView(inputNDosesVaric);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(inputNDosesVaric.getText()
									.toString())) {
								if (inputNDosesVaric.getText().toString()
										.equalsIgnoreCase("0")) {
									setPreferences(arg2, inputNDosesVaric
											.getText().toString(), false, true);
									((ImageView) view
											.findViewById(R.id.nome_img_check))
											.setImageResource(R.drawable.ic_certo);

//									for (int i = VariloidForm3.VAC_VARIC_DT_DOSE1; i < VariloidForm3.TITULO_VACINA_CONTRA_VARICELA; i++) {
//										setPreferences(i, "", true, false);
//									}
//									for (int i = VariloidForm3.TCLE; i < VariloidForm3.campos.length; i++) {
//										myAdapter.updateItens(i,
//												VariloidForm3.campos[i]);
//									}
//									myAdapter.notifyDataSetChanged();
								} else {
									setPreferences(arg2, inputNDosesVaric
											.getText().toString(), false, true);
									((ImageView) view
											.findViewById(R.id.nome_img_check))
											.setImageResource(R.drawable.ic_certo);

//									for (int i = VariloidForm3.VAC_VARIC_DT_DOSE1; i < VariloidForm3.TITULO_VACINA_CONTRA_VARICELA; i++) {
//										setPreferences(i, "", false, false);
//									}
//									for (int i = VariloidForm3.TCLE; i < VariloidForm3.campos.length; i++) {
//										myAdapter.updateItens(i,
//												VariloidForm3.campos[i]);
//									}
//									myAdapter.notifyDataSetChanged();
								}

								// setPreferences(arg2,
								// inputNDosesVaric.getText().toString(), false,
								// true);
								// ((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);

//								for (int i = VariloidForm3.VAC_VARIC_DT_DOSE1; i < VariloidForm3.TITULO_VACINA_CONTRA_VARICELA; i++) {
//									setPreferences(i, "", false, false);
//								}
//								for (int i = VariloidForm3.TCLE; i < VariloidForm3.campos.length; i++) {
//									myAdapter.updateItens(i,
//											VariloidForm3.campos[i]);
//								}
//								myAdapter.notifyDataSetChanged();
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.VAC_VARIC_DT_DOSE1:
			final EditText vacVaricDtDoses1 = new EditText(
					getSherlockActivity());
			vacVaricDtDoses1.setInputType(InputType.TYPE_CLASS_DATETIME);
			vacVaricDtDoses1.addTextChangedListener(Mask.insert("##/##/####",
					vacVaricDtDoses1));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getDataPrimeiraDose())) {
				String replaceCampo = Data.listaFormularioTres
						.get(paginaFragment).getDataPrimeiraDose()
						.replaceAll("/", "");
				vacVaricDtDoses1.setText(replaceCampo);
			}

			alert.setTitle(getString(R.string.formulario3_vac_varic_dt_dose1));
			alert.setView(vacVaricDtDoses1);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(vacVaricDtDoses1.getText()
									.toString())) {
								setPreferences(arg2, vacVaricDtDoses1.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.VAC_VARIC_QUAL_DOSE1:
			View layout29 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton84 = (RadioButton) layout29
					.findViewById(R.id.radio1);
			radioButton84
					.setText(getString(R.string.formulario3_opcao_vacina_tetraviral));
			final RadioButton radioButton85 = (RadioButton) layout29
					.findViewById(R.id.radio2);
			radioButton85
					.setText(getString(R.string.formulario3_opcao_vacina_glaxo_varilrix));
			final RadioButton radioButton86 = (RadioButton) layout29
					.findViewById(R.id.radio3);
			radioButton86.setVisibility(View.VISIBLE);
			radioButton86
					.setText(getString(R.string.formulario3_opcao_vacina_merck_varivax));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getQualVacinaUsadaPrimeiraDose())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getQualVacinaUsadaPrimeiraDose()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_vacina_tetraviral))) {
					radioButton84.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getQualVacinaUsadaPrimeiraDose()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_vacina_glaxo_varilrix))) {
					radioButton85.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getQualVacinaUsadaPrimeiraDose()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_vacina_merck_varivax))) {
					radioButton86.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_vac_varic_qual_dose1));
			alert.setView(layout29);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton84.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_vacina_tetraviral),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton85.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_vacina_glaxo_varilrix),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton86.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_vacina_merck_varivax),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.VAC_VARIC_DT_DOSE2:
			final EditText vacVaricDtDoses2 = new EditText(
					getSherlockActivity());
			vacVaricDtDoses2.setInputType(InputType.TYPE_CLASS_DATETIME);
			vacVaricDtDoses2.addTextChangedListener(Mask.insert("##/##/####",
					vacVaricDtDoses2));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getDataSegundaDose())) {
				String replaceCampo = Data.listaFormularioTres
						.get(paginaFragment).getDataSegundaDose()
						.replaceAll("/", "");
				vacVaricDtDoses2.setText(replaceCampo);
			}

			alert.setTitle(getString(R.string.formulario3_vac_varic_dt_dose2));
			alert.setView(vacVaricDtDoses2);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(vacVaricDtDoses2.getText()
									.toString())) {
								setPreferences(arg2, vacVaricDtDoses2.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.VAC_VARIC_QUAL_DOSE2:
			View layout30 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton87 = (RadioButton) layout30
					.findViewById(R.id.radio1);
			radioButton87
					.setText(getString(R.string.formulario3_opcao_vacina_tetraviral));
			final RadioButton radioButton88 = (RadioButton) layout30
					.findViewById(R.id.radio2);
			radioButton88
					.setText(getString(R.string.formulario3_opcao_vacina_glaxo_varilrix));
			final RadioButton radioButton89 = (RadioButton) layout30
					.findViewById(R.id.radio3);
			radioButton89.setVisibility(View.VISIBLE);
			radioButton89
					.setText(getString(R.string.formulario3_opcao_vacina_merck_varivax));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getQualVacinaUsadaPrimeiraDose())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getQualVacinaUsadaSegundaDose()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_vacina_tetraviral))) {
					radioButton87.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getQualVacinaUsadaSegundaDose()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_vacina_glaxo_varilrix))) {
					radioButton88.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getQualVacinaUsadaSegundaDose()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_vacina_merck_varivax))) {
					radioButton89.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_vac_varic_qual_dose2));
			alert.setView(layout30);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton87.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_vacina_tetraviral),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton88.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_vacina_glaxo_varilrix),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton89.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_vacina_merck_varivax),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.VACTRIPLICE:

			View layout272 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton812 = (RadioButton) layout272
					.findViewById(R.id.radio1);
			radioButton812.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton822 = (RadioButton) layout272
					.findViewById(R.id.radio2);
			radioButton822.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton832 = (RadioButton) layout272
					.findViewById(R.id.radio3);
			radioButton832.setVisibility(View.VISIBLE);
			radioButton832
					.setText(getString(R.string.formulario3_opcao_ignorado));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getQualVacinaUsadaPrimeiraDose())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getCriancaRecebeuVacinaTripliceViral()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton812.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getCriancaRecebeuVacinaTripliceViral()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton822.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getCriancaRecebeuVacinaTripliceViral()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_ignorado))) {
					radioButton832.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_vac_triplice_viral));
			alert.setView(layout272);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton812.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton822.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton832.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_ignorado),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();

			break;
		case VariloidForm3.N_DOSES_TRIPLICE:

			final EditText inputNDosesTriplice = new EditText(
					getSherlockActivity());
			int maxLength181 = 2;
			inputNDosesTriplice
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength181) });
			inputNDosesTriplice.setInputType(InputType.TYPE_CLASS_NUMBER);

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getNumDosesRecebidasContraTripliceViral()))
				inputNDosesTriplice.setText(Data.listaFormularioTres.get(
						paginaFragment)
						.getNumDosesRecebidasContraTripliceViral());

			alert.setTitle(getString(R.string.formulario3_n_doses_triplice_viral));
			alert.setView(inputNDosesTriplice);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(inputNDosesTriplice
									.getText().toString())) {
								setPreferences(arg2, inputNDosesTriplice
										.getText().toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;

		case VariloidForm3.MMR1:
			final EditText dataMmr1 = new EditText(getSherlockActivity());
			dataMmr1.setInputType(InputType.TYPE_CLASS_DATETIME);
			dataMmr1.addTextChangedListener(Mask.insert("##/##/####", dataMmr1));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getDataPrimeiraDoseMMR())) {
				String replaceCampo = Data.listaFormularioTres
						.get(paginaFragment).getDataPrimeiraDoseMMR()
						.replaceAll("/", "");
				dataMmr1.setText(replaceCampo);
			}

			alert.setTitle(getString(R.string.formulario3_mmr1));
			alert.setView(dataMmr1);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(dataMmr1.getText()
									.toString())) {
								setPreferences(arg2, dataMmr1.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.MMR2:
			final EditText dataMmr2 = new EditText(getSherlockActivity());
			dataMmr2.setInputType(InputType.TYPE_CLASS_DATETIME);
			dataMmr2.addTextChangedListener(Mask.insert("##/##/####", dataMmr2));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getDataSegundaDoseMMR())) {
				String replaceCampo = Data.listaFormularioTres
						.get(paginaFragment).getDataSegundaDoseMMR()
						.replaceAll("/", "");
				dataMmr2.setText(replaceCampo);
			}

			alert.setTitle(getString(R.string.formulario3_mmr2));
			alert.setView(dataMmr2);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(dataMmr2.getText()
									.toString())) {
								setPreferences(arg2, dataMmr2.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.FREQCRECHE1:
			View layout31 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton90 = (RadioButton) layout31
					.findViewById(R.id.radio1);
			radioButton90.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton91 = (RadioButton) layout31
					.findViewById(R.id.radio2);
			radioButton91.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton92 = (RadioButton) layout31
					.findViewById(R.id.radio3);
			radioButton92.setVisibility(View.VISIBLE);
			radioButton92
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getAlguemFrequentouCreche())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getAlguemFrequentouCreche()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton90.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getAlguemFrequentouCreche()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton91.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getAlguemFrequentouCreche()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton92.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_freq_creche1));
			alert.setView(layout31);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton90.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton91.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton92.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.FREQCRECHE2:
			
			View layout32 = layoutInflater.inflate(R.layout.checkbox_formulario, null);
			
			final CheckBox checkbox31 = (CheckBox) layout32.findViewById(R.id.checkBox1);
			checkbox31.setText(getString(R.string.formulario3_opcao_crianca_com_catapora));
			final CheckBox checkbox32 = (CheckBox) layout32.findViewById(R.id.checkBox2);
			checkbox32.setText(getString(R.string.formulario3_opcao_pais_outra_crianca_menos_que_dez_anos));
			final CheckBox checkbox33 = (CheckBox) layout32.findViewById(R.id.checkBox3);
			checkbox33.setText(getString(R.string.formulario3_opcao_familia_trabalha_creche));
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
			
			if(!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getQuemFamiliaFrequentouCreche())){
				if(Data.listaFormularioTres.get(paginaFragment)
						.getQuemFamiliaFrequentouCreche().indexOf(getString(R.string.formulario3_opcao_crianca_com_catapora))!=-1){
					checkbox31.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getQuemFamiliaFrequentouCreche().indexOf(getString(R.string.formulario3_opcao_pais_outra_crianca_menos_que_dez_anos))!=-1){
					checkbox32.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getQuemFamiliaFrequentouCreche().indexOf(getString(R.string.formulario3_opcao_familia_trabalha_creche))!=-1){
					checkbox33.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario3_freq_creche2));
			alert.setView(layout32);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				List<String> lisStrings = new ArrayList<String>();
				String valor;
				public void onClick(DialogInterface dialog, int whichButton) {
					if(checkbox31.isChecked()){
						lisStrings.add(getString(R.string.formulario3_opcao_crianca_com_catapora));
					}
					if(checkbox32.isChecked()){
						lisStrings.add(getString(R.string.formulario3_opcao_pais_outra_crianca_menos_que_dez_anos));
					}
					if(checkbox33.isChecked()){
						lisStrings.add(getString(R.string.formulario3_opcao_familia_trabalha_creche));
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
		case VariloidForm3.FREQSERVSAUDE1:
			View layout33 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton96 = (RadioButton) layout33
					.findViewById(R.id.radio1);
			radioButton96.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton97 = (RadioButton) layout33
					.findViewById(R.id.radio2);
			radioButton97.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton98 = (RadioButton) layout33
					.findViewById(R.id.radio3);
			radioButton98.setVisibility(View.VISIBLE);
			radioButton98
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getAlguemFrequentouServicoSaude())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getAlguemFrequentouServicoSaude()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton96.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getAlguemFrequentouServicoSaude()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton97.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getAlguemFrequentouServicoSaude()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton98.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_freq_serv_saude1));
			alert.setView(layout33);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton96.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton97.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton98.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.FREQSERVSAUDE2:
			
			View layout34 = layoutInflater.inflate(R.layout.checkbox_formulario, null);
			
			final CheckBox checkbox311 = (CheckBox) layout34.findViewById(R.id.checkBox1);
			checkbox311.setText(getString(R.string.formulario3_opcao_crianca_com_catapora));
			final CheckBox checkbox312 = (CheckBox) layout34.findViewById(R.id.checkBox2);
			checkbox312.setText(getString(R.string.formulario3_opcao_criancas_ate_dez_anos));
			final CheckBox checkbox313 = (CheckBox) layout34.findViewById(R.id.checkBox3);
			checkbox313.setText(getString(R.string.formulario3_opcao_familia_trabalha_em_instituicao_de_saude));
			final CheckBox checkbox314 = (CheckBox) layout34.findViewById(R.id.checkBox4);
			checkbox314.setText(getString(R.string.formulario3_opcao_outro_adulto));
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
			
			if(!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getQuemFamiliaFrequentouServicoSaude())){			
				if(Data.listaFormularioTres.get(paginaFragment)
						.getQuemFamiliaFrequentouServicoSaude().indexOf(getString(R.string.formulario3_opcao_crianca_com_catapora))!=-1){
					checkbox311.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getQuemFamiliaFrequentouServicoSaude().indexOf(getString(R.string.formulario3_opcao_criancas_ate_dez_anos))!=-1){
					checkbox312.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getQuemFamiliaFrequentouServicoSaude().indexOf(getString(R.string.formulario3_opcao_familia_trabalha_em_instituicao_de_saude))!=-1){
					checkbox313.setChecked(true);
				}if(Data.listaFormularioTres.get(paginaFragment)
						.getQuemFamiliaFrequentouServicoSaude().indexOf(getString(R.string.formulario3_opcao_outro_adulto))!=-1){
					checkbox314.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario3_freq_serv_saude2));
			alert.setView(layout34);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				List<String> lisStrings = new ArrayList<String>();
				String valor;
				public void onClick(DialogInterface dialog, int whichButton) {
					if(checkbox311.isChecked()){
						lisStrings.add(getString(R.string.formulario3_opcao_crianca_com_catapora));
					}
					if(checkbox312.isChecked()){
						lisStrings.add(getString(R.string.formulario3_opcao_criancas_ate_dez_anos));
					}
					if(checkbox313.isChecked()){
						lisStrings.add(getString(R.string.formulario3_opcao_familia_trabalha_em_instituicao_de_saude));
					}
					if(checkbox314.isChecked()){
						lisStrings.add(getString(R.string.formulario3_opcao_outro_adulto));
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
		case VariloidForm3.HOSPITALIZACAO:
			View layout35 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton102 = (RadioButton) layout35
					.findViewById(R.id.radio1);
			radioButton102.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton103 = (RadioButton) layout35
					.findViewById(R.id.radio2);
			radioButton103.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton104 = (RadioButton) layout35
					.findViewById(R.id.radio3);
			radioButton104.setVisibility(View.VISIBLE);
			radioButton104
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getCriancaInternou())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getCriancaInternou()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton102.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getCriancaInternou()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton103.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getCriancaInternou()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton104.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_hospitalizacao));
			alert.setView(layout35);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton102.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton103.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton104.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
//		case VariloidForm3.MAE_TRABALHA:
//			View layout36 = layoutInflater.inflate(
//					R.layout.group_box_formulario, null);
//			final RadioButton radioButton105 = (RadioButton) layout36
//					.findViewById(R.id.radio1);
//			radioButton105.setText(getString(R.string.formulario3_opcao_sim));
//			final RadioButton radioButton106 = (RadioButton) layout36
//					.findViewById(R.id.radio2);
//			radioButton106.setText(getString(R.string.formulario3_opcao_nao));
//			final RadioButton radioButton107 = (RadioButton) layout36
//					.findViewById(R.id.radio3);
//			radioButton107.setVisibility(View.VISIBLE);
//			radioButton107
//					.setText(getString(R.string.formulario3_opcao_nao_sabe));
//
//			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
//					.getMaeTrabalhaFora())) {
//
//				if (Data.listaFormularioTres
//						.get(paginaFragment)
//						.getMaeTrabalhaFora()
//						.equalsIgnoreCase(
//								getString(R.string.formulario3_opcao_sim))) {
//					radioButton105.setChecked(true);
//				} else if (Data.listaFormularioTres
//						.get(paginaFragment)
//						.getMaeTrabalhaFora()
//						.equalsIgnoreCase(
//								getString(R.string.formulario3_opcao_nao))) {
//					radioButton106.setChecked(true);
//				} else if (Data.listaFormularioTres
//						.get(paginaFragment)
//						.getMaeTrabalhaFora()
//						.equalsIgnoreCase(
//								getString(R.string.formulario3_opcao_nao_sabe))) {
//					radioButton107.setChecked(true);
//				}
//			}
//
//			alert.setTitle(getString(R.string.formulario3_mae_trabalha));
//			alert.setView(layout36);
//			alert.setPositiveButton("Ok",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
//
//							if (radioButton105.isChecked()) {
//								setPreferences(
//										arg2,
//										getString(R.string.formulario3_opcao_sim),
//										false, true);
//								((ImageView) view
//										.findViewById(R.id.nome_img_check))
//										.setImageResource(R.drawable.ic_certo);
//							} else if (radioButton106.isChecked()) {
//								setPreferences(
//										arg2,
//										getString(R.string.formulario3_opcao_nao),
//										false, true);
//								((ImageView) view
//										.findViewById(R.id.nome_img_check))
//										.setImageResource(R.drawable.ic_certo);
//							} else if (radioButton107.isChecked()) {
//								setPreferences(
//										arg2,
//										getString(R.string.formulario3_opcao_nao_sabe),
//										false, true);
//								((ImageView) view
//										.findViewById(R.id.nome_img_check))
//										.setImageResource(R.drawable.ic_certo);
//							} else {
//								setPreferences(arg2, "", false, false);
//								((ImageView) view
//										.findViewById(R.id.nome_img_check))
//										.setImageDrawable(null);
//							}
//							dialog.cancel();
//						}
//					});
//
//			alert.setNegativeButton("Cancelar",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
//							dialog.cancel();
//						}
//					});
//			alert.show();
//			break;
		case VariloidForm3.ES_CMAE:
			View layout37 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton108 = (RadioButton) layout37
					.findViewById(R.id.radio1);
			radioButton108
					.setText(getString(R.string.formulario3_opcao_nao_alfabetizada));
			final RadioButton radioButton109 = (RadioButton) layout37
					.findViewById(R.id.radio2);
			radioButton109
					.setText(getString(R.string.formulario3_opcao_ensino_fundamental));
			final RadioButton radioButton110 = (RadioButton) layout37
					.findViewById(R.id.radio3);
			radioButton110.setVisibility(View.VISIBLE);
			radioButton110
					.setText(getString(R.string.formulario3_opcao_ensino_medio));
			final RadioButton radioButton111 = (RadioButton) layout37
					.findViewById(R.id.radio4);
			radioButton111.setVisibility(View.VISIBLE);
			radioButton111
					.setText(getString(R.string.formulario3_opcao_ensino_superior));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getEscolaridadeMae())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getEscolaridadeMae()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_alfabetizada))) {
					radioButton108.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getEscolaridadeMae()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_ensino_fundamental))) {
					radioButton109.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getEscolaridadeMae()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_ensino_medio))) {
					radioButton110.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getEscolaridadeMae()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_ensino_superior))) {
					radioButton111.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_esc_mae));
			alert.setView(layout37);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton108.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_alfabetizada),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton109.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_ensino_fundamental),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton110.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_ensino_medio),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton111.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_ensino_superior),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.NCCAS_CASA5:
			final EditText numeroCriancas1 = new EditText(getSherlockActivity());
			int maxLength19 = 2;
			numeroCriancas1
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength19) });
			numeroCriancas1.setInputType(InputType.TYPE_CLASS_NUMBER);

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getNumCriancasMenos5anosDomicilio()))
				numeroCriancas1.setText(Data.listaFormularioTres.get(
						paginaFragment).getNumCriancasMenos5anosDomicilio());

			alert.setTitle(getString(R.string.formulario3_nc_cas_casa5));
			alert.setView(numeroCriancas1);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(numeroCriancas1.getText()
									.toString())) {
								setPreferences(arg2, numeroCriancas1.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.NCCAS_CASA10:
			final EditText numeroCriancas2 = new EditText(getSherlockActivity());
			int maxLength20 = 2;
			numeroCriancas2
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength20) });
			numeroCriancas2.setInputType(InputType.TYPE_CLASS_NUMBER);

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getNumCriancasMenos10anosDomicilio()))
				numeroCriancas2.setText(Data.listaFormularioTres.get(
						paginaFragment).getNumCriancasMenos10anosDomicilio());

			alert.setTitle(getString(R.string.formulario3_nc_cas_casa10));
			alert.setView(numeroCriancas2);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(numeroCriancas2.getText()
									.toString())) {
								setPreferences(arg2, numeroCriancas2.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.QT_DE_PESSOAS_CASA:
			final EditText qtdPessoasCasa = new EditText(getSherlockActivity());
			int maxLength21 = 2;
			qtdPessoasCasa
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength21) });
			qtdPessoasCasa.setInputType(InputType.TYPE_CLASS_NUMBER);

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getQuantasPessoasCasa()))
				qtdPessoasCasa.setText(Data.listaFormularioTres.get(
						paginaFragment).getQuantasPessoasCasa());

			alert.setTitle(getString(R.string.formulario3_qtd_pessoas_casa));
			alert.setView(qtdPessoasCasa);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(qtdPessoasCasa.getText()
									.toString())) {
								setPreferences(arg2, qtdPessoasCasa.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.QTD_EPESSOAS_QTO:
			final EditText qtdPessoasQuarto = new EditText(
					getSherlockActivity());
			int maxLength22 = 2;
			qtdPessoasQuarto
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength22) });
			qtdPessoasQuarto.setInputType(InputType.TYPE_CLASS_NUMBER);

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getQuantasPessoasDormemMesmoComodoCrianca()))
				qtdPessoasQuarto.setText(Data.listaFormularioTres.get(
						paginaFragment)
						.getQuantasPessoasDormemMesmoComodoCrianca());

			alert.setTitle(getString(R.string.formulario3_qtd_pessoas_quarto));
			alert.setView(qtdPessoasQuarto);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils.isEmpty(qtdPessoasQuarto.getText()
									.toString())) {
								setPreferences(arg2, qtdPessoasQuarto.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.DOENCAFALCIFORME:
			View layout38 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton112 = (RadioButton) layout38
					.findViewById(R.id.radio1);
			radioButton112.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton113 = (RadioButton) layout38
					.findViewById(R.id.radio2);
			radioButton113.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton114 = (RadioButton) layout38
					.findViewById(R.id.radio3);
			radioButton114.setVisibility(View.VISIBLE);
			radioButton114
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getDoencaFalciforme())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaFalciforme()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton112.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaFalciforme()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton113.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaFalciforme()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton114.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_doenca_falciforme));
			alert.setView(layout38);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton112.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton113.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton114.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.PREMATURIDADE:
			View layout39 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton115 = (RadioButton) layout39
					.findViewById(R.id.radio1);
			radioButton115.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton116 = (RadioButton) layout39
					.findViewById(R.id.radio2);
			radioButton116.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton117 = (RadioButton) layout39
					.findViewById(R.id.radio3);
			radioButton117.setVisibility(View.VISIBLE);
			radioButton117
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getPrematuridade())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getPrematuridade()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton115.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getPrematuridade()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton116.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getPrematuridade()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton117.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_prematuridade));
			alert.setView(layout39);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton115.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton116.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton117.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.INFECHIV:
			View layout40 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton118 = (RadioButton) layout40
					.findViewById(R.id.radio1);
			radioButton118.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton119 = (RadioButton) layout40
					.findViewById(R.id.radio2);
			radioButton119.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton120 = (RadioButton) layout40
					.findViewById(R.id.radio3);
			radioButton120.setVisibility(View.VISIBLE);
			radioButton120
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getInfeccaoHIVeAIDS())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getInfeccaoHIVeAIDS()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton118.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getInfeccaoHIVeAIDS()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton119.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getInfeccaoHIVeAIDS()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton120.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_infec_hiv));
			alert.setView(layout40);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton118.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton119.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton120.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.IMUNODEFICIENCIA:
			View layout41 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton121 = (RadioButton) layout41
					.findViewById(R.id.radio1);
			radioButton121.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton122 = (RadioButton) layout41
					.findViewById(R.id.radio2);
			radioButton122.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton123 = (RadioButton) layout41
					.findViewById(R.id.radio3);
			radioButton123.setVisibility(View.VISIBLE);
			radioButton123
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getImunodeficiencia())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getImunodeficiencia()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton121.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getImunodeficiencia()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton122.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getImunodeficiencia()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton123.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_imuno_deficiencia));
			alert.setView(layout41);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton121.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton122.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton123.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.ASMA:
			View layout42 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton124 = (RadioButton) layout42
					.findViewById(R.id.radio1);
			radioButton124.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton125 = (RadioButton) layout42
					.findViewById(R.id.radio2);
			radioButton125.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton126 = (RadioButton) layout42
					.findViewById(R.id.radio3);
			radioButton126.setVisibility(View.VISIBLE);
			radioButton126
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getAsma())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getAsma()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton124.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getAsma()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton125.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getAsma()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton126.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_asma));
			alert.setView(layout42);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton124.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton125.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton126.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.CORTICOIDE:
			View layout43 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton127 = (RadioButton) layout43
					.findViewById(R.id.radio1);
			radioButton127.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton128 = (RadioButton) layout43
					.findViewById(R.id.radio2);
			radioButton128.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton129 = (RadioButton) layout43
					.findViewById(R.id.radio3);
			radioButton129.setVisibility(View.VISIBLE);
			radioButton129
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getUsoCorticoideSistemico())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getUsoCorticoideSistemico()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton127.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getUsoCorticoideSistemico()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton128.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getUsoCorticoideSistemico()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton129.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_corticoide));
			alert.setView(layout43);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton127.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton128.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton129.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.CANCER:
			View layout44 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton130 = (RadioButton) layout44
					.findViewById(R.id.radio1);
			radioButton130.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton131 = (RadioButton) layout44
					.findViewById(R.id.radio2);
			radioButton131.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton132 = (RadioButton) layout44
					.findViewById(R.id.radio3);
			radioButton132.setVisibility(View.VISIBLE);
			radioButton132
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getCancer())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getCancer()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton130.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getCancer()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton131.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getCancer()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton132.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_cancer));
			alert.setView(layout44);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton130.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton131.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton132.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.DOENCAPULMONARCRONICA:
			View layout45 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton133 = (RadioButton) layout45
					.findViewById(R.id.radio1);
			radioButton133.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton134 = (RadioButton) layout45
					.findViewById(R.id.radio2);
			radioButton134.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton135 = (RadioButton) layout45
					.findViewById(R.id.radio3);
			radioButton135.setVisibility(View.VISIBLE);
			radioButton135
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getDoencaPulmonarCronica())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaPulmonarCronica()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton133.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaPulmonarCronica()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton134.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaPulmonarCronica()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton135.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_doenca_pulmonar_cronica));
			alert.setView(layout45);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton133.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton134.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton135.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.DOENCARENALCRONICA:
			View layout46 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton136 = (RadioButton) layout46
					.findViewById(R.id.radio1);
			radioButton136.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton137 = (RadioButton) layout46
					.findViewById(R.id.radio2);
			radioButton137.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton138 = (RadioButton) layout46
					.findViewById(R.id.radio3);
			radioButton138.setVisibility(View.VISIBLE);
			radioButton138
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getDoencaRenalCronica())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaRenalCronica()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton136.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaRenalCronica()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton137.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaRenalCronica()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton138.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_doenca_renal_cronica));
			alert.setView(layout46);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton136.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton137.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton138.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.DOENCACARDIACA:
			View layout47 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton139 = (RadioButton) layout47
					.findViewById(R.id.radio1);
			radioButton139.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton140 = (RadioButton) layout47
					.findViewById(R.id.radio2);
			radioButton140.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton141 = (RadioButton) layout47
					.findViewById(R.id.radio3);
			radioButton141.setVisibility(View.VISIBLE);
			radioButton141
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getDoencaCardiaca())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaCardiaca()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton139.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaCardiaca()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton140.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaCardiaca()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton141.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_doenca_cardiaca));
			alert.setView(layout47);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton139.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton140.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton141.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.DOENCAHEMATOLOGICA:
			View layout48 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton142 = (RadioButton) layout48
					.findViewById(R.id.radio1);
			radioButton142.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton143 = (RadioButton) layout48
					.findViewById(R.id.radio2);
			radioButton143.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton144 = (RadioButton) layout48
					.findViewById(R.id.radio3);
			radioButton144.setVisibility(View.VISIBLE);
			radioButton144
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getDoencaHematologicaCronica())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaHematologicaCronica()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton142.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaHematologicaCronica()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton143.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaHematologicaCronica()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton144.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_doenca_hematologica));
			alert.setView(layout48);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton142.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton143.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton144.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.DOENCAHEPATICA:
			View layout49 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton145 = (RadioButton) layout49
					.findViewById(R.id.radio1);
			radioButton145.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton146 = (RadioButton) layout49
					.findViewById(R.id.radio2);
			radioButton146.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton147 = (RadioButton) layout49
					.findViewById(R.id.radio3);
			radioButton147.setVisibility(View.VISIBLE);
			radioButton147
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getDoencaHepaticaCronica())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaHepaticaCronica()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton145.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaHepaticaCronica()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton146.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDoencaHepaticaCronica()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton147.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_doenca_hepatica));
			alert.setView(layout49);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton145.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton146.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton147.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.ASPLENIA:
			View layout50 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton148 = (RadioButton) layout50
					.findViewById(R.id.radio1);
			radioButton148.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton149 = (RadioButton) layout50
					.findViewById(R.id.radio2);
			radioButton149.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton150 = (RadioButton) layout50
					.findViewById(R.id.radio3);
			radioButton150.setVisibility(View.VISIBLE);
			radioButton150
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getAspleniaCongenitaOuFuncional())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getAspleniaCongenitaOuFuncional()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton148.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getAspleniaCongenitaOuFuncional()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton149.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getAspleniaCongenitaOuFuncional()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton150.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_asplenia));
			alert.setView(layout50);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton148.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton149.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton150.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
		case VariloidForm3.DIABETES:
			View layout51 = layoutInflater.inflate(
					R.layout.group_box_formulario, null);
			final RadioButton radioButton151 = (RadioButton) layout51
					.findViewById(R.id.radio1);
			radioButton151.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButton152 = (RadioButton) layout51
					.findViewById(R.id.radio2);
			radioButton152.setText(getString(R.string.formulario3_opcao_nao));
			final RadioButton radioButton153 = (RadioButton) layout51
					.findViewById(R.id.radio3);
			radioButton153.setVisibility(View.VISIBLE);
			radioButton153
					.setText(getString(R.string.formulario3_opcao_nao_sabe));

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getDiabetes())) {

				if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDiabetes()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_sim))) {
					radioButton151.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDiabetes()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao))) {
					radioButton152.setChecked(true);
				} else if (Data.listaFormularioTres
						.get(paginaFragment)
						.getDiabetes()
						.equalsIgnoreCase(
								getString(R.string.formulario3_opcao_nao_sabe))) {
					radioButton153.setChecked(true);
				}
			}

			alert.setTitle(getString(R.string.formulario3_diabetes));
			alert.setView(layout51);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (radioButton151.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_sim),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton152.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else if (radioButton153.isChecked()) {
								setPreferences(
										arg2,
										getString(R.string.formulario3_opcao_nao_sabe),
										false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
			break;
			
		case VariloidForm3.CASO_CASA_CRIANCA:
			View layoutCasoCasaCrianca = layoutInflater.inflate(R.layout.group_box_formulario, null);
			final RadioButton radioButtonCasoSim = (RadioButton) layoutCasoCasaCrianca.findViewById(R.id.radio1);
			radioButtonCasoSim.setText(getString(R.string.formulario3_opcao_sim));
			final RadioButton radioButtonCasoNao = (RadioButton) layoutCasoCasaCrianca.findViewById(R.id.radio2);
			radioButtonCasoNao.setText(getString(R.string.formulario3_opcao_nao));
			
			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getCasoCasaCrianca())){
				if(Data.listaFormularioTres.get(paginaFragment)
						.getCasoCasaCrianca().equalsIgnoreCase(getString(R.string.formulario3_opcao_sim))){
					radioButtonCasoSim.setChecked(true);
				}else if(Data.listaFormularioTres.get(paginaFragment)
						.getCasoCasaCrianca().equalsIgnoreCase(getString(R.string.formulario3_opcao_nao))){
					radioButtonCasoNao.setChecked(true);
				}
			}
			
			alert.setTitle(getString(R.string.formulario3_caso_casa_crianca));
			alert.setView(layoutCasoCasaCrianca);
			alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if(radioButtonCasoSim.isChecked()){
						setPreferences(arg2, getString(R.string.formulario3_opcao_sim), false,  true);
						((ImageView)view.findViewById(R.id.nome_img_check)).setImageResource(R.drawable.ic_certo);
						
					}else if(radioButtonCasoNao.isChecked()){
						setPreferences(arg2, getString(R.string.formulario3_opcao_nao), false, true);
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
			
		case VariloidForm3.OBS_CASO:
			final EditText obsCasp = new EditText(getSherlockActivity());
			int maxLength27 = 250;
			obsCasp.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					maxLength27) });

			if (!TextUtils.isEmpty(Data.listaFormularioTres.get(paginaFragment)
					.getObservacoesRelacionadasCaso()))
				obsCasp.setText(Data.listaFormularioTres.get(paginaFragment)
						.getObservacoesRelacionadasCaso());

			alert.setTitle(getString(R.string.formulario3_obs_caso));
			alert.setView(obsCasp);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							if (!TextUtils
									.isEmpty(obsCasp.getText().toString())) {
								setPreferences(arg2, obsCasp.getText()
										.toString(), false, true);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageResource(R.drawable.ic_certo);
							} else {
								setPreferences(arg2, "", false, false);
								((ImageView) view
										.findViewById(R.id.nome_img_check))
										.setImageDrawable(null);
							}
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();

			break;
		default:
			break;
		}
	}

	private void setPreferences(int posicao, String imput, boolean inativar,
			boolean sucesso) {
		Data.listaFormularioTres.get(paginaFragment).setInativar(posicao, inativar);
		Data.listaFormularioTres.get(paginaFragment).setSucesso(posicao, sucesso);
		setAtributos(posicao, imput);
		myAdapter.notifyDataSetInvalidated();
	}

	public void setAtributos(int posicao, String string) {

		switch (posicao) {
		case VariloidForm3.CRIANCA_JA_TEVE_CATAPORA:
			Data.listaFormularioTres.get(paginaFragment).setCriancaCataporaAnt(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.CRIANCA_MORA_NA_VIZINHANCA:
			Data.listaFormularioTres.get(paginaFragment).setCriancaMoraVizinhanca(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.MUNICIPIO:
			Data.listaFormularioTres.get(paginaFragment).setMunicipio(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.VACVARICELA:
			Data.listaFormularioTres.get(paginaFragment).setCriancaRecebeuVacinaCatapora(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.MOTIVO_NAO_VACINACAO:
			Data.listaFormularioTres.get(paginaFragment).setRazoesNaoVacinacao(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.CASO_CASA_CRIANCA:
			Data.listaFormularioTres.get(paginaFragment).setNomeCrianca(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.NOME:
			Data.listaFormularioTres.get(paginaFragment).setNomeCrianca(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.SEXO:
			Data.listaFormularioTres.get(paginaFragment).setSexo(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.DATA_NASCIMENTO:
			Data.listaFormularioTres.get(paginaFragment).setDataNascimento(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
//		case VariloidForm3.JA_TEVE_CATAPORA:
//			formularioTres.setCriancaCataporaAnt(string);
//			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
//			break;
		case VariloidForm3.CONTATO_COM_QUEM_JA_TEVE:
			Data.listaFormularioTres.get(paginaFragment).setContatoPessoaCatapora(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.LOCAL_CONTATO:
			Data.listaFormularioTres.get(paginaFragment).setLocalContato(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.RESIDENCIA:
			Data.listaFormularioTres.get(paginaFragment).setResidencia(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.TIPO_LOGRADOURO:
			Data.listaFormularioTres.get(paginaFragment).setTipoLogradouro(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.NOME_LOGRADOURO:
			Data.listaFormularioTres.get(paginaFragment).setNomeLogradouro(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.QUADRA:
			Data.listaFormularioTres.get(paginaFragment).setQuadra(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.LOTE:
			Data.listaFormularioTres.get(paginaFragment).setLote(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.NUMERO:
			Data.listaFormularioTres.get(paginaFragment).setNumero(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.COMPLEMENTO:
			Data.listaFormularioTres.get(paginaFragment).setComplemento(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.BAIRRO:
			Data.listaFormularioTres.get(paginaFragment).setBairro(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
//		case VariloidForm3.TITULO_CRITERIO_INCLUSAO:
//			//
//			break;
		case VariloidForm3.TCLE:
			Data.listaFormularioTres.get(paginaFragment).setTcleAssinado(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.NOME_MAE:
			Data.listaFormularioTres.get(paginaFragment).setNomeMae(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.IDADE_MAE:
			Data.listaFormularioTres.get(paginaFragment).setIdadeMae(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.NOME_PAI:
			Data.listaFormularioTres.get(paginaFragment).setNomePai(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.TELEFONE:
			Data.listaFormularioTres.get(paginaFragment).setTelefoneFixo(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.CELULAR1:
			Data.listaFormularioTres.get(paginaFragment).setCelular1(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.OPER_CELULAR1:
			Data.listaFormularioTres.get(paginaFragment).setOperadoraCelular1(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.CELULAR2:
			Data.listaFormularioTres.get(paginaFragment).setCelular2(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.OPER_CELULAR2:
			Data.listaFormularioTres.get(paginaFragment).setOperadoraCelular2(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.COBERTURA_PSF:
			Data.listaFormularioTres.get(paginaFragment).setCriancaCadastradaPSF(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.PESONASCER:
			Data.listaFormularioTres.get(paginaFragment).setPesoNascimentoGramas(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.IDADEGESTACIONAL:
			Data.listaFormularioTres.get(paginaFragment).setIdadeGestacional(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.RACA:
			Data.listaFormularioTres.get(paginaFragment).setRaca(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.TITULO_VACINA_CONTRA_VARICELA:
			//
			break;
		case VariloidForm3.N_DOSES_VARIC:
			Data.listaFormularioTres.get(paginaFragment).setNumDosesRecebidasContraVaricela(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.VAC_VARIC_DT_DOSE1:
			Data.listaFormularioTres.get(paginaFragment).setDataPrimeiraDose(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.VAC_VARIC_QUAL_DOSE1:
			Data.listaFormularioTres.get(paginaFragment).setQualVacinaUsadaPrimeiraDose(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.VAC_VARIC_DT_DOSE2:
			Data.listaFormularioTres.get(paginaFragment).setDataSegundaDose(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.VAC_VARIC_QUAL_DOSE2:
			Data.listaFormularioTres.get(paginaFragment).setQualVacinaUsadaSegundaDose(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
//		case VariloidForm3.TITULO_VACINA_TRIPLICE:
//			//
//			break;
		case VariloidForm3.VACTRIPLICE:
			Data.listaFormularioTres.get(paginaFragment).setCriancaRecebeuVacinaTripliceViral(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.N_DOSES_TRIPLICE:
			Data.listaFormularioTres.get(paginaFragment).setNumDosesRecebidasContraTripliceViral(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.MMR1:
			Data.listaFormularioTres.get(paginaFragment).setDataPrimeiraDoseMMR(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.MMR2:
			Data.listaFormularioTres.get(paginaFragment).setDataSegundaDoseMMR(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.FREQCRECHE1:
			Data.listaFormularioTres.get(paginaFragment).setAlguemFrequentouCreche(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.FREQCRECHE2:
			Data.listaFormularioTres.get(paginaFragment).setQuemFamiliaFrequentouCreche(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.FREQSERVSAUDE1:
			Data.listaFormularioTres.get(paginaFragment).setAlguemFrequentouServicoSaude(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.FREQSERVSAUDE2:
			Data.listaFormularioTres.get(paginaFragment).setQuemFamiliaFrequentouServicoSaude(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.HOSPITALIZACAO:
			Data.listaFormularioTres.get(paginaFragment).setCriancaInternou(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
//		case VariloidForm3.MAE_TRABALHA:
//			Data.listaFormularioTres.get(paginaFragment).setMaeTrabalhaFora(string);
//			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
//			break;
		case VariloidForm3.ES_CMAE:
			Data.listaFormularioTres.get(paginaFragment).setEscolaridadeMae(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.NCCAS_CASA5:
			Data.listaFormularioTres.get(paginaFragment).setNumCriancasMenos5anosDomicilio(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.NCCAS_CASA10:
			Data.listaFormularioTres.get(paginaFragment).setNumCriancasMenos10anosDomicilio(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.QT_DE_PESSOAS_CASA:
			Data.listaFormularioTres.get(paginaFragment).setQuantasPessoasCasa(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.QTD_EPESSOAS_QTO:
			Data.listaFormularioTres.get(paginaFragment).setQuantasPessoasDormemMesmoComodoCrianca(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
//		case VariloidForm3.TITULO_DOENCAFALCIFORME:
//			//
//			break;
		case VariloidForm3.DOENCAFALCIFORME:
			Data.listaFormularioTres.get(paginaFragment).setDoencaFalciforme(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.PREMATURIDADE:
			Data.listaFormularioTres.get(paginaFragment).setPrematuridade(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.INFECHIV:
			Data.listaFormularioTres.get(paginaFragment).setInfeccaoHIVeAIDS(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.IMUNODEFICIENCIA:
			Data.listaFormularioTres.get(paginaFragment).setImunodeficiencia(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.ASMA:
			Data.listaFormularioTres.get(paginaFragment).setAsma(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.CORTICOIDE:
			Data.listaFormularioTres.get(paginaFragment).setUsoCorticoideSistemico(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.CANCER:
			Data.listaFormularioTres.get(paginaFragment).setCancer(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.DOENCAPULMONARCRONICA:
			Data.listaFormularioTres.get(paginaFragment).setDoencaPulmonarCronica(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.DOENCARENALCRONICA:
			Data.listaFormularioTres.get(paginaFragment).setDoencaRenalCronica(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.DOENCACARDIACA:
			Data.listaFormularioTres.get(paginaFragment).setDoencaCardiaca(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.DOENCAHEMATOLOGICA:
			Data.listaFormularioTres.get(paginaFragment).setDoencaHematologicaCronica(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.DOENCAHEPATICA:
			Data.listaFormularioTres.get(paginaFragment).setDoencaHepaticaCronica(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.ASPLENIA:
			Data.listaFormularioTres.get(paginaFragment).setAspleniaCongenitaOuFuncional(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.DIABETES:
			Data.listaFormularioTres.get(paginaFragment).setDiabetes(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		case VariloidForm3.OBS_CASO:
			Data.listaFormularioTres.get(paginaFragment).setObservacoesRelacionadasCaso(string);
			Data.listaMapFormularioTres.get(paginaFragment).set(Data.FORM3_KEY.concat("[").concat(String.valueOf(paginaFragment)).concat("].").concat(VariloidForm3.idCampos[posicao]), string);
			break;
		default:
			break;
		}
	}

}
