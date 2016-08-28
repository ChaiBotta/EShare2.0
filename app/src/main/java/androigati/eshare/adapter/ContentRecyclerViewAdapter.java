package androigati.eshare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androigati.eshare.model.Content;
import de.rwth.R;

/**
 * Created by Antonello Fodde on 28/08/16.
 * fodde.antonello@gmail.com
 */
public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<ContentRecyclerViewAdapter.ContentListViewHolder> {

    private static final int IMAGE = 0, TEXT = 1;

    private List<Content> contentList;

    public ContentRecyclerViewAdapter(List<Content> contentList) {
        this.contentList = contentList;
    }

    @Override
    public int getItemViewType(int position) {
        Content content = contentList.get(position);
        if (content.getType().equals("image"))
            return IMAGE;
        else
            return TEXT;
    }

    @Override
    public ContentListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = null;
        switch (viewType) {
            case IMAGE:
                row = inflater.inflate(R.layout.content_image, parent, false);
                break;
            case TEXT:
                row = inflater.inflate(R.layout.content_text, parent, false);
                break;
        }
        return new ContentListViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ContentListViewHolder holder, int position) {
        Content content = contentList.get(position);
        switch (getItemViewType(position)) {

            case IMAGE:
                holder.contentImageTitle.setText(content.getTitle());
                Glide.with(holder.contentImage.getContext())
                        .load(content.getUrl())
                        .animate(android.R.anim.fade_in)
                        .into(holder.contentImage);
                break;

            case TEXT:
                holder.contentTextTitle.setText(content.getTitle());
                holder.contentText.setText(content.getBody());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public static class ContentListViewHolder extends RecyclerView.ViewHolder {

        TextView contentImageTitle, contentTextTitle, contentText;
        ImageView contentImage;

        public ContentListViewHolder(View contentRow) {
            super(contentRow);

            contentImageTitle = (TextView) contentRow.findViewById(R.id.content_image_title);
            contentImage = (ImageView) contentRow.findViewById(R.id.content_image_image);

            contentTextTitle = (TextView) contentRow.findViewById(R.id.content_text_title);
            contentText = (TextView) contentRow.findViewById(R.id.content_text_text);
        }
    }
}
