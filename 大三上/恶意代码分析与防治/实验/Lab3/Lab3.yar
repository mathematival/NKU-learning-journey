private rule isMZ_PE {
    meta:
        description = "It is a PE file"
    condition:
        filesize <10MB and uint16(0) == 0x5A4D and uint32(uint32(0x3C)) == 0x00004550
        // 文件大小小于10MB，文件头的前两个字节为MZ，PE头的偏移地址处的四个字节为PE标志
}

// Lab03-01
rule Lab03_01_exe {
    meta:
        description = "It may like Lab03-01.exe"
    strings:
        $s1 = "vmx32to64.exe"
        $s2 = "WinVMX32-"
        $s3 = "StubPath"
        $s4 = "SOFTWARE\\Classes\\http\\shell\\open\\commandV"
        $s5 = "Software\\Microsoft\\Active Setup\\Installed Components\\"
        $s6 = "www.practicalmalwareanalysis.com"
        $s7 = "VideoDriver"
        $s8 = "AppData"
    condition:
        isMZ_PE and all of them
}

// Lab03-02
rule Lab03_02_dll {
    meta:
        description = "It may like Lab03-02.dll"
    strings:
        $s1 = "IPRIP"
        $s2 = "svchost.exe"
        $s3 = "OpenService"
        $s4 = "CreateService"
        $s5 = "RegOpenKeyEx"
        $s6 = "RegQueryValueEx"
        $s7 = "cmd.exe"
        $s8 = "Lab03-02.dll"
        $s9 = "practicalmalwareanalysis.com"
    condition:
        isMZ_PE and all of them
}

// Lab03-03
rule Lab03_03_exe {
    meta:
        description = "It may like Lab03-03.exe"
    strings:
        $s1 = "\\svchost.exe"
        $s2 = "Sleep"
        $s3 = "ntdll.dll"
    condition:
        isMZ_PE and all of them
}

// Lab03-04
rule Lab03_04_exe {
    meta:
        description = "It may like Lab03-04.exe"
    strings:
        $s1 = "/c del "
        $s2 = "cmd.exe"
        $s3 = " >> NUL"
        $s4 = "SOFTWARE\\Microsoft \\XPS"
        $s5 = "HTTP/1.0"
        $s6 = "DOWNLOAD"
        $s7 = "UPLOAD"
        $s8 = "http://www.practicalmalwareanalysis.com"
    condition:
        isMZ_PE and all of them
}
