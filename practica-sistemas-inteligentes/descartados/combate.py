import random
from colorama import Fore, Style


def calcular_efectividad(tipo_ataque, tipo_defensor1, tipo_defensor2, matriz_efectividad):
    efectividad1 = matriz_efectividad.at[tipo_ataque, tipo_defensor1] if tipo_ataque in matriz_efectividad.index and tipo_defensor1 in matriz_efectividad.columns else 1.0
    efectividad2 = matriz_efectividad.at[tipo_ataque, tipo_defensor2] if tipo_ataque in matriz_efectividad.index and tipo_defensor2 in matriz_efectividad.columns else 1.0
    return efectividad1 * efectividad2


def calcular_dano(ataque, atacante, defensor, matriz_efectividad):
    Nivel = 50
    Bonificacion = 1.5 if ataque['Tipo'] in [atacante['Tipo1'], atacante['Tipo2']] else 1
    Variacion = random.randint(85, 100)
    Potencia = ataque['Potencia']
    Efectividad = calcular_efectividad(ataque['Tipo'], defensor['Tipo1'], defensor['Tipo2'], matriz_efectividad)

    if ataque['Categoria'] == 'Physical':
        Ataque = atacante['Ataque']
        Defensa = defensor['Defensa']
    else:
        Ataque = atacante['Ataque_Especial']
        Defensa = defensor['Defensa_Especial']

    dano = 0.01 * Bonificacion * Efectividad * Variacion * (
                ((0.2 * Nivel + 1) * Ataque * Potencia) / (25 * Defensa) + 2)
    return max(1, int(dano))


def turno(atacante, defensor, matriz_efectividad):
    ataque = random.choice([atk for atk in atacante['Ataques'] if atk['PP'] > 0])
    if ataque:
        ataque['PP'] -= 1
        if random.random() <= ataque['Precision']:
            dano = calcular_dano(ataque, atacante, defensor, matriz_efectividad)
            defensor['HP'] -= dano
            efectividad = calcular_efectividad(ataque['Tipo'], defensor['Tipo1'], defensor['Tipo2'], matriz_efectividad)

            # Cambiar color según efectividad
            if efectividad > 1:
                msg = f"{Fore.GREEN}{atacante['Nombre']} usó {ataque['Nombre']} y causó {dano} de daño. ¡Es muy eficaz!{Style.RESET_ALL}"
            elif efectividad < 1:
                msg = f"{Fore.RED}{atacante['Nombre']} usó {ataque['Nombre']} y causó {dano} de daño. No es muy eficaz...{Style.RESET_ALL}"
            else:
                msg = f"{atacante['Nombre']} usó {ataque['Nombre']} y causó {dano} de daño."
            msg += f" (HP restante {defensor['Nombre']}: {max(defensor['HP'], 0)})"
            return msg
        else:
            return f"{atacante['Nombre']} usó {ataque['Nombre']} pero falló."
    return f"{atacante['Nombre']} no tiene ataques disponibles."


def batalla(pokemon1, pokemon2, matriz_efectividad):
    print(f"--- Estadísticas iniciales ---")
    print(f"{pokemon1['Nombre']} - HP: {pokemon1['HP']} | Ataque: {pokemon1['Ataque']} | Defensa: {pokemon1['Defensa']}")
    print(f"{pokemon2['Nombre']} - HP: {pokemon2['HP']} | Ataque: {pokemon2['Ataque']} | Defensa: {pokemon2['Defensa']}\n")

    while pokemon1['HP'] > 0 and pokemon2['HP'] > 0:
        if pokemon1['Velocidad'] >= pokemon2['Velocidad']:
            print(turno(pokemon1, pokemon2, matriz_efectividad))
            if pokemon2['HP'] > 0:
                print(turno(pokemon2, pokemon1, matriz_efectividad))
        else:
            print(turno(pokemon2, pokemon1, matriz_efectividad))
            if pokemon1['HP'] > 0:
                print(turno(pokemon1, pokemon2, matriz_efectividad))
        print("\n")
    ganador = pokemon1 if pokemon1['HP'] > 0 else pokemon2
    return f"El ganador es {ganador['Nombre']}!"
