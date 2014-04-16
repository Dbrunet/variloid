package br.ufg.iptsp.app.variloid.servico;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import br.ufg.iptsp.app.variloid.R;
import br.ufg.iptsp.app.variloid.Variloid;
import br.ufg.iptsp.app.variloid.VariloidForm2;
import br.ufg.iptsp.app.variloid.VariloidForm3;
import br.ufg.iptsp.app.variloid.VariloidForm4;
import br.ufg.iptsp.app.variloid.negocio.Entrevista;
import br.ufg.iptsp.app.variloid.negocio.FormularioDois;
import br.ufg.iptsp.app.variloid.negocio.FormularioTres;
import br.ufg.iptsp.app.variloid.negocio.Usuario;
import br.ufg.iptsp.app.variloid.provider.Data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Servico {

	private static final String FORM4_KEY = "formularioQuatro.";

	private static final String IP = "galfano.com.br";
//	 private static final String IP = "192.168.1.5";

	private String urlEnviarEntrevista = "http://" + IP
			+ "/variloid/rest/entrevistas/enviar";
	private String urlListaUsuarios = "http://" + IP
			+ "/variloid/rest/usuarios/listar";
	private String urlListaPendentes = "http://" + IP
			+ "/variloid/rest/entrevistas/pendentes/";
	
	private RestTemplate restTemplate;
	private MultiValueMap<String, Object> map;
	private Context context;
	private Gson gson;

	public Servico(Context context) {
		this.context = context;
		restTemplate = new RestTemplate();
		map = new LinkedMultiValueMap<String, Object>();
		gson = new GsonBuilder().create();
	}

	public void tamanhoArray() {
		Log.v(Variloid.tag, "Form2 idCampos: " + VariloidForm2.idCampos.length);
		Log.v(Variloid.tag,
				"Form2 Campos (6 itens a mais por causa dos titulos): "
						+ VariloidForm2.campos.length);

		Log.v(Variloid.tag, "Form3 idCampos: " + VariloidForm3.idCampos.length);
		Log.v(Variloid.tag,
				"Form3 Campos (4 itens a mais por causa dos titulos): "
						+ VariloidForm3.campos.length);

		Log.v(Variloid.tag, "Form4 idCampos: " + VariloidForm4.idCampos.length);
		Log.v(Variloid.tag,
				"Form4 Campos(3 itens a mais por causa dos titulos): "
						+ VariloidForm4.campos.length);
	}

	public List<Usuario> getUsuarios() {

		Type listType = new TypeToken<LinkedList<Usuario>>() {
		}.getType();
		List<Usuario> listaDeUsuarios = null;

		try {
			listaDeUsuarios = gson.fromJson(
					restTemplate.getForObject(urlListaUsuarios, String.class),
					listType);

		} catch (Exception e) {
			Log.w("e.printStackTrace()", e.toString());
			listaDeUsuarios = null;
		}

		return listaDeUsuarios;
	}

	public List<Entrevista> getEntrevistaPendente(String idUsuario) {
		Type listType = new TypeToken<LinkedList<Entrevista>>() {
		}.getType();
		List<Entrevista> entrevistasPendentes = null;

		try {
			entrevistasPendentes = gson.fromJson(restTemplate.getForObject(
					urlListaPendentes + idUsuario, String.class), listType);

		} catch (Exception e) {
			Log.w("e.printStackTrace()", e.toString());
			entrevistasPendentes = null;
		}

		return entrevistasPendentes;
	}

	public String enviarForm2e3() {
		String nid = null;

		// formulario 2
		// remove os campos titulos "????"

		for (int i = 0; i < VariloidForm2.idCampos.length; i++) {
			switch (i) {
			case 14:
				Data.mapFormularioDois.remove(VariloidForm2.idCampos[i]);
				break;
			case 27:
				Data.mapFormularioDois.remove(VariloidForm2.idCampos[i]);
				break;
			case 32:
				Data.mapFormularioDois.remove(VariloidForm2.idCampos[i]);
				break;
			case 47:
				Data.mapFormularioDois.remove(VariloidForm2.idCampos[i]);
				break;
			case 53:
				Data.mapFormularioDois.remove(VariloidForm2.idCampos[i]);
				break;
			case 69:
				Data.mapFormularioDois.remove(VariloidForm2.idCampos[i]);
				break;
			default:
				if (!TextUtils.isEmpty(Data.mapFormularioDois.get(
						Data.FORM2_KEY.concat(VariloidForm2.idCampos[i])).get(0).toString())) {
					
					Data.mapService.add((Data.FORM2_KEY.concat(VariloidForm2.idCampos[i])),
							Data.mapFormularioDois.get(Data.FORM2_KEY
									.concat(VariloidForm2.idCampos[i])).get(0));
				}
				break;
				
			}
		}

		// formulario 3
		for (int i = 0; i < Data.listaMapFormularioTres.size(); i++) {

			for (int j = 0; j < VariloidForm3.idCampos.length; j++) {
				switch (j) {
				case 14:
					Data.listaMapFormularioTres.get(i).remove(
							VariloidForm3.idCampos[j]);
					break;
				case 28:
					Data.listaMapFormularioTres.get(i).remove(
							VariloidForm3.idCampos[j]);
					break;
				case 34:
					Data.listaMapFormularioTres.get(i).remove(
							VariloidForm3.idCampos[j]);
					break;
				case 50:
					Data.listaMapFormularioTres.get(i).remove(
							VariloidForm3.idCampos[j]);
					break;
				default:
					if (!TextUtils.isEmpty(Data.listaMapFormularioTres.get(i)
							.get(Data.FORM3_KEY.concat("[")
									.concat(String.valueOf(i)).concat("].")
									.concat(VariloidForm3.idCampos[j])).get(0)
							.toString())) {
						
						Data.mapService.add(Data.FORM3_KEY.concat("[")
								.concat(String.valueOf(i)).concat("].")
								.concat(VariloidForm3.idCampos[j]),
								Data.listaMapFormularioTres
										.get(i)
										.get(Data.FORM3_KEY
												.concat("[")
												.concat(String.valueOf(i))
												.concat("].")
												.concat(VariloidForm3.idCampos[j])).get(0)
										.toString());
					}
					break;
				}
			}
			
		}
		
		nid = "ok";

		try {
			nid = restTemplate.postForObject(urlEnviarEntrevista, Data.mapService,
					String.class);

			Data.formularioDois = new FormularioDois();
			Data.mapFormularioDois = new LinkedMultiValueMap<String, Object>();
			Data.mapService = new LinkedMultiValueMap<String, Object>();
			Data.listaFormularioTres.clear();
			Data.listaMapFormularioTres.clear();
			

		} catch (Exception e) {
			Log.w("e.printStackTrace()", e.toString());
			nid = null;
		}

		return nid;
	}

	public String enviarForm4() {
		String nid = null;

		SharedPreferences pref = context.getSharedPreferences(
				Variloid.PREFERENCIAS, 0);

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
				if (!TextUtils.isEmpty(getCamposForm4(pref, i))) {
					map.add(FORM4_KEY + VariloidForm4.idCampos[i],
							getCamposForm4(pref, i));
				}
				break;
			}
		}

		map.add("faixaEtaria", context.getString(R.string.faixa_etaria2));
		map.add(FORM4_KEY + "latitude",
				pref.getString(VariloidForm4.FORM4_CAMPO + "latitude", ""));
		map.add(FORM4_KEY + "longitude",
				pref.getString(VariloidForm4.FORM4_CAMPO + "longitude", ""));

		if (!TextUtils.isEmpty(pref.getString(
				Variloid.FORM_FOTO_CARTAO_VACINA4, ""))) {
			map.add(FORM4_KEY + "cartaoVacina.commonsMultipartFile",
					new FileSystemResource(pref.getString(
							Variloid.FORM_FOTO_CARTAO_VACINA4, "")));
		}

		if (!TextUtils.isEmpty(pref.getString(Variloid.ID_ENTREVISTADOR, ""))) {
			map.add(Variloid.ID_ENTREVISTADOR,
					pref.getString(Variloid.ID_ENTREVISTADOR, ""));
		}

		try {
			nid = restTemplate.postForObject(urlEnviarEntrevista, map,
					String.class);
			limpandoFormularios();
		} catch (Exception e) {
			Log.w("e.printStackTrace()", e.toString());
			nid = null;
		}

		return nid;

	}

	private String getCamposForm4(SharedPreferences pref, int posicao) {
		return pref.getString(VariloidForm4.FORM4_CAMPO + posicao, "");
	}

	private void limpandoFormularios() {
		SharedPreferences pref = context.getSharedPreferences(
				Variloid.PREFERENCIAS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		for (int i = 0; i < VariloidForm2.idCampos.length; i++) {
			switch (i) {
			case 14:
				break;
			case 27:
				break;
			case 32:
				break;
			case 47:
				break;
			case 53:
				break;
			case 69:
				break;
			default:
				editor.putString(VariloidForm2.FORM2_CAMPO + i, "");
				editor.putBoolean(VariloidForm2.FORM2_IMAGEM + i, false);
				editor.putBoolean(VariloidForm2.FORM2_INATIVAR + i, false);
				break;
			}
		}

		for (int i = 0; i < VariloidForm3.idCampos.length; i++) {
			switch (i) {
			case 14:
				break;
			case 28:
				break;
			case 34:
				break;
			case 50:
				break;
			default:
				editor.putString(VariloidForm3.FORM3_CAMPO + i, "");
				editor.putBoolean(VariloidForm3.FORM3_IMAGEM + i, false);
				editor.putBoolean(VariloidForm3.FORM3_INATIVAR + i, false);
				break;
			}

		}

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
				editor.putString(VariloidForm4.FORM4_CAMPO + i, "");
				editor.putBoolean(VariloidForm4.FORM4_IMAGEM + i, false);
				editor.putBoolean(VariloidForm4.FORM4_INATIVAR + i, false);
				break;
			}
		}

		editor.putString(VariloidForm2.FORM2_CAMPO + "latitude", "");
		editor.putString(VariloidForm2.FORM2_CAMPO + "longitude", "");
		editor.putString(VariloidForm3.FORM3_CAMPO + "latitude", "");
		editor.putString(VariloidForm3.FORM3_CAMPO + "longitude", "");
		editor.putString(VariloidForm4.FORM4_CAMPO + "latitude", "");
		editor.putString(VariloidForm4.FORM4_CAMPO + "longitude", "");
		editor.putString(Variloid.ID_ENTREVISTA_PENDENTE, "");
		editor.putString(Variloid.NOME_ENTREVISTADOR, "");
		editor.putString(Variloid.ID_ENTREVISTADOR_PENDENTE, "");
		editor.putString(Variloid.ID_FOTO_LESAO, "");
		editor.putString(Variloid.ID_FOTO_CARTAO, "");
		editor.putString(Variloid.ID_FORM3, "");
		editor.putString(Variloid.ID_FORM2, "");
		editor.putString(Variloid.FORM_FOTO_CARTAO_VACINA, "");
		editor.putString(Variloid.FORM_FOTO_CARTAO_VACINA4, "");
		editor.putString(Variloid.FORM_FOTO_LESAO_COLETADA, "");

		editor.commit();
	}
}
