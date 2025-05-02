#include <stdio.h>
#include <stdlib.h>

void infiniteRecursion();
void bigMalloc();
void runTimeErr();

void main(void) {
    int a, b, c;
    scanf("%d%d%d", &a, &b, &c);
    printf("%d\n", a + b + c);
    infiniteRecursion();
}

// 无限递归函数
void infiniteRecursion() {
    infiniteRecursion(); // 递归调用自身
}

void bigMalloc() {
    while (1){}
}

void runTimeErr() {
    int a = 1 / 0;
}