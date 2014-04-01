package br.ufg.iptsp.app.variloid.servico;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import br.ufg.iptsp.app.variloid.R;

public class ServicoConexao {
	
	private static Toast toast;
	private static final String log = "ServicoConexao";

	private static boolean Mobile = false;
	private static boolean Wifi = false;
	private static boolean Conectado_rede = false;
	private static boolean Mobile_2G = false;
	private static boolean Mobile_3G = false;
	private static boolean Mobile_4G = false;

	public static boolean isMobile() {
		return Mobile;
	}

	private static void setMobile(boolean mobile) {
		Mobile = mobile;
	}

	public static boolean isWifi() {
		return Wifi;
	}

	private static void setWifi(boolean wifi) {
		Wifi = wifi;
	}

	public static boolean isConectadoRede() {
		return Conectado_rede;
	}

	private static void setConectadoRede(boolean conectado_rede) {
		Conectado_rede = conectado_rede;

	}

	public static boolean isMobile_2G() {
		return Mobile_2G;
	}

	private static void setMobile_2G(boolean mobile_2g) {
		Mobile_2G = mobile_2g;
	}

	public static boolean isMobile_3G() {
		return Mobile_3G;
	}

	private static void setMobile_3G(boolean mobile_3g) {
		Mobile_3G = mobile_3g;
	}

	public static boolean isMobile_4G() {
		return Mobile_4G;
	}

	private static void setMobile_4G(boolean mobile_4g) {
		Mobile_4G = mobile_4g;

	}

	private static void showToast(Context contexto, int duration, int textoId) {

		if (toast != null) {
			toast.cancel();

		}
		toast = Toast.makeText(contexto, textoId, duration);
		toast.show();

	}

	public static void verificaTipoConexao(Context ctx) {

		try {

			ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo netinfo = cm.getActiveNetworkInfo();

			// SEM CONEX√ÉO
			if (netinfo == null || !netinfo.isConnected()) {
				Log.d(log, "sem conex„o ativa");

				setConectadoRede(false);
				showToast(ctx, Toast.LENGTH_SHORT, R.string.advertencia_conexao_off);

				 Toast.makeText(ctx, R.string.advertencia_conexao_off,
				 Toast.LENGTH_SHORT).show();

			}
			// COM CONEX√ÉO
			else {
				switch (netinfo.getType()) {

				// Todos os tipos de conex√µes mobile
				case ConnectivityManager.TYPE_MOBILE: {

					setConectadoRede(true);
					setMobile(true);

					// VERIFICA o TIPO DE CONEX√ÇO MOBILE
					TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

					switch (tm.getNetworkType()) {

					case TelephonyManager.NETWORK_TYPE_EDGE:

						setMobile_2G(true);
						Log.d(log, "Mobile REDE: 2G NETWORK_TYPE_EDGE");
						 Toast.makeText(ctx,
						 R.string.advertencia_conexao_lenta,
						 Toast.LENGTH_SHORT).show();

						showToast(ctx, Toast.LENGTH_SHORT, R.string.advertencia_conexao_lenta);
						break;
					case TelephonyManager.NETWORK_TYPE_GPRS:

						setMobile_2G(true);
						Log.d(log, "Mobile REDE: 2G NETWORK_TYPE_GPRS");
						showToast(ctx, Toast.LENGTH_SHORT, R.string.advertencia_conexao_lenta);
						 Toast.makeText(ctx,
						 R.string.advertencia_conexao_lenta,
						 Toast.LENGTH_SHORT).show();
						break;
					case TelephonyManager.NETWORK_TYPE_UMTS:

						setMobile_2G(true);
						Log.d(log, "Mobile REDE: 2G NETWORK_TYPE_UMTS");
						showToast(ctx, Toast.LENGTH_SHORT, R.string.advertencia_conexao_lenta);
						 Toast.makeText(ctx,
						 R.string.advertencia_conexao_lenta,
						 Toast.LENGTH_SHORT).show();
						break;
					case TelephonyManager.NETWORK_TYPE_HSUPA:

						setMobile_3G(true);
						Log.d(log, "Mobile REDE: 3G NETWORK_TYPE_HSUPA");
						break;
					case TelephonyManager.NETWORK_TYPE_HSDPA:

						setMobile_3G(true);
						Log.d(log, "Mobile REDE: 3G NETWORK_TYPE_HSDPA");
						break;
					case TelephonyManager.NETWORK_TYPE_HSPA:

						setMobile_3G(true);
						Log.d(log, "Mobile REDE: 3G NETWORK_TYPE_HSPA");
						break;
					case TelephonyManager.NETWORK_TYPE_HSPAP:

						setMobile_3G(true);
						Log.d(log, "Mobile REDE: 3G NETWORK_TYPE_HSPAP");
						break;
					case TelephonyManager.NETWORK_TYPE_LTE:

						setMobile_4G(true);
						Log.d(log, "Mobile REDE: 4G NETWORK_TYPE_LTE");
						break;
					default:

					}

					Log.d(log, "Status de conex„o MOBILE: " + cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected());

					break;

				}

				case ConnectivityManager.TYPE_WIFI: {

					@SuppressWarnings("unused")
					boolean WIFI = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();

					Log.d(log, "Status de conex„o Wifi: " + true);
					setConectadoRede(true);
					setWifi(true);

					break;

				}
				default:
					Log.e(log, "Outro tipo de conex„o: " + cm.getActiveNetworkInfo().getTypeName());
					break;

				}
			}

		} catch (Exception e) {

			Log.e(log, "Erro de conex„o: " + e.getMessage());
			setConectadoRede(false);

		}
	}

}