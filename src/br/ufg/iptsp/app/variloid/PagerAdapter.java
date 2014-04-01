package br.ufg.iptsp.app.variloid;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter{

	private List<Fragment> mFragments;

	public PagerAdapter(FragmentManager fm) {
		super(fm);
		mFragments = new ArrayList<Fragment>();
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment;
		fragment = mFragments.get(position);
		Bundle bundle = new Bundle();
		bundle.putInt("indexFragment", position);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	public void addFragment(Fragment fragment){
		mFragments.add(fragment);
	}
}
