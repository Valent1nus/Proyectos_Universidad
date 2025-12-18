import pandas as pd


def actualizar_efectividad(tipo_ataque, tipo_defensor, resultado_efectividad, tabla_tipos_individuo, alpha=0.05):
    if tipo_ataque in tabla_tipos_individuo.index and tipo_defensor in tabla_tipos_individuo.columns:
        valor_anterior = tabla_tipos_individuo.loc[tipo_ataque, tipo_defensor]
        nuevo_valor = alpha * resultado_efectividad + (1 - alpha) * valor_anterior
        tabla_tipos_individuo.loc[tipo_ataque, tipo_defensor] = nuevo_valor


def obtener_efectividad(tipo_ataque, tipo_defensor, tabla_tipos_individuo):
    if tipo_ataque in tabla_tipos_individuo.index and tipo_defensor in tabla_tipos_individuo.columns:
        return float(tabla_tipos_individuo.loc[tipo_ataque, tipo_defensor])
    return 1.0  # Por defecto, neutro


def guardar_tabla_efectividad(tabla_tipos_individuo, ruta="tabla_tipos_aprendida.csv"):
    tabla_tipos_individuo.round(2).to_csv(ruta)


def cargar_tabla_efectividad(ruta="tabla_tipos_aprendida.csv"):
    global tabla_tipos
    tabla_tipos = pd.read_csv(ruta, index_col=0)
    tabla_tipos = tabla_tipos.astype(float)
    return tabla_tipos
