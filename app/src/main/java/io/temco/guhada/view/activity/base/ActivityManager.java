package io.temco.guhada.view.activity.base;

import android.app.Activity;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import io.temco.guhada.common.Type;

public class ActivityManager {

    // -------- LOCAL VALUE --------
    private static ActivityManager mActivityManager;
    private List<TypeActivity> mActivityList;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    private ActivityManager() {
        mActivityList = new ArrayList<>();
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public static ActivityManager getInstance() {
        if (mActivityManager == null) {
            mActivityManager = new ActivityManager();
        }
        return mActivityManager;
    }

    public void addActivity(Type.View type, Activity activity) {
        if (mActivityList != null) {
            mActivityList.add(new TypeActivity(type, activity));
        }
    }

    public void addActivity(Type.View type, Activity activity, int position) {
        if (mActivityList != null) {
            mActivityList.add(position, new TypeActivity(type, activity));
        }
    }

    public boolean removeActivity(Activity activity) {
        if (mActivityList != null) {
            for (TypeActivity t : mActivityList) {
                if (t.activity == activity) {
                    if (!t.activity.isFinishing()) {
                        t.activity.finish();
                    }
                    return mActivityList.remove(t);
                }
            }
        }
        return false;
    }

    public boolean removeActivity(Type.View type) {
        if (mActivityList != null) {
            int count = 0;
            for (TypeActivity t : mActivityList) {
                if (t.type == type) {
                    if (!t.activity.isFinishing()) {
                        t.activity.finish();
                    }
                    mActivityList.remove(t);
                    count++;
                }
            }
            return count > 0;
        }
        return false;
    }

    public TypeActivity removeActivity(int position) {
        if (mActivityList == null) {
            return null;
        } else {
            return mActivityList.remove(position);
        }
    }

    public void finishAllActivity(boolean isShowFirst) {
        if (mActivityList != null) {
            if (isShowFirst) {
                if (mActivityList.size() > 1) {
                    for (int i = 1; i < mActivityList.size(); i++) {
                        mActivityList.get(i).activity.finish();
                        removeActivity(i);
                    }
                }
            } else {
                for (TypeActivity t : mActivityList) {
                    ActivityCompat.finishAffinity(t.activity);
                    // t.activity.finish();
                }
            }
        }
    }

    public void recreate() {
        if (mActivityList != null) {
            for (TypeActivity t : mActivityList) {
                t.activity.recreate();
            }
        }
    }

    ////////////////////////////////////////////////
    // INNER CLASS
    ////////////////////////////////////////////////

    private class TypeActivity {

        public Type.View type;
        public Activity activity;

        TypeActivity(Type.View type, Activity activity) {
            this.type = type;
            this.activity = activity;
        }
    }

    ////////////////////////////////////////////////
}
