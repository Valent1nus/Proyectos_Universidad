import sys
import pygame
import os
from src.menus.button import Button, get_font
from src.ia.estrategias import *
from src.ia.seleccionar_equipo import tabla_tipos_aprendida, tabla_tipos
from src.fight.attack import *
from src.menus.final_menu import final_menu

pygame.init()

SCREEN = pygame.display.set_mode((1280, 720))
pygame.display.set_caption("Combat")
base_dir = os.path.dirname(os.path.abspath(__file__))
bg_at_path = os.path.join(base_dir, "../../assets/menu/bg_combat_at.png")

BG_ALTERNATE = pygame.image.load(bg_at_path)
BG_ALTERNATE = pygame.transform.scale(BG_ALTERNATE, (SCREEN.get_width(), SCREEN.get_height()))


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


def turno_ataque(pokemon, rival, index_attack_list, tabla_tipos, estrategia, equipo_ia, equipo):
    ataque_sinPP = Attack("Placaje", "Normal", "Physical", 40, 100, 35)
    if pokemon.attack_list[index_attack_list].pp <= 0:
        dano_persona = calcular_dano(pokemon, rival, ataque_sinPP, tabla_tipos)
        ataque_persona = ataque_sinPP
    else:
        dano_persona = calcular_dano(pokemon, rival, pokemon.attack_list[index_attack_list], tabla_tipos)
        ataque_persona = pokemon.attack_list[index_attack_list]

    ataque_rival = seleccionar_estrategia(estrategia, rival, pokemon, tabla_tipos_aprendida)
    if ataque_rival.pp <= 0:
        dano_ia = calcular_dano(rival, pokemon, ataque_sinPP, tabla_tipos)
        ataque_rival = ataque_sinPP
    else:
        dano_ia = calcular_dano(rival, pokemon, ataque_rival, tabla_tipos)
        ataque_rival = ataque_rival

    print("\n\n\n")
    if pokemon.speed >= rival.speed:
        rival.hp_max -= dano_definitivo(dano_persona, ataque_persona, pokemon)
        if pokemon.attack_list[index_attack_list].pp > 0:
            pokemon.attack_list[index_attack_list].pp -= 1
        if rival.hp_max <= 0:
            print("\033[1;30;41m" + f"¡{equipo_ia[0].nombre} ha sido derrotado!" + '\033[0;m')
            equipo_ia.pop(0)
            if len(equipo_ia) == 0:
                hp_text = f"¡¡¡HAS GANADO!!!"  # Texto a mostrar
                final_menu(hp_text)
            return
        else:
            pokemon.hp_max -= dano_definitivo(dano_ia, ataque_rival, rival)
            if ataque_rival.pp > 0:
                ataque_rival.pp -= 1
            if pokemon.hp_max <= 0:
                print("\033[1;30;41m" + f"¡{equipo[0].nombre} ha sido derrotado!" + '\033[0;m')
                equipo.pop(0)
                if len(equipo) == 0:
                    hp_text = f"HAS PERDIDO"  # Texto a mostrar
                    final_menu(hp_text)
                return
    else:
        pokemon.hp_max -= dano_definitivo(dano_ia, ataque_rival, rival)
        if ataque_rival.pp > 0:
            ataque_rival.pp -= 1
        if pokemon.hp_max <= 0:
            print("\033[1;30;41m" + f"¡{equipo[0].nombre} ha sido derrotado!" + '\033[0;m')
            equipo.pop(0)
            if len(equipo) == 0:
                hp_text = f"HAS PERDIDO"  # Texto a mostrar
                final_menu(hp_text)
            return
        else:
            rival.hp_max -= dano_definitivo(dano_persona, ataque_persona, pokemon)
            if pokemon.attack_list[index_attack_list].pp > 0:
                pokemon.attack_list[index_attack_list].pp -= 1
            if rival.hp_max <= 0:
                print("\033[1;30;41m" + f"¡{equipo_ia[0].nombre} ha sido derrotado!" + '\033[0;m')
                equipo_ia.pop(0)
                if len(equipo_ia) == 0:
                    hp_text = f"¡¡¡HAS GANADO!!!"  # Texto a mostrar
                    final_menu(hp_text)
                return


