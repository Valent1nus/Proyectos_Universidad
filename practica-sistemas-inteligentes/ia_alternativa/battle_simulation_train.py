import random
import pandas as pd
from tipo_learning import actualizar_efectividad, obtener_efectividad, guardar_tabla_efectividad
from carga_datos import pokemon_lista, efectividad_tipos_df

# Limpiar los nombres de tipos en la tabla real (eliminar espacios extra)
efectividad_tipos_df.columns = efectividad_tipos_df.columns.str.strip()
efectividad_tipos_df.index = efectividad_tipos_df.index.str.strip()


# Fórmula de cálculo de daño
def calcular_daño(nivel, ataque, poder, defensa, bonificacion, efectividad):
    daño = 0.01 * bonificacion * efectividad * ((((0.2 * nivel) + 1) * ataque * poder) / ((25 * defensa) + 2))
    return round(daño)


def simular_combate(pokemon_ia, pokemon_rival, delta=0.1):
    nivel = 50
    atacante, defensor = (pokemon_ia, pokemon_rival) if pokemon_ia['Velocidad'] > pokemon_rival['Velocidad'] else (
    pokemon_rival, pokemon_ia)

    max_turnos = 1000  # Limitar el combate a un máximo de 1000 turnos
    turnos = 0

    while True:
        turnos += 1
        if turnos > max_turnos:
            print("Combate finalizado por límite de turnos.")
            return 'Empate'

        ataque = random.choice(atacante['Ataques'])
        tipo_ataque = ataque['Tipo']
        poder = ataque['Potencia']
        categoria = ataque['Categoria']
        ataque_valor = atacante['Ataque'] if categoria == 'Physical' else atacante['Ataque_Especial']
        defensa_valor = defensor['Defensa'] if categoria == 'Physical' else defensor['Defensa_Especial']
        bonificacion = 1.5 if tipo_ataque in [atacante['Tipo1'], atacante['Tipo2']] else 1

        # Calcular daño esperado utilizando la tabla aprendida
        efectividad_esperada = obtener_efectividad(tipo_ataque, defensor['Tipo1'])
        if defensor['Tipo2'] != '':
            efectividad_esperada *= obtener_efectividad(tipo_ataque, defensor['Tipo2'])

        daño_esperado = calcular_daño(nivel, ataque_valor, poder, defensa_valor, bonificacion, efectividad_esperada,)

        # Calcular daño real utilizando la tabla real
        efectividad_real = efectividad_tipos_df.loc[tipo_ataque, defensor['Tipo1']]
        if defensor['Tipo2'] != ' ':
            efectividad_real *= efectividad_tipos_df.loc[tipo_ataque, defensor['Tipo2']]
        daño_real = calcular_daño(nivel, ataque_valor, poder, defensa_valor, bonificacion, efectividad_real)

        # Comparar daño real con esperado
        if daño_real > daño_esperado:
            # El ataque es más fuerte de lo que se esperaba, aumentar efectividad
            actualizar_efectividad(tipo_ataque, defensor['Tipo1'], 2)
            if defensor['Tipo2'] != '':
                actualizar_efectividad(tipo_ataque, defensor['Tipo2'], 2)
        elif daño_real < daño_esperado:
            # El ataque es más débil de lo que se esperaba, disminuir efectividad
            actualizar_efectividad(tipo_ataque, defensor['Tipo1'], 0.5)
            if defensor['Tipo2'] != '':
                actualizar_efectividad(tipo_ataque, defensor['Tipo2'], 0.5)

        # Aplicar daño real al defensor
        defensor['HP'] -= daño_real

        # Verificar si el defensor fue derrotado
        if defensor['HP'] <= 0:
            return 'IA' if defensor == pokemon_rival else 'Rival'

        # Cambiar roles atacante-defensor
        atacante, defensor = defensor, atacante

def comparar_tablas():
    coincidencias = 0
    total = 0

    # Iterar por todos los tipos en filas y columnas
    for tipo_ataque in efectividad_tipos_df.index:
        for tipo_defensor in efectividad_tipos_df.columns:
            valor_real = efectividad_tipos_df.loc[tipo_ataque, tipo_defensor]
            valor_aprendido = obtener_efectividad(tipo_ataque, tipo_defensor)

            # Contar como coincidencia si está dentro del margen de ±0.25
            if abs(valor_real - valor_aprendido) <= 0.1:
                coincidencias += 1
            total += 1

    porcentaje_aprendido = (coincidencias / total) * 100
    return porcentaje_aprendido

def ejecutar_simulaciones(n_simulaciones):
    victorias_ia = 0
    for _ in range(n_simulaciones):
        pokemon_rival = random.choice(pokemon_lista)
        pokemon_ia = random.choice(pokemon_lista)
        ganador = simular_combate(pokemon_ia, pokemon_rival)
        if ganador == 'IA':
            victorias_ia += 1

    print(f"Victorias IA: {victorias_ia}/{n_simulaciones} ({(victorias_ia / n_simulaciones) * 100:.2f}%)")
    # Comparar las tablas y mostrar el porcentaje aprendido
    porcentaje_aprendido = comparar_tablas()
    print(f"Porcentaje de efectividades aprendidas correctamente (±0.1): {porcentaje_aprendido:.2f}%")

    guardar_tabla_efectividad()


if __name__ == "__main__":
    ejecutar_simulaciones(10000000)





