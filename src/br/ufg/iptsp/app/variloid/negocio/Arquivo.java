package br.ufg.iptsp.app.variloid.negocio;

import java.util.Date;

public class Arquivo {

	private long id;
	private String nome;
	private byte[] bytes;
	private Date dataCriacao;
	private Integer tamanho;
	private String caminho;

	
	public Arquivo() {
		this.setDataCriacao(new Date());
	}

	public long getId() {
		return id;
	}

	public Integer getTamanho() {
		return tamanho;
	}

	public void setTamanho(Integer tamanho) {
		this.tamanho = tamanho;
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

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
//	public void setCommonsMultipartFile(CommonsMultipartFile commonsMultipartFile) {
//		if (commonsMultipartFile.getBytes().length == 0) {
//			return;
//		}
//		
//		this.setNome(commonsMultipartFile.getOriginalFilename());
//		this.setBytes(commonsMultipartFile.getBytes());
//		this.setDataCriacao(Calendar.getInstance().getTime());
//		this.setTamanho(Integer.parseInt(String.valueOf(commonsMultipartFile.getSize())));
//	}
	
	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Date getDataCriacao() {
		return this.dataCriacao;
	}

	public String getCaminho() {
		return caminho;
	}

	public void setCaminho(String caminho) {
		this.caminho = caminho;
	}
}
