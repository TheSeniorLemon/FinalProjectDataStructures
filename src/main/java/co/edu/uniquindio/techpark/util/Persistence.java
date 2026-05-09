package co.edu.uniquindio.techpark.util;

import java.io.*;

public class Persistence {
    /**
     * Metodo que permite serializar un objeto en un archivo en la ruta especificada
     * @param object Object a serializar
     * @throws IOException
     */
    public static void serializeObject(String path, Object object) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
        oos.writeObject(object);
        oos.close();
    }

    /**
     * Metodo que permite deserializar un objeto de un archivo en la ruta especificada
     * @return Objeto deserializado
     * @throws Exception
     */
    public static Object deserializeObject(String path) throws Exception {
        if (!new File(path).exists()) {
            return null;
        }

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
        Object object = ois.readObject();
        ois.close();

        return object;
    }
}