package com.diego.ut1;

import jakarta.xml.bind.annotation.XmlElement;


public class Artista
{
    private int id;
    @XmlElement(name="nombre")
    private String nom;
    private double cache;
    private int age;
    private char type;

    public Artista() {}

    public Artista(int id, String nombre, double cache, int age, char type)
    {
        this.id = id;
        this.nom = nombre;
        this.cache = cache;
        this.age = age;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nom;
    }

    public void setNombre(String nombre) {
        this.nom = nombre;
    }

    public double getCache() {
        return cache;
    }

    public void setCache(double cache) {
        this.cache = cache;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "Artista{" +
                "id=" + id +
                ", nombre='" + nom + '\'' +
                ", cache=" + cache +
                ", age=" + age +
                ", type=" + type +
                '}';
    }

    public void muestraArtista()
    {
        System.out.println("--------------");
        System.out.println("ID: "+getId());
        System.out.println("Nombre: "+getNombre());
        System.out.println("Cache: "+getCache());
        System.out.println("Edad: "+getAge());
        char type = getType();
        if(type=='s')
            System.out.println("Tipo: Solista");
        else
            System.out.println("Tipo: Banda");
    }
}
