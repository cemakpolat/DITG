LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_CPPFLAGS   += -std=c++0x -frtti
LOCAL_LDLIBS += -lz -lm -llog -lc 
LOCAL_MODULE    := ITGRecv
LOCAL_C_INCLUDES := $(LOCAL_PATH)/../common/
#LOCAL_C_INCLUDES := ../common/

LOCAL_SRC_FILES := ITGRecv.cpp data.cpp
LOCAL_STATIC_LIBRARIES := libCommon

include $(BUILD_EXECUTABLE)