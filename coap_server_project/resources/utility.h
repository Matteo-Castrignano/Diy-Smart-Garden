#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "coap-engine.h"
#include <sys/node-id.h>
#include "dev/leds.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

extern float add_fertilize;

float generate_random_value(float upper);

