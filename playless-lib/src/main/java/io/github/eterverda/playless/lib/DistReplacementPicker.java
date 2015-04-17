package io.github.eterverda.playless.lib;

import android.content.Context;
import android.content.pm.PackageManager;

import org.jetbrains.annotations.Nullable;

import io.github.eterverda.playless.common.Dist;

public class DistReplacementPicker {
    private final String applicationId;
    private final DistFilterChecker matcher;
    private final int versionCode;

    public DistReplacementPicker(Context context) {
        matcher = new DistFilterChecker(context);

        try {
            applicationId = context.getPackageName();
            versionCode = context.getPackageManager().getPackageInfo(applicationId, 0x0).versionCode;
        } catch (PackageManager.NameNotFoundException ignore) {
            throw new AssertionError();
        }
    }

    @Nullable
    public Dist bestMyReplacement(Dist... candidates) {
        int bestVersionCode = versionCode;
        Dist bestDist = null;
        for (Dist candidate : candidates) {
            if (!candidate.applicationId.equals(applicationId)) {
                continue;
            }
            if (candidate.version.versionCode <= bestVersionCode) {
                continue;
            }
            if (matcher.isCompatible(candidate.filter)) {
                continue;
            }
            bestDist = candidate;
        }
        return bestDist;
    }
}
