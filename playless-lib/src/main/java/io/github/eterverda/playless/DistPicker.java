package io.github.eterverda.playless;

import android.content.Context;

import io.github.eterverda.playless.common.Dist;

public class DistPicker {
    private final String applicationId;
    private final DistMatcher matcher;

    public DistPicker(Context context) {
        applicationId = context.getPackageName();
        matcher = new DistMatcher(context);
    }

    public Dist bestReplacement(Dist... candidateDists) {
        int bestVersionCode = -1;
        Dist bestDist = null;
        for (Dist candidateDist : candidateDists) {
            if (!candidateDist.applicationId.equals(applicationId)) {
                continue;
            }
            if (candidateDist.version.versionCode <= bestVersionCode) {
                continue;
            }
            if (matcher.matches(candidateDist.filter)) {
                continue;
            }
            bestDist = candidateDist;
        }
        return bestDist;
    }

    private boolean canInstall(Dist candidateDist) {
        throw new UnsupportedOperationException();
    }
}
