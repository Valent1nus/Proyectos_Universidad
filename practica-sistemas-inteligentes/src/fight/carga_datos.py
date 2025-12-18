import os
import pandas as pd

# Rutas y carga de datos
_base_dir = os.path.dirname(os.path.abspath(__file__))
_csv_path_pokemons = os.path.join(_base_dir, "../../dataheets/gen01only3.csv")
_csv_path_ataques = os.path.join(_base_dir, "../../dataheets/attacks01.csv")
_csv_tabla_tipos = os.path.join(_base_dir, "../../dataheets/tabla_tipos_gen1.csv")
_csv_tabla_tipos_aprendida = os.path.join(_base_dir, "../ia/tabla_tipos_aprendida.csv")

def cargar_csv(csv_file):
    return pd.read_csv(csv_file, delimiter=';')

def cargar_tabla_tipos(csv_path):
    tabla = pd.read_csv(csv_path, delimiter=';', index_col=0)
    tabla.index = tabla.index.str.strip()  # Limpiar espacios en índices
    tabla.columns = tabla.columns.str.strip()  # Limpiar espacios en columnas
    return tabla

def cargar_tabla_tipos_aprendida(csv_path):
    tabla = pd.read_csv(csv_path, delimiter=',', index_col=0)
    tabla.index = tabla.index.str.strip()  # Limpiar espacios en índices
    tabla.columns = tabla.columns.str.strip()  # Limpiar espacios en columnas
    return tabla

dataframe_p = cargar_csv(_csv_path_pokemons)
dataframe_a = cargar_csv(_csv_path_ataques)
tabla_tipos = cargar_tabla_tipos(_csv_tabla_tipos)
tabla_tipos_aprendida = cargar_tabla_tipos_aprendida(_csv_tabla_tipos_aprendida)