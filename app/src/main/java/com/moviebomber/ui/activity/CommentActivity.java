package com.moviebomber.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.moviebomber.R;
import com.moviebomber.ui.fragment.PttCommentFragment;
import com.moviebomber.ui.fragment.YahooCommentFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class CommentActivity extends ActionBarActivity implements MaterialTabListener {

	private static final int[] TABS = {R.string.tab_comment_ptt, R.string.tab_comment_yahoo};

	@InjectView(R.id.toolbar)
	Toolbar mToolbar;
	@InjectView(R.id.tab_comment_list)
	MaterialTabHost mTabComment;
	@InjectView(R.id.pager_comment)
	ViewPager mPagerComment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		ButterKnife.inject(this);
		this.setSupportActionBar(this.mToolbar);
		if (this.getSupportActionBar() != null) {
			this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		CommentPagerAdapter adapter = new CommentPagerAdapter(this.getSupportFragmentManager(), this);
		this.mPagerComment.setAdapter(adapter);
		this.mPagerComment.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				mTabComment.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		for (int i = 0; i < adapter.getCount(); i++) {
			this.mTabComment.addTab(this.mTabComment.newTab()
					.setTabListener(this)
					.setText(this.getResources().getString(TABS[i])));
		}
	}

	@Override
	public void onTabSelected(MaterialTab materialTab) {
		this.mPagerComment.setCurrentItem(materialTab.getPosition());
	}

	@Override
	public void onTabReselected(MaterialTab materialTab) {

	}

	@Override
	public void onTabUnselected(MaterialTab materialTab) {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_comment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	class CommentPagerAdapter extends FragmentStatePagerAdapter {

		private Context mContext;

		CommentPagerAdapter(FragmentManager fm, Context context) {
			super(fm);
			this.mContext = context;
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0)
				return PttCommentFragment.newInstance();
			else
				return YahooCommentFragment.newInstance();
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}