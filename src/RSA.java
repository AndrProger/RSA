import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {
    private BigInteger p;
    private BigInteger q;
    private BigInteger e=new BigInteger("65537");
    private BigInteger f;
    private BigInteger n;
    private BigInteger d;
    private int countBits=512;
    //SecureRandom использует криптографически стойкие алгоритмы для генерации случайных чисел.
     private SecureRandom random = new SecureRandom();

    private Runnable genP =new Runnable() {
        @Override
        public void run() {
            p=BigInteger.probablePrime(countBits,random);
        }
    };
    private Runnable genQ =new Runnable() {
        @Override
        public void run() {

            q=BigInteger.probablePrime(countBits,random);
        }
    };
    private Runnable genN =new Runnable() {
        @Override
        public void run() {
            n=q.multiply(p);
        }
    };
    private Runnable genF =new Runnable() {
        @Override
        public void run() {
            BigInteger one = new BigInteger("1");
            // f=(q-1)*(p-1);
            f=q.subtract(one).multiply(p.subtract(one));
        }
    };
    private void parallelCalculation(Runnable firstRunnable, Runnable secondRunnable) throws InterruptedException {
        Thread threadOne=new Thread(firstRunnable);
        Thread threadSecond=new Thread(secondRunnable);

        threadOne.start();
        threadSecond.start();

        threadOne.join();
        threadSecond.join();

        return;

    }

    public void generateRSA(){
        try {
            while (p==null||p.equals(q)){
                parallelCalculation(genP,genQ);
            }

        } catch (InterruptedException ex) {
            throw new RuntimeException("Ошибка возникла при параллельной генерации q и p",ex);
        }
        try {
            parallelCalculation(genN,genF);
        } catch (InterruptedException ex) {
            throw new RuntimeException("Ошибка возникла при параллельной генерации N и F",ex);
        }
        checkE();
        genD();
    }

    private void checkE(){
        // сравнение e<f елси нет то генерируем новое e
        // изначально е дефолт, но вовзможно когда ключи будут маленикие тогда e по условию алгоритма становится не верным
        while (e.compareTo(f)>0){
            e=genE();
        }
    }
    private BigInteger genE(){
        int countBitsF=f.bitCount();
        int countBitsE= (int) ((Math.random()*((countBitsF-3)+1))+3);
        BigInteger maybeE=BigInteger.probablePrime(countBitsE,random);
        while(!maybeE.gcd(f).equals(BigInteger.ONE)){
            // возвращает след простое число
            maybeE=maybeE.nextProbablePrime();
        }
        return maybeE;
    }
    private void genD(){
//        System.out.println("e="+e);
//        System.out.println("n="+n);
        d = e.modInverse(f);
    }

    // функция для тестирования на бою удалить !!!!
    public  void info(){
        System.out.println("p="+p);
        System.out.println("q="+q);
        System.out.println("n="+n);
        System.out.println("e="+e);
        System.out.println("f="+f);
        System.out.println("d="+d);
    }

    public String encrypt(String text){

        String encryptText="";
        for(int i=0;i<text.length();i++){
            char sym = text.charAt(i);
            BigInteger encryptSym=new BigInteger((int)sym+"");
            encryptSym=encryptSym.modPow(e,n);
            encryptSym=encryptSym.mod(n);
            encryptText+=encryptSym.toString()+"/";
        }
        return  encryptText;
    }
    public String decrypt(String text){
        String decryptText="";
        String strNow="";
        for(int i=0;i<text.length();i++){
            char sym = text.charAt(i);
            if(sym=='/'){
                BigInteger decryptSym=new BigInteger(strNow);
                decryptSym=decryptSym.modPow(d,n);
                decryptText+=(char)decryptSym.intValue();
                strNow="";
            }
            else {
                strNow+=sym;
            }

            BigInteger encryptSym=new BigInteger((int)sym+"");
            encryptSym=encryptSym.pow(e.intValue());
            encryptSym=encryptSym.mod(n);
//            encryptText+=encryptSym.toString()+"/";
        }
        return decryptText;
    }

    public RSA(int countBits) {
        this.countBits = countBits;
        generateRSA();
    }

    public RSA() {
        generateRSA();
    }
}
