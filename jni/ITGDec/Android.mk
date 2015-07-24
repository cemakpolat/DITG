LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    :=ITGDec
LOCAL_CPPFLAGS   += -std=c++0x -frtti

LOCAL_LDLIBS += -lz -lm -llog -lc 
LOCAL_C_INCLUDES := ../common/

LOCAL_SRC_FILES :=ITGDecod.cpp
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/

include $(BUILD_EXECUTABLE)