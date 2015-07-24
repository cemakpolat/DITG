LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libnewran
LOCAL_CPPFLAGS   += -frtti -Wall
LOCAL_LDLIBS += -lz -lm -llog -lc 

LOCAL_C_INCLUDES := $(LOCAL_PATH)/

LOCAL_SRC_FILES := extreal.cpp myexcept.cpp newran1.cpp newran2.cpp simpstr.cpp 
	
include $(BUILD_STATIC_LIBRARY)