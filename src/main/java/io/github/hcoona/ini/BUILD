package(
    default_visibility = ["//src:__subpackages__"]
)

java_library(
    name = "ini_parser",
    srcs = [
        "IniParser.java",
    ],
)

java_library(
    name = "ini_api",
    srcs = [
        "IniFileObject.java",
        "IniSectionObject.java",
        "IniOptionObject.java",
        "IniException.java",
    ],
    deps = [
        "@org_apache_commons_commons_lang3//jar",
    ],
)

java_library(
    name = "ini_reader",
    srcs = [
        "IniReader.java",
    ],
    deps = [
        ":ini_api",
        ":ini_parser",
    ],
    exports = [
        ":ini_api",
        ":ini_parser",
    ],
)
