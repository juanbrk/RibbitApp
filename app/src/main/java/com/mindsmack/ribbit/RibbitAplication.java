package com.mindsmack.ribbit;

import android.app.Application;
import android.util.Log;

import com.mindsmack.ribbit.UI.MainActivity;
import com.mindsmack.ribbit.utils.ParseConstants;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.Parse;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * Created by Juan on 16/03/2015.
 */
public class RibbitAplication extends Application {
    @Override
    public void onCreate() {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "bDJlwhowBvEpAuJR8X2n4MdTuhNph91oZtf7WXBC", "3CQuB3NA33xPTEOtwaK0BcwcCj9F5fTKickQxTQx");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static void updateParseInstallation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.CLAVE_ID_USUARIO, ParseUser.getCurrentUser());
        installation.saveInBackground();
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}
