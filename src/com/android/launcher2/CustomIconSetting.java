
package com.android.launcher2;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Properties;

public class CustomIconSetting {
    public static final String THEME_DIRECTORY = "/theme/launcher/";
    public static final String CONFIGURATION_FILE = "icon.conf";

    private final String mFilePath;
    private final HashMap<ComponentName, CustomIconItem> mIconMap = new HashMap<ComponentName, CustomIconItem>();

    public CustomIconSetting() {
        this(getIconDirectory() + CONFIGURATION_FILE);
    }

    public CustomIconSetting(String filepath) {
        mFilePath = filepath;
        loadConf();
    }

    public void reloadMap() {
        mIconMap.clear();
        loadConf();
    }

    private void loadConf() {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(mFilePath));
        } catch (IOException e) {
            return;
        }

        for (Entry<Object, Object> entry : p.entrySet()) {
            ComponentName key = CustomIconItem.getComponent(entry.getKey().toString());
            CustomIconItem value = parseValue(entry.getValue().toString());
            set(key, value);
        }
    }

    private static CustomIconItem parseValue(String value) {
        boolean title = true;
        int pos = value.lastIndexOf(',');
        if (pos >= 0) {
            String flag = value.substring(pos + 1).toLowerCase();
            if ("false".equals(flag) || "off".equals(flag) || "no".equals(flag)) {
                title = false;
            }
            value = value.substring(0, pos);
        }
        return new CustomIconItem(value, title);
    }

    public boolean saveConf(Context context, String filepath) {
        PackageManager pm = (context != null) ? context.getPackageManager() : null;
        try {
            FileWriter out = new FileWriter(filepath);

            for (Entry<ComponentName, CustomIconItem> entry : mIconMap.entrySet()) {
                ComponentName component = entry.getKey();
                CustomIconItem value = entry.getValue();

                if (pm != null) {
                    // get & write application name
                    try {
                        ActivityInfo info = pm.getActivityInfo(component, 0);
                        out.write(String.format("# %s\n", info.loadLabel(pm)));
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                }

                // write setting
                String line = component.flattenToShortString() + " = ";
                if (value.filename.isEmpty() && value.title) {
                    line = "#" + line;
                } else {
                    line += value.filename;
                    if (!value.title) {
                        line += ",false";
                    }
                }
                out.write(line + "\n\n");
            }

            out.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void set(ComponentName component, CustomIconItem item) {
        mIconMap.put(component, item);
    }

    public void set(ActivityInfo info, CustomIconItem item) {
        set(CustomIconItem.getComponent(info), item);
    }

    public CustomIconItem get(ComponentName component) {
        return mIconMap.get(component);
    }

    public CustomIconItem get(ActivityInfo info) {
        return get(CustomIconItem.getComponent(info));
    }

    public String getCustomFile(ComponentName component) {
        CustomIconItem item = get(component);
        return (item != null) ? item.filename : null;
    }

    public String getCustomFile(ActivityInfo info) {
        return getCustomFile(CustomIconItem.getComponent(info));
    }

    public static String getIconDirectory() {
        return Environment.getDataDirectory() + THEME_DIRECTORY;
    }

    private static CustomIconSetting sInstance;

    static public CustomIconSetting getInstance() {
        if (sInstance == null) {
            sInstance = new CustomIconSetting();
        }
        return sInstance;
    }
}
