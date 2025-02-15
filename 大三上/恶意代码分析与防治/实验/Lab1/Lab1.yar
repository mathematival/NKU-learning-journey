private rule isMZ_PE {
    meta:
        description = "It is a PE file"
    condition:
        filesize <10MB and uint16(0) == 0x5A4D and uint32(uint32(0x3C)) == 0x00004550
        // 文件大小小于10MB，文件头的前两个字节为MZ，PE头的偏移地址处的四个字节为PE标志
}

// Lab01-01
rule Lab01_01_exe {
    meta:
        description = "It may like Lab01-01.exe"
    strings:
        $string1 = "Lab01-01.dll"
        $string2 = "kerne132.dll"
        $string3 = "C:\\windows\\system32\\kerne132.dll"
    condition:
        isMZ_PE and all of them
}

rule Lab01_01_dll {
    meta:
        description = "It may like Lab01-01.dll"
    strings: 
        $string1 = "127.26.152.13"
        $string2 = "exec" 
        $string3 = "sleep"
        $string4 = "hello"
        $string5 = "WS2_32.dll"
    condition:
        isMZ_PE and all of them
}

// Lab01-02
rule Lab01_02_exe {
    meta:
        description = "It may like Lab01-02.exe"
    strings:
        $string1 = "HGL345"
        $string2 = "MalService" 
        $string3 = {68 74 74 70 3A 2F 2F 77 FF B7 BF DD 00 2E 6D 1E 77 61 72 65 61 6E 07 79 73 69 73 62 6F 6F 6B 2E 63 6F FF DB DB 6F 6D}
        $string4 = "InternetOpen"
    condition:
        isMZ_PE and all of them
}

// Lab01-03
rule Lab01_03_exe {
    meta:
        description = "It may like Lab01-03.exe"
    strings:
        $string1 = "ole32.vd"
        $string2 = "}OLEAUTLA" 
        $string3 = "_getmas"
    condition:
        isMZ_PE and all of them
}

// Lab01-04
rule Lab01_04_exe {
    meta:
        description = "It may like Lab01-04.exe"
    strings:
        $string1 = "\\winup.exe"
        $string2 = "\\system32\\wupdmgr.exe"
        $string3 = "http://www.practicalmalwareanalysis.com/updater.exe"
    condition:
        isMZ_PE and all of them
}
