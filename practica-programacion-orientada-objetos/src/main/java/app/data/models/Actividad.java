package app.data.models;

import app.data.models.ModelsExceptions.ForbiddenArgumentException;

public abstract class Actividad {
    private String nombre;
    private String descripcion;
    private Integer duracion;
    private Float coste;
    private Integer aforo;
    private Integer id;

    public Float aplicarDescuentoActividadUsuario(int edad) {
        return coste;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        if (duracion <= 0) {
            throw new ForbiddenArgumentException("No puedes meter duraciones negativos o con valor igual a cero");
        } else {
            this.duracion = duracion;
        }
    }

    public Integer getAforo() {
        return aforo;
    }

    public void setAforo(Integer aforo) {
        if (aforo == null) {
            this.aforo = Integer.MAX_VALUE;
        } else if (aforo <= 0) {
            throw new ForbiddenArgumentException("No puedes meter aforos negativos o con valor igual a cero");
        } else {
            this.aforo = aforo;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getCoste() {
        return coste;
    }

    public void setCoste(Float coste) {
        if (coste < 0) {
            throw new ForbiddenArgumentException("El coste no puede ser negativo");
        }
        this.coste = coste;
    }

    @Override
    public String toString() {
        return "Actividad{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", duracion=" + duracion +
                ", coste=" + coste +
                ", aforo=" + aforo +
                ", id=" + id + '\'' +
                '}';
    }
}
