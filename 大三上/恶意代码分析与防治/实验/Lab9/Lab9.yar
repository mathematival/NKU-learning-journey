private rule isMZ_PE {
    meta:
        description = "It is a PE file"
    condition:
        filesize <10MB and uint16(0) == 0x5A4D and uint32(uint32(0x3C)) == 0x00004550
        // 文件大小小于10MB，文件头的前两个字节为MZ，PE头的偏移地址处的四个字节为PE标志
}

// Lab12-01
rule Lab12_01_exe {
    meta:
        description = "It may like Lab12_01_exe"
    strings:
        $s1 = "Lab12-01.dll"
        $s2 = "explorer.exe"
        $s3 = "psapi.dll"
    condition:
        isMZ_PE and all of them
}

rule Lab12_01_dll {
    meta:
        description = "It may like Lab12_01_dll"
    strings:
        $s1 = "Practical Malware Analysis %d"
        $s2 = "Press OK to reboot"
    condition:
        isMZ_PE and all of them
}

// Lab12-02
rule Lab12_02_exe {
    meta:
        description = "It may like Lab12_02_exe"
    strings:
        $s1 = "\\svchost.exe"
        $s2 = "AAAqAAApAAAsAAArAAAuAAAtAAAwAAAvAAAyAAAxAAA"
        $s3 = "LOCALIZATION"
    condition:
        isMZ_PE and all of them
}

// Lab12-03
rule Lab12_03_exe {
    meta:
        description = "It may like Lab12_03_exe"
    strings:
        $s1 = "practicalmalwareanalysis.log"
        $s2 = "[SHIFT]"
        $s3 = "[ENTER]"
        $s4 = "[BACKSPACE]"
        $s5 = "[TAB]"
        $s6 = "[CTRL]"
        $s7 = "[DEL]"
        $s8 = "[CAPS LOCK]"
    condition:
        isMZ_PE and all of them
}

rule Lab12_04_exe {
    meta:
        description = "It may like Lab12_04_exe"
    strings:
        $s1 = "http://www.practicalmalwareanalysis.com/updater.exe"
        $s2 = "<not real>"
        $s3 = "\\system32\\wupdmgr.exe"
        $s4 = "\\winup.exe"
        $s5 = "winlogon.exe"
    condition:
        isMZ_PE and all of them
}