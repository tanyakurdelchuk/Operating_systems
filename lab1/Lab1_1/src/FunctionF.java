
import spos.lab1.demo.IntOps;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class FunctionF extends Thread {
    private int x;
    private int res;
    private Pipe.SinkChannel sinkChannel;

    FunctionF(int x, Pipe.SinkChannel sc) {
        this.x = x;
        sinkChannel = sc;
    }

    @Override
    public void run() {
        try {
            res = IntOps.funcF(x);
        } catch (InterruptedException e) {}

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(res);
        buffer.flip();
        while(buffer.hasRemaining()) {
            try {
                sinkChannel.write(buffer);
            } catch (IOException e) {}
        }
    }
}


