package br.ufg.iptsp.app.variloid;

public class VariloidForm2 {

	/** chave preferencia Formlario 2 */
	public static final String FORM2_CAMPO = "form2_campo_";
	public static final String FORM2_IMAGEM = "form2_imagem_";
	public static final String FORM2_INATIVAR = "form2_inativar_";

	/** campos Formlario 2 */
	public static final int[] campos = new int[] { 
		//Elegibilidade do caso
		R.string.formulario2_titulo_elegibilidade_do_caso,
		R.string.formulario2_lesao_sugestiva, R.string.formulario2_tcle,
		//Identifica��o
		R.string.formulario2_titulo_identificacao,
		R.string.formulario2_nome, R.string.formulario2_sexo, R.string.formulario2_data,
		R.string.formulario2_residencia,
		R.string.formulario2_tipo_logradouro, R.string.formulario2_nome_logradouro,
		R.string.formulario2_qudra, R.string.formulario2_lote, R.string.formulario2_numero,
		R.string.formulario2_complemento, R.string.formulario2_bairro,R.string.formulario2_municipio,
		R.string.formulario2_nome_mae, R.string.formulario2_idade_mae, R.string.formulario2_nome_pai,
		R.string.formulario2_peso_nascer, R.string.formulario2_idade_gestacional, R.string.formulario2_raca,
		R.string.formulario2_tel_fixo, R.string.formulario2_tel_celular1, R.string.formulario2_operadora1,
		R.string.formulario2_tel_celular2, R.string.formulario2_operadora2, R.string.formulario2_cobertura_psf,
		//Sinais 24 - 48 horas antes do RASH
		R.string.formulario2_titulo_sinais_rash,
		R.string.formulario2_febre, R.string.formulario2_anorexia, R.string.formulario2_malestar, R.string.formulario2_dor_abdominal,
		//Descri��o do RASH Cut�neo
		R.string.formulario2_titulo_descricao_rash,
		R.string.formulario2_dt_rash, R.string.formulario2_loc_rash, R.string.formulario2_loc_rash2, R.string.formulario2_n_lesoes,
		R.string.formulario2_rash_mp, R.string.formulario2_rash_v, R.string.formulario2_rash_c, R.string.formulario2_lesoes,  R.string.formulario2_rash, R.string.formulario2_diag_quem,
		//Vacina contra Varicela
		R.string.formulario2_titulo_vacina_contra_varicela,
		R.string.formulario2_vac_varicela, R.string.formulario2_motivo_nao_vacinacao, R.string.formulario2_n_doses_varic,
		R.string.formulario2_vac_varic_dt_dose1, R.string.formulario2_vac_varic_qual_dose1, R.string.formulario2_vac_varic_dt_dose2, R.string.formulario2_vac_varic_qual_dose2,
		//Vacina MMR
		R.string.formulario2_titulo_vacina_mmr,
		R.string.formulario2_vac_triplice_viral, R.string.formulario2_n_doses_triplice_viral,
		R.string.formulario2_mmr1, R.string.formulario2_mmr2,
		//Hist�ria pregressa de sa�de da crian�a
		R.string.formulario2_titulo_historia_pregressa,
		R.string.formulario2_ja_teve_catapora, R.string.formulario2_contato_com_ja_teve_catapora,R.string.formulario2_local_contato,
		R.string.formulario2_doenca_falciforme, R.string.formulario2_prematuridade, R.string.formulario2_infec_hiv,
		R.string.formulario2_imuno_deficiencia, R.string.formulario2_asma, R.string.formulario2_corticoide, R.string.formulario2_cancer,
		R.string.formulario2_doenca_pulmonar_cronica, R.string.formulario2_doenca_renal_cronica,
		R.string.formulario2_doenca_cardiaca, R.string.formulario2_doenca_hematologica,
		R.string.formulario2_doenca_hepatica, R.string.formulario2_asplenia, R.string.formulario2_diabetes,
		//Antecedentes Familiares
		R.string.formulario2_titulo_antecedentes_familiares,
		R.string.formulario2_freq_creche1, R.string.formulario2_freq_creche2, R.string.formulario2_freq_serv_saude1,
		R.string.formulario2_freq_serv_saude2, R.string.formulario2_hospitalizacao, R.string.formulario2_esc_mae, R.string.formulario2_nc_cas_casa5,
		R.string.formulario2_nc_cas_casa10, R.string.formulario2_qtd_pessoas_casa, R.string.formulario2_qtd_pessoas_quarto,
		//Swabs Coletados
		R.string.formulario2_titulo_swabs_coletados,
		R.string.formulario2_caso_casa_crianca, R.string.formulario2_n_swabs, R.string.formulario2_id_swabs_macula,
		R.string.formulario2_id_swabs_visicula, R.string.formulario2_id_swabs_crosta, R.string.formulario2_obs_caso};
	
