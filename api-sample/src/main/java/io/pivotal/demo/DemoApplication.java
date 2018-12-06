package io.pivotal.demo;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableConfigurationProperties(DemoProperties.class)
@RestController
public class DemoApplication {

	@Autowired
	DemoProperties properties;

	@Autowired
	WebClient.Builder webClientBuilder;

	private final Configuration jsonPathConfiguration;

	public DemoApplication() {
		jsonPathConfiguration = Configuration.builder()
				.options(Option.SUPPRESS_EXCEPTIONS)
				.build();
	}

	@Bean
	Mono<String> tokenProvider() {
		return webClientBuilder
				.baseUrl(properties.getAccessTokenUrl())
				.build()
				.post()
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", authorizationHeaderValue())
				.body(BodyInserters.fromFormData("grant_type", "client_credentials"))
				.retrieve()
				.bodyToMono(AccessTokenResponse.class)
				.cache(accessTokenResponse -> Duration.ofSeconds(accessTokenResponse.getExpiresIn())
						.minus(Duration.ofMinutes(1)), t -> Duration.ZERO, () -> Duration.ZERO)
				.flatMap(accessTokenResponse -> Mono.just(accessTokenResponse.getAccessToken()));
	}

	@RequestMapping("/version")
	public Mono<String> version() {
		return tokenProvider().flatMap(token -> authenticatedGetRequest(token, "/about")
				.retrieve()
				.bodyToMono(Map.class)
				.map(body -> JsonPath.read(body, "$.versionInfo.core.version").toString())
				.doOnError(Throwable::printStackTrace));
	}

	@RequestMapping("/apps")
	public Mono<List<String>> streams() {
		return tokenProvider().flatMap(token -> authenticatedGetRequest(token, "/apps")
				.retrieve()
				.bodyToMono(Map.class)
				.flatMapMany(this::appBodyToFlux)
				.collectList()
				.doOnError(Throwable::printStackTrace));
}

	private Flux<String> appBodyToFlux(Map body) {
		final List<String> names = JsonPath.parse(body, jsonPathConfiguration).read("$._embedded.appRegistrationResourceList[*].name");
		return Flux.fromIterable(names);
	}

	private WebClient.RequestHeadersSpec authenticatedGetRequest(String token, String uri) {
		return baseApiWebClient()
				.get()
				.uri(uri)
				.header("Authorization", "bearer " + token)
				.accept(MediaType.APPLICATION_JSON);
	}

	private WebClient baseApiWebClient() {
		return webClientBuilder
				.baseUrl(properties.getDataflowUrl())
				.build();
	}

	private String authorizationHeaderValue() {
		final String credentials = properties.getClientId() + ":" + properties.getClientSecret();
		return "Basic " + Base64Utils.encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
