import random

class Attack:
    def __init__(self, name, type, category, power, accuracy, pp):
        self.name = name
        self.type = type
        self.category = category
        self.power = power
        self.accuracy = accuracy
        self.pp = pp
        self.remaining_uses = pp

class Pokemon:
    def __init__(self, name, type1, type2, hp, attack, defense, speed, sp_atk, sp_def, attacks):
        self.name = name
        self.type1 = type1
        self.type2 = type2
        self.hp = hp
        self.attack = attack
        self.defense = defense
        self.speed = speed
        self.sp_atk = sp_atk
        self.sp_def = sp_def
        self.attacks = [Attack(**atk) for atk in attacks]
