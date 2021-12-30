#include "utility.h"

static void put_handler(coap_message_t* request, coap_message_t* response, uint8_t* buffer, uint16_t preferred_size, int32_t* offset);

RESOURCE(fertilize_actuator,
	"title=\"FertilizeActuator\";rt=\"Control\"",
	NULL,
	NULL,
	put_handler,
	NULL);

static void
put_handler(coap_message_t* request, coap_message_t* response, uint8_t* buffer, uint16_t preferred_size, int32_t* offset)
{
	size_t len = 0;
	const uint8_t* payload = NULL;
	int success = 1;

	if ((len = coap_get_payload(request, &payload))) {

		if (strncmp((char*)payload, "ON", len) == 0) {
			add_fertilize = 0.3;
			LOG_INFO("Feritilization ON\n");
			leds_set(LEDS_NUM_TO_MASK(LEDS_GREEN));
		}
		else if (strncmp((char*)payload, "OFF", len) == 0) {
			add_fertilize = 0;
			LOG_INFO("Feritilization OFF\n");
			leds_set(LEDS_NUM_TO_MASK(LEDS_RED));
		}
		else {
			success = 0;
		}

	}

	if (!success)
		coap_set_status_code(response, BAD_REQUEST_4_00);
}
