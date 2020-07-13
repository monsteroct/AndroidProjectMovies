package com.salab.project.projectmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salab.project.projectmovies.R;
import com.salab.project.projectmovies.model.Movie;
import com.salab.project.projectmovies.utility.MovieAPIUtility;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private final MovieOnClickListener listener;

    /**
     * Interface to handle viewholder onclick events.
     */
    public interface MovieOnClickListener {
        void onClickMovie(Movie selectedMovie);
    }

    public MovieAdapter(MovieOnClickListener listener){
        this.listener = listener;
    }

    /**
     * Setter for adapter database update.
     * @param movieList
     */
    public void setMovieAdapterList(List<Movie> movieList){
        this.movieList = movieList;
        this.notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mPosterImageView = null;

        public MovieViewHolder(View view){
            super(view);
            mPosterImageView = view.findViewById(R.id.iv_item_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Movie selectedMovie = movieList.get(position);
            listener.onClickMovie(selectedMovie);
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        Context context = parent.getContext();
        int itemLayout = R.layout.item_movie_poster;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(itemLayout, parent, false);

        return new MovieViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {

        String posterPath = this.movieList.get(position).getPosterPath();
        URL posterURL = MovieAPIUtility.buildPosterURL(posterPath, MovieAPIUtility.IMAGE_SIZE_185);

        Picasso.get()
                .load(posterURL.toString())
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_image_24)
                .into(holder.mPosterImageView);

    }

    @Override
    public int getItemCount() {
        if (this.movieList == null){
            return 0;
        }else{
            return this.movieList.size();
        }
    }
}
