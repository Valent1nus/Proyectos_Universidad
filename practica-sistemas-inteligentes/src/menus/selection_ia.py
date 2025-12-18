from src.ia.seleccionar_equipo import *
from src.menus.combat_menu import *

pygame.init()

SCREEN = pygame.display.set_mode((1280, 720))
pygame.display.set_caption("IA Selection")

# Configuración de música
music_path = os.path.join("assets/sound/File_Select_Medley.flac")
if not os.path.exists(music_path):
    raise FileNotFoundError(f"No se encontró el archivo de música en la ruta: {music_path}")
sound_select = pygame.mixer.Sound("assets/sound/sonido_de_pokemon.flac")
base_dir = os.path.dirname(os.path.abspath(__file__))
bg_white_path = os.path.join(base_dir, "../../assets/menu/fondo_blanco.png")
BG = pygame.image.load(bg_white_path)
BG = pygame.transform.scale(BG, (SCREEN.get_width(), SCREEN.get_height()))

def selection_ia(equipo, seleccionados):
    while True:
        # Cargar y reproducir música al entrar al menú
        if not pygame.mixer.music.get_busy():
            pygame.mixer.music.load(music_path)
            pygame.mixer.music.set_volume(0.3)  # Ajusta el volumen (0.0 a 1.0)
            pygame.mixer.music.play(-1)  # Reproduce en bucle infinito

        SCREEN.blit(BG, (0, 0))
        MENU_MOUSE_POS = pygame.mouse.get_pos()

        # Cargar los botones
        BUTTON_ATK1 = Button(image=None, pos=(475, 575), text_input='Aleatoria', font=get_font(20),
                             base_color="Black", hovering_color="Purple")
        BUTTON_ATK2 = Button(image=None, pos=(160, 575), text_input='Mejor_daño', font=get_font(20),
                             base_color="Black", hovering_color="Purple")
        BUTTON_ATK3 = Button(image=None, pos=(475, 650), text_input='Peor_daño', font=get_font(20),
                             base_color="Black", hovering_color="Purple")
        BUTTON_ATK4 = Button(image=None, pos=(160, 650), text_input='Conservar_pp', font=get_font(20),
                             base_color="Black", hovering_color="Purple")

        buttons = [BUTTON_ATK1, BUTTON_ATK2, BUTTON_ATK3, BUTTON_ATK4]

        # Dibujar botones
        for button in buttons:
            button.changeColor(pygame.mouse.get_pos())
            button.update(SCREEN)

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()
            if event.type == pygame.MOUSEBUTTONDOWN:
                if buttons[0].checkForInput(MENU_MOUSE_POS):
                    sound_select.play()
                    estrategia = 'aleatoria'
                    equipo_ia = algoritmo_genetico(equipo, tabla_tipos_aprendida, estrategia)
                    pygame.mixer.music.stop()  # Detén la música al ir a play_menu
                    print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n")
                    combat_menu(equipo, seleccionados, equipo_ia, estrategia)
                    return
                if buttons[1].checkForInput(MENU_MOUSE_POS):
                    sound_select.play()
                    estrategia = 'mejor_dano'
                    equipo_ia = algoritmo_genetico(equipo, tabla_tipos_aprendida, estrategia)
                    pygame.mixer.music.stop()  # Detén la música al ir a play_menu
                    print("\n\n\n\n\n\n\n\n\n\n\n\n")
                    combat_menu(equipo, seleccionados, equipo_ia, estrategia)
                    return
                if buttons[2].checkForInput(MENU_MOUSE_POS):
                    sound_select.play()
                    estrategia = 'peor_dano'
                    equipo_ia = algoritmo_genetico(equipo, tabla_tipos_aprendida, estrategia)
                    pygame.mixer.music.stop()  # Detén la música al ir a play_menu
                    print("\n\n\n\n\n\n\n\n\n\n\n\n")
                    combat_menu(equipo, seleccionados, equipo_ia, estrategia)
                    return
                if buttons[3].checkForInput(MENU_MOUSE_POS):
                    sound_select.play()
                    estrategia = 'conservar_pp'
                    equipo_ia = algoritmo_genetico(equipo, tabla_tipos_aprendida, estrategia)
                    pygame.mixer.music.stop()  # Detén la música al ir a play_menu
                    print("\n\n\n\n\n\n\n\n\n\n\n\n")
                    combat_menu(equipo, seleccionados, equipo_ia, estrategia)
                    return

        pygame.display.update()