import random

def decidir_turno(jugador1, jugador2):
    if jugador1["equipo"][0].speed >= jugador2["equipo"][0].speed:
        return jugador1, jugador2
    else:
        return jugador2, jugador1

def calcular_efectividad(tabla_tipos, ataque, defensor):
    tipo_ataque = ataque.typ.strip()  # Limpiar espacios
    tipo1 = defensor.type1.strip()
    tipo2 = defensor.type2.strip() if defensor.type2 else None
    try:
        efectividad1 = tabla_tipos.loc[tipo1, tipo_ataque]
        efectividad2 = tabla_tipos.loc[tipo2, tipo_ataque] if tipo2 else 1.0
        return float(efectividad1) * float(efectividad2)
    except KeyError as e:
        print(f"Error: Tipo no encontrado: {e}")
        raise

def calcular_dano(atacante, defensor, ataque, tabla_tipos):
    Lvl = 50  # Nivel del Pokémon atacante
    B = 1.5 if ataque.typ in [atacante.type1, atacante.type2] else 1  # Bonificación del mismo tipo
    V = random.randint(85, 100)  # Variación de daño
    P = ataque.power  # Potencia del ataque
    E = calcular_efectividad(tabla_tipos, ataque, defensor) # Efectividad del ataque

    if ataque.category == 'Physical':
        A = atacante.attack
        D = defensor.defense
    else:  # ataque.category == 'Special'
        A = atacante.sp_attack
        D = defensor.sp_defense

    if E == 2:
        print("\033[1;30;42m"+"¡El ataque es muy efectivo!"+'\033[0;m')
    elif E == 0.5:
        print("\033[1;30;41m"+"¡El ataque es poco eficaz!"+'\033[0;m')

    dano = 0.01 * B * E * V * (((0.2 * Lvl + 1) * A * P) / (25 * D) + 2)
    return int(dano)


def turno(jugador, enemigo, tabla_tipos):
    atacante = jugador["equipo"][0]
    defensor = enemigo["equipo"][0]
    print(f"\nTurno de {jugador['nombre']} ({atacante.nombre}):")

    # Mostrar lista de ataques con PP restantes
    print("Elige un ataque:")
    for i, ataque in enumerate(atacante.attack_list, 1):
        print(
            f"{i}. {ataque.name} ({ataque.typ} - {ataque.category}): Potencia {ataque.power},"
            f" PP: {ataque.pp}/{ataque.max_pp}")

    # Elegir ataque
    while True:
        try:
            eleccion = int(input("\nIngresa el número del ataque: "))
            if 1 <= eleccion <= len(atacante.attack_list):
                ataque_seleccionado = atacante.attack_list[eleccion - 1]
                if ataque_seleccionado.pp > 0:
                    break
                else:
                    print(f"¡{ataque_seleccionado.name} no tiene PP restantes! Elige otro ataque.")
            else:
                print("Elección inválida. Intenta de nuevo.")
        except ValueError:
            print("Por favor, ingresa un número válido.")

    # Ejecutar ataque
    print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n")
    print(f"\n{jugador['nombre']} ({atacante.nombre}) usa {ataque_seleccionado.name}!")
    accuracy = ataque_seleccionado.accuracy
    if random.uniform(0, 100) > accuracy:
        print(f"¡{atacante.nombre} usó {ataque_seleccionado.name} pero falló!")
        return False

    # Reducir PP y calcular daño
    ataque_seleccionado.pp -= 1
    dano = calcular_dano(atacante, defensor, ataque_seleccionado, tabla_tipos)
    enemigo["equipo"][0].hp_max -= dano
    print(f"¡{atacante.nombre} infligió {dano} de daño a {defensor.nombre}!")
    if enemigo["equipo"][0].hp_max <= 0:
        print("\033[1;30;41m"+f"¡{defensor.nombre} ha sido derrotado!"+ '\033[0;m')
        return True
    else:
        print("\33[1;33m" + f"La vida de {defensor.nombre} es {enemigo["equipo"][0].hp_max}" + '\033[0;m')
    return False
