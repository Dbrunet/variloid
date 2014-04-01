package br.ufg.iptsp.app.variloid.negocio;

import java.util.List;


public class Entrevista {

	private long id;
	
	private Usuario entrevistador;

	private String faixaEtaria;

	private FormularioDois formularioDois;

	private List<FormularioTres> formularioTres;

	private FormularioQuatro formularioQuatro;

	private String dataCriacao;
	
	private Integer status=0; //0=finalizado, 1=pendente

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return this.id;
	}

	public void setDataCriacao(String dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public String getDataCriacao() {
		return dataCriacao;
	}

	public String getFaixaEtaria() {
		return faixaEtaria;
	}

	public void setFaixaEtaria(String faixaEtaria) {
		this.faixaEtaria = faixaEtaria;
	}

	public FormularioDois getFormularioDois() {
		return formularioDois;
	}

	public void setFormularioDois(FormularioDois formularioDois) {
		this.formularioDois = formularioDois;
	}

	public FormularioQuatro getFormularioQuatro() {
		return formularioQuatro;
	}

	public void setFormularioQuatro(FormularioQuatro formularioQuatro) {
		this.formularioQuatro = formularioQuatro;
	}

	public Usuario getEntrevistador() {
		return entrevistador;
	}

	public void setEntrevistador(Usuario entrevistador) {
		this.entrevistador = entrevistador;
	}

	public List<FormularioTres> getFormularioTres() {
		return formularioTres;
	}

	public void setFormularioTres(List<FormularioTres> formularioTres) {
		this.formularioTres = formularioTres;
	}

}
