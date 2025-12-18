import random
from team_generator import generar_equipos_rivales


def generar_cuadro_torneo(equipo_ia, equipos_rivales):
    # Asegurarnos de que hay 8 equipos (IA + 7 rivales)
    if len(equipos_rivales) != 7:
        raise ValueError("Debe haber exactamente 7 equipos rivales.")

    # Combinar el equipo de la IA con los equipos rivales
    todos_los_equipos = [{"nombre": "IA", "equipo": equipo_ia}] + [
        {"nombre": f"Rival {i + 1}", "equipo": equipo}
        for i, equipo in enumerate(equipos_rivales)
    ]

    # Mezclar los equipos de manera aleatoria
    random.shuffle(todos_los_equipos)

    # Generar el cuadro del torneo
    cuadro = []
    ronda_actual = todos_los_equipos

    while len(ronda_actual) > 1:
        siguiente_ronda = []
        for i in range(0, len(ronda_actual), 2):
            # Crear enfrentamientos por pares
            enfrentamiento = (ronda_actual[i], ronda_actual[i + 1])
            siguiente_ronda.append(enfrentamiento)
        cuadro.append(siguiente_ronda)
        ronda_actual = siguiente_ronda  # Preparar la siguiente ronda

    return cuadro, todos_los_equipos


def imprimir_cuadro(cuadro):
    for ronda_num, ronda in enumerate(cuadro, start=1):
        print(f"\nRonda {ronda_num}:")
        for enfrentamiento in ronda:
            equipo_1 = enfrentamiento[0]["nombre"]
            equipo_2 = enfrentamiento[1]["nombre"]
            print(f"  - {equipo_1} vs {equipo_2}")


def calcular_poder_equipo(equipo):
    return sum(pokemon["Poder"] for pokemon in equipo)


def combatir(equipo_1, equipo_2):
    poder_1 = calcular_poder_equipo(equipo_1["equipo"])
    poder_2 = calcular_poder_equipo(equipo_2["equipo"])

    print(f"Combate: {equipo_1['nombre']} (Poder: {poder_1}) vs {equipo_2['nombre']} (Poder: {poder_2})")

    if poder_1 > poder_2:
        print(f"Ganador: {equipo_1['nombre']}")
        return equipo_1
    elif poder_2 > poder_1:
        print(f"Ganador: {equipo_2['nombre']}")
        return equipo_2
    else:
        # Si hay empate, decidir ganador aleatoriamente
        ganador = random.choice([equipo_1, equipo_2])
        print(f"Empate. Ganador aleatorio: {ganador['nombre']}")
        return ganador


def jugar_torneo(cuadro):
    for ronda_num, ronda in enumerate(cuadro, start=1):
        print(f"\n=== Ronda {ronda_num} ===")
        siguiente_ronda = []
        for enfrentamiento in ronda:
            ganador = combatir(enfrentamiento[0], enfrentamiento[1])
            siguiente_ronda.append(ganador)
        cuadro[ronda_num - 1] = siguiente_ronda  # Actualizamos la ronda con los ganadores
        if len(siguiente_ronda) == 1:  # Si solo queda un ganador, termina el torneo
            return siguiente_ronda[0]
    return None  # Por si algo falla

# Prueba de generación del cuadro
if __name__ == "__main__":
    # Generar equipos rivales
    equipos_rivales = generar_equipos_rivales()

    # Generar un equipo de la IA
    equipo_ia = [{"Nombre": f"Pokemon_IA_{i+1}", "Poder": random.randint(50, 100)} for i in range(6)]

    # Generar cuadro
    cuadro, participantes = generar_cuadro_torneo(equipo_ia, equipos_rivales)

    # Jugar torneo
    ganador = jugar_torneo(cuadro)
    print(f"\n=== Ganador del Torneo: {ganador['nombre']} ===")
