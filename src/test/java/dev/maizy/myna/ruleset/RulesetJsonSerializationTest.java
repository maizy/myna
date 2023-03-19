package dev.maizy.myna.ruleset;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
public class RulesetJsonSerializationTest {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private JacksonTester<Ruleset> jacksonTester;

  @Test
  public void testRoundTrip() throws IOException {

    final var ruleset = SampleRulesets.sampleRuleset();

    final var serialized = jacksonTester.write(ruleset);
    final var json = serialized.getJson();
    logger.debug(json);
    final var deserializedRuleset = jacksonTester.parse(json).getObject();

    Assertions.assertEquals(ruleset, deserializedRuleset);
  }
}
