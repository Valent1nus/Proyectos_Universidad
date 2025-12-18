----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 17.05.2025 12:05:56
-- Design Name: 
-- Module Name: UART_TX_Test - Test
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

entity UART_TX_Test is
end entity UART_TX_Test;

architecture Test of UART_TX_Test is
    -- Señales internas
    signal clk         : std_logic := '0';                     -- Reloj principal de 100 MHz
    signal data_in     : std_logic_vector(7 downto 0);         -- Datos a transmitir
    signal tx_send     : std_logic := '0';                     -- Pulso para iniciar transmisión
    signal tx_reset    : std_logic := '0';                     -- Reset de la UART
    signal rx          : std_logic := '1';                     -- Entrada RX (no se usa, permanece en 1)
    signal tx          : std_logic;                            -- Salida TX
    signal data_recv   : std_logic_vector(7 downto 0);         -- Datos recibidos (no usados aquí)
    signal tx_event    : std_logic;                            -- Evento de fin de transmisión
    signal rx_event    : std_logic;                            -- Evento de recepción (no usado)

    constant BAUD_CYCLES : integer := 868;

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
    uut: UART
        generic map (BAUD_CYCLES => BAUD_CYCLES)
        port map (
            clk => clk,
            data_in => data_in,
            tx_send => tx_send,
            tx_reset => tx_reset,
            rx => rx,
            tx => tx,
            data_recv => data_recv,
            tx_event => tx_event,
            rx_event => rx_event
        );

    -- Clock generator
    reloj: process
    begin
        -- Genera un ciclo de reloj de 100 MHz
        clk <= not clk;
        wait for 10 ns;
    end process;


    stim_proc: process
    begin
        wait for 20 ns;
        tx_reset <= '1';
        wait for 10 ns;
        tx_reset <= '0';

        -- Enviar "ATDATA" letra por letra
        for char in 0 to 5 loop
            case char is
                when 0 => data_in <= x"41"; -- 'A'
                when 1 => data_in <= x"54"; -- 'T'
                when 2 => data_in <= x"44"; -- 'D'
                when 3 => data_in <= x"41"; -- 'A'
                when 4 => data_in <= x"54"; -- 'T'
                when 5 => data_in <= x"41"; -- 'A'
                when others => null;
            end case;

            tx_send <= '1';                    -- Se pone a 1 para permitir el envio del byte
            wait for 20 * BAUD_CYCLES * 10 ns; -- Espera 10 bits
            tx_send <= '0';                    -- Se pone a 0 para poner tx a 1 cuando terminamos de enviar un byte
            wait for 50 ns;
        end loop;

        wait;
    end process;
end Test;
