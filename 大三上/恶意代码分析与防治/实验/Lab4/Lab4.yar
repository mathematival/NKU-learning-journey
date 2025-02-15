private rule isMZ_PE {
    meta:
        description = "It is a PE file"
    condition:
        filesize <10MB and uint16(0) == 0x5A4D and uint32(uint32(0x3C)) == 0x00004550
        // 文件大小小于10MB，文件头的前两个字节为MZ，PE头的偏移地址处的四个字节为PE标志
}

// Lab05-01
rule Lab05_01_dll {
    meta:
        description = "It may like Lab05_01_dll"
    strings:
        $s1 = "\\command.exe /c"
        $s2 = "\\cmd.exe /c"
        $s3 = "pics.praticalmalwareanalysis.com"
        $s4 = "Remote Shell Session"
        $s5 = "robotwork"
        $s6 = "socket() GetLastError reports %d"
    condition:
        isMZ_PE and all of them
}