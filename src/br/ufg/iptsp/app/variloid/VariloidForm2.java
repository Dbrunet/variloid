package br.ufg.iptsp.app.variloid;

public class VariloidForm2 {

	/** chave preferencia Formlario 2 */
	public static final String FORM2_CAMPO = "form2_campo_";
	public static final String FORM2_IMAGEM = "form2_imagem_";
	public static final String FORM2_INATIVAR = "form2_inativar_";

	/** id campo Formlario 2 */
	public static final String[] idCampos = new String[] { "nomeCrianca",
			"sexo", "dataNascimento", "criancaCataporaAnt",
			"contatoPessoaCatapora", "localContato", "residencia",
			"tipoLogradouro", "nomeLogradouro", "quadra", "lote", "numero",
			"complemento", "bairro", "????1", "erupcaoDifusaAgudo", "tcleAssinado",
			"nomeMae", "idadeMae", "nomePai", "telefoneFixo", "celular1",
			"operadoraCelular1", "celular2", "operadoraCelular2",
			"criancaCadastradaPSF", "dataInicioCatapora","????2", "febre", "anorexia",
			"malEstar", "dorAbdominal","????3", "dataInicioRash", "localizacaoRash",
			"primeiralocalizacaoRash", "numLesoes", "maculoPapular",
			"vesicular", "crosta", "maioriaLesoes", "diagnosticoFeitoPor",
			"pesoNascimentoGramas", "idadeGestacional", "raca",
			"criancaRecebeuVacinaCatapora", "razoesNaoVacinacao", "????4",
			"numDosesRecebidasContraVaricela", "dataPrimeiraDose",
			"qualVacinaUsadaPrimeiraDose", "dataSegundaDose",
			"qualVacinaUsadaSegundaDose", "????5" ,
			"criancaRecebeuVacinaTripliceViral", "numDosesRecebidasContraTripliceViral",//DOIS CASOS NOVOS
			"dataPrimeiraDoseMMR","dataSegundaDoseMMR", "alguemFrequentouCreche",
			"quemFamiliaFrequentouCreche", "alguemFrequentouServicoSaude",
			"quemFamiliaFrequentouServicoSaude", "criancaInternou",
			"maeTrabalhaFora", "escolaridadeMae",
			"numCriancasMenos5anosDomicilio",
			"numCriancasMenos10anosDomicilio", "quantasPessoasCasa",
			"quantasPessoasDormemMesmoComodoCrianca", "????6","doencaFalciforme",
			"prematuridade", "infeccaoHIVeAIDS", "imunodeficiencia", "asma",
			"usoCorticoideSistemico", "cancer", "doencaPulmonarCronica",
			"doencaRenalCronica", "doencaCardiaca",
			"doencaHematologicaCronica", "doencaHepaticaCronica",
			"aspleniaCongenitaOuFuncional", "diabetes", "numeroSwabsColetados",
			"idSwabColetadoMaculoPapular", "idSwabColetadoVesicula",
			"idSwabColetadoCrosta", "observacoesRelacionadasCaso" };

