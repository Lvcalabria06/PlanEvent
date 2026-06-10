package presentationbackend.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DotEnvLoaderTest {

    @Test
    void carregaParesChaveValorComoSystemProperties(@org.junit.jupiter.api.io.TempDir Path dir) throws IOException {
        Path env = dir.resolve(".env");
        Files.writeString(env, """
                # comentario deve ser ignorado
                DOTENV_DB_PASSWORD=segredo123

                DOTENV_DB_URL="jdbc:postgresql://localhost:5432/planevent"
                """);

        DotEnvLoader.carregarArquivo(env);

        assertEquals("segredo123", System.getProperty("DOTENV_DB_PASSWORD"));
        assertEquals("jdbc:postgresql://localhost:5432/planevent", System.getProperty("DOTENV_DB_URL"));
    }

    @Test
    void naoSobrescreveSystemPropertyExistente(@org.junit.jupiter.api.io.TempDir Path dir) throws IOException {
        System.setProperty("DOTENV_JA_DEFINIDA", "valor_original");
        Path env = dir.resolve(".env");
        Files.writeString(env, "DOTENV_JA_DEFINIDA=valor_do_env\n");

        DotEnvLoader.carregarArquivo(env);

        assertEquals("valor_original", System.getProperty("DOTENV_JA_DEFINIDA"));
        System.clearProperty("DOTENV_JA_DEFINIDA");
    }

    @Test
    void linhaSemIgualEhIgnorada(@org.junit.jupiter.api.io.TempDir Path dir) throws IOException {
        Path env = dir.resolve(".env");
        Files.writeString(env, "LINHA_INVALIDA_SEM_IGUAL\n");

        DotEnvLoader.carregarArquivo(env);

        assertNull(System.getProperty("LINHA_INVALIDA_SEM_IGUAL"));
    }
}
