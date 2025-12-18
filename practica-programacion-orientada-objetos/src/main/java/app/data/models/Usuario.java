package app.data.models;

import app.AppException.StringLengthException;
import app.data.models.ModelsExceptions.ForbiddenArgumentException;

public class Usuario {
    private static final int MINEDAD = 14;
    private static final int MAXEDAD = 100;
    private static final int MINCONTRASENIA = 3;
    private final String nombre;
    private Integer edad;
    private final Integer movil;
    private String contrasenia;
    private Integer id;


    public Usuario(String nombre, Integer edad, Integer movil, String contrasenia) {
        this.nombre = nombre;
        this.setEdad(edad);
        this.movil = movil;
        this.setContrasenia(contrasenia);
    }

    public boolean comprobarSolapamientoDosPlanes(Plan plan1, Plan plan2) {
        boolean solapamiento = true;
        if (plan1.getFechaYHora().isBefore(plan2.getFechaYHora())) {
            solapamiento = !plan1.getFechaYHora().plusMinutes(plan1.calcularTiempoPlan()).isBefore(plan2.getFechaYHora());
        } else if (plan1.getFechaYHora().isAfter(plan2.getFechaYHora())) {
            solapamiento = !plan1.getFechaYHora().isAfter(plan2.getFechaYHora().plusDays(plan2.calcularTiempoPlan()));
        } else if (plan1.getFechaYHora().isEqual(plan2.getFechaYHora())) {
            solapamiento = true;
        }
        return solapamiento;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        if (edad < MINEDAD || edad > MAXEDAD) {
            throw new ForbiddenArgumentException("No cumple los estándares de edad (entre 14 y 100 años): " + edad);
        }
        this.edad = edad;
    }

    public Integer getMovil() {
        return movil;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        if (contrasenia.length() < MINCONTRASENIA) {
            throw new StringLengthException("No cumple los estándares de contrasenias (mas de 3 caracteres): " + contrasenia);
        }
        this.contrasenia = contrasenia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", edad=" + edad +
                ", movil=" + movil +
                ", contrasenia='" + contrasenia + '\'' +
                '}';
    }
}