	/** campos Formlario 2 */
	public static final int[] campos = new int[] { R.string.formulario2_nome,
			R.string.formulario2_sexo, R.string.formulario2_data,
			R.string.formulario2_ja_teve_catapora,
			R.string.formulario2_contato_com_ja_teve_catapora,
			R.string.formulario2_local_contato,
			R.string.formulario2_residencia,
			R.string.formulario2_tipo_logradouro,
			R.string.formulario2_nome_logradouro, R.string.formulario2_qudra,
			R.string.formulario2_lote, R.string.formulario2_numero,
			R.string.formulario2_complemento, R.string.formulario2_bairro,
			R.string.formulario2_titulo_criterio_inclusao,
			R.string.formulario2_rash, R.string.formulario2_tcle,
			R.string.formulario2_nome_mae, R.string.formulario2_idade_mae,
			R.string.formulario2_nome_pai, R.string.formulario2_tel_fixo,
			R.string.formulario2_tel_celular1, R.string.formulario2_operadora1,
			R.string.formulario2_tel_celular2, R.string.formulario2_operadora2,
			R.string.formulario2_cobertura_psf,
			R.string.formulario2_data_inicio_varicela,
			R.string.formulario2_titulo_rash_sinais,
			R.string.formulario2_febre, R.string.formulario2_anorexia,
			R.string.formulario2_malestar, R.string.formulario2_dor_abdominal,
			R.string.formulario2_titulo_rash, R.string.formulario2_dt_rash,
			R.string.formulario2_loc_rash, R.string.formulario2_loc_rash2,
			R.string.formulario2_n_lesoes, R.string.formulario2_rash_mp,
			R.string.formulario2_rash_v, R.string.formulario2_rash_c,
			R.string.formulario2_lesoes, R.string.formulario2_diag_quem,
			R.string.formulario2_peso_nascer,
			R.string.formulario2_idade_gestacional, R.string.formulario2_raca,
			R.string.formulario2_vac_varicela,
			R.string.formulario2_motivo_nao_vacinacao,
			R.string.formulario2_titulo_vacina_contra_varicela,
			R.string.formulario2_n_doses_varic,
			R.string.formulario2_vac_varic_dt_dose1,
			R.string.formulario2_vac_varic_qual_dose1,
			R.string.formulario2_vac_varic_dt_dose2,
			R.string.formulario2_vac_varic_qual_dose2,
			R.string.formulario2_titulo_data_vacina_mmr,
			R.string.formulario2_vac_triplice_viral,//NOVO
			R.string.formulario2_n_doses_triplice_viral,//NOVO
			R.string.formulario2_mmr1, R.string.formulario2_mmr2,
			R.string.formulario2_freq_creche1,
			R.string.formulario2_freq_creche2,
			R.string.formulario2_freq_serv_saude1,
			R.string.formulario2_freq_serv_saude2,
			R.string.formulario2_hospitalizacao,
			R.string.formulario2_mae_trabalha, R.string.formulario2_esc_mae,
			R.string.formulario2_nc_cas_casa5,
			R.string.formulario2_nc_cas_casa10,
			R.string.formulario2_qtd_pessoas_casa,
			R.string.formulario2_qtd_pessoas_quarto,
			R.string.formulario2_doenca_titulo_falciforme,
			R.string.formulario2_doenca_falciforme,
			R.string.formulario2_prematuridade, R.string.formulario2_infec_hiv,
			R.string.formulario2_imuno_deficiencia, R.string.formulario2_asma,
			R.string.formulario2_corticoide, R.string.formulario2_cancer,
			R.string.formulario2_doenca_pulmonar_cronica,
			R.string.formulario2_doenca_renal_cronica,
			R.string.formulario2_doenca_cardiaca,
			R.string.formulario2_doenca_hematologica,
			R.string.formulario2_doenca_hepatica,
			R.string.formulario2_asplenia, R.string.formulario2_diabetes,
			R.string.formulario2_n_swabs, R.string.formulario2_id_swabs_macula,
			R.string.formulario2_id_swabs_visicula,
			R.string.formulario2_id_swabs_crosta, R.string.formulario2_obs_caso };

