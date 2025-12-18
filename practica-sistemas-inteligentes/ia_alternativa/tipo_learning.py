import pandas as pd

# Inicializar tabla de efectividad con valores tipo float64
tipos = ['Normal', 'Fighting', 'Flying', 'Poison', 'Ground', 'Rock', 'Bug', 'Fire',
         'Water', 'Grass', 'Electric', 'Psychic', 'Ice', 'Dragon']
efectividad_aprendida = pd.DataFrame(1.0, index=tipos, columns=tipos, dtype=float)


def actualizar_efectividad(tipo_ataque, tipo_defensor, resultado_efectividad, alpha=0.05):
    if tipo_ataque in efectividad_aprendida.index and tipo_defensor in efectividad_aprendida.columns:
        valor_anterior = efectividad_aprendida.loc[tipo_ataque, tipo_defensor]
        nuevo_valor = alpha * resultado_efectividad + (1 - alpha) * valor_anterior
        efectividad_aprendida.loc[tipo_ataque, tipo_defensor] = nuevo_valor


def obtener_efectividad(tipo_ataque, tipo_defensor):
    if tipo_ataque in efectividad_aprendida.index and tipo_defensor in efectividad_aprendida.columns:
        return float(efectividad_aprendida.loc[tipo_ataque, tipo_defensor])
    return 1.0  # Por defecto, neutro


def guardar_tabla_efectividad(ruta="efectividad_aprendida.csv"):
    efectividad_aprendida.round(2).to_csv(ruta)



def cargar_tabla_efectividad(ruta="efectividad_aprendida.csv"):
    global efectividad_aprendida
    efectividad_aprendida = pd.read_csv(ruta, index_col=0)
    efectividad_aprendida = efectividad_aprendida.astype(float)
    return efectividad_aprendida
