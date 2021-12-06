#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "sys/etimer.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

/* Declare and auto-start this file's process */
PROCESS(contiki_ng_coap_server, "Contiki-NG CoAP Observable Server");
AUTOSTART_PROCESSES(&contiki_ng_coap_server);

extern coap_resource_t res_iron;
extern coap_resource_t res_nitrogen;
extern coap_resource_t fertilize_actuator;

static struct etimer e_timer;

/*---------------------------------------------------------------------------*/
PROCESS_THREAD(contiki_ng_coap_server, ev, data) {
	
	PROCESS_BEGIN();

	PROCESS_PAUSE();

	LOG_INFO("CoAP Observable Server started\n");

	coap_activate_resource(&res_iron, "iron");
	coap_activate_resource(&res_nitrogen, "nitrogen");
	coap_activate_resource(&fertilize_actuator, "fertilize");

	etimer_set(&e_timer, CLOCK_SECOND * 4);

	printf("Loop\n");

	while (1) {
		PROCESS_WAIT_EVENT();

		if (ev == PROCESS_EVENT_TIMER && data == &e_timer) {
			printf("Event triggered\n");

			res_iron.trigger();
			res_nitrogen.trigger();

			etimer_set(&e_timer, CLOCK_SECOND * 5);
		}
	}

	PROCESS_END();
}
