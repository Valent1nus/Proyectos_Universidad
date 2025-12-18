import os
import sys
import pygame
from src.ia.seleccionar_equipo import *
from src.menus.combat_menu import *

# Inicialización de Pygame
pygame.init()

# Configuración de pantalla
SCREEN = pygame.display.set_mode((1280, 720))
pygame.display.set_caption("IA Selection")

# Configuración de música
loser_path = os.path.join("assets/sound/Loser.flac")
winner_path = os.path.join("assets/sound/Winner.flac")

# Verificar rutas de archivos de música
if not os.path.exists(winner_path):
    raise FileNotFoundError(f"No se encontró el archivo de música: {winner_path}")
if not os.path.exists(loser_path):
    raise FileNotFoundError(f"No se encontró el archivo de sonido: {loser_path}")


# Cargar fondo blanco
base_dir = os.path.dirname(os.path.abspath(__file__))
bg_white_path = os.path.join(base_dir, "../../assets/menu/fondo_blanco.png")
if not os.path.exists(bg_white_path):
    raise FileNotFoundError(f"No se encontró el fondo blanco: {bg_white_path}")

BG = pygame.image.load(bg_white_path)
BG = pygame.transform.scale(BG, (SCREEN.get_width(), SCREEN.get_height()))


def final_menu(hp_text):
    if hp_text == "¡¡¡HAS GANADO!!!":
        # Reproducir musica de victoria
        pygame.mixer.music.load(winner_path)
        pygame.mixer.music.play()
    else:
        # Reproducir musica de derrota
        pygame.mixer.music.load(loser_path)
        pygame.mixer.music.play()

    while True:
        SCREEN.blit(BG, (0, 0))

        # Renderizar el texto centrado en pantalla
        font = get_font(50)  # Cambié a una fuente más grande
        text_surface = font.render(hp_text, True, "Black")
        text_rect = text_surface.get_rect(center=(SCREEN.get_width() // 2, SCREEN.get_height() // 2))
        SCREEN.blit(text_surface, text_rect)

        # Detectar eventos
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_RETURN:
                    # Parar la música y salir del menú final
                    pygame.mixer.music.stop()
                    return

        pygame.display.update()
