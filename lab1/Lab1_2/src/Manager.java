import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.util.Scanner;

class Manager {

    private Pipe pipeF;
    private Pipe pipeG;
    private FunctionF f;
    private FunctionG g;

    private boolean readyF, readyG;
    private int resF, resG;
    private int sleepTime, sleepCount;
    private Prompt mode;


    Manager() throws IOException {
        sleepCount = 40;
        sleepTime = 100;
    }

    public void start() throws IOException, InterruptedException {
        while (true) {
            userChoice();
            Thread.sleep(1000);
        }
    }

    private void userChoice() throws IOException {
        System.out.println("\n\nEnter x (x from 0 to 5)");
        System.out.print("  > ");

        Scanner in = new Scanner(System.in);
        int x = in.nextInt();

        if (x < 0 || x > 5) {
            System.out.println("Result undefined for this input");
        } else {
            readyG = readyF = false;
            mode = Prompt.CONTINUE;

            pipeF = Pipe.open();
            f = new FunctionF(x, pipeF.sink());
            pipeF.source().configureBlocking(false);
            f.start();

            pipeG = Pipe.open();
            g = new FunctionG(x, pipeG.sink());
            pipeG.source().configureBlocking(false);
            g.start();

            while (true) {
                try {
                    for (int i = 0; i < sleepCount; i++) {
                        if (checkResult()) {
                            f.interrupt();
                            g.interrupt();
                            return;
                        }
                        Thread.sleep(sleepTime);
                    }
                } catch (InterruptedException e) {}

                if (mode != Prompt.CONTINUE_WITHOUT)
                    if (askPrompt() == Prompt.CANCEL) {
                        if (checkResult()) break;
                        else cancellation();
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
            return;
        }
        return;

    }

    private boolean isDoneF() {
        if (!readyF) getFunctionResult(pipeF.source());
        return readyF;
    }

    private boolean isDoneG() {
        if (!readyG) getFunctionResult(pipeG.source());
        return readyG;
    }

    private void getFunctionResult(Pipe.SourceChannel sourceChannel) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        int funcR;
        try {
            if (sourceChannel.read(buffer) != 0) {
                buffer.flip();
                funcR = buffer.getInt();
                if (sourceChannel.equals(pipeF.source())) {
                    readyF = true;
                    resF = funcR;
                } else {
                    readyG = true;
                    resG = funcR;
                }
            }
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Prompt askPrompt() {
        int answer;
        System.out.println("  Continue - 1");
        System.out.println("  Continue without prompt - 2");
        System.out.println("  Cancel - 3");
        System.out.print("  > ");
        Scanner in = new Scanner(System.in);
        answer = in.nextInt();
        if (answer == 1) {
            return Prompt.CONTINUE;
        }
        if (answer == 2) {
            mode = Prompt.CONTINUE_WITHOUT;
            return Prompt.CONTINUE_WITHOUT;
        } else {
            return Prompt.CANCEL;
        }
    }
}