	public static final String[] idCampos = new String[] { 
		//Elegibilidade do caso
		"????1", "lesaoSugestivaVaricela", "tcleAssinado", 
		//Identifica��o
		"????2", "nomeCrianca", "sexo", "dataNascimento", "residencia", "tipoLogradouro", "nomeLogradouro", "quadra", "lote", "numero",
		"complemento", "bairro", "municipio", "nomeMae", "idadeMae", "nomePai", "pesoNascimentoGramas", "idadeGestacional",
		"raca","telefoneFixo", "celular1", "operadoraCelular1", "celular2", "operadoraCelular2", "criancaCadastradaPSF",
		 //Sinais 24 - 48 horas antes do RASH
		"????3", "febre", "anorexia","malEstar", "dorAbdominal",
		// Descri��o do RASH Cut�neo
		"????4", "dataInicioRash", "localizacaoRash","primeiralocalizacaoRash", "numLesoes", "maculoPapular",
		"vesicular", "crosta", "maioriaLesoes","erupcaoDifusaAgudo", "diagnosticoFeitoPor",
		//Vacina contra varicela
		"????5", "criancaRecebeuVacinaCatapora", "razoesNaoVacinacao", "numDosesRecebidasContraVaricela", "dataPrimeiraDose",
		"qualVacinaUsadaPrimeiraDose","dataSegundaDose", "qualVacinaUsadaSegundaDose",
		//Vacina MMR
		"????6" , "criancaRecebeuVacinaTripliceViral", "numDosesRecebidasContraTripliceViral", "dataPrimeiraDoseMMR","dataSegundaDoseMMR",
		//Hist�ria pregressa de sa�de da crian�a
		"????7", "criancaCataporaAnt", "contatoPessoaCatapora", "localContato", "doencaFalciforme", "prematuridade", "infeccaoHIVeAIDS", 
		"imunodeficiencia", "asma", "usoCorticoideSistemico", "cancer", "doencaPulmonarCronica", "doencaRenalCronica", "doencaCardiaca",
		"doencaHematologicaCronica", "doencaHepaticaCronica", "aspleniaCongenitaOuFuncional", "diabetes", 
		//Antecedentes Familiares
		"????8", "alguemFrequentouCreche", "quemFamiliaFrequentouCreche", "alguemFrequentouServicoSaude", "quemFamiliaFrequentouServicoSaude", "criancaInternou",
		 "escolaridadeMae", "numCriancasMenos5anosDomicilio", "numCriancasMenos10anosDomicilio", "quantasPessoasCasa", "quantasPessoasDormemMesmoComodoCrianca", 
		//Swabs Coletados
		"????9", "casoCasaCrianca",  "numeroSwabsColetados", "idSwabColetadoMaculoPapular", "idSwabColetadoVesicula", "idSwabColetadoCrosta", "observacoesRelacionadasCaso" };
	
	
	/** keys Formlario 2 */
	public static final int TITULO_ELEGIBILIDADE_DO_CASO = 0;
	public static final int LESAO_SUGESTIVA_VARICELA = 1;
	public static final int TCLE = 2;
	public static final int TITULO_IDENTIFICACAO = 3;
	public static final int NOME = 4;
	public static final int SEXO = 5;
	public static final int DATA_NASCIMENTO = 6;
	public static final int RESIDENCIA = 7;
	public static final int TIPO_LOGRADOURO = 8;
	public static final int NOME_LOGRADOURO = 9;
	public static final int QUADRA = 10;
	public static final int LOTE = 11;
	public static final int NUMERO = 12;
	public static final int COMPLEMENTO = 13;
	public static final int BAIRRO = 14;
	public static final int MUNICIPIO = 15;
	public static final int NOME_MAE = 16;
	public static final int IDADE_MAE = 17;
	public static final int NOME_PAI = 18;
	public static final int PESONASCER = 19;
	public static final int IDADEGESTACIONAL = 20;
	public static final int RACA = 21;
	public static final int TELEFONE = 22;
	public static final int CELULAR1 = 23;
	public static final int OPER_CELULAR1 = 24;
	public static final int CELULAR2 = 25;
	public static final int OPER_CELULAR2 = 26;
	public static final int COBERTURA_PSF = 27;
	public static final int TITULO_SINAIS_ANTES_DO_RASH = 28;
	public static final int FEBRE = 29;
	public static final int ANOREXIA = 30;
	public static final int MALESTAR = 31;
	public static final int DOR_ABDOMINAL = 32;
	public static final int DESCRICAO_DO_RASH_CUTANEO = 33;
	public static final int DT_RASH = 34;
	public static final int LOC_RASH = 35;
	public static final int LOC_RASH2 = 36;
	public static final int N_LESOES = 37;
	public static final int RASH_MP = 38;
	public static final int RASH_V = 39;
	public static final int RASH_C = 40;
	public static final int LESOES = 41;
	public static final int ERUPCAO_DIFUSA_AGUDA = 42;
	public static final int DIAG_QUEM = 43;
	public static final int TITULO_VACINA_CONTRA_VARICELA = 44;
	public static final int VACVARICELA = 45;
	public static final int MOTIVO_NAO_VACINACAO = 46;
	public static final int N_DOSES_VARIC = 47;
	public static final int VAC_VARIC_DT_DOSE1 = 48;
	public static final int VAC_VARIC_QUAL_DOSE1 = 49;
	public static final int VAC_VARIC_DT_DOSE2 = 50;
	public static final int VAC_VARIC_QUAL_DOSE2 = 51;
	public static final int TITULO_VACINA_MMR = 52;
	public static final int VACTRIPLICE = 53;
	public static final int N_DOSES_TRIPLICE = 54;
	public static final int MMR1 = 55;
	public static final int MMR2 = 56;
	public static final int HISTORIA_PREGRESSA_DE_SAUDE_DA_CRIANCA = 57;
	public static final int JA_TEVE_CATAPORA = 58;
	public static final int CONTATO_COM_QUEM_JA_TEVE = 59;
	public static final int LOCAL_CONTATO = 60;
	public static final int DOENCAFALCIFORME = 61;
	public static final int PREMATURIDADE = 62;
	public static final int INFECHIV = 63;
	public static final int IMUNODEFICIENCIA = 64;
	public static final int ASMA = 65;
	public static final int CORTICOIDE = 66;
	public static final int CANCER = 67;
	public static final int DOENCAPULMONARCRONICA = 68;
	public static final int DOENCARENALCRONICA = 69;
	public static final int DOENCACARDIACA = 70;
	public static final int DOENCAHEMATOLOGICA = 71;
	public static final int DOENCAHEPATICA = 72;
	public static final int ASPLENIA = 73;
	public static final int DIABETES = 74;
	public static final int TITULO_ANTECEDENTES_FAMILIARES = 75;
	public static final int FREQCRECHE1 = 76;
	public static final int FREQCRECHE2 = 77;
	public static final int FREQSERVSAUDE1 = 78;
	public static final int FREQSERVSAUDE2 = 79;
	public static final int HOSPITALIZACAO = 80;
	public static final int ES_CMAE = 81;
	public static final int NCCAS_CASA5 = 82;
	public static final int NCCAS_CASA10 = 83;
	public static final int QT_DE_PESSOAS_CASA = 84;
	public static final int QTD_EPESSOAS_QTO = 85;
	public static final int TITULO_SWABS_COLETADOS = 86;
	public static final int CASO_CASA_CRIANCA = 87;
	public static final int N_SWABS = 88;
	public static final int ID_SWAB_MACULA = 89;
	public static final int ID_SWAB_VESICULA = 90;
	public static final int ID_SWAB_CROSTA = 91;
	public static final int OBS_CASO = 92;
}
