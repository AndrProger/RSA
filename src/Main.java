import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        RSA rsa=new RSA(2048);
        rsa.info();

        String text="АНD";
        System.out.println("text:"+text);
        String enc=rsa.encrypt(text);
        System.out.println("encrypt text:"+enc);
        String dec=rsa.decrypt(enc);
        System.out.println("decrypt text:"+dec);
    }
}