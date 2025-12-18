import copy
from src.fight.pokemon import *
from src.fight.carga_datos import *

_tipos = ['Normal', 'Fighting', 'Flying', 'Poison', 'Ground', 'Rock', 'Bug', 'Fire',
             'Water', 'Grass', 'Electric', 'Psychic', 'Ice', 'Dragon']


class Individuo:
    def __init__(self, pokemon, tabla_tipos=pd.DataFrame(1.0, index=_tipos, columns=_tipos, dtype=float)):
        self.pokemon = pokemon
        self.tabla_tipos = copy.deepcopy(tabla_tipos)

    def get_pokemon(self):
        return self.pokemon

    def get_tabla_tipos(self):
        return self.tabla_tipos

    def set_pokemon(self, pokemon):
        self.pokemon = pokemon

    def set_tabla_tipos(self, tabla_tipos):
        self.tabla_tipos = tabla_tipos

def generar_individuo():
    return Individuo(copy.deepcopy(elegir_pokemon_aleatorio(dataframe_p,dataframe_a)))
