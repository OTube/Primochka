package com.utuakgames.primochka;

import java.io.*;
import java.net.Socket;

public class UtuakPngLoader {
    private Socket sock;
    private InputStream br;
    private OutputStream bw;
    private FileOutputStream fos;
    private final Thread rt;
    private byte[] cut(byte[] u, int from, int to){
        byte[] v = new byte[to - from];
        if (to - from >= 0) System.arraycopy(u, from, v, 0, to - from);
        return v;
    }
    private int from_bytes(byte[] bytes){
        return (bytes[0]<<24)&0xff000000|(bytes[1]<<16)&0x00ff0000|(bytes[2]<< 8)&0x0000ff00|(bytes[3])&0x000000ff;
        //return ByteBuffer.wrap(bytes).getInt();
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
    public UtuakPngLoader(IOnPngLoad onPngLoad, String ip, int port) {
        rt = new Thread(() -> {
            try {
                sock = new Socket(ip, port);
                br = sock.getInputStream();
                bw = sock.getOutputStream();
            }catch (IOException ignored){}
            byte[] buf = new byte[4096];
            int images_avi;
            byte[] buff0 = new byte[4];
            read(buff0, 4);
            images_avi = from_bytes(buff0);
            //byte[] ar = new byte[LoadConf.images_len = Math.max(LoadConf.images_len, images_avi)];
            if(LoadConf.images_len > 0) {
                byte[] ar = new byte[images_avi];
                System.arraycopy(LoadConf.is_down, 0, ar, 0, LoadConf.images_len);
                LoadConf.is_down = ar;
            }else LoadConf.is_down = new byte[images_avi];
            LoadConf.images_len = images_avi;
            write(LoadConf.is_down, images_avi);
            int req;
            read(buff0, 4);
            req = from_bytes(buff0);
            for(int o = 0; o < req; ++o){
                byte[] buff1 = new byte[8];
                read(buff1, 8);
                byte[] name = cut(buff1, 0, 4);
                byte[] size = cut(buff1, 4, 8);
                int name_i = from_bytes(name);
                int size_i = from_bytes(size);
                int rows = size_i / 4096;
                if(size_i % 4096 > 0) ++rows;
                String filename = name_i + ".png";
                try {fos = new FileOutputStream(new File(LoadConf.dir, filename));} catch (FileNotFoundException ignored){}
                for(int y = 0; y < rows; ++y){
                    int reads = (y + 1 != rows) ? 4096 : size_i - y * 4096;
                    if(reads == 4096){
                        read(buf, 4096);
                        try {fos.write(buf);} catch (IOException ignored) {}
                    }else{
                        byte[] b1 = new byte[reads];
                        read(b1, reads);
                        try {fos.write(b1);} catch (IOException ignored) {}
                    }
                }
                try {fos.close();} catch (IOException ignored){}
                onPngLoad.onLoad(name_i, filename);
                byte[] key = new byte[1];
                read(key, 1);
                LoadConf.is_down[name_i] = key[0];
            }
            try {
                onPngLoad.onAllLoad();
                bw.close();
                br.close();
                sock.close();
            } catch (IOException ignored){}
        });
        rt.start();
    }
    public void destroy(){
        rt.interrupt();
    }
}
