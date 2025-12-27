#ifndef MQ135_H
#define MQ135_H

#ifdef __cplusplus
extern "C" {
#endif

/* Inicializa ADC y comienza la lectura continua del MQ135.
   Esta función NO termina (tarea bloqueante). */
float mq135_task(void);
void mq135_start(void);


#ifdef __cplusplus
}
#endif

#endif // MQ135_H
