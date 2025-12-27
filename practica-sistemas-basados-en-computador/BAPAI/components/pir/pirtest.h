#ifndef PIR_H
#define PIR_H

#ifdef __cplusplus
extern "C" {
#endif

#define PIR_GPIO_DEFAULT   27

/* Inicia el sensor PIR en un GPIO (si pasas -1 usa el default). */
void pir_init(int gpio);

/* Tarea que monitorea continuamente el PIR */
int pir_task(void);

/* Inicializa + crea tarea automáticamente */
void pir_start(void);

#ifdef __cplusplus
}
#endif

#endif // PIR_H
