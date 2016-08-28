package androigati.eshare.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import androigati.eshare.model.Content;
import de.rwth.R;

/**
 * Created by Antonello Fodde on 28/08/16.
 * fodde.antonello@gmail.com
 */
public class TextDialog extends Dialog {

    public TextDialog(Context context, Content content) {
        super(context);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.content_text);

        TextView contentTitle = (TextView) findViewById(R.id.content_text_title);
        TextView contentText = (TextView) findViewById(R.id.content_text_text);

        contentTitle.setText(content.getTitle());
        contentText.setText(content.getBody());
    }
}
