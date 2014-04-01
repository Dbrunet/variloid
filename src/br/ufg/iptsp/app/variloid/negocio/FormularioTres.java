package br.ufg.iptsp.app.variloid.negocio;

import java.util.ArrayList;
import java.util.List;

public class FormularioTres {

	private long id;
	private String nomeCrianca;
	private String sexo; // feminino ou masculino
	private String dataNascimento; // formato dd/mm/aaaa
	private String criancaCataporaAnt;
	private String contatoPessoaCatapora;
	private String localContato;
	private String residencia;
	private String tipoLogradouro; // avenida, rua, alameda, rodovia, travessa,
									// outro
	private String nomeLogradouro;
	private String quadra;
	private String lote;
	private String numero;
	private String complemento;
	private String bairro;
	private String latitude;
	private String longitude;
	private String tcleAssinado;
	private String nomeMae;
	private String idadeMae;
	private String nomePai;
	private String telefoneFixo;
	private String celular1;
	private String operadoraCelular1;
	private String celular2;
	private String operadoraCelular2;
	private String criancaCadastradaPSF;
	private String pesoNascimentoGramas;
	private String idadeGestacional;
	private String raca;

	// Parte 3
	private String numDosesRecebidasContraVaricela;
	private String dataPrimeiraDose;
	private String qualVacinaUsadaPrimeiraDose;
	private String dataSegundaDose;
	private String qualVacinaUsadaSegundaDose;
	private String criancaRecebeuVacinaTripliceViral;
	private String numDosesRecebidasContraTripliceViral;
	private String dataPrimeiraDoseMMR;
	private String dataSegundaDoseMMR;
	private String alguemFrequentouCreche;
	private String quemFamiliaFrequentouCreche;
	private String alguemFrequentouServicoSaude;
	private String quemFamiliaFrequentouServicoSaude;
	private String criancaInternou;
	private String maeTrabalhaFora;
	private String escolaridadeMae;
	private String numCriancasMenos5anosDomicilio;
	private String numCriancasMenos10anosDomicilio;
	private String quantasPessoasCasa;
	private String quantasPessoasDormemMesmoComodoCrianca;

	// Parte 4
	private String doencaFalciforme;
	private String prematuridade;
	private String infeccaoHIVeAIDS;
	private String imunodeficiencia;
	private String asma;
	private String usoCorticoideSistemico;
	private String cancer;
	private String doencaPulmonarCronica;
	private String doencaRenalCronica;
	private String doencaCardiaca;
	private String doencaHematologicaCronica;
	private String doencaHepaticaCronica;
	private String aspleniaCongenitaOuFuncional;
	private String diabetes;

	// Parte 5
	private String observacoesRelacionadasCaso;
	private Arquivo cartaoVacina;
	private List<Boolean> listInativar = new ArrayList<Boolean>();

	public List<Boolean> getListInativar() {
		return listInativar;
	}

	public void setListInativar(List<Boolean> listInativar) {
		this.listInativar = listInativar;
	}

	public List<Boolean> getListSucesso() {
		return listSucesso;
	}

	public void setListSucesso(List<Boolean> listSucesso) {
		this.listSucesso = listSucesso;
	}

	private List<Boolean> listSucesso = new ArrayList<Boolean>();

	public void setInativar(int posicao, boolean inativar) {
		listInativar.set(posicao, inativar);
	}

