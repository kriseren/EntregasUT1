package com.diego.ut1;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ejercicio3
{
    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); //Creo el document factory.
    public static void main(String[] args) throws ParserConfigurationException {
        //Creo la carpeta files si no existe.
        File dirFile = new File("files");
        if(!dirFile.exists())
            dirFile.mkdir();

        System.out.println("[GENERADOR DE FICHERO XML]\nGenerando fichero artistas.xml...");
        write();
        read();
    }

    public static void read()
    {
        //Creo los objetos necesarios para la lectura.
        Document documentoEntrada;
        try
        {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            documentoEntrada = builder.parse(new File("files/artistasEj3.xml"));
        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            throw new RuntimeException(e);
        }

        //Procedo a leer
        System.out.println("\nRESULTADO DE LA LECTURA\n----------------------------------");
        NodeList artistas = documentoEntrada.getElementsByTagName("artista"); //Creo una lista de todos los nodos artista.
        for(int i=0;i<artistas.getLength();i++)
        {
            Node artista = artistas.item(i);
            Element elemento = (Element)artista;
            System.out.println("ID: "+elemento.getElementsByTagName("id").item(0).getTextContent());
            System.out.println("Nombre: "+elemento.getElementsByTagName("nombre").item(0).getTextContent());
            System.out.println("Cache: "+elemento.getElementsByTagName("cache").item(0).getTextContent());
            System.out.println("Edad: "+elemento.getElementsByTagName("edad").item(0).getTextContent());
            System.out.println("Tipo: "+elemento.getElementsByTagName("tipo").item(0).getTextContent());
            System.out.println("----------------------------------");
        }
    }

    public static void write() throws ParserConfigurationException {

        //Creo un array de objetos artista.
        ArrayList<Artista> artistas = new ArrayList<>();
        Artista a1 = new Artista(1,"Javier Egido",450.56,19,'s');
        Artista a2 = new Artista(2,"Ramón Egido",320,31,'b');
        Artista a3 = new Artista(3,"Arturo Collados",745.8,38,'s');
        Artista a4 = new Artista(4,"Diego Calatayud",598,19,'b');
        artistas.add(a1);artistas.add(a2);artistas.add(a3);artistas.add(a4);

        //Creo los objetos necesarios para la creación de un XML.
        DocumentBuilder db = dbf.newDocumentBuilder(); //Creo el document builder.
        DOMImplementation dom = db.getDOMImplementation(); //Creo un DOM Implementation
        Document document = dom.createDocument(null,"xml",null); //Creo el document.

        //Creo los nodos raíz y artistas.
        Element raiz = document.createElement("artistas");
        document.getDocumentElement().appendChild(raiz);
        //Voy creando el árbol a partir de raíz.
        for(Artista a: artistas)
        {
            Element nodoArtista = document.createElement("artista");
            raiz.appendChild(nodoArtista);
            creaNodo(document,nodoArtista,"id",String.valueOf(a.getId()));
            creaNodo(document,nodoArtista,"nombre",a.getNombre());
            creaNodo(document,nodoArtista,"cache",String.valueOf(a.getCache()));
            creaNodo(document,nodoArtista,"edad",String.valueOf(a.getEdad()));
            char type = a.getTipo();
            if(type=='s')
                creaNodo(document,nodoArtista,"tipo","Solista");
            else
                creaNodo(document,nodoArtista,"tipo","Banda");

        }

        //Escribo en el fichero.
        Source source = new DOMSource(document);
        Result resultado = new StreamResult(new File("files/artistasEj3.xml"));
        Transformer transformer = null; //Creamos una nueva instancia de Transformer.
        try
        {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("indent","yes"); //Definimos la propiedad de tabulaciones y espacios.
            transformer.transform(source,resultado); // Transformamos la source en el fichero resultado.
            System.out.println("\nRESULTADO DE LA ESCRITURA\n----------------------------------");
            System.out.println("Fichero generado correctamente");
        }
        catch (TransformerException e) {
            throw new RuntimeException(e);
        }

    }

    public static void creaNodo(Document document,Element nodoArtista,String nombre, String dato)
    {
        Element nodoDatos = document.createElement(nombre);
        Text valor = document.createTextNode(dato);
        nodoArtista.appendChild(nodoDatos);
        nodoDatos.appendChild(valor);
    }
}
