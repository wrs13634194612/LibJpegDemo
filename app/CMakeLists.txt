# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.
# 设置 cmake 最小支持版本
cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

# 创建一个库
add_library(
        # Sets the name of the library.
        # 库名称，比如现在会生成 native-lib.so
        effective-bitmap

        # Sets the library as a shared library.
        # 设置是动态库（SHARED）还是静态库（STATIC）
        SHARED

        # Provides a relative path to your source file(s).
        # 设置源文件的相对路径
        src/main/cpp/effective-bitmap.c)

include_directories(src/main/cpp/jpeg/)

add_library(jpegbither SHARED IMPORTED)
set_target_properties(jpegbither PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libjpegbither.so)


# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

# 搜索并指定预构建库并将路径存储为变量。
# NDK中已经有一部分预构建库（比如 log），并且ndk库已经是被配置为cmake搜索路径的一部分
# 可以不写 直接在 target_link_libraries 写上log
find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

find_library(jnigraphics-lib jnigraphics)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

# 指定CMake应链接到目标库的库。你可以链接多个库，例如构建脚本、预构建的第三方库或系统库。
target_link_libraries( # Specifies the target library.

        effective-bitmap

        jpegbither
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        ${jnigraphics-lib})
