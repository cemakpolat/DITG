
LOCAL_PATH := $(call my-dir)
LOCAL_LDLIBS += -lz -lm -llog -lc -L$(call host-path, $(LOCAL_PATH))/$(TARGET_ARCH_ABI) 

include $(CLEAR_VARS)
include common/Android.mk
include libITG/Android.mk  
include ITGDec/Android.mk  

include ITGRecv/Android.mk
include ITGManager/Android.mk
include ITGSend/Android.mk
include ITGLog/Android.mk
