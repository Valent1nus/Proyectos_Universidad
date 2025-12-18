import pandas as pd
import random

# Cargar datos de ataques
ataques_df = pd.read_csv('../dataheets/attacks01.csv', sep=';')

# Cargar datos de Pokemon
pokemon_df = pd.read_csv('../dataheets/gen01only3.csv', sep=';')

# Cargar la tabla de efectividad de tipos
efectividad_tipos_df = pd.read_csv('../dataheets/tabla_tipos_gen1.csv', sep=';', index_col=0)

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

# Crear lista de Pokemon con sus datos y ataques
pokemon_lista = []
for _, row in pokemon_df.iterrows():
    pokemon_lista.append({
        'ID': row['ID_p'],
        'Nombre': row['Name'],
        'Tipo1': row['Type1'],
        'Tipo2': row['Type2'],
        'HP': row['HP'],
        'Ataque': row['Attack'],
        'Defensa': row['Defense'],
        'Velocidad': row['Speed'],
        'Ataque_Especial': row['Sp.Atk'],
        'Defensa_Especial': row['Sp.Def'],
        'Ataques': [
            ataques[row['Atq1.']],
            ataques[row['Atq2.']],
            ataques[row['Atq3.']],
            ataques[row['Atq4.']]
        ]
    })
