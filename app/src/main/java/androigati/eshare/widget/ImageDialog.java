package androigati.eshare.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androigati.eshare.model.Content;
import de.rwth.R;

/**
 * Created by Antonello Fodde on 28/08/16.
 * fodde.antonello@gmail.com
 */
public class ImageDialog extends Dialog {

    public ImageDialog(Context context, Content content) {
        super(context);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_image);

        TextView contentTitle = (TextView) findViewById(R.id.dialog_image_title);
        ImageView contentImage = (ImageView) findViewById(R.id.dialog_image_image);

        Glide.with(getContext())
                .load(content.getUrl())
                .into(contentImage);

        contentTitle.setText(content.getTitle());
    }
}
