#pragma once
#include <stdint.h>
#include <stdbool.h>

bool ota_manager_start(uint32_t task_stack, uint32_t task_prio);


#ifdef __cplusplus
}
#endif
