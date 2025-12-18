-- Nombre de alumnos: Víctor Manuel, Rodrigo, Valentín Manuel, Lenin
-- Tareas:
--    PosAlt_Control ----->  (2,3-CONTROL DE ALABEO, CABECEO Y ALTITUD)
--    Speed   ------------>  (4-CONTROL DE VELOCIDAD - MOTORES)
--    Collision ---------->  (5- DETECCIÓN DE OBSTÁCULOS - COLISIONES)
--    Display ------------>  (7- VISUALIZACION)
-- Objeto protegido:
--    Pitch_Roll_Command
--    Current_Speed
--    Roll_Maniobra_Colision
--    record_Status
with Kernel.Serial_Output; use Kernel.Serial_Output;
with Ada.Real_Time; use Ada.Real_Time;
with System; use System;
with Tools; use Tools;
with devicesFSS_V1; use devicesFSS_V1;

package body fss is

type mensajes is array (1..4) of String(1..50);

Collision_Prio  : constant Integer := 5;
PosAlt_Prio : constant Integer := 4;
Speed_Prio  : constant Integer := 3;
Display_Prio: constant Integer:= 2;


procedure Background is
begin
   loop
      null;
   end loop;
end Background;

-----------------------------------------------------------------
--               DECLARACION OBJETOS PROTEGIDOS                --
-----------------------------------------------------------------

protected Pitch_Roll_Command is
   pragma Priority (PosAlt_Prio);
   procedure Get_Pitch_Protected (Pitch : out Pitch_Samples_Type);
   procedure Get_Roll_Protected (Roll : out Roll_Samples_Type);
   procedure Set_Pitch_Protected (Pitch : in Pitch_Samples_Type);
   procedure Set_Roll_Protected (Roll: in Roll_Samples_Type);
private
   Pitch_Protected : Pitch_Samples_Type := 0;
   Roll_Protected : Roll_Samples_Type := 0;
end Pitch_Roll_Command;


protected Current_Speed is 
   pragma Priority (Collision_Prio);
   procedure Get_Speed_Protected (Speed : out Speed_Samples_Type);
   procedure Set_Speed_Protected (Speed : in Speed_Samples_Type);
private
   Speed_Protected : Speed_Samples_Type := 0;
end Current_Speed;


protected Roll_Maniobra_Colision is
   pragma Priority (Collision_Prio);
   procedure Set_Bloqueo_Maniobra (Maniobra: in Boolean; Roll: in Roll_Samples_Type);
   procedure Set_Roll_Protected (Roll: in Roll_Samples_Type);
private
   Bloqueo_Maniobra: Boolean := False;
end Roll_Maniobra_Colision;

protected record_Status is
   pragma Priority(Collision_Prio);
   procedure set_Altitude_Display(altitud: in Altitude_Samples_Type);
   procedure set_Power_Display(potencia: in Power_Samples_Type);
   procedure set_Speed_Display(velocidad: in Speed_Samples_Type);
   procedure set_Joystick_Display(Joystick: in Joystick_Samples_Type);
   procedure set_Pitch_Display(pitch: in Pitch_Samples_Type);
   procedure set_Roll_Display(roll: in Roll_Samples_Type);
   procedure send_message_Display(mensaje: in String);
   procedure display_Status;
private
   altitud_Display : Altitude_Samples_Type := 0;
   potencia_Display : Power_Samples_Type := 0;
   velocidad_Display : Speed_Samples_Type := 0;
   joystick_Display : Joystick_Samples_Type:= (0,0);
   posicion_x_Display : Pitch_Samples_Type:= 0;
   posicion_y_Display : Roll_Samples_Type:= 0;
   mensajes_display : mensajes := (others => (others => ' '));
   i: Integer:= 1;
end record_Status;

-----------------------------------------------------------------
--                      DECLARACION TAREAS                     --
-----------------------------------------------------------------

task PosAlt_Control is
   pragma Priority (PosAlt_Prio);
end PosAlt_Control;

task Speed is
   pragma Priority (Speed_Prio);
end Speed;

task Collision is
   pragma Priority (Collision_Prio);
end Collision;

task Display is
   pragma Priority (Display_Prio);
end Display;

-----------------------------------------------------------------
--                 CUERPO DE OBJETOS PROTEGIDOS                --
-----------------------------------------------------------------