	public void setSucesso(int posicao, boolean sucesso) {
		listSucesso.set(posicao, sucesso);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Arquivo getCartaoVacina() {
		if (cartaoVacina == null) {
			cartaoVacina = new Arquivo();
		}
		return cartaoVacina;
	}

	public void setCartaoVacina(Arquivo cartaoVacina) {
		this.cartaoVacina = cartaoVacina;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getCriancaCataporaAnt() {
		return criancaCataporaAnt;
	}

	public void setCriancaCataporaAnt(String criancaCataporaAnt) {
		this.criancaCataporaAnt = criancaCataporaAnt;
	}

	public String getContatoPessoaCatapora() {
		return contatoPessoaCatapora;
	}

	public void setContatoPessoaCatapora(String contatoPessoaCatapora) {
		this.contatoPessoaCatapora = contatoPessoaCatapora;
	}

	public String getResidencia() {
		return residencia;
	}

	public void setResidencia(String residencia) {
		this.residencia = residencia;
	}

	public String getTipoLogradouro() {
		return tipoLogradouro;
	}

	public void setTipoLogradouro(String tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}

	public String getNomeLogradouro() {
		return nomeLogradouro;
	}

	public void setNomeLogradouro(String nomeLogradouro) {
		this.nomeLogradouro = nomeLogradouro;
	}

	public String getQuadra() {
		return quadra;
	}

	public void setQuadra(String quadra) {
		this.quadra = quadra;
	}

	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getTcleAssinado() {
		return tcleAssinado;
	}

	public void setTcleAssinado(String tcleAssinado) {
		this.tcleAssinado = tcleAssinado;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getIdadeMae() {
		return idadeMae;
	}

	public void setIdadeMae(String idadeMae) {
		this.idadeMae = idadeMae;
	}

	public String getNomePai() {
		return nomePai;
	}

	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	public String getTelefoneFixo() {
		return telefoneFixo;
	}

	public void setTelefoneFixo(String telefoneFixo) {
		this.telefoneFixo = telefoneFixo;
	}

	public String getCelular1() {
		return celular1;
	}

	public void setCelular1(String celular1) {
		this.celular1 = celular1;
	}

	public String getOperadoraCelular1() {
		return operadoraCelular1;
	}

	public void setOperadoraCelular1(String operadoraCelular1) {
		this.operadoraCelular1 = operadoraCelular1;
	}

	public String getCelular2() {
		return celular2;
	}

	public void setCelular2(String celular2) {
		this.celular2 = celular2;
	}

	public String getOperadoraCelular2() {
		return operadoraCelular2;
	}

	public void setOperadoraCelular2(String operadoraCelular2) {
		this.operadoraCelular2 = operadoraCelular2;
	}

	public String getCriancaCadastradaPSF() {
		return criancaCadastradaPSF;
	}

	public void setCriancaCadastradaPSF(String criancaCadastradaPSF) {
		this.criancaCadastradaPSF = criancaCadastradaPSF;
	}

	public String getPesoNascimentoGramas() {
		return pesoNascimentoGramas;
	}

	public void setPesoNascimentoGramas(String pesoNascimentoGramas) {
		this.pesoNascimentoGramas = pesoNascimentoGramas;
	}

	public String getIdadeGestacional() {
		return idadeGestacional;
	}

	public void setIdadeGestacional(String idadeGestacional) {
		this.idadeGestacional = idadeGestacional;
	}

	public String getRaca() {
		return raca;
	}

	public void setRaca(String raca) {
		this.raca = raca;
	}

	public String getNumDosesRecebidasContraVaricela() {
		return numDosesRecebidasContraVaricela;
	}

	public void setNumDosesRecebidasContraVaricela(
			String numDosesRecebidasContraVaricela) {
		this.numDosesRecebidasContraVaricela = numDosesRecebidasContraVaricela;
	}

	public String getDataPrimeiraDose() {
		return dataPrimeiraDose;
	}

	public void setDataPrimeiraDose(String dataPrimeiraDose) {
		this.dataPrimeiraDose = dataPrimeiraDose;
	}

	public String getQualVacinaUsadaPrimeiraDose() {
		return qualVacinaUsadaPrimeiraDose;
	}

	public void setQualVacinaUsadaPrimeiraDose(
			String qualVacinaUsadaPrimeiraDose) {
		this.qualVacinaUsadaPrimeiraDose = qualVacinaUsadaPrimeiraDose;
	}

	public String getDataSegundaDose() {
		return dataSegundaDose;
	}

	public void setDataSegundaDose(String dataSegundaDose) {
		this.dataSegundaDose = dataSegundaDose;
	}

	public String getQualVacinaUsadaSegundaDose() {
		return qualVacinaUsadaSegundaDose;
	}

	public void setQualVacinaUsadaSegundaDose(String qualVacinaUsadaSegundaDose) {
		this.qualVacinaUsadaSegundaDose = qualVacinaUsadaSegundaDose;
	}

	public String getDataPrimeiraDoseMMR() {
		return dataPrimeiraDoseMMR;
	}

	public void setDataPrimeiraDoseMMR(String dataPrimeiraDoseMMR) {
		this.dataPrimeiraDoseMMR = dataPrimeiraDoseMMR;
	}

	public String getDataSegundaDoseMMR() {
		return dataSegundaDoseMMR;
	}

	public void setDataSegundaDoseMMR(String dataSegundaDoseMMR) {
		this.dataSegundaDoseMMR = dataSegundaDoseMMR;
	}

	public String getAlguemFrequentouCreche() {
		return alguemFrequentouCreche;
	}

	public void setAlguemFrequentouCreche(String alguemFrequentouCreche) {
		this.alguemFrequentouCreche = alguemFrequentouCreche;
	}

	public String getQuemFamiliaFrequentouCreche() {
		return quemFamiliaFrequentouCreche;
	}

	public void setQuemFamiliaFrequentouCreche(
			String quemFamiliaFrequentouCreche) {
		this.quemFamiliaFrequentouCreche = quemFamiliaFrequentouCreche;
	}

	public String getAlguemFrequentouServicoSaude() {
		return alguemFrequentouServicoSaude;
	}

	public void setAlguemFrequentouServicoSaude(
			String alguemFrequentouServicoSaude) {
		this.alguemFrequentouServicoSaude = alguemFrequentouServicoSaude;
	}

	public String getQuemFamiliaFrequentouServicoSaude() {
		return quemFamiliaFrequentouServicoSaude;
	}

	public void setQuemFamiliaFrequentouServicoSaude(
			String quemFamiliaFrequentouServicoSaude) {
		this.quemFamiliaFrequentouServicoSaude = quemFamiliaFrequentouServicoSaude;
	}

	public String getCriancaInternou() {
		return criancaInternou;
	}

	public void setCriancaInternou(String criancaInternou) {
		this.criancaInternou = criancaInternou;
	}

	public String getMaeTrabalhaFora() {
		return maeTrabalhaFora;
	}

	public void setMaeTrabalhaFora(String maeTrabalhaFora) {
		this.maeTrabalhaFora = maeTrabalhaFora;
	}

	public String getEscolaridadeMae() {
		return escolaridadeMae;
	}

	public void setEscolaridadeMae(String escolaridadeMae) {
		this.escolaridadeMae = escolaridadeMae;
	}

	public String getNumCriancasMenos5anosDomicilio() {
		return numCriancasMenos5anosDomicilio;
	}

	public void setNumCriancasMenos5anosDomicilio(
			String numCriancasMenos5anosDomicilio) {
		this.numCriancasMenos5anosDomicilio = numCriancasMenos5anosDomicilio;
	}

	public String getNumCriancasMenos10anosDomicilio() {
		return numCriancasMenos10anosDomicilio;
	}

	public void setNumCriancasMenos10anosDomicilio(
			String numCriancasMenos10anosDomicilio) {
		this.numCriancasMenos10anosDomicilio = numCriancasMenos10anosDomicilio;
	}

	public String getQuantasPessoasCasa() {
		return quantasPessoasCasa;
	}

	public void setQuantasPessoasCasa(String quantasPessoasCasa) {
		this.quantasPessoasCasa = quantasPessoasCasa;
	}

	public String getQuantasPessoasDormemMesmoComodoCrianca() {
		return quantasPessoasDormemMesmoComodoCrianca;
	}

	public void setQuantasPessoasDormemMesmoComodoCrianca(
			String quantasPessoasDormemMesmoComodoCrianca) {
		this.quantasPessoasDormemMesmoComodoCrianca = quantasPessoasDormemMesmoComodoCrianca;
	}

	public String getDoencaFalciforme() {
		return doencaFalciforme;
	}

	public void setDoencaFalciforme(String doencaFalciforme) {
		this.doencaFalciforme = doencaFalciforme;
	}

	public String getPrematuridade() {
		return prematuridade;
	}

	public void setPrematuridade(String prematuridade) {
		this.prematuridade = prematuridade;
	}

	public String getInfeccaoHIVeAIDS() {
		return infeccaoHIVeAIDS;
	}

	public void setInfeccaoHIVeAIDS(String infeccaoHIVeAIDS) {
		this.infeccaoHIVeAIDS = infeccaoHIVeAIDS;
	}

	public String getImunodeficiencia() {
		return imunodeficiencia;
	}

	public void setImunodeficiencia(String imunodeficiencia) {
		this.imunodeficiencia = imunodeficiencia;
	}

	public String getAsma() {
		return asma;
	}

	public void setAsma(String asma) {
		this.asma = asma;
	}

	public String getUsoCorticoideSistemico() {
		return usoCorticoideSistemico;
	}

	public void setUsoCorticoideSistemico(String usoCorticoideSistemico) {
		this.usoCorticoideSistemico = usoCorticoideSistemico;
	}

	public String getCancer() {
		return cancer;
	}

	public void setCancer(String cancer) {
		this.cancer = cancer;
	}

	public String getDoencaPulmonarCronica() {
		return doencaPulmonarCronica;
	}

	public void setDoencaPulmonarCronica(String doencaPulmonarCronica) {
		this.doencaPulmonarCronica = doencaPulmonarCronica;
	}

	public String getDoencaRenalCronica() {
		return doencaRenalCronica;
	}

	public void setDoencaRenalCronica(String doencaRenalCronica) {
		this.doencaRenalCronica = doencaRenalCronica;
	}

	public String getDoencaCardiaca() {
		return doencaCardiaca;
	}

	public void setDoencaCardiaca(String doencaCardiaca) {
		this.doencaCardiaca = doencaCardiaca;
	}

	public String getDoencaHematologicaCronica() {
		return doencaHematologicaCronica;
	}

	public void setDoencaHematologicaCronica(String doencaHematologicaCronica) {
		this.doencaHematologicaCronica = doencaHematologicaCronica;
	}

	public String getDoencaHepaticaCronica() {
		return doencaHepaticaCronica;
	}

	public void setDoencaHepaticaCronica(String doencaHepaticaCronica) {
		this.doencaHepaticaCronica = doencaHepaticaCronica;
	}

	public String getAspleniaCongenitaOuFuncional() {
		return aspleniaCongenitaOuFuncional;
	}

	public void setAspleniaCongenitaOuFuncional(
			String aspleniaCongenitaOuFuncional) {
		this.aspleniaCongenitaOuFuncional = aspleniaCongenitaOuFuncional;
	}

	public String getDiabetes() {
		return diabetes;
	}

	public void setDiabetes(String diabetes) {
		this.diabetes = diabetes;
	}

	public String getObservacoesRelacionadasCaso() {
		return observacoesRelacionadasCaso;
	}

	public void setObservacoesRelacionadasCaso(
			String observacoesRelacionadasCaso) {
		this.observacoesRelacionadasCaso = observacoesRelacionadasCaso;
	}

	public String getNomeCrianca() {
		return nomeCrianca;
	}

	public void setNomeCrianca(String nomeCrianca) {
		this.nomeCrianca = nomeCrianca;
	}

	public String getLocalContato() {
		return localContato;
	}

	public void setLocalContato(String localContato) {
		this.localContato = localContato;
	}

	public String getCriancaRecebeuVacinaTripliceViral() {
		return criancaRecebeuVacinaTripliceViral;
	}

	public void setCriancaRecebeuVacinaTripliceViral(
			String criancaRecebeuVacinaTripliceViral) {
		this.criancaRecebeuVacinaTripliceViral = criancaRecebeuVacinaTripliceViral;
	}

	public String getNumDosesRecebidasContraTripliceViral() {
		return numDosesRecebidasContraTripliceViral;
	}

	public void setNumDosesRecebidasContraTripliceViral(
			String numDosesRecebidasContraTripliceViral) {
		this.numDosesRecebidasContraTripliceViral = numDosesRecebidasContraTripliceViral;
	}

}
