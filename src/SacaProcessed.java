import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rmajasol
 */
public class SacaProcessed extends Saca {
    //public static String magnet = "magnet:?xt=urn:sha1:";

    public static void setnFichero() {
        Saca.nFichero = "Processed " + fInicio + "_" + fFin + ".csv";
    }

    public static String[] tokenizaProcessed(String linea) {
        String[] data = new String[5];
        //Eliminamos el carácter de escape '\' que nos da florencio
        linea = linea.replace("\\", "\\\\");
        linea = linea.replace("\\\\", "");
        //sacamos los tokens
        Pattern p = Pattern.compile("(.+?)\\|(.+?)\\|file\\("
                + "(.+?)\\) mime\\((.+?)\\) hash\\(SHA1:(.+?)\\)");
        Matcher m = p.matcher(linea);
        while (m.find()) {
            data[0] = m.group(1);//usuario
            data[1] = m.group(2);//ip
            data[2] = m.group(3);//file
            data[3] = m.group(4);//mime
            data[4] = m.group(5);//sha1
        }
        return data;
    }

    public static void sacaProcessed() throws FileNotFoundException, IOException {
        setnFichero();
        String fecha = fInicio;
        //mientras fecha no sea mayor a la fecha fín vemos si hay archivos
        while (!(fFin.compareTo(fecha) < 0)) {
            String nombreArchivo = ruta + "nautilus-" + fecha + ".log.processed";
            if (existeFichero(nombreArchivo)) {
                System.out.println("Volcando log.processed de fecha " + fecha + ".. ");
                BufferedReader br = new BufferedReader(new FileReader(nombreArchivo));
                String linea = null;
                String[] data = new String[4];
                while ((linea = br.readLine()) != null) {
                    data = tokenizaProcessed(linea);
                    /*
                     * En casos muy poco frecuentes Florencio arroja 
                     * líneas defectuosas que no coinciden con el patrón a tokenizar,
                     * por lo que en ese caso el método tokenizaSHA1 devuelve un
                     * valor 'null' y a la hora de escribirlo en el fichero 
                     * destino el programa peta. Para evitar ese NullPointerException
                     * que suelta cuando va a escribir comprobamos si se tokenizó un null o no
                     */
                    if (data[1] != null) {
                        escribeLinea(data);
                    }
                }
                //Cerramos para poder abrir el siguiente fichero sin tener que crear una BufferedReader nueva
                br.close();
            }
            fecha = incremFecha(fecha);
        }
    }
}