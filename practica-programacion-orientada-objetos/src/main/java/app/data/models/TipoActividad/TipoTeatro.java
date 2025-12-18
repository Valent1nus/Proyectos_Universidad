package app.data.models.TipoActividad;

import app.data.models.Actividad;

public class TipoTeatro extends Actividad {
    private static final int EDAD_JOVEN = 25;
    private static final int EDAD_MAYOR = 65;
    private static final float DESCUENTO_JOVEN = 0.5f;
    private static final float DESCUENTO_MAYOR = 0.7f;

    public static Builder builder() {
        return new Builder();
    }

    public Float aplicarDescuentoActividadUsuario(int edad) {
        float cantidadDescuento = super.getCoste();
        if (edad <= EDAD_JOVEN) {
            cantidadDescuento *= DESCUENTO_JOVEN;
        }
        if (edad >= EDAD_MAYOR) {
            cantidadDescuento -= cantidadDescuento * DESCUENTO_MAYOR;
        }
        return cantidadDescuento;
    }

    public static class Builder {
        private final TipoTeatro tipoTeatro;

        private Builder() {
            this.tipoTeatro = new TipoTeatro();
        }

        public Builder nombre(String nombre) {
            this.tipoTeatro.setNombre(nombre);
            return this;
        }

        public Builder descripcion(String descripcion) {
            this.tipoTeatro.setDescripcion(descripcion);
            return this;
        }

        public Builder duracion(Integer duracion) {
            this.tipoTeatro.setDuracion(duracion);
            return this;
        }

        public Builder coste(Float coste) {
            this.tipoTeatro.setCoste(coste);
            return this;
        }

        public Builder aforo(Integer aforo) {
            this.tipoTeatro.setAforo(aforo);
            return this;
        }

        public TipoTeatro build() {
            return this.tipoTeatro;
        }
    }
}
