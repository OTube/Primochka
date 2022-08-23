import java.util.Scanner;

public class Main {
    static byte[] is_down = new byte[17];
    static int images_len = 1;

    public static void main(String[] args) {
        UtuakPngLoader upl = new UtuakPngLoader();
        Scanner in = new Scanner(System.in);
        in.next();
        in.close();
        upl.destroy();
    }
}