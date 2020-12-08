import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.util.Scanner;

class Manager {

    private Pipe pipeF;
    private Pipe pipeG;
    private FunctionF f;
    private FunctionG g;
    BufferedReader br;

    boolean isDoneF, isDoneG;
    private int resF, resG;


    public void start() throws IOException, InterruptedException {
        while (true) {
            task();
            Thread.sleep(1000);
        }
    }

    private void task() throws IOException {
        System.out.println("\n\nEnter x (x from 0 to 5)");
        System.out.print("  > ");

        Scanner in = new Scanner(System.in);
        int x = in.nextInt();

        if (x < 0 || x > 5) {
            System.out.println("Result undefined for this input");
            return;
        } else {
            isDoneF = false;
            isDoneG = false;

            pipeF = Pipe.open();
            f = new FunctionF(x, pipeF.sink());
            pipeF.source().configureBlocking(false);
            f.start();
            pipeG = Pipe.open();
            g = new FunctionG(x, pipeG.sink());
            pipeG.source().configureBlocking(false);
            g.start();
            br = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                try {
                    if (checkResult()) break;
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (br.ready())
                    if (br.readLine().length() == 0) {
                        System.out.println("Cancellation");
                        cancellation();
                        break;
                    }
            }
            f.interrupt();
            g.interrupt();
        }
    }

    private int multiplication() {
        return resF * resG;
    }

    private boolean checkResult() {
        if (isDoneF() && isDoneG()) {
            System.out.println("Result: " + multiplication());
            return true;
        }
        if (isDoneF()) {
            if (resF == 0) {
                System.out.println("Result: " + resF);
                return true;
            }
        }
        if (isDoneG()) {
            if (resG == 0) {
                System.out.println("Result: " + resG);
                return true;
            }
        }
        return false;
    }

    private void cancellation() {
        if (!isDoneF() && !isDoneG()) {
            System.out.println("Unable to calculate the result. Both functions haven't finished yet.");
            return;
        }
        if (!isDoneF()) {
            System.out.println("Unable to calculate the result. Function F hasn't finished yet.");
            return;
        }
        if (!isDoneG()) {
            System.out.println("Unable to calculate the result. Function G hasn't finished yet.");
        }
    }

    private boolean isDoneF() {
        if (!isDoneF) getFunctionResult(pipeF.source());
        return isDoneF;
    }

    private boolean isDoneG() {
        if (!isDoneG) getFunctionResult(pipeG.source());
        return isDoneG;
    }

    private void getFunctionResult(Pipe.SourceChannel sourceChannel) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        int funcR;
        try {
            if (sourceChannel.read(buffer) != 0) {
                buffer.flip();
                funcR = buffer.getInt();
                if (sourceChannel.equals(pipeF.source())) {
                    isDoneF = true;
                    resF = funcR;
                } else {
                    isDoneG = true;
                    resG = funcR;
                }
            }
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}

