#include "utility.h"

static void res_get_handler(coap_message_t* request, coap_message_t* response, uint8_t* buffer, uint16_t preferred_size, int32_t* offset);
static void res_event_handler(void);

static float iron_value = 2.5;
static bool increase = true;

/* Define event resouce */
EVENT_RESOURCE(res_iron,
    "title=\"Iron\";rt=\"Iron\";obs",
    res_get_handler,
    NULL,
    NULL,
    NULL,
    res_event_handler);

static void
res_event_handler(void)
{
    coap_notify_observers(&res_iron);
}

static void
res_get_handler(coap_message_t* request, coap_message_t* response, uint8_t* buffer, uint16_t preferred_size, int32_t* offset)
{

    if (request != NULL) {
        LOG_DBG("Received GET\n");
    }

    if (increase) {
	iron_value += generate_random_value(0.4);
	increase = false;
    }
    else {
	iron_value -= generate_random_value(0.3);
	increase = true;
    }

    iron_value += add_fertilize;

    printf("add_fertilize=%f/n", add_fertilize);

    unsigned int accept = -1;
    coap_get_header_accept(request, &accept);

    if (accept == TEXT_PLAIN) {
        coap_set_header_content_format(response, TEXT_PLAIN);
	snprintf((char*)buffer, COAP_MAX_CHUNK_SIZE, "node=%d, iron_value=%.2f", node_id, iron_value);
        coap_set_payload(response, (uint8_t*)buffer, strlen((char*)buffer));

    }
    else if (accept == APPLICATION_XML) {
        coap_set_header_content_format(response, APPLICATION_XML);
	snprintf((char*)buffer, COAP_MAX_CHUNK_SIZE, "<node=\"%d\"/><iron_value=\"%.2f\"/>", node_id, iron_value);
        coap_set_payload(response, buffer, strlen((char*)buffer));

    }
    else if (accept == -1 || accept == APPLICATION_JSON) {
        coap_set_header_content_format(response, APPLICATION_JSON);
	snprintf((char*)buffer, COAP_MAX_CHUNK_SIZE, "{\"node\":%d,\"iron_value\":%.2f}", node_id, iron_value);
        coap_set_payload(response, buffer, strlen((char*)buffer));

    }
    else {
        coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
        const char* msg = "Supporting content-types text/plain, application/xml, and application/json";
        coap_set_payload(response, msg, strlen(msg));
    }   
}
