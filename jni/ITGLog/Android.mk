LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    :=ITGLog
LOCAL_CPPFLAGS   += -std=c++0x -frtti

LOCAL_LDLIBS += -lz -lm -llog -lc 
LOCAL_C_INCLUDES := ../common/

LOCAL_SRC_FILES :=ITGLog.cpp channel.cpp
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/
LOCAL_STATIC_LIBRARIES := libCommon

include $(BUILD_EXECUTABLE)