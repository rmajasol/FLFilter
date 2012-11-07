import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rmajasol
 */
public class SacaSHA1 extends Saca {

    public static void setnFichero() {
        Saca.nFichero = "CodigosSHA1 "+fInicio+"_"+fFin+".txt";
    }

    public static void escribeLinea(String sha1) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(ruta + nFichero, true));
        try {
            if (primeraEscritura) {
                out.write(sha1 + saltoLinea);
                primeraEscritura = false;
            } else {
                if (!lineaRepetida(sha1, ruta + nFichero)) {
                    out.write(sha1 + saltoLinea);
                }
            }
            out.close();
        } catch (IOException e) {
        }
    }

    public static String tokenizaSHA1(String linea) {
        String sha1 = null;
        //Eliminamos el carácter de escape '\' que nos da florencio
        linea = linea.replace("\\", "\\\\");
        linea = linea.replace("\\\\", "");
        //sacamos el token en cuestión de la línea leída
        Pattern p = Pattern.compile("hash\\(SHA1:(.+?)\\)");
        Matcher m = p.matcher(linea);
        while (m.find()) {
            sha1 = m.group(1);
        }
        return sha1;
    }

    public static void sacaChorizo() throws FileNotFoundException, IOException {
        setnFichero();
        String fecha = fInicio;
        //mientras fecha no sea mayor a la fecha fín vemos si hay archivos
        while (!(fFin.compareTo(fecha) < 0)) {
            String nombreArchivo = ruta + "nautilus-" + fecha + ".log.processed";
            if (existeFichero(nombreArchivo)) {
                System.out.println("Volcando " + fecha + ".. ");
                BufferedReader br = new BufferedReader(new FileReader(nombreArchivo));
                String linea = null;
                String sha1;
                while ((linea = br.readLine()) != null) {
                    sha1 = tokenizaSHA1(linea);
                    /*
                     * En casos muy poco frecuentes Florencio arroja 
                     * líneas defectuosas que no coinciden con el patrón a tokenizar,
                     * por lo que en ese caso el método tokenizaSHA1 devuelve un
                     * valor 'null' y a la hora de escribirlo en el fichero 
                     * destino el programa peta. Para evitar ese NullPointerException
                     * que suelta cuando va a escribir comprobamos si se tokenizó un null o no
                     */
                    if (sha1 != null) {
                        escribeLinea(sha1);
                    }
                }
                //Cerramos para poder abrir el siguiente fichero sin tener que crear una BufferedReader nueva
                br.close();
            }
            fecha = incremFecha(fecha);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        //sacaChorizo();
        //System.out.println(incremFecha("20120230"));
        
        //SacaProcessed.sacaProcessed();
        
        SacaDump.sacaDump("CO");
        SacaExcel.sacaExcel("CO");

    }
}
