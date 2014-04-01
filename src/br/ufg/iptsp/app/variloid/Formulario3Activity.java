package br.ufg.iptsp.app.variloid;

import java.util.ArrayList;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;
import br.ufg.iptsp.app.variloid.negocio.FormularioTres;
import br.ufg.iptsp.app.variloid.provider.Data;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class Formulario3Activity extends SherlockFragmentActivity {

	private ViewPager vp;
	private PagerAdapter pagerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_form3);

		getSupportActionBar().setTitle(
				String.valueOf(1).concat("° ")
						.concat(getString(R.string.formulario_tres)));
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.background_vermelho));

		vp = (ViewPager) findViewById(R.id.viewpager);
		pagerAdapter = new PagerAdapter(getSupportFragmentManager());
		vp.setAdapter(pagerAdapter);
		vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				getSupportActionBar().setTitle(
						String.valueOf(position + 1).concat("° ")
								.concat(getString(R.string.formulario_tres)));
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		vp.setCurrentItem(0);
		
		super.onCreate(savedInstanceState);
	}

	public class PagerAdapter extends FragmentPagerAdapter {

		private ArrayList<Fragment> mFragments;

		public PagerAdapter(FragmentManager fm) {
			super(fm);
			mFragments = new ArrayList<Fragment>();

			if (Data.listaFormularioTres.size() > 0) {
				for (int i = 0; i < Data.listaFormularioTres.size(); i++) {
					mFragments.add(new Formulario3Fragment());
				}
			} else {
				mFragments.add(new Formulario3Fragment());
			}
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		public void addItem(Fragment fragment) {
			//adicionar um novo fragmento no pager
			mFragments.add(fragment);
			//adicionar um novo formulario na lista
			Data.listaFormularioTres.add(new FormularioTres());
			//adiciona um novo mapa no fragment criados
		}
		
		public void removeItem(int position){
			mFragments.remove(position);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = mFragments.get(position);
			Bundle bundle = new Bundle();
			bundle.putInt("paginaForm3", position);
			fragment.setArguments(bundle);
			return fragment;
		}

	}

	public void addItemPager(int position) {
		pagerAdapter.addItem(new Formulario3Fragment());
		
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		for (String strings : VariloidForm3.idCampos) {
			map.add(Data.FORM3_KEY.concat("[").concat(String.valueOf(position)).concat("]").concat(strings), "");
		}
		Data.listaMapFormularioTres.add(map);
		
		pagerAdapter.notifyDataSetChanged();
		Toast.makeText(getApplicationContext(), "Um ".concat(String.valueOf(position + 1).concat("° ")
								.concat(getString(R.string.formulario_tres))).concat(" foi criado"),
				Toast.LENGTH_LONG).show();
		vp.setCurrentItem(position);
	}
	
	public void removeItemPager(int position){
		pagerAdapter.removeItem(position);
		pagerAdapter.notifyDataSetChanged();
		Toast.makeText(getApplicationContext(), "O ".concat(String.valueOf(position + 1).concat("° ")
				.concat(getString(R.string.formulario_tres))).concat(" foi removido"),
				Toast.LENGTH_LONG).show();
		vp.setCurrentItem(position-1);
	}

	public ViewPager getViewer() {
		return this.vp;
	}

}
