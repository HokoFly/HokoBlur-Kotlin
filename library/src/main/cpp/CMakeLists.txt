cmake_minimum_required(VERSION 3.6)
project(hoko_blur)

set(CMAKE_CXX_STANDARD 11)

include_directories(
    ${CMAKE_SOURCE_DIR}/include
)

add_compile_options(-fPIC)

aux_source_directory(. SOURCE_FILES)

set(BLUR_LIB_NAME hoko_blur)

add_library(${BLUR_LIB_NAME} SHARED ${SOURCE_FILES})

#androi log library
find_library(log-lib log)
target_link_libraries(${BLUR_LIB_NAME} ${log-lib})

#Android graphic library
find_library(graphics-lib jnigraphics)
target_link_libraries(${BLUR_LIB_NAME} ${graphics-lib})

