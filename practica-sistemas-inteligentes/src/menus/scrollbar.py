import pygame

scroll_y = 0
scroll_speed = 70  # Velocidad de desplazamiento
scrollbar_width = 20  # Ancho de la barra de desplazamiento vertical
line_height = 30  # Altura de cada línea de texto

def draw_scrollbar(screen, scroll_y, max_scroll_y, list_pos_x):
    scrollbar_height = screen.get_height() * (screen.get_height() / max_scroll_y)  # Ajusta la altura de la barra
    scrollbar_height = max(scrollbar_height, 20)  # Asegurarse de que la barra no sea demasiado pequeña

    # Fondo de la barra (gris claro)
    pygame.draw.rect(screen, (169, 169, 169), (list_pos_x + 200, 0, scrollbar_width, screen.get_height()))

    # Posición y dibujo de la barra de desplazamiento (rojo)
    scrollbar_position = (screen.get_height() - scrollbar_height) * (scroll_y / max_scroll_y)
    pygame.draw.rect(screen, (255, 0, 0), (list_pos_x + 200, scrollbar_position, scrollbar_width, scrollbar_height))

def handle_scroll(event, scroll_y, max_scroll_y):
    if event.type == pygame.MOUSEBUTTONDOWN:
        if event.button == 4:  # Rueda del ratón hacia arriba
            scroll_y = max(0, scroll_y - scroll_speed)
        if event.button == 5:  # Rueda del ratón hacia abajo
            scroll_y = min(max_scroll_y, scroll_y + scroll_speed)
    return scroll_y
