package app.data.models.TipoActividad;

import app.data.models.Actividad;

public class TipoCine extends Actividad {
    private static final int EDAD_JOVEN = 21;
    private static final float DESCUENTO = 0.5f;

    public static Builder builder() {
        return new Builder();
    }

    public Float aplicarDescuentoActividadUsuario(int edad) {
        float cantidadDescuento = super.getCoste();
        if (edad <= EDAD_JOVEN) {
            cantidadDescuento *= DESCUENTO;
        }
        return cantidadDescuento;
    }

    public static class Builder {
        private final TipoCine tipoCine;

        public Builder() {
            this.tipoCine = new TipoCine();
        }

        public Builder nombre(String nombre) {
            this.tipoCine.setNombre(nombre);
            return this;
        }

        public Builder descripcion(String descripcion) {
            this.tipoCine.setDescripcion(descripcion);
            return this;
        }

        public Builder duracion(Integer duracion) {
            this.tipoCine.setDuracion(duracion);
            return this;
        }

        public Builder coste(Float coste) {
            this.tipoCine.setCoste(coste);
            return this;
        }

        public Builder aforo(Integer aforo) {
            this.tipoCine.setAforo(aforo);
            return this;
        }

        public TipoCine build() {
            return this.tipoCine;
        }
    }
}
