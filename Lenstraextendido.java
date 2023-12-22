import java.util.*;
import java.beans.beancontext.BeanContextServiceProviderBeanInfo;
import java.math.*;
import java.io.*;

public class Lenstraextendido{
    public static void main (String [] args){
        Runtime runtime = Runtime.getRuntime();
        long memoriaInicio = runtime.totalMemory() - runtime.freeMemory(); //memoria gastada al inicio
        //Lectura del fichero
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduzca el numero de bits de los numeros a factorizar>>> ");
        int numeroBits =scanner.nextInt();
        scanner.close();
        String ruta = "/Users/davidmunozclemente/Desktop/MUCC 1º CURSO/CSD/Criptoanálisis/Factorizacion/RetosFactorizacion.csv"; //ruta del fichero a leer con los numeros...
        int contador = 0;
        long tiempoTotal=0;
        int fallos=0;
        try{
            Scanner scanner2 = new Scanner(new File(ruta)); //objeto clase scanner con lectura del fichero con ruta ruta

            //bucle principal para leer el fichero
            while(scanner2.hasNextLine()){
                String linea=scanner2.nextLine(); //linea del fichero
                String[] campos = linea.split(","); //me creo un array de string separando cada linea por la comma

                int tamanyoNumero = Integer.parseInt(campos[0].trim()); 
                BigInteger n = new BigInteger(campos[1].trim());
                long t0 = System.currentTimeMillis();
                long tf=0;
                if(numeroBits==tamanyoNumero){
                    BigInteger factor = doLenstra(n);
                    if(factor==null){
                        System.out.println("Algoritmo FINALIZADO");
                        tf=System.currentTimeMillis();
                        contador++;
                    }
                   
                    tiempoTotal=tiempoTotal+(tf-t0);
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
        System.out.println("TIEMPO MEDIO >>> "+(double) tiempoTotal/contador);;
        System.out.println("Memoria Usada =   "+(memoriaFin-memoriaInicio) + "   Bytes");
    }

    public static BigInteger doLenstra(BigInteger n){
        //Curva eliptica y^2 = x^3 + ax + b
       
        
        //Genero aleatorio a, x, y
        while(true){
           
            BigInteger a = generarBigIntegerAleatorio(n);
            BigInteger x = generarBigIntegerAleatorio(n);
            BigInteger y = generarBigIntegerAleatorio(n);
        
            //ya tengo a,x,y, puedo calcular b despejando de la ecuacion de la curva 
            BigInteger b = calculaBencurva(x, y, a, n);
            BigInteger B = new BigInteger("10000");
            Punto punto = new Punto(x, y);
            BigInteger k = new BigInteger("2");
            BigInteger d;
            Punto puntoauxiliar = punto;
            while(k.compareTo(B)<=0){
                Punto puntonuevo = calcularProductoEscalar(k, puntoauxiliar, n,a);
                //puntoauxiliar=puntonuevo;
            
                if(puntonuevo!=null){
                    k=k.add(BigInteger.ONE);
                }else{
                    return null;
                }
                puntoauxiliar=puntonuevo;
            } 
            System.out.println("No se ha encontrado ningun valor");
            System.out.println("Buscamos otra curva y otro punto...");
        }
       
    }
       
    public static BigInteger generarBigIntegerAleatorio (BigInteger n){
        Random random = new Random();
        BigInteger numero;
        do {
            numero = new BigInteger(n.bitLength(), random);
        } while (numero.compareTo(BigInteger.ONE) < 0 || numero.compareTo(n.subtract(BigInteger.ONE)) >= 0);

        return numero;
    }

    public static BigInteger calculaBencurva(BigInteger x, BigInteger y, BigInteger a, BigInteger n){
        BigInteger b = (y.modPow(new BigInteger("2"),n).subtract(x.modPow(new BigInteger("3"),n).subtract(a.multiply(x)))).mod(n);
        return b;
    }

    public static Punto calcularProductoEscalar (BigInteger k, Punto p, BigInteger n, BigInteger a){
        String representacion = k.toString(2);
        Punto sol = Punto.getPuntoInfinito(); //iniciamos con punto en infinito (x,y)=(0,0)

        //Iteracion sobre la representacion binaria de k para calcular la solucion
        for (int i=0; i<representacion.length();i++){
            sol=sumarPuntosCurva(sol,sol,n,a);
            if(sol==null){
                return null;
            }else{
                if (representacion.charAt(i) == '1') {
                    sol = sumarPuntosCurva(sol, p, n,a);
                    if(sol==null){
                       return null;
                    }
                }
            }
        }
        return sol;
    }

    public static Punto sumarPuntosCurva(Punto p, Punto q, BigInteger n, BigInteger a){
        //BigInteger m = ((p.getX().pow(2).multiply(new BigInteger("3")).add(a)).divide(p.getY().multiply(new BigInteger("2")))).mod(n);
        BigInteger m=BigInteger.ZERO;
        BigInteger numeradorpendiente;
        BigInteger denominadorpendiente;
        //boolean check = (p.equals(q) && p.getX().compareTo(BigInteger.ZERO)!=0 && p.getY().compareTo(BigInteger.ZERO)!=0 && q.getX().compareTo(BigInteger.ZERO)!=0 && q.getY().compareTo(BigInteger.ZERO)!=0);

        if((p.getX().compareTo(q.getX())==0 && p.getY().compareTo(q.getY())==0 && 
        p.getX().compareTo(BigInteger.ZERO)!=0 && p.getY().compareTo(BigInteger.ZERO)!=0 && 
        q.getX().compareTo(BigInteger.ZERO)!=0 && q.getY().compareTo(BigInteger.ZERO)!=0)){ //los puntos p y q son iguales
            //si los dos puntos son iguales y NO SON (0,0)
            numeradorpendiente=(p.getX().pow(2).multiply(BigInteger.valueOf(3)).add(a)).mod(n);
            denominadorpendiente=(p.getY().multiply(BigInteger.valueOf(2))).mod(n);
            if(denominadorpendiente.gcd(n).compareTo(BigInteger.ONE)==0){
                m=(numeradorpendiente.multiply(denominadorpendiente.modInverse(n))).mod(n);
            }else{
                System.out.println("Un factor de n = "+n+" es >>> "+p.getY().multiply(BigInteger.TWO).gcd(n).mod(n));
                return null;
            }
            
            //m= p.getX().pow(2).multiply(BigInteger.valueOf(3)).add(a).multiply(p.getY().multiply(BigInteger.valueOf(2)).modInverse(n)).mod(n);
        }else if(q.getX().compareTo(p.getX())!=0 && q.getY().compareTo(p.getY())!=0 && p.getX().compareTo(BigInteger.ZERO)==0 && p.getY().compareTo(BigInteger.ZERO)==0){
            //los dos puntos son distintos, y el punto P es (0,0)
            return new Punto(q.getX(), q.getY());
            //El resultado es Q
        }else if((p.getX().compareTo(q.getX())!=0 && p.getY().compareTo(q.getY())!=0 && q.getX().compareTo(BigInteger.ZERO)==0 && q.getY().compareTo(BigInteger.ZERO)==0)){
            //los dos puntos son distintos, y el punto Q es (0,0)
            return new Punto(p.getX(), p.getY());
            //El resultado es P
        }else if(p.getX().compareTo(q.getX())!=0 && p.getY().compareTo(q.getY())!=0 && p.getX().compareTo(BigInteger.ZERO)!=0 && p.getY().compareTo(BigInteger.ZERO)!=0 && q.getX().compareTo(BigInteger.ZERO)!=0 && q.getY().compareTo(BigInteger.ZERO)!=0){
            //Los dos puntos P y Q son distintos, y ninguna coordenada es 0
            //m=q.getY().subtract(p.getY()).multiply(q.getX().subtract(p.getX()).modInverse(n)).mod(n);
            numeradorpendiente=p.getY().subtract(q.getY()).mod(n);
            
            
            //Compruebo si xp-xq tiene inverso --> mcd(xp-xq, n) --> devuelvo r
            if(((p.getX().subtract(q.getX())).mod(n)).gcd(n).compareTo(BigInteger.ONE)==0){
                denominadorpendiente=(p.getX().subtract(q.getX())).modInverse(n);
                m=(numeradorpendiente.multiply(denominadorpendiente)).mod(n);
            }else{
                System.out.println("Un factor de n"+"="+n+" es >>>"+ ((p.getX().subtract(q.getX())).mod(n).gcd(n)));
                return null;
                
            }
        
        }else{

        }


        if(m.compareTo(BigInteger.ZERO)==0){
            return new Punto(BigInteger.ZERO, BigInteger.ZERO);
        }else{
            BigInteger x = (m.modPow(BigInteger.TWO,n).subtract(p.getX()).subtract(q.getX())).mod(n);
            BigInteger factory1 = (m.multiply(p.getX().subtract(x))).mod(n);
            BigInteger y = (factory1.subtract(p.getY())).mod(n);
            Punto r = new Punto(x,y);
            return r;
        }
    }
    
    
    public static boolean tieneInverso (BigInteger x, BigInteger y, BigInteger n){
        //Metodo para saber si la expresion x-y tiene inverso (true) o no tiene inverso (false)
        if(x.subtract(y).mod(n).gcd(n).compareTo(BigInteger.ONE)==0){
            return true;
        }else{
            return false;
        }
    }

    //Clase estática Punto
    static class Punto {
        private BigInteger x;
        private BigInteger y;

        public Punto(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }

        public BigInteger getX(){
            return x;
        }

        public BigInteger getY(){
            return y;
        }


        public static Punto getPuntoInfinito() {
            return new Punto(BigInteger.ZERO, BigInteger.ZERO);
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }

        
    }
        
}


