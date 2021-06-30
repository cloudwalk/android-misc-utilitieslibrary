package io.cloudwalk.pos.pinpadservice.commands;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import io.cloudwalk.pos.pinpadlibrary.ABECS;
import io.cloudwalk.pos.pinpadservice.PinpadAbstractionLayer;
import io.cloudwalk.pos.pinpadservice.utilities.ManufacturerUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import br.com.verifone.bibliotecapinpad.AcessoFuncoesPinpad;
import br.com.verifone.bibliotecapinpad.entradas.EntradaComandoGetCard;
import br.com.verifone.bibliotecapinpad.saidas.SaidaComandoGetCard;

public class GCX {
    private static final String TAG_LOGCAT = GCX.class.getSimpleName();

    private static String SPE_PANMASK = "9999";

    private static AcessoFuncoesPinpad getPinpad() {
        return PinpadAbstractionLayer.getInstance().getPinpad();
    }

    private static Bundle parseRSP(SaidaComandoGetCard response) {
        Bundle output = new Bundle();

        switch (response.obtemTipoCartaoLido()) {
            case MAGNETICO:
                output.putInt(ABECS.PP_CARDTYPE, 0);

                SaidaComandoGetCard.StatusUltimaLeituraChip
                        status = response.obtemStatusUltimaLeituraChip();

                switch (status) { /* 2021-06-29: mandatory - 'null' should trigger an exception, to
                                   * be caught and saved by the request handler */
                    case BEM_SUCEDIDA:
                        output.putInt(ABECS.PP_ICCSTAT, 0);
                        break;

                    case ERRO_PASSIVEL_FALLBACK:
                        output.putInt(ABECS.PP_ICCSTAT, 1);
                        break;

                    case APLICACAO_NAO_SUPORTADA:
                        output.putInt(ABECS.PP_ICCSTAT, 2);
                        break;

                    default:
                        /* Nothing to do */
                        break;
                }
                break;

            case EMV_COM_CONTATO:
                output.putInt(ABECS.PP_CARDTYPE, 3);
                break;

            case TARJA_SEM_CONTATO:
                output.putInt(ABECS.PP_CARDTYPE, 5);
                break;

            case EMV_SEM_CONTATO:
                output.putInt(ABECS.PP_CARDTYPE, 6);

                switch (response.obtemTipoDispositivoCtls()) {
                    case TABLET:
                        output.putString(ABECS.PP_DEVTYPE, "10");
                        break;

                    case RELOGIO:
                        output.putString(ABECS.PP_DEVTYPE, "03");
                        break;

                    case CHAVEIRO:
                        output.putString(ABECS.PP_DEVTYPE, "02");
                        break;

                    case PULSEIRA:
                        output.putString(ABECS.PP_DEVTYPE, "05");
                        break;

                    case CAPA_TELEFONE:
                        output.putString(ABECS.PP_DEVTYPE, "06");
                        break;

                    case ETIQUETA_MOVEL:
                        output.putString(ABECS.PP_DEVTYPE, "04");
                        break;

                    case TELEFONE_MOVEL:
                        output.putString(ABECS.PP_DEVTYPE, "01");
                        break;

                    default:
                        output.putString(ABECS.PP_DEVTYPE, "00");
                        break;
                }
                break;

            default:
                /* Nothing to do */
                break;
        }

        List<SaidaComandoGetCard.InformacaoTabelaAID> appl =
                response.obtemInformacaoTabelaAIDs();

        if (output.getInt(ABECS.PP_CARDTYPE) != 0) {
            // TODO: PP_AIDTABINFO

            /*
            output.putInt   (ABECS.GCR_ACQIDX,   appl.get(0).obtemListaRegistrosAID().getListaIndiceAdquirente().get(0));
            output.putString(ABECS.GCR_RECIDX,   appl.get(0).obtemListaRegistrosAID().getListaIndiceRegistro().get(0) + "");
            output.putInt   (ABECS.GCR_APPTYPE,  appl.get(0).obtemTipoAplicacao());
             */
        }

        SaidaComandoGetCard.DadosCartao card = response.obtemDadosCartao();

        if (output.getInt   (ABECS.PP_CARDTYPE) == 3 ||
            output.getInt   (ABECS.PP_CARDTYPE) == 6) {

            output.putString(ABECS.PP_PAN,      card.obtemPan()); /* TODO: SPE_PANMASK */
            output.putInt   (ABECS.PP_PANSEQNO, card.obtemPanSequenceNumber());
            output.getString(ABECS.PP_CHNAME,   card.obtemNomePortador());
            output.putInt   (ABECS.PP_ISSCNTRY, card.obtemIssuerCountryCode());

            int PP_CARDEXP = -1;

            if (card.obtemDataVencimento() != null) {
                PP_CARDEXP = Integer.parseInt(((new SimpleDateFormat("yyMMdd", Locale.getDefault())).format(card.obtemDataVencimento())));
            }

            if (PP_CARDEXP > 0) {
                output.putInt(ABECS.PP_CARDEXP, PP_CARDEXP);
            }
        }

        if (card.obtemTrilha1() != null) {
            String PP_TRK1INC = ManufacturerUtility.getPP_TRKxINC(SPE_PANMASK, card, 1); /* TODO: INC as argument */

            output.putString(ABECS.PP_TRK1INC, PP_TRK1INC);
        }

        if (card.obtemTrilha2() != null) {
            String PP_TRK2INC = ManufacturerUtility.getPP_TRKxINC(SPE_PANMASK, card, 2);

            output.putString(ABECS.PP_TRK2INC, PP_TRK2INC);
        }

        if (card.obtemTrilha3() != null) {
            String PP_TRK3INC = ManufacturerUtility.getPP_TRKxINC(SPE_PANMASK, card, 3);

            output.putString(ABECS.PP_TRK3INC, PP_TRK3INC);
        }

        if (output.getInt(ABECS.PP_CARDTYPE) != 0) {
            output.putString(ABECS.PP_LABEL, card.obtemNomeAplicacao());
        }

        // TODO: PP_EMVDATA

        return output;
    }

