import pygame
import sys
import os
from src.menus.button import Button, get_font
from src.menus.play_menu import play

pygame.init()

SCREEN = pygame.display.set_mode((1280, 720))
pygame.display.set_caption("Main Menu")

# Configuración de música
pygame.mixer.init()
music_path = os.path.join("assets/sound/opening.flac")
if not os.path.exists(music_path):
    raise FileNotFoundError(f"No se encontró el archivo de música en la ruta: {music_path}")
sound_select = pygame.mixer.Sound("assets/sound/sonido_de_pokemon.flac")

# Cargar fondo del menú principal
base_dir = os.path.dirname(os.path.abspath(__file__))
bg_path = os.path.join(base_dir, "../../assets/menu/Background_inicio.png")
if not os.path.exists(bg_path):
    raise FileNotFoundError(f"No se encontró la imagen de fondo en la ruta: {bg_path}")
BG = pygame.image.load(bg_path)
BG = pygame.transform.scale(BG, (SCREEN.get_width(), SCREEN.get_height()))

def main_menu():
    while True:
        # Cargar y reproducir música al entrar al menú
        if not pygame.mixer.music.get_busy():
            pygame.mixer.music.load(music_path)
            pygame.mixer.music.set_volume(0.3)  # Ajusta el volumen (0.0 a 1.0)
            pygame.mixer.music.play(-1)  # Reproduce en bucle infinito

        SCREEN.blit(BG, (0, 0))
        MENU_MOUSE_POS = pygame.mouse.get_pos()

        # Rutas para los botones
        play_button_path = os.path.join(base_dir, "../../assets/menu/Play Rect.png")
        quit_button_path = os.path.join(base_dir, "../../assets/menu/Quit Rect.png")

        # Verificar las rutas
        if not os.path.exists(play_button_path):
            raise FileNotFoundError(f"No se encontró la imagen del botón PLAY en la ruta: {play_button_path}")
        if not os.path.exists(quit_button_path):
            raise FileNotFoundError(f"No se encontró la imagen del botón QUIT en la ruta: {quit_button_path}")

        # Cargar los botones
        PLAY_BUTTON = Button(image=pygame.image.load(play_button_path), pos=(640, 250),
                             text_input="JUGAR", font=get_font(65), base_color="#d7fcd4", hovering_color="White")
        QUIT_BUTTON = Button(image=pygame.image.load(quit_button_path), pos=(640, 400),
                             text_input="SALIR", font=get_font(65), base_color="#d7fcd4", hovering_color="White")

        for button in [PLAY_BUTTON, QUIT_BUTTON]:
            button.changeColor(MENU_MOUSE_POS)
            button.update(SCREEN)

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()
            if event.type == pygame.MOUSEBUTTONDOWN:
                if PLAY_BUTTON.checkForInput(MENU_MOUSE_POS):
                    sound_select.play()
                    pygame.mixer.music.stop()  # Detén la música al ir a play_menu
                    play()  # Llamar al menú de juego
                if QUIT_BUTTON.checkForInput(MENU_MOUSE_POS):
                    pygame.quit()
                    sys.exit()

        pygame.display.update()
