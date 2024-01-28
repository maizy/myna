package dev.maizy.myna.ruleset;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.configuration.JacksonConfiguration;
import java.io.IOException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@JsonTest
@Import(JacksonConfiguration.class)
@ActiveProfiles({"default", "test"})
public class RulesetJsonSerializationTest {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private JacksonTester<Ruleset> jacksonTester;

  private static Stream<Arguments> testRulesets() {
    return Stream.of(
      Arguments.of(SampleRulesets.allFeaturesRuleset()),
      Arguments.of(SampleRulesets.checkersRuleset())
    );
  }

  @ParameterizedTest
  @MethodSource("testRulesets")
  public void testRoundTrip(Ruleset ruleset) throws IOException {

    final var serialized = jacksonTester.write(ruleset);
    final var json = serialized.getJson();
    logger.debug("\n" + ruleset.name() + ":\n" + json);
    final var deserializedRuleset = jacksonTester.parse(json).getObject();

    Assertions.assertEquals(ruleset, deserializedRuleset);
  }
}
