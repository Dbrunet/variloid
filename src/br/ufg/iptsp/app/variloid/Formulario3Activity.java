package br.ufg.iptsp.app.variloid;

import java.util.ArrayList;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;
import br.ufg.iptsp.app.variloid.negocio.FormularioTres;
import br.ufg.iptsp.app.variloid.provider.Data;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class Formulario3Activity extends SherlockFragmentActivity implements LocationListener{

	private ViewPager vp;
	private PagerAdapter pagerAdapter;
	private LocationManager locationManager;
	private double latitude;
	private double longitude;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_form3);
		
		getSupportActionBar().setTitle(
				String.valueOf(1).concat("° ")
				.concat(getString(R.string.formulario_tres)));
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.background_vermelho));
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		boolean enabledGPS = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!enabledGPS) {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
			Toast.makeText(this, "GPS sinal não encontrado!",
					Toast.LENGTH_LONG).show();
			Toast.makeText(this,
					"Ative o GPS do dispositivo!", Toast.LENGTH_LONG).show();
		}else {

			Criteria criteria = new Criteria();
			String provider = locationManager.getBestProvider(criteria,
					true);
			Location location = locationManager
					.getLastKnownLocation(provider);
			if (location != null) {
				onLocationChanged(location);
			}
			locationManager.requestLocationUpdates(provider, 20000, 0,
					Formulario3Activity.this);
		}


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
				Data.mapService.add(Data.FORM3_KEY.concat("[").concat(String.valueOf(0)).concat("].").concat("latitude"), String.valueOf(latitude));
				Data.mapService.add(Data.FORM3_KEY.concat("[").concat(String.valueOf(0)).concat("].").concat("longitude"), String.valueOf(longitude));
			}
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		public void addItem(Fragment fragment) {
			//adicionar um novo fragmento no pager
			mFragments.add(fragment);
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
		
		Data.mapService.add(Data.FORM3_KEY.concat("[").concat(String.valueOf(position)).concat("].").concat("latitude"), String.valueOf(latitude));
		Data.mapService.add(Data.FORM3_KEY.concat("[").concat(String.valueOf(position)).concat("].").concat("longitude"), String.valueOf(longitude));
		
		FormularioTres formularioTres = new FormularioTres();
		MultiValueMap<String, Object> mapFormularioTres = new LinkedMultiValueMap<String, Object>();
		Data.listaFormularioTres.add(formularioTres);
		Data.listaMapFormularioTres.add(mapFormularioTres);
		
		for (String strings : VariloidForm3.idCampos) {
			mapFormularioTres.add(Data.FORM3_KEY.concat("[").concat(String.valueOf(position)).concat("].").concat(strings), "");
			
			if (Data.listaFormularioTres.get(position).getListInativar().size() != VariloidForm3.idCampos.length)
				Data.listaFormularioTres.get(position).getListInativar().add(false);
			if (Data.listaFormularioTres.get(position).getListSucesso().size() != VariloidForm3.idCampos.length)
				Data.listaFormularioTres.get(position).getListSucesso().add(false);
		}
		
		
		
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

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
