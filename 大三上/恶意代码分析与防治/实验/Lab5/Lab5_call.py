import idaapi
import idautils
import idc

# 检查指令是否是 'call'
def is_call_insn(ea):
    """
    检查给定地址的助记符是否为 'call'
    """
    mnemonic = idc.print_insn_mnem(ea)
    return mnemonic == 'call'

# 主函数
def main():
    # 遍历所有函数
    for func in idautils.Functions():
        # 遍历函数中的所有指令
        ea = func
        func_end = idc.find_func_end(func)
        while ea != idaapi.BADADDR and ea < func_end:
            # 如果是调用指令
            if is_call_insn(ea):
                print("Address: 0x{:X}, Instruction: {}".format(ea, idc.generate_disasm_line(ea, 0)))
            # 继续处理下一条指令
            ea = idc.next_head(ea)

if __name__ == '__main__':
    main()