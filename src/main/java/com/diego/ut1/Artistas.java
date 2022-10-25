package com.diego.ut1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Artistas
{
    private ArrayList<Artista> artistas = null;

    public Artistas()
    {
        artistas = new ArrayList<>();
    }

    public void agregaArtista(Artista a)
    {
        artistas.add(a);
    }

    public void muestraArtistas()
    {
        for(Artista a: artistas)
            a.muestraArtista();
    }
}
