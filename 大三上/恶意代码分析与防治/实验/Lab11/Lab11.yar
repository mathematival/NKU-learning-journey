private rule isMZ_PE {
    meta:
        description = "It is a PE file"
    condition:
        filesize <10MB and uint16(0) == 0x5A4D and uint32(uint32(0x3C)) == 0x00004550
        // 文件大小小于10MB，文件头的前两个字节为MZ，PE头的偏移地址处的四个字节为PE标志
}

// Lab14-01
rule Lab14_01_exe {
    meta:
        description = "It may like Lab14_01_exe"
    strings:
        $s1 = "http://www.practicalmalwareanalysis.com/%s/%c.png"
        $s2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        $s3 = "%c%c:%c%c:%c%c:%c%c:%c%c:%c%c"
        $s4 = "%s-%s"
    condition:
        isMZ_PE and all of them
}

// Lab14-02
rule Lab14_02_exe {
    meta:
        description = "It may like Lab14_02_exe"
    strings:
        $s1 = "WXYZlabcd3fghijko12e456789ABCDEFGHIJKL+/MNOPQRSTUVmn0pqrstuvwxyz"
        $s2 = "http://127.0.0.1/tenfour.html" wide
        $s3 = "cmd.exe"
        $s4 = "Internet Surf"
        $s5 = "/c del "
    condition:
        isMZ_PE and all of them
}

// Lab14-03
rule Lab14_03_exe {
    meta:
        description = "It may like Lab14_03_exe"
    strings:
        $s1 = "http://www.practicalmalwareanalysis.com/start.htm"
        $s2 = "C:\\autobat.exe"
        $s3 = "/abcdefghijklmnopqrstuvwxyz0123456789:."
        $s4 = "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)"
    condition:
        isMZ_PE and all of them
}
