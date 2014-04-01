package br.ufg.iptsp.app.variloid.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.ufg.iptsp.app.variloid.R;
import br.ufg.iptsp.app.variloid.negocio.Entrevista;

public class AdapterPendentes extends ArrayAdapter<Entrevista> {

	private List<Entrevista> objects;
	private int resource;
	private LayoutInflater layoutInflater;

	public AdapterPendentes(Context context, int resource, List<Entrevista> objects) {
		super(context, resource, objects);
		this.resource = resource;
		this.objects = objects;
		layoutInflater = LayoutInflater.from(context);
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
		
		holder.imageView.setVisibility(View.GONE);
		holder.titulo.setText("NID: "+objects.get(position).getId());
		
		if(objects.get(position).getId()!=0){
			holder.relativeLayout.setVisibility(View.VISIBLE);
		}else{
			holder.relativeLayout.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	public String getIditem(int posicao){
		return String.valueOf(objects.get(posicao).getId());
	}
	
	public Entrevista getEntrevista(int posicao){
		return objects.get(posicao);
	}
	
	static class viewHolder {
		RelativeLayout relativeLayout;
		TextView titulo;
		ImageView imageView;
	}
}
