----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 17.05.2025 11:19:09
-- Design Name: 
-- Module Name: UART_NIR - Behavioral
-- Project Name: 
-- Target Devices: 
-- Tool Versions: 
-- Description: 
-- 
-- Dependencies: 
-- 
-- Revision:
-- Revision 0.01 - File Created
-- Additional Comments:
-- 
----------------------------------------------------------------------------------


library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.NUMERIC_STD.ALL;

entity UART_NIR is
    Port (
        clk      : in  std_logic;                           -- Reloj de sistema (100 MHz)
        rx       : in  std_logic;                           -- Entrada UART (desde sensor)
        tx       : out std_logic;                           -- Salida UART (a sensor)
        value_G  : out std_logic_vector(39 downto 0);       -- Numero del canal G, min 1 byte y max 5 bytes
        value_E  : out std_logic_vector(39 downto 0);       -- Numero del canal E, min 1 byte y max 5 bytes
        value_G_len : out unsigned(2 downto 0);             -- Numero de bytes que tiene G
        value_E_len : out unsigned(2 downto 0);             -- Numero de bytes que tiene E
        swt_ATDATA    : in  std_logic;                      -- Pulso externo para enviar "ATDATA"
        tx_reset    : in std_logic
    );
end UART_NIR;

architecture Behavioral of UART_NIR is

    -- Señal de envío del mensaje
    signal send_ATDATA : std_logic := '0';      
    -- Señales para tx    
    signal data_in    : std_logic_vector(7 downto 0):= x"41";
    signal tx_send    : std_logic;
    signal char_index_tx : integer range 0 to 7 := 0;
    signal tx_done    : std_logic := '0';
    signal uart_tx_event : std_logic;   
      

    -- Variables para rx
    signal data_recv   : std_logic_vector(7 downto 0);
    signal rx_event    : std_logic;
    type byte_array is array (0 to 4) of std_logic_vector(7 downto 0);
    signal temp_value  : byte_array := (others => (others => '0'));
    signal number_index : integer := 0;
    signal collecting  : boolean := false;
    signal char_index_rx  : integer range 0 to 4 := 0;

    signal reg_value_G : byte_array := (others => (others => '0'));
    signal reg_value_E : byte_array := (others => (others => '0'));
    
    -- Contadores de longitud
    signal len_G : unsigned(2 downto 0) := (others => '0');
    signal len_E : unsigned(2 downto 0) := (others => '0');
    
    component UART
        generic (
            BAUD_CYCLES : integer := 868
        );
        port (
            clk         : in std_logic;
            data_in     : in std_logic_vector(7 downto 0);
            tx_send     : in std_logic;
            tx_reset    : in std_logic;
            rx          : in std_logic;
            tx          : out std_logic;
            data_recv   : out std_logic_vector(7 downto 0);
            tx_event    : out std_logic;
            rx_event    : out std_logic
        );
    end component;
begin
    
    -- Salidas de valores finales
    value_G <= reg_value_G(0) & reg_value_G(1) & reg_value_G(2) & reg_value_G(3) & reg_value_G(4);
    value_E <= reg_value_E(0) & reg_value_E(1) & reg_value_E(2) & reg_value_E(3) & reg_value_E(4);
    
    value_G_len <= len_G;
    value_E_len <= len_E;
    -- Instancia del módulo UART
    uart_inst : UART
        generic map (
            BAUD_CYCLES => 868
        )
        port map (
            clk         => clk,
            data_in     => data_in,
            tx_send     => tx_send,
            tx_reset    => tx_reset,
            rx          => rx,
            tx          => tx,
            data_recv   => data_recv,
            tx_event    => uart_tx_event,
            rx_event    => rx_event
        );
        
    -- Proceso para detectar el flanco de subida del switch que activa el mensaje ATDATA
    process(clk)
    begin
        if rising_edge(clk) then
            -- Detectar flanco de subida
            if (swt_ATDATA = '1' and tx_done = '0') then
                send_ATDATA <= '1';  -- Enviar mensaje
            else
                send_ATDATA <= '0';
            end if;

        end if;
    end process;
    -- Proceso para el control de la transmisión del mensaje ATDATA
    tx_process : process(clk)
    begin
    if rising_edge(clk) then
        -- Reset de transmisión
        if tx_reset = '1' or swt_ATDATA = '0'then
            tx_send <= '0';
            char_index_tx <= 0;
            tx_done <= '0';
        elsif send_ATDATA = '1' and tx_done = '0' then
            -- Solo cargar el siguiente carácter si no se está enviando
            if tx_send = '0' then
                -- Cargar el carácter correspondiente
                case char_index_tx is
                    when 0 => data_in <= x"41"; -- 'A' ,   Primer byte enviado es basura (por error de arranque), se descarta
                    when 1 => data_in <= x"54"; -- 'T'   Para limpiar entrada anterior en el sensor
                    when 2 => data_in <= x"44"; -- 'D'    Inicio del comando "ATDATA"
                    when 3 => data_in <= x"41"; -- 'A'
                    when 4 => data_in <= x"54"; -- 'T'
                    when 5 => data_in <= x"41"; -- 'A'
                    when 6 => data_in <= x"0D"; -- '\r'
                    when 7 => data_in <= x"0A"; -- '\n'    Ultima letra del comando  Final para indicar fin de comando al sensor
                    when others => data_in <= x"00"; -- Default o error (no debería entrar aquí)
                end case;
    
                -- Iniciar el envío del carácter actual
                tx_send <= '1';
            elsif uart_tx_event = '1' then
                -- Desactivar el envío tras el evento de transmisión
                tx_send <= '0';
    
                -- Incrementar el índice o finalizar
                if char_index_tx < 7 then
                    char_index_tx <= char_index_tx + 1;
                else
                    tx_done <= '1'; -- Señal de mensaje completo
                    char_index_tx <= 0;
                end if;
            end if;
        end if;
    end if;
    end process;

    -- Receptor de datos y extracción de canales 6 y 16
    rx_process: process(clk)
    begin
        if rising_edge(clk) then
            if rx_event = '1' then
                if data_recv = std_logic_vector(to_unsigned(character'pos(','), 8)) then -- Byte se ha completado
                    
                    if number_index = 6 then -- 
                        reg_value_G <= temp_value;
                        len_G <= to_unsigned(char_index_rx, 3);
                    elsif number_index = 16 then
                        reg_value_E <= temp_value;
                        len_E <= to_unsigned(char_index_rx, 3);
                    end if;
                    number_index <= number_index + 1;
                    collecting <= false;
                    char_index_rx <= 0;
                    temp_value <= (others => (others => '0'));
                elsif data_recv = std_logic_vector(to_unsigned(character'pos(' '), 8)) then -- Ignorar espacio si no recolectando
                    null;
                else
                    if collecting and char_index_rx < 5 then
                        temp_value(char_index_rx) <= data_recv;
                        char_index_rx <= char_index_rx + 1;
                    elsif number_index = 6 or number_index = 16 then
                      
                        collecting <= true;
                        temp_value(0) <= data_recv;
                        char_index_rx <= 1;
                    end if;
                end if;
            end if;
        end if;
    end process;

end Behavioral;

