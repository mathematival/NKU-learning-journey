private rule isMZ_PE {
    meta:
        description = "It is a PE file"
    condition:
        filesize <10MB and uint16(0) == 0x5A4D and uint32(uint32(0x3C)) == 0x00004550
        // 文件大小小于10MB，文件头的前两个字节为MZ，PE头的偏移地址处的四个字节为PE标志
}

// Lab07-01
rule Lab07_01_exe {
    meta:
        description = "It may like Lab07_01_exe"
    strings:
        $s1 = "http://www.malwareanalysisbook.com"
        $s2 = "Internet Explorer 8.0"
        $s3 = "MalService"
        $s4 = "HGL345"
    condition:
        isMZ_PE and all of them
}

// Lab07-02
rule Lab07_02_exe {
    meta:
        description = "It may like Lab07_02_exe"
    strings:
        $s1 = "http://www.malwareanalysisbook.com/ad.html" wide
        $s2 = "OleUninitialize"
        $s3 = "CoCreateInstance"
    condition:
        isMZ_PE and all of them
}

// Lab07-03
rule Lab07_03_exe {
    meta:
        description = "It may like Lab07_03_exe"
    strings:
        $s1 = "kerne132.dll"
        $s2 = "Lab07-03.dll"
        $s3 = "C:\\Windows\\System32\\Kernel32.dll"
    condition:
        isMZ_PE and all of them
}

rule Lab07_03_dll {
    meta:
        description = "It may like Lab07_03_dll"
    strings:
        $s1 = "127.26.152.13"
        $s2 = "Sleep"
        $s3 = "hello"
    condition:
        isMZ_PE and all of them
}