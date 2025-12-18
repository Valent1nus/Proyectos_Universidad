import pandas as pd


def cargar_datos():
    """
    Carga los datos de los ataques, Pokémon y tabla de efectividad desde archivos CSV.

    Retorna:
    - ataques (dict): Diccionario con los datos de los ataques.
    - pokemon_lista (list): Lista de diccionarios que representan los Pokémon con sus atributos y ataques.
    - efectividad_tipos (pd.DataFrame): Matriz de efectividad de tipos.
    """
    # Cargar datos de ataques
    ataques_df = pd.read_csv('../../dataheets/attacks01.csv', sep=';')

    # Cargar datos de Pokémon
    pokemon_df = pd.read_csv('../../dataheets/gen01only3.csv', sep=';')

    # Cargar la tabla de efectividad de tipos
    efectividad_tipos = pd.read_csv('../../dataheets/tabla_tipos_gen1.csv', sep=';', index_col=0)

    # Crear diccionario de ataques
    ataques = {
        row['ID_a']: {
            'Nombre': row['Name'],
            'Tipo': row['Type'],
            'Categoria': row['Category'],
            'Potencia': row['Power'],
            'Precision': row['Accuracy'],
            'PP': row['PP']
        }
        for _, row in ataques_df.iterrows()
    }

    # Crear lista de Pokémon con sus atributos y ataques
    pokemon_lista = []
    for _, row in pokemon_df.iterrows():
        # Asignar los ataques al Pokémon
        ataques_pokemon = [
            ataques.get(row['Atq1.'], None),
            ataques.get(row['Atq2.'], None),
            ataques.get(row['Atq3.'], None),
            ataques.get(row['Atq4.'], None)
        ]

        pokemon_lista.append({
            'ID': row['ID_p'],
            'Nombre': row['Name'],
            'Tipo1': row['Type1'],
            'Tipo2': row['Type2'] if not pd.isna(row['Type2']) else None,
            'HP': row['HP'],
            'Ataque': row['Attack'],
            'Defensa': row['Defense'],
            'Velocidad': row['Speed'],
            'Ataque_Especial': row['Sp.Atk'],
            'Defensa_Especial': row['Sp.Def'],
            'Ataques': [atk for atk in ataques_pokemon if atk is not None]
        })

    return ataques, pokemon_lista, efectividad_tipos


# Prueba de carga de datos
if __name__ == "__main__":
    ataques, pokemon_lista, efectividad_tipos = cargar_datos()
    print(f"Se cargaron {len(ataques)} ataques.")
    print(f"Se cargaron {len(pokemon_lista)} Pokémon.")
    print(f"La tabla de efectividad tiene dimensiones: {efectividad_tipos.shape}.")
