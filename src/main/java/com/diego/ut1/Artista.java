package com.diego.ut1;

public class Artista
{
    private int id;
    private String nombre;
    private double cache;
    private int edad;
    private char tipo;

    public Artista() {}

    public Artista(int id, String nombre, double cache, int age, char type)
    {
        this.id = id;
        this.nombre = nombre;
        this.cache = cache;
        this.edad = age;
        this.tipo = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getCache() {
        return cache;
    }

    public void setCache(double cache) {
        this.cache = cache;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int age) {
        this.edad = age;
    }

    public char getTipo() {
        return tipo;
    }

    public void setTipo(char tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString()
    {
        return "Artista{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", cache=" + cache +
                ", edad=" + edad +
                ", tipo=" + tipo +
                '}';
    }

    public void muestraArtista()
    {
        System.out.println("-------------------------");
        System.out.println("ID: "+getId());
        System.out.println("Nombre: "+getNombre());
        System.out.println("Cache: "+getCache());
        System.out.println("Edad: "+getEdad());
        char type = getTipo();
        if(type=='s')
            System.out.println("Tipo: Solista");
        else
            System.out.println("Tipo: Banda");
    }
}
