import java.io.*;

public class Saca {

    public static String ruta = "/Users/yomac/Desktop/logs/";
    public static String nFichero = null;
    public static String fInicio = "20111022", fFin = "20120105";
    public static String sep = "|";
    public static String saltoLinea = "\r\n";
    public static boolean primeraEscritura = true;

    public static boolean existeFichero(String nFichero) {
        if (new File(nFichero).exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static void escribeLinea(String[] data) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(ruta + nFichero, true));
        String linea = "";
        for (int i = 0; i < data.length - 1; i++) {
            linea += data[i] + sep;
        }
        linea += data[data.length-1] + saltoLinea;

        out.write(linea);
        out.close();
    }

    /*
     * antes de escribir cada SHA1 se mira si ya está en el fichero resultante
     * para que así no aparezcan duplicados
     */
    public static boolean lineaRepetida(String linea, String nFichero) throws FileNotFoundException, IOException {
        BufferedReader r = new BufferedReader(new FileReader(nFichero));
        String lineaFich = null;
        boolean encontrado = false, sal = false;
        while ((lineaFich = r.readLine()) != null && !sal) {
            if (linea.equals(lineaFich)) {
                encontrado = true;
                sal = true;
            }
        }
        return encontrado;
    }

    public static String incremFecha(String fecha) {
        int a = Integer.parseInt(fecha.substring(0, 4));
        int m = Integer.parseInt(fecha.substring(4, 6));
        int d = Integer.parseInt(fecha.substring(6, 8));
        String fechaA = fecha.substring(0, 4);
        String fechaM = fecha.substring(4, 6);
        if (d <= 31) {
            d++;
            if (d < 10) {
                fecha = fechaA + fechaM + "0" + String.valueOf(d);
            } else {
                fecha = fechaA + fechaM + String.valueOf(d);
            }
        } else {
            if (m <= 12) {
                m++;//d=1
                if (m < 10) {
                    fecha = fechaA + "0" + String.valueOf(m) + "01";
                } else {
                    fecha = fechaA + String.valueOf(m) + "01";
                }
            } else {
                a++;
                fecha = a + "01" + "01";
            }
        }
        return fecha;
    }
}
