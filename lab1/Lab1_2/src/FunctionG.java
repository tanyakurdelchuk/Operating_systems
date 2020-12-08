import java.nio.channels.Pipe;


public class FunctionG extends Thread {
    private int x;
    private int res;
    private Pipe.SinkChannel sinkChannel;

    FunctionG(int x, Pipe.SinkChannel sc) {
        this.x = x;
        sinkChannel = sc;
    }

    @Override
    public void run() {

        try {
            res = IntOps.funcG(x);
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
