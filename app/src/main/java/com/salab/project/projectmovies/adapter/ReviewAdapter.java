package com.salab.project.projectmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salab.project.projectmovies.R;
import com.salab.project.projectmovies.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {


    private List<Review> reviewList = null;

    public ReviewAdapter() {}

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
        this.notifyDataSetChanged();
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder{
        TextView mReviewAuthorTextView;
        TextView mReviewContentTextView;

        public ReviewViewHolder(View view){
            super(view);
            mReviewAuthorTextView = view.findViewById(R.id.tv_review_author);
            mReviewContentTextView = view.findViewById(R.id.tv_review_content);
        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int reviewItemLayout = R.layout.item_movie_review;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(reviewItemLayout, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review selectedReview = reviewList.get(position);
        holder.mReviewAuthorTextView.setText(selectedReview.getAuthor());
        holder.mReviewContentTextView.setText(selectedReview.getContent());
    }

    @Override
    public int getItemCount() {
        if (reviewList==null){
            return 0;
        }else{
            return reviewList.size();
        }
    }
}
