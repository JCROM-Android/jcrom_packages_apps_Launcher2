
package com.android.launcher2;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;

public class CustomIconItem {
    public final String filename;
    public final boolean title;

    public CustomIconItem(String filename, boolean title) {
        this.filename = (filename != null) ? filename : "";
        this.title = title;
    }

    public String getFilePath() {
        return CustomIconSetting.getIconDirectory() + filename;
    }

    public static ComponentName getComponent(String key) {
        int pos = key.indexOf('/');
        if (pos < 0) {
            // only package name
            return getComponent(key, null);
        } else {
            // package/.activity
            String pkg = key.substring(0, pos).trim();
            String cls = key.substring(pos + 1).trim();
            return getComponent(pkg, cls);
        }
    }

    public static ComponentName getComponent(String packageName, String activityName) {
        if (packageName == null) {
            if (activityName == null) {
                // Invalid format
                return null;
            }
            int pos = activityName.lastIndexOf('.');
            if (pos < 0) {
                // Invalid format
                return null;
            }
            packageName = activityName.substring(0, pos);
        }
        packageName = packageName.trim();

        if (activityName == null) {
            activityName = "";
        } else {
            activityName = activityName.trim();
        }
        if (activityName.startsWith(".")) {
            activityName = packageName + activityName;
        }
        return new ComponentName(packageName, activityName);
    }

    public static ComponentName getComponent(ActivityInfo info) {
        return getComponent(info.packageName, info.name);
    }
}
