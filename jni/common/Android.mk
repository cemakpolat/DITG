LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_CFLAGS := 
LOCAL_CPPFLAGS   += -std=c++0x -frtti 

LOCAL_MODULE    := libCommon
LOCAL_SRC_FILES :=\
        crypto.cpp \
        ITG.cpp \
        pipes.cpp \
        serial.cpp \
        thread.cpp \
        timestamp.cpp  
        
LOCAL_LDLIBS += -lz -lm -llog -lc -L$(call host-path, $(LOCAL_PATH))/$(TARGET_ARCH_ABI) 
LOCAL_C_INCLUDES := $(LOCAL_PATH)/
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/
LOCAL_LDLIBS     := -L$(SYSROOT)/usr/lib -pthread

#include $(BUILD_SHARED_LIBRARY)
include $(BUILD_STATIC_LIBRARY)