    public static Bundle gcx(Bundle input)
            throws Exception {
        final long timestamp = SystemClock.elapsedRealtime();

        final Bundle[] output = { new Bundle() };
        final Semaphore[] semaphore = { new Semaphore(0, true) };

        long    SPE_CASHBACK    = input.getLong  (ABECS.SPE_CASHBACK, -1);
        int     SPE_TRNTYPE     = input.getInt   (ABECS.SPE_TRNTYPE, (SPE_CASHBACK != -1) ? ((int) 0x09) : ((int) 0x00));
        int     SPE_ACQREF      = input.getInt   (ABECS.SPE_ACQREF, 0);

        String  SPE_APPTYPE     = input.getString(ABECS.SPE_APPTYPE);
                SPE_APPTYPE     = (SPE_APPTYPE != null) ? SPE_APPTYPE : "99";

        String  SPE_AIDLIST     = input.getString(ABECS.SPE_AIDLIST);
        long    SPE_AMOUNT      = input.getLong  (ABECS.SPE_AMOUNT, -1);
        int     SPE_TRNCURR     = input.getInt   (ABECS.SPE_TRNCURR, -1);
        String  SPE_TRNDATE     = input.getString(ABECS.SPE_TRNDATE);
        String  SPE_TRNTIME     = input.getString(ABECS.SPE_TRNTIME);

        String  SPE_GCXOPT      = input.getString(ABECS.SPE_GCXOPT);
                SPE_GCXOPT      = (SPE_GCXOPT != null) ? SPE_GCXOPT   : "00000";

                SPE_PANMASK     = input.getString(ABECS.SPE_PANMASK);
                SPE_PANMASK     = (SPE_PANMASK != null) ? SPE_PANMASK : "9999";

        String  SPE_EMVDATA     = input.getString(ABECS.SPE_EMVDATA);
        String  SPE_TAGLIST     = input.getString(ABECS.SPE_TAGLIST);
        int     SPE_TIMEOUT     = input.getInt   (ABECS.SPE_TIMEOUT, -1);
        String  SPE_DSPMSG      = input.getString(ABECS.SPE_DSPMSG);

        Date date;

        try {
            date = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault())
                    .parse(SPE_TRNDATE + SPE_TRNTIME);
        } catch (Exception exception) {
            Log.e(TAG_LOGCAT, Log.getStackTraceString(exception));
            date = new Date();
        }

