LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    :=libITG
LOCAL_CFLAGS += -fPIC
LOCAL_SRC_FILES :=ITGapi.cpp
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/

include $(BUILD_STATIC_LIBRARY)