def dano_definitivo(dano, ataque, pokemon):
    random_number = random.randint(0, 100)
    if (random_number > ataque.accuracy):
        print(f"{pokemon.nombre} ha fallado el ataque {ataque.name}")
        return 0
    return dano


def show_attack_options(pok, equipo, equipo_ia, estrategia):
    while True:
        SCREEN.blit(BG_ALTERNATE, (0, 0))
        show_currently(equipo)
        show_enemy(equipo_ia)

        # Botones con ataques
        BUTTON_ATK1 = Button(image=None, pos=(475, 575), text_input=f"{pok.attack_list[0].name}", font=get_font(20),
                             base_color="Black", hovering_color="Purple")
        BUTTON_ATK2 = Button(image=None, pos=(160, 575), text_input=f"{pok.attack_list[1].name}", font=get_font(20),
                             base_color="Black", hovering_color="Purple")
        BUTTON_ATK3 = Button(image=None, pos=(475, 650), text_input=f"{pok.attack_list[2].name}", font=get_font(20),
                             base_color="Black", hovering_color="Purple")
        BUTTON_ATK4 = Button(image=None, pos=(160, 650), text_input=f"{pok.attack_list[3].name}", font=get_font(20),
                             base_color="Black", hovering_color="Purple")

        buttons = [BUTTON_ATK1, BUTTON_ATK2, BUTTON_ATK3, BUTTON_ATK4]

        # Dibujar botones
        for button in buttons:
            button.changeColor(pygame.mouse.get_pos())
            button.update(SCREEN)

        # Mostrar texto de PP al pasar el mouse sobre un botón
        font = get_font(20)  # Obtener fuente
        mouse_pos = pygame.mouse.get_pos()
        text1 = font.render("", True, "Black")
        text2 = font.render("", True, "Black")

        if BUTTON_ATK1.checkForInput(mouse_pos):
            text1 = font.render(f"PP: {pok.attack_list[0].pp}", True, "Black")
            text2 = font.render(f"Tipo: {pok.attack_list[0].typ}", True, "Black")
        elif BUTTON_ATK2.checkForInput(mouse_pos):
            text1 = font.render(f"PP: {pok.attack_list[1].pp}", True, "Black")
            text2 = font.render(f"Tipo: {pok.attack_list[1].typ}", True, "Black")
        elif BUTTON_ATK3.checkForInput(mouse_pos):
            text1 = font.render(f"PP: {pok.attack_list[2].pp}", True, "Black")
            text2 = font.render(f"Tipo: {pok.attack_list[2].typ}", True, "Black")
        elif BUTTON_ATK4.checkForInput(mouse_pos):
            text1 = font.render(f"PP: {pok.attack_list[3].pp}", True, "Black")
            text2 = font.render(f"Tipo: {pok.attack_list[3].typ}", True, "Black")

        # Dibujar el texto en pantalla
        SCREEN.blit(text1, (975, 575))
        SCREEN.blit(text2, (960, 650))

        # Manejo de eventos
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()
            if event.type == pygame.MOUSEBUTTONDOWN:
                mouse_pos = pygame.mouse.get_pos()

                # Verificar si algún botón fue presionado y realizar acción
                if buttons[0].checkForInput(mouse_pos):
                    turno_ataque(equipo[0], equipo_ia[0], 0, tabla_tipos, estrategia, equipo_ia, equipo)
                    return
                if buttons[1].checkForInput(mouse_pos):
                    turno_ataque(equipo[0], equipo_ia[0], 1, tabla_tipos, estrategia, equipo_ia, equipo)
                    return
                if buttons[2].checkForInput(mouse_pos):
                    turno_ataque(equipo[0], equipo_ia[0], 2, tabla_tipos, estrategia, equipo_ia, equipo)
                    return
                if buttons[3].checkForInput(mouse_pos):
                    turno_ataque(equipo[0], equipo_ia[0], 3, tabla_tipos, estrategia, equipo_ia, equipo)
                    return

        pygame.display.update()
