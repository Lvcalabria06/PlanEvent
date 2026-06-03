package presentationbackend.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Carregador mínimo de arquivo {@code .env}, sem dependências externas (a lib
 * spring-dotenv não é compatível com o Spring Boot 4). Deve ser chamado no início
 * do {@code main()}, antes de subir o Spring: cada par {@code CHAVE=valor} vira
 * uma system property, que o Spring resolve em {@code ${CHAVE}}.
 *
 * <p>Precedência: variáveis de ambiente reais e system properties já definidas
 * têm prioridade sobre o {@code .env}.</p>
 */
public final class DotEnvLoader {

    private DotEnvLoader() {
    }

    /** Procura um {@code .env} a partir do diretório atual subindo até 5 níveis e o carrega. */
    public static void load() {
        Path arquivo = localizar();
        if (arquivo != null) {
            carregarArquivo(arquivo);
        }
    }

    private static Path localizar() {
        Path dir = Paths.get("").toAbsolutePath();
        for (int nivel = 0; nivel < 5 && dir != null; nivel++) {
            Path candidato = dir.resolve(".env");
            if (Files.isRegularFile(candidato)) {
                return candidato;
            }
            dir = dir.getParent();
        }
        return null;
    }

    static void carregarArquivo(Path arquivo) {
        try {
            for (String linha : Files.readAllLines(arquivo)) {
                String conteudo = linha.trim();
                if (conteudo.isEmpty() || conteudo.startsWith("#")) {
                    continue;
                }
                int separador = conteudo.indexOf('=');
                if (separador <= 0) {
                    continue;
                }
                String chave = conteudo.substring(0, separador).trim();
                String valor = desempacotar(conteudo.substring(separador + 1).trim());

                if (System.getenv(chave) == null && System.getProperty(chave) == null) {
                    System.setProperty(chave, valor);
                }
            }
        } catch (IOException e) {
            System.err.println("Aviso: não foi possível ler o .env (" + arquivo + "): " + e.getMessage());
        }
    }

    private static String desempacotar(String valor) {
        if (valor.length() >= 2
                && ((valor.startsWith("\"") && valor.endsWith("\""))
                || (valor.startsWith("'") && valor.endsWith("'")))) {
            return valor.substring(1, valor.length() - 1);
        }
        return valor;
    }
}
