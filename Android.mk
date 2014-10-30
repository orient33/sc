LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

###################add by huayang###############################
LOCAL_JAVA_LIBRARIES := telephony-common
LOCAL_STATIC_JAVA_LIBRARIES := xutils
###################add by huayang###############################

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := SecurityCenter
LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)

###################add by huayang###############################
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := xutils:libs/xUtils-2.6.13.jar
include $(BUILD_MULTI_PREBUILT)
###################add by huayang###############################

# Use the folloing include to make our test apk.
# include $(call all-makefiles-under,$(LOCAL_PATH))
