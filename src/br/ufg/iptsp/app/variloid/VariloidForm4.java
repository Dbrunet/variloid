package br.ufg.iptsp.app.variloid;

public class VariloidForm4 {

	/** chave preferencia Formlario 4 */
	public static final String FORM4_CAMPO = "form4_campo_";
	public static final String FORM4_IMAGEM = "form4_imagem_";
	public static final String FORM4_INATIVAR = "form4_inativar_";

	/** id campo Formlario 4 */
	public static final String[] idCampos = new String[] { "nomeCrianca",
			"sexo", "dataNascimento", "local", "tcleAssinado", "residencia",
			"tipoLogradouro ", "nomeLogradouro", "quadra", "lote", "numero",
			"complemento", "bairro", "nomeMae", "idadeMae", "nomePai",
			"telefoneFixo", "celular1", "operadoraCelular1", "celular2",
			"operadoraCelular2", "criancaCadastradaPSF",
			"pesoNascimentoGramas", "idadeGestacional", "raca",
			"escolaridadeMae", "numCriancasMenos5anosDomicilio",
			"numCriancasMenos10anosDomicilio", "quantasPessoasCasa",
			"quantasPessoasDormemMesmoComodoCrianca",
			"criancaRecebeuVacinaPneumonia", "razoesNaoVacinacao", "????",
			"numDosesRecebidasPneumonia", "dataPrimeiraDose",
			"qualVacinaUsadaPrimeiraDose", "dataSegundaDose",
			"qualVacinaUsadaSegundaDose", "dataTerceiraDose",
			"qualVacinaUsadaTerceiraDose", "dataQuartaDose",
			"qualVacinaUsadaQuartaDose", "????", "numDosesRecebidasHib",
			"dataPrimeiraDoseHib", "dataSegundaDoseHib", "dataTerceiraDoseHib",
			"????", "alguemFrequentouCreche", "quemFamiliaFrequentouCreche",
			"alguemFrequentouServicoSaude",
			"quemFamiliaFrequentouServicoSaude", "????",
			"quantasInfeccoesOuvidoEmSeisMeses",
			"quantasInfeccoesOuvidoEmDozeMeses", "criancaInternou",
			"quantasVezesTevePneumoniaEmSeisMeses",
			"quantasVezesTevePneumoniaEmDozeMeses",
			"criancaInternouPorPneumoniaEmSeisMeses",
			"criancaInternouPorPneumoniaEmDozeMeses",
			"criancaTeveChiadoNoPeitoNoPassado",
			"qtdCrisesNosUltimosDozeMeses",
			"frequenciaDeSonoPrejudicadoPorChiadoNoPeito",
			"chiadoImpediuDizerPalavras", "criancaTeveAsma",
			"criancaTeveChiadoAposExercicios",
			"criancaTeveTosseSemEstarGripado",
			"criancaTeveChiadoNosUltimosDozeMeses",
			"doencaFalciforme", "prematuridade", "infeccaoHIVeAIDS",
			"imunodeficiencia", "asma", "usoCorticoideSistemico", "cancer",
			"doencaPulmonarCronica", "doencaRenalCronica", "doencaCardiaca",
			"doencaHematologicaCronica", "doencaHepaticaCronica",
			"aspleniaCongenitaOuFuncional", "diabetes", "????",
			"atividadesFisicasQuadro1", "criancaFazAtividadesQuadro1",
			"atividadesFisicasQuadro2", "criancaFazAtividadesQuadro2", "????",
			"criancaFezUsoAntibiotico", "qualAntibioticoUsado",
			"numeroSwabsColetados", "dataSwab", "idSwabColetado",
			"observacoesRelacionadasCaso" };