protected body Pitch_Roll_Command is
   procedure Get_Pitch_Protected (Pitch : out Pitch_Samples_Type) is
   begin
      Pitch := Pitch_Protected;
   end Get_Pitch_Protected;

   procedure Get_Roll_Protected (Roll : out Roll_Samples_Type) is
   begin
      Roll := Roll_Protected;
   end Get_Roll_Protected;

   procedure Set_Pitch_Protected (Pitch : in Pitch_Samples_Type) is
   begin
      Pitch_Protected := Pitch;
   end Set_Pitch_Protected;

   procedure Set_Roll_Protected (Roll: in Roll_Samples_Type) is
   begin
      Roll_Protected := Roll;
   end Set_Roll_Protected;
end Pitch_Roll_Command;


protected body Current_Speed is
   procedure Get_Speed_Protected (Speed : out Speed_Samples_Type) is
   begin
      Speed := Speed_Protected;
   end Get_Speed_Protected;

   procedure Set_Speed_Protected (Speed : in Speed_Samples_Type) is
   begin
      Speed_Protected := Speed;
   end Set_Speed_Protected;
end Current_Speed;


protected body Roll_Maniobra_Colision is
   procedure Set_Bloqueo_Maniobra (Maniobra: in Boolean; Roll: in Roll_Samples_Type) is
   begin
      Bloqueo_Maniobra := Maniobra;
      Set_Aircraft_Roll (Roll);
      
      
   end Set_Bloqueo_Maniobra;

   procedure Set_Roll_Protected (Roll: in Roll_Samples_Type) is
   begin
      if (Bloqueo_Maniobra = False) then
         Set_Aircraft_Roll (Roll);
         
      end if;
   end Set_Roll_Protected;

end Roll_Maniobra_Colision;


