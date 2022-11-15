package com.diego.ut1;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * Programa de gestión de objetos Artista (id,nombre,edad,tipo) con opciones de alta y consulta en un fichero RAF.
 * @author Diego Calatayud
 */
public class ejercicio1
{
    /*DEFINICIÓN DE ATRIBUTOS*/
    private static final int MAXSTRING=32;
    private static final long SIZE = 2*(Integer.SIZE/8)+(Double.SIZE/8)+MAXSTRING+1; //ID + ARTIST_NAME + CACHE + AGE + CHAR
    private static final Scanner scan = new Scanner(System.in);
    private static final File f0 = new File("files/artistas.dat");
    private static final File f1 = new File("files/sinonimos.dat");
    private static int coincidencias = 0;

    public static void main(String[] args) throws InterruptedException {
        //Llamo al método menu.
        menu();
    }

    public static void menu() throws InterruptedException
    {
        int op=1;

        //Si el directorio files no existe se crea.
        File dirFiles = new File("files");
        if(!dirFiles.exists())
            dirFiles.mkdir();

        while(op>=0)
        {
            System.out.print("\n[GESTOR DE ARTISTAS]\n 1-. Dar de alta un artista.\n 2-. Consultar un artista.\n 3-. Dar de baja un artista.\n 4-. Exportar artistas a xml.\n 0-. Salir del programa\nSelecciona una opción: ");
            op=  scan.nextInt();
            switch(op)
            {
                case 1:alta();break;
                case 2:consulta();break;
                case 3:baja();break;
                case 4:exportar();break;
                case 0:System.exit(0);break;
                default:System.out.println("Introduce un número válido");
            }
        }
    }

