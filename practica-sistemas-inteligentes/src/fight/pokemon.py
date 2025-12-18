import pandas as pd
import random
from src.fight.attack import Attack

class Pokemon:
    def __init__(self, nombre, type1, type2, hp_max, attack, defense, sp_attack, sp_defense, speed):
        self.nombre = nombre
        self.type1 = type1
        self.type2 = type2
        self.hp_max = hp_max
        self.attack = attack
        self.defense = defense
        self.sp_attack = sp_attack
        self.sp_defense = sp_defense
        self.speed = speed
        self.attack_list = []

    def asignar_ataques(self, ataques):
        self.attack_list = ataques

    def seleccionar_ataque(self):
        for ataque in self.attack_list:
            if ataque.pp > 0:
                return ataque
        # Si no tiene PP en ningún ataque, usa Placaje
        return Attack("Placaje", "Normal", "Physical", 40, 100, 35)

    def elegir_ataque_aleatorio(self):
        return random.choice(self.attack_list)
def crear_pokemon_por_id(dataframe_p, dataframe_a, id_poke):
    datos_pokemon = dataframe_p[dataframe_p['ID_p'] == id_poke].iloc[0]
    nombre = datos_pokemon['Name']
    type1 = datos_pokemon['Type1']
    type2 = datos_pokemon['Type2']
    hp_max = datos_pokemon['HP']
    attack = datos_pokemon['Attack']
    defense = datos_pokemon['Defense']
    sp_attack = datos_pokemon['Sp.Atk']
    sp_defense = datos_pokemon['Sp.Def']
    speed = datos_pokemon['Speed']

    nuevo_pokemon = Pokemon(nombre, type1, type2, hp_max, attack, defense, sp_attack, sp_defense, speed)

    # Asignar ataques desde el CSV
    ataques_ids = [datos_pokemon['Atq1.'], datos_pokemon['Atq2.'], datos_pokemon['Atq3.'], datos_pokemon['Atq4.']]
    ataques = []
    for ataque_id in ataques_ids:
        if not pd.isna(ataque_id):
            datos_ataque = dataframe_a[dataframe_a['ID_a'] == int(ataque_id)].iloc[0]
            ataque = Attack(
                datos_ataque['Name'],
                datos_ataque['Type'],
                datos_ataque['Category'],
                datos_ataque['Power'],
                datos_ataque['Accuracy'],
                datos_ataque['PP'],
            )
            ataques.append(ataque)
    nuevo_pokemon.asignar_ataques(ataques)
    return nuevo_pokemon

def elegir_pokemon_aleatorio(dataframe_p, dataframe_a):
    return crear_pokemon_por_id(dataframe_p, dataframe_a, random.randint(1, 83))