	/** keys Formlario 2 */
	public static final int NOME = 0;
	public static final int SEXO = 1;
	public static final int DATA_NASCIMENTO = 2;
	public static final int JA_TEVE_CATAPORA = 3;
	public static final int CONTATO_COM_QUEM_JA_TEVE = 4;
	public static final int LOCAL_CONTATO = 5;
	public static final int RESIDENCIA = 6;
	public static final int TIPO_LOGRADOURO = 7;
	public static final int NOME_LOGRADOURO = 8;
	public static final int QUADRA = 9;
	public static final int LOTE = 10;
	public static final int NUMERO = 11;
	public static final int COMPLEMENTO = 12;
	public static final int BAIRRO = 13;
	public static final int TITULO_CRITERIO_INCLUSAO = 14;
	public static final int RASH = 15;
	public static final int TCLE = 16;
	public static final int NOME_MAE = 17;
	public static final int IDADE_MAE = 18;
	public static final int NOME_PAI = 19;
	public static final int TELEFONE = 20;
	public static final int CELULAR1 = 21;
	public static final int OPER_CELULAR1 = 22;
	public static final int CELULAR2 = 23;
	public static final int OPER_CELULAR2 = 24;
	public static final int COBERTURA_PSF = 25;
	public static final int DT_INICIO_VARICELA = 26;
	public static final int TITULO_SINAIS_ANTES_DO_RASH = 27;
	public static final int FEBRE = 28;
	public static final int ANOREXIA = 29;
	public static final int MALESTAR = 30;
	public static final int DOR_ABDOMINAL = 31;
	public static final int TITULO_RASH = 32;
	public static final int DT_RASH = 33;
	public static final int LOC_RASH = 34;
	public static final int LOC_RASH2 = 35;
	public static final int N_LESOES = 36;
	public static final int RASH_MP = 37;
	public static final int RASH_V = 38;
	public static final int RASH_C = 39;
	public static final int LESOES = 40;
	public static final int DIAG_QUEM = 41;
	public static final int PESONASCER = 42;
	public static final int IDADEGESTACIONAL = 43;
	public static final int RACA = 44;
	public static final int VACVARICELA = 45;
	public static final int MOTIVO_NAO_VACINACAO = 46;
	public static final int TITULO_VACINA_CONTRA_VARICELA = 47;
	public static final int N_DOSES_VARIC = 48;
	public static final int VAC_VARIC_DT_DOSE1 = 49;
	public static final int VAC_VARIC_QUAL_DOSE1 = 50;
	public static final int VAC_VARIC_DT_DOSE2 = 51;
	public static final int VAC_VARIC_QUAL_DOSE2 = 52;
	public static final int TITULO_VACINA_TRIPLICE = 53;
	public static final int VACTRIPLICE = 54;//NOVO
	public static final int N_DOSES_TRIPLICE = 55;//NOVO
	public static final int MMR1 = 56;
	public static final int MMR2 = 57;
	public static final int FREQCRECHE1 = 58;
	public static final int FREQCRECHE2 = 59;
	public static final int FREQSERVSAUDE1 = 60;
	public static final int FREQSERVSAUDE2 = 61;
	public static final int HOSPITALIZACAO = 62;
	public static final int MAE_TRABALHA = 63;
	public static final int ES_CMAE = 64;
	public static final int NCCAS_CASA5 = 65;
	public static final int NCCAS_CASA10 = 66;
	public static final int QT_DE_PESSOAS_CASA = 67;
	public static final int QTD_EPESSOAS_QTO = 68;
	public static final int TITULO_DOENCAFALCIFORME = 69;
	public static final int DOENCAFALCIFORME = 70;
	public static final int PREMATURIDADE = 71;
	public static final int INFECHIV = 72;
	public static final int IMUNODEFICIENCIA = 73;
	public static final int ASMA = 74;
	public static final int CORTICOIDE = 75;
	public static final int CANCER = 76;
	public static final int DOENCAPULMONARCRONICA = 77;
	public static final int DOENCARENALCRONICA = 78;
	public static final int DOENCACARDIACA = 79;
	public static final int DOENCAHEMATOLOGICA = 80;
	public static final int DOENCAHEPATICA = 81;
	public static final int ASPLENIA = 82;
	public static final int DIABETES = 83;
	public static final int N_SWABS = 84;
	public static final int ID_SWAB_MACULA = 85;
	public static final int ID_SWAB_VESICULA = 86;
	public static final int ID_SWAB_CROSTA = 87;
	public static final int OBS_CASO = 88;
}
