package com.cyanogenmod.setupwizard.setup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.cyanogenmod.setupwizard.R;
import com.cyanogenmod.setupwizard.ui.SetupPageFragment;

import java.io.File;
import java.io.IOException;

/**
 * Created by ryan on 3/17/16.
 */
public class InstallationPage extends SetupPage {
    private static final String TAG = "InstallationPage";

    private InstallationFragment mInstallationFragment;

    private boolean mIsInstallFinished = false;
    private boolean mIsInstallStarted = false;

    public InstallationPage(Context context, SetupDataCallbacks callbacks) {
        super(context, callbacks);
    }

    @Override
    public Fragment getFragment(FragmentManager fragmentManager, int action) {
        mInstallationFragment = (InstallationFragment)fragmentManager.findFragmentByTag(getKey());
        if (mInstallationFragment == null) {
            Bundle args = new Bundle();
            args.putString(Page.KEY_PAGE_ARGUMENT, getKey());
            args.putInt(Page.KEY_PAGE_ACTION, action);
            mInstallationFragment = new InstallationFragment();
            mInstallationFragment.setArguments(args);
        }
        return mInstallationFragment;
    }

    @Override
    public String getKey() {
        return TAG;
    }

    @Override
    public int getTitleResId() {
        return R.string.setup_start;
    }

    @Override
    public boolean doNextAction() {
        if (!isInstallFinished()) {
            Toast.makeText(mContext.getApplicationContext(),
                    "Please wait for installation to complete", Toast.LENGTH_LONG).show();
            return true;
        } else {
            return super.doNextAction();
        }
    }

    @Override
    public void doLoadAction(FragmentManager fragmentManager, int action) {
        super.doLoadAction(fragmentManager, action);

        if (!mIsInstallStarted) {
            mIsInstallStarted = true;
            Thread installThread = new Thread() {
                @Override
                public void run() {
                    try {
                        Process sysinit = Runtime.getRuntime().exec("start sysinit");
                        sysinit.waitFor();
                        File finished = new File("/data/.firstboot");
                        while (!finished.exists()) {
                            Thread.sleep(1000);
                        }
                        onInstallSuccess();
                    } catch (IOException e) {
                        onInstallError(e);
                    } catch (InterruptedException e) {
                        onInstallError(e);
                    } finally {
                        mIsInstallFinished = true;
                    }
                }
            };
            installThread.start();
        }
    }

    private void showInstallMessage(final String toast, final String log, final int level) {
        mIsInstallFinished = true;
        getCallbacks().onNextPage();
        Looper.prepare();
        Toast.makeText(mContext.getApplicationContext(),
                toast, Toast.LENGTH_LONG).show();
        Log.println(level, TAG, log);
        Looper.loop();
    }

    private void onInstallSuccess() {
        showInstallMessage("Installation complete!", "first boot installation complete", Log.INFO);
    }

    private void onInstallError(Exception e) {
        showInstallMessage("Failed to complete installation", "failed to run sysinit: " + e, Log.ERROR);
    }

    public boolean isInstallFinished() {
        return mIsInstallFinished;
    }

    public static class InstallationFragment extends SetupPageFragment {
        @Override
        protected void initializePage() {

        }

        @Override
        protected int getLayoutResource() {
            return R.layout.setup_installation_page;
        }
    }
}
