package dev.maizy.myna;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MynaApplicationTests {

	@Test
	void hello() {
    assertThat("hello world").isLowerCase();
	}

}
