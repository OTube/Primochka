package utuakgames.com.primochka;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class LoadConf {
    public static byte[] is_down;
    public static int images_len = 0;
    public static void saveConfig(){
        try {
            FileOutputStream fos = new FileOutputStream("images_conf.ucf");
            byte[] header = new byte[4];
            /*
            header[0] = (byte) (images_len >>> 24);
            header[1] = (byte) (images_len >>> 16);
            header[2] = (byte) (images_len >>> 8);
            header[3] = (byte) images_len;
            */
            header[0] = (byte) ((images_len & 0xFF000000) >> 24);
            header[1] = (byte) ((images_len & 0x00FF0000) >> 16);
            header[2] = (byte) ((images_len & 0x0000FF00) >> 8);
            header[3] = (byte) (images_len & 0x000000FF);
            fos.write(header);
            fos.write(is_down);
            fos.close();
        } catch (IOException ignored){}
    }
    public static void loadConfig(){
        try {
            FileInputStream fis = new FileInputStream("images_conf.ucf");
            byte[] header = new byte[4];
            fis.read(header);
            images_len = (header[0]<<24)&0xff000000|(header[1]<<16)&0x00ff0000|(header[2]<< 8)&0x0000ff00|(header[3])&0x000000ff;
            if(images_len > 0) {
                is_down = new byte[images_len];
                fis.read(is_down);
            }
            fis.close();
        } catch (IOException ignored){}
    }
}
