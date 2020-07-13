package com.salab.project.projectmovies.adapter;

import android.app.VoiceInteractor;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salab.project.projectmovies.R;
import com.salab.project.projectmovies.model.Trailer;
import com.salab.project.projectmovies.utility.MovieAPIUtility;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private List<Trailer> trailerList;
    private  TrailerOnClickListener trailerOnClickListener;

    public interface TrailerOnClickListener{
        void onTrailerClick(String videoPath);
    }

    public List<Trailer> getTrailerList() {
        return trailerList;
    }

    public void setTrailerList(List<Trailer> trailerList) {
        this.trailerList = trailerList;
        this.notifyDataSetChanged();
    }

    public TrailerAdapter(TrailerOnClickListener listener){
        trailerOnClickListener = listener;
    }


    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mTrailerImageView;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            mTrailerImageView = itemView.findViewById(R.id.iv_trailer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Trailer selectedTrailer = trailerList.get(position);
            String videoPath = selectedTrailer.getKey();
            trailerOnClickListener.onTrailerClick(videoPath);
            Log.d(this.getClass().getSimpleName(), "Trailer is clicked");
        }
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_movie_trailer, parent, false);

        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {

        Trailer trailer = trailerList.get(position);
        if (trailer.getSite().equals(MovieAPIUtility.VIDEO_SITE_YT)){
            //get thumbnail only support youtube videos
            ImageView trailerImageView = holder.mTrailerImageView;
            String videoKey = trailer.getKey();
            URL thumbnailURL = MovieAPIUtility.buildVideoThumbnailUri(videoKey);
            //load image
            Picasso.get()
                    .load(thumbnailURL.toString())
                    .placeholder(R.drawable.ic_baseline_ondemand_video_24)
                    .error(R.drawable.ic_baseline_ondemand_video_24)
                    .into(trailerImageView);
        }
    }

    @Override
    public int getItemCount() {
        return trailerList == null ? 0 : trailerList.size();
    }

}
