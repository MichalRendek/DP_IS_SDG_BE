package sk.stuba.sdg.isbe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@SpringBootTest
@Profile("!deployment")
class IsBeApplicationTests {

	@Test
	void contextLoads() {
	}

}
