package br.ufg.iptsp.app.variloid;

public class VariloidForm3 {

	/** chave preferencia Formlario 2 */
	public static final String FORM3_CAMPO = "form3_campo_";
	public static final String FORM3_IMAGEM = "form3_imagem_";
	public static final String FORM3_INATIVAR = "form3_inativar_";

	/** id campo Formlario 3 */
//	public static final String[] idCampos = new String[] { "nomeCrianca",
//			"sexo", "dataNascimento", "criancaCataporaAnt",
//			"contatoPessoaCatapora", "localContato", "residencia",
//			"tipoLogradouro", "nomeLogradouro", "quadra", "lote", "numero",
//			"complemento", "bairro", "????1", "tcleAssinado",
//			"nomeMae", "idadeMae", "nomePai", "telefoneFixo", "celular1",
//			"operadoraCelular1", "celular2", "operadoraCelular2",
//			"criancaCadastradaPSF", "pesoNascimentoGramas", "idadeGestacional",
//			"raca","????2", "numDosesRecebidasContraVaricela", "dataPrimeiraDose",
//			"qualVacinaUsadaPrimeiraDose", "dataSegundaDose",
//			"qualVacinaUsadaSegundaDose", "????3",
//			"criancaRecebeuVacinaTripliceViral", "numDosesRecebidasContraTripliceViral",//DOIS CASOS NOVOS
//			"dataPrimeiraDoseMMR","dataSegundaDoseMMR", "alguemFrequentouCreche",
//			"quemFamiliaFrequentouCreche", "alguemFrequentouServicoSaude",
//			"quemFamiliaFrequentouServicoSaude", "criancaInternou",
//			"maeTrabalhaFora", "escolaridadeMae",
//			"numCriancasMenos5anosDomicilio",
//			"numCriancasMenos10anosDomicilio", "quantasPessoasCasa",
//			"quantasPessoasDormemMesmoComodoCrianca", "????4", "doencaFalciforme",
//			"prematuridade", "infeccaoHIVeAIDS", "imunodeficiencia", "asma",
//			"usoCorticoideSistemico", "cancer", "doencaPulmonarCronica",
//			"doencaRenalCronica", "doencaCardiaca",
//			"doencaHematologicaCronica", "doencaHepaticaCronica",
//			"aspleniaCongenitaOuFuncional", "diabetes",
//			"observacoesRelacionadasCaso" };

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

	/** keys Formlario 3 */
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
	public static final int TCLE = 15;
	public static final int NOME_MAE = 16;
	public static final int IDADE_MAE = 17;
	public static final int NOME_PAI = 18;
	public static final int TELEFONE = 19;
	public static final int CELULAR1 = 20;
	public static final int OPER_CELULAR1 = 21;
	public static final int CELULAR2 = 22;
	public static final int OPER_CELULAR2 = 23;
	public static final int COBERTURA_PSF = 24;
	public static final int PESONASCER = 25;
	public static final int IDADEGESTACIONAL = 26;
	public static final int RACA = 27;
	public static final int TITULO_VACINA_CONTRA_VARICELA = 28;
	public static final int N_DOSES_VARIC = 29;
	public static final int VAC_VARIC_DT_DOSE1 = 30;
	public static final int VAC_VARIC_QUAL_DOSE1 = 31;
	public static final int VAC_VARIC_DT_DOSE2 = 32;
	public static final int VAC_VARIC_QUAL_DOSE2 = 33;
	public static final int TITULO_VACINA_TRIPLICE = 34;
	public static final int VACTRIPLICE = 35;//NOVO
	public static final int N_DOSES_TRIPLICE = 36;//NOVO
	public static final int MMR1 = 37;
	public static final int MMR2 = 38;
	public static final int FREQCRECHE1 = 39;
	public static final int FREQCRECHE2 = 40;
	public static final int FREQSERVSAUDE1 = 41;
	public static final int FREQSERVSAUDE2 = 42;
	public static final int HOSPITALIZACAO = 43;
	public static final int MAE_TRABALHA = 44;
	public static final int ES_CMAE = 45;
	public static final int NCCAS_CASA5 = 46;
	public static final int NCCAS_CASA10 = 47;
	public static final int QT_DE_PESSOAS_CASA = 48;
	public static final int QTD_EPESSOAS_QTO = 49;
	public static final int TITULO_DOENCAFALCIFORME = 50;
	public static final int DOENCAFALCIFORME = 51;
	public static final int PREMATURIDADE = 52;
	public static final int INFECHIV = 53;
	public static final int IMUNODEFICIENCIA = 54;
	public static final int ASMA = 55;
	public static final int CORTICOIDE = 56;
	public static final int CANCER = 57;
	public static final int DOENCAPULMONARCRONICA = 58;
	public static final int DOENCARENALCRONICA = 59;
	public static final int DOENCACARDIACA = 60;
	public static final int DOENCAHEMATOLOGICA = 61;
	public static final int DOENCAHEPATICA = 62;
	public static final int ASPLENIA = 63;
	public static final int DIABETES = 64;
	public static final int OBS_CASO = 65;
}
