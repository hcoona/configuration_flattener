package io.github.hcoona.config_flattener;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class ConfigurationFlattenerTest {

  @Test
  public void testFlatten1() {
    Map<String, String> attributes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    attributes.put("DC", "lf");
    attributes.put("Cluster", "default");

    Map<String, String> input = new LinkedCaseInsensitiveMap<>();
    input.put("a", "1");
    input.put("b", "1");
    input.put("DC:lf$c", "0");
    input.put("c", "1");
    input.put("Cluster:default$b", "0");
    input.put("Cluster:dw$b", "2");
    input.put("d", "1");
    input.put("Cluster:flink$d", "3");
    input.put("Cluster:default$e", "1");

    ConfigurationFlattener flattener = new ConfigurationFlattener(
        attributes, LinkedCaseInsensitiveMap::new);
    Map<String, String> output = flattener.flatten(input);

    Assert.assertEquals(5, output.size());
    Iterator<Map.Entry<String, String>> iterator = output.entrySet().iterator();
    Iterator<Map.Entry<String, String>> iterator2 = Arrays.asList(
        (Map.Entry<String, String>) new AbstractMap.SimpleEntry<>("a", "1"),
        (Map.Entry<String, String>) new AbstractMap.SimpleEntry<>("b", "0"),
        (Map.Entry<String, String>) new AbstractMap.SimpleEntry<>("c", "0"),
        (Map.Entry<String, String>) new AbstractMap.SimpleEntry<>("d", "1"),
        (Map.Entry<String, String>) new AbstractMap.SimpleEntry<>("e", "1")).iterator();
    for (int i = 0; i < output.size(); i++) {
      Map.Entry<String, String> entry = iterator.next();
      Map.Entry<String, String> entry2 = iterator2.next();
      Assert.assertEquals(entry2, entry);
    }
  }
}