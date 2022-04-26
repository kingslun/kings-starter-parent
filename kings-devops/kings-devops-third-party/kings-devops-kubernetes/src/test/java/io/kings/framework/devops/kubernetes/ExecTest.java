package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;

public class ExecTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        KubernetesClient client = new DefaultKubernetesClient();
        client.pods().inNamespace("default").withName("demo-5859c5997f-2cd94")
            .readingInput(new ReadInput("pwd")).writingOutput(new WriteOutput())
            .writingError(new WriteError()).withTTY().exec();
        new CountDownLatch(1).await();
    }

    @Test(expected = IllegalStateException.class)
    public void run() throws IOException {
        OutputStream out = new WriteError();
        out.write('k');
        out.flush();
        out.close();
        throw new IllegalStateException();
    }

    static class ReadInput extends InputStream {

        ReadInput(String cmd) {
            this.cmd = cmd.getBytes(StandardCharsets.UTF_8);
        }

        private final byte[] cmd;
        private int offset;

        @Override
        public int read() {
            if (offset >= cmd.length) {
                this.reset();
                return -1;
            }
            return cmd[offset++];
        }

        @Override
        public synchronized void reset() {
            offset = 0;
        }
    }

    static class WriteOutput extends OutputStream {

        OutputStream out;

        WriteOutput() throws IOException {
            File file = new File("/Users/mac/Documents/out.txt");
            if (file.exists() || file.createNewFile()) {
                out = new FileOutputStream(file, true);
            }
        }

        @Override
        public void write(int b) throws IOException {
            assert out != null;
            out.write(b);
            if (b == '\n') {
                out.flush();
            }
        }
    }

    static class WriteError extends OutputStream {

        OutputStream err;

        WriteError() throws IOException {
            File file = new File("/Users/mac/Documents/err.txt");
            if (file.exists() || file.createNewFile()) {
                err = new FileOutputStream(file, true);
            }
        }

        @Override
        public void write(int b) throws IOException {
            assert err != null;
            err.write(b);
        }
    }
}
