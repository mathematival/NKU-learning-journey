private rule isMZ_PE {
    meta:
        description = "It is a PE file"
    condition:
        filesize <10MB and uint16(0) == 0x5A4D and uint32(uint32(0x3C)) == 0x00004550
        // 文件大小小于10MB，文件头的前两个字节为MZ，PE头的偏移地址处的四个字节为PE标志
}

// Lab11-01
rule Lab11_01_exe {
    meta:
        description = "It may like Lab11_01_exe"
    strings:
        $s1 = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon"
        $s2 = "GinaDLL"
        $s3 = "msgina32.dll"
    condition:
        isMZ_PE and all of them
}

// Lab11-02
rule Lab11_02_dll {
    meta:
        description = "It may like Lab11_02_dll"
    strings:
        $s1 = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Windows"
        $s2 = "spoolvxx32.dll"
        $s3 = "THEBAT.EXE"
        $s4 = "OUTLOOK.EXE"
        $s5 = "MSIMN.EXE"
        $s6 = "AppInit_DLLs"
        $s7 = "Lab11-02.ini"
    condition:
        isMZ_PE and all of them
}

// Lab11-03
rule Lab11_03_exe {
    meta:
        description = "It may like Lab11_03_exe"
    strings:
        $s1 = "C:\\WINDOWS\\System32\\inet_epar32.dll"
        $s2 = "Lab11-03.dll"
        $s3 = "net start cisvc"
        $s4 = "zzz69806582"
    condition:
        isMZ_PE and all of them
}

rule Lab11_03_dll {
    meta:
        description = "It may like Lab11_03_dll"
    strings:
        $s1 = "C:\\WINDOWS\\System32\\kernel64x.dll"
        $s5 = "<SHIFT>"
        $s6 = "zzz69806582"
        $s7 = "Lab1103dll.dll"
    condition:
        isMZ_PE and all of them
}