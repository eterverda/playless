package io.github.eterverda.playless.cli;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.eterverda.playless.common.Distribution;
import io.github.eterverda.playless.core.JsonDistributionDumper;
import io.github.eterverda.playless.core.Repository;
import io.github.eterverda.util.checksum.Checksum;

public class DumpCommand implements Command {
    private static final Pattern VERSION_CODE = Pattern.compile("package:.* versionCode='([0-9]*).*'");
    private static final Pattern APPLICATION_ID = Pattern.compile("package:.* name='([\\p{Alnum}\\.]*).*'");
    private static final Pattern MIN_SDK_VERSION = Pattern.compile("sdkVersion:'([0-9]*)'");
    private static final Pattern DEBUGGABLE = Pattern.compile("application-debuggable");
    private static final Pattern USES_FEATURE = Pattern.compile("\\p{Space}*uses-feature:.* name='([\\p{Alnum}\\.]*)'");

    @Override
    public void main(Repository repo, String[] rawArgs) {
        final ArrayList<String> args = new ArrayList<>(rawArgs.length);
        Collections.addAll(args, rawArgs);

        try {
            main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void main(ArrayList<String> args) throws IOException {
        final String aapt = extractAapt(args);

        final CommandLine baseLine = new CommandLine(aapt).addArgument("dump").addArgument("badging");

        final JsonDistributionDumper dumper = new JsonDistributionDumper(System.out);

        for (String arg : args) {
            final Distribution.Builder dist = new Distribution.Builder();
            final DistributionHandler handler = new DistributionHandler(dist);

            final DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(handler);

            final CommandLine line = new CommandLine(baseLine).addArgument(arg);
            executor.execute(line, handler);

            final Checksum checksum = Checksum.sha1(new FileInputStream(new File(arg)));
            dist.checksum(checksum);

            dumper.write(dist.build());
        }
    }

    private static class DistributionHandler implements ExecuteStreamHandler, ExecuteResultHandler {
        private BufferedReader in;
        private Distribution.Builder dist;

        public DistributionHandler(Distribution.Builder dist) {
            this.dist = dist;
        }

        @Override
        public void onProcessComplete(int exitValue) {
        }

        @Override
        public void onProcessFailed(ExecuteException e) {

        }

        @Override
        public void setProcessInputStream(OutputStream os) throws IOException {

        }

        @Override
        public void setProcessErrorStream(InputStream is) throws IOException {

        }

        @Override
        public void setProcessOutputStream(InputStream in) throws IOException {
            this.in = new BufferedReader(new InputStreamReader(in));
        }

        @Override
        public void start() throws IOException {
            String line;
            while ((line = in.readLine()) != null) {
                final Matcher applicationId = APPLICATION_ID.matcher(line);
                if (applicationId.matches()) {
                    dist.applicationId(applicationId.group(1));
                }
                final Matcher versionCode = VERSION_CODE.matcher(line);
                if (versionCode.matches()) {
                    dist.versionCode(Integer.parseInt(versionCode.group(1)));
                }
                final Matcher minSdkVersion = MIN_SDK_VERSION.matcher(line);
                if (minSdkVersion.matches()) {
                    dist.minSdkVersion(Integer.parseInt(minSdkVersion.group(1)));
                }
                final Matcher debuggable = DEBUGGABLE.matcher(line);
                if (debuggable.matches()) {
                    dist.debug(true);
                }
                final Matcher usesFeature = USES_FEATURE.matcher(line);
                if (usesFeature.matches()) {
                    dist.usesFeature(usesFeature.group(1));
                }
            }
        }

        @Override
        public void stop() throws IOException {
        }
    }

    private static String extractAapt(ArrayList<String> args) {
        for (int i = 0; i < args.size(); i++) {
            final String arg = args.get(i);
            if (arg.startsWith("--aapt=")) {
                args.remove(i);
                return arg.substring(7);
            }
            if (arg.equals("--aapt")) {
                if (args.size() == i - 1) {
                    throw new IllegalArgumentException("No value for option " + arg);
                }
                args.remove(i);
                return args.remove(i);
            }
        }
        return null;
    }
}