protected body record_Status is
   procedure set_Altitude_Display(altitud: in Altitude_Samples_Type) is
   begin
      altitud_Display:= altitud;
   end set_Altitude_Display;
   procedure set_Power_Display(potencia: in Power_Samples_Type) is
   begin
      potencia_Display:= potencia;
   end set_Power_Display;
   procedure set_Speed_Display(velocidad: in Speed_Samples_Type) is
   begin
      velocidad_Display:= velocidad;
   end set_Speed_Display;
   procedure set_Joystick_Display(Joystick: in Joystick_Samples_Type) is 
   begin
      joystick_Display:= Joystick;
   end set_Joystick_Display;
   procedure set_Pitch_Display(pitch: in Pitch_Samples_Type) is
   begin
      posicion_x_Display:= pitch;
   end set_Pitch_Display;
   procedure set_Roll_Display(roll: in Roll_Samples_Type) is
   begin
      posicion_y_Display:= roll;
   end set_Roll_Display;
   procedure send_message_Display(mensaje: in String) is
   begin
      if i > mensajes_display'Last then
   		return;
      end if;
      mensajes_display(i) := (others => ' ');
      mensajes_display(i)(mensajes_display(i)'First.. mensajes_display(i)'First + mensaje'Length - 1) := mensaje;
      i := i+1;
   end send_message_Display;
   procedure display_Status is
      First : constant Integer := mensajes_display'First;
      Last  : Integer          := i - 1;
   begin
      Display_Altitude (altitud_Display);
      Display_Pilot_Power (potencia_Display);
      Display_Speed (velocidad_Display);
      Display_Joystick (joystick_Display);
      Display_Pitch (posicion_x_Display);
      Display_Roll (posicion_y_Display);

      -- Normalizar "Last" para evitar rangos inválidos
      if Last < First then
         Last := First - 1;  -- sin mensajes -> bucle vacío
      elsif Last > mensajes_display'Last then
         Last := mensajes_display'Last;
      end if;

      for x in First .. Last loop
         Display_Message (mensajes_display(x));
         mensajes_display(x) := (others => ' ');
      end loop;

      -- Aquí SIEMPRE se reinicia i
      i := First;
   end display_Status;
end record_Status;


-----------------------------------------------------------------
--                       CUERPO DE TAREAS                      --
-----------------------------------------------------------------



task body PosAlt_Control is
         Siguiente_Instante: Time ;
         Intervalo: Time_Span := Milliseconds(200) ; -- 0.2 s, 200 ms

         J   : Joystick_Samples_Type := (0, 0);

         Cmd_Pitch : Pitch_Samples_Type := 0;
         PITCH_MAX  : constant Pitch_Samples_Type :=  30;
         PITCH_MIN  : constant Pitch_Samples_Type := -30;

         Alt : Altitude_Samples_Type := 0;
         ALT_WARN_L : constant Altitude_Samples_Type := 2500;
         ALT_SAFE_L : constant Altitude_Samples_Type := 2000;
         ALT_WARN_H : constant Altitude_Samples_Type := 9500;
         ALT_SAFE_H : constant Altitude_Samples_Type := 10000;

         Cmd_Roll : Roll_Samples_Type := 0;
         ROLL_MAX      : constant Roll_Samples_Type := 45;  
         ROLL_WARN_ABS : constant Roll_Samples_Type := 35;

      begin
         Siguiente_Instante:= Big_Bang + Intervalo;
         loop
            Start_Activity ("2,3-PosAlt_Control");

            --2- CONTROL DE CABECEO Y ALTITUD (PosAlt_Control)
            Read_Joystick (J);
            record_Status.set_Joystick_Display(J);

            Alt := Read_Altitude;


            if (Alt < ALT_WARN_L) or else (Alt > ALT_WARN_H) then
               Light_1 (On);
            else
               Light_1 (Off);
            end if;
            Cmd_Pitch := Pitch_Samples_Type(J(x));
            if Cmd_Pitch > PITCH_MAX then
               Cmd_Pitch := PITCH_MAX;
            elsif Cmd_Pitch < PITCH_MIN then
               Cmd_Pitch := PITCH_MIN;
            end if;

            if Alt <= ALT_SAFE_L or else Alt >= ALT_SAFE_H then
               Cmd_Pitch := 0; 
            end if;

            Pitch_Roll_Command.Set_Pitch_Protected(Cmd_Pitch);
            Set_Aircraft_Pitch (Cmd_Pitch);
            record_Status.set_Altitude_Display(Alt);
            record_Status.set_Pitch_Display(Cmd_Pitch);

            -- 3-CONTROL DE ALABEO

            Cmd_Roll := Roll_Samples_Type(J(y));

            if abs (Cmd_Roll) > ROLL_WARN_ABS then
               record_Status.send_message_Display("ALABEO > ±35: ");
            end if;

            if Cmd_Roll > ROLL_MAX then
               Cmd_Roll := ROLL_MAX;
            elsif Cmd_Roll < -ROLL_MAX then
               Cmd_Roll := -ROLL_MAX;
            end if;

            Pitch_Roll_Command.Set_Roll_Protected(Cmd_Roll);
            Roll_Maniobra_Colision.Set_Roll_Protected(Cmd_Roll);
            record_Status.set_Roll_Display(Cmd_Roll);

            Finish_Activity ("2,3-PosAlt_Control");
            delay until Siguiente_Instante;
            Siguiente_Instante := Siguiente_Instante + Intervalo;
         end loop;
end PosAlt_Control;


task body Speed is
         Siguiente_Instante: Time ;
         Intervalo: Time_Span := Milliseconds(300) ; -- 0.3 s, 300 ms

         Current_Pw: Power_Samples_Type := 0;
         Current_S: Speed_Samples_Type := 500; 
         Calculated_S: Speed_Samples_type := 0; 
               
         Current_Pitch: Pitch_Samples_Type := 0;
         Current_Roll : Roll_Samples_Type := 0;
    begin
         Siguiente_Instante:= Big_Bang + Intervalo;
         loop
            Start_Activity ("4-Speed");        
                   
            -- a. Leer potenciometro indicado por el piloto 
            Read_Power (Current_Pw);
  	    record_Status.set_Power_Display(Current_Pw);
                      
            -- b. Transfiere la potencia/velocidad a la aeronave
            Calculated_S := Speed_Samples_type (float (Current_Pw) * 1.2); -- aplicar fórmula

            Pitch_Roll_Command.Get_Pitch_Protected(Current_Pitch); -- Cabeceo
            Pitch_Roll_Command.Get_Roll_Protected(Current_Roll); -- Alabeo
           
            -- g. f. e. Aplicar velocidad en maniobras
            if (Current_Pitch > 10 and Current_Roll /= 0) then
               Calculated_S := Calculated_S + 200;
            elsif (Current_Roll /= 0) then
               Calculated_S := Calculated_S + 100;
            elsif (Current_Pitch > 10) then
               Calculated_S := Calculated_S + 150;
            end if;

            -- c. d. Limitar velocidad a máximo y mínimo
            if (Calculated_S >= 1000) then
               Calculated_S := Speed_Samples_Type(1000);
               Light_2(On);
            elsif (Calculated_S <= 300) then
               Calculated_S := Speed_Samples_Type(300);
               Light_2(On);
            else
               Light_2(Off);
            end if;
            Set_Speed(Calculated_S);
            Current_Speed.Set_Speed_Protected (Calculated_S);
            record_Status.set_Speed_Display(Calculated_S);
            
            Finish_Activity ("4-Speed");   
            delay until Siguiente_Instante;
            Siguiente_Instante := Siguiente_Instante + Intervalo;
         end loop;

    end Speed;

task body Collision is
         Siguiente_Instante: Time ;
         Intervalo: Time_Span := Milliseconds(250) ; -- 0.25 s, 250 ms

         DISTANCIA_OBJETO  : constant Distance_Samples_Type:= 5000;

         Current_Distance: Distance_Samples_Type:= 0;
         Current_PilotPresence: PilotPresence_Samples_Type := 0;
         Current_Light_Intensity: Light_Samples_Type := 0;
         Current_S : Speed_Samples_Type:= 0;
         
         Tiempo_Colision : Float:= 0.0;
         Tiempo_Aviso: Float:= 0.0;
         Tiempo_Maniobra: Float:= 0.0;

         Contador: Integer:= 0;
         Maniobra: Boolean := False;
         DESVIO_ROLL: constant Roll_Samples_Type := 45;

      begin
         Siguiente_Instante:= Big_Bang + Intervalo;
         loop

            Start_Activity ("5-COLISIONES");  

            Current_PilotPresence:= Read_PilotPresence;
            Read_Light_Intensity (Current_Light_Intensity);

            if(Current_Light_Intensity < 500 or Current_PilotPresence = 0) then
               Tiempo_Aviso:= 15.0;
               Tiempo_Maniobra:= 10.0;
            else
               Tiempo_Aviso:= 10.0;
               Tiempo_Maniobra:= 5.0;
            end if;

            --Calcular tiempo de colision (V=X/T -> T=X/V)
            Read_Distance (Current_Distance);
            Current_Speed.Get_Speed_Protected (Current_S);
            if Current_S /= 0 then
               Tiempo_Colision := (Float(Current_Distance) * 3600.0) /
                                  (Float(Current_S) * 1000.0); -- En segundos
            else
               Tiempo_Colision := Float'Last;  --  Tiempo infinito
            end if;
            --Display_Message ("Tiempo colision:" & Float'Image (Tiempo_Colision));

            if(Current_Distance <= DISTANCIA_OBJETO or Maniobra = True) then
               if(Tiempo_Colision <= Tiempo_Aviso) then
                  Alarm(4);
               else
                  Alarm(0);
               end if;

               if(Tiempo_Colision <= Tiempo_Maniobra or Maniobra = True) then
                  if(Contador = 0) then
                     Maniobra := True;
                     Roll_Maniobra_Colision.Set_Bloqueo_Maniobra (Maniobra, DESVIO_ROLL);
                     Contador := Contador + 1 ;
                     record_Status.send_message_Display ("INICIO MANIOBRA");
                  else
                     if (Contador <= 12) then -- 12 x 120ms = 3000ms = 3s de maniobra
                        Contador := Contador + 1;
                     else --Han pasado 3 segundos, terminar maniobra
                        Contador := 0;
                        Maniobra := False;
                        Roll_Maniobra_Colision.Set_Bloqueo_Maniobra (Maniobra, 0);
                        record_Status.send_message_Display ("FIN MANIOBRA");
                     end if;
                  end if;
               end if;

            end if;

            Finish_Activity ("5-COLISIONES");
            delay until Siguiente_Instante;
            Siguiente_Instante := Siguiente_Instante + Intervalo; 
         end loop;
      end Collision;

task body Display is
   Siguiente_Instante: Time ;
   Intervalo: Time_Span := Milliseconds(1000) ; -- 1 s
begin
   Siguiente_Instante:= Big_Bang + Intervalo;
   loop
      Start_Activity ("7- VISUALIZACION");
      record_Status.display_Status;
      Finish_Activity ("7- VISUALIZACION");
      delay until Siguiente_Instante;
      Siguiente_Instante := Siguiente_Instante + Intervalo; 
   end loop;

end Display;

begin
   Start_Activity ("Programa Principal");

   Finish_Activity ("Programa Principal");
end fss;