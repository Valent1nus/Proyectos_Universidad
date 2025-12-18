from src.fight.turn import *
from src.fight.carga_datos import *
from src.fight.pokemon import *

def juego():
    jugador1 = {
        "nombre": "Héroe",
        "equipo": [elegir_pokemon_aleatorio(dataframe_p, dataframe_a) for _ in range(6)],
    }
    jugador2 = {
        "nombre": "IA malévola",
        "equipo": [elegir_pokemon_aleatorio(dataframe_p, dataframe_a) for _ in range(6)],
    }

    print(f"\n¡{jugador1['nombre']} y {jugador2['nombre']} están listos para el combate!")
    print(f"Equipo de {jugador1['nombre']}: {[p.nombre for p in jugador1['equipo']]}")
    print(f"Equipo de {jugador2['nombre']}: {[p.nombre for p in jugador2['equipo']]}")

    turno_actual, siguiente_turno = decidir_turno(jugador1, jugador2)

    while jugador1["equipo"] and jugador2["equipo"]:
        print(f"\nTurno de {turno_actual['nombre']} con {turno_actual['equipo'][0].nombre}")
        print(f"{siguiente_turno['nombre']} tiene a {siguiente_turno['equipo'][0].nombre}")

        if turno(turno_actual, siguiente_turno, tabla_tipos):
            print(f"\n¡{siguiente_turno['nombre']} perdió a {siguiente_turno['equipo'][0].nombre}!")
            siguiente_turno["equipo"].pop(0)  # Retirar al Pokémon derrotado
            if siguiente_turno["equipo"]:  # Si el jugador tiene más Pokémon
                turno_actual, siguiente_turno = decidir_turno(turno_actual, siguiente_turno)
            continue  # Saltar el intercambio de turnos para respetar la nueva decisión

        turno_actual, siguiente_turno = siguiente_turno, turno_actual

    ganador = jugador1 if jugador1["equipo"] else jugador2
    print(f"\n¡{ganador['nombre']} gana el combate con {len(ganador['equipo'])} Pokémon restantes!")

# Ejecutar el juego
if __name__ == "__main__":
    juego()
