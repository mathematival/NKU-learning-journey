; LLVM IR 示例：展示各种语言特性
; 目标三元组
target triple = "x86_64-pc-linux-gnu"

; ---- 全局变量和常量 ----

; 定义全局整型数组arr
@arr = global [5 x i32] [i32 1, i32 2, i32 3, i32 4, i32 5]

; 定义换行符和空格字符串常量
;unnamed_addr属性表明该变量或常量的地址不会被直接引用,\00是字符串的null终止符
@str_newline = private unnamed_addr constant [2 x i8] c"\0A\00"
@str_space = private unnamed_addr constant [2 x i8] c" \00"

; 定义结构体MyStruct
%MyStruct = type { i32, float }

; 定义全局结构体变量global_struct
@global_struct = global %MyStruct { i32 10, float 3.25 }

; 定义全局函数指针变量，初始化为 null
@function_ptr = global ptr null

; 定义 switch 语句用的字符串常量
@str_case_ten = private unnamed_addr constant [23 x i8] c"Switch case: Ten (10)\0A\00"
@str_case_nine = private unnamed_addr constant [23 x i8] c"Switch case: Nine (9)\0A\00"
@str_case_eight = private unnamed_addr constant [24 x i8] c"Switch case: Eight (8)\0A\00"
@str_default_case = private unnamed_addr constant [27 x i8] c"Switch case: Default case\0A\00"

; ---- 外部函数声明 ----
declare void @putint(i32)
declare void @putfloat(float)
declare void @putarray(i32, ptr)
declare void @putf(ptr, ...)
declare i32 @getint()
declare float @getfloat()
declare ptr @malloc(i64)
declare void @free(ptr)

; ---- 辅助函数 ----

; 输出换行符
define void @output_newline() {
    call void @putf(ptr @str_newline)
    ret void
}

; 输出空格
define void @output_space() {
    call void @putf(ptr @str_space)
    ret void
}

; 定义 add_ten 函数：int add_ten(int x) { return x + 10; }
define i32 @add_ten(i32 %x) {
    %result = add i32 %x, 10
    ret i32 %result
}

; ---- 功能展示函数 ----

define void @demonstrate_features() {
    ; 使用全局数组
    call void @putarray(i32 5, ptr @arr)
    call void @output_newline()

    ; 结构体
    ; 获取结构体成员
    ;getelementptr指令用于计算给定类型的元素在内存中的地址,inbounds关键字用于优化,i32 0指定结构体实例的索引（在这个例子中是第一个，也是唯一一个实例）
    %global_int = load i32, ptr getelementptr inbounds (%MyStruct, ptr @global_struct, i32 0, i32 0)
    %global_float = load float, ptr getelementptr inbounds (%MyStruct, ptr @global_struct, i32 0, i32 1)
    ; 输出结构体成员
    call void @putint(i32 %global_int)
    call void @output_space()
    call void @putfloat(float %global_float)
    call void @output_newline()

    ; 循环和条件语句
    ; 实现 for (int i = input; i < end; i++) { if (i != 5) printf("%d", i); }
    ; 为循环变量i分配栈空间
    %i = alloca i32
    ; 输入循环开始、结束和跳过的整数
    %input_int = call i32 @getint()
    store i32 %input_int, ptr %i
    %end = call i32 @getint()
    %not_print = call i32 @getint()
    br label %loop_start

loop_start:
    ; 加载i的值
    %i_val = load i32, ptr %i
    ; 比较i是否小于end
    %cond = icmp slt i32 %i_val, %end
    ; 根据比较结果跳转
    br i1 %cond, label %loop_body, label %loop_end

loop_body:
    ; 检查i是否等于不需要打印的整数
    %is_five = icmp eq i32 %i_val, %not_print
    ; 如果i等于5，跳过打印
    br i1 %is_five, label %continue, label %print

print:
    ; 打印i的值
    call void @putint(i32 %i_val)
    call void @output_space()
    br label %continue

continue:
    ; i自增
    %next_i = add i32 %i_val, 1
    store i32 %next_i, ptr %i
    ; 跳回循环开始
    br label %loop_start

loop_end:
    call void @output_newline()

    ; Switch语句
    ; 实现 switch (i) { case 10: ... case 9: ... case 8: ... default: ... }
    ; 输入整数i
    %final_i = call i32 @getint()
    switch i32 %final_i, label %default_case [
        i32 10, label %case_ten
        i32 9, label %case_nine
        i32 8, label %case_eight
    ]

case_ten:
    call void @putf(ptr @str_case_ten)
    br label %switch_end

case_nine:
    call void @putf(ptr @str_case_nine)
    br label %switch_end

case_eight:
    call void @putf(ptr @str_case_eight)
    br label %switch_end

default_case:
    call void @putf(ptr @str_default_case)
    br label %switch_end

switch_end:
    ; 浮点数和位运算操作
    ; 计算 a * b
    %a = call float @getfloat()
    %b = call float @getfloat()
    %sum = fmul float %a, %b
    call void @putfloat(float %sum)
    call void @output_newline()
    ; 计算 c & d
    %c = call i32 @getint()
    %d = call i32 @getint()
    %and_result = and i32 %c, %d
    call void @putint(i32 %and_result)
    call void @output_newline()

    ; 函数指针调用
    ; 实现 function_ptr = &add_ten; printf("%d", function_ptr(in));
    ; 将add_ten函数的地址存储到函数指针
    store ptr @add_ten, ptr @function_ptr
    ; 加载函数指针
    %func = load ptr, ptr @function_ptr
    ; 通过函数指针调用函数,调用输入函数传入参数in
    %in = call i32 @getint()
    %result = call i32 %func(i32 %in)
    ; 打印结果
    call void @putint(i32 %result)
    call void @output_newline()

    ; 动态内存分配
    ; 实现 int *ptr = (int *)malloc(20); ptr[0] = 10; ptr[1] = 20; ptr[2] = 30;
    ; 分配20字节的内存,足够存储5个int类型的值
    %ptr = call ptr @malloc(i64 20)
    ; 设置数组元素
    %element0 = getelementptr i32, ptr %ptr, i32 0
    store i32 10, ptr %element0
    %element1 = getelementptr i32, ptr %ptr, i32 1
    store i32 20, ptr %element1
    %element2 = getelementptr i32, ptr %ptr, i32 2
    store i32 30, ptr %element2
    ; 打印数组
    call void @putarray(i32 5, ptr %ptr)
    call void @output_newline()
    ; 释放内存
    call void @free(ptr %ptr)

    ret void
}

; ---- 主函数 ----
; 定义主函数：int main() { demonstrate_features(); return 0; }
define i32 @main() {
    call void @demonstrate_features()
    ret i32 0
}
