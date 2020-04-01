import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class DumpFilePath {
	public static void main(String[] args) throws IOException {
		try (Stream<Path> paths = Files.walk(Paths.get("//prodisinas/fxsp-postal1prd/PostageManifest/staging"))) {
			paths.filter(Files::isRegularFile).forEach(System.out::println);
		}
	}
}
