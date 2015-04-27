package com.mindsmack.ribbit.adapters;

/**
 * Created by Juan on 20/03/2015.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Context;

import com.mindsmack.ribbit.UI.FriendsFragment;
import com.mindsmack.ribbit.UI.InboxFragment;
import com.mindsmack.ribbit.R;

import java.util.Locale;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    protected Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) { // Crea los elementos que iran en cada pagina o seccion
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        switch(position){
            case 0:
                return new InboxFragment();
            case 1: return new FriendsFragment();
        }
        return null;
    }

    @Override
    public int getCount() { // Define el numero de paginas o secciones
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) { // Define el titulo que ira en cada tab p cada seccion o pagina
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
        }
        return null;
    }
}
