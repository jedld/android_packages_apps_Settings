/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.dashboard.conditional;

import android.graphics.drawable.Icon;
import android.os.PersistableBundle;
import android.util.Log;

public abstract class Condition {

    private static final String KEY_SILENCE = "silence";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_LAST_STATE = "last_state";

    protected final ConditionManager mManager;

    private boolean mIsSilenced;
    private boolean mIsActive;
    private long mLastStateChange;

    public Condition(ConditionManager manager) {
        mManager = manager;
    }

    void restoreState(PersistableBundle bundle) {
        mIsSilenced = bundle.getBoolean(KEY_SILENCE);
        mIsActive = bundle.getBoolean(KEY_ACTIVE);
        mLastStateChange = bundle.getLong(KEY_LAST_STATE);
    }

    void saveState(PersistableBundle bundle) {
        bundle.putBoolean(KEY_SILENCE, mIsSilenced);
        bundle.putBoolean(KEY_ACTIVE, mIsActive);
        bundle.putLong(KEY_LAST_STATE, mLastStateChange);
    }

    protected void notifyChanged() {
        mManager.notifyChanged(this);
    }

    public boolean isSilenced() {
        return mIsSilenced;
    }

    public boolean isActive() {
        return mIsActive;
    }

    protected void setActive(boolean active) {
        if (mIsActive == active) {
            return;
        }
        mIsActive = active;
        mLastStateChange = System.currentTimeMillis();
        if (mIsSilenced && !active) {
            mIsSilenced = false;
            onSilenceChanged(mIsSilenced);
        }
        notifyChanged();
    }

    public void silence() {
        if (!mIsSilenced) {
            mIsSilenced = true;
            onSilenceChanged(mIsSilenced);
            notifyChanged();
        }
    }

    protected void onSilenceChanged(boolean state) {
        // Optional enable/disable receivers based on silence state.
    }

    public boolean shouldShow() {
        return isActive() && !isSilenced();
    }

    long getLastChange() {
        return mLastStateChange;
    }

    // State.
    public abstract void refreshState();

    // UI.
    public abstract Icon getIcon();
    public abstract CharSequence getTitle();
    public abstract CharSequence getSummary();
    public abstract CharSequence[] getActions();

    public abstract void onPrimaryClick();
    public abstract void onActionClick(int index);
}