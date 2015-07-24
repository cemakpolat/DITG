LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libnewran
LOCAL_CPPFLAGS   += -frtti -Wall
LOCAL_LDLIBS += -lz -lm -llog -lc 

LOCAL_C_INCLUDES := $(LOCAL_PATH)/newran/

LOCAL_SRC_FILES := ./newran/extreal.cpp ./newran/myexcept.cpp ./newran/newran1.cpp ./newran/newran2.cpp ./newran/simpstr.cpp 
	
include $(BUILD_STATIC_LIBRARY)
################## build ITG ###################

include $(CLEAR_VARS)

LOCAL_MODULE    := ITGSend
LOCAL_CPPFLAGS   += -frtti -Wall -lnewran

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../

LOCAL_SRC_FILES := ITGSend.cpp traffic.cpp 

LOCAL_STATIC_LIBRARIES := libCommon libnewran

include $(BUILD_EXECUTABLE)
