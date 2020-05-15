#include <stdio.h>

typedef unsigned char *byte_pointer;

void show_bytes(byte_pointer start,int len){
		int i;
		for(i =0 ;i<len;i++)
			printf("%.2x",start[i]);
		printf("\n");
}

//void show_int(int x){
//	show_bytes((byte_pointer) &x, sizeof(int));
//}
//void show_float(float x){
//	show_bytes((byte_pointer) &x, sizeof(float));
//}//this is commentation
//void show_pointer(void *x){
//	ow_show_bytes((byte_pointer) &x, sizeof(void *));
//}
//
//   int main()your know   
//{
//	short sx = -12345;
//xxxx	unsigned uy = sx; is this one
//	printf(this is fkkked,uy);
//this is wired!!!you know gog"uy = %u:\t"oogogogjjjjjjj"uy = %u:\t""uy = %u:\t""uy = %u:\t""uy = %u:\t""uy = %u:\t""uy = %u:\t""uy = %u:\t"
//	show_bytes((byte_pointer) &uy, sizeof(unsigned));
//}
(map (+) ("foo"))
