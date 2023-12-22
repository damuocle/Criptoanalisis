import java.math.*;
import java.io.*;
import java.util.*;

public class Fermat{
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
                    BigInteger factores[] = doFermat(n);
					BigInteger factor1 = factores[0];
					BigInteger factor2 = factores[1];
					System.out.println(factor1 + "y "+ factor2);
                    tf=System.currentTimeMillis();
                	contador++;
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
	


	public static BigInteger[] doFermat (BigInteger n){
		
		BigInteger[] a_aux = n.sqrtAndRemainder(); //array con raiz cuadrada y resto
		BigInteger a;
		if(a_aux[1].compareTo(BigInteger.ZERO)==0){
			a=a_aux[1];
		}else{
			a=a_aux[0].add(BigInteger.ONE);
		}
		
		BigInteger b = a.pow(2).subtract(n);
		BigInteger [] result = new BigInteger[2];
		
		BigInteger[] b_aux = b.sqrtAndRemainder();
		while(b_aux[1].compareTo(BigInteger.ZERO)!=0){
			a=a.add(BigInteger.ONE);
			b=a.pow(2).subtract(n);
			b_aux = b.sqrtAndRemainder();
		}

		result[0] = a.subtract(b.sqrt());
		result[1] = a.add(b.sqrt());

		

		return result;
	}

}