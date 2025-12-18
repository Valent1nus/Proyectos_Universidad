from src.menus.scrollbar import handle_scroll, draw_scrollbar
from src.menus.selection_ia import *

pygame.init()

SCREEN = pygame.display.set_mode((1280, 720))
pygame.display.set_caption("Play Menu")

# Configuración de música
music_path_back = os.path.join("assets/sound/title_screen.flac")
if not os.path.exists(music_path_back):
    raise FileNotFoundError(f"No se encontró el archivo de música en la ruta: {music_path_back}")
sound_select = pygame.mixer.Sound("assets/sound/sonido_de_pokemon.flac")

# Cargar fondo del menú principal
base_dir = os.path.dirname(os.path.abspath(__file__))
bg_path = os.path.join(base_dir, "../../assets/menu/Background_play.png")
if not os.path.exists(bg_path):
    raise FileNotFoundError(f"No se encontró la imagen de fondo en la ruta: {bg_path}")
BG = pygame.image.load(bg_path)
BG = pygame.transform.scale(BG, (SCREEN.get_width(), SCREEN.get_height()))






# Rutas y carga de datos
csv_path_pokemons = os.path.join(base_dir, "../../dataheets/gen01only3.csv")
csv_path_ataques = os.path.join(base_dir, "../../dataheets/attacks01.csv")

ataques = cargar_csv(csv_path_ataques)
pokemons = cargar_csv(csv_path_pokemons)
pokemons_list = pokemons[['ID_p', 'Name']].values.tolist()
pokemons_list.append(['?', 'Random'])  # Añadir opción aleatoria
scroll_y_max = max(0, len(pokemons_list) * 30 - SCREEN.get_height())  # Máximo desplazamiento



def play():
    scroll_y = 0
    equipo = []  # Lista de Pokémon seleccionados
    seleccionados = set()  # Set para guardar los nombres de Pokémon seleccionados
    while True:
        # Cargar y reproducir música al entrar al menú
        if not pygame.mixer.music.get_busy():
            pygame.mixer.music.load(music_path_back)
            pygame.mixer.music.set_volume(0.1)  # Ajusta el volumen (0.0 a 1.0)
            pygame.mixer.music.play(-1)  # Reproduce en bucle infinito

        SCREEN.blit(BG, (0, 0))
        rect_positions = []

        # Mostrar Pokémon visibles según el desplazamiento
        start_idx = max(0, scroll_y // 30)
        end_idx = min((scroll_y + SCREEN.get_height()) // 30 + 1, len(pokemons_list))

        for i, pokemon in enumerate(pokemons_list[start_idx:end_idx]):
            rect = pygame.Rect(20, 20 + i * 30, 200, 30)
            rect_positions.append((rect, pokemon))

            # Si el Pokémon ya está seleccionado, renderizar su nombre en rojo
            if pokemon[1] in seleccionados:
                color = "Red"
            elif rect.collidepoint(pygame.mouse.get_pos()):
                color = "Green"
            else:
                color = "White"

            text = get_font(20).render(f"{pokemon[0]}: {pokemon[1]}", True, color)
            SCREEN.blit(text, rect.topleft)

        # Dibujar la barra de desplazamiento
        draw_scrollbar(SCREEN, scroll_y, scroll_y_max, list_pos_x=1150)

        # Mostrar equipo seleccionado
        for idx, pokemon in enumerate(equipo):
            # Mostrar la imagen del Pokémon
            image_path = os.path.join(base_dir, "../../assets/pokemons", f"{pokemon.nombre}.png")
            if os.path.exists(image_path):
                image = pygame.image.load(image_path)
                image = pygame.transform.scale(image, (90, 90))
                image_x = 350
                image_y = 60 + idx * 100
                SCREEN.blit(image, (image_x, image_y))

                # Mostrar ataques a la derecha de la imagen
                if pokemon.attack_list:  # Verificar que el Pokémon tiene ataques asignados
                    text_x = image_x + 100
                    text_y = image_y
                    for attack in pokemon.attack_list:
                        attack_text = f"{attack.name} | Power: {attack.power} | PP: {attack.pp}"
                        attack_render = get_font(16).render(attack_text, True, "White")
                        SCREEN.blit(attack_render, (text_x, text_y))
                        text_y += 20

        # Botones
        BACK_BUTTON = Button(image=None, pos=(640, 690),
                             text_input="BACK", font=get_font(25), base_color="White", hovering_color="Green")
        REMOVE_BUTTON = Button(image=None, pos=(1150, 690),
                               text_input="REMOVE", font=get_font(25), base_color="White", hovering_color="Green")
        COMBAT_BUTTON = None

        if len(equipo) == 6:  # Mostrar el botón "COMBATIR" si el equipo tiene 6 Pokémon
            COMBAT_BUTTON = Button(image=None, pos=(1150, 25),
                                    text_input="COMBATIR", font=get_font(25), base_color="White", hovering_color="Green")
            COMBAT_BUTTON.changeColor(pygame.mouse.get_pos())
            COMBAT_BUTTON.update(SCREEN)

        BACK_BUTTON.changeColor(pygame.mouse.get_pos())
        REMOVE_BUTTON.changeColor(pygame.mouse.get_pos())
        BACK_BUTTON.update(SCREEN)
        REMOVE_BUTTON.update(SCREEN)

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()

            scroll_y = handle_scroll(event, scroll_y, scroll_y_max)

            if event.type == pygame.MOUSEBUTTONDOWN and event.button == 1: # Sólo procesamos clic izquierdo (botón 1)
                if BACK_BUTTON.checkForInput(pygame.mouse.get_pos()):
                    sound_select.play()
                    pygame.mixer.music.stop()  # Detén la música al ir a play_menu
                    return  # Volver al menú principal
                if REMOVE_BUTTON.checkForInput(pygame.mouse.get_pos()):
                    if equipo:
                        sound_select.play()
                        eliminado = equipo.pop()  # Eliminar del equipo
                        seleccionados.remove(eliminado.nombre)  # Quitar de la lista de seleccionados
                if COMBAT_BUTTON and COMBAT_BUTTON.checkForInput(pygame.mouse.get_pos()):
                    sound_select.play()
                    pygame.mixer.music.stop()
                    selection_ia(equipo, seleccionados)

                # Mover este bloque dentro del bucle de eventos
                for rect, pokemon in rect_positions:
                    if rect.collidepoint(event.pos) and len(equipo) < 6 and pokemon[1] not in seleccionados:
                        selected_name = pokemon[1] if pokemon[1] != "Random" else random.choice(
                            [p[1] for p in pokemons_list[:-1]])
                        sound_select.play()
                        # Buscar los datos completos del Pokémon en el DataFrame y crear el objeto Pokémon
                        selected_id = int(pokemons[pokemons['Name'] == selected_name]['ID_p'].iloc[0])
                        new_pokemon = crear_pokemon_por_id(pokemons, ataques, selected_id)
                        equipo.append(new_pokemon)  # Agregar el objeto Pokemon al equipo
                        seleccionados.add(new_pokemon.nombre)  # Añadir a la lista de seleccionados

                        # Reproducir el sonido del Pokémon
                        sound_pokemon_path = os.path.join(base_dir, "../../assets/sound/pokemon", f"{new_pokemon.nombre}.wav")
                        if os.path.exists(sound_pokemon_path):
                            sound_pokemon = pygame.mixer.Sound(sound_pokemon_path)
                            sound_pokemon.set_volume(0.4)
                            sound_pokemon.play()

        pygame.display.update()
