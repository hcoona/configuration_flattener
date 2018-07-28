package io.github.hcoona.config_flattener;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

public class ConfigurationFlattener {
  private final Map<String, String> attributes;
  private final Supplier<Map<String, String>> optionMapSupplier;

  public ConfigurationFlattener(
      Map<String, String> attributes,
      Supplier<Map<String, String>> optionMapSupplier) {
    this.attributes = attributes;
    this.optionMapSupplier = optionMapSupplier;
  }

  public Map<String, String> flatten(Map<String, String> config) {
    Map<String, String> result = optionMapSupplier.get();
    Set<String> overwrittenKeys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    for (Map.Entry<String, String> entry : config.entrySet()) {
      String key = entry.getKey();
      if (key.contains("$")) {
        String[] g = StringUtils.split(key, "$", 2);
        if (g.length == 2) {
          String realKey = g[1];
          if (!overwrittenKeys.contains(realKey)) {
            if (isMatch(g[0])) {
              overwrittenKeys.add(realKey);
              result.put(realKey, entry.getValue());
            }
          }
        } else {
          throw new IllegalStateException("Empty overwritten key. " + entry);
        }
      } else {
        if (!overwrittenKeys.contains(key)) {
          if (result.containsKey(key)) {
            throw new IllegalStateException("Duplicated key: " + key);
          } else {
            result.put(key, entry.getValue());
          }
        }
      }
    }

    return result;
  }

  public Map<String, String> getAttributes() {
    return this.attributes;
  }

  private boolean isMatch(String predictions) {
    return Arrays.stream(StringUtils.split(predictions, ","))
        .allMatch(p -> {
          String[] g = StringUtils.split(p, ":", 2);
          return attributes.getOrDefault(g[0], "").equalsIgnoreCase(g[1]);
        });
  }
}
