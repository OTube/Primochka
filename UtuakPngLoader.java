import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class UtuakPngLoader {
    private Socket sock;
    private InputStream br;
    private OutputStream bw;
    private FileOutputStream fos;
    private Thread rt;
    private byte[] cut(byte[] u, int from, int to){
        byte[] v = new byte[to - from];
        if (to - from >= 0) System.arraycopy(u, from, v, 0, to - from);
        //if (to - from >= 0) System.arraycopy(u, from, v, from, to - from);
        return v;
    }
    private int from_bytes(byte[] num){
        return ByteBuffer.wrap(num).getInt();
    }
    private int read(byte[] to, int len){
        try {
            return br.read(to, 0, len);
        } catch (IOException ignored){}
        return len;
    }
    private void write(byte[] to, int len){
        try {
            bw.write(to, 0, len);
        } catch (IOException ignored){}
    }
    public UtuakPngLoader() {
        System.out.println("start");
        rt = new Thread(() -> {
            try {
                sock = new Socket("127.0.0.1", 47892);
                br = sock.getInputStream();
                bw = sock.getOutputStream();
                System.out.println("connected");
            }catch (IOException ignored){System.out.println("error");}
            byte[] buf = new byte[4096];
            int images_avi;
            byte[] buff0 = new byte[4];
            System.out.println(Arrays.toString(buff0));
            read(buff0, 4);
            images_avi = from_bytes(buff0);
            System.out.println("avi: " + images_avi);
            if(Main.images_len < images_avi){
                Main.is_down = new byte[images_avi];
                Main.images_len = images_avi;
            }
            int req = 0;
            for(int h = 0; h < images_avi; ++h){
                if(Main.is_down[h] == 0){
                    ++req;
                }
            }
            write(Main.is_down, images_avi);
            for(int o = 0; o < req; ++o){
                byte[] buff1 = new byte[8];
                read(buff1, 8);
                byte[] name = cut(buff1, 0, 4);
                byte[] size = cut(buff1, 4, 8);
                int name_i = from_bytes(name);
                int size_i = from_bytes(size);
                int rows = size_i / 4096;
                if(rows % 4096 > 0) ++rows;
                try {fos = new FileOutputStream(name_i + ".png");} catch (FileNotFoundException ignored){}
                for(int y = 0; y < rows; ++y){
                    int reads = (y + 1 != rows) ? 4096 : size_i - y * 4096;
                    if(reads < 4096){
                        byte[] b1 = new byte[reads];
                        read(b1, reads);
                        try {fos.write(b1);} catch (IOException ignored) {}
                    }else{
                        read(buf, 4096);
                        try {fos.write(buf);} catch (IOException ignored) {}
                    }
                }
                try {fos.close();} catch (IOException ignored){}
                Main.is_down[name_i] = 1;
            }
            try {
                bw.close();
                br.close();
                sock.close();
            } catch (IOException ignored){}
            System.out.println("exit");
        });
        rt.start();
    }
    public void destroy(){
        rt.interrupt();
    }
}
