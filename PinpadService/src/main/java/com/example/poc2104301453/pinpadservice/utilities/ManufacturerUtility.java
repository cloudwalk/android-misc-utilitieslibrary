package com.example.poc2104301453.pinpadservice.utilities;

import com.example.poc2104301453.pinpadlibrary.ABECS;

import br.com.verifone.bibliotecapinpad.definicoes.CodigosRetorno;

public class ManufacturerUtility {
    private static final String TAG_LOGCAT = ManufacturerUtility.class.getSimpleName();

    private ManufacturerUtility() {
        /* Nothing to do */
    }

    public static ABECS.STAT toSTAT(CodigosRetorno input) {
        switch (input) {
            case OK:
                return ABECS.STAT.ST_OK;
            case CHAMADA_INVALIDA:
                return ABECS.STAT.ST_INVCALL;
            case PARAMETRO_INVALIDO:
                return ABECS.STAT.ST_INVPARM;
            case TIMEOUT:
                return ABECS.STAT.ST_TIMEOUT;

            case OPERACAO_CANCELADA:
            case OPERACAO_ABORTADA:
                return ABECS.STAT.ST_CANCEL;

            case PINPAD_NAO_INICIALIZADO:
                return ABECS.STAT.PP_NOTOPEN;
            case MODELO_INVALIDO:
                return ABECS.STAT.PP_INVMODEL;
            case OPERACAO_NAO_SUPORTADA:
                return ABECS.STAT.PP_NOFUNC;
            case TABELAS_EXPIRADAS:
                return ABECS.STAT.ST_TABVERDIF;
            case ERRO_GRAVACAO_TABELAS:
                return ABECS.STAT.ST_TABERR;
            case ERRO_LEITURA_CARTAO_MAG:
                return ABECS.STAT.ST_MCDATAERR;
            case CHAVE_PIN_AUSENTE:
                return ABECS.STAT.ST_ERRKEY;
            case CARTAO_AUSENTE:
                return ABECS.STAT.ST_NOCARD;
            case PINPAD_OCUPADO:
                return ABECS.STAT.ST_PINBUSY;
            case ERRO_MODULO_SAM:
                return ABECS.STAT.PP_SAMERR;
            case SAM_AUSENTE:
                return ABECS.STAT.PP_NOSAM;
            case SAM_INVALIDO:
                return ABECS.STAT.PP_SAMINV;
            case CARTAO_MUDO:
                return ABECS.STAT.ST_DUMBCARD;
            case ERRO_COMUNICACAO_CARTAO:
                return ABECS.STAT.ST_ERRCARD;
            case CARTAO_INVALIDADO:
                return ABECS.STAT.ST_CARDINVALIDAT;
            case CARTAO_COM_PROBLEMAS:
                return ABECS.STAT.ST_CARDPROBLEMS;
            case CARTAO_COM_DADOS_INVALIDOS:
                return ABECS.STAT.ST_CARDINVDATA;
            case CARTAO_SEM_APLICACAO:
                return ABECS.STAT.ST_CARDAPPNAV;
            case APLICACAO_NAO_UTILIZADA:
                return ABECS.STAT.ST_CARDAPPNAUT;
            case ERRO_FALLBACK:
                return ABECS.STAT.ST_ERRFALLBACK;
            case VALOR_INVALIDO:
                return ABECS.STAT.ST_INVAMOUNT;
            case EXCEDE_CAPACIDADE_AID:
                return ABECS.STAT.ST_ERRMAXAID;
            case CARTAO_BLOQUEADO:
                return ABECS.STAT.ST_CARDBLOCKED;
            case MULTIPLOS_CTLSS:
                return ABECS.STAT.ST_CTLSMULTIPLE;
            case ERRO_COMUNICACAO_CTLSS:
                return ABECS.STAT.ST_CTLSCOMMERR;
            case CTLSS_INVALIDADO:
                return ABECS.STAT.ST_CTLSINVALIDAT;
            case CTLSS_COM_PROBLEMAS:
                return ABECS.STAT.ST_CTLSPROBLEMS;
            case CTLSS_SEM_APLICACAO:
                return ABECS.STAT.ST_CTLSAPPNAV;
            case CTLSS_APLICACAO_NAO_SUPORTADA:
                return ABECS.STAT.ST_CTLSAPPNAUT;
            case CTLSS_DISPOSITIVO_EXTERNO:
                return ABECS.STAT.ST_CTLSEXTCVM;
            case CTLSS_MUDA_INTERFACE:
                return ABECS.STAT.ST_CTLSIFCHG;

            default:
                return ABECS.STAT.ST_INTERR;
        }
    }
}
