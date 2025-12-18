import pygame
from src.fight.carga_datos import *
from src.fight.pokemon import *
from src.fight.turn import *

pygame.init()
def combat(equipo, ataques, pokemons):
    def add_message(message):
        if len(messages) >= max_messages:
            messages.pop(0)  # Elimina el mensaje más antiguo si hay demasiados
        messages.append(message)

    pokemons_list = pokemons[['ID_p', 'Name']].values.tolist()
    jugador1 = {
        "nombre": "Héroe",
        "equipo": equipo,
    }
    jugador2 = {
        "nombre": "IA malévola",
        "equipo": [
            crear_pokemon_por_id(
                pokemons,
                ataques,
                int(pokemons[pokemons['Name'] == random.choice([p[1] for p in pokemons_list[:-1]])]['ID_p'].iloc[0])
            ) for _ in range(6)
        ],
    }

    messages = []  # Lista de mensajes para mostrar en pantalla
    max_messages = 10  # Número máximo de mensajes visibles

    add_message(f"¡{jugador1['nombre']} y {jugador2['nombre']} están listos para el combate!")
    add_message(f"Equipo de {jugador1['nombre']}: {[p.nombre for p in jugador1['equipo']]}")
    add_message(f"Equipo de {jugador2['nombre']}: {[p.nombre for p in jugador2['equipo']]}")

    turno_actual, siguiente_turno = decidir_turno(jugador1, jugador2)

    while jugador1["equipo"] and jugador2["equipo"]:
        add_message(f"Turno de {turno_actual['nombre']} con {turno_actual['equipo'][0].nombre}")
        add_message(f"{siguiente_turno['nombre']} tiene a {siguiente_turno['equipo'][0].nombre}")

        if turno(turno_actual, siguiente_turno, tabla_tipos):
            add_message(f"¡{siguiente_turno['nombre']} perdió a {siguiente_turno['equipo'][0].nombre}!")
            siguiente_turno["equipo"].pop(0)  # Retirar al Pokémon derrotado
            if siguiente_turno["equipo"]:  # Si el jugador tiene más Pokémon
                turno_actual, siguiente_turno = decidir_turno(turno_actual, siguiente_turno)
            continue  # Saltar el intercambio de turnos para respetar la nueva decisión

        turno_actual, siguiente_turno = siguiente_turno, turno_actual

    ganador = jugador1 if jugador1["equipo"] else jugador2
    add_message(f"¡{ganador['nombre']} gana el combate con {len(ganador['equipo'])} Pokémon restantes!")
    # Devuelve los mensajes para mostrarlos en la pantalla
    return messages