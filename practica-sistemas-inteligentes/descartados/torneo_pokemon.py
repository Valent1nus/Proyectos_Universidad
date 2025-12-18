import random
from carga_datos import pokemon_lista

class TorneoPokemon:
    def __init__(self, lista_pokemon, ia):
        self.lista_pokemon = lista_pokemon
        self.ia = ia
        self.entrenadores = []

    def generar_equipos_entrenadores(self):
        tipos = set(p['Tipo1'] for p in self.lista_pokemon)
        equipos_por_tipo = {
            tipo: [p for p in self.lista_pokemon if p['Tipo1'] == tipo]
            for tipo in tipos
        }

        # Filtrar tipos con menos de 6 Pokémon válidos
        tipos_validos = [tipo for tipo, equipo in equipos_por_tipo.items() if len(equipo) >= 6]
        if len(tipos_validos) < 7:
            raise ValueError("No hay suficientes tipos con al menos 6 Pokémon para formar 7 equipos")

        # Seleccionar 7 tipos aleatorios
        tipos_seleccionados = random.sample(tipos_validos, k=7)

        for i, tipo in enumerate(tipos_seleccionados):
            equipo = equipos_por_tipo[tipo]
            equipo_seleccionado = random.sample(equipo, k=6)
            self.entrenadores.append({
                'Nombre': f'Entrenador {i + 1}',
                'Tipo': tipo,
                'Equipo': equipo_seleccionado
            })

    def mostrar_equipos(self):
        for entrenador in self.entrenadores:
            print(f"Entrenador: {entrenador['Nombre']}, Tipo: {entrenador['Tipo']}")
            for pokemon in entrenador['Equipo']:
                print(f"  - {pokemon['Nombre']} (Tipo1: {pokemon['Tipo1']}, Tipo2: {pokemon['Tipo2']})")

# Crear la instancia del torneo
torneo = TorneoPokemon(pokemon_lista, None)

# Generar los equipos para los entrenadores
torneo.generar_equipos_entrenadores()

# Mostrar los equipos generados
torneo.mostrar_equipos()
