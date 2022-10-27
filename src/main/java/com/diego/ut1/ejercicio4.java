package com.diego.ut1;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;

public class ejercicio4
{
    public static void main(String[] args)
    {
        //Escribe XML.
        generaXML();
        //Lee XML.
        leeXML();
    }
    public static void generaXML()
    {
        try
        {
            //Creamos los objetos lenguajes.
            Artista a1 = new Artista(1,"Diego Calatayud",123.4,19,'B');
            Artista a2 = new Artista(1,"Javier Egido",341.2,19,'S');
            Artista a3 = new Artista(1,"Arturo Collados",409,38,'S');
            Artistas artistas = new Artistas();
            artistas.agregaArtista(a1);
            artistas.agregaArtista(a2);
            artistas.agregaArtista(a3);
            //Creamos los objetos necesarios para la escritura.
            JAXBContext context = JAXBContext.newInstance(artistas.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
            marshaller.marshal(artistas,new File("files/artistasEj4.xml"));

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static void leeXML()
    {
        try
        {
            Artistas artistas;
            JAXBContext context = JAXBContext.newInstance(Artistas.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            artistas = (Artistas) unmarshaller.unmarshal(new File("files/artistasEj4.xml"));
            artistas.muestraArtistas();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}