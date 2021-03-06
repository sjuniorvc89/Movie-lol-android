package com.moviebomber.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.moviebomber.R;
import com.moviebomber.model.api.MovieListItem;
import com.moviebomber.ui.activity.MainActivity;
import com.moviebomber.ui.activity.MovieDetailActivity;
import com.moviebomber.utils.GAApplication;
import com.orhanobut.logger.Logger;
import com.rey.material.widget.Button;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by engine on 15/3/29.
 */
public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private List<MovieListItem> mMovieList = new ArrayList<>();
	private Context mContext;
	private boolean mShowBomberCount;
	private int mAdPosition = -1;
//			(int)(Math.random() * 2) + 1;

	public MovieListAdapter(Context context, List<MovieListItem> mMovieList, boolean showBomberCount) {
		this.mContext = context;
		this.mMovieList = mMovieList;
		this.mShowBomberCount = showBomberCount;
	}

	public List<MovieListItem> getMovieList() {
		return mMovieList;
	}

	public void setMovieList(List<MovieListItem> mMovieList) {
		this.mMovieList = mMovieList;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == mAdPosition)
			return R.layout.card_ad;
		else
			return R.layout.card_movie_list;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == R.layout.card_movie_list)
			return new MovieListItemHolder(LayoutInflater.from(parent.getContext())
				.inflate(viewType, parent, false), this.mShowBomberCount);
		else
			return new AdHolder(LayoutInflater.from(parent.getContext())
			.inflate(viewType, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
		if (viewHolder instanceof AdHolder) {
			AdHolder adHolder = (AdHolder)viewHolder;
			AdRequest adRequest = new AdRequest.Builder().build();
			adHolder.mAdView.loadAd(adRequest);
		} else {
			MovieListItemHolder holder = (MovieListItemHolder)viewHolder;
			// correct for ad position
			int p = position;
//			if (position > mAdPosition)
//				p--;
//			if (p < 0)
//				p = 0;
			final MovieListItem movieItem = this.mMovieList.get(p);
			holder.mTextMovieName.setText(movieItem.getTitleChinese());
			String thumbnailPath = movieItem.getThumbnailPath();

//		thumbnailUrl = thumbnailUrl.replace("mpost4", "mpost");
			holder.mImageMovieCover.setImageResource(R.drawable.img_empty);
			Picasso.with(holder.mImagePoster.getContext())
					.load(MainActivity.getResizePhoto(this.mContext, thumbnailPath))
					.into(holder.mImagePoster);
//		LabelView label = new LabelView(this.mContext);
//		label.setText("POP");
//		label.setTextColor(this.mContext.getResources().getColor(android.R.color.white));
//		label.setBackgroundColor(this.mContext.getResources().getColor(R.color.accent_light));
//		label.setTargetView(holder.mImageMovieCover, 10, LabelView.Gravity.LEFT_TOP);
			if (movieItem.getPhotoLists().size() > 0) {
				String coverUrl = movieItem.getPhotoLists().get(
						(int) (Math.random() * movieItem.getPhotoLists().size())).getPath();
//			coverUrl = coverUrl.replace("mpho3", "mpho");
				Logger.wtf(MainActivity.getResizePhoto(this.mContext, coverUrl));
				Picasso.with(holder.mImageMovieCover.getContext())
						.load(MainActivity.getResizePhoto(this.mContext, coverUrl)).into(holder.mImageMovieCover);
			}
			holder.mTextDuration.setText(this.mContext.getResources().getString(R.string.text_duration)
					+ ": " + movieItem.getDuration());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
			holder.mTextReleaseDate.setText(this.mContext.getResources().getString(R.string.text_release_date)
					+ ": " + dateFormat.format(movieItem.getReleaseDate()));

			holder.mRipple.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showMovieDetail(movieItem.getId(), movieItem.getTitleChinese());
				}
			});
			holder.mButtonOrder.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showMovieDetail(movieItem.getId(), movieItem.getTitleChinese());
				}
			});

			holder.mTextGoodBomber.setText(String.valueOf(movieItem.getGoodBomber()));
			holder.mTextNormalBomber.setText(String.valueOf(movieItem.getNormalBomber()));
			holder.mTextBadBomber.setText(String.valueOf(movieItem.getBadBomber()));
			holder.mProgressGoodBomber.setProgress((int) (movieItem.getGoodRate() * 100));
			holder.mProgressNormalBomber.setProgress((int) (movieItem.getNormalRate() * 100));
			holder.mProgressBadBomber.setProgress((int) (movieItem.getBadRate() * 100));
		}
	}

	private void showMovieDetail(int id, String name) {
		GAApplication.getTracker(mContext).send(new HitBuilders.AppViewBuilder()
				.build());
		Intent movieDetail = new Intent(mContext, MovieDetailActivity.class);
		movieDetail.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, id);
		movieDetail.putExtra(MovieDetailActivity.EXTRA_MOVIE_NAME, name);
		Activity activity = (Activity) mContext;
		activity.startActivity(movieDetail);
		activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override
	public int getItemCount() {
		return this.mMovieList.size();
//				+ 1; // +1 for admob
	}

	class AdHolder extends RecyclerView.ViewHolder {
		@InjectView(R.id.adView)
		AdView mAdView;

		public AdHolder(View itemView) {
			super(itemView);
			ButterKnife.inject(this, itemView);
		}
	}

	class MovieListItemHolder extends RecyclerView.ViewHolder {
		@InjectView(R.id.card_movie_list_item)
		CardView mCardMovie;
		@InjectView(R.id.ripple)
		MaterialRippleLayout mRipple;
		@InjectView(R.id.layout_bomber_count)
		View mLayoutBomberCount;

		@InjectView(R.id.image_movie_cover)
		ImageView mImageMovieCover;

		@InjectView(R.id.image_movie_poster)
		ImageView mImagePoster;

		@InjectView(R.id.text_movie_name)
		TextView mTextMovieName;

		@InjectView(R.id.text_release_date)
		TextView mTextReleaseDate;
		@InjectView(R.id.text_duration)
		TextView mTextDuration;
		@InjectView(R.id.text_good_bomber)
		TextView mTextGoodBomber;
		@InjectView(R.id.progress_good_bomber)
		ArcProgress mProgressGoodBomber;
		@InjectView(R.id.text_normal_bomber)
		TextView mTextNormalBomber;
		@InjectView(R.id.progress_normal_bomber)
		ArcProgress mProgressNormalBomber;
		@InjectView(R.id.text_bad_bomber)
		TextView mTextBadBomber;
		@InjectView(R.id.progress_bad_bomber)
		ArcProgress mProgressBadBomber;
		@InjectView(R.id.button_order)
		Button mButtonOrder;

		MovieListItemHolder(View itemView, boolean showBomberCount) {
			super(itemView);
			ButterKnife.inject(this, itemView);
			if (showBomberCount)
				this.mLayoutBomberCount.setVisibility(View.VISIBLE);
			else
				this.mLayoutBomberCount.setVisibility(View.GONE);
		}
	}
}
