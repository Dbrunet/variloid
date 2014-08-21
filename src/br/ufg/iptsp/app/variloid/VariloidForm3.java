package br.ufg.iptsp.app.variloid;

public class VariloidForm3 {

	/** chave preferencia Formlario 2 */
	public static final String FORM3_CAMPO = "form3_campo_";
	public static final String FORM3_IMAGEM = "form3_imagem_";
	public static final String FORM3_INATIVAR = "form3_inativar_";

	/** campos Formlario 2 */
	public static final int[] campos = new int[] { 
		//Elegibilidade do caso
		R.string.formulario3_titulo_elegibilidade_do_caso,
		R.string.formulario3_ja_teve_catapora, R.string.formulario3_crianca_mora_na_vizinhanca, R.string.formulario3_tcle,
		//Identificação
		R.string.formulario3_titulo_identificacao,
		R.string.formulario3_nome, R.string.formulario3_sexo, R.string.formulario3_data,
		R.string.formulario3_residencia, R.string.formulario3_tipo_logradouro, R.string.formulario3_nome_logradouro, 
		R.string.formulario3_qudra, R.string.formulario3_lote, R.string.formulario3_numero,
		R.string.formulario3_complemento, R.string.formulario3_bairro,  R.string.formulario3_municipio,
		R.string.formulario3_nome_mae, R.string.formulario3_idade_mae, R.string.formulario3_nome_pai,
		R.string.formulario3_peso_nascer, R.string.formulario3_idade_gestacional, R.string.formulario3_raca,
		R.string.formulario3_tel_fixo, R.string.formulario3_tel_celular1, R.string.formulario3_operadora1,
		R.string.formulario3_tel_celular2, R.string.formulario3_operadora2, R.string.formulario3_cobertura_psf,
		//Vacina contra Varicela
		R.string.formulario3_titulo_vacina_contra_varicela,
		R.string.formulario3_crianca_recebeu_vacina_catapora, R.string.formulario3_razoes_nao_vacinacao,
		R.string.formulario3_n_doses_varic, R.string.formulario3_vac_varic_dt_dose1,
		R.string.formulario3_vac_varic_qual_dose1, R.string.formulario3_vac_varic_dt_dose2, R.string.formulario3_vac_varic_qual_dose2,
		//Vacina MMR
		R.string.formulario3_titulo_vacina_mmr,
		R.string.formulario2_vac_triplice_viral, R.string.formulario2_n_doses_triplice_viral,
		R.string.formulario3_mmr1, R.string.formulario3_mmr2,
		//História pregressa de saúde da criança
		R.string.formulario3_titulo_historia_pregressa,
		R.string.formulario3_contato_com_ja_teve_catapora, R.string.formulario3_local_contato,
		R.string.formulario3_doenca_falciforme, R.string.formulario3_prematuridade, R.string.formulario3_infec_hiv,
		R.string.formulario3_imuno_deficiencia, R.string.formulario3_asma, R.string.formulario3_corticoide, R.string.formulario3_cancer,
		R.string.formulario3_doenca_pulmonar_cronica, R.string.formulario3_doenca_renal_cronica, R.string.formulario3_doenca_cardiaca,
		R.string.formulario3_doenca_hematologica, R.string.formulario3_doenca_hepatica, R.string.formulario3_asplenia, R.string.formulario3_diabetes,
		//Antecedentes Familiares
		R.string.formulario3_titulo_antecedentes_familiares,
		R.string.formulario3_freq_creche1, R.string.formulario3_freq_creche2,
		R.string.formulario3_freq_serv_saude1, R.string.formulario3_freq_serv_saude2,
		R.string.formulario3_hospitalizacao, R.string.formulario3_esc_mae, R.string.formulario3_nc_cas_casa5,
		R.string.formulario3_nc_cas_casa10, R.string.formulario3_qtd_pessoas_casa, R.string.formulario3_qtd_pessoas_quarto,
		R.string.formulario3_caso_casa_crianca,	R.string.formulario3_obs_caso };
	
