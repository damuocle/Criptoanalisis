import java.io.*;
import java.util.*;
import java.math.*;

public class PollardRhoDL{
    public static void main (String [] args){
        Runtime runtime = Runtime.getRuntime();
        long memoriaInicio = runtime.totalMemory() - runtime.freeMemory(); //memoria gastada al inicio
        Scanner scanner = new Scanner(System.in);

        System.out.println("Introduce numero de bits para hacer las pruebas");
        int numeroBits =scanner.nextInt();
        scanner.close();
        String ruta="/Users/davidmunozclemente/Desktop/MUCC 1º CURSO/CSD/Criptoanálisis/DL/RetosDLextendido.csv";
        int contador=0;
        int fallos=0;
        BigDecimal tiempoTotal=BigDecimal.ZERO;

        try{
            Scanner scanner2 = new Scanner(new File(ruta));
            while(scanner2.hasNextLine()){
                String linea=scanner2.nextLine(); //linea del fichero
                String[] campos = linea.split(",");

                int tamanyoNumero = Integer.parseInt(campos[0].trim()); 
                BigInteger p = new BigInteger(campos[1].trim());
                BigInteger alpha = new BigInteger(campos[2].trim());
                BigInteger beta = new BigInteger(campos[3].trim());
                BigInteger orden = new BigInteger(campos[4].trim());
                long t0 = System.currentTimeMillis();
                BigDecimal t0BigDecimal =BigDecimal.valueOf(t0);
                long tf=0;
                if(tamanyoNumero==numeroBits){
                    BigInteger k = doPollardrho(alpha,beta,p,orden);
                    if(k!=null){
                        //System.out.println("k = "+ k + ">>>" +alpha+"^"+k+" = "+beta);
                    }else{
                        System.out.println("ERROR");
                        fallos++;
                    }
                    contador++;
                    tf=System.currentTimeMillis();
                    tiempoTotal=tiempoTotal.add(BigDecimal.valueOf(tf-t0));
                }
            }
            scanner2.close();
        }catch(FileNotFoundException e){
            System.err.println("ERROR! Archivo no encontrado "+e.getMessage());
        }

        long memoriaFin = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("<<<<<<<<RESULTADO>>>>>>>");
        System.out.println("Tamaño en bits de los números >>> "+numeroBits);
        System.out.println("Número de pruebas realizadas >>> "+contador);
        System.out.println("TIEMPO MEDIO >>> "+  tiempoTotal.divide(new BigDecimal(contador)));;
        System.out.println("Success rate >>>" + (100-fallos)+ "%");
        System.out.println("Memoria Usada =   "+(memoriaFin-memoriaInicio) + "   Bytes");
    }//Fin main  
    
    
    public static BigInteger doPollardrho(BigInteger alpha, BigInteger beta, BigInteger p, BigInteger orden){
        BigInteger a = BigInteger.ZERO;
        BigInteger b = BigInteger.ZERO;
        BigInteger aa = BigInteger.ZERO;
        BigInteger bb = BigInteger.ZERO;
        
        BigInteger x = BigInteger.ONE;
        BigInteger xx = BigInteger.ONE;
        BigInteger i = BigInteger.ONE;
        
        BigInteger xab[] = new BigInteger[3];
        while(i.compareTo(p)<0){
            xab=calculaXAB(x,a,b,p,alpha,beta);
            x=xab[0];
            a=xab[1];
            b=xab[2];
            //calculo ahora xx,aa,bb
            xab=calculaXAB(xx,aa,bb,p,alpha,beta);
            xx=xab[0];
            aa=xab[1];
            bb=xab[2];
            //segundo calculo
            xab=calculaXAB(xx,aa,bb,p,alpha,beta);
            xx=xab[0];
            aa=xab[1];
            bb=xab[2];
            
            if(x.compareTo(xx)==0){
                if(((b.subtract(bb)).gcd(orden)).compareTo(BigInteger.ONE)!=0){
                    return null;
                }
                BigInteger resta_a = aa.subtract(a);
                BigInteger resta_b = (b.subtract(bb)).modInverse(orden);
                BigInteger k = (resta_a.multiply(resta_b)).mod(orden);
                return k;
            }
            i=i.add(BigInteger.ONE);
        }
        return null;
    }
    
    public static BigInteger[] calculaXAB (BigInteger x, BigInteger a, BigInteger b, BigInteger p,BigInteger alpha, BigInteger beta){
        BigInteger[] resultado = new BigInteger[3];
        //resultado=(x,a,b)
        if(x.mod(new BigInteger("3")).compareTo(BigInteger.ZERO)==0){
            resultado[0]=x.modPow(BigInteger.TWO,p);
            resultado[1]=(a.multiply(BigInteger.TWO)).mod(p.subtract(BigInteger.ONE));
            resultado[2]=(b.multiply(BigInteger.TWO)).mod(p.subtract(BigInteger.ONE));
        }else if(x.mod(new BigInteger("3")).compareTo(BigInteger.ONE)==0){
            resultado[0]= (beta.multiply(x)).mod(p);
            resultado[1]= a;
            resultado[2]= (b.add(BigInteger.ONE)).mod(p.subtract(BigInteger.ONE));
        }else if(x.mod(new BigInteger("3")).compareTo(BigInteger.TWO)==0){
            resultado[0]= (alpha.multiply(x)).mod(p);
            resultado[1]= (a.add(BigInteger.ONE)).mod(p.subtract(BigInteger.ONE));
            resultado[2]= b;
        }else{
            resultado[0]=null;
            resultado[1]=null;
            resultado[2]=null;
        }
        return resultado;
        
    }
}//finclase




        
        
