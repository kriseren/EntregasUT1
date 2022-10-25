package com.diego.ut1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Programa que mediante canales y buffers de NIO, realiza una copia de un archivo binario. En este caso será "artistas.dat".
 * @author Diego Calatayud
 */
public class ejercicio2
{
    public static void main(String[] args) throws IOException
    {
        //Creo un objeto Scanner.
        Scanner scan = new Scanner(System.in);
        //Imprimo la información del programa.
        System.out.println("[Programa de copia de un fichero binario]");
        System.out.println("Introduce el fichero a copiar: ");
        String original = scan.nextLine();
        System.out.println("Introduce el nombre del fichero resultante: ");
        String copia = scan.nextLine();
        //Realizo la copia.
        copia(original,copia);
    }

    public static void copia(String original, String copia) throws IOException
    {
        //LECTURA.
            //Creo el Random Access File para leer.
            RandomAccessFile raf = new RandomAccessFile(original,"rw");
            //Creo un nuevo File Channel para la lectura.
            FileChannel fileChannelLectura = raf.getChannel();
            //Creo el buffer para leer el fichero.
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //ESCRITURA.
            //Creo un file que almacenará la copia.
            Path resultado = Paths.get(copia);
            //Creo un Hash set para poder almacenar las opciones del File channel.
            Set<StandardOpenOption> options = new HashSet<>();
            options.add(StandardOpenOption.CREATE);
            options.add(StandardOpenOption.APPEND);
            //Creo un nuevo File Channel para la escritura.
            FileChannel fileChannelEscritura = FileChannel.open(resultado,options);

        //PROCEDO A LEER Y ESCRIBIR
            //Mientras que el tamaño del buffer sea mayor que 0.
            while(fileChannelLectura.read(byteBuffer) > 0)
            {
                byteBuffer.flip(); //Paso el buffer a escritura.
                fileChannelEscritura.write(byteBuffer); //Escribo en el fichero.
                byteBuffer.rewind(); //Reinicio el límite y la posición para el caso de una nueva pasada.
            }

        //CIERRO LOS CHANNELS.
        raf.close();
        fileChannelLectura.close();
        fileChannelEscritura.close();
    }
}
