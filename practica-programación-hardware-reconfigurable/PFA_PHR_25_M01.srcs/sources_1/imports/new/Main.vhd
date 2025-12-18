----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 17.05.2025 11:19:09
-- Design Name: 
-- Module Name: Main - Behavioral
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

entity Main is
    Port (
        clk: in STD_LOGIC;        -- Reloj principal (100 MHz, pin W5)
        bomba1 : out STD_LOGIC;   -- JA7 (bomba1)
        bomba2 : out STD_LOGIC;   -- JA8 (bomba2)
        rx: in STD_LOGIC;         -- JA1 (recepción UART, cable verde)
        tx: out STD_LOGIC;        -- JA2 (transmisión UART, cable blanco)
        swt_A: in STD_LOGIC;      -- U17 (control manual bomba1)
        swt_B: in STD_LOGIC;      -- V16 (control manual bomba2)
        led_A: out STD_LOGIC;     -- U16 (indicador bomba1)
        led_B: out STD_LOGIC;     -- E19 (indicador bomba2)
        swt_ATDATA: in STD_LOGIC  -- R2  (activar el envio de ATDATA)
    );
end entity Main;

architecture Behavioral of Main is

    -- Señales para activar las bombas durante 5 segundos
    constant tiempo_bomba : integer := 500_000_000; -- 5 segundos a 100 MHz
    signal contador_bomba : integer := 0;
    
    signal activar_bomba1 : std_logic := '0';
    signal activar_bomba2 : std_logic := '0';
    signal activar_5s_bomba1 : std_logic := '0';
    signal activar_5s_bomba2 : std_logic := '0';
    
    signal espera_completada_bomba : std_logic := '0';
    
    -- Señales para los valores recibidos de los canales G y E
    constant umbral_G: std_logic_vector(39 downto 0):= x"3031323030"; --01200
    constant umbral_E: std_logic_vector(39 downto 0):= x"3031303030"; --01000
    
    signal value_G  : std_logic_vector(39 downto 0);
    signal value_E  : std_logic_vector(39 downto 0);
    signal value_G_len : unsigned(2 downto 0);
    signal value_E_len : unsigned(2 downto 0);
    
    signal valor_desplazado_G : std_logic_vector(39 downto 0) := (others => '1');
    signal valor_desplazado_E : std_logic_vector(39 downto 0) := (others => '0');
    signal valores_actualizados: std_logic := '0';
    
    -- Señales para poner al inicio tx a 1
    constant reset_delay : integer := 1_000_000;
    signal reset_counter : integer := 0;
    signal tx_reset: std_logic := '1';
    
    -- Señales de un contador para esperar la actualizazión de datos
    constant tiempo_espera : integer := 200_000_000;  -- 2 segundos a 100 MHz
    signal contador_espera : integer := 0;
    signal espera_completada : std_logic := '0';
    
    -- La funcion permite desplazar los bytes de los valores del sensor para poder comparar bien las constantes de G y E
    -- Por ejemplo, el valor recibido de G es x"3132000000", la funcion devuelve x"3030303132"
    function desplazar_bytes(input: std_logic_vector(39 downto 0); num_bytes: unsigned(2 downto 0)) return std_logic_vector is
        variable output : std_logic_vector(39 downto 0) := (others => '0');
    begin
        if num_bytes = 1 then
            output := x"30303030" & input(39 downto 32);
        elsif num_bytes = 2 then
            output := x"303030" & input(39 downto 24);
        elsif num_bytes = 3 then
            output := x"3030" & input(39 downto 16);
        elsif num_bytes = 4 then
            output := x"30" & input(39 downto 8);
        elsif num_bytes = 5 then
            output := input(39 downto 0);
        else
            output := x"3030303030";
        end if;
        return output;
    end function;

    component UART_NIR
        Port (
            clk      : in  std_logic;
            rx       : in  std_logic;
            tx       : out std_logic;
            value_G  : out std_logic_vector(39 downto 0);
            value_E  : out std_logic_vector(39 downto 0);
            value_G_len : out unsigned(2 downto 0);
            value_E_len : out unsigned(2 downto 0);
            swt_ATDATA : in  std_logic;
            tx_reset   : in std_logic
        );
    end component;

begin

    UART_INST : UART_NIR
        port map (
            clk => clk,
            rx => rx,
            tx => tx,
            value_G => value_G,
            value_E => value_E,
            value_G_len => value_G_len,
            value_E_len => value_E_len,
            swt_ATDATA => swt_ATDATA,
            tx_reset => tx_reset
        );
    
    -- Procesar los valores G y E para hacerlos comparables con los umbrales
    --valor_desplazado_G <= desplazar_bytes(value_G, value_G_len);
    --valor_desplazado_E <= desplazar_bytes(value_E, value_E_len);
    
    -- Poner al inicio de la ejecucion tx a 1, manteniendo tx_reset a 1 cierto tiempo 
    reset_process : process(clk)
    begin
        if rising_edge(clk) then
            if reset_counter < reset_delay then
                tx_reset <= '1';
                reset_counter <= reset_counter + 1;
            else
                tx_reset <= '0';
            end if;
        end if;
    end process;


    -- Para activar las bombas 5 segundos se comparan los valores G y E con sus umbrales
    activar_bombas : process(clk)
    begin
        if rising_edge(clk) then
            if swt_ATDATA = '1' then
               if espera_completada = '0' then -- Contador para esperar a recibir los datos del sensor despues de enviar ATDATA
                    if contador_espera < tiempo_espera then
                        contador_espera <= contador_espera + 1;
                    else
                        espera_completada <= '1';
                        contador_espera <= 0;
                    end if;
               elsif valores_actualizados = '0' then
    
                    if valor_desplazado_G < umbral_G then -- (Activar bomba 2, que es echar el bote B)
                        activar_bomba2 <= '1'; 
                    elsif valor_desplazado_E > umbral_E then -- (Activar bomba 1, que es echar el bote A)
                        activar_bomba1 <= '1';
                    end if;
                    
                    valores_actualizados <= '1'; -- Evita que se activen las bombas constantemente    
               end if;
                
               if activar_bomba2 = '1' then -- Las señales internas ahora activan la señal que enciende a la bomba
                    activar_5s_bomba2 <= '1';
               elsif activar_bomba1 = '1' then 
                    activar_5s_bomba1 <= '1';
               end if;
                
               if activar_bomba1 = '1' or activar_bomba2 = '1' then -- Contador que activa temporalmente las bombas
                    if espera_completada_bomba = '0' then
                        if contador_bomba < tiempo_bomba then
                            contador_bomba <= contador_bomba + 1;
                        else
                            espera_completada_bomba <= '1';
                            contador_bomba <= 0;
                        end if;   
                    else 
                        activar_bomba1 <= '0';
                        activar_bomba2 <= '0';
                        activar_5s_bomba1 <= '0';
                        activar_5s_bomba2 <= '0';
                        espera_completada_bomba <= '0';
                    end if;
               end if; 
               
            else
                contador_espera <= 0;
                espera_completada <= '0';
                valores_actualizados <= '0';
                
                activar_bomba1 <= '0';
                activar_bomba2 <= '0';
                activar_5s_bomba1 <= '0';
                activar_5s_bomba2 <= '0';
                espera_completada_bomba <= '0';
            end if;
           
            -- Salidas combinadas, manual (switches) o automatico
            bomba1 <= swt_A or activar_5s_bomba1;
            bomba2 <= swt_B or activar_5s_bomba2;

            led_A <= swt_A or activar_5s_bomba1;
            led_B <= swt_B or activar_5s_bomba2;
        end if;
    end process;

end Behavioral;
