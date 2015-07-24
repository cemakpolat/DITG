LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_CPPFLAGS   += -std=c++0x -frtti
LOCAL_MODULE    :=ITGManager
LOCAL_SRC_FILES :=ITGManager.cpp
LOCAL_STATIC_LIBRARIES := libCommon libITG
#LOCAL_LDLIBS     := -L$(SYSROOT)/usr/lib -pthread
LOCAL_C_INCLUDES := ../common/ ../libITG/
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/

include $(BUILD_EXECUTABLE)