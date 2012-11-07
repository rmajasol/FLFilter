/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rmajasol
 */
public class SacaDump extends Saca {

    public static void setnFichero(String codPais) {
        Saca.nFichero = "Dumps " + fInicio + "_" + fFin + " - "+ codPais +".csv";
    }

    public static String[] tokenizaDump(String linea) {
        /*
         * Pattern MY_PATTERN = Pattern.compile("\\[(.*?)\\]"); //Matcher m = MY_PATTERN.matcher("FOO[BAR]");
         */
        String[] data = new String[5];
        Pattern p = Pattern.compile("\\s(.+?)\\s+\\|\\s+/(.+?)\\s+\\|"
                + "\\s+(.+?)\\s+\\|\\s+(.+?)\\s+\\|\\s+(.+?)\\s+\\|\\s+(.+?.+?)");
        Matcher m = p.matcher(linea);
        while (m.find()) {
            data[0] = m.group(1);//usuario
            data[1] = m.group(2);//ip
            data[2] = m.group(3);//fecha
            data[3] = m.group(4);//hash1
            //data[4] = m.group(5);//hash2
            data[4] = m.group(6);//pais*/
        }
        return data;
    }

    /**
     * Saca para el país dado *
     */
    public static void sacaDump(String codPais) throws FileNotFoundException, IOException {
        setnFichero(codPais);
        String fecha = fInicio;
        //mientras fecha no sea mayor a la fecha fín vemos si hay archivos
        while (!(fFin.compareTo(fecha) < 0)) {
            String nombreArchivo = ruta + fecha + "-dump.log";
            if (existeFichero(nombreArchivo)) {
                System.out.println("Volcando dump.log de fecha " + fecha + ".. ");
                BufferedReader br = new BufferedReader(new FileReader(nombreArchivo));
                String linea = null, pais = null;
                String[] data = new String[5];
                while ((linea = br.readLine()) != null) {
                    data = tokenizaDump(linea);
                    /*
                     * En casos muy poco frecuentes Florencio arroja líneas
                     * defectuosas que no coinciden con el patrón a tokenizar,
                     * por lo que en ese caso el método tokenizaSHA1 devuelve un
                     * valor 'null' y a la hora de escribirlo en el fichero
                     * destino el programa peta. Para evitar ese
                     * NullPointerException que suelta cuando va a escribir
                     * comprobamos si se tokenizó un null o no
                     */
                    if (data[1] != null) {
                        pais = data[4];
                        if (pais.equalsIgnoreCase(codPais)) {
                            escribeLinea(data);
                        }
                    }
                }
                //Cerramos para poder abrir el siguiente fichero sin tener que crear una BufferedReader nueva
                br.close();
            }
            fecha = incremFecha(fecha);
        }
    }
}