	/** campos Formlario 4 */
	public static final int[] campos = new int[] { 
			R.string.formulario4_nome,
			R.string.formulario4_sexo, 
			R.string.formulario4_data,
			R.string.formulario4_local, 
			R.string.formulario4_tcle, 
			R.string.formulario4_residencia,
			R.string.formulario4_tipo_logradouro,
			R.string.formulario4_nome_logradouro, 
			R.string.formulario4_qudra,
			R.string.formulario4_lote, 
			R.string.formulario4_numero,
			R.string.formulario4_complemento, 
			R.string.formulario4_bairro,
			R.string.formulario4_nome_mae, 
			R.string.formulario4_idade_mae, 
			R.string.formulario4_nome_pai, 
			R.string.formulario4_tel_fixo, 
			R.string.formulario4_tel_celular1,
			R.string.formulario4_operadora1,
			R.string.formulario4_tel_celular2,
			R.string.formulario4_operadora2,
			R.string.formulario4_cobertura_psf,
			R.string.formulario4_peso_nascer,
			R.string.formulario4_idade_gestacional, 
			R.string.formulario4_raca,
			R.string.formulario4_esc_mae,
			R.string.formulario4_nc_cas_casa5,
			R.string.formulario4_nc_cas_casa10,
			R.string.formulario4_qtd_pessoas_casa,
			R.string.formulario4_qtd_pessoas_quarto,
			R.string.formulario4_vac_pcv,
			R.string.formulario4_motivo_nao_vacinacao,
			
			R.string.formulario4_titulo_vacina_contra_pneumonia,
			R.string.formulario4_n_doses_pcv,
			R.string.formulario4_vac_pcv_dt_dose1,
			R.string.formulario4_vac_pcv_qual_dose1,
			R.string.formulario4_vac_pcv_dt_dose2,
			R.string.formulario4_vac_pcv_qual_dose2,
			R.string.formulario4_vac_pcv_dt_dose3,
			R.string.formulario4_vac_pcv_qual_dose3,
			R.string.formulario4_vac_pcv_dt_dose4,
			R.string.formulario4_vac_pcv_qual_dose4,
			
			R.string.formulario4_titulo_vacina_contra_hib,
			R.string.formulario4_n_doses_hib,
			R.string.formulario4_vac_hib_dt_dose1,
			R.string.formulario4_vac_hib_dt_dose2,
			R.string.formulario4_vac_hib_dt_dose3,
			
			R.string.formulario4_titulo_historia_pregressa_saude,
			R.string.formulario4_freq_creche1,
			R.string.formulario4_freq_creche2,
			R.string.formulario4_freq_serv_saude1,
			R.string.formulario4_freq_serv_saude2, 
			
			R.string.formulario4_titulo_antecedentes_patologicos,
			R.string.formulario4_otite6,
			R.string.formulario4_otite12, 
			R.string.formulario4_hospitalizacao,
			R.string.formulario4_pnm6, 
			R.string.formulario4_pnm12,
			R.string.formulario4_hospitalizacao_pnm6,
			R.string.formulario4_hospitalizacao_pnm12,
			R.string.formulario4_teve_chiado_no_peito_no_passado,
			R.string.formulario4_qtd_crises_nos_ultimos_doze_meses,
			R.string.formulario4_frequencia_de_sono_prejudicado_por_chiado_no_peito,
			R.string.formulario4_chiado_impediu_dizer_palavras,
			R.string.formulario4_teve_asma,
			R.string.formulario4_chiado_apos_exercicios,
			R.string.formulario4_tosse_sem_estar_gripado,
			R.string.formulario4_teve_chiado_nos_ultimos_doze_meses,
			R.string.formulario4_doenca_falciforme,
			R.string.formulario4_prematuridade, 
			R.string.formulario4_infec_hiv,
			R.string.formulario4_imuno_deficiencia, 
			R.string.formulario4_asma,
			R.string.formulario4_corticoide, 
			R.string.formulario4_cancer,
			R.string.formulario4_doenca_pulmonar_cronica,
			R.string.formulario4_doenca_renal_cronica,
			R.string.formulario4_doenca_cardiaca,
			R.string.formulario4_doenca_hematologica,
			R.string.formulario4_doenca_hepatica,
			R.string.formulario4_asplenia, 
			R.string.formulario4_diabetes,
			
			R.string.formulario4_titulo_atividade_fisica,
			R.string.formulario4_quadro_um_a_crianca_praticou,
			R.string.formulario4_seu_filho_faz_pelo_menos_sessenta_minutos,
			R.string.formulario4_quadro_dois_a_crianca_praticou,
			R.string.formulario4_seu_filho_faz_atividade_fisica_pelos_menos_tres_vezes,
			
			R.string.formulario4_titulo_swab,
			R.string.formulario4_uso_atb, 
			R.string.formulario4_qual_atb,
			R.string.formulario4_n_swabs,
			R.string.formulario4_data_swab,
			R.string.formulario4_id_swabs_coletado,
			R.string.formulario4_obs_caso };

