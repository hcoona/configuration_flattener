load("//:junit5.bzl", "java_junit5_test")

java_junit5_test(
    name = "IniParserTest",
    srcs = [
        "IniParserTest.java",
    ],
    test_class = "io.github.hcoona.ini.IniParserTest",
    deps = [
        "//src/main/java/io/github/hcoona/ini:ini_parser",
    ],
    resources = [
        "//src/test/resources/ini:ini_test_cases",
    ],
    size = "small",
)

java_junit5_test(
    name = "IniReaderTest",
    srcs = [
        "IniReaderTest.java",
    ],
    test_class = "io.github.hcoona.ini.IniReaderTest",
    deps = [
        "//src/main/java/io/github/hcoona/ini:ini_reader",
    ],
    resources = [
        "//src/test/resources/ini:ini_test_cases",
    ],
    size = "small",
)
