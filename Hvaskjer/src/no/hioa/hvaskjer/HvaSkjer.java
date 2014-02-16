package no.hioa.hvaskjer;


import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class HvaSkjer extends SherlockFragmentActivity {
    
    Button btnViewProducts;
    Button btnNewProduct;
    Button btnScan;
    
    
    ViewPager mViewPager;
    TabsAdapter mTabsAdapter;
    TextView tabCenter;
    TextView tabText;
    String test;
    static Activity mActivity;
    public String mytext;
    
  
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	
        if (item.getItemId() == android.R.id.home) {
			Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
		} else if (item.getItemId() == R.id.menu_refresh) {
			Toast.makeText(this, "Fake refreshing...", Toast.LENGTH_SHORT).show();
			getWindow().getDecorView().postDelayed(
			        new Runnable() {
			            @Override
			            public void run() {
			                AsyncTask.execute(this);
			            }
			        }, 1000);
		} else if (item.getItemId() == R.id.menu_search) {
			Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
		} else if (item.getItemId() == R.id.menu_share) {
			Toast.makeText(this, "Tapped share", Toast.LENGTH_SHORT).show();
		} else if (item.getItemId() == R.id.menu_map) {
			Intent i = new Intent(getApplicationContext(),BasicMapActivity.class);
			startActivity(i);
		}
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.showHome);        
        setContentView(mViewPager);
        
        ActionBar bar = getSupportActionBar();
        //ActionBar ab = getActionBar();
        //AllProductsActivity act = new AllProductsActivity(this.getParent());
        bar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
        bar.setDisplayShowTitleEnabled(false);
        
        mTabsAdapter = new TabsAdapter(this, mViewPager);

        
        mTabsAdapter.addTab(bar.newTab().setText("Avd.for IKT"),
        		AllProductsActivity.class, null);
        
        mTabsAdapter.addTab(bar.newTab().setText("Hva skjer?"),
        		AllCalendarActivity.class, null);

        this.mytext = getIntent().getStringExtra("barcode");
        
    }
    
    public String getTxt() {
    	if (this.mytext == null)
    		return "tullete";
    	else
    		return this.mytext;
    }
    
    public static class TabsAdapter extends FragmentPagerAdapter implements
	    	ActionBar.TabListener, ViewPager.OnPageChangeListener {
		
    	private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
		
		static final class TabInfo {
		    private final Class<?> clss;
		    private final Bundle args;
		
		    TabInfo(Class<?> _class, Bundle _args) {
		        clss = _class;
		        args = _args;
		    }
		}
		
		public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
		    super(activity.getSupportFragmentManager());
		    
		    mContext = activity;
		    mActionBar = activity.getSupportActionBar();
		    mViewPager = pager;
		    mViewPager.setAdapter(this);
		    mViewPager.setOnPageChangeListener(this);
		}
		
		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
		    TabInfo info = new TabInfo(clss, args);
		    tab.setTabListener(this);
		    mActionBar.addTab(tab);
		    tab.setTag(info);		    		    
		    mTabs.add(info);
		    
		    notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
		    return mTabs.size();
		}
		
		@Override
		public Fragment getItem(int position) {
		    TabInfo info = mTabs.get(position);
		    return Fragment.instantiate(mContext, info.clss.getName(),
		            info.args);
		}
		
		@Override
		public void onPageScrolled(int position, float positionOffset,
		        int positionOffsetPixels) {
		}
		
		@Override
		public void onPageSelected(int position) {
		    mActionBar.setSelectedNavigationItem(position);
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
		}
		
		@Override
		public void onTabSelected(Tab tab, android.support.v4.app.FragmentTransaction ft) {
		    Object tag = tab.getTag();
		    for (int i = 0; i < mTabs.size(); i++) {
		        if (mTabs.get(i) == tag) {
		            mViewPager.setCurrentItem(i);
		        }
		    }
		    
		    System.out.println("get tabposition" + tab.getPosition());
		    if (tab.getPosition() == 2) {
		    	HvaSkjer test = new HvaSkjer();
		    	System.out.println("hva kommer nå" + test.getTxt());
		    }
		}
		
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}
		
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabUnselected(Tab tab,
				android.support.v4.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTabReselected(Tab tab,
				android.support.v4.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
    }

}
