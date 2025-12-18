----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 17.05.2025 12:06:25
-- Design Name: 
-- Module Name: UART_RX_Test - Test
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


library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity UART_RX_Test is
end entity UART_RX_Test;

architecture Test of UART_RX_Test is
    constant CLK_PERIOD     : time := 10 ns;        -- 100 MHz clock
    constant BAUD_PERIOD    : time := 8680 ns;       -- 115200 bps (1 / 115200)

    signal clk         : std_logic := '0';
    signal rx          : std_logic := '1';  -- UART idle = 1
    signal data_recv   : std_logic_vector(7 downto 0);
    signal rx_event    : std_logic;

    -- Para guardar los valores deseados
    type byte_array is array (0 to 4) of std_logic_vector(7 downto 0);
    signal value_G : byte_array := (others => (others => '0'));
    signal value_E : byte_array := (others => (others => '0'));

    signal token_index : integer := 0;
    signal collecting  : boolean := false; -- Empezamos sin recolectar hasta encontrar un número
    signal char_index  : integer range 0 to 5 := 0;
    signal temp_value  : byte_array := (others => (others => '0'));

    -- Simulador de entrada UART (envía un carácter ASCII por rx)
    procedure send_uart_char(signal rx_line : out std_logic; char : in character) is
        variable ascii_val : std_logic_vector(7 downto 0) := std_logic_vector(to_unsigned(character'pos(char), 8));
    begin
        -- Start bit
        rx_line <= '0';
        wait for BAUD_PERIOD;

        -- Data bits (LSB first)
        for i in 0 to 7 loop
            rx_line <= ascii_val(i);
            wait for BAUD_PERIOD;
        end loop;

        -- Stop bit
        rx_line <= '1';
        wait for BAUD_PERIOD;
    end procedure;

begin

    -- Reloj de 100 MHz
    clk_proc: process
    begin
        clk <= not clk;
        wait for CLK_PERIOD / 2;
    end process;

    -- Instancia del receptor UART (ya hecho por ti)
    uut: entity work.UART
        generic map (
            BAUD_CYCLES => 868
        )
        port map (
            clk         => clk,
            data_in     => (others => '0'),
            tx_send     => '0',
            tx_reset    => '0',
            rx          => rx,
            tx          => open,
            data_recv   => data_recv,
            tx_event    => open,
            rx_event    => rx_event
        );

    -- Simulación principal
    stimulus: process
        type str_array is array(natural range <>) of character;
        constant input_str : str_array := (
    
    '1', ',', ' ',                      -- 0
    '2', '3', ',', ' ',                 -- 1
    '4', '5', '6', ',', ' ',            -- 2
    '7', ',', ' ',                      -- 3
    '8', ',', ' ',                      -- 4
    '9', '0', ',', ' ',                 -- 5
    '1', '2', '3', '4', '5', ',', ' ',  -- 6 (G)
    'C', ',', ' ', -- 7
    'D', ',', ' ', -- 8
    'E', ',', ' ', -- 9
    'F', ',', ' ', -- 10
    'G', ',', ' ', -- 11
    'H', ',', ' ', -- 12
    'I', ',', ' ', -- 13
    'J', ',', ' ', -- 14
    'K', ',', ' ', -- 15 
    '4', '5', ',', ' ', -- 16 (E)
    '7', '8', '9', ' ', 'O', 'K'  -- 17 (último número)
);


    begin
        wait for 100 ns;

        -- Enviar caracteres UART uno a uno
        for i in input_str'range loop
            send_uart_char(rx, input_str(i));
        end loop;

        wait;
    end process;

    -- Monitoreo de rx_event y guardado de valores de canal G y E
    process(clk)
    begin
        if rising_edge(clk) then
            if rx_event = '1' then
                if data_recv = std_logic_vector(to_unsigned(character'pos(','), 8)) then -- Fin del número actual
                    
                    if token_index = 6 then -- Mirar si es el numero del valor G
                        value_G <= temp_value;
                    elsif token_index = 16 then -- Mirar si es el numero del valor E
                        value_E <= temp_value;
                    end if;
                    token_index <= token_index + 1;
                    collecting <= false;
                    char_index <= 0;
                    temp_value <= (others => (others => '0'));
                elsif data_recv = std_logic_vector(to_unsigned(character'pos(' '), 8)) then
                    -- Ignorar el espacio después de la coma si no estamos recolectando
                    if not collecting then
                        null; -- No hacer nada
                    else
                        if char_index < 5 then
                            temp_value(char_index) <= data_recv;
                            char_index <= char_index + 1;
                        end if;
                    end if;
                else
                    -- Recolectar el carácter si estamos en modo de recolección y no hemos llegado al límite
                    if collecting and char_index < 5 then
                        temp_value(char_index) <= data_recv;
                        char_index <= char_index + 1;
                    elsif token_index = 6 or token_index = 16 then
                        -- Empezar a recolectar el siguiente número después de la coma
                        collecting <= true;
                        temp_value(0) <= data_recv;
                        char_index <= 1;
                    end if;
                end if;
            end if;
        end if;
    end process;

end Test;
