package app.data.models.TipoActividad;

import app.data.models.Actividad;

public class TipoGenerica extends Actividad {

    public static Builder builder() {
        return new Builder();
    }

    public Float aplicarDescuentoActividadUsuario(int edad) {
        return super.getCoste();
    }

    public static class Builder {
        private final TipoGenerica tipoGenerica;

        public Builder() {
            this.tipoGenerica = new TipoGenerica();
        }

        public Builder nombre(String nombre) {
            this.tipoGenerica.setNombre(nombre);
            return this;
        }

        public Builder descripcion(String descripcion) {
            this.tipoGenerica.setDescripcion(descripcion);
            return this;
        }

        public Builder duracion(Integer duracion) {
            this.tipoGenerica.setDuracion(duracion);
            return this;
        }

        public Builder coste(Float coste) {
            this.tipoGenerica.setCoste(coste);
            return this;
        }

        public Builder aforo(Integer aforo) {
            this.tipoGenerica.setAforo(aforo);
            return this;
        }

        public TipoGenerica build() {
            return this.tipoGenerica;
        }
    }
}
