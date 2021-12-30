#include "utility.h"

float add_fertilize = 0;

float generate_random_value(float upper) 
{ 
	float num = ((float)rand()/(float)(RAND_MAX)) * upper; 
	return num;
}
