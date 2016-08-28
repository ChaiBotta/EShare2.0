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
        setContentView(R.layout.dialog_text);

        TextView contentText = (TextView) findViewById(R.id.dialog_text_text);

        contentText.setText(content.getBody());
    }
}