        EntradaComandoGetCard.Builder builder = new EntradaComandoGetCard.Builder(date);

        builder.informaTipoTransacao            ((byte) SPE_TRNTYPE);
        builder.informaIndiceAdquirente         (SPE_ACQREF);
        builder.informaValorTotal               (SPE_AMOUNT);
        builder.informaPermiteCtls              (!(SPE_GCXOPT.charAt(0) != '1'));
        builder.informaTimeoutOperacao          (SPE_TIMEOUT);

        if (!SPE_APPTYPE.equals("99")) {
            List<Integer> typeList = new ArrayList<>(SPE_APPTYPE.length() / 2);

            while (SPE_APPTYPE.length() > 1) {
                typeList.add(Integer.parseInt(SPE_APPTYPE.substring(0, 2)));

                SPE_APPTYPE = SPE_APPTYPE.substring(2);
            }

            builder.informaTipoAplicacao(typeList);
        }

        if (SPE_AIDLIST != null) {
            EntradaComandoGetCard.ListaRegistrosAID registerList = new EntradaComandoGetCard.ListaRegistrosAID();

            while (SPE_AIDLIST.length() > 3) {
                int AA = Integer.parseInt(SPE_AIDLIST.substring(0, 2));
                int RR = Integer.parseInt(SPE_AIDLIST.substring(2, 4));

                registerList.adicionaEntrada(AA, RR);

                SPE_AIDLIST = SPE_AIDLIST.substring(4);
            }

            builder.informaListaRegistrosAID(registerList);
        }

        if (SPE_CASHBACK != -1) {
            builder.informaValorSaque(SPE_CASHBACK);
        }

        if (SPE_TRNCURR != -1) {
            builder.informaCodigoMoeda(SPE_TRNCURR);
        }

        if (SPE_EMVDATA != null) {
            byte[] data = new byte[SPE_EMVDATA.length() / 2];
            int    i    = 0;

            while (SPE_EMVDATA.length() > 1) {
                data[i++] = Long.getLong(SPE_EMVDATA.substring(0, 2)).byteValue();

                SPE_EMVDATA = SPE_EMVDATA.substring(2);
            }

            builder.informaDadosEMV(data);
        }

        if (SPE_TAGLIST != null) {
            byte[] data = new byte[SPE_TAGLIST.length() / 2];
            int    i    = 0;

            while (SPE_TAGLIST.length() > 1) {
                data[i++] = Long.getLong(SPE_TAGLIST.substring(0, 2)).byteValue();

                SPE_TAGLIST = SPE_TAGLIST.substring(2);
            }

            builder.informaListaTagsEMV(data);
        }

        if (SPE_DSPMSG != null) {
            builder.informaMensagemCapturaCartao(SPE_DSPMSG);
        }

        getPinpad().getCard(builder.build(), response -> {
            ABECS.STAT status = ManufacturerUtility.toSTAT(response.obtemResultadoOperacao());

            output[0].putString(ABECS.RSP_ID,   ABECS.GCX);
            output[0].putInt   (ABECS.RSP_STAT, status.ordinal());

            try {
                if (status != ABECS.STAT.ST_OK) {
                    return;
                }

                output[0].putAll(parseRSP(response));
            } finally {
                semaphore[0].release();
            }
        });

        semaphore[0].acquireUninterruptibly();

        Log.d(TAG_LOGCAT, ABECS.GCX + "::timestamp [" + (SystemClock.elapsedRealtime() - timestamp) + "ms]");

        return output[0];
    }
}
