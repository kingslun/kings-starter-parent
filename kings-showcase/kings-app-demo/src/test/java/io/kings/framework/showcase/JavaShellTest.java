package io.kings.framework.showcase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JavaShellTest {

    private static final ExecutorService executor = new ThreadPoolExecutor(1, 1, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), (r) -> new Thread(r, "KubectlExecutor"));
    private final static String sh = "/usr/src/myapp/replay/start.sh";
    private final static String[] cmd = new String[]{"sh", "-c", sh};

    private static String read(InputStream stream) throws IOException {
        try (InputStream in = stream; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    public static void main(String[] args) {
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(cmd);
            final InputStream in = proc.getInputStream();
            final InputStream err = proc.getErrorStream();
            executor.submit(() -> {
                try {
                    System.out.println(read(in));
                    System.out.println(read(err));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (proc != null) {
                proc.destroy();
            }
        }
    }
}