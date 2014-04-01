package br.ufg.iptsp.app.variloid.negocio;

import java.util.Date;

public class Usuario {
	
	private long id;
	
	private String nome;
	
	private Date dataCriacao;
	
	
	public Usuario() {
		this.setDataCriacao(new Date());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Date getDataCriacao() {
		return this.dataCriacao;
	}
}
