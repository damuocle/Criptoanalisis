import java.util.*;
import java.math.*;
import java.io.*;

public class Pollardrho{
	public static void main (String [] args){
		Runtime runtime = Runtime.getRuntime();
		long memoriaInicio = runtime.totalMemory() - runtime.freeMemory(); //memoria gastada al inicio
		Scanner scanner = new Scanner(System.in);
		System.out.println("Introduzca numero de bits para analizar");
		int bits = scanner.nextInt();
		String ruta = "/Users/davidmunozclemente/Desktop/MUCC 1º CURSO/CSD/Criptoanálisis/Factorizacion/RetosFactorizacion.csv"; //ruta del fichero a leer con los numeros...
        int contador = 0;
        long tiempoTotal=0;
		int fallos=0;
		try{
            Scanner scanner2 = new Scanner(new File(ruta)); //objeto clase scanner con lectura del fichero con ruta ruta

            //bucle principal para leer el fichero
            while(scanner2.hasNextLine() && contador <1){
                String linea=scanner2.nextLine(); //linea del fichero
                String[] campos = linea.split(","); //me creo un array de string separando cada linea por la comma

                int tamanyoNumero = Integer.parseInt(campos[0].trim()); 
                BigInteger n = new BigInteger(campos[1].trim());
                long t0 = System.currentTimeMillis();
                long tf=0;
					if(bits==tamanyoNumero){
						BigInteger factor = doPollardrho(n);
						System.out.println("Un factor de n "+n +"  es " + factor);
						contador++;
						tf=System.currentTimeMillis();
						tiempoTotal=tiempoTotal+(tf-t0);
					}
				
				
			}
			scanner2.close();
		}catch(FileNotFoundException e){
		System.err.println("ERROR! Archivo no encontrado "+e.getMessage());
		}

	long memoriaFin = runtime.totalMemory() - runtime.freeMemory();
	System.out.println("<<<<<<<<RESULTADO>>>>>>>");
	System.out.println("Tamaño en bits de los números >>> "+bits);
	System.out.println("Número de pruebas realizadas >>> "+contador);
	System.out.println("TIEMPO MEDIO >>> "+(double) tiempoTotal/contador);
	System.out.println("Memoria Usada =   "+(memoriaFin-memoriaInicio) + "   Bytes");
	}
	

	public static BigInteger doPollardrho (BigInteger n){
		Random random = new Random();
		BigInteger a = generarBigIntegerAleatorio(n);
		BigInteger b;
		BigInteger p;
		b=a;

		while(true){
			a= (a.pow(2).add(BigInteger.ONE)).mod(n);
			b= (b.pow(2).add(BigInteger.ONE)).mod(n);
			b= (b.pow(2).add(BigInteger.ONE)).mod(n);
			
			p = (a.subtract(b)).gcd(n);
			
			if (p.compareTo(BigInteger.ONE)>0 && p.compareTo(n)<0){
				return p;
			}else if (p.compareTo(n)==0){
				return n;
			}
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

}
