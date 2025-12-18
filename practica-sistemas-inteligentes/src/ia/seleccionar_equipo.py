from src.fight.carga_datos import *
from src.ia.estrategias import *
from src.fight.pokemon import *

def calcular_fitness_pokemon(pokemon, rival, tabla_tipos, estrategia):
    # Aqui escogemos la estrategia
    ataque_p = seleccionar_estrategia(estrategia, pokemon, rival, tabla_tipos)
    dano_p = calcular_dano(pokemon, rival, ataque_p, tabla_tipos)

    # Suponemos que el rival siempre hará el máximo daño
    ataque_r = estrategia_mejor_dano(rival, pokemon, tabla_tipos)
    dano_r = calcular_dano(rival, pokemon, ataque_r, tabla_tipos)

    turnos_para_vencer = rival.hp_max / dano_p if dano_p > 0 else 1000000000
    turnos_para_ser_vencido = pokemon.hp_max / dano_r if dano_r > 0 else 1000000000

    return 1 / turnos_para_vencer if turnos_para_vencer + 1 <= turnos_para_ser_vencido else 0

def calcular_fitness_equipo(equipo_usuario, equipo_ia, tabla_tipos, estrategia):
    contador = 0
    for pokemon, pokemon_ia in zip(equipo_usuario, equipo_ia):
        contador += calcular_fitness_pokemon(pokemon_ia, pokemon, tabla_tipos, estrategia)
    return contador

def generar_equipo_aleatorio():
    return [crear_pokemon_por_id(dataframe_p, dataframe_a, random.randint(1, 83)) for _ in range(6)]


def algoritmo_genetico(equipo_usuario, tabla_tipos, estrategia, generaciones=100, num_individuos=100):
    # Crear la población inicial de 83 Pokémon
    poblacion = []

    for i in range(num_individuos):
        poblacion.append(generar_equipo_aleatorio())

    for generacion in range(generaciones):
        # Evaluar fitness
        fitness = [calcular_fitness_equipo(equipo_usuario, equipo_ia, tabla_tipos, estrategia) for equipo_ia in poblacion]

        # Seleccionar el peor
        peor_indice = min(range(len(fitness)), key=lambda i: fitness[i])
        peor_equipo = poblacion[peor_indice]


        poblacion.remove(peor_equipo)
        poblacion.append(generar_equipo_aleatorio())

    # Evaluar de nuevo el fitness para los Pokémon de la población
    fitness = [calcular_fitness_equipo(equipo_usuario, equipo_ia, tabla_tipos, estrategia) for equipo_ia in poblacion]


    mejor_indice_final = max(range(len(fitness)), key=lambda i: fitness[i])
    mejor_equipo_final = poblacion[mejor_indice_final]

    print("Mejor equipo IA: ", mejor_equipo_final[0].nombre, mejor_equipo_final[1].nombre, mejor_equipo_final[2].nombre, mejor_equipo_final[3].nombre, mejor_equipo_final[4].nombre, mejor_equipo_final[5].nombre)
    return mejor_equipo_final


# Ejemplo de uso
if __name__ == "__main__":
    # Seleccionar un Pokémon rival
    equipo = generar_equipo_aleatorio()

    estrategia = 'mejor_dano'
    algoritmo_genetico(equipo, tabla_tipos, estrategia)
    print("Mi equipo:", equipo[0].nombre, equipo[1].nombre, equipo[2].nombre, equipo[3].nombre,
          equipo[4].nombre, equipo[5].nombre)