    public static void alta()
    {
        char type='f'; double cache; int id=0,age=0;
        while (id<1) //Controlo que el usuario introduzca un ID mayor que 0.
        {
            System.out.print("Introduce el código ID del artista (mayor que 0): ");
            String idStr = scan.next();
            if(esNumerico(idStr))
                id = Integer.parseInt(idStr);
        }
        System.out.print("Introduce el nombre del artista: ");
        scan.nextLine(); //Limpio el buffer del teclado
        String artistName = scan.nextLine();
        artistName = recortaNombre(artistName); //Recorto el nombre del artista.
        System.out.print("Introduce el caché del artista: ");
        //scan.nextLine(); //Limpio el buffer del teclado.
        cache = scan.nextDouble();
        while (age<1) //Controlo que el usuario introduzca una edad mayor que 0.
        {
            System.out.print("Introduce la edad del artista (mayor que 0): ");
            String ageStr = scan.next();
            if(esNumerico(ageStr))
                age = Integer.parseInt(ageStr);
        }
        while(type!='s'&&type!='b') //Controlo que el usuario introduzca una s o una b.
        {
            System.out.print("Introduce si el artista es solista (s) o participa en una banda (b): ");
            type = scan.next().charAt(0);
        }

        //Escribo los datos en el fichero RAF y sinónimos.
        try(RandomAccessFile raf = new RandomAccessFile(f0,"rw"))
        {
            if(buscaDuplicados(id)) //Si el artista existe, se escribe en el área de sinónimos. Si no, se escribe en el RAF.
            {
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(f1,true));
                muestraMensaje("El artista ya existe, por lo que se agregará en el área de sinónimos.");
                dos.writeInt(id);
                dos.writeUTF(artistName);
                dos.writeDouble(cache);
                dos.writeInt(age);
                dos.writeChar(type);
                dos.close();
            }
            else
            {
                raf.seek(SIZE*(id-1));
                raf.writeInt(id);
                raf.writeUTF(artistName);
                raf.writeDouble(cache);
                raf.writeInt(age);
                raf.writeChar(type);
            }
            muestraMensaje("Artista agregado correctamente");
        } catch(IOException fnfe)
        {
            fnfe.printStackTrace();
        }
    }

    public static void consulta() throws InterruptedException
    {
        int idConsulta=0;
        if(f0.exists())
        {
            //Pido un ID para realizar una consulta.
            while (idConsulta<1) //Controlo que el usuario introduzca un ID mayor que 0.
            {
                System.out.print("Introduce el código ID del artista a buscar (mayor que 0): ");
                String idConsultaStr = scan.next();
                if(esNumerico(idConsultaStr))
                    idConsulta = Integer.parseInt(idConsultaStr);
            }
            System.out.print("Buscando artista");

            for (int i=0;i<3;i++) //Chorrada para que parezca que la búsqueda trabaja.
            {
                Thread.sleep((long)((Math.random()*1000)+1));
                System.out.print(".");
            }
            System.out.println();

            //Extraigo los datos del RAF.
            if(buscaDuplicados(idConsulta)) //Si el artista existe, entonces se leen los demás datos.
            {
                muestraArtista(idConsulta);
            }
            else
                muestraMensaje("No existe ningún artista asociado a ese ID.");
        }
        else
            muestraMensaje("No se ha dado de alta ningún artista.");
    }


    public static void baja()
    {
        if(f0.exists()||f1.exists())
        {
            int idBaja=0;
            //Pido un ID para realizar una consulta.
            while (idBaja<1) //Controlo que el usuario introduzca un ID mayor que 0.
            {
                System.out.print("Introduce el código ID del artista a dar de baja (mayor que 0): ");
                String idBajaStr = scan.next();
                if(esNumerico(idBajaStr))
                    idBaja = Integer.parseInt(idBajaStr);
            }

            //Mostrar todos los artistas con el mismo ID
            if(buscaDuplicados(idBaja)) //Si existe un registro asociado a ese ID.
            {
                muestraArtista(idBaja);
                //Contar y mostrar las coincidencias encontradas.
                if(coincidencias>1) //Existen duplicados.
                {
                    System.out.println("Se han encontrado "+coincidencias+" artistas asociados al mismo ID.");
                    System.out.print("Introduce el nombre del artista a eliminar: ");
                    String artist = scan.next();
                    System.out.print("¿Estás seguro de que deseas dar de baja el artista? (S/N): ");
                    char op = scan.next().toUpperCase().charAt(0);
                    if(op=='S')
                    {
                        File f2 = new File("files/artistas2.dat"); //Fichero que guarda los cambios y es renombrado.
                        try(DataInputStream dis = new DataInputStream(new FileInputStream("files/sinonimos.dat"));
                            DataOutputStream dos = new DataOutputStream(new FileOutputStream(f2,true));
                            RandomAccessFile raf = new RandomAccessFile(f0,"rw"))
                        {
                            //Leer el id y nombre, y si es, borrarlo.
                            raf.seek(SIZE*(idBaja-1));
                            int id = raf.readInt();
                            String artistName = raf.readUTF();
                            if(artistName.equals(artist)) //Si el nombre coincide, se borra.
                            {
                                raf.seek(SIZE*(idBaja-1));
                                raf.writeInt(0);
                                raf.writeUTF("");
                                raf.writeDouble(0);
                                raf.writeInt(0);
                                raf.writeChar(0);
                            }
                            else //Si no coincide, se lee del área de sinónimos.
                            {
                                while(dis.available()>0) //Mientras hayan líneas a leer.
                                {
                                    id = dis.readInt();
                                    artistName = dis.readUTF();
                                    double cache = dis.readDouble();
                                    int age = dis.readInt();
                                    char type = dis.readChar();
                                    if(!artistName.equals(artist)) //Si el nombre coincide, no se escriben los datos.
                                    {
                                        dos.writeInt(id);
                                        dos.writeUTF(artistName);
                                        dos.writeDouble(cache);
                                        dos.writeInt(age);
                                        dos.writeChar(type);
                                    }
                                }
                                f2.renameTo(f1);
                            }
                            muestraMensaje("Artista eliminado correctamente.");
                        }
                        catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                        muestraMensaje("No se ha eliminado ningún artista.");
                }
                else //No existen duplicados.
                {
                    System.out.print("¿Estás seguro de que deseas dar de baja el artista? (S/N): ");
                    char op = scan.next().toUpperCase().charAt(0);
                    if(op=='S')
                    {
                        try(RandomAccessFile raf = new RandomAccessFile(f0,"rw"))
                        {
                            raf.seek(SIZE*(idBaja-1));
                            raf.writeInt(0);
                            raf.writeUTF("");
                            raf.writeDouble(0);
                            raf.writeInt(0);
                            raf.writeChar(0);
                            muestraMensaje("Artista eliminado correctamente.");
                        }
                        catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            else //Si no hay ningún registro asociado.
                muestraMensaje("No existe ningún artista asociado a ese ID.");
        }
        else //Si existe ninguno de los ficheros de datos.
            muestraMensaje("No existen registros a dar de baja.");
    }

    public static void exportar()
    {
        if(f0.exists()|| f1.exists()) //Si existe alguno de los ficheros.
        {
            //Pido los datos para crear el fichero.
            System.out.print("Introduce el nombre del fichero a exportar: ");
            scan.nextLine();
            String fileName = scan.nextLine();

            //Creo los objetos necesarios para la escritura.
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            try
            {
                db = dbf.newDocumentBuilder();
            }
            catch (ParserConfigurationException e)
            {
                throw new RuntimeException(e);
            }
            DOMImplementation dom = db.getDOMImplementation();
            Document document = dom.createDocument(null,"xml",null);

            //Construcción del documento.
            try(RandomAccessFile raf = new RandomAccessFile(f0,"r"))
            {
                //Creación de la raíz.
                Element raiz = document.createElement("artistas");
                document.getDocumentElement().appendChild(raiz);

                //Voy leyendo el documento de artistas y posteriormente el de sinónimos.
                int id;
                for(int i=1;true;i++) //Mientras que no se llegue al end of file del randomAccessFile
                {
                    try
                    {
                        raf.seek(SIZE*(i-1));
                        id=raf.readInt();
                        if(id>0)
                        {
                            //Creación del primer nodo.
                            Element nodoArtista = document.createElement("artista");
                            raiz.appendChild(nodoArtista);
                            //Creo y agrego cada nodo con su valor.
                            creaNodo(document,nodoArtista,"id", String.valueOf(id));
                            creaNodo(document,nodoArtista,"nombre",raf.readUTF());
                            creaNodo(document,nodoArtista,"cache", String.valueOf(raf.readDouble()));
                            creaNodo(document,nodoArtista,"edad", String.valueOf(raf.readInt()));
                            char type = raf.readChar();
                            String valor;
                            if(type=='s')
                                valor="Solista";
                            else
                                valor="Banda";
                            creaNodo(document,nodoArtista,"tipo",valor);

                            //Se escribe en el fichero
                            Source source = new DOMSource(document);
                            Result resultado = new StreamResult(new File("files/"+fileName));
                            try
                            {
                                Transformer transformer = TransformerFactory.newInstance().newTransformer(); //Creamos una nueva instancia de Transformer.
                                transformer.setOutputProperty("indent","yes"); //Definimos la propiedad de tabulaciones y espacios.
                                transformer.transform(source,resultado); // Transformamos la source en el fichero resultado.
                            }
                            catch(TransformerException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    catch(EOFException e)
                    {
                        break;
                    }
                }
                if(f1.exists())
                {
                    DataInputStream dis = new DataInputStream(Files.newInputStream(f1.toPath()));
                    while(dis.available()>0) //Mientras que queden líneas a leer en los sinónimos
                    {
                        //Creación del primer nodo.
                        Element nodoArtista = document.createElement("artista");
                        raiz.appendChild(nodoArtista);
                        //Creo y agrego cada nodo con su valor.
                        creaNodo(document,nodoArtista,"id", String.valueOf(dis.readInt()));
                        creaNodo(document,nodoArtista,"nombre",dis.readUTF());
                        creaNodo(document,nodoArtista,"cache", String.valueOf(dis.readDouble()));
                        creaNodo(document,nodoArtista,"edad", String.valueOf(dis.readInt()));
                        char type = dis.readChar();
                        String valor;
                        if(type=='s')
                            valor="Solista";
                        else
                            valor="Banda";
                        creaNodo(document,nodoArtista,"tipo",valor);

                        //Se escribe en el fichero
                        Source source = new DOMSource(document);
                        Result resultado = new StreamResult(new File("files/"+fileName));
                        try
                        {
                            Transformer transformer = TransformerFactory.newInstance().newTransformer(); //Creamos una nueva instancia de Transformer.
                            transformer.setOutputProperty("indent","yes"); //Definimos la propiedad de tabulaciones y espacios.
                            transformer.transform(source,resultado); // Transformamos la source en el fichero resultado.
                        }
                        catch(TransformerException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    dis.close(); //Cierro el dataInputStream.
                }
                muestraMensaje("El fichero "+fileName+" se ha generado correctamente.");
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        else
            muestraMensaje("No existen registros a exportar.");
    }

    public static boolean buscaDuplicados(int idConsulta)
    {
        try(RandomAccessFile raf = new RandomAccessFile(f0,"rw"))
        {
            //Me posiciono.
            raf.seek((idConsulta-1)*SIZE);
            int idLeido = raf.readInt();
            if(idConsulta == idLeido)
                return true;
            else
            {
                if(f1.exists()) //Se comprueba si el fichero de sinónimos existe. En tal caso se comprueba si hay coincidencias.
                {
                    try(DataInputStream dis = new DataInputStream(Files.newInputStream(f1.toPath())))
                    {
                        while(dis.available()>0)
                        {
                            idLeido = dis.readInt();
                            if(idLeido==idConsulta)
                                return true;
                        }
                    }
                    catch(EOFException ignored)
                    {

                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch(EOFException ignored)
        {
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static void creaNodo(Document document,Element nodoArtista,String nombre, String valor)
    {
        Element nodoDatos = document.createElement(nombre);
        nodoArtista.appendChild(nodoDatos);
        Text texto = document.createTextNode(valor); //Obtenemos el valor del id en texto.
        nodoDatos.appendChild(texto);
    }

    public static String recortaNombre(String artistName)
    {
        return artistName.substring(0,Math.min(artistName.length(),MAXSTRING));
    }

    public static void muestraMensaje(String mensaje)
    {
        System.out.println("\n---------------------------");
        System.out.println(mensaje);
    }

    public static boolean esNumerico(String cadena){
        try
        {
            Integer.parseInt(cadena);
            return true;
        }
        catch(NumberFormatException nfe){
            return false;
        }
    }

    public static void imprimir(int id, String artistName,double cache,int age, char type)
    {
        System.out.println("ID: "+id);
        System.out.println("Nombre: "+artistName);
        System.out.println("Caché: "+cache+" €");
        System.out.println("Edad: "+age);
        System.out.print("Solista o banda: ");
        if(type=='b')
            System.out.println("Banda");
        else
            System.out.println("Solista");
        System.out.println("---------------------------");
    }

    public static void muestraArtista(int idMuestra)
    {
        //Limpio el número de coincidencias
        coincidencias=0;
        String artistName; int id,age; char type;double cache;
        try
        {
            if(f0.exists()) //Si el fichero principal existe
            {
                RandomAccessFile raf = new RandomAccessFile(f0,"rw");
                raf.seek(SIZE*(idMuestra-1));
                id = raf.readInt();

                //Imprimo título del resultado.
                System.out.println("\nRESULTADO DE LA BÚSQUEDA");
                System.out.println("---------------------------");

                if(id==idMuestra) //Si el artista existe en el raf.
                {
                    artistName = raf.readUTF();
                    cache = raf.readDouble();
                    age = raf.readInt();
                    type = raf.readChar();

                    //Imprimo el resultado.
                    imprimir(id,artistName,cache,age,type);
                    coincidencias++;
                }
            }
            if(f1.exists()) //Si el fichero de sinónimos existe.
            {
                DataInputStream dis = new DataInputStream(new FileInputStream(f1));
                while(dis.available()>0)
                {
                    id = dis.readInt();
                    artistName = dis.readUTF();
                    cache = dis.readDouble();
                    age = dis.readInt();
                    type = dis.readChar();
                    if(id==idMuestra)
                    {
                        imprimir(id,artistName,cache,age,type);
                    }
                    coincidencias++;
                }
                dis.close();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}