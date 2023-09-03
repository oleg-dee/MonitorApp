package com.example.monitorapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;
import java.util.Stack;

public class Emailer {
    private static final String RECIPIENT_EMAIL = "anatolyden@gmail.com"; //"oleg.dee@gmail.com";
    private Context context;
    private File file;

    public Emailer(Context context) {
        this.context = context;
    }

    public void setFile(File file) {
        this.file = file;
    }

    private Intent createEmailOnlyChooserIntent(Intent source,
                                                CharSequence chooserTitle) {
        Stack<Intent> intents = new Stack<Intent>();
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                "info@domain.com", null));
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        List<ResolveInfo> activities = context.getPackageManager()
                .queryIntentActivities(i, 0);

        for(ResolveInfo ri : activities) {
            Intent target = new Intent(source);
            target.setPackage(ri.activityInfo.packageName);
            intents.add(target);
        }

        if(!intents.isEmpty()) {
            Intent chooserIntent = Intent.createChooser(intents.remove(0),
                    chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    intents.toArray(new Parcelable[intents.size()]));

            return chooserIntent;
        } else {
            return Intent.createChooser(source, chooserTitle);
        }
    }

    public void send()
    {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("*/*");
        i.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getPackageName()+".fileprovider", file));
        i.putExtra(Intent.EXTRA_EMAIL, new String[] {
                RECIPIENT_EMAIL
        });
        i.putExtra(Intent.EXTRA_SUBJECT, "Test email");
        i.putExtra(Intent.EXTRA_TEXT, "Please find the file attached");

        context.startActivity(createEmailOnlyChooserIntent(i, "Send via email"));
    }

}
