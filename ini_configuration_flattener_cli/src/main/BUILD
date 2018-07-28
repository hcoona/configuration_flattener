java_binary(
    name = "ini_configuration_flattener",
    srcs = glob([
        "java/**/*.java"
    ]),
    main_class = "io.github.hcoona.cli.App",
    deps = [
        "//file_processor/src/main:file_processor",
        "//ini_config_flatten_processor/src/main:ini_config_flatten_processor",
        "@org_slf4j_slf4j_api//jar",
        "@ch_qos_logback_logback_classic//jar",
        "@ch_qos_logback_logback_core//jar",
        "@commons_codec_commons_codec//jar",
        "@org_springframework_spring_core//jar"
    ]
)
