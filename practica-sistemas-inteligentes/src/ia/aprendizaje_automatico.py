from individuo import *
from aprender_tabla_tipos import ejecutar_simulaciones, comparar_tablas
from src.ia.modificar_tabla_tipos import guardar_tabla_efectividad


def aprendizaje_automatico(num_individuos = 100, num_generaciones = 10000):
    prob_mutar_pokemon = 0.3
    prob_mutar_tabla_tipos = 0.1
    poblacion = []

    #Generar la población inicial
    for i in range(num_individuos):
        poblacion.append(generar_individuo())

    #Comienzo del algoritmo evolutivo
    for i in range(num_generaciones):
        print(f"Generación: {i}")
        for individuo in poblacion:
            #Realiza cada individuo 1 combate 1vs1
            ejecutar_simulaciones(1,individuo.pokemon,individuo.tabla_tipos)

            #Mutar el pokemon se cambia por otro aleatoriamente
            if random.random() <= prob_mutar_pokemon:
                individuo.set_pokemon(elegir_pokemon_aleatorio(dataframe_p,dataframe_a))

            #Mutar la tabla hace que de 1 a 3 elementos varíen su valor +-(0.1-0.7)
            if random.random() <= prob_mutar_tabla_tipos:
                individuo.set_tabla_tipos(mutar_tabla_tipos(individuo.tabla_tipos))

    mejor_individuo, mejor_porcentaje = buscar_mejor_individuo(poblacion)
    print(f"mejor_porcentaje {comparar_tablas(mejor_individuo.get_tabla_tipos())}")
    print(mejor_individuo.get_tabla_tipos())
    guardar_tabla_efectividad(mejor_individuo.get_tabla_tipos())

def mutar_tabla_tipos(tabla_tipos_individuo):
    num_mutaciones = random.randint(1,3)
    for i in range(num_mutaciones):

        fila = random.choice(tabla_tipos.index)
        columna = random.choice(tabla_tipos.columns)
        nuevo_valor = random.uniform(0.1,0.4)
        if random.random() <= 0.5:
            tabla_tipos_individuo.at[fila, columna] += nuevo_valor
        else:
            tabla_tipos_individuo.at[fila, columna] -= nuevo_valor
    return tabla_tipos_individuo


def buscar_mejor_individuo(poblacion):
    mejor_individuo = Individuo(None)
    mejor_porcentaje = 0.0
    for individuo in poblacion:
        porcentaje_actual = comparar_tablas(individuo.get_tabla_tipos())
        if mejor_porcentaje < porcentaje_actual:
            mejor_individuo = individuo
            mejor_porcentaje = porcentaje_actual
    return mejor_individuo, mejor_porcentaje


#Main del aprendizaje automatico
if __name__ == "__main__":
    aprendizaje_automatico()