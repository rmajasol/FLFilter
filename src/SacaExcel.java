
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rmajasol
 */
//sacar para un .csv a importar en excel
//public static String magnet = "magnet:?xt=urn:sha1:";
public class SacaExcel extends Saca {

    public static void setnFichero(String codPais) {
        Saca.nFichero = "MezclaExcel " + fInicio + "_" + fFin + " - " + codPais + ".csv";
    }

    public static void escribeLinea(String[] data, String fecha, String pais) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(ruta + nFichero, true));
        if (primeraEscritura) {
            out.write("User|IP|Date|Country|Magnet URI|filename|mime" + saltoLinea);
            Saca.primeraEscritura = false;
        } else {
            //magnet:?xt=urn:sha1:4LTTABEPQHOJKLAHSPLTN2X7YBCXGHBA&dn=para descargar mas rapido en ares.txt
            //=HIPERVINCULO("HTTP://WWW.MARCA.ES";"MARCA")
//        String link = "=HIPERVINCULO(\"magnet:?xt=urn:sha1:"
//                + data[4] + "&dn=" + data[2] + "\";\"" + data[2]
//                + "\")";
            String link = "magnet:?xt=urn:sha1:" + data[4];
            String linea = data[0] + sep //user
                    + data[1] + sep //ip
                    + fecha + sep //fecha
                    + pais + sep //pais
                    + link + sep //SHA1 + filename en un link para excel
                    + data[2] + sep
                    + data[3]//mime
                    + saltoLinea;
            out.write(linea);
        }
        out.close();
    }

    public static String[] tokenizaIpYPais(String linea) {
        String data[] = new String[2];
        Pattern p = Pattern.compile("\\s(.+?)\\s+\\|\\s+/(.+?)\\s+\\|"
                + "\\s+(.+?)\\s+\\|\\s+(.+?)\\s+\\|\\s+(.+?)\\s+\\|\\s+(.+?.+?)");
        Matcher m = p.matcher(linea);
        while (m.find()) {
            data[0] = m.group(2);//ip
            data[1] = m.group(6);//pais
        }
        return data;
    }

    //lee en el dump para sacar el país a partir de la ip
    public static String sacaPais(String ip, String nFichero) throws IOException {
        String pais = null;
        if (existeFichero(nFichero)) {
            BufferedReader br = new BufferedReader(new FileReader(nFichero));
            String linea = null;
            String data[] = new String[2];
            boolean sal = false;
            int i = 0;
            while ((linea = br.readLine()) != null && !sal) {
                data = tokenizaIpYPais(linea);
                if (ip.equals(data[0])) {
                    pais = data[1];
                    sal = true;
                }
                i++;
            }
            if (!sal) {
                pais = "--";
            }
        } else {
            pais = "--";
        }

        return pais;
    }

    /**
     * Saca excel solo para el país dado *
     */
    public static void sacaExcel(String codPais) throws FileNotFoundException, IOException {
        setnFichero(codPais);
        String fecha = fInicio;
        //mientras fecha no sea mayor a la fecha fín vemos si hay archivos
        while (!(fFin.compareTo(fecha) < 0)) {
            String nombreArchivo1 = ruta + fecha + "-dump.log";
            String nombreArchivo2 = ruta + "nautilus-" + fecha + ".log.processed";
            if (existeFichero(nombreArchivo1) && existeFichero(nombreArchivo2)) {
                System.out.println("Volcando " + fecha + ".. ");
                BufferedReader br = new BufferedReader(new FileReader(nombreArchivo2));
                String linea = null, pais = null;
                String[] data = new String[4];
                while ((linea = br.readLine()) != null) {
                    data = SacaProcessed.tokenizaProcessed(linea);
                    if (data[1] != null) {
                        pais = sacaPais(data[1], nombreArchivo1);
                        if (pais.equalsIgnoreCase(codPais)) {
                            escribeLinea(data, fecha, pais);
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
