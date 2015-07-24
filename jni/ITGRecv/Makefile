LOCAL_PATH:= $(call my-dir)
################## build ITG ###################
include $(CLEAR_VARS)
LOCAL_SRC_FILES := ./common/ITG.cpp 
LOCAL_CFLAGS += -fPIC
LOCAL_MODULE:= libITG
APP_STL := stlport_static
LOCAL_STATIC_LIBRARIES += libstlport_static libcutils libc libm 
include $(BUILD_STATIC_LIBRARY)
################## build ITG ###################
include $(CLEAR_VARS)
LOCAL_SRC_FILES :=  ./common/timestamp.cpp 
LOCAL_CFLAGS += -fPIC
APP_STL := gnustl_static
LOCAL_MODULE:= libTS
LOCAL_STATIC_LIBRARIES := libcutils libc libm
include $(BUILD_STATIC_LIBRARY)
################## build ITG ###################
include $(CLEAR_VARS)
LOCAL_SRC_FILES :=./common/serial.cpp 
LOCAL_CFLAGS += -fPIC
APP_STL := gnustl_static
LOCAL_MODULE:= libSerial
LOCAL_STATIC_LIBRARIES := libcutils libc libm
include $(BUILD_STATIC_LIBRARY)
################## build ITG ###################
include $(CLEAR_VARS)
LOCAL_SRC_FILES := ./common/pipes.cpp
LOCAL_CFLAGS += -fPIC
APP_STL := gnustl_static
LOCAL_MODULE:= libPipe
LOCAL_STATIC_LIBRARIES := libcutils libc libm
include $(BUILD_STATIC_LIBRARY)
################## build iwconfig ###################
include $(CLEAR_VARS)
LOCAL_SRC_FILES := ./ITGRecv/ITGRecv.cpp
LOCAL_CFLAGS += -Wstrict-prototypes -Wmissing-prototypes -Wshadow -Wpointer-arith -Wcast-qual -Winline -MMD -fPIC
APP_STL := gnustl_static
LOCAL_MODULE:= ITGRecv
LOCAL_STATIC_LIBRARIES := libcutils libc libm libITG libTS libSerial libPipe
LOCAL_MODULE_PATH := $(TARGET_OUT_OPTIONAL_EXECUTABLES) # install to system/xbin
include $(BUILD_EXECUTABLE)