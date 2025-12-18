import random
import pandas as pd
from tipo_learning import actualizar_efectividad, obtener_efectividad, guardar_tabla_efectividad, cargar_tabla_efectividad
from carga_datos import pokemon_lista, efectividad_tipos_df
from ia import historial_pokemon, seleccionar_pokemon_estrategico, elegir_mejor_ataque

# Limpiar los nombres de tipos en la tabla real (eliminar espacios extra)
efectividad_tipos_df.columns = efectividad_tipos_df.columns.str.strip()
efectividad_tipos_df.index = efectividad_tipos_df.index.str.strip()


# Fórmula de cálculo de daño
def calcular_dano(nivel, ataque, poder, defensa, bonificacion, efectividad, variacion):
    dano = 0.01 * bonificacion * efectividad * variacion * (((0.2 * nivel + 1) * ataque * poder) / ((25 * defensa) + 2))
    return round(dano)


def simular_combate(pokemon_ia, pokemon_rival):
    nivel = 50
    atacante, defensor = (pokemon_ia, pokemon_rival) if pokemon_ia['Velocidad'] > pokemon_rival['Velocidad'] else (pokemon_rival, pokemon_ia)

    max_turnos = 1000  # Limitar el combate a un máximo de 1000 turnos
    turnos = 0

    while True:
        turnos += 1
        if turnos > max_turnos:
            print("Combate finalizado por límite de turnos.")
            return 'Empate'

        if atacante == pokemon_ia:
            ataque = elegir_mejor_ataque(atacante, defensor, cargar_tabla_efectividad())
        else:
            ataque = random.choice(atacante['Ataques'])

        tipo_ataque = ataque['Tipo']
        poder = ataque['Potencia']
        categoria = ataque['Categoria']
        ataque_valor = atacante['Ataque'] if categoria == 'Physical' else atacante['Ataque_Especial']
        defensa_valor = defensor['Defensa'] if categoria == 'Physical' else defensor['Defensa_Especial']
        bonificacion = 1.5 if tipo_ataque in [atacante['Tipo1'], atacante['Tipo2']] else 1
        variacion = random.randint(85, 100) / 100


        # Calcular daño
        efectividad = efectividad_tipos_df.loc[tipo_ataque, defensor['Tipo1']]
        if defensor['Tipo2'] != ' ':
            efectividad *= efectividad_tipos_df.loc[tipo_ataque, defensor['Tipo2']]
        dano = calcular_dano(nivel, ataque_valor, poder, defensa_valor, bonificacion, efectividad, variacion)

        # Aplicar daño al defensor
        defensor['HP'] -= dano

        # Verificar si el defensor fue derrotado
        if defensor['HP'] <= 0:
            if atacante == pokemon_ia:
                historial_pokemon.setdefault(defensor['Nombre'], []).append(atacante['Nombre'])
            return 'IA' if defensor == pokemon_rival else 'Rival'

        atacante, defensor = defensor, atacante

def ejecutar_simulaciones(n_simulaciones):
    victorias_ia = 0
    for _ in range(n_simulaciones):
        pokemon_rival = random.choice(pokemon_lista)
        pokemon_ia = seleccionar_pokemon_estrategico(pokemon_rival, cargar_tabla_efectividad(), pokemon_lista)
        ganador = simular_combate(pokemon_ia, pokemon_rival)
        if ganador == 'IA':
            victorias_ia += 1

    print(f"Victorias IA: {victorias_ia}/{n_simulaciones} ({(victorias_ia / n_simulaciones) * 100:.2f}%)")

if __name__ == "__main__":
    ejecutar_simulaciones(300)