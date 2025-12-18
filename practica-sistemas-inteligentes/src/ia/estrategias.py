import random

# Definir colores usando códigos ANSI
RESET = "\033[0m"  # Resetea el color
GREEN = "\033[92m"  # Verde (muy eficaz)
RED = "\033[91m"  # Rojo (no es eficaz)
YELLOW = "\033[93m"  # Amarillo (neutro)


def calcular_efectividad_ataque(tabla_tipos, ataque, defensor):
    tipo_ataque = ataque.typ.strip()  # Limpiar espacios
    tipo1 = defensor.type1.strip()
    tipo2 = defensor.type2.strip() if defensor.type2 else None
    try:
        efectividad1 = tabla_tipos.loc[tipo_ataque, tipo1]
        efectividad2 = tabla_tipos.loc[tipo_ataque, tipo2] if tipo2 else 1.0
        return float(efectividad1) * float(efectividad2)
    except KeyError as e:
        print(f"Error: Tipo no encontrado: {e}")
        raise

def calcular_efectividad_pokemon(tabla_tipos, atacante, defensor):
    a_tipo1 = atacante.type1.strip()
    a_tipo2 = atacante.type2.strip() if defensor.type2 else None
    d_tipo1 = defensor.type1.strip()
    d_tipo2 = defensor.type2.strip() if defensor.type2 else None
    try:
        efectividad1 = tabla_tipos.loc[a_tipo1, d_tipo1]
        efectividad2 = tabla_tipos.loc[a_tipo2, d_tipo1] if a_tipo2 else 1.0
        primera_efectividad = float(efectividad1) * float(efectividad2)

        efectividad3 = tabla_tipos.loc[a_tipo1, d_tipo2] if d_tipo2 else 1.0
        efectividad4 = tabla_tipos.loc[a_tipo2, d_tipo2] if a_tipo2 and d_tipo2 else 1.0
        segunda_efectividad = float(efectividad3) * float(efectividad4)

        return primera_efectividad * segunda_efectividad
    except KeyError as e:
        print(f"Error: Tipo no encontrado: {e}")
        raise
def calcular_dano(atacante, defensor, ataque, tabla_tipos):
    Lvl = 50  # Nivel del Pokémon atacante
    B = 1.5 if ataque.typ in [atacante.type1, atacante.type2] else 1  # Bonificación del mismo tipo
    V = random.randint(85, 100)  # Variación de daño
    P = ataque.power  # Potencia del ataque
    E = calcular_efectividad_ataque(tabla_tipos, ataque, defensor) + calcular_efectividad_pokemon(tabla_tipos, atacante, defensor)  # Efectividad del ataque

    if ataque.category == 'Physical':
        A = atacante.attack
        D = defensor.defense
    else:  # ataque.category == 'Special'
        A = atacante.sp_attack
        D = defensor.sp_defense

    dano = 0.01 * B * E * V * (((0.2 * Lvl + 1) * A * P) / (25 * D) + 2)

    # Construir el mensaje con colores
    if E >= 2:
        msg = f"{GREEN}{atacante.nombre} usó {ataque.name} y causó {dano:0.0f} de daño. ¡Es muy eficaz!{RESET}"
    elif E < 1:
        msg = f"{RED}{atacante.nombre} usó {ataque.name} y causó {dano:0.0f} de daño. No es muy eficaz...{RESET}"
    else:
        msg = f"{YELLOW}{atacante.nombre} usó {ataque.name} y causó {dano:0.0f} de daño.{RESET}"

    print("\n", msg)
    return int(dano)


#Esta estrategia consiste en escoger siempre el ataque con más daño de los 4
def estrategia_mejor_dano(atacante, defensor, tabla_tipos):
    dano_ataq1 = calcular_dano(atacante, defensor, atacante.attack_list[0], tabla_tipos)
    dano_ataq2 = calcular_dano(atacante, defensor, atacante.attack_list[1], tabla_tipos)
    dano_ataq3 = calcular_dano(atacante, defensor, atacante.attack_list[2], tabla_tipos)
    dano_ataq4 = calcular_dano(atacante, defensor, atacante.attack_list[3], tabla_tipos)
    max_dano = max(dano_ataq1, dano_ataq2, dano_ataq3, dano_ataq4)
    if max_dano == dano_ataq1:
        return atacante.attack_list[0]
    elif max_dano == dano_ataq2:
        return atacante.attack_list[1]
    elif max_dano == dano_ataq3:
        return atacante.attack_list[2]
    else:
        return atacante.attack_list[3]

#Esta estrategia consiste en escoger un ataque aleatorio de los 4
def estrategia_aleatoria(atacante):
    return random.choice(atacante.attack_list)

#Esta estrategia consiste en escoger siempre el ataque con menos daño de los 4
def estrategia_peor_dano(atacante, defensor, tabla_tipos):
    dano_ataq1 = calcular_dano(atacante, defensor, atacante.attack_list[0], tabla_tipos)
    dano_ataq2 = calcular_dano(atacante, defensor, atacante.attack_list[1], tabla_tipos)
    dano_ataq3 = calcular_dano(atacante, defensor, atacante.attack_list[2], tabla_tipos)
    dano_ataq4 = calcular_dano(atacante, defensor, atacante.attack_list[3], tabla_tipos)
    min_dano = min(dano_ataq1, dano_ataq2, dano_ataq3, dano_ataq4)
    if min_dano == dano_ataq1:
        return atacante.attack_list[0]
    elif min_dano == dano_ataq2:
        return atacante.attack_list[1]
    elif min_dano == dano_ataq3:
        return atacante.attack_list[2]
    else:
        return atacante.attack_list[3]


#Esta estrategia consiste en escoger siempre el ataque con mas PP de los 4
def estrategia_conservar_pp(atacante):
    pp_ataq1 = atacante.attack_list[0].pp
    pp_ataq2 = atacante.attack_list[1].pp
    pp_ataq3 = atacante.attack_list[2].pp
    pp_ataq4 = atacante.attack_list[3].pp
    mas_pp = max(pp_ataq1, pp_ataq2, pp_ataq3, pp_ataq4)
    if mas_pp == pp_ataq1:
        return atacante.attack_list[0]
    elif mas_pp == pp_ataq2:
        return atacante.attack_list[1]
    elif mas_pp == pp_ataq3:
        return atacante.attack_list[2]
    else:
        return atacante.attack_list[3]

def seleccionar_estrategia(estrategia, atacante, defensor, tabla_tipos):
    if estrategia == 'aleatoria':
        return estrategia_aleatoria(atacante)
    elif estrategia == 'mejor_dano':
        return estrategia_mejor_dano(atacante, defensor, tabla_tipos)
    elif estrategia == 'peor_dano':
        return estrategia_peor_dano(atacante, defensor, tabla_tipos)
    elif estrategia == 'conservar_pp':
        return estrategia_conservar_pp(atacante)