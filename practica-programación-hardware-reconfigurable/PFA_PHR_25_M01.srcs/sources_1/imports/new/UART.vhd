----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 13.05.2025 14:32:20
-- Design Name: 
-- Module Name: UART - RTL
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

entity UART is
    generic (
        BAUD_CYCLES : integer := 868  -- Número de ciclos del reloj principal por bit UART (para 115200 bps a 100 MHz)
    );
    port (
        clk: in std_logic;                           -- Reloj del sistema
        data_in: in std_logic_vector(7 downto 0);    -- Datos a transmitir (8 bits)
        tx_send: in std_logic;                       -- Pulso para iniciar transmisión
        tx_reset: in std_logic;                      -- Reset para el transmisor
        rx: in std_logic;                            -- Entrada de datos serial (desde otro UART)
        tx: out std_logic;                           -- Salida de datos serial
        data_recv: out std_logic_vector(7 downto 0); -- Datos recibidos (8 bits)
        tx_event: out std_logic;                     -- Pulso al finalizar la transmisión de 1 byte
        rx_event: out std_logic                      -- Pulso cuando se completa la recepción
    );
end UART;

architecture RTL of UART is
    -- Señal para dividir el reloj base a frecuencia de baudios (baudrate)
    signal div_clk         : std_logic := '0';
    
    -- Buffer de transmisión y recepcion(10 bits: start + 8 datos + stop)
    signal tx_buffer_data  : std_logic_vector(9 downto 0) := (others => '0');
    signal rx_buffer_data  : std_logic_vector(7 downto 0) := (others => '0');
    
    -- Registro de salida para los datos recibidos
    signal rx_out_data     : std_logic_vector(7 downto 0) := (others => '0');
    
begin

    
    tx_buffer_data <= '1' & data_in & '0'; -- Formato UART: Start bit (0) + 8 bits de datos + Stop bit (1)
    data_recv <= rx_out_data;
    
    baud_clk_divider : process(clk)
        variable baud_current : integer range 0 to BAUD_CYCLES := 0;
    begin
        if rising_edge(clk) then
            if baud_current < (BAUD_CYCLES - 1) then
                baud_current := baud_current + 1;
                div_clk <= '0';
            else
                div_clk <= '1';
                baud_current := 0;
            end if;
        end if;
    end process;
    
    -- Transmisión UART
    tx_process : process(div_clk, tx_reset)
        variable tx_current_bit : integer range 0 to 9 := 0;
    begin
        tx_event <= '0';
        if tx_reset = '1' then
            tx <= '1'; -- tx si no envía nada se pone en 1
            tx_current_bit := 0;
        elsif rising_edge(div_clk) then
            if tx_send = '0' then -- No se envía nada, mantener tx en alto
                tx <= '1';
            else
                    tx <= tx_buffer_data(tx_current_bit); -- Envio de bit en bit por tx
                if tx_current_bit < 9 then
                    tx_current_bit := tx_current_bit + 1;
                else
                    tx_event <= '1'; -- Indicar que se acaba de enviar un byte y reiniciar contador
                    tx_current_bit := 0;
                end if;
            end if;
        end if;
    end process;
    
    -- Recepción UART
    rx_process : process(div_clk)
        variable rx_current_bit : integer range 0 to 9 := 0;
    begin
        rx_event <= '0';
        if rising_edge(div_clk) then
            if rx_current_bit = 9 then -- Se ha recibido start bit (0) + 8 bits de datos + stop bit (1)
                rx_current_bit := 0;
                if rx = '1' then -- Asegurar que el último bit es el stop bit
                    rx_event <= '1';
                    rx_out_data <= rx_buffer_data;
                end if;
            elsif rx_current_bit > 0 then -- Almacenar los 8 bits de datos
                rx_buffer_data(rx_current_bit - 1) <= rx;
                rx_current_bit := rx_current_bit + 1;
            elsif rx = '0' then  -- Detección de start bit (0)
                rx_current_bit := 1;
            end if;
        end if;
    end process;
end RTL;