	/** keys Formlario 4 */
	public static final int NOME = 0;
	public static final int SEXO = 1;
	public static final int DATA_NASCIMENTO = 2;
	public static final int LOCAL = 3;
	public static final int TCLE = 4;
	public static final int RESIDENCIA = 5;
	public static final int TIPO_LOGRADOURO = 6;
	public static final int NOME_LOGRADOURO = 7;
	public static final int QUADRA = 8;
	public static final int LOTE = 9;
	public static final int NUMERO = 10;
	public static final int COMPLEMENTO = 11;
	public static final int BAIRRO = 12;
	public static final int NOME_MAE = 13;
	public static final int IDADE_MAE = 14;
	public static final int NOME_PAI = 15;
	public static final int TELEFONE = 16;
	public static final int CELULAR1 = 17;
	public static final int OPER_CELULAR1 = 18;
	public static final int CELULAR2 = 19;
	public static final int OPER_CELULAR2 = 20;
	public static final int COBERTURA_PSF = 21;
	public static final int PESONASCER = 22;
	public static final int IDADEGESTACIONAL = 23;
	public static final int RACA = 24;
	public static final int ES_CMAE = 25;
	public static final int NCCAS_CASA5 = 26;
	public static final int NCCAS_CASA10 = 27;
	public static final int QT_DE_PESSOAS_CASA = 28;
	public static final int QTD_EPESSOAS_QTO = 29;
	public static final int VACPCV = 30;
	public static final int MOTIVO_NAO_VACINACAO = 31;
	
	public static final int TITULO_VACINA_CONTRA_PNEUMONIA = 32;
	public static final int N_DOSES_PCV = 33;
	public static final int VAC_PCV_DT_DOSE1 = 34;
	public static final int VAC_PCV_QUAL_DOSE1 = 35;
	public static final int VAC_PCV_DT_DOSE2 = 36;
	public static final int VAC_PCV_QUAL_DOSE2 = 37;
	public static final int VAC_PCV_DT_DOSE3 = 38;
	public static final int VAC_PCV_QUAL_DOSE3 = 39;
	public static final int VAC_PCV_DT_DOSE4 = 40;
	public static final int VAC_PCV_QUAL_DOSE4 = 41;
	
	public static final int TITULO_VACINA_CONTRA_HIB = 42;
	public static final int N_DOSES_HIB = 43;
	public static final int VAC_HIB_DT_DOSE1 = 44;
	public static final int VAC_HIB_DT_DOSE2 = 45;
	public static final int VAC_HIB_DT_DOSE3 = 46;
	
	public static final int TITULO_HISTORICO_PREGRESSA_SAUDE = 47;
	public static final int FREQCRECHE1 = 48;
	public static final int FREQCRECHE2 = 49;
	public static final int FREQSERVSAUDE1 = 50;
	public static final int FREQSERVSAUDE2 = 51;
	
	public static final int TITULO_ANTECEDENTES_PATOLOGICOS = 52;
	public static final int OTITE6 = 53;
	public static final int OTITE12 = 54;
	public static final int HOSPITALIZACAO = 55;
	public static final int PNM6 = 56;
	public static final int PNM12 = 57;
	public static final int HOSPITALIZACAO_PNM6 = 58;
	public static final int HOSPITALIZACAO_PNM12 = 59;
	public static final int TEVE_CHIADO_NO_PEITO_NO_PASSADO = 60;
	public static final int QTD_CRISES_NOS_ULTIMOS_DOZE_MESES = 61;
	public static final int FREQ_DE_SONO_PREJUDICADO_POR_CHIADO = 62;
	public static final int CHIADO_IMPEDIU_DIZER_PALAVRAS = 63;
	public static final int TEVE_ASMA = 64;
	public static final int TEVE_CHIADO_APOS_EXERCICIOS = 65;
	public static final int TEVE_TOSSE_SEM_GRIPAR = 66;
	public static final int TEVE_CHIADO_NOS_ULTIMOS_DOZE_MESES = 67;
	public static final int DOENCAFALCIFORME = 68;
	public static final int PREMATURIDADE = 69;
	public static final int INFECHIV = 70;
	public static final int IMUNODEFICIENCIA = 71;
	public static final int ASMA = 72;
	public static final int CORTICOIDE = 73;
	public static final int CANCER = 74;
	public static final int DOENCAPULMONARCRONICA = 75;
	public static final int DOENCARENALCRONICA = 76;
	public static final int DOENCACARDIACA = 77;
	public static final int DOENCAHEMATOLOGICA = 78;
	public static final int DOENCAHEPATICA = 79;
	public static final int ASPLENIA = 80;
	public static final int DIABETES = 81;
	
	public static final int TITULO_ATIVIDADES = 82;
	public static final int QUADRO1_QUESTAO1 = 83;
	public static final int QUADRO1_QUESTAO2 = 84;
	public static final int QUADRO2_QUESTAO1 = 85;
	public static final int QUADRO2_QUESTAO2 = 86;
	
	public static final int TITULO_SWABS = 87;
	public static final int USO_ATB = 88;
	public static final int QUAL_ATB = 89;
	public static final int N_SWABS = 90;
	public static final int DATA_SWABS = 91;
	public static final int ID_SWAB_COLETADO = 92;
	public static final int OBS_CASO = 93;
}
