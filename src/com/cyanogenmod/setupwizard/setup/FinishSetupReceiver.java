package com.cyanogenmod.setupwizard.setup;

import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.cyanogenmod.setupwizard.SetupWizardApp;
import com.cyanogenmod.setupwizard.util.SetupWizardUtils;

public class FinishSetupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("FinishSetupReceiver", "onReceive");
        if (SetupWizardUtils.isDeviceLocked() || SetupWizardUtils.frpEnabled(context)) {
            return;
        }
        Log.i("FinishSetupReceiver", "save settings");
        Settings.System.putInt(context.getContentResolver(), Settings.System.USER_ROTATION, 1);
        Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.SHOW_IME_WITH_HARD_KEYBOARD, 1);
        Settings.Global.putInt(context.getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
        Settings.Secure.putInt(context.getContentResolver(),
                Settings.Secure.USER_SETUP_COMPLETE, 1);
        ((StatusBarManager)context.getSystemService(Context.STATUS_BAR_SERVICE)).disable(
                StatusBarManager.DISABLE_NONE);
        Settings.Global.putInt(context.getContentResolver(),
                SetupWizardApp.KEY_DETECT_CAPTIVE_PORTAL, 1);
        SetupWizardUtils.disableGMSSetupWizard(context);
        SetupWizardUtils.disableSetupWizard(context);
    }
}
