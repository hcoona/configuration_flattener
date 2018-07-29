package io.github.hcoona.config_flattener;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationFlattenerTest {

  @Test
  public void testEmptyAttributesEmptyInput() {
    Map<String, String> attributes = new HashMap<>();
    Map<String, String> input = new LinkedCaseInsensitiveMap<>();

    ConfigurationFlattener flattener = new ConfigurationFlattener(
        attributes, LinkedCaseInsensitiveMap::new);

    Map<String, String> output = flattener.flatten(input);

    Assert.assertEquals(0, output.size());
  }

  @Test
  public void testEmptyAttributesNoFlattenInput() {
    Map<String, String> attributes = new HashMap<>();
    Map<String, String> input = new LinkedCaseInsensitiveMap<>();
    input.put("a", "0");

    ConfigurationFlattener flattener = new ConfigurationFlattener(
        attributes, LinkedCaseInsensitiveMap::new);

    Map<String, String> output = flattener.flatten(input);

    assertMap(input, output);
  }

  @Test
  public void testEmptyAttributesFlattenInput() {
    Map<String, String> attributes = new HashMap<>();
    Map<String, String> input = new LinkedCaseInsensitiveMap<>();
    input.put("Region:CN$a", "0");

    ConfigurationFlattener flattener = new ConfigurationFlattener(
        attributes, LinkedCaseInsensitiveMap::new);

    Map<String, String> output = flattener.flatten(input);

    Assert.assertEquals(0, output.size());
  }

  @Test
  public void testEmptyInput() {
    Map<String, String> attributes = Collections.singletonMap("Region", "CN");
    Map<String, String> input = new LinkedCaseInsensitiveMap<>();

    ConfigurationFlattener flattener = new ConfigurationFlattener(
        attributes, LinkedCaseInsensitiveMap::new);

    Map<String, String> output = flattener.flatten(input);

    Assert.assertEquals(0, output.size());
  }

  @Test
  public void testNoFlattenInput() {
    Map<String, String> attributes = Collections.singletonMap("Region", "CN");
    Map<String, String> input = new LinkedCaseInsensitiveMap<>();
    input.put("a", "0");

    ConfigurationFlattener flattener = new ConfigurationFlattener(
        attributes, LinkedCaseInsensitiveMap::new);

    Map<String, String> output = flattener.flatten(input);

    assertMap(input, output);
  }

  @Test
  public void testFlattenInput() {
    Map<String, String> attributes = Collections.singletonMap("Region", "CN");
    Map<String, String> input = new LinkedCaseInsensitiveMap<>();
    input.put("Region:CN$a", "0");

    ConfigurationFlattener flattener = new ConfigurationFlattener(
        attributes, LinkedCaseInsensitiveMap::new);

    Map<String, String> output = flattener.flatten(input);

    assertMap(Collections.singletonMap("a", "0"), output);
  }

  @Test
  public void testOverwriteAfterInput() {
    Map<String, String> attributes = Collections.singletonMap("Region", "CN");
    Map<String, String> input = new LinkedCaseInsensitiveMap<>();
    input.put("a", "1");
    input.put("Region:CN$a", "0");

    ConfigurationFlattener flattener = new ConfigurationFlattener(
        attributes, LinkedCaseInsensitiveMap::new);

    Map<String, String> output = flattener.flatten(input);

    assertMap(Collections.singletonMap("a", "0"), output);
  }

  @Test
  public void testOverwriteBeforeInput() {
    Map<String, String> attributes = Collections.singletonMap("Region", "CN");
    Map<String, String> input = new LinkedCaseInsensitiveMap<>();
    input.put("Region:CN$a", "0");
    input.put("a", "1");

    ConfigurationFlattener flattener = new ConfigurationFlattener(
        attributes, LinkedCaseInsensitiveMap::new);

    Map<String, String> output = flattener.flatten(input);

    assertMap(Collections.singletonMap("a", "0"), output);
  }

  @Test
  public void testOverwriteHitFirstInput() {
    Map<String, String> attributes = new LinkedCaseInsensitiveMap<>();
    attributes.put("Region", "CN");
    attributes.put("Cluster", "default");

    Map<String, String> input = new LinkedCaseInsensitiveMap<>();
    input.put("Region:CN$a", "0");
    input.put("Region:CN,Cluster:default$a", "2");
    input.put("a", "1");

    ConfigurationFlattener flattener = new ConfigurationFlattener(
        attributes, LinkedCaseInsensitiveMap::new);

    Map<String, String> output = flattener.flatten(input);

    assertMap(Collections.singletonMap("a", "0"), output);
  }

  @Test
  public void testOverwriteMismatchInput() {
    Map<String, String> attributes = new LinkedCaseInsensitiveMap<>();
    attributes.put("Region", "CN");
    attributes.put("Cluster", "default");

    Map<String, String> input = new LinkedCaseInsensitiveMap<>();
    input.put("Region:SG$a", "0");
    input.put("Region:CN,Cluster:flink$a", "2");
    input.put("a", "1");

    ConfigurationFlattener flattener = new ConfigurationFlattener(
        attributes, LinkedCaseInsensitiveMap::new);

    Map<String, String> output = flattener.flatten(input);

    assertMap(Collections.singletonMap("a", "1"), output);
  }

  @Test
  public void testFlattenOverall() {
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

    assertMap(
        Stream
            .of(
                new AbstractMap.SimpleEntry<>("a", "1"),
                new AbstractMap.SimpleEntry<>("b", "0"),
                new AbstractMap.SimpleEntry<>("c", "0"),
                new AbstractMap.SimpleEntry<>("d", "1"),
                new AbstractMap.SimpleEntry<>("e", "1"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
        output);
  }

  private static <K, V> void assertMap(
      Map<? extends K, ? extends V> expected,
      Map<? extends K, ? extends V> actual) {
    Assert.assertEquals(expected.size(), actual.size());

    Iterator<? extends Map.Entry<? extends K, ? extends V>>
        expectedEntryIterator = expected.entrySet().iterator();
    Iterator<? extends Map.Entry<? extends K, ? extends V>>
        actualEntryIterator = actual.entrySet().iterator();

    while (expectedEntryIterator.hasNext()) {
      Assert.assertEquals(
          expectedEntryIterator.next(),
          actualEntryIterator.next());
    }
  }
}
