package io.github.eterverda.playless.android;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

import io.github.eterverda.playless.common.Dist;
import io.github.eterverda.playless.android.content.pm.PackageInfos;

@SuppressWarnings("SpellCheckingInspection")
public final class InstalledDistFactory {
    public static final String META_INSTALLER = "installer";
    @NotNull
    private final PackageManager pacman;

    private InstalledDistFactory(@NotNull PackageManager pacman) {
        this.pacman = pacman;
    }

    @NotNull
    private static final AtomicReference<InstalledDistFactory> INSTANCE = new AtomicReference<>();

    @NotNull
    public static InstalledDistFactory getInstance(@NotNull Context context) {
        final @NotNull PackageManager pacman = context.getPackageManager();

        final @Nullable InstalledDistFactory cachedInstance = INSTANCE.getAndSet(null);
        final @NotNull InstalledDistFactory instance;
        if (cachedInstance != null && cachedInstance.pacman == pacman) {
            instance = cachedInstance;
        } else {
            instance = new InstalledDistFactory(pacman);
        }
        INSTANCE.set(instance);

        return instance;
    }

    public static final int NO_META = 0x01;
    public static final int NO_SIGNATURES = 0x10;
    public static final int NO_FINGERPRINT = 0x20;

    @NotNull
    public Dist load(@NotNull String packageName,
                             @MagicConstant(flags = {NO_META, NO_SIGNATURES, NO_FINGERPRINT}) int flags)

            throws PackageManager.NameNotFoundException {

        final boolean meta = (flags & NO_META) == 0;
        final boolean signatures = (flags & NO_SIGNATURES) == 0;
        final boolean fingerprint = (flags & NO_FINGERPRINT) == 0;

        final int pacmanFlags = meta ? PackageManager.GET_SIGNATURES : 0x0;
        final @NotNull PackageInfo info = pacman.getPackageInfo(packageName, pacmanFlags);

        final Dist.Editor dist = new Dist.Editor();
        dist.applicationId(info.packageName);
        dist.versionCode(info.versionCode);
        dist.debug(loadDebug(info));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            dist.timestamp(info.lastUpdateTime);
        }
        if (fingerprint) {
            dist.fingerprint(PackageInfos.loadFingerprint(info));
        }
        if (signatures) {
            dist.signatures(PackageInfos.loadSignatures(info));
        }
        if (meta) {
            dist.meta(Dist.META_VERSION_NAME, info.versionName);
            dist.meta(Dist.META_LABEL, loadLabel(info));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                final String installer = pacman.getInstallerPackageName(packageName);
                if (installer != null) {
                    dist.meta(META_INSTALLER, installer);
                }
            }
        }

        return dist.build();
    }

    @NotNull
    public Dist load(@NotNull String packageName) throws
            PackageManager.NameNotFoundException {
        return load(packageName, 0x0);
    }

    private String loadLabel(PackageInfo info) {
        return info.applicationInfo.loadLabel(pacman).toString();
    }

    private static boolean loadDebug(PackageInfo info) {
        return (info.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}
