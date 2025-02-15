private rule isMZ_PE {
    meta:
        description = "It is a PE file"
    condition:
        filesize <10MB and uint16(0) == 0x5A4D and uint32(uint32(0x3C)) == 0x00004550
        // 文件大小小于10MB，文件头的前两个字节为MZ，PE头的偏移地址处的四个字节为PE标志
}

// Lab13-01
rule Lab13_01_exe {
    meta:
        description = "It may like Lab13_01_exe"
    strings:
        $s1 = "Mozilla/4.0"
        $s2 = "http://%s/%s/"
        $s3 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    condition:
        isMZ_PE and all of them
}

// Lab13-02
rule Lab13_02_exe {
    meta:
        description = "It may like Lab13_02_exe"
    strings:
        $s1 = "temp%08x"
        $s2 = "GDI32.dll"
        $s3 = "GetTickCount"
    condition:
        isMZ_PE and all of them
}

// Lab13-03
rule Lab13_03_exe {
    meta:
        description = "It may like Lab13_03_exe"
    strings:
        $s1 = "CDEFGHIJKLMNOPQRSTUVWXYZABcdefghijklmnopqrstuvwxyzab0123456789+/"
        $s2 = "ijklmnopqrstuvwx"
        $s3 = "www.practicalmalwareanalysis.com"
        $s4 = "cmd.exe"
    condition:
        isMZ_PE and all of them
}
