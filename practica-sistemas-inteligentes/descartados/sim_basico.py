from ia_alternativa.carga_datos import *
from descartados.combate import *

class JuegoPokemon:
    def __init__(self, lista_pokemon, matriz_efectividad):
        self.lista_pokemon = lista_pokemon
        self.matriz_efectividad = matriz_efectividad

    def seleccionar_pokemon_azar(self):
        return random.choice(self.lista_pokemon).copy()

    def ejecutar(self):
        print("Seleccionando Pokémon al azar...\n")
        pokemon1 = self.seleccionar_pokemon_azar()
        pokemon2 = self.seleccionar_pokemon_azar()

        print(f"Pokémon 1: {pokemon1['Nombre']} (HP: {pokemon1['HP']})")
        print(f"Pokémon 2: {pokemon2['Nombre']} (HP: {pokemon2['HP']})")
        print("\n--- COMIENZA LA BATALLA ---\n")
        resultado = batalla(pokemon1, pokemon2, self.matriz_efectividad)
        print("\n--- FIN DE LA BATALLA ---\n")
        print(resultado)

# Ejecutar el juego
juego = JuegoPokemon(pokemon_lista, efectividad_tipos_df)
juego.ejecutar()
