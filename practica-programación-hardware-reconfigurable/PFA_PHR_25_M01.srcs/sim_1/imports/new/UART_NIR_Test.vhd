----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 17.05.2025 11:19:09
-- Design Name: 
-- Module Name: UART_NIR_Test - Test
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

entity UART_NIR_Test is
end entity UART_NIR_Test;

architecture Test of UART_NIR_Test is
    constant CLK_PERIOD     : time := 10 ns;          -- 100 MHz
    constant BAUD_PERIOD    : time := 8680 ns;        -- 115200 bps

    signal clk: std_logic;                           -- Reloj de sistema (100 MHz)
    signal rx: std_logic;                            -- Entrada UART (desde sensor)
    signal tx: std_logic;                            -- Salida UART (a sensor)
    signal value_G: std_logic_vector(39 downto 0);   -- Valor en ASCII, min 1 byte y max 5 bytes
    signal value_E : std_logic_vector(39 downto 0);       
    signal value_G_len: unsigned(2 downto 0);        -- Longitud de valor G
    signal value_E_len: unsigned(2 downto 0);
    signal send_ATDATA: std_logic;                   -- Pulso para enviar "ATDATA"
    signal tx_reset: std_logic;

    -- Simulador de entrada UART (1 carácter a rx)
    procedure send_uart_char(signal rx_line : out std_logic; char : in character) is
        variable ascii_val : std_logic_vector(7 downto 0) := std_logic_vector(to_unsigned(character'pos(char), 8));
    begin
        rx_line <= '0'; -- Start bit
        wait for BAUD_PERIOD;

        for i in 0 to 7 loop
            rx_line <= ascii_val(i);
            wait for BAUD_PERIOD;
        end loop;

        rx_line <= '1'; -- Stop bit
        wait for BAUD_PERIOD;
    end procedure;
    
    component UART_NIR is
            Port (
        clk      : in  std_logic;                           -- Reloj de sistema (100 MHz)
        rx       : in  std_logic;                           -- Entrada UART (desde sensor)
        tx       : out std_logic;                           -- Salida UART (a sensor)
        value_G  : out std_logic_vector(39 downto 0);       -- Valor en ASCII, min 1 byte y max 5 bytes
        value_E  : out std_logic_vector(39 downto 0);       
        value_G_len : out unsigned(2 downto 0);             -- Longitud de valor G
        value_E_len : out unsigned(2 downto 0);
        swt_ATDATA : in  std_logic;                      -- Pulso externo para enviar "ATDATA"
        tx_reset    : in std_logic
    );
    end component;
    
begin

    -- Instancia del módulo UART_NIR
    uut: UART_NIR
        port map (
            clk     => clk,
            rx      => rx,
            tx      => tx,
            value_G => value_G,
            value_E => value_E,
            value_G_len => value_G_len,
            value_E_len => value_E_len,
            swt_ATDATA => send_ATDATA,
            tx_reset => tx_reset
        );

    -- Reloj de 100 MHz
    clk_gen: process
    begin
        while true loop
            clk <= '0';
            wait for CLK_PERIOD / 2;
            clk <= '1';
            wait for CLK_PERIOD / 2;
        end loop;
    end process;

    -- Generación de pulso para enviar ATDATA
    stimulus: process
    type str_array is array(natural range <>) of character;
    constant input_str : str_array := (
        '1', ',', ' ',                  -- 0 R
        '2', ',', ' ',                  -- 1 S
        '3', ',', ' ',                  -- 2 T
        '4', ',', ' ',                  -- 3 U
        '5', ',', ' ',                  -- 4 V
        '6', ',', ' ',                  -- 5 W
        '0', '1', '2', '3', ',', ' ',   -- 6 G (guardado en value_G)
        '7', ',', ' ',                  -- 7 H
        '8', ',', ' ',                  -- 8 I
        '9', ',', ' ',                  -- 9 J
        '1', ',', ' ',                  -- 10 K
        '2', ',', ' ',                  -- 11 L
        '3', ',', ' ',                  -- 12 A
        '4', ',', ' ',                  -- 13 B
        '5', ',', ' ',                  -- 14 C
        '6', ',', ' ',                  -- 15 D
        '4', '5', '6', ',', ' ',        -- 16 E (guardado en value_E)
        '7', '8', '9', ' ', 'O', 'K'    -- 17 F
    );
    begin
        -- Esperamos un poco antes de comenzar
        wait for 2 * CLK_PERIOD;
    
        -- Poner tx al incio a 1 con tx_reset a 1 y luego a 0
        wait until rising_edge(clk);
        tx_reset <= '1';  -- Activa el reset
        wait for CLK_PERIOD;
        tx_reset <= '0';  -- Desactiva el reset
        wait for 20 ns;
        
        -- Activar el envío de ATDATA
        wait until rising_edge(clk);
        send_ATDATA <= '1';  -- Activar envio ATDATA
    
    
        wait for 1 ms; -- Espera envío de ATDATA
    
        -- Enviar la cadena simulada de datos UART
        for i in input_str'range loop
            send_uart_char(rx, input_str(i));  -- Simula la recepción de cada byte
            wait for BAUD_PERIOD * 8;  -- Espera entre caracteres (un byte completo)
        end loop;
        wait for 1 ms;
        send_ATDATA <= '0';
        wait for 1 ms;
        send_ATDATA <= '1';
        wait;
    end process;
    
    end Test;
