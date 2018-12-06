package io.pivotal.demo;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Autowired
	DemoApplication demoApplication;

	@Test
	public void contextLoads() {
	}

	@Test
	public void emptyAppsResponse() {
		final Flux<String> response = ReflectionTestUtils.invokeMethod(demoApplication, "appBodyToFlux", Collections.emptyMap());
		final List<String> apps = response.collectList().block();
		assertThat(apps).isEmpty();
	}
}
