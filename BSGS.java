import java.util.*;
import java.util.Formatter.BigDecimalLayoutForm;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;


public class BSGS {
    public static void main (String [] args){
        Runtime runtime = Runtime.getRuntime();
        long memoriaInicio = runtime.totalMemory() - runtime.freeMemory(); //memoria gastada al inicio
        Scanner scanner = new Scanner(System.in);

        System.out.println("Introduce numero de bits para hacer las pruebas");
        int numeroBits =scanner.nextInt();
        scanner.close();
        String ruta="/Users/davidmunozclemente/Desktop/MUCC 1º CURSO/CSD/Criptoanálisis/DL/RetosDLextendido.csv";
        int contador=0;
        BigDecimal tiempoTotal=BigDecimal.ZERO;
        int fallos=0;

        try{
            Scanner scanner2 = new Scanner(new File(ruta));
            while(scanner2.hasNextLine()){
                String linea=scanner2.nextLine(); //linea del fichero
                String[] campos = linea.split(",");

                int tamanyoNumero = Integer.parseInt(campos[0].trim()); 
                BigInteger p = new BigInteger(campos[1].trim());
                BigInteger alpha = new BigInteger(campos[2].trim());
                BigInteger beta = new BigInteger(campos[3].trim());
                long t0 = System.currentTimeMillis();
                BigDecimal t0BigDecimal =BigDecimal.valueOf(t0);
                long tf=0;
                if(tamanyoNumero==numeroBits){
                    BigInteger k = doBabyStepGiantStep(p, alpha, beta);
                    if(k!=null){
                        System.out.println("k = "+ k + ">>>" +alpha+"^"+k+" = "+beta);
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
        System.out.println("Número de fallos >>> " + fallos);
        System.out.println("TIEMPO MEDIO >>> "+  tiempoTotal.divide(new BigDecimal(contador)));;
        System.out.println("Success rate >>>" + (100-fallos/contador)+ "%");
        System.out.println("Memoria Usada =   "+(memoriaFin-memoriaInicio) + "   Bytes");
    }//Fin main  
    

    public static BigInteger doBabyStepGiantStep (BigInteger p, BigInteger alpha, BigInteger beta){
        BigInteger n = calculaRaiz(p);
        LinkedHashMap <BigInteger,BigInteger> T= new LinkedHashMap <BigInteger,BigInteger>(); //Tabla donde me voy a guardar (r, alpha.modPow(r,p))
        for(BigInteger r=BigInteger.ZERO; r.compareTo(n)<0; r=r.add(BigInteger.ONE)){
            T.put(r,alpha.modPow(r,p));
        } //end For
        BigInteger alphainv = (alpha.modPow(n, p)).modInverse(p); //Esto es correcto?
        BigInteger gamma = beta;
       

        for (BigInteger q=BigInteger.ZERO; q.compareTo(n)<0;q=q.add(BigInteger.ONE)){
            if(T.containsValue(gamma)){
                BigInteger j = obtenerIndiceTabla(T, gamma);
                BigInteger k = (q.multiply(n)).add(j);
                return k;
            }
            gamma = (gamma.multiply(alphainv)).mod(p);
        }
        return null;
    }

    public static BigInteger calculaRaiz (BigInteger n){
        //Metodo para calcular la raiz cuadrada de un big integer
        BigInteger a= BigInteger.ZERO.setBit(n.bitLength()/2);
        BigInteger b= a;
        while(true) {
            BigInteger c = a.add(n.divide(a)).shiftRight(1);
            if (c.equals(a) || c.equals(b))
                if((c.pow(2)).subtract(n).compareTo(BigInteger.ZERO)==0){ //si el resultado es exacto, no modifica
                    return c;
                }else{
                    return c.add(BigInteger.ONE); //si no es exacto, redondea al alza 
                }
            b= a;
            a= c;
        }
    }

    public static BigInteger obtenerIndiceTabla (LinkedHashMap<BigInteger,BigInteger> Tabla, BigInteger valor){
        //Metodo que me devuelve el valor de la clave del mapa correspondiente
        for (Map.Entry<BigInteger, BigInteger> entry : Tabla.entrySet()) {
            if (entry.getValue().equals(valor)) {
                return entry.getKey();
            }
        }
        return null;
    }
} //fin clase
