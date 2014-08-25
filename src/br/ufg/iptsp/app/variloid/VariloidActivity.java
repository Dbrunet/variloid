package br.ufg.iptsp.app.variloid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.Toast;
import br.ufg.iptsp.app.variloid.provider.Data;
import br.ufg.iptsp.app.variloid.servico.ServicoConexao;
import br.ufg.iptsp.app.variloid.util.HelpUtils;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class VariloidActivity extends BaseActivity {

	private SharedPreferences pref;
	private boolean isPreenchido2e3, isPreenchido4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_variloid);

		ServicoConexao.verificaTipoConexao(this);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			Toast.makeText(this,
					"Inicie outro Caso de Varicela ou Escolares!.",
					Toast.LENGTH_LONG).show();
		}

		pref = getSharedPreferences(Variloid.PREFERENCIAS, 0);

		getSupportActionBar().setTitle(getString(R.string.app_name));
		
		final Button button = (Button) findViewById(R.id.button);

		final RadioButton radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
		radioButton1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					button.setText(getString(R.string.formulario_dois));
				}
			}
		});
		
		final RadioButton radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
		radioButton2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					button.setText(getString(R.string.formulario_dois));
				}
			}
		});
		
		final RadioButton radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
		radioButton3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					button.setText(getString(R.string.formulario_dois));
				}
			}
		});

		final RadioButton radioButton4 = (RadioButton) findViewById(R.id.radioButton4);

		radioButton4.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					button.setText(getString(R.string.formulario_quatro));
				}
			}
		});

		button.setOnClickListener(new OnClickListener() {
			boolean check1, check2, check3, check4;

			@Override
			public void onClick(View arg0) {

				if (radioButton1.isChecked()) {
					check1 = true;
				} else {
					check1 = false;
				}

				if (radioButton2.isChecked()) {
					check2 = true;
				} else {
					check2 = false;
				}

				if (radioButton3.isChecked()) {
					check3 = true;
				} else {
					check3 = false;
				}
				
				if (radioButton4.isChecked()) {
					check4 = true;
				} else {
					check4 = false;
				}
				
				if (!check1 && !check2 && !check3 && !check4) {
					Toast.makeText(VariloidActivity.this,
							getString(R.string.faixa_etaria_btn),
							Toast.LENGTH_LONG).show();
				}

				if (check1) {
					startActivity(new Intent(VariloidActivity.this,
							Formulario2Activity.class));
					Data.mapService.add("faixaEtaria", getString(R.string.faixa_etaria1));
				}

				if (check2) {
					startActivity(new Intent(VariloidActivity.this,
							Formulario2Activity.class));
					Data.mapService.add("faixaEtaria", getString(R.string.faixa_etaria2));
				}
				
				if (check3) {
					startActivity(new Intent(VariloidActivity.this,
							Formulario2Activity.class));
					Data.mapService.add("faixaEtaria", getString(R.string.faixa_etaria3));
				}
				
				if (check4) {
					startActivity(new Intent(VariloidActivity.this,
							Formulario4Activity.class));
					Data.mapService.add("faixaEtaria", getString(R.string.faixa_etaria4));
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, "Lista de Pendentes");
		menu.add(1, 1, 0, "Sobre");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {

		case 0:
			startActivity(new Intent(VariloidActivity.this, ListaPendentes.class));
			return true;
		case 1:
			HelpUtils.showAbout(this);
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
//	private void limpandoFormularios() {
//		SharedPreferences pref = getSharedPreferences(Variloid.PREFERENCIAS, Context.MODE_PRIVATE);
//		SharedPreferences.Editor editor = pref.edit();
//		
//		for (int i = 0; i < VariloidForm2.campos.length; i++) {
//			switch (i) {
//			case 14:
//				break;
//			case 27:
//				break;
//			case 32:
//				break;
//			case 47:
//				break;
//			case 53:
//				break;
//			case 69:
//				break;
//			default:
//				editor.putString(VariloidForm2.FORM2_CAMPO + i, "");
//				editor.putBoolean(VariloidForm2.FORM2_IMAGEM + i, false);
//				editor.putBoolean(VariloidForm2.FORM2_INATIVAR + i, false);
//				break;
//			}
//		}
//		
//		for (int i = 0; i < VariloidForm3.campos.length; i++) {
//			switch (i) {
//			case 14:
//				break;
//			case 30:
//				break;
//			case 36:
//				break;
//			case 52:
//				break;
//			default:
//				editor.putString(VariloidForm3.FORM3_CAMPO + i, "");
//				editor.putBoolean(VariloidForm3.FORM3_IMAGEM + i, false);
//				editor.putBoolean(VariloidForm3.FORM3_INATIVAR + i, false);
//				break;
//			}
//
//		}
//
//		for (int i = 0; i < VariloidForm4.campos.length; i++) {
//			switch (i) {
//			case 29:
//				break;
//			case 37:
//				break;
//			case 58:
//				break;
//			default:
//				editor.putString(VariloidForm4.FORM4_CAMPO + i, "");
//				editor.putBoolean(VariloidForm4.FORM4_IMAGEM + i, false);
//				editor.putBoolean(VariloidForm4.FORM4_INATIVAR + i, false);
//				break;
//			}
//		}
//		
//		editor.putString(VariloidForm2.FORM2_CAMPO + "latitude", "");
//		editor.putString(VariloidForm2.FORM2_CAMPO + "longitude", "");
//		editor.putString(VariloidForm3.FORM3_CAMPO + "latitude", "");
//		editor.putString(VariloidForm3.FORM3_CAMPO + "longitude", "");
//		editor.putString(VariloidForm4.FORM4_CAMPO + "latitude", "");
//		editor.putString(VariloidForm4.FORM4_CAMPO + "longitude", "");
//		editor.putString(Variloid.ID_ENTREVISTA_PENDENTE, "");
//		editor.putString(Variloid.NOME_ENTREVISTADOR, "");
//		editor.putString(Variloid.ID_ENTREVISTADOR_PENDENTE, "");
//		editor.putString(Variloid.ID_FOTO_LESAO, "");
//		editor.putString(Variloid.ID_FOTO_CARTAO, "");
//		editor.putString(Variloid.ID_FORM3, "");
//		editor.putString(Variloid.ID_FORM2, "");
//		editor.putString(Variloid.FORM_FOTO_CARTAO_VACINA, "");
//		editor.putString(Variloid.FORM_FOTO_LESAO_COLETADA, "");
//		
//		editor.commit();
//		
//		Toast.makeText(this,
//				"Campos limpos",
//				Toast.LENGTH_SHORT).show();
//	}

}
