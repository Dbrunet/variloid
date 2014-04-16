package br.ufg.iptsp.app.variloid.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.ufg.iptsp.app.variloid.R;
import br.ufg.iptsp.app.variloid.provider.Data;

public class MyAdapterForm3 extends BaseAdapter {

	private List<Integer> objects;
	private int resource;
	private Context context;
	private int paginaFragment;
	private LayoutInflater layoutInflater;

	public MyAdapterForm3(Context context, int resource, int paginaFragment, List<Integer> objects) {
		super();
		this.context = context;
		this.resource = resource;
		this.paginaFragment = paginaFragment;
		this.objects = objects;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final viewHolder holder;

		if (convertView == null) {
			convertView = layoutInflater.inflate(resource, parent, false);
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
		case 14:
			holder.relativeLayout.setBackgroundResource(Color.TRANSPARENT);
			holder.titulo.setGravity(Gravity.CENTER_HORIZONTAL);
			holder.titulo.setTextColor(Color.parseColor("#CC0000"));
			holder.titulo.setText(context.getString(getItem(position)));
			break;
		case 28:
			holder.relativeLayout.setBackgroundResource(Color.TRANSPARENT);
			holder.titulo.setGravity(Gravity.CENTER_HORIZONTAL);
			holder.titulo.setTextColor(Color.parseColor("#CC0000"));
			holder.titulo.setText(context.getString(getItem(position)));
			break;
		case 34:
			holder.relativeLayout.setBackgroundResource(Color.TRANSPARENT);
			holder.titulo.setGravity(Gravity.CENTER_HORIZONTAL);
			holder.titulo.setTextColor(Color.parseColor("#CC0000"));
			holder.titulo.setText(context.getString(getItem(position)));
			break;
		case 50:
			holder.relativeLayout.setBackgroundResource(Color.TRANSPARENT);
			holder.titulo.setGravity(Gravity.CENTER_HORIZONTAL);
			holder.titulo.setTextColor(Color.parseColor("#CC0000"));
			holder.titulo.setText(context.getString(getItem(position)));
			break;
		default:
			holder.relativeLayout
					.setBackgroundResource(R.drawable.seletor_ui_card);
			holder.titulo.setGravity(Gravity.LEFT);
			holder.titulo.setTextColor(Color.BLACK);
			holder.titulo.setText(context.getString(getItem(position)));
			break;
		}

		if(!Data.listaFormularioTres.isEmpty()){
			if(!Data.listaFormularioTres.get(paginaFragment).getListSucesso().isEmpty()){
				if (Data.listaFormularioTres.get(paginaFragment).getListSucesso().get(position)) {
					holder.imageView.setImageResource(R.drawable.ic_certo);
				} else {
					holder.imageView.setImageDrawable(null);
				}
			}else{
				holder.imageView.setImageDrawable(null);
			}

			if (!Data.listaFormularioTres.get(paginaFragment).getListInativar().isEmpty()) {
				if (Data.listaFormularioTres.get(paginaFragment).getListInativar().get(position)) {
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
			}else{
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

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return objects.size();
	}

	@Override
	public Integer getItem(int arg0) {
		// TODO Auto-generated method stub
		return objects.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}
