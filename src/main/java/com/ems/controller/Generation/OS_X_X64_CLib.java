package com.ems.controller.Generation;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface OS_X_X64_CLib extends Library {
    OS_X_X64_CLib INSTANCE = Native.load("sysinfo-x64", OS_X_X64_CLib.class);

    int hw_logicalcpu();
    long hw_memsize();
    Pointer cpu_tick_info(Pointer addr, int numCPUs, long utime);
    Pointer mem_size_info(Pointer addr);
}
