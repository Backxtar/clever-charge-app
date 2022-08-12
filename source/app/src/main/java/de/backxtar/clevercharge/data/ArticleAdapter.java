package de.backxtar.clevercharge.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import de.backxtar.clevercharge.MainActivity;
import de.backxtar.clevercharge.R;
import de.backxtar.clevercharge.managers.UserManager;
import de.backxtar.clevercharge.services.DownloadService;
import de.backxtar.clevercharge.services.MessageService;

/**
 * ArticleAdapter
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    /* Global adapter variables */

    /**
     * Article list.
     */
    private ArrayList<Article> articles;

    /**
     * Activity of recyclerView.
     */
    private final Activity activity;
    //====================================

    /**
     * ArticleAdapter constructor.
     * @param articles as ArrayList
     * @param activity as object
     */
    public ArticleAdapter(ArrayList<Article> articles, Activity activity) {
        this.articles = articles;
        this.activity = activity;
    }

    /* ViewHolder class of the adapter */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView author, date, title, desc, content;
        private final ProgressBar bar;
        private ImageButton read;

        /**
         * ViewHolder constructor.
         * @param itemView as view
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.newsImg);
            author = itemView.findViewById(R.id.newsAuthor);
            date = itemView.findViewById(R.id.newsDate);
            title = itemView.findViewById(R.id.newsTitle);
            desc = itemView.findViewById(R.id.newsDesc);
            content = itemView.findViewById(R.id.contentNews);
            bar = itemView.findViewById(R.id.load_photo);
            read = itemView.findViewById(R.id.mark_as_read);
        }
    }

    /**
     * Return a viewType based on read / non read.
     * @param position as int
     * @return type as int
     */
    @Override
    public int getItemViewType(int position) {
        ArrayList<Integer> news_ids = UserManager.getApi_data().getNews_read_ids();
        return news_ids.contains(articles.get(position).getId()) ? 1 : 0;
    }

    /**
     * Creates the viewHolders layout.
     * @param parent as viewGroup
     * @param viewType as viewType
     * @return new viewHolder object
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view;
        if (viewType == 1) view = inflater.inflate(R.layout.dashboard_item_2, parent, false);
        else view = inflater.inflate(R.layout.dashboard_item_1, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Creates 1 item in itemList
     * @param holder as object
     * @param position as int
     */
    @SuppressLint({"CheckResult", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articles.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(activity)
                .load(article.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.bar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.bar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.image);

        holder.author.setText(article.getAuthor());
        holder.date.setText(article.getPublishedAt());
        holder.title.setText(article.getTitle());
        holder.desc.setText(article.getDescription());
        holder.content.setText(article.getContent());

        if (holder.read != null) {
            holder.read.setOnClickListener(v -> {
                callApi(article.getId());
                UserManager.getApi_data().getNews_read_ids().add(article.getId());
                MainActivity.updateBadges(R.id.nav_home, UserManager.getArticles().size() - UserManager.getApi_data().getNews_read_ids().size());
                notifyDataSetChanged();
            });
        }
    }

    /**
     * Get item count of list.
     * @return item count as int
     */
    @Override
    public int getItemCount() {
        return articles.size();
    }

    /**
     * Sets new list for the adapter.
     * @param articles as ArrayList
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setArticles(ArrayList<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    /**
     * Contact the api to mark news as read.
     * @param article_id as int
     */
    private void callApi(int article_id) {
        CompletableFuture.supplyAsync(() -> DownloadService.getResponse("set_read",
                new String[]{"user_id=" + UserManager.getApi_data().getUserID(), "news_id=" + article_id}))
                .whenComplete(((apiResponse, throwable) -> {
                    if (throwable != null || apiResponse.getResponseCode() != 1) {
                        MessageService service = new MessageService(activity, "Something went wrong!", Gravity.TOP, true);
                        activity.runOnUiThread(service::sendToast);
                    }
                }));
    }
}
