import random
from data_loader import cargar_datos


def generar_equipos_rivales(num_equipos=7):
    """
    Genera equipos rivales para un torneo.

    Parámetros:
    - num_equipos (int): Número de equipos a generar (por defecto, 7).

    Retorna:
    - equipos (list): Lista de equipos, donde cada equipo es una lista de Pokémon.
    """
    # Cargar los datos de Pokémon
    _, pokemon_lista, _ = cargar_datos()

    # Obtener los tipos únicos presentes en los Pokémon
    tipos_disponibles = set(pokemon['Tipo1'] for pokemon in pokemon_lista)

    # Filtrar Pokémon de tipo Normal como reserva
    pokemon_normales = [pokemon for pokemon in pokemon_lista if pokemon['Tipo1'] == 'Normal']

    if not pokemon_normales:
        raise ValueError("No hay Pokémon de tipo Normal para completar los equipos.")

    equipos = []

    for _ in range(num_equipos):
        # Seleccionar un tipo principal aleatorio para el equipo
        tipo_principal = random.choice(list(tipos_disponibles))

        # Filtrar los Pokémon que tienen este tipo como Tipo1
        pokemon_filtrados = [pokemon for pokemon in pokemon_lista if pokemon['Tipo1'] == tipo_principal]

        # Seleccionar los Pokémon del tipo principal
        equipo = pokemon_filtrados[:]

        # Completar con Pokémon de tipo Normal si faltan
        while len(equipo) < 6:
            # Añadir Pokémon de tipo Normal aleatorios que no estén ya en el equipo
            normal_a_agregar = random.choice(pokemon_normales)
            if normal_a_agregar not in equipo:
                equipo.append(normal_a_agregar)

        # Elegir aleatoriamente 6 Pokémon si hay más
        equipo = random.sample(equipo, 6)

        equipos.append(equipo)

    return equipos


# Prueba de generación de equipos
if __name__ == "__main__":
    equipos_rivales = generar_equipos_rivales()
    for i, equipo in enumerate(equipos_rivales):
        print(f"Equipo {i + 1}:")
        for pokemon in equipo:
            print(f"  - {pokemon['Nombre']} (Tipo: {pokemon['Tipo1']})")