	public static final String[] idCampos = new String[] { 
		//Elegibilidade do caso
		"????1",
		"criancaCataporaAnt", "criancaMoraVizinhanca", "tcleAssinado", 
		//Identificação
		"????2",
		"nomeCrianca", "sexo", "dataNascimento", "residencia", "tipoLogradouro", "nomeLogradouro", "quadra",
		"lote", "numero", "complemento", "bairro", "municipio", "nomeMae", "idadeMae", "nomePai",
		"pesoNascimentoGramas", "idadeGestacional", "raca","telefoneFixo", "celular1",
		"operadoraCelular1", "celular2", "operadoraCelular2", "criancaCadastradaPSF", 
		//Vacina contra Varicela
		"????3",
		"criancaRecebeuVacinaTripliceViral", "razoesNaoVacinacao", "numDosesRecebidasContraVaricela", "dataPrimeiraDose",
		"qualVacinaUsadaPrimeiraDose", "dataSegundaDose", "qualVacinaUsadaSegundaDose",
		//Vacina MMR
		"????4",
		"criancaRecebeuVacinaTripliceViral", "numDosesRecebidasContraTripliceViral", "dataPrimeiraDoseMMR","dataSegundaDoseMMR",
		//História pregressa de saúde da criança
		"????5",
		"contatoPessoaCatapora", "localContato", "doencaFalciforme", "prematuridade", "infeccaoHIVeAIDS", "imunodeficiencia", "asma",
		"usoCorticoideSistemico", "cancer", "doencaPulmonarCronica", "doencaRenalCronica", "doencaCardiaca",
		"doencaHematologicaCronica", "doencaHepaticaCronica", "aspleniaCongenitaOuFuncional", "diabetes",
		//Antecedentes Familiares
		"????6",
		 "alguemFrequentouCreche", "quemFamiliaFrequentouCreche", "alguemFrequentouServicoSaude", "quemFamiliaFrequentouServicoSaude", 
		 "criancaInternou", "escolaridadeMae", "numCriancasMenos5anosDomicilio", "numCriancasMenos10anosDomicilio", "quantasPessoasCasa",
		"quantasPessoasDormemMesmoComodoCrianca", "casoCasaCrianca","observacoesRelacionadasCaso" };
	

	/** keys Formlario 3 */
	public static final int TITULO_ELEGIBILIDADE_DO_CASO = 0;
	public static final int CRIANCA_JA_TEVE_CATAPORA = 1;
	public static final int CRIANCA_MORA_NA_VIZINHANCA = 2;
	public static final int TCLE = 3;
	public static final int TITULO_IDENTIFICACAO = 4;
	public static final int NOME = 5;
	public static final int SEXO = 6;
	public static final int DATA_NASCIMENTO = 7;
	public static final int RESIDENCIA = 8;
	public static final int TIPO_LOGRADOURO = 9;
	public static final int NOME_LOGRADOURO = 10;
	public static final int QUADRA = 11;
	public static final int LOTE = 12;
	public static final int NUMERO = 13;
	public static final int COMPLEMENTO = 14;
	public static final int BAIRRO = 15;
	public static final int MUNICIPIO = 16;
	public static final int NOME_MAE = 17;
	public static final int IDADE_MAE = 18;
	public static final int NOME_PAI = 19;
	public static final int PESONASCER = 20;
	public static final int IDADEGESTACIONAL = 21;
	public static final int RACA = 22;
	public static final int TELEFONE = 23;
	public static final int CELULAR1 = 24;
	public static final int OPER_CELULAR1 = 25;
	public static final int CELULAR2 = 26;
	public static final int OPER_CELULAR2 = 27;
	public static final int COBERTURA_PSF = 28;
	public static final int TITULO_VACINA_CONTRA_VARICELA = 29;
	public static final int VACVARICELA = 30;
	public static final int MOTIVO_NAO_VACINACAO = 31;
	public static final int N_DOSES_VARIC = 32;
	public static final int VAC_VARIC_DT_DOSE1 = 33;
	public static final int VAC_VARIC_QUAL_DOSE1 = 34;
	public static final int VAC_VARIC_DT_DOSE2 = 35;
	public static final int VAC_VARIC_QUAL_DOSE2 = 36;
	public static final int TITULO_VACINA_MMR = 37;
	public static final int VACTRIPLICE = 38;
	public static final int N_DOSES_TRIPLICE = 39;
	public static final int MMR1 = 40;
	public static final int MMR2 = 41;
	public static final int TITULO_HISTORIA_PREGRESSA_DE_SAUDE_DA_CRIANCA = 42;
//	public static final int JA_TEVE_CATAPORA = 43;
	public static final int CONTATO_COM_QUEM_JA_TEVE = 43;
	public static final int LOCAL_CONTATO = 44;
	public static final int DOENCAFALCIFORME = 45;
	public static final int PREMATURIDADE = 46;
	public static final int INFECHIV = 47;
	public static final int IMUNODEFICIENCIA = 48;
	public static final int ASMA = 49;
	public static final int CORTICOIDE = 50;
	public static final int CANCER = 51;
	public static final int DOENCAPULMONARCRONICA = 52;
	public static final int DOENCARENALCRONICA = 53;
	public static final int DOENCACARDIACA = 54;
	public static final int DOENCAHEMATOLOGICA = 55;
	public static final int DOENCAHEPATICA = 56;
	public static final int ASPLENIA = 57;
	public static final int DIABETES = 58;
	public static final int TITULO_ANTECEDENTES_FAMILIARES = 59;
	public static final int FREQCRECHE1 = 60;
	public static final int FREQCRECHE2 = 61;
	public static final int FREQSERVSAUDE1 = 62;
	public static final int FREQSERVSAUDE2 = 63;
	public static final int HOSPITALIZACAO = 64;
	public static final int ES_CMAE = 65;
	public static final int NCCAS_CASA5 = 66;
	public static final int NCCAS_CASA10 = 67;
	public static final int QT_DE_PESSOAS_CASA = 68;
	public static final int QTD_EPESSOAS_QTO = 69;
	public static final int CASO_CASA_CRIANCA = 70;
	public static final int OBS_CASO = 71;
}
