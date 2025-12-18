import random
import pandas as pd
from src.ia.modificar_tabla_tipos import *
from src.fight.carga_datos import *
from src.fight.pokemon import *

# Fórmula de cálculo de daño
def calcular_daño(nivel, ataque, poder, defensa, bonificacion, efectividad):
    daño = 0.01 * bonificacion * efectividad * ((((0.2 * nivel) + 1) * ataque * poder) / ((25 * defensa) + 2))
    return round(daño)


def simular_combate(pokemon_ia, pokemon_rival, tabla_tipos_individuo, delta=0.1):
    nivel = 50
    atacante, defensor = (pokemon_ia, pokemon_rival) if pokemon_ia.speed > pokemon_rival.speed else (
    pokemon_rival, pokemon_ia)

    max_turnos = 100  # Limitar el combate a un máximo de 1000 turnos
    turnos = 0

    while True:
        turnos += 1
        if turnos > max_turnos:
            #print("Combate finalizado por límite de turnos.")
            return 'Empate'

        ataque = atacante.elegir_ataque_aleatorio()
        tipo_ataque = ataque.typ
        poder = ataque.power
        categoria = ataque.category
        ataque_valor = atacante.attack if categoria == 'Physical' else atacante.sp_attack
        defensa_valor = defensor.defense if categoria == 'Physical' else defensor.sp_defense
        bonificacion = 1.5 if tipo_ataque in [atacante.type1, atacante.type2] else 1

        # Calcular daño esperado utilizando la tabla aprendida
        efectividad_esperada = obtener_efectividad(tipo_ataque, defensor.type1, tabla_tipos_individuo)
        if defensor.type2 != '':
            efectividad_esperada *= obtener_efectividad(tipo_ataque, defensor.type2, tabla_tipos_individuo)

        daño_esperado = calcular_daño(nivel, ataque_valor, poder, defensa_valor, bonificacion, efectividad_esperada,)

        # Calcular daño real utilizando la tabla real
        efectividad_real = tabla_tipos.loc[tipo_ataque, defensor.type1]
        if defensor.type2 != ' ':
            efectividad_real *= tabla_tipos.loc[tipo_ataque, defensor.type2]
        daño_real = calcular_daño(nivel, ataque_valor, poder, defensa_valor, bonificacion, efectividad_real)

        # Comparar daño real con esperado
        if daño_real > daño_esperado:
            # El ataque es más fuerte de lo que se esperaba, aumentar efectividad
            actualizar_efectividad(tipo_ataque, defensor.type1, 2, tabla_tipos_individuo)
            if defensor.type2 != '':
                actualizar_efectividad(tipo_ataque, defensor.type2, 2, tabla_tipos_individuo)
        elif daño_real < daño_esperado:
            # El ataque es más débil de lo que se esperaba, disminuir efectividad
            actualizar_efectividad(tipo_ataque, defensor.type1, 0.5, tabla_tipos_individuo)
            if defensor.type2 != '':
                actualizar_efectividad(tipo_ataque, defensor.type2, 0.5, tabla_tipos_individuo)

        # Aplicar daño real al defensor
        defensor.hp_max -= daño_real

        # Verificar si el defensor fue derrotado
        if defensor.hp_max <= 0:
            return 'IA' if defensor == pokemon_rival else 'Rival'

        # Cambiar roles atacante-defensor
        atacante, defensor = defensor, atacante

def comparar_tablas(tabla_tipos_individuo):
    coincidencias = 0
    total = 0

    # Iterar por todos los tipos en filas y columnas
    for tipo_ataque in tabla_tipos.index:
        for tipo_defensor in tabla_tipos.columns:
            valor_real = tabla_tipos.loc[tipo_ataque, tipo_defensor]
            valor_aprendido = obtener_efectividad(tipo_ataque, tipo_defensor, tabla_tipos_individuo)

            # Contar como coincidencia si está dentro del margen de ±0.25
            if abs(valor_real - valor_aprendido) <= 0.1:
                coincidencias += 1
            total += 1

    porcentaje_aprendido = (coincidencias / total) * 100
    return porcentaje_aprendido

def ejecutar_simulaciones(n_simulaciones, pokemon_individuo, tabla_tipos_individuo):
    victorias_ia = 0
    for _ in range(n_simulaciones):
        pokemon_rival = elegir_pokemon_aleatorio(dataframe_p,dataframe_a)
        pokemon_ia = pokemon_individuo
        ganador = simular_combate(pokemon_ia, pokemon_rival, tabla_tipos_individuo)
        if ganador == 'IA':
            victorias_ia += 1






