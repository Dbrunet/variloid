package br.ufg.iptsp.app.variloid.provider;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import br.ufg.iptsp.app.variloid.negocio.FormularioDois;
import br.ufg.iptsp.app.variloid.negocio.FormularioTres;

public class Data {

	//formulario 3
	public static FormularioTres formularioTres;
	public static List<FormularioTres> listaFormularioTres = new ArrayList<FormularioTres>();
	public static List<MultiValueMap<String, Object>> listaMapFormularioTres = new ArrayList<MultiValueMap<String,Object>>();
	public static MultiValueMap<String, Object> mapFormularioTres = new LinkedMultiValueMap<String, Object>();
	
	//formulario 2
	public static FormularioDois formularioDois = new FormularioDois();
	public static MultiValueMap<String, Object> mapFormularioDois = new LinkedMultiValueMap<String, Object>();
	
	//Map service
	public static MultiValueMap<String, Object> mapService = new LinkedMultiValueMap<String, Object>();
	
	public static final String FORM2_KEY = "formularioDois.";
	public static final String FORM3_KEY = "formularioTres.";
	public static final String FORM4_KEY = "formularioQuatro.";
}
