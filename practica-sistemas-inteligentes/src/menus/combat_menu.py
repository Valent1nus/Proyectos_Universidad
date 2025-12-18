import sys
import pygame
from src.menus.button import Button, get_font
from src.ia.seleccionar_equipo import *
from src.menus.combat_attack_menu import *

pygame.init()

SCREEN = pygame.display.set_mode((1280, 720))
pygame.display.set_caption("Combat")

# Configuración de música
music_path_back = os.path.join("assets/sound/battle.flac")
if not os.path.exists(music_path_back):
    raise FileNotFoundError(f"No se encontró el archivo de música en la ruta: {music_path_back}")

sound_select = pygame.mixer.Sound("assets/sound/sonido_de_pokemon.flac")

# Cargar fondo del menú principal
base_dir = os.path.dirname(os.path.abspath(__file__))
bg_ini_path = os.path.join(base_dir, "../../assets/menu/bg_combat_ini.png")
bg_at_path = os.path.join(base_dir, "../../assets/menu/bg_combat_at.png")

if not os.path.exists(bg_ini_path) or not os.path.exists(bg_at_path):
    raise FileNotFoundError("No se encontraron las imágenes de fondo en las rutas proporcionadas.")

BG = pygame.image.load(bg_ini_path)
BG = pygame.transform.scale(BG, (SCREEN.get_width(), SCREEN.get_height()))


def show_currently(equipo):
    image_path = os.path.join(base_dir, "../../assets/pokemons", f"{equipo[0].nombre}_back.png")
    if os.path.exists(image_path):
        # Cargar y escalar la imagen
        image = pygame.image.load(image_path).convert_alpha()
        image = pygame.transform.scale(image, (360, 360))
        image_x = 100
        image_y = 145
        SCREEN.blit(image, (image_x, image_y))

        # Mostrar el texto de la vida (HP máximo) encima de la imagen
        font = get_font(25)  # Fuente para el texto
        hp_text = f"HP: {equipo[0].hp_max}"  # Texto a mostrar
        text_surface = font.render(hp_text, True, "Black")  # Renderizar el texto
        text_x = image_x + 100  # Centrar el texto sobre la imagen
        text_y = image_y - 30  # Colocar el texto encima de la imagen
        SCREEN.blit(text_surface, (text_x, text_y))  # Dibujar el texto en pantalla



def show_enemy(equipo_ia):
    # Ruta de la imagen del enemigo
    image_path = os.path.join(base_dir, "../../assets/pokemons", f"{equipo_ia[0].nombre}.png")

    if os.path.exists(image_path):
        # Cargar y escalar la imagen
        image = pygame.image.load(image_path).convert_alpha()
        image = pygame.transform.scale(image, (280, 280))
        image_x = 720
        image_y = 0
        SCREEN.blit(image, (image_x, image_y))

        # Mostrar el texto de la vida (HP máximo)
        font = get_font(25)  # Fuente para el texto
        hp_text = f"HP: {equipo_ia[0].hp_max}"  # Texto a mostrar
        text_surface = font.render(hp_text, True, "Black")  # Renderizar el texto
        text_x = image_x + 20  # Posición x del texto (ajustado junto a la imagen)
        text_y = image_y + 300  # Posición y del texto (debajo de la imagen)
        SCREEN.blit(text_surface, (text_x, text_y))  # Dibujar el texto en pantalla


def combat_menu(equipo, seleccionados, equipo_ia, estrategia):
    while True:
        if not pygame.mixer.music.get_busy():
            pygame.mixer.music.load(music_path_back)
            pygame.mixer.music.set_volume(0.3)
            pygame.mixer.music.play(-1)

        SCREEN.blit(BG, (0, 0))



        # Mostrar el Pokémon que esté en combate
        show_currently(equipo)
        show_enemy(equipo_ia)

        # Botones "RENDIRSE" y "ATACAR"
        RENDIRSE_BUTTON = Button(image=None, pos=(1000, 650), text_input="RENDIRSE", font=get_font(25), base_color="Black", hovering_color="Green")
        ATACAR_BUTTON = Button(image=None, pos=(1050, 575), text_input="ATACAR", font=get_font(25), base_color="Black", hovering_color="Green")

        for button in [RENDIRSE_BUTTON, ATACAR_BUTTON]:
            button.changeColor(pygame.mouse.get_pos())
            button.update(SCREEN)

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()
            if event.type == pygame.MOUSEBUTTONDOWN:
                if RENDIRSE_BUTTON.checkForInput(pygame.mouse.get_pos()):
                    equipo.clear()
                    seleccionados.clear()
                    sound_select.play()
                    pygame.mixer.music.stop()
                    return
                if ATACAR_BUTTON.checkForInput(pygame.mouse.get_pos()):
                    sound_select.play()
                    show_attack_options(equipo[0], equipo, equipo_ia, estrategia)
                    # Verifica si el menú final fue ejecutado y rompe el bucle
                    if len(equipo) == 0 or len(equipo_ia) == 0:
                        return  # Salir del bucle si se ejecuta el final_menu

        pygame.display.update()
