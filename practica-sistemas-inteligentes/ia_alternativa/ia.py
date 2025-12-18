historial_pokemon = {}

def elegir_mejor_ataque(atacante, defensor, tabla_efectividad):
    mejor_ataque = None
    mejor_puntuacion = -float('inf')

    for ataque in atacante['Ataques']:
        efectividad = tabla_efectividad.loc[ataque['Tipo'], defensor['Tipo1']]
        if defensor['Tipo2'] != ' ':
            efectividad *= tabla_efectividad.loc[ataque['Tipo'], defensor['Tipo2']]

        # Ponderar por potencia y precisión
        puntuacion = efectividad * ataque['Potencia'] * (ataque['Precision'] / 100)
        if puntuacion > mejor_puntuacion:
            mejor_ataque = ataque
            mejor_puntuacion = puntuacion

    return mejor_ataque


def calcular_puntuacion_pokemon(pokemon, rival, tabla_efectividad):
    puntuacion = 0

    # Evaluar la efectividad del tipo del Pokémon contra el rival
    efectividad_tipo1 = tabla_efectividad.loc[pokemon['Tipo1'], rival['Tipo1']]
    if rival['Tipo2'] != ' ':
        efectividad_tipo1 *= tabla_efectividad.loc[pokemon['Tipo1'], rival['Tipo2']]

    efectividad_tipo2 = 1
    if pokemon['Tipo2'] != ' ':
        efectividad_tipo2 = tabla_efectividad.loc[pokemon['Tipo2'], rival['Tipo1']]
        if rival['Tipo2'] != ' ':
            efectividad_tipo2 *= tabla_efectividad.loc[pokemon['Tipo2'], rival['Tipo2']]

    # Promedio de efectividades
    efectividad_total = (efectividad_tipo1 + efectividad_tipo2) / (2 if pokemon['Tipo2'] else 1)
    puntuacion += efectividad_total * 100  # Ponderación base para efectividad

    # Evaluar la efectividad de los ataques del Pokémon
    for ataque in pokemon['Ataques']:
        efectividad_ataque = tabla_efectividad.loc[ataque['Tipo'], rival['Tipo1']]
        if rival['Tipo2'] != ' ':
            efectividad_ataque *= tabla_efectividad.loc[ataque['Tipo'], rival['Tipo2']]

        probabilidad_exito = ataque['Precision'] / 100
        puntuacion += efectividad_ataque * ataque['Potencia'] * probabilidad_exito * 0.2  # Ponderación por ataque

    # Ponderar estadísticas
    puntuacion += pokemon['Ataque'] * 0.3  # Peso para ataque físico
    puntuacion += pokemon['Ataque_Especial'] * 0.3  # Peso para ataque especial
    puntuacion += pokemon['Velocidad'] * 0.2  # Peso para velocidad
    puntuacion += pokemon['Defensa'] * 0.1  # Peso para defensa física
    puntuacion += pokemon['Defensa_Especial'] * 0.1  # Peso para defensa especial

    debilidad_tipo1 = tabla_efectividad.loc[rival['Tipo1'], pokemon['Tipo1']]
    if pokemon['Tipo2'] != ' ':
        debilidad_tipo1 *= tabla_efectividad.loc[rival['Tipo1'], pokemon['Tipo2']]

    debilidad_tipo2 = 1
    if rival['Tipo2'] != ' ':
        debilidad_tipo2 = tabla_efectividad.loc[rival['Tipo2'], pokemon['Tipo1']]
        if pokemon['Tipo2'] != ' ':
            debilidad_tipo2 *= tabla_efectividad.loc[rival['Tipo2'], pokemon['Tipo2']]

    debilidad_total = (debilidad_tipo1 + debilidad_tipo2) / (2 if rival['Tipo2'] else 1)
    puntuacion -= debilidad_total * 100  # Penalización con peso de 50

    return puntuacion


def seleccionar_pokemon_estrategico(rival, tabla_efectividad, pokemon_lista):

    mejor_pokemon = None
    mejor_puntuacion = -float('inf')

    # Revisar si el rival está en el historial
    if rival['Nombre'] in historial_pokemon:
        candidatos = historial_pokemon[rival['Nombre']]
        # Priorizar Pokémon que han funcionado bien contra este rival
        for pokemon in candidatos:
            if pokemon in pokemon_lista:  # Asegurarse de que el Pokémon esté disponible
                return pokemon

    # Evaluar todos los Pokémon si no hay historial o candidatos válidos
    for pokemon in pokemon_lista:
        puntuacion = calcular_puntuacion_pokemon(pokemon, rival, tabla_efectividad)
        if puntuacion > mejor_puntuacion:
            mejor_pokemon = pokemon
            mejor_puntuacion = puntuacion

    return mejor_pokemon
