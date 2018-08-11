load("//:junit5.bzl", "java_junit5_test")

java_junit5_test(
    name = "IniParserTest",
    srcs = [
        "ini/IniParserTest.java",
    ],
    test_class = "io.github.hcoona.parser.ini.IniParserTest",
    deps = [
        "//src/main/java/io/github/hcoona/parser:ini_parser",
    ],
    resources = [
        "//src/test/resources/ini:ini_test_cases",
    ],
    size = "small",
)
