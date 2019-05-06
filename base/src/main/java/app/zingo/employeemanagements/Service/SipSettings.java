package app.zingo.employeemanagements.Service;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import app.zingo.employeemanagements.base.R;

public class SipSettings extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Note that none of the preferences are actually defined here.
        // They're all in the XML file res/xml/preferences.xml.
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
