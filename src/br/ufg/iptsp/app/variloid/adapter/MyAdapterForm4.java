package br.ufg.iptsp.app.variloid.adapter;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.ufg.iptsp.app.variloid.R;
import br.ufg.iptsp.app.variloid.Variloid;
import br.ufg.iptsp.app.variloid.VariloidForm4;

public class MyAdapterForm4 extends ArrayAdapter<Integer> {

	private SharedPreferences pref;
	private List<Integer> objects;
	private int resource;
	private Context context;
	private LayoutInflater layoutInflater;

	public MyAdapterForm4(Context context, int resource, List<Integer> objects) {
		super(context, resource, objects);
		this.context = context;
		this.resource = resource;
		this.objects = objects;
		layoutInflater = LayoutInflater.from(context);
		pref = context.getSharedPreferences(Variloid.PREFERENCIAS, 0);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final viewHolder holder;

		if (convertView == null) {
			convertView = layoutInflater.inflate(resource, null);
			holder = new viewHolder();

			holder.titulo = (TextView) convertView.findViewById(R.id.nome);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.nome_img_check);
			holder.relativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.relativelayout_nome);

			convertView.setTag(holder);
		} else {
			holder = (viewHolder) convertView.getTag();
		}

		switch (position) {
		case 32:
			holder.relativeLayout.setBackgroundResource(Color.TRANSPARENT);
			holder.titulo.setGravity(Gravity.CENTER_HORIZONTAL);
			holder.titulo.setTextColor(Color.parseColor("#CC0000"));
			holder.titulo.setText(context.getString(objects.get(position)));
			break;
		case 42:
			holder.relativeLayout.setBackgroundResource(Color.TRANSPARENT);
			holder.titulo.setGravity(Gravity.CENTER_HORIZONTAL);
			holder.titulo.setTextColor(Color.parseColor("#CC0000"));
			holder.titulo.setText(context.getString(objects.get(position)));
			break;
		case 47:
			holder.relativeLayout.setBackgroundResource(Color.TRANSPARENT);
			holder.titulo.setGravity(Gravity.CENTER_HORIZONTAL);
			holder.titulo.setTextColor(Color.parseColor("#CC0000"));
			holder.titulo.setText(context.getString(objects.get(position)));
			break;
		case 52:
			holder.relativeLayout.setBackgroundResource(Color.TRANSPARENT);
			holder.titulo.setGravity(Gravity.CENTER_HORIZONTAL);
			holder.titulo.setTextColor(Color.parseColor("#CC0000"));
			holder.titulo.setText(context.getString(objects.get(position)));
			break;
		case 82:
			holder.relativeLayout.setBackgroundResource(Color.TRANSPARENT);
			holder.titulo.setGravity(Gravity.CENTER_HORIZONTAL);
			holder.titulo.setTextColor(Color.parseColor("#CC0000"));
			holder.titulo.setText(context.getString(objects.get(position)));
			break;
		case 87:
			holder.relativeLayout.setBackgroundResource(Color.TRANSPARENT);
			holder.titulo.setGravity(Gravity.CENTER_HORIZONTAL);
			holder.titulo.setTextColor(Color.parseColor("#CC0000"));
			holder.titulo.setText(context.getString(objects.get(position)));
			break;
		default:
			holder.relativeLayout
					.setBackgroundResource(R.drawable.seletor_ui_card);
			holder.titulo.setGravity(Gravity.LEFT);
			holder.titulo.setTextColor(Color.BLACK);
			holder.titulo.setText(context.getString(objects.get(position)));
			break;
		}



		if (pref.getBoolean(VariloidForm4.FORM4_IMAGEM + position, false)) {
			holder.imageView.setImageResource(R.drawable.ic_certo);
		} else {
			holder.imageView.setImageDrawable(null);
		}

		if (pref.getBoolean(VariloidForm4.FORM4_INATIVAR + position, false)) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				final AlphaAnimation animation = new AlphaAnimation(0.4F, 0.4F);
				animation.setDuration(0);
				animation.setFillAfter(true);
				holder.relativeLayout.startAnimation(animation);
				holder.relativeLayout.setClickable(true);
				holder.titulo.startAnimation(animation);
			} else {
				holder.relativeLayout.setAlpha(0.4F);
				holder.relativeLayout.setClickable(true);
				holder.titulo.setAlpha(0.4F);
			}

		} else {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				final AlphaAnimation animation = new AlphaAnimation(1F, 1F);
				animation.setDuration(0);
				animation.setFillAfter(true);
				holder.relativeLayout.startAnimation(animation);
				holder.relativeLayout.setClickable(false);
				holder.titulo.startAnimation(animation);
			} else {
				holder.relativeLayout.setAlpha(1F);
				holder.relativeLayout.setClickable(false);
				holder.titulo.setAlpha(1F);
			}
		}

		return convertView;
	}
	
	public void updateItens(int posicao, Integer item){
		objects.set(posicao, item);
	}

	static class viewHolder {
		TextView titulo;
		ImageView imageView;
		RelativeLayout relativeLayout;
	}